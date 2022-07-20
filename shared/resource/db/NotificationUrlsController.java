package com.mynet.shared.resource.db;

import com.mynet.shared.resource.CacheController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class NotificationUrlsController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationUrlsController.class);

    private static NotificationUrlsController INSTANCE;

    private HashMap<String, String> urls;

    public NotificationUrlsController() {
        urls = new HashMap<>();
        urls = DBController.getInstance().getNotificationUrls();
        addRedisListeners();
    }

    public static void init() {
        if (INSTANCE == null) {
            INSTANCE = new NotificationUrlsController();
        }
    }

    public static NotificationUrlsController getInstance() {
        return INSTANCE;
    }

    public String getUrl(String name) {
        return urls.get(name.toLowerCase());
    }

    private void addRedisListeners() {
        CacheController.getInstance().listenUpdateNotificationUrls((charSequence, info) -> {
            urls = DBController.getInstance().getNotificationUrls();
            logger.info("Notification urls updated!");
        });
    }
}
