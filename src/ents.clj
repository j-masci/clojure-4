(ns ents
  (:require shapes
            vec
            [clojure.core.matrix :as m]))

(defn ent [type & keyvals]
  "Creates an entity which is simply a map."
  (merge {:type   type
          :shapes []
          :pos    [0 0]
          :vel    [0 0]
          :acc    [0 0]
          :dir    0} (apply hash-map keyvals)))

(defn integrate [ent]
  "ie. semi-implicit Euler or something (or do I have it backwards? It
  makes no difference right now)."
  (-> ent
      (assoc :vel (m/add (:vel ent) (:acc ent)))
      (assoc :pos (m/add (:pos ent) (:vel ent)))))

;(defn ent-plus-deg [ent deg] (+ (:dir ent) deg))
;(ent-plus-deg ent 90)
;(+ (:dir ent) 90)

; -- Coordinate Conversions --

(defn align-all-shapes [ent]
  "Call once :dir and :pos are aligned to the window. Also, call only once."
  (update ent :shapes (fn [shapes] (mapv #(shapes/-align-shape-to-ent % ent) shapes))))

(defn ent-and-all-shapes-to-window-coords [ent camera]
  (-> ent
      (vec/ent-from-global-to-window-coords camera)
      (align-all-shapes)))

(defn draw! [state ent canvas g2d]
  "Draw an entity."
  (-> ent
      (ent-and-all-shapes-to-window-coords (:camera state))
      ((fn [e] (doseq [shape (:shapes e)] (shapes/draw! shape g2d))))))

