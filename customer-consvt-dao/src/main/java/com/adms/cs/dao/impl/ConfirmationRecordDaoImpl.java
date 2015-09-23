package com.adms.cs.dao.impl;

import org.springframework.stereotype.Repository;

import com.adms.common.dao.generic.impl.GenericDaoHibernate;
import com.adms.cs.dao.ConfirmationRecordDao;
import com.adms.entity.cs.ConfirmationRecord;

@Repository("confirmationRecordDao")
public class ConfirmationRecordDaoImpl extends GenericDaoHibernate<ConfirmationRecord, Long> implements ConfirmationRecordDao {
	
	public ConfirmationRecordDaoImpl() {
		super(ConfirmationRecord.class);
	}
}
