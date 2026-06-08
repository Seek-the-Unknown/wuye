package com.ruoyi.web.controller.property;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import com.ruoyi.property.domain.PmsOwner;
import com.ruoyi.property.service.IPmsOwnerService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 业主管理Controller
 * 
 * 处理业主信息的增删改查、导入导出等业务操作
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/property/owner")
public class PmsOwnerController extends BaseController
{
    /** 业主服务接口 */
    @Autowired
    private IPmsOwnerService pmsOwnerService;

    /**
     * 查询业主管理列表
     * 
     * @param pmsOwner 查询条件
     * @return 分页列表数据
     */
    @PreAuthorize("@ss.hasPermi('property:owner:list')")
    @GetMapping("/list")
    public TableDataInfo list(PmsOwner pmsOwner)
    {
        // 开启分页
        startPage();
        // 根据条件查询业主列表
        List<PmsOwner> list = pmsOwnerService.selectPmsOwnerList(pmsOwner);
        // 返回分页响应数据
        return getDataTable(list);
    }

    /**
     * 获取全部业主（不分页，供下拉选用）
     * 
     * @param pmsOwner 查询条件
     * @return 包含所有符合条件的业主信息的响应对象
     */
    @GetMapping("/listAll")
    public AjaxResult listAll(PmsOwner pmsOwner)
    {
        // 查询所有符合条件的业主并返回
        return success(pmsOwnerService.selectPmsOwnerList(pmsOwner));
    }

    /**
     * 导出业主管理列表
     * 
     * @param response HTTP响应对象
     * @param pmsOwner 查询条件
     */
    @PreAuthorize("@ss.hasPermi('property:owner:export')")
    @Log(title = "业主管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PmsOwner pmsOwner)
    {
        // 根据条件查询业主列表
        List<PmsOwner> list = pmsOwnerService.selectPmsOwnerList(pmsOwner);
        // 创建Excel导出工具类实例
        ExcelUtil<PmsOwner> util = new ExcelUtil<PmsOwner>(PmsOwner.class);
        // 执行导出操作
        util.exportExcel(response, list, "业主管理数据");
    }

    /**
     * 下载业主导入模板
     * 
     * @param response HTTP响应对象
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        // 创建Excel导出工具类实例
        ExcelUtil<PmsOwner> util = new ExcelUtil<PmsOwner>(PmsOwner.class);
        // 导出模板供用户下载
        util.importTemplateExcel(response, "业主数据");
    }

    /**
     * 导入业主数据
     * 
     * @param file 上传的Excel文件
     * @param updateSupport 是否支持更新已存在的用户（备用参数）
     * @return 操作结果响应对象
     * @throws Exception 导入异常
     */
    @Log(title = "业主管理", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('property:owner:add')")
    @PostMapping("/importData")
    public AjaxResult importData(org.springframework.web.multipart.MultipartFile file, boolean updateSupport) throws Exception
    {
        // 创建Excel导入工具类实例
        ExcelUtil<PmsOwner> util = new ExcelUtil<PmsOwner>(PmsOwner.class);
        // 解析Excel文件内容并转为对象集合
        List<PmsOwner> ownerList = util.importExcel(file.getInputStream());
        int successNum = 0;
        int failureNum = 0;
        StringBuilder failureMsg = new StringBuilder();
        
        // 遍历处理解析出的每条业主数据
        for (PmsOwner owner : ownerList) {
            try {
                // 设置创建人
                owner.setCreateBy(getUsername());
                // 执行插入操作
                pmsOwnerService.insertPmsOwner(owner);
                successNum++;
            } catch (Exception e) {
                failureNum++;
                // 拼接失败信息
                String msg = "<br/>" + failureNum + "、业主 " + owner.getOwnerName() + " 导入失败：";
                if (e.getCause() != null) {
                    msg += e.getCause().getMessage();
                } else {
                    msg += e.getMessage();
                }
                failureMsg.append(msg);
            }
        }
        
        // 如果有失败记录，则抛出异常并提示失败详情
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new com.ruoyi.common.exception.ServiceException(failureMsg.toString());
        }
        // 如果全部成功，返回成功提示及数量
        return success("恭喜您，数据已全部导入成功！共 " + successNum + " 条");
    }

    /**
     * 获取业主管理详细信息
     * 
     * @param ownerId 业主ID
     * @return 业主详情响应对象
     */
    @PreAuthorize("@ss.hasPermi('property:owner:query')")
    @GetMapping(value = "/{ownerId}")
    public AjaxResult getInfo(@PathVariable("ownerId") Long ownerId)
    {
        // 根据主键获取详细信息
        return success(pmsOwnerService.selectPmsOwnerByOwnerId(ownerId));
    }

    /**
     * 新增业主管理
     * 
     * @param pmsOwner 业主信息对象
     * @return 操作结果响应对象
     */
    @PreAuthorize("@ss.hasPermi('property:owner:add')")
    @Log(title = "业主管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PmsOwner pmsOwner)
    {
        // 设置创建人
        pmsOwner.setCreateBy(getUsername());
        // 插入记录并返回操作结果
        return toAjax(pmsOwnerService.insertPmsOwner(pmsOwner));
    }

    /**
     * 修改业主管理
     * 
     * @param pmsOwner 业主信息对象
     * @return 操作结果响应对象
     */
    @PreAuthorize("@ss.hasPermi('property:owner:edit')")
    @Log(title = "业主管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PmsOwner pmsOwner)
    {
        // 设置更新人
        pmsOwner.setUpdateBy(getUsername());
        // 更新记录并返回操作结果
        return toAjax(pmsOwnerService.updatePmsOwner(pmsOwner));
    }

    /**
     * 删除业主管理
     * 
     * @param ownerIds 需要删除的业主主键数组
     * @return 操作结果响应对象
     */
    @PreAuthorize("@ss.hasPermi('property:owner:remove')")
    @Log(title = "业主管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ownerIds}")
    public AjaxResult remove(@PathVariable Long[] ownerIds)
    {
        // 批量删除业主记录
        return toAjax(pmsOwnerService.deletePmsOwnerByOwnerIds(ownerIds));
    }
}
