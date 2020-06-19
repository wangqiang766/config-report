package com.hxyl.bbp.common.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import javax.persistence.Transient;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Table(name = "level_data_cache")
public class LevelDataCache implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;//主键id

    private String code;//编码

    private String name;//名称

    private String parentCode;//父类编码

    private Integer level; //级别

    private String type;//基础数据类型

    private Integer state; //状态 0失效,1有效
    @Transient
    @TableField(exist=false)
    private List<LevelDataCache> child = new ArrayList<>();   //下级节点

}
