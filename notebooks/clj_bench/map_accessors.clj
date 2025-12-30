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
;; Clojure uses PersistentArrayMap for small maps (≤8 keys) and
;; PersistentHashMap for larger maps (>8 keys). We test both.

(defrecord Point [x y z])

(def array-map-3 {:x 1 :y 2 :z 3})
(def hash-map-10 (assoc (into {} (map #(vector (keyword (str (char (+ 97 %)))) %)
                                      (range 9)))
                        :x 9))
(def test-record (->Point 1 2 3))
(def test-record-ext (assoc test-record :w 4))

;; ## Array Map Access (3 keys)
;;
;; PersistentArrayMap uses linear scan - fast for small maps.

(defn map-destructure [m]
  (let [{:keys [x]} m] x))

(domain/bench
 (domain/domain-expr
  [test-map [array-map-3]]
  {:get                    (get test-map :x)
   :get-with-default       (get test-map :x 0)
   :keyword-access         (:x test-map)
   :keyword-with-default   (:x test-map 0)
   :map-as-fn              (test-map :x)
   :map-as-fn-with-default (test-map :x 0)
   :destructuring          (map-destructure test-map)})
 :domain-plan domain-plans/implementation-comparison)

;; ## Hash Map Access (10 keys)
;;
;; PersistentHashMap uses HAMT - O(log32 n) lookup.

(domain/bench
 (domain/domain-expr
  [test-map [hash-map-10]]
  {:get                    (get test-map :x)
   :get-with-default       (get test-map :x 0)
   :keyword-access         (:x test-map)
   :keyword-with-default   (:x test-map 0)
   :map-as-fn              (test-map :x)
   :map-as-fn-with-default (test-map :x 0)
   :destructuring          (map-destructure test-map)})
 :domain-plan domain-plans/implementation-comparison)

;; ## Record Access Patterns
;;
;; Five ways to access a value from a defrecord (records don't implement IFn):

(defn record-destructure [r]
  (let [{:keys [x]} r] x))

(domain/bench
 (domain/domain-expr
  [test-rec [test-record]]
  {:keyword-access    (:x test-rec)
   :get               (get test-rec :x)
   :destructuring     (record-destructure test-rec)
   :get-with-default  (get test-rec :x 0)
   :field-access      (.x ^Point test-rec)})
 :domain-plan domain-plans/implementation-comparison)

;; ## Record Extension Map Access
;;
;; Accessing keys not defined in the record (stored in __extmap).

(defn record-ext-destructure [r]
  (let [{:keys [w]} r] w))

(domain/bench
 (domain/domain-expr
  [test-rec [test-record-ext]]
  {:keyword-access    (:w test-rec)
   :get               (get test-rec :w)
   :destructuring     (record-ext-destructure test-rec)
   :get-with-default  (get test-rec :w 0)})
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
