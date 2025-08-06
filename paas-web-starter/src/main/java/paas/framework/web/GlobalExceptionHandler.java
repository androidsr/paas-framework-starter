package paas.framework.web;

import cn.dev33.satoken.jwt.exception.SaJwtException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import paas.framework.model.enums.ResultMessage;
import paas.framework.model.exception.BusException;
import paas.framework.model.web.HttpResult;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 统一异常处理
 *
 * @author sirui
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public HttpResult handleException(Exception e) {
        HttpResult response = new HttpResult();
        response.setCode(ResultMessage.UNKNOW_ERROR.getCode());
        response.setMsg(ResultMessage.UNKNOW_ERROR.getMessage());
        log.error("响应编码：{}，响应消息：{}，异常信息：{}", response.getCode(), response.getMsg(), e);
        return response;
    }

    @ExceptionHandler(BusException.class)
    @ResponseBody
    public HttpResult handleBusinessException(BusException e) {
        HttpResult response = new HttpResult();
        response.setCode(e.getCode());
        response.setMsg(e.getMessage());
        log.error("响应编码：{}，响应消息：{}，异常信息：{}", response.getCode(), response.getMsg(), e);
        return response;
    }

    @ExceptionHandler(SaJwtException.class)
    @ResponseBody
    public HttpResult handleSaJwtException(SaJwtException e) {
        HttpResult response = new HttpResult();
        response.setCode(401);
        response.setMsg("认证已过期");
        log.error("认证已过期：{}", e.getMessage(), e);
        return response;
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public HttpResult handleValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getFieldErrors();
        log.error("请求参数校验失败： -> {}", fieldErrors);
        return HttpResult.fail(500, "请求参数校验失败：" + fieldErrors);
    }

    @ResponseBody
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public HttpResult handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("无法读取请求消息体： -> {}", e);
        return HttpResult.fail(500, "无法读取请求消息体：" + e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public HttpResult handleHttpMediaTypeNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("不支持的请求方法：{} -> {}", e.getMethod(), e);
        return HttpResult.fail(500, "不支持的请求方法：" + e.getMethod());
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public HttpResult handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("请求参数类型不匹配：{} -> {}", e.getName(), e);
        return HttpResult.fail(405, "请求参数类型不匹配：" + e.getName());
    }

    @ResponseBody
    @ExceptionHandler(NoResourceFoundException.class)
    public HttpResult handleNoResourceFoundExceptionException(NoResourceFoundException e) {
        log.error("请求地址不存在 -> {}", e);
        return HttpResult.fail(404, "请求地址不存在");
    }

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException ex) {
        // 处理约束验证异常
        List<String> errorMessages = ex.getConstraintViolations().stream().map(violation -> violation.getPropertyPath() + ": " + violation.getMessage()).collect(Collectors.toList());
        return ResponseEntity.badRequest().body("请求参数校验失败：" + errorMessages);
    }

}