package com.dotdot.marketplace.payment;

import com.dotdot.marketplace.payment.dto.CheckoutRequest;
import com.dotdot.marketplace.product.dto.ProductResponseDto;
import com.dotdot.marketplace.product.entity.Product;
import com.dotdot.marketplace.product.repository.ProductRepository;
import com.dotdot.marketplace.product.service.ProductServiceImpl;
import com.dotdot.marketplace.productImage.entity.ProductImage;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class PaymentController {

    private final ProductServiceImpl productService;
    private final ProductRepository productRepository;
    private final  String ngrok;


    public PaymentController(@Value("${stripe.api.key}") String stripeApiKey,
                             ProductRepository productRepository,
                             @Value("${app_image_base_url}") String ngrok,
                             ProductServiceImpl productService) {
        Stripe.apiKey = stripeApiKey;
        this.productService = productService;
        this.productRepository = productRepository;
        this.ngrok = ngrok;
    }


    @PostMapping("/auth/create-checkout-session")
    public Map<String, String> createCheckoutSession(@RequestBody CheckoutRequest request) throws StripeException {
        Product productEntity = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        String imageUrl = null;
        if (productEntity.getImages() != null && !productEntity.getImages().isEmpty()) {
            ProductImage mainImage = productEntity.getImages().stream()
                    .filter(ProductImage::isMainImage)
                    .findFirst()
                    .orElse(productEntity.getImages().get(0));


            imageUrl = ngrok + "/marketplace-images/" + mainImage.getUrl();
        }

        SessionCreateParams.LineItem.PriceData.ProductData.Builder productDataBuilder =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(productEntity.getName());

        if (imageUrl != null) {
            productDataBuilder.addImage(imageUrl);
        }

        SessionCreateParams.LineItem.PriceData.ProductData productData = productDataBuilder.build();
        long unitAmount = (long) (productEntity.getPrice() * 100);

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

        Map<String, String> response = new HashMap<>();
        response.put("url", session.getUrl());

        return response;
    }

    @GetMapping("/success")
    public String success() {
        return "Payment successful!";
    }
}
