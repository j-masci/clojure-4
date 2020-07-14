(ns game
  "init, update, draw, etc."
  (:require colors
            ents
            globals
            graphics
            input
            mock
            shapes
            utils
            vec
            [seesaw.core :as ss]))

(defn get-camera []
  {:pos [0 0] :dir 0 :zoom 0})

(def ent-process
  {:types []
   :f (fn [e]
        ())})

(defn get-empty-state []
  "Minimal-ish initial state (no ents added yet)."
  {:step       0
   :delta-time 0
   :paused     false
   :camera     (get-camera)
   :input      []
   :ents       []})

(def ent_player
  (ents/ent :player
            :dir 45
            :pos [0.0 0.0]
            :vel [0.0 0.0]
            :acc [0.0 0.0]
            :shapes (concat [(shapes/create-circle [0 16] 4)
                             (shapes/create-circle [-10 -16] 4)
                             (shapes/create-circle [-10 16] 4)
                             (shapes/create-circle [10 16] 4)
                             (shapes/create-circle [10 -16] 4)]
                            (shapes/points->lines [[-10 -16]
                                                   [-10 16]
                                                   [10 16]
                                                   [10 -16]]))))

(def ent_grid-lines
  (ents/ent :grid-lines
            :shapes [(assoc (shapes/create-line [-1000 0] [1000 0]) :color [100 100 100 255])
                     (assoc (shapes/create-line [0 -1000] [0 1000]) :color (colors/rgba :black))]))

(defn get-initial-state []
  "Adds some entities and stuff to an empty state map."
  (-> (get-empty-state)
      (update :ents conj ent_player)
      (update :ents conj ent_grid-lines)))

(defn is-paused [state]
  "Indicates how to pause the game via state."
  (boolean (:paused state)))

; might register listeners like this.
(def listener-eg?
  {:pred   (fn [state] (true))
   :action (fn [state] state)})

(defn inc-by [x] (fn [i] (+ i x)))

; partial?
;(defmacro something-by [op x]
;  ())
; (something-by + 5)

; does this already work?
; (partial + 5)

(defn handle-input [state]
  (let [kd (fn [text] (input/is-key-down (:input state) text))]
    (cond-> state
            (kd "up") (update :camera vec/add-rel-deg :pos 0 10)
            (kd "left") (update :camera vec/add-rel-deg :pos 90 10)
            (kd "down") (update :camera vec/add-rel-deg :pos 180 10)
            (kd "right") (update :camera vec/add-rel-deg :pos -90 10)
            (kd "w") (update-in [:ents 0] vec/add-rel-deg :pos 0 10)
            (kd "a") (update-in [:ents 0] #(update % :dir (partial + 5)))
            (kd "s") (update-in [:ents 0] vec/add-rel-deg :pos 0 -5)
            (kd "d") (update-in [:ents 0] #(update % :dir (partial + -5)))
            (kd "space") (update-in [:ents 0] #(assoc % :vel [0 0]))
            (kd "f1") (#(do (utils/log-debug! (deref globals/*all-states*)) %))
            (kd "f2") (#(do (utils/log-debug! (last (deref globals/*all-states*))) %))
            (kd "f5") (fn [state] (get-initial-state))
            (kd "esc") (assoc :paused true))))

(defn update*
  "Get the next state via the current state. A pure function."
  ([state input]
   (assert (vector? input))
   (assert (map? state))
   (update* (assoc state :input input)))
  ([state]
   (-> state
       (update :step inc)
       (handle-input)
       (update :ents #(mapv ents/integrate %))
       (assoc-in [:camera :pos] (get-in state [:ents 0 :pos]))
       (assoc-in [:camera :dir] (get-in state [:ents 0 :dir]))
       )))

(defn paint! [state canvas g2d]
  "Paint state to the screen."
  (utils/draw-string g2d 5 20 (with-out-str (clojure.pprint/pprint (select-keys state [:step :camera]))))
  (utils/draw-string g2d 5 40 (with-out-str (clojure.pprint/pprint (-> state (:input)))))
  (utils/draw-string g2d 5 60 (with-out-str (clojure.pprint/pprint (-> state (:ents) (get 0) (dissoc :shapes)))))
  (doseq [ent (:ents state)] (ents/draw! state ent canvas g2d)))