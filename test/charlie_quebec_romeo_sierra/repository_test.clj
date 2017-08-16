(ns charlie-quebec-romeo-sierra.repository-test
  (:require [charlie-quebec-romeo-sierra.repository :as repository]
            [charlie-quebec-romeo-sierra.event
             :as event :refer :all]
            [kinsky.client :as client]
            [kinsky.async :as async]
            [clj-uuid :as uuid]
            [clojure.core.async :refer [promise-chan promise-chan]]
            [midje.open-protocols :refer [defrecord-openly]])
  (:use [midje.sweet :only [facts fact => provided irrelevant]]))

(facts

  (require '[charlie-quebec-romeo-sierra.repository :as repository
             :refer :all]
           :reload)

  (defrecord-openly TestEvent []
    event/Event
    (data [this] ..data..)
    (aggregate-identifier [this] ..identifier..)
    (type-of [this] ..type..))

  (fact
    "should create producer"
    (#'repository/create-producer) => ..producer..
    (provided
      (client/producer {:bootstrap.servers "localhost:9092"}
                       :keyword
                       :edn) => ..producer..))

  (fact
    "should register consumer"
    (#'repository/register-consumer ..type_of.. ..consumer..)
    @repository/consumers => {..type_of.. ..consumer..})

  (fact
    "should create consumer"
    (#'repository/create-consumer ..type_of..) => irrelevant
    (provided
      (client/keyword-deserializer) => ..keyword_deserializer..
      (client/edn-deserializer) => ..value_deserializer..
      (async/consumer {:bootstrap.servers "localhost:9092"
                       :group.id ..type_of..}
                      ..keyword_deserializer..
                      ..value_deserializer..) => ..consumer..
      (#'repository/register-consumer ..type_of.. ..consumer..) => irrelevant))

  (fact
    "should produce events"
    (let [event (->TestEvent)]
      (repository/produce [event]) => irrelevant
      (provided
        (#'repository/producer) => ..producer..
        (client/send! ..producer..
                      ..type..
                      ..identifier..
                      ..data..) => ..result..))))
