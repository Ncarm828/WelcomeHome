package com.philips.lighting.quickstart.DataClass.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListOfLightContent {


    public static final List<Light> ITEMS = new ArrayList<Light>();
    public static final Map<String, Light> ITEM_MAP = new HashMap<String, Light>();


    private static void addItem(Light item) {
        ITEMS.add(item);
        ITEM_MAP.put(String.valueOf(item.id), item);
    }

    /**
     * Class that holds the Hardware information
     */
    public static class Light {
        public final int id;
        public final String Hardware;

        public Light(int id, String content) {
            this.id = id;
            this.Hardware = content;
        }

        @Override
        public String toString() {
            return Hardware;
        }
    }
}
