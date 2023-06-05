package com.es.phoneshop.utils;

import com.es.phoneshop.exception.OutOfStockException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NoArgsConstructor;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ParseRequestUtil {

    public static void handleError(Map<Long, String> errors, Long productId, Exception e) {
        if (e.getClass().equals(ParseException.class)) {
            errors.put(productId, "Quantity should be a positive integer number");
        } else if (e.getClass().equals(OutOfStockException.class)) {
            errors.put(productId, "Out of stock, available " + ((OutOfStockException) e).getStockAvailable());
        }
    }

    public static int getQuantity(HttpServletRequest request, String quantityString) throws ParseException {
        var format = NumberFormat.getInstance(request.getLocale());
        var number = format.parse(quantityString);
        if (number.intValue() != number.doubleValue() || number.intValue() <= 0) {
            throw new ParseException("Quantity should be a positive integer number", 0);
        }
        return number.intValue();
    }
}
