(ns sgwr.utilities
  (:import java.awt.geom.Path2D))

; ---------------------------------------------------------------------- 
;                                   Math

(def ^:private k-rad (/ Math/PI 180))
(def ^:private k-deg (/ 1 k-rad))

(defn deg->rad [d]
  "Convert degrees to radians"
  (* d k-rad))

(defn rad->deg [r]
  "Convert radians to degrees"
  (* r k-deg))

(defn abs [n]
  (Math/abs n))

(defn sqrt [n]
  (Math/sqrt n))


(defn clamp [n mn mx]
  "Restrict value
   Returns n such that  mn <= n <= mx"
  (max (min n mx) mn))

(defn distance [p0 p1]
  (let [[x0 y0] p0
        [x1 y1] p1
        dx (- x1 x0)
        dy (- y1 y0)]
    (sqrt(+ (* dx dx)(* dy dy)))))



; ---------------------------------------------------------------------- 
;                                  Shapes

(defn combine-shapes 
  "Combine two instances of java.awt.Shape"
  ([s1 s2 connect]
     (let [p1 (java.awt.geom.Path2D$Double. s1)]
       (.append p1 s2 connect)
       p1))
  ([s1 s2]
     (combine-shapes s1 s2 false)))