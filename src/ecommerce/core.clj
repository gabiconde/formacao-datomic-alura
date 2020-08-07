(ns ecommerce.core
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db.datomic.config :as db]
            [ecommerce.produto.model :as model]))

(def conn (db/abre-conexao))

(let [camera (model/novo-produto "Camera" "/camera" 2500.10M)]
  (d/transact conn [camera]))
;#datom [id-da-entidade atributo valor id-da-tx added?]
;when datom is true in the last attribute it means that something was added to the db

;two transactors (read/write)
;conn is only for read
(def db (d/db conn)) ;snapshot of db, only read
(d/q '[:find ?entidade
       :where [?entidade :produto/preco]] db)

;our schema accept a insert with only one attribute
(d/transact conn [{:produto/nome "Calculadora"}])

;It is not possible to insert a nil value.
;updating a value
(let [celular (model/novo-produto "Celular" "/celular" 34.9M)
      resultado @(d/transact conn [celular])
      id-entity (-> resultado :tempids vals first)]
  (pprint resultado)
  (pprint @(d/transact conn [[:db/add id-entity :produto/preco 23.0M]]))
  ;two datoms one is for the value removed and the other for the new value added
  (pprint @(d/transact conn [[:db/retract id-entity :produto/slug "/celular"]])))
