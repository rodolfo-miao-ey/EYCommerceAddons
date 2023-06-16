package br.com.braspag.dao.impl;

import br.com.braspag.dao.BraspagSalesDao;
import br.com.braspag.model.OrderPaymentLogInfoModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import javax.annotation.Resource;
import java.util.List;

public class DefaultBraspagSalesDao implements BraspagSalesDao {

    @Resource(name = "flexibleSearchService")
    private FlexibleSearchService flexibleSearchService;

    private static String QUERY_GLOBAL = "SELECT {pk} FROM {OrderPaymentLogInfo}";

    @Override
    public OrderPaymentLogInfoModel getLog(String orderCode) {
        StringBuilder queryString = new StringBuilder(QUERY_GLOBAL);
        queryString.append("WHERE {orderCode}=?orderCode");
        FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(queryString);
        flexibleSearchQuery.addQueryParameter("orderCode", orderCode);

        final SearchResult<OrderPaymentLogInfoModel> search = flexibleSearchService.search(flexibleSearchQuery);
        List<OrderPaymentLogInfoModel> listOrders = search.getResult();
        if(listOrders != null && !listOrders.isEmpty()) {
            return listOrders.get(0);
        }

        return null;
    }

}
