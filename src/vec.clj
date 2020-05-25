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

(defn rotation-matrix
  "Rotates a point counter clockwise in radians, when you matrix multiply a point by this."
  [theta]
  ([[(Math/cos theta) (- (Math/sin theta))]
    [(Math/sin theta) (Math/cos theta)]]))

(defn rotation-matrix-clockwise [theta]
  ([[(Math/cos theta) (Math/sin theta)]
    [(- (Math/sin theta)) (Math/cos theta)]]))

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

(defn relative-direction [p1-direction p2-direction]
  ; fixes something ? don't know why
  ; (+ p2-direction 90 (- p1-direction)))
  (- p2-direction p1-direction))

(defn relative-pos [p1 p1-dir p2]
  "Get p2 in a coordinate system with p1 as (0,0) and p1-dir as 0 degrees."
  (from-polar-deg
    (* -1 (- p1-dir (angle-between p1 p2) 90))
    (matrix/distance p1 p2)))

(defn relative-pos-dir
  "Returns an updated position and direction of p2 relative to p1. The keys
  in the map returned are suitable for merging into an existing entity. In
  the normal use case, p1 will be the camera, and p2 will be an entity."
  [p1 p1-dir p2 p2-dir]
  {:pos (relative-pos p1 p1-dir p2)
   :dir (relative-direction p1-dir p2-dir)})

(defn ent-in-camera-coords
  [ent camera]
  (merge ent (relative-pos-dir (:pos camera) (:dir camera) (:pos ent) (:dir ent))))




