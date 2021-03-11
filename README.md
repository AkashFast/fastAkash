<h1 align="center">Fast Akash for Java</h1>

<div align="center">
专于业务，精于业务，最低的冗余，最高的效率
</div>

<br/> 
<br/> 
<br/>
 
- 首页: [搭建中](https://github.com/AkashFast/fastAkash)
- 帮助文档：[搭建中](https://github.com/AkashFast/fastAkash)

Overview
----

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210311162030107.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzI3MDQ3MjE1,size_16,color_FFFFFF,t_70#pic_center)
>  1. 驾驶舱：简易驾驶舱，目前仅用于监测系统内部日志相关指标数据 `*支持自定义`
>  2. 菜单管理
>  3. 权限管理
>  4. 用户管理：`支持单用户多权限切换`
>  5. 日志管理：可以从`新增、编辑、删除、查询、导出、导入、上传`等多维度进行数据日志的追踪及管理
>  6. 更加贴近业务的SQL引擎`[目前仅支持mysql]`及chars图表引擎，只需简单学习，即可轻松完成`增删改查`及`图表数据`生成
>  7. ……更多功能，正在加急开发中

环境和依赖
----

- jdk 1.8+
- redis
- rabbitMQ
- mybaitis 5.7+
- maven
- idea `建议`
> 前端所需环境及依赖请参照[Fast AKash Ant Design Vue Pro](https://github.com/AkashFast/akashFastVue)

项目下载和运行
----
- 安装相关工具及依赖
- 拉取项目代码
```bash
git clone https://github.com/AkashFast/fastAkash
```
- 执行`src/main/resource/sqlinit/fastAkash.sql`
- idea项目导入
- `application-dev.yml` - 设置相关配置参数
```bash
 init:
    enable: false     # 基础数据初始化 「关闭后系统启动时将不再自动对「数据库库表、字段」及「@schema逻辑层」进行数据同步」
    baseInit: false  # 是否同步系统底层数据表 默认为「false」
    history:  false  # 是否保存历史字段及表信息 默认为「false」 设置为「true」时，同步数据会对原有数据进行备份
    tables:          # 在init.enable为「true」时,可以指定需要同步的数据表「多个间以,隔开」,为空视为同步指定数据库所有数据表
  access:
    enable: false  # 强制鉴权控制,测试时请根据需要打开[true]或关闭[false]本项
   ```
   > 一定要注意`mysql`/`mq`及`redis`的配置信息是否准确
   > 第一次运行项目时，请将 `init.enable`及`access.enable`设置为`true`
   > 后期开发阶段，建议将`access.enable`设置为`false`
   > 若数据库没有字段新增或新的`@schema`类时，理论上建议 `init.enable`设置为`false`
   > `akashConfig.log_queue`:日志推送队列，建议设置MQ中新增的Queens名称
- 编译/启动项目
- 访问项目
```
localhost/fastAkash
```
`建议` ： 当然，您也可以在hosts中配置，将localhost替换成指定的域名

其他说明
---
-  开发时，如无其他需求，则直接在`schema`目录下创建`业务逻辑类`即可
-  使用文档请参照[搭建中](https://github.com/AkashFast/fastAkash)
