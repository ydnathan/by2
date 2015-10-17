package com.example.helloworld.dao;

import com.example.helloworld.entities.core.Company;
import com.example.helloworld.entities.core.CompanyOffice;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by vaidyanathan.s on 10/05/15.
 */
public class CompanyOfficeDAO extends AbstractDAO<CompanyOffice> {
    public CompanyOfficeDAO(SessionFactory factory) {
        super(factory);
    }

    public CompanyOffice findById(Long id) {
        return get(id);
    }

    public long create(CompanyOffice companyOffice) {
        return persist(companyOffice).getId();
    }

    public List<CompanyOffice> findAll() {
        return currentSession().createCriteria(CompanyOffice.class).list();
    }
    
    public List<CompanyOffice> findOfficesByCompany(Company company) {
        return currentSession().createCriteria(CompanyOffice.class).add(Restrictions.eq("company", company)).list();
    }
}
