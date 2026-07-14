package com.javaoffers.nexus.core.service;

import com.javaoffers.nexus.core.model.PageParam;
import com.javaoffers.nexus.core.model.Suite;
import com.javaoffers.nexus.core.repository.ApiMapper;
import com.javaoffers.nexus.core.repository.SuiteMapper;
import com.javaoffers.nexus.pkg.exception.BizException;
import org.springframework.stereotype.Service;

import java.util.List;

/** 对应 Go core/service/suite_service.go。 */
@Service
public class SuiteService {

    private final SuiteMapper suiteMapper;
    private final ApiMapper apiMapper;

    public SuiteService(SuiteMapper suiteMapper, ApiMapper apiMapper) {
        this.suiteMapper = suiteMapper;
        this.apiMapper = apiMapper;
    }

    public void createSuite(Suite s) {
        suiteMapper.create(s);
    }

    public void updateSuite(Suite s) {
        suiteMapper.update(s);
    }

    public void deleteSuite(Long id) {
        if (apiMapper.countBySuiteId(id) > 0) {
            throw new BizException("该套件下存在接口，无法删除");
        }
        suiteMapper.delete(id);
    }

    public List<Suite> listSuites() {
        return suiteMapper.listAll();
    }

    public Page<Suite> pageSuites(PageParam p, String suiteName, Integer suiteFlag) {
        int pn = p.getPageNum() <= 0 ? 1 : p.getPageNum();
        int ps = p.getPageSize() <= 0 ? 10 : p.getPageSize();
        List<Suite> list = suiteMapper.page(pn, ps, suiteName, suiteFlag);
        long total = suiteMapper.count(suiteName, suiteFlag);
        return new Page<>(total, list);
    }
}
