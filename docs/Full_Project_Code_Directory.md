# 📂 全功能系统架构与代码目录分布图 (完整增强版)

这份增强版的完整目录不仅涵盖了**若依 (RuoYi-Vue) 核心脚手架**的全部自带功能，还事无巨细地列出了我们定制开发的**智慧物业管理系统**的每一个功能模块，包括前端页面、API 接口和后端控制器的精确映射。

> [!TIP]
> 所有的文件及文件夹路径都可以直接点击，以在您的编辑器中快速打开。

---

## 🎨 一、 前端页面与交互层 (Vue3 + Element Plus)
**前端根目录：** [E:\RuoYi-Vue\RuoYi-Vue3](file:///E:/RuoYi-Vue/RuoYi-Vue3)

### 1. 🏢 物业定制业务大类 (Property)
这里包含了物业人员日常使用的全部 16 大定制化功能模块。

* **🏘️ 小区与资产管理**
  * **小区管理 (Community)**：视图 [views/property/community/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/property/community/index.vue) | API [api/property/community.js](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/api/property/community.js)
  * **楼宇管理 (Building)**：视图 [views/property/building/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/property/building/index.vue) | API [api/property/building.js](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/api/property/building.js)
  * **房屋管理 (Room)**：视图 [views/property/room/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/property/room/index.vue) | API [api/property/room.js](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/api/property/room.js)
* **👨‍👩‍👧‍👦 业主与服务管理**
  * **业主资料登记 (Owner)**：视图 [views/property/owner/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/property/owner/index.vue) | API [api/property/owner.js](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/api/property/owner.js)
  * **业主账号授权绑定 (UserOwner)**：视图 [views/property/userOwner/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/property/userOwner/index.vue) | API [api/property/userOwner.js](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/api/property/userOwner.js)
  * **物业公告推送 (Notice)**：视图 [views/property/notice/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/property/notice/index.vue) | API [api/property/notice.js](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/api/property/notice.js)
* **🚗 车位与车辆识别出入管理**
  * **停车位管理 (Parking)**：视图 [views/property/parking/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/property/parking/index.vue) | API [api/property/parking.js](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/api/property/parking.js)
  * **车辆进出台账 (VehicleRecord)**：视图 [views/property/vehicleRecord/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/property/vehicleRecord/index.vue) | API [api/property/vehicleRecord.js](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/api/property/vehicleRecord.js)
    * 包含独立的：入场页面 `enter.vue` 和 出场算费页面 `exit.vue`
* **💰 财务与缴费管理**
  * **收费项目设置 (FeeType)**：视图 [views/property/feeType/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/property/feeType/index.vue) | API [api/property/feeType.js](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/api/property/feeType.js)
  * **财务流水台账 (FeeRecord)**：视图 [views/property/feeRecord/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/property/feeRecord/index.vue) | API [api/property/feeRecord.js](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/api/property/feeRecord.js)
* **🔧 工单与外访管理**
  * **报修派单与评价 (Repair)**：视图 [views/property/repair/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/property/repair/index.vue) | API [api/property/repair.js](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/api/property/repair.js)
  * **投诉建议处理 (Complaint)**：视图 [views/property/complaint/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/property/complaint/index.vue) | API [api/property/complaint.js](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/api/property/complaint.js)
  * **访客预约登记 (Visitor)**：视图 [views/property/visitor/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/property/visitor/index.vue) | API [api/property/visitor.js](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/api/property/visitor.js)
* **🌐 定制化数据门户 (Portal & Dashboard)**
  * **数据大屏监控 (Dashboard)**：API [api/property/dashboard.js](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/api/property/dashboard.js)
  * **业主端移动门户 (Portal)**：视图 [views/property/portal/](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/property/portal/) | API [api/property/portal.js](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/api/property/portal.js)
  * **维修工端接单门户 (WorkerPortal)**：视图 [views/property/workerPortal/](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/property/workerPortal/) | API [api/property/workerPortal.js](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/api/property/workerPortal.js)

### 2. ⚙️ 系统管理基础大类 (System & User)
控制系统的菜单、组织架构、人员、权限等底层生命线逻辑。
* **🔐 身份与访问控制**
  * **用户管理 (User)**: [views/system/user/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/system/user/index.vue) (全量员工增删改查、Excel导入导出)
  * **角色管理 (Role)**: [views/system/role/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/system/role/index.vue) (数据权限 DataScope 控制与菜单分配)
  * **菜单管理 (Menu)**: [views/system/menu/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/system/menu/index.vue) (左侧路由与按钮权限字符配置)
  * **部门管理 (Dept)**: [views/system/dept/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/system/dept/index.vue) (公司层级与小区组织架构映射)
  * **岗位管理 (Post)**: [views/system/post/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/system/post/index.vue) (用户职级头衔配置)
* **🔧 核心配置与工具**
  * **数据字典 (Dict)**: [views/system/dict/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/system/dict/index.vue) (配置例如“缴费状态”、“报修状态”等下拉框枚举值)
  * **参数设置 (Config)**: [views/system/config/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/system/config/index.vue) (系统级全局开关与配置变量)
  * **通知公告 (Notice)**: [views/system/notice/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/system/notice/index.vue) (系统级管理员内部通报)
  * **个人中心 (Profile)**: [views/system/user/profile/](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/system/user/profile/) (当前登录员工的个人信息修改、密码重置)
* **👁️ 系统监控 (Monitor)**
  * **定时任务 (Job)**: [views/monitor/job/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/monitor/job/index.vue) (基于 Quartz 的后台自动任务，例如每月自动生成物业费)
  * **操作日志 (Operlog)**: [views/monitor/operlog/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/monitor/operlog/index.vue) (记录谁修改了哪条敏感数据)

---

## ☕ 二、 后端核心工程 (Java Spring Boot 多模块)
**后端根目录：** [E:\RuoYi-Vue](file:///E:/RuoYi-Vue)

### 1. 🌐 Web 接口控制层 (my-admin)
直接与前端 Vue 页面通过 HTTP 交互的最外层路由网关。
* **🏘️ 物业专属接口 (`com.ruoyi.web.controller.property`)**:
  包含 16 个核心控制器：
  `PmsBuildingController`, `PmsCommunityController`, `PmsComplaintController`, `PmsDashboardController`, `PmsFeeRecordController`, `PmsFeeTypeController`, `PmsNoticeController`, `PmsOwnerController`, `PmsParkingController`, `PmsPortalController`, `PmsRepairController`, `PmsRoomController`, `PmsUserOwnerController`, `PmsVehicleRecordController` (核心识别入口), `PmsVisitorController`, `PmsWorkerPortalController`。
* **⚙️ 系统功能接口 (`com.ruoyi.web.controller.system`)**:
  包含 13 个系统级控制器：
  `SysUserController` (导入导出与用户分配核心), `SysRoleController`, `SysMenuController`, `SysDeptController`, `SysDictDataController`, `SysDictTypeController`, `SysConfigController`, `SysNoticeController`, `SysPostController`, `SysProfileController`, `SysLoginController` (登录认证签发 Token), `SysRegisterController`, `SysIndexController`。

### 2. 🧠 业务处理与数据库交互层 (my-system)
业务逻辑大脑与数据持久化处理。包含两个并行的大包：
* **业务核心 (`com.ruoyi.property` 与 `com.ruoyi.system`)** 均包含以下三层结构：
  * **Domain (实体层)**: 所有对应数据库表的映射对象（例如 `PmsRepair`, `SysUser`）。
  * **Mapper (数据库访问层)**: 供 MyBatis 调用的 Java 接口（例如 `PmsFeeRecordMapper`），其具体的复杂 SQL 语句编写在 `my-system/src/main/resources/mapper/` 的 XML 文件中。
  * **Service (业务服务层)**: 所有的强业务逻辑（如物业费生成、 Redis 缓存同步、权限解析）均在 `service/impl/` 文件夹下的实现类中完成。

### 3. 🛡️ 底层安全与拦截框架层 (my-framework)
* `SecurityConfig.java`: Spring Security 的鉴权过滤网。
* `TokenService.java`: JWT 令牌的安全颁发与校验机制。

---

## 🐍 三、 外部微服务工程 (Python)
专门为了分离高计算量的图像处理业务而独立部署的服务模块，由后端的 `PmsVehicleRecordServiceImpl` 发起 HTTP 调用。
**根目录：** [E:\python_service](file:///E:/python_service)
* **核心脚本**: [app.py](file:///E:/python_service/app.py) （加载离线权重模型，提供图片转车牌号文本的功能）
* **AI 权重模型**: `model/` 目录（存放 HyperLPR3 训练好的离线模型文件）

---

## 🔒 四、 核心安全与隔离架构设计 (RBAC & DataScope)
本系统不仅包含了业务功能，还深度集成了若依的底层权限控制与数据隔离机制，确保多层级物业管理的数据安全性。

### 1. 🎭 动态多级角色体系 (RBAC 控制)
系统采用 **用户(User) -> 角色(Role) -> 权限(Menu/Button)** 的标准 RBAC 模型：
* **动态鉴权注解**：后端所有 Controller 的接口均标有 `@PreAuthorize("@ss.hasPermi('模块:功能:动作')")` 注解。
* **前端按钮级控制**：Vue 页面中的增删改查按钮全部包裹有 `v-hasPermi` 指令。如果当前登录员工的角色没有该权限，对应按钮会被自动隐藏。

### 2. 🛡️ 数据隔离与归属控制 (DataScope)
为了满足“多小区接入且数据互不相通”的 SaaS 化需求，底层具备了强大的数据隔离拦截机制：
* **核心拦截器**：底层核心类 `DataScopeAspect.java`。
* **实现方式**：在 Service 层标注 `@DataScope` 注解后，Mybatis 在执行 SQL 时，底层会自动在您的 `WHERE` 子句后面动态拼接类似 `AND dept_id IN (100, 101)` 的数据权限隔离语句。不同角色只能看到被允许的小区数据。

---

### 💡 开发修改快速索引速查表：

| 想修改什么？ | 前端在哪里改？ | 后端在哪里改？ |
| :--- | :--- | :--- |
| **页面上多加一个输入框/表格列** | `views/xxx/index.vue` 的 `<el-table>` / `<el-form>` | 修改 `domain/实体类.java` 的字段，及对应的 `Mapper.xml` 里的 SQL 语句 |
| **修改系统或物业后台的审核/计算逻辑** | 无需改动 | 寻找对应的 `my-system/.../service/impl/XXXServiceImpl.java` 修改核心逻辑代码 |
| **新增一个左侧导航栏菜单或功能按钮** | 仅需要在前端运行时的【系统管理 -> 菜单管理】中添加节点配置 | 在后端新功能的 Controller 对应方法上加上 `@PreAuthorize` 鉴权注解 |
| **修复/优化车牌号的识别准确率** | 无需改动 | 调整 `E:\python_service\app.py` 内部的图片处理算法或更换新权重模型 |
