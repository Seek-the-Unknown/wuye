package com.ruoyi.web.controller.property;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.property.domain.PmsNotice;
import com.ruoyi.property.service.IPmsNoticeService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 社区公告Controller
 * 
 * 处理社区公告的增删改查等业务操作
 */
@RestController
@RequestMapping("/property/notice")
public class PmsNoticeController extends BaseController {

    /** 公告服务接口 */
    @Autowired
    private IPmsNoticeService pmsNoticeService;

    /**
     * 查询公告列表
     * 
     * @param pmsNotice 查询条件
     * @return 分页列表数据
     */
    @PreAuthorize("@ss.hasPermi('property:notice:list')")
    @GetMapping("/list")
    public TableDataInfo list(PmsNotice pmsNotice) {
        // 开启分页
        startPage();
        // 根据条件查询公告列表
        List<PmsNotice> list = pmsNoticeService.selectPmsNoticeList(pmsNotice);
        // 返回分页响应数据
        return getDataTable(list);
    }

    /**
     * 获取公告详细信息
     * 
     * @param noticeId 公告ID
     * @return 公告详情响应对象
     */
    @PreAuthorize("@ss.hasPermi('property:notice:query')")
    @GetMapping("/{noticeId}")
    public AjaxResult getInfo(@PathVariable Long noticeId) {
        // 根据主键获取详细信息
        return success(pmsNoticeService.selectPmsNoticeByNoticeId(noticeId));
    }

    /**
     * 新增公告
     * 
     * @param pmsNotice 公告信息对象
     * @return 操作结果响应对象
     */
    @PreAuthorize("@ss.hasPermi('property:notice:add')")
    @Log(title = "公告管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PmsNotice pmsNotice) {
        // 设置创建人
        pmsNotice.setCreateBy(getUsername());
        // 插入记录并返回操作结果
        return toAjax(pmsNoticeService.insertPmsNotice(pmsNotice));
    }

    /**
     * 修改公告
     * 
     * @param pmsNotice 公告信息对象
     * @return 操作结果响应对象
     */
    @PreAuthorize("@ss.hasPermi('property:notice:edit')")
    @Log(title = "公告管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PmsNotice pmsNotice) {
        // 设置更新人
        pmsNotice.setUpdateBy(getUsername());
        // 更新记录并返回操作结果
        return toAjax(pmsNoticeService.updatePmsNotice(pmsNotice));
    }

    /**
     * 删除公告
     * 
     * @param noticeIds 需要删除的公告主键数组
     * @return 操作结果响应对象
     */
    @PreAuthorize("@ss.hasPermi('property:notice:remove')")
    @Log(title = "公告管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{noticeIds}")
    public AjaxResult remove(@PathVariable Long[] noticeIds) {
        // 批量删除公告记录
        return toAjax(pmsNoticeService.deletePmsNoticeByNoticeIds(noticeIds));
    }
}
