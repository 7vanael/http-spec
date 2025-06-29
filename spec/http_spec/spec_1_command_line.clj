(ns http-spec.spec-1-command-line
  (:require
    [clojure.java.shell :as sh]
    [http-spec.spec-helper :as helper]
    [speclj.core :refer :all]
    [clojure.string :as str]
    [clojure.java.io :as io]))

;; The HTTP server behaves like a traditional command line tool with options.
;;
;; e.g. The command name to start the server is 'my-http-server', the -help option should print the following, perhaps
;;   with additional options:
;;
;; Usage: my-http-server [options]
;;   -p     Specify the port.  Default is 80.
;;   -r     Specify the root directory.  Default is the current working directory.
;;   -h     Print this help message
;;   -x     Print the startup configuration without starting the server
;;
;; Upon startup, the server should print it's startup configuration
;;
;; <server name>
;; Running on port: <port>
;; Serving files from: <dir>

(defn output-from-server [& args]
  (let [result (apply helper/run-server args)]
    (should= "" (:err result))
    (should= 0 (:exit result))
    (:out result)))

(describe "Command Line"

  (it "-h prints usage without starting server"
    (let [output (output-from-server "-h")]
      ;(should-contain (str "Usage: " (:cmd (helper/config)) " [options]") output)
      (should-contain "  -p     Specify the port.  Default is 80." output)
      (should-contain "  -r     Specify the root directory.  Default is the current working directory." output)
      (should-contain "  -h     Print this help message" output)
      (should-contain "  -x     Print the startup configuration without starting the server" output)))

  (it "-x argument prints configuration without starting server"
    (let [output (output-from-server "-x")]
      (should-contain "Challenge Server" output)
      (should-contain "Running on port: 80" output)
      (should-contain (str "Serving files from: " (.getCanonicalPath (io/file "."))) output)))

  (it "default port is 80"
    (should-contain "Running on port: 80" (output-from-server "-x")))

  (it "default root is current directory"
    (should-contain (str "Serving files from: " (.getCanonicalPath (io/file "."))) (output-from-server "-x")))

  (it "port can be set using the -p argument"
    (should-contain "Running on port: 1234" (output-from-server "-x" "-p" "1234")))

  (it "root can be set using the -r argument"
    (should-contain (str "Serving files from: " (.getCanonicalPath (io/file "testroot")))
                    (output-from-server "-x" "-r" "testroot")))

  )
