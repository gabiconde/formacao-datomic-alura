(ns ecommerce.bancoFiltradoeHistorico.aula05
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

(pprint @(db.venda/altera-status! conn venda-id "preparacao"))
(pprint @(db.venda/altera-status! conn venda-id "enviado"))
(pprint @(db.venda/altera-status! conn venda-id "entregue"))
(pprint @(db.venda/altera-status! conn (:venda/id venda2) "preparacao"))

(pprint (db.venda/historico-status (d/db conn) venda-id))

(pprint (db.venda/cancela! conn (:venda/id venda3)))
(pprint (count (db.venda/todas-canceladas (d/db conn))))
(pprint (count (db.venda/todas (d/db conn))))
(pprint (count (db.venda/todas-ativas (d/db conn))))

;since retorna mudança desde
(pprint (db.venda/historico-geral (d/db conn) #inst "2020-09-15T13:43:20.326-00:00"))