(ns simple.core-test
  (:require [simple.core :as core :refer [->CreditAccountCommand
                                          ->CreditAccountCommandHandler
                                          ->AccountCreditedEvent
                                          ->AccountCreditedEventHandler
                                          ->AccountFactory
                                          ->Account]]
            [charlie-quebec-romeo-sierra.command :as command]
            [charlie-quebec-romeo-sierra.repository :as repository]
            [charlie-quebec-romeo-sierra.event :as event]
            [charlie-quebec-romeo-sierra.aggregate :as aggregate]
            [charlie-quebec-romeo-sierra.consumer :as consumer]
            [clj-uuid :as uuid]
            [clojure.core.async :refer [promise-chan <!! >!!]])
  (:use [midje.sweet :only [facts fact => provided irrelevant against-background]])
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
  "Account"

  (fact
    "should process event"
    (.data (.process (->Account ..type_of.. ..identifier.. (atom {:balance 0}))
                     (->AccountCreditedEvent ..type_of..
                                             ..identifier..
                                             {:amount 100}))) => {:balance 100}))

(facts
  "with aggregate and command handlers registered"

  (require '[simple.core :as core :refer [->CreditAccountCommand
                                          ->CreditAccountCommandHandler
                                          ->AccountCreditedEvent
                                          ->AccountCreditedEventHandler
                                          ->AccountFactory
                                          ->Account]]
           :reload)

  (defn- clear-registered
    []
    (aggregate/clear)
    (command/clear)
    (event/clear)
    (aggregate/register-aggregate core/TYPE_OF (->AccountFactory))
    (command/register-handler core/TYPE_OF (->CreditAccountCommandHandler)))

  (against-background
    [(before :facts (clear-registered))]

    (fact
      "should propagate to credit event handler"
      (let [uuid (str (uuid/v1))
            handle_event_channel (promise-chan)]
        (event/register-handler core/TYPE_OF (->AccountCreditedEventHandler
                                               handle_event_channel))
        (consumer/consumer core/TYPE_OF (fn [event]))
        (do
          (core/process-command (core/command uuid
                                              100))
          (<!! handle_event_channel)) => (str "account: "
                                              ":" uuid ", "
                                              "credit: 100")))

    (fact
      "should load account aggregate balance result"
      (let [uuid (str (uuid/v1))
            handle_event_channel (promise-chan)]
        (event/register-handler core/TYPE_OF (->AccountCreditedEventHandler
                                               handle_event_channel))
        (consumer/consumer core/TYPE_OF (fn [event]))
        (core/process-command (core/command uuid 125))
        (core/process-command (core/command uuid 150))
        (<!! handle_event_channel)
        (<!! handle_event_channel)
        (.data (repository/load-aggregate core/TYPE_OF
                                          uuid)) => {:balance 275}))))
