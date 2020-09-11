(ns ecommerce.introducaoDatomic.aula5
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db.datomic.config :as db]
            [ecommerce.produto.model :as model]
            [ecommerce.produto.db.produto :as produto]))

(def conn (db/abre-conexao!))

(let [camera (model/novo-produto "Camera" "/camera" 2500.10M)
      celular (model/novo-produto "Celular" "/celular" 34.9M)
      resultado @(d/transact conn [camera celular])]
  ; :db-after in resultado == (d/db conn)
  (pprint resultado)
  (def past-snapshot (d/db conn))
  (= past-snapshot (:db-after resultado)))

(let [calculadora {:produto/nome "Calculadora"}
      notebook (model/novo-produto "Notebook" "/notebook" 5648.90M)]
  (d/transact conn [calculadora notebook]))

;snapshots
(pprint (produto/find-all (d/db conn))) ;has 4
(pprint (produto/find-all past-snapshot)) ;has 2

;before
(pprint (produto/find-all (d/as-of (d/db conn) #inst "2020-08-10T23:49:46.770-00:00")))
;middle
(pprint (produto/find-all (d/as-of (d/db conn) #inst "2020-08-10T23:50:46.770-00:00")))
;after
(pprint (produto/find-all (d/as-of (d/db conn) #inst "2020-08-10T23:50:50.770-00:00")))


;(db/apaga-banco!)
