# 智慧物业管理系统 - 核心功能代码实现一览表

这份文档直接抛弃虚构概念，干货直击“什么功能写在哪段代码里，实现了什么效果”。答辩时可直接对照以下代码文件和功能进行陈述。

---

## 1. 【安全核心】业主端的数据越权防护与隔离
**功能描述**：确保不同业主登录门户端时，只能看到自己的访客和报修数据，防止 A 业主通过抓包篡改接口参数窃取 B 业主的数据。

### 代码实现位置：
- **拦截与身份注入层 (Controller)**：
  - 文件：`my-admin/src/main/java/com/ruoyi/web/controller/property/PmsPortalController.java`
  - 实现：在 `myVisitor()` 和 `myRepair()` 接口方法中，通过 `SecurityUtils.getUserId()` 获取当前真实登录的账号，反查其对应的 `OwnerId`（业主ID），并将这个 `OwnerId` 强行塞入查询实体类 `query` 中。不管前端怎么传参数，后端以这个为准。
- **底层数据库拦截层 (MyBatis XML)**：
  - 文件：`my-system/src/main/resources/mapper/property/PmsVisitorMapper.xml`
  - 实现：在 `<select id="selectPmsVisitorList">` 查询标签内，加入动态 SQL 判断：
    ```xml
    <where>
        <if test="ownerId != null">and v.owner_id = #{ownerId}</if>
    </where>
    ```
  - **效果**：数据库在执行 `SELECT` 时，条件被焊死为 `owner_id = 当前业主ID`，从物理查询层面彻底切断了数据越权的可能性。

---

## 2. 【权限核心】物业管理员（新增角色）动态权限放行
**功能描述**：系统新增“物业管理员”角色后，页面上点击各类按钮均提示“没有权限(403)”。如何让这个角色绕开繁琐的底层菜单配置，直接获取整个物业模块的管理权？

### 代码实现位置：
- **权限校验服务组件**：
  - 文件：`my-framework/src/main/java/com/ruoyi/framework/web/service/PermissionService.java`
  - 实现：我们重写了 `hasPermissions(Set<String> permissions, String permission)` 这个核心验权方法。
    ```java
    // 伪代码展示实现逻辑
    if (SecurityUtils.getLoginUser().getUser().getRoles().stream().anyMatch(r -> "property_admin".equals(r.getRoleKey()))) {
        if (permission.startsWith("property:")) {
            return true; // 如果是物业管理员，且请求的操作是 property: 开头，直接放行
        }
    }
    ```
  - **效果**：前端带有 `@PreAuthorize("@ss.hasPermi('property:house:add')")` 等权限拦截的 Controller 接口，在遇到 `property_admin` 角色时自动无条件放行。实现了“角色即权限”的快速动态注入。

---

## 3. 【业务核心】后台公告推送到前端首页数据不同步
**功能描述**：物业在后台发布了最新公告，但业主门户端的轮播图和通知栏却不显示或者显示老旧的错误数据。

### 代码实现位置：
- **前后端接口打通与联调**：
  - 后端接口：直接重用并暴露了 `SysNoticeController.java` 中的列表查询接口，或者在 `PmsPortalController.java` 中新增公开的 `getNoticeList` 接口。
  - 前端请求：修改 `RuoYi-Vue3/src/views/property/portal/index.vue`。
  - 实现：在 Vue 组件的 `onMounted()` 生命周期钩子中，调用重新编写的 API：
    ```javascript
    import { listNotice } from "@/api/system/notice";
    // 页面加载时拉取后台最新的非草稿状态公告
    listNotice({ status: "0" }).then(response => {
       noticeList.value = response.rows; // 将真实数据绑定到门户页面的跑马灯/列表组件上
    });
    ```
  - **效果**：彻底修复了前端写死假数据或调用错接口的问题，实现了后台（发布）到前台（展示）的数据大一统。

---

## 4. 【业务核心】收费模块计算与事务强一致性
**功能描述**：物业财务在后台进行“批量生成账单”操作，或者业主缴纳费用时，如何保证不出现“扣费但没账单”的脏数据？

### 代码实现位置：
- **业务逻辑与事务控制层**：
  - 文件：`my-system/src/main/java/com/ruoyi/property/service/impl/PmsFeeServiceImpl.java`
  - 实现：在涉及数据库多表写操作的方法（如 `generateFee()` 或 `payFee()`）头部，强制加上 `@Transactional(rollbackFor = Exception.class)`。
  - **效果**：
    1. 方法内部会执行两步：写入 `pms_fee_record` (账单明细)，如果有预存款则扣减 `pms_owner` (业主余额)。
    2. Spring 的 AOP 切面会接管此方法，一旦上述任意一句 SQL 报错（如网络中断），所有对数据库的更改将全部回滚。

---

## 5. 【数据核心】基础数据的三表联结绑定映射
**功能描述**：前端页面上只看到“业主管理”和“房屋管理”，代码底层是怎么把账号、业主实体、房屋实体挂钩的？

### 代码实现与表关联关系：
- 这部分主要体现在 MyBatis 的联表查询以及关联表上。
  - **SysUser (系统用户表)**：保存登录账号（如张三注册了账号 `zhangsan`）。
  - **PmsOwner (业主表)**：保存业主真实资料库（张三的身份证号、手机号）。
  - **PmsHouse (房屋房间表)**：保存资产（例如 1栋2单元304室）。
- **关系映射代码**：在 `PmsHouseMapper.xml` 或者对应的业务逻辑中，通过维护关系表 `pms_user_owner` 或者直接外键关联。业务层判断：某手机号注册的 `SysUser` -> 自动绑定同手机号的 `PmsOwner` -> 此 `PmsOwner` 对应的 `PmsHouse`，最终在业主端成功渲染出该业主的私有房产列表。

---

## 6. 【业务核心】业务模块字典的深度集成
**功能描述**：满足“系统实现中至少有一个业务模块中体现字典的使用”的要求。在“投诉与建议管理”业务中，引入并使用字典动态管理其“处理状态”。

### 代码实现位置：
- **后端数据库层**：
  - 在 `sys_dict_type` 和 `sys_dict_data` 表中插入了类型名为 `pms_complaint_status` 的字典数据（0-待处理, 1-处理中, 2-已回复, 3-已关闭）。
- **前端视图层**：
  - 文件：`RuoYi-Vue3/src/views/property/complaint/index.vue`
  - 实现：使用 Vue 的 `const { pms_complaint_status } = proxy.useDict('pms_complaint_status')` 钩子获取字典数据。在列表表格中使用 `<dict-tag :options="pms_complaint_status" :value="scope.row.handleStatus" />` 组件进行数据动态回显；在表单下拉框中使用 `<el-option v-for="dict in pms_complaint_status" :label="dict.label" :value="dict.value" />` 进行动态绑定。

---

## 7. 【数据流转】自实现的业主数据 Excel 导入与解析
**功能描述**：满足“至少有一个业务模块中包含自实现的导出、导入功能”的要求。在“业主管理”模块中，实现了贴合业务逻辑的自实现 Excel 模板下载与数据导入解析功能。

### 代码实现位置：
- **前端交互层**：
  - 文件：`RuoYi-Vue3/src/views/property/owner/index.vue`
  - 实现：通过 `<el-upload>` 组件编写了弹窗式的拖拽文件上传模块，并添加了 `importTemplate()` 模板下载请求接口，以及 `handleFileUploadProgress` 文件流传输回调逻辑。
- **后端解析与入库层 (Controller)**：
  - 文件：`my-admin/src/main/java/com/ruoyi/web/controller/property/PmsOwnerController.java`
  - 实现：独立手写了 `importData()` 接口方法。通过 `ExcelUtil` 工具类将前端传入的 `MultipartFile` 文件流反序列化为 `List<PmsOwner>` 集合。然后在代码中使用 `for` 循环手动对数据进行遍历，最后调用 `ownerMapper.insertPmsOwner()` 批量落库。如果发生异常（如由于身份证号过长导致数据库报错），利用 `try-catch` 捕获并将底层具体报错原因拼接返回给前端（例如：“业主 张兰兰 导入失败：Data truncation: Data too long for column 'id_card'”）。

---

## 8. 【角色体系】两类角色隔离体系与普通用户自助注册
**功能描述**：满足“至少包含管理员和普通用户两类角色，且支持普通用户的自注册”的要求。系统不仅内置了后台管理员和物业员工等角色，还彻底打通了外部普通住户（业主）的开放式自助注册入口。

### 代码实现位置：
- **底层基础配置放行**：
  - 表数据配置：直接深入到系统的 `sys_config` 参数配置表中，将键值为 `sys.account.registerUser` 的配置项显式修改并激活为 `true`。
- **前端注册视图与接口路由**：
  - 文件：`RuoYi-Vue3/src/views/register.vue` 及其对应的路由 `src/router/index.js`。
  - 实现：在前端检测到注册功能开启后，极简炫酷的登录页面自动暴露出“立即注册”入口。普通外部用户填写用户名、密码后，直接调用 `register()` 接口。
- **注册业务逻辑流转 (Service)**：
  - 文件：`my-framework/src/main/java/com/ruoyi/framework/web/service/SysRegisterService.java`
  - 实现：用户注册成功后，系统会在 `sys_user` 表自动为其创建基础账号，赋予“普通用户”的初始权限。随后，根据其填写的真实信息（或后续通过物业后台绑定），与 `PmsOwner` 和 `PmsHouse` 数据挂钩，从而实现内部管理员（拥有全局系统路由）和外部普通用户（仅拥有业主个人前台权限）的角色完全隔离。
