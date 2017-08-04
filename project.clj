(defproject see-queue-are-ess "0.0.1-SNAPSHOT"
  :description "A CQRS library for Clojure"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [trptcolin/versioneer "0.2.0"]]
  :min-lein-version "2.0.0"
  :pedantic? :abort


  :profiles {:dev
             {:aliases {"test" "midje"
                        "quality" ["do" ["eastwood"] ["kibit"] ["kibit" "test"]]}
              :plugins [[lein-midje "3.2.1"]
                        [jonase/eastwood "0.2.4"]
                        [lein-kibit "0.1.5" :exclusions [org.clojure/clojure]]]
              :dependencies [[midje "1.8.3"]]}})
