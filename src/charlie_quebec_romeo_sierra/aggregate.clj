(ns charlie-quebec-romeo-sierra.aggregate)

(def aggregates (atom {}))

(defprotocol AggregateFactory
  (create [this type_of identifier]))

(defprotocol Aggregate
  (data [this])
  (identifier [this])
  (type-of [this])
  (valid? [this event])
  (process [this event]))

(defn register-aggregate
  [type_of constructor]
  (swap! aggregates assoc type_of constructor))

(defn get-aggregate
  [type_of identifier]
  (.create (get @aggregates type_of) type_of identifier))

(defn clear
  []
  (reset! aggregates {}))
