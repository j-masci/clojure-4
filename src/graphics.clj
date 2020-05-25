(ns graphics
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

(def window-height 900)
(def window-width 1200)

(def frame (ss/frame
             :width window-width
             :height window-height
             :title "Clojure-4"
             :on-close :exit
             :content panel))

(defn repaint! []
  (ss/repaint! canvas))

(defn paint-given-fn! [f]
  (do (def -*paint-fn* f) (repaint!)))

