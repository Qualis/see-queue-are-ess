(ns charlie-quebec-romeo-sierra.event-test
  (:require [charlie-quebec-romeo-sierra.event
             :as event
             :refer :all]
            [midje.open-protocols :refer [defrecord-openly]])
  (:use [midje.sweet :only [facts fact => provided]]))

(facts

  (require '[charlie-quebec-romeo-sierra.event
             :as event
             :refer :all] :reload)

  (defrecord-openly TestEvent []
    event/Event
    (data [this] ..data..)
    (aggregate-identifier [this] ..identifier..)
    (aggregate-type [this] ..aggregate_type..)
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
    "should have aggregate type"
    (aggregate-type (->TestEvent)) => ..aggregate_type..)

  (fact
    "should have type"
    (type-of (->TestEvent)) => ..type..)

  (fact
    "should register first handler"
    (let [handler (->TestEventHandler)
          handlers (list handler)]
      (event/register-handler ..type_of.. handler)
      @event/handlers => {..type_of.. handlers}))

  (fact
    "should register new handler"
      (let [existing_handler (reify event/EventHandler)
            handler (->TestEventHandler)
            handlers (list handler existing_handler)]
        (with-redefs [event/handlers (atom {..type_of.. (list
                                                          existing_handler)})]
          (event/register-handler ..type_of.. handler)
          @event/handlers => {..type_of.. handlers})))

  (fact
    "should find handler"
    (let [handler (->TestEventHandler)
          handlers (list handler)]
      (with-redefs [event/handlers (atom {..type_of.. handlers})]
        (event/find-handler ..type_of..) => handlers))))
