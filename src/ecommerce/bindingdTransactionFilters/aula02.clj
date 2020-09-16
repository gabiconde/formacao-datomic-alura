(ns ecommerce.bindingdTransactionFilters.aula02
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

;(pprint @(db.produto/atualiza-preco! conn (:produto/id primeiro-produto) 500M 60M))

(def produto-antigo (second (produto.datomic/find-all (d/db conn))))
(pprint produto-antigo)

(def a-atualizar {:produto/id (:produto/id produto-antigo)
                  :produto/preco 80M
                  :produto/slug "/cel"})

(pprint (produto.datomic/atualiza! conn produto-antigo a-atualizar))
