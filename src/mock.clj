(ns mock
  "Mock data, useful in repl."
  (:require shapes vec ents utils colors input))

(def ent (ents/ent :player
                      :pos [0 200]
                      :dir -90
                      ; A circle 10 units "in front", and a perpendicular line of length 200
                      :shapes [(shapes/create-circle [0 10] 2)
                               (shapes/create-line [-100 0] [100 0])]))

(def player ent)

; simple shape with a circle at its position and a line pointing
; in the direction its entity is facing
(defn simple-shapes [rgba]
  [(assoc (shapes/create-circle [0 0] 10) :color rgba)
   (assoc (shapes/create-line [0 0] [0 50]) :color rgba)])

(def ent0 (ents/ent :ent0
                    :pos [200 0]
                    :dir 0
                    ; red
                    :shapes (simple-shapes [255 0 0 255])))

(def ent1 (ents/ent :ent1
                    :pos [0 200]
                    :dir 90
                    ; green
                    :shapes (simple-shapes [0 255 0 255])))

(def ent2 (ents/ent :ent2
                    :pos [-200 0]
                    :dir 180
                    ; blue
                    :shapes (simple-shapes [0 0 255 255])))

(def ent3 (ents/ent :ent3
                    :pos [0 -200]
                    :dir -90
                    ; brown-ish?
                    :shapes (simple-shapes [200 200 100 255])))

(def ents [ent0 ent1 ent2 ent3])

(def camera {:pos [0 0]
             :dir 0
             :zoom 1.0})

(def state {:step 0
            :input []
            :camera camera
            :ents [player]})


;(shapes/ent-align-shapes [(shapes/create-line [0 0] [0 20])] {:pos [0 0] :dir 0})
;[{:type :line, :p1 [0.0 0.0], :p2 [20.0 1.2246467991473533E-15]}]
