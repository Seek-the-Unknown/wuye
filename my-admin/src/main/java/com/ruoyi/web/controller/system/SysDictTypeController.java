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
 * 数据字典信息
 * 处理系统数据字典类型的相关请求，如查询、新增、修改、删除和缓存刷新等
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/dict/type")
public class SysDictTypeController extends BaseController
{
    /** 数据字典类型服务接口，用于处理字典类型的业务逻辑 */
    @Autowired
    private ISysDictTypeService dictTypeService;

    /**
     * 获取字典类型列表
     * 
     * @param dictType 包含查询条件的字典类型对象
     * @return 包含字典类型列表的表格数据对象
     */
    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysDictType dictType)
    {
        // 开启分页功能，基于 PageHelper
        startPage();
        // 根据条件查询字典类型列表
        List<SysDictType> list = dictTypeService.selectDictTypeList(dictType);
        // 将查询结果包装为响应给前端的表格数据对象
        return getDataTable(list);
    }

    /**
     * 导出字典类型列表到 Excel
     * 
     * @param response HTTP响应对象，用于返回下载的 Excel 文件
     * @param dictType 包含查询条件的字典类型对象
     */
    @Log(title = "字典类型", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:dict:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysDictType dictType)
    {
        // 根据条件查询字典类型列表
        List<SysDictType> list = dictTypeService.selectDictTypeList(dictType);
        // 创建 Excel 工具类实例
        ExcelUtil<SysDictType> util = new ExcelUtil<SysDictType>(SysDictType.class);
        // 将列表数据导出为 Excel 并写入响应中
        util.exportExcel(response, list, "字典类型");
    }

    /**
     * 查询字典类型详细
     * 
     * @param dictId 字典类型ID
     * @return 包含字典类型详细信息的 AjaxResult 对象
     */
    @PreAuthorize("@ss.hasPermi('system:dict:query')")
    @GetMapping(value = "/{dictId}")
    public AjaxResult getInfo(@PathVariable Long dictId)
    {
        // 根据字典类型ID查询字典类型信息，并返回成功响应
        return success(dictTypeService.selectDictTypeById(dictId));
    }

    /**
     * 新增字典类型
     * 
     * @param dict 包含新增信息的字典类型对象，经过后端校验
     * @return 返回新增操作结果的 AjaxResult 对象
     */
    @PreAuthorize("@ss.hasPermi('system:dict:add')")
    @Log(title = "字典类型", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysDictType dict)
    {
        // 校验字典类型名称或类型标识是否全局唯一
        if (!dictTypeService.checkDictTypeUnique(dict))
        {
            // 如果不唯一，则返回错误提示
            return error("新增字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        // 设置当前登录用户为创建者
        dict.setCreateBy(getUsername());
        // 调用服务层新增字典类型，根据返回的影响行数决定成功或失败
        return toAjax(dictTypeService.insertDictType(dict));
    }

    /**
     * 修改字典类型
     * 
     * @param dict 包含修改信息的字典类型对象，经过后端校验
     * @return 返回修改操作结果的 AjaxResult 对象
     */
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    @Log(title = "字典类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysDictType dict)
    {
        // 校验修改后的字典类型名称或类型标识是否与其他记录冲突
        if (!dictTypeService.checkDictTypeUnique(dict))
        {
            // 如果冲突，则返回错误提示
            return error("修改字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        // 设置当前登录用户为更新者
        dict.setUpdateBy(getUsername());
        // 调用服务层修改字典类型，根据返回的影响行数决定成功或失败
        return toAjax(dictTypeService.updateDictType(dict));
    }

    /**
     * 删除字典类型
     * 
     * @param dictIds 待删除的字典类型主键数组
     * @return 返回删除操作结果的 AjaxResult 对象
     */
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictIds}")
    public AjaxResult remove(@PathVariable Long[] dictIds)
    {
        // 根据字典类型ID数组批量删除字典类型
        dictTypeService.deleteDictTypeByIds(dictIds);
        // 返回删除成功响应
        return success();
    }

    /**
     * 刷新字典缓存
     * 清除 Redis 等缓存中的所有字典数据，并重新加载
     * 
     * @return 返回刷新操作结果的 AjaxResult 对象
     */
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    @Log(title = "字典类型", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public AjaxResult refreshCache()
    {
        // 调用服务层执行缓存重置逻辑
        dictTypeService.resetDictCache();
        // 返回成功响应
        return success();
    }

    /**
     * 获取字典选择框列表
     * 
     * @return 包含所有字典类型的 AjaxResult 对象，供前端下拉框使用
     */
    @GetMapping("/optionselect")
    public AjaxResult optionselect()
    {
        // 查询所有字典类型列表
        List<SysDictType> dictTypes = dictTypeService.selectDictTypeAll();
        // 返回包含字典类型的成功响应
        return success(dictTypes);
    }
}
