package com.example.EyeCareHubDB.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.EyeCareHubDB.entity.*;
import com.example.EyeCareHubDB.repository.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

        private final AccountRepository accountRepository;
        private final CustomerRepository customerRepository;
        private final CategoryRepository categoryRepository;
        private final ProductRepository productRepository;
        private final ProductVariantRepository productVariantRepository;
        private final ProductMediaRepository productMediaRepository;
        private final PromotionRepository promotionRepository;
        private final InventoryLocationRepository locationRepository;
        private final InventoryStockRepository stockRepository;
        private final PolicyRepository policyRepository;
        private final AddressRepository addressRepository;
        private final CartRepository cartRepository;
        private final CartItemRepository cartItemRepository;
        private final OrderRepository orderRepository;
        private final OrderItemRepository orderItemRepository;
        private final PrescriptionRepository prescriptionRepository;
        private final ShipmentRepository shipmentRepository;
        private final FulfillmentTaskRepository fulfillmentTaskRepository;
        private final FeedbackRepository feedbackRepository;
        private final AfterSalesCaseRepository afterSalesCaseRepository;
        private final AuditLogRepository auditLogRepository;
        private final PasswordEncoder passwordEncoder;

        @Override
        public void run(String... args) {
                if (accountRepository.count() > 0) {
                        log.info("=== Data already seeded, skipping ===");
                        return;
                }

                log.info("=== Starting Data Seeder ===");

                List<Account> accounts = seedAccounts();
                List<Category> categories = seedCategories();
                List<ProductVariant> variants = seedProducts(categories);
                seedPromotions();
                InventoryLocation warehouse = seedInventory(variants);
                seedPolicies();
                seedAddresses(accounts);

                seedCartsAndItems(accounts, variants);
                List<Order> orders = seedOrders(accounts, variants);
                seedPrescriptions(orders, accounts);
                seedShipments(orders);
                seedFulfillmentTasks(orders, accounts);
                seedFeedbacks(orders, accounts);
                seedAfterSalesCases(orders, accounts);
                seedAuditLogs(orders, accounts);

                log.info("=== Data Seeding Completed! ===");
        }

        private List<Account> seedAccounts() {
                log.info("Seeding Accounts & Customers...");

                Account admin = accountRepository.save(Account.builder()
                                .email("admin@eyecarehub.com")
                                .passwordHash(passwordEncoder.encode("admin123"))
                                .phoneNumber("0900000001")
                                .role(Account.AccountRole.ADMIN)
                                .build());
                customerRepository.save(Customer.builder()
                                .account(admin).firstName("Admin").lastName("EyeCareHub")
                                .gender(Customer.Gender.MALE).build());

                Account staff1 = accountRepository.save(Account.builder()
                                .email("staff1@eyecarehub.com")
                                .passwordHash(passwordEncoder.encode("staff123"))
                                .phoneNumber("0900000002")
                                .role(Account.AccountRole.STAFF)
                                .build());
                customerRepository.save(Customer.builder()
                                .account(staff1).firstName("Minh").lastName("Nguyen")
                                .gender(Customer.Gender.MALE).build());

                Account staff2 = accountRepository.save(Account.builder()
                                .email("staff2@eyecarehub.com")
                                .passwordHash(passwordEncoder.encode("staff123"))
                                .phoneNumber("0900000003")
                                .role(Account.AccountRole.STAFF)
                                .build());
                customerRepository.save(Customer.builder()
                                .account(staff2).firstName("Lan").lastName("Tran")
                                .gender(Customer.Gender.FEMALE).build());

                Account cust1 = accountRepository.save(Account.builder()
                                .email("customer1@gmail.com")
                                .passwordHash(passwordEncoder.encode("customer123"))
                                .phoneNumber("0901234567")
                                .role(Account.AccountRole.CUSTOMER)
                                .build());
                customerRepository.save(Customer.builder()
                                .account(cust1).firstName("An").lastName("Nguyen Van")
                                .gender(Customer.Gender.MALE).dateOfBirth(LocalDate.of(1995, 5, 15)).build());

                Account cust2 = accountRepository.save(Account.builder()
                                .email("customer2@gmail.com")
                                .passwordHash(passwordEncoder.encode("customer123"))
                                .phoneNumber("0912345678")
                                .role(Account.AccountRole.CUSTOMER)
                                .build());
                customerRepository.save(Customer.builder()
                                .account(cust2).firstName("Binh").lastName("Tran Thi")
                                .gender(Customer.Gender.FEMALE).dateOfBirth(LocalDate.of(1998, 8, 20)).build());

                Account cust3 = accountRepository.save(Account.builder()
                                .email("customer3@gmail.com")
                                .passwordHash(passwordEncoder.encode("customer123"))
                                .phoneNumber("0923456789")
                                .role(Account.AccountRole.CUSTOMER)
                                .build());
                customerRepository.save(Customer.builder()
                                .account(cust3).firstName("Cuong").lastName("Le")
                                .gender(Customer.Gender.MALE).dateOfBirth(LocalDate.of(2000, 3, 10)).build());

                log.info("  -> 6 accounts (1 admin, 2 staff, 3 customers)");
                return List.of(admin, staff1, staff2, cust1, cust2, cust3);
        }

        private List<Category> seedCategories() {
                log.info("Seeding Categories...");

                Category sunglasses = categoryRepository.save(Category.builder()
                                .name("Kinh mat").slug("kinh-mat")
                                .description("Kinh mat thoi trang, chong UV").displayOrder(1).build());

                Category prescription = categoryRepository.save(Category.builder()
                                .name("Kinh can").slug("kinh-can")
                                .description("Kinh can vien loan da trong").displayOrder(2).build());

                Category contactLenses = categoryRepository.save(Category.builder()
                                .name("Kinh ap trong").slug("kinh-ap-trong")
                                .description("Kinh ap trong ngay thang mau").displayOrder(3).build());

                Category lenses = categoryRepository.save(Category.builder()
                                .name("Trong kinh").slug("trong-kinh")
                                .description("Trong kinh cat gon theo toa").displayOrder(4).build());

                Category accessories = categoryRepository.save(Category.builder()
                                .name("Phu kien").slug("phu-kien")
                                .description("Hop kinh, khan lau, nuoc rua").displayOrder(5).build());

                categoryRepository.save(Category.builder()
                                .name("Kinh mat nam").slug("kinh-mat-nam").parent(sunglasses).displayOrder(1).build());
                categoryRepository.save(Category.builder()
                                .name("Kinh mat nu").slug("kinh-mat-nu").parent(sunglasses).displayOrder(2).build());
                categoryRepository.save(Category.builder()
                                .name("Gong kinh").slug("gong-kinh").parent(prescription).displayOrder(1).build());

                log.info("  -> 8 categories");
                return List.of(sunglasses, prescription, contactLenses, lenses, accessories);
        }

        private List<ProductVariant> seedProducts(List<Category> categories) {
                log.info("Seeding Products, Variants & Media...");

                Category sunglasses = categories.get(0);
                Category prescription = categories.get(1);
                Category contactLenses = categories.get(2);
                Category lenses = categories.get(3);
                Category accessories = categories.get(4);

                Product p1 = productRepository.save(Product.builder()
                                .name("Ray-Ban Aviator Classic").slug("ray-ban-aviator-classic").sku("RB-AVI")
                                .category(sunglasses).brand("Ray-Ban")
                                .shortDescription("Kinh mat phi cong huyen thoai")
                                .fullDescription("Ray-Ban Aviator mang tinh bieu tuong, trong G-15 chong UV 100%.")
                                .basePrice(new BigDecimal("3200000")).salePrice(new BigDecimal("2890000"))
                                .isFeatured(true).build());

                ProductVariant v1 = createVariant(p1, "RB-AVI-GOLD-M", "Gold", "M", null, null, null, BigDecimal.ZERO,
                                50);
                ProductVariant v2 = createVariant(p1, "RB-AVI-SILVER-M", "Silver", "M", null, null, null,
                                BigDecimal.ZERO, 30);
                ProductVariant v3 = createVariant(p1, "RB-AVI-BLACK-L", "Black", "L", null, null, null,
                                new BigDecimal("100000"), 20);
                createMedia(p1, "https://placehold.co/800x600?text=RB+Aviator+Main", true);

                Product p2 = productRepository.save(Product.builder()
                                .name("Oakley Holbrook XL").slug("oakley-holbrook-xl").sku("OK-HBK")
                                .category(sunglasses).brand("Oakley")
                                .shortDescription("Kinh mat the thao phong cach")
                                .fullDescription("Oakley Holbrook XL trong Prizm tang cuong mau sac.")
                                .basePrice(new BigDecimal("4100000")).isFeatured(true).build());

                ProductVariant v4 = createVariant(p2, "OK-HBK-MBLK-STD", "Matte Black", "Standard", null, null, null,
                                BigDecimal.ZERO, 25);
                ProductVariant v5 = createVariant(p2, "OK-HBK-TORT-STD", "Tortoise", "Standard", null, null, null,
                                BigDecimal.ZERO, 15);
                createMedia(p2, "https://placehold.co/800x600?text=Oakley+Main", true);

                Product p3 = productRepository.save(Product.builder()
                                .name("Ray-Ban Clubmaster Optics").slug("ray-ban-clubmaster-optics").sku("RB-CLB")
                                .category(prescription).brand("Ray-Ban")
                                .shortDescription("Gong kinh Clubmaster vintage")
                                .fullDescription("Gong kinh retro, acetate + kim loai.")
                                .basePrice(new BigDecimal("2500000")).salePrice(new BigDecimal("2200000"))
                                .isFeatured(true).build());

                ProductVariant v6 = createVariant(p3, "RB-CLB-BLK-49", "Black", "49mm", "Acetate", null, "Browline",
                                BigDecimal.ZERO, 40);
                ProductVariant v7 = createVariant(p3, "RB-CLB-TORT-51", "Tortoise", "51mm", "Acetate", null, "Browline",
                                new BigDecimal("50000"), 35);
                createMedia(p3, "https://placehold.co/800x600?text=Clubmaster+Main", true);

                Product p4 = productRepository.save(Product.builder()
                                .name("Gucci GG0061OA").slug("gucci-gg0061oa").sku("GC-061")
                                .category(prescription).brand("Gucci")
                                .shortDescription("Gong kinh cat eye sang trong")
                                .fullDescription("Gucci GG0061OA cat-eye nu tinh, acetate cao cap.")
                                .basePrice(new BigDecimal("6500000")).build());

                ProductVariant v8 = createVariant(p4, "GC-061-BLK-53", "Black", "53mm", "Acetate", null, "Cat Eye",
                                BigDecimal.ZERO, 15);
                createMedia(p4, "https://placehold.co/800x600?text=Gucci+Main", true);

                Product p5 = productRepository.save(Product.builder()
                                .name("Trong Essilor Crizal Sapphire UV").slug("trong-essilor-crizal-sapphire-uv")
                                .sku("ESS-CRZ")
                                .category(lenses).brand("Essilor")
                                .shortDescription("Trong kinh chong choi chong UV")
                                .fullDescription("Essilor Crizal Sapphire UV giam choi 95%, chong bui, chong xuoc.")
                                .basePrice(new BigDecimal("1500000")).salePrice(new BigDecimal("1350000")).build());

                ProductVariant v9 = createVariant(p5, "ESS-CRZ-150", "Clear", "1.50", null, "Crizal Sapphire UV", null,
                                BigDecimal.ZERO, 100);
                ProductVariant v10 = createVariant(p5, "ESS-CRZ-160", "Clear", "1.60", null, "Crizal Sapphire UV", null,
                                new BigDecimal("200000"), 80);
                ProductVariant v11 = createVariant(p5, "ESS-CRZ-167", "Clear", "1.67", null, "Crizal Sapphire UV", null,
                                new BigDecimal("500000"), 50);
                createMedia(p5, "https://placehold.co/800x600?text=Essilor+Main", true);

                Product p6 = productRepository.save(Product.builder()
                                .name("Trong Zeiss BlueProtect").slug("trong-zeiss-blueprotect").sku("ZS-BPR")
                                .category(lenses).brand("Zeiss")
                                .shortDescription("Trong chong anh sang xanh")
                                .fullDescription("Zeiss BlueProtect loc 40% anh sang xanh tu man hinh.")
                                .basePrice(new BigDecimal("2000000")).build());

                ProductVariant v12 = createVariant(p6, "ZS-BPR-150", "Blue Coating", "1.50", null, "BlueProtect", null,
                                BigDecimal.ZERO, 60);
                createMedia(p6, "https://placehold.co/800x600?text=Zeiss+Main", true);

                Product p7 = productRepository.save(Product.builder()
                                .name("Acuvue Oasys 1-Day").slug("acuvue-oasys-1-day").sku("ACV-OAS")
                                .category(contactLenses).brand("Acuvue")
                                .shortDescription("Kinh ap trong ngay HydraLuxe")
                                .fullDescription("Acuvue Oasys 1-Day HydraLuxe giu am ca ngay, chong UV.")
                                .basePrice(new BigDecimal("750000")).build());

                ProductVariant v13 = createVariant(p7, "ACV-OAS-200", "Clear", "-2.00", null, null, null,
                                BigDecimal.ZERO, 40);
                ProductVariant v14 = createVariant(p7, "ACV-OAS-300", "Clear", "-3.00", null, null, null,
                                BigDecimal.ZERO, 40);
                createMedia(p7, "https://placehold.co/800x600?text=Acuvue+Main", true);

                Product p8 = productRepository.save(Product.builder()
                                .name("Hop Kinh Da Cao Cap").slug("hop-kinh-da-cao-cap").sku("ACC-BOX")
                                .category(accessories).brand("EyeCareHub")
                                .shortDescription("Hop dung kinh da PU cao cap")
                                .fullDescription("Hop kinh da PU lot nhung mem, nhieu mau.")
                                .basePrice(new BigDecimal("150000")).salePrice(new BigDecimal("120000")).build());

                ProductVariant v15 = createVariant(p8, "ACC-BOX-BLK", "Black", "Standard", null, null, null,
                                BigDecimal.ZERO, 200);
                ProductVariant v16 = createVariant(p8, "ACC-BOX-BRN", "Brown", "Standard", null, null, null,
                                BigDecimal.ZERO, 150);
                createMedia(p8, "https://placehold.co/800x600?text=Box+Main", true);

                Product p9 = productRepository.save(Product.builder()
                                .name("Nuoc Rua Kinh EyeClean Pro").slug("nuoc-rua-kinh-eyeclean-pro").sku("ACC-CLN")
                                .category(accessories).brand("EyeClean")
                                .shortDescription("Nuoc rua kinh an toan cho lop phu")
                                .fullDescription("EyeClean Pro an toan cho trong co lop phu.")
                                .basePrice(new BigDecimal("85000")).build());

                ProductVariant v17 = createVariant(p9, "ACC-CLN-60ML", "Blue", "60ml", null, null, null,
                                BigDecimal.ZERO, 300);
                createMedia(p9, "https://placehold.co/800x600?text=EyeClean+Main", true);

                Product p10 = productRepository.save(Product.builder()
                                .name("Gong Kinh Titan Silhouette").slug("gong-kinh-titan-silhouette").sku("SH-TIT")
                                .category(prescription).brand("Silhouette")
                                .shortDescription("Gong titan sieu nhe 1.8 gram")
                                .fullDescription("Silhouette Titan Minimal Art, khong oc vit, Made in Austria.")
                                .basePrice(new BigDecimal("8500000")).isFeatured(true).build());

                ProductVariant v18 = createVariant(p10, "SH-TIT-GUN-52", "Gunmetal", "52mm", "Titan", null, "Rimless",
                                BigDecimal.ZERO, 10);
                createMedia(p10, "https://placehold.co/800x600?text=Silhouette+Main", true);

                log.info("  -> 10 products, 18 variants, 10 media");
                return List.of(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18);
        }

        private ProductVariant createVariant(Product product, String sku, String color, String size,
                        String material, String lensType, String frameShape, BigDecimal additionalPrice, int stock) {
                return productVariantRepository.save(ProductVariant.builder()
                                .product(product).sku(sku).color(color).size(size)
                                .material(material).lensType(lensType).frameShape(frameShape)
                                .additionalPrice(additionalPrice).stockQuantity(stock)
                                .imageUrl("https://placehold.co/600x400?text=" + sku)
                                .build());
        }

        private void createMedia(Product product, String url, boolean isPrimary) {
                productMediaRepository.save(ProductMedia.builder()
                                .product(product).url(url).altText(product.getName()).isPrimary(isPrimary).build());
        }

        private void seedPromotions() {
                log.info("Seeding Promotions...");

                promotionRepository.save(Promotion.builder()
                                .code("WELCOME10").name("Chao mung thanh vien moi")
                                .description("Giam 10% don dau tien, toi da 200k")
                                .discountType(Promotion.DiscountType.PERCENTAGE).discountValue(new BigDecimal("10"))
                                .maximumDiscount(new BigDecimal("200000")).minimumOrderValue(new BigDecimal("500000"))
                                .startDate(LocalDateTime.now().minusDays(30)).endDate(LocalDateTime.now().plusDays(365))
                                .usageLimit(1000).isActive(true).build());

                promotionRepository.save(Promotion.builder()
                                .code("EYECARE20").name("Giam 20% kinh mat")
                                .description("Giam 20% tat ca kinh mat, toi da 500k")
                                .discountType(Promotion.DiscountType.PERCENTAGE).discountValue(new BigDecimal("20"))
                                .maximumDiscount(new BigDecimal("500000")).minimumOrderValue(new BigDecimal("1000000"))
                                .startDate(LocalDateTime.now().minusDays(7)).endDate(LocalDateTime.now().plusDays(90))
                                .usageLimit(500).isActive(true).build());

                promotionRepository.save(Promotion.builder()
                                .code("FREESHIP").name("Mien phi van chuyen")
                                .description("Free ship don tu 300k")
                                .discountType(Promotion.DiscountType.FREE_SHIPPING).discountValue(BigDecimal.ZERO)
                                .minimumOrderValue(new BigDecimal("300000"))
                                .startDate(LocalDateTime.now().minusDays(30)).endDate(LocalDateTime.now().plusDays(180))
                                .usageLimit(0).isActive(true).build());

                promotionRepository.save(Promotion.builder()
                                .code("FLAT100K").name("Giam 100.000d")
                                .description("Giam truc tiep 100k cho don tu 800k")
                                .discountType(Promotion.DiscountType.FIXED_AMOUNT)
                                .discountValue(new BigDecimal("100000"))
                                .minimumOrderValue(new BigDecimal("800000"))
                                .startDate(LocalDateTime.now().minusDays(5)).endDate(LocalDateTime.now().plusDays(60))
                                .usageLimit(200).isActive(true).build());

                promotionRepository.save(Promotion.builder()
                                .code("EXPIRED01").name("Ma da het han")
                                .description("Ma cu da het han")
                                .discountType(Promotion.DiscountType.PERCENTAGE).discountValue(new BigDecimal("15"))
                                .startDate(LocalDateTime.now().minusDays(60)).endDate(LocalDateTime.now().minusDays(1))
                                .usageLimit(100).isActive(false).build());

                log.info("  -> 5 promotions (4 active + 1 expired)");
        }

        private InventoryLocation seedInventory(List<ProductVariant> variants) {
                log.info("Seeding Inventory...");

                InventoryLocation warehouse = locationRepository.save(InventoryLocation.builder()
                                .code("WH-HCM-01").name("Kho chinh HCM")
                                .address("456 Ly Thuong Kiet, Q10, TP.HCM")
                                .type(InventoryLocation.LocationType.WAREHOUSE).build());
                locationRepository.save(InventoryLocation.builder()
                                .code("STORE-Q1").name("Cua hang Quan 1")
                                .address("123 Nguyen Hue, Q1, TP.HCM").type(InventoryLocation.LocationType.STORE)
                                .build());
                locationRepository.save(InventoryLocation.builder()
                                .code("STORE-Q7").name("Cua hang Quan 7")
                                .address("789 Nguyen Thi Thap, Q7, TP.HCM").type(InventoryLocation.LocationType.STORE)
                                .build());
                locationRepository.save(InventoryLocation.builder()
                                .code("SUP-ESS").name("NCC Essilor Viet Nam")
                                .address("100 Dien Bien Phu, Q3, TP.HCM").type(InventoryLocation.LocationType.SUPPLIER)
                                .build());

                for (ProductVariant v : variants) {
                        stockRepository.save(InventoryStock.builder()
                                        .productVariant(v).location(warehouse)
                                        .onHandQuantity(v.getStockQuantity()).reservedQuantity(0).build());
                }

                log.info("  -> 4 locations, " + variants.size() + " stock records");
                return warehouse;
        }

        private void seedPolicies() {
                log.info("Seeding Policies...");

                policyRepository.save(Policy.builder().type(Policy.PolicyType.RETURN_POLICY)
                                .title("Chinh sach doi tra").slug("chinh-sach-doi-tra")
                                .content("1. Doi tra trong 7 ngay.\n2. Con nguyen tem mac.\n3. Trong cat theo toa khong doi tra.")
                                .isPublished(true).displayOrder(1).publishedAt(LocalDateTime.now()).build());

                policyRepository.save(Policy.builder().type(Policy.PolicyType.WARRANTY_POLICY)
                                .title("Chinh sach bao hanh").slug("chinh-sach-bao-hanh")
                                .content("1. Gong kinh BH 12 thang.\n2. Trong kinh BH 6 thang loi ky thuat.\n3. Khong BH roi vo.")
                                .isPublished(true).displayOrder(2).publishedAt(LocalDateTime.now()).build());

                policyRepository.save(Policy.builder().type(Policy.PolicyType.SHIPPING_POLICY)
                                .title("Chinh sach van chuyen").slug("chinh-sach-van-chuyen")
                                .content("1. Giao hang toan quoc GHN/GHTK.\n2. Noi thanh HCM 1-2 ngay.\n3. Free ship don tu 300k.")
                                .isPublished(true).displayOrder(3).publishedAt(LocalDateTime.now()).build());

                policyRepository.save(Policy.builder().type(Policy.PolicyType.PAYMENT_POLICY)
                                .title("Chinh sach thanh toan").slug("chinh-sach-thanh-toan")
                                .content("1. COD.\n2. Chuyen khoan.\n3. Vi MoMo, ZaloPay, VNPay.\n4. Tra gop 0%.")
                                .isPublished(true).displayOrder(4).publishedAt(LocalDateTime.now()).build());

                policyRepository.save(Policy.builder().type(Policy.PolicyType.PRIVACY_POLICY)
                                .title("Chinh sach bao mat").slug("chinh-sach-bao-mat")
                                .content("Cam ket bao mat thong tin ca nhan khach hang.")
                                .isPublished(true).displayOrder(5).publishedAt(LocalDateTime.now()).build());

                policyRepository.save(Policy.builder().type(Policy.PolicyType.ABOUT_US)
                                .title("Ve chung toi").slug("ve-chung-toi")
                                .content("EyeCareHub - Chuoi cua hang kinh mat uy tin voi 10 nam kinh nghiem.")
                                .isPublished(true).displayOrder(6).publishedAt(LocalDateTime.now()).build());

                log.info("  -> 6 policies");
        }

        private void seedAddresses(List<Account> accounts) {
                log.info("Seeding Addresses...");

                Customer cust1 = customerRepository.findByAccountId(accounts.get(3).getId()).orElse(null);
                if (cust1 != null) {
                        addressRepository.save(Address.builder().customer(cust1)
                                        .recipientName("Nguyen Van An").phoneNumber("0901234567")
                                        .addressLine1("123 Nguyen Hue").district("Quan 1").ward("Ben Nghe")
                                        .city("Ho Chi Minh").province("Ho Chi Minh").postalCode("700000")
                                        .isDefault(true).type(Address.AddressType.HOME).build());
                        addressRepository.save(Address.builder().customer(cust1)
                                        .recipientName("Nguyen Van An").phoneNumber("0901234567")
                                        .addressLine1("456 Le Loi").addressLine2("Tang 5 toa ABC").district("Quan 1")
                                        .city("Ho Chi Minh").province("Ho Chi Minh").postalCode("700000")
                                        .type(Address.AddressType.OFFICE).build());
                }

                Customer cust2 = customerRepository.findByAccountId(accounts.get(4).getId()).orElse(null);
                if (cust2 != null) {
                        addressRepository.save(Address.builder().customer(cust2)
                                        .recipientName("Tran Thi Binh").phoneNumber("0912345678")
                                        .addressLine1("789 Pham Van Dong").district("Thu Duc")
                                        .city("Ho Chi Minh").province("Ho Chi Minh").postalCode("700000")
                                        .isDefault(true).type(Address.AddressType.HOME).build());
                }

                Customer cust3 = customerRepository.findByAccountId(accounts.get(5).getId()).orElse(null);
                if (cust3 != null) {
                        addressRepository.save(Address.builder().customer(cust3)
                                        .recipientName("Le Cuong").phoneNumber("0923456789")
                                        .addressLine1("100 Tran Hung Dao").district("Hoan Kiem")
                                        .city("Ha Noi").province("Ha Noi").postalCode("100000")
                                        .isDefault(true).type(Address.AddressType.HOME).build());
                }

                log.info("  -> 4 addresses");
        }

        private void seedCartsAndItems(List<Account> accounts, List<ProductVariant> variants) {
                log.info("Seeding Carts & CartItems...");

                Account cust2 = accounts.get(4);
                Account cust3 = accounts.get(5);

                Cart cart2 = cartRepository.save(Cart.builder().account(cust2).build());
                cartItemRepository.save(CartItem.builder()
                                .cart(cart2).productVariant(variants.get(0))
                                .quantity(1).snapshotPrice(new BigDecimal("2890000"))
                                .snapshotProductName("Ray-Ban Aviator Classic - Gold").build());
                cartItemRepository.save(CartItem.builder()
                                .cart(cart2).productVariant(variants.get(14))
                                .quantity(2).snapshotPrice(new BigDecimal("120000"))
                                .snapshotProductName("Hop Kinh Da Cao Cap - Black").build());

                Cart cart3 = cartRepository.save(Cart.builder().account(cust3).build());
                cartItemRepository.save(CartItem.builder()
                                .cart(cart3).productVariant(variants.get(3))
                                .quantity(1).snapshotPrice(new BigDecimal("4100000"))
                                .snapshotProductName("Oakley Holbrook XL - Matte Black").build());

                log.info("  -> 2 carts, 3 cart items");
        }

        private List<Order> seedOrders(List<Account> accounts, List<ProductVariant> variants) {
                log.info("Seeding Orders & OrderItems...");

                Account cust1 = accounts.get(3);
                Account cust2 = accounts.get(4);
                Account cust3 = accounts.get(5);

                Order order1 = orderRepository.save(Order.builder()
                                .orderNumber("ORD-20260227-001").account(cust1)
                                .orderType(Order.OrderType.IN_STOCK)
                                .subtotalPrice(new BigDecimal("6990000"))
                                .discountAmount(new BigDecimal("200000"))
                                .shippingFee(BigDecimal.ZERO)
                                .totalPrice(new BigDecimal("6790000"))
                                .promotionCode("WELCOME10")
                                .status(Order.OrderStatus.DELIVERED)
                                .paymentMethod("COD").paymentStatus(Order.PaymentStatus.PAID)
                                .paidAt(LocalDateTime.now().minusDays(5))
                                .shippingAddress("123 Nguyen Hue, Q1, HCM").phoneNumber("0901234567")
                                .build());

                OrderItem oi1 = orderItemRepository.save(OrderItem.builder()
                                .order(order1).productVariant(variants.get(0))
                                .quantity(1).price(new BigDecimal("2890000")).build());
                OrderItem oi2 = orderItemRepository.save(OrderItem.builder()
                                .order(order1).productVariant(variants.get(3))
                                .quantity(1).price(new BigDecimal("4100000")).build());

                Order order2 = orderRepository.save(Order.builder()
                                .orderNumber("ORD-20260227-002").account(cust1)
                                .orderType(Order.OrderType.PRESCRIPTION)
                                .subtotalPrice(new BigDecimal("3550000"))
                                .discountAmount(BigDecimal.ZERO)
                                .shippingFee(new BigDecimal("30000"))
                                .totalPrice(new BigDecimal("3580000"))
                                .status(Order.OrderStatus.PROCESSING)
                                .paymentMethod("BANKING").paymentStatus(Order.PaymentStatus.PAID)
                                .paidAt(LocalDateTime.now().minusDays(3))
                                .shippingAddress("123 Nguyen Hue, Q1, HCM").phoneNumber("0901234567")
                                .build());

                OrderItem oi3 = orderItemRepository.save(OrderItem.builder()
                                .order(order2).productVariant(variants.get(5))
                                .quantity(1).price(new BigDecimal("2200000")).build());
                OrderItem oi4 = orderItemRepository.save(OrderItem.builder()
                                .order(order2).productVariant(variants.get(8))
                                .quantity(1).price(new BigDecimal("1350000")).build());

                Order order3 = orderRepository.save(Order.builder()
                                .orderNumber("ORD-20260228-003").account(cust2)
                                .orderType(Order.OrderType.IN_STOCK)
                                .subtotalPrice(new BigDecimal("4100000"))
                                .discountAmount(new BigDecimal("100000"))
                                .shippingFee(BigDecimal.ZERO)
                                .totalPrice(new BigDecimal("4000000"))
                                .promotionCode("FLAT100K")
                                .status(Order.OrderStatus.CONFIRMED)
                                .paymentMethod("MOMO").paymentStatus(Order.PaymentStatus.PAID)
                                .paidAt(LocalDateTime.now().minusDays(1))
                                .shippingAddress("789 Pham Van Dong, Thu Duc, HCM").phoneNumber("0912345678")
                                .build());

                OrderItem oi5 = orderItemRepository.save(OrderItem.builder()
                                .order(order3).productVariant(variants.get(3))
                                .quantity(1).price(new BigDecimal("4100000")).build());

                Order order4 = orderRepository.save(Order.builder()
                                .orderNumber("ORD-20260228-004").account(cust3)
                                .orderType(Order.OrderType.IN_STOCK)
                                .subtotalPrice(new BigDecimal("270000"))
                                .discountAmount(BigDecimal.ZERO)
                                .shippingFee(new BigDecimal("30000"))
                                .totalPrice(new BigDecimal("300000"))
                                .status(Order.OrderStatus.PENDING)
                                .paymentMethod("COD").paymentStatus(Order.PaymentStatus.UNPAID)
                                .shippingAddress("100 Tran Hung Dao, Hoan Kiem, Ha Noi").phoneNumber("0923456789")
                                .build());

                OrderItem oi6 = orderItemRepository.save(OrderItem.builder()
                                .order(order4).productVariant(variants.get(14))
                                .quantity(1).price(new BigDecimal("120000")).build());
                OrderItem oi7 = orderItemRepository.save(OrderItem.builder()
                                .order(order4).productVariant(variants.get(16))
                                .quantity(1).price(new BigDecimal("85000")).build());
                orderItemRepository.save(OrderItem.builder()
                                .order(order4).productVariant(variants.get(15))
                                .quantity(1).price(new BigDecimal("120000")).build());

                Order order5 = orderRepository.save(Order.builder()
                                .orderNumber("ORD-20260228-005").account(cust2)
                                .orderType(Order.OrderType.IN_STOCK)
                                .subtotalPrice(new BigDecimal("750000"))
                                .discountAmount(BigDecimal.ZERO)
                                .shippingFee(BigDecimal.ZERO)
                                .totalPrice(new BigDecimal("750000"))
                                .status(Order.OrderStatus.CANCELLED)
                                .paymentMethod("COD").paymentStatus(Order.PaymentStatus.UNPAID)
                                .shippingAddress("789 Pham Van Dong, Thu Duc, HCM").phoneNumber("0912345678")
                                .build());

                orderItemRepository.save(OrderItem.builder()
                                .order(order5).productVariant(variants.get(12))
                                .quantity(1).price(new BigDecimal("750000")).build());

                log.info("  -> 5 orders, 9 order items");
                log.info("     Order1: DELIVERED (cust1), Order2: PROCESSING/PRESCRIPTION (cust1)");
                log.info("     Order3: CONFIRMED (cust2), Order4: PENDING (cust3), Order5: CANCELLED (cust2)");

                order1.getOrderItems().addAll(List.of(oi1, oi2));
                order2.getOrderItems().addAll(List.of(oi3, oi4));
                order3.getOrderItems().addAll(List.of(oi5));
                order4.getOrderItems().addAll(List.of(oi6, oi7));

                return List.of(order1, order2, order3, order4, order5);
        }

        private void seedPrescriptions(List<Order> orders, List<Account> accounts) {
                log.info("Seeding Prescriptions...");

                Order prescOrder = orders.get(1);
                Account admin = accounts.get(0);

                if (!prescOrder.getOrderItems().isEmpty()) {
                        OrderItem lensItem = prescOrder.getOrderItems().get(1);
                        prescriptionRepository.save(Prescription.builder()
                                        .orderItem(lensItem)
                                        .customerName("Nguyen Van An")
                                        .rightSphere("-2.50").rightCylinder("-0.75").rightAxis("90").rightPD("31.5")
                                        .leftSphere("-3.00").leftCylinder("-1.00").leftAxis("180").leftPD("32.0")
                                        .addPower("+2.00")
                                        .notes("Can nhin xa bi mo, lam viec nhieu voi may tinh")
                                        .status(Prescription.PrescriptionStatus.VERIFIED)
                                        .verifiedByStaff(admin).verifiedAt(LocalDateTime.now().minusDays(2))
                                        .build());

                        OrderItem frameItem = prescOrder.getOrderItems().get(0);
                        prescriptionRepository.save(Prescription.builder()
                                        .orderItem(frameItem)
                                        .customerName("Nguyen Van An")
                                        .prescriptionImageUrl("https://placehold.co/400x300?text=Toa+kinh")
                                        .notes("Gui anh toa, nho shop nhap dum")
                                        .status(Prescription.PrescriptionStatus.PENDING)
                                        .build());
                }

                log.info("  -> 2 prescriptions (1 VERIFIED, 1 PENDING with image)");
        }

        private void seedShipments(List<Order> orders) {
                log.info("Seeding Shipments...");

                Order deliveredOrder = orders.get(0);
                shipmentRepository.save(Shipment.builder()
                                .order(deliveredOrder)
                                .carrier("GHN").trackingNumber("GHN20260227001").shippingMethod("EXPRESS")
                                .shippingFee(new BigDecimal("30000"))
                                .estimatedDeliveryDate(LocalDate.now().minusDays(3))
                                .actualDeliveryDate(LocalDate.now().minusDays(4))
                                .status(Shipment.ShipmentStatus.DELIVERED)
                                .recipientName("Nguyen Van An").recipientPhone("0901234567")
                                .shippingAddress("123 Nguyen Hue, Q1, HCM")
                                .build());

                Order confirmedOrder = orders.get(2);
                shipmentRepository.save(Shipment.builder()
                                .order(confirmedOrder)
                                .carrier("GHTK").trackingNumber("GHTK20260228003").shippingMethod("STANDARD")
                                .shippingFee(new BigDecimal("25000"))
                                .estimatedDeliveryDate(LocalDate.now().plusDays(3))
                                .status(Shipment.ShipmentStatus.PENDING)
                                .recipientName("Tran Thi Binh").recipientPhone("0912345678")
                                .shippingAddress("789 Pham Van Dong, Thu Duc, HCM")
                                .build());

                log.info("  -> 2 shipments (1 DELIVERED, 1 PENDING)");
        }

        private void seedFulfillmentTasks(List<Order> orders, List<Account> accounts) {
                log.info("Seeding FulfillmentTasks...");

                Account staff1 = accounts.get(1);
                Account staff2 = accounts.get(2);

                Order deliveredOrder = orders.get(0);
                fulfillmentTaskRepository.save(FulfillmentTask.builder()
                                .order(deliveredOrder).taskType(FulfillmentTask.TaskType.QC)
                                .assignee(staff2).status(FulfillmentTask.TaskStatus.DONE).displayOrder(0)
                                .startedAt(LocalDateTime.now().minusDays(6))
                                .completedAt(LocalDateTime.now().minusDays(6)).build());
                fulfillmentTaskRepository.save(FulfillmentTask.builder()
                                .order(deliveredOrder).taskType(FulfillmentTask.TaskType.PACK)
                                .assignee(staff1).status(FulfillmentTask.TaskStatus.DONE).displayOrder(1)
                                .startedAt(LocalDateTime.now().minusDays(5))
                                .completedAt(LocalDateTime.now().minusDays(5)).build());
                fulfillmentTaskRepository.save(FulfillmentTask.builder()
                                .order(deliveredOrder).taskType(FulfillmentTask.TaskType.SHIP)
                                .assignee(staff1).status(FulfillmentTask.TaskStatus.DONE).displayOrder(2)
                                .startedAt(LocalDateTime.now().minusDays(5))
                                .completedAt(LocalDateTime.now().minusDays(4)).build());

                Order prescOrder = orders.get(1);
                fulfillmentTaskRepository.save(FulfillmentTask.builder()
                                .order(prescOrder).taskType(FulfillmentTask.TaskType.CUT_LENS)
                                .assignee(staff1).status(FulfillmentTask.TaskStatus.IN_PROGRESS).displayOrder(0)
                                .startedAt(LocalDateTime.now().minusDays(1)).build());
                fulfillmentTaskRepository.save(FulfillmentTask.builder()
                                .order(prescOrder).taskType(FulfillmentTask.TaskType.ASSEMBLE)
                                .status(FulfillmentTask.TaskStatus.TODO).displayOrder(1).build());
                fulfillmentTaskRepository.save(FulfillmentTask.builder()
                                .order(prescOrder).taskType(FulfillmentTask.TaskType.QC)
                                .status(FulfillmentTask.TaskStatus.TODO).displayOrder(2).build());
                fulfillmentTaskRepository.save(FulfillmentTask.builder()
                                .order(prescOrder).taskType(FulfillmentTask.TaskType.PACK)
                                .status(FulfillmentTask.TaskStatus.TODO).displayOrder(3).build());
                fulfillmentTaskRepository.save(FulfillmentTask.builder()
                                .order(prescOrder).taskType(FulfillmentTask.TaskType.SHIP)
                                .status(FulfillmentTask.TaskStatus.TODO).displayOrder(4).build());

                log.info("  -> 8 tasks (3 DONE for delivered, 5 for prescription: 1 IN_PROGRESS + 4 TODO)");
        }

        private void seedFeedbacks(List<Order> orders, List<Account> accounts) {
                log.info("Seeding Feedbacks...");

                Order deliveredOrder = orders.get(0);
                Account cust1 = accounts.get(3);
                Account admin = accounts.get(0);

                if (!deliveredOrder.getOrderItems().isEmpty()) {
                        feedbackRepository.save(Feedback.builder()
                                        .order(deliveredOrder)
                                        .orderItem(deliveredOrder.getOrderItems().get(0))
                                        .account(cust1)
                                        .product(deliveredOrder.getOrderItems().get(0).getProductVariant().getProduct())
                                        .rating(5).title("Rat dep va chat luong")
                                        .comment("Kinh Ray-Ban chinh hang, dep lam. Giao hang nhanh, dong goi can than.")
                                        .isVerifiedPurchase(true)
                                        .status(Feedback.FeedbackStatus.APPROVED)
                                        .staffReply("Cam on ban da ung ho EyeCareHub!")
                                        .staffReplyAt(LocalDateTime.now().minusDays(2))
                                        .repliedByStaff(admin)
                                        .build());

                        feedbackRepository.save(Feedback.builder()
                                        .order(deliveredOrder)
                                        .orderItem(deliveredOrder.getOrderItems().get(1))
                                        .account(cust1)
                                        .product(deliveredOrder.getOrderItems().get(1).getProductVariant().getProduct())
                                        .rating(4).title("Tot nhung hoi chat")
                                        .comment("Oakley chat luong tot, nhung deo hoi chat tai. Can deo quen.")
                                        .isVerifiedPurchase(true)
                                        .status(Feedback.FeedbackStatus.APPROVED)
                                        .build());
                }

                feedbackRepository.save(Feedback.builder()
                                .account(accounts.get(4))
                                .product(deliveredOrder.getOrderItems().get(0).getProductVariant().getProduct())
                                .rating(3).title("Tam duoc")
                                .comment("Kinh binh thuong, khong co gi dac biet.")
                                .isVerifiedPurchase(false)
                                .status(Feedback.FeedbackStatus.PENDING)
                                .build());

                log.info("  -> 3 feedbacks (2 APPROVED verified + 1 PENDING unverified)");
        }

        private void seedAfterSalesCases(List<Order> orders, List<Account> accounts) {
                log.info("Seeding AfterSalesCases...");

                Order deliveredOrder = orders.get(0);
                Account cust1 = accounts.get(3);
                Account staff1 = accounts.get(1);

                afterSalesCaseRepository.save(AfterSalesCase.builder()
                                .order(deliveredOrder).account(cust1)
                                .type(AfterSalesCase.CaseType.WARRANTY)
                                .reason("Gong kinh bi long oc vit")
                                .description("Sau 1 tuan su dung, oc vit ben tai trai bi long. Nho shop bao hanh.")
                                .status(AfterSalesCase.CaseStatus.RESOLVED)
                                .resolution("Da thay oc vit moi va siet chat lai. BH mien phi.")
                                .assignedStaff(staff1)
                                .resolvedAt(LocalDateTime.now().minusDays(1))
                                .build());

                afterSalesCaseRepository.save(AfterSalesCase.builder()
                                .order(deliveredOrder).account(cust1)
                                .type(AfterSalesCase.CaseType.RETURN)
                                .reason("Doi mau kinh")
                                .description("Muon doi tu mau Gold sang mau Silver.")
                                .status(AfterSalesCase.CaseStatus.OPEN)
                                .build());

                log.info("  -> 2 after-sales cases (1 RESOLVED warranty, 1 OPEN return)");
        }

        private void seedAuditLogs(List<Order> orders, List<Account> accounts) {
                log.info("Seeding AuditLogs...");

                Account admin = accounts.get(0);
                Account cust1 = accounts.get(3);

                auditLogRepository.save(AuditLog.builder()
                                .entityType("Order").entityId(orders.get(0).getId())
                                .action(AuditLog.AuditAction.CREATE)
                                .newValue("{\"orderNumber\":\"ORD-20260227-001\",\"status\":\"PENDING\",\"totalPrice\":6790000}")
                                .changedBy(cust1).changedAt(LocalDateTime.now().minusDays(7))
                                .ipAddress("192.168.1.100").build());

                auditLogRepository.save(AuditLog.builder()
                                .entityType("Order").entityId(orders.get(0).getId())
                                .action(AuditLog.AuditAction.STATUS_CHANGE)
                                .oldValue("PENDING").newValue("CONFIRMED")
                                .changedBy(admin).changedAt(LocalDateTime.now().minusDays(6))
                                .ipAddress("10.0.0.1").build());

                auditLogRepository.save(AuditLog.builder()
                                .entityType("Order").entityId(orders.get(0).getId())
                                .action(AuditLog.AuditAction.STATUS_CHANGE)
                                .oldValue("CONFIRMED").newValue("DELIVERED")
                                .changedBy(admin).changedAt(LocalDateTime.now().minusDays(4))
                                .ipAddress("10.0.0.1").build());

                auditLogRepository.save(AuditLog.builder()
                                .entityType("Prescription").entityId(1L)
                                .action(AuditLog.AuditAction.UPDATE)
                                .oldValue("{\"status\":\"PENDING\"}")
                                .newValue("{\"status\":\"VERIFIED\",\"verifiedBy\":\"admin\"}")
                                .changedBy(admin).changedAt(LocalDateTime.now().minusDays(2))
                                .ipAddress("10.0.0.1").build());

                auditLogRepository.save(AuditLog.builder()
                                .entityType("InventoryStock").entityId(1L)
                                .action(AuditLog.AuditAction.UPDATE)
                                .oldValue("{\"onHandQuantity\":50}")
                                .newValue("{\"onHandQuantity\":49}")
                                .changedBy(admin).changedAt(LocalDateTime.now().minusDays(5))
                                .ipAddress("10.0.0.1").build());

                auditLogRepository.save(AuditLog.builder()
                                .entityType("Feedback").entityId(1L)
                                .action(AuditLog.AuditAction.CREATE)
                                .newValue("{\"rating\":5,\"title\":\"Rat dep va chat luong\"}")
                                .changedBy(cust1).changedAt(LocalDateTime.now().minusDays(3))
                                .ipAddress("192.168.1.100").build());

                log.info("  -> 6 audit logs");
        }
}
