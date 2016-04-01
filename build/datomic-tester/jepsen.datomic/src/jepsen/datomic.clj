(ns jepsen.datomic
  (:require [clojure.tools.logging :refer :all]
            [datomic.api :only [q db] :as d]
            [jepsen
             [db :as db]
             [tests :as tests]
             [client :as client]
             [control :as c]
             [util :refer [timeout meh]]]
            [jepsen.control.net :as net]
            [jepsen.os :as os]
            [jepsen.os.debian :as debian]))

(defn da-node-ids
  "Returns a map of node names to node ids."
  [test]
  (->> test
       :nodes
       (map-indexed (fn [i node] [node i]))
       (into {})))

(defn da-node-id
  "Given a test and a node name from that test, returns the ID for that node."
  [test node]
  ((da-node-ids test) node))

(defn r   [_ _] {:type :invoke, :f :read, :value nil})
(defn w   [_ _] {:type :invoke, :f :write, :value (rand-int 5)})
(defn cas [_ _] {:type :invoke, :f :cas, :value [(rand-int 5) (rand-int 5)]})

(defn client
  "A client for a single compare-and-set register"
  []
  (reify client/Client
    (setup! [_ test node]
      (client))

    (invoke! [this test op])

    (teardown! [_ test])))

(defn db
  "Datomic DB for a particular version."
  [version]
  (reify db/DB
    (setup! [_ test node]
       (info node "db setup" version)
       (info node "id is" (da-node-id test node)))

    (teardown! [_ test node]
      (info node "db teardown"))

    db/Primary
    (setup-primary! [_ test node]
      (info node "db setup primary" version)
      (let [uri "datomic:sql://datomic-tester?jdbc:postgresql://postgres:5432/datomic?user=datomic&password=datomic"
            delete (d/delete-database uri)
            create (d/create-database uri)
            conn (d/connect uri)
            schema-tx [{:db/id #db/id[:db.part/db]
                        :db/ident :datomic-tester/register
                        :db/valueType :db.type/long
                        :db/cardinality :db.cardinality/one
                        :db/doc "A register for datomic tester"
                        :db.install/_attribute :db.part/db}]]
        ;; submit schema transaction
        @(d/transact conn schema-tx)))))

(def os
  (reify os/OS
    (setup! [_ test node]
      (info node "os setup")
      ;; TODO
      ;;(debian/setup-hostfile!)
      ;;(meh (net/heal)))
      )

    (teardown! [_ test node]
      (info node "os teardown"))))

(defn da-test
  [version]
  (assoc tests/noop-test
         :nodes ["n1"] ; TODO n1-n3
         :os os
         :db (db version)
         :client (client)))
