package paas.framework.spring.ai;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JsoupContent {

    /**
     * 内容过虑器
     */
    TextFilter textFilter;

    /**
     * 字段名称
     */
    private String name;

    /**
     * 1 文本,2 属性
     */
    private GetType type;

    /**
     * 节点查找器
     */
    private String findLabel;
    /**
     * 属性名称
     */
    private String attrName;

    public JsoupContent(TextFilter textFilter, String name, GetType type, String findLabel) {
        this.textFilter = textFilter;
        this.name = name;
        this.type = type;
        this.findLabel = findLabel;
    }

    public JsoupContent(TextFilter textFilter, String name, GetType type, String attrName, String findLabel) {
        this.textFilter = textFilter;
        this.name = name;
        this.type = type;
        this.findLabel = findLabel;
        this.attrName = attrName;
    }
}
