(ns ecommerce.bancoFiltradoeHistorico.aula01e02
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db.datomic.config :as db.config]
            [ecommerce.db.datomic.seed :as db.seed]
            [ecommerce.produto.schema :refer [Produto]]
            [ecommerce.categoria.schema :refer [Categoria]]
            [ecommerce.produto.db.produto :as produto.datomic]
            [ecommerce.venda.db.venda :as db.venda]))

(db.config/apaga-banco!)
(def conn (db.config/abre-conexao!))

(db.seed/insert-seeds! conn)
(def produto (first (produto.datomic/find-all (d/db conn))))

(def venda1 (db.venda/insert! conn (:produto/id produto) 3))
(pprint (db.venda/insert! conn (:produto/id produto) 1))
(pprint venda1)
