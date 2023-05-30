<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="errorMessage" scope="request" type="java.lang.String"/>
<tags:master pageTitle="Error">

    <h1>
            ${errorMessage}
    </h1>
</tags:master>