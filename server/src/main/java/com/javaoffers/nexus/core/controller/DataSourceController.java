package com.javaoffers.nexus.core.controller;

import com.javaoffers.nexus.core.model.DataSource;
import com.javaoffers.nexus.core.model.PageParam;
import com.javaoffers.nexus.core.service.DataSourceService;
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

/** 对应 Go core/handler/data_source_handler.go。 */
@RestController
@RequestMapping("/api/v1/datasource")
public class DataSourceController {

    private final DataSourceService dataSourceService;

    public DataSourceController(DataSourceService dataSourceService) {
        this.dataSourceService = dataSourceService;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class DsQueryParam extends PageParam {
        private String dataSourceName;
        private String dataSourceType;
    }

    @PostMapping("/add")
    public Result<?> addDataSource(@RequestBody DataSource ds) {
        try {
            dataSourceService.createDataSource(ds);
            return R.ok(true);
        } catch (BizException e) {
            return R.error(10001, e.getMessage());
        }
    }

    @PutMapping("/update")
    public Result<?> updateDataSource(@RequestBody DataSource ds) {
        try {
            dataSourceService.updateDataSource(ds);
            return R.ok(true);
        } catch (BizException e) {
            return R.error(10002, e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> deleteDataSource(@PathVariable Long id) {
        try {
            dataSourceService.deleteDataSource(id);
            return R.ok(true);
        } catch (BizException e) {
            return R.error(10003, e.getMessage());
        }
    }

    @GetMapping("/info/{id}")
    public Result<?> getDataSourceInfo(@PathVariable Long id) {
        try {
            return R.ok(dataSourceService.getDataSourceByID(id));
        } catch (BizException e) {
            return R.error(10004, e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<?> listDataSources() {
        try {
            return R.ok(dataSourceService.listDataSources());
        } catch (BizException e) {
            return R.error(10005, e.getMessage());
        }
    }

    @PostMapping("/page")
    public Object pageDataSources(@RequestBody DsQueryParam param) {
        try {
            com.javaoffers.nexus.core.service.Page<DataSource> p =
                    dataSourceService.pageDataSources(param, param.getDataSourceName(), param.getDataSourceType());
            return R.okPage(p.getTotal(), p.getRecords());
        } catch (BizException e) {
            return R.error(10006, e.getMessage());
        }
    }

    @GetMapping("/connect/{id}")
    public Result<?> testDataSourceConnection(@PathVariable Long id) {
        try {
            dataSourceService.testDataSourceConnection(id);
            return R.ok(true);
        } catch (BizException e) {
            return R.error(10007, e.getMessage());
        }
    }
}
