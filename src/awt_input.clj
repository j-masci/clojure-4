(ns awt_input
  (:import
    java.awt.event.KeyEvent
    java.awt.event.MouseEvent
    java.awt.event.MouseWheelEvent))

(defmulti map-input
          "Convert a subclass of java.awt.event.AWTEvent to a clojure map"
          (fn [^java.awt.AWTEvent ev] (class ev)))

(defmethod map-input
  KeyEvent
  [ev]
  (let [type (condp = (.getID ev)
               KeyEvent/KEY_TYPED :key-press
               KeyEvent/KEY_PRESSED :key-down
               KeyEvent/KEY_RELEASED :key-up)]
    {:type type
     :code (.getKeyCode ev)
     :text (.toLowerCase (KeyEvent/getKeyText (.getKeyCode ev)))}))

(defmethod map-input
  MouseEvent
  [ev]
  (let [type (condp = (.getID ev)
               MouseEvent/MOUSE_CLICKED :mouse-click
               :TODO
               )]
    {:type :TODO}))

(defmethod map-input
  MouseWheelEvent
  [ev]
  {:type :TODO})

(defmethod map-input
  :default
  [ev]
  {:type :not-recognized})

(defn coll-contains [coll & preds]
  "Returns true if all predicates return true on at least one element of the collection
  (and does this efficiently/lazily). Basically just (boolean (some pred coll)) but allows
  for multiple predicates (ie. it calls every-pred for you)."
  (boolean (some (apply every-pred preds) coll)))

(defn where [key val]
  (fn [a-map] (= a-map key) val))

(defn is-key-up [evs key-text]
  (coll-contains evs
    (where :type :key-up)
    (where :keyText key-text)))

(def eg-events [{:type :mouse-click} {:type :key-up :keyText "s"} {:type :key-press :keyText "space"}])

(def eg-check (is-key-up eg-events "s"))


