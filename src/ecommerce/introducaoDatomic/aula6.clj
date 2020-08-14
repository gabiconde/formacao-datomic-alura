(ns ecommerce.aula6
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db.datomic.config :as db]
            [ecommerce.produto.model :as model]
            [ecommerce.db.datomic.produto :as produto]))

(def conn (db/abre-conexao))

(let [camera (model/novo-produto "Camera" "/camera" 2500.10M)
      celular (model/novo-produto "Celular" "/celular" 34.9M)
      calculadora {:produto/nome "Calculadora"}
      notebook (model/novo-produto "Notebook" "/notebook" 5648.90M)]

  (pprint @(d/transact conn [camera celular calculadora notebook]))
  (pprint (produto/find-by-price (d/db conn) 1000))

  (produto/update-produto conn 17592186045436 :produto/tags "flash")
  (produto/update-produto conn 17592186045436 :produto/tags "lente 18mm")

  (produto/update-produto conn 17592186045437 :produto/tags "android 10")
  (produto/update-produto conn 17592186045438 :produto/tags "flash")

  (pprint (produto/find-by-tag (d/db conn) "flash")))

;(db/apaga-banco)
