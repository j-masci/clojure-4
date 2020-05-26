(ns ents
  (:require shapes
            vec
            [clojure.core.matrix :as m]
            [clojure.spec.alpha :as s]
            [seesaw.graphics :as gr])
  (:use globals))

(defn ent [type & keyvals]
  (merge {:type   type
          :shapes []
          :pos    [0 0]
          :vel    [0 0]
          :acc    [0 0]
          :dir    0} (apply hash-map keyvals)))

(defn integrate [ent]
  (-> ent
      (assoc :vel (m/add (:vel ent) (:acc ent)))
      (assoc :pos (m/add (:pos ent) (:vel ent)))))

; -- Coordinate Conversions --

(defn align-all-shapes [ent]
  "Call once :dir and :pos are aligned to the window. Also, call only once."
  (update ent :shapes (fn [shapes] (mapv #(shapes/-align-shape-to-ent % ent) shapes))))

(defn ent-and-all-shapes-to-window-coords [ent camera]
  (-> ent
      (vec/ent-from-global-to-window-coords camera)
      (align-all-shapes)))

(defn draw! [state ent canvas g2d]
  (-> ent
      (ent-and-all-shapes-to-window-coords (:camera state))
      ((fn [e] (doseq [shape (:shapes e)] (shapes/draw! shape g2d))))))

