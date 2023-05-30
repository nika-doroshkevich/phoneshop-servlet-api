<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="label" required="true" %>
<%@ attribute name="order" required="true" type="com.es.phoneshop.model.order.Order" %>

<tr>
    <td>${label}</td>
    <td>
        ${order[name]}
    </td>
</tr>