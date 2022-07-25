
package br.com.ey.core.services.order;

import br.com.braspag.model.BraspagPaymentModeModel;
import java.util.List;

public interface EyCheckoutService {
    List<BraspagPaymentModeModel> getInstallments();

    BraspagPaymentModeModel findPaymentInstallmentByCode(final String code);

}
