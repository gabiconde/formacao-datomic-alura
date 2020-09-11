(ns ecommerce.bindingdTransactionFilters.aula01
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db.datomic.config :as db.config]
            [ecommerce.db.datomic.seed :as db.seed]
            [ecommerce.produto.schema :refer [Produto]]
            [ecommerce.categoria.schema :refer [Categoria]]
            [ecommerce.produto.db.produto :as produto.datomic]))

(db.config/apaga-banco!)
(def conn (db.config/abre-conexao!))

(db.seed/insert-seeds! conn)

(pprint (produto.datomic/todos-produtos-pela-categoria (d/db conn) ["eletronicos" "jogos"]))
(pprint (produto.datomic/todos-produtos-pela-categoria (d/db conn) ["jogos"]))

(pprint (produto.datomic/todos-produtos-pela-categoria (d/db conn) []))

(pprint (produto.datomic/todos-produtos-pela-categoria-e-digital (d/db conn) ["eletronicos"] true))
(pprint (produto.datomic/todos-produtos-pela-categoria-e-digital (d/db conn) ["eletronicos"] false))