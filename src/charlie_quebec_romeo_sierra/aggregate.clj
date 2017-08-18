(ns charlie-quebec-romeo-sierra.aggregate)


(defprotocol Aggregate
  (data [this])
  (identifier [this])
  (type-of [this])
  (is-valid [this event]))
