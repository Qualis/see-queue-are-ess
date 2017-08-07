(ns charlie-quebec-romeo-sierra.command)

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

(defn process
  [^charlie_quebec_romeo_sierra.command.Command command]
  (handle (find-handler (type-of command)) command))
