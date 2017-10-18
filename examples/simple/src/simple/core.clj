(ns simple.core
  (:require [charlie-quebec-romeo-sierra.command :as command]
            [charlie-quebec-romeo-sierra.event :as event]
            [charlie-quebec-romeo-sierra.aggregate :as aggregate]
            [charlie-quebec-romeo-sierra.consumer :as consumer]
            [clojure.core.async :refer [>!!]]))

(def ^:const TYPE_OF "credit")

(defrecord AccountCreditedEvent [type_of
                                 aggregate_identifier
                                 data]
  event/Event
  (aggregate-identifier [this] aggregate_identifier)
  (data [_] data)
  (type-of [_] type_of))

(deftype Account [type_of
                  identifier
                  data]
  aggregate/Aggregate
  (data [_] @data)
  (identifier [_] identifier)
  (type-of [_] type_of)
  (valid? [_ event] true)
  (process [this event] (do
                          (swap! data
                                 update-in
                                 [:balance]
                                 #(+ % (:amount (.data event))))
                          this)))

(deftype AccountFactory []
  aggregate/AggregateFactory
  (create [_ type_of identifier] (->Account type_of
                                            identifier
                                            (atom {:balance 0}))))

(defn- credit-event
  [type_of aggregate_identifier amount]
  (list (->AccountCreditedEvent type_of
                                (keyword aggregate_identifier)
                                {:amount amount})))

(defrecord CreditAccountCommand [account amount]
  command/Command
  (type-of [_] TYPE_OF))

(deftype CreditAccountCommandHandler []
  command/CommandHandler
  (handle [_ command]
    (credit-event (.type-of command)
                  (:account command)
                  (:amount command))))

(deftype AccountCreditedEventHandler [channel]
  event/EventHandler
  (handle [_ event]
    (>!! channel (clojure.string/join
                   ", " [(str "account: " (:key event))
                         (str "credit: " (:amount(:value event)))]))))

(defn command
  [account amount]
  (->CreditAccountCommand account amount))

(defn process-command
  [command]
  (command/process command))
