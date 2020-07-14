(ns shapes
  "Shapes are maps containing only data. Entities which are drawn will have a vector
  of shapes, which describes how to draw the entity in \"local coordinates\".
  A complex process takes shapes drawn in local coords and an entities position
  and direction to convert into camera and then window coords."
  (:require [seesaw.graphics :as gr]
            [clojure.core.matrix :as matrix]
            [incanter.core :as incanter]
            colors
            vec)
  (:use globals))

; -- CONSTRUCT --

(defn create-shape [type & keyvals]
  "Build a generic shape which is just a map. This function is probably pointless.
  Use another function named \"create-\". Only shapes with known types are supported.
  Multi methods will fail if you call them on unknown shape types (most likely)."
  (merge {:type type} (apply hash-map keyvals)))

(defn create-line [p1 p2]
  {:type :line
   :p1 p1
   :p2 p2})

(defn create-circle [center radius]
  (assert (vector? center))
  (assert (number? radius))
  {:type :circle
   :center center
   :radius radius})

(defn create-text
  ([text pos]
   (create-text text pos [0 0 0]))
  ([text pos color]
   {:type :text
    :text text
    :pos pos
    :color color}))

; -- MISC --

(defn points->lines [points]
  "ie. [[x1 y2] [x2 y2] [x3 y3]] -> lines connecting all the points."
  (mapv create-line points (rest (conj points (first points)))))

; -- TRANSFORM --

(defmulti flip-y "Negate y-coordinates"
          (fn [shape] (:type shape)))

(defmethod flip-y :line [shape]
  (-> shape
      (update :p1 #(update % 1 -))
      (update :p2 #(update % 1 -))))

(defmethod flip-y :circle [shape]
  (-> shape
      (update :center #(update % 1 -))))

(defmethod flip-y :text [shape]
  (-> shape
      (update :pos #(update % 1 -))))

; -- TRANSFORM --

(defmulti transform "Move a shape"
          (fn [shape offset-v] (:type shape)))

(defmethod transform :line [shape offset-v]
  (-> shape
      (update :p1 #(matrix/add % offset-v))
      (update :p2 #(matrix/add % offset-v))))

(defmethod transform :circle [shape offset-v]
  (update shape :center #(matrix/add % offset-v)))

(defmethod transform :text [shape offset-v]
  (update shape :pos #(matrix/add % offset-v)))

; -- ROTATE --

(defmulti rotate "Rotate the entity relative to its center."
          (fn [shape around deg] (:type shape)))

(defmethod rotate :circle [shape around deg]
  (assert (vector? around))
  (update shape :center
          #(vec/rotate-around around % deg)))

(defmethod rotate :line [shape around deg]
  (-> shape
      (update :p1 #(vec/rotate-around around % deg))
      (update :p2 #(vec/rotate-around around % deg))))

(defmethod rotate :text [shape around deg]
  (-> shape))

; -- SCALE --
; not done

(defmulti scale
          (fn [shape amt] (:type shape)))

(defmethod scale :line [shape amt]
  (-> shape))

(defmethod scale :circle [shape amt]
  (update shape :radius #(* % amt)))

; -- DRAW --

(defmulti draw!
          "Draw a shape in window coordinates (top left = (0,0))"
          (fn [shape g2d] (:type shape)))

(defmethod draw! :line [shape g2d]
  "Draws a line."
  (let [line (gr/line ((:p1 shape) 0) ((:p1 shape) 1) ((:p2 shape) 0) ((:p2 shape) 1))
        color (apply seesaw.color/color (colors/check (:color shape)))
        style (gr/style :foreground color
                        :background color
                        :stroke (gr/stroke :width (:width shape 1)))]
    (gr/draw g2d line style)))

(defmethod draw! :circle [shape g2d]
  "Draws a circle."
  (let [circle (gr/circle ((:center shape) 0) ((:center shape) 1) (:radius shape))
        color (apply seesaw.color/color (colors/check (:color shape)))
        style (gr/style :foreground color
                        :background (seesaw.color/color 0 0 0 0)
                        :stroke (gr/stroke :width (:width shape 1)))]
    (gr/draw g2d circle style)))

(defmethod draw! :text [shape g2d]
  "Draws text."
  (.drawString g2d (:text shape) (get-in shape [:pos 0]) (get-in shape [:pos 1])))

; --- TRANSFORMATIONS ---

(defn -rotate-shape-from-north-to-dir
  "Rotate a shape to point in a given direction. 90 is subtracted first
  since the shape is drawn facing north."
  [shape dir]
  (rotate shape [0 0] (+ -90 dir)))

(defn -align-shape-to-ent [shape ent]
  (-> shape
      (-rotate-shape-from-north-to-dir (:dir ent))
      (flip-y)
      (transform (:pos ent))))

(defn ent-align-shapes
  "Align all shapes to the :pos and :dir of ent. This needs to be done after
  the entity is changed from world to cam/window coords. Returns a vector
  of shapes, not the entity. Requires :pos and :dir from ent."
  [shapes ent]
  (mapv (fn [shape] (-> shape
                        (-rotate-shape-from-north-to-dir (:dir ent))
                        (transform (:pos ent)))) shapes))

