package com.ruoyi.property.service;

import java.util.List;
import com.ruoyi.property.domain.PmsComplaint;

/**
 * 投诉建议管理Service接口
 * 
 * @author ruoyi
 */
public interface IPmsComplaintService {
    /**
     * 查询投诉建议
     * 
     * @param complaintId 投诉建议主键
     * @return 投诉建议
     */
    PmsComplaint selectPmsComplaintByComplaintId(Long complaintId);

    /**
     * 查询投诉建议列表
     * 
     * @param pmsComplaint 投诉建议
     * @return 投诉建议集合
     */
    List<PmsComplaint> selectPmsComplaintList(PmsComplaint pmsComplaint);

    /**
     * 新增投诉建议
     * 
     * @param pmsComplaint 投诉建议
     * @return 结果
     */
    int insertPmsComplaint(PmsComplaint pmsComplaint);

    /**
     * 修改投诉建议
     * 
     * @param pmsComplaint 投诉建议
     * @return 结果
     */
    int updatePmsComplaint(PmsComplaint pmsComplaint);

    /**
     * 删除投诉建议信息
     * 
     * @param complaintId 投诉建议主键
     * @return 结果
     */
    int deletePmsComplaintByComplaintId(Long complaintId);

    /**
     * 批量删除投诉建议
     * 
     * @param complaintIds 需要删除的投诉建议主键集合
     * @return 结果
     */
    int deletePmsComplaintByComplaintIds(Long[] complaintIds);
}
