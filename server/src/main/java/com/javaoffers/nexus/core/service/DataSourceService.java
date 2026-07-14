package com.javaoffers.nexus.core.service;

import com.javaoffers.nexus.core.model.DataSource;
import com.javaoffers.nexus.core.model.PageParam;
import com.javaoffers.nexus.core.repository.DataSourceMapper;
import com.javaoffers.nexus.pkg.exception.BizException;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

/** 对应 Go core/service/data_source_service.go。 */
@Service
public class DataSourceService {

    private final DataSourceMapper dataSourceMapper;

    public DataSourceService(DataSourceMapper dataSourceMapper) {
        this.dataSourceMapper = dataSourceMapper;
    }

    public void createDataSource(DataSource d) {
        dataSourceMapper.create(d);
    }

    public void updateDataSource(DataSource d) {
        dataSourceMapper.update(d);
    }

    public void deleteDataSource(Long id) {
        dataSourceMapper.delete(id);
    }

    public DataSource getDataSourceByID(Long id) {
        return dataSourceMapper.getById(id);
    }

    public List<DataSource> listDataSources() {
        return dataSourceMapper.listAll();
    }

    public Page<DataSource> pageDataSources(PageParam p, String name, String dsType) {
        int pn = p.getPageNum() <= 0 ? 1 : p.getPageNum();
        int ps = p.getPageSize() <= 0 ? 10 : p.getPageSize();
        List<DataSource> list = dataSourceMapper.page(pn, ps, name, dsType);
        long total = dataSourceMapper.count(name, dsType);
        return new Page<>(total, list);
    }

    public void testDataSourceConnection(Long id) {
        DataSource ds = dataSourceMapper.getById(id);
        if (ds == null) {
            throw new BizException("数据源不存在");
        }
        String dsn = "jdbc:mysql://" + ds.getAddress() + ":" + ds.getPort() + "/" + ds.getDatabaseName()
                + "?connectTimeout=5000&socketTimeout=5000&useUnicode=true&characterEncoding=utf8";
        try (Connection conn = DriverManager.getConnection(dsn, ds.getUserName(), ds.getPassword())) {
            if (!conn.isValid(5)) {
                throw new BizException("连接失败");
            }
        } catch (Exception e) {
            throw new BizException("连接失败: " + e.getMessage());
        }
    }
}
