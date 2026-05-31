# 法兰下料管理系统（Blanking Manage）

这是一个前后端分离的全栈项目，面向法兰加工与下料场景，聚焦订单开单、客户管理、库存物料、库存流水追溯与用户登录鉴权等核心业务。

项目目录中：

- `api`：Spring Boot 后端服务
- `web`：Vue 3 + Element Plus 前端管理台

相较于传统 ERP 的重关联设计，这个项目更强调单体快速交付、单表高性能查询、订单快照留痕、库存流水追溯，以及“厚前端、薄后端”的协作方式。

## 1. 项目概览

### 核心业务场景

- 订单管理：保存订单说明、客户快照、金额信息、交付状态，并支持导出与批量状态更新
- 客户管理：维护客户公司、联系人、电话、地址，为订单开单提供快速选择数据
- 物料管理：维护库存物品、库存数量、位置图片、用途与备注信息
- 库存流水：记录出入库变动前后数量、关联订单号、发起人和审核状态，形成可追溯链路
- 用户管理：提供登录、退出、用户列表、用户保存与编辑能力
- 通用能力：操作日志、异常日志、文件上传、OpenAPI 文档

### 前后端协作方式

- 后端负责数据读写、权限状态、金额校验、库存扣减、日志留痕
- 前端负责页面编排、表格检索、表单交互、远程搜索、上传展示与管理体验
- 前端开发环境通过 `/api` 代理到 `http://localhost:8080`

## 2. 技术栈

### 后端（api）

- Java 11
- Spring Boot 2.7.18
- MyBatis-Plus 3.5.4.1
- Sa-Token 1.37.0
- MySQL 8.0
- Redis
- MinIO
- SpringDoc OpenAPI
- EasyExcel
- Lombok
- Fastjson2

### 前端（web）

- Vue 3
- Vite 6
- TypeScript 5
- Element Plus 2.9
- Pinia
- Vue Router 4
- Axios
- pnpm

## 3. 目录结构

```text
coding01/
├─ api/                       # Spring Boot 后端
│  ├─ pom.xml
│  ├─ README.md
│  └─ src/main/
│     ├─ java/com/example/blankingmanage/
│     │  ├─ controller/       # 用户、客户、订单、物料、库存、公共接口
│     │  ├─ service/          # 业务逻辑
│     │  ├─ mapper/           # MyBatis-Plus Mapper
│     │  ├─ entity/           # 数据库实体
│     │  └─ config/           # Swagger、Sa-Token、MyBatis、线程池、日志切面
│     └─ resources/
│        ├─ application.yml
│        └─ schema.sql
├─ web/                       # Vue 3 管理后台
│  ├─ package.json
│  ├─ README.md
│  └─ src/
│     ├─ api/                 # 前端请求封装
│     ├─ axios/               # Axios 实例与拦截器
│     ├─ views/
│     │  ├─ Business/         # 客户、订单、物料、库存流水业务页面
│     │  └─ Authorization/    # 用户、角色、菜单、部门页面
│     ├─ store/               # Pinia 状态管理
│     └─ router/              # 路由配置
└─ README.md                  # 当前全栈说明文档
```

## 4. 后端说明

### 后端定位

后端是一个单体 Spring Boot 服务，统一提供 REST API、登录鉴权、库存变动、上传接口和日志能力。

### 核心模块

- `/user`：登录、退出、当前用户信息、用户增删改查
- `/customer`：客户分页查询、保存、删除、全量客户下拉数据
- `/order`：订单分页查询、保存、删除、导出、状态更新
- `/material`：物料分页查询、保存、删除
- `/inventory`：库存流水保存与查询
- `/common`：公共上传接口

### 关键实现特点

- 订单保存时会回填客户快照字段，避免客户资料变更影响历史订单
- 订单金额中会自动计算未支付金额
- 订单关联库存扣减时，会按物料 ID 排序并生成库存流水，降低并发写入时的死锁风险
- 所有核心控制器通过操作日志注解记录行为轨迹
- 全局异常会记录到错误日志表，便于排查线上问题
- 提供 OpenAPI 文档能力，便于前后端联调

### 数据库核心表

`schema.sql` 中定义了主要业务表：

- `sys_user`：用户表
- `sys_customer`：客户表
- `sys_order`：订单表
- `sys_material`：物料表
- `sys_inventory_flow`：库存流水审核表
- `sys_operation_log`：操作日志表
- `sys_error_log`：异常日志表

其中订单表使用客户快照字段，库存流水表记录变动前、变动后与关联订单号，用于业务追溯。

## 5. 前端说明

### 前端定位

前端基于 `vue-element-plus-admin` 模板进行了业务化改造，保留了中后台基础能力，并增加了订单、客户、物料、库存流水等业务页面。

### 核心页面与能力

- `src/views/Business/Customer`：客户列表、详情、编辑
- `src/views/Business/Order`：订单检索、新增编辑、状态操作、导出
- `src/views/Business/Material`：物料维护、图片上传、库存展示
- `src/views/Business/InventoryFlow`：库存流水查询与详情展示
- `src/views/Authorization/User`：用户管理页面
- `src/views/Login`：登录页面与登录态处理

### 前端实现特点

- 所有业务接口统一放在 `src/api/*` 下维护
- Axios 拦截器统一处理返回码、错误提示与 401 退出逻辑
- 登录成功后把 Token 保存到本地状态，并通过 `Authorization` 请求头参与后续接口访问
- 订单页面支持客户远程搜索、物料远程搜索、状态批量更新
- 物料页面支持上传位置图片，上传接口为 `/api/common/upload`
- Vite 开发服务器默认运行在 `4000` 端口，并代理后端接口

## 6. 关键业务链路

### 订单开单链路

1. 前端在订单页面远程搜索客户
2. 提交订单时传入客户 ID、订单说明、金额信息和物料消耗列表
3. 后端根据客户 ID 回填联系人、公司名、电话快照
4. 后端自动计算未支付金额
5. 如订单中包含物料消耗，则同步扣减库存并写入库存流水
6. 前端再通过订单列表与库存流水列表做后续跟踪

### 物料与库存链路

1. 先维护物料基础信息与当前库存
2. 物料页面可上传位置图片到 MinIO
3. 订单出库或库存操作会生成库存流水
4. 库存流水保留变更前数量、变更数量、变更后数量、发起人和关联订单号

## 7. 运行环境

建议本地准备以下环境：

- JDK 11
- Maven 3.8+ 或直接使用 Maven Wrapper
- Node.js 18+
- pnpm 8+
- MySQL 8.0
- Redis 6+
- MinIO（如需图片上传功能）

## 8. 快速启动

### 1）初始化数据库

执行后端提供的初始化脚本：

```sql
api/src/main/resources/schema.sql
```

### 2）修改后端配置

编辑 `api/src/main/resources/application.yml`，至少确认以下配置：

- MySQL 地址、库名、账号、密码
- Redis 地址与密码
- MinIO 地址、访问密钥、桶名称
- 服务端口（默认 `8080`）

### 3）启动后端

在 `api` 目录下执行：

```bash
./mvnw spring-boot:run
```

Windows 也可以使用：

```bash
mvnw.cmd spring-boot:run
```

### 4）启动前端

在 `web` 目录下执行：

```bash
pnpm install
pnpm run dev
```

前端默认访问地址：

```text
http://localhost:4000
```

### 5）访问接口文档

后端启动后可访问：

```text
http://localhost:8080/swagger-ui/index.html
```

## 9. 配置说明

### 后端默认端口

- API 服务端口：`8080`

### 前端默认端口

- Web 开发端口：`4000`

### 前端代理规则

前端使用 `/api` 作为接口前缀，本地开发时通过 Vite 代理转发到：

```text
http://localhost:8080
```

因此业务代码中通常会看到：

- `/api/order/*`
- `/api/customer/*`
- `/api/material/*`
- `/api/inventory/*`
- `/api/common/upload`

## 10. 适合继续扩展的方向

- 补齐部门、角色、菜单与后端权限模型的完全联动
- 增加订单统计看板与经营分析页面
- 为库存流水补充更完整的审核流转
- 补充自动化测试、部署文档与生产环境配置模板
- 把前端模板中仍保留的示例页面进一步裁剪为纯业务系统

## 11. 参考说明

- 后端已有说明：`api/README.md`
- 前端保留模板说明：`web/README.md`
- 若要理解具体业务实现，建议优先阅读 `api/src/main/java` 与 `web/src/views/Business`

该 README 旨在帮助你从全栈视角快速理解当前仓库，而不是替代前后端子项目各自的细节说明。