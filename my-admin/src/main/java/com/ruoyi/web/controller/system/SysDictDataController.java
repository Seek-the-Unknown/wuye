package com.ruoyi.web.controller.system;

import java.util.ArrayList;
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
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.service.ISysDictDataService;
import com.ruoyi.system.service.ISysDictTypeService;

/**
 * 数据字典信息 控制器
 * 处理系统数据字典数据的相关请求，如查询、新增、修改、删除和导出等。
 * 字典数据是字典类型下属的具体选项（如字典类型"性别"对应的字典数据有"男"、"女"、"未知"等）
 * 
 * @author ruoyi
 */
@RestController // 标识这是一个RESTful控制器，所有响应都会自动序列化为JSON返回给前端
@RequestMapping("/system/dict/data") // 映射请求的基础路径
public class SysDictDataController extends BaseController
{
    /** 数据字典服务接口，用于处理字典具体选项数据（SysDictData）的业务逻辑 */
    @Autowired // 自动注入对应的服务实现类
    private ISysDictDataService dictDataService;

    /** 数据字典类型服务接口，用于处理字典类型（SysDictType）的业务逻辑 */
    @Autowired // 自动注入类型服务实现类
    private ISysDictTypeService dictTypeService;

    /**
     * 查询字典数据列表
     * 支持前端表格的分页展示
     * 
     * @param dictData 包含前端传递的查询条件（如所属字典类型、字典标签名、状态等）
     * @return 包含字典数据列表的表格分页数据对象(TableDataInfo)
     */
    @PreAuthorize("@ss.hasPermi('system:dict:list')") // 权限校验，要求具备查询字典列表权限
    @GetMapping("/list") // 处理GET请求
    public TableDataInfo list(SysDictData dictData)
    {
        // 开启分页功能，利用MyBatis PageHelper拦截后续的第一次查询，自动注入limit/offset参数
        startPage();
        // 根据条件查询字典数据列表集合
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        // 封装集合为标准的前端表格结构并返回，其中包含了总条数(total)和行数据(rows)
        return getDataTable(list);
    }

    /**
     * 导出字典数据列表到Excel
     * 将查询出的字典项通过文件流形式供用户下载
     * 
     * @param response HTTP响应对象，用于写入生成的 Excel 文件流
     * @param dictData 包含查询条件的数据字典对象
     */
    @Log(title = "字典数据", businessType = BusinessType.EXPORT) // 操作日志：记录导出动作
    @PreAuthorize("@ss.hasPermi('system:dict:export')") // 权限校验：具备导出权限
    @PostMapping("/export") // 处理POST请求
    public void export(HttpServletResponse response, SysDictData dictData)
    {
        // 1. 根据条件全量查询出符合要求的字典数据（不分页）
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        // 2. 创建基于SysDictData实体的Excel工具类实例，以便解析@Excel注解的配置
        ExcelUtil<SysDictData> util = new ExcelUtil<SysDictData>(SysDictData.class);
        // 3. 将列表数据生成为名为"字典数据"的工作表，写入到HTTP响应流(response)中
        util.exportExcel(response, list, "字典数据");
    }

    /**
     * 查询字典数据详细信息
     * 用于前端修改单个字典数据时，在弹窗中回显原有信息
     * 
     * @param dictCode 路径变量，指定要查询的字典数据主键ID
     * @return 包含字典数据详细信息的AjaxResult对象
     */
    @PreAuthorize("@ss.hasPermi('system:dict:query')") // 权限校验：查询详情权限
    @GetMapping(value = "/{dictCode}") // 处理GET请求，REST风格带有ID参数
    public AjaxResult getInfo(@PathVariable Long dictCode)
    {
        // 通过ID查询出单条字典数据记录，并包装到success的结果中返回
        return success(dictDataService.selectDictDataById(dictCode));
    }

    /**
     * 根据字典类型查询字典数据信息
     * 常用于前端页面下拉框（Select）、单选框（Radio）等组件动态加载指定的字典选项列表
     * 
     * @param dictType 路径变量，指定要获取的字典类型标识（如 sys_user_sex）
     * @return 包含对应字典数据列表的AjaxResult对象
     */
    @GetMapping(value = "/type/{dictType}") // 处理无严格权限控制的GET请求，方便基础数据加载
    public AjaxResult dictType(@PathVariable String dictType)
    {
        // 1. 根据字典类型调用服务层查询其下的所有可用数据项。底层通常会优先从Redis缓存中获取，提升性能
        List<SysDictData> data = dictTypeService.selectDictDataByType(dictType);
        // 2. 如果因为某些原因（如无该字典或无子项）查出的集合为null，则初始化一个空列表，避免前端渲染报错或引起空指针异常
        if (StringUtils.isNull(data))
        {
            data = new ArrayList<SysDictData>();
        }
        // 3. 返回包含字典选项集合的成功结果
        return success(data);
    }

    /**
     * 新增字典数据
     * 
     * @param dict 前端传来的包含新增信息的字典数据JSON，使用@Validated进行基础的非空、长度等校验
     * @return 返回新增操作结果状态
     */
    @PreAuthorize("@ss.hasPermi('system:dict:add')") // 权限校验：新增权限
    @Log(title = "字典数据", businessType = BusinessType.INSERT) // 记录日志：新增动作
    @PostMapping // 处理POST请求
    public AjaxResult add(@Validated @RequestBody SysDictData dict)
    {
        // 1. 设置当前登录系统用户的用户名为该条数据的创建者
        dict.setCreateBy(getUsername());
        // 2. 调用服务层将字典数据持久化到数据库中（并同步更新相关缓存），将返回的影响行数转换为标准Ajax结果响应
        return toAjax(dictDataService.insertDictData(dict));
    }

    /**
     * 修改保存字典数据
     * 
     * @param dict 前端传来的带有ID等修改信息的JSON字典对象
     * @return 返回修改操作结果状态
     */
    @PreAuthorize("@ss.hasPermi('system:dict:edit')") // 权限校验：修改权限
    @Log(title = "字典数据", businessType = BusinessType.UPDATE) // 记录日志：修改动作
    @PutMapping // 处理PUT请求
    public AjaxResult edit(@Validated @RequestBody SysDictData dict)
    {
        // 1. 设置当前登录用户为更新者标识
        dict.setUpdateBy(getUsername());
        // 2. 调用服务层更新数据库对应记录（并同步刷新涉及到的Redis缓存），返回处理结果
        return toAjax(dictDataService.updateDictData(dict));
    }

    /**
     * 删除字典数据
     * 支持多选批量删除
     * 
     * @param dictCodes 路径中的待删除的主键数组，通过逗号分隔自动绑定到Long数组
     * @return 返回删除操作结果状态
     */
    @PreAuthorize("@ss.hasPermi('system:dict:remove')") // 权限校验：删除权限
    @Log(title = "字典类型", businessType = BusinessType.DELETE) // 记录日志：删除动作
    @DeleteMapping("/{dictCodes}") // 处理DELETE请求
    public AjaxResult remove(@PathVariable Long[] dictCodes)
    {
        // 1. 调用服务层根据传入的主键数组批量删除数据库对应的字典项，并清理相关缓存
        dictDataService.deleteDictDataByIds(dictCodes);
        // 2. 返回通用成功标识
        return success();
    }
}
