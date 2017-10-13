(ns simple.core
  (:require [charlie-quebec-romeo-sierra.command :as command]
            [charlie-quebec-romeo-sierra.event :as event]
            [charlie-quebec-romeo-sierra.aggregate :as aggregate]
            [charlie-quebec-romeo-sierra.consumer :as consumer]
            [clj-uuid :as uuid]
            [clojure.core.async :refer [>!!]]))

(def ^:const TYPE_OF "simple")

(defrecord SimpleEvent [type_of
                        aggregate_identifier
                        data]
  event/Event
  (aggregate-identifier [this] aggregate_identifier)
  (data [_] data)
  (type-of [_] type_of))

(deftype SimpleAggregate [type_of
                          identifier
                          data]
  aggregate/Aggregate
  (data [_] @data)
  (identifier [_] identifier)
  (type-of [_] type_of)
  (valid? [_ event] true)
  (process [this event] (do
                          (swap! data merge (.data event))
                          this)))

(deftype SimpleAggregateFactory []
  aggregate/AggregateFactory
  (create [_ type_of identifier] (->SimpleAggregate type_of
                                                    identifier
                                                    (atom {}))))

(defn- event
  [type_of aggregate_identifier]
  (list (->SimpleEvent type_of
                       (keyword aggregate_identifier)
                       {:coconuts true})))

(deftype SimpleCommand []
  command/Command
  (type-of [_] TYPE_OF))

(deftype SimpleCommandHandler []
  command/CommandHandler
  (handle [_ command]
    (event (.type-of command)
           (str (uuid/v1)))))

(deftype SimpleEventHandler [channel]
  event/EventHandler
  (handle [_ event]
    (>!! channel (clojure.string/join
                   ", " [(str "aggregate-identifier: " (:key event))
                         (str "data: " (:value event))]))))

(defn- command
  []
  (->SimpleCommand))

(defn process-command
  [command]
  (command/process command))
