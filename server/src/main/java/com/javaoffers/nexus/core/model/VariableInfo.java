package com.javaoffers.nexus.core.model;

import com.javaoffers.brief.modelhelper.anno.BaseModel;
import com.javaoffers.brief.modelhelper.anno.BaseUnique;
import com.javaoffers.brief.modelhelper.anno.ColName;
import lombok.Data;

import java.util.Date;

/**
 * 注意：原 GORM 模型无 deleted 字段（硬删除），故此处不声明 IsDel。
 */
@BaseModel("t_variable_info")
@Data
public class VariableInfo {
    @BaseUnique
    private Long id;
    private Date createdAt;
    private Long createdBy;
    private Date updatedAt;
    private Long updatedBy;

    @ColName(value = "id", excludeColAll = true)
    private Long countId;

    private Long flowDefinitionId;
    private String variableKey;
    private String variableName;
    private Integer variableType;
    private String dataType;
}
