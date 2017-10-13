(defproject simple "0.1.0-SNAPSHOT"
  :description "Simple example of Charlie Quebec Romeo Sierra"
  :url "https://github.com/svo/charlie-quebec-romeo-sierra"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [charlie-quebec-romeo-sierra/charlie-quebec-romeo-sierra "0.0.1-SNAPSHOT"]
                 [danlentz/clj-uuid "0.1.7"]]

  :min-lein-version "2.0.0"
  :pedantic? :abort

  :profiles {:dev
             {:aliases {"test" "midje"}
              :plugins [[lein-midje "3.2.1"]]
              :dependencies [[midje "1.8.3"]]}})
