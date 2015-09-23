package com.adms.cs.service;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import com.adms.entity.cs.ConfirmationRecord;

public interface ConfirmationRecordService {

	public ConfirmationRecord add(ConfirmationRecord example, String userLogin) throws Exception;

	public ConfirmationRecord update(ConfirmationRecord example, String userLogin) throws Exception;

	public List<ConfirmationRecord> findAll() throws Exception;
	
	public ConfirmationRecord find(Long id) throws Exception;
	
	public List<ConfirmationRecord> find(ConfirmationRecord example) throws Exception;

	public List<ConfirmationRecord> findByHql(String hql, Object...vals) throws Exception;

	public List<ConfirmationRecord> findByNamedQuery(String namedQuery, Object...vals) throws Exception;

	public List<ConfirmationRecord> findByCriteria(DetachedCriteria detachedCriteria) throws Exception;

}
