package br.com.braspag.service;
import br.com.braspag.exceptions.BraspagTimeoutException;
import br.com.braspag.model.OrderPaymentLogInfoModel;
import br.com.braspag.payment.data.AuthorizeRequestDTO;
import br.com.braspag.payment.data.AuthorizeResponseDTO;
import br.com.braspag.payment.data.BraspagAuthorizationPojo;
import br.com.braspag.payment.data.VoidResponseDTO;
import de.hybris.platform.core.model.order.AbstractOrderModel;

public interface BraspagSalesService {

    AuthorizeResponseDTO authorizePayment(AuthorizeRequestDTO request, final AbstractOrderModel orderModel) throws BraspagTimeoutException;

    VoidResponseDTO voidPayment(final String paymentId, final BraspagAuthorizationPojo braspagAuthorizationPojo, final AbstractOrderModel cartModel);

    OrderPaymentLogInfoModel getLog(String orderCode);

}