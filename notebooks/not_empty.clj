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

;; Comparing performance of different approaches to check if a collection is
;; not empty.
;; Two common approaches:
;; - `(seq x)` - idiomatic Clojure, returns nil or first element as truthy value
;; - `(not (empty? x))` - explicit boolean check

;; ## Environment

*clojure-version*

(jvm/runtime-details)

(jvm/os-details)

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

(kind/hidden
 (bench/set-default-viewer! :print))
