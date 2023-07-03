## 	★<font color='#900000'>P321-服务配置</font>

### 1.1 页面索引

```
src/main/java/com/sitech/pgcenter/service/impl/PdGoodsPrcDictServiceImpl.java
src/main/java/com/sitech/pgcenter/service/impl/GrpGoodsCfgServiceImpl.java
src/main/java/com/sitech/pgcenter/comp/inter/IOutBillingInterCo.java
com/sitech/pgcenter/comp/inter/IGrpGoodsCfgCo.java
```



```mysql
起始页：pgcmng_op_web_view/pgcmng-base-app/npage/busi/gdfamily/P005/P005.html
新增页：pgcmng_op_web_view/pgcmng-base-app/npage/busi/gdfamily/P005/P005_insert.html
js: pgcmng_op_web_view/pgcmng-base-app/nresource/assets/js/busi/gdfamily/P005/P005_insert.js
创建接口：IPdSvcDictAoSvc_insertPdSvcDict
修改接口：IPdSvcDictAoSvc_updatePdSvcDict
新建服务后查询SQL:
SELECT PROD_ID,PROD_NAME,PROD_TYPE,MASTER_SERV_ID,STATE FROM PD_PROD_DICT WHERE PROD_ID = '服务ID'
```

### 1.2.1 主题服务类型

> （1）取值来源
>
> ```mysql
> 查询接口：IPdMasterDictSvc_queryPdMasterDictList
> # 查询口径；MASTER_SERV_ID,MASTER_SERV_NAME
> SELECT * from PD_MASTER_DICT
> ```
>
> （2）入库去向
>
> ```java
> 新增接口：IPdSvcDictAoSvc_insertPdSvcDict 
> ```
>
> #### 入表 **<font color='#900000'>PD_SVC_DICT</font>** ，其中**服务类型**入字段**<font color='#900000'>SVC_TYPE</font>**,**主体服务类型**入字段<font color='#900000'>MASTER_SERV_ID</font>，授权大小入字段**<font color='#900000'>SVC_VERSION</font>**，服务标识入字段<font color='#900000'>SVC_ID</font>，服务名称入字段<font color='#900000'>SVC_NAME</font>

### 1.2.2 服务指令

> （1）取值来源
>
> 网元查询：
>
> ```mysql
> select * from PD_DYNSRV_DICT where SVC_NAME = 'sQryHlrCodeAoSvc';
> # 口径：
> SELECT DISTINCT HLR_CODE,DEVICE_NAME FROM PD_DEVICECMDTYPEHLR_DICT ORDER BY HLR_CODE
> ```
>
> 根据网元动态查询开关机指令：
>
> ```mysql
> 查询接口：IPdCommandCodeDictAoSvc_qryCommandCodeDictListByHlrCode
> # 口径
> SELECT COMMAND_CODE,COMMAND_NAME,HLR_CODE FROM PD_COMMANDCODE_DICT 
> WHERE BINARY HLR_CODE = 'c'  ORDER BY COMMAND_CODE DESC
> ```
>
> （2）入库去向
>
> #### 入表 `PD_SRVCMDRELAT_REL`，其中<font color='#900000'>服务标识</font>入字段`SERVICE_CODE`，<font color='#900000'>网元</font>入字段`SRV_NET_TYPE`，<font color='#900000'>开机指令</font>入字段`ON_CMD`，<font color='#900000'>开机指令ID</font>入字段`ON_CTRL_CODE`,<font color='#900000'>关机指令</font>入字段`OFF_CMD`，<font color='#900000'>关机指令ID</font>入字段`OFF_CTRL_CODE`
>
> ```sql
> select SRV_NET_TYPE 网元,ON_CMD 开机指令,ON_CTRL_CODE 开机指令ID,OFF_CMD 关机指令,OFF_CTRL_CODE关机费用ID from PD_SRVCMDRELAT_REL where SERVICE_CODE = ''
> ```
>
> 

### 1.2.3 服务与频道关系

> （1）取值来源
>
> ```mysql
> 暂无
> ```
>
> （2）入库去向
>
> #### 入表 `PD_SVCCHANNEL_REL`,其中<font color='#900000'>频道代码</font>入字段`CHN_CODE`，<font color='#900000'>频道名称</font>入字段`CHN_NAME`，<font color='#900000'> TV名称</font>入字段`TV_NAME`

### 2.1 查询口径

> 接口：
>
> ```java
> com_sitech_pgcenter_atom_inter_IPdSvcDictAoSvc_qrySvcDict
> ```
>
> 口径：
>
> ```mysql
> SELECT A.SVC_ID, A.SVC_NAME, A.SVC_COMMENTS, A.SVC_TYPE, A.MASTER_SERV_ID, B.MASTER_SERV_NAME, A.CREATE_LOGIN, A.OP_TIME, A.EFF_DATE, A.EXP_DATE, A.STATE, A.SVC_VERSION 
> FROM PD_SVC_DICT A LEFT JOIN pd_master_dict B ON A.MASTER_SERV_ID = B.MASTER_SERV_ID 
> WHERE A.TENANTID = '51' 
> ORDER BY A.CREATE_DATE DESC
> LIMIT 20;
> 
> ```
>
> 

## ★<font color='#900000'>P321-产品配置</font>

### 1.1 页面索引

```java
起始页：pgcmng_op_web_view/pgcmng-base-app/npage/busi/gdfamily/P001/P001.html
新增页：pgcmng_op_web_view/pgcmng-base-app/npage/busi/gdfamily/P001/P001_insert.html
js: pgcmng_op_web_view/pgcmng-base-app/nresource/assets/js/busi/gdfamily/P001/P001_insert.js
创建接口：IProductAoSvc_insert
修改接口：IUpdateProdInfoAoSvc_updateProdInfo
删除接口：IDeleteProdInfoAoSvc_deleteProdInfo
```

### 1.2  取值与入库

#### 1.2.1 产品基本信息

> #### 入表 `PD_PROD_DICT (产品基本信息表)`,其中<font color='#900000'>产品编码</font>入字段`PROD_ID`，<font color='#900000'>产品编码</font>入字段`PROD_TYPE`，<font color='#900000'>产品名称</font>入字段`PROD_NAME`，<font color='#900000'>产品状态（A:已发布，X:已注销，H:未发布）</font>入字段`STATE`，<font color='#900000'>产品类型</font>入字段`PROD_TYPE`

#### 1.2.2 业务类型

> #### （1）取值来源
>
> **`PD_BUSITYPE_DICT`**（业务类型定义表）
>
> ```mysql
> 接口：ProductAoSvc_queryAllBusitype
> # 取值口径
> SELECT BUSITYPE,BUSINAME FROM PD_BUSITYPE_DICT ORDER BY BUSIORDER ASC
> ```
>
> #### （2）入库去向
>
> ```java
> 新增接口：IProductAoSvc_insert
> 修改接口：IUpdateProdInfoAoSvc_updateProdInfo
> ```
>
> #### <font color='#900000'>业务类型</font>：<font color='#900000'>业务类型不为空</font>，入表 `PD_PRODBUSITYPE_REL（产品业务关系表） `，入字段`BUSITYPE`

#### 1.2.3 产品服务关系

> #### （1）取值来源
>
> ```mysql
> 接口：IPdSvcDictAoSvc_qrySvcDict
> # 口径说明：只查询存在主题服务类型的服务
> select
> 	A.SVC_ID,
> 	A.SVC_NAME,
> 	A.SVC_COMMENTS,
> 	A.SVC_TYPE,
> 	A.MASTER_SERV_ID,
> 	B.MASTER_SERV_NAME,
> 	A.CREATE_LOGIN,
> 	A.OP_TIME,
> 	A.EFF_DATE,
> 	A.EXP_DATE,
> 	A.STATE,
> 	A.SVC_VERSION
> from
> 	PD_SVC_DICT A,
> 	pd_master_dict B
> where
> 	A.MASTER_SERV_ID = B.MASTER_SERV_ID
> ```
>
> #### （2）入库去向
>
> ```
> 新增接口：IProductAoSvc_insert
> 修改接口：IUpdateProdInfoAoSvc_updateProdInfo
> ```
>
> ### 入表 `PD_PRODSVC_REL (产品与服务关系表)`，其中<font color='#900000'>服务ID</font>入字段`SVC_ID`，<font color='#900000'>产品ID</font>入字段`PROD_ID`
>
> #### 注：一个产品可包含多个服务，但这些服务的<font color='#900000'>主题服务类型</font>必须相同，**`主题服务类型`**入表`PD_PROD_DICT（产品定义表）`，入字段 `MASTER_SERV_ID`

### 2.1 查询口径

> 接口：
>
> ```java
> ISearchProdInfoListAoSvc_searchProdInfoList
> ```

## ★<font color='#900000'>P321-单商品配置</font>

### 1.1 页面索引

```java
起始页：pgcmng_op_web_view/pgcmng-base-app/npage/busi/gdfamily/P016/P016.html
新增页：pgcmng_op_web_view/pgcmng-base-app/npage/busi/gdfamily/P016/P016_goodsInfo.html
js: pgcmng_op_web_view/pgcmng-base-app/nresource/assets/js/busi/gdfamily/P016/P016_goodsInfo.js
创建接口：ICreateGoodsAndProdRelCoSvc_createGoodsAndProdRel
修改接口：ICreateGoodsAndProdRelCoSvc_updateGoodsAndProdRel
删除接口：ICreateGoodsAndProdRelCoSvc_deleteGoodsAndProdRel
新增后数据库校验：
# 商品基本信息
select * from pd_goods_DICT where GOODS_ID = 'G12431'
# 商品销售目录树
select CATA_ITEM_ID DOODS_ID,CATA_ID from PD_GOODSCATAITEM_DICT where CATA_ITEM_ID = 'G12431'
# 业务品牌
select GOODS_ID,BRAND_ID from PD_GOODSBRAND_REL where GOODS_ID = 'G12431'
# 商品产品关系
select * from pd_goodsprod_rel where GOODS_ID  = 'G12431'
# 商品属性
select * from pd_goodsattr_dict where GOODS_ID = 'G12431'
```

### 1.2 取值与入库

#### 1.2.1 商品基本属性

> #### 入表 `PD_GOODS_DICT (商品信息表)`，其中<font color='#900000'>商品ID</font>入字段`GOODS_ID`，<font color='#900000'>商品名称</font>入字段`GOODS_NAME`，<font color='#900000'>商品类型（0:主商品，1:附加商品）</font>入字段`GOODS_TYPE`，<font color='#900000'>商品状态</font>入字段`STATE`，<font color='#900000'>商品描述</font>入字段`GOODS_DESC`，**<font color='#900000'>受理单描述</font>**入字段`REMARK`，<font color='#900000'>商品打包类型（0:原子商品，1:融合商品）</font>入字段`COM_FLAG`

#### 1.2.2 商品销售目录树	

> #### （1）取值来源
>
> ```mysql
> 顶级节点查询接口：IPdGoodscataDictAoSvc_queryPdGoodscataDictList
> 口径：
> SELECT CATA_ID,CATA_NAME FROM PD_GOODSCATA_DICT WHERE BASE_CATA_FLAG ='A'
> 
> 查询子节点接口：IPdGoodscataDictAoSvc_qryGoodsCata
> # 主要字段：CATA_ID当前节点，PAR_CATA_ID父节点
> SELECT  A.CATA_ID,
> B.PAR_CATA_ID,
> A.CATA_NAME,
> B.CATA_LEVEL
> FROM 
> 	    PD_GOODSCATA_DICT A,PD_GOODSCATA_REL B
> WHERE  
> 		A.CATA_ID=B.CATA_ID 
> 		and B.CATA_LEVEL = '1'
> 		and B.CATA_ID != B.PAR_CATA_ID
> 		and B.PAR_CATA_ID = #{cataId}
> ```
>
> #### （2）入库去向
>
> 新增: 
>
> ```java
> 调用接口：ICreateGoodsAndProdRelCoSvc_createGoodsAndProdRel
> ```
>
> 入表**<font color='#900000'>PD_GOODSCATAITEM_DICT</font>** **商品标识(GOODS_ID)**入字段**<font color='#900000'>CATA_ITEM_ID</font>**,**目录树节点标识(CATA_ID)**入字段**<font color='#900000'>CATA_ID</font>**
>
> 修改：
>
> ```java
> 调用接口：ICreateGoodsAndProdRelCoSvc_updateGoodsAndProdRel
> ```
>
> 复制：
>
> ```java
> 调用接口：ICreateGoodsAndProdRelCoSvc_updateGoodsAndProdRel
> ```

#### 1.2.3 业务品牌

> #### （1）取值来源
>
> ```mysql
> 取值接口：select * from PD_DYNSRV_DICT where SVC_NAME in ('getBrandList');
> # 口径：
> SELECT BRAND_ID,BRAND_NAME,PAR_BRAND_ID,BRAND_DESC
> FROM PD_BRAND_DICT
> WHERE BRAND_ID IN ('a0','b0','b1','b2','d1','n1','n2')
> ```
>
> #### （2）入库去向
>
> 入表**<font color='#900000'>PD_GOODSBRAND_REL</font>** **商品标识(GOODS_ID)**入字段**<font color='#900000'>GOODS_ID</font>**,**品牌标识**入字段**<font color='#900000'>BRAND_ID</font>**

#### 1.2.4 商品账本

> 1. ##### 商品账本
>
> （1）取值来源
>
> ```json
> 本地接口：IOutBillingInterCoSvc_accountAdd
> 接口： http://acctmgr-service-fund/com_sitech_acctmgr_inter_pay_ProdPayTypeSvc_add
> ```
>
> （2）入库去向
>
> #### <font color='#900000'>入库去向</font>：`同商品附加属性`，只不过，商品账本的属性ID为固定<font color='#900000'>10117</font>，
>
> #### `DEVALUE_VALUE`为返回参数中的`PAY_TYPE`
>
> 1. ##### 商品赠送账本
>
> （1）取值来源
>
> ```java
> 
> ```
>
> （2）入库去向
>
> #### <font color='#900000'>入库去向</font>：`同商品附加属性`，商品赠送账本的属性值为固定<font color='#900000'>103002</font>
>
> 

#### 1.2.5 协议期标识

> #### 入库去向：
>
> ##### （非）协议期标识，入表`PD_GOODS_DICT`字段`MODIFY_FLAG`，订购单位入表`PD_TIMERULE_DICT (时间规则配置信息表)`入字段`OFFSET_UNIT`，协议期长度入字段`OFFSET_CYCLE`,
>
> ##### 两表通过`	PD_GOODS_DICT`的`EXP_RULE_ID`字段和`PD_TIMERULE_DICT`表的`RULE_ID`字段进行关联。
>
> ```
> 00 协议期
> 01 非协议期 EXP_RULE_ID:1023 
> ```
>
> ```mysql
> select a.MODIFY_FLAG 协议期标识,b.OFFSET_UNIT 订购单位,b.OFFSET_CYCLE 协议期长度
> from  pd_goods_dict a,PD_TIMERULE_DICT b
> where a.EXP_RULE_ID  = b.RULE_ID 
> and a.GOODS_ID = '商品标识'
> ```

#### 1.2.6 商品附加属性

> #### （1）取值来源
>
> ```mysql
> select * from PD_DYNSRV_DICT where SVC_NAME = 'getAttrList';
> # 口径
> SELECT A.ATTR_ID,A.VAR_NAME,A.ATTR_TYPE,A.CONTROL_TYPE,A.CONF_REMARK,A.DISP_TYPE,A.CTRL_CODE
> FROM PD_ATTRCTRL_DICT A
> WHERE A.ATTR_TYPE IN ('1','12','13','123') 
> ```
>
> #### （2）入库去向
>
> **商品属性显隐性关系以及功能代码入表 `PD_SEGMENTATTRLMT_REL`（商品、定价属性显隐性、功能代码关系表），其中**
>
> OBJECT_TYPE： 对象类型0:不区分按照属性标识限制，1：商品定价，2：服务标识，3：商品标识;
>
> OBJECT_ID：对象标识，类型配置0时，配置Z，OBJECT_TYPE=1：存定价，OBJECT_TYPE=2：服务标识，OBJECT_TYPE=3：商品标识;
>
> ATTR_ID： 属性标识;
>
> SEGMENT： 功能代码
>
> DISP_TYPE：是否展示，Y：允许，N：不允许;
>
> ##### 属性值入表`PD_GOODSATTR_DICT (商品属性表)`，商品标识入字段`GOODS_ID`，属性标识入字段`ATTR_ID`，
>
> ##### <font color='#900000'>属性值</font>入字段`ATTR_VAL`，属性名称入字段`ATTR_NAME`
>
> ### (3) 商品属性初始化
>
> ```sql
> 接口：IPdGoodsRelAoSvc_queryAttrByGoodsId
> 口径：
> 		SELECT
> 			A.GOODS_ID,
> 			A.ATTR_ID,
> 			A.ATTR_NAME,
> 			A.ATTR_DESC,
> 			A.DEFAULT_VALUE,
> 			B.CTRL_CODE,
> 			B.DISP_TYPE
> 		FROM
> 			PD_GOODSATTR_DICT A,
> 			PD_ATTRCTRL_DICT B
> 		WHERE
> 			A.ATTR_ID = B.ATTR_ID
> 		  AND B.ATTR_TYPE in ('1','2','3','12','13','23','123')
> 		  AND A.GOODS_ID = #{GOODS_ID}
> ```
>
> 

#### 1.2.7 商品产品关系

> #### （1）取值来源
>
> ```java
> 接口：ISearchProdInfoListAoSvc_searchProdInfoList
> ```
>
> #### （2）入库去向
>
> ##### 入表`PD_GOODSPROD_REL (商品产品关系表)`，商品标识入字段`GOODS_ID`，产品标识入字段`PROD_ID`

#### 1.2.8 商品预授权区域配置

> #### （1）触发条件
>
> ##### 	当附加属性选择属性标识是 `20050` ，后会触发商品预授权区域配置
>
> #### （1）取值来源
>
> ```java
> 同 单商品定价发布 -> 客户配置区域
> ```
>
> #### （2）入库去向
>
> ##### 入表 **`PD_GOODSRELEASE_DICT（发布信息表）`** ，其中商品标识入字段`GOODS_ID`，PRC_ID为固定值`"F"`,预授权区域标识入字段 **`GROUP_ID`** ，`CHANNEL_TYPE = 'Z'`，`CTRL_CODE = 'Z'` ，每增加一个预授权区域就增加一条记录。

### 1.3 初始化说明

> #### 1.商品基本信息初始化
>
> （1）查询商品基本信息
>
> ```mysql
> 查询商品基本信息接口：IPdGoodsDictAoSvc_queryGoodsById
> 口径：select * from PD_GOODS_DICT where GOODS_ID = 'goodsId'
> ```
>
> （2）查询商品产品关系
>
> ```sql
> 接口：IPdGoodsprodRelAoSvc_qryByGoodsId
> 口径：select * from PD_GOODSPROD_REL where GOODS_ID = 'goodsId' and tenantid = 'goodsId'
> ```
>
> #### 2.产品信息
>
> （1）查询产品
>
> ```mysql
> 接口：ISearchProdInfoListAoSvc_searchProdInfoList
> 口径：select * from PD_PROD_DICT where PROD_ID = 'prodId'
> ```
>
> （2）查询产品服务关系
>
> ```mysql
> 接口：ISearchProdInfoListAoSvc_qryProdSvcList
> 口径：        
> SELECT
> A.PROD_ID,A.SVC_ID,B.SVC_NAME,B.SVC_TYPE,B.MASTER_SERV_ID,C.MASTER_SERV_NAME,B.SVC_COMMENTS,A.SEL_FLAG,A.EFF_DATE,A.EXP_DATE,A.GROUP_ID,A.CREATE_DATE,A.CREATE_LOGIN,A.VERSION
> FROM PD_PRODSVC_REL A JOIN PD_SVC_DICT B ON A.SVC_ID = B.SVC_ID
> LEFT JOIN PD_MASTER_DICT C  ON C.MASTER_SERV_ID = B.MASTER_SERV_ID
> WHERE A.PROD_ID = #{prodId}
> AND A.TENANTID = #{tenantid}
> ```
>
> #### 3.服务信息
>
> （1）查询服务
>
> ```mysql
> 接口：IPdSvcDictAoSvc_qrySvcDict
> 口径：SELECT	A.SVC_ID,
>           A.SVC_NAME,
>           A.SVC_COMMENTS,
>           A.SVC_TYPE,
>           A.MASTER_SERV_ID,
>           B.MASTER_SERV_NAME,
>           A.CREATE_LOGIN,
>          A.OP_TIME,
>           A.EFF_DATE,
>          A.EXP_DATE,
>           A.STATE,
>          A.SVC_VERSION
>   FROM PD_SVC_DICT A LEFT JOIN pd_master_dict B
>   ON A.MASTER_SERV_ID = B.MASTER_SERV_ID
>   WHERE A.TENANTID = #{tenantid}
>      AND A.SVC_ID = #{svcId}
> ```
>
> （3）查询服务指令关系
>
> ```mysql
> 接口：IPdSvcDictAoSvc_qrySrvCmdRelList
> 口径：select * from PD_SRVCMDRELAT_REL where SVC_ID = 'svcId'
> ```
>
> 

1.3 销售品（单商品）分析

```sql
-- 分析销售品组成、看是否需要新建账务资费、是否需要新建服务或产品或销售品
-- 1/1------配置【销售品】GOODS_ID，注意先去找是否已经存在相同授权的销售品，若有存在则用回
-- 1/1、1---按【资费】PRC_ID找【销售品】GOODS_ID名字  
select distinct A.PRC_ID "资费ID",A.PRC_NAME "资费名称",C.GOODS_ID "商品ID",C.GOODS_NAME "商品名称",
A.`STATE` "资费状态",C.COM_FLAG "商品打包类型",C.GOODS_TYPE "商品类型",A.EFF_DATE "定价生效时间",A.EXP_DATE "定价失效时间",C.EFF_DATE "商品生效时间",C.EXP_DATE "商品失效时间"
from PD_GOODSPRC_DICT A,PD_GOODS_DICT C
Where A.GOODS_ID = C.GOODS_ID
and A.PRC_NAME like CONCAT('%','甜果','%')  and A.PRC_NAME like CONCAT('%','59','%') and A.PRC_NAME like CONCAT('%','20年','%')
and C.GOODS_NAME like CONCAT('%','甜果','%')  and C.GOODS_NAME like CONCAT('%','59','%') and C.GOODS_NAME like CONCAT('%','20年','%')
and A.PRC_ID in ('X2636')
and C.GOODS_ID in ('GZ126607')
and C.COM_FLAG = '0' -- 0原子商品，1融合（组合）商品
and C.GOODS_TYPE = '0' -- 0主商品，1附加商品
and A.`STATE` in ('A','S') -- A资费已发布，S资费待审核，X已下架
and sysdate() between C.eff_date and C.exp_date
and C.EXP_DATE >= ('2022-10-01 23:59:59')
```

1.4  查询商品(销售品)下对应的产品、服务、主题服务类型、网元、开机指令、关机指令 

```mysql
-- 查询商品(销售品)下对应的产品、服务、主题服务类型、网元、开机指令、关机指令 
select
	B.GOODS_ID "商品标识", B.GOODS_NAME "商品名称", A.PROD_ID "产品标识", E.PROD_NAME "产品名称",
	C.SVC_ID "服务标识", F.SVC_NAME "服务名称", F.MASTER_SERV_ID "主题服务类型", G.MASTER_SERV_NAME "主题服务名称",
	D.SRV_NET_TYPE "网元", D.ON_CMD "开机指令", D.ON_CTRL_CODE "开机码", D.OFF_CMD "关机指令", D.OFF_CTRL_CODE "关机码"
from pd_goods_dict B left join PD_GOODSPROD_REL A 
on A.GOODS_ID = B.GOODS_ID left join PD_PROD_DICT E 
on A.PROD_ID = E.PROD_ID left join PD_PRODSVC_REL C 
on E.PROD_ID = C.PROD_ID left join pd_svc_dict F 
on C.SVC_ID = F.SVC_ID left join pd_master_dict G
on F.MASTER_SERV_ID = G.MASTER_SERV_ID left join PD_SRVCMDRELAT_REL D
on F.SVC_ID = D.SERVICE_CODE
where
	B.GOODS_ID = 'GZ129845'
	-- and (B.GOODS_NAME = '甜果-月享(时移回看+付费频道+newTV+随心录多屏+甜果4K)' or B.GOODS_ID = 'GZ126529')
```

## ★<font color='#900000'>P321-单商品定价配置</font>

### 1.1 起始页

```java
起始页：pgcmng_op_web_view/pgcmng-base-app/npage/busi/gdfamily/P254/P254.html
定价信息创建接口：IPdGoodsprcDictAoSvc_create
修改接口：IPdGoodsprcDictAoSvc_updatePrc
删除接口：IPdGoodsprcDictAoSvc_deleteGoodsPrc
```

### 1.2 取值与入库

#### 1.2.1 商品信息

> #### （1）取值来源
>
> ```mysql
> 接口：IServiceOfGoodsInfoAoSvc_qryOfPages
> # 主要字段：GOODS_ID
> SQL: select * from PD_GOODS_DICT where COM_FLAG = '0'
> ```
>
> #### （2）入库去向
>
> 新增:
>
> ```java
> 调用接口：IPdGoodsprcDictAoSvc_create
> ```
>
> 入表**<font color='#900000'>PD_GOODSPRC_DICT（商品定价信息表）</font>** **商品标识**入字段**<font color='#900000'>GOODS_ID</font>**，**定价标识**入字段**<font color='#900000'>PRC_ID</font>**，**定价名称**入字段**<font color='#900000'>PRC_NAME</font>**，**定价描述**入字段**<font color='#900000'>GOODS_PRC_DESC</font>**

#### 1.2.2 第三方SP互联网销售品

> 配置第三方SP互联网销售品，需在商品附加属性添加相对应的属性类型(第三方SP互联网销售品采用不同属性ID区分)，
> 配置定价时使用动态SQL判断定价所属商品的附加属性是否包含第三方SP互联网销售品，方便后期动态增删维护，
> 若商品包含SP互联网销售品属性，在配置商品定价时动态添加外部商品授权码，存入PD_OUTGOODS_REL表，其中外部商品编码存入OUT_GOODS_ID字段，第三方SP互联网销售品对应的附件属性ID存入BIZ_CODE字段，商品ID存入GOODS_ID字段，定价ID存入PRC_ID字段。
>
> （1）动态SQL:
>
> ```sql
> select * from PD_DYNSRV_DICT where SVC_NAME = 'getAllPrcClass';
> # 口径
> SELECT A.ATTR_ID,B.VAR_NAME
> FROM PD_GOODSATTR_DICT A,PD_ATTRCTRL_DICT B 
> WHERE A.ATTR_ID = B.ATTR_ID 
> AND A.ATTR_ID  IN ('10402','10403','10404','10405','10408','10409')
> AND A.GOODS_ID = #QRY_PARAM#
> 
> ```
>
> 

#### 1.2.3 客户类型

> #### （1）取值来源
>
> ```mysql
> 接口：IPdAttrValDictAoSvc_qryCustomerAndCardVal
> # 主要字段：ELEMENT_VALUE
> SELECT
> A.ELEMENT_VALUE, A.ELEMENT_VALUE_NAME
> FROM PD_ATTRVAL_DICT A, PD_ATTRCTRL_DICT B
> WHERE B.ATTR_ID=A.ELEMENT_ID
> AND B.ATTR_ID = '10020'
> ```
>
> #### （2）入库去向
>
> **<font color='#900000'>注意</font>**：入库去向**<font color='#900000'>同普通附加属性</font>**，客户类型占用固定属性值**`10020`**

#### 1.2.4 主副端标识

> #### （1）取值来源
>
> ```mysql
> 接口：IPdAttrValDictAoSvc_qryCustomerAndCardVal
> # 主要字段：ELEMENT_VALUE
> SELECT
> A.ELEMENT_VALUE, A.ELEMENT_VALUE_NAME
> FROM PD_ATTRVAL_DICT A, PD_ATTRCTRL_DICT B
> WHERE B.ATTR_ID=A.ELEMENT_ID
> AND B.ATTR_ID = '10040'
> ```
>
> #### （2）入库去向
>
> **<font color='#900000'>注意</font>**：入库去向**<font color='#900000'>同普通附加属性</font>**，主副端标识占用固定属性值**`10040`**

#### 1.2.4 用户属性配置

> #### （1）取值来源
>
> ```mysql
> 接口：IPdAttrValDictAoSvc_qryCustomerAndCardVal
> # 主要字段：ELEMENT_VALUE
> SELECT
> 			A.ELEMENT_VALUE, A.ELEMENT_VALUE_NAME
> 		FROM PD_ATTRVAL_DICT A, PD_ATTRCTRL_DICT B
> 		WHERE B.ATTR_ID=A.ELEMENT_ID
> 		AND B.ATTR_ID = '20084'
> ```
>
> #### （2）入库去向
>
> **<font color='#900000'>注意</font>**：入库去向**<font color='#900000'>同普通附加属性</font>**，用户属性配置占用固定属性值**`20084`**

#### 1.2.5 营业费用(一次性费用)信息

> #### （1）取值来源
>
> ```java
> 一级项费用接口：IOrFeecodeDictAoSvc_queryFirstFee
>  外部接口（受理）：http://crm-cbn-home-query/api/b830/b830Qry
> 费用编码：FEE_CATALOG
> 费用名称：CATALOG_NAME + OP_NOTE
> 费用类型：未传，默认为0
> 二级项费用接口：IOrFeecodeDictAoSvc_querySecondFee
>  外部接口（受理）：http://crm-cbn-home-query/api/b829/sb829Qry
> 费用编码：FEE_CODE
> 费用名称：FEE_NAME + SM_NAME + OP_CODE_NAME + RES_TYPE_NAME
> 费用类型：FEE_TYPE
> ```
>
> #### （2）入库去向
>
> ```java
> 新增接口：IPdGoodsprcfeeRelAoSvc_createOrUpdate
> ```
>
> ##### 入表**`PD_GOODSPRCFEE_REL`<font color='#900000'>一次性费用项ID</font>存入字段`FEE_CODE`,一次性费用**值（金额）存入字段为**`FEE_VALUE`**，一次性费用类型入字段`FEE_TYPE`，一次性费用名称入字段`REMARK`，定价标识入字段`PRC_ID`，其中`PRC_EFF_ID `自增

#### 1.2.6 资费构成信息（账务）

> #### （1）取值来源
>
> ```java
> //根据定价查询资费构成接口
> 本地接口：IOutBillingInterCoSVC_queryPrcFee
> SpringCloud调用外部接口：
> http://10.215.160.150:30193/acctmgr-service-crminfo/com_sitech_acctmgr_crminfo_config_IConfigServiceSvc_getProdPrcMsg
> ```
>
> #### （2）入库去向
>
> ```java
> 增加定价与资费构成关系接口：IOutBillingInterCoSvc_prcFeeAdd
> 外部接口：http://acctmgr-service-fund/com_sitech_acctmgr_inter_pay_ProdPrcSvc_config
> 查询定价与资费构成关系接口：IOutBillingInterCoSVC_queryPrcFee
> 
> ```

#### 1.2.7 定价计费(元)周期性费用(账本)

> #### （1）取值来源
>
> #### （2）入库去向
>
> ```java
> 周期性费用(账本)：ATTR_ID = 10062
> 周期性费用(账本)占用固定属性值10062
> ```
>
> 入表**`PD_GOODSPRCATTR_DICT`**入的字段为**`ATTR_ID`**，只记录该定价包含哪些属性，每一种属性占据一条记录，
>
> 而**`周期性费用(账本)值`**记录在表**`PD_GOODSPRCATTRLMT_REL`**中，其中字段**`ATTR_ID=10062`**，**`ATTR_VALUE`**记录属性值（周期性费用），**`GROUP_ID`**记录月租规则（**`RULE_ID`**），**`ATTR_TYPE`**记录资费分类(**`SOURCE_TYPE`**)，每个属性值占据一条记录。

#### 1.2.8 定价附加属性列表

> #### （1）取值来源
>
> ```mysql
> select * from PD_DYNSRV_DICT where SVC_NAME = 'getAttrctrl';
> # ATTR_ID
> #口径（使用）
> SELECT A.ATTR_ID,A.VAR_NAME,A.ATTR_TYPE,A.CONF_REMARK,A.DISP_TYPE,A.CTRL_CODE,A.CONTROL_TYPE
> FROM PD_ATTRCTRL_DICT A
> WHERE A.ATTR_TYPE IN ('2','12','23','123')
> ```
>
> #### （2）入库去向
>
> ```java
> 调用接口：IPdGoodsprcDictAoSvc_create
> ```
>
> 入表**`PD_GOODSPRCATTR_DICT`**入的字段为**`ATTR_ID`**，只记录该定价包含哪些属性，每一种属性占据一条记录，
>
> 而**`定价属性值`**记录在表**`PD_GOODSPRCATTRLMT_REL`**中，字段**`ATTR_ID`**以及**`ATTR_VALUE`**每个属性值占据一条记录。
>
> #### （3）初始化属性列表
>
> ```java
> 接口：goodsPrcAttrLmtRel_queryByPrcId = IPdGoodsprcattrlmtRelAoSvc_queryByPrcId
> ```
>
> ```sql
> #批量添加合同：
> select * from PD_GOODSPRCATTR_DICT where ATTR_ID = '10021' and PRC_ID in ('X3123','120717','X2831','120300','124268','121790','125567','128490');
> delete From PD_GOODSPRCATTR_DICT where ATTR_ID = '10021' and PRC_ID in ('X3123','120717','X2831','120300','124268','121790','125567','128490');
> INSERT INTO `pd_goodsprcattr_dict` (`PRC_ID`, `ATTR_ID`, `ATTR_NAME`, `ATTR_DESC`, `MIN_VALUE`, `MAX_VALUE`, `STATE`, `STATE_DATE`, `BILL_SEND_FLAG`, `IF_DEFAULT_VALUE`, `SHOW_ORDER`, `CREATE_DATE`, `EFF_DATE`, `EXP_DATE`, `DEFAULT_VALUE`, `PASS_WAY`, `CHECK_FLAG`, `EXPRESSION`, `PRINT_FLAG`, `EFF_RULE_ID`, `CHG_FLAG`, `GRP_NO`, `GROUP_ID`, `USE_RANGE`, `CREATE_LOGIN`, `VERSION`, `TENANTID`, `LOGIN_ACCEPT`, `OP_TIME`, `LOGIN_NO`, `REMARK`) 
> select distinct PRC_ID, '10021', '合同', '合同', '1', '1', 'A', '2022-09-23 17:47:21', 'F', 'F', NULL, '2022-09-23 17:47:21', '2022-08-29 00:00:00', '2099-12-31 00:00:00', '0', '0', 'Y', NULL, 'N', '1001', 'N', '0', '440000', 'Z ', 'gdcs02', '1.0', '44', '16200', '2022-09-23 17:47:21', 'gdcs02', null
> from pd_goodsprc_dict
> where PRC_ID in ('X3123','120717','X2831','120300','124268','121790','125567','128490')
> 
> select * from PD_GOODSPRCATTRLMT_REL where ATTR_ID = '10021' 
> and PRC_ID in ('X3123','120717','X2831','120300','124268','121790','125567','128490')
> and ATTR_VALUE in ();
> delete from PD_GOODSPRCATTRLMT_REL where ATTR_ID = '10021' 
> and PRC_ID in ('X3123','120717','X2831','120300','124268','121790','125567','128490') 
> and ATTR_VALUE in ();
> INSERT INTO `pd_goodsprcattrlmt_rel` (`GOODS_ID`, `PRC_ID`, `ATTR_ID`, `GROUP_ID`, `ATTR_VALUE`, `ATTR_TYPE`, `ACTIVE_FLAG`, `CREATE_LOGIN`, `CREATE_TIME`, `VERSION`, `TENANTID`, `LOGIN_ACCEPT`, `OP_TIME`, `LOGIN_NO`, `REMARK`) 
> select distinct GOODS_ID, PRC_ID, '10021', '440000', 'CZCBN-202209-0172', '2', 'Y', 'gdcs02', '2022-09-23 18:00:41', NULL, '44', '16214', '2022-09-23 18:00:41', 'gdcs02', NULL
> from pd_goodsprc_dict
> where PRC_ID in ('X3123','120717','X2831','120300','124268','121790','125567','128490')
> 
> ```
>
> ```java
> 几个常用属性说明：
> 10021：合同属性
> 10022：批条属性
> 20100：资费标签属性
> 10062：周期性费用（入账务的一次性费用）
> ```

#### 1.2.9 设备类型列表

> #### （1）取值来源
>
> ```mysql
> # 设备购买类型取值来源
> select * from PD_DYNSRV_DICT where SVC_NAME = 'getUnicodedef';
> SELECT CODE_ID,CODE_NAME FROM PD_UNICODEDEF_DICT WHERE CODE_CLASS='P900001'
> # 设备类型取值来源
> select * from PD_DYNSRV_DICT where SVC_NAME = 'getUnicodeDeviceType';
> SELECT CODE_ID,CODE_NAME FROM PD_UNICODEDEF_DICT WHERE CODE_CLASS='P900002'
> ```
>
> #### （2）入库去向
>
> ```java
> 新增接口：IPdGoodsprcDictAoSvc_create
> ```
>
> 入表 **`PD_GOODSRESBUYTYPE_DICT`**  ，**购买类型**入字段为 **`BUYTYPE`** ，**设备类型**入字段为 **`RESTYPE`**，每增加一种设备类型就增加一条记录，**定价标识**入字段**`PRC_ID`**

#### 1.2.10 品牌（隐藏）

> #### （1）取值来源
>
> ```mysql
> 接口：IPdGoodsBrandRelAoSvc_queryByGoodsId
> # 主要字段：BRAND_ID
> SELECT A.GOODS_ID,A.BRAND_ID,B.BRAND_NAME
> FROM PD_GOODSBRAND_REL A,PD_BRAND_DICT B
> WHERE A.BRAND_ID = B.BRAND_ID
> AND A.GOODS_ID = #{GOODS_ID}
> ```
>
> #### （2）入库去向
>
> 入表 **`PD_GOODSPRC_DICT`**  字段为 **`BRAND_ID`** 

#### 1.2.11 刷新缓存

```java
//刷新缓存
pdGoodsprcDictService.updateDspStatus();
```

### 1.2.13 验证功能：

```
接口: IGoodsCheckCoSvc_checkPrcCfgRule
```



## ★<font color='#900000'>P202-单商品定价发布</font>

### 1.1 页面索引

```java
起始页：pgcmng_op_web_view/pgcmng-base-app/npage/busi/gdfamily/P202/P202.html
新增/修改接口：IPdGoodsReleaseDictAoSvc_creatRelease
删除接口：IPdGoodsprcDictAoSvc_deleteGoodsPrc
```

### 1.2 取值与入库

#### 1.2.2 发布类型取值来源:

> （1）取值来源
>
> ```mysql
> 动态SQL：select * from PD_DYNSRV_DICT where SVC_NAME = 'getClassId';
> # 口径：
> SELECT CODE_VALUE,CODE_NAME FROM PD_UNICODEDEF_DICT WHERE CODE_CLASS ='P900003'
> ```
>
> （2）入库去向
>
> 入表 **<font color='#900abc'>PD_GOODSCLASS_REL</font>** ， 发布类型入字段 **<font color='#900abc'>CLASS_ID</font>** ，商品ID入字段 **<font color='#900abc'>GOODS_ID</font>** ，定价ID入字段 **<font color='#900abc'>PRC_ID</font>** ，每增加一种发布类型就增加一条记录
>
> ```mysql
> # 入库检查
> select A.CLASS_ID 发布类型Id,B.CODE_NAME 发布类型名称
> from PD_GOODSCLASS_REL A,PD_UNICODEDEF_DICT B
> where A.CLASS_ID = B.CODE_VALUE 
> and B.CODE_CLASS ='P900003'
> and A.PRC_ID = 'M230614'
> ```
>
> 

#### 1.2.1 发布渠道取值来源：

> （1）取值来源
>
> ```mysql
> select * from PD_DYNSRV_DICT where SVC_NAME = 'getChannelType';
> # 口径 ：发布渠道（CODE_VALUE）
> SELECT CODE_VALUE,CODE_NAME FROM PD_UNICODEDEF_DICT WHERE CODE_CLASS ='P900004'
> ```
>
> （2）入库去向
>
> ```java
> 新增/修改接口：IPdGoodsReleaseDictAoSvc_creatRelease
> ```
>
> 入表 **`PD_GOODSRELEASE_DICT`** ，客户配置区域入字段 **`GROUP_ID`** ， 发布渠道ID（CODE_VALUE）入字段为 **`CHANNEL_TYPE`** ，**RELEASE_ID自增** ，商品ID入字段**`GOODS_ID`** ，定价ID入字段**`PRC_ID`** ，商品每增加一种发布渠道就增加一条记录
>
> ```mysql
> # 入库检查
> select A.CHANNEL_TYPE CHANNEL_TYPE发布渠道ID,B.CODE_NAME 发布渠道,A.GROUP_ID  GROUP_ID客户配置区域ID,C.org_name 客户配置区域,A.CTRL_CODE CTRL_CODE功能代码ID,D.CODE_NAME 功能代码
> from PD_GOODSRELEASE_DICT A,PD_UNICODEDEF_DICT B,EP_ORGANIZATION C,PD_UNICODEDEF_DICT D
> where A.CHANNEL_TYPE = B.CODE_VALUE 
> and C.ORG_ID = A.GROUP_ID
> and A.CTRL_CODE = D.CODE_ID 
> and B.CODE_CLASS = 'P900004'
> and D.CODE_CLASS = 'P25400001'
> and A.PRC_ID = 'M230831'
> ```
>
> 

#### 1.2.3 发布工号

> （1）取值来源
>
> ```mysql
> 接口
> com_sitech_pgcenter_atom_inter_IPdGoodsReleaseDictAoSvc_qryLoginGroup
> # 口径：LOGIN_NO 工号,NICK_NAME 昵称
> SELECT DISTINCT CODE_CLASS,CODE_DESC
>      FROM PD_UNICODEDEF_DICT
>      WHERE BEGIN_VALUE = 'A006'
> ```
>
> （2）入库去向
>
> 入表 **`PD_GOODSRELAADD_DICT`**  **发布工号组织（群）**入字段 **`GROUP_ID`** ，**`FACTOR_TYPE`** 默认为**`A010`**

#### 1.2.4 操作配置区域：

> （1）取值来源：**同客户配置区域**
>
> （2）入库去向
>
> ```java
> 新增\修改接口：IPdGoodsReleaseDictAo_creatRelease
> ```
>
> 入表 **`PD_GOODSRELAADD_DICT`**  **操作配置区域**入字段 **`FACTOR_VALUE`** ，**`FACTOR_TYPE`** 默认为**`A010`**每增加一个操作配置区域就增加一条记录
>
> ```mysql
> # 入库检查：
> select A.FACTOR_VALUE 操作配置区域ID,C.org_name 操作配置区域
> from PD_GOODSRELAADD_DICT A,EP_ORGANIZATION C
> where C.ORG_ID = A.GROUP_ID
> and A.PRC_ID = 'M230614'
> ```
>
> 

说明：

> 根据**定价ID**在表**`PD_GOODSRELEASE_DICT`**中获取**客户配置区域**，在表**`PD_GOODSRELAADD_DICT`**中获取**操作配置区域**

#### 1.2.3 客户配置区域

> （1）取值来源
>
> ```mysql
> 查询接口：IOrgManageSvc_getDistGroupTree1
> 的
> # 口径：获取有用字段GROUP_ID当前区域id，PARENT_GROUP_ID父级区域id，ORG_NAME 当前区域名称
> select
> 	a.GROUP_ID ,
> 	a.PARENT_GROUP_ID ,
> 	a.DENORM_LEVEL ,
> 	a.PARENT_LEVEL,
> 	a.CURRENT_LEVEL,
> 	b.ORG_NAME ,
> 	b.ORG_LEVEL ,
> 	b.HAS_CHILD ,
> 	b.ORG_INDEX ,
> 	b.BUREAU_CODE,
> 	b.BOSS_ORG_CODE ,
> 	b.FIRST_CLASS_CODE ,
> 	b.ORG_TYPE ,
> 	b.STATUS_CD ,
> 	b.CITY_GRADE_CODE,
> 	b.CREATE_DATE ,
> 	b.GRADE_CODE ,
> 	b.ORG_DESC ,
> 	b.CREDIT ,
> 	b.BAIL,
> 	b.BUSINESS_HOURS ,
> 	b.OPEN_DATE ,
> 	b.ORG_PHONE ,
> 	b.FAX ,
> 	b.LAYER_CODE ,
> 	b.MAP ,
> 	b.ACTIVE_TIME,
> 	b.INVALID_TIME ,
> 	b.AUDIT_FLAG ,
> 	b.AUDIT_STATUS ,
> 	b.AUDIT_TIME ,
> 	b.ERP_CODE ,
> 	b.REGION_ID ,
> 	b.LOGIN_PREFIX ,
> 	b.ORG_ADDR,
> 	b.SERVICE_CONTENT ,
> 	B.CREATE_STAFF ,
> 	b.TWO_DIMENSIONAL_CODE ,
> 	B.GIVEOUT_FLAG
> from
> 	EP_REGION_DICT c
> inner join EP_ORGANIZATION b on
> 	b.REGION_ID = c.REGION_ID
> inner join EP_CHNGROUP_REL a on
> 	b.ORG_ID = a.GROUP_ID
> where
> 	b.ORG_TYPE not in('21G', '94')
> 	and b.STATUS_CD = '1'
> 	and a.PARENT_GROUP_ID = '1'
> 	and a.DENORM_LEVEL = 1
> 	and b.VILLAGE_FLAG = 440000
> 	and a.PROVINCE_ID = 440000
> 	and c.PROVINCE_ID = 440000
> order by
> 	convert(b.ORG_NAME
> 		using gbk);
> ```
>
> （2）入库去向
>
> 入表 **`PD_GOODSRELEASE_DICT`** ，客户配置区域入字段 **`GROUP_ID`** ， 发布渠道ID（CODE_VALUE）入字段为 **`CHANNEL_TYPE`** ，功能代码入字段 **`CTRL_CODE`** ， **功能代码**入字段 **`CTRL_CODE`** ，**RELEASE_ID自增** ，商品ID入字段**`GOODS_ID`** ，定价ID入字段**`PRC_ID`** ，商品每增加一种发布渠道就增加一条记录
>
> ```mysql
> # 入库检查：
> 移步：1.2.1发布渠道取值来源
> ```

#### 1.2.5 功能代码取值来源：

> （1）取值来源
>
> ```mysql
> 接口：IPdUniCodeDefDictAoSvc_qryByCodeId
> # 口径：CODE_ID,CODE_NAME
> SELECT * FROM PD_UNICODEDEF_DICT WHERE CODE_CLASS = 'P25400001'
> ```
>
> （2）入库去向
>
> 入表 **`PD_GOODSRELEASE_DICT`**  **功能代码**入字段 **`CTRL_CODE`** ，每增加一种功能代码就增加一条记录
>
> ```mysql
> # 入库检查：
> 移步：1.2.1发布渠道取值来源
> ```

#### 1.2.6 发布地址入表

> ```mysql
> PD_GOODSPRCAREA_REL记录定价与小区代码的关系，支持按照地市配置
> SELECT C.AREA_ID, C.REMARK
>      FROM PD_GOODSPRCAREA_REL C
>      WHERE C.PRC_ID = #{PRC_ID}
> ```

**对应关系**：与客户配置区域一一对应

#### 注：`客户配置区域`与`发布渠道`对应关系

**`PD_GOODSRELEASE_DICT`**  表中数据对应每个定价的关系：对于`每个定价`对应的`总条目数`为 "**<font color='#900000'>客户配置区域*发布渠道</font>**"，其中**<font color='#900000'>每个客户配置区域对应一组发布渠道</font>**，如发布渠道选择了两个，客户配置区域选择了两个，则每个客户配置区域对应两个发布渠道。

<img src="C:/Users/13656/Desktop/项目索引/image-20220906142333631.png" alt="image-20220906142333631" style="zoom:50%;" />

## ★P322融合商品配置

### 1.1 页面索引

```java
起始页：pgcmng_op_web_view/pgcmng-base-app/npage/busi/gdfamily/P323/P323.html
新增页：pgcmng_op_web_view/pgcmng-base-app/npage/busi/gdfamily/P323/P323_goodsInfo.html
js: pgcmng_op_web_view/pgcmng-base-app/nresource/assets/js/busi/gdfamily/P323/P323_goodsInfo.js
创建接口：ICreateGoodsAndProdRelCoSvc_createGoodsAndProdRel
修改接口：ICreateGoodsAndProdRelCoSvc_updateGoodsAndProdRel
删除接口：ICreateGoodsAndProdRelCoSvc_deleteGoodsAndProdRel
```

### 1.2  取值与入库

#### 1.2.1 商品基本属性

> #### 入表 `PD_GOODS_DICT (商品信息表)`，其中<font color='#900000'>商品ID</font>入字段`GOODS_ID`，<font color='#900000'>商品名称</font>入字段`GOODS_NAME`，<font color='#900000'>商品描述</font>入字段`GOODS_DESC`，**<font color='#900000'>受理单描述</font>**入字段`REMARK`，<font color='#900000'>商品打包类型（0:原子商品，1:融合商品）</font>入字段`COM_FLAG`

#### 1.2.2 商品销售目录树	

> **同** 单商品配置

#### 1.2.3 业务品牌

> **同** 单商品配置

#### 1.2.4 协议期标识

> **同** 单商品配置

#### 1.2.5 商品附加属性

> #### （1）取值来源
>
> ```mysql
> 单商品：
> select * from PD_DYNSRV_DICT where SVC_NAME = 'getAttrList';
> # 口径
> SELECT A.ATTR_ID,A.VAR_NAME,A.ATTR_TYPE,A.CONTROL_TYPE,A.CONF_REMARK,A.DISP_TYPE,A.CTRL_CODE
> FROM PD_ATTRCTRL_DICT A
> WHERE A.ATTR_TYPE IN ('1','12','13','123') 
> 融合商品：
> select * from PD_DYNSRV_DICT where SVC_NAME = 'getMixAttrctrl';
> # 口径
> SELECT A.CONTROL_TYPE,A.ATTR_ID,A.VAR_NAME,A.ATTR_TYPE,A.CONF_REMARK,A.DISP_TYPE,A.CTRL_CODE 
> FROM PD_ATTRCTRL_DICT A WHERE A.ATTR_TYPE IN ('3','13','23','123')
> ```
>
> #### （2）入库去向（同单商品）
>
> ##### 入表`PD_GOODSATTR_DICT (商品属性表)`，商品标识入字段`GOODS_ID`，属性标识入字段`ATTR_ID`，
>
> ##### <font color='#900000'>属性值</font>入字段`ATTR_VAL`，属性名称入字段`ATTR_NAME`

## ★P322融合商品定价配置（合）

### 1.1 页面索引

```java
起始页：pgcmng_op_web_view/pgcmng-base-app/npage/busi/gdfamily/P324/P324.html
新增页：pgcmng_op_web_view/pgcmng-base-app/npage/busi/gdfamily/P324/P324_goodsPrc.html
js: pgcmng_op_web_view/pgcmng-base-app/nresource/assets/js/busi/gdfamily/P324/P324_goodsPrc.js
创建接口：IGrpGoodsCfgCoSvc_submitGrpGoodsPrc
修改接口：IGrpGoodsCfgCoSvc_updateGrpGoodsPrc
删除接口：IGrpGoodsCfgCoSvc_deleteGrpGoodsPrc
详情接口：IGrpGoodsCfgCoSvc_qryGrpGoodsPrcContent
```

### 1.2  取值与入库

#### 1.2.1 定价基本信息

> 入表**<font color='#900000'>PD_GOODSPRC_DICT（商品定价信息表）</font>** **商品标识**入字段**<font color='#900000'>GOODS_ID</font>**，**定价标识**入字段**<font color='#900000'>PRC_ID</font>**，**定价名称**入字段**<font color='#900000'>PRC_NAME</font>**，**定价描述**入字段**<font color='#900000'>GOODS_PRC_DESC</font>**

#### 1.2.1 附加属性下拉框取值口径

> ```java
> select * from PD_DYNSRV_DICT where SVC_NAME = 'getSelectAttrVal';
> ```

#### 1.2.3 融合列表

```mysql
select * from PD_DYNSRV_DICT where SVC_NAME = 'getBrandList';
select * from PD_BRAND_DICT where PAR_BRAND_ID not in ('X','c3')

select * from pd_goods_dict where GOODS_ID = 'SC1012' -- 1320
select * from pd_goodsbrand_rel where GOODS_ID = 'SC1012' -- c1

select  * from pd_goodsprc_dict pgd where GOODS_ID = 'SC1012' -- M1002839
select  * from PD_GOODSPRCATTR_DICT where PRC_ID = 'M1002839'
select  * from pd_goodsprcattrlmt_rel_his where GOODS_ID = 'SC1012'

show full columns from PD_GOODSPRC_DICT 
# 模板与融合定价关系 --> 根据定价（prc_id）查询使用到了哪个模板（templateid = 'T10064'） PD_GRPPRCDETAIL_REL
SELECT * FROM PD_PRCGRPTEMPLATE_REL WHERE prc_id = 'M1002839'
# 模板与品牌大项之间的关系 --> 根据模板id（templateid）获取定价包含的品牌类型（element_id）
SELECT * FROM PD_GRPDETAILTEMPLATE_DICT WHERE  templateid = 'T10241' 
# 组成成员与模板、定价标识以及品牌之间的关系 --> 根据模板id、融合定价查询以及品牌大项标识查询具体的单商品（sub_goods）及资费（sub_prc）
SELECT * FROM PD_GRPCOMMBRPRC_REL WHERE templateid = 'T10241' and packge_prc = 'M1002839' and element_id in ( 'C1311','C1312','C1313' )
# 资费与成员资费的关系的
SELECT * FROM PD_GOODSPRC_DICT WHERE  prc_id in ()
# 群资费约束构成关系
SELECT * FROM PD_GRPPRCDETAIL_REL WHERE pkg_prcid = 'M1002839' and element_id in ( 'C1311','C1312','C1313' )
# 成员主费约束
SELECT * FROM PD_FAMNEWMBRPRC_REL WHERE pkgprc_id = 'M1002839' and element_id in ( 'C1311','C1312','C1313' )
# 子资费主副端标识
select * from PD_GRPDPRCATTRLMT_REL where  element_id in ( 'C1311','C1312','C1313' )
```

### 1.3融合构成分析

```sql
select A.GOODS_ID "融合商品标识",B.GOODS_NAME "融合商品名称",A.`STATE` "融合定价状态",A.PRC_ID "融合定价标识",A.PRC_NAME "融合定价名称",
C.TEMPLATEID "模板ID",D.ELEMENT_ID "构成标识",E.SUB_GOODS "包含子商品标识",F.GOODS_NAME "包含子商品名称",E.SUB_PRC "包含子商品定价标识" ,G.PRC_NAME "包含子商品定价名称"
from PD_GOODSPRC_DICT A,pd_goods_dict B,PD_PRCGRPTEMPLATE_REL C,PD_GRPDETAILTEMPLATE_DICT D,PD_GRPCOMMBRPRC_REL E,pd_goods_dict F,PD_GOODSPRC_DICT G
where A.GOODS_ID = B.GOODS_ID 
and C.PRC_ID = A.PRC_ID 
and D.TENANTID = C.TENANTID 
and E.PACKGE_PRC = A.PRC_ID 
and E.TENANTID = C.TENANTID 
and E.ELEMENT_ID = D.ELEMENT_ID 
and F.GOODS_ID = E.SUB_GOODS 
and G.PRC_ID = E.SUB_PRC 
-- and A.PRC_NAME like CONCAT('%','甜果','%')  and A.PRC_NAME like CONCAT('%','59','%') and A.PRC_NAME like CONCAT('%','20年','%')
-- and B.GOODS_NAME like CONCAT('%','甜果','%')  and B.GOODS_NAME like CONCAT('%','59','%') and B.GOODS_NAME like CONCAT('%','20年','%')
and A.PRC_ID in ('M230691')
-- and B.GOODS_ID in ('44G1005227')
and B.COM_FLAG = '1' 
and A.`STATE` in ('A','S') -- A资费已发布，S资费待审核，X已下架
```

### 1.4 融合增加营销活动

> 1.4.1 查询可用营销活动
>
> ```java
> 接口：com_sitech_pgcenter_comp_inter_IGrpGoodsCfgCoSvc_selectMarketing
> 外部接口：http://crm-pgcenter-market-mng/QryActInfoCo/qryBindGroupAct
> ```
>
> 1.4.2 新增资费与营销关系
>
> ```java
> 接口：com_sitech_pgcenter_comp_inter_IGrpGoodsCfgCoSvc_addMarketingRel
> 外部接口：http://crm-pgcenter-market-mng/CompBindRelCo/saveBusiBindRel
> ```
>
> 1.4.3 根据资费标识查询绑定的营销活动
>
> ```java
> 接口：com_sitech_pgcenter_comp_inter_IGrpGoodsCfgCoSvc_selectMarketingByPrcId
> 外部接口：http://crm-pgcenter-market-mng/CompBindRelCo/queryBusiBindRel
> ```
>
> ```java
> //查询营销
> OutDTO selectMarketing(@RequestBody InDTO<RequestMessage<QryGoodsPrcListInDTO>> inDTO);
> //新增资费与营销关系
> OutDTO addMarketingRel(@RequestBody InDTO<RequestMessage<QryGoodsPrcListInDTO>> inDTO);
> //查询资费与营销关系
> OutDTO selectMarketingByPrcId(@RequestBody InDTO<RequestMessage<QryGoodsPrcListInDTO>> inDTO);
> ```





## ★<font color='#900000'>P022商品目录管理</font>

### 1.1 页面索引

```html
起始页：pgcmng_op_web_view/pgcmng-base-app/npage/busi/gdfamily/P022/P022.html

```

### 1.2 取值与入库

#### 1.2.1 顶级目录查询

**注**：同商品配置 ---> 商品销售目录树

```mysql
查询接口：IPdGoodscataDictAoSvc_queryPdGoodscataDictList
口径：
SELECT CATA_ID,CATA_NAME FROM PD_GOODSCATA_DICT WHERE BASE_CATA_FLAG ='A'
```

#### 1.2.2 子节点查询

**注**：同商品配置 ---> 商品销售目录树

```sql
查询子节点接口：IPdGoodscataDictAoSvc_qryGoodsCata
# 主要字段：CATA_ID当前节点，PAR_CATA_ID父节点
SELECT  A.CATA_ID,
     B.PAR_CATA_ID,
     A.CATA_NAME,
     B.CATA_LEVEL
FROM 
	    PD_GOODSCATA_DICT A,PD_GOODSCATA_REL B
WHERE  
		A.CATA_ID=B.CATA_ID 
		and B.CATA_LEVEL = '1'
		and B.CATA_ID != B.PAR_CATA_ID
		and B.PAR_CATA_ID = #{cataId}
```

#### 1.2.3 叶子节点下添加商品

> （1）添加接口：
>
> ```java
> IPdGoodscataitemDictAoSvc_addItem
> ```
>
> 入表**<font color='#900000'>PD_GOODSCATAITEM_DICT</font>** **商品标识(GOODS_ID)**入字段**<font color='#900000'>CATA_ITEM_ID</font>**,**目录树节点标识(CATA_ID)**入字段**<font color='#900000'>CATA_ID</font>**

## ★P016商品管理

### 1.1 页面索引

```java
起始页：pgcmng_op_web_view/pgcmng-base-app/npage/busi/gdfamily/P016/P016.html
新增页：pgcmng_op_web_view/pgcmng-base-app/npage/busi/gdfamily/P016/P016_goodsInfo.html
js: pgcmng_op_web_view/pgcmng-base-app/nresource/assets/js/busi/gdfamily/P016/P016_goodsInfo.js
创建接口：ICreateGoodsAndProdRelCoSvc_createGoodsAndProdRel
修改接口：ICreateGoodsAndProdRelCoSvc_updateGoodsAndProdRel
```

### 1.2  页面说明

> #### P016页面统一管理单商品和融合商品，查询列表中既包含单商品也包含融合商品
>
> 单商品配置中，由**`P321/service_config_choose.html`**页面跳转到P016页面跳转中传入COM_FLAG（商品打包类型标识）的值为0，具体操作单商品还是融合商品由P016页面统一调度。

## ★P266商品关系管理

### 1.1 页面索引

```java
起始页：pgcmng_op_web_view/pgcmng-base-app/npage/busi/gdfamily/P266/P266_goodsrel.html
新增页：pgcmng_op_web_view/pgcmng-base-app/npage/busi/gdfamily/P266/P266_insert.html
js: pgcmng_op_web_view/pgcmng-base-app/nresource/assets/js/busi/gdfamily/P266/P266_insert.js
```

### 1.2  取值与入库

1.2.1 查询

> 接口：
>
> ```java
> IPdGoodsRelAoSvc_queryPdGoodsRelList
> ```
>
> 

#### 1.2.1 商品关系

> #### （1）取值来源
>
> **A端商品标识** 和 **B端商品标识**均来源于**`PD_GOODS_DICT`**表
>
> #### （2）入库去向
>
> 入表**<font color='#900000'>PD_GOODS_REL</font>** **A端商品标识**入字段**<font color='#900000'>ELEMENT_IDA</font>**， **B端商品标识**入字段**<font color='#900000'>ELEMENT_IDAB</font>**， **商品关系**入字段**<font color='#900000'>RELATION_TYPE</font>**
>
> **<font color='#900000'>注</font>：商品之间建立了某种关系，那么该商品下的所有定价也就继承了这种关系**
>
> 商品关系：
>
> ```java
> 5-转移关系(变更)
> 6-可选关系(依赖)
> ```

1.2.1 批量添加商品关系

> - 互动商品添加可选商品
>
> ```sql
> 接口：com_sitech_pgcenter_atom_inter_IPdGoodsRelAoSvc_createBatch
> 口径：
> SELECT DISTINCT F.GOODS_ID,F.GOODS_NAME
> 		FROM PD_SRVCMDRELAT_REL D JOIN PD_PRODSVC_REL C
> 		ON C.SVC_ID = D.SERVICE_CODE JOIN PD_GOODSPROD_REL E
> 		ON E.PROD_ID = C.PROD_ID JOIN PD_GOODS_DICT F											ON F.GOODS_ID = E.GOODS_ID JOIN PD_GOODSBRAND_REL G
> 		ON G.GOODS_ID = F.GOODS_ID
> 		WHERE D.SRV_NET_TYPE = #{hlrCode}
> 		  AND F.GOODS_TYPE = #{goodsType}
> 		  AND F.EXP_DATE >= SYSDATE()
> 		  AND G.BRAND_ID = 'd1'
> UNION
> 			SELECT DISTINCT A.GOODS_ID,A.GOODS_NAME
> 			FROM PD_GOODS_DICT A JOIN PD_GOODSBRAND_REL B
> 			ON A.GOODS_ID = B.GOODS_ID
> 			WHERE A.GOODS_TYPE = #{goodsType}
> 			AND B.BRAND_ID = 'e1'
> 			AND A.EXP_DATE >= SYSDATE()
> UNION
> 			SELECT DISTINCT A.GOODS_ID,A.GOODS_NAME
> 			FROM PD_GOODS_DICT A JOIN PD_GOODSBRAND_REL B
> 			ON A.GOODS_ID = B.GOODS_ID
> 			WHERE A.GOODS_TYPE = #{goodsType}
> 			AND B.BRAND_ID = 'e3'
> 			AND A.EXP_DATE >= SYSDATE()
> ```
>
> - 可选商品添加依赖关系
>
> ```sql
> SELECT DISTINCT
> 	 A.GOODS_ID,
> 	 A.GOODS_NAME
> FROM  PD_GOODS_DICT A,PD_GOODSBRAND_REL B
> WHERE A.GOODS_ID = B.GOODS_ID
> 	AND A.GOODS_TYPE = #{goodsType}
> 	AND B.BRAND_ID = #{brandId}
> 	AND A.GOODS_NAME LIKE CONCAT('%',#{goodsName},'%')
> ```
>
> 

## ★P274商品定价关系管理

### 1.1 页面索引

```
起始页：pgcmng_op_web_view/pgcmng-base-app/npage/busi/gdfamily/P274/P274.html
新增接口：com_sitech_pgcenter_atom_inter_IPdGoodsPrcRelAoSvc_create
```

### 1.2  取值与入库

1.2.1 新增

> 新增接口：
>
> ```java
> IPdGoodsPrcRelAoSvc_create
> ```
>
> 

#### 1.2.1 商品定价关系

> #### （1）取值来源
>
> **A端商品标识** 和 **B端商品标识**均来源于**`PD_GOODSPRC_DICT`**表
>
> #### （2）入库去向
>
> 入表**<font color='#900000'>PD_GOODSPRC_REL</font>** **A端商品定价标识**入字段**<font color='#900000'>ELEMENT_IDA</font>**， **B端商品定价标识**入字段**<font color='#900000'>ELEMENT_IDAB</font>**， **商品定价关系**入字段**<font color='#900000'>RELATION_TYPE</font>**
>
> **<font color='#900000'>注</font>：商品之间建立了某种关系，那么该商品下的所有定价也就继承了这种关系**
>
> 商品关系：
>
> ```java
> 5-转移关系(变更)
> 6-可选关系(依赖)
> 7-连带关系(绑定)
> 19-续签转移关系
> ```

## ★P403统一视图管理

> #### 同单商品配置详情



## ★<font color='#900000'>P9987-批条配置</font>

### 1.1 页面索引

```java
起始页：pgcmng_op_web_view/pgcmng-base-app/npage/busi/zjgd/P9987/P9987_main.html
新增页：pgcmng_op_web_view/pgcmng-base-app/npage/busi/zjgd/P9987/P9987_add.html
js: pgcmng_op_web_view/pgcmng-base-app/nresource/assets/js/busi/zjgd/P9987/P9987_add.js
创建接口：IPdApproveInfoDictAoSvc_add

```

### 1.2  取值与入库

#### 1.2.1 批条信息

> #### （1）取值来源：前端
>
> #### （2）入库去向
>
> ##### 入表`	PD_APPROVEINFO_DICT  (批条信息表)`，批条标识入字段`APPROVE_NO`，是否有效入字段`VALIDFLAG`

#### 1.2.2 <font color='#900000'>批条编号</font>、<font color='#900000'>服务代码</font>和<font color='#900000'>使用次数</font>关系

> #### （1）取值来源：前端
>
> #### （2）入库去向
>
> ##### 入表`PD_APPROVEPHONENO_REL (批条编号和服务代码关系)`，批条标识入字段`APPROVE_NO`，服务号码入字段`PHONE_NO`，使用次数入字段`MAXNUMBER`

#### 1.2.3 批条与资费关系

> #### （1）取值来源
>
> ```mysql
> select PRC_ID from PD_GOODSPRC_DICT where STATE = 'A'
> ```
>
> #### （2）入库去向
>
> #### 入库去向
>
> ```java
> 批条属性：ATTR_ID = 10022
> ```
>
> 入表**`PD_GOODSPRCATTR_DICT`**入的字段为**`ATTR_ID`**，只记录该定价包含哪些属性，每一种属性占据一条记录，
>
> 而**`批条值`**记录在表**`PD_GOODSPRCATTRLMT_REL`**中，字段**`ATTR_ID`**以及**`ATTR_VALUE`**每个属性值占据一条记录。
>
> ##### 资费入字段`PRC_ID`、固定属性`ATTR_ID = 10022`

#### 1.2.4 批条与营销活动关系

> #### （1）取值来源
>
> ```mysql
> SELECT
> 	DISTINCT A.ACT_ID,A.ACT_NAME
> FROM
> 	MK_ACT_INFO A,MK_MEANS_INFO B
> WHERE
> 	A.ACT_ID = B.ACT_ID
> 	AND B.VALID_FLAG = 'Y'
> 	AND A.STATUS_CODE = '04'
> 	AND A.START_DATE <= CURRENT_TIMESTAMP
> 	AND A.END_DATE >= CURRENT_TIMESTAMP
> ```
>
> #### （2）入库去向
>
> ##### 入表`MK_BUSITICKET_INFO (营销与批条关系表)`，批条标识（APPROVE_NO）入字段`DEFAULT_VALUE`、营销ID入字段`BUSI_ID`
>
> 其中**`BUSI_TYPE = '02' ICKET_ID = '10022'`**

#### 1.2.5 合同批条

> 新增合同批条接口：
>
> ```java
> 新增、修改接口：IPdApproveInfoDictAoSvc_contract
> ```
>
> #### 入库去向
>
> ```java
> 合同属性：ATTR_ID = 10021
> ```
>
> 入表**`PD_GOODSPRCATTR_DICT`**入的字段为**`ATTR_ID`**，只记录该定价包含哪些属性，每一种属性占据一条记录，
>
> 而**`合同批条值`**记录在表**`PD_GOODSPRCATTRLMT_REL`**中，字段**`ATTR_ID`**以及**`ATTR_VALUE`**每个属性值占据一条记录。

## ★<font color='#900000'>P281-营销资费发布管理</font>

### 1.1 页面索引

```java
起始页：pgcmng_op_web_view/pgcmng-base-app/npage/busi/hlj/P281/P281.html
js: pgcmng_op_web_view/pgcmng-base-app/nresource/assets/js/busi/hlj/P281/P281.js
创建接口：IPdMargoodsreleDictAoSvc_insertPdMargoodsrele
修改接口：IPdMargoodsreleDictAoSvc_updatePdMargoodsrele
删除接口：IPdMargoodsreleDictAoSvc_deletePdMargoodsrele
```

### 1.2  取值与入库

#### 1.2.1 需要走营销的定价取值口径

> 取值接口：IPdGoodsprcDictAoSvc_queryGoodsPrcListForMark
>
> 口径：
>
> ```sql
> SELECT
>      A.PRC_ID,
>      A.GOODS_ID,
>      B.GOODS_NAME,
>      B.GOODS_TYPE,
>      B.COM_FLAG,
>      A.GOODS_PRC_DESC,
>      A.BRAND_ID,
>      A.EFF_RULE_ID,
>      A.EXP_RULE_ID,
>      A.CANCEL_RULE_ID,
>      A.USE_RANGE,
>      A.SALE_FLAG,
>      A.MIN_NUM,
>      A.MAX_NUM,
>      A.VERSION,
>      A.MODIFY_FLAG,
>      A.UNI_CODE,
>      A.EFF_NUM,
>      A.UNEXP_NUM,
>      A.PRC_CLASS,
>      A.CHINESE_INDEX,
>      A.PRC_NAME,
>      A.PRC_TYPE,
>      A.BILLING_MODE,
>      A.EFF_DATE,
>      A.EXP_DATE,
>      A.STATE,
>      A.CREATE_LOGIN,
>      A.CREATE_TIME,
>      C.BRAND_NAME,
>      A.OP_TIME
>      FROM
>      PD_GOODSPRC_DICT A JOIN PD_GOODS_DICT B ON A.GOODS_ID = B.GOODS_ID
>      LEFT JOIN PD_BRAND_DICT C ON A.BRAND_ID=C.BRAND_ID
>      WHERE
>      A.TENANTID = #{tenantid}
>      AND B.TENANTID = #{tenantid}
> 
> ```
>
> 

#### 1.2.1 权限和归属地市标识

> #### （1）取值来源
>
> - 权限
>
> ```mysql
> 取值接口：IStaffManageSvc_getUniCodeInfo
> 口径：select * from EP_UNICODEDEF_DICT where CODE_CLASS='1002'
> ```
>
> - 归属地市标识
>
> ```mysql
> 取值接口：IOutInterCoSvc_getDistGroupTree => IOrgManageSvc_getDistGroupTree
> 取值接口：IPdUniCodeDefDictAoSvc_getGroupMsg => 
> 口径：select * from EP_ORGANIZATION where village_flag = '440000' and org_id = '1'
> ```
>
> #### （2）入库去向
>
> 入表**`PD_MARGOODSRELE_DICT`**，其中**定价标识**入字段**`PRC_ID`**，**商品标识**入字段**`GOODS_ID`** **归属地市标识**入字段**`GROUP_ID`**，**权限**入字段**`POWER_RIGHT`**

## <font color='#900000'>★P402-属性管理</font>

### 1.1 页面索引

```java
起始页：pgcmng_op_web_view/pgcmng-base-app/npage/busi/zjgd/P402/P402_main.html
js: pgcmng_op_web_view/pgcmng-base-app/nresource/assets/js/busi/hlj/P281/P281.js
创建接口：IPdAttrCtrlDictAoSvc_create
修改接口：IPdAttrCtrlDictAoSvc_create
删除接口：IPdAttrCtrlDictAoSvc_delete
```

### 1.2  取值与入库

#### 1.2.1 属性控制表（**`PD_ATTRCTRL_DICT`**）

> 属性定义表模型列字段类型更改：字段CTRL_CODE （功能代码）类型由decimal(9,0)更改为varchar(10)
>
> ```sql
> alter table pd_attrctrl_dict modify column CTRL_CODE varchar(10)
> alter table pd_attrctrl_dict_his modify column CTRL_CODE varchar(10)
> ```
>
> （1）功能代码
>
> ```sql
> 取值接口：IPdUniCodeDefDictAoSvc_qryByCodeId
> 口径：SELECT DISTINCT * FROM PD_UNICODEDEF_DICT WHERE CODE_CLASS = 'P25400001';
> 入库去向：入表`PD_ATTRCTRL_DICT`，入字段`CTRL_CODE`
> ```
>
> （2）是否展示（DISP_TYPE）
>
> ```java
> 入表`PD_ATTRCTRL_DICT`，入字段`DISP_TYPE`
> ```
>
> （3）页面元素类型（CONTROL_TYPE）
>
> ```java
> 取值有四种：11复选框；19文本框；20下拉框；16单选框
> 入表`PD_ATTRCTRL_DICT`，入字段`CONTROL_TYPE`
> ```
>
> （4）属性类型（ATTR_TYPE）
>
> ```java
> 取值来源：select * from PD_DYNSRV_DICT where SVC_NAME = 'getAttrTypeCheckBox';
>  口径：select '[{"value":"基本属性","key":"0"},{"value":"单商品属性","key":"1"},{"value":"单商品定价属性","key":"2"},{"value":"融合商品属性","key":"3"}]'
> 商品、定价属性取值的过滤条件，用于区分商品属性、商品定价属性、融合商品属性
> 入表`PD_ATTRCTRL_DICT`，入字段`ATTR_TYPE`(需手动输入)
> ```

#### 1.2.2 属性值表（`PD_ATTRVAL_DICT`）

> 当属性类型（CONTROL_TYPE）为11：复选框，20：下拉框，16：单选框之一时
>
> 将属性对应的多个值入表`PD_ATTRVAL_DICT`，
>
> 其中**<font color='#900000'>属性ID</font>**入字段**<font color='#900000'>ATTR_ID</font>**，**<font color='#900000'>属性值标识</font>**入字段**<font color='#900000'>ELEMENT_VALUE</font>**，**<font color='#900000'>属性值描述</font>**入字段**<font color='#900000'>ELEMENT_VALUE_NAME</font>**
>
> 

#### 1.2.3 商品属性显隐性关系表（`PD_SEGMENTATTRLMT_REL`）

> 当是否展示（DISP_TYPE）为'Y'时，入表
>
> **商品属性显隐性关系以及功能代码入表 `PD_SEGMENTATTRLMT_REL`（商品、定价属性显隐性、功能代码关系表），其中**
>
> OBJECT_TYPE： 对象类型0:不区分按照属性标识限制，1：商品定价，2：服务标识，3：商品标识;
>
> OBJECT_ID：对象标识，类型配置0时，配置Z，OBJECT_TYPE=1：存定价，OBJECT_TYPE=2：服务标识，OBJECT_TYPE=3：商品标识;
>
> ATTR_ID： 属性标识;
>
> SEGMENT： 功能代码
>
> DISP_TYPE：是否展示，Y：允许，N：不允许;
>
> 

## <font color='#900000'>★P416个人优惠销售品</font>

### 1.1 页面索引

```java
起始页：pgcmng_op_web_view/pgcmng-base-app/npage/busi/gdfamily/P416/P416.html
js: pgcmng_op_web_view/pgcmng-base-app/nresource/assets/js/busi/gdfamily/P416/P416.js
查询个人优惠销售品接口：com_sitech_pgcenter_comp_inter_IPersonalDiscountCoSvc_qryDyFavConf
增加、批量个人优惠销售品：com_sitech_pgcenter_comp_inter_IPersonalDiscountCoSvc_saveDyFavConf
    外部接口：crm-cbn-home-inter/com_sitech_cbn_home_inter_controller_inter_IProvideProductControllerSvc_saveDyFavConf
修改、批量个人优惠销售品：com_sitech_pgcenter_comp_inter_IPersonalDiscountCoSvc_updateDyFavConf
    外部接口：
```

### 1.2  取值与入库

> （1）新增个人优惠销售品来源取值口径
>
> ```sql
> 接口： IPersonalDiscountCoSvc_qryDyFavConf
> SELECT
> 	A.GOODS_ID,B.GOODS_NAME
> FROM PD_GOODSCLASS_REL A,PD_GOODS_DICT B
> WHERE A.GOODS_ID = B.GOODS_ID
> 	AND A.CLASS_ID in ('d4','Z') 
> ORDER BY A.OP_TIME DESC
> # 说明：发布类型选择个人优惠销售品的商品才能被选到
> ```
>
> （2）入库去向
>
> A. 存入**`PD_GOODSSALERULE_REL`**（商品与规则标识关系表），其中**`GOODS_ID`**入字段**`OBJECT_ID`**，规则标识**`RULE_ID`**入字段**`RULE_ID`**
>
> B. 通过外部接口：**`crm-cbn-home-inter/`**
>
> **`com_sitech_cbn_home_inter_controller_inter_IProvideProductControllerSvc_saveDyFavConf`**
>
> 传值：
>
> ```json
> { "GOODS_NAME": "测试数据1",
> "RULE_ID": "999999",
> "SQL_CONFIG": "select '1' as offerId, '2' as offerName from dual",
> "VALID_FLAG": "3"
> }
> ```
>
> 



## ★P404 配置操作查询

#### 1.1 页面索引

```
起始页：pgcmng_op_web_view/pgcmng-base-app/npage/busi/zjgd/P404/P404_main.html
js: pgcmng_op_web_view/pgcmng-base-app/nresource/assets/js/busi/zjgd/P404/P404.js
查询接口：com_sitech_pgcenter_atom_inter_IPdCfgoprRdAoSvc_selectPdCfgoprRd	
口径：SELECT
        A.LOGIN_ACCEPT,
        A.CFG_OBJ_TYPE,
        A.CFG_OBJ_VAL,
        A.CFG_OBJ_NAME,
        A.LOGIN_NO,
        A.SYSTEM_NOTE,
        A.OP_TIME,
        A.OP_NOTE,
        A.OP_CODE,
        A.REQ_FILE_ID,
        A.APPLY_CODE,
        A.APPLY_NAME,
        A.REQ_FILE_PATH,
        A.REQ_FILE_NAME,
        C.FUNCTION_NAME
        FROM
        PD_CFGOPR_RD A
        LEFT JOIN PS_FUNCTIONCODE_DICT C ON A.OP_CODE = C.FUNCTION_CODE
```

#### 1.2 配置操作新增

> 1.2.1 调用方式
>
> ```java
> //写入操作记录
> pdCfgoprRdService.addCfgOprRd(CFG_OBJ_TYPE.PRICE, CFG_OP_TYPE.DELETE,
>                            inDTO.getBody().getBusiInfo().getPrcId(),
>                            inDTO.getBody().getBusiInfo().getPrcName(),
>                            inDTO.getBody().getOprInfo());
> ```
>
> 1.2.2 源码
>
> ```java
> public int addCfgOprRd(CFG_OBJ_TYPE cfgObjType, CFG_OP_TYPE cfgOpType,
>                         String cfgObjVal,String cfgObjName, OprInfo oprInfo) {
>      Optional.ofNullable(cfgObjVal).orElseThrow(() -> new NullPointerException("写入操作记录时，传入参数为空！！"));
>      TableBaseCol<Object> baseCol = new TableBaseCol<>();
>      String loginNo = oprInfo.getLoginNo();
>      String opCode = oprInfo.getOpCode();
>      String opNote = oprInfo.getOpNote();
>      StringBuffer stringBuffer = new StringBuffer();
>      stringBuffer.append(opNote)
>              .append(",于")
>              .append(baseCol.getOpTime())
>              .append("对")
>              .append(cfgObjVal)
>              .append("(")
>              .append(cfgObjName)
>              .append(")")
>              .append("，进行了")
>              .append(cfgOpType.getDesc())
>              .append("操作");
>      PdCfgoprRdEntity pdCfgoprRdEntity = new PdCfgoprRdEntity();
>      pdCfgoprRdEntity.setLoginAccept(baseCol.getOpAccept());
>      pdCfgoprRdEntity.setContactId("0");
>      pdCfgoprRdEntity.setCfgObjType(cfgObjType.getValue());
>      pdCfgoprRdEntity.setCfgObjVersion("1.0");
>      pdCfgoprRdEntity.setApplyCode(cfgOpType.getValue());
>      pdCfgoprRdEntity.setApplyName(cfgOpType.getDesc());
>      pdCfgoprRdEntity.setCfgObjVal(cfgObjVal);
>      pdCfgoprRdEntity.setCfgObjName(cfgObjName);
>      pdCfgoprRdEntity.setLoginNo(loginNo);
>      pdCfgoprRdEntity.setOpCode(opCode);
>      pdCfgoprRdEntity.setOpNote(String.valueOf(stringBuffer));
>      pdCfgoprRdEntity.setCreateDate(baseCol.getCreateTime());
>      pdCfgoprRdEntity.setOpTime(baseCol.getOpTime());
>      pdCfgoprRdEntity.setChannelType("Z");
>      pdCfgoprRdEntity.setSystemNote(String.valueOf(stringBuffer));
>      String s = Optional.ofNullable(oprInfo.getTenantId()).orElse("44");
>      pdCfgoprRdEntity.setTenantid(s);
>      int insert = baseMapper.insert(pdCfgoprRdEntity);
>      return insert;
>  }
> ```
>
> 1.2.3 入表**`PD_CFGOPR_RD`**，其中操作类型（01：创建，02：修改，：06：删除）入字段**`CFG_OP_TYPE`**，配置类型（0：商品，1：定价，2：产品，3：服务）入字段**`CFG_OBJ_TYPE`**，配置编码（服务、产品、商品、定价标识）入字段**`CFG_OBJ_VAL`**，操作名称（服务、产品、商品、定价名称）入字段**`CFG_OBJ_NAME`**，操作模块（OP_CODE）入字段**`OP_CODE`**，操作备注入字段**`OP_NOTE`**

系统功能定义表：PS_FUNCTIONCODE_DICT

## ★P255商品定价审批管理

### 1.1 页面索引

```java
起始页：pgcmng_op_web_view/pgcmng-base-app/npage/busi/gdfamily/P255/P255_prcPending.html
js: pgcmng-base-app/nresource/assets/js/busi/gdfamily/P255/P255_prcPending.js
创建接口：IPdGoodsApproveInfoAoSvc_create
通过接口：IPdGoodsApproveInfoAoSvc_updateState
```

### 1.2  取值与入库

#### 1.2.1 商品定价审批新增

> #### （1）取值范围
>
> ```
> APP_OBJ_TYPE：0：商品 1：定价 2：产品 3：资费
> ```
>
> #### （2）入库去向 
>
> ```java
> 入表：PD_GOODSAPPROVE_INFO（商品审批信息表）
> 其中，定价ID 入字段 APP_OBJ_VAL
> GOODS_ID 入字段 CHECK_GOODS
> ```

## ★<font color='#900000'>P330-资源方案配置</font>

```java
起始页：pgcmng_op_web_view/pgcmng-base-app/npage/busi/zjgd/e704/html_config_choose.html
```

### 1. 设备分类配置

1.1 初始化

> （1）设备分类查询
>
> **`PD_EQUIPMENTTYPE_DICT`**（设备分类定义表） 
>
> **`PD_UNICODEDEF_DICT`**（统一码表）
>
> ```mysql
> 接口：IPdEquipmentTypeDictAoSvc_qryEqmTypeList
> 口径：
> select
> 	A.OP_TIME,
> 	A.TYPE_CODE,
> 	A.TYPE_NAME,
> 	A.RES_CLASS,
> 	B.CODE_NAME as RES_CLASS_NAME
> from
> 	PD_EQUIPMENTTYPE_DICT A,
> 	PD_UNICODEDEF_DICT B
> where
> 	A.RES_CLASS = B.CODE_ID
> ```
>
> 

1.2页面索引

```java
起始页: pgcmng_op_web_view/pgcmng-base-app/npage/busi/zjgd/e702/e702_qry.html
新增页：pgcmng_op_web_view/pgcmng-base-app/npage/busi/zjgd/e702/e702_add.html
js: pgcmng_op_web_view/pgcmng-base-app/nresource/assets/js/busi/zjgd/e702/e702_add.js
创建接口：IPdEquipmentTypeDictAoSvc_addEquiType
```

1.3  取值与入库

> 入表**`PD_EQUIPMENTTYPE_DICT`**（设备分类定义表），其中**设备资源大类**（CM:宽带猫；SC:智能卡；STB:机顶盒）入字段**`RES_CLASS`**，**设备分类代码** 入字段 **`TYPE_CODE `**，**设备分类名称** 入字段 **`TYPE_NAME`**
>
> ```java
> PD_EQUIPMENTTYPE_DICT 设备分类定义表
> RES_CLASS: 设备资源大类（CM:宽带猫；SC:智能卡；STB:机顶盒）
> TYPE_CODE：设备分类代码
> TYPE_NAME：设备分类名称
> ```
>
> 

1.3 设备分类明细

> （1）页面索引
>
> ```java
> 起始页: pgcmng_op_web_view/pgcmng-base-app/npage/busi/zjgd/e702/e702_dtail.html
> 新增页：pgcmng_op_web_view/pgcmng-base-app/npage/busi/zjgd/e702/e702_adddtail.html
> js: pgcmng_op_web_view/pgcmng-base-app/nresource/assets/js/busi/zjgd/e702/e702_adddtail.js
> 创建接口：IPdEquipmentTypeDetailDictAoSvc_addEquiTypeDetail
> ```
>
> （2）初始化
>
> ```mysql
> 设备分类明细查询接口：IPdEquipmentTypeDetailDictAoSvc_qryEquiTypeDetail
> 口径：
> SELECT A.TYPE_CODE, A.RES_TYPE, A.OP_TIME,B.RES_NAME
> FROM PD_EQUIPMENTTYPEDTAIL_DICT A,
>     SRESTYPE B
> WHERE A.RES_TYPE = B.RES_TYPE
>  AND A.TYPE_CODE = #{TYPE_CODE}
> ORDER BY A.OP_TIME,A.RES_TYPE
> ```
>
> ```java
> PD_EQUIPMENTTYPEDTAIL_DICT(设备分类和具体设备关系表 )
> TYPE_CODE: 设备分类标识
> RES_TYPE: 具体设备
> SRESTYPE(设备资源定义表)
> RES_NAME:设备资源类型名称
> ```
>
> （3） 取值与入库
>
> A.取值：具体设备取值
>
> ```mysql
> 动态SQL: select * from PD_DYNSRV_DICT where SVC_NAME = 'qryResType';
> 口径：
> select
> 	RES_TYPE,RES_NAME
> from
> 	SRESTYPE
> where
> 	RES_CLASS = (select RES_CLASS from PD_EQUIPMENTTYPE_DICT where TYPE_CODE = '100000')
> and RES_TYPE not in (
> 	select
> 		RES_TYPE
> 	from
> 		PD_EQUIPMENTTYPEDTAIL_DICT
> 	where
> 		TYPE_CODE = '100009' );
> ```
>
> B.入库
>
> 入表**`PD_EQUIPMENTTYPEDTAIL_DICT`**（设备分类和设备资源类型关系表），**设备分类代码** 入字段 **`TYPE_CODE `**，**具体设备** 入字段 **`RES_TYPE`**
>
> 注：类似于：华为手机（资源大类：CM:宽带猫；SC:智能卡；STB:机顶盒）-> Mate系列（设备分类）-> mate20,mate30（设备分类明细）

### 2. 资源方案配置

1.1 初始化

> （1）资源方案配置列表查询
>
> **`pd_resourceplan_dict`**（资源方案定义表）
>
> ```mysql
> 接口：IPdResourcePlanDictAoSvc_qryResPlanList
> 口径：
> select
> 	PLAN_CODE,
> 	PLAN_NAME,
> 	SC_TYPE_CODE,
> 	STB_TYPE_CODE,
> 	CM_TYPE_CODE,
> 	LOGIN_NO,
> 	OP_TIME,
> 	IFNULL(( select CONCAT_WS( '->', TYPE_CODE, TYPE_NAME ) from pd_equipmenttype_dict pdd where pdd.type_code = prd.sc_type_code and pdd.res_class = "SC" ), "无" ) SC_TYPE_NAME,
> 	IFNULL(( select CONCAT_WS( '->', TYPE_CODE, TYPE_NAME ) from pd_equipmenttype_dict pdd where pdd.type_code = prd.stb_type_code and pdd.res_class = "STB" ), "无" ) STB_TYPE_NAME,
> 	IFNULL(( select CONCAT_WS( '->', TYPE_CODE, TYPE_NAME ) from pd_equipmenttype_dict pdd where pdd.type_code = prd.cm_type_code and pdd.res_class = "CM" ), "无" ) CM_TYPE_NAME
> from
> 	pd_resourceplan_dict prd
> where
> 	1 = 1
> order by
> 	OP_TIME desc
> ```
>
> 

1.2页面索引

```
起始页: pgcmng_op_web_view/pgcmng-base-app/npage/busi/zjgd/e703/e703_qry.html
新增页：pgcmng_op_web_view/pgcmng-base-app/npage/busi/zjgd/e703/e703_add.html
js: pgcmng_op_web_view/pgcmng-base-app/nresource/assets/js/busi/zjgd/e703/e703_add.js
创建接口：
```

1.3  取值与入库

> 入表**`pd_resourceplan_dict`**（资源方案定义表），其中**资源方案标识**入字段**`PLAN_CODE`**，**资源方案名称** 入字段 **`PLAN_NAME`**，**智能卡设备分类** 入字段 **`SC_TYPE_CODE`**，**机顶盒设备分类** 入字段 **`STB_TYPE_CODE`**，**宽带猫设备分类** 入字段 **`CM_TYPE_CODE`**
>
> **智能卡设备分类取值**
>
> ```mysql
> 动态SQL: select * from PD_DYNSRV_DICT where SVC_NAME = 'getSCTypeCode';
> 口径：
> SELECT TYPE_CODE, CONCAT_WS( '->', TYPE_CODE, TYPE_NAME ) AS TYPE_NAME FROM pd_equipmenttype_dict WHERE RES_CLASS = 'SC'
> ```
>
> **机顶盒设备分类取值**
>
> ```mysql
> 动态SQL: select * from PD_DYNSRV_DICT where SVC_NAME = 'getSTBTypeCode';
> 口径：
> SELECT TYPE_CODE, CONCAT_WS( '->', TYPE_CODE, TYPE_NAME ) AS TYPE_NAME FROM pd_equipmenttype_dict WHERE RES_CLASS = 'STB'
> ```
>
> **宽带猫设备分类取值**
>
> ```mysql
> 动态SQL: select * from PD_DYNSRV_DICT where SVC_NAME = 'getCMTypeCode';
> 口径：
> SELECT TYPE_CODE, CONCAT_WS( '->', TYPE_CODE, TYPE_NAME ) AS TYPE_NAME FROM pd_equipmenttype_dict WHERE RES_CLASS = 'CM'
> ```
>
> 

1.4 资源方案发布

> （1）初始化
>
> ```mysql
> 接口：IPdBusiResPlanRelAoSvc_qryBusiResPlanList
> 口径：
> 		SELECT
>          A.plan_code PLAN_CODE,
>          b.plan_name PLAN_NAME,
>          a.busitype BUSI_TYPE,
>          c.businame BUSI_NAME,
>          A.login_no LOGIN_NO,
>          A.op_time OP_TIME
>      FROM
>          PD_BUSIRESPLAN_REL A,
>          PD_RESOURCEPLAN_DICT b,
>          PD_BUSITYPE_DICT c
>      WHERE
>          A.plan_code = b.plan_code
>          AND a.busitype = c.busitype
>          AND A.PLAN_CODE =#{planCode}
> ```
>
> **`PD_BUSITYPE_DICT`**（业务类型定义表: 用于产品配置-业务类型）
>
> **`PD_RESOURCEPLAN_DICT`**（资源方案定义表）
>
> **`PD_BUSIRESPLAN_REL`**（资源方案和业务类型关系表）
>
> （2）新增
>
> 业务类型取值
>
> ```mysql
> 动态SQL: select * from PD_DYNSRV_DICT where SVC_NAME = 'getBusiType';
> 口径：
> SELECT
> 	* 
> FROM
> 	(
> 	SELECT
> 		aa.* 
> 	FROM
> 		(
> 		SELECT
> 			busitype,
> 			businame 
> 		FROM
> 			PD_BUSITYPE_DICT 
> 		WHERE
> 			 busitype NOT IN ( SELECT busitype FROM PD_BUSIRESPLAN_REL WHERE plan_code = '100032' ) 
> 		) aa 
> 	) bb
> ```
>
> （3）入库
>
> **`PD_BUSIRESPLAN_REL`**（资源方案和业务类型关系表），其中**资源方案标识**入字段**`PLAN_CODE`**，**业务类型** 入字段 **`BUSITYPE`**

## ★<font color='#900000'>P417-商品折扣配置</font>

### 1.1 起始页

```java
起始页：pgcmng_op_web_view/pgcmng-base-app/npage/busi/gdfamily/P426/P426.html
创建接口：com_sitech_pgcenter_atom_inter_IPdGoodsprcDictAoSvc_insertGoodsPrcFav
修改接口：
删除接口：
```

### 1.2 取值与入库

#### 1.2.1 定价与

> #### （1）取值来源
>
> ```mysql
> 接口：IServiceOfGoodsInfoAoSvc_qryOfPages
> #口径 主要字段：GOODS_ID
> SQL: select * from PD_GOODS_DICT where COM_FLAG = '0'
> ```
>
> #### （2）入库去向
>
> 新增:
>
> ```java
> 调用接口：IPdGoodsprcDictAoSvc_create
> ```
>
> 入表**<font color='#900000'>PD_GOODSPRC_DICT（商品定价信息表）</font>** **商品标识**入字段**<font color='#900000'>GOODS_ID</font>**，**定价标识**入字段**<font color='#900000'>PRC_ID</font>**，**定价名称**入字段**<font color='#900000'>PRC_NAME</font>**，**定价描述**入字段**<font color='#900000'>GOODS_PRC_DESC</font>**

#### 1.2.2 第三方SP互联网销售品

> 配置第三方SP互联网销售品，需在商品附加属性添加相对应的属性类型(第三方SP互联网销售品采用不同属性ID区分)，
> 配置定价时使用动态SQL判断定价所属商品的附加属性是否包含第三方SP互联网销售品，方便后期动态增删维护，
> 若商品包含SP互联网销售品属性，在配置商品定价时动态添加外部商品授权码，存入PD_OUTGOODS_REL表，其中外部商品编码存入OUT_GOODS_ID字段，第三方SP互联网销售品对应的附件属性ID存入BIZ_CODE字段，商品ID存入GOODS_ID字段，定价ID存入PRC_ID字段。
>
> （1）动态SQL:
>
> ```sql
> select * from PD_DYNSRV_DICT where SVC_NAME = 'getAllPrcClass';
> # 口径
> SELECT A.ATTR_ID,B.VAR_NAME
> FROM PD_GOODSATTR_DICT A,PD_ATTRCTRL_DICT B 
> WHERE A.ATTR_ID = B.ATTR_ID 
> AND A.ATTR_ID  IN ('10402','10403','10404','10405','10408','10409')
> AND A.GOODS_ID = #QRY_PARAM#
> 
> ```
>
> 
