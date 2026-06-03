package com.ruoyi.property.mapper;

import java.util.List;
import com.ruoyi.property.domain.PmsVisitor;

public interface PmsVisitorMapper {
    PmsVisitor selectPmsVisitorByVisitorId(Long visitorId);
    List<PmsVisitor> selectPmsVisitorList(PmsVisitor pmsVisitor);
    int insertPmsVisitor(PmsVisitor pmsVisitor);
    int updatePmsVisitor(PmsVisitor pmsVisitor);
    int deletePmsVisitorByVisitorId(Long visitorId);
    int deletePmsVisitorByVisitorIds(Long[] visitorIds);
}
