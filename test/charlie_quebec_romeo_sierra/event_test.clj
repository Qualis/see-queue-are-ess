(ns charlie-quebec-romeo-sierra.event-test
  (:require [charlie-quebec-romeo-sierra.event
             :as event
             :refer :all]
            [midje.open-protocols :refer [defrecord-openly]])
  (:use [midje.sweet :only [facts fact => provided]]))

(facts

  (defrecord-openly TestEvent []
    event/Event
    (revision [this] ..revision..)
    (data [this] ..data..))

  (fact
    "should have revision"
    (revision (->TestEvent)) => ..revision..)

  (fact
    "should have data"
    (data (->TestEvent)) => ..data..))