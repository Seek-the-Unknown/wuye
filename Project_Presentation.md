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
