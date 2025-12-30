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
  [_ [1]]
  {:get                    (get array-map-3 :x)
   :get-with-default       (get array-map-3 :x 0)
   :keyword-access         (:x array-map-3)
   :keyword-with-default   (:x array-map-3 0)
   :map-as-fn              (array-map-3 :x)
   :map-as-fn-with-default (array-map-3 :x 0)
   :destructuring          (map-destructure array-map-3)})
 :domain-plan domain-plans/implementation-comparison)

;; ## Hash Map Access (10 keys)
;;
;; PersistentHashMap uses HAMT - O(log32 n) lookup.

(domain/bench
 (domain/domain-expr
  [_ [1]]
  {:get                    (get hash-map-10 :x)
   :get-with-default       (get hash-map-10 :x 0)
   :keyword-access         (:x hash-map-10)
   :keyword-with-default   (:x hash-map-10 0)
   :map-as-fn              (hash-map-10 :x)
   :map-as-fn-with-default (hash-map-10 :x 0)
   :destructuring          (map-destructure hash-map-10)})
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

;; ## Record Extension Map Access
;;
;; Accessing keys not defined in the record (stored in __extmap).

(defn record-ext-destructure [r]
  (let [{:keys [w]} r] w))

(domain/bench
 (domain/domain-expr
  [_ [1]]
  {:keyword-access    (:w test-record-ext)
   :get               (get test-record-ext :w)
   :destructuring     (record-ext-destructure test-record-ext)
   :get-with-default  (get test-record-ext :w 0)})
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
