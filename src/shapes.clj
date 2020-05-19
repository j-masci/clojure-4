(ns shapes
  (:require [seesaw.graphics :as gr]))

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
;(defn rect->shapes [points]
;  ())

(defmulti ent->shapes (fn [type ent] type))

(defmethod ent->shapes :player [_ ent]
  (into []
        (concat [(-> (circle [0 0] 10)
                     (assoc :color :blue))]
                (points->lines [[-10 -10]
                                [-10 10]
                                [10 10]
                                [10 -10]]))))

(defmethod ent->shapes :default [_ ent]
  [(text "??" (get ent :pos [100 100]))])

; --- TRANSFORMATIONS ---

(defmulti rotate (fn [type shape deg] type))

(defmethod rotate :circle [_ shape deg] shape)

(defmethod rotate :line [_ shape deg] (-> shape))

(defmulti transform (fn [type shape offset-v] type))

(defmethod transform :line [_ shape offset-v]
  (-> shape))

(defmethod transform :circle [_ shape offset-v]
  (-> shape))

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

