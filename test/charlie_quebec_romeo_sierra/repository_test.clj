(ns charlie-quebec-romeo-sierra.repository-test
  (:require [charlie-quebec-romeo-sierra.repository :as repository]
            [charlie-quebec-romeo-sierra.event :as event]
            [charlie-quebec-romeo-sierra.aggregate :as aggregate]
            [monger.core :as mongo]
            [monger.collection :as mongo.collection]
            [midje.open-protocols :refer [defrecord-openly]])
  (:use [midje.sweet :only [facts fact => provided irrelevant unfinished]]))

(unfinished processor)

(facts

  (defrecord-openly TestEvent []
    event/Event
    (event/data [this] ..data..)
    (event/aggregate-type [this] ..aggregate_type..)
    (event/aggregate-identifier [this] ..identifier..)
    (event/type-of [this] ..type..))

  (defrecord-openly TestAggregate []
    aggregate/Aggregate
    (aggregate/process [this event] (processor event)))

  (fact
    "should get connection"
    (#'repository/connection) => ..connection..
    (provided
      (mongo/connect {:host "localhost"}) => ..connection..))

  (fact
    "should get database name"
    (#'repository/database-name) => "cqrs")

  (fact
    "should persist event"
    (let [event (->TestEvent)
          serialized_event (pr-str event)]
      (#'repository/persist-events ..database..
                                   (list event)) => ..result..
      (provided
        (mongo.collection/insert-batch
          ..database..
          "events"
          [{:aggregate_identifier ..identifier..
            :event serialized_event}]) => ..result..)))

  (fact
    "should save"
    (repository/save ..events..) => irrelevant
    (provided
      (#'repository/connection) => ..connection..
      (#'repository/database-name) => ..database_name..
      (mongo/get-db ..connection.. ..database_name..) => ..database..
      (#'repository/persist-events ..database.. ..events..) => irrelevant
      (mongo/disconnect ..connection..) => irrelevant))

  (fact
    "should load aggregate events"
    (let [test_aggregate (->TestAggregate)
          event (->TestEvent)
          events (list {:event (pr-str event)})]
      (repository/load-aggregate ..aggregate_type..
                                 ..identifier..) => test_aggregate
      (provided
        (#'repository/connection) => ..connection..
        (#'repository/database-name) => ..database_name..
        (mongo/get-db ..connection.. ..database_name..) => ..database..
        (mongo.collection/find-maps
          ..database..
          "events"
          {:aggregate_identifier ..identifier..}) => events
        (aggregate/get-aggregate ..aggregate_type..
                                 ..identifier..) => test_aggregate
        (processor event) => irrelevant))))
