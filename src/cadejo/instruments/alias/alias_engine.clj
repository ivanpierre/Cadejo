(println "--> Loading Alias mono synth")

(ns cadejo.instruments.alias.alias-engine
  (:use [overtone.core])
  (:require [cadejo.midi.mono-mode])
  (:require [cadejo.midi.poly-mode])
  (:require [cadejo.midi.mono-exclusive])
  (:require [cadejo.midi.performance])
  (:require [cadejo.instruments.descriptor])
  (:require [cadejo.instruments.alias.constants :as constants])
  (:require [cadejo.instruments.alias.genpatch])
  (:require [cadejo.instruments.alias.program])
  (:require [cadejo.instruments.alias.pp])
  (:require [cadejo.instruments.alias.data])
  (:require [cadejo.instruments.alias.control])
  (:require [cadejo.instruments.alias.head])
  (:require [cadejo.instruments.alias.tone])
  (:require [cadejo.instruments.alias.efx])
  (:require [cadejo.instruments.alias.editor.alias-editor :as aliased]))


(def clipboard* (atom nil))

(def alias-descriptor 
  (let [d (cadejo.instruments.descriptor/instrument-descriptor :alias "Alias Mono Synth" clipboard*)]
    (.add-controller! d :cc7 "Volume" 7)
    (.add-controller! d :cca "A"  1)
    (.add-controller! d :ccb "B" 16)
    (.add-controller! d :ccc "C" 17)
    (.add-controller! d :ccd "d"  4)
    (.set-editor-constructor! d aliased/alias-editor)
    (.initial-program! d constants/initial-program)
    (.program-generator! d cadejo.instruments.alias.genpatch/random-alias-program)
    (.help-topic! d :alias)
    d))

(defn create-performance [chanobj id keymode main-out
                          cca ccb ccc ccd cc-volume]
  (let [bank (.clone cadejo.instruments.alias.program/bank)
        performance (cadejo.midi.performance/performance chanobj id keymode
                                                         bank alias-descriptor
                                                         [:cca cca :linear 0.0]
                                                         [:ccb ccb :linear 0.0]
                                                         [:ccc ccc :linear 0.0]
                                                         [:ccd ccd :linear 0.0]
                                                         [:cc7 cc-volume :linear 1.0])]
    (.put-property! performance :instrument-type :alias)
    (.add-controller! performance :cc7 cc-volume :linear 1.0)
    (.set-bank! performance bank)
    (let [a-bus (control-bus)
          b-bus (control-bus)
          c-bus (control-bus)
          d-bus (control-bus)
          e-bus (control-bus)
          f-bus (control-bus)
          g-bus (control-bus)
          h-bus (control-bus)
          env1-bus (control-bus)
          env2-bus (control-bus)
          env3-bus (control-bus)
          lfo1-bus (control-bus)
          lfo2-bus (control-bus)
          lfo3-bus (control-bus)
          filter-in-bus (audio-bus 2)
          filter-out-bus (audio-bus 2)
          efx-in-bus (audio-bus 2)]
      (.pp-hook! bank cadejo.instruments.alias.pp/pp-alias)
      (.add-control-bus! performance :a a-bus)
      (.add-control-bus! performance :b b-bus)
      (.add-control-bus! performance :c c-bus)
      (.add-control-bus! performance :d d-bus)
      (.add-control-bus! performance :e e-bus)
      (.add-control-bus! performance :f f-bus)
      (.add-control-bus! performance :g g-bus)
      (.add-control-bus! performance :h h-bus)
      (.add-control-bus! performance :env1 env1-bus)
      (.add-control-bus! performance :env2 env2-bus)
      (.add-control-bus! performance :env3 env3-bus)
      (.add-control-bus! performance :lfo1 lfo1-bus)
      (.add-control-bus! performance :lfo2 lfo2-bus)
      (.add-control-bus! performance :lfo3 lfo3-bus)
      (.add-audio-bus! performance :filter-in filter-in-bus)
      (.add-audio-bus! performance :efx-in efx-in-bus)
      (.add-audio-bus! performance :main-out main-out)
      performance)))
  
(defn- sleep 
  ([arg]
     (Thread/sleep 100)
     arg)
  ([]
     (sleep nil)))

;; cca - general controller - default vibrato
;; ccb - general controller
;; ccc - general controller
;; ccd - general controller
;; cc7 - volume

(defn alias-instance
  ([scene chan id keymode & {:keys [cca ccb ccc ccd cc7 main-out]
                     :or {cca 1
                          ccb 16
                          ccc 17
                          ccd 4
                          cc7 7
                          main-out 0}}]
     (let [chanobj (.channel scene chan)
           ;keymode (cadejo.midi.mono-mode/mono-keymode :Alias)
           performance (create-performance chanobj id keymode main-out
                                           cca ccb ccc ccd cc7)
           bend-bus (.control-bus performance :bend)
           pressure-bus (.control-bus performance :pressure)
           cca-bus (.control-bus performance cca)
           ccb-bus (.control-bus performance ccb)
           ccc-bus (.control-bus performance ccc)
           ccd-bus (.control-bus performance ccd)
           volume-bus (.control-bus performance cc7)
           a-bus (.control-bus performance :a)
           b-bus (.control-bus performance :b)
           c-bus (.control-bus performance :c)
           d-bus (.control-bus performance :d)
           e-bus (.control-bus performance :e)
           f-bus (.control-bus performance :f)
           g-bus (.control-bus performance :g)
           h-bus (.control-bus performance :h)
           env1-bus (.control-bus performance :env1)
           env2-bus (.control-bus performance :env2)
           env3-bus (.control-bus performance :env3)
           lfo1-bus (.control-bus performance :lfo1)
           lfo2-bus (.control-bus performance :lfo2)
           lfo3-bus (.control-bus performance :lfo3)
           filter-in-bus (.audio-bus performance :filter-in)
           efx-in-bus (.audio-bus performance :efx-in)
           control-block (cadejo.instruments.alias.control/ControlBlock)
           head-block (sleep (cadejo.instruments.alias.head/AliasHead))
           filter1 (sleep (cadejo.instruments.alias.tone/ToneBlock1))
           filter2 (sleep (cadejo.instruments.alias.tone/ToneBlock2))
           efx-block (sleep (cadejo.instruments.alias.efx/EfxBlock))]
       (sleep)
       (ctl control-block  
            :pressure-bus pressure-bus
            :cca-bus cca-bus
            :ccb-bus ccb-bus
            :ccc-bus ccc-bus
            :ccd-bus ccd-bus
            :env1-bus env1-bus
            :env2-bus env2-bus
            :env3-bus env3-bus
            :lfo1-bus lfo1-bus
            :lfo2-bus lfo2-bus
            :lfo3-bus lfo3-bus
            :a-bus a-bus
            :b-bus b-bus
            :c-bus c-bus
            :d-bus d-bus
            :e-bus e-bus
            :f-bus f-bus
            :g-bus g-bus
            :h-bus h-bus)
       (sleep)
       (ctl head-block 
            :bend-bus bend-bus
            :a-bus a-bus
            :b-bus b-bus
            :c-bus c-bus
            :d-bus d-bus
            :e-bus e-bus
            :f-bus f-bus
            :g-bus g-bus
            :h-bus h-bus
            :out-bus filter-in-bus)
       (sleep)
       (ctl filter1 
            :a-bus a-bus
            :b-bus b-bus
            :c-bus c-bus
            :d-bus d-bus
            :e-bus e-bus
            :f-bus f-bus
            :g-bus g-bus
            :h-bus h-bus
            :env3-bus env3-bus
            :in-bus filter-in-bus
            :out-bus efx-in-bus)
       (sleep)
       (ctl filter2
            :a-bus a-bus
            :b-bus b-bus
            :c-bus c-bus
            :d-bus d-bus
            :e-bus e-bus
            :f-bus f-bus
            :g-bus g-bus
            :h-bus h-bus
            :env3-bus env3-bus
            :in-bus filter-in-bus
            :out-bus efx-in-bus)
       (sleep)
       (ctl efx-block 
            :a-bus a-bus
            :b-bus b-bus
            :c-bus c-bus
            :d-bus d-bus
            :e-bus e-bus
            :f-bus f-bus
            :g-bus g-bus
            :h-bus h-bus
            :cc-volume-bus volume-bus
            :in-bus efx-in-bus
            :out-bus main-out)
       (sleep)
       (ctl head-block :mute-amp 1)
       (.add-synth! performance :control control-block)
       (.add-synth! performance :filter1 filter1)
       (.add-synth! performance :filter2 filter2)
       (.add-synth! performance :efx efx-block)
       (.add-voice! performance head-block)
       (.reset chanobj)
       performance)))

(defn alias-exclusive [scene chan id  & {:keys [cca ccb ccc ccd cc7 main-out]
                                    :or {cca 1
                                         ccb 16
                                         ccc 17
                                         ccd 4
                                         cc7 7
                                         main-out 0}}]
  (let [km (cadejo.midi.mono-exclusive/mono-exclusive-keymode :Alias)]
    (alias-instance scene chan id km 
                    :cca cca :ccb ccb :ccc ccc :ccd ccd
                    :cc7 cc7 :main-out main-out)))

(defn alias-mono [scene chan id  & {:keys [cca ccb ccc ccd cc7 main-out]
                                    :or {cca 1
                                         ccb 16
                                         ccc 17
                                         ccd 4
                                         cc7 7
                                         main-out 0}}]
  (let [km (cadejo.midi.mono-mode/mono-keymode :Alias)]
    (alias-instance scene chan id km 
                    :cca cca :ccb ccb :ccc ccc :ccd ccd
                    :cc7 cc7 :main-out main-out)))


(.add-constructor! alias-descriptor :exclusize alias-exclusive)
(.add-constructor! alias-descriptor :mono alias-mono)
