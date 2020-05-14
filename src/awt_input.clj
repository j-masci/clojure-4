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
  (let [type (case (.getID ev)
               KeyEvent/KEY_TYPED :key-press
               KeyEvent/KEY_PRESSED :key-down
               KeyEvent/KEY_RELEASED :key-up)]
    {:type type
     :code (.getKeyCode ev)
     :text (KeyEvent/getKeyText (.getKeyCode ev))}))

(defmethod map-input
  MouseEvent
  [ev]
  (let [type (case (.getID ev)
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


