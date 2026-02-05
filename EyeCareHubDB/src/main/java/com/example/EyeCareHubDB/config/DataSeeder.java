package com.example.EyeCareHubDB.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import com.example.EyeCareHubDB.entity.Account;
import com.example.EyeCareHubDB.entity.Category;
import com.example.EyeCareHubDB.entity.Customer;
import com.example.EyeCareHubDB.entity.Order;
import com.example.EyeCareHubDB.entity.OrderItem;
import com.example.EyeCareHubDB.entity.Product;
import com.example.EyeCareHubDB.entity.ProductVariant;
import com.example.EyeCareHubDB.repository.AccountRepository;
import com.example.EyeCareHubDB.repository.CategoryRepository;
import com.example.EyeCareHubDB.repository.CustomerRepository;
import com.example.EyeCareHubDB.repository.OrderItemRepository;
import com.example.EyeCareHubDB.repository.OrderRepository;
import com.example.EyeCareHubDB.repository.ProductRepository;
import com.example.EyeCareHubDB.repository.ProductVariantRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

        private final CategoryRepository categoryRepository;
        private final ProductRepository productRepository;
        private final ProductVariantRepository productVariantRepository;
        private final AccountRepository accountRepository;
        private final CustomerRepository customerRepository;
        private final OrderRepository orderRepository;
        private final OrderItemRepository orderItemRepository;
        private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

        @Override
        @Transactional
        public void run(String... args) throws Exception {
                if (categoryRepository.count() > 0) {
                        log.info("Database already seeded. Skipping...");
                        return;
                }

                log.info("Starting database seeding with expanded data...");

                // 1. Seed Categories
                Category frames = createCategory("Gọng Kính", "gong-kinh",
                                "Gọng kính thời trang từ nhiều thương hiệu.");
                Category lenses = createCategory("Tròng Kính", "trong-kinh",
                                "Tròng kính kỹ thuật số, chống ánh sáng xanh.");
                Category sunglasses = createCategory("Kính Mát", "kinh-mat",
                                "Kính mát thời trang bảo vệ mắt khỏi tia UV.");
                Category contactLenses = createCategory("Kính Áp Tròng", "kinh-ap-trong",
                                "Kính áp trọng cận thị và thời trang.");
                Category accessories = createCategory("Phụ Kiện", "phu-kien", "Hộp kính, nước rửa kính, khăn lau.");

                categoryRepository.saveAll(Arrays.asList(frames, lenses, sunglasses, contactLenses, accessories));

                // 2. Seed Products & Variants
                // Frames
                Product p1 = createProduct("Kính Ray-Ban Aviator Classic", "RB-AVIATOR-01", frames, "Ray-Ban",
                                new BigDecimal("3500000"), new BigDecimal("3150000"), true);
                createVariant(p1, "RB-AVI-GOLD-G", "Vàng/Xanh", "L (58-14)", "Kim loại", BigDecimal.ZERO, 20);
                createVariant(p1, "RB-AVI-SILVER-G", "Bạc/Xanh", "L (58-14)", "Kim loại", BigDecimal.ZERO, 15);

                Product p2 = createProduct("Gọng Kính Gucci Square-Frame", "GC-SQUARE-02", frames, "Gucci",
                                new BigDecimal("8500000"), null, true);
                createVariant(p2, "GC-SQ-BLACK", "Đen", "M (52-19)", "Acetate", BigDecimal.ZERO, 10);
                createVariant(p2, "GC-SQ-HAVANA", "Havana", "M (52-19)", "Acetate", new BigDecimal("500000"), 5);

                // Sunglasses
                Product p3 = createProduct("Kính Mát Oakley Holbrook", "OK-HOLBROOK-03", sunglasses, "Oakley",
                                new BigDecimal("4200000"), new BigDecimal("3800000"), false);
                createVariant(p3, "OK-HB-MATTE", "Đen Nhám", "One Size", "O-Matter", BigDecimal.ZERO, 25);

                // Lenses
                Product p4 = createProduct("Tròng Essilor Eyezen Start", "ES-EYEZEN-04", lenses, "Essilor",
                                new BigDecimal("1800000"), null, false);
                createVariant(p4, "ES-EZ-1.56", "Trong suốt (1.56)", "N/A", "Plastic", BigDecimal.ZERO, 100);
                createVariant(p4, "ES-EZ-1.60", "Trong suốt (1.60)", "N/A", "Plastic", new BigDecimal("700000"), 50);

                // Contact Lenses
                Product p5 = createProduct("Kính Áp Tròng Acuvue Moist", "AC-MOIST-05", contactLenses,
                                "Johnson & Johnson",
                                new BigDecimal("750000"), new BigDecimal("650000"), true);
                createVariant(p5, "AC-M-BC85", "Trong suốt", "BC 8.5", "Hydrogel", BigDecimal.ZERO, 40);

                // 3. Seed Accounts
                String pass = passwordEncoder.encode("password123");
                Account admin = createAccount("admin@eyecarehub.com", pass, "0123456789", Account.AccountRole.ADMIN);
                Account staff = createAccount("staff@eyecarehub.com", pass, "0111222333", Account.AccountRole.STAFF);

                Account c1Acc = createAccount("customer1@gmail.com", pass, "0901234567", Account.AccountRole.CUSTOMER);
                Account c2Acc = createAccount("customer2@gmail.com", pass, "0908889999", Account.AccountRole.CUSTOMER);
                Account c3Acc = createAccount("demo@example.com", pass, "0999999999", Account.AccountRole.CUSTOMER);

                accountRepository.saveAll(Arrays.asList(admin, staff, c1Acc, c2Acc, c3Acc));

                Customer customer1 = createCustomer(c1Acc, "Nguyễn", "Văn An", Customer.Gender.MALE);
                Customer customer2 = createCustomer(c2Acc, "Trần", "Thị Bình", Customer.Gender.FEMALE);
                Customer demoCustomer = createCustomer(c3Acc, "Người Dùng", "Demo", Customer.Gender.OTHER);

                customerRepository.saveAll(Arrays.asList(customer1, customer2, demoCustomer));

                // 4. Seed Orders
                List<ProductVariant> allVariants = productVariantRepository.findAll();
                seedSampleOrders(c3Acc, allVariants); // Orders for demo account
                seedSampleOrders(c1Acc, allVariants); // Orders for customer 1

                log.info("Database seeding completed successfully with {} products and sample orders.",
                                productRepository.count());
        }

        private Category createCategory(String name, String slug, String desc) {
                return Category.builder().name(name).slug(slug).description(desc).isActive(true).build();
        }

        private Product createProduct(String name, String sku, Category cat, String brand, BigDecimal base,
                        BigDecimal sale, boolean featured) {
                Product p = Product.builder()
                                .name(name).slug(sku.toLowerCase()).sku(sku).category(cat).brand(brand)
                                .shortDescription(name + " chất lượng cao.")
                                .fullDescription("Sản phẩm " + name + " từ thương hiệu " + brand
                                                + " mang lại trải nghiệm tuyệt vời.")
                                .basePrice(base).salePrice(sale).isActive(true).isFeatured(featured)
                                .build();
                return productRepository.save(p);
        }

        private void createVariant(Product p, String sku, String color, String size, String material, BigDecimal extra,
                        int stock) {
                ProductVariant v = ProductVariant.builder()
                                .product(p).sku(sku).color(color).size(size).material(material)
                                .additionalPrice(extra).stockQuantity(stock).isActive(true)
                                .build();
                productVariantRepository.save(v);
        }

        private Account createAccount(String email, String pass, String phone, Account.AccountRole role) {
                return Account.builder().email(email).passwordHash(pass).phoneNumber(phone).role(role)
                                .status(Account.AccountStatus.ACTIVE).build();
        }

        private Customer createCustomer(Account acc, String first, String last, Customer.Gender gender) {
                return Customer.builder().account(acc).firstName(first).lastName(last).gender(gender).build();
        }

        private void seedSampleOrders(Account account, List<ProductVariant> variants) {
                if (variants.isEmpty())
                        return;
                Random rand = new Random();

                // PENDING Order
                createOrder(account, variants.get(0), 1, Order.OrderStatus.PENDING, "123 Lê Lợi, Quận 1, HCM");

                // CONFIRMED Order
                if (variants.size() > 1)
                        createOrder(account, variants.get(1), 2, Order.OrderStatus.CONFIRMED,
                                        "456 Nguyễn Huệ, Quận 1, HCM");

                // DELIVERED Order
                if (variants.size() > 2)
                        createOrder(account, variants.get(2), 1, Order.OrderStatus.DELIVERED,
                                        "789 Đồng Khởi, Quận 1, HCM");

                // CANCELLED Order
                if (variants.size() > 3)
                        createOrder(account, variants.get(3), 1, Order.OrderStatus.CANCELLED,
                                        "321 Cách Mạng Tháng 8, Quận 3, HCM");
        }

        private void createOrder(Account account, ProductVariant variant, int qty, Order.OrderStatus status,
                        String address) {
                BigDecimal unitPrice = variant.getProduct().getSalePrice() != null ? variant.getProduct().getSalePrice()
                                : variant.getProduct().getBasePrice();
                unitPrice = unitPrice.add(
                                variant.getAdditionalPrice() != null ? variant.getAdditionalPrice() : BigDecimal.ZERO);
                BigDecimal total = unitPrice.multiply(new BigDecimal(qty));

                Order order = Order.builder()
                                .orderNumber("ORD-SEED-" + System.nanoTime() % 1000000)
                                .account(account)
                                .totalPrice(total)
                                .status(status)
                                .shippingAddress(address)
                                .phoneNumber(account.getPhoneNumber())
                                .orderItems(new ArrayList<>())
                                .createdAt(LocalDateTime.now().minusDays(new Random().nextInt(30)))
                                .updatedAt(LocalDateTime.now())
                                .build();

                OrderItem item = OrderItem.builder()
                                .order(order)
                                .productVariant(variant)
                                .quantity(qty)
                                .price(unitPrice)
                                .build();

                order.getOrderItems().add(item);
                orderRepository.save(order);
        }
}
