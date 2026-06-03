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
import com.ruoyi.property.domain.PmsRoom;
import com.ruoyi.property.service.IPmsRoomService;
import com.ruoyi.common.core.page.TableDataInfo;

@RestController
@RequestMapping("/property/room")
public class PmsRoomController extends BaseController
{
    @Autowired
    private IPmsRoomService pmsRoomService;

    @PreAuthorize("@ss.hasPermi('property:room:list')")
    @GetMapping("/list")
    public TableDataInfo list(PmsRoom pmsRoom)
    {
        startPage();
        List<PmsRoom> list = pmsRoomService.selectPmsRoomList(pmsRoom);
        return getDataTable(list);
    }

    /** 获取全部房屋（不分页，供下拉选用） */
    @GetMapping("/listAll")
    public AjaxResult listAll(PmsRoom pmsRoom)
    {
        return success(pmsRoomService.selectPmsRoomList(pmsRoom));
    }

    @PreAuthorize("@ss.hasPermi('property:room:query')")
    @GetMapping(value = "/{roomId}")
    public AjaxResult getInfo(@PathVariable("roomId") Long roomId)
    {
        return success(pmsRoomService.selectPmsRoomByRoomId(roomId));
    }

    @PreAuthorize("@ss.hasPermi('property:room:add')")
    @Log(title = "房屋管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PmsRoom pmsRoom)
    {
        pmsRoom.setCreateBy(getUsername());
        return toAjax(pmsRoomService.insertPmsRoom(pmsRoom));
    }

    @PreAuthorize("@ss.hasPermi('property:room:edit')")
    @Log(title = "房屋管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PmsRoom pmsRoom)
    {
        pmsRoom.setUpdateBy(getUsername());
        return toAjax(pmsRoomService.updatePmsRoom(pmsRoom));
    }

    @PreAuthorize("@ss.hasPermi('property:room:remove')")
    @Log(title = "房屋管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{roomIds}")
    public AjaxResult remove(@PathVariable Long[] roomIds)
    {
        return toAjax(pmsRoomService.deletePmsRoomByRoomIds(roomIds));
    }
}
