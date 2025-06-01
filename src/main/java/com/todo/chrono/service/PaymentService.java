package com.todo.chrono.service;

import com.todo.chrono.dto.request.RechargeRequestDTO;
import com.todo.chrono.entity.Payment;
import com.todo.chrono.entity.User;
import com.todo.chrono.enums.PaymentStatus;
import com.todo.chrono.enums.Role;

import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import com.todo.chrono.repository.UserRepository;
import com.todo.chrono.repository.PaymentRepository;

@Service
@AllArgsConstructor
public class PaymentService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    public String createUrl(RechargeRequestDTO rechargeRequestDTO)
            throws NoSuchAlgorithmException, InvalidKeyException, Exception {

        User user = userRepository.findById(rechargeRequestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User ID không hợp lệ: " + rechargeRequestDTO.getUserId()));
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
        String returnUrl = "https://www.exe201.space/" + rechargeRequestDTO.getUserId();
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
        vnpParams.put("vnp_Amount", rechargeRequestDTO.getAmount() + "00");
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
        if (!queryParams.containsKey("userId") || !queryParams.containsKey("amount")) {
            throw new IllegalArgumentException("Thiếu thông tin bắt buộc trong callback.");
        }
        RechargeRequestDTO rechargeRequestDTO = new RechargeRequestDTO();
        rechargeRequestDTO.setUserId(Integer.parseInt(queryParams.get("userId")));
        rechargeRequestDTO.setAmount(queryParams.get("amount"));

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

        Payment payment = new Payment();
        payment.setUser(user);

        try {
            payment.setTotal_money(Integer.parseInt(rechargeRequestDTO.getAmount()));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Số tiền không hợp lệ: " + rechargeRequestDTO.getAmount());
        }

        payment.setPaidAt(LocalDateTime.now());
        payment.setPaymentStatus(paymentStatus);
        payment.setPayment_method("VNPAY");
        paymentRepository.save(payment);

        if (paymentStatus == PaymentStatus.PAID) {
            if (user.getRole() == Role.PREMIUM) {
                throw new RuntimeException("Tài khoản đã là PREMIUM, không thể nâng cấp thêm lần nữa.");
            }
    
            user.setRole(Role.PREMIUM);
    
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime currentExpiry = user.getPremiumExpiry();
            if (currentExpiry != null && currentExpiry.isAfter(now)) {
                user.setPremiumExpiry(currentExpiry.plusDays(30));
            } else {
                user.setPremiumExpiry(now.plusDays(30));
            }
    
            userRepository.save(user);
        }
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
