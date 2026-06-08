package com.ruoyi.web.controller.property;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.property.domain.PmsComplaint;
import com.ruoyi.property.service.IPmsComplaintService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 投诉建议Controller
 * 
 * 处理投诉建议的增删改查及处理操作
 */
@RestController
@RequestMapping("/property/complaint")
public class PmsComplaintController extends BaseController
{
    /** 投诉建议服务接口 */
    @Autowired
    private IPmsComplaintService pmsComplaintService;

    /**
     * 查询投诉建议列表
     * 
     * @param pmsComplaint 查询条件
     * @return 分页列表数据
     */
    @PreAuthorize("@ss.hasPermi('property:complaint:list')")
    @GetMapping("/list")
    public TableDataInfo list(PmsComplaint pmsComplaint)
    {
        // 开启分页
        startPage();
        // 根据条件查询投诉建议列表
        List<PmsComplaint> list = pmsComplaintService.selectPmsComplaintList(pmsComplaint);
        // 返回分页响应数据
        return getDataTable(list);
    }

    /**
     * 获取投诉建议详细信息
     * 
     * @param complaintId 投诉建议ID
     * @return 投诉建议详情响应对象
     */
    @PreAuthorize("@ss.hasPermi('property:complaint:query')")
    @GetMapping("/{complaintId}")
    public AjaxResult getInfo(@PathVariable Long complaintId)
    {
        // 根据主键获取详细信息
        return success(pmsComplaintService.selectPmsComplaintByComplaintId(complaintId));
    }

    /**
     * 新增投诉建议
     * 
     * @param pmsComplaint 投诉建议信息对象
     * @return 操作结果响应对象
     */
    @PreAuthorize("@ss.hasPermi('property:complaint:add')")
    @Log(title = "投诉建议", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PmsComplaint pmsComplaint)
    {
        // 设置创建人
        pmsComplaint.setCreateBy(getUsername());
        // 插入记录并返回操作结果
        return toAjax(pmsComplaintService.insertPmsComplaint(pmsComplaint));
    }

    /**
     * 修改投诉建议
     * 
     * @param pmsComplaint 投诉建议信息对象
     * @return 操作结果响应对象
     */
    @PreAuthorize("@ss.hasPermi('property:complaint:edit')")
    @Log(title = "投诉建议", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PmsComplaint pmsComplaint)
    {
        // 设置更新人
        pmsComplaint.setUpdateBy(getUsername());
        // 更新记录并返回操作结果
        return toAjax(pmsComplaintService.updatePmsComplaint(pmsComplaint));
    }

    /**
     * 处理投诉建议
     * 
     * @param pmsComplaint 投诉建议信息对象（包含处理信息）
     * @return 操作结果响应对象
     */
    @PreAuthorize("@ss.hasPermi('property:complaint:edit')")
    @Log(title = "处理投诉建议", businessType = BusinessType.UPDATE)
    @PutMapping("/handle")
    public AjaxResult handle(@RequestBody PmsComplaint pmsComplaint)
    {
        // 设置处理人
        pmsComplaint.setHandleBy(getUsername());
        // 设置处理时间为当前时间
        pmsComplaint.setHandleTime(new java.util.Date());
        // 更新记录并返回操作结果
        return toAjax(pmsComplaintService.updatePmsComplaint(pmsComplaint));
    }

    /**
     * 删除投诉建议
     * 
     * @param complaintIds 需要删除的投诉建议主键数组
     * @return 操作结果响应对象
     */
    @PreAuthorize("@ss.hasPermi('property:complaint:remove')")
    @Log(title = "投诉建议", businessType = BusinessType.DELETE)
    @DeleteMapping("/{complaintIds}")
    public AjaxResult remove(@PathVariable Long[] complaintIds)
    {
        // 批量删除投诉建议记录
        return toAjax(pmsComplaintService.deletePmsComplaintByComplaintIds(complaintIds));
    }
}
