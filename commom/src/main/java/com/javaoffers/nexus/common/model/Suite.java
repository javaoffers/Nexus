package com.javaoffers.nexus.common.model;

import com.javaoffers.brief.modelhelper.anno.BaseModel;
import com.javaoffers.brief.modelhelper.anno.BaseUnique;
import com.javaoffers.brief.modelhelper.anno.ColName;
import com.javaoffers.brief.modelhelper.anno.derive.flag.IsDel;
import lombok.Data;

import java.util.Date;

@BaseModel("t_suite")
@Data
public class Suite {
    @BaseUnique
    private Long id;
    private IsDel deleted;
    private Date createdAt;
    private Long createdBy;
    private Date updatedAt;
    private Long updatedBy;

    @ColName(value = "id", excludeColAll = true)
    private Long countId;

    private String suiteCode;
    private String suiteName;
    private Long suiteClassifyId;
    private String suiteImage;
    private String suiteVersion;
    private String suiteDesc;
    private String suiteHelpDocJson;
    private Integer suiteFlag;
}
