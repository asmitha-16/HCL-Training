package com.campus.lostandfound.item.model;

import java.util.List;

public record Category(
        String id,
        String code,
        String displayName,
        String icon,
        List<String> typicalVerificationQuestions
) {
    public static List<Category> defaultCategories() {
        return List.of(
                new Category("CAT-ELEC", "ELECTRONICS", "Electronics & Gadgets", "laptop",
                        List.of("What is the device brand and color?", "What is the lock-screen wallpaper or case design?", "What is the exact serial number or model?", "Are there specific stickers or scratches?")),
                new Category("CAT-CARD", "ID_CARDS", "ID Cards & Wallets", "id-card",
                        List.of("What is the full name or student/staff ID printed on the card?", "What bank/organization issued the card?", "What color and material is the wallet?")),
                new Category("CAT-KEYS", "KEYS", "Keys & Access Badges", "key",
                        List.of("How many keys are on the keychain?", "What color/design is the lanyard or keychain attached?", "Is there an RFID fob or specific brand tag?")),
                new Category("CAT-BAGS", "BAGS", "Backpacks & Bags", "briefcase",
                        List.of("What brand, color, and number of compartments?", "What specific textbook, notebook, or item is in the front pocket?", "Are there any keyrings attached to the zipper?")),
                new Category("CAT-WEAR", "WEARABLES", "Clothing, Glasses & Watches", "watch",
                        List.of("What is the frame color, brand, and prescription type?", "What is the strap color and watch model?", "What is the jacket size, brand, and color?")),
                new Category("CAT-BOOK", "BOOKS_STATIONERY", "Books, Notes & Documents", "book",
                        List.of("What is the title/author or course code on the notebook?", "What name or markings appear inside the front cover?"))
        );
    }
}
