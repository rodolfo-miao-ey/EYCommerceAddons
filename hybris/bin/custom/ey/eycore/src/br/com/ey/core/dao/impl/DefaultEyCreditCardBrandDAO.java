package br.com.ey.core.dao.impl;


import br.com.braspag.model.CreditCardBrandModel;
import br.com.ey.core.dao.EyCreditCardBrandDAO;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.log4j.Logger;

import java.util.List;

public class DefaultEyCreditCardBrandDAO implements EyCreditCardBrandDAO
{
    private static final Logger LOG = Logger.getLogger(DefaultEyCreditCardBrandDAO.class);

    private static final String SEARCH_ALL_QUERY = "SELECT DISTINCT {pk} FROM {CreditCardBrand}";
    private static final String SEARCH_BY_CODE_QUERY = SEARCH_ALL_QUERY + " WHERE {code} = ?code";

    private FlexibleSearchService flexibleSearchService;

    @Override
    public List<CreditCardBrandModel> getAllCreditCardBrands()
    {
        final SearchResult<CreditCardBrandModel> search =
                flexibleSearchService.search(new FlexibleSearchQuery(SEARCH_ALL_QUERY));
        return search.getResult();
    }

    @Override
    public CreditCardBrandModel getCreditCardBrandByCode(final String code)
    {
        final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(SEARCH_BY_CODE_QUERY);
        flexibleSearchQuery.addQueryParameter(CreditCardBrandModel.CODE, code);
        final SearchResult<CreditCardBrandModel> search = flexibleSearchService.search(flexibleSearchQuery);
        return search.getResult().stream().findFirst().orElse(null);
    }

    public FlexibleSearchService getFlexibleSearchService()
    {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
    {
        this.flexibleSearchService = flexibleSearchService;
    }
}
