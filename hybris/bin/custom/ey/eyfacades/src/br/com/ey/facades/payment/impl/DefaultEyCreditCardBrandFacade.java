package br.com.ey.facades.payment.impl;

import br.com.braspag.facades.payment.data.CreditCardBrandData;
import br.com.braspag.model.CreditCardBrandModel;
import br.com.ey.core.services.payment.EyCreditCardBrandService;
import br.com.ey.facades.payment.EyCreditCardBrandFacade;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DefaultEyCreditCardBrandFacade implements EyCreditCardBrandFacade
{
    private static final Logger LOG = Logger.getLogger(DefaultEyCreditCardBrandFacade.class);

    private Converter<CreditCardBrandModel, CreditCardBrandData> eyCreditCardBrandConverter;
    private EyCreditCardBrandService eyCreditCardBrandService;

    @Override
    public List<CreditCardBrandData> getAllCreditCardBrands()
    {
        final List<CreditCardBrandModel> creditCardBrandModels =
                getEyCreditCardBrandService().getAllCreditCardBrands();
        if(CollectionUtils.isNotEmpty(creditCardBrandModels))
        {
            return getEyCreditCardBrandConverter().convertAll(creditCardBrandModels);
        }
        return new ArrayList<>();
    }

    @Override
    public Set<CreditCardBrandData> getAllSupportedCreditCardBrands()
    {
        final Set<CreditCardBrandModel> creditCardBrandModelSet =
                getEyCreditCardBrandService().getAllSupportedCreditCardBrands();
        if(CollectionUtils.isNotEmpty(creditCardBrandModelSet))
        {
            return new HashSet<>(getEyCreditCardBrandConverter()
                    .convertAll(new ArrayList<>(creditCardBrandModelSet)));
        }
        return new HashSet<>();
    }

    @Override
    public CreditCardBrandData getCreditCardBrandByCode(final String code)
    {
        final CreditCardBrandModel creditCardBrandModel =
                getEyCreditCardBrandService().getCreditCardBrandByCode(code);
        if(creditCardBrandModel != null)
        {
            return getEyCreditCardBrandConverter().convert(creditCardBrandModel);
        }
        return null;
    }

    public Converter<CreditCardBrandModel, CreditCardBrandData> getEyCreditCardBrandConverter()
    {
        return eyCreditCardBrandConverter;
    }

    public void setEyCreditCardBrandConverter(
            final Converter<CreditCardBrandModel, CreditCardBrandData> eyCreditCardBrandConverter)
    {
        this.eyCreditCardBrandConverter = eyCreditCardBrandConverter;
    }

    public EyCreditCardBrandService getEyCreditCardBrandService()
    {
        return eyCreditCardBrandService;
    }

    public void setEyCreditCardBrandService(
            final EyCreditCardBrandService eyCreditCardBrandService)
    {
        this.eyCreditCardBrandService = eyCreditCardBrandService;
    }
}
