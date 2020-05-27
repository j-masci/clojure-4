(ns input
  (:import
    java.awt.event.KeyEvent
    java.awt.event.MouseEvent
    java.awt.event.MouseWheelEvent))

(defmulti awt-event-obj->map
          "Convert a subclass of java.awt.event.AWTEvent to a clojure map"
          (fn [^java.awt.AWTEvent ev] (class ev)))

(defmethod awt-event-obj->map
  KeyEvent
  [ev]
  (let [type (condp = (.getID ev)
               KeyEvent/KEY_TYPED :key-typed
               KeyEvent/KEY_PRESSED :key-down
               KeyEvent/KEY_RELEASED :key-up)]
    {:type         type
     :code         (.getKeyCode ev)
     :text         (.toLowerCase (KeyEvent/getKeyText (.getKeyCode ev)))
     :param-string (.paramString ev)}))

(defmethod awt-event-obj->map
  MouseEvent
  [ev]
  (let [type (condp = (.getID ev)
               MouseEvent/MOUSE_CLICKED :mouse-click
               MouseEvent/MOUSE_FIRST :mouse-first
               MouseEvent/MOUSE_LAST :mouse-last
               MouseEvent/MOUSE_CLICKED :mouse-clicked
               MouseEvent/MOUSE_PRESSED :mouse-pressed
               MouseEvent/MOUSE_RELEASED :mouse-released
               MouseEvent/MOUSE_MOVED :mouse-moved
               MouseEvent/MOUSE_ENTERED :mouse-entered
               MouseEvent/MOUSE_EXITED :mouse-exited
               MouseEvent/MOUSE_DRAGGED :mouse-dragged
               MouseEvent/MOUSE_WHEEL :mouse-wheel
               ; MouseEvent/NOBUTTON :mouse-no-button         ; wtf?
               MouseEvent/BUTTON1 :mouse-1
               MouseEvent/BUTTON2 :mouse-2
               MouseEvent/BUTTON3 :mouse-3
               :mouse-unknown
               )]
    {:type         type
     :param-string (.paramString ev)}))

(defmethod awt-event-obj->map
  MouseWheelEvent
  [ev]
  {:type         :TODO
   :param-string (.paramString ev)})

(defmethod awt-event-obj->map
  :default
  [ev]
  {:type         :not-recognized
   :param-string (.paramString ev)})

(defn coll-contains [coll & preds]
  "Returns true if all predicates return true on at least one element of the collection
  (and does this efficiently/lazily). Basically just (boolean (some pred coll)) but allows
  for multiple predicates (ie. it calls every-pred for you)."
  (when (> (count coll) 0) (boolean (some (apply every-pred preds) coll))))

(defn where [key val]
  "the returned function checks if (a-map key) equals val"
  (fn [a-map] (= (a-map key) val)))

; key typed broken atm
(defn is-key-typed [evs key-text]
  (coll-contains evs
                 (where :type :key-typed)
                 (where :text key-text)))

(defn is-key-down [evs key-text]
  (coll-contains evs
                 (where :type :key-down)
                 (where :text key-text)))

(defn is-key-up [evs key-text]
  (coll-contains evs
                 (where :type :key-up)
                 (where :text key-text)))
