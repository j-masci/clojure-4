(ns ents
  (:require [clojure.core.matrix :as m]
            [seesaw.graphics :as gr])
  (:use globals))

(def player {:type :player
             :dir  0.0
             :pos  [500 500]
             :vel  [0 0]
             :acc  [0 0]})

(defn integrate [ent]
  (-> ent
      (assoc :vel (m/add (:vel ent) (:acc ent)))
      (assoc :pos (m/add (:pos ent) (:vel ent)))))

(defmulti draw (fn [ent g] (:type ent)))

(defmethod draw
  :player
  [ent g2d]
  (doto g2d
    (gr/draw (gr/circle (in ent :pos 0) (in ent :pos 1) 10) (utils/gr.style [100 100 100 250] 1))
    (.drawString (str (:pos ent)) 50 50)))



(defmulti transform-shape
          (fn [shape offset rotation]
            (:type shape)))

(defmethod transform-shape
  :line
  [line offset rotation]
  ())

(def eg-render-data
  [{:type :line
    :start [-10 -10]
    :end [-10 20]}
   {:type   :circle
    :center [0 0]
    :radius 5}])