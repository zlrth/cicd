(ns cicd.steps
  (:require [lambdacd.steps.shell :as shell]
            [lambdacd-git.core :as git]
            [clojure.tools.logging :as log]))

(def repo-uri "git@github.com:Stylitics/clj-collage.git")

(defn clone [args ctx]
  (log/log :info (str "args FIND CWD are: " args))
  (git/clone ctx repo-uri (:revision args) (:cwd args)))

(defn some-step-that-does-nothing [args ctx]
  {:status :success})

(defn some-step-that-echos-foo [args ctx]
  (shell/bash ctx "/" "echo foo"))

(defn some-step-that-echos-bar [args ctx]
  (shell/bash ctx "/" "echo bar"))

(defn some-failing-step [args ctx]
  (shell/bash ctx "/" "echo \"i am going to fail now...\"" "exit 1"))
