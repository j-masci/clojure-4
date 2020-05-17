(ns tests
  (:require input)
  (:use clojure.test))

(deftest test-is-key-up
  (let [evs [{:type :key-up :text "j"}]]
    (is (input/is-key-up evs "j"))))

(deftest test-that-im-not-an-idiot
  (let [evs [{:type :key-up :text "ASDKUJHGASDJKHGASD"}]]
    (is (= false (input/is-key-up evs "j")))))