package backend.cinemaplanet.data.utils;

public class DoubleAndStringConverter {
    private Double doubleValue;
    private String stringValue;

    public DoubleAndStringConverter() {

    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public void setLongValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public static Double convertToDouble(String value) {
        if (value != null) {
            return Double.parseDouble(value);
        } else {
            return null;
        }
    }

    public static String convertToString(Double value) {
        if (value != null) {
            return value.toString();
        } else {
            return null;
        }
    }
}