package com.ruoyi.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.common.annotation.RepeatSubmit;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.biz.domain.ArchiveBuildingList;
import com.ruoyi.biz.service.IArchiveBuildingListService;
import com.ruoyi.biz.mapper.ArchiveBuildingListMapper;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

import java.util.List;
import java.util.ArrayList;

/**
 * 楼栋信息Controller
 *
 * @author 姜彦汐
 * @site https://www.undsky.com
 * @date 2026-01-05
 */
@RestController
@RequestMapping("/biz/ArchiveBuildingList")
@RequiredArgsConstructor
public class ArchiveBuildingListController extends BaseController {
    private final IArchiveBuildingListService archiveBuildingListService;
    // private final ArchiveBuildingListMapper archiveBuildingListMapper;

/**
 * 查询楼栋信息列表
 */
@PreAuthorize("@ss.hasPermi('biz:ArchiveBuildingList:list')")
@GetMapping("/list")
    public TableDataInfo list(ArchiveBuildingList archiveBuildingList, HttpServletRequest request) {
        QueryWrapper<ArchiveBuildingList> queryWrapper = new QueryWrapper<>();

        // 手动构建查询条件，使用表别名避免字段歧义
        String buildingCodeLike = request.getParameter("buildingCodeLike");
        if (StringUtils.isNotBlank(buildingCodeLike)) {
            queryWrapper.like("ab.building_code", buildingCodeLike);
        }

        String buildingNameLike = request.getParameter("buildingNameLike");
        if (StringUtils.isNotBlank(buildingNameLike)) {
            queryWrapper.like("ab.building_name", buildingNameLike);
        }

        String entranceCountLike = request.getParameter("entranceCountLike");
        if (StringUtils.isNotBlank(entranceCountLike)) {
            queryWrapper.like("ab.entrance_count", entranceCountLike);
        }

        String houseCountLike = request.getParameter("houseCountLike");
        if (StringUtils.isNotBlank(houseCountLike)) {
            queryWrapper.like("ab.house_count", houseCountLike);
        }

        String buildingAreaLike = request.getParameter("buildingAreaLike");
        if (StringUtils.isNotBlank(buildingAreaLike)) {
            queryWrapper.like("ab.building_area", buildingAreaLike);
        }

        String billingAreaLike = request.getParameter("billingAreaLike");
        if (StringUtils.isNotBlank(billingAreaLike)) {
            queryWrapper.like("ab.billing_area", billingAreaLike);
        }

        String villageId = request.getParameter("villageId");
        if (StringUtils.isNotBlank(villageId)) {
            queryWrapper.eq("ab.village_id", villageId);
        }

        IPage<ArchiveBuildingList> page = archiveBuildingListService.pageArchiveBuildingList(getPage(), queryWrapper);
        return getDataTableByPage(page);
    }

    /**
     * 导出楼栋信息列表
     */
    @PreAuthorize("@ss.hasPermi('biz:ArchiveBuildingList:export')")
    @Log(title = "楼栋信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ArchiveBuildingList archiveBuildingList, HttpServletRequest request) {
        QueryWrapper<ArchiveBuildingList> queryWrapper = new QueryWrapper<>();

        // 手动构建查询条件，使用表别名避免字段歧义
        String buildingCodeLike = request.getParameter("buildingCodeLike");
        if (StringUtils.isNotBlank(buildingCodeLike)) {
            queryWrapper.like("ab.building_code", buildingCodeLike);
        }

        String buildingNameLike = request.getParameter("buildingNameLike");
        if (StringUtils.isNotBlank(buildingNameLike)) {
            queryWrapper.like("ab.building_name", buildingNameLike);
        }

        String entranceCountLike = request.getParameter("entranceCountLike");
        if (StringUtils.isNotBlank(entranceCountLike)) {
            queryWrapper.like("ab.entrance_count", entranceCountLike);
        }

        String houseCountLike = request.getParameter("houseCountLike");
        if (StringUtils.isNotBlank(houseCountLike)) {
            queryWrapper.like("ab.house_count", houseCountLike);
        }

        String buildingAreaLike = request.getParameter("buildingAreaLike");
        if (StringUtils.isNotBlank(buildingAreaLike)) {
            queryWrapper.like("ab.building_area", buildingAreaLike);
        }

        String billingAreaLike = request.getParameter("billingAreaLike");
        if (StringUtils.isNotBlank(billingAreaLike)) {
            queryWrapper.like("ab.billing_area", billingAreaLike);
        }

        String villageId = request.getParameter("villageId");
        if (StringUtils.isNotBlank(villageId)) {
            queryWrapper.eq("ab.village_id", villageId);
        }

        IPage<ArchiveBuildingList> page = archiveBuildingListService.pageArchiveBuildingList(getPage(), queryWrapper);
        List<ArchiveBuildingList> list = page.getRecords();
        ExcelUtil<ArchiveBuildingList> util = new ExcelUtil<ArchiveBuildingList>(ArchiveBuildingList. class);
        util.exportExcel(response, list, "楼栋信息");
    }

    /**
     * 导入楼栋信息数据
     */
    @PreAuthorize("@ss.hasPermi('biz:ArchiveBuildingList:import')")
    @Log(title = "楼栋信息", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        int titleNum = 0;
        ExcelUtil<ArchiveBuildingList> util = new ExcelUtil<ArchiveBuildingList>(ArchiveBuildingList. class);
        List<ArchiveBuildingList> list = util.importExcel(file.getInputStream(), titleNum);
        String operName = getUsername();
        String message = archiveBuildingListService.importArchiveBuildingList(list, titleNum, updateSupport, operName);
        return success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response, ArchiveBuildingList archiveBuildingList) {
        List<ArchiveBuildingList> list = new ArrayList<>();
        ExcelUtil<ArchiveBuildingList> util = new ExcelUtil<ArchiveBuildingList>(ArchiveBuildingList. class);
        util.exportExcel(response, list, "楼栋信息");
    }

    /**
     * 获取楼栋信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('biz:ArchiveBuildingList:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(archiveBuildingListService.selectArchiveBuildingListById(id));
    }

    /**
     * 新增楼栋信息
     */
    @PreAuthorize("@ss.hasPermi('biz:ArchiveBuildingList:add')")
    @Log(title = "楼栋信息", businessType = BusinessType.INSERT)
    @PostMapping
    @RepeatSubmit
    public AjaxResult add(@Validated @RequestBody ArchiveBuildingList archiveBuildingList) {
        // 自动填充审核字段
        archiveBuildingList.setCreatePerson(getUsername());
        archiveBuildingList.setUpdatePerson(getUsername());
            archiveBuildingListService.insertArchiveBuildingList(archiveBuildingList);
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOpt("id", archiveBuildingList.getId());
        return success(jsonObject);
    }

    /**
     * 修改楼栋信息
     */
    @PreAuthorize("@ss.hasPermi('biz:ArchiveBuildingList:edit')")
    @Log(title = "楼栋信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ArchiveBuildingList archiveBuildingList) {
        // 自动填充更新人
        archiveBuildingList.setUpdatePerson(getUsername());
        return toAjax(archiveBuildingListService.updateArchiveBuildingList(archiveBuildingList));
    }

    /**
     * 删除楼栋信息
     */
    @PreAuthorize("@ss.hasPermi('biz:ArchiveBuildingList:remove')")
    @Log(title = "楼栋信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(archiveBuildingListService.deleteArchiveBuildingListByIds(ids));
    }
}
