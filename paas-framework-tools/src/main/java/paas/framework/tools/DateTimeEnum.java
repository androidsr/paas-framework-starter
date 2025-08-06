package paas.framework.tools;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
public enum DateTimeEnum {
    Y_M_D_H_M_S(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
    Y_M_D_H_M(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
    Y_M_D_H(DateTimeFormatter.ofPattern("yyyy-MM-dd HH")),
    H_M_S(DateTimeFormatter.ofPattern("HH:mm:ss")),
    H_M(DateTimeFormatter.ofPattern("HH:mm")),
    YMDHMS(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
    YMDHM(DateTimeFormatter.ofPattern("yyyyMMddHHmm")),
    YMDH(DateTimeFormatter.ofPattern("yyyyMMddHH")),
    HMS(DateTimeFormatter.ofPattern("HHmmss")),
    HM(DateTimeFormatter.ofPattern("HHmm")),
    H(DateTimeFormatter.ofPattern("HH")),
    M(DateTimeFormatter.ofPattern("mm")),
    S(DateTimeFormatter.ofPattern("ss")),
    ;
    DateTimeFormatter formatter;
}
