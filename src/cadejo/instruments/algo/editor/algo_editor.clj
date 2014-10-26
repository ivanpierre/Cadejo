(ns cadejo.instruments.algo.editor.algo-editor
  (:require [cadejo.config :as config])
  (:require [cadejo.util.user-message :as umsg])
  (:require [cadejo.ui.util.factory :as factory])
  (:require [cadejo.ui.util.lnf :as lnf])
  (:require [cadejo.ui.instruments.instrument-editor :as ied])
  (:require [cadejo.ui.instruments.subedit])
  (:require [cadejo.instruments.algo.editor.overview :as overview])
  (:require [cadejo.instruments.algo.editor.env-editor :as enved])
  (:require [cadejo.instruments.algo.editor.freq-editor :as freqed])
  (:require [cadejo.instruments.algo.editor.amp-editor :as amped])
  (:require [cadejo.instruments.algo.editor.fx-editor :as fxed])
  (:require [seesaw.core :as ss]))  

(def ^:private icon-style (config/icon-style))
(def ^:private fm-icon (lnf/read-icon :wave :fm))
(def ^:private am-icon (lnf/read-icon :wave :am))
(def ^:private env-icon (lnf/read-icon :env :adsr))
(def ^:private fx-icon (lnf/read-icon :general :fx))

(defn algo-editor [performance]
  (let [ied (ied/instrument-editor performance)
        faed (freqed/freq-editor performance ied)
        enved (enved/envelope-editor performance ied)
        amped (amped/amp-editor performance ied)
        fxed (fxed/fx-editor performance ied)]
    (.add-sub-editor! ied "FM" fm-icon faed)
    (.add-sub-editor! ied "Env" env-icon enved)
    (.add-sub-editor! ied "AM" am-icon amped)
    (.add-sub-editor! ied "Fx" fx-icon fxed)
    ied))
