(defproject cicd "0.1.0-SNAPSHOT"
            :description "FIXME: write description"
            :url "http://example.com/FIXME"
            :dependencies [[lambdacd "0.14.0"]
                           [lambdaui "1.1.0"]
                           [http-kit "2.2.0"]
                           [org.clojure/clojure "1.9.0"]
                           [org.clojure/tools.logging "0.3.1"]
                           [org.slf4j/slf4j-api "1.7.5"]
                           [ch.qos.logback/logback-core "1.0.13"]
                           [ch.qos.logback/logback-classic "1.0.13"]
                           [lambdacd-git "0.4.1"]
                           [integrant "0.7.0-alpha2"]
                           [integrant/repl "0.3.1"]]
  :main cicd.core
  :profiles {:uberjar {:aot :all}
             :dev     {:source-paths ["src/clj" "src/cljc" "dev"]
                       ;; :plugins [[venantius/ultra "0.5.1"]]
                       :repl-options {:init-ns user
                                      :timeout 200000
                                      ;; :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]
                                      }
                       #_:dependencies #_[[figwheel-sidecar "0.5.14"]
                                      [org.clojure/tools.nrepl "0.2.13"]
                                      [com.cemerick/piggieback "0.2.2"]
                                      [org.clojure/test.check "0.9.0"]]}
             #_:uberjar #_{:aot [collage.handler]
                       :prep-tasks ["compile" ["cljsbuild" "once" "release"]]
                       :omit-source true}})
