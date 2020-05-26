(ns test-vec
  (:require shapes
            vec
            test-utils)
  (:use clojure.test))

(deftest test-cam-to-window-coords
  (let [width 1200
        height 900]
    (is (test-utils/vectors-almost-equal 0.1 [600 450] (vec/cam->window [0 0] width height)))
    (is (test-utils/vectors-almost-equal 0.1 [600 0] (vec/cam->window [0 450] width height)))
    (is (test-utils/vectors-almost-equal 0.1 [300 750] (vec/cam->window [-300 -300] width height)))))

(deftest test-cam-to-window-and-inverse-op
  (let [in [40 90]
        to-window #(vec/cam->window % 2000 1600)
        to-cam #(vec/window->cam % 2000 1600)
        result-1 (to-window in)
        result-2 (to-cam result-1)]
    (is (= in result-2) (str "Expected inverse operations..." in result-1 result-2))))

(deftest test-ent-cam-dir
  (is (test-utils/numbers-almost-equal 0.1 90 (vec/ent-dir-in-cam-coords 0 0))  "If the camera and entity are pointing the same direction, the result should be 90.")
  (is (test-utils/numbers-almost-equal 0.1 180 (vec/ent-dir-in-cam-coords 0 90))  "Entity should be pointing east (180 deg) relative to camera.")
  )

(deftest test-ent-cam-pos
  (let [case0 (vec/ent-pos-in-cam-coords [0 0] 45 [50 50])
        case1 (vec/ent-pos-in-cam-coords [0 0] 90 [100 0])
        case2 (vec/ent-pos-in-cam-coords [-200 -200] -90 [-200 -100])]
    (is (test-utils/numbers-almost-equal 0.1 (case0 0) 0))
    (is (test-utils/vectors-almost-equal 0.1 [100 -0] case1))
    (is (test-utils/vectors-almost-equal 0.1 [0 -100] case2))))

(deftest test-ent-window-coords
  (let [ent {:pos [300 0] :dir 0}
        cam {:pos [0 0] :dir 0}
        width 1200
        height 900
        expected {:pos [600 150] :dir 90}
        actual (vec/ent-from-global-to-window-coords ent cam width height)]
    (is (test-utils/numbers-almost-equal 0.1 (:dir expected) (:dir actual)))
    (is (test-utils/vectors-almost-equal 0.1 (:pos expected) (:pos actual)))))

