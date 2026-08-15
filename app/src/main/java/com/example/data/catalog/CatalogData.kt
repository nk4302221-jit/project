package com.example.data.catalog

import com.example.model.*

object CatalogData {

    val categories = listOf(
        CategoryItem(
            id = "electronics",
            name = "Electronics",
            iconName = "Devices",
            bannerText = "Up to 50% Off Top Tech",
            subcategories = listOf("Smartphones", "Laptops", "Audio & Headphones", "Smartwatches", "Cameras", "Gaming")
        ),
        CategoryItem(
            id = "fashion",
            name = "Fashion",
            iconName = "Checkroom",
            bannerText = "Trending Styles & Footwear",
            subcategories = listOf("Footwear", "Men's Wear", "Women's Wear", "Watches", "Bags & Luggage", "Eyewear")
        ),
        CategoryItem(
            id = "home",
            name = "Home & Kitchen",
            iconName = "Home",
            bannerText = "Modern Living Essentials",
            subcategories = listOf("Appliances", "Cookware", "Furniture", "Smart Home", "Decor")
        ),
        CategoryItem(
            id = "beauty",
            name = "Beauty & Care",
            iconName = "Face",
            bannerText = "Glow & Grooming Kits",
            subcategories = listOf("Skincare", "Fragrances", "Haircare", "Grooming")
        ),
        CategoryItem(
            id = "sports",
            name = "Sports & Fitness",
            iconName = "FitnessCenter",
            bannerText = "Gear Up & Train Hard",
            subcategories = listOf("Gym Equipment", "Yoga & Activewear", "Hydration", "Outdoor")
        ),
        CategoryItem(
            id = "groceries",
            name = "Essentials",
            iconName = "ShoppingBasket",
            bannerText = "Daily Gourmet & Organic",
            subcategories = listOf("Gourmet Snacks", "Coffee & Tea", "Nutrition", "Personal Hygiene")
        )
    )

    val products = listOf(
        Product(
            id = "prod_tech_01",
            title = "AuraWave Pro ANC Wireless Headphones",
            brand = "AuraSound",
            categoryId = "electronics",
            subcategory = "Audio & Headphones",
            price = 14999.0,
            originalPrice = 24999.0,
            discountPct = 40,
            rating = 4.8f,
            reviewCount = 3420,
            stock = 45,
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&q=80",
                "https://images.unsplash.com/photo-1484704849700-f032a568e944?w=800&q=80",
                "https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=800&q=80"
            ),
            description = "Experience industry-leading active noise cancellation powered by custom dual-core neural processors. Ultra-comfortable memory foam earcups with 40-hour battery life and high-res lossless spatial audio.",
            highlights = listOf(
                "Adaptive Hybrid Active Noise Cancellation (up to 42dB)",
                "40-Hour Battery with Fast USB-C Quick Charge (10 min = 5 hours)",
                "Custom 40mm Beryllium Acoustic Drivers",
                "Multipoint Bluetooth 5.3 & Low Latency Gaming Mode",
                "Crystal-clear 4-mic beamforming voice pickup"
            ),
            specifications = mapOf(
                "Driver Size" to "40mm Custom Tuned",
                "Battery Life" to "40 Hours (ANC On)",
                "Bluetooth Version" to "5.3 with LDAC, AAC, SBC",
                "Charging Port" to "USB Type-C Fast Charge",
                "Weight" to "254 grams",
                "Warranty" to "1 Year Brand Warranty"
            ),
            isPrimeDelivery = true,
            deliveryDays = 1,
            isDealOfTheDay = true,
            tags = listOf("Top Rated", "ANC", "Wireless", "Best Seller")
        ),
        Product(
            id = "prod_tech_02",
            title = "Apex Ultra 5G Smartphone (256GB / 12GB RAM)",
            brand = "ApexTech",
            categoryId = "electronics",
            subcategory = "Smartphones",
            price = 64999.0,
            originalPrice = 79999.0,
            discountPct = 19,
            rating = 4.7f,
            reviewCount = 1890,
            stock = 22,
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800&q=80",
                "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=800&q=80",
                "https://images.unsplash.com/photo-1580910051074-3eb694886505?w=800&q=80"
            ),
            description = "Flagship redefined with 200MP OIS sensor, 6.7-inch 120Hz LTPO AMOLED display, Snapdragon 8 Gen 3 octa-core powerhouse, and 5000mAh battery with 100W HyperCharge.",
            highlights = listOf(
                "6.7\" QHD+ 120Hz LTPO AMOLED 2600 nits Peak Brightness",
                "200MP Triple Camera System with 5x Periscope Optical Zoom",
                "Snapdragon 8 Gen 3 Processor with LiquidCool Vapor Chamber",
                "5000mAh Battery with 100W Wired & 50W Wireless Charging",
                "IP68 Water & Dust Resistance with Titanium Frame"
            ),
            specifications = mapOf(
                "Display" to "6.7\" 120Hz LTPO AMOLED QHD+",
                "Processor" to "Snapdragon 8 Gen 3 (4nm)",
                "RAM & Storage" to "12GB LPDDR5X / 256GB UFS 4.0",
                "Rear Camera" to "200MP (OIS) + 50MP Ultra-wide + 64MP Telephoto",
                "Battery" to "5000 mAh (100W Charger in box)",
                "OS" to "Android 15 with 5 Years OS Updates"
            ),
            isPrimeDelivery = true,
            deliveryDays = 1,
            isBestSeller = true,
            tags = listOf("Flagship", "5G", "Camera Beast", "Titanium")
        ),
        Product(
            id = "prod_tech_03",
            title = "Chronos Pulse 3 Titanium Smartwatch",
            brand = "Chronos",
            categoryId = "electronics",
            subcategory = "Smartwatches",
            price = 18999.0,
            originalPrice = 27999.0,
            discountPct = 32,
            rating = 4.6f,
            reviewCount = 945,
            stock = 38,
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800&q=80",
                "https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=800&q=80"
            ),
            description = "Aerospace-grade titanium chassis with sapphire crystal glass, 1.43\" AMOLED always-on display, dual-frequency GPS, ECG, SpO2, and 14-day battery life.",
            highlights = listOf(
                "Grade 5 Titanium Body with Sapphire Crystal Glass",
                "Advanced Biometric Sensors: ECG, Heart Rate, SpO2 & Sleep Tracker",
                "Dual-Frequency Multi-Band GPS with Turn-by-Turn navigation",
                "5 ATM + IP68 Water Resistance (Swim-proof)",
                "Up to 14 Days Battery on standard mode"
            ),
            specifications = mapOf(
                "Case Material" to "Grade 5 Titanium",
                "Display" to "1.43\" AMOLED Always-On (1000 nits)",
                "Water Resistance" to "50m / 5 ATM",
                "Battery Life" to "14 Days Typical / 40h GPS",
                "Sensors" to "ECG, PPG, SpO2, Barometer, Compass",
                "Connectivity" to "Bluetooth 5.3, NFC, Wi-Fi"
            ),
            isPrimeDelivery = true,
            deliveryDays = 2,
            tags = listOf("Titanium", "Fitness", "ECG", "GPS")
        ),
        Product(
            id = "prod_tech_04",
            title = "ZenBook Horizon 16 AI OLED Laptop",
            brand = "ZenComp",
            categoryId = "electronics",
            subcategory = "Laptops",
            price = 89990.0,
            originalPrice = 119990.0,
            discountPct = 25,
            rating = 4.9f,
            reviewCount = 612,
            stock = 15,
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=800&q=80",
                "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800&q=80"
            ),
            description = "Intel Core Ultra 7 processor with dedicated NPU for on-device AI tasks, 16-inch 3.2K 120Hz OLED HDR display, 32GB RAM, 1TB NVMe SSD in an all-metal 1.39kg chassis.",
            highlights = listOf(
                "Intel Core Ultra 7 155H with Intel AI Boost NPU",
                "16\" 3.2K 120Hz OLED 100% DCI-P3 Color Accuracy",
                "32GB LPDDR5X RAM + 1TB PCIe 4.0 NVMe SSD",
                "75Wh Battery with 15+ Hours Battery Life",
                "Harman Kardon Quad Speakers with Dolby Atmos"
            ),
            specifications = mapOf(
                "Processor" to "Intel Core Ultra 7 155H (16 Cores, 22 Threads)",
                "RAM" to "32GB LPDDR5X 7467MHz",
                "Storage" to "1TB M.2 NVMe PCIe 4.0 SSD",
                "Display" to "16\" 3.2K OLED 120Hz 500 nits",
                "Weight" to "1.39 kg Ultra-light",
                "OS" to "Windows 11 Home + MS Office 2024"
            ),
            isPrimeDelivery = true,
            deliveryDays = 2,
            tags = listOf("AI Laptop", "OLED", "Intel Ultra 7")
        ),
        Product(
            id = "prod_fash_01",
            title = "Velocity Nitro Pro Running Sneakers",
            brand = "AeroStride",
            categoryId = "fashion",
            subcategory = "Footwear",
            price = 4999.0,
            originalPrice = 8999.0,
            discountPct = 44,
            rating = 4.6f,
            reviewCount = 1420,
            stock = 60,
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800&q=80",
                "https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=800&q=80",
                "https://images.unsplash.com/photo-1595950653106-6c9ebd614d3a?w=800&q=80"
            ),
            description = "Engineered with supercritical nitrogen-infused foam midsole for maximum energy return and propulsion. Breathable engineered mesh upper with carbon-fiber shank plate.",
            highlights = listOf(
                "Nitro-Infused Responsive Cushioning System",
                "Carbon Fiber Composite Stability Plate",
                "Pumagrip / Continental Grade High Traction Rubber Outsole",
                "Featherlight 210g race-day weight",
                "Ortholite anti-microbial moisture-wicking insole"
            ),
            specifications = mapOf(
                "Sole Material" to "High-Traction Carbon Rubber",
                "Upper Material" to "Engineered Breathable Jacquard Mesh",
                "Closure" to "Lace-Up with Dynamic Fit Ribs",
                "Heel Drop" to "8mm Drop",
                "Occasion" to "Marathon, Running, Daily Training"
            ),
            isPrimeDelivery = true,
            deliveryDays = 2,
            isDealOfTheDay = true,
            tags = listOf("Running", "Sneakers", "Carbon Plate")
        ),
        Product(
            id = "prod_fash_02",
            title = "Heritage Classic Chronograph Leather Watch",
            brand = "NordicCraft",
            categoryId = "fashion",
            subcategory = "Watches",
            price = 6499.0,
            originalPrice = 12999.0,
            discountPct = 50,
            rating = 4.8f,
            reviewCount = 830,
            stock = 25,
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=800&q=80",
                "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=800&q=80"
            ),
            description = "Handcrafted minimalist luxury chronograph with 316L stainless steel case, genuine Italian vegetable-tanned leather strap, and Japanese precision quartz movement.",
            highlights = listOf(
                "316L Surgical Grade Stainless Steel Case",
                "Genuine Italian Full-Grain Leather Strap (Quick-release)",
                "Sapphire Coated Scratch-Resistant Mineral Crystal",
                "50M Water Resistant with Triple Sub-Dials",
                "2 Years International Movement Warranty"
            ),
            specifications = mapOf(
                "Dial Diameter" to "42mm",
                "Case Thickness" to "10.5mm",
                "Strap Width" to "20mm Genuine Italian Leather",
                "Movement" to "Japanese Quartz Chronograph",
                "Water Resistance" to "5 ATM / 50 Meters"
            ),
            isPrimeDelivery = true,
            deliveryDays = 1,
            isBestSeller = true,
            tags = listOf("Luxury", "Chronograph", "Italian Leather")
        ),
        Product(
            id = "prod_fash_03",
            title = "Urban Nomad Waterproof Commuter Backpack (28L)",
            brand = "TerraPack",
            categoryId = "fashion",
            subcategory = "Bags & Luggage",
            price = 3299.0,
            originalPrice = 5999.0,
            discountPct = 45,
            rating = 4.7f,
            reviewCount = 1105,
            stock = 50,
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800&q=80",
                "https://images.unsplash.com/photo-1622560480605-d83c853bc5c3?w=800&q=80"
            ),
            description = "Cordura ballistic nylon with waterproof roll-top closure, dedicated padded 16\" laptop compartment, hidden anti-theft passport pocket, and magnetic Fidlock buckles.",
            highlights = listOf(
                "1000D Cordura Waterproof Ballistic Fabric",
                "Suspended Padded Compartment for 16\" MacBook / Laptops",
                "Ergonomic Breathable Airflow Back Panel",
                "Luggage Pass-Through Strap & Magnetic Fidlock Fasteners",
                "YKK Aquaguard Weatherproof Zippers"
            ),
            specifications = mapOf(
                "Capacity" to "28 Liters Expandable to 32L",
                "Dimensions" to "48 x 30 x 18 cm",
                "Weight" to "890 grams",
                "Material" to "1000D Water-Repellent Ballistic Nylon"
            ),
            isPrimeDelivery = true,
            deliveryDays = 2,
            tags = listOf("Waterproof", "Backpack", "Laptop Bag")
        ),
        Product(
            id = "prod_home_01",
            title = "Barista Master Pro Espresso & Cappuccino Machine",
            brand = "CaffèLuxe",
            categoryId = "home",
            subcategory = "Appliances",
            price = 24999.0,
            originalPrice = 39999.0,
            discountPct = 37,
            rating = 4.8f,
            reviewCount = 760,
            stock = 18,
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?w=800&q=80",
                "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=800&q=80"
            ),
            description = "Commercial grade 20-bar Italian ULKA pump with integrated conical burr grinder (30 grind settings), PID temperature control, and micro-foam steam wand for silky latte art.",
            highlights = listOf(
                "20-Bar Professional Italian ULKA High-Pressure Pump",
                "Built-in Precision Stainless Steel Conical Burr Grinder",
                "PID Temperature Stability & Low-Pressure Pre-Infusion",
                "Commercial 360° Stainless Steel Microfoam Steam Wand",
                "Includes 54mm Portafilter, Tamper, Milk Pitcher & Cleaning Kit"
            ),
            specifications = mapOf(
                "Pressure" to "20 Bar Italian Pump",
                "Bean Hopper Capacity" to "250g Airtight Sealed",
                "Water Tank" to "2.2 Liters Removable with Water Filter",
                "Power" to "1500 Watts Fast ThermoBlock Heating",
                "Body" to "Brushed Stainless Steel 304"
            ),
            isPrimeDelivery = true,
            deliveryDays = 2,
            isDealOfTheDay = true,
            tags = listOf("Espresso", "Coffee", "Barista")
        ),
        Product(
            id = "prod_home_02",
            title = "SmartClean RoboVac 3000 LiDAR Robot Vacuum & Mop",
            brand = "RoboLife",
            categoryId = "home",
            subcategory = "Smart Home",
            price = 29999.0,
            originalPrice = 49999.0,
            discountPct = 40,
            rating = 4.7f,
            reviewCount = 920,
            stock = 20,
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1589710751893-f9a6770ad71b?w=800&q=80",
                "https://images.unsplash.com/photo-1558317374-067fb5f30001?w=800&q=80"
            ),
            description = "Next-gen 360° LiDAR navigation with 5000Pa hyper suction, active sonic vibrating mopping system, auto carpet detection, and smart room-by-room app mapping.",
            highlights = listOf(
                "5000Pa Cyclone Suction with Anti-Tangle Main Brush",
                "Precision 3D LiDAR Obstacle Avoidance & Multi-Floor Mapping",
                "Sonic Vibrating Mop Scrubbing at 3000 vibrations/min",
                "Auto Carpet Boost & Virtual No-Go Zones via App",
                "5200mAh Battery (Up to 180 mins continuous cleaning)"
            ),
            specifications = mapOf(
                "Suction Power" to "5000 Pa",
                "Navigation" to "LDS Laser LiDAR + 3D ToF Obstacle Sensor",
                "Dustbin / Water" to "450ml Dustbin / 300ml Electronically Controlled Water Tank",
                "Battery" to "5200 mAh (Auto-Dock & Recharge)",
                "App Integration" to "ShopWave Smart App, Alexa, Google Home"
            ),
            isPrimeDelivery = true,
            deliveryDays = 1,
            isBestSeller = true,
            tags = listOf("RoboVac", "LiDAR", "Smart Clean")
        ),
        Product(
            id = "prod_beauty_01",
            title = "Radiance Elixir Triple Peptide & Vitamin C Serum (50ml)",
            brand = "LumiGlow",
            categoryId = "beauty",
            subcategory = "Skincare",
            price = 1499.0,
            originalPrice = 2499.0,
            discountPct = 40,
            rating = 4.9f,
            reviewCount = 2890,
            stock = 120,
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=800&q=80",
                "https://images.unsplash.com/photo-1608248597359-25f0a8c279c6?w=800&q=80"
            ),
            description = "Potent brightening and anti-aging antioxidant elixir with 15% Ethyl Ascorbic Acid (Vitamin C), Ferulic Acid, Hyaluronic Acid, and Matrixyl 3000 peptides.",
            highlights = listOf(
                "15% Stable Vitamin C + 1% Ferulic Acid for Intense Glow",
                "Triple Peptide Complex to boost natural collagen synthesis",
                "Multi-molecular Hyaluronic Acid for deep 72h hydration",
                "Fragrance-Free, Non-Comedogenic, Cruelty-Free, Dermatologist Tested",
                "Visible reduction in dark spots within 14 days"
            ),
            specifications = mapOf(
                "Volume" to "50 ml / 1.7 fl oz",
                "Key Ingredients" to "15% Vitamin C, Ferulic Acid, Peptides, Hyaluronic Acid",
                "Skin Type" to "All Skin Types (Sensitive Safe)",
                "Shelf Life" to "24 Months"
            ),
            isPrimeDelivery = true,
            deliveryDays = 1,
            isBestSeller = true,
            tags = listOf("Vitamin C", "Serum", "Glow", "Skincare")
        ),
        Product(
            id = "prod_sports_01",
            title = "ApexForm Smart Adjustable Dumbbells Set (2.5kg - 24kg)",
            brand = "ApexForm",
            categoryId = "sports",
            subcategory = "Gym Equipment",
            price = 16999.0,
            originalPrice = 24999.0,
            discountPct = 32,
            rating = 4.8f,
            reviewCount = 480,
            stock = 14,
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?w=800&q=80",
                "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?w=800&q=80"
            ),
            description = "Rapid 1-second dial adjustment mechanism replaces 15 sets of weights in one compact footprint. Heavy-duty laser cut steel plates with textured knurled grip handles.",
            highlights = listOf(
                "Replaces 15 Pairs of Individual Dumbbells (2.5kg to 24kg per dumbbell)",
                "Instant 1-Turn Dial Selection Locking System",
                "Durable Commercial-Grade Steel Plates with Anti-Noise Molding",
                "High-Traction Knurled Aluminum Grip Handle",
                "Includes Heavy-Duty Storage Base Trays"
            ),
            specifications = mapOf(
                "Weight Range" to "2.5kg, 3.5kg, 4.5kg, 5.5kg ... up to 24kg each",
                "Material" to "Laser-cut Steel with Composite Polymer Sheath",
                "Quantity" to "Set of 2 Dumbbells with 2 Storage Trays",
                "Warranty" to "2 Years Structural Warranty"
            ),
            isPrimeDelivery = true,
            deliveryDays = 2,
            tags = listOf("Gym", "Dumbbells", "Adjustable")
        ),
        Product(
            id = "prod_groc_01",
            title = "Single Origin Arabica Reserve Coffee Beans (1kg)",
            brand = "Artisan Roast",
            categoryId = "groceries",
            subcategory = "Coffee & Tea",
            price = 1299.0,
            originalPrice = 1899.0,
            discountPct = 31,
            rating = 4.9f,
            reviewCount = 1530,
            stock = 85,
            imageUrls = listOf(
                "https://images.unsplash.com/photo-1559056199-641a0ac8b55e?w=800&q=80",
                "https://images.unsplash.com/photo-1447933601403-0c6688de566e?w=800&q=80"
            ),
            description = "100% Grade A Specialty Estate Arabica beans grown at 1400m altitude in Chikmagalur. Medium-dark roast with tasting notes of dark chocolate, hazelnut, and citrus caramel.",
            highlights = listOf(
                "100% Single Origin High-Altitude Arabica (Grade A)",
                "Freshly Roasted in Small Batches with Degassing Valve Pack",
                "Notes of Dark Chocolate, Roasted Hazelnut & Golden Honey",
                "Perfect for Espresso, French Press, Moka Pot, and Pour Over",
                "Rainforest Alliance & Fair Trade Certified"
            ),
            specifications = mapOf(
                "Net Quantity" to "1000 grams (1 kg)",
                "Roast Profile" to "Medium-Dark Roast",
                "Origin" to "Chikmagalur, Western Ghats (1400m AMSL)",
                "Shelf Life" to "12 Months Sealed"
            ),
            isPrimeDelivery = true,
            deliveryDays = 1,
            tags = listOf("Coffee", "Arabica", "Artisan", "Organic")
        )
    )

    val sampleReviews = listOf(
        Review(
            id = "rev_01",
            productId = "prod_tech_01",
            userName = "Priya Sharma",
            rating = 5.0f,
            title = "Best ANC Headphones in this price bracket!",
            comment = "The sound clarity and deep bass are phenomenal. Traveled on a flight yesterday and the ANC blocked out all engine hum. Battery lasted through 3 days of nonstop work meetings!",
            date = "2 days ago",
            isVerifiedPurchase = true,
            helpfulCount = 42
        ),
        Review(
            id = "rev_02",
            productId = "prod_tech_01",
            userName = "Rohan Mehta",
            rating = 4.5f,
            title = "Great build quality & crystal mic",
            comment = "Super lightweight and doesn't squeeze the ears. Multipoint pairing between laptop and phone switches seamlessly within 1 second. Highly recommended!",
            date = "1 week ago",
            isVerifiedPurchase = true,
            helpfulCount = 18
        ),
        Review(
            id = "rev_03",
            productId = "prod_tech_02",
            userName = "Arjun Verma",
            rating = 5.0f,
            title = "Incredible 200MP camera and display!",
            comment = "Pictures look like they were taken on a DSLR. Charging speed is mind-blowing (went from 5% to 100% in 22 minutes). Display is bright enough under direct noon sunlight.",
            date = "3 days ago",
            isVerifiedPurchase = true,
            helpfulCount = 35
        ),
        Review(
            id = "rev_04",
            productId = "prod_fash_01",
            userName = "Vikram Patil",
            rating = 5.0f,
            title = "Felt like running on clouds",
            comment = "Shaved 2 minutes off my 10k personal best on the first run. The nitrogen foam bounce is real. True to size fit with snug midfoot lock.",
            date = "5 days ago",
            isVerifiedPurchase = true,
            helpfulCount = 29
        )
    )

    val sampleCoupons = listOf(
        Coupon(
            code = "WAVE20",
            title = "20% OFF",
            discountPct = 20,
            maxDiscount = 1500.0,
            minOrderAmount = 1999.0,
            description = "Get 20% discount up to ₹1,500 on all electronics & fashion orders"
        ),
        Coupon(
            code = "WELCOME50",
            title = "₹500 Flat OFF",
            fixedDiscount = 500.0,
            minOrderAmount = 2499.0,
            description = "Flat ₹500 off on your order above ₹2,499"
        ),
        Coupon(
            code = "SUPERDEAL",
            title = "₹1,000 OFF",
            fixedDiscount = 1000.0,
            minOrderAmount = 9999.0,
            description = "Save flat ₹1,000 on premium gadgets and home appliances"
        ),
        Coupon(
            code = "FREESHIP",
            title = "Free Express Delivery",
            fixedDiscount = 99.0,
            minOrderAmount = 499.0,
            description = "Free priority delivery on all orders above ₹499"
        )
    )

    val sampleAddresses = listOf(
        Address(
            id = "addr_1",
            fullName = "Nirbhay Sharma",
            phone = "+91 98765 43210",
            line1 = "Flat 402, Highline Residency, 12th Main",
            line2 = "Indiranagar 2nd Stage",
            city = "Bengaluru",
            state = "Karnataka",
            pincode = "560038",
            label = "Home",
            isDefault = true
        ),
        Address(
            id = "addr_2",
            fullName = "Nirbhay Sharma",
            phone = "+91 98765 43210",
            line1 = "TechPark Block 4, Level 6, Outer Ring Road",
            line2 = "Bellandur",
            city = "Bengaluru",
            state = "Karnataka",
            pincode = "560103",
            label = "Work",
            isDefault = false
        )
    )

    val sampleFaqs = listOf(
        "How do I track my ShopWave delivery?" to "Once your order is placed, visit the 'My Orders' tab. You'll see real-time updates across Placed, Confirmed, Packed, Shipped, Out for Delivery, and Delivered stages with courier tracking numbers.",
        "What payment options are supported?" to "We accept UPI (Google Pay, PhonePe, Paytm, BHIM), Credit/Debit cards (Visa, MasterCard, RuPay), Net Banking with 50+ banks, Wallets, and Cash on Delivery (COD).",
        "What is the ShopWave return & refund policy?" to "Most items qualify for a 7-day hassle-free return or replacement. You can initiate a return straight from the Order Details screen once delivered.",
        "Is express 1-day delivery available in my area?" to "Yes, products marked with the Prime Wave badge support same-day or next-day delivery across major serviceable pincodes. You can verify your pincode on any product page.",
        "How do I apply promotional discount coupons?" to "In your Cart or during Step 2 of Checkout, browse available coupons or type your promo code (e.g. WAVE20, WELCOME50) and tap 'Apply'."
    )
}
