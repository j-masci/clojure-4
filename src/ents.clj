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

(defn sync-shapes
  "Transform shapes relative to entity pos/dir. Cannot be done
  more than once."
  [ent]
  (update ent :shapes shapes/ent-align-shapes ent))

(defn to-window-coords-fuck [ent]
  (let [p (:pos ent)]
    (-> ent
        (update :pos vec/cam->window 1200 900)
        (update :shapes ))))

(defn to-window-coords
  "Transform an entity from world->camera->window coordinates."
  [ent state]
  (-> ent

      ; to cam coordinates, first
      (vec/ent-in-camera-coords (:camera state))

      ; then to window coordinates
      (#(update % :pos vec/cam->window 1200 900))

      (sync-shapes)

      ; ((fn [e] (update e :shapes (fn [shapes] (mapv (fn [shape] ()) shapes)))))

      ; then sync shapes
      ; (sync-shapes)
      ))

(defn to-cam-coords
  "Transform an entity from world to camera coordinates."
  [ent state]
  (-> ent
      ; to cam coordinates, first
      (vec/ent-in-camera-coords (:camera state))
      ; then to window coordinates
      ; (#(update % :pos vec/cam->window 1200 900))
      ; then sync shapes
      (sync-shapes)))

(defn draw! [state ent canvas g2d]
  (-> ent
      (to-window-coords state)
      (#(doseq [shape (:shapes %)] (shapes/draw! shape g2d)))))

