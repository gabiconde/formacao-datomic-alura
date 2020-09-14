(ns ecommerce.venda.db.venda
  (:require [schema.core :as s]
            [datomic.api :as d]
            [ecommerce.venda.model :as model]))

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