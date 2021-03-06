;#*********************************
;# [intro]
;#   author=larluo@spiderdt.com
;#   func=partition algorithm for data warehouse
;#=================================
;# [param]
;#   tabname=staging table name
;#   prt_cols_str=ods partition cols
;#=================================
;# [caller]
;#   [PORG] bolome.dau
;#   [PORG] bolome.event
;#   [PORG] bolome.inventory
;#   [PORG] bolome.order
;#   [PORG] bolome.product_category
;#   [PORG] bolome.show
;#=================================
;# [version]
;#   v1_0=2016-09-28@larluo{create}
;#*********************************

(ns hadoop.bolome.model.d_bolome_show
  (:require [cascalog.api :refer [?- ??- <- ?<- ??<- stdout defmapfn mapfn defmapcatfn mapcatfn defaggregatefn aggregatefn cross-join select-fields]]
            [cascalog.logic.ops :as c]
            [cascalog.cascading.tap :refer [hfs-seqfile hfs-textline]]
            [cascalog.more-taps :refer [hfs-delimited hfs-wrtseqfile hfs-wholefile]]
            [taoensso.timbre :refer [info debug warn set-level!]]
            [clj-time.core :as t :refer [last-day-of-the-month-]]
            [clj-time.format :as tf]
            [clj-time.local :as tl]
            [clj-time.periodic :refer [periodic-seq]])
  (:gen-class))


(set-level! :warn)


(defn -main []
  
  (as-> (<- [?dw-id ?dw-src-id
             ?dw-first-dt ?dw-first-ts ?dw-latest-dt ?dw-latest-ts
             ?show-id ?show-name ?begin-ts ?end-ts]
            ((hfs-delimited "hdfs://192.168.1.3:9000/user/hive/warehouse/ods.db/d_bolome_show" :delimiter "\001")
             :> ?dw-id ?dw-src-id
                ?dw-first-dt ?dw-first-ts ?dw-latest-dt ?dw-latest-ts
                ?show-id ?show-name ?begin-ts ?end-ts))
      $
      (?- (hfs-delimited "hdfs://192.168.1.3:9000/user/hive/warehouse/model.db/d_bolome_show"
                         :outfields ["?dw-id" "?dw-src-id"
                                     "?dw-first-dt" "?dw-first-ts" "?dw-latest-dt" "?dw-latest-ts"
                                     "?show-id" "?show-name" "?begin-ts" "?end-ts"]
                         :delimiter "\001"
                         :quote ""
                         :sinkmode :replace
                         :compression  :enable) $))
  )
