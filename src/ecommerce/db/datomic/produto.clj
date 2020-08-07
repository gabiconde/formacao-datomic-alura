(ns ecommerce.db.datomic.produto)

(defn find-all [db]
  (d/q '[:find ?entidade
         :where [?entidade :produto/nome]] db))