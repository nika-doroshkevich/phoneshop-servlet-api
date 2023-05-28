<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="cart" type="com.es.phoneshop.model.cart.Cart" scope="request"/>
<tags:master pageTitle="Cart">
    <p>
        Cart: ${cart}
    </p>

    <c:if test="${not empty message}">
        <div class="success">
                ${message}
        </div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="error">
            There was an error adding to cart
        </div>
    </c:if>

    <table>
        <thead>
        <tr>
            <td>Image</td>
            <td>
                Description
            </td>
            <td class="price">
                Price
            </td>
        </tr>
        </thead>
        <c:forEach var="item" items="${cart.items}">
            <tr>
                <td>
                    <img class="product-tile" src="${item.product.imageUrl}">
                </td>
                <td>
                    <a href="${pageContext.servletContext.contextPath}/products/${item.product.id}">
                            ${item.product.description}
                    </a>
                </td>
                <td class="price">
                    <a href="${pageContext.servletContext.contextPath}/products/prices/${item.product.id}"
                       target="_blank">
                        <fmt:formatNumber value="${item.product.price}" type="currency"
                                          currencySymbol="${item.product.currency.symbol}"/>
                    </a>
                </td>
            </tr>
        </c:forEach>
    </table>
</tags:master>