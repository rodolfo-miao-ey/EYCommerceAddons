package br.com.braspag.facades.impl;

import br.com.braspag.exceptions.BraspagTimeoutException;
import br.com.braspag.facades.BraspagFacade;
import br.com.braspag.payment.data.*;
import br.com.braspag.service.BraspagSalesService;
import br.com.braspag.service.exception.BraspagApiException;
import br.com.braspag.enums.BraspagPaymentStatus;
import br.com.braspag.model.OrderPaymentLogInfoModel;
//import br.com.whitemartins.core.model.WhiteMartinsPaymentModeModel;
import br.com.braspag.facades.order.data.BrasPagPaymentMethodData;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2bacceleratorservices.enums.CheckoutPaymentType;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
//import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.log4j.Logger;
import org.apache.commons.lang.StringUtils;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

public class DefaultBraspagFacade implements BraspagFacade {

    private static final Logger LOG = Logger.getLogger(DefaultBraspagFacade.class);

    //private static int BRASPAG_STATUS_AUTHORIZED = 1;
    private static int BRASPAG_STATUS_CAPTURED = 2;
    private static int BRASPAG_STATUS_VOID = 10;

    @Resource
    private BraspagSalesService braspagSalesService;

    @Resource
    private SessionService sessionService;

    @Resource
    private ModelService modelService;

    @Resource
    private Converter<AddressModel, AddressData> addressConverter;

    @Resource
    private BaseStoreService baseStoreService;

    @Resource(name = "timeService")
    private TimeService timeService;

    @Resource
    private CartService cartService;

    @Resource(name = "userService")
    private UserService userService;

    @Override
    public boolean authorizePayment(final AbstractOrderModel cartModel, final CustomerData customerData,
                                    final Integer installments,
                                    final String securityCode,
                                    final String cardNumber,
                                    final String cardHolder,
                                    final String cardBrand,
                                    final String expiryMonth,
                                    final String expiryYear,
                                    final String documentType,
                                    final String documentNumber,
                                    final Double amount)
            throws BraspagApiException {

        final BaseStoreModel currentBaseStore = baseStoreService.getCurrentBaseStore();

        cartModel.setCreditCardGatewayCount(cartModel.getCreditCardGatewayCount() == null ? 1 : cartModel.getCreditCardGatewayCount() + 1);
        modelService.save(cartModel);

        AuthorizeRequestDTO authorizeRequestDTO = getAuthorizeRequestDTO(cartModel, customerData,
                installments,
                securityCode,
                cardNumber,
                cardHolder,
                cardBrand,
                expiryMonth,
                expiryYear,
                documentNumber,
                documentType,
                amount,
                currentBaseStore);


        try {

            AuthorizeResponseDTO responseDTO = braspagSalesService.authorizePayment(authorizeRequestDTO, cartModel);

            CreditCardPaymentInfoModel paymentInfoModel = (CreditCardPaymentInfoModel) cartModel.getPaymentInfo();

            if (responseDTO.getPayment() != null && responseDTO.getPayment().getStatus() == BRASPAG_STATUS_CAPTURED){

                paymentInfoModel.setBraspagPaymentId(responseDTO.getPayment().getPaymentId());
                paymentInfoModel.setBraspagStatus(responseDTO.getPayment().getStatus());
                paymentInfoModel.setBraspagReasonMessage(responseDTO.getPayment().getReasonMessage());
                paymentInfoModel.setBraspagReasonCode(responseDTO.getPayment().getReasonCode());
                paymentInfoModel.setBraspagProviderReturnMessage(responseDTO.getPayment().getProviderReturnMessage());
                paymentInfoModel.setBraspagProviderReturnCode(responseDTO.getPayment().getProviderReturnCode());
                paymentInfoModel.setBraspagPaymentStatus(BraspagPaymentStatus.CAPTURED);
                paymentInfoModel.setBraspagAuthCode(responseDTO.getPayment().getAuthorizationCode());
                paymentInfoModel.setBraspagNsuCode(responseDTO.getPayment().getProofOfSale());
                paymentInfoModel.setBraspagCardHolder(cardHolder);
                paymentInfoModel.setBraspagCardBrand(cardBrand);
                paymentInfoModel.setBraspagCardNumber(maskCardNumber(cardNumber));
                paymentInfoModel.setBraspagExpiryMonth(expiryMonth);
                paymentInfoModel.setBraspagExpiryYear(expiryYear);
                paymentInfoModel.setBraspagIssuerId(documentNumber);
                paymentInfoModel.setBraspagInstallments(installments);
                paymentInfoModel.setBraspagTotalValue(amount);
                paymentInfoModel.setBraspagCardHolder(cardHolder);
                paymentInfoModel.setBraspagMerchantId(currentBaseStore.getBraspagMerchantId());
                paymentInfoModel.setBraspagMerchantKey(currentBaseStore.getBraspagMerchantKey());
                paymentInfoModel.setBraspagReceivedDate(responseDTO.getPayment().getReceivedDate());

                modelService.save(paymentInfoModel);

                BigDecimal amountDecimal = new BigDecimal(amount);

                final PaymentTransactionModel paymentTransaction = new PaymentTransactionModel();
                final PaymentTransactionEntryModel entry = new PaymentTransactionEntryModel();
                entry.setType(PaymentTransactionType.CAPTURE);
                entry.setTransactionStatus(TransactionStatus.ACCEPTED.name());
                entry.setAmount(amountDecimal);
                entry.setCode(cartModel.getCode() + UUID.randomUUID());
                modelService.save(entry);
                paymentTransaction.setCode(cartModel.getCode() + UUID.randomUUID());
                paymentTransaction.setOrder(cartModel);
                paymentTransaction.setPaymentProvider("BrasPag");
                paymentTransaction.setInfo(paymentInfoModel);
                paymentTransaction.setPlannedAmount(amountDecimal);
                paymentTransaction.setEntries(Arrays.asList(entry));
                modelService.save(paymentTransaction);
                cartModel.setPaymentTransactions(Arrays.asList(paymentTransaction));
                cartModel.setPaymentStatus(PaymentStatus.PAID);
                modelService.save(cartModel);

                saveLogPayment(cartModel,
                        installments,
                        cardNumber,
                        cardHolder,
                        cardBrand,
                        expiryMonth,
                        expiryYear,
                        documentType,
                        documentNumber,
                        amount,null);

                return true;

            } else {
                paymentInfoModel.setBraspagPaymentStatus(BraspagPaymentStatus.NOT_AUTHORIZED);
                if (responseDTO.getPayment() != null) {
                    paymentInfoModel.setBraspagPaymentId(responseDTO.getPayment().getPaymentId());
                    paymentInfoModel.setBraspagStatus(responseDTO.getPayment().getStatus());
                    paymentInfoModel.setBraspagReasonMessage(responseDTO.getPayment().getReasonMessage());
                    paymentInfoModel.setBraspagReasonCode(responseDTO.getPayment().getReasonCode());
                    paymentInfoModel.setBraspagProviderReturnMessage(responseDTO.getPayment().getProviderReturnMessage());
                    paymentInfoModel.setBraspagProviderReturnCode(responseDTO.getPayment().getProviderReturnCode());
                    paymentInfoModel.setBraspagAuthCode(responseDTO.getPayment().getAuthorizationCode());
                    paymentInfoModel.setBraspagNsuCode(responseDTO.getPayment().getProofOfSale());
                    paymentInfoModel.setBraspagMerchantId(currentBaseStore.getBraspagMerchantId());
                    paymentInfoModel.setBraspagMerchantKey(currentBaseStore.getBraspagMerchantKey());
                    paymentInfoModel.setBraspagReceivedDate(responseDTO.getPayment().getReceivedDate());
                }
                modelService.save(paymentInfoModel);

                final PaymentTransactionModel paymentTransaction = new PaymentTransactionModel();
                final PaymentTransactionEntryModel entry = new PaymentTransactionEntryModel();
                BigDecimal amountDecimal = new BigDecimal(amount);

                entry.setType(PaymentTransactionType.CAPTURE);
                entry.setTransactionStatus(TransactionStatus.REJECTED.name());
                entry.setAmount(amountDecimal);
                entry.setCode(cartModel.getCode() + UUID.randomUUID());
                modelService.save(entry);
                paymentTransaction.setCode(cartModel.getCode() + UUID.randomUUID());
                paymentTransaction.setEntries(Arrays.asList(entry));
                paymentTransaction.setOrder(cartModel);
                paymentTransaction.setPaymentProvider("BrasPag");
                paymentTransaction.setInfo(paymentInfoModel);
                paymentTransaction.setPlannedAmount(amountDecimal);
                modelService.save(paymentTransaction);
                cartModel.setPaymentTransactions(Arrays.asList(paymentTransaction));

                cartModel.setPaymentStatus(PaymentStatus.NOTPAID);
                modelService.save(cartModel);

                saveLogPayment(cartModel,
                        installments,
                        cardNumber,
                        cardHolder,
                        cardBrand,
                        expiryMonth,
                        expiryYear,
                        documentType,
                        documentNumber,
                        amount,null);


                return false;
            }
        }
        catch (Exception ex){
            LOG.error("[Braspag] Error consuming API.", ex);
            if (ex instanceof BraspagTimeoutException)
                throw (BraspagTimeoutException) ex;
            return false;
        }
    }




    private AuthorizeRequestDTO getAuthorizeRequestDTO(AbstractOrderModel cartModel, CustomerData customerData,
                                                       Integer installments, String securityCode,
                                                       String cardNumber, String cardHolder, String cardBrand,
                                                       String expiryMonth, String expiryYear,
                                                       String documentNumber, String documentType, Double amount,
                                                       BaseStoreModel currentBaseStore) {

        AuthorizeRequestDTO authorizeRequestDTO = new AuthorizeRequestDTO();
        authorizeRequestDTO.setMerchantOrderId(cartModel.getCode() + "_" + (cartModel.getCreditCardGatewayCount() == null ? 0 : cartModel.getCreditCardGatewayCount()));

        CustomerInfoDTO customerInfoDTO = new CustomerInfoDTO();

        customerInfoDTO.setName(cardHolder);
        customerInfoDTO.setEmail(customerData.getUid());
        customerInfoDTO.setIdentity(documentNumber);
        customerInfoDTO.setIdentityType(documentType);

        //Builds customer address object
        AuthorizeRequestAddressInfoDTO addressDTO = new AuthorizeRequestAddressInfoDTO();

        AddressData addressData = null;
        if (customerData.getDefaultShippingAddress() != null){
            addressData = customerData.getDefaultShippingAddress();
        }

        if (addressData != null) {
            addressDTO.setCity(addressData.getTown());
            addressDTO.setCountry(addressData.getCountry().getIsocode());
            addressDTO.setDistrict(addressData.getDistrict());
            addressDTO.setNumber(addressData.getStreetNumber());
            addressDTO.setState(addressData.getRegion().getIsocodeShort());
            if(!StringUtils.isEmpty(addressData.getRemarks()) && addressData.getRemarks().length() >= 50) {
                addressDTO.setComplement(addressData.getRemarks().substring(0,50));
            } else {
                addressDTO.setComplement(addressData.getRemarks());
            }
            addressDTO.setZipCode(addressData.getPostalCode());
            addressDTO.setStreet(addressData.getLine1());

            customerInfoDTO.setAddress(addressDTO);


            //Builds delivery address object
            AuthorizeRequestDeliveryAddressInfoDTO deliveryAddressDTO = getAuthorizeRequestDeliveryAddressInfoDTO(addressData);
            customerInfoDTO.setDeliveryAddress(deliveryAddressDTO);
        }

        PaymentInfoDTO paymentInfoDTO = getPaymentInfoDTO(cartModel, installments, securityCode, cardNumber, cardHolder,
                cardBrand, expiryMonth, expiryYear, currentBaseStore, amount);

        authorizeRequestDTO.setCustomer(customerInfoDTO);
        authorizeRequestDTO.setPayment(paymentInfoDTO);
        return authorizeRequestDTO;
    }



    private AuthorizeRequestDeliveryAddressInfoDTO getAuthorizeRequestDeliveryAddressInfoDTO(AddressData addressData) {
        AuthorizeRequestDeliveryAddressInfoDTO deliveryAddressDTO = new AuthorizeRequestDeliveryAddressInfoDTO();

        deliveryAddressDTO.setCity(addressData.getTown());
        deliveryAddressDTO.setCountry(addressData.getCountry().getIsocode());
        deliveryAddressDTO.setDistrict(addressData.getDistrict());
        deliveryAddressDTO.setNumber(addressData.getStreetNumber());
        deliveryAddressDTO.setState(addressData.getRegion().getIsocodeShort());
        if(!StringUtils.isEmpty(addressData.getRemarks()) && addressData.getRemarks().length() >=50){
            deliveryAddressDTO.setComplement(addressData.getRemarks().substring(0,50));
        } else {
            deliveryAddressDTO.setComplement(addressData.getRemarks());
        }
        deliveryAddressDTO.setZipCode(addressData.getPostalCode());
        deliveryAddressDTO.setStreet(addressData.getLine1());
        return deliveryAddressDTO;
    }

    private PaymentInfoDTO getPaymentInfoDTO(AbstractOrderModel cartModel, Integer installments, String securityCode, String cardNumber, String cardHolder, String cardBrand, String expiryMonth, String expiryYear,
                                             BaseStoreModel currentBaseStore,
                                             Double amount) {
        PaymentInfoDTO paymentInfoDTO = new PaymentInfoDTO();
        paymentInfoDTO.setProvider(currentBaseStore.getBraspagProvider());
        paymentInfoDTO.setType("CreditCard");

        //Valor em centavos
        paymentInfoDTO.setAmount((int)(amount * 100));
        paymentInfoDTO.setCurrency(cartModel.getCurrency().getIsocode());
        paymentInfoDTO.setCountry("BRA");
        paymentInfoDTO.setInstallments(installments);
        paymentInfoDTO.setInterest("ByMerchant");
        paymentInfoDTO.setCapture(true);
        paymentInfoDTO.setAuthenticate(false);
        paymentInfoDTO.setRecurrent(false);
        paymentInfoDTO.setDoSplit(false);


        CreditCardInfoDTO creditCardInfoDTO = new CreditCardInfoDTO();
        creditCardInfoDTO.setAlias("");
        creditCardInfoDTO.setBrand(cardBrand);
        creditCardInfoDTO.setSecurityCode(securityCode);
        creditCardInfoDTO.setCardNumber(cardNumber);
        creditCardInfoDTO.setHolder(cardHolder);
        creditCardInfoDTO.setExpirationDate(expiryMonth + "/" + expiryYear);


        paymentInfoDTO.setCreditCard(creditCardInfoDTO);

        CredentialsInfoDTO credentialsInfoDTO = new CredentialsInfoDTO();
        credentialsInfoDTO.setCode("9999999");
        credentialsInfoDTO.setKey("D8888888");
        paymentInfoDTO.setCredentials(credentialsInfoDTO);

        return paymentInfoDTO;
    }

    @Override
    public boolean voidPayment(AbstractOrderModel cartModel) throws BraspagApiException {

        try {
            CreditCardPaymentInfoModel paymentInfoModel = (CreditCardPaymentInfoModel) cartModel.getPaymentInfo();
            if (paymentInfoModel == null) {
                LOG.error("[Braspag] Error defining PaymentInfoModel on voidPayment.");
                return false;
            }
            String merchantKey = paymentInfoModel.getBraspagMerchantKey();
            String merchantId = paymentInfoModel.getBraspagMerchantId();


            BraspagAuthorizationPojo braspagAuthorizationPojo = getBraspagAuthorizationPojo(merchantKey, merchantId);

            VoidResponseDTO responseDTO = braspagSalesService.voidPayment(paymentInfoModel.getBraspagPaymentId(), braspagAuthorizationPojo, cartModel);

            if (responseDTO != null){

                paymentInfoModel.setBraspagStatus(responseDTO.getStatus());
                paymentInfoModel.setBraspagReasonMessage(responseDTO.getReasonMessage());
                paymentInfoModel.setBraspagReasonCode(responseDTO.getReasonCode());
                paymentInfoModel.setBraspagProviderReturnMessage(responseDTO.getProviderReturnMessage());
                paymentInfoModel.setBraspagProviderReturnCode(responseDTO.getProviderReturnCode());
                paymentInfoModel.setBraspagPaymentStatus(BraspagPaymentStatus.VOID);


                saveLogPayment(cartModel,
                        paymentInfoModel.getBraspagInstallments(),
                        paymentInfoModel.getBraspagCardNumber(),
                        paymentInfoModel.getBraspagCardHolder(),
                        paymentInfoModel.getBraspagCardBrand(),
                        paymentInfoModel.getBraspagExpiryMonth(),
                        paymentInfoModel.getBraspagExpiryYear(),
                        null,
                        paymentInfoModel.getBraspagIssuerId(),
                        paymentInfoModel.getBraspagTotalValue()
                        ,null);

                modelService.save(paymentInfoModel);

                final PaymentTransactionModel paymentTransaction = new PaymentTransactionModel();
                final PaymentTransactionEntryModel entry = new PaymentTransactionEntryModel();
                entry.setType(PaymentTransactionType.CANCEL);
                entry.setTransactionStatus(TransactionStatus.ACCEPTED.name());
                entry.setCode(cartModel.getCode() + UUID.randomUUID());
                modelService.save(entry);
                paymentTransaction.setCode(cartModel.getCode() + UUID.randomUUID());
                paymentTransaction.setEntries(Arrays.asList(entry));
                modelService.save(paymentTransaction);
                cartModel.setPaymentTransactions(Arrays.asList(paymentTransaction));
                cartModel.setPaymentStatus(PaymentStatus.NOTPAID);

                modelService.save(cartModel);
                return responseDTO.getStatus() == BRASPAG_STATUS_VOID;
            }else{
                saveLogPayment(cartModel,
                        paymentInfoModel.getBraspagInstallments(),
                        paymentInfoModel.getBraspagCardNumber(),
                        paymentInfoModel.getBraspagCardHolder(),
                        paymentInfoModel.getBraspagCardBrand(),
                        paymentInfoModel.getBraspagExpiryMonth(),
                        paymentInfoModel.getBraspagExpiryYear(),
                        null,
                        paymentInfoModel.getBraspagIssuerId(),
                        paymentInfoModel.getBraspagTotalValue()
                        ,"Erro Cancelamento Captura sem resposta - 500");
                return false;
            }
        }
        catch (Exception ex){
            LOG.error("[Braspag] Error capturing Payment.", ex);
            return false;
        }
    }

    private BraspagAuthorizationPojo getBraspagAuthorizationPojo(final String merchantKey, final String merchantId) {
        BraspagAuthorizationPojo braspagAuthorizationPojo = new BraspagAuthorizationPojo();
        braspagAuthorizationPojo.setMerchantKey(merchantKey);
        braspagAuthorizationPojo.setMerchantId(merchantId);
        return braspagAuthorizationPojo;
    }

    public void saveLogPayment(final AbstractOrderModel orderModel,
                               final Integer installments,
                               final String cardNumber,
                               final String cardHolder,
                               final String cardBrand,
                               final String expiryMonth,
                               final String expiryYear,
                               final String documentType,
                               final String documentNumber,
                               final Double amount,
                               final String message){

        //PaymentInfoModel paymentInfoModel = orderModel.getPaymentInfo();
        CreditCardPaymentInfoModel paymentInfoModel = (CreditCardPaymentInfoModel) orderModel.getPaymentInfo();;

        try {

            OrderPaymentLogInfoModel paymentLogInfo = modelService.create(OrderPaymentLogInfoModel.class);
            paymentLogInfo.setOrderCode(orderModel.getCode());
            paymentLogInfo.setDateTime(timeService.getCurrentTime().getTime());
            paymentLogInfo.setIntegrationStatus(paymentInfoModel.getBraspagReasonCode() != null ? "OK" : "ERROR");
            paymentLogInfo.setInstallments(installments != null ? installments.toString() : null);
            paymentLogInfo.setCardNumber(maskCardNumber(cardNumber));
            paymentLogInfo.setCardHolder(cardHolder);
            paymentLogInfo.setCardBrand(cardBrand);
            paymentLogInfo.setExpiryMonth(expiryMonth);
            paymentLogInfo.setExpiryYear(expiryYear);
            paymentLogInfo.setDocumentType(documentType);
            paymentLogInfo.setDocumentNumber(documentNumber);
            paymentLogInfo.setAmount(amount != null ? amount.toString() : null);
            //paymentLogInfo.setSystemName(step);

            if(message != null){
                paymentLogInfo.setIntegrationStatus("ERROR");
                paymentLogInfo.setMessage(message);
                modelService.save(paymentLogInfo);
                return;
            }


            String values;

            values = "ReasonCode : " + (paymentInfoModel.getBraspagReasonCode() != null ? paymentInfoModel.getBraspagReasonCode() : "") + ";" +
                    "ReasonMessage : " + (paymentInfoModel.getBraspagReasonMessage() != null ? paymentInfoModel.getBraspagReasonMessage() : "") + ";" +
                    "Status: " + (paymentInfoModel.getBraspagStatus() != null ? paymentInfoModel.getBraspagStatus() : "") + ";" +
                    "ProviderReturnCode : " + (paymentInfoModel.getBraspagProviderReturnCode() != null ? paymentInfoModel.getBraspagProviderReturnCode() : "") + ";" +
                    "ProviderReturnMessage : " + (paymentInfoModel.getBraspagProviderReturnMessage() != null ? paymentInfoModel.getBraspagProviderReturnMessage() : "") + ";";

            paymentLogInfo.setMessage(values);

            modelService.save(paymentLogInfo);

        } catch (Exception ex) {
            LOG.error("Failed to save data", ex);
        }

    }

    public void handlePaymentForm(BrasPagPaymentMethodData paymentMethodData)
    {
        AbstractOrderModel cartModel;
        cartModel = cartService.getSessionCart();
        final B2BCustomerModel currentUser = (B2BCustomerModel) userService.getCurrentUser();

        //Create payment info
        CreditCardPaymentInfoModel paymentInfo = createPaymentInfo(cartModel, paymentMethodData);
        cartModel.setPaymentInfo(paymentInfo);
        cartModel.setPaymentType(CheckoutPaymentType.CARD);

        //Clona o endere�o de entrega para o de cobran�a.
        AddressModel paymentAddress = new AddressModel();
        paymentAddress.setVisibleInAddressBook(false);
        paymentAddress.setRegion(currentUser.getDefaultShipmentAddress().getRegion());
        paymentAddress.setDistrict(currentUser.getDefaultShipmentAddress().getDistrict());
        paymentAddress.setStreetname(currentUser.getDefaultShipmentAddress().getStreetname());
        paymentAddress.setStreetnumber(currentUser.getDefaultShipmentAddress().getStreetnumber());
        paymentAddress.setTown(currentUser.getDefaultShipmentAddress().getTown());
        paymentAddress.setPostalcode(currentUser.getDefaultShipmentAddress().getPostalcode());
        paymentAddress.setCountry(currentUser.getDefaultShipmentAddress().getCountry());
        paymentAddress.setLine1(currentUser.getDefaultShipmentAddress().getLine1());
        paymentAddress.setLine2(currentUser.getDefaultShipmentAddress().getLine2());
        paymentAddress.setRemarks(currentUser.getDefaultShipmentAddress().getRemarks());
        paymentAddress.setOwner(currentUser.getDefaultShipmentAddress().getOwner());
        cartModel.setPaymentAddress(paymentAddress);

        modelService.save(cartModel);

    }

    public CreditCardPaymentInfoModel createPaymentInfo(final AbstractOrderModel cartModel, BrasPagPaymentMethodData paymentMethodData) {
        final CreditCardPaymentInfoModel paymentInfo = modelService.create(CreditCardPaymentInfoModel.class);
        paymentInfo.setUser(cartModel.getUser());
        paymentInfo.setSaved(false);
        paymentInfo.setCode(generateCcPaymentInfoCode(cartModel));

        AddressModel billingAddress = new AddressModel();

        billingAddress.setVisibleInAddressBook(false);
        billingAddress.setRegion(cartModel.getDeliveryAddress().getRegion());
        billingAddress.setDistrict(cartModel.getDeliveryAddress().getDistrict());
        billingAddress.setStreetname(cartModel.getDeliveryAddress().getStreetname());
        billingAddress.setStreetnumber(cartModel.getDeliveryAddress().getStreetnumber());
        billingAddress.setTown(cartModel.getDeliveryAddress().getTown());
        billingAddress.setPostalcode(cartModel.getDeliveryAddress().getPostalcode());
        billingAddress.setCountry(cartModel.getDeliveryAddress().getCountry());
        billingAddress.setLine1(cartModel.getDeliveryAddress().getLine1());
        billingAddress.setLine2(cartModel.getDeliveryAddress().getLine2());
        billingAddress.setRemarks(cartModel.getDeliveryAddress().getRemarks());
        billingAddress.setOwner(cartModel.getDeliveryAddress().getOwner());
        // Clone DeliveryAdress to BillingAddress
        paymentInfo.setBillingAddress(billingAddress);

        //installments
        //paymentInfo.setBraspagInstallments(paymentMethodData.getInstallments());

        paymentInfo.setCcOwner(paymentMethodData.getCardHolder());
        paymentInfo.setNumber(maskCardNumber(paymentMethodData.getCardNumber()));
        paymentInfo.setType(CreditCardType.VISA);
        paymentInfo.setValidToMonth(paymentMethodData.getExpiryMonth());
        paymentInfo.setValidToYear(paymentMethodData.getExpiryYear());

        modelService.save(paymentInfo);

        return paymentInfo;
    }

    protected String generateCcPaymentInfoCode(final AbstractOrderModel cartModel) {
        return cartModel.getCode() + "_" + UUID.randomUUID();
    }

    private static String maskCardNumber(String value)
    {
        if (value == null)
        {
            return new String();
        }
        return StringUtils.overlay(value, StringUtils.repeat("*", 6), 6, value.length()-4);
    }

}