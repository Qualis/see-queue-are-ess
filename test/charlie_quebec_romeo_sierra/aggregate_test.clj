(ns charlie-quebec-romeo-sierra.aggregate-test
  (:require [charlie-quebec-romeo-sierra.aggregate
             :as aggregate
             :refer :all]
            [midje.open-protocols :refer [defrecord-openly]])
  (:use [midje.sweet :only [facts fact => provided]]))

(facts

  (defrecord-openly TestAggregate []
    aggregate/Aggregate
    (data [this] ..data..)
    (identifier [this] ..identifier..)
    (type-of [this] ..type..)
    (is-valid [this event] ..result..))

  (fact
    "should have data"
    (data (->TestAggregate)) => ..data..)

  (fact
    "should have identifier"
    (identifier (->TestAggregate)) => ..identifier..)

  (fact
    "should have type"
    (type-of (->TestAggregate)) => ..type..)

  (fact
    "should determine if event is valid for aggregate"
    (is-valid (->TestAggregate) ..event..) => ..result..))
