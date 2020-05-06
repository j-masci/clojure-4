(ns _core
  (:require
    [clojure.core.matrix :as m]
    [seesaw.core :as ss]
    [seesaw.graphics :as gr]
    [seesaw.color :as color]
    ;[jdk.awt.core]
    ;[jdk.awt.Frame :as frame]
    ;[jdk.awt.Color :as color]
    ))

(def color color/color)

(defn reload
  []
  (use '_core :reload)
  (use 'scratch :reload))

; style object which is 4th param of gr/draw
(defn style
  ([[r g b a]] (style [r g b a] 1))
  ([[r g b a] width] (let [c (color r g b (if (= a nil) 255 a))]
                       (gr/style
                         :foreground c
                         :background c
                         :stroke (gr/stroke :width (if width width 1))))))

(def player {:type :player
             :pos  [400 400]
             :vel  [1 1]
             :acc  [1 1]})

; blank state, see init-state
(def state
  {:step       0
   :delta-time 0
   :config     {}
   :ents   [player]
   ; wondering if there's a way to not include these mutable objects in our immutable state...
   :canvas     nil
   :panel      nil
   :frame      nil})

(defn integrate [ent]
  (-> ent
      (assoc :vel (m/add (:vel ent) (:acc ent)))
      (assoc :pos (m/add (:pos ent) (:vel ent)))))

; for now, dispatch the entity update function via type. later we'll add support for sub types
; or custom updaters... or custom actions before/after updating on a per-entity basis
(defmulti update-entity (fn [ent _] (:type ent)))

(defmethod update-entity :player
  [ent state]
  (-> ent
      integrate))

(defn update-ents [state]
  (assoc state :ents (mapv #(update-entity % state) (:ents state))))

(defmulti draw-entity (fn [ent _] (:type ent)))

(defmethod draw-entity :player
  [ent state] (println "Draw entity..."))

(defn draw-ents [state]
  (doseq [ent (:ents state)] (draw-entity ent state)))

(defn step [state]
  "The update function. Returns the next state via the current state."
  (-> state
      (update :step inc)
      update-ents))

(defn draw [state]
  (draw-ents state))

(defn paint [canvas g2d]
  (doto g2d
    (gr/draw (gr/line 0 0 200 400) (style [100 23 250 255] 3))
    (.drawString "clojure-4" 200 200)))

(def canvas (ss/canvas :id "canvas" :background "#EBEBEB" :paint paint))

; a JPanel object
(def panel (ss/border-panel :hgap 1 :vgap 1 :border 3 :center canvas))

(def frame (ss/frame
             :width 1200
             :height 900
             :title "Clojure-4"
             :on-close :exit
             :content panel))

; initialize state after dependencies are defined
(defn init-state [state]
  (-> state
      ; (assoc :ents (conj (:ents state) player))
      (assoc :canvas canvas)
      (assoc :panel panel)
      (assoc :frame frame)))

(defn -main [& args]
  (println "-MAIN")
  (let
    [state (init-state state)]
    (ss/invoke-later
      (-> frame ss/show!)
      )))

