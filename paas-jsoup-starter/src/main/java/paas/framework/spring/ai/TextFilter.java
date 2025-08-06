package paas.framework.spring.ai;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextFilter {
    private FilterType filterType;
    private List<String> filterText;
}
