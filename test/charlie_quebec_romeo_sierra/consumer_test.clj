(ns charlie-quebec-romeo-sierra.consumer-test
  (:require [charlie-quebec-romeo-sierra.consumer :as consumer
             :refer [->KafkaConsumerController]]
            [charlie-quebec-romeo-sierra.event :as event]
            [charlie-quebec-romeo-sierra.aggregate :as aggregate]
            [kinsky.client :as client]
            [kinsky.async :as async]
            [clj-uuid :as uuid]
            [clojure.core.async :refer [chan put! >!! <!!]])
  (:use [midje.sweet :only [facts
                            fact
                            =>
                            provided
                            irrelevant
                            anything
                            unfinished]]))

(unfinished handler)

(facts
  (with-redefs [event/handlers (atom {})]
    (require '[charlie-quebec-romeo-sierra.consumer :as consumer
               :refer [->KafkaConsumerController]]
             :reload)

    (fact
      "should create consumer"
      (#'consumer/consumer ..type_of..
                           ..handler..) => ..consumer..
      (provided
        (client/keyword-deserializer) => ..keyword_deserializer..
        (client/edn-deserializer) => ..value_deserializer..
        (async/consumer {:bootstrap.servers "localhost:9092"
                         :group.id ..type_of..}
                        ..keyword_deserializer..
                        ..value_deserializer..) => [..output..
                                                    ..control..]
        (->KafkaConsumerController ..output.. ..control..) => ..consumer..
        (#'consumer/listen ..type_of..
                           ..consumer..
                           ..handler..) => irrelevant))

    (facts
      "Kafka consumer controller"

      (fact
        "should subscribe"
        (let [control (chan)
              consumer (->KafkaConsumerController anything control)]
          (.subscribe consumer ..topic..) => irrelevant
          (provided
            (put! control {:op :subscribe :topic ..topic..}) => irrelevant)))

      (fact
        "should unsubscribe"
        (let [control (chan)
              consumer (->KafkaConsumerController anything control)]
          (.unsubscribe consumer) => irrelevant
          (provided
            (put! control {:op :unsubscribe}) => irrelevant)))

      (fact
        "should commit"
        (let [control (chan)
              consumer (->KafkaConsumerController anything control)]
          (.commit consumer) => irrelevant
          (provided
            (put! control {:op :commit}) => irrelevant)))

      (fact
        "should stop"
        (let [control (chan)
              consumer (->KafkaConsumerController anything control)]
          (.stop consumer) => irrelevant
          (provided
            (put! control {:op :stop}) => irrelevant)))

      (fact
        "should return output channel"
        (.output-channel (->KafkaConsumerController
                           ..output..
                           anything)) => ..output..))

    (facts
      "consumer"

      (fact
        "should be subscribed to events of type"
        (let [subscribed_type (atom nil)]
          (#'consumer/listen ..type_of..
                             (reify consumer/ConsumerController
                               (output-channel [_] (chan))
                               (subscribe [_ type_of]
                                 (reset! subscribed_type type_of))
                               (commit [_]))
                             (fn [event]))
          @subscribed_type => ..type_of..))

      (fact
        "should be committed"
        (let [commited (atom false)]
          (#'consumer/listen ..type_of..
                             (reify consumer/ConsumerController
                               (output-channel [_] (chan))
                               (subscribe [_ type_of])
                               (commit [_]
                                 (reset! commited true)))
                             (fn [event]))
          @commited => true))

      (fact
        "should have messages consumed"
        (let [expected_event {:key ..key.. :value ..value..}
              consumer_channel (chan)
              handler_channel (chan)]
          (#'consumer/listen ..type_of..
                             (reify consumer/ConsumerController
                               (output-channel [_] consumer_channel)
                               (subscribe [_ type_of]
                                 type_of => ..type_of..)
                               (commit [_]))
                             #(>!! handler_channel %))
          (>!! consumer_channel {:type :record
                                 :key ..key..
                                 :value ..value..})
          (<!! handler_channel) => expected_event)))))

(facts
  "event handlers"
  (require '[charlie-quebec-romeo-sierra.consumer :as consumer
             :refer [->KafkaConsumerController]]
           :reload)

  (fact
    "should handle messages"
    (let [expected_event {:key ..key.. :value ..value..}
          consumer_channel (chan)
          event_handler_channel (chan)
          event_handlers (list (reify event/EventHandler
                                 (handle [_ event]
                                   (>!! event_handler_channel event))))]
      (with-redefs [event/handlers (atom {..type_of.. event_handlers})]
        (#'consumer/listen ..type_of..
                           (reify consumer/ConsumerController
                             (output-channel [_] consumer_channel)
                             (subscribe [_ type_of])
                             (commit [_]))
                           (fn [event]))
        (>!! consumer_channel {:type :record
                               :key ..key..
                               :value ..value..})
        (<!! event_handler_channel) => expected_event))))
