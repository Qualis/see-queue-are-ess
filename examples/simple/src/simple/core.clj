(ns simple.core
  (:require [charlie-quebec-romeo-sierra.command :as command]
            [charlie-quebec-romeo-sierra.event :as event]
            [charlie-quebec-romeo-sierra.aggregate :as aggregate]))

(def ^:const TYPE_OF "simple")

(defrecord SimpleEvent [type_of
                        aggregate_identifier
                        data]
  event/Event
  (aggregate-identifier [this] aggregate_identifier)
  (data [_] data)
  (type-of [_] type_of))

(defrecord SimpleAggregate [type_of
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

(defrecord SimpleAggregateFactory []
  aggregate/AggregateFactory
  (create [_ type_of identifier] (->SimpleAggregate type_of
                                                    identifier
                                                    (atom {}))))

(aggregate/register-aggregate "simple" (->SimpleAggregateFactory))

(defn- event
  [type_of]
  (list (->SimpleEvent type_of
                       "34fa3c0c-8786-11e7-bb31-be2e44b06b34"
                       {:coconuts true})))

(defrecord SimpleCommand []
  command/Command
  (type-of [_] TYPE_OF))

(defrecord SimpleCommandHandler []
  command/CommandHandler
  (handle [_ command]
    (event (.type-of command))))

(defn- command
  []
  (->SimpleCommand))

(def command_handler (->SimpleCommandHandler))

(command/register-handler TYPE_OF command_handler)

(defn create
  [command]
  (command/process command))
