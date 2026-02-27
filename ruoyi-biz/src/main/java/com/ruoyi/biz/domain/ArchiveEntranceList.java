package com.ruoyi.biz.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * 楼门信息对象 archive_entrance_list
 *
 * @author 姜彦汐
 * @site https://www.undsky.com
 * @date 2026-02-27
 */
@Data
@TableName("archive_entrance_list")
public class ArchiveEntranceList implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    @OrderBy(asc = false, sort = 1)
    private Long id;
    public static final String ID = "id";

    /** 楼门编码 */
    @Excel(name = "楼门编码")
    @TableField("entrance_code")
    @NotBlank(message = "楼门编码不能为空")
    @Size(min = 0, max = 64, message = "楼门编码长度不能超过64个字符")
    private String entranceCode;
    public static final String ENTRANCECODE = "entrance_code";

    /** 楼门名称 */
    @Excel(name = "楼门名称")
    @TableField("entrance_name")
    @NotBlank(message = "楼门名称不能为空")
    @Size(min = 0, max = 128, message = "楼门名称长度不能超过128个字符")
    private String entranceName;
    public static final String ENTRANCENAME = "entrance_name";

    /** 所属小区ID */
    @Excel(name = "所属小区ID")
    @TableField("village_id")
    private Long villageId;
    public static final String VILLAGEID = "village_id";

    /** 所属小区名称 */
    @TableField(exist = false)
    private String villageName;
    public static final String VILLAGENAME = "villageName";

    /** 所属楼栋ID */
    @Excel(name = "所属楼栋ID")
    @TableField("building_id")
    private Long buildingId;
    public static final String BUILDINGID = "building_id";

    /** 所属楼栋名称 */
    @TableField(exist = false)
    private String buildingName;
    public static final String BUILDINGNAME = "buildingName";

    /** 房屋数量 */
    @Excel(name = "房屋数量")
    @TableField("house_count")
    private Long houseCount;
    public static final String HOUSECOUNT = "house_count";

    /** 建筑面积 */
    @Excel(name = "建筑面积")
    @TableField("building_area")
    private BigDecimal buildingArea;
    public static final String BUILDINGAREA = "building_area";

    /** 计费面积 */
    @Excel(name = "计费面积")
    @TableField("billing_area")
    private BigDecimal billingArea;
    public static final String BILLINGAREA = "billing_area";

    /** 状态 (0:停用, 1:启用) */
    // 逻辑删除替代物理删除，'0'：正常；'1'：删除
    // @TableLogic(value = "'0'", delval = "'1'")
    private Long status;
    public static final String STATUS = "status";

    /** 创建人 */
    @TableField("create_person")
    private String createPerson;
    public static final String CREATEPERSON = "create_person";

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    private Date createTime;
    public static final String CREATETIME = "create_time";

    /** 更新人 */
    @TableField("update_person")
    private String updatePerson;
    public static final String UPDATEPERSON = "update_person";

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_time")
    private Date updateTime;
    public static final String UPDATETIME = "update_time";

}
