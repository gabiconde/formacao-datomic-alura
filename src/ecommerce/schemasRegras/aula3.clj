(ns ecommerce.schemasRegras.aula3
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db.datomic.config :as db.config]
            [ecommerce.db.datomic.seed :as db.seed]
            [ecommerce.produto.model :as produto.model]
            [ecommerce.categoria.model :as categoria.model]
            [ecommerce.produto.schema :refer [Produto]]
            [ecommerce.categoria.schema :refer [Categoria]]
            [ecommerce.categoria.db.categoria :as categoria.datomic]
            [ecommerce.produto.db.produto :as produto.datomic]
            [schema.core :as s]))

(db.config/apaga-banco!)
(def conn (db.config/abre-conexao!))

(def jogos (categoria.model/nova-categoria "jogos"))
(def notebook (produto.model/novo-produto "Notebook" "/notebook" 5648.90M))

(pprint (s/validate Categoria jogos))
(pprint (s/validate Produto notebook))

(db.seed/insert-seeds! conn)
(pprint (categoria.datomic/todas (d/db conn)))
(pprint (produto.datomic/find-all (d/db conn)))

;find with s/maybe should return nil if the product do not exists
(pprint (produto.datomic/one-produto-by-id (d/db conn) (produto.model/uuid)))

;find with side-effects! should raise an exception
(pprint (produto.datomic/one-produto-by-id! (d/db conn) (produto.model/uuid)))
