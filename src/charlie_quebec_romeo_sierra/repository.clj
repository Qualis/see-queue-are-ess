(ns charlie-quebec-romeo-sierra.repository
  (:require [monger.core :as mongo]
            [monger.collection :as mongo.collection]))

(def ^:const DATABASE_NAME "cqrs")

(defn connection
  []
  (mongo/connect {:host "localhost"}))

(defn database-name
  []
  DATABASE_NAME)

(defn- persist-events
  [database events]
  (mongo.collection/insert-batch
    database
    "events"
    (map #(hash-map :data (.data %)
                    :aggregate_identifier (.aggregate-identifier %))
         events)))

(defn load-aggregate
  [identifier]
  (let [database (mongo/get-db (connection) (database-name))]
    (mongo.collection/find-maps database
                                "events"
                                {:aggregate_identifier identifier})))

(defn save
  [events]
  (let [connection (connection)
        database (mongo/get-db connection (database-name))]
    (persist-events database events)
    (mongo/disconnect connection)))
