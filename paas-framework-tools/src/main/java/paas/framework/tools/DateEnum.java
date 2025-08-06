package paas.framework.tools;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
public enum DateEnum {
    Y_M_D(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
    YMD(DateTimeFormatter.ofPattern("yyyyMMdd")),
    M_D(DateTimeFormatter.ofPattern("MM-dd")),
    MD(DateTimeFormatter.ofPattern("MMdd")),
    M(DateTimeFormatter.ofPattern("MM")),
    D(DateTimeFormatter.ofPattern("dd")),
    ;
    DateTimeFormatter formatter;
}
