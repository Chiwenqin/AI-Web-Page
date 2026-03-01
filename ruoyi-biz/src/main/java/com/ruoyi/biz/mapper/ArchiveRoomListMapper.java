package com.ruoyi.biz.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.biz.domain.ArchiveRoomList;
import com.ruoyi.biz.domain.ArchiveBuildingList;

/**
 * 房间信息Mapper接口
 *
 * @author 姜彦汐
 * @site https://www.undsky.com
 * @date 2026-03-01
 */
public interface ArchiveRoomListMapper extends BaseMapper<ArchiveRoomList> {

    /**
     * 查询房间信息列表
     *
     * @param page         分页对象
     * @param queryWrapper 查询条件
     * @return 房间信息分页列表
     */
    Page<ArchiveRoomList> selectListPage(@Param("page") Page<ArchiveRoomList> page,
            @Param("ew") QueryWrapper<ArchiveRoomList> queryWrapper);

}