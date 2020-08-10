(ns ecommerce.core
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db.datomic.config :as db]
            [ecommerce.produto.model :as model]
            [ecommerce.db.datomic.produto :as produto]))

(def conn (db/abre-conexao))

(let [camera (model/novo-produto "Camera" "/camera" 2500.10M)
      celular (model/novo-produto "Celular" "/celular" 34.9M)
      resultado @(d/transact conn [celular])
      id-entity (-> resultado :tempids vals first)]

  (pprint resultado)
  (pprint (produto/update-produto conn id-entity :produto/preco 23.0M))
  ;(pprint (produto/remove conn id-entity :produto/slug "/celular"))

  (d/transact conn [camera]))

(def db (d/db conn))
(pprint (produto/find-all db))
(pprint (produto/find-by-slug db "/camera"))
(pprint (produto/find-all-slugs db))

(d/transact conn [{:produto/nome "Calculadora"}])

(db/apaga-banco)