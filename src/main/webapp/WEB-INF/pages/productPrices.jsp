<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="productPricesDto" type="com.es.phoneshop.model.product.price.ProductPricesDto" scope="request"/>
<tags:master pageTitle="Product Details">
    <p>
        <span>Price history</span>
    </p>
    <p>
            ${productPricesDto.productName}
    </p>
    <table>
        <thead>
        <tr>
            <td>Start date</td>
            <td>Price</td>
        </tr>
        </thead>
        <c:forEach var="productPrice" items="${productPricesDto.prices}">
            <tr>
                <td>${productPrice.date}</td>
                <td class="price">
                    <fmt:formatNumber value="${productPrice.price}" type="currency"
                                      currencySymbol="${productPrice.currency.symbol}"/>
                </td>
            </tr>
        </c:forEach>
    </table>
</tags:master>