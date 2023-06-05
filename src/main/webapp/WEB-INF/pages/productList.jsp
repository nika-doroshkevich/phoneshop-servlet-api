<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>
<tags:master pageTitle="Product List">
    <a href="${pageContext.servletContext.contextPath}/cart">Cart</a>

    <p>
        Welcome to Expert-Soft training!
    </p>

    <c:if test="${not empty param.message}">
        <p class="success">
                ${param.message}
        </p>
    </c:if>
    <c:if test="${not empty errors}">
        <p class="error">
            There was an error adding cart
        </p>
    </c:if>

    <form>
        <input name="query" value="${param.query}">
        <button>Search</button>
    </form>
    <table>
        <thead>
        <tr>
            <td>Image</td>
            <td>
                Description
                <tags:sortLink sort="description" order="asc"/>
                <tags:sortLink sort="description" order="desc"/>
            </td>
            <td class="quantity">
                Quantity
            </td>
            <td class="price">
                Price
                <tags:sortLink sort="price" order="asc"/>
                <tags:sortLink sort="price" order="desc"/>
            </td>
            <td></td>
        </tr>
        </thead>
        <c:forEach var="product" items="${products}">
            <tr>
                <td>
                    <img class="product-tile" src="${product.imageUrl}">
                </td>
                <td>
                    <a href="${pageContext.servletContext.contextPath}/products/${product.id}">
                            ${product.description}
                    </a>
                </td>
                <form method="post">
                    <td>
                        <fmt:formatNumber value="1" var="quantity"/>
                        <c:set var="error" value="${errors[product.id]}"/>

                        <input name="quantity" value="${not empty error ? param.quantity : 1}"
                               class="quantity" style="width: 100%;"/>
                        <c:if test="${not empty error}">
                            <div class="error">
                                    ${error}
                            </div>
                        </c:if>
                        <input type="hidden" name="productId" value="${product.id}"/>
                    </td>
                    <td class="price">
                        <a href="${pageContext.servletContext.contextPath}/products/prices/${product.id}"
                           target="_blank">
                            <fmt:formatNumber value="${product.price}" type="currency"
                                              currencySymbol="${product.currency.symbol}"/>
                        </a>
                    </td>
                    <td>
                        <p>
                            <button>Add to cart</button>
                        </p>
                    </td>
                </form>
            </tr>
        </c:forEach>
    </table>
    <tags:recentlyViewedProducts recentlyViewedProducts="${recentlyViewedProducts}"/>
</tags:master>