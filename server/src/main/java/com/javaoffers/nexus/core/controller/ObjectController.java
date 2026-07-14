package com.javaoffers.nexus.core.controller;

import com.javaoffers.nexus.core.model.NexusObject;
import com.javaoffers.nexus.core.model.PageParam;
import com.javaoffers.nexus.core.model.Parameter;
import com.javaoffers.nexus.core.service.ObjectService;
import com.javaoffers.nexus.pkg.exception.BizException;
import com.javaoffers.nexus.pkg.response.R;
import com.javaoffers.nexus.pkg.response.Result;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** 对应 Go core/handler/object_handler.go。 */
@RestController
@RequestMapping("/api/v1/object")
public class ObjectController {

    private final ObjectService objectService;

    public ObjectController(ObjectService objectService) {
        this.objectService = objectService;
    }

    @Data
    public static class ObjectAddParam {
        private String objectKey;
        private String objectName;
        private String objectDesc;
        private List<Parameter> props;
    }

    @Data
    public static class ObjectUpdateParam {
        private Long id;
        private String objectKey;
        private String objectName;
        private String objectDesc;
        private List<Parameter> props;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class ObjectQueryParam extends PageParam {
        private String objectName;
    }

    @Data
    public static class ObjectKeyParam {
        private Long id;
        private String objectKey;
    }

    @PostMapping("/add")
    public Result<?> addObject(@RequestBody ObjectAddParam param) {
        if (param.getObjectKey() == null || param.getObjectName() == null) {
            return R.error(400, "参数错误");
        }
        NexusObject o = new NexusObject();
        o.setObjectKey(param.getObjectKey());
        o.setObjectName(param.getObjectName());
        o.setObjectDesc(param.getObjectDesc());
        try {
            objectService.createObject(o, param.getProps());
            return R.ok(true);
        } catch (BizException e) {
            return R.error(4001, e.getMessage());
        }
    }

    @PutMapping("/update")
    public Result<?> updateObject(@RequestBody ObjectUpdateParam param) {
        if (param.getId() == null) {
            return R.error(400, "参数错误");
        }
        NexusObject o = new NexusObject();
        o.setId(param.getId());
        o.setObjectKey(param.getObjectKey());
        o.setObjectName(param.getObjectName());
        o.setObjectDesc(param.getObjectDesc());
        try {
            objectService.updateObject(o, param.getProps());
            return R.ok(true);
        } catch (BizException e) {
            return R.error(4002, e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> deleteObject(@PathVariable Long id) {
        try {
            objectService.deleteObject(id);
            return R.ok(true);
        } catch (BizException e) {
            return R.error(4003, e.getMessage());
        }
    }

    @GetMapping("/info/{id}")
    public Result<?> getObjectInfo(@PathVariable Long id) {
        try {
            return R.ok(objectService.getObjectInfo(id));
        } catch (BizException e) {
            return R.error(4004, e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<?> listObjects() {
        try {
            return R.ok(objectService.listObjects());
        } catch (BizException e) {
            return R.error(4005, e.getMessage());
        }
    }

    @PostMapping("/page")
    public Object pageObjects(@RequestBody ObjectQueryParam param) {
        try {
            com.javaoffers.nexus.core.service.Page<NexusObject> p = objectService.pageObjects(param, param.getObjectName());
            return R.okPage(p.getTotal(), p.getRecords());
        } catch (BizException e) {
            return R.error(4006, e.getMessage());
        }
    }

    @PostMapping("/exist/key")
    public Result<?> checkObjectKeyExists(@RequestBody ObjectKeyParam param) {
        if (param.getObjectKey() == null) {
            return R.error(400, "参数错误");
        }
        return R.ok(objectService.checkObjectKeyExists(param.getObjectKey(), param.getId()));
    }
}
