package com.javaoffers.nexus.core.controller;

import com.javaoffers.nexus.core.model.PageParam;
import com.javaoffers.nexus.core.model.Suite;
import com.javaoffers.nexus.core.service.SuiteService;
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

/** 对应 Go core/handler/suite_handler.go。 */
@RestController
@RequestMapping("/api/v1/suite")
public class SuiteController {

    private final SuiteService suiteService;

    public SuiteController(SuiteService suiteService) {
        this.suiteService = suiteService;
    }

    @Data
    public static class SuiteAddParam {
        private String suiteCode;
        private String suiteName;
        private String suiteImage;
        private String suiteDesc;
    }

    @Data
    public static class SuiteUpdateParam {
        private Long id;
        private String suiteCode;
        private String suiteName;
        private String suiteImage;
        private String suiteDesc;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class SuiteQueryParam extends PageParam {
        private String suiteName;
        private Integer suiteFlag;
    }

    @PostMapping("/add")
    public Result<?> addSuite(@RequestBody SuiteAddParam param) {
        if (param.getSuiteName() == null || param.getSuiteName().isEmpty()) {
            return R.error(400, "参数错误");
        }
        Suite s = new Suite();
        s.setSuiteCode(param.getSuiteCode());
        s.setSuiteName(param.getSuiteName());
        s.setSuiteImage(param.getSuiteImage());
        s.setSuiteDesc(param.getSuiteDesc());
        try {
            suiteService.createSuite(s);
            return R.ok(true);
        } catch (BizException e) {
            return R.error(2001, e.getMessage());
        }
    }

    @PutMapping("/update")
    public Result<?> updateSuite(@RequestBody SuiteUpdateParam param) {
        if (param.getId() == null) {
            return R.error(400, "参数错误");
        }
        Suite s = new Suite();
        s.setId(param.getId());
        s.setSuiteCode(param.getSuiteCode());
        s.setSuiteName(param.getSuiteName());
        s.setSuiteImage(param.getSuiteImage());
        s.setSuiteDesc(param.getSuiteDesc());
        try {
            suiteService.updateSuite(s);
            return R.ok(true);
        } catch (BizException e) {
            return R.error(2002, e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> deleteSuite(@PathVariable Long id) {
        try {
            suiteService.deleteSuite(id);
            return R.ok(true);
        } catch (BizException e) {
            return R.error(2003, e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<?> listSuites() {
        try {
            return R.ok(suiteService.listSuites());
        } catch (BizException e) {
            return R.error(2004, e.getMessage());
        }
    }

    @PostMapping("/page")
    public Object pageSuites(@RequestBody SuiteQueryParam param) {
        try {
            com.javaoffers.nexus.core.service.Page<Suite> p = suiteService.pageSuites(param, param.getSuiteName(), param.getSuiteFlag());
            return R.okPage(p.getTotal(), p.getRecords());
        } catch (BizException e) {
            return R.error(2005, e.getMessage());
        }
    }

    @PostMapping("/market")
    public Object suiteMarketPage(@RequestBody SuiteQueryParam param) {
        try {
            com.javaoffers.nexus.core.service.Page<Suite> p = suiteService.pageSuites(param, param.getSuiteName(), param.getSuiteFlag());
            return R.okPage(p.getTotal(), p.getRecords());
        } catch (BizException e) {
            return R.error(2006, e.getMessage());
        }
    }

    @PostMapping("/market/classify")
    public Result<?> suiteMarketClassify() {
        try {
            return R.ok(suiteService.listSuites());
        } catch (BizException e) {
            return R.error(2006, e.getMessage());
        }
    }
}
