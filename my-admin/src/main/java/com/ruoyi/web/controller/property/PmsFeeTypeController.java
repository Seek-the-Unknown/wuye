package com.ruoyi.web.controller.property;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.property.domain.PmsFeeType;
import com.ruoyi.property.service.IPmsFeeTypeService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 费用类型Controller
 * 
 * 处理费用类型的增删改查业务操作
 */
@RestController
@RequestMapping("/property/feeType")
public class PmsFeeTypeController extends BaseController {

    /** 费用类型服务接口 */
    @Autowired
    private IPmsFeeTypeService pmsFeeTypeService;

    /**
     * 查询费用类型列表
     * 
     * @param pmsFeeType 查询条件
     * @return 分页列表数据
     */
    @PreAuthorize("@ss.hasPermi('property:feeType:list')")
    @GetMapping("/list")
    public TableDataInfo list(PmsFeeType pmsFeeType) {
        // 开启分页
        startPage();
        // 根据条件查询费用类型列表
        List<PmsFeeType> list = pmsFeeTypeService.selectPmsFeeTypeList(pmsFeeType);
        // 返回分页响应数据
        return getDataTable(list);
    }

    /**
     * 获取全部费用类型（不分页，供下拉选用）
     * 
     * @return 包含所有正常状态费用类型的响应对象
     */
    @GetMapping("/listAll")
    public AjaxResult listAll() {
        PmsFeeType query = new PmsFeeType();
        // 只查询状态为正常的费用类型
        query.setStatus("0");
        return success(pmsFeeTypeService.selectPmsFeeTypeList(query));
    }

    /**
     * 获取费用类型详细信息
     * 
     * @param feeTypeId 费用类型ID
     * @return 费用类型详情响应对象
     */
    @PreAuthorize("@ss.hasPermi('property:feeType:query')")
    @GetMapping("/{feeTypeId}")
    public AjaxResult getInfo(@PathVariable Long feeTypeId) {
        // 根据主键获取详细信息
        return success(pmsFeeTypeService.selectPmsFeeTypeByFeeTypeId(feeTypeId));
    }

    /**
     * 新增费用类型
     * 
     * @param pmsFeeType 费用类型信息对象
     * @return 操作结果响应对象
     */
    @PreAuthorize("@ss.hasPermi('property:feeType:add')")
    @Log(title = "费用类型", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PmsFeeType pmsFeeType) {
        // 设置创建人
        pmsFeeType.setCreateBy(getUsername());
        // 插入记录并返回操作结果
        return toAjax(pmsFeeTypeService.insertPmsFeeType(pmsFeeType));
    }

    /**
     * 修改费用类型
     * 
     * @param pmsFeeType 费用类型信息对象
     * @return 操作结果响应对象
     */
    @PreAuthorize("@ss.hasPermi('property:feeType:edit')")
    @Log(title = "费用类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PmsFeeType pmsFeeType) {
        // 设置更新人
        pmsFeeType.setUpdateBy(getUsername());
        // 更新记录并返回操作结果
        return toAjax(pmsFeeTypeService.updatePmsFeeType(pmsFeeType));
    }

    /**
     * 删除费用类型
     * 
     * @param feeTypeIds 需要删除的费用类型主键数组
     * @return 操作结果响应对象
     */
    @PreAuthorize("@ss.hasPermi('property:feeType:remove')")
    @Log(title = "费用类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{feeTypeIds}")
    public AjaxResult remove(@PathVariable Long[] feeTypeIds) {
        // 批量删除费用类型记录
        return toAjax(pmsFeeTypeService.deletePmsFeeTypeByFeeTypeIds(feeTypeIds));
    }
}
