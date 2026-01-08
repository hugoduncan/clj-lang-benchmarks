(ns not-empty
  {:clay {:title "Not-Empty Predicate Benchmarks"}}
  (:require
   [criterium.bench :as bench]
   [criterium.domain :as domain]
   [criterium.domain-plans :as domain-plans]
   [criterium.jvm :as jvm]
   [scicloj.kindly.v4.kind :as kind]))

(kind/hidden
 (bench/set-default-viewer! :kindly))

;; # Not-Empty Predicate Benchmarks

;; ## Environment

*clojure-version*

(jvm/os-details)

;; Comparing performance of different approaches to check if a collection is
;; not empty.
;; Two common approaches:
;; - `(seq x)` - idiomatic Clojure, returns nil or first element as truthy value
;; - `(not (empty? x))` - explicit boolean check

;;; Non-empty Collections

;; ## Non-empty Vector
;;
;; Vectors are the most common collection type in Clojure.

(let [v [1 2 3]]
  (domain/bench
   (domain/domain-expr
    [coll [v]]
    {:seq       (seq coll)
     :not-empty (not (empty? coll))})
   :domain-plan domain-plans/implementation-comparison))

;; ## Non-empty List

(let [l '(1 2 3)]
  (domain/bench
   (domain/domain-expr
    [coll [l]]
    {:seq       (seq coll)
     :not-empty (not (empty? coll))})
   :domain-plan domain-plans/implementation-comparison))

;; ## Non-empty Set

(let [s #{1 2 3}]
  (domain/bench
   (domain/domain-expr
    [coll [s]]
    {:seq       (seq coll)
     :not-empty (not (empty? coll))})
   :domain-plan domain-plans/implementation-comparison))

;; ## Non-empty Map

(let [m {:a 1 :b 2}]
  (domain/bench
   (domain/domain-expr
    [coll [m]]
    {:seq       (seq coll)
     :not-empty (not (empty? coll))})
   :domain-plan domain-plans/implementation-comparison))

;; ## Non-empty Lazy Sequence
;;
;; Lazy sequences are created by many Clojure functions like `map`, `filter`,
;; and `range`.

(let [lazy-seq (range 3)]
  (domain/bench
   (domain/domain-expr
    [coll [lazy-seq]]
    {:seq       (seq coll)
     :not-empty (not (empty? coll))})
   :domain-plan domain-plans/implementation-comparison))

;;; Empty Collections

;; ## Empty Vector

(let [v []]
  (domain/bench
   (domain/domain-expr
    [coll [v]]
    {:seq       (seq coll)
     :not-empty (not (empty? coll))})
   :domain-plan domain-plans/implementation-comparison))

;; ## Empty List

(let [l '()]
  (domain/bench
   (domain/domain-expr
    [coll [l]]
    {:seq       (seq coll)
     :not-empty (not (empty? coll))})
   :domain-plan domain-plans/implementation-comparison))

;; ## Empty Set

(let [s #{}]
  (domain/bench
   (domain/domain-expr
    [coll [s]]
    {:seq       (seq coll)
     :not-empty (not (empty? coll))})
   :domain-plan domain-plans/implementation-comparison))

;; ## Empty Map

(let [m {}]
  (domain/bench
   (domain/domain-expr
    [coll [m]]
    {:seq       (seq coll)
     :not-empty (not (empty? coll))})
   :domain-plan domain-plans/implementation-comparison))

;; ## Empty Lazy Sequence

(let [lazy-seq (range 0)]
  (domain/bench
   (domain/domain-expr
    [coll [lazy-seq]]
    {:seq       (seq coll)
     :not-empty (not (empty? coll))})
   :domain-plan domain-plans/implementation-comparison))

;;; Analysis

;; ## Summary
;;
;; Both `(seq x)` and `(not (empty? x))` check emptiness, but they have different
;; characteristics:
;;
;; **`(seq x)`**:
;; - Returns nil for empty collections, or the seq of the collection
;; - Idiomatic Clojure - used directly in conditionals
;; - Single function call
;;
;; **`(not (empty? x))`**:
;; - Returns a boolean
;; - Two function calls: `empty?` then `not`

(kind/hidden
 (bench/set-default-viewer! :print))
