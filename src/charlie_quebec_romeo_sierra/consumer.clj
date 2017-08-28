(ns charlie-quebec-romeo-sierra.consumer
  (:require [kinsky.client :as client]
            [kinsky.async :as async]
            [clojure.core.async :refer [go-loop <! put!]] ))

(def consumers (atom {}))

(defprotocol ConsumerController
  (output-channel [this])
  (subscribe [this topic])
  (unsubscribe [this])
  (commit [this])
  (stop [this]))

(defrecord KafkaConsumerController
  [output control]
  ConsumerController
  (output-channel [this]
    output)
  (subscribe [this topic]
    (put! control {:op :subscribe :topic topic}))
  (unsubscribe [this]
    (put! control {:op :unsubscribe}))
  (commit [this]
    (put! control {:op :commit}))
  (stop [this]
    (put! control {:op :stop})))

(defn- register-consumer
  [type_of consumer]
  (swap! consumers assoc type_of consumer)
  consumer)

(defn- listen
  [type_of consumer handler]
  (go-loop
    []
    (when-let [record (<! (.output-channel consumer))]
      (when (= (:type record) :record)
        (handler record))
      (recur)))
  (.subscribe consumer type_of)
  (.commit consumer))

(defn create-consumer
  [type_of handler]
  (let [[output control] (async/consumer
                           {:bootstrap.servers "localhost:9092"
                            :group.id type_of}
                           (client/keyword-deserializer)
                           (client/edn-deserializer))
        consumer (register-consumer
                   type_of
                   (->KafkaConsumerController
                     output
                     control))]
    (listen type_of
            consumer
            handler)
    consumer))

(def consumer (memoize create-consumer))
