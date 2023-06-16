package br.com.ey.core.services.order.impl;

import br.com.braspag.model.BraspagPaymentModeModel;
import br.com.ey.core.dao.EyPaymentModeDao;
import br.com.ey.core.services.order.EyCheckoutService;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCheckoutService;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultEyCheckoutService extends DefaultCommerceCheckoutService implements EyCheckoutService {

    private EyPaymentModeDao eyPaymentModeDao;
    
    @Override
    public List<BraspagPaymentModeModel> getInstallments(){


        Set<BraspagPaymentModeModel> payments = new HashSet<>();
        payments.addAll(getEyPaymentModeDao().findInstallments());

        List<BraspagPaymentModeModel> paymentsReturn = new ArrayList<>(payments);
        paymentsReturn = paymentsReturn.stream().sorted(Comparator.comparing(
                BraspagPaymentModeModel::getCode)).collect(Collectors.toList());

        return paymentsReturn;
    }

    @Override
    public BraspagPaymentModeModel findPaymentInstallmentByCode(final String code)
    {
        return getEyPaymentModeDao().findPaymentInstallmentByCode(code);
    }


    public EyPaymentModeDao getEyPaymentModeDao() {
        return eyPaymentModeDao;
    }

    public void setEyPaymentModeDao(EyPaymentModeDao eyPaymentModeDao) {
        this.eyPaymentModeDao = eyPaymentModeDao;
    }

}
