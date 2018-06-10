(ns cicd.pipeline
  (:use [lambdacd.steps.control-flow]
        [cicd.steps])
  (:require
        [lambdacd.steps.manualtrigger :as manualtrigger]))

(def pipeline-def
  `(
    manualtrigger/wait-for-manual-trigger
    some-step-that-does-nothing
    (with-workspace
      clone)))
