(ns ecommerce.schemasRegras.aula4
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db.datomic.config :as db.config]
            [ecommerce.db.datomic.seed :as db.seed]
            [ecommerce.produto.schema :refer [Produto]]
            [ecommerce.categoria.schema :refer [Categoria]]
            [ecommerce.categoria.db.categoria :as categoria.datomic]
            [ecommerce.produto.db.produto :as produto.datomic]
            [schema.core :as s]))

(db.config/apaga-banco!)
(def conn (db.config/abre-conexao!))

(db.seed/insert-seeds! conn)
(pprint (categoria.datomic/todas (d/db conn)))
(def produtos (produto.datomic/find-all (d/db conn)))

(pprint (produto.datomic/todos-produtos-com-estoque (d/db conn)))
(pprint (produto.datomic/um-produto-com-estoque (d/db conn) (:produto/id (first produtos))))
(pprint (produto.datomic/um-produto-com-estoque (d/db conn) (:produto/id (second produtos))))