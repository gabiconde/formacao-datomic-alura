(ns ecommerce.bindingdTransactionFilters.aula03
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db.datomic.config :as db.config]
            [ecommerce.db.datomic.seed :as db.seed]
            [ecommerce.produto.schema :refer [Produto]]
            [ecommerce.categoria.schema :refer [Categoria]]
            [ecommerce.produto.db.datomic :as produto.datomic]))

(db.config/apaga-banco!)
(def conn (db.config/abre-conexao!))

(db.seed/insert-seeds! conn)
(def primeiro-produto (last (produto.datomic/find-all (d/db conn))))
(pprint primeiro-produto)

(pprint (produto.datomic/adiciona-variacao! conn (:produto/id primeiro-produto) "digital pass" 40M))
(pprint (produto.datomic/adiciona-variacao! conn (:produto/id primeiro-produto) "digital pass 4 anos" 80M))
