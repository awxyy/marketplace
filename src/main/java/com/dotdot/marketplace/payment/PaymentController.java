package com.dotdot.marketplace.payment;

import com.dotdot.marketplace.product.dto.CheckoutRequest;
import com.dotdot.marketplace.product.dto.ProductResponseDto;
import com.dotdot.marketplace.product.entity.Product;
import com.dotdot.marketplace.product.service.ProductService;
import com.dotdot.marketplace.product.service.ProductServiceImpl;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class PaymentController {

    private final ProductServiceImpl productService;

    public PaymentController(@Value("${stripe.api.key}") String stripeApiKey,
                             ProductServiceImpl productService) {
        Stripe.apiKey = stripeApiKey;
        this.productService = productService;
    }


    @PostMapping("/create-checkout-session")
    public Map<String,String> createCheckoutSession(@RequestBody CheckoutRequest request ) throws StripeException {
        ProductResponseDto product = productService.getById(request.getProductId());

        if (product == null) {
            throw new RuntimeException("Product not found");
        }
        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(product.getName())
                        .build();

        long unitAmount = (long) (product.getPrice() * 100);

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency("usd")
                        .setUnitAmount(unitAmount)
                        .setProductData(productData)
                        .build();

        SessionCreateParams.LineItem lineItem =
                SessionCreateParams.LineItem.builder()
                        .setPriceData(priceData)
                        .setQuantity((long) request.getQuantity())
                        .build();

        long applicationFee = (long) (unitAmount * 0.1 * request.getQuantity());

        SessionCreateParams.PaymentIntentData paymentIntentData =
                SessionCreateParams.PaymentIntentData.builder()
                        .setApplicationFeeAmount(applicationFee)
                        .setTransferData(
                                SessionCreateParams.PaymentIntentData.TransferData.builder()
                                        .setDestination("acct_1StXiHBCl19E4dOc")
                                        .build()
                        )
                        .build();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/success")
                .setCancelUrl("http://localhost:8080/cancel")
                .addLineItem(lineItem)
                .setPaymentIntentData(paymentIntentData)
                .build();

        Session session = Session.create(params);

        Map<String,String> response = new HashMap<>();
        response.put("url", session.getUrl());

        return response;
    }

    @GetMapping("/success")
    public String success() {
        return "Payment successful!";
    }
}
