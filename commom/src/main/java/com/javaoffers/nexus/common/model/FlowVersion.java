package com.javaoffers.nexus.common.model;

import com.javaoffers.brief.modelhelper.anno.BaseModel;
import com.javaoffers.brief.modelhelper.anno.BaseUnique;
import com.javaoffers.brief.modelhelper.anno.ColName;
import com.javaoffers.brief.modelhelper.anno.derive.flag.IsDel;
import lombok.Data;

import java.util.Date;

@BaseModel("t_flow_version")
@Data
public class FlowVersion {
    @BaseUnique
    private Long id;
    private IsDel deleted;
    private Date createdAt;
    private Long createdBy;
    private Date updatedAt;
    private Long updatedBy;

    @ColName(value = "id", excludeColAll = true)
    private Long countId;

    private Long flowId;
    private String flowVersion;
    private Integer flowVersionStatus;
    private String flowVersionRemark;
    private String flowContent;
    private String inputs;
    private String outputs;
    private String variables;
}
