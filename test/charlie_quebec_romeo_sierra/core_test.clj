(ns charlie-quebec-romeo-sierra.core-test
  (:require [charlie-quebec-romeo-sierra.core :as core]
            [trptcolin.versioneer.core :as versioning])
  (:use [midje.sweet :only [fact =>]]))

(fact
  "should return version details"
  (core/version) => ..version..
  (provided
    (versioning/get-version "is.qual"
                            "charlie-quebec-romeo-sierra") => ..version..))
