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
    )
  (:use globals))

; updates/frames per second
(def ups 30)

(def *paused* false)

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
    ((fn [g] (doseq [ent (:ents state)] (ents/draw ent g))))
    (gr/draw (gr/line 0 0 200 400) (utils/gr.style [100 23 250 255] 3))
    (.drawString (str "Step: " (:step state)) 200 200)))

; the callback that lives on the canvas object, it can't accept state
; as a parameter so it uses a global instead.
(defn --paint-callback [c g]
  (--paint *state-to-paint* c g))

(def canvas (ss/canvas :id "canvas" :background "#EBEBEB" :paint --paint-callback))
(def panel (ss/border-panel :hgap 1 :vgap 1 :border 3 :center canvas))
(def frame (ss/frame
             :width 1200
             :height 900
             :title "Clojure-4"
             :on-close :exit
             :content panel))

; call this one in the loop
(defn --repaint [state]
  (def *state-to-paint* state)
  (ss/repaint! canvas))

(defn loop-game [state]
  "ie. start game, run"
  (let [t1 (System/nanoTime)
        next-state (if *paused* state (step state))
        _ (--repaint next-state)
        t2 (System/nanoTime)
        diff (- t2 t1)
        sleep-for-ns (- (utils/nano-seconds-per-frame ups) diff)]
    (do
      (when (> sleep-for-ns 0) (Thread/sleep (long (/ sleep-for-ns 1000000))))
      (recur next-state))))

(defn open-window [] (ss/invoke-later (ss/show! frame)))

(defn close-window [] (ss/invoke-later (ss/hide! frame)))

(def -event-queue (atom []))

(defn queue-event [e] (swap! -event-queue conj e))

(defn get-queued-events
  "get all queued events and then reset the queue"
  ([] (get-queued-events false))
  ([reset]
   (let [queue @-event-queue]
     (when reset (reset! -event-queue []))
     queue)))

(ss/listen canvas
           :mouse-entered queue-event)

; keys, focus, and mouse wheel, but not mouse entered
(ss/listen frame
           :focus-gained queue-event
           :mouse-wheel-moved queue-event
           :key queue-event
           ;:key-pressed (fn [e] (println "key pressed") (println e))
           ;:key-typed (fn [e] (println "key typed"))
           ;:key-released (fn [e] (println "key released"))
           )

(defn -main [& args] (do (open-window) (loop-game state-0)))
