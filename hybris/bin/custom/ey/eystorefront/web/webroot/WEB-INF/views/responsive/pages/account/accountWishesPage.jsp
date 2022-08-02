<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>

<spring:htmlEscape defaultHtmlEscape="true"/>
<div class="breadcrumb-section">
<ol class="breadcrumb">
    <li>
        <a href="/eystorefront/electronics/en/"><spring:theme code="text.account.homeAc"/></a>
    </li>

    <li class="active"><spring:theme code="text.account.wishes"/></li>
</ol>
</div>

<div class="account-section-header">
	<spring:theme code="text.account.wishes" text="My Wishes" htmlEscape="true"/>
</div>


<c:if test="${empty wishList}">
    <div class="account-section-content content-empty">
        <spring:theme code="text.account.wishList.noSaved"/>
    </div>
</c:if>

  <c:if test="${not empty wishList}">
        <c:forEach items="${wishList}" var="wishList">
        <c:url value="${wishList.url}" var="productUrl"/>
        <ul class="item__list">

        <li class="item__list--item">
        <div class="item__image">
           <a href="${fn:escapeXml(productUrl)}">
             <product:productPrimaryImage product="${wishList.product}" format="thumbnail"/>
           </a>
        </div>

        <div class="item__info">
            <a href="${fn:escapeXml(productUrl)}">
                <span class="item__name">${fn:escapeXml(wishList.product.name)}</span>
            </a>
        </div>

        <div class="item__quantity">
            <c:if test="${not wishList.product.multidimensional }">
                <c:url value="/cart/add" var="addToCartUrl"/>
                <spring:url value="${wishList.product.url}/configuratorPage/{/configuratorType}" var="configureProductUrl" htmlEscape="false">
                    <spring:param name="configuratorType" value="${configuratorType}" />
                </spring:url>

                <form:form id="addToCartForm${fn:escapeXml(wishList.product.code)}" action="${addToCartUrl}" method="post" class="add_to_cart_form addtocart">

                    <ycommerce:testId code="addToCartButton">
                        <input type="hidden" name="productCodePost" value="${fn:escapeXml(wishList.product.code)}"/>
                        <input type="hidden" name="productNamePost" value="${fn:escapeXml(wishList.product.name)}"/>
                        <input type="hidden" name="productPostPrice" value="${fn:escapeXml(wishList.product.price.value)}"/>

                        <c:choose>
                            <c:when test="${wishList.product.stock.stockLevelStatus.code eq 'outOfStock' }">
                                <a href="${wishList.product.url}">
                                    <button type="button" class="btn btn-primary btn-block js-enable-btn saiba-mais">
                                        <span class="lbl-button-buy"> Details </span>
                                   </button>
                                </a>
                            </c:when>

                            <c:when test="${wishList.product.price.value == null}">
                                <a href="${wishList.product.url}">
                                    <button type="button" class="btn btn-primary btn-block js-enable-btn saiba-mais">
                                        <span class="lbl-button-buy"> Details </span>
                                    </button>
                                </a>
                            </c:when>

                            <c:otherwise>
                                <button type="submit" class="btn btn-primary btn-block js-enable-btn confirm-cart-features" disabled="disabled">
                                  <span class="lbl-button-buy"> Add to Cart </span>
                                </button>
                            </c:otherwise>
                        </c:choose>
                    </ycommerce:testId>
                </form:form>
            </c:if>
       </div>

        <div class="item__quantity carousel__order">
          <a href="/eystorefront/electronics/en/my-account/wishes/delete/?product=${fn:escapeXml(wishList.product.code)}">
              <span class="item-value"><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24"><path d="M3 6v18h18v-18h-18zm5 14c0 .552-.448 1-1 1s-1-.448-1-1v-10c0-.552.448-1 1-1s1 .448 1 1v10zm5 0c0 .552-.448 1-1 1s-1-.448-1-1v-10c0-.552.448-1 1-1s1 .448 1 1v10zm5 0c0 .552-.448 1-1 1s-1-.448-1-1v-10c0-.552.448-1 1-1s1 .448 1 1v10zm4-18v2h-20v-2h5.711c.9 0 1.631-1.099 1.631-2h5.315c0 .901.73 2 1.631 2h5.712z"/></svg>
                  <p class="text-icon">Delete</p></span>
          </a>
        </div>

        </li>
      </ul>
  </c:forEach>
</c:if>

