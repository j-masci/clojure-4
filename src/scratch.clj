(ns scratch)

(def games (atom [{}]))

(defn update-game
  ([f] (swap! games (fn [prev] (conj @games (f (last @games)))))))

(defn print-games [] (doseq [game @games] (println game)))

(update-game #(assoc % :entities []))

(update-game #(update % :entities conj "new-ent"))

(update-game (fn [p] (assoc p :asdf 12)))

(update-game (fn [p] (assoc p :asdf 13)))

(update-game (fn [p] (assoc-in p [:asd :qwer] 5555)))

(print-games)

;{}
;{:entities []}
;{:entities [new-ent]}
;{:entities [new-ent], :asdf 12}
;{:entities [new-ent], :asdf 13}
;{:entities [new-ent], :asdf 13, :asd {:qwer 5555}}

(map (fn [a b] (str a "." b)) (list 1 2 3) (list 99 99 99))
; ("1.99" "2.99" "3.99")

(defn
  to-nth [coll] (range (count coll)))

(to-nth '(50 50 50 120 "blah"))
; (0 1 2 3 4)

(def _list (list 5 4 20))

(map (fn [key value] (str key ": " value)) (to-nth _list) _list)
; ("0: 5" "1: 4" "2: 20")

(defmacro m1 [] (list 'println "m2"))

(m1)

(defmacro m2 [a] (list 'println (str "..." a "...")))

(m2 "hi")
; redundant macro (may as well be a function)

(defmacro backwards [& body] (reverse body))

(backwards 40 println)
; 40

(defn func-args-into [what] (fn [& params] (into what params)))

(map (func-args-into []) (list 4 6 23) (list 3 3 3))
; ([4 3] [6 3] [23 3])

(defn filter-key-val [f coll] (let [keys-vals (map (func-args-into []) (range (count coll)) coll)
                                    filtered (filter (fn [[key val]] (f key val)) keys-vals)
                                    ret (map (fn [[key val]] val) filtered)]
                                ret))

(filter-key-val (fn [key val] (not= key 1)) (list 1 2 3 4 5))
; (1 3 4 5)

; not working atm....
(defmacro ignore [which-ones & body] (filter-key-val (fn [[key val]] (not (contains? which-ones key))) body))

(for [x (range 3)] x)
; (0 1 2)

(for [x (list 1 2 3)] (+ x 5))
; (6, 7, 8)

(for [x (range 3) x (range 3 6) x (range 6 9)] x)
; (6 7 8 6 7 8 6 7 8 6 7 8 6 7 8 6 7 8 6 7 8 6 7 8 6 7 8)

(if true 3 4)
(when true 3)

(defmacro first-true [& pairs] ())
; evaluate every other pairs until we find a true pair, then evaluate
; the result

; (first-true false 32 false (+ 3 3) true (* 2 2))


(defn side-effect [] (do (println "Side Effect...") "-- side effect --"))

(sort (fn [v1 v2] (cond (even? v1) -1 (even? v2) 1 :else 0)) [1 3 4 23 213 4234 1 12 23])
; (12 4234 4 1 3 23 213 1 23)

(defmacro -none [& args] ())

(#(let [_ %2] (+ %1 %1)) 5 5)
; 10

; (#(+ %1 %1) 5 5)
; execution error (arity)

(defprotocol p-adds-and-prints
  (-add [self a b])
  (-static? [b])
  (-self-not-first [a self])
  (-pr [self what])
  (-both [self a b]))

(deftype adpr-1 [wtf]
  p-adds-and-prints
  (-add [self a b] (+ 1000 a b))
  (-static? [b] (* b b))
  (-self-not-first [a self] (* a a))
  (-pr [self what] (println (str "self: " self "..." wtf "..." what "...")))
  (-both [self a b] (-pr self (-add self a b))))

(def add-printer-1 (adpr-1. "??"))

(-add add-printer-1 2 2)
; 1004

(-pr add-printer-1 "idk")
; self: scratch.adpr-1@3d222ae1...??...idk...

(-both add-printer-1 5 5)
; self: scratch.adpr-1@3d222ae1...??...1010...

; (-static? 9)
; error
; No implementation of method: :-static? of protocol: #'scratch/p-adds-and-prints found for class: java.lang.Long
; "methods" must accept self because that's the only way they can be invoked (?)

; (-self-not-first 10 add-printer-1)
; No implementation of method: :-self-not-first of protocol: #'scratch/p-adds-and-prints found for class: java.lang.Long
; don't know how to do this

(defmulti -func (fn [a b] (- b a)))

(defmethod -func 10 [a b] (do (println "a: " a) (println "b: " b)))

(methods -func)

(-func 10 20)
;a:  10
;b:  20

; (-func 50 50)
; not cool

(apply + (vector 3 4))
(apply + (list 3 4))
; 7

((juxt inc dec #(* 5 %)) 3)
; [4 3 15]

((juxt inc inc inc dec dec dec) 5)
; [6 6 6 4 4 4]

(mapcat reverse [[1 5 5] [2 92 12] [9 0]])
; [5 5 1 12 92 2 0 9]

(-> 23 inc inc inc inc)
; 27

((partial * 2) 10)
; 20

((partial println "Hello...") 59)
; Hello... 59

(-> 25 (+ 5) (+ 9) (+ 10))
; 49

(macroexpand '(-> 25 (+ 5) (+ 9) (+ 10)))
; (+ (+ (+ 25 5) 9) 10)

; not like this apparently..
;(def *thing* ^:dynamic 32)
;(def *thing* 43)
;(def *thing* 55)

(Thread/sleep 100)

; invoke static method
; (javax.swing.JPanel/isLightweightComponent 23)



(defn -f1 [] (println "-f1"))

(defn -f2 [] (do (println "-f2") (-f1)))

(-f2)
; -f2
; -f1

(defn -f1 [] (println "The new -f1"))
(-f2)
; -f2
; The new -f1

(def -a-value 100)
(def -my-map {:a 42 :b -a-value})
(println -my-map)
; {:a 42, :b 100}

(def -a-value 200)
(println -my-map)
; {:a 42, :b 100} (same)


; (defstruct mystruct :hi :hello)