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
  {:home-dir "some-builds"
   :name     "cicd"
   :git {:timeout              2000
         :ssh {:use-agent                true
               :known-hosts-files        ["~/.ssh/known_hosts"
                                          "/etc/ssh/ssh_known_hosts"]
               :identity-file            nil
               :strict-host-key-checking nil}}})

(def config {:webserver {:handler (ig/ref :ui-routes)} ;; have to reload config after changing pipeline-def
             :ui-routes {:handler (ig/ref :pipeline)}
             :runners   {:handler (ig/ref :pipeline)}
             :pipeline  {:pipeline-def pipeline/pipeline-def :pipeline-config pipeline-config}})

(defmethod ig/init-key :pipeline [_ {:keys [pipeline-def pipeline-config]}]
  (lambdacd/assemble-pipeline pipeline-def pipeline-config))

(defmethod ig/init-key :ui-routes [_ {:keys [handler]}]
  (ui-selection/ui-routes handler))

(defmethod ig/init-key :runners [_ {:keys [handler] :as opts}]
  {:runners (runners/start-one-run-after-another handler)
   :handler handler})

(defmethod ig/init-key :webserver [_ {:keys [handler] :as opts}]
  (http-kit/run-server handler  {:open-browser? false
                                 :port          8080}))

(defmethod ig/halt-key! :webserver [_ server-stop-command]
  (server-stop-command))

(defmethod ig/halt-key! :runners [_  {:keys [handler]}]
  (runners/stop-runner (:context handler)))
