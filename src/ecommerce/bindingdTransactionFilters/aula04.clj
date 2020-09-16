(ns ecommerce.bindingdTransactionFilters.aula04
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
(def primeiro-produto (last (produto.datomic/find-all (d/db conn))))
(pprint primeiro-produto)

(dotimes [n 10]
  (pprint @(produto.datomic/visualizacao! conn (:produto/id primeiro-produto))))

(pprint (produto.datomic/one-produto-by-id (d/db conn) (:produto/id primeiro-produto)))