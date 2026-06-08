package com.ruoyi.property.mapper;

import java.util.List;
import com.ruoyi.property.domain.PmsComplaint;

/**
 * 投诉建议管理Mapper接口
 * 
 * @author ruoyi
 */
public interface PmsComplaintMapper {
    /**
     * 查询投诉建议管理
     * 
     * @param complaintId 投诉建议管理主键
     * @return 投诉建议管理
     */
    public PmsComplaint selectPmsComplaintByComplaintId(Long complaintId);

    /**
     * 查询投诉建议管理列表
     * 
     * @param pmsComplaint 投诉建议管理
     * @return 投诉建议管理集合
     */
    public List<PmsComplaint> selectPmsComplaintList(PmsComplaint pmsComplaint);

    /**
     * 新增投诉建议管理
     * 
     * @param pmsComplaint 投诉建议管理
     * @return 结果
     */
    public int insertPmsComplaint(PmsComplaint pmsComplaint);

    /**
     * 修改投诉建议管理
     * 
     * @param pmsComplaint 投诉建议管理
     * @return 结果
     */
    public int updatePmsComplaint(PmsComplaint pmsComplaint);

    /**
     * 删除投诉建议管理
     * 
     * @param complaintId 投诉建议管理主键
     * @return 结果
     */
    public int deletePmsComplaintByComplaintId(Long complaintId);

    /**
     * 批量删除投诉建议管理
     * 
     * @param complaintIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePmsComplaintByComplaintIds(Long[] complaintIds);
}
