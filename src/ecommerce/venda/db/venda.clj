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
