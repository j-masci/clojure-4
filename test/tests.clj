(ns tests
  (:require input
            colors
            _core
            shapes
            vec
            ents)
  (:use clojure.test))

(deftest test-is-key-up
  (let [evs [{:type :key-up :text "j"}]]
    (is (input/is-key-up evs "j"))))

(deftest cam-window-coords
  (let [in [40 90]
        to-window #(vec/cam->window % 2000 1600)
        to-cam #(vec/window->cam % 2000 1600)
        result-1 (to-window in)
        result-2 (to-cam result-1)]
    (is (= in result-2) (str "Expected inverse operations..." in result-1 result-2))))

(deftest test-that-im-not-an-idiot
  (let [evs [{:type :key-up :text "ASDKUJHGASDJKHGASD"}]]
    (is (= false (input/is-key-up evs "j")))))

(deftest test-color-check
  (is (= (colors/check [100 100 100]) [100 100 100 255]) "Failed to add a logical default alpha.")
  (is (= (colors/check [100 100 100] 199) [100 100 100 199]) "Didn't properly accept the specified alpha.")
  (is (= (colors/check [5 5 5 5] 25) [5 5 5 5]) "Modified an already valid vector when we provided a default alpha.")
  (is (= (colors/check [101 101 101 99]) [101 101 101 99]) "Modified an already valid vector."))

