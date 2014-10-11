(defproject cadejo "0.1.2-SNAPSHOT"
  :description "MIDI management tool for Overtone"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/math.numeric-tower "0.0.2"]
                 [seesaw "1.4.4"]
                 [com.github.insubstantial/substance "7.1"]
                 [overtone "0.9.1"]]
  ;:main cadejo.instruments.algo.op-amp-editor
  ;:main cadejo.instruments.algo.mute-editor
  :main cadejo.gui
  ;:main sgwr.seven-segment-display
  ;:main sgwr.utilities
)
