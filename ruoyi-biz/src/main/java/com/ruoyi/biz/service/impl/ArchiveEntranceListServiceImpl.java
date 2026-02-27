package com.ruoyi.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.biz.domain.ArchiveEntranceList;
import com.ruoyi.biz.mapper.ArchiveEntranceListMapper;
import com.ruoyi.biz.service.IArchiveEntranceListService;
import com.ruoyi.common.core.text.Convert;
import org.springframework.stereotype.Service;

/**
 * 楼门信息Service实现
 *
 * @author 姜彦汐
 * @site https://www.undsky.com
 * @date 2026-02-27
 */
@Service
public class ArchiveEntranceListServiceImpl extends ServiceImpl<ArchiveEntranceListMapper, ArchiveEntranceList>
        implements IArchiveEntranceListService {

    @Override
    public Page<ArchiveEntranceList> pageArchiveEntranceList(Page<ArchiveEntranceList> page,
            QueryWrapper<ArchiveEntranceList> queryWrapper) {
        return baseMapper.selectListPage(page, queryWrapper);
    }

    @Override
    public ArchiveEntranceList selectArchiveEntranceListById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public int insertArchiveEntranceList(ArchiveEntranceList archiveEntranceList) {
        return baseMapper.insert(archiveEntranceList);
    }

    @Override
    public int updateArchiveEntranceList(ArchiveEntranceList archiveEntranceList) {
        return baseMapper.updateById(archiveEntranceList);
    }

    @Override
    public int deleteArchiveEntranceListByIds(Long[] ids) {
        return baseMapper.deleteBatchIds(java.util.Arrays.asList(ids));
    }

    @Override
    public int deleteArchiveEntranceListById(Long id) {
        return baseMapper.deleteById(id);
    }
}
