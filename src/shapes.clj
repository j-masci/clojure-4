(ns shapes
  "Shapes are maps containing only data. Entities which are drawn will have a vector
  of shapes, which describes how to draw the entity centered on the origin with north being
  the forward facing direction. In order to draw the shapes on the screen, relative to the camera,
  we'll apply a number of transformations (transform/rotate/scale) to every shape to convert
  it to window coordinates and then draw it."
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

(defmulti transform (fn [shape offset-v] (:type shape)))

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
          (fn [shape deg] (:type shape)))

(defmethod rotate :circle [shape deg]
  (update shape :center
          #(vec/rotate-around [0 0] % deg)))

(defmethod rotate :line [shape deg]
  (-> shape
      (update :p1 #(vec/rotate-around [0 0] % deg))
      (update :p2 #(vec/rotate-around [0 0] % deg))))

(defmethod rotate :text [shape deg]
  (-> shape))

; -- SCALE --
; not done

(defmulti scale (fn [shape amt] (:type shape)))

(defmethod scale :line [shape amt]
  (-> shape))

(defmethod scale :circle [shape amt]
  (-> shape))

; -- DRAW --

(defmulti draw!
          "Draw a shape in window coordinates (top left = (0,0))"
          (fn [shape g2d] (:type shape)))

(defmethod draw! :line [shape g2d]
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

(defmethod draw! :circle [shape g2d]
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

(defmethod draw! :text [shape g2d]
  (.drawString g2d (:text shape) (get-in shape [:pos 0]) (get-in shape [:pos 1])))

; --- TRANSFORMATIONS ---

(defn -align-ent-pos
  [shape ent]
  (transform shape (:pos ent)))

(defn -align-ent-dir
  "Align a shape to its entity (rotate it).
  Subtract 90 because shapes are drawn with north (90 deg) as forward direction."
  [shape ent]
  (rotate shape (+ (:dir ent) -90)))

(defn ent-align-shapes
  "Align all shapes to the :pos and :dir of ent. This needs to be done after
  the entity is changed from world to cam/window coords. Returns a vector
  of shapes, not the entity. Requires :pos and :dir from ent."
  [shapes ent]
  (mapv (fn [shape] (-> shape
                        (-align-ent-dir ent)
                        (-align-ent-pos ent))) shapes))

