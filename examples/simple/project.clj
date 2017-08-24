(defproject simple "0.1.0-SNAPSHOT"
  :description "Simple example of Charlie Quebec Romeo Sierra"
  :url "https://github.com/svo/charlie-quebec-romeo-sierra"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [charlie-quebec-romeo-sierra/charlie-quebec-romeo-sierra "0.0.1-SNAPSHOT"]]

  :min-lein-version "2.0.0"
  :pedantic? :abort

  :profiles {:dev
             {:aliases {"test" "midje"}
              :dependencies [[midje "1.8.3"]]}})
