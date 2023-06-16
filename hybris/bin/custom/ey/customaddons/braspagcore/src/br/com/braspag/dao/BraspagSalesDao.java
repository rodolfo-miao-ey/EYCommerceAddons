package br.com.braspag.dao;

import br.com.braspag.model.OrderPaymentLogInfoModel;

public interface BraspagSalesDao {
    OrderPaymentLogInfoModel getLog(String orderCode);
}
