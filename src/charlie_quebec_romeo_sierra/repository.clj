(ns charlie-quebec-romeo-sierra.repository
  (:require [monger.core :as mongo]
            [monger.collection :as mongo.collection]))

(def ^:const DATABASE_NAME "event")

(defn connection
  []
  (mongo/connect {:host "localhost"}))

(defn database-name
  []
  DATABASE_NAME)

(defn- persist-event
  [database event]
  (mongo.collection/insert-and-return
    database
    (.type-of event)
    {:data (.data event)
     :aggregate_identifier (.aggregate-identifier event)}))

(defn load-aggregate
  [identifier])

(defn save
  [events]
  (let [connection (connection)
        database (mongo/get-db connection (database-name))]
    (doseq [event events]
      (persist-event database event))
    (mongo/disconnect connection)))
