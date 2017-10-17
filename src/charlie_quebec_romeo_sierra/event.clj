(ns charlie-quebec-romeo-sierra.event)

(def handlers (atom {}))

(defprotocol Event
  (data [this])
  (aggregate-type [this])
  (aggregate-identifier [this])
  (type-of [this]))

(defprotocol EventHandler
  (handle [this event]))

(defn register-handler
  [type_of handler]
  (swap! handlers update-in [type_of] #(conj % handler)))

(defn find-handler
  [type_of]
  (get @handlers type_of))

(defn clear
  []
  (reset! handlers {}))
