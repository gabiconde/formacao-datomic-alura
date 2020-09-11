(ns ecommerce.produto.db.datomic
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.produto.schema :refer [Produto]]
            [ecommerce.produto.adapter :as adapter]
            [ecommerce.produto.model :as model]
            [clojure.set :as cset]
            [schema.core :as s]))

;pull explicit attr by attr
(defn find-all2 [db]
  (d/q '[:find (pull ?entidade [:produto/id :produto/nome :produto/preco :produto/slug])
         :where [?entidade :produto/nome]] db))

(s/defn find-all :- [Produto]
  [db]
  (adapter/datomic->produto
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
  (let [produto (adapter/datomic->produto (d/pull db '[*] [:produto/id produto-id]))]
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

(def regras
  '[
    [(estoque ?produto ?estoque)
     [?produto :produto/estoque ?estoque]]
    [(estoque ?produto ?estoque)
     [?produto :produto/digital true]
     [(ground 100) ?estoque]]
    [(pode-vender? ?produto)
     (estoque ?produto ?estoque)
     [(> ?estoque 0)]]
    [(produto-categoria ?produto ?nome-categoria)
     [?categoria :categoria/nome ?nome-categoria]
     [?produto :produto/categoria ?categoria]]])


(s/defn todos-produtos-com-estoque :- [Produto]
  [db]
  (adapter/datomic->produto
    (d/q '[:find (pull ?produto [* {:produto/categoria [*]}])
           :in $ %
           :where (pode-vender? ?produto)] db regras)))

;se busca só um numa query coloque o .
(s/defn um-produto-com-estoque :- (s/maybe [Produto])
  [db produto-id]
  (let [query '[:find (pull ?produto [* {:produto/categoria [*]}]).
                :in $ % ?id
                :where [?produto :produto/id ?id]
                       (pode-vender? ?produto ?estoque)]
        produto (adapter/datomic->produto (d/q query db regras produto-id))]
    (if (:produto/id produto)
      produto
      nil)))

(s/defn todos-produtos-pela-categoria :- [Produto]
  [db
   categorias :- [s/Str]]
  (adapter/datomic->produto
    (d/q '[:find (pull ?produto [* {:produto/categoria [*]}])
           :in $ % [?nome-categoria ...]
           :where (produto-categoria ?produto ?nome-categoria)]
         db regras categorias)))

(s/defn todos-produtos-pela-categoria-e-digital :- [Produto]
  [db
   categorias :- [s/Str]
   digital? :- s/Bool]
  (adapter/datomic->produto
    (d/q '[:find (pull ?produto [* {:produto/categoria [*]}])
           :in $ % [?nome-categoria ...] ?digital?
           :where (produto-categoria ?produto ?nome-categoria)
                  [?produto :produto/digital ?digital?]]
         db regras categorias digital?)))

(s/defn atualiza-preco!
  [conn
   produto-id :- s/Uuid
   antigo :- BigDecimal
   novo :- BigDecimal]
  (d/transact conn [[:db/cas [:produto/id produto-id] :produto/preco antigo novo]]))

(s/defn atualiza-produto!
  [conn
   antigo :- Produto
   novo :- Produto]
  (let [produto-id (:produto/id antigo)
        atributos (-> (cset/intersection (set (keys antigo))
                                         (set (keys novo)))
                      (disj :produto/id))
        txs (map
              (fn [atributo]
                [:db/cas [:produto/id produto-id] atributo (get antigo atributo) (get novo atributo)])
              atributos)]
    (d/transact conn txs)))

(s/defn adiciona-variacao!
  [conn
   produto-id :- s/Uuid
   variacao :- s/Str
   preco :- BigDecimal]
  (d/transact conn [{:db/id "variacao-temp"
                     :variacao/nome variacao
                     :variacao/preco preco
                     :variacao/id  (model/uuid)}

                    {:produto/id produto-id
                     :produto/variacao "variacao-temp"}]))

(s/defn apaga-produto!
  [conn
   produto-id :- s/Uuid]
  (d/transact conn [[:db/retractEntity [:produto/id produto-id]]]))


; o . no find retorna um unico valor ou nil
(s/defn visualizacoes
  [db
   produto-id :- s/Uuid]
  (or (d/q '[:find ?visualizacoes .
             :in $ ?id
             :where [?p :produto/id ?id]
                    [?p :produto/visualizacoes ?visualizacoes]]
           db produto-id)
      0))

#_(s/defn visualizacao!
    [conn
     produto-id :- s/Uuid]
    (let [ate-agora (visualizacoes (d/db conn) produto-id)
          novo-valor (inc ate-agora)]
      (d/transact conn [{:produto/id           produto-id
                         :produto/visualizacoes novo-valor}])))

(s/defn visualizacao!
  [conn
   produto-id :- s/Uuid]
  (d/transact conn [[:incrementa-visu produto-id]]))
