(ns awt-event
  (:import
    java.awt.event.KeyEvent
    java.awt.event.MouseEvent
    java.awt.event.MouseWheelEvent))

(defmulti convert
          "Convert a subclass of java.awt.event.AWTEvent to a clojure map"
          (fn [^java.awt.AWTEvent ev] (class ev)))

(defmethod convert
  KeyEvent
  [ev]
  {:type :key
   :code (.getKeyCode ev)
   :text (. KeyEvent getKeyText (.getKeyCode ev))})

(defmethod convert
  MouseEvent
  [ev]
  {:type :key
   :sub-type :TODO})

(defmethod convert
  MouseWheelEvent
  [ev]
  {:type :mouse-wheel
   :sub-type :TODO})

(defmethod convert
  :default
  [ev]
  {:type :not-recognized})


