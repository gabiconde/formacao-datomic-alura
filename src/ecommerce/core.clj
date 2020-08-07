(ns ecommerce.core
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db.datomic.config :as db]
            [ecommerce.model :as model]))

(def conn (db/abre-conexao))

(let [camera (model/novo-produto "Camera" "/camera" 2500.10M)]
  (d/transact conn [camera]))
;#datom [id-da-entidade atributo valor id-da-tx added?]
;when datom is true in the last attribute it means that something was added to the db

;two transactors (read/write)
;conn is only for read
(def db (d/db conn)) ;snapshot of db, only read
(d/q '[:find ?entidade
       :where [?entidade :produto/nome]] db)