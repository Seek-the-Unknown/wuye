package com.ruoyi.property.service;

import java.util.List;
import com.ruoyi.property.domain.PmsComplaint;

public interface IPmsComplaintService {
    PmsComplaint selectPmsComplaintByComplaintId(Long complaintId);
    List<PmsComplaint> selectPmsComplaintList(PmsComplaint pmsComplaint);
    int insertPmsComplaint(PmsComplaint pmsComplaint);
    int updatePmsComplaint(PmsComplaint pmsComplaint);
    int deletePmsComplaintByComplaintId(Long complaintId);
    int deletePmsComplaintByComplaintIds(Long[] complaintIds);
}
