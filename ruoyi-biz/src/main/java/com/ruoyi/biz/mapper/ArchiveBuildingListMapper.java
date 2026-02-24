package com.ruoyi.biz.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.biz.domain.ArchiveBuildingList;
import org.apache.ibatis.annotations.Param;

/**
 * 楼栋信息Mapper接口
 *
 * @author 姜彦汐
 * @site https://www.undsky.com
 * @date 2026-01-05
 */
public interface ArchiveBuildingListMapper extends BaseMapper<ArchiveBuildingList> {

    IPage<ArchiveBuildingList> selectListPage(Page<ArchiveBuildingList> page, @Param("ew") QueryWrapper<ArchiveBuildingList> queryWrapper);
}
