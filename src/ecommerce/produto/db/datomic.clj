(ns ecommerce.produto.db.datomic
  (:require [datomic.api :as d]
            [ecommerce.produto.schema :refer [Produto]]
            [ecommerce.produto.adapter :as adapter]
            [schema.core :as s]))

;pull explicit attr by attr
(defn find-all2 [db]
  (d/q '[:find (pull ?entidade [:produto/nome :produto/preco :produto/slug])
         :where [?entidade :produto/nome]] db))

(s/defn find-all :- [Produto]
  [db]
  (adapter/datomic->Produto
    (d/q '[:find [(pull ?entidade [* {:produto/categoria [*]}]) ...]
           :where [?entidade :produto/nome]] db)))

(defn find-by-slug
  [db slug]
  (d/q '[:find ?entidade
         :in $ ?slug-buscado
         :where [?entidade :produto/slug ?slug-buscado]]
       db slug))
;any entity that has the attribute :produto/slug

(defn find-all-slugs
  [db]
  (d/q '[:find ?slug
         :where [_ :produto/slug ?slug]] db))
;use _ when we don't care about the data

(defn find-price-and-name
  [db]
  (d/q '[:find ?nome ?preco
         ;:keys nome preco ;[{:nome x :preco y}]
         :keys produto/nome produto/preco ;[#:produto{:nome x :preco y}]
         :where [?produto :produto/preco ?preco]
                [?produto :produto/nome ?nome]] db))
;if the ?produto is no explicit the return will be a cartesian product
; of all values it found [nome1, preco1] [nome1, preco2] [nome2, preco1] [nome2, preco2]

(defn find-by-price
  [db valor-minimo]
  (d/q '[:find ?nome ?preco
         :in $ ?valor-minimo
         :where [?produto :produto/preco ?preco]
                [(> ?preco ?valor-minimo)]
                [?produto :produto/nome ?nome]]
       db valor-minimo))

(defn find-by-tag
  [db tag]
  (d/q '[:find (pull ?produto [*])
         :in $ ?tag
         :where [?produto :produto/tags ?tag]]
       db tag))

(s/defn insert-produto!
  ([conn
    produto :- [Produto]]
   @(d/transact conn produto))

  ([conn produto ip]
   (let [db-add-ip [:db/add "datomic.tx" :tx-data/ip ip]]
     @(d/transact conn (conj produto db-add-ip)))))

(defn update-produto!
  [conn id-entity attr value]
  @(d/transact conn [[:db/add id-entity attr value]]))

(defn remove-produto
  [conn id-entity attr value]
  @(d/transact conn [[:db/retract id-entity attr value]]))

(defn one-produto
  [db id]
  (d/pull db '[*] id))
;por padrao d/pull busca por db/id

(defn one-produto-by-id
  [db produto-id]
  (d/pull db '[*] [:produto/id produto-id]))

(defn db-adds-produtos
  [produtos categoria]
  (reduce (fn [db-adds produto] (conj db-adds [:db/add
                                               [:produto/id (:produto/id produto)]
                                               :produto/categoria
                                               [:categoria/id (:categoria/id categoria)]]))
          []
          produtos))

(defn atribui-categoria!
  [conn produtos categoria]
  (d/transact conn (db-adds-produtos produtos categoria)))

(defn find-product-and-category-names
  [db]
  (d/q '[:find ?nome-produto ?nome-categoria
         :keys produto categoria
         :where [?produto :produto/nome ?nome-produto]
                [?produto :produto/categoria ?categoria]
                [?categoria :categoria/nome ?nome-categoria]]
       db))

(defn resumo-precos
  [db]
  (d/q '[:find (min ?preco) (max ?preco) (count ?preco)
         :keys minimo maximo total
         :with ?produto
         :where [?produto :produto/preco ?preco]]
       db))

(defn resumo-precos-por-categoria
  [db]
  (d/q '[:find ?nome-categoria (min ?preco) (max ?preco) (count ?preco) (sum ?preco)
         :keys categoria minimo maximo total soma
         :with ?produto
         :where [?produto :produto/preco ?preco]
                [?produto :produto/categoria ?categoria]
                [?categoria :categoria/nome ?nome-categoria]]
       db))

;nested query
(defn produto-mais-caro
  [db]
  (d/q '[:find (pull ?produto [*])
         :where [(q '[:find (max ?preco)
                      :where [_ :produto/preco ?preco]]
                    $) [[?preco]]]
                [?produto :produto/preco ?preco]]
       db))

(defn produto-mais-barato
  [db]
  (d/q '[:find (pull ?produto [*])
         :where [(q '[:find (min ?preco)
                      :where [_ :produto/preco ?preco]]
                    $) [[?preco]]]
                [?produto :produto/preco ?preco]]
       db))

(defn todos-produtos-por-ip
  [db ip]
  (d/q '[:find (pull ?produto [*])
         :in $ ?ip
         :where [?transacao :tx-data/ip ?ip]
                [?produto :produto/id _ ?transacao]]
       db ip))