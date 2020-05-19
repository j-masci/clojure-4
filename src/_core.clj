(ns _core
  (:require
    graphics
    utils
    ents
    input
    shapes
    [seesaw.core :as ss]
    [clojure.data.json :as json]
    [clojure.pprint]
    ; [clojure.core.matrix :as m]
    ; [seesaw.graphics :as gr]
    )
  (:use globals))

(def *updates-per-second* 20)

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

(defn get-player-nth [state] 0)

(def -all-states (atom []))

(defn dump-state! []
  (let [timestamp (int (/ (System/currentTimeMillis) 1000))
        content (with-out-str (json/pprint @-all-states))
        _ (println "--DUMPING STATE--" (count content))]
    (spit (str "debug/states-" timestamp ".txt") content)))

(defn -check-global-controls [state]
  (when (input/is-key-up (:input state) "f1") (println "Ok what?"))
  (when (input/is-key-up (:input state) "f2") (dump-state!))
  (if (input/is-key-up (:input state) "f5") state-0 state))

(def listener-eg {:type :key-up :text "w" :callback (fn [state] (-> state))})

(defn -check-player-controls [state]
  (let [typed #(input/is-key-typed (:input state) %)
        player-nth (get-player-nth state)]
    (cond-> state
            (input/is-key-down (:input state) "w") (update-in [:ents player-nth :vel 1] #(- % 0.2))
            (input/is-key-down (:input state) "a") (update-in [:ents player-nth :vel 0] #(- % 0.2))
            (input/is-key-down (:input state) "s") (update-in [:ents player-nth :vel 1] #(+ % 0.2))
            (input/is-key-down (:input state) "d") (update-in [:ents player-nth :vel 0] #(+ % 0.2)))))

(defn -compute-ent-shapes [state]
  "Set shapes vec on each entity which will get passed to a drawing function."
  (update state :ents
          (fn [ents]
            (mapv
              (fn [ent] (assoc ent :shapes (shapes/ent->shapes (:type ent) ent))) ents))))

(defn -iterate [state]
  "The update function. Returns the next state via the current state."
  (-> state
      (update :step inc)
      (-check-global-controls)
      (-check-player-controls)
      (-compute-ent-shapes)
      (update :ents #(mapv ents/integrate %))))

(defn -paint [state canvas g2d]
  (shapes/draw! :circle (shapes/circle (get-in state [:ents 0 :pos]) 20) g2d)
  (doseq [ent (:ents state)] (doseq [shape (:shapes ent)] (shapes/draw! (:type shape) shape g2d)))
  (.drawString g2d (str "Test" (:step state)) 500 500))

(defn -paint-via-state [state]
  "paint state to the screen"
  (graphics/paint-given-fn! (fn [canvas g2d]
                              (-paint state canvas g2d))))

(def -input-queue
  "vector of input events to process on next update"
  (atom []))

(defn get-input-queue
  "get queued inputs as a vector, and optionally reset the queue (side effect)"
  ([] (get-input-queue false))
  ([reset]
   (let [queue @-input-queue]
     (when reset (reset! -input-queue []))
     queue)))

(defn -queue-input [e] (swap! -input-queue conj e))

(ss/listen graphics/canvas
           :mouse-entered -queue-input)

; keys, focus, and mouse wheel, but not mouse entered
(ss/listen graphics/frame
           :focus-gained -queue-input
           :mouse-wheel-moved -queue-input
           :key -queue-input)

(defn loop-game [state]
  "ie. start game, run"
  (let [t1 (System/nanoTime)
        ; not perfect input logic in respect to game paused for now
        input (mapv input/map-input (get-input-queue true))
        next-state (if *paused* state (-iterate (assoc state :input input)))
        _ (-paint-via-state next-state)
        t2 (System/nanoTime)
        diff (- t2 t1)
        sleep-for-ns (- (utils/nano-seconds-per-frame *updates-per-second*) diff)]
    (do
      (if-not *paused* (swap! -all-states conj next-state))
      (when (> sleep-for-ns 0) (Thread/sleep (long (/ sleep-for-ns 1000000))))
      (recur next-state))))

(defn open-window []
  (ss/invoke-later (ss/show! graphics/frame)))

(defn close-window []
  (ss/invoke-later (ss/hide! graphics/frame)))

(defn game! [] (loop-game state-0))

(defn -main [& args] (do (open-window) (loop-game state-0)))
