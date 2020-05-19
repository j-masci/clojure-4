(ns shapes
  (:require [seesaw.graphics :as gr]
            [clojure.core.matrix :as matrix]
            [incanter.core :as incanter]
            colors)
  (:use globals))

; --- SHAPES ---

(defn shape [type & keyvals]
  "A shape is a map with a :type, and other data depending on the type."
  (merge {:type type} (apply hash-map keyvals)))

(defn line [p1 p2]
  (shape :line
         :p1 p1
         :p2 p2))

(defn circle [center radius]
  (shape :circle
         :center center
         :radius radius))

(defn text
  ([_text pos] (text _text pos [0 0 0]))
  ([_text pos color] (shape :text
                            :text _text
                            :pos pos
                            :color color)))

(defn points->lines [points]
  "ie. [[x1 y2] [x2 y2] [x3 y3]] -> lines connecting all the points."
  (mapv line points (rest (conj points (first points)))))

;maybe later
;(defn rect->shapes [points])

; --- DRAWING ---

(defmulti draw! (fn [type shape g2d] type))

(defmethod draw! :line [_ shape g2d]
  (let [x1 (get-in shape [:p1 0])
        y1 (get-in shape [:p1 1])
        x2 (get-in shape [:p2 0])
        y2 (get-in shape [:p2 1])
        color (apply seesaw.color/color (colors/check (:color shape)))
        width (:width shape 1)
        line (gr/line x1 y1 x2 y2)
        ; todo: both foreground/background?
        style (gr/style :foreground color
                        :background color
                        :stroke (gr/stroke :width width))]
    (gr/draw g2d line style)))

(defmethod draw! :circle [_ shape g2d]
  (let [x (get-in shape [:center 0])
        y (get-in shape [:center 1])
        radius (:radius shape)
        color (apply seesaw.color/color (colors/check (:color shape)))
        width (:width shape 1)
        circle (gr/circle x y radius)
        style (gr/style :foreground color
                        :background (seesaw.color/color 0 0 0 0)
                        :stroke (gr/stroke :width width))]
    (gr/draw g2d circle style)))

(defmethod draw! :text [_ shape g2d]
  (.drawString g2d (:text shape) (get-in shape [:pos 0]) (get-in shape [:pos 1])))

; --- TRANSFORMATIONS ---

(defn angle-between [p1 p2]
  "The angle between point 1 and point 2 where 0 degrees points to the right
   and the positive direction rotates counter clockwise. If p2 lies directly
   to the right of p1, we expect 0 degrees."
  (assert (and (vector? p1) (= 2 (count p1))) "p1 must be a vector of length 2.")
  (assert (and (vector? p2) (= 2 (count p2))) "p2 must be a vector of length 2.")
  ; ie. (atan2 delta-y delta-x)
  (Math/toDegrees (apply incanter/atan2 (reverse (matrix/sub p2 p1)))))

(defn relative-position [p1 rotation-1 p2 rotation-2]
  ())

(defn polar->point [rad dist]
  [(* dist (Math/cos rad))
   (* dist (Math/sin rad))])

(defn polar->point|deg [deg dist] (polar->point (Math/toRadians deg) dist))

(defn cam-relative-to-screen [point cam-width cam-height]
  "Ie. to screen coordinates with 0,0 at top left."
  [(+ (/ cam-width 2) (point 0))
   (- (/ cam-height 2) (point 1))])

; WORKS!
(defn cam-ent-relative-position [cam-position cam-deg ent-position]
  (let [between-deg (angle-between cam-position ent-position)
        abs-dist (matrix/distance cam-position ent-position)
        relative-deg (- (- cam-deg between-deg 90))]
    (polar->point|deg relative-deg abs-dist)))

(defn delta-y [p1 p2] (- (p2 1) (p1 1)))

(defn delta-x [p1 p2] (- (p2 0) (p1 0)))

(defn rotate-point [origin point deg]
  (let [rad (Math/toRadians deg)
        cos (Math/cos rad)
        sin (Math/sin rad)
        dx (delta-x origin point)
        dy (delta-y origin point)]
    [(+ (origin 0) (* cos dx) (- (* sin dy)))
     (+ (origin 1) (* sin dx) (+ (* cos dy)))]))

;# rotate a point around another point
;def rotate_point(origin, point, deg):
;rad = to_rad(deg)
;
;ox, oy = origin
;px, py = point
;
;qx = ox + math.cos(rad) * (px - ox) - math.sin(rad) * (py - oy)
;qy = oy + math.sin(rad) * (px - ox) + math.cos(rad) * (py - oy)
;return qx, qy

(defmulti rotate (fn [type shape deg] type))

(defmethod rotate :circle [_ shape deg]
  (update shape :center #(rotate-point [0 0] % deg)))

(defmethod rotate :line [_ shape deg]
  (-> shape
      (update :p1 #(rotate-point [0 0] % deg))
      (update :p2 #(rotate-point [0 0] % deg))))

(defmulti transform (fn [type shape offset-v] type))

(defmethod transform :line [_ shape offset-v]
  (-> shape
      (update :p1 #(matrix/add % offset-v))
      (update :p2 #(matrix/add % offset-v))))

(defmethod transform :circle [_ shape offset-v]
  (update shape :center #(matrix/add % offset-v)))

(defmulti scale (fn [type shape amt] type))

; todo
(defmethod scale :line [_ shape amt]
  (-> shape))

; todo
(defmethod scale :circle [_ shape amt]
  (-> shape))

