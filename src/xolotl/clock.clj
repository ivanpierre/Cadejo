(println "    --> xolotl.clock")
(ns xolotl.clock
  (:require [xolotl.counter :as counter])
  (:require [xolotl.timebase :as timebase])
  (:require [xolotl.cycle]))

(defprotocol Clock

  (enable! [this flag])
  
  (clock-select! [this mode])   ;; mode :internal :external

  (advance [this])   ;; Used by external timebase to step clock
  
  (tempo! [this bpm])  ;; Do not call directly,
                       ;; Use global timebase/set-tempo function

  (input-channel! [this c0])

  (enable-reset-on-first-key! [this flag])

  (enable-key-track! [this flag])

  (enable-key-gate! [this flag])
  
  (transpose! [this x])

  (repeat! [this n])

  (jump! [this n])
  
  (midi-reset [this]) ;; calls reset function

  (reset-function! [this rfn])

  (controller-function! [this cfn])

  (pitch-function! [this pfn])

  (program-function! [this pfn])

  (rhythm-pattern! [this pat])

  (rhythm-pattern [this])
  
  (hold-pattern! [this pat])
  
  (event-dispatcher [this])

  (step [this])

  (current-rhythm-value [this])

  (current-hold-value [this])
  
  (dump-state [this]))



;; rsfn []
;; cfn []
;; pfn [gate keynumber hold-time]
;; progfn [program-number]
;;
(defn clock [id rsfn cfn pfn progfn]
  (let [enabled* (atom true)
        clock-mode* (atom :internal)
        tempo* (atom 60)
        input-channel* (atom 15)
        reset-on-key* (atom false)
        key-track-scale* (atom 1)  ;; 0 (disabled) or 1 (enabled)
        key-gate-mode* (atom false)
        transpose* (atom 0)
        phrase-counter* (atom -1)
        repeat* (atom -1)
        jump* (atom -1)
        reset-function* (atom rsfn)
        controller-function* (atom cfn)
        pitch-function* (atom pfn)
        program-function* (atom progfn)
        rhythm-pattern (xolotl.cycle/cycle [24])
        rhythm-counter (counter/counter 24)
        hold-pattern (xolotl.cycle/cycle [1.0])
        key-counter* (atom 0)
        last-keynum* (atom 60)
        key-up (fn [event]
                 (let [kn (:data1 event)]
                   (swap! key-counter* (fn [q](max 0 (dec q))))))
        key-down (fn [event]
                   (let [kn (:data1 event)
                         vl (:data2 event)]
                     (if (zero? vl)
                       (key-up event)
                       (do
                         (reset! last-keynum* kn)
                         (swap! key-counter* inc)
                         (if (and @reset-on-key*
                                  (= 1 @key-counter*))
                           (@reset-function*))))))
                                  
        clkobj (reify Clock

                 (enable! [this flag]
                   (reset! enabled* flag))
                 
                 (clock-select! [this mode]
                   (reset! clock-mode* mode)
                   (cond (= mode :internal)
                         (timebase/sync-clock this)

                         (= mode :external)
                         (timebase/free-clock this)

                         :else
                         (timebase/sync-clock)))

                 (advance [this]
                   (if @enabled*
                     (do
                       (.step rhythm-counter)
                       (if (and (zero? (swap! phrase-counter* dec)) (>=  @jump* 0))
                         (let [event {:command :program-change
                                      :channel @input-channel*
                                      :data @jump*}]
                           (@program-function* @jump*)
                           (.midi-reset this))))))
                 
                 (tempo! [this bpm]
                   (reset! tempo* (float bpm)))
                 
                 (input-channel! [this c0]
                   (reset! input-channel* c0))
                 
                 (enable-reset-on-first-key! [this flag]
                   (reset! reset-on-key* flag))
                 
                 (enable-key-track! [this flag]
                   (reset! key-track-scale* (if flag 1 0))
                   flag)

                 (enable-key-gate! [this flag]
                   (reset! key-gate-mode* flag))
                 
                 (transpose! [this x]
                   (reset! transpose* (int x)))

                 (repeat! [this n]
                   (reset! repeat* (int n))
                   (reset! phrase-counter* (int n)))

                 (jump! [this n]
                   (reset! jump* (int n)))
                 
                 (midi-reset [this]
                   (reset! key-counter* 0)
                   (reset! last-keynum* 60)
                   (.midi-reset rhythm-pattern)
                   (.midi-reset hold-pattern)
                   (reset! phrase-counter* (* @repeat* (.sum rhythm-pattern)))
                   (@reset-function*))
                 
                 (reset-function! [this rfn]
                   (reset! reset-function* rfn))
                 
                 (controller-function! [this cfn]
                   (reset! controller-function* cfn))
                 
                 (pitch-function! [this pfn]
                   (reset! pitch-function* pfn))
                 
                 (program-function! [this pfn]
                   (reset! program-function* pfn))
                 
                 (rhythm-pattern! [this pat]
                   (.values! rhythm-pattern pat))

                 (rhythm-pattern [this]
                   (.values rhythm-pattern)
                   (reset! phrase-counter* (* @repeat* (.period rhythm-pattern))))
                 
                 (hold-pattern! [this pat]
                   (.values! hold-pattern pat))

                 (step [this]
                   (let [p (.next rhythm-pattern)
                         ht (/ (* 5.0 p (.next hold-pattern))(* 2 @tempo*))
                         kn (+ @transpose* (* @key-track-scale* @last-keynum*))
                         gate true]
                     (.period! rhythm-counter p)
                     (@controller-function*)
                     (@pitch-function* gate kn ht)
                     (swap! phrase-counter* dec)
                     (if (zero? @phrase-counter*)
                       (let [prognum @jump*
                             event {:command :program-change
                                    :channel @input-channel*
                                    :data prognum}]
                         (if (>= prognum 0)
                           (do 
                             (@program-function* prognum)
                           ))))))

                 (current-rhythm-value [this]
                   (.value rhythm-pattern))

                 (current-hold-value [this]
                   (.value hold-pattern))
                 
                 (dump-state [this]
                   (let [pad "  "
                         pad2 (str pad pad)
                         sb (StringBuilder.)]
                     (.append sb (format "%sClock\n" pad))
                     (.append sb (format "%senabled         -> %s\n" pad2 @enabled*))
                     (.append sb (format "%sclock-mode      -> %s\n" pad2 @clock-mode*))
                     (.append sb (format "%stempo           -> %s\n" pad2 @tempo*))
                     (.append sb (format "%sinput-channel   -> %s\n" pad2 @input-channel*))
                     (.append sb (format "%sreset-on-key    -> %s\n" pad2 @reset-on-key*))
                     (.append sb (format "%skey-track-scale -> %s\n" pad2 @key-track-scale*))
                     (.append sb (format "%skey-gate-mode   -> %s\n" pad2 @key-gate-mode*))
                     (.append sb (format "%stranspose       -> %s\n" pad2 @transpose*))
                     (.append sb (format "%srhythm-pattern  -> %s\n" pad2 (.values rhythm-pattern)))
                     (.append sb (format "%shold-pattern    -> %s\n" pad2 (.values hold-pattern)))
                     (.append sb (format "%s[key-counter   ]-> %s\n" pad2 @key-counter*))
                     (.append sb (format "%s[last-keynum   ]-> %s\n" pad2 @last-keynum*))
                     (.toString sb)))
                 
                 (event-dispatcher [this]
                   (fn [event]
                     (let [cmd (:command event)]
                       (cond (= cmd :clock)
                             (if (= @clock-mode* :external)
                               (do 
                                 (.step rhythm-counter)
                                 ))
                             
                             (= cmd :reset)
                             (@reset-function*)

                             (= (:channel event) @input-channel*)
                             (cond (= cmd :note-on)
                                   (key-down event)

                                   (= cmd :note-off)
                                   (key-up event)

                                   (= cmd :program-change)
                                   (@program-function* (:data1 event))

                                   :else
                                   nil)
                             :else
                             nil)))) )]
    (.action! rhythm-counter
              (fn []
                (let [p (.next rhythm-pattern)
                      ht (/ (* 5.0 p (.next hold-pattern))(* 2 @tempo*))
                      kn (+ @transpose* (* @key-track-scale* @last-keynum*))
                      gate (if @key-gate-mode*
                             (pos? @key-counter*)
                             true)]
                  (.period! rhythm-counter p)
                  (@controller-function*)
                  (@pitch-function* gate kn ht))))
    (.clock-select! clkobj :internal)
    clkobj))
