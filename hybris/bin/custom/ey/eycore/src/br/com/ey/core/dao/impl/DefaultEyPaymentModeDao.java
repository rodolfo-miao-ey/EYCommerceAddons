package br.com.ey.core.dao.impl;

import br.com.braspag.model.BraspagPaymentModeModel;
import br.com.ey.core.dao.EyPaymentModeDao;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

public class DefaultEyPaymentModeDao extends DefaultGenericDao<BraspagPaymentModeModel>
        implements EyPaymentModeDao {
    public DefaultEyPaymentModeDao(final String typecode) {
        super(typecode);
    }

    @Override
    public List<BraspagPaymentModeModel> findInstallments() {
        BraspagPaymentModeModel paymentMode = new BraspagPaymentModeModel();
        paymentMode.setActive(true);

        List<BraspagPaymentModeModel> paymentModeList = getFlexibleSearchService().getModelsByExample(paymentMode);

        paymentModeList = paymentModeList.stream().sorted(Comparator.comparing(
                BraspagPaymentModeModel::getCode)).collect(Collectors.toList());

        return paymentModeList;
    }

    @Override
    public BraspagPaymentModeModel findPaymentInstallmentByCode(final String code) {
        validateParameterNotNull(code, "Payment mode code must not be null!");

        return findUnique(Collections.singletonMap(BraspagPaymentModeModel.CODE, code));
    }

    private BraspagPaymentModeModel findUnique(Map<String, Object> params)
    {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT {p:" + BraspagPaymentModeModel.PK + "} ");
        stringBuilder.append("FROM {" + BraspagPaymentModeModel._TYPECODE + " AS p }");
        stringBuilder.append("WHERE {p:" + BraspagPaymentModeModel.CODE + "} = ?code");

        final FlexibleSearchQuery query = new FlexibleSearchQuery(stringBuilder.toString());
        query.addQueryParameters(params);

        final List<BraspagPaymentModeModel> results = getFlexibleSearchService().<BraspagPaymentModeModel>search(query).getResult();
        if (results.size() > 1) {
            throw new AmbiguousIdentifierException("Found " + results.size() +
                    " objects from type " + "WhiteMartinsPaymentModeModel" + " with " + params.toString() + "'");
        } else {
            return results.isEmpty() ? null : results.get(0);
        }
    }

}

