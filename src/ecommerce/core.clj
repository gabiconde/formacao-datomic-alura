(ns ecommerce.core
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db.datomic.config :as db]
            [ecommerce.produto.model :as model]
            [ecommerce.categoria.model :as categoria.model]
            [ecommerce.produto.db.datomic :as produto]
            [ecommerce.categoria.db.datomic :as categoria]))

