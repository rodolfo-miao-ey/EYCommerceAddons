package br.com.ey.core.dao;

import br.com.braspag.model.CreditCardBrandModel;

import java.util.List;

public interface EyCreditCardBrandDAO {

    List<CreditCardBrandModel> getAllCreditCardBrands();

    CreditCardBrandModel getCreditCardBrandByCode(final String code);

}
