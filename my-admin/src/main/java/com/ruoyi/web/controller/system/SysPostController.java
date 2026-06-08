package com.ruoyi.web.controller.system;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.SysPost;
import com.ruoyi.system.service.ISysPostService;

/**
 * 岗位信息操作处理 控制器
 * 用于处理系统中职务/岗位数据的增删改查及导出操作
 * 
 * @author ruoyi
 */
@RestController // 将此类的所有方法的返回值都解析为 JSON 并写入响应体中
@RequestMapping("/system/post") // 处理以 "/system/post" 路径开头的 HTTP 请求
public class SysPostController extends BaseController
{
    /** 岗位服务接口，包含操作岗位的各类业务逻辑和规则校验 */
    @Autowired // 让 Spring 自动将实现类的对象注入进来
    private ISysPostService postService;

    /**
     * 获取岗位列表
     * 支持分页及多条件复合查询
     * 
     * @param post 封装有查询条件（岗位编码、岗位名称、状态等）的实体对象
     * @return 返回包含了分页信息及岗位数据集合的表格对象
     */
    @PreAuthorize("@ss.hasPermi('system:post:list')") // 进行权限拦截，需具备查岗位的权限
    @GetMapping("/list") // 映射 GET 方法
    public TableDataInfo list(SysPost post)
    {
        // 调用底层封装好的分页方法，在随后的第一个 SQL 中自动追加 LIMIT 分页参数
        startPage();
        // 从数据库获取符合条件的岗位列表
        List<SysPost> list = postService.selectPostList(post);
        // 将普通 List 转化为前端可用的带数据总条数(total)的表格信息并返回
        return getDataTable(list);
    }
    
    /**
     * 导出岗位列表到 Excel
     * 
     * @param response HTTP 响应流，用于发送生成的 Excel 文件
     * @param post 导出过滤条件，与查询列表条件的参数结构一致
     */
    @Log(title = "岗位管理", businessType = BusinessType.EXPORT) // 记录行为日志：导出动作
    @PreAuthorize("@ss.hasPermi('system:post:export')") // 验证是否有导出的对应权限
    @PostMapping("/export") // 映射 POST 方法
    public void export(HttpServletResponse response, SysPost post)
    {
        // 1. 查询全部符合条件的数据集合，此处无需分页
        List<SysPost> list = postService.selectPostList(post);
        // 2. 实例化通用的 Excel 工具类，解析 SysPost 类上的 @Excel 注解进行表头映射
        ExcelUtil<SysPost> util = new ExcelUtil<SysPost>(SysPost.class);
        // 3. 将集合数据写入 Excel 文件流并设置工作表名称为 "岗位数据" 返回给前端下载
        util.exportExcel(response, list, "岗位数据");
    }

    /**
     * 根据岗位编号获取详细信息
     * 常用于前端编辑操作时的数据回显
     * 
     * @param postId URL 路径中的岗位 ID
     * @return 包含目标岗位所有详细字段属性的结果对象
     */
    @PreAuthorize("@ss.hasPermi('system:post:query')") // 验证查询单条详细信息的权限
    @GetMapping(value = "/{postId}") // 映射带有路径参数的 GET 方法
    public AjaxResult getInfo(@PathVariable Long postId)
    {
        // 通过调用服务层查出详情后包装为统一成功响应体返回
        return success(postService.selectPostById(postId));
    }

    /**
     * 新增岗位
     * 
     * @param post 接收前端提交的 JSON 数据，并执行基本字段约束（@Validated）
     * @return 返回新增成功与否的结果对象
     */
    @PreAuthorize("@ss.hasPermi('system:post:add')") // 权限校验，要求具备新增岗位权限
    @Log(title = "岗位管理", businessType = BusinessType.INSERT) // 操作日志：新增动作
    @PostMapping // 映射 POST 方法
    public AjaxResult add(@Validated @RequestBody SysPost post)
    {
        // 1. 业务规则校验：全系统内岗位名称不能有重复
        if (!postService.checkPostNameUnique(post))
        {
            return error("新增岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        }
        // 2. 业务规则校验：全系统内岗位编码(code)不能重复
        else if (!postService.checkPostCodeUnique(post))
        {
            return error("新增岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        // 3. 记录当前登录用户作为该记录的创始人
        post.setCreateBy(getUsername());
        // 4. 调用持久层存入数据库中，响应结果(成功即影响行数>0)
        return toAjax(postService.insertPost(post));
    }

    /**
     * 修改岗位
     * 
     * @param post 包含了欲修改数据以及目标 ID 的对象
     * @return 返回更新操作成功与否的结果对象
     */
    @PreAuthorize("@ss.hasPermi('system:post:edit')") // 权限校验：修改岗位权限
    @Log(title = "岗位管理", businessType = BusinessType.UPDATE) // 操作日志：修改动作
    @PutMapping // 映射 PUT 方法
    public AjaxResult edit(@Validated @RequestBody SysPost post)
    {
        // 1. 检查修改后的新名称是否与其他岗位的名称发生冲突
        if (!postService.checkPostNameUnique(post))
        {
            return error("修改岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        }
        // 2. 检查修改后的新编码是否与其他岗位的编码发生冲突
        else if (!postService.checkPostCodeUnique(post))
        {
            return error("修改岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        // 3. 记录当前操作用户的名称作为更新者标记
        post.setUpdateBy(getUsername());
        // 4. 提交数据更新指令并返回
        return toAjax(postService.updatePost(post));
    }

    /**
     * 删除岗位
     * 支持通过逗号拼接的多个 ID 批量删除
     * 
     * @param postIds 从 URL 路径获取的要被删除的岗位 ID 集合
     * @return 返回操作结果对象
     */
    @PreAuthorize("@ss.hasPermi('system:post:remove')") // 权限校验：删除权限
    @Log(title = "岗位管理", businessType = BusinessType.DELETE) // 操作日志：删除动作
    @DeleteMapping("/{postIds}") // 映射 DELETE 方法
    public AjaxResult remove(@PathVariable Long[] postIds)
    {
        // 批量删除指定的岗位记录，并在服务层校验该岗位是否有关联用户，有则抛出异常阻止删除
        return toAjax(postService.deletePostByIds(postIds));
    }

    /**
     * 获取岗位选择框列表
     * 通常提供给前端在创建用户或其他业务中分配岗位时，渲染出的基础下拉选项
     * 
     * @return 包含所有系统中记录的可用岗位集合
     */
    @GetMapping("/optionselect") // 映射 GET 方法，无需特意设防太严密，供公共数据获取
    public AjaxResult optionselect()
    {
        // 仅查询可用的所有岗位集合
        List<SysPost> posts = postService.selectPostAll();
        // 将岗位集合放置在标准的响应结构中并返回
        return success(posts);
    }
}
