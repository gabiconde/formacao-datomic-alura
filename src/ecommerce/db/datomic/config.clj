(ns ecommerce.db.datomic.config
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.produto.model :as produto.model]
            [ecommerce.categoria.model :as categoria.model]))

(def db-uri "datomic:dev://localhost:4334/ecommerce")

#_(def schema (conj
                produto.model/produto-schema
                categoria.model/categoria-schema))

(defn abre-conexao! []
  (d/create-database db-uri)
  (let [conn (d/connect db-uri)]
    (d/transact conn produto.model/produto-schema)
    conn))

(defn apaga-banco! []
  (d/delete-database db-uri))