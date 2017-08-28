(ns simple.core-test
  (:require [simple.core :as core :refer [->SimpleCommand
                                          ->SimpleCommandHandler
                                          ->SimpleEvent
                                          ->SimpleAggregateFactory]]
            [charlie-quebec-romeo-sierra.command :as command]
            [charlie-quebec-romeo-sierra.aggregate :as aggregate]
            [charlie-quebec-romeo-sierra.consumer :as consumer]
            [clojure.core.async :refer [chan <!! >!!]])
  (:use [midje.sweet :only [fact => provided irrelevant]]))

(fact
  "should be able to construct command"
  (#'core/command) => (->SimpleCommand))

(fact
  "should have expected type on command"
  (.type-of (#'core/command)) => "simple")

(fact
  "should create expected events"
  (.handle (->SimpleCommandHandler)
           (->SimpleCommand)) => (list (->SimpleEvent
                                         "simple"
                                         "34fa3c0c-8786-11e7-bb31-be2e44b06b34"
                                         {:coconuts true})))

(fact
  "should process command"
  (core/create ..command..) => ..result..
  (provided
    (command/process ..command..) => ..result..))

(aggregate/register-aggregate "simple" (->SimpleAggregateFactory))

(command/register-handler core/TYPE_OF (->SimpleCommandHandler))

(fact
  "should process command"
  (let [handle_event_channel (chan)]
    (consumer/consumer "simple" (fn [event]
                                  (>!! handle_event_channel event)))
    (command/process (->SimpleCommand))
    (<!! handle_event_channel) => irrelevant))
