package com.izettle.localconfiguration;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;

import com.izettle.localconfiguration.util.ConfigurationContentValueProducer;
import com.izettle.localconfiguration.util.ConfigurationCursorParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LocalConfiguration {
    private final ContentResolver contentResolver;

    public LocalConfiguration(Context context) {
        this.contentResolver = context.getContentResolver();
    }

    private static void insertConfiguration(ContentResolver contentResolver, Configuration configuration) {
        contentResolver.insert(ConfigProviderHelper.configurationUri(), new ConfigurationContentValueProducer().toContentValues(configuration));
    }

    private static ArrayList<Configuration> getConfigurations(ContentResolver contentResolver) {
        ConfigurationCursorParser configurationCursorParser = new ConfigurationCursorParser();
        ArrayList<Configuration> configurations = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(ConfigProviderHelper.configurationUri(), configurationCursorParser.PROJECTION, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                configurations.add(configurationCursorParser.populateFromCursor(new Configuration(), cursor));
            }

        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return configurations;
    }

    /**
     * @return The configuration. Or null if provider doesn't exist. Empty Configuration if key did not exist in the provider.
     */
    @Nullable
    private static Configuration getConfiguration(ContentResolver contentResolver, String key) {
        ConfigurationCursorParser configurationCursorParser = new ConfigurationCursorParser();
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(ConfigProviderHelper.configurationUri(), configurationCursorParser.PROJECTION, ConfigurationCursorParser.Columns.KEY + " = ?", new String[]{key}, null);
            if (cursor == null) {
                return null;
            }

            if (cursor.moveToFirst()) {
                return configurationCursorParser.populateFromCursor(new Configuration(), cursor);
            }


        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return new Configuration();
    }

    public Map<String, ?> getAll() {
        Map<String, Object> values = new HashMap<>();
        ArrayList<Configuration> configurations = getConfigurations(contentResolver);
        for (Configuration configuration : configurations) {
            if (Boolean.class.getName().equals(configuration.type)) {
                values.put(configuration.key, Boolean.valueOf(configuration.value));
            } else if (Integer.class.getName().equals(configuration.type)) {
                values.put(configuration.key, Integer.valueOf(configuration.value));
            } else {
                values.put(configuration.key, configuration.value);
            }

        }
        return values;
    }

    public String getString(String key, String defValue) {
        Configuration configuration = getConfiguration(contentResolver, key);
        if (configuration == null) {
            return defValue;
        }

        if (configuration._id == 0) {
            configuration.key = key;
            configuration.value = defValue;
            configuration.type = String.class.getName();
            insertConfiguration(contentResolver, configuration);
        }
        return configuration.value;
    }

    public boolean getBoolean(String key, boolean defValue) {
        Configuration configuration = getConfiguration(contentResolver, key);
        if (configuration == null) {
            return defValue;
        }

        if (configuration._id == 0) {
            configuration.key = key;
            configuration.value = String.valueOf(defValue);
            configuration.type = Boolean.class.getName();
            insertConfiguration(contentResolver, configuration);
        }
        return Boolean.valueOf(configuration.value);
    }

    public int getInt(String key, int defValue) {
        Configuration configuration = getConfiguration(contentResolver, key);
        if (configuration == null) {
            return defValue;
        }

        if (configuration._id == 0) {
            configuration.key = key;
            configuration.value = String.valueOf(defValue);
            configuration.type = Integer.class.getName();
            insertConfiguration(contentResolver, configuration);
        }
        return Integer.valueOf(configuration.value);
    }

}
