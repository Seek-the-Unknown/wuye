package com.ruoyi.property.service;

import java.util.List;
import com.ruoyi.property.domain.PmsVisitor;

public interface IPmsVisitorService {
    PmsVisitor selectPmsVisitorByVisitorId(Long visitorId);
    List<PmsVisitor> selectPmsVisitorList(PmsVisitor pmsVisitor);
    int insertPmsVisitor(PmsVisitor pmsVisitor);
    int updatePmsVisitor(PmsVisitor pmsVisitor);
    int deletePmsVisitorByVisitorIds(Long[] visitorIds);
    int deletePmsVisitorByVisitorId(Long visitorId);
}
