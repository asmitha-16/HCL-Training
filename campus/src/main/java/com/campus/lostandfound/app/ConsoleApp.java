package com.campus.lostandfound.app;

import com.campus.lostandfound.menu.ConsoleMenu;
import com.campus.lostandfound.service.LostAndFoundService;
import com.campus.lostandfound.store.CampusDataStore;

/**
 * Main application launcher for the Campus Lost and Found Console Application.
 */
public class ConsoleApp {

    public static void main(String[] args) {
        // Initialize Collections Store
        CampusDataStore dataStore = new CampusDataStore();

        // Initialize Core Domain Service with 7-Day Sample Data
        LostAndFoundService service = new LostAndFoundService(dataStore);

        // Launch Interactive Console Menu
        ConsoleMenu menu = new ConsoleMenu(service);
        menu.run();
    }
}
