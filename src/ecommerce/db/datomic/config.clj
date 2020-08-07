(ns ecommerce.db.datomic.config
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.model :as model]))

(def db-uri "datomic:dev://localhost:4334/ecommerce")

(defn abre-conexao []
  (d/create-database db-uri)
  (let [conn (d/connect db-uri)]
    (d/transact conn model/produto-skeleton)
    conn))

(defn apaga-banco []
  (d/delete-database db-uri))