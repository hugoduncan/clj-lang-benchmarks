(ns build
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.tools.build.api :as b]
            [scicloj.clay.v2.api :as clay]))

(defn- notebook-files []
  (->> (io/file "notebooks")
       file-seq
       (filter #(.isFile %))
       (filter #(.endsWith (.getName %) ".clj"))
       (mapv #(.getPath %))))

(defn- load-clay-config []
  (-> (edn/read-string (slurp "clay.edn"))
      (assoc :source-path (notebook-files))))

(defn- notebooks* []
  (let [config (load-clay-config)]
    (clay/make! config)
    (shutdown-agents)))

(defn notebooks
  "Build Clay notebooks to docs/."
  [_]
  (notebooks*)
  (System/exit 0))

(defn- run-process!
  "Run a shell command, throwing on non-zero exit."
  [command-args]
  (let [{:keys [exit]} (b/process {:command-args command-args})]
    (when-not (zero? exit)
      (throw (ex-info (str "Command failed: " (pr-str command-args))
                      {:command-args command-args :exit exit})))))

(defn publish
  "Publish docs/ to GitHub Pages (manual).
   Builds notebooks and commits docs/ for GitHub Pages.
   Assumes GitHub Pages is configured to serve from docs/ on main branch."
  [_]
  (notebooks*)
  (run-process! ["git" "add" "docs"])
  (run-process! ["git" "commit" "-m" "Publish notebooks"])
  (println "Committed docs/. Push to publish via GitHub Pages."))
