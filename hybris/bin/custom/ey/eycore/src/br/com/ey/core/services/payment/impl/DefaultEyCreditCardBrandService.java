package br.com.ey.core.services.payment.impl;

import br.com.braspag.model.CreditCardBrandModel;
import br.com.ey.core.dao.EyCreditCardBrandDAO;
import br.com.ey.core.services.payment.EyCreditCardBrandService;

import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DefaultEyCreditCardBrandService implements EyCreditCardBrandService
{
    private static final Logger LOG = Logger.getLogger(DefaultEyCreditCardBrandService.class);

    private BaseStoreService baseStoreService;
    private EyCreditCardBrandDAO eyCreditCardBrandDAO;

    @Override
    public List<CreditCardBrandModel> getAllCreditCardBrands()
    {
        return getEyCreditCardBrandDAO().getAllCreditCardBrands();
    }

    @Override
    public Set<CreditCardBrandModel> getAllSupportedCreditCardBrands()
    {
        final BaseStoreModel baseStoreModel = getBaseStoreService().getCurrentBaseStore();
        if(baseStoreModel != null && CollectionUtils.isNotEmpty(baseStoreModel.getSupportedCreditCardBrands()))
        {
            return baseStoreModel.getSupportedCreditCardBrands();
        }
        return new HashSet<>();
    }

    @Override
    public CreditCardBrandModel getCreditCardBrandByCode(final String code)
    {
        return getEyCreditCardBrandDAO().getCreditCardBrandByCode(code);
    }

    public BaseStoreService getBaseStoreService()
    {
        return baseStoreService;
    }

    public void setBaseStoreService(final BaseStoreService baseStoreService)
    {
        this.baseStoreService = baseStoreService;
    }

    public EyCreditCardBrandDAO getEyCreditCardBrandDAO()
    {
        return eyCreditCardBrandDAO;
    }

    public void setEyCreditCardBrandDAO(final EyCreditCardBrandDAO eyCreditCardBrandDAO)
    {
        this.eyCreditCardBrandDAO = eyCreditCardBrandDAO;
    }
}
