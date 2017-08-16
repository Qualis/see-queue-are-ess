(ns charlie-quebec-romeo-sierra.event-test
  (:require [charlie-quebec-romeo-sierra.event
             :as event
             :refer :all]
            [midje.open-protocols :refer [defrecord-openly]])
  (:use [midje.sweet :only [facts fact => provided]]))

(facts

  (defrecord-openly TestEvent []
    event/Event
    (data [this] ..data..)
    (aggregate-identifier [this] ..identifier..)
    (type-of [this] ..type..))

  (defrecord-openly TestEventHandler []
    event/EventHandler
    (handle [this event] ..handler_result..))

  (fact
    "should have data"
    (data (->TestEvent)) => ..data..)

  (fact
    "should have aggregate identifier"
    (aggregate-identifier (->TestEvent)) => ..identifier..)

  (fact
    "should have type"
    (type-of (->TestEvent)) => ..type..)

  (fact
    "should register handler"
    (let [handler (->TestEventHandler)]
      (#'event/register-handler ..type_of.. handler)
      @event/handlers => {..type_of.. handler}))

  (fact
    "should find handler"
    (let [handler (->TestEventHandler)]
      (with-redefs [event/handlers (atom {..type_of.. handler})]
        (#'event/find-handler ..type_of..) => handler))))
