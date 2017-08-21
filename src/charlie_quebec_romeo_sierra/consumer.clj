(ns charlie-quebec-romeo-sierra.consumer
  (:require [kinsky.client :as client]
            [kinsky.async :as async]))

(def consumers (atom {}))

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
