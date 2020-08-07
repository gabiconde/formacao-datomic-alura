(ns ecommerce.aula1
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db.datomic.config :as config]
            [ecommerce.db.datomic.produto :as datomic.produto]
            [ecommerce.produto.model :as model]))

(def conn (config/abre-conexao))

(let [camera (model/novo-produto "Camera" "/camera" 2500.10M)]
  (d/transact conn [camera]))
;#datom [id-da-entidade atributo valor id-da-tx added?]
;when datom is true in the last attribute it means that something was added to the db

;two transactors (read/write)
;conn is only for read
(datomic.produto/find-all (d/db conn)) ;snapshot of db, only read

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
    ;[db:add id-de-uma-entidade-existente-que-ja-possui-esse-atributo :produto/preco 155.6M]
    ;[db:add id-de-uma-entidade-existente-que-nao-possui-esse-atributo :produto/preco 155.6M]
    ;[db:add id-de-uma-entidade-nao-existente :produto/preco 155.6M]
  (pprint @(d/transact conn [[:db/retract id-entity :produto/slug "/celular"]])))
  ;one datom with the value removed)

(config/apaga-banco)