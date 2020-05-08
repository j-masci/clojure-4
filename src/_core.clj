(ns _core
  (:require
    [clojure.core.matrix :as m]
    [seesaw.core :as ss]
    [seesaw.options :as ss.options]
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

; create a paint function via state
(defn make-paint-fn [state]
  (fn [canvas g2d]
    (println "Paint: " (:step state))
    (doto g2d
      (gr/draw (gr/line 0 0 200 400) (style [100 23 250 255] 3))
      (.drawString (str "Step: " (:step state)) 200 200))))

(defn make-frame [] (let [paint-fn (make-paint-fn {})
                          canvas (ss/canvas :id "canvas" :background "#EBEBEB" :paint paint-fn)
                          panel (ss/border-panel :hgap 1 :vgap 1 :border 3 :center canvas)
                          frame (ss/frame
                                  :width 1200
                                  :height 900
                                  :title "Clojure-4"
                                  :on-close :exit
                                  :content panel)]
                      [canvas panel frame]))

(def -frame-etc (make-frame))
(def canvas (-frame-etc 0))
(def panel (-frame-etc 1))
(def frame (-frame-etc 2))

; set the paint fn on the canvas
(defn set-paint-fn [f] (ss.options/apply-options canvas {:paint f}))

; repaint via state
(defn paint [state] (set-paint-fn (make-paint-fn state)) (ss/repaint! canvas))

; updates and/or frames per second
(def ^:dynamic *ups* 2)

(defn nano-seconds-per-frame [ups]
  (if (> ups 0) (int (* 1000000000 (float (/ 1 ups)))) 0))

; blank state, see init-state
(def state
  {:step       0
   :delta-time 0
   :config     {}
   :ents       [player]})

(defn run [state]
  (let [t1 (System/nanoTime)
        next-state (step state)
        _ (paint next-state)
        t2 (System/nanoTime)
        diff (- t2 t1)
        sleep-for-ns (- (nano-seconds-per-frame *ups*) diff)]
    (do
      (when (> sleep-for-ns 0) (Thread/sleep (long (/ sleep-for-ns 1000000))))
      (recur next-state))))

(defn -main [& args]
  (println "-MAIN")
  (ss/invoke-later (ss/show! frame)))

