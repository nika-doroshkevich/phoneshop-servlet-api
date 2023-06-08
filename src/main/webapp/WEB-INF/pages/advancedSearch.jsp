<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>
<tags:master pageTitle="Product List">

    <h1>
        Advanced search
    </h1>

    <form>
        <table>
            <tr>
                <td>Description</td>
                <td>
                    <input name="description" value="${param.description}"/>
                    <input type="hidden" name="fromSearch" value="fromSearch"/>
                </td>
            </tr>

            <tr>
                <td>Search option</td>
                <td>
                    <select name="searchOption">
                        <c:forEach var="searchOption" items="${searchOptions}">
                            <option>${searchOption}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>


            <tr>
                <td>Min price</td>
                <td>
                    <input name="minPrice" value="${param.minPrice}"/>
                </td>
            </tr>

            <tr>
                <td>Max price</td>
                <td>
                    <input name="maxPrice" value="${param.maxPrice}"/>
                </td>
            </tr>

        </table>

        <p>
            <button>Search</button>
        </p>
    </form>

    <table>
        <thead>
        <tr>
            <td>Image</td>
            <td>Description
            </td>
            <td class="price">
                Price
            </td>
        </tr>
        </thead>
        <c:forEach var="product" items="${products}">
            <tr>
                <td>
                    <img class="product-tile" src="${product.imageUrl}">
                </td>
                <td>
                        ${product.description}
                </td>
                <td class="price">
                    <fmt:formatNumber value="${product.price}" type="currency"
                                      currencySymbol="${product.currency.symbol}"/>
                </td>
            </tr>
        </c:forEach>
    </table>

</tags:master>