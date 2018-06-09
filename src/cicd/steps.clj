(ns cicd.steps
  (:require [lambdacd.steps.shell :as shell]
            [lambdacd-git.core :as git]))

(def repo-uri "https://github.com/Stylitics/clj-collage.git")

  (def ssh-uri "git@github.com:Stylitics/clj-collage.git")
  (def shen-uri "git@github.com:zlrth/shen-rss.git")
(defn clone-shen [args ctx]
  (git/clone ctx shen-uri (:revision args) (:cwd args)))

(defn some-step-that-does-nothing [args ctx]
  {:status :success})

(defn some-step-that-echos-foo [args ctx]
  (shell/bash ctx "/" "echo foo"))

(defn some-step-that-echos-bar [args ctx]
  (shell/bash ctx "/" "echo bar"))

(defn some-failing-step [args ctx]
  (shell/bash ctx "/" "echo \"i am going to fail now...\"" "exit 1"))
