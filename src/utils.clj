(ns utils
  (:require [seesaw.graphics :as gr]
            [seesaw.color :as color]))

; style object which is 4th param of gr/draw
(defn gr.style
  ([[r g b a]] (gr/style [r g b a] 1))
  ([[r g b a] width] (let [c (color/color r g b (if (= a nil) 255 a))]
                       (gr/style
                         :foreground c
                         :background c
                         :stroke (gr/stroke :width (if width width 1))))))

(defn nano-seconds-per-frame [ups]
  (if (> ups 0) (int (* 1000000000 (float (/ 1 ups)))) 0))