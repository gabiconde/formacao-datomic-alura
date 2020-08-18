(ns ecommerce.avancandoComModelo.aula1
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db.datomic.config :as db]
            [ecommerce.produto.model :as model]
            [ecommerce.produto.db.datomic :as produto]))

(def conn (db/abre-conexao!))

(let [camera (model/novo-produto "Camera" "/camera" 2500.10M)
      celular (model/novo-produto "Celular" "/celular" 34.9M)
      calculadora {:produto/nome "Calculadora"}
      notebook (model/novo-produto "Notebook" "/notebook" 5648.90M)]

  (pprint @(d/transact conn [camera celular calculadora notebook]))
  (def produto-db-id (-> (produto/find-all2 (d/db conn))
                         ffirst
                         :db/id))
  (pprint (produto/one-produto (d/db conn) produto-db-id)))

(def produto-id (-> (produto/find-all2 (d/db conn))
                    second
                    first
                    :produto/id))
(pprint (produto/one-produto-by-id (d/db conn) produto-id))


;(db/apaga-banco!))
