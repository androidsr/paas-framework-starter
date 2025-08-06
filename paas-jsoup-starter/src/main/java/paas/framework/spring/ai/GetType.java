package paas.framework.spring.ai;

public enum GetType {
    Attr(1, "属性"),
    Text(2, "文本"),
    Html(3, "网页"),

    ;

    private int code;
    private String title;

    GetType(int code, String title) {
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
