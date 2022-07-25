package br.com.ey.eyb2bstorefront.form.validation;

import br.com.ey.eyb2bstorefront.form.BraspagPaymentForm;
import org.springframework.stereotype.Component;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("braspagPaymentFormValidator")
public class BraspagPaymentFormValidator implements Validator {


    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate(Object object, Errors errors) {

        final BraspagPaymentForm paymentForm = (BraspagPaymentForm) object;


        String CardNumber = paymentForm.getCardNumber();
        CardNumber = CardNumber.replaceAll("\\s", "");
        paymentForm.setCardNumber(CardNumber);

        String documentNumber = paymentForm.getDocumentNumber();
        documentNumber = documentNumber.replaceAll("[./-]", "");
        paymentForm.setDocumentNumber(documentNumber);

        validateStringField(paymentForm.getCardNumber(), PaymentFormField.CARDNUMBER, errors);
        validateStringField(paymentForm.getCard_cardType(), PaymentFormField.CARDTYPE, errors);
        validateStringField(paymentForm.getDocumentNumber(), PaymentFormField.DOCUMENTNUMBER, errors);
        validateStringField(paymentForm.getExpiryMonth(), PaymentFormField.EXPIRYMONTH, errors);
        validateStringField(paymentForm.getExpiryYear(), PaymentFormField.EXPIRYYEAR, errors);
        validateStringField(paymentForm.getSecurityNumber(), PaymentFormField.SECURITYNUMBER, errors);
        validateStringField(paymentForm.getPaymentInstallmentCard(), PaymentFormField.INSTALLMENTS, errors);


    }

    protected static void validateStringField(final String addressField, final PaymentFormField fieldType,
                                              final Errors errors) {

        if (addressField == null || StringUtils.isEmpty(addressField)) {
            errors.rejectValue(fieldType.getFieldKey(), fieldType.getErrorKey());
        }
    }

    protected enum PaymentFormField {
        CARDNUMBER("cardNumber", "payment.cardNumber.invalid"),
        CARDNAME("nameOnCard", "payment.nameOnCard.invalid"),
        CARDTYPE("card_cardType", "payment.cardType.invalid"),
        SECURITYNUMBER("securityNumber", "payment.securityNumber.invalid"),
        EXPIRYYEAR("expiryYear", "payment.expiryYear.invalid"),
        EXPIRYMONTH("expiryMonth", "payment.expiryMonth.invalid"),
        INSTALLMENTS("paymentInstallmentCard", "payment.installments.invalid"),
        DOCUMENTNUMBER("documentNumber", "payment.document.invalid");

        private String fieldKey;
        private String errorKey;

        private PaymentFormField(final String fieldKey, final String errorKey) {
            this.fieldKey = fieldKey;
            this.errorKey = errorKey;
        }

        public String getFieldKey() {
            return fieldKey;
        }

        public String getErrorKey() {
            return errorKey;
        }
    }
}