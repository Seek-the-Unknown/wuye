package com.ruoyi.property.service;

import java.util.List;
import com.ruoyi.property.domain.PmsVisitor;

/**
 * 访客管理Service接口
 * 
 * @author ruoyi
 */
public interface IPmsVisitorService {
    /**
     * 查询访客管理
     * 
     * @param visitorId 访客主键
     * @return 访客管理信息
     */
    PmsVisitor selectPmsVisitorByVisitorId(Long visitorId);

    /**
     * 查询访客管理列表
     * 
     * @param pmsVisitor 访客管理
     * @return 访客管理集合
     */
    List<PmsVisitor> selectPmsVisitorList(PmsVisitor pmsVisitor);

    /**
     * 新增访客管理
     * 
     * @param pmsVisitor 访客管理
     * @return 结果
     */
    int insertPmsVisitor(PmsVisitor pmsVisitor);

    /**
     * 修改访客管理
     * 
     * @param pmsVisitor 访客管理
     * @return 结果
     */
    int updatePmsVisitor(PmsVisitor pmsVisitor);

    /**
     * 批量删除访客管理
     * 
     * @param visitorIds 需要删除的访客主键集合
     * @return 结果
     */
    int deletePmsVisitorByVisitorIds(Long[] visitorIds);

    /**
     * 删除访客管理信息
     * 
     * @param visitorId 访客主键
     * @return 结果
     */
    int deletePmsVisitorByVisitorId(Long visitorId);
}
