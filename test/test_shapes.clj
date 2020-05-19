(ns test-shapes
  (:require shapes)
  (:use clojure.test))

(deftest test-shape-constructor
  (is (= (shapes/shape :test :key-1 "val-1") {:type :test :key-1 "val-1"})))

(deftest test-points->lines
  (let [in [[10 20]
            [21 30]
            [31 40]]
        out (shapes/points->lines in)]
    (is (= (count out) 3) (str "Number of points should be equal to the number of lines" out))
    (is (= (get-in out [0 :p1 0]) 10) (str "The x coordinate of the first lines first point seems invalid." out))
    (is (= (get-in out [2 :p2 1]) 20) (str "The y coordinate of the third lines second point seems invalid." out))))

