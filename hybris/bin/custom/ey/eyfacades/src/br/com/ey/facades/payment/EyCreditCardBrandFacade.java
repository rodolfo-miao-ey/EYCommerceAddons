package br.com.ey.facades.payment;

import br.com.braspag.facades.payment.data.CreditCardBrandData;

import java.util.List;
import java.util.Set;

public interface EyCreditCardBrandFacade {

    List<CreditCardBrandData> getAllCreditCardBrands();

    Set<CreditCardBrandData> getAllSupportedCreditCardBrands();

    CreditCardBrandData getCreditCardBrandByCode(final String code);

}
