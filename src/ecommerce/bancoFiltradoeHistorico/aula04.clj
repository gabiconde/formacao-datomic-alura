(ns ecommerce.bancoFiltradoeHistorico.aula04
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
(def venda3 (db.venda/insert! conn (:produto/id produto) 67))
(def venda-id (:venda/id venda1))

(pprint @(db.venda/remove! conn venda-id))

(pprint (count (db.venda/todas-nao-canceladas (d/db conn))))
(pprint (count (db.venda/todas (d/db conn))))
(pprint (count (db.venda/todas-canceladas (d/db conn))))

(pprint (db.produto/upsert! conn [{:produto/id    (:produto/id produto)
                                   :produto/preco 100M}]))
(pprint (db.produto/upsert! conn [{:produto/id    (:produto/id produto)
                                   :produto/preco 60M}]))
(pprint (db.produto/upsert! conn [{:produto/id    (:produto/id produto)
                                   :produto/preco 378M}]))

(pprint (db.produto/historico-de-precos (d/db conn) (:produto/id produto)))
