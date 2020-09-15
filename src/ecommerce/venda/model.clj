(ns ecommerce.venda.model
  (:import (java.util UUID)))

(defn uuid []
  (UUID/randomUUID))

(defn nova-venda
  ([quantidade] (nova-venda quantidade (uuid)))
  ([quantidade id]
   {:venda/quantidade quantidade
    :venda/id         id
    :venda/status     "nova"}))