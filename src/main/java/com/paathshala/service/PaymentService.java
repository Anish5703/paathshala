package com.paathshala.service;
import com.paathshala.dto.payment.PaymentCheckoutRequest;
import com.paathshala.dto.payment.PaymentCheckoutResponse;
import com.paathshala.dto.payment.PaymentVerifyRequest;
import com.paathshala.dto.payment.PaymentVerifyResponse;
import com.paathshala.entity.Course;
import com.paathshala.entity.Enrollment;
import com.paathshala.entity.User;
import com.paathshala.exception.enrollment.PaymentFailedException;
import com.paathshala.exception.course.CourseNotFoundException;
import com.paathshala.repository.CourseRepo;
import com.paathshala.repository.UserRepo;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PaymentService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    private final EnrollmentService enrollmentService;

    private final UserRepo userRepo;

    private final CourseRepo courseRepo;

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    public PaymentService(EnrollmentService enrollmentService,UserRepo userRepo,CourseRepo courseRepo)
    {
        this.enrollmentService=enrollmentService;
        this.userRepo=userRepo;
        this.courseRepo=courseRepo;
    }

    @Transactional
    public PaymentCheckoutResponse createCheckout(PaymentCheckoutRequest request){
        try{
        log.info("Entered createCheckout method");
        Stripe.apiKey = stripeSecretKey;

        log.info("Stripe secret key {}", stripeSecretKey);
        // 1. Validate user
        User user = userRepo.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + request.getUsername()));

        // 2. Validate course
        Course course = courseRepo.findByTitle(request.getCourseTitle())
                .orElseThrow(() -> new CourseNotFoundException(
                        "Course not found: " + request.getCourseTitle()));

        // 3. Verify it's a paid course
        if (course.getPrice() <= 0) {
            throw new IllegalArgumentException(
                    "This is a free course. Use free enrollment instead.");
        }


            // 4. Create Stripe Checkout Session
            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(request.getSuccessUrl() + "?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(request.getCancelUrl())
                    .setCustomerEmail(user.getEmail())
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("npr")
                                                    .setUnitAmount((long) (course.getPrice() * 100))
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(course.getTitle())
                                                                    .setDescription(course.getDescription())
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    // Store username and courseTitle in metadata for verification
                    .putMetadata("username", request.getUsername())
                    .putMetadata("courseTitle", request.getCourseTitle())
                    .build();

            Session session = Session.create(params);

            return new PaymentCheckoutResponse(
                    true,
                    session.getUrl(),
                    "Checkout session created"
            );
        }
        catch(StripeException ex)
        {
            log.error("Stripe Exception : {}",ex.getLocalizedMessage());
            throw new PaymentFailedException("Payment Processing Failed ");

        }

    }

    /*
     * Verify Stripe payment and create enrollment
     *
     * Steps:
     * 1. Retrieve Stripe session by sessionId
     * 2. Check payment_status == "paid"
     * 3. Extract username and courseTitle from metadata
     * 4. Create enrollment record via EnrollmentService
     * 5. Return verification result
     */
    @Transactional
    public PaymentVerifyResponse verifyAndEnroll(PaymentVerifyRequest request) {
        Stripe.apiKey = stripeSecretKey;

        try {
            // 1. Retrieve session from Stripe
            Session session = Session.retrieve(request.getSessionId());

            // 2. Check payment status
            if ("paid".equals(session.getPaymentStatus())) {
                // 3. Extract metadata
                String username = session.getMetadata().get("username");
                String courseTitle = session.getMetadata().get("courseTitle");

                // 4. Create paid enrollment
                Enrollment enrollment = enrollmentService.createPaidEnrollment(
                        username, courseTitle, request.getSessionId()
                );

                // 5. Return success
                return new PaymentVerifyResponse(
                        true,
                        enrollment.getId(),
                        true,
                        "Payment verified and enrolled successfully"
                );
            }

            // Payment not completed
            return new PaymentVerifyResponse(
                    false, 0,false,
                    "Payment not completed"
            );

        } catch (StripeException e) {
            throw new PaymentFailedException("Payment processing failed");

        } catch (IllegalArgumentException e) {
            throw new PaymentFailedException("Already enrolled or course not found");
        }
    }
}