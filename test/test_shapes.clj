(ns test-shapes
  (:require shapes
            vec)
  (:use clojure.test))

(deftest test-shape-constructor
  (is (= (shapes/create-shape :test :key-1 "val-1") {:type :test :key-1 "val-1"})))

(deftest test-points->lines
  (let [in [[10 20]
            [21 30]
            [31 40]]
        out (shapes/points->lines in)]
    (is (= (count out) 3) (str "Number of points should be equal to the number of lines" out))
    (is (= (get-in out [0 :p1 0]) 10) (str "The x coordinate of the first lines first point seems invalid." out))
    (is (= (get-in out [2 :p2 1]) 20) (str "The y coordinate of the third lines second point seems invalid." out))))

(deftest test-angle-between
  (is (= (vec/angle-between [0 0] [0 1]) 90.0))
  (is (= (vec/angle-between [0 0] [1 0]) 0.0))
  (is (= (vec/angle-between [0 0] [-1 0]) 180.0))
  (is (= (vec/angle-between [0 0] [0 -1]) -90.0))
  (let [small (vec/angle-between [10 10] [5000 11])]
    (is (< small 0.02) "Not less than 0.02. (Expecting about 0.011482)")
    (is (> small 0) "Not greater than zero. (Expecting about 0.011482)")))