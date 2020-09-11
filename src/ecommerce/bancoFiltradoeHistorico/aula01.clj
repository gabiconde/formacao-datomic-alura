(ns ecommerce.bancoFiltradoeHistorico.aula01
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
(pprint (produto.datomic/find-all (d/db conn)))

