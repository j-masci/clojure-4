(ns _core
  (:require
    [seesaw.core :as ss]
    [seesaw.graphics :as gr]
    [seesaw.color :as color]
    ;[jdk.awt.core]
    ;[jdk.awt.Frame :as frame]
    ;[jdk.awt.Color :as color]
    ))

(def color color/color)

; style object which is 4th param of gr/draw
(defn style
  ([[r g b a]] (style [r g b a] 1))
  ([[r g b a] width] (let [c (color r g b (if (= a nil) 255 a))] (gr/style
                                                                   :foreground c
                                                                   :background c
                                                                   :stroke (gr/stroke :width (if width width 1))))))
;(get-in)
(def state {
            :entities []
            })

(def player {
             :pos [400 400]
             :vel [0 0]
             :acl [0 0]
             :draw (fn [p] ())
             })



; entities......
; game state... reducer ?
; entity state / reducer ?
; do we need pure...
; paint function does what.. loops through entities?
; do entities have draw functions or are they just data that we'll know how
; to draw (and could have a draw function if needed...).. it seems very inconvenient
; to not have a draw function on the entity itself... where else does the code go that
; knows how to draw the entity

(defn paint [canvas g2d]
  (do
    (gr/draw g2d (gr/line 0 0 200 400) (style [100 23 250 255] 3))
    (.drawString g2d "fuck you" 500 100)))

(def canvas (ss/canvas :id "go-fuck-yourself" :background "#EBEBEB" :paint paint))

; a JPanel object
(def panel (ss/border-panel :hgap 1 :vgap 1 :border 3 :center canvas))

(def frame (ss/frame
             :width 1200
             :height 900
             :title "Clojure-4"
             :on-close :exit
             :content panel))

(defn -main [& args]
  (println "[main 1]")
  (ss/invoke-later
    (-> frame ss/show!))
  (println "[main 2]"))

(defn reload
  []
  (use '_core :reload)
  (use 'scratch :reload))

