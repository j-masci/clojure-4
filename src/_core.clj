(ns _core
  (:require
    [clojure.core.matrix :as m]
    [seesaw.core :as ss]
    [seesaw.options :as ss.options]
    [seesaw.graphics :as gr]
    [seesaw.color :as color]
    utils
    ents
    ;[jdk.awt.core]
    ;[jdk.awt.Frame :as frame]
    ;[jdk.awt.Color :as color]
    ))

; updates/frames per second
(def ups 2)

(def *paused* ^:dynamic false)

; an ugly but necessary global to get the state into the canvas callback function
(def *state-to-paint* ^:dynamic {})

(def state-0
  "initial state"
  {:step       0
   :delta-time 0
   :config     {}
   :ents       [ents/player]})

(defn step [state]
  "The update function. Returns the next state via the current state."
  (-> state
      (update :step inc)
      (assoc :ents (mapv (fn [ent] (ents/integrate ent)) (:ents state)))))

(defn --paint [state canvas g2d]
  (doto g2d
    (gr/draw (gr/line 0 0 200 400) (utils/graphics.style [100 23 250 255] 3))
    (.drawString (str "Step: " (:step state)) 200 200)))

; the callback that lives on the canvas object, it can't accept state
; as a parameter so it uses a global instead.
(defn --paint-callback [c g]
  (--paint *state-to-paint* c g))

; call this one in the loop
(defn --trigger-repaint [state canvas]
  (def *state-to-paint* state)
  (ss/repaint! canvas))

(def canvas (ss/canvas :id "canvas" :background "#EBEBEB" :paint --paint-callback))
(def panel (ss/border-panel :hgap 1 :vgap 1 :border 3 :center canvas))
(def frame (ss/frame
             :width 1200
             :height 900
             :title "Clojure-4"
             :on-close :exit
             :content panel))

(defn loop-game [state]
  "ie. start game, run"
  (let [t1 (System/nanoTime)
        next-state (if *paused* state (step state))
        _ (--trigger-repaint next-state canvas)
        t2 (System/nanoTime)
        diff (- t2 t1)
        sleep-for-ns (- (utils/nano-seconds-per-frame ups) diff)]
    (do
      (when (> sleep-for-ns 0) (Thread/sleep (long (/ sleep-for-ns 1000000))))
      (recur next-state))))

(defn open-window [] (ss/invoke-later (ss/show! frame)))

(defn close-window [] (ss/invoke-later (ss/hide! frame)))

(defn -main [& args] (do (open-window) (loop-game state-0)))
