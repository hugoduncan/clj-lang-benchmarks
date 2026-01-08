(ns build
  (:require
   [clojure.edn :as edn]
   [clojure.tools.build.api :as b]
   [scicloj.clay.v2.api :as clay]))

(defn- notebook-files
  "Discover all .clj files in the notebooks directory."
  []
  (->> (java.io.File. "notebooks")
       .listFiles
       (filter #(.isFile %))
       (map #(.getName %))
       (filter #(.endsWith % ".clj"))
       sort
       vec))

(defn- load-clay-config
  "Load clay.edn and override :source-path with discovered notebooks."
  []
  (-> (edn/read-string (slurp "clay.edn"))
      (assoc :source-path (notebook-files))))

(defn- notebooks* []
  (clay/make! (load-clay-config))
  (shutdown-agents))

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
  "Publish docs/ to gh-pages branch.
   Builds notebooks and pushes docs/ content to gh-pages branch."
  [_]
  (notebooks*)
  ;; Add .nojekyll to prevent GitHub from processing with Jekyll
  (spit "docs/.nojekyll" "")
  ;; Use git subtree to push docs/ to gh-pages branch
  (run-process! ["git" "add" "-f" "docs"])
  (run-process! ["git" "commit" "-m" "Build notebooks for publishing"])
  (run-process! ["git" "subtree" "split" "--prefix" "docs" "-b" "gh-pages-temp"])
  (run-process! ["git" "push" "-f" "origin" "gh-pages-temp:gh-pages"])
  (run-process! ["git" "branch" "-D" "gh-pages-temp"])
  ;; Clean up: reset the commit we made for subtree
  (run-process! ["git" "reset" "--soft" "HEAD~1"])
  (println "Published to gh-pages branch."))
