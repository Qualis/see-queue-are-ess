(ns charlie-quebec-romeo-sierra.repository
  (:require [kinsky.client :as client]
            [kinsky.async :as async]
            [clj-uuid :as uuid]
            [clojure.core.async :refer [go go-loop <! >!! put!]]))

(def consumers (atom {}))

(defn- create-producer
  []
  (client/producer {:bootstrap.servers "localhost:9092"}
                   :keyword
                   :edn))

(def producer (memoize create-producer))

(defn- register-consumer
  [type_of consumer]
  (swap! consumers assoc type_of consumer))

(defn create-consumer
  [type_of]
  (register-consumer type_of
                     (async/consumer
                       {:bootstrap.servers "localhost:9092"
                        :group.id type_of}
                       (client/keyword-deserializer)
                       (client/edn-deserializer))))

(def consumer (memoize create-consumer))

(defn- load-aggregate
  [identifier])

(defn- valid?
  [aggregates events]
  (every? #(.valid? (get aggregates (.aggregate-identifier %)) %) events))

(defn- save
  [aggregates])

(defn produce
  [events]
  (let [aggregates (atom
                     (into {}
                           (map #(hash-map
                                   (.aggregate-identifier %)
                                   (load-aggregate
                                     (.aggregate-identifier %)))
                                events)))]
    (when (valid? aggregates events)
      (save aggregates)
      (doseq [event events]
        (client/send! (producer)
                      (.type-of event)
                      (.aggregate-identifier event)
                      (.data event))))))
