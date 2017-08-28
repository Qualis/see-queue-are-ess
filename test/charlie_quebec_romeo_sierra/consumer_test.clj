(ns charlie-quebec-romeo-sierra.consumer-test
  (:require [charlie-quebec-romeo-sierra.consumer :as consumer
             :refer [->KafkaConsumerController]]
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
             :refer [->KafkaConsumerController]]
           :reload)

  (facts
    "Kafka consumer controller"

    (fact
      "should subscribe"
      (let [control (chan)
            consumer (->KafkaConsumerController control anything)]
        (.subscribe consumer ..topic..) => irrelevant
        (provided
          (put! control {:op :subscribe :topic ..topic..}) => irrelevant)))

    (fact
      "should unsubscribe"
      (let [control (chan)
            consumer (->KafkaConsumerController control anything)]
        (.unsubscribe consumer) => irrelevant
        (provided
          (put! control {:op :unsubscribe}) => irrelevant)))

    (fact
      "should commit"
      (let [control (chan)
            consumer (->KafkaConsumerController control anything)]
        (.commit consumer) => irrelevant
        (provided
          (put! control {:op :commit}) => irrelevant)))

    (fact
      "should stop"
      (let [control (chan)
            consumer (->KafkaConsumerController control anything)]
        (.stop consumer) => irrelevant
        (provided
          (put! control {:op :stop}) => irrelevant)))

    (fact
      "should return output channel"
        (.output-channel (->KafkaConsumerController
                           ..control..
                           ..output..)) => ..output..))

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
