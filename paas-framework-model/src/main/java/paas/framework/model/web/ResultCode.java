package paas.framework.model.web;

import java.io.Serializable;

public interface ResultCode extends Serializable {
    int getCode();

    String getMessage();
}