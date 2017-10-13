(ns simple.core-test
  (:require [simple.core :as core :refer [->SimpleCommand
                                          ->SimpleCommandHandler
                                          ->SimpleEvent
                                          ->SimpleEventHandler
                                          ->SimpleAggregateFactory]]
            [charlie-quebec-romeo-sierra.command :as command]
            [charlie-quebec-romeo-sierra.event :as event]
            [charlie-quebec-romeo-sierra.aggregate :as aggregate]
            [charlie-quebec-romeo-sierra.consumer :as consumer]
            [clj-uuid :as uuid]
            [clojure.core.async :refer [promise-chan <!! >!!]])
  (:use [midje.sweet :only [facts fact => provided irrelevant contains]])
  (:import [java.util UUID]))

(fact
  "should be able to construct command"
  (#'core/command) => ..command..
  (provided
    (->SimpleCommand) => ..command..))

(fact
  "should have expected type on command"
  (.type-of (#'core/command)) => "simple")

(fact
  "should create expected events"
  (.handle (->SimpleCommandHandler)
           (->SimpleCommand)) => (list ..simple_event..)
  (provided
    (uuid/v1) => ..uuid..
    (->SimpleEvent
      "simple"
      :..uuid..
      {:coconuts true}) => ..simple_event..))

(fact
  "should handle created events"
  (let [channel (promise-chan)]
    (.handle (->SimpleEventHandler channel)
             {:key ..uuid..
              :value ..data..})
    (<!! channel) => "aggregate-identifier: ..uuid.., data: ..data.."))

(fact
  "should process command"
  (core/process-command ..command..) => ..result..
  (provided
    (command/process ..command..) => ..result..))

(facts
  "when aggregate and handlers registered"

  (require '[simple.core :as core :refer [->SimpleCommand
                                          ->SimpleCommandHandler
                                          ->SimpleEvent
                                          ->SimpleEventHandler
                                          ->SimpleAggregateFactory]]
           :reload)

  (fact
    "should handle event"
    (let [uuid_string "9dd43770-af98-11e7-8f37-758b0816f6b7"
          uuid (UUID/fromString uuid_string)
          handle_event_channel (promise-chan)]
      (aggregate/register-aggregate core/TYPE_OF (->SimpleAggregateFactory))
      (command/register-handler core/TYPE_OF (->SimpleCommandHandler))
      (event/register-handler core/TYPE_OF (->SimpleEventHandler
                                             handle_event_channel))
      (consumer/consumer core/TYPE_OF (fn [event]))
      (do
        (command/process (->SimpleCommand))
        (<!! handle_event_channel)) => (str "aggregate-identifier: "
                                            ":" uuid_string ", "
                                            "data: {:coconuts true}")
      (provided
        (uuid/v1) => uuid))))
