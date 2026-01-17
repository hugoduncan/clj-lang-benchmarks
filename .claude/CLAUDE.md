# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Purpose

Benchmarking Clojure implementations using criterium, with results published as Clay notebooks.

## Common Commands

```bash
# Start REPL with warnings enabled
clj -M:dev

# Start nREPL (for editor integration)
clj -M:nrepl

# Build notebooks to docs/
clj -T:build notebooks

# Publish to gh-pages branch (builds + pushes)
clj -T:build publish

# Check formatting
cljfmt check src dev notebooks build.clj

# Lint
clj-kondo --lint src dev notebooks build.clj
```

## Clojure Style

- Enable `*warn-on-reflection*` and `*unchecked-math* :boxed` when developing (auto-enabled in `:dev` alias)
- Type hint where necessary to avoid reflection
- Run benchmarks with criterium's `bench` or `quick-bench`

## Benchmark Structure

Each benchmark is a Clay notebook in `notebooks/` that:

1. Defines the code being benchmarked
2. Explains what is being measured and why
3. Runs criterium benchmarks
4. Displays results

Example structure:

```clojure
(ns notebooks.example-benchmark
  {:clay {:title "My Benchmark Title"}}
  (:require [criterium.core :as criterium]))

;; ## What we're benchmarking
;; Explanation of the benchmark purpose

;; ## Implementation
(defn implementation-1 [x] ...)
(defn implementation-2 [x] ...)

;; ## Benchmarks
(criterium/bench (implementation-1 test-data))
(criterium/bench (implementation-2 test-data))

;; ## Analysis
;; Discussion of results
```

## Clay Notebook Conventions

- Notebooks live in `notebooks/` with single-segment namespaces (e.g., `index`, `map-accessors`)
- Use `:clay {:title "..."}` in ns metadata for display title
- Index notebook (`notebooks/index.clj`) auto-discovers and links to all benchmarks
- Output goes to `docs/` for GitHub Pages (gitignored)

## Architecture Notes

- `build.clj` dynamically discovers notebooks via `notebook-files` function
- The `:build` alias includes JVM options for criterium blackhole optimization
- GitHub Actions runs check (lint/format) and publish workflows on push to master
