package com.example.EyeCareHubDB.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.EyeCareHubDB.dto.BuyNowRequest;
import com.example.EyeCareHubDB.dto.CheckoutRequest;
import com.example.EyeCareHubDB.dto.OrderDTO;
import com.example.EyeCareHubDB.dto.OrderItemDTO;
import com.example.EyeCareHubDB.dto.OrderStatisticsResponse;
import com.example.EyeCareHubDB.dto.PageResponse;
import com.example.EyeCareHubDB.entity.Account;
import com.example.EyeCareHubDB.entity.Cart;
import com.example.EyeCareHubDB.entity.CartItem;
import com.example.EyeCareHubDB.entity.Order;
import com.example.EyeCareHubDB.entity.OrderItem;
import com.example.EyeCareHubDB.entity.ProductVariant;
import com.example.EyeCareHubDB.repository.AccountRepository;
import com.example.EyeCareHubDB.repository.CartRepository;
import com.example.EyeCareHubDB.repository.OrderRepository;
import com.example.EyeCareHubDB.repository.ProductVariantRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductVariantRepository productVariantRepository;
    private final AccountRepository accountRepository;

    public OrderStatisticsResponse getOrderStatistics() {
        java.util.List<Object[]> statusCounts = orderRepository.countOrdersByStatus();
        java.util.Map<String, Long> statusMap = new java.util.HashMap<>();
        long totalOrders = 0;

        for (Object[] result : statusCounts) {
            String status = ((Order.OrderStatus) result[0]).name();
            Long count = (Long) result[1];
            statusMap.put(status, count);
            totalOrders += count;
        }

        java.math.BigDecimal totalRevenue = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() != Order.OrderStatus.CANCELLED)
                .map(Order::getTotalPrice)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        return OrderStatisticsResponse.builder()
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .ordersByStatus(statusMap)
                .build();
    }

    @Transactional
    public OrderDTO buyNow(BuyNowRequest request) {
        Long accountId = getCurrentUserId();
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        ProductVariant variant = productVariantRepository.findById(request.getProductVariantId())
                .orElseThrow(() -> new RuntimeException("Product variant not found"));

        if (variant.getStockQuantity() < request.getQuantity()) {
            throw new RuntimeException("Not enough stock available");
        }

        BigDecimal unitPrice = calculateUnitPrice(variant);
        BigDecimal totalPrice = unitPrice.multiply(new BigDecimal(request.getQuantity()));

        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .account(account)
                .totalPrice(totalPrice)
                .status(Order.OrderStatus.PENDING)
                .shippingAddress(request.getShippingAddress())
                .phoneNumber(request.getPhoneNumber())
                .notes(request.getNotes())
                .build();

        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .productVariant(variant)
                .quantity(request.getQuantity())
                .price(unitPrice)
                .build();

        order.getOrderItems().add(orderItem);

        variant.setStockQuantity(variant.getStockQuantity() - request.getQuantity());
        productVariantRepository.save(variant);

        Order savedOrder = orderRepository.save(order);
        return toDTO(savedOrder);
    }

    @Transactional
    public OrderDTO placeOrder(CheckoutRequest request) {
        Long accountId = getCurrentUserId();
        Cart cart = cartRepository.findByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException("Cart not found for this account"));

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem item : cart.getCartItems()) {
            ProductVariant variant = item.getProductVariant();
            if (variant.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException(
                        "Not enough stock for: " + variant.getProduct().getName() + " (" + variant.getColor() + ")");
            }

            BigDecimal unitPrice = calculateUnitPrice(variant);
            totalPrice = totalPrice.add(unitPrice.multiply(new BigDecimal(item.getQuantity())));
        }

        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .account(account)
                .totalPrice(totalPrice)
                .status(Order.OrderStatus.PENDING)
                .shippingAddress(request.getShippingAddress())
                .phoneNumber(request.getPhoneNumber())
                .notes(request.getNotes())
                .build();

        for (CartItem cartItem : cart.getCartItems()) {
            ProductVariant variant = cartItem.getProductVariant();
            BigDecimal unitPrice = calculateUnitPrice(variant);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productVariant(variant)
                    .quantity(cartItem.getQuantity())
                    .price(unitPrice)
                    .build();

            order.getOrderItems().add(orderItem);

            variant.setStockQuantity(variant.getStockQuantity() - cartItem.getQuantity());
            productVariantRepository.save(variant);
        }

        Order savedOrder = orderRepository.save(order);

        cart.getCartItems().clear();
        cartRepository.save(cart);

        return toDTO(savedOrder);
    }

    public OrderDTO getOrderById(Long orderId) {
        Account current = getCurrentAccount();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        boolean isAdminOrStaff = current.getRole() == Account.AccountRole.ADMIN ||
                current.getRole() == Account.AccountRole.STAFF;

        if (!isAdminOrStaff && !order.getAccount().getId().equals(current.getId())) {
            throw new RuntimeException("You do not have permission to view this order");
        }

        return toDTO(order);
    }

    public PageResponse<OrderDTO> getMyOrders(String status, int page, int size) {
        Long accountId = getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> orderPage;

        if (status != null && !status.isEmpty()) {
            try {
                Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
                orderPage = orderRepository.findByAccountIdAndStatus(accountId, orderStatus, pageable);
            } catch (IllegalArgumentException e) {
                orderPage = orderRepository.findByAccountId(accountId, pageable);
            }
        } else {
            orderPage = orderRepository.findByAccountId(accountId, pageable);
        }

        return toPageResponse(orderPage);
    }

    public PageResponse<OrderDTO> getAllOrdersPaginated(String query, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Order.OrderStatus orderStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }

        Page<Order> orderPage = orderRepository.searchOrders(query, orderStatus, pageable);
        return toPageResponse(orderPage);
    }

    @Transactional
    public OrderDTO cancelOrder(Long orderId) {
        Account current = getCurrentAccount();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        boolean isAdminOrStaff = current.getRole() == Account.AccountRole.ADMIN ||
                current.getRole() == Account.AccountRole.STAFF;

        if (!isAdminOrStaff && !order.getAccount().getId().equals(current.getId())) {
            throw new RuntimeException("You do not have permission to cancel this order");
        }

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("Only PENDING orders can be cancelled");
        }

        for (OrderItem item : order.getOrderItems()) {
            ProductVariant variant = item.getProductVariant();
            variant.setStockQuantity(variant.getStockQuantity() + item.getQuantity());
            productVariantRepository.save(variant);
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        return toDTO(orderRepository.save(order));
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        try {
            order.setStatus(Order.OrderStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid order status: " + status);
        }

        return toDTO(orderRepository.save(order));
    }

    private BigDecimal calculateUnitPrice(ProductVariant variant) {
        BigDecimal basePrice = variant.getProduct().getSalePrice() != null ? variant.getProduct().getSalePrice()
                : variant.getProduct().getBasePrice();

        return basePrice.add(variant.getAdditionalPrice() != null ? variant.getAdditionalPrice() : BigDecimal.ZERO);
    }

    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = new Random().nextInt(900) + 100;
        return "ORD-" + timestamp + "-" + random;
    }

    private OrderDTO toDTO(Order order) {
        return OrderDTO.builder()
                .id(order.getId())
                .accountId(order.getAccount().getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus().name())
                .totalPrice(order.getTotalPrice())
                .shippingAddress(order.getShippingAddress())
                .phoneNumber(order.getPhoneNumber())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt().toString())
                .items(order.getOrderItems().stream().map(this::toItemDTO).collect(Collectors.toList()))
                .build();
    }

    private OrderItemDTO toItemDTO(OrderItem item) {
        ProductVariant variant = item.getProductVariant();
        return OrderItemDTO.builder()
                .id(item.getId())
                .productVariantId(variant.getId())
                .productName(variant.getProduct().getName())
                .variantSku(variant.getSku())
                .variantColor(variant.getColor())
                .variantSize(variant.getSize())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .subtotal(item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .build();
    }

    private PageResponse<OrderDTO> toPageResponse(Page<Order> orderPage) {
        List<OrderDTO> content = orderPage.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return PageResponse.<OrderDTO>builder()
                .content(content)
                .pageNumber(orderPage.getNumber())
                .pageSize(orderPage.getSize())
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .last(orderPage.isLast())
                .build();
    }

    private Account getCurrentAccount() {
        Account account = com.example.EyeCareHubDB.util.SecurityUtils.getCurrentAccount();
        if (account == null) {
            throw new RuntimeException("Unauthorized");
        }
        return account;
    }

    private Long getCurrentUserId() {
        Long id = com.example.EyeCareHubDB.util.SecurityUtils.getCurrentUserId();
        if (id == null) {
            throw new RuntimeException("You must login first to use this function !!!.");
        }
        return id;
    }
}
