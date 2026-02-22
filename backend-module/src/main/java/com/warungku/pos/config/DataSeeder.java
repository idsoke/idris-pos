package com.warungku.pos.config;

import com.warungku.pos.entity.*;
import com.warungku.pos.entity.enums.Role;
import com.warungku.pos.entity.subscription.*;
import com.warungku.pos.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Database seeder for development/testing.
 * Creates initial outlet, users, categories, and products.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataSeeder {
    
    private final OutletRepository outletRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Bean
    CommandLineRunner seedData() {
        return args -> {
            if (planRepository.count() == 0) {
                log.info("Seeding subscription plans...");
                seedPlans();
            }
            
            if (outletRepository.count() == 0) {
                log.info("Seeding initial data...");
                seedOutlets();
                seedUsers();
                seedSubscriptions();
                seedCategories();
                seedProducts();
                log.info("Data seeding completed!");
            } else {
                log.info("Data already exists, skipping seeding.");
            }
        };
    }

    private void seedPlans() {
        Plan starter = Plan.builder()
                .code("STARTER")
                .name("Starter")
                .description("Perfect for small warung or kiosk")
                .monthlyPrice(new BigDecimal("40000"))
                .yearlyPrice(new BigDecimal("400000"))
                .trialDays(7)
                .maxOutlets(1)
                .maxUsers(3)
                .maxProducts(100)
                .maxTransactionsPerMonth(1000)
                .featureReports(true)
                .featureInventory(true)
                .featureMultiOutlet(false)
                .featureApiAccess(false)
                .featureExport(true)
                .featureEmailSupport(true)
                .featurePrioritySupport(false)
                .isActive(true)
                .sortOrder(1)
                .build();

        Plan pro = Plan.builder()
                .code("PRO")
                .name("Pro")
                .description("For growing businesses")
                .monthlyPrice(new BigDecimal("99000"))
                .yearlyPrice(new BigDecimal("990000"))
                .trialDays(7)
                .maxOutlets(3)
                .maxUsers(10)
                .maxProducts(500)
                .maxTransactionsPerMonth(5000)
                .featureReports(true)
                .featureInventory(true)
                .featureMultiOutlet(true)
                .featureApiAccess(false)
                .featureExport(true)
                .featureEmailSupport(true)
                .featurePrioritySupport(true)
                .isActive(true)
                .isPopular(true)
                .sortOrder(2)
                .build();

        Plan business = Plan.builder()
                .code("BUSINESS")
                .name("Business")
                .description("For established businesses")
                .monthlyPrice(new BigDecimal("249000"))
                .yearlyPrice(new BigDecimal("2490000"))
                .trialDays(14)
                .maxOutlets(10)
                .maxUsers(50)
                .maxProducts(null) // Unlimited
                .maxTransactionsPerMonth(null) // Unlimited
                .featureReports(true)
                .featureInventory(true)
                .featureMultiOutlet(true)
                .featureApiAccess(true)
                .featureExport(true)
                .featureEmailSupport(true)
                .featurePrioritySupport(true)
                .isActive(true)
                .sortOrder(3)
                .build();

        planRepository.saveAll(List.of(starter, pro, business));
        log.info("Created 3 subscription plans");
    }

    private void seedSubscriptions() {
        Plan starterPlan = planRepository.findByCode("STARTER").orElse(null);
        if (starterPlan == null) return;

        List<Outlet> outlets = outletRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Outlet outlet : outlets) {
            Subscription subscription = Subscription.builder()
                    .tenantId(outlet.getId())
                    .plan(starterPlan)
                    .status(SubscriptionStatus.TRIAL)
                    .billingCycle(BillingCycle.MONTHLY)
                    .startDate(now)
                    .endDate(now.plusDays(7))
                    .trialEndDate(now.plusDays(7))
                    .price(BigDecimal.ZERO)
                    .autoRenew(true)
                    .outletsUsed(1)
                    .usersUsed(1)
                    .build();
            subscriptionRepository.save(subscription);
        }
        log.info("Created subscriptions for {} outlets", outlets.size());
    }
    
    private void seedOutlets() {
        Outlet outlet1 = Outlet.builder()
                .name("Warungku - Cabang Utama")
                .address("Jl. Sudirman No. 123, Jakarta Pusat")
                .phone("021-12345678")
                .email("main@warungku.com")
                .taxRate(new BigDecimal("0.10"))
                .receiptHeader("Terima kasih telah berbelanja!")
                .receiptFooter("Barang yang sudah dibeli tidak dapat dikembalikan")
                .build();
        
        Outlet outlet2 = Outlet.builder()
                .name("Warungku - Cabang Selatan")
                .address("Jl. Gatot Subroto No. 456, Jakarta Selatan")
                .phone("021-87654321")
                .email("south@warungku.com")
                .taxRate(new BigDecimal("0.10"))
                .build();
        
        outletRepository.saveAll(List.of(outlet1, outlet2));
        log.info("Created 2 outlets");
    }
    
    private void seedUsers() {
        Outlet mainOutlet = outletRepository.findAll().get(0);
        Outlet southOutlet = outletRepository.findAll().get(1);
        
        // SUPERADMIN - Full access, no subscription required
        User superadmin = User.builder()
                .name("Super Admin")
                .email("superadmin@warungku.com")
                .password(passwordEncoder.encode("super123"))
                .phone("08000000000")
                .role(Role.SUPERADMIN)
                .tenantId(mainOutlet.getId())
                .isActive(true)
                .build();
        
        User admin = User.builder()
                .name("Admin User")
                .email("admin@warungku.com")
                .password(passwordEncoder.encode("admin123"))
                .phone("08123456789")
                .role(Role.ADMIN)
                .tenantId(mainOutlet.getId())
                .isActive(true)
                .build();
        
        User cashier1 = User.builder()
                .name("Cashier One")
                .email("cashier1@warungku.com")
                .password(passwordEncoder.encode("cashier123"))
                .phone("08234567890")
                .role(Role.CASHIER)
                .tenantId(mainOutlet.getId())
                .isActive(true)
                .build();
        
        User admin2 = User.builder()
                .name("Admin Cabang Selatan")
                .email("admin.south@warungku.com")
                .password(passwordEncoder.encode("admin123"))
                .phone("08345678901")
                .role(Role.ADMIN)
                .tenantId(southOutlet.getId())
                .isActive(true)
                .build();
        
        userRepository.saveAll(List.of(superadmin, admin, cashier1, admin2));
        log.info("Created 4 users (including SUPERADMIN)");
    }
    
    private void seedCategories() {
        Outlet mainOutlet = outletRepository.findAll().get(0);
        Long tenantId = mainOutlet.getId();
        
        List<Category> categories = List.of(
                createCategory("Makanan", "🍔", 1, tenantId),
                createCategory("Minuman", "🥤", 2, tenantId),
                createCategory("Snack", "🍿", 3, tenantId),
                createCategory("Dessert", "🍰", 4, tenantId)
        );
        
        categoryRepository.saveAll(categories);
        log.info("Created {} categories", categories.size());
    }
    
    private Category createCategory(String name, String icon, int order, Long tenantId) {
        Category cat = Category.builder()
                .name(name)
                .icon(icon)
                .sortOrder(order)
                .isActive(true)
                .build();
        cat.setTenantId(tenantId);
        return cat;
    }
    
    private void seedProducts() {
        Outlet mainOutlet = outletRepository.findAll().get(0);
        Long tenantId = mainOutlet.getId();
        List<Category> categories = categoryRepository.findAll();
        
        Category food = categories.stream().filter(c -> c.getName().equals("Makanan")).findFirst().orElse(null);
        Category drinks = categories.stream().filter(c -> c.getName().equals("Minuman")).findFirst().orElse(null);
        Category snacks = categories.stream().filter(c -> c.getName().equals("Snack")).findFirst().orElse(null);
        Category desserts = categories.stream().filter(c -> c.getName().equals("Dessert")).findFirst().orElse(null);
        
        List<Product> products = List.of(
                // Food
                createProduct("Nasi Goreng", "FOOD001", 25000, 50, food, tenantId),
                createProduct("Mie Goreng", "FOOD002", 22000, 45, food, tenantId),
                createProduct("Ayam Goreng", "FOOD003", 28000, 30, food, tenantId),
                createProduct("Sate Ayam", "FOOD004", 30000, 25, food, tenantId),
                createProduct("Gado-gado", "FOOD005", 20000, 40, food, tenantId),
                createProduct("Bakso", "FOOD006", 18000, 35, food, tenantId),
                
                // Drinks
                createProduct("Es Teh Manis", "DRK001", 5000, 100, drinks, tenantId),
                createProduct("Es Jeruk", "DRK002", 8000, 80, drinks, tenantId),
                createProduct("Kopi Hitam", "DRK003", 10000, 60, drinks, tenantId),
                createProduct("Es Kopi Susu", "DRK004", 15000, 50, drinks, tenantId),
                createProduct("Jus Alpukat", "DRK005", 18000, 30, drinks, tenantId),
                createProduct("Air Mineral", "DRK006", 4000, 200, drinks, tenantId),
                
                // Snacks
                createProduct("Pisang Goreng", "SNK001", 10000, 40, snacks, tenantId),
                createProduct("Tahu Goreng", "SNK002", 8000, 50, snacks, tenantId),
                createProduct("Tempe Goreng", "SNK003", 8000, 50, snacks, tenantId),
                createProduct("Kentang Goreng", "SNK004", 15000, 35, snacks, tenantId),
                
                // Desserts
                createProduct("Es Campur", "DST001", 15000, 25, desserts, tenantId),
                createProduct("Es Cendol", "DST002", 12000, 30, desserts, tenantId),
                createProduct("Pudding", "DST003", 10000, 20, desserts, tenantId),
                createProduct("Kue Lapis", "DST004", 8000, 40, desserts, tenantId)
        );
        
        productRepository.saveAll(products);
        log.info("Created {} products", products.size());
    }
    
    private Product createProduct(String name, String sku, int price, int stock, Category category, Long tenantId) {
        Product product = Product.builder()
                .name(name)
                .sku(sku)
                .price(BigDecimal.valueOf(price))
                .stock(stock)
                .minStock(5)
                .category(category)
                .isActive(true)
                .build();
        product.setTenantId(tenantId);
        return product;
    }
}
