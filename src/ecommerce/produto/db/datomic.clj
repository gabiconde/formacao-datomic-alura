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

(defn find-by-price
  [db valor-minimo]
  (d/q '[:find ?nome ?preco
         :in $ ?valor-minimo
         :where [?produto :produto/preco ?preco]
                [(> ?preco ?valor-minimo)]
                [?produto :produto/nome ?nome]]
       db valor-minimo))

(s/defn upsert-produto!
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

(s/defn one-produto-by-id :- (s/maybe [Produto])
  [db
   produto-id :- s/Uuid]
  (let [produto (adapter/datomic->Produto (d/pull db '[*] [:produto/id produto-id]))]
    (if (:produto/id produto)
      produto
      nil)))

(s/defn one-produto-by-id! :- [Produto]
  [db
   produto-id :- s/Uuid]
  (let [produto (one-produto-by-id db produto-id)]
    (when (nil? produto)
      (throw (ex-info "Não encontrei uma entidade" {:type :errors/not-found :id produto-id})))
   produto))

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