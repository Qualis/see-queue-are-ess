(ns charlie-quebec-romeo-sierra.consumer-test
  (:require [charlie-quebec-romeo-sierra.consumer :as consumer
             :refer [->ConsumerController]]
            [charlie-quebec-romeo-sierra.event :as event]
            [charlie-quebec-romeo-sierra.aggregate :as aggregate]
            [kinsky.client :as client]
            [kinsky.async :as async]
            [clj-uuid :as uuid]
            [clojure.core.async :refer [chan put!]])
  (:use [midje.sweet :only [facts
                            fact
                            =>
                            provided
                            irrelevant
                            anything
                            unfinished]]))

(unfinished handler)

(facts
  (require '[charlie-quebec-romeo-sierra.consumer :as consumer
             :refer [->ConsumerController]]
           :reload)

  (facts
    "Consumer"

    (fact
      "should unsubscribe"
      (let [control (chan)
            consumer (->ConsumerController control anything)]
        (.unsubscribe consumer) => irrelevant
        (provided
          (put! control {:op :unsubscribe}) => irrelevant)))

    (fact
      "should stop"
      (let [control (chan)
            consumer (->ConsumerController control anything)]
        (.stop consumer) => irrelevant
        (provided
          (put! control {:op :stop}) => irrelevant)))

    (fact
      "should return record channel"
        (.record-channel (->ConsumerController
                           ..control..
                           ..record_channel..)) => ..record_channel..))

  (fact
    "should register consumer"
    (#'consumer/register-consumer ..type_of.. ..consumer..)
    @consumer/consumers => {..type_of.. ..consumer..})

  (fact
    "should return registered consumer"
    (#'consumer/register-consumer ..type_of.. ..consumer..) => ..consumer..)

  (fact
    "should create consumer"
    (#'consumer/create-consumer ..type_of..
                                ..handler..) => irrelevant
    (provided
      (client/keyword-deserializer) => ..keyword_deserializer..
      (client/edn-deserializer) => ..value_deserializer..
      (async/consumer {:bootstrap.servers "localhost:9092"
                       :group.id ..type_of..}
                      ..keyword_deserializer..
                      ..value_deserializer..) => ..consumer..
      (#'consumer/register-consumer ..type_of..
                                    ..consumer..) => ..consumer..
      (#'consumer/listen ..type_of..
                         ..consumer..
                         ..handler..) => irrelevant)))
