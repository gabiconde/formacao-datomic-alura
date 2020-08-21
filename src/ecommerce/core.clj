(ns ecommerce.core
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db.datomic.config :as db.config]
            [ecommerce.db.datomic.seed :as db.seed]
            [ecommerce.produto.model :as produto.model]
            [ecommerce.categoria.model :as categoria.model]
            [ecommerce.produto.schema :refer [Produto]]
            [ecommerce.categoria.schema :refer [Categoria]]
            [ecommerce.categoria.db.datomic :as categoria.datomic]
            [ecommerce.produto.db.datomic :as produto.datomic]
            [schema.core :as s]))

(db.config/apaga-banco!)
(def conn (db.config/abre-conexao!))

(def jogos (categoria.model/nova-categoria "jogos"))
(def notebook (produto.model/novo-produto "Notebook" "/notebook" 5648.90M))

(pprint (s/validate Categoria jogos))
(pprint (s/validate Produto notebook))

(db.seed/insert-seeds! conn)
(pprint (categoria.datomic/todas-as-categorias (d/db conn)))
(pprint (produto.datomic/find-all (d/db conn)))

(def dama (produto.model/novo-produto "dama" "/dama" 5789M))
(produto.datomic/upsert-produto! conn [dama])


(defn atualiza-preco []
  (let [produto (produto.datomic/one-produto-by-id (d/db conn) (:produto/id dama))
        new-produto (assoc produto :produto/preco 1M)]
    (produto.datomic/upsert-produto! conn [new-produto])
    (pprint "Preço Atualizado")
    new-produto))

(defn atualiza-slug []
  (let [produto (produto.datomic/one-produto-by-id (d/db conn) (:produto/id dama))
        new-produto (assoc produto :produto/slug "/nova-dama")]
    (Thread/sleep 3000)
    (produto.datomic/upsert-produto! conn [new-produto])
    (pprint "Preço Atualizado")
    new-produto))

(defn roda-transacoes
  [tx]
  (let [futuros (mapv #(future (%)) tx)]
    (pprint (map deref futuros))
    (pprint "Resultado final"))
  (pprint (produto.datomic/one-produto-by-id (d/db conn) (:produto/id dama))))

(roda-transacoes [atualiza-preco atualiza-slug])

(defn atualiza-preco-smart []
  (let [produto {:produto/id    (:produto/id dama)
                 :produto/preco 900M}]
    (produto.datomic/upsert-produto! conn [produto])
    (pprint "Preço Atualizado")
    produto))

(defn atualiza-slug-smart []
  (let [produto {:produto/id   (:produto/id dama)
                 :produto/slug "/dama-top"}]
    (Thread/sleep 3000)
    (produto.datomic/upsert-produto! conn [produto])
    (pprint "Slug Atualizado")
    produto))

(roda-transacoes [atualiza-preco-smart atualiza-slug-smart])