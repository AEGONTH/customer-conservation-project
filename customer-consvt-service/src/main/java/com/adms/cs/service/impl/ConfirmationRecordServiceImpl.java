package com.adms.cs.service.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adms.cs.dao.ConfirmationRecordDao;
import com.adms.cs.service.ConfirmationRecordService;
import com.adms.entity.cs.ConfirmationRecord;

@Service("confirmationRecordService")
@Transactional
public class ConfirmationRecordServiceImpl implements ConfirmationRecordService {

	@Autowired
	private ConfirmationRecordDao confirmationRecordDao;
	
	public ConfirmationRecordServiceImpl() {
		
	}

	public void setConfirmationRecordDao(ConfirmationRecordDao confirmationRecordDao) {
		this.confirmationRecordDao = confirmationRecordDao;
	}
	
	@Override
	public List<ConfirmationRecord> findAll() throws Exception {
		return confirmationRecordDao.findAll();
	}

	@Override
	public ConfirmationRecord add(ConfirmationRecord example, String userLogin) throws Exception {
		return confirmationRecordDao.save(example);
	}
	
	@Override
	public ConfirmationRecord update(ConfirmationRecord example, String userLogin) throws Exception {
		return confirmationRecordDao.save(example);
	}

	@Override
	public ConfirmationRecord find(Long id) throws Exception {
		return confirmationRecordDao.find(id);
	}
	
	@Override
	public List<ConfirmationRecord> find(ConfirmationRecord example) throws Exception {
		return confirmationRecordDao.find(example);
	}
	
	@Override
	public List<ConfirmationRecord> findByHql(String hql, Object...vals) throws Exception {
		return confirmationRecordDao.findByHQL(hql, vals);
	}

	@Override
	public List<ConfirmationRecord> findByNamedQuery(String namedQuery, Object...vals) throws Exception {
		return confirmationRecordDao.findByNamedQuery(namedQuery, vals);
	}
	
	@Override
	public List<ConfirmationRecord> findByCriteria(DetachedCriteria detachedCriteria) throws Exception {
		return confirmationRecordDao.findByCriteria(detachedCriteria);
	}
	
}
