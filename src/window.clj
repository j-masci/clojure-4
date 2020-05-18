(ns window
  (:require [seesaw.core :as ss]
            [seesaw.graphics :as gr]
            [seesaw.options :as opt]
            [seesaw.color]
            colors))

(def -*paint-fn*
  "Paint fn that can be mutated to paint something new on the canvas."
  (fn [canvas g2d] (println "placeholder callback.")))

(def *hack* nil)

(defn -canvas-paint-fn [canvas g2d]
  "lives on canvas permanently, invokes a mutable function. very not pure."
  (def *hack* g2d)
  (-*paint-fn* canvas g2d))

(def canvas (ss/canvas :id "canvas" :background "#EBEBEB" :paint -canvas-paint-fn))

(def panel (ss/border-panel :hgap 1 :vgap 1 :border 3 :center canvas))

(def frame (ss/frame
             :width 1200
             :height 900
             :title "Clojure-4"
             :on-close :exit
             :content panel))

(defn set-paint-fn! [f]
  "pass a function f of [canvas g2d]"
  (def -*paint-fn* f))

(defn repaint! []
  (ss/repaint! canvas))

(defn paint-given-fn! [f]
  (do (set-paint-fn! f) repaint!))

(defn draw-line [shape g2d]
  (let [x1 (get-in shape [:p1 0])
        y1 (get-in shape [:p1 1])
        x2 (get-in shape [:p2 0])
        y2 (get-in shape [:p2 1])
        color (apply seesaw.color/color (colors/check (get shape :color [0 0 0 255])))
        width (:width shape 1)
        line (gr/line x1 y1 x2 y2)
        ; todo: both foreground/background?
        style (gr/style :foreground color
                        :background color
                        :stroke (gr/stroke :width width))]
    (gr/draw g2d line style)))

(defn draw-circle [shape g2d]
  (let [x (get-in shape [:center 0])
        y (get-in shape [:center 1])
        radius (:radius shape)
        color (apply seesaw.color/color (colors/check (get shape :color [0 0 0 255])))
        width (:width shape 1)
        circle (gr/circle x y radius)
        style (gr/style :foreground color
                        :background (seesaw.color/color 0 0 0 0)
                        :stroke (gr/stroke :width width))]
    (gr/draw g2d circle style)))

(defmulti draw-shape
          (fn [shape g2d]
            (:type shape)))

(defmethod draw-shape :line [shape g2d]
  (draw-line shape g2d))

(defmethod draw-shape :circle [shape g2d]
  (draw-circle shape g2d))
