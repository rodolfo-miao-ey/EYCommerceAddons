package br.com.ey.core.services.payment;

import br.com.braspag.model.CreditCardBrandModel;

import java.util.List;
import java.util.Set;

public interface EyCreditCardBrandService {

    List<CreditCardBrandModel> getAllCreditCardBrands();

    Set<CreditCardBrandModel> getAllSupportedCreditCardBrands();

    CreditCardBrandModel getCreditCardBrandByCode(final String code);

}
