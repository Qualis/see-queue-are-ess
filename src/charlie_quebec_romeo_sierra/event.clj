(ns charlie-quebec-romeo-sierra.event)

(defprotocol Event
  (revision [this])
  (data [this]))
