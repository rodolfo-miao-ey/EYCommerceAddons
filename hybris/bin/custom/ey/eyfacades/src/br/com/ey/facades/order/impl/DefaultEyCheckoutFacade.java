package br.com.ey.facades.order.impl;

import br.com.braspag.facades.order.data.BraspagPaymentModeData;
import br.com.braspag.model.BraspagPaymentModeModel;
import br.com.ey.core.services.order.EyCheckoutService;
import br.com.ey.facades.order.EyCheckoutFacade;
import de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData;
import de.hybris.platform.b2bacceleratorfacades.exception.EntityValidationException;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BCommentData;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BReplenishmentRecurrenceEnum;
import de.hybris.platform.b2bacceleratorfacades.order.data.TriggerData;
import de.hybris.platform.b2bacceleratorservices.enums.CheckoutPaymentType;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.impl.DefaultCheckoutFacade;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import de.hybris.platform.b2bacceleratorfacades.order.impl.DefaultB2BCheckoutFacade;

import java.util.List;

import static de.hybris.platform.util.localization.Localization.getLocalizedString;

public class DefaultEyCheckoutFacade extends DefaultB2BCheckoutFacade implements EyCheckoutFacade {

    private static final String CART_CHECKOUT_NO_QUOTE_DESCRIPTION = "cart.no.quote.description";
    private static final String CART_CHECKOUT_REPLENISHMENT_NO_STARTDATE = "cart.replenishment.no.startdate";
    private static final String CART_CHECKOUT_REPLENISHMENT_NO_FREQUENCY = "cart.replenishment.no.frequency";

    private EyCheckoutService eyCheckoutService;
    private Converter<BraspagPaymentModeModel, BraspagPaymentModeData> eyPaymentModeConverter;

    public List<BraspagPaymentModeData> getInstallments() {

        return getEyPaymentModeConverter().convertAll(
                getEyCheckoutService().getInstallments());

    }

    public void updateCart(){
        final CartModel cartModel = getCart();
        if (cartModel == null)
        {
            return;
        }

        cartModel.setCalculated(true);
        getModelService().save(cartModel);

    }

    @Override
    public <T extends AbstractOrderData> T placeOrder(final PlaceOrderData placeOrderData) throws InvalidCartException
    {
        if (isValidCheckoutCart(placeOrderData))
        {
            // validate quote negotiation
            if (placeOrderData.getNegotiateQuote() != null && placeOrderData.getNegotiateQuote().equals(Boolean.TRUE))
            {
                if (StringUtils.isBlank(placeOrderData.getQuoteRequestDescription()))
                {
                    throw new EntityValidationException(getLocalizedString(CART_CHECKOUT_NO_QUOTE_DESCRIPTION));
                }
                else
                {
                    final B2BCommentData b2BComment = new B2BCommentData();
                    b2BComment.setComment(placeOrderData.getQuoteRequestDescription());

                    final CartData cartData = new CartData();
                    cartData.setB2BComment(b2BComment);

                    updateCheckoutCart(cartData);
                }
            }

            // validate replenishment
            if (placeOrderData.getReplenishmentOrder() != null && placeOrderData.getReplenishmentOrder().equals(Boolean.TRUE))
            {
                if (placeOrderData.getReplenishmentStartDate() == null)
                {
                    throw new EntityValidationException(getLocalizedString(CART_CHECKOUT_REPLENISHMENT_NO_STARTDATE));
                }

                if (placeOrderData.getReplenishmentRecurrence().equals(B2BReplenishmentRecurrenceEnum.WEEKLY)
                        && CollectionUtils.isEmpty(placeOrderData.getNDaysOfWeek()))
                {
                    throw new EntityValidationException(getLocalizedString(CART_CHECKOUT_REPLENISHMENT_NO_FREQUENCY));
                }

                final TriggerData triggerData = new TriggerData();
                populateTriggerDataFromPlaceOrderData(placeOrderData, triggerData);

                return (T) scheduleOrder(triggerData);
            }

            return (T) super.placeOrder();
        }

        return null;
    }


    @Override
    public boolean authorizePayment(final String securityCode)
    {
        return true;
    }


    public EyCheckoutService getEyCheckoutService() {
        return eyCheckoutService;
    }

    public void setEyCheckoutService(EyCheckoutService eyCheckoutService) {
        this.eyCheckoutService = eyCheckoutService;
    }

    public Converter<BraspagPaymentModeModel, BraspagPaymentModeData> getEyPaymentModeConverter() {
        return eyPaymentModeConverter;
    }

    public void setEyPaymentModeConverter(Converter<BraspagPaymentModeModel, BraspagPaymentModeData> eyPaymentModeConverter) {
        this.eyPaymentModeConverter = eyPaymentModeConverter;
    }


}
