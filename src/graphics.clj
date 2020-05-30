(ns graphics
  "This file lets you construct jframe/jpanel/canvas objects and then
  provides some helper functions that operate on the 3 of these things.
  It does not store any instances of these classes."
  (:require colors
            [seesaw.core :as ss]
            [seesaw.graphics :as gr]
            [seesaw.options :as opt]
            [seesaw.color]))

(def window-height 900)
(def window-width 1200)

(defn create-window-instances [paint-fn]
  "Creates instances of a jframe, jpanel, and j?-canvas?. Probably, you will
  store this globally somewhere so you can pass it into other functions in
  this file.

  You might be able to use seesaw.options/apply-options to modify the canvas,
  panel, or frame after its returned from here.

  The paint-fn you pass in eventually get's invoked when you call repaint!.
  The function accepts a canvas and a graphics2D object. Calling repaint! is
  the only way I know to get the (mysterious) graphics2D object. I don't know
  what it is or where it even lives, but magically, its passed to the canvas
  paint function, and you can't do anything without it. This makes lots of
  challenges in being able to define a function of your game state and the
  graphics 2d object required to draw anything. I won't explain why but some
  ugly tricks have to be put in place to have a function like this."
  (let [canvas (ss/canvas
                 :id "canvas"
                 :background "#EBEBEB"
                 :paint paint-fn)
        panel (ss/border-panel
                :hgap 1
                :vgap 1
                :border 3
                :center canvas)
        frame (ss/frame
                :width window-width
                :height window-height
                :title "Clojure-4"
                :on-close :exit
                :content panel)]
    {:frame  frame
     :panel  panel
     :canvas canvas}))

(defn repaint! [instances]
  (assert (map? instances))
  (ss/repaint! (:canvas instances)))

