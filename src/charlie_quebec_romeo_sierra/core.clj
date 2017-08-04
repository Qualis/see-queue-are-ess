(ns charlie-quebec-romeo-sierra.core
  (:require [trptcolin.versioneer.core :as versioning]))

(defn version
  []
  (versioning/get-version
    "is.qual"
    "charlie-quebec-romeo-sierra"))
