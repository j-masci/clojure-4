(ns test-shapes
  (:require shapes
            vec
            test-utils)
  (:use clojure.test))

;(deftest test-all-shapes-to-window-coords
;  (let [ent {:pos [200 200]}
;        ents/ent-and-all-shapes-to-window-coords
;        ]
;    (is 1)))

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

(deftest test-rotate-shape
  (let [in (shapes/create-line [0 0] [0 10])
        expected (shapes/create-line [0 0] [-10 0])
        out (shapes/rotate in [0 0] 90)]
    (is (test-utils/vectors-almost-equal 0.1 (:p1 expected) (:p1 out)))
    (is (test-utils/vectors-almost-equal 0.1 (:p2 expected) (:p2 out)))))

(deftest test-rotate-shape-from-north
  (let [in (shapes/create-line [0 0] [0 (Math/sqrt 2)])
        deg 225
        out (shapes/-rotate-shape-from-north-to-dir in deg)]
    (is (test-utils/vectors-almost-equal 0.01 [0 0] (:p1 out)) (str "The first point should remain at the origin."))
    (is (test-utils/vectors-almost-equal 0.01 [-1 -1] (:p2 out))) (str "The second point should be roughly [-1 -1]")))

(deftest test-rotate-shape-from-north-2
  (let [in (shapes/create-line [0 0] [0 10])
        deg 0.01
        out (shapes/-rotate-shape-from-north-to-dir in deg)
        second-point-y-coord ((:p2 out) 1)]
    (is (< 0 second-point-y-coord) (str "The second points y coordinate should be just above zero."))
    (is (> 1 second-point-y-coord) (str "The second points y coordinate should be just above zero (its too large)."))))

(deftest test-shape-transform
  (let [line (shapes/create-line [0 0] [0 10])
        offset [50 50]
        expected-p1 [50 50]
        expected-p2 [50 60]
        actual (shapes/transform line offset)]
    (is (= expected-p1 (mapv int (:p1 actual))))
    (is (= expected-p2 (mapv int (:p2 actual))))
    ))