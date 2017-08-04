(ns see-queue-are-ess.core-test
  (:require [see-queue-are-ess.core :as core]
            [trptcolin.versioneer.core :as versioning])
  (:use [midje.sweet :only [fact =>]]))

(fact
  "should return version details"
  (core/version) => ..version..
  (provided
    (versioning/get-version "is.qual"
                            "see-queue-are-ess") => ..version..))
