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
import com.ruoyi.common.core.domain.entity.SysDictType;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.service.ISysDictTypeService;

/**
 * 数据字典类型 控制器
 * 处理系统数据字典类型的相关请求，如查询、新增、修改、删除和缓存刷新等
 * 
 * @author ruoyi
 */
@RestController // 声明这是一个RESTful控制器，所有响应结果都会自动转换为JSON格式
@RequestMapping("/system/dict/type") // 映射该控制器的基础请求路径
public class SysDictTypeController extends BaseController
{
    /** 数据字典类型服务接口，用于处理字典类型的核心业务逻辑 */
    @Autowired // 自动装配对应的Service实现类
    private ISysDictTypeService dictTypeService;

    /**
     * 获取字典类型列表
     * 支持前端带条件的分页查询
     * 
     * @param dictType 包含查询条件（如字典名称、字典类型、状态、时间范围等）的实体对象
     * @return 包含字典类型列表及分页信息(total)的表格数据对象
     */
    @PreAuthorize("@ss.hasPermi('system:dict:list')") // 权限校验，需拥有 'system:dict:list' 权限才能访问
    @GetMapping("/list") // 处理 GET 请求
    public TableDataInfo list(SysDictType dictType)
    {
        // 开启 MyBatis 的分页功能，自动拦截并追加 limit 语句
        startPage();
        // 根据条件查询符合要求的字典类型列表数据
        List<SysDictType> list = dictTypeService.selectDictTypeList(dictType);
        // 将查询结果包装为前端组件所需的数据结构（TableDataInfo）后返回
        return getDataTable(list);
    }

    /**
     * 导出字典类型列表到 Excel 文件
     * 
     * @param response HTTP 响应对象，用于通过输出流向客户端返回文件
     * @param dictType 包含查询条件的字典类型对象，用于筛选要导出的数据
     */
    @Log(title = "字典类型", businessType = BusinessType.EXPORT) // 记录操作日志，业务类型为导出
    @PreAuthorize("@ss.hasPermi('system:dict:export')") // 权限校验，需拥有导出权限
    @PostMapping("/export") // 处理 POST 请求
    public void export(HttpServletResponse response, SysDictType dictType)
    {
        // 1. 根据条件查询出所有的字典类型列表（不分页）
        List<SysDictType> list = dictTypeService.selectDictTypeList(dictType);
        // 2. 实例化 Excel 工具类，指定泛型为 SysDictType，以便反射读取字段上的 @Excel 注解
        ExcelUtil<SysDictType> util = new ExcelUtil<SysDictType>(SysDictType.class);
        // 3. 执行导出操作，生成名为 "字典类型" 的工作表，并通过 response 输出文件流
        util.exportExcel(response, list, "字典类型");
    }

    /**
     * 查询单个字典类型详细信息
     * 用于前端点击修改时的数据回显
     * 
     * @param dictId URL 路径参数，即要查询的字典类型ID
     * @return 包含单条字典类型详细信息的 AjaxResult 对象
     */
    @PreAuthorize("@ss.hasPermi('system:dict:query')") // 权限校验，需拥有查询详情的权限
    @GetMapping(value = "/{dictId}") // 处理 GET 请求
    public AjaxResult getInfo(@PathVariable Long dictId)
    {
        // 根据字典类型ID查询字典信息，并封装在成功的 AjaxResult 中返回
        return success(dictTypeService.selectDictTypeById(dictId));
    }

    /**
     * 新增字典类型
     * 
     * @param dict 包含新增信息的字典类型对象，通过 @Validated 进行字段校验
     * @return 返回新增操作成功与否的结果对象
     */
    @PreAuthorize("@ss.hasPermi('system:dict:add')") // 权限校验，需拥有新增权限
    @Log(title = "字典类型", businessType = BusinessType.INSERT) // 记录操作日志，业务类型为新增
    @PostMapping // 处理 POST 请求
    public AjaxResult add(@Validated @RequestBody SysDictType dict)
    {
        // 1. 校验字典类型标识（dictType字段）是否全局唯一
        if (!dictTypeService.checkDictTypeUnique(dict))
        {
            // 如果不唯一，说明已存在相同标识的字典，返回错误提示
            return error("新增字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        // 2. 将当前登录的用户账号设置为这条新记录的创建人
        dict.setCreateBy(getUsername());
        // 3. 调用服务层插入数据（包含存入数据库及更新缓存），并返回受影响行数对应的结果
        return toAjax(dictTypeService.insertDictType(dict));
    }

    /**
     * 修改保存字典类型
     * 
     * @param dict 包含修改信息的字典类型对象，通过 @Validated 进行基本校验
     * @return 返回修改操作成功与否的结果对象
     */
    @PreAuthorize("@ss.hasPermi('system:dict:edit')") // 权限校验，需拥有修改权限
    @Log(title = "字典类型", businessType = BusinessType.UPDATE) // 记录操作日志，业务类型为修改
    @PutMapping // 处理 PUT 请求
    public AjaxResult edit(@Validated @RequestBody SysDictType dict)
    {
        // 1. 校验修改后的字典类型标识是否与系统中其他已存在的记录冲突
        if (!dictTypeService.checkDictTypeUnique(dict))
        {
            // 如果存在冲突，返回错误提示
            return error("修改字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        // 2. 设置当前登录用户为更新操作人
        dict.setUpdateBy(getUsername());
        // 3. 调用服务层更新数据（并同步更新相关缓存），返回更新结果
        return toAjax(dictTypeService.updateDictType(dict));
    }

    /**
     * 删除字典类型
     * 
     * @param dictIds 路径参数，由逗号分隔的字典类型ID数组
     * @return 返回删除操作成功与否的结果对象
     */
    @PreAuthorize("@ss.hasPermi('system:dict:remove')") // 权限校验，需拥有删除权限
    @Log(title = "字典类型", businessType = BusinessType.DELETE) // 记录操作日志，业务类型为删除
    @DeleteMapping("/{dictIds}") // 处理 DELETE 请求
    public AjaxResult remove(@PathVariable Long[] dictIds)
    {
        // 调用服务层批量删除选中的字典类型，内部会校验字典是否已被分配使用，并在删除后清理缓存
        dictTypeService.deleteDictTypeByIds(dictIds);
        // 如果没有抛出异常，则代表删除成功，返回通用成功结果
        return success();
    }

    /**
     * 刷新字典缓存
     * 手动触发清除 Redis 中所有的字典缓存数据，并重新从数据库加载最新字典数据
     * 
     * @return 返回刷新成功状态的结果对象
     */
    @PreAuthorize("@ss.hasPermi('system:dict:remove')") // 权限校验，要求具备相应的管理权限
    @Log(title = "字典类型", businessType = BusinessType.CLEAN) // 记录操作日志，业务类型为清空
    @DeleteMapping("/refreshCache") // 处理 DELETE 请求
    public AjaxResult refreshCache()
    {
        // 调用服务层执行重置缓存的逻辑
        dictTypeService.resetDictCache();
        // 返回成功结果
        return success();
    }

    /**
     * 获取字典选择框列表
     * 提供给前端新增字典数据（SysDictData）时，选择所属"字典类型"的下拉列表数据源
     * 
     * @return 包含所有可用字典类型实体的 AjaxResult 对象
     */
    @GetMapping("/optionselect") // 处理 GET 请求
    public AjaxResult optionselect()
    {
        // 查询数据库中所有的字典类型集合
        List<SysDictType> dictTypes = dictTypeService.selectDictTypeAll();
        // 封装集合为成功的结果对象并返回
        return success(dictTypes);
    }
}
