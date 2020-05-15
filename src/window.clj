(ns window
  (:require [seesaw.core :as ss]
            [seesaw.options :as opt]))

(def -*paint-fn*
  "Paint fn that can be mutated to paint something new on the canvas."
  (fn [canvas g2d] (println "placeholder callback.")))

(defn -canvas-paint-fn [canvas g2d]
  "lives on canvas permanently, invokes a mutable function. very not pure."
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

(defmulti draw-shape
          (fn [shape]
            (:type shape)))

(defmethod draw-shape :line [line]
  ())
