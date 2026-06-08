# 📂 全功能系统架构与代码目录分布图 (完整版)

这份完整目录涵盖了整个**智慧物业管理系统**及底层**若依 (RuoYi-Vue) 核心脚手架**的所有功能模块。
> [!TIP]
> 所有的文件及文件夹路径都可以直接点击，以在您的编辑器中快速打开。

---

## 🎨 一、 前端核心工程 (Vue3 + Element Plus)
**根目录：** [E:\RuoYi-Vue\RuoYi-Vue3](file:///E:/RuoYi-Vue/RuoYi-Vue3)

### 1. 🏢 物业管理业务模块 (Property)
这是本系统最核心的定制化业务层。
* **🏘️ 小区档案管理**
  * 视图：[src/views/property/community/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/property/community/index.vue)
  * API：[src/api/property/community.js](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/api/property/community.js)
* **🏢 楼宇及房屋管理**
  * 视图：[src/views/property/room/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/property/room/index.vue)
  * API：[src/api/property/room.js](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/api/property/room.js)
* **👨‍👩‍👧‍👦 业主信息登记**
  * 视图：[src/views/property/owner/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/property/owner/index.vue)
  * API：[src/api/property/owner.js](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/api/property/owner.js)
* **🅿️ 停车位绑定与管理**
  * 视图：[src/views/property/parking/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/property/parking/index.vue)
  * API：[src/api/property/parking.js](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/api/property/parking.js)
* **🚗 车辆进出台账 (联动 Python 识别)**
  * 总台账：[src/views/property/vehicleRecord/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/property/vehicleRecord/index.vue)
  * 车辆入场：[src/views/property/vehicleRecord/enter.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/property/vehicleRecord/enter.vue)
  * 出场结算：[src/views/property/vehicleRecord/exit.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/property/vehicleRecord/exit.vue)
  * API：[src/api/property/vehicleRecord.js](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/api/property/vehicleRecord.js)
* **💰 财务收费类型配置**
  * 视图：[src/views/property/feeType/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/property/feeType/index.vue)
  * API：[src/api/property/feeType.js](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/api/property/feeType.js)
* **💸 费用台账与缴费管理**
  * 视图：[src/views/property/feeRecord/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/property/feeRecord/index.vue)
  * API：[src/api/property/feeRecord.js](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/api/property/feeRecord.js)
* **🔧 报修工单处理流**
  * 视图：[src/views/property/repair/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/property/repair/index.vue)
  * API：[src/api/property/repair.js](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/api/property/repair.js)

### 2. ⚙️ 系统管理基础模块 (System)
控制系统的菜单、人员、权限等底层逻辑。
* **👥 用户管理**: [src/views/system/user/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/system/user/index.vue)
* **🔑 角色与权限**: [src/views/system/role/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/system/role/index.vue)
* **📁 菜单与路由管理**: [src/views/system/menu/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/system/menu/index.vue)
* **🏢 部门与层级管理**: [src/views/system/dept/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/system/dept/index.vue)
* **📖 数据字典**: [src/views/system/dict/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/system/dict/index.vue)
* **⚙️ 系统参数配置**: [src/views/system/config/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/system/config/index.vue)

### 3. 🖥️ 系统监控与工具模块 (Monitor / Tool)
* **📊 定时任务管理**: [src/views/monitor/job/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/monitor/job/index.vue)
* **📜 系统操作日志**: [src/views/monitor/operlog/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/monitor/operlog/index.vue)
* **🚀 代码生成器**: [src/views/tool/gen/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/views/tool/gen/index.vue)

### 4. 🌐 前端公共与核心配置
* **动态路由与权限守卫**: [src/permission.js](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/permission.js)
* **入口主页面框架**: [src/layout/index.vue](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/layout/index.vue)
* **通用 Axios 请求封装**: [src/utils/request.js](file:///E:/RuoYi-Vue/RuoYi-Vue3/src/utils/request.js)

---

## ☕ 二、 后端核心工程 (Java Spring Boot 多模块)
**根目录：** [E:\RuoYi-Vue](file:///E:/RuoYi-Vue)

### 1. 🌐 Web 接口层 (my-admin)
直接与前端交互的 API 控制器。
* **物业业务控制器**：[my-admin/src/main/java/com/ruoyi/web/controller/property/](file:///E:/RuoYi-Vue/my-admin/src/main/java/com/ruoyi/web/controller/property/)
  * `PmsCommunityController.java`, `PmsRoomController.java`, `PmsOwnerController.java` 等。
* **系统基础控制器**：[my-admin/src/main/java/com/ruoyi/web/controller/system/](file:///E:/RuoYi-Vue/my-admin/src/main/java/com/ruoyi/web/controller/system/)

### 2. 🧠 业务逻辑层 (my-system)
业务处理核心、实体对象、与数据库交互的 Mapper。
* **物业业务层 (Property)**: [my-system/src/main/java/com/ruoyi/property/](file:///E:/RuoYi-Vue/my-system/src/main/java/com/ruoyi/property/)
  * **实体模型 (Domain)**: `domain/` 下包含所有对象结构，如 `PmsVehicleRecord.java`。
  * **服务接口与实现 (Service)**: `service/` 和 `service/impl/`，主要负责复杂逻辑（比如车辆进出场的计费逻辑）。
  * **Mapper 接口**: `mapper/` 供 Mybatis 调用。
* **系统业务层 (System)**: [my-system/src/main/java/com/ruoyi/system/](file:///E:/RuoYi-Vue/my-system/src/main/java/com/ruoyi/system/)

### 3. 💾 数据库交互层 (Mybatis XML)
定义复杂 SQL 查询和存取逻辑的 XML 映射文件。
* **物业 SQL Mapper**: [my-system/src/main/resources/mapper/property/](file:///E:/RuoYi-Vue/my-system/src/main/resources/mapper/property/)
* **系统 SQL Mapper**: [my-system/src/main/resources/mapper/system/](file:///E:/RuoYi-Vue/my-system/src/main/resources/mapper/system/)

### 4. 🛡️ 核心框架与安全配置层 (my-framework)
负责启动配置、安全拦截、Redis缓存、权限认证等。
* **跨域与 Web 拦截器**: [my-framework/src/main/java/com/ruoyi/framework/config/ResourcesConfig.java](file:///E:/RuoYi-Vue/my-framework/src/main/java/com/ruoyi/framework/config/ResourcesConfig.java)
* **Spring Security 核心安全配置**: [my-framework/src/main/java/com/ruoyi/framework/config/SecurityConfig.java](file:///E:/RuoYi-Vue/my-framework/src/main/java/com/ruoyi/framework/config/SecurityConfig.java)
* **Token 颁发与验证服务**: [my-framework/src/main/java/com/ruoyi/framework/web/service/TokenService.java](file:///E:/RuoYi-Vue/my-framework/src/main/java/com/ruoyi/framework/web/service/TokenService.java)

---

## 🐍 三、 外部微服务工程 (Python)
专门为了分离高计算量的图像处理业务而设计的轻量级微服务。
**子目录：** [python_service](file:///E:/RuoYi-Vue/python_service)

* **🚦 API 入口与控制逻辑**: [python_service/app.py](file:///E:/RuoYi-Vue/python_service/app.py) （接收图片，解码，并返回车牌号文本）
* **🧠 模型资源存放路径**: [python_service/model/](file:///E:/RuoYi-Vue/python_service/model/) （存放 HyperLPR3 的离线权重数据，避免依赖 C 盘和重复下载，已被配置忽略上传至远端）

---

### 💡 常见的开发修改场景对照表：

| 想修改什么？ | 前端在哪里改？ | 后端在哪里改？ |
| :--- | :--- | :--- |
| **页面上多加一个输入框/表格列** | `views/property/.../index.vue` 里面的 `<el-table>` 或 `<el-form>` | 在 `domain/PmsXXX.java` 加字段，同时修改 `mapper/.../PmsXXXMapper.xml` 里的 SQL |
| **修改计费规则或后台审核流** | 无需大改 | 在 `my-system/.../service/impl/PmsXXXServiceImpl.java` 中改写逻辑代码 |
| **新增一个菜单或按钮** | 在“系统管理 -> 菜单管理”中配置后，在页面上加 `v-hasPermi` 权限指令 | 在 Controller 方法上添加 `@PreAuthorize("@ss.hasPermi('...')")` 注解 |
| **修改车牌识别准确度与算法** | 无需大改 | 在 `E:\python_service\app.py` 里的 `catcher` 传参进行修改 |
