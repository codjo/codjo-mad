package net.codjo.mad.gui.request;

public class Column implements Comparable {
    public static enum Sorter {STRING, NUMERIC, BOOLEAN, DATE;
        public static Sorter getType(String value) {
            if (null == value) {
                return null;
            }
            if (STRING.name().equalsIgnoreCase(value)) {
                return STRING;
            }
            if (NUMERIC.name().equalsIgnoreCase(value)) {
                return NUMERIC;
            }
            if (BOOLEAN.name().equalsIgnoreCase(value)) {
                return BOOLEAN;
            }
            if (value.toUpperCase().startsWith(DATE.name())) {
                return DATE;
            }
            return null;
        }
    };

    private String fieldName;
    private String label;
    private int maxSize;
    private int minSize;
    private int preferredSize;
    private String format;
    private String sorter;
    private String renderer;
    private boolean summable;
    private String summableLabel;


    public Column() {
    }


    public Column(String fieldName, String label) {
        this(fieldName, label, 0, 0, 0);
    }


    public Column(String fieldName, String label, int minSize, int maxSize,
                  int preferredSize) {
        this(fieldName, label, minSize, maxSize, preferredSize, false, null);
    }


    public Column(String fieldName, String label, int minSize, int maxSize,
                  int preferredSize, boolean summable, String summableLabel) {
        setFieldName(fieldName);
        setLabel(label);
        setMinSize(minSize);
        setMaxSize(maxSize);
        setPreferredSize(preferredSize);
        setSummable(summable);
        setSummableLabel(summableLabel);
    }


    public int getPreferredSize() {
        return preferredSize;
    }


    public void setPreferredSize(int preferredSize) {
        this.preferredSize = preferredSize;
    }


    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }


    public void setLabel(String label) {
        this.label = label;
    }


    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }


    public void setMinSize(int minSize) {
        this.minSize = minSize;
    }


    public String getFieldName() {
        return fieldName;
    }


    public String getLabel() {
        return label;
    }


    public int getMaxSize() {
        return maxSize;
    }


    public int getMinSize() {
        return minSize;
    }


    public String getFormat() {
        return format;
    }


    public void setFormat(String format) {
        this.format = format;
    }


    public String getSorter() {
        return sorter;
    }


    public void setSorter(String sorter) {
        this.sorter = sorter;
    }


    public String getRenderer() {
        return renderer;
    }


    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }


    public void setSummable(boolean summable) {
        this.summable = summable;
    }


    public boolean isSummable() {
        return summable;
    }


    public String getSummableLabel() {
        return summableLabel;
    }


    public void setSummableLabel(String summableLabel) {
        this.summableLabel = summableLabel;
    }


    public int compareTo(Object o) {
        Column column = (Column)o;
        return getLabel().compareTo(column.getLabel());
    }
}
