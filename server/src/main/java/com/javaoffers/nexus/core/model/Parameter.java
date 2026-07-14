package com.javaoffers.nexus.core.model;

import com.javaoffers.brief.modelhelper.anno.BaseModel;
import com.javaoffers.brief.modelhelper.anno.BaseUnique;
import com.javaoffers.brief.modelhelper.anno.ColName;
import com.javaoffers.brief.modelhelper.anno.derive.flag.IsDel;
import lombok.Data;

import java.util.Date;

@BaseModel("t_parameter")
@Data
public class Parameter {
    @BaseUnique
    private Long id;
    private IsDel deleted;
    private Date createdAt;
    private Long createdBy;
    private Date updatedAt;
    private Long updatedBy;

    @ColName(value = "id", excludeColAll = true)
    private Long countId;

    private Integer paramType;
    private String paramKey;
    private String paramName;
    private String paramPosition;
    private String paramDesc;
    private String dataType;
    private Integer required;
    private String sourceType;
    private Long sourceId;
}
