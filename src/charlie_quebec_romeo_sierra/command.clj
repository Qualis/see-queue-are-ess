(ns charlie-quebec-romeo-sierra.command
  (:require [charlie-quebec-romeo-sierra.event :as event]
            [charlie-quebec-romeo-sierra.producer :as producer]))

(def handlers (atom {}))

(defprotocol Command
  (type-of [this]))

(defprotocol CommandHandler
  (handle [this command]))

(defn- register-handler
  [type_of handler]
  (swap! handlers assoc type_of handler))

(defn- find-handler
  [type_of]
  (get @handlers type_of))

(defn- generate-events
  [command]
  {:pre  [(satisfies? Command command)]
   :post [(every? (fn [event] (satisfies? event/Event event)) %)]}
  (handle (find-handler (type-of command)) command))

(defn process
  [command]
  (producer/produce (generate-events command)))
