package com.ruoyi.biz.domain;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 楼栋信息对象 archive_building_list
 *
 * @author 姜彦汐
 * @site https://www.undsky.com
 * @date 2026-01-05
 */
@Data
@TableName("archive_building_list" )
public class ArchiveBuildingList {
private static final long serialVersionUID = 1L;

            /** 主键ID */
            @TableId(type = IdType.AUTO)
            @OrderBy(asc = false, sort = 1)
        private Long id;
        public static final String ID = "id";

            /** 楼栋编码 */
                @Excel(name = "楼栋编码" )
            @TableField("building_code" )
            @NotBlank(message = "楼栋编码不能为空")
            @Size(min = 0, max = 64, message = "楼栋编码长度不能超过64个字符")
        private String buildingCode;
        public static final String BUILDINGCODE = "building_code";

            /** 楼栋名称 */
                @Excel(name = "楼栋名称" )
            @TableField("building_name" )
            @NotBlank(message = "楼栋名称不能为空")
            @Size(min = 0, max = 128, message = "楼栋名称长度不能超过128个字符")
        private String buildingName;
        public static final String BUILDINGNAME = "building_name";

            /** 排序 */
            @TableField("sort" )
        private Long sort;
        public static final String SORT = "sort";

            /** 楼门数量 */
                @Excel(name = "楼门数量" )
            @TableField("entrance_count" )
        private Long entranceCount;
        public static final String ENTRANCECOUNT = "entrance_count";

            /** 房屋数量 */
                @Excel(name = "房屋数量" )
            @TableField("house_count" )
        private Long houseCount;
        public static final String HOUSECOUNT = "house_count";

            /** 建筑面积 */
                @Excel(name = "建筑面积" )
            @TableField("building_area" )
        private BigDecimal buildingArea;
        public static final String BUILDINGAREA = "building_area";

            /** 计费面积 */
                @Excel(name = "计费面积" )
            @TableField("billing_area" )
        private BigDecimal billingArea;
        public static final String BILLINGAREA = "billing_area";

            /** 状态 (0:停用, 1:启用) */
            // 逻辑删除替代物理删除，'0'：正常；'1'：删除
            // @TableLogic(value = "'0'" , delval = "'1'" )
        private Long status;
        public static final String STATUS = "status";

            /** 所属小区ID */
                @Excel(name = "所属小区ID" )
            @TableField("village_id" )
        private Long villageId;
        public static final String VILLAGEID = "village_id";

            /** 所属小区名称 */
            @TableField(exist = false)
        private String villageName;
        public static final String VILLAGENAME = "villageName";

            /** 创建人 */
                @Excel(name = "创建人" )
            @TableField("create_person" )
        private String createPerson;
        public static final String CREATEPERSON = "create_person";

            /** 更新人 */
                @Excel(name = "更新人" )
            @TableField("update_person" )
        private String updatePerson;
        public static final String UPDATEPERSON = "update_person";

}
