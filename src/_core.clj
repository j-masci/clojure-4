(ns _core
  (:require
    [awt_input]
    [clojure.core.matrix :as m]
    [seesaw.core :as ss]
    [seesaw.options :as ss.options]
    [seesaw.graphics :as gr]
    [seesaw.color :as color]
    [clojure.pprint]
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

(def map-1 "
00000000000000000000111111000000000000000000000000000000000000000000000000
00000000000000000000011111100111000000000000000000000000000000000000000000
00000000000111110000000001110000000000000000000000000000000110000000000000
00000000000000000000000000000000000000000000000000000000000000000000000000
00000000000000000000000000000000000000011100000000000000000000000000000000
00000000000000001000000000000000000000000000000000000000000000000000000000
00000000000000000011000000000000000000000000000000000000000000000000000000
00000000000000000000110000000000110000000000000000000000000000011000000000
00000000000000000000000000000000000000000000000000000000000000000000000000
00000000000000000000000000000000000000011100000000000000000000000000000000
00000000000000000000000000000000000000011100000000000000000000000000000000
00000000000000000000000000000000000000011100000000000000000000000000000000
00000000000000000000000000000000000000000000000000000000000000000000000000
")

(def state-0
  "initial state"
  {:step       0
   :delta-time 0
   :map        map-1
   :input      []
   :config     {}
   :camera     {:pos [0 0]
                :dir 0.0
                :zoom 1.0}
   :ents       [ents/player]})

(defn step [state]
  "The update function. Returns the next state via the current state."
  (clojure.pprint/pprint (:input state))
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

(def -input-queue
  "vector of input events to process on next update"
  (atom []))

(defn -queue-input [e] (swap! -input-queue conj e))

(defn get-input-queue
  "get queued inputs as a vector, and optionally reset the queue (side effect)"
  ([] (get-input-queue false))
  ([reset]
   (let [queue @-input-queue]
     (when reset (reset! -input-queue []))
     queue)))

(defn map-inputs [evs]
  (map awt_input/map-input evs))

(defn loop-game [state]
  "ie. start game, run"
  (let [t1 (System/nanoTime)
        ; not perfect input logic in respect to game paused for now
        input (map-inputs (get-input-queue true))
        next-state (if *paused* state (step (assoc state :input input)))
        _ (--repaint next-state)
        t2 (System/nanoTime)
        diff (- t2 t1)
        sleep-for-ns (- (utils/nano-seconds-per-frame ups) diff)]
    (do
      (when (> sleep-for-ns 0) (Thread/sleep (long (/ sleep-for-ns 1000000))))
      (recur next-state))))

(defn open-window []
  (ss/invoke-later (ss/show! frame)))

(defn close-window []
  (ss/invoke-later (ss/hide! frame)))

(ss/listen canvas
           :mouse-entered -queue-input)

; keys, focus, and mouse wheel, but not mouse entered
(ss/listen frame
           :focus-gained -queue-input
           :mouse-wheel-moved -queue-input
           :key -queue-input
           ;:key-pressed (fn [e] (println "key pressed") (println e))
           ;:key-typed (fn [e] (println "key typed"))
           ;:key-released (fn [e] (println "key released"))
           )

(defn -main [& args] (do (open-window) (loop-game state-0)))
