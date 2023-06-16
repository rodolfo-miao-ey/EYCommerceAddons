package br.com.braspag.facades;

import br.com.braspag.service.exception.BraspagApiException;
import br.com.braspag.facades.order.data.BrasPagPaymentMethodData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;

public interface BraspagFacade {

    boolean authorizePayment(final AbstractOrderModel cartModel, final CustomerData customerData,
                             final Integer installments,
                             final String securityCode,
                             final String cardNumber,
                             final String cardHolder,
                             final String cardBrand,
                             final String expiryMonth,
                             final String expiryYear,
                             final String documentType,
                             final String documentNumber,
                             final Double amount)
            throws BraspagApiException;


    void saveLogPayment(final AbstractOrderModel orderModel,
                        final Integer installments,
                        final String cardNumber,
                        final String cardHolder,
                        final String cardBrand,
                        final String expiryMonth,
                        final String expiryYear,
                        final String documentType,
                        final String documentNumber,
                        final Double amount,
                        final String message);

    boolean voidPayment(final AbstractOrderModel cartModel) throws BraspagApiException;

    void handlePaymentForm(BrasPagPaymentMethodData paymentMethodData, String remoteAddress);

    PaymentInfoModel createPaymentInfo(AbstractOrderModel cartModel, BrasPagPaymentMethodData paymentMethodData);

    void updateLog(String orderCode, String cartCode);

}