package com.todo.chrono.service;

import com.todo.chrono.dto.request.PaymentHistoryDTO;
import com.todo.chrono.dto.request.RechargeRequestDTO;
import com.todo.chrono.entity.Payment;
import com.todo.chrono.entity.SubscriptionPlans;
import com.todo.chrono.entity.User;
import com.todo.chrono.enums.PaymentStatus;
import com.todo.chrono.enums.Role;
import com.todo.chrono.mapper.PaymentMapper;
import com.todo.chrono.repository.PaymentRepository;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import com.todo.chrono.repository.UserRepository;
import com.todo.chrono.util.error.IdInvalidException;
import com.todo.chrono.repository.PaymentRepository;
import com.todo.chrono.repository.SubscriptionPlansRepository;

@Service
@AllArgsConstructor
public class PaymentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private SubscriptionPlansRepository subscriptionPlanRepository;

    public String createUrl(RechargeRequestDTO rechargeRequestDTO)
            throws NoSuchAlgorithmException, InvalidKeyException, Exception {

        User user = userRepository.findById(rechargeRequestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User ID không hợp lệ: " + rechargeRequestDTO.getUserId()));
        SubscriptionPlans subscriptionPlan = subscriptionPlanRepository
                .findById(rechargeRequestDTO.getSubscriptionPlanId())
                .orElseThrow(() -> new RuntimeException(
                        "Subscription plan ID không hợp lệ: " + rechargeRequestDTO.getSubscriptionPlanId()));
        Double amount = subscriptionPlan.getPrice();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime createDate = LocalDateTime.now();
        String formattedCreateDate = createDate.format(formatter);

        // User user = accountUtils.getCurrentUser();
        String userId = UUID.randomUUID().toString().substring(0, 6);

        String tmnCode = "IGHPSKMK";
        String secretKey = "X8ER54EJXNT8I5IJBUBUWDIEY0DFIT3D";
        String vnpUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
        // String returnUrl = "http://mathcha.online?student_id=" +
        // rechargeRequestDTO.getStudent_id() + "&course_id=" +
        // rechargeRequestDTO.getCourse_id();
        // String returnUrl = "http://159.223.39.71?student_id=" +
        // rechargeRequestDTO.getStudent_id() + "&course_id=" +
        // rechargeRequestDTO.getCourse_id();
        String returnUrl = "https://www.chronobuddy.live/home/PageProListPage"
                + "?userId=" + rechargeRequestDTO.getUserId()
                + "&subscriptionPlanId=" + rechargeRequestDTO.getSubscriptionPlanId();

        String currCode = "VND";
        Map<String, String> vnpParams = new TreeMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", tmnCode);
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_CurrCode", currCode);
        vnpParams.put("vnp_TxnRef", userId);
        vnpParams.put("vnp_OrderInfo", "Thanh toan cho ma GD: " + userId);
        vnpParams.put("vnp_OrderType", "other");
        long vnpAmount = Math.round(amount * 100);
        vnpParams.put("vnp_Amount", String.valueOf(vnpAmount));
        vnpParams.put("vnp_ReturnUrl", returnUrl);
        vnpParams.put("vnp_CreateDate", formattedCreateDate);
        vnpParams.put("vnp_IpAddr", "128.199.178.23");

        StringBuilder signDataBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            signDataBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
            signDataBuilder.append("=");
            signDataBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
            signDataBuilder.append("&");
        }
        signDataBuilder.deleteCharAt(signDataBuilder.length() - 1); // Remove last '&'

        String signData = signDataBuilder.toString();
        String signed = generateHMAC(secretKey, signData);

        vnpParams.put("vnp_SecureHash", signed);

        StringBuilder urlBuilder = new StringBuilder(vnpUrl);
        urlBuilder.append("?");
        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            urlBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
            urlBuilder.append("=");
            urlBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
            urlBuilder.append("&");
        }
        urlBuilder.deleteCharAt(urlBuilder.length() - 1); // Remove last '&'

        return urlBuilder.toString();
    }

    private String generateHMAC(String secretKey, String signData)
            throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmacSha512 = Mac.getInstance("HmacSHA512");
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmacSha512.init(keySpec);
        byte[] hmacBytes = hmacSha512.doFinal(signData.getBytes(StandardCharsets.UTF_8));

        StringBuilder result = new StringBuilder();
        for (byte b : hmacBytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    public void handlePaymentCallback(Map<String, String> queryParams) {
        if (!queryParams.containsKey("userId") || !queryParams.containsKey("subscriptionPlanId")) {
            throw new IllegalArgumentException("Thiếu thông tin bắt buộc trong callback.");
        }
        RechargeRequestDTO rechargeRequestDTO = new RechargeRequestDTO();
        rechargeRequestDTO.setUserId(Integer.parseInt(queryParams.get("userId")));
        rechargeRequestDTO.setSubscriptionPlanId(Integer.parseInt(queryParams.get("subscriptionPlanId")));

        // Thêm các tham số student_id và course_id nếu có
        if (queryParams.containsKey("userId")) {
            rechargeRequestDTO.setUserId(Integer.parseInt(queryParams.get("userId")));
        }
        String responseCode = queryParams.get("vnp_ResponseCode");
        if ("00".equals(responseCode)) {
            // Thành công
            savePayment(rechargeRequestDTO, PaymentStatus.PAID);
        } else {
            // Thất bại
            savePayment(rechargeRequestDTO, PaymentStatus.FAILED);
        }
    }

    public void savePayment(RechargeRequestDTO rechargeRequestDTO, PaymentStatus paymentStatus) {
        User user = userRepository.findById(rechargeRequestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User ID không hợp lệ: " + rechargeRequestDTO.getUserId()));

        SubscriptionPlans subscriptionPlan = subscriptionPlanRepository
                .findById(rechargeRequestDTO.getSubscriptionPlanId())
                .orElseThrow(() -> new RuntimeException(
                        "Subscription plan ID không hợp lệ: " + rechargeRequestDTO.getSubscriptionPlanId()));

        if (paymentStatus == PaymentStatus.PAID) {
            if (user.getRole() == Role.PREMIUM) {
                Payment payment = new Payment();
                payment.setUser(user);
                payment.setSubscriptionPlan(subscriptionPlan);
                payment.setTotalMoney(subscriptionPlan.getPrice());
                payment.setPaidAt(LocalDateTime.now());
                payment.setPaymentStatus(PaymentStatus.FAILED); // Hoặc thêm enum STATUS_DUPLICATE
                payment.setPaymentMethod("MOMO");
                paymentRepository.save(payment);

                throw new RuntimeException("Tài khoản đã là PREMIUM, không thể nâng cấp thêm lần nữa.");
            }

            user.setRole(Role.PREMIUM);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime currentExpiry = user.getPremiumExpiry();
            Integer durationDays = subscriptionPlan.getDurationDays();

            if (durationDays != null && durationDays == -1) {
                // Gói vĩnh viễn
                user.setPremiumExpiry(now.plusYears(100));
            } else if (currentExpiry != null && currentExpiry.isAfter(now)) {
                // Gia hạn nếu còn hạn
                user.setPremiumExpiry(currentExpiry.plusDays(durationDays));
            } else {
                // Gán mới nếu hết hạn
                user.setPremiumExpiry(now.plusDays(durationDays));
            }

            userRepository.save(user);
        }

        // Sau khi tất cả đều hợp lệ mới lưu payment
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setSubscriptionPlan(subscriptionPlan);
        payment.setTotalMoney(subscriptionPlan.getPrice());
        payment.setPaidAt(LocalDateTime.now());
        payment.setPaymentStatus(paymentStatus);
        payment.setPaymentMethod("MOMO");

        paymentRepository.save(payment);
    }
    public BigDecimal getRevenueByDay(int year, int month, int day) {
        return paymentRepository.getTotalRevenueByDay(year, month, day);
    }

    public BigDecimal getRevenueByMonth(int year, int month) {
        return paymentRepository.getTotalRevenueByMonth(year, month);
    }

    public BigDecimal getRevenueByYear(int year) {
        return paymentRepository.getTotalRevenueByYear(year);
    }

    public BigDecimal getRevenueBetweenDates(LocalDate startDate, LocalDate endDate) {
        return paymentRepository.getTotalRevenueBetweenDates(startDate, endDate);
    }
    public BigDecimal getRevenueBySubscriptionPlan(int subscriptionPlanId, LocalDate  start, LocalDate end) {
        return paymentRepository.getRevenueBySubscriptionPlanAndDateRange(subscriptionPlanId, start, end);
    }
    public BigDecimal getRevenueBySubscriptionPlanAndDay(int subscriptionPlanId, int year, int month, int day) {
        return paymentRepository.getRevenueBySubscriptionPlanAndDay(subscriptionPlanId, year, month, day);
    }

    public BigDecimal getRevenueBySubscriptionPlanAndMonth(int subscriptionPlanId, int year, int month) {
        return paymentRepository.getRevenueBySubscriptionPlanAndMonth(subscriptionPlanId, year, month);
    }

    public BigDecimal getRevenueBySubscriptionPlanAndYear(int subscriptionPlanId, int year) {
        return paymentRepository.getRevenueBySubscriptionPlanAndYear(subscriptionPlanId, year);
    }

    public List<PaymentHistoryDTO> getPaymentHistoryByUserId(Integer userId) throws IdInvalidException {
        userRepository.findById(userId)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy người dùng với ID = " + userId));
    
        return paymentRepository.findByUserIdOrderByPaidAtDesc(userId)
                .stream()
                .map(PaymentMapper::mapToPaymentHistoryDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentHistoryDTO> getAllPaymentHistory() {
        return paymentRepository.findAllWithDeletedPlans().stream()
                .map(p -> new PaymentHistoryDTO(
                        p.getId(),
                        p.getSubscriptionPlan() != null 
                            ? p.getSubscriptionPlan().getName() 
                            : "[Gói đã xóa]",
                        p.getTotalMoney(),
                        p.getPaymentMethod(),
                        p.getPaymentStatus(),
                        p.getPaidAt(),
                        p.getUser().getId(),
                        p.getUser().getUsername()
                ))
                .toList();
    }

    

    // public List<PaymentDTO> getPaymetsByUserId(int user_id) throws
    // RuntimeException {
    // User user = userRepository.findById(user_id)
    // .orElseThrow(() -> new RuntimeException("User ID is invalid"));
    // List<Payment> payments = paymentRepository.findPaymentByUserId(user_id);
    // return payments.stream()
    // .map(PaymentMapper::mapToPaymentDTO)
    // .collect(Collectors.toList());
    // }
    // // public List<ResPaymentDTO> getPaymentsByDate(LocalDate date) {
    //// String formattedDate = date.toString(); // yyyy-MM-dd
    //// List<Payment> payments =
    // paymentRepository.findPaymentsByDate(formattedDate);
    //// return payments.stream()
    //// .map(PaymentMapper::mapToPaymentDTO)
    //// .map(this::convertToResPaymentDTO)
    //// .collect(Collectors.toList());
    //// }
    // public ResPaymentDTO convertToResPaymentDTO(PaymentDTO paymentDTO) {
    // ResPaymentDTO res = new ResPaymentDTO();
    // res.setPayment_date(paymentDTO.getPayment_date());
    // res.setPayment_id(paymentDTO.getPayment_id());
    // res.setTotal_money(paymentDTO.getTotal_money());
    // res.setPayment_method(paymentDTO.getPayment_method());
    // res.setOrderId(paymentDTO.getOrderId());
    // res.setUser(paymentDTO.getUser());
    // return res;
    // }
    // public List<ResPaymentDTO> getPaymetsByCourseId(int course_id) throws
    // RuntimeException {
    // Course course = courseRepository.findById(course_id)
    // .orElseThrow(() -> new RuntimeException("Course ID is invalid"));
    // List<Payment> payments = paymentRepository.findPaymentByCourseId(course_id);
    // return payments.stream()
    // .map(PaymentMapper::mapToPaymentDTO)
    // .map(this::convertToResPaymentDTO)
    // .collect(Collectors.toList());
    // }
    // public List<PaymentDTO> getPaymentsByUserIdAndPaymentDate(int user_id, String
    // payment_date) {
    // User user = userRepository.findById(user_id)
    // .orElseThrow(() -> new RuntimeException("User ID is invalid"));
    // List<Payment> payments =
    // paymentRepository.findPaymentsByUserIdAndPaymentDate(user_id,payment_date);
    // return payments.stream()
    // .map(PaymentMapper::mapToPaymentDTO)
    // .collect(Collectors.toList());
    // }
    // public List<PaymentDTO> getPaymentsByUserIdAndPaymentMonth(int user_id,
    // String payment_month) {
    // User user = userRepository.findById(user_id)
    // .orElseThrow(() -> new RuntimeException("User ID is invalid"));
    // List<Payment> payments =
    // paymentRepository.findPaymentsByUserIdAndPaymentMonth(user_id,payment_month);
    // return payments.stream()
    // .map(PaymentMapper::mapToPaymentDTO)
    // .collect(Collectors.toList());
    // }
    // public List<PaymentDTO> getPaymentsByUserIdAndPaymentYear(int user_id, String
    // payment_year) {
    // User user = userRepository.findById(user_id)
    // .orElseThrow(() -> new RuntimeException("User ID is invalid"));
    // List<Payment> payments =
    // paymentRepository.findPaymentsByUserIdAndPaymentYear(user_id,payment_year);
    // return payments.stream()
    // .map(PaymentMapper::mapToPaymentDTO)
    // .collect(Collectors.toList());
    // }
}
