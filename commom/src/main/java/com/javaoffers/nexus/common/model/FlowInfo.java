package com.javaoffers.nexus.common.model;

import com.javaoffers.brief.modelhelper.anno.BaseModel;
import com.javaoffers.brief.modelhelper.anno.BaseUnique;
import com.javaoffers.brief.modelhelper.anno.ColName;
import com.javaoffers.brief.modelhelper.anno.derive.flag.IsDel;
import lombok.Data;

import java.util.Date;

@BaseModel("t_flow_info")
@Data
public class FlowInfo {
    @BaseUnique
    private Long id;
    private IsDel deleted;
    private Date createdAt;
    private Long createdBy;
    private Date updatedAt;
    private Long updatedBy;

    @ColName(value = "id", excludeColAll = true)
    private Long countId;

    private String flowKey;
    private String flowName;
    private String flowType;
    private String remark;
}
