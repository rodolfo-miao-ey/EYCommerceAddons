package br.com.ey.eyb2bstorefront.controllers.pages.checkout.steps;

import br.com.braspag.exceptions.BraspagTimeoutException;
import br.com.braspag.facades.BraspagFacade;
import br.com.braspag.facades.order.data.BrasPagPaymentMethodData;
import br.com.braspag.facades.order.data.BraspagPaymentModeData;
import br.com.braspag.facades.payment.data.CreditCardBrandData;
import br.com.braspag.model.BraspagPaymentModeModel;
import br.com.ey.core.services.order.EyCheckoutService;
import br.com.ey.eyb2bstorefront.controllers.ControllerConstants;
import br.com.ey.eyb2bstorefront.form.BraspagPaymentForm;
import br.com.ey.eyb2bstorefront.form.validation.BraspagPaymentFormValidator;

import br.com.ey.facades.order.EyCheckoutFacade;
import br.com.ey.facades.payment.EyCreditCardBrandFacade;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateQuoteCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.checkout.steps.AbstractCheckoutStepController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.util.AddressDataUtil;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.enums.CountryType;

import java.util.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping(value = "/checkout/multi/payment-method")
public class BraspagPaymentMethodCheckoutStepController extends AbstractCheckoutStepController
{
    protected static final Map<String, String> CYBERSOURCE_SOP_CARD_TYPES = new HashMap<>();
    private static final String PAYMENT_METHOD = "payment-method";
    private static final String CART_DATA_ATTR = "cartData";

    private static final Logger LOGGER = Logger.getLogger(PaymentMethodCheckoutStepController.class);

    @Resource(name = "addressDataUtil")
    private AddressDataUtil addressDataUtil;

    @Resource(name = "braspagPaymentFormValidator")
    private BraspagPaymentFormValidator BraspagPaymentFormValidator;

    @Resource
    private CartService cartService;

    @Resource
    private BraspagFacade braspagFacade;

    @Resource
    private CustomerFacade customerFacade;

    @Resource
    private EyCheckoutFacade eyCheckoutFacade;

    @Resource
    private EyCheckoutService eyCheckoutService;

    @Resource
    private EyCreditCardBrandFacade eyCreditCardBrandFacade;

    @ModelAttribute("billingCountries")
    public Collection<CountryData> getBillingCountries()
    {
        return getCheckoutFacade().getCountries(CountryType.BILLING);
    }

    @ModelAttribute("cardTypes")
    public Collection<CardTypeData> getCardTypes()
    {
        return getCheckoutFacade().getSupportedCardTypes();
    }

    @ModelAttribute("months")
    public List<SelectOption> getMonths()
    {
        final List<SelectOption> months = new ArrayList<SelectOption>();

        months.add(new SelectOption("1", "01"));
        months.add(new SelectOption("2", "02"));
        months.add(new SelectOption("3", "03"));
        months.add(new SelectOption("4", "04"));
        months.add(new SelectOption("5", "05"));
        months.add(new SelectOption("6", "06"));
        months.add(new SelectOption("7", "07"));
        months.add(new SelectOption("8", "08"));
        months.add(new SelectOption("9", "09"));
        months.add(new SelectOption("10", "10"));
        months.add(new SelectOption("11", "11"));
        months.add(new SelectOption("12", "12"));

        return months;
    }

    @ModelAttribute("expiryYears")
    public List<SelectOption> getExpiryYears()
    {
        final List<SelectOption> expiryYears = new ArrayList<SelectOption>();
        final Calendar calender = new GregorianCalendar();

        for (int i = calender.get(Calendar.YEAR); i < calender.get(Calendar.YEAR) + 11; i++)
        {
            expiryYears.add(new SelectOption(String.valueOf(i), String.valueOf(i)));
        }

        return expiryYears;
    }

    @ModelAttribute("paymentCardInstallments")
    public Collection<BraspagPaymentModeData> getPaymentCardInstallments()
    {
        return eyCheckoutFacade.getInstallments();
    }

    @ModelAttribute("sopCardTypes") // Rodolfo Fazer
    public Collection<CardTypeData> getCardListTypes()
    {
        return getSopCardTypes();
    }

    @Override
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    @RequireHardLogIn
    @PreValidateQuoteCheckoutStep
    @PreValidateCheckoutStep(checkoutStep = PAYMENT_METHOD)
    public String enterStep(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
    {
        final CartData cartData = getCheckoutFacade().getCheckoutCart();
        model.addAttribute(CART_DATA_ATTR, cartData);

        setupAddPaymentPage(model);
        setCheckoutStepLinksForModel(model, getCheckoutStep());

        final BraspagPaymentForm braspagPaymentForm = new BraspagPaymentForm();
        model.addAttribute("braspagPaymentForm", braspagPaymentForm);

        return ControllerConstants.Views.Pages.MultiStepCheckout.AddPaymentMethodPage;
    }

    @RequestMapping(value = "/credit" , method = RequestMethod.POST)
    @RequireHardLogIn
    public String add(final Model model, @Valid final BraspagPaymentForm paymentDetailsForm,
                      final BindingResult bindingResult, final RedirectAttributes redirectModel,
                      final HttpServletRequest request)
            throws CMSItemNotFoundException
    {

        BraspagPaymentFormValidator.validate(paymentDetailsForm, bindingResult);
        setupAddPaymentPage(model);
        final CartData cartData = getCheckoutFacade().getCheckoutCart();
        model.addAttribute(CART_DATA_ATTR, cartData);

        if (bindingResult.hasErrors())
        {
            GlobalMessages.addErrorMessage(model, "checkout.error.paymentethod.formentry.invalid");
            return ControllerConstants.Views.Pages.MultiStepCheckout.AddPaymentMethodPage;
        }

        Boolean paymentAuthorized;
        final BrasPagPaymentMethodData paymentInfoData = new BrasPagPaymentMethodData();
        try{

            fillInPaymentData(paymentDetailsForm, paymentInfoData);
            braspagFacade.handlePaymentForm(paymentInfoData, request.getRemoteAddr());
            paymentAuthorized = authorizePayment(paymentInfoData);
        } catch (final BraspagTimeoutException ignored)
        {
            GlobalMessages.addErrorMessage(model, "checkout.error.payment.timeout");
            return ControllerConstants.Views.Pages.MultiStepCheckout.AddPaymentMethodPage;
        }

        if (!paymentAuthorized) {
            GlobalMessages.addErrorMessage(model, "checkout.error.payment.unauthorized");
            return ControllerConstants.Views.Pages.MultiStepCheckout.AddPaymentMethodPage;
        }

        //whiteMartinsCheckoutFacade.updatePaymentMode(paymentInfoData, paymentDetailsForm.getPaymentInstallmentCard());

        //return placeOrder(model, redirectModel);

        return getCheckoutStep().nextStep();
    }

    @RequestMapping(value = "/payment-type", method = RequestMethod.GET)
    @RequireHardLogIn
    public String paymentType(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
    {
        return REDIRECT_URL_ADD_PAYMENT_METHOD;
    }

   /* protected String placeOrder(final Model model, final RedirectAttributes redirectModel){
        CartModel cartModel = cartService.getSessionCart();

        final PlaceOrderData placeOrderData = new PlaceOrderData();
        placeOrderData.setReplenishmentOrder(false);
        placeOrderData.setTermsCheck(true);

        OrderData orderData = null;
        try
        {
            orderData = eyCheckoutFacade.placeOrder(placeOrderData);

        }
        catch (final EntityValidationException e)
        {

            GlobalMessages.addErrorMessage(model, e.getLocalizedMessage());

                try {
                    braspagFacade.voidPayment(cartModel);
                } catch (BraspagApiException ex) {
                    LOGGER.error("Failed to place Order", e);
                }

            try {
                return enterStep(model, redirectModel);
            } catch (CMSItemNotFoundException ex) {
                LOGGER.error("Failed to place Order", ex);
            }
            //return ControllerConstants.Views.Pages.MultiStepCheckout.AddPaymentMethodPage;
        }
        catch (final Exception e)
        {

                try {
                    braspagFacade.voidPayment(cartModel);
                } catch (BraspagApiException ex) {
                    LOGGER.error("Failed to place Order", e);
                }

            GlobalMessages.addErrorMessage(model, "checkout.placeOrder.failed");
            //return ControllerConstants.Views.Pages.MultiStepCheckout.AddPaymentMethodPage;
            try {
                return enterStep(model, redirectModel);
            } catch (CMSItemNotFoundException ex) {
                LOGGER.error("Failed to place Order", ex);
            }
        }

        return redirectToOrderConfirmationPage(orderData);
    } */

    protected void fillInPaymentData(@Valid final BraspagPaymentForm paymentDetailsForm, final BrasPagPaymentMethodData paymentInfoData)
    {

        CartModel cartModel = cartService.getSessionCart();

        paymentInfoData.setCardHolder(paymentDetailsForm.getNameOnCard());
        paymentInfoData.setCardNumber(paymentDetailsForm.getCardNumber());
        paymentInfoData.setExpiryMonth(paymentDetailsForm.getExpiryMonth());
        paymentInfoData.setExpiryYear(paymentDetailsForm.getExpiryYear());

        final BraspagPaymentModeModel braspagPaymentModeModel =
                eyCheckoutService.findPaymentInstallmentByCode(paymentDetailsForm.getPaymentInstallmentCard());

        paymentInfoData.setInstallments(braspagPaymentModeModel.getInstallment());
        paymentInfoData.setSecurityCode(paymentDetailsForm.getSecurityNumber());
        paymentInfoData.setAmount(cartModel.getTotalPrice());
        paymentInfoData.setDocumentType(paymentDetailsForm.getDocumentType());
        paymentInfoData.setDocumentNumber(paymentDetailsForm.getDocumentNumber());
        paymentInfoData.setCardBrand(paymentDetailsForm.getCard_cardType());


    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    @RequireHardLogIn
    public String remove(@RequestParam(value = "paymentInfoId") final String paymentMethodId,
                         final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
    {
        //noinspection removal
        getUserFacade().unlinkCCPaymentInfo(paymentMethodId);
        GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
                "text.account.profile.paymentCart.removed");
        return getCheckoutStep().currentStep();
    }

    /**
     * This method gets called when the "Use These Payment Details" button is clicked. It sets the selected payment
     * method on the checkout facade and reloads the page highlighting the selected payment method.
     *
     * @param selectedPaymentMethodId
     *           - the id of the payment method to use.
     * @return - a URL to the page to load.
     */
    @RequestMapping(value = "/choose", method = RequestMethod.GET)
    @RequireHardLogIn
    public String doSelectPaymentMethod(@RequestParam("selectedPaymentMethodId") final String selectedPaymentMethodId)
    {
        if (StringUtils.isNotBlank(selectedPaymentMethodId))
        {
            getCheckoutFacade().setPaymentDetails(selectedPaymentMethodId);
        }
        return getCheckoutStep().nextStep();
    }

    @RequestMapping(value = "/back", method = RequestMethod.GET)
    @RequireHardLogIn
    @Override
    public String back(final RedirectAttributes redirectAttributes)
    {
        return getCheckoutStep().previousStep();
    }

    @RequestMapping(value = "/next", method = RequestMethod.GET)
    @RequireHardLogIn
    @Override
    public String next(final RedirectAttributes redirectAttributes)
    {
        return getCheckoutStep().nextStep();
    }

    private boolean authorizePayment(final BrasPagPaymentMethodData paymentInfoData) throws BraspagTimeoutException
    {
        CustomerData currentCustomerData;
        currentCustomerData = customerFacade.getCurrentCustomer();

        CartModel cartModel = cartService.getSessionCart();

        try {
            boolean cardAuthorized = false;

            cardAuthorized = braspagFacade.authorizePayment(cartModel, currentCustomerData,
                    paymentInfoData.getInstallments(),
                    paymentInfoData.getSecurityCode(),
                    paymentInfoData.getCardNumber(),
                    paymentInfoData.getCardHolder(),
                    paymentInfoData.getCardBrand(),
                    paymentInfoData.getExpiryMonth(),
                    paymentInfoData.getExpiryYear(),
                    paymentInfoData.getDocumentType(),
                    paymentInfoData.getDocumentNumber(),
                    paymentInfoData.getAmount());

            return cardAuthorized;
        } catch (Exception ex) {
            LOGGER.error("[Payment] Error authorizing payment for cart: " + cartModel.getCode(), ex);
            if (ex instanceof BraspagTimeoutException)
                throw (BraspagTimeoutException) ex;
        }

        return false;
    }

    protected CardTypeData createCardTypeData(final String code, final String name)
    {
        final CardTypeData cardTypeData = new CardTypeData();
        cardTypeData.setCode(code);
        cardTypeData.setName(name);
        return cardTypeData;
    }

    protected void setupAddPaymentPage(final Model model) throws CMSItemNotFoundException
    {
        model.addAttribute("metaRobots", "noindex,nofollow");
        model.addAttribute("hasNoPaymentInfo", Boolean.valueOf(getCheckoutFlowFacade().hasNoPaymentInfo()));
        prepareDataForPage(model);
        model.addAttribute(WebConstants.BREADCRUMBS_KEY,
                getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.paymentMethod.breadcrumb"));
        final ContentPageModel contentPage = getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL);
        storeCmsPageInModel(model, contentPage);
        setUpMetaDataForContentPage(model, contentPage);
        setCheckoutStepLinksForModel(model, getCheckoutStep());
    }

    protected Collection<CardTypeData> getSopCardTypes()
    {
        final Collection<CardTypeData> sopCardTypes = new ArrayList<CardTypeData>();

        final Set<CreditCardBrandData> creditCardBrandDataSet =
                eyCreditCardBrandFacade.getAllSupportedCreditCardBrands();
        for(final CreditCardBrandData creditCardBrandData : creditCardBrandDataSet)
        {
            sopCardTypes.add(
                    createCardTypeData(creditCardBrandData.getCode(), creditCardBrandData.getName()));
        }

        return sopCardTypes;
    }

    protected CheckoutStep getCheckoutStep()
    {
        return getCheckoutStep(PAYMENT_METHOD);
    }

    static
    {
        // Map hybris card type to Cybersource SOP credit card
        CYBERSOURCE_SOP_CARD_TYPES.put("visa", "001");
        CYBERSOURCE_SOP_CARD_TYPES.put("master", "002");
        CYBERSOURCE_SOP_CARD_TYPES.put("amex", "003");
        CYBERSOURCE_SOP_CARD_TYPES.put("diners", "005");
        CYBERSOURCE_SOP_CARD_TYPES.put("maestro", "024");
    }

}