<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<c:set var="searchUrl" value="/my-account/orders?sort=${ycommerce:encodeUrl(searchPageData.pagination.sort)}"/>

 <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
    <script type="text/javascript">
      google.charts.load('current', {'packages':['corechart', 'bar']});
      google.charts.setOnLoadCallback(drawChart);
      google.charts.setOnLoadCallback(drawStuff);

      function drawChart() {

        var data = google.visualization.arrayToDataTable([
          ['Status', 'Pruchase Status'],
          ['In Process',     3],
          ['Completed',    1]
        ]);

        var options = {
          width: 500,
          height: 400,
          is3D: true,
          title: 'Status'
        };

        var chart = new google.visualization.PieChart(document.getElementById('piechart'));

        chart.draw(data, options);
      }

      function drawStuff() {
        var data = new google.visualization.arrayToDataTable([
          ['Month', 'Value'],
          ["January", 144.55],
          ["February ", 1531.60],
          ["March", 112.50],
          ["April", 6350.00],
          ["May", 8350.00],
          ["June", 5250.00],
          ["July", 9350.00],
          ["August", 0.00],
          ["September", 0.00],
          ["October", 0.00],
          ["November", 0.00],
          ['December', 0.00]
        ]);

        var options = {
          width: 500,
          height: 400,
          legend: { position: 'none' },
          chart: {
            title: 'Purchase Value',
            subtitle: 'Purchase totals by month' },
          axes: {
            x: {
              0: { side: 'top', label: '2022'} // Top x-axis.
            }
          },
          bar: { groupWidth: "90%" }
        };

        var chart = new google.charts.Bar(document.getElementById('top_x_div'));
        // Convert the Classic options to Material options.
        chart.draw(data, google.charts.Bar.convertOptions(options));
      };

    </script>


<div class="account-section-header">
	<spring:theme code="text.account.orderHistory" />
</div>

  <table class="columns">
      <tr>
        <td><div id="piechart"></div></td>
        <td><div id="top_x_div"></div></td>
      </tr>
    </table>


<c:if test="${empty searchPageData.results}">
	<div class="account-section-content content-empty">
		<ycommerce:testId code="orderHistory_noOrders_label">
			<spring:theme code="text.account.orderHistory.noOrders" />
		</ycommerce:testId>
	</div>
</c:if>
<c:if test="${not empty searchPageData.results}">
	<div class="account-section-content	">
		<div class="account-orderhistory">
			<div class="account-orderhistory-pagination">
				<nav:pagination top="true" msgKey="text.account.orderHistory.page" showCurrentPageInfo="true" hideRefineButton="true" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchUrl}"  numberPagesShown="${numberPagesShown}"/>
			</div>
            <div class="account-overview-table">
				<table class="orderhistory-list-table responsive-table">
					<tr class="account-orderhistory-table-head responsive-table-head hidden-xs">
						<th><spring:theme code="text.account.orderHistory.orderNumber" /></th>
						<th><spring:theme code="text.account.orderHistory.orderStatus"/></th>
						<th><spring:theme code="text.account.orderHistory.datePlaced"/></th>
						<th><spring:theme code="text.account.orderHistory.total"/></th>
					</tr>
					<c:forEach items="${searchPageData.results}" var="order">
						<tr class="responsive-table-item">
							<ycommerce:testId code="orderHistoryItem_orderDetails_link">
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.orderHistory.orderNumber" /></td>
								<td class="responsive-table-cell">
									<spring:url value="/my-account/order/{/orderCode}" var="orderDetailsUrl" htmlEscape="false">
										<spring:param name="orderCode" value="${order.code}"/>
									</spring:url>
									<a href="${fn:escapeXml(orderDetailsUrl)}" class="responsive-table-link">
										${fn:escapeXml(order.code)}
									</a>
								</td>
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.orderHistory.orderStatus"/></td>																
								<td class="status">
									<spring:theme code="text.account.order.status.display.${order.statusDisplay}"/>
								</td>
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.orderHistory.datePlaced"/></td>
								<td class="responsive-table-cell">
									<fmt:formatDate value="${order.placed}" dateStyle="medium" timeStyle="short" type="both"/>
								</td>
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.orderHistory.total"/></td>
								<td class="responsive-table-cell responsive-table-cell-bold">
									${fn:escapeXml(order.total.formattedValue)}
								</td>
							</ycommerce:testId>
						</tr>
					</c:forEach>
				</table>
            </div>
		</div>
		<div class="account-orderhistory-pagination">
			<nav:pagination top="false" msgKey="text.account.orderHistory.page" showCurrentPageInfo="true" hideRefineButton="true" supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchUrl}"  numberPagesShown="${numberPagesShown}"/>
		</div>
	</div>
</c:if>