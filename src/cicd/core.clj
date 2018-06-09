(ns cicd.core
  (:require
    [cicd.pipeline :as pipeline]
    [cicd.ui-selection :as ui-selection]
    [org.httpkit.server :as http-kit]
    [lambdacd.runners :as runners]
    [lambdacd.core :as lambdacd]
    [clojure.tools.logging :as log]
    [integrant.core :as ig])
  (:import (java.nio.file.attribute FileAttribute)
    (java.nio.file Files LinkOption))
  (:gen-class))

(defn- create-temp-dir []
  (str (Files/createTempDirectory "lambdacd" (into-array FileAttribute []))))

(def pipeline-config
  {:home-dir (create-temp-dir) ;; put separately?
   :name     "cicd"
   :git {:timeout              20
         :ssh {:use-agent                true
               :known-hosts-files        ["~/.ssh/known_hosts"
                                          "/etc/ssh/ssh_known_hosts"]
               :identity-file            nil
               :strict-host-key-checking nil}}})

(def config {:webserver {:handler (ig/ref :ui-routes)}
             :ui-routes {:handler (ig/ref :pipeline)}
             :pipeline  {#_:pipeline-def #_pipeline/pipeline-def #_:pipeline-config #_pipeline-config}
             })

(defmethod ig/init-key :pipeline [_ {:keys [#_pipeline-def #_pipeline-config]}]
  (def assembled-pipeline (lambdacd/assemble-pipeline pipeline/pipeline-def pipeline-config))
  (lambdacd/assemble-pipeline pipeline/pipeline-def pipeline-config))

(defmethod ig/init-key :ui-routes [_ {:keys [handler]}]
  (log/log :info "ui-routes log--the handler that's going into ui-routes")
  (def initkeyuirouteshandler handler)
  (log/log :info handler)
  (def ui-routestop (ui-selection/ui-routes assembled-pipeline)
    )
  (ui-selection/ui-routes assembled-pipeline))

(defmethod ig/init-key :webserver [_ {:keys [handler] :as opts}]
  (log/log :info "webserver log")
  (log/log :info handler)
  (http-kit/run-server ui-routestop  {:open-browser? false
                                      :port          8080}))



(defmethod ig/halt-key! :webserver [_ server-stop-command]
  (server-stop-command)) ;; (http-kit/run-server) returns the function to stop the server

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
        app      (ui-selection/ui-routes pipeline)]
    (log/info "LambdaCD Home Directory is " home-dir)
    ;; this starts the pipeline and runs one build after the other.
    ;; there are other runners and you can define your own as well.
    (runners/start-one-run-after-another pipeline)
    ;; start the webserver to serve the UI
    (http-kit/run-server app {:open-browser? false
                              :port          8080})))
