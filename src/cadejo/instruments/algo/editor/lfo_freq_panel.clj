
;; paramters
;; :lfo1-freq              :lfo2-freq                                           
;; :cca->lfo1-freq         :cca->lfo2-freq                                                
;; :ccb->lfo1-freq         :ccb->lfo2-freq   

(ns cadejo.instruments.algo.editor.lfo-freq-panel
  (:use [cadejo.instruments.algo.algo-constants])
  (:require [cadejo.util.math :as math])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.util.sgwr-factory :as sfactory])
  (:require [sgwr.components.line :as line])
  (:require [sgwr.components.text :as text])
  (:require [sgwr.indicators.displaybar :as dbar])
  (:require [sgwr.tools.slider :as slider]))

(defn freq-panel [n drw p0 ied]
     (let [param-freq (keyword (format "lfo%d-freq" n))
           param-cca (keyword (format "cca->lfo%d-freq" n))
           param-ccb (keyword (format "ccb->lfo%d-freq" n))
           id (keyword (format "lfo%d" n))
           root (.root drw)
           tools (.tool-root drw)
           [x0 y0] p0
           x-dbar (+ x0 20)
           x-edit (+ x-dbar 182)
           x-slider-a (+ x0 280)
           x-slider-b (+ x-slider-a (* 1.5 slider-spacing))
           x-label-offset -12
           y-dbar (- y0 50)
           y-edit y-dbar
           y-sliders (- y0 32)
           y-labels (- y0 8)
           dbar (sfactory/displaybar drw [x-dbar y-dbar] 6)
           edit-action (fn [b _]
                         (dbar/displaybar-dialog dbar
                                                 (format "LFO %s Frequency" n)
                                                 :validator (fn [q]
                                                              (let [b (math/str->float q)]
                                                                (and b (pos? b))))
                                                 :callback (fn [_]
                                                             (let [s (.current-display dbar)
                                                                   b (math/str->float s)]
                                                               (.set-param! ied param-freq b)))))
           b-edit (sfactory/mini-edit-button drw[x-edit y-edit] id edit-action)
           slider-action (fn [s _]
                           (let [p (.get-property s :id)
                                 v (slider/get-slider-value s)]
                             (.set-param! ied p v)))
           s-cca (sfactory/vslider drw ied param-cca [x-slider-a y-sliders] -10.0 10.0 slider-action)
           s-ccb (sfactory/vslider drw ied param-ccb [x-slider-b y-sliders] -10.0 10.0 slider-action)
           sync-fn (fn []
                     (let [dmap (.current-data (.bank (.parent-performance ied)))
                           freq (float (param-freq dmap))
                           cca (float (param-cca dmap))
                           ccb (float (param-ccb dmap))]
                       (.display! dbar (format "%6.3f" freq) false)
                       (slider/set-slider-value! s-cca cca false)
                       (slider/set-slider-value! s-ccb ccb false)))]
       (sfactory/label drw [(+ x-slider-a x-label-offset) y-labels] "CCA")
       (sfactory/label drw [(+ x-slider-b x-label-offset) y-labels] "CCB")
       (sfactory/text drw [(+ x-dbar 38)(- y-dbar 20)] "Frequency")
       (sfactory/minor-border drw [x0 y0][(+ x0 390)(- y0 210)])
       ;; rules
       (let [x1 (- x-slider-a 10)
             x2 (+ x-slider-b 10)
             xtx (- x1 35)
             vn1 (- y-sliders 0)
             vp1 (- vn1 slider-length)
             v0 (math/mean vn1 vp1)
             vline (fn [y c] 
                     (line/line root [x1 y][x2 y] :id id
                                :style :dotted
                                :color c))
             minor (fn [y n]
                     (vline y (lnf/minor-tick))
                     (text/text root [xtx (+ y 5)] (format "%+2d" n)
                                :style :mono
                                :size 6
                                :color (lnf/minor-tick)))]
         (minor vn1 -10)
         (minor vp1 10)
         (minor v0 0)
         (minor (math/mean v0 vp1) 5)
         (minor (math/mean v0 vn1) -5))
       {:sync-fn sync-fn}))
