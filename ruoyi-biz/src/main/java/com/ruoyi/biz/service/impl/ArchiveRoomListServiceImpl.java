package com.ruoyi.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.biz.domain.ArchiveRoomList;
import com.ruoyi.biz.mapper.ArchiveRoomListMapper;
import com.ruoyi.biz.service.IArchiveRoomListService;
import com.ruoyi.common.core.text.Convert;
import org.springframework.stereotype.Service;

/**
 * 房间信息Service实现
 *
 * @author 姜彦汐
 * @site https://www.undsky.com
 * @date 2026-03-01
 */
@Service
public class ArchiveRoomListServiceImpl extends ServiceImpl<ArchiveRoomListMapper, ArchiveRoomList>
        implements IArchiveRoomListService {

    @Override
    public Page<ArchiveRoomList> pageArchiveRoomList(Page<ArchiveRoomList> page,
            QueryWrapper<ArchiveRoomList> queryWrapper) {
        return baseMapper.selectListPage(page, queryWrapper);
    }

    @Override
    public ArchiveRoomList selectArchiveRoomListById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public int insertArchiveRoomList(ArchiveRoomList archiveRoomList) {
        return baseMapper.insert(archiveRoomList);
    }

    @Override
    public int updateArchiveRoomList(ArchiveRoomList archiveRoomList) {
        return baseMapper.updateById(archiveRoomList);
    }

    @Override
    public int deleteArchiveRoomListByIds(Long[] ids) {
        return baseMapper.deleteBatchIds(java.util.Arrays.asList(ids));
    }

    @Override
    public int deleteArchiveRoomListById(Long id) {
        return baseMapper.deleteById(id);
    }
}