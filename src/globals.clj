(ns globals)

(defmacro in
  "like get-in but keys are function args (not a vector). As a result, you can't pass a default value.
  (get-in m [:key_1 :key_2 0])
  (in m :key_1 :key_2 0)
  "
  [coll & keys]
  (list get-in coll (into [] keys)))