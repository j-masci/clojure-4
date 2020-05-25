(ns scratch
  (:require shapves vec ents mock graphics input utils colors))

; good
(shapes/ent-align-shapes [(shapes/create-line [0 0] [0 20])] {:pos [0 0] :dir 0})
; [{:type :line, :p1 [0.0 0.0], :p2 [20.0 1.2246467991473533E-15]}]

; good
(shapes/ent-align-shapes [(shapes/create-line [0 -5] [0 5])] {:pos [100 100] :dir 45})
; [{:type :line, :p1 [96.46446609406726 96.46446609406726], :p2 [103.53553390593274 103.53553390593274]}]

; good
(shapes/ent-align-shapes [(shapes/create-line [10 10] [20 20])] {:pos [0 10] :dir 90})
; [{:type :line, :p1 [10.0 20.0], :p2 [20.0 30.0]}]
