package se.izettle.localconfig.library;


import android.database.Cursor;

import se.izettle.localconfiguration.library.util.ConfigurationCursorParser;
import se.izettle.localconfiguration.library.util.ICursorParser;

public class ConfigurationFullCursorParser implements ICursorParser<ConfigurationFull> {

    public static String[] PROJECTION = new String[]{
            Columns._ID,
            Columns.APPLICATION_ID,
            Columns.KEY,
            Columns.VALUE,
            Columns.TYPE
    };
    private ConfigurationCursorParser innerParser;

    public ConfigurationFullCursorParser() {
        innerParser = new ConfigurationCursorParser();
    }

    public ConfigurationFull populateFromCursor(ConfigurationFull configuration, Cursor cursor) {
        innerParser.populateFromCursor(configuration, cursor);

        configuration.applicationId = cursor.getLong(cursor.getColumnIndex(ConfigurationFullCursorParser.Columns.APPLICATION_ID));

        return configuration;
    }

    public interface Columns extends ConfigurationCursorParser.Columns {
        String APPLICATION_ID = "applicationId";
    }
}
