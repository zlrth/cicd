(ns cicd.pipeline
  (:use [lambdacd.steps.control-flow]
        [cicd.steps])
  (:require
   [lambdacd.steps.manualtrigger :as manualtrigger]
   [lambdacd-git.core :as git]))

(def pipeline-def
  `(
    (either
     manualtrigger/wait-for-manual-trigger
     wait-for-repo)
    some-step-that-does-nothing
    (with-workspace
      clone
      mytest)))
