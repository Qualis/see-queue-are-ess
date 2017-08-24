(ns charlie-quebec-romeo-sierra.producer
  (:require [kinsky.client :as client]
            [charlie-quebec-romeo-sierra.repository :as repository]))

(defn- create-producer
  []
  (client/producer {:bootstrap.servers "localhost:9092"}
                   :keyword
                   :edn))

(def producer (memoize create-producer))

(defn- valid?
  [aggregates events]
  (doseq [event events]
    (swap! aggregates
           update-in
           [(.aggregate-identifier event)]
           #(.process % event)))
  (every? #(.valid? (get @aggregates (.aggregate-identifier %)) %)
          events))

(defn produce
  [events]
  (let [aggregates (atom
                     (into {}
                           (map #(hash-map
                                   (.aggregate-identifier %)
                                   (repository/load-aggregate
                                     (.type-of %)
                                     (.aggregate-identifier %)))
                                events)))]
    (when (valid? aggregates events)
      (repository/save events)
      (doseq [event events]
        (client/send! (producer)
                      (.type-of event)
                      (.aggregate-identifier event)
                      (.data event))))))
