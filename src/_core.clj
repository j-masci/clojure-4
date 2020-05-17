(ns _core
  (:require
    [input]
    [clojure.core.matrix :as m]
    [seesaw.core :as ss]
    [seesaw.options :as ss.options]
    [seesaw.graphics :as gr]
    [seesaw.color :as color]
    [clojure.data.json :as json]
    [clojure.pprint]
    utils
    ents
    window
    ;[jdk.awt.core]
    ;[jdk.awt.Frame :as frame]
    ;[jdk.awt.Color :as color]
    )
  (:use globals))

; updates/frames per second
(def ups 30)

(def *paused* false)

(def map-1 "
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
   :input      []
   :config     {}
   :camera     {:pos  [0 0]
                :dir  0.0
                :zoom 1.0}
   :ents       [ents/player]})

(def -all-states (atom []))

(defn dump-state! []
  (let [timestamp (int (/ (System/currentTimeMillis) 1000))
        content (with-out-str (json/pprint @-all-states))
        _ (println "--DUMPING STATE--" (count content))]
    (spit (str "debug/states-" timestamp ".txt") content)))

(defn global-input-handler [state]
  (when (input/is-key-up (:input state) "f2") (dump-state!))
  state)

(defn step [state]
  "The update function. Returns the next state via the current state."
  (-> state
      (global-input-handler)
      (update :step inc)
      (assoc :ents (mapv (fn [ent] (ents/integrate ent)) (:ents state)))))

(defn -paint [state canvas g2d]
  (doto g2d
    ((fn [g] (doseq [ent (:ents state)] (ents/draw ent g))))
    (.drawString (str "Step: " (:step state)) 200 200)))

(defn -paint-via-state [state]
  "paint state to the screen"
  (window/paint-given-fn! (fn [canvas g2d]
                            (-paint state canvas g2d))))

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
  (map input/map-input evs))

(ss/listen window/canvas
           :mouse-entered -queue-input)

; keys, focus, and mouse wheel, but not mouse entered
(ss/listen window/frame
           :focus-gained -queue-input
           :mouse-wheel-moved -queue-input
           :key -queue-input
           ;:key-pressed (fn [e] (println "key pressed") (println e))
           ;:key-typed (fn [e] (println "key typed"))
           ;:key-released (fn [e] (println "key released"))
           )

(def *input-debug* true)

(defn loop-game [state]
  "ie. start game, run"
  (let [t1 (System/nanoTime)
        ; not perfect input logic in respect to game paused for now
        input (map-inputs (get-input-queue true))
        next-state (if *paused* state (step (assoc state :input input)))
        _ (-paint-via-state next-state)
        t2 (System/nanoTime)
        diff (- t2 t1)
        sleep-for-ns (- (utils/nano-seconds-per-frame ups) diff)]
    (do
      (when *input-debug* (when (seq input) (println "--INPUT--") (clojure.pprint/pprint input)))
      (if-not *paused* (swap! -all-states conj next-state))
      (when (> sleep-for-ns 0) (Thread/sleep (long (/ sleep-for-ns 1000000))))
      (recur next-state))))

(defn open-window []
  (ss/invoke-later (ss/show! window/frame)))

(defn close-window []
  (ss/invoke-later (ss/hide! window/frame)))

(defn game! [] (loop-game state-0))

(defn -main [& args] (do (open-window) (loop-game state-0)))
