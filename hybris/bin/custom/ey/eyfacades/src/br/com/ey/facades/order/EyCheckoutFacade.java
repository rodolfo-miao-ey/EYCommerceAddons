package br.com.ey.facades.order;

import br.com.braspag.facades.order.data.BraspagPaymentModeData;
import de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.order.InvalidCartException;

import java.util.List;

public interface EyCheckoutFacade extends CheckoutFacade {

    List<BraspagPaymentModeData> getInstallments();

    /**
     * Place Order function
     *
     * @param placeOrderData
     * @return OrderData
     * @throws InvalidCartException
     */
    <T extends AbstractOrderData> T placeOrder(PlaceOrderData placeOrderData) throws InvalidCartException;

}
