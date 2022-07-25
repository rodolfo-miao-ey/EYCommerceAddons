package br.com.ey.facades.order.impl;

import br.com.braspag.facades.order.data.BraspagPaymentModeData;
import br.com.braspag.model.BraspagPaymentModeModel;
import br.com.ey.core.services.order.EyCheckoutService;
import br.com.ey.facades.order.EyCheckoutFacade;
import de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.impl.DefaultCheckoutFacade;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class DefaultEyCheckoutFacade extends DefaultCheckoutFacade implements EyCheckoutFacade {

    private EyCheckoutService eyCheckoutService;
    private Converter<BraspagPaymentModeModel, BraspagPaymentModeData> eyPaymentModeConverter;

    public List<BraspagPaymentModeData> getInstallments() {

        return getEyPaymentModeConverter().convertAll(
                getEyCheckoutService().getInstallments());

    }

    @Override
    public <T extends AbstractOrderData> T placeOrder(final PlaceOrderData placeOrderData) throws InvalidCartException
    {

       return (T) super.placeOrder();

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
