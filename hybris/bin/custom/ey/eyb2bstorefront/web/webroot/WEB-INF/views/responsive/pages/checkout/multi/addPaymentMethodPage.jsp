<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="multiCheckout" tagdir="/WEB-INF/tags/responsive/checkout/multi"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/responsive/address" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">

    <div class="row">
        <div class="col-sm-6">
            <div class="checkout-headline">
                <span class="glyphicon glyphicon-lock"></span>
                <spring:theme code="checkout.multi.secure.checkout"/>
            </div>
            <multiCheckout:checkoutSteps checkoutSteps="${checkoutSteps}" progressBarId="${progressBarId}">
                <jsp:body>
                    <ycommerce:testId code="checkoutStepThree">
                        <div class="checkout-paymentmethod">
                            <div class="checkout-indent">

                                <div class="headline"><spring:theme code="checkout.multi.paymentMethod"/></div>

                           <form:form id="braspagPaymentForm" name="braspagPaymentForm" modelAttribute="braspagPaymentForm"
                               action="${request.contextPath}/checkout/multi/payment-method/credit" method="POST">

                                    <formElement:formInputBox idKey="card_accountNumber" labelKey="payment.cardNumber"
                                    path="cardNumber" inputCSS="form-control" mandatory="true" tabindex="1" placeholder="XXXX XXXX XXXX XXXX"
                                    autocomplete="off" maxlength="2"/>
                                    <formElement:formSelectBox idKey="card_cardType" selectCSSClass="form-control" labelKey="payment.cardType"
                                    path="card_cardType" mandatory="true" skipBlank="false" skipBlankMessageKey="payment.cardType.pleaseSelect"
                                    items="${sopCardTypes}" tabindex="2"/>
                                    <formElement:formInputBox idKey="card_nameOnCard" labelKey="payment.nameOnCard" path="nameOnCard"
                                    inputCSS="form-control" tabindex="3" mandatory="true"/>

                                        <fieldset id="document">

                                                    <div class="row">
                                                        <div class="col-xs-6">
                                                    <formElement:formRadioBoxLeft idKey="card_documentType_cpf"
                                                                                  labelKey="payment.documentType.cpf"
                                                                                  value="CPF" path="documentType" checked="true"/>
                                                </div>
                                                <div class="col-xs-6">
                                                    <formElement:formRadioBoxLeft idKey="card_documentType_cnpj"
                                                                                  labelKey="payment.documentType.cnpj"
                                                                                  value="CNPJ"
                                                                                  path="documentType"/>
                                                </div>

                                                 <div class="col-xs-12">
                                                    <formElement:formInputBox idKey="documentNumber" labelKey="payment.documentNumber.cpf"
                                                    path="documentNumber" inputCSS="form-control" tabindex="5" mandatory="true" maxlength="14"/>
                                                 </div>
                                        </fieldset>


                                <fieldset id="cardDate">
                                    <label for="" class="control-label"><spring:theme code="payment.expiryDate"/></label>
                                    <div class="row">
                                        <div class="col-xs-6">
                                            <formElement:formSelectBox idKey="ExpiryMonth" selectCSSClass="form-control" labelKey="payment.month"
                                            path="expiryMonth" mandatory="true" skipBlank="false" skipBlankMessageKey="payment.month" items="${months}" tabindex="6"/>
                                        </div>
                                        <div class="col-xs-6">
                                            <formElement:formSelectBox idKey="ExpiryYear" selectCSSClass="form-control" labelKey="payment.year"
                                            path="expiryYear" mandatory="true" skipBlank="false" skipBlankMessageKey="payment.year" items="${expiryYears}" tabindex="7"/>
                                        </div>
                                    </div>
                                </fieldset>

                                <div class="row">
                                    <div class="form-group col-xs-12">
                                        <formElement:formInputBox idKey="securityNumber" labelKey="payment.securityNumber"
                                        path="securityNumber" maxlength="4" inputCSS="form-control" mandatory="true" tabindex="8"/>
                                    </div>
                                </div>

                                <div class="row">
                                    <div class="form-group col-xs-12">
                                        <formElement:formSelectBox idKey="paymentInstallmentSelectCard" path="paymentInstallmentCard" skipBlank="false"
                                            items="${paymentCardInstallments}" mandatory="true"
                                            selectCSSClass="form-control js-payment-installment-select"
                                            itemValue="code" itemLabel="name" labelKey="checkout.multi.paymentInstallment.label"
                                            skipBlankMessageKey="checkout.multi.paymentInstallment.title.pleaseSelect" tabindex="9"/>
                                    </div>
                                </div>

                                <button type="submit" class="btn btn-primary btn-block submit_silentOrderPostForm checkout-next"><spring:theme code="checkout.multi.paymentMethod.continue"/></button>

                            </form:form>
                        </div>
                    </div>

                    </ycommerce:testId>
               </jsp:body>

            </multiCheckout:checkoutSteps>
		</div>

        <div class="col-sm-6 hidden-xs">
            <multiCheckout:checkoutOrderDetails cartData="${cartData}" showDeliveryAddress="true" showPaymentInfo="false" showTaxEstimate="false" showTax="true" />
        </div>

		<div class="col-sm-12 col-lg-12">
			<cms:pageSlot position="SideContent" var="feature" element="div" class="checkout-help">
				<cms:component component="${feature}"/>
			</cms:pageSlot>
		</div>
	</div>

</template:page>
