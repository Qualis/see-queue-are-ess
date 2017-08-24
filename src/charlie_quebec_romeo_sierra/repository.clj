(ns charlie-quebec-romeo-sierra.repository
  (:require [charlie-quebec-romeo-sierra.aggregate :as aggregate]
            [monger.core :as mongo]
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
    (map #(hash-map :aggregate_identifier (.aggregate-identifier %)
                    :event (pr-str %))
         events)))

(defn load-aggregate
  [type_of identifier]
  (let [database (mongo/get-db (connection) (database-name))
        aggregate (aggregate/get-aggregate type_of
                                           identifier)]
    (doseq [event (mongo.collection/find-maps
                    database
                    "events"
                    {:aggregate_identifier identifier})]
      (.process aggregate (read-string (:event event))))
    aggregate))

(defn save
  [events]
  (let [connection (connection)
        database (mongo/get-db connection (database-name))]
    (persist-events database events)
    (mongo/disconnect connection)))
