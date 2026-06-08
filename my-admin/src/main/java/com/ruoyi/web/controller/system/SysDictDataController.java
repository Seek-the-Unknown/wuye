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
 * 数据字典信息
 * 处理系统数据字典数据的相关请求，如查询、新增、修改、删除和导出等
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataController extends BaseController
{
    /** 数据字典服务接口，用于处理字典数据的业务逻辑 */
    @Autowired
    private ISysDictDataService dictDataService;

    /** 数据字典类型服务接口，用于处理字典类型的业务逻辑 */
    @Autowired
    private ISysDictTypeService dictTypeService;

    /**
     * 查询字典数据列表
     * 
     * @param dictData 包含查询条件的数据字典对象
     * @return 包含字典数据列表的表格数据对象
     */
    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysDictData dictData)
    {
        // 开启分页功能，基于 PageHelper
        startPage();
        // 根据条件查询字典数据列表
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        // 将查询结果包装为响应给前端的表格数据对象
        return getDataTable(list);
    }

    /**
     * 导出字典数据列表到 Excel
     * 
     * @param response HTTP响应对象，用于返回下载的 Excel 文件
     * @param dictData 包含查询条件的数据字典对象
     */
    @Log(title = "字典数据", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:dict:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysDictData dictData)
    {
        // 根据条件查询字典数据列表
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        // 创建 Excel 工具类实例
        ExcelUtil<SysDictData> util = new ExcelUtil<SysDictData>(SysDictData.class);
        // 将列表数据导出为 Excel 并写入响应中
        util.exportExcel(response, list, "字典数据");
    }

    /**
     * 查询字典数据详细
     * 
     * @param dictCode 字典数据ID
     * @return 包含字典数据详细信息的 AjaxResult 对象
     */
    @PreAuthorize("@ss.hasPermi('system:dict:query')")
    @GetMapping(value = "/{dictCode}")
    public AjaxResult getInfo(@PathVariable Long dictCode)
    {
        // 根据字典编码查询字典数据信息，并返回成功响应
        return success(dictDataService.selectDictDataById(dictCode));
    }

    /**
     * 根据字典类型查询字典数据信息
     * 
     * @param dictType 字典类型
     * @return 包含对应字典数据列表的 AjaxResult 对象
     */
    @GetMapping(value = "/type/{dictType}")
    public AjaxResult dictType(@PathVariable String dictType)
    {
        // 根据字典类型查询字典数据列表（包含缓存读取）
        List<SysDictData> data = dictTypeService.selectDictDataByType(dictType);
        // 如果数据为空，则初始化一个空列表，避免前端产生空指针异常
        if (StringUtils.isNull(data))
        {
            data = new ArrayList<SysDictData>();
        }
        // 返回包含字典数据的成功响应
        return success(data);
    }

    /**
     * 新增字典数据
     * 
     * @param dict 包含新增信息的数据字典对象，经过后端校验
     * @return 返回新增操作结果的 AjaxResult 对象
     */
    @PreAuthorize("@ss.hasPermi('system:dict:add')")
    @Log(title = "字典数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysDictData dict)
    {
        // 设置当前登录用户为创建者
        dict.setCreateBy(getUsername());
        // 调用服务层新增字典数据，根据返回的影响行数决定成功或失败
        return toAjax(dictDataService.insertDictData(dict));
    }

    /**
     * 修改保存字典数据
     * 
     * @param dict 包含修改信息的数据字典对象，经过后端校验
     * @return 返回修改操作结果的 AjaxResult 对象
     */
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    @Log(title = "字典数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysDictData dict)
    {
        // 设置当前登录用户为更新者
        dict.setUpdateBy(getUsername());
        // 调用服务层修改字典数据，根据返回的影响行数决定成功或失败
        return toAjax(dictDataService.updateDictData(dict));
    }

    /**
     * 删除字典数据
     * 
     * @param dictCodes 待删除的字典数据主键数组
     * @return 返回删除操作结果的 AjaxResult 对象
     */
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictCodes}")
    public AjaxResult remove(@PathVariable Long[] dictCodes)
    {
        // 根据字典编码数组批量删除字典数据
        dictDataService.deleteDictDataByIds(dictCodes);
        // 返回删除成功响应
        return success();
    }
}
