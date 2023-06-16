package br.com.ey.facades.populators;

import br.com.braspag.facades.payment.data.CreditCardBrandData;
import br.com.braspag.model.CreditCardBrandModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.log4j.Logger;

public class EyCreditCardBrandPopulator implements Populator<CreditCardBrandModel, CreditCardBrandData>
{
    private static final Logger LOG = Logger.getLogger(EyCreditCardBrandPopulator.class);

    @Override
    public void populate(final CreditCardBrandModel source, final CreditCardBrandData target)
            throws ConversionException
    {
        target.setCode(source.getCode());
        target.setName(source.getName());
    }
}
