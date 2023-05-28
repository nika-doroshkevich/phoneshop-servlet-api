<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="recentlyViewedProducts" required="true" type="java.util.ArrayList" %>

<p>
    Recently viewed
</p>

<c:choose>
    <c:when test="${fn:length(recentlyViewedProducts) > 0}">
        <ul style="text-align: left; padding-inline-start: 0px;">
            <c:forEach var="product" items="${recentlyViewedProducts}">
                <li style="display: inline-block;">
                    <table>
                        <tr>
                            <td style="text-align: center;">
                                <img class="product-tile" src="${product.imageUrl}">
                                <div>
                                    <a href="${pageContext.servletContext.contextPath}/products/${product.id}">
                                            ${product.description}
                                    </a>
                                </div>
                                <div>
                                    <fmt:formatNumber value="${product.price}" type="currency"
                                                      currencySymbol="${product.currency.symbol}"/>
                                </div>
                            </td>
                        </tr>
                    </table>
                </li>
            </c:forEach>
        </ul>
    </c:when>
</c:choose>