(ns ecommerce.bancoFiltradoeHistorico.aula01e02e03
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db.datomic.config :as db.config]
            [ecommerce.db.datomic.seed :as db.seed]
            [ecommerce.produto.schema :refer [Produto]]
            [ecommerce.categoria.schema :refer [Categoria]]
            [ecommerce.produto.db.produto :as db.produto]
            [ecommerce.venda.db.venda :as db.venda]))

(db.config/apaga-banco!)
(def conn (db.config/abre-conexao!))

(db.seed/insert-seeds! conn)
(def produto (first (db.produto/find-all (d/db conn))))

(def venda1 (db.venda/insert! conn (:produto/id produto) 3))
(def venda2 (db.venda/insert! conn (:produto/id produto) 1))
(def venda-id (:venda/id venda1))

(pprint (db.venda/custo (d/db conn) venda-id))
(pprint (db.venda/custo (d/db conn) (:venda/id venda2)))

(pprint (db.produto/upsert! conn [{:produto/id    (:produto/id produto)
                                   :produto/preco 100M}]))

(pprint (db.venda/custo (d/db conn) venda-id))
(pprint (db.venda/custo (d/db conn) (:venda/id venda2)))

