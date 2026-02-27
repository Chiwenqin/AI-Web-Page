package com.ruoyi.biz.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.biz.domain.ArchiveEntranceList;
import com.ruoyi.biz.domain.ArchiveBuildingList;

/**
 * 楼门信息Mapper接口
 *
 * @author 姜彦汐
 * @site https://www.undsky.com
 * @date 2026-02-27
 */
public interface ArchiveEntranceListMapper extends BaseMapper<ArchiveEntranceList> {

    /**
     * 查询楼门信息列表
     *
     * @param page         分页对象
     * @param queryWrapper 查询条件
     * @return 楼门信息分页列表
     */
    Page<ArchiveEntranceList> selectListPage(@Param("page") Page<ArchiveEntranceList> page,
            @Param("ew") QueryWrapper<ArchiveEntranceList> queryWrapper);

}
