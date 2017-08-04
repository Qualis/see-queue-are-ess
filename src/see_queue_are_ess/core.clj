(ns see-queue-are-ess.core
  (:require [trptcolin.versioneer.core :as versioning]))

(defn version
  []
  (versioning/get-version
    "is.qual"
    "see-queue-are-ess"))
