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
 * 房间信息对象 archive_room_list
 *
 * @author 姜彦汐
 * @site https://www.undsky.com
 * @date 2026-03-01
 */
@Data
@TableName("archive_room_list")
public class ArchiveRoomList implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    @OrderBy(asc = false, sort = 1)
    private Long id;
    public static final String ID = "id";

    /** 一卡通号 */
    @Excel(name = "一卡通号")
    @TableField("card_number")
    @NotBlank(message = "一卡通号不能为空")
    @Size(min = 0, max = 64, message = "一卡通号长度不能超过64个字符")
    private String cardNumber;
    public static final String CARDNUMBER = "card_number";

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

    /** 所属楼门ID */
    @Excel(name = "所属楼门ID")
    @TableField("entrance_id")
    private Long entranceId;
    public static final String ENTRANCEID = "entrance_id";

    /** 所属楼门名称 */
    @TableField(exist = false)
    private String entranceName;
    public static final String ENTRANCENAME = "entranceName";

    /** 房间号码 */
    @Excel(name = "房间号码")
    @TableField("room_number")
    @NotBlank(message = "房间号码不能为空")
    @Size(min = 0, max = 32, message = "房间号码长度不能超过32个字符")
    private String roomNumber;
    public static final String ROOMNUMBER = "room_number";

    /** 计费面积 */
    @Excel(name = "计费面积")
    @TableField("billing_area")
    private BigDecimal billingArea;
    public static final String BILLINGAREA = "billing_area";

    /** 供暖状态 (0:停供, 1:正常) */
    @Excel(name = "供暖状态", readConverterExp = "0=停供,1=正常")
    @TableField("heating_status")
    private Long heatingStatus;
    public static final String HEATINGSTATUS = "heating_status";

    /** 计量方式 (0:按面积计费, 1:按表计费) */
    @Excel(name = "计量方式", readConverterExp = "0=按面积计费,1=按表计费")
    @TableField("measurement_method")
    private Long measurementMethod;
    public static final String MEASUREMENTMETHOD = "measurement_method";

    /** 过程数据 */
    @Excel(name = "过程数据")
    @TableField("process_data")
    private String processData;
    public static final String PROCESSDATA = "process_data";

    /** 关联机组ID */
    @Excel(name = "关联机组ID")
    @TableField("equipment_id")
    private Long equipmentId;
    public static final String EQUIPMENTID = "equipment_id";

    /** 关联机组名称 */
    @TableField(exist = false)
    private String equipmentName;
    public static final String EQUIPMENTNAME = "equipmentName";

    /** 当前采暖季 */
    @Excel(name = "当前采暖季")
    @TableField("current_season")
    private String currentSeason;
    public static final String CURRENTSEASON = "current_season";

    /** 入网状态 (0:未入网, 1:已入网) */
    @Excel(name = "入网状态", readConverterExp = "0=未入网,1=已入网")
    @TableField("network_status")
    private Long networkStatus;
    public static final String NETWORKSTATUS = "network_status";

    /** 入网采暖季 */
    @Excel(name = "入网采暖季")
    @TableField("network_season")
    private String networkSeason;
    public static final String NETWORKSEASON = "network_season";

    /** 创建人 */
    @TableField(value = "create_person", fill = FieldFill.INSERT)
    private String createPerson;
    public static final String CREATEPERSON = "create_person";

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;
    public static final String CREATETIME = "create_time";

    /** 更新人 */
    @TableField(value = "update_person", fill = FieldFill.INSERT_UPDATE)
    private String updatePerson;
    public static final String UPDATEPERSON = "update_person";

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    public static final String UPDATETIME = "update_time";

}