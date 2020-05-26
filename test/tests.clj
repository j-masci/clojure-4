(ns tests
  (:require input
            colors
            _core
            shapes
            vec
            ents
            test-utils)
  (:use clojure.test))

(deftest test-numbers-almost-equal
  (is (test-utils/numbers-almost-equal 1 5 4.1))
  (is (false? (test-utils/numbers-almost-equal 1 5 10))))

(deftest test-vectors-almost-equal
  (is (test-utils/vectors-almost-equal 0.1 [5 5 5] [4.999 5 5.05]))
  (is (false? (test-utils/vectors-almost-equal 0.5 [5 5 5] [4 5 6]))))

(deftest test-is-key-up
  (let [evs [{:type :key-up :text "j"}]]
    (is (input/is-key-up evs "j"))))

(deftest test-that-im-not-an-idiot
  (let [evs [{:type :key-up :text "ASDKUJHGASDJKHGASD"}]]
    (is (= false (input/is-key-up evs "j")) "So all keys are up, all the time?")))

(deftest test-color-check
  (is (= (colors/check [100 100 100]) [100 100 100 255]) "Failed to add a logical default alpha.")
  (is (= (colors/check [100 100 100] 199) [100 100 100 199]) "Didn't properly accept the specified alpha.")
  (is (= (colors/check [5 5 5 5] 25) [5 5 5 5]) "Modified an already valid vector when we provided a default alpha.")
  (is (= (colors/check [101 101 101 99]) [101 101 101 99]) "Modified an already valid vector."))

