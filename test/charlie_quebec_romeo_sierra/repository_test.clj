(ns charlie-quebec-romeo-sierra.repository-test
  (:require [charlie-quebec-romeo-sierra.repository :as repository]
            [charlie-quebec-romeo-sierra.event :as event]
            [monger.core :as mongo]
            [monger.collection :as mongo.collection]
            [midje.open-protocols :refer [defrecord-openly]])
  (:use [midje.sweet :only [facts fact => provided irrelevant]]))

(facts

  (defrecord-openly TestEvent []
    event/Event
    (event/data [this] ..data..)
    (event/aggregate-identifier [this] "coconuts")
    (event/type-of [this] ..type..))
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
    (#'repository/persist-event ..database.. (->TestEvent)) => ..result..
    (provided
      (mongo.collection/insert-and-return
        ..database..
        ..type..
        {:aggregate_identifier "coconuts"
         :data ..data..}) => ..result..))

  (fact
    "should save"
    (repository/save (list ..event..)) => irrelevant
    (provided
      (#'repository/connection) => ..connection..
      (#'repository/database-name) => ..database_name..
      (mongo/get-db ..connection.. ..database_name..) => ..database..
      (#'repository/persist-event ..database.. ..event..) => irrelevant
      (mongo/disconnect ..connection..) => irrelevant))

  (fact
    "should load aggregate events"
    (repository/load-aggregate ..identifier..) => ..result..
    (provided
      (#'repository/connection) => ..connection..
      (#'repository/database-name) => ..database_name..
      (mongo/get-db ..connection.. ..database_name..) => ..database..
      (mongo.collection/find-maps
        ..database..
        "events"
        {:aggregate_identifier ..identifier..}) => ..result..)))
