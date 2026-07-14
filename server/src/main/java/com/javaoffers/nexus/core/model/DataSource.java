package com.javaoffers.nexus.core.model;

import com.javaoffers.brief.modelhelper.anno.BaseModel;
import com.javaoffers.brief.modelhelper.anno.BaseUnique;
import com.javaoffers.brief.modelhelper.anno.ColName;
import com.javaoffers.brief.modelhelper.anno.derive.flag.IsDel;
import lombok.Data;

import java.util.Date;

@BaseModel("t_data_source")
@Data
public class DataSource {
    @BaseUnique
    private Long id;
    private IsDel deleted;
    private Date createdAt;
    private Long createdBy;
    private Date updatedAt;
    private Long updatedBy;

    @ColName(value = "id", excludeColAll = true)
    private Long countId;

    private String dataSourceName;
    private String dataSourceType;
    private String dataSourceDesc;
    private String address;
    private String port;
    private String userName;
    private String password;
    private String databaseName;
    private String connectExtInfo;
    private Integer minPoolSize;
    private Integer maxPoolSize;
    private Integer queryTimeout;
    private String dataSourceExtInfo;
}
