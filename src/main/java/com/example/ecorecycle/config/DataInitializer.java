package com.example.ecorecycle.config;

import com.example.ecorecycle.entity.EcoGift;
import com.example.ecorecycle.repository.EcoGiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Initialize database with sample eco gifts
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final EcoGiftRepository ecoGiftRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if gifts already exist
        if (ecoGiftRepository.findByIsAvailableOrderByCreatedAtDesc(true).isEmpty()) {
            // Initialize sample eco gifts
            createSampleGifts();
        }
    }

    private void createSampleGifts() {
        // ...existing water bottle, cutlery set, tote bag...

        // Kitchen & Dining (Eco-Friendly)
        ecoGiftRepository.save(EcoGift.builder()
                .name("Eco-Friendly Water Bottle")
                .description("Durable stainless steel water bottle, BPA-free and keeps drinks cold for 24 hours. Perfect for daily hydration.")
                .ecoPointPrice(50L)
                .category("Kitchen & Dining")
                .stockQuantity(100)
                .isAvailable(true)
                .imageUrl("🍾")
                .build());

        ecoGiftRepository.save(EcoGift.builder()
                .name("Bamboo Cutting Board Set")
                .description("Large cutting board made from sustainable bamboo, perfect for kitchens. Includes 2 boards of different sizes.")
                .ecoPointPrice(65L)
                .category("Kitchen & Dining")
                .stockQuantity(45)
                .isAvailable(true)
                .imageUrl("🔪")
                .build());

        ecoGiftRepository.save(EcoGift.builder()
                .name("Reusable Food Container Set (5 pcs)")
                .description("Leak-proof glass food containers for meal prep and storage. Microwave and dishwasher safe. Set includes 5 containers.")
                .ecoPointPrice(85L)
                .category("Kitchen & Dining")
                .stockQuantity(60)
                .isAvailable(true)
                .imageUrl("🥡")
                .build());

        ecoGiftRepository.save(EcoGift.builder()
                .name("Eco-Friendly Coffee Mug")
                .description("Recyclable coffee mug made from sustainable materials with heat-resistant coating. Keeps beverages warm longer.")
                .ecoPointPrice(45L)
                .category("Kitchen & Dining")
                .stockQuantity(80)
                .isAvailable(true)
                .imageUrl("☕")
                .build());

        ecoGiftRepository.save(EcoGift.builder()
                .name("Bamboo Cutlery Set")
                .description("Portable bamboo utensil set with carrying case. Perfect for lunch boxes and picnics. Includes fork, spoon, and knife.")
                .ecoPointPrice(35L)
                .category("Kitchen & Dining")
                .stockQuantity(75)
                .isAvailable(true)
                .imageUrl("🥢")
                .build());

        ecoGiftRepository.save(EcoGift.builder()
                .name("Eco-Friendly Lunch Box")
                .description("Stainless steel lunch box with 3 compartments, perfect for packing meals. Durable and long-lasting.")
                .ecoPointPrice(70L)
                .category("Kitchen & Dining")
                .stockQuantity(50)
                .isAvailable(true)
                .imageUrl("🍱")
                .build());

        // Home Decor Items
        ecoGiftRepository.save(EcoGift.builder()
                .name("Natural Jute Wall Hanging")
                .description("Beautiful wall decoration made from natural jute fibers. Adds warmth and character to any room. Size: 60x40 cm")
                .ecoPointPrice(95L)
                .category("Home Decor")
                .stockQuantity(35)
                .isAvailable(true)
                .imageUrl("🏠")
                .build());

        ecoGiftRepository.save(EcoGift.builder()
                .name("Bamboo Plant Pot")
                .description("Eco-friendly plant pot made from sustainable bamboo. Perfect for indoor plants. Water-resistant coating included.")
                .ecoPointPrice(40L)
                .category("Home Decor")
                .stockQuantity(80)
                .isAvailable(true)
                .imageUrl("🪴")
                .build());

        ecoGiftRepository.save(EcoGift.builder()
                .name("Eco Wooden Shelf Unit")
                .description("Minimalist wooden shelf unit made from sustainably harvested wood. Multi-purpose storage for any room.")
                .ecoPointPrice(150L)
                .category("Home Decor")
                .stockQuantity(25)
                .isAvailable(true)
                .imageUrl("📚")
                .build());

        ecoGiftRepository.save(EcoGift.builder()
                .name("Natural Cork Floor Mat")
                .description("Eco-friendly floor mat made from natural cork. Non-slip and water-resistant. Perfect for kitchens and bathrooms.")
                .ecoPointPrice(55L)
                .category("Home Decor")
                .stockQuantity(60)
                .isAvailable(true)
                .imageUrl("🧶")
                .build());

        ecoGiftRepository.save(EcoGift.builder()
                .name("Organic Cotton Wall Tapestry")
                .description("Beautiful tapestry made from 100% organic cotton. Features eco-friendly dyes and sustainable manufacturing.")
                .ecoPointPrice(80L)
                .category("Home Decor")
                .stockQuantity(40)
                .isAvailable(true)
                .imageUrl("🎨")
                .build());

        ecoGiftRepository.save(EcoGift.builder()
                .name("Bamboo Room Divider")
                .description("Stylish room divider made from sustainable bamboo. Creates privacy and adds aesthetic appeal to your space.")
                .ecoPointPrice(120L)
                .category("Home Decor")
                .stockQuantity(30)
                .isAvailable(true)
                .imageUrl("🛏️")
                .build());

        // Personal Care & Bath
        ecoGiftRepository.save(EcoGift.builder()
                .name("Bamboo Toothbrush Pack (3)")
                .description("Eco-friendly bamboo toothbrushes with soft bristles and biodegradable packaging. Pack of 3 brushes.")
                .ecoPointPrice(30L)
                .category("Personal Care")
                .stockQuantity(200)
                .isAvailable(true)
                .imageUrl("🪥")
                .build());

        ecoGiftRepository.save(EcoGift.builder()
                .name("Natural Fiber Shower Mat")
                .description("Eco-friendly shower mat made from natural jute and rubber. Absorbent and durable for daily use.")
                .ecoPointPrice(38L)
                .category("Personal Care")
                .stockQuantity(70)
                .isAvailable(true)
                .imageUrl("🛁")
                .build());

        ecoGiftRepository.save(EcoGift.builder()
                .name("Organic Soap Gift Set")
                .description("Set of 5 handmade organic soaps made with natural ingredients. Eco-friendly packaging included.")
                .ecoPointPrice(50L)
                .category("Personal Care")
                .stockQuantity(90)
                .isAvailable(true)
                .imageUrl("🧼")
                .build());

        ecoGiftRepository.save(EcoGift.builder()
                .name("Bamboo Hair Brush")
                .description("Gentle hair brush made from sustainable bamboo with natural bristles. Perfect for all hair types.")
                .ecoPointPrice(32L)
                .category("Personal Care")
                .stockQuantity(85)
                .isAvailable(true)
                .imageUrl("💇")
                .build());

        ecoGiftRepository.save(EcoGift.builder()
                .name("Eco Shampoo & Conditioner Bar")
                .description("Solid shampoo and conditioner bars that replace 2-3 plastic bottles. Long-lasting and plastic-free.")
                .ecoPointPrice(45L)
                .category("Personal Care")
                .stockQuantity(110)
                .isAvailable(true)
                .imageUrl("🧴")
                .build());

        ecoGiftRepository.save(EcoGift.builder()
                .name("Organic Cotton Towel Set")
                .description("Set of 2 soft towels made from 100% organic cotton. Hypoallergenic and durable for everyday use.")
                .ecoPointPrice(75L)
                .category("Personal Care")
                .stockQuantity(55)
                .isAvailable(true)
                .imageUrl("🏖️")
                .build());

        // Fashion & Accessories
        ecoGiftRepository.save(EcoGift.builder()
                .name("Eco T-Shirt (Organic Cotton)")
                .description("Comfortable t-shirt made from 100% organic cotton with eco-friendly dyes. Available in multiple colors.")
                .ecoPointPrice(75L)
                .category("Fashion & Accessories")
                .stockQuantity(40)
                .isAvailable(true)
                .imageUrl("👕")
                .build());

        ecoGiftRepository.save(EcoGift.builder()
                .name("Organic Cotton Tote Bag")
                .description("Large reusable shopping bag made from 100% organic cotton. Perfect for shopping and everyday carry.")
                .ecoPointPrice(35L)
                .category("Fashion & Accessories")
                .stockQuantity(150)
                .isAvailable(true)
                .imageUrl("🛍️")
                .build());

        ecoGiftRepository.save(EcoGift.builder()
                .name("Bamboo Sunglasses")
                .description("Stylish sunglasses with frames made from sustainable bamboo. UV protection lenses included.")
                .ecoPointPrice(90L)
                .category("Fashion & Accessories")
                .stockQuantity(45)
                .isAvailable(true)
                .imageUrl("😎")
                .build());

        ecoGiftRepository.save(EcoGift.builder()
                .name("Eco Canvas Backpack")
                .description("Durable backpack made from organic canvas and recycled materials. Perfect for school or travel.")
                .ecoPointPrice(100L)
                .category("Fashion & Accessories")
                .stockQuantity(50)
                .isAvailable(true)
                .imageUrl("🎒")
                .build());

        ecoGiftRepository.save(EcoGift.builder()
                .name("Bamboo Phone Stand")
                .description("Adjustable phone stand made from sustainable bamboo. Works with all phones and tablets.")
                .ecoPointPrice(32L)
                .category("Fashion & Accessories")
                .stockQuantity(90)
                .isAvailable(true)
                .imageUrl("📱")
                .build());

        // Office & Study
        ecoGiftRepository.save(EcoGift.builder()
                .name("Recycled Paper Notebook")
                .description("Eco-friendly notebook made from 100% recycled paper. Perfect for notes and journaling.")
                .ecoPointPrice(28L)
                .category("Office & Study")
                .stockQuantity(120)
                .isAvailable(true)
                .imageUrl("📓")
                .build());

        ecoGiftRepository.save(EcoGift.builder()
                .name("Bamboo Desk Organizer")
                .description("Multifunctional desk organizer made from sustainable bamboo. Keeps your workspace organized and tidy.")
                .ecoPointPrice(65L)
                .category("Office & Study")
                .stockQuantity(70)
                .isAvailable(true)
                .imageUrl("✏️")
                .build());

        ecoGiftRepository.save(EcoGift.builder()
                .name("Eco Wooden Pen Set")
                .description("Set of 5 writing pens made from sustainably harvested wood. Non-toxic and biodegradable.")
                .ecoPointPrice(40L)
                .category("Office & Study")
                .stockQuantity(95)
                .isAvailable(true)
                .imageUrl("🖊️")
                .build());

        // Gardening & Plants
        ecoGiftRepository.save(EcoGift.builder()
                .name("Eco Plant Seeds Gift Box")
                .description("Box containing 10 different varieties of organic vegetable and flower seeds. Complete with planting guide.")
                .ecoPointPrice(35L)
                .category("Gardening & Plants")
                .stockQuantity(120)
                .isAvailable(true)
                .imageUrl("🌱")
                .build());

        ecoGiftRepository.save(EcoGift.builder()
                .name("Bamboo Garden Tool Set")
                .description("Complete garden tool set made from sustainable bamboo. Includes spade, rake, and hoe for easy gardening.")
                .ecoPointPrice(85L)
                .category("Gardening & Plants")
                .stockQuantity(50)
                .isAvailable(true)
                .imageUrl("🌿")
                .build());

        ecoGiftRepository.save(EcoGift.builder()
                .name("Eco Herb Growing Kit")
                .description("All-in-one kit to grow fresh herbs at home. Includes seeds, soil, and biodegradable pots.")
                .ecoPointPrice(55L)
                .category("Gardening & Plants")
                .stockQuantity(75)
                .isAvailable(true)
                .imageUrl("🌾")
                .build());

        ecoGiftRepository.save(EcoGift.builder()
                .name("Organic Compost Bin")
                .description("Compact home composting bin made from recycled materials. Turn kitchen waste into nutrient-rich compost.")
                .ecoPointPrice(110L)
                .category("Gardening & Plants")
                .stockQuantity(40)
                .isAvailable(true)
                .imageUrl("♻️")
                .build());

        // Lighting & Energy
        ecoGiftRepository.save(EcoGift.builder()
                .name("Solar LED Table Lamp")
                .description("Eco-friendly table lamp powered by solar energy. Perfect for indoor and outdoor use.")
                .ecoPointPrice(130L)
                .category("Lighting & Energy")
                .stockQuantity(35)
                .isAvailable(true)
                .imageUrl("💡")
                .build());

        ecoGiftRepository.save(EcoGift.builder()
                .name("Bamboo Solar Lights (4 pack)")
                .description("Set of 4 solar-powered lights made from sustainable bamboo. Automatic dusk-to-dawn operation.")
                .ecoPointPrice(60L)
                .category("Lighting & Energy")
                .stockQuantity(55)
                .isAvailable(true)
                .imageUrl("🌟")
                .build());

        // Health & Wellness
        ecoGiftRepository.save(EcoGift.builder()
                .name("Organic Yoga Mat")
                .description("Non-slip yoga mat made from natural rubber and organic cotton. Perfect for meditation and exercise.")
                .ecoPointPrice(95L)
                .category("Health & Wellness")
                .stockQuantity(50)
                .isAvailable(true)
                .imageUrl("🧘")
                .build());

        ecoGiftRepository.save(EcoGift.builder()
                .name("Bamboo Water Filter Bottle")
                .description("Reusable water bottle with built-in natural bamboo filter. Purifies water as you drink.")
                .ecoPointPrice(85L)
                .category("Health & Wellness")
                .stockQuantity(65)
                .isAvailable(true)
                .imageUrl("💧")
                .build());

        ecoGiftRepository.save(EcoGift.builder()
                .name("Natural Essential Oil Diffuser")
                .description("Eco-friendly diffuser made from wood and ceramic. Uses ultrasonic technology for aromatherapy.")
                .ecoPointPrice(75L)
                .category("Health & Wellness")
                .stockQuantity(45)
                .isAvailable(true)
                .imageUrl("🌸")
                .build());

        // Storage & Organization
        ecoGiftRepository.save(EcoGift.builder()
                .name("Eco Storage Basket Set (3)")
                .description("Set of 3 storage baskets woven from natural fibers. Perfect for organizing any room in your home.")
                .ecoPointPrice(70L)
                .category("Storage & Organization")
                .stockQuantity(60)
                .isAvailable(true)
                .imageUrl("🧺")
                .build());

        ecoGiftRepository.save(EcoGift.builder()
                .name("Bamboo Wardrobe Organizer")
                .description("Drawer organizer set made from sustainable bamboo. Keeps clothes neat and organized.")
                .ecoPointPrice(55L)
                .category("Storage & Organization")
                .stockQuantity(75)
                .isAvailable(true)
                .imageUrl("👔")
                .build());

        // Travel & Outdoor
        ecoGiftRepository.save(EcoGift.builder()
                .name("Eco Travel Toiletry Bag")
                .description("Waterproof toiletry bag made from recycled materials. Perfect for travel and daily use.")
                .ecoPointPrice(48L)
                .category("Travel & Outdoor")
                .stockQuantity(80)
                .isAvailable(true)
                .imageUrl("✈️")
                .build());

        ecoGiftRepository.save(EcoGift.builder()
                .name("Bamboo Camping Utensil Set")
                .description("Lightweight camping utensil set made from sustainable bamboo. Includes fork, spoon, knife, and case.")
                .ecoPointPrice(52L)
                .category("Travel & Outdoor")
                .stockQuantity(70)
                .isAvailable(true)
                .imageUrl("⛺")
                .build());
    }
}

