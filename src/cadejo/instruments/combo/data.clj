(ns cadejo.instruments.combo.data
  (:use [cadejo.instruments.combo.program 
         :only [bank combo save-program bypass low high band notch]]))

(save-program 0 "Alpha" "Remarks for program 0"
    (combo :a1      1.000  :w1 0.000 
           :a2      0.000  :w2 0.000
           :a3      0.000  :w3 0.000
           :a4      0.000  :w4 0.000
           :chorus  0.000
           :vibrato [:freq 5.000 :sens 0.010]
           :filter  [:freq 8     :type bypass]
           :flanger [:rate 1.000 :depth 0.100 :fb +0.50 :mix 0.000]
           :reverb  0.000  :amp 0.400))

(save-program 1 "Beta"
   (combo :a1      1.000  :w1 0.000
          :a2      0.800  :w2 0.000
          :a3      0.000  :w3 0.000
          :a4      0.000  :w4 0.000
          :chorus  0.000
          :vibrato [:freq 5.000 :sens 0.010]
          :filter  [:freq  8    :type bypass]
          :flanger [:rate 0.250 :depth 0.250 :fb +0.50 :mix 0.000]
          :reverb  0.000  :amp 0.200))

(save-program 2 "Gamma"
   (combo :a1      0.800  :w1 0.000
          :a2      0.000  :w2 0.000
          :a3      1.000  :w3 0.100
          :a4      0.000  :w4 0.000
          :chorus  0.010
          :vibrato [:freq 5.000 :sens 0.010]
          :filter  [:freq  8    :type low]
          :flanger [:rate 0.250 :depth 0.250 :fb +0.50 :mix 0.300]
          :reverb  0.500  :amp 0.300))

(save-program 3 "Delta"
    (combo :a1      0.950  :w1 0.774
           :a2      0.350  :w2 0.722
           :a3      0.450  :w3 1.000
           :a4      0.450  :w4 0.967
           :chorus  0.000
           :vibrato [:freq 6.172 :sens 0.010]
           :filter  [:freq  6    :type    low]
           :flanger [:rate 0.219 :depth 0.250 :fb -0.60 :mix 0.200]
           :reverb  0.600  :amp 0.500))

(save-program 4 "Epsilon"
    (combo :a1      0.643  :w1 0.378
           :a2      0.859  :w2 0.950
           :a3      1.000  :w3 0.614
           :a4      0.420  :w4 0.620
           :chorus  0.618
           :vibrato [:freq 3.117 :sens 0.010]
           :filter  [:freq  4    :type bypass]
           :flanger [:rate 0.294 :depth 0.319 :fb -0.04 :mix 0.746]
           :reverb  0.000  :amp 0.200))

(save-program 5 "Zeta"
    (combo :a1      0.755  :w1 0.381
           :a2      0.904  :w2 0.406
           :a3      0.122  :w3 0.005
           :a4      1.000  :w4 0.297
           :chorus  0.010
           :vibrato [:freq 5.675 :sens 0.010]
           :filter  [:freq  6    :type    low]
           :flanger [:rate 0.100 :depth 0.239 :fb +0.69 :mix 0.000]
           :reverb  0.000  :amp 0.200))

(save-program 6 "Eta" 
    (combo :a1      0.143  :w1 0.413
           :a2      1.000  :w2 0.567
           :a3      0.624  :w3 0.765
           :a4      0.973  :w4 0.049
           :chorus  0.139
           :vibrato [:freq 5.806 :sens 0.010]
           :filter  [:freq  6    :type   band]
           :flanger [:rate 0.935 :depth 0.101 :fb -0.96 :mix 0.000]
           :reverb  0.000  :amp 0.200))

(save-program 7 "Theta"
    (combo :a1      1.000  :w1 0.370
           :a2      0.864  :w2 0.537
           :a3      0.100  :w3 0.324
           :a4      0.000  :w4 0.000
           :chorus  0.990
           :vibrato [:freq 3.949 :sens 0.010]
           :filter  [:freq  6    :type  notch]
           :flanger [:rate 0.049 :depth 0.305 :fb +0.76 :mix 0.000]
           :reverb  0.000  :amp 0.200))

(save-program 8 "Iota"
    (combo :a1      1.000  :w1  0.653
           :a2      0.883  :w2 0.982
           :a3      0.866  :w3 0.370
           :a4      0.437  :w4 0.184
           :chorus  0.639
           :vibrato [:freq 4.736 :sens 0.010]
           :filter  [:freq  4    :type bypass]
           :flanger [:rate 0.100 :depth 0.173 :fb -0.59 :mix 0.275]
           :reverb  0.834  :amp 0.200))

(save-program 9 "Kappa"
    (combo :a1      0.907  :w1 0.381
           :a2      1.000  :w2 0.258
           :a3      0.761  :w3 0.502
           :a4      0.970  :w4 0.678
           :chorus  0.000
           :vibrato [:freq 5.438 :sens 0.010]
           :filter  [:freq  8    :type  notch]
           :flanger [:rate 0.499 :depth 0.139 :fb +0.63 :mix 0.000]
           :reverb  0.000  :amp 0.200))

(save-program 10 "Lambda"
    (combo :a1      0.000  :w1 0.386
           :a2      0.000  :w2 0.905
           :a3      1.000  :w3 0.300
           :a4      1.000  :w4 0.300
           :chorus  0.000
           :vibrato [:freq 3.407 :sens 0.010]
           :filter  [:freq  6    :type bypass]
           :flanger [:rate 0.393 :depth 0.735 :fb +0.14 :mix 0.000]
           :reverb  0.000  :amp 0.200))

(save-program 11 "Mu"
    (combo :a1      0.200  :w1  0.621
           :a2      1.000  :w2 0.576
           :a3      0.220  :w3 0.362
           :a4      0.678  :w4 0.077
           :chorus  0.001
           :vibrato [:freq 4.833 :sens 0.010]
           :filter  [:freq  8    :type  notch]
           :flanger [:rate 0.100 :depth 0.937 :fb +0.62 :mix 0.100]
           :reverb  1.000  :amp 0.400))

(save-program 12 "Nu"
    (combo :a1      1.000  :w1 0.911
           :a2      0.513  :w2 0.697
           :a3      0.434  :w3 0.189
           :a4      0.650  :w4 0.300
           :chorus  0.000
           :vibrato [:freq 5.619 :sens 0.010]
           :filter  [:freq  8    :type bypass]
           :flanger [:rate 0.403 :depth 0.226 :fb +0.27 :mix 0.915]
           :reverb  0.000  :amp 0.200))

(save-program 13 "Xi" ""
    (combo :a1      0.551  :w1 0.322
           :a2      0.494  :w2 0.184
           :a3      1.000  :w3 0.180
           :a4      0.994  :w4 0.168
           :chorus  0.010
           :vibrato [:freq 5.256 :sens 0.010]
           :filter  [:freq  8    :type  notch]
           :flanger [:rate 0.483 :depth 0.500 :fb -0.78 :mix 0.000]
           :reverb  0.000  :amp 0.200))

(save-program 14 "Omicron" ""
    (combo :a1      1.000  :w1 0.000
           :a2      0.000  :w2 0.740
           :a3      0.000  :w3 0.900
           :a4      1.000  :w4 0.100
           :chorus  0.010
           :vibrato [:freq 5.000 :sens 0.010]
           :filter  [:freq  4    :type  notch]
           :flanger [:rate 4.973 :depth 0.418 :fb +0.77 :mix 0.000]
           :reverb  0.000  :amp 0.200))
