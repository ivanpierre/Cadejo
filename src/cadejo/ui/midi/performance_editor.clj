(println "--> cadejo.ui.midi.performance-editor")

(ns cadejo.ui.midi.performance-editor
  (:require [cadejo.config :as config])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.midi.bank-editor])
  (:require [cadejo.ui.midi.cceditor-tab])
  (:require [cadejo.ui.midi.node-editor])
  (:require [cadejo.ui.midi.properties-editor])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [seesaw.core :as ss])
  (:import java.awt.BorderLayout
           java.awt.event.WindowListener
           javax.swing.SwingUtilities))

(def frame-size [1280 :by 587])

(defprotocol PerformanceEditor

   (widgets 
    [this])

  (widget
    [this key])
  
  (node 
    [this])

  (working
    [this flag])

  (status!
    [this msg])

  (warning!
    [this msg])

  (frame 
    [this])

  (show-channel
   [this])
    
  (sync-ui!
    [this]))

(defn performance-editor [performance]
  (let [descriptor (.get-property performance :descriptor)
        basic-ed (let [bed (cadejo.ui.midi.node-editor/basic-node-editor :performance performance)
                       logo (.logo descriptor :small)
                       iname (.instrument-name descriptor)
                       id (.get-property performance :id)]
                   (.set-icon! bed logo)
                   (ss/config! (.widget bed :frame) :title (name id))
                   (ss/config! (.widget bed :lab-id) :text (name id))
                   bed)
        instrument-editor* (atom nil) ;; editor created only if needed
        toolbar (.widget basic-ed :toolbar)
        bank (.bank performance)
        bank-ed (let [bed (cadejo.ui.midi.bank-editor/bank-editor bank)]
                  (.editor! bank bed)
                  bed)
        properties-editor (cadejo.ui.midi.properties-editor/properties-editor)
        card-buttons* (atom [])
        card-group (ss/button-group)
        pan-cards (ss/card-panel :items [[(.widget bank-ed :pan-main) "BANK"]
                                         [(.widget properties-editor :pan-main) "MIDI"]])
        pan-center (let [panc (.widget basic-ed :pan-center)]
                     (.add panc pan-cards)
                     panc)
        
        available-controllers (.controllers descriptor)
        cc-panels* (atom [])]
    
    (let [b (factory/button "Transmit" :midi :transmit "Transmit current program")]
      (.add toolbar b)
      (ss/listen b :action (fn [_]
                             (let [slot (.current-slot bank)]
                               (if slot 
                                 (do
                                   (.program-change bank {:data1 slot})
                                   ))))))
    (let [b (factory/toggle "Bank" :general :bank "Display program bank" card-group)]
      (.putClientProperty b :card "BANK")
      (ss/config! b :selected? true)
      (swap! card-buttons* (fn [n](conj n b)))
      (.add toolbar b))
    (let [b (factory/toggle "MIDI" :midi :plug "Display MIDI properties" card-group)]
      (.putClientProperty b :card "MIDI")
      (swap! card-buttons* (fn [n](conj n b)))
      (.add toolbar b))
    ;; Add CTRL properties panels
    (doseq [i (range 0 (count available-controllers) 4)]
      (let [cced (cadejo.ui.midi.cceditor-tab/cceditor-tab descriptor i)
            card-id (format "CTRL-%d" i)
            jb (factory/toggle card-id :midi :ctrl "Display MIDI controller properties" card-group)]
        (.putClientProperty jb :card card-id)
        (.add pan-cards (.widget cced :pan-main) card-id)
        (swap! card-buttons* (fn [n](conj n jb)))
        (.add toolbar jb)))
    (let [b (factory/toggle "Edit" :edit nil "Edit current program" card-group)]
      (.putClientProperty b :card "EDIT")
      (ss/listen b :action (fn [_]
                             (if (not @instrument-editor*)
                               (do (.working basic-ed true)
                                   (.status! basic-ed "Creating editor")
                                   (SwingUtilities/invokeLater
                                    (proxy [Runnable][]
                                      (run []
                                        (let [ied (.create-editor descriptor performance)]
                                          (reset! instrument-editor* ied)
                                          (.add pan-cards (.widget ied :pan-main) "EDIT"))
                                        (.working basic-ed false)
                                        (.status! basic-ed "")
                                        (ss/show-card! pan-cards "EDIT")))))
                               (try
                                 (ss/show-card! pan-cards "EDIT")
                                 (catch NullPointerException ex
                                   (.warning! basic-ed "Editor not defined"))))))
      (.add toolbar b))
    (ss/config! (.widget basic-ed :frame) :on-close :hide)
    (let [ped (reify PerformanceEditor
                 (widgets [this] (.widgets basic-ed))

                 (widget [this key]
                   (or (.widget basic-ed key)
                      (umsg/warning (format "PerformanceEditor does not have %s widget" key))))

                 (node [this] (.node basic-ed))
                
                 (working [this flag]
                   (.working basic-ed flag))

                 (status! [this msg]
                   (.status! basic-ed msg))
                 
                 (warning! [this msg]
                   (.warning! basic-ed msg))
                 
                 (frame [this]
                   (.widget this :frame))

                 (show-channel [this]
                   (let [chanobj (.parent performance)
                         ced (.get-editor chanobj)
                         cframe (.frame ced)]
                     (ss/show! cframe)
                     (.toFront cframe)))
                 
                 (sync-ui! [this]
                   (.sync-ui! bank-ed)
                   (.sync-ui! properties-editor)
                   (doseq [cced @cc-panels*]
                     (.sync-ui! cced))))]
      (.set-parent-editor! properties-editor ped)
      (.set-parent-editor! bank-ed ped)
      (.put-property! performance :bank-editor bank-ed)
      (doseq [cced @cc-panels*]
        (.set-parent-editor! cced ped))
      (ss/config! (.frame ped) :size frame-size)
      (ss/listen (.widget ped :jb-parent)
                 :action (fn [_]
                           (let [chanobj (.parent performance)
                                 ced (.get-editor chanobj)
                                 cframe (.frame ced)]
                             (ss/show! cframe)
                             (.toFront cframe))))
      (.addWindowListener (.widget ped :frame)
                          (proxy [WindowListener][]
                            (windowClosed [_] nil)
                            (windowClosing [_] nil)
                            (windowDeactivated [_] nil)
                            (windowIconified [_] nil)
                            (windowDeiconified [_] (.sync-ui! ped))
                            (windowActivated [_] 
                              (.sync-ui! ped))
                            (windowOpened [_] nil)))
      (.set-path-text! basic-ed (let [scene (.get-scene performance)
                                  chanobj (.parent performance)
                                  sid (.get-property scene :id)
                                  cid (.get-property chanobj :id)
                                  pid (.get-property performance :id)]
                              (format "Root / %s / chan %s / %s"
                                      sid cid (name pid))))
      (doseq [b @card-buttons*]
        (ss/listen b :action (fn [ev]
                               (let [src (.getSource ev)
                                     card-id (.getClientProperty src :card)]
                                 (println (format "DEBUG card-id = '%s'" card-id)) ;; DEBUG
                                 (ss/show-card! pan-cards card-id)))))
      (.putClientProperty (.widget basic-ed :jb-help) :topic :performance)
      ped))) 
