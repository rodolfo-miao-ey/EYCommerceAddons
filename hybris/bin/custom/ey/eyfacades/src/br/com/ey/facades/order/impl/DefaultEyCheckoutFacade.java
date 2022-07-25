package br.com.ey.facades.order.impl;

import br.com.braspag.facades.order.data.BraspagPaymentModeData;
import br.com.ey.facades.order.EyCheckoutFacade;
import de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.impl.DefaultCheckoutFacade;
import de.hybris.platform.order.InvalidCartException;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class DefaultEyCheckoutFacade extends DefaultCheckoutFacade implements EyCheckoutFacade {


    public List<BraspagPaymentModeData> getInstallments() {
      //  return getWhiteMartinsPaymentModeConverter().convertAll(
      //          getWhiteMartinsCheckoutService().getInstallmentsByPaymentType(paymentTypeCode));

        return  null;

    }

    @Override
    public <T extends AbstractOrderData> T placeOrder(final PlaceOrderData placeOrderData) throws InvalidCartException
    {

       return (T) super.placeOrder();

    }


}
