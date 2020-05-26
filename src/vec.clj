(ns vec
  "2d vectors, a.k.a points."
  (:require [clojure.core.matrix :as matrix]
            [incanter.core :as incanter]))

(defn delta-y
  "The different in y-coordinate between 2 points. A positive number of the second
  point has a larger value."
  [p1 p2]
  (- (p2 1) (p1 1)))

(defn delta-x
  "The different in x-coordinate between 2 points. A positive number of the second
  point has a larger value."
  [p1 p2]
  (- (p2 0) (p1 0)))

(defn from-polar
  "Get a 2d vector from polar coordinates (using radians)."
  [rad dist]
  [(* dist (Math/cos rad))
   (* dist (Math/sin rad))])

(defn from-polar-deg
  "Get a 2d vector from polar coordinates (using degrees)."
  [deg dist]
  (from-polar (Math/toRadians deg) dist))

(defn angle-between
  "The angle between point 1 and point 2 where 0 degrees points to the right
   and the positive direction rotates counter clockwise. If p2 lies directly
   to the right of p1, we expect 0 degrees."
  [p1 p2]
  (assert (and (vector? p1) (= 2 (count p1))) "p1 must be a vector of length 2.")
  (assert (and (vector? p2) (= 2 (count p2))) "p2 must be a vector of length 2.")
  (Math/toDegrees (incanter/atan2 (delta-y p1 p2) (delta-x p1 p2))))

(defn rotate-around
  "Rotate a target point around a center point."
  [center target deg]
  (let [rad (Math/toRadians deg)
        cos (Math/cos rad)
        sin (Math/sin rad)
        dx (delta-x center target)
        dy (delta-y center target)]
    [(+ (center 0) (* cos dx) (- (* sin dy)))
     (+ (center 1) (* sin dx) (+ (* cos dy)))]))

;;;;;;;;;;;;;;

(defn cam->window [point cam-width cam-height]
  "Convert to window coords (top-left is (0,0)) from a scheme where (0,0) is the center."
  (let [x (point 0)
        y (point 1)
        w2 (/ cam-width 2)
        h2 (/ cam-height 2)]
    [(+ x w2)
     (- h2 y)]))

(defn window->cam [point cam-width cam-height]
  (let [x (point 0)
        y (point 1)
        w2 (/ cam-width 2)
        h2 (/ cam-height 2)]
    [(- x w2)
     (- h2 y)]))

(defn ent-pos-in-cam-coords [cam-pos cam-dir ent-pos]
  "Pass in pos/dir in world coords."
  (from-polar-deg
    (* -1 (- cam-dir (angle-between cam-pos ent-pos) 90))
    (matrix/distance cam-pos ent-pos)))

(defn ent-dir-in-cam-coords [cam-dir ent-dir]
  "Pass in dirs in world coords."
  (- 90 (- cam-dir ent-dir)))

(defn ent-from-global-to-cam-coords
  "Updates an entities :pos and :dir to be relative to the provided
  camera's :pos and :dir."
  [ent camera]
  (-> ent
      (assoc :pos (ent-pos-in-cam-coords (:pos camera) (:dir camera) (:pos ent)))
      (assoc :dir (ent-dir-in-cam-coords (:dir camera) (:dir ent)))))

(defn ent-from-global-to-window-coords
  ([ent camera]
   (ent-from-global-to-window-coords ent camera 1200 900))
  ([ent camera window-width window-height]
   (-> ent
       (ent-from-global-to-cam-coords camera)
       (#(assoc % :pos (cam->window (:pos %) window-width window-height))))))