(ns charlie-quebec-romeo-sierra.producer-test
  (:require [charlie-quebec-romeo-sierra.producer :as producer]
            [charlie-quebec-romeo-sierra.event :as event]
            [charlie-quebec-romeo-sierra.repository :as repository]
            [charlie-quebec-romeo-sierra.aggregate :as aggregate]
            [kinsky.client :as client]
            [midje.open-protocols :refer [defrecord-openly]])
  (:use [midje.sweet :only [facts
                            fact
                            =>
                            provided
                            irrelevant
                            defchecker
                            unfinished]]))

(unfinished processor)

(facts
  (require '[charlie-quebec-romeo-sierra.producer :as producer
             :refer :all]
           :reload)

  (defrecord-openly TestEvent []
    event/Event
    (event/data [this] ..data..)
    (event/aggregate-identifier [this] "coconuts")
    (event/type-of [this] ..type..))

  (defrecord-openly TestAggregate [valid]
    aggregate/Aggregate
    (aggregate/identifier [this] "coconuts")
    (aggregate/valid? [this event] valid)
    (aggregate/process [this event] (processor this event)))

  (fact
    "should create producer"
    (#'producer/create-producer) => ..producer..
    (provided
      (client/producer {:bootstrap.servers "localhost:9092"}
                       :keyword
                       :edn) => ..producer..))

  (fact
    "should be invalid"
    (let [event (->TestEvent)
          events (list event)
          initial_aggregate (->TestAggregate true)
          aggregate (->TestAggregate false)
          aggregates (atom {"coconuts" initial_aggregate})]
      (#'producer/valid? aggregates events) => false
      (provided
        (processor initial_aggregate event) => aggregate)))

  (fact
    "should be valid"
    (let [event (->TestEvent)
          events (list event)
          initial_aggregate (->TestAggregate false)
          aggregate (->TestAggregate true)
          aggregates (atom {"coconuts" initial_aggregate})]
      (#'producer/valid? aggregates events) => true
      (provided
        (processor initial_aggregate event) => aggregate)))

  (defchecker aggregates-checker
    [actual]
    (and (= (type actual) clojure.lang.Atom)
         (= @actual {"coconuts" ..aggregate..})))

  (fact
    "should produce events"
    (let [event (->TestEvent)
          events (list event)
          aggregates {"coconuts" ..aggregate..}]
      (producer/produce events) => irrelevant
      (provided
        (repository/load-aggregate "coconuts") => ..aggregate..
        (#'producer/valid? aggregates-checker events) => true
        (repository/save events) => irrelevant
        (#'producer/producer) => ..producer..
        (client/send! ..producer..
                      ..type..
                      "coconuts"
                      ..data..) => ..result..)))

  (fact
    "should not produce event if invalid"
    (let [event (->TestEvent)
          events (list event)
          aggregates {"coconuts" ..aggregate..}]
      (producer/produce events) => irrelevant
      (provided
        (repository/load-aggregate "coconuts") => ..aggregate..
        (#'producer/valid? aggregates-checker events) => false
        (client/send! ..producer..
                      ..type..
                      "coconuts"
                      ..data..) => ..result.. :times 0))))
