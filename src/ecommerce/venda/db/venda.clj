(ns ecommerce.venda.db.venda
  (:require [schema.core :as s]
            [datomic.api :as d]
            [ecommerce.venda.model :as model]
            [ecommerce.venda.adapter :as adapter]))

(s/defn insert!
        [conn
         produto-id :- s/Uuid
         quantidade :- s/Int]
  (let [nova-venda (model/nova-venda quantidade)]
    (d/transact conn [(assoc nova-venda
                        :venda/produto [:produto/id produto-id])])
    nova-venda))

(defn instante-da-venda
  [db venda-id]
  (d/q '[:find ?instante .
         :in $ ?id
         :where [_ :venda/id ?id ?tx true]
                [?tx :db/txInstant ?instante]]
       db venda-id))

(s/defn custo
  [db
   venda-id :- s/Uuid]
  (let [instante (instante-da-venda db venda-id)]
    (d/q '[:find (sum ?preco-por-produto) .
           :in $ ?id
           :where [?venda :venda/id ?id]
           [?venda :venda/quantidade ?qtd]
           [?venda :venda/produto ?produto]
           [?produto :produto/preco ?preco]
           [(* ?preco ?qtd) ?preco-por-produto]]
         (d/as-of db instante) venda-id)))

(defn cancela! [conn venda-id]
  (d/transact conn [{:venda/id     venda-id
                     :venda/status "cancelada"}]))

(defn todas-ativas
  [db]
  (d/q '[:find ?id
           :where [?venda :venda/id ?id]
                  [?venda :venda/status ?status]
                  [(not= ?status "cancelada")]]
    db))

(defn todas
  [db]
  (d/q '[:find ?id
         :where [?venda :venda/id ?id]]
       db))

(defn todas-canceladas
  [db]
  (d/q '[:find ?id
         :where [?venda :venda/id ?id]
                [?venda :venda/status "cancelada"]]
       db))

(defn altera-status!
  [conn venda-id status]
  (d/transact conn [{:venda/id     venda-id
                     :venda/status status}]))

(defn historico-status
  [db venda-id]
  (->> (d/q '[:find ?instante ?status
              :in $ ?id
              :where [?venda :venda/id ?id]
              [?venda :venda/status ?status ?tx true]
              [?tx :db/txInstant ?instante]]
            (d/history db) venda-id)
       (sort-by first)))

