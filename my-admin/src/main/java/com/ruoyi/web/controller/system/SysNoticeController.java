package com.ruoyi.web.controller.system;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.service.ISysNoticeService;

/**
 * 公告 信息操作处理 控制器
 * 负责系统内部通知公告的增删改查等业务请求处理
 * 
 * @author ruoyi
 */
@RestController // 标识这是一个RESTful控制器，方法的返回值将自动序列化为JSON格式返回给前端
@RequestMapping("/system/notice") // 映射该控制器的基础请求路径为 "/system/notice"
public class SysNoticeController extends BaseController
{
    /** 通知公告服务接口，处理与公告相关的数据持久化和业务逻辑 */
    @Autowired // 自动装配对应的实现类
    private ISysNoticeService noticeService;

    /**
     * 获取通知公告列表
     * 支持分页和条件过滤
     * 
     * @param notice 包含查询条件（如公告标题、公告类型、创建人等）的公告实体对象
     * @return 包含系统公告列表及分页信息(total)的表格数据对象
     */
    @PreAuthorize("@ss.hasPermi('system:notice:list')") // 权限校验，要求当前用户具备 'system:notice:list' 权限
    @GetMapping("/list") // 映射 GET 请求
    public TableDataInfo list(SysNotice notice)
    {
        // 开启 MyBatis 的 PageHelper 分页拦截功能，自动拦截并拼装 limit/offset 语句
        startPage();
        // 调用服务层，根据前端传入的条件查询符合要求的公告列表
        List<SysNotice> list = noticeService.selectNoticeList(notice);
        // 将查询结果集转化为适用于前端展示的数据表格格式并返回
        return getDataTable(list);
    }

    /**
     * 根据通知公告编号获取详细信息
     * 用于前端修改页面回显数据，或查看单条公告的内容详情
     * 
     * @param noticeId URL路径变量中的公告ID
     * @return 包含单条通知公告完整信息的 AjaxResult 对象
     */
    @PreAuthorize("@ss.hasPermi('system:notice:query')") // 权限校验，要求具备公告查询权限
    @GetMapping(value = "/{noticeId}") // 映射 GET 请求
    public AjaxResult getInfo(@PathVariable Long noticeId)
    {
        // 根据主键ID调用服务层查询单个公告实体，并将其包装为成功的结果对象返回
        return success(noticeService.selectNoticeById(noticeId));
    }

    /**
     * 新增通知公告
     * 
     * @param notice 前端传入的包含新增数据的JSON对象，通过 @Validated 进行基础非空和长度校验
     * @return 包含操作执行状态的 AjaxResult 对象
     */
    @PreAuthorize("@ss.hasPermi('system:notice:add')") // 权限校验，要求具备新增公告权限
    @Log(title = "通知公告", businessType = BusinessType.INSERT) // 记录系统操作日志，标记为新增操作
    @PostMapping // 映射 POST 请求
    public AjaxResult add(@Validated @RequestBody SysNotice notice)
    {
        // 将当前登录用户设置为该条公告的创建者
        notice.setCreateBy(getUsername());
        // 调用服务层将公告记录插入数据库，如果影响行数大于0则返回成功，否则失败
        return toAjax(noticeService.insertNotice(notice));
    }

    /**
     * 修改通知公告
     * 
     * @param notice 包含修改字段数据及公告ID的实体对象
     * @return 包含操作执行状态的 AjaxResult 对象
     */
    @PreAuthorize("@ss.hasPermi('system:notice:edit')") // 权限校验，要求具备修改公告权限
    @Log(title = "通知公告", businessType = BusinessType.UPDATE) // 记录系统操作日志，标记为更新操作
    @PutMapping // 映射 PUT 请求
    public AjaxResult edit(@Validated @RequestBody SysNotice notice)
    {
        // 将当前登录用户标记为该记录的更新者
        notice.setUpdateBy(getUsername());
        // 调用服务层执行数据库的 Update 语句，并返回结果
        return toAjax(noticeService.updateNotice(notice));
    }

    /**
     * 删除通知公告
     * 支持批量删除操作
     * 
     * @param noticeIds 通过逗号分隔的 URL 路径参数自动绑定的公告ID数组
     * @return 包含操作执行状态的 AjaxResult 对象
     */
    @PreAuthorize("@ss.hasPermi('system:notice:remove')") // 权限校验，要求具备删除公告权限
    @Log(title = "通知公告", businessType = BusinessType.DELETE) // 记录系统操作日志，标记为删除操作
    @DeleteMapping("/{noticeIds}") // 映射 DELETE 请求
    public AjaxResult remove(@PathVariable Long[] noticeIds)
    {
        // 调用服务层，根据主键数组批量物理删除指定的公告记录，并返回操作结果
        return toAjax(noticeService.deleteNoticeByIds(noticeIds));
    }
}
