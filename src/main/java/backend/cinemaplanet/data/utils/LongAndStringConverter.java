package backend.cinemaplanet.data.utils;

public class LongAndStringConverter {
    private long longValue;
    private String stringValue;

    public LongAndStringConverter() {

    }

    public long getLongValue() {
        return longValue;
    }

    public void setLongValue(long longValue) {
        this.longValue = longValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public static Long convertToLong(String value) {
        if (value != null) {
            return Long.parseLong(value);
        } else {
            return null;
        }
    }

    public static String convertToString(Long value) {
        if (value != null) {
            return value.toString();
        } else {
            return null;
        }
    }
}
