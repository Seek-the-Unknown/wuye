package com.ruoyi.property.service;

import java.util.List;
import com.ruoyi.property.domain.PmsUserOwner;

public interface IPmsUserOwnerService {
    PmsUserOwner selectByUserId(Long userId);
    PmsUserOwner selectPmsUserOwnerById(Long id);
    List<PmsUserOwner> selectPmsUserOwnerList(PmsUserOwner query);
    int insertPmsUserOwner(PmsUserOwner pmsUserOwner);
    int updatePmsUserOwner(PmsUserOwner pmsUserOwner);
    int deletePmsUserOwnerById(Long id);
}
