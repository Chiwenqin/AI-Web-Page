package com.ruoyi.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.biz.domain.ArchiveRoomList;
import com.ruoyi.biz.service.IArchiveRoomListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 房间信息Controller
 *
 * @author 姜彦汐
 * @site https://www.undsky.com
 * @date 2026-03-01
 */
@RestController
@RequestMapping("/biz/room")
public class ArchiveRoomListController extends BaseController {
    @Autowired
    private IArchiveRoomListService archiveRoomListService;

    /**
     * 查询房间信息列表
     */
    @PreAuthorize("@ss.hasPermi('biz:room:list')")
    @GetMapping("/list")
    public TableDataInfo list(HttpServletRequest request, ArchiveRoomList archiveRoomList) {
        QueryWrapper<ArchiveRoomList> queryWrapper = new QueryWrapper<>();

        // 手动构建查询条件，避免驼峰命名转换问题
        // 获取前端传递的参数（兼容 Like 后缀的参数名）
        // 注意：所有列名需加上表别名 ar. 以避免多表关联时的列名歧义
        String cardNumber = request.getParameter("cardNumberLike");
        if (cardNumber != null && !cardNumber.isEmpty()) {
            queryWrapper.like("ar.card_number", cardNumber);
        }
        String villageId = request.getParameter("villageId");
        if (villageId != null && !villageId.isEmpty()) {
            queryWrapper.eq("ar.village_id", villageId);
        }
        String buildingId = request.getParameter("buildingId");
        if (buildingId != null && !buildingId.isEmpty()) {
            queryWrapper.eq("ar.building_id", buildingId);
        }
        String entranceId = request.getParameter("entranceId");
        if (entranceId != null && !entranceId.isEmpty()) {
            queryWrapper.eq("ar.entrance_id", entranceId);
        }
        String roomNumber = request.getParameter("roomNumberLike");
        if (roomNumber != null && !roomNumber.isEmpty()) {
            queryWrapper.like("ar.room_number", roomNumber);
        }
        String heatingStatus = request.getParameter("heatingStatus");
        if (heatingStatus != null && !heatingStatus.isEmpty()) {
            queryWrapper.eq("ar.heating_status", heatingStatus);
        }
        String measurementMethod = request.getParameter("measurementMethod");
        if (measurementMethod != null && !measurementMethod.isEmpty()) {
            queryWrapper.eq("ar.measurement_method", measurementMethod);
        }
        String networkStatus = request.getParameter("networkStatus");
        if (networkStatus != null && !networkStatus.isEmpty()) {
            queryWrapper.eq("ar.network_status", networkStatus);
        }

        Page<ArchiveRoomList> page = getPage();
        Page<ArchiveRoomList> result = archiveRoomListService
                .pageArchiveRoomList(page, queryWrapper);
        return getDataTableByPage(result);
    }

    /**
     * 导出房间信息列表
     */
    @PreAuthorize("@ss.hasPermi('biz:room:export')")
    @Log(title = "房间信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HttpServletRequest request,
            ArchiveRoomList archiveRoomList) {
        QueryWrapper<ArchiveRoomList> queryWrapper = new QueryWrapper<>();

        // 手动构建查询条件，避免驼峰命名转换问题
        // 获取前端传递的参数（兼容 Like 后缀的参数名）
        // 注意：所有列名需加上表别名 ar. 以避免多表关联时的列名歧义
        String cardNumber = request.getParameter("cardNumberLike");
        if (cardNumber != null && !cardNumber.isEmpty()) {
            queryWrapper.like("ar.card_number", cardNumber);
        }
        String villageId = request.getParameter("villageId");
        if (villageId != null && !villageId.isEmpty()) {
            queryWrapper.eq("ar.village_id", villageId);
        }
        String buildingId = request.getParameter("buildingId");
        if (buildingId != null && !buildingId.isEmpty()) {
            queryWrapper.eq("ar.building_id", buildingId);
        }
        String entranceId = request.getParameter("entranceId");
        if (entranceId != null && !entranceId.isEmpty()) {
            queryWrapper.eq("ar.entrance_id", entranceId);
        }
        String roomNumber = request.getParameter("roomNumberLike");
        if (roomNumber != null && !roomNumber.isEmpty()) {
            queryWrapper.like("ar.room_number", roomNumber);
        }
        String heatingStatus = request.getParameter("heatingStatus");
        if (heatingStatus != null && !heatingStatus.isEmpty()) {
            queryWrapper.eq("ar.heating_status", heatingStatus);
        }
        String measurementMethod = request.getParameter("measurementMethod");
        if (measurementMethod != null && !measurementMethod.isEmpty()) {
            queryWrapper.eq("ar.measurement_method", measurementMethod);
        }
        String networkStatus = request.getParameter("networkStatus");
        if (networkStatus != null && !networkStatus.isEmpty()) {
            queryWrapper.eq("ar.network_status", networkStatus);
        }

        Page<ArchiveRoomList> page = new Page<>(1, 10000);
        Page<ArchiveRoomList> result = archiveRoomListService
                .pageArchiveRoomList(page, queryWrapper);
        List<ArchiveRoomList> list = result.getRecords();
        ExcelUtil<ArchiveRoomList> util = new ExcelUtil<ArchiveRoomList>(ArchiveRoomList.class);
        util.exportExcel(response, list, "房间信息");
    }

    /**
     * 获取房间信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('biz:room:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(archiveRoomListService.selectArchiveRoomListById(id));
    }

    /**
     * 新增房间信息
     */
    @PreAuthorize("@ss.hasPermi('biz:room:add')")
    @Log(title = "房间信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ArchiveRoomList archiveRoomList) {
        // 自动填充创建人和创建时间
        archiveRoomList.setCreatePerson(getUsername());
        archiveRoomList.setCreateTime(new java.util.Date());
        // 同时填充更新人和更新时间
        archiveRoomList.setUpdatePerson(getUsername());
        archiveRoomList.setUpdateTime(new java.util.Date());
        return toAjax(archiveRoomListService.insertArchiveRoomList(archiveRoomList));
    }

    /**
     * 修改房间信息
     */
    @PreAuthorize("@ss.hasPermi('biz:room:edit')")
    @Log(title = "房间信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ArchiveRoomList archiveRoomList) {
        // 自动填充更新人和更新时间
        archiveRoomList.setUpdatePerson(getUsername());
        archiveRoomList.setUpdateTime(new java.util.Date());
        return toAjax(archiveRoomListService.updateArchiveRoomList(archiveRoomList));
    }

    /**
     * 删除房间信息
     */
    @PreAuthorize("@ss.hasPermi('biz:room:remove')")
    @Log(title = "房间信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id) {
        return toAjax(archiveRoomListService.deleteArchiveRoomListById(id));
    }

    /**
     * 批量删除房间信息
     */
    @PreAuthorize("@ss.hasPermi('biz:room:remove')")
    @Log(title = "房间信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch")
    public AjaxResult removeBatch(@RequestBody Long[] ids) {
        return toAjax(archiveRoomListService.deleteArchiveRoomListByIds(ids));
    }
}