(ns cicd.core
  (:require
    [cicd.pipeline :as pipeline]
    [cicd.ui-selection :as ui-selection]
    [org.httpkit.server :as http-kit]
    [lambdacd.runners :as runners]
    [lambdacd.core :as lambdacd]
    [clojure.tools.logging :as log])
  (:import (java.nio.file.attribute FileAttribute)
    (java.nio.file Files LinkOption))
  (:gen-class))

(defn- create-temp-dir []
  (str (Files/createTempDirectory "lambdacd" (into-array FileAttribute []))))

(defn -main [& args]
  (let [;; the home dir is where LambdaCD saves all data.
        ;; point this to a particular directory to keep builds around after restarting
        home-dir (create-temp-dir)
        config   {:home-dir home-dir
                  :name     "cicd"
                  :git {:timeout              20
                        :ssh {:use-agent                true
                              :known-hosts-files        ["~/.ssh/known_hosts"
                                                         "/etc/ssh/ssh_known_hosts"]
                              :identity-file            nil
                              :strict-host-key-checking nil}}}
        ;; initialize and wire everything together
        pipeline (lambdacd/assemble-pipeline pipeline/pipeline-def config)
        ;; create a Ring handler for the UI
        app      (ui-selection/ui-routes pipeline)]
    (log/info "LambdaCD Home Directory is " home-dir)
    ;; this starts the pipeline and runs one build after the other.
    ;; there are other runners and you can define your own as well.
    (runners/start-one-run-after-another pipeline)
    ;; start the webserver to serve the UI
    (http-kit/run-server app {:open-browser? false
                              :port          8080})))