(ns _core
  (:require
    graphics
    utils
    ents
    input
    shapes
    vec
    mock
    [seesaw.core :as ss]
    [clojure.data.json :as json]
    [clojure.pprint]
    [clojure.core.matrix :as matrix]
    [seesaw.graphics :as gr]
    )
  (:use globals))

(def *updates-per-second* 60)

(def *paused* false)

(def state-0
  "Initial state. Immutable."
  {:step       0
   :delta-time 0
   :input      []
   :config     {}
   :camera     {:pos  [0 0]
                :dir  90
                :zoom 1.0}
   ; for now, player must be the first entity
   :ents       [
                (ents/ent :player
                          :dir 45
                          :pos [0.0 0.0]
                          :vel [0.0 0.0]
                          :acc [0.0 0.0]
                          :shapes (concat [
                                           (shapes/create-circle [0 16] 4)
                                           (shapes/create-circle [-10 -16] 4)
                                           (shapes/create-circle [-10 16] 4)
                                           (shapes/create-circle [10 16] 4)
                                           (shapes/create-circle [10 -16] 4)]
                                          (shapes/points->lines [[-10 -16]
                                                                 [-10 16]
                                                                 [10 16]
                                                                 [10 -16]])))

                (ents/ent :grid-lines
                          :shapes [
                                   ; y-axis, grey
                                   (assoc (shapes/create-line [-1000 0] [1000 0]) :color [100 100 100 255])
                                   ; x axis, black
                                   (assoc (shapes/create-line [0 -1000] [0 1000]) :color (colors/rgba :black))])
                ; a circle in the first quadrant with a line pointing towards the origin
                ;(ents/ent :thing
                ;          :dir 225
                ;          :pos [250 250]
                ;          :shapes [(shapes/create-circle [0 0] 30)
                ;                   (shapes/create-line [0 0] [0 250])])
                ; simple shapes on each axis
                mock/ent0
                mock/ent1
                mock/ent2
                mock/ent3]})

; good for testing/repl
(defn player [] (get-in state-0 [:ents 0]))

; for debugging
(def -all-states (atom []))

(defn dump-state! [state]
  (let [timestamp (int (/ (System/currentTimeMillis) 1000))
        content (with-out-str (json/pprint state))
        _ (println "--DUMPING STATE--" (count content))]
    (spit (str "debug/states-" timestamp ".txt") content)))

(defn -check-global-controls [state]
  (when (input/is-key-up (:input state) "f1") (println "Ok what?"))
  (when (input/is-key-up (:input state) "f2") (dump-state! @-all-states))
  (when (input/is-key-up (:input state) "f3") (dump-state! (last @-all-states)))
  (if (input/is-key-up (:input state) "f5") state-0 state))

; should we define listeners as data ?? ya probably
(def listener-eg {:type :key-up :text "w" :active true :callback (fn [state] (-> state))})

(defn add-rel-deg [ent key degrees magnitude]
  "Update an entities pos/vel/acc in a direction which is relative to its current direction. 0 degrees
  means forward, 90 is to the left, etc."
  (update ent key matrix/add (vec/from-polar-deg (+ (:dir ent) degrees) magnitude)))

(defn -check-player-controls [state]
  (cond-> state
          (input/is-key-down (:input state) "q") (update-in [:camera :dir] #(- % 2))
          (input/is-key-down (:input state) "e") (update-in [:camera :dir] #(+ % 2))
          (input/is-key-down (:input state) "w") (update :camera add-rel-deg :pos 0 10)
          (input/is-key-down (:input state) "a") (update :camera add-rel-deg :pos 90 10)
          (input/is-key-down (:input state) "s") (update :camera add-rel-deg :pos 180 10)
          (input/is-key-down (:input state) "d") (update :camera add-rel-deg :pos -90 10)
          (input/is-key-down (:input state) "up") (update-in [:ents 0] add-rel-deg :vel 0 5)
          (input/is-key-down (:input state) "left") (update-in [:ents 0] add-rel-deg :vel 90 5)
          (input/is-key-down (:input state) "down") (update-in [:ents 0] add-rel-deg :vel 180 5)
          (input/is-key-down (:input state) "right") (update-in [:ents 0] add-rel-deg :vel -90 5)))

(defn -iterate [state]
  "Get the next state via the current state. A pure function. Note that state contains input."
  (-> state
      (update :step inc)
      (-check-global-controls)
      (-check-player-controls)
      ; (-compute-ent-shapes)
      (update :ents #(mapv ents/integrate %))))

; why the FUCK does this do nothing
(defn draw-absolute-line
  ([g2d p1 p2] (draw-absolute-line g2d p1 p2 [255 255 255 255]))
  ([g2d p1 p2 rgba] (gr/draw g2d (gr/line (p1 0) (p1 1) (p2 0) (p2 1)) (utils/gr.style rgba))))

(defn draw-string
  [g2d x y str]
  (.drawString g2d str x y))

(defn -paint! [state canvas g2d]
  (let [ww graphics/window-width
        ww2 (int (/ ww 2))
        wh graphics/window-height
        wh2 (int (/ wh 2))]
    ; draw the entities
    (doseq [ent (:ents state)] (ents/draw! state ent canvas g2d))
    (draw-absolute-line g2d [0 wh2] [ww wh2] (colors/rgba :green1))
    (draw-absolute-line g2d [ww2 0] [ww2 wh] (colors/rgba :greenyellow))
    ; debugging stuff
    (draw-string g2d 5 20 (with-out-str (clojure.pprint/pprint (select-keys state [:step :camera]))))
    (draw-string g2d 5 40 (with-out-str (clojure.pprint/pprint (-> state (:input)))))
    (draw-string g2d 5 60 (with-out-str (clojure.pprint/pprint (-> state (:ents) (get 0) (dissoc :shapes)))))
    ;(draw-string g2d 5 80 (with-out-str (clojure.pprint/pprint (-> state (:ents) (get 2) (dissoc :shapes)))))
    ;(draw-string g2d 5 100 (with-out-str (clojure.pprint/pprint (-> state (:ents) (get 2) (ents/to-cam-coords state) (:shapes)))))
    ))

(defn -paint-via-state! [state]
  "paint state to the screen"
  (graphics/paint-given-fn! (fn [canvas g2d]
                              (-paint! state canvas g2d))))

(def -*input-queue*
  "vector of input events to process on next update"
  (atom []))

(defn get-input-queue!
  "get queued inputs as a vector, and optionally reset the queue (side effect)"
  ([] (get-input-queue! false))
  ([reset]
   (let [queue @-*input-queue*]
     (when reset (reset! -*input-queue* []))
     queue)))

(defn -queue-input! [e] (swap! -*input-queue* conj e))

(ss/listen graphics/canvas
           :mouse-entered -queue-input!)

; keys, focus, and mouse wheel, but not mouse entered
(ss/listen graphics/frame
           :focus-gained -queue-input!
           :mouse-wheel-moved -queue-input!
           :key -queue-input!)

(defn loop-game! [state]
  "ie. start game, run"
  (let [t1 (System/nanoTime)
        ; not perfect input logic in respect to game paused for now
        input (mapv input/map-input (get-input-queue! true))
        next-state (if *paused* state (-iterate (assoc state :input input)))
        _ (-paint-via-state! next-state)
        t2 (System/nanoTime)
        diff (- t2 t1)
        sleep-for-ns (- (utils/nano-seconds-per-frame *updates-per-second*) diff)]
    (do
      (if-not *paused* (swap! -all-states conj next-state))
      (when (> sleep-for-ns 0) (Thread/sleep (long (/ sleep-for-ns 1000000))))
      (recur next-state))))

(defn open-window! []
  (ss/invoke-later (ss/show! graphics/frame)))

(defn close-window! []
  (ss/invoke-later (ss/hide! graphics/frame)))

(defn game! [] (loop-game! state-0))

(defn -main [& args] (do (open-window!) (loop-game! state-0)))


