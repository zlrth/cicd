(ns cicd.steps
  (:require [lambdacd.steps.shell :as shell]
            [lambdacd-git.core :as git]
            [clojure.tools.logging :as log]))

(def shen-uri "git@github.com:zlrth/shen.clj.git")

(defn wait-for-repo [_ ctx]
  (git/wait-for-git ctx shen-uri :ms-between-polls 5000 :ref "refs/heads/master"))

(defn clone [args ctx]
  (log/log :info (str "args FIND CWD are: " args))
  (git/clone ctx shen-uri (:revision args) (:cwd args)))

(defn mytest [args ctx]
  (shell/bash ctx (:cwd args) "lein test"))

(defn some-step-that-does-nothing [args ctx]
  {:status :success})

(defn some-step-that-echos-foo [args ctx]
  (shell/bash ctx "/" "echo foo"))

(defn some-step-that-echos-bar [args ctx]
  (shell/bash ctx "/" "echo bar"))

(defn some-failing-step [args ctx]
  (shell/bash ctx "/" "echo \"i am going to fail now...\"" "exit 1"))
