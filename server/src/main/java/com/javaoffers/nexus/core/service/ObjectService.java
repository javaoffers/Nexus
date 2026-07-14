package com.javaoffers.nexus.core.service;

import com.javaoffers.nexus.core.model.NexusObject;
import com.javaoffers.nexus.core.model.PageParam;
import com.javaoffers.nexus.core.model.Parameter;
import com.javaoffers.nexus.core.repository.ObjectMapper;
import com.javaoffers.nexus.core.repository.ParameterMapper;
import com.javaoffers.nexus.pkg.exception.BizException;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 对应 Go core/service/object_service.go。 */
@Service
public class ObjectService {

    private final ObjectMapper objectMapper;
    private final ParameterMapper parameterMapper;

    public ObjectService(ObjectMapper objectMapper, ParameterMapper parameterMapper) {
        this.objectMapper = objectMapper;
        this.parameterMapper = parameterMapper;
    }

    @Data
    public static class ObjectInfoDTO {
        private Long id;
        private String objectKey;
        private String objectName;
        private String objectDesc;
        private List<Parameter> props;
    }

    public void createObject(NexusObject o, List<Parameter> props) {
        objectMapper.create(o);
        o = objectMapper.getByKey(o.getObjectKey());
        if (props != null) {
            saveObjectProps(o.getId(), props);
        }
    }

    public void updateObject(NexusObject o, List<Parameter> props) {
        objectMapper.update(o);
        if (props != null) {
            saveObjectProps(o.getId(), props);
        }
    }

    public void deleteObject(Long id) {
        objectMapper.delete(id);
    }

    public ObjectInfoDTO getObjectInfo(Long id) {
        NexusObject o = objectMapper.getById(id);
        if (o == null) {
            throw new BizException("对象不存在");
        }
        ObjectInfoDTO dto = new ObjectInfoDTO();
        dto.setId(o.getId());
        dto.setObjectKey(o.getObjectKey());
        dto.setObjectName(o.getObjectName());
        dto.setObjectDesc(o.getObjectDesc());
        dto.setProps(parameterMapper.listBySource("object", id));
        return dto;
    }

    public List<NexusObject> listObjects() {
        return objectMapper.listAll();
    }

    public Page<NexusObject> pageObjects(PageParam p, String objectName) {
        int pn = p.getPageNum() <= 0 ? 1 : p.getPageNum();
        int ps = p.getPageSize() <= 0 ? 10 : p.getPageSize();
        List<NexusObject> list = objectMapper.page(pn, ps, objectName);
        long total = objectMapper.count(objectName);
        return new Page<>(total, list);
    }

    public boolean checkObjectKeyExists(String objectKey, Long excludeID) {
        return objectMapper.objectKeyExists(objectKey, excludeID);
    }

    private void saveObjectProps(Long objectID, List<Parameter> props) {
        parameterMapper.deleteBySource("object", objectID);
        for (Parameter p : props) {
            p.setSourceType("object");
            p.setSourceId(objectID);
            p.setParamType(3);
        }
        parameterMapper.batchCreate(props);
    }

    public List<Map<String, Object>> getDataTypeOptions() {
        List<NexusObject> objects = objectMapper.listAll();
        if (objects == null) {
            throw new BizException("获取数据类型失败");
        }
        List<Map<String, Object>> result = new ArrayList<>();
        result.add(type("String", "字符串", 1));
        result.add(type("Integer", "整数", 1));
        result.add(type("Double", "小数", 1));
        result.add(type("Boolean", "布尔", 1));
        result.add(type("Date", "日期", 1));
        result.add(type("Time", "时间", 1));
        result.add(type("List", "列表", 2));

        for (NexusObject o : objects) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("type", "Object");
            m.put("displayName", o.getObjectName());
            m.put("objectKey", o.getObjectKey());
            m.put("objectStructure", parameterMapper.listBySource("object", o.getId()));
            m.put("dataTypeClassify", 3);
            result.add(m);
        }
        return result;
    }

    private Map<String, Object> type(String type, String displayName, int classify) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("type", type);
        m.put("displayName", displayName);
        m.put("dataTypeClassify", classify);
        return m;
    }
}
