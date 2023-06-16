package br.com.ey.facades.populators;

import br.com.braspag.facades.order.data.BraspagPaymentModeData;
import br.com.braspag.model.BraspagPaymentModeModel;
import de.hybris.platform.converters.Populator;

public class EyPaymentModePopulator implements Populator<BraspagPaymentModeModel, BraspagPaymentModeData> {

    @Override
    public void populate(final BraspagPaymentModeModel source, final BraspagPaymentModeData target) {
        target.setCode(source.getCode());
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setInstallment(source.getInstallment());
    }
}
