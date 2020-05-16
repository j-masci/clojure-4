(ns tests
  (:require awt_input)
  (:use clojure.test))

(deftest test-is-key-up
  (let [evs [{:type :key-up :key-text "j"}]]
    (is (awt_input/is-key-up evs "j"))))
