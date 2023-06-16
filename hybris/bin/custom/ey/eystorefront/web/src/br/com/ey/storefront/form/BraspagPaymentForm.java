package br.com.ey.storefront.form;

import javax.validation.constraints.Size;

public class BraspagPaymentForm {

    private String nameOnCard;
    private String cardNumber;
    private String expiryMonth;
    private String expiryYear;
    private String securityNumber;
    private String paymentInstallmentCard;
    private String documentNumber;
    private String documentType;
    private String card_cardType; // NOSONAR


    @Size(min = 1, max = 255, message = "{payment.installment.invalid}")
    public String getPaymentInstallmentCard() {
        return paymentInstallmentCard;
    }

    public void setPaymentInstallmentCard(String paymentInstallmentCard) {
        this.paymentInstallmentCard = paymentInstallmentCard;
    }


    @Size(min = 1, max = 255, message = "{payment.nameOnCard.invalid}")
    public String getNameOnCard() {
        return nameOnCard;
    }

    public void setNameOnCard(final String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(final String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(final String expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public String getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(final String expiryYear) {
        this.expiryYear = expiryYear;
    }

    public String getSecurityNumber() {
        return securityNumber;
    }

    public void setSecurityNumber(String securityNumber) {
        this.securityNumber = securityNumber;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }


    public String getCard_cardType() // NOSONAR
    {
        return card_cardType;
    }

    public void setCard_cardType(final String card_cardType) // NOSONAR
    {
        this.card_cardType = card_cardType;
    }

}
