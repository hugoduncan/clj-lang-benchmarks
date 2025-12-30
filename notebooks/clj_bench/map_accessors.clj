(ns clj-bench.map-accessors
  {:clay {:title "Map Accessor Benchmarks"}}
  (:require
   [criterium.bench :as bench]
   [criterium.domain :as domain]
   [criterium.domain-plans :as domain-plans]
   [scicloj.kindly.v4.kind :as kind]))

(kind/hidden
 (bench/set-default-viewer! :kindly))

;; # Map Accessor Benchmarks
;;
;; Comparing performance of different map and record access patterns in Clojure.

;; ## Test Data
;;
;; A simple 3-key map and defrecord with numeric values.

(defrecord Point [x y z])

(def test-map {:x 1 :y 2 :z 3})
(def test-record (->Point 1 2 3))

;; ## Map Access Patterns
;;
;; Five ways to access a value from a Clojure map:

(defn map-destructure [m]
  (let [{:keys [x]} m] x))

(domain/bench
 (domain/domain-expr
  [_ [1]]
  {:keyword-access    (:x test-map)
   :map-as-fn         (test-map :x)
   :get               (get test-map :x)
   :destructuring     (map-destructure test-map)
   :get-with-default  (get test-map :x 0)})
 :domain-plan domain-plans/implementation-comparison)

;; ## Record Access Patterns
;;
;; Five ways to access a value from a defrecord (records don't implement IFn):

(defn record-destructure [r]
  (let [{:keys [x]} r] x))

(domain/bench
 (domain/domain-expr
  [_ [1]]
  {:keyword-access    (:x test-record)
   :get               (get test-record :x)
   :destructuring     (record-destructure test-record)
   :get-with-default  (get test-record :x 0)
   :field-access      (.x ^Point test-record)})
 :domain-plan domain-plans/implementation-comparison)

;; ## Analysis
;;
;; Key observations:
;;
;; - **Maps**: Keyword-first `(:x m)` is idiomatic and well-optimized
;; - **Records**: Direct field access `(.x r)` is fastest due to direct JVM field read
;; - Destructuring has overhead from creating intermediate bindings
;; - `get` with default adds a nil check compared to plain `get`

(kind/hidden
 (bench/set-default-viewer! :print))
