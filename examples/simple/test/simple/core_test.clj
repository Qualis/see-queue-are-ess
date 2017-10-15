(ns simple.core-test
  (:require [simple.core :as core :refer [->CreditAccountCommand
                                          ->CreditAccountCommandHandler
                                          ->AccountCreditedEvent
                                          ->AccountCreditedEventHandler
                                          ->AccountFactory]]
            [charlie-quebec-romeo-sierra.command :as command]
            [charlie-quebec-romeo-sierra.event :as event]
            [charlie-quebec-romeo-sierra.aggregate :as aggregate]
            [charlie-quebec-romeo-sierra.consumer :as consumer]
            [clj-uuid :as uuid]
            [clojure.core.async :refer [promise-chan <!! >!!]])
  (:use [midje.sweet :only [facts fact => provided irrelevant contains]])
  (:import [java.util UUID]))

(fact
  "should be able to construct account credit command"
  (core/command ..account.. ..amount..) => ..command..
  (provided
    (->CreditAccountCommand ..account.. ..amount..) => ..command..))

(fact
  "should have expected type account credit on command"
  (.type-of (core/command ..account.. ..amount..)) => "credit")

(fact
  "should create expected events for credit"
  (.handle (->CreditAccountCommandHandler)
           (->CreditAccountCommand "ff7c42f7-00ac-409a-ba09-12ec588dcdd5"
                                   ..amount..)) => (list ..account_credited..)
  (provided
    (->AccountCreditedEvent
      "credit"
      :ff7c42f7-00ac-409a-ba09-12ec588dcdd5
      {:amount ..amount..}) => ..account_credited..))

(fact
  "should handle created credit events"
  (let [channel (promise-chan)]
    (.handle (->AccountCreditedEventHandler channel)
             {:key ..uuid..
              :value {:amount ..amount..}})
    (<!! channel) => "account: ..uuid.., credit: ..amount.."))

(fact
  "should process account credit command"
  (core/process-command ..command..) => ..result..
  (provided
    (command/process ..command..) => ..result..))

(facts
  "when aggregate and handlers registered"

  (require '[simple.core :as core :refer [->CreditAccountCommand
                                          ->CreditAccountCommandHandler
                                          ->AccountCreditedEvent
                                          ->AccountCreditedEventHandler
                                          ->AccountFactory]]
           :reload)

  (fact
    "should handle credit event"
    (let [uuid_string "9dd43770-af98-11e7-8f37-758b0816f6b7"
          uuid (UUID/fromString uuid_string)
          handle_event_channel (promise-chan)]
      (aggregate/register-aggregate core/TYPE_OF (->AccountFactory))
      (command/register-handler core/TYPE_OF (->CreditAccountCommandHandler))
      (event/register-handler core/TYPE_OF (->AccountCreditedEventHandler
                                             handle_event_channel))
      (consumer/consumer core/TYPE_OF (fn [event]))
      (do
        (core/process-command (core/command uuid_string
                                            100))
        (<!! handle_event_channel)) => (str "account: "
                                            ":" uuid_string ", "
                                            "credit: 100"))))
