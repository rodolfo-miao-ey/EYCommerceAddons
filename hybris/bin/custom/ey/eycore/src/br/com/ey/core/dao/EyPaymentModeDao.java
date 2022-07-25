package br.com.ey.core.dao;

import br.com.braspag.model.BraspagPaymentModeModel;

import java.util.List;

public interface EyPaymentModeDao {

    List<BraspagPaymentModeModel> findInstallments();

    BraspagPaymentModeModel findPaymentInstallmentByCode(final String code);

}
