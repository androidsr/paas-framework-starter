package paas.framework.spring.ai;

public enum FilterType {
    StartsWith(1, "指定字符开头"),
    EndsWith(2, "指定字符结束"),
    Text(3, "获取文本"),
    Contains(4, "包含指定字符"),


    ;

    private int code;
    private String title;

    FilterType(int code, String title) {
        this.code = code;
        this.title = title;
    }

    public int getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }
}
