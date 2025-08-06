package paas.framework.mybatis;

import org.apache.ibatis.exceptions.PersistenceException;
import paas.framework.model.exception.BusException;

import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseExceptionHandler {
    /**
     * 统一处理由MyBatis抛出的PersistenceException，并返回友好的错误提示
     *
     * @param e   PersistenceException 异常对象
     * @param msg 自定义的提示信息
     */
    public static void handleDatabaseException(PersistenceException e, String msg) {
        // 检查 PersistenceException 的根本原因是否是 SQLException
        Throwable cause = e.getCause();
        if (cause instanceof SQLException) {
            SQLException sqlException = (SQLException) cause;
            handleDatabaseException(sqlException, msg);
        } else {
            // 如果不是 SQLException，则直接抛出友好的业务异常
            BusException.fail(60001, "数据库操作失败，请稍后重试！");
        }
    }

    /**
     * 统一处理数据库操作异常并返回友好的错误提示
     *
     * @param e   SQLException 异常对象
     * @param msg 自定义的提示信息
     */
    public static void handleDatabaseException(SQLException e, String msg) {
        // 判断异常类型并根据不同情况给出友好的提示
        if (e instanceof SQLIntegrityConstraintViolationException) {
            handleIntegrityConstraintViolation((SQLIntegrityConstraintViolationException) e, msg);
        } else if (e instanceof SQLTimeoutException) {
            BusException.fail(60001, "数据库操作超时，请检查数据库性能或网络连接！");
        } else if (e instanceof SQLNonTransientConnectionException) {
            BusException.fail(60001, "无法连接到数据库，请稍后再试！");
        } else if (e instanceof SQLSyntaxErrorException) {
            BusException.fail(60001, "SQL语法错误，请检查数据库操作的SQL语句！");
        } else if (e instanceof SQLDataException) {
            BusException.fail(60001, String.format("数据类型不匹配，请检查输入的数据格式;%s", msg));
        } else if (e instanceof SQLFeatureNotSupportedException) {
            BusException.fail(60001, String.format("数据库不支持此功能，请检查数据库版本或查询文档;%s", msg));
        } else if (e instanceof SQLTransientException) {
            BusException.fail(60001, "数据库暂时无法连接，请稍后再试！");
        } else if (e instanceof SQLInvalidAuthorizationSpecException) {
            BusException.fail(60001, "用户名或密码无效，或者没有执行此操作的权限！");
        } else if (e instanceof SQLRecoverableException) {
            BusException.fail(60001, "数据库连接已丢失，正在尝试重连，请稍后重试！");
        } else if (e.getMessage().contains("Column") && e.getMessage().contains("cannot be null")) {
            handleNullValueViolation(e, msg);
        } else if (e.getMessage().contains("for column") && e.getMessage().contains("Incorrect decimal value")) {
            BusException.fail(60001, String.format("插入失败：数据类型不正确,必需是数字类型;%s", msg));
        } else if (e.getMessage().contains("Out of range value")) {
            BusException.fail(60001, String.format("插入失败：字段的值超出允许范围，请检查数据大小；%s", msg));
        } else if (e.getMessage().contains("Incorrect date value")) {
            BusException.fail(60001, String.format("插入失败：日期格式不正确，请输入有效的日期；%s", msg));
        } else if (e.getMessage().contains("Data too long")) {
            BusException.fail(60001, String.format("插入失败：字段的字符长度超出限制，请检查数据长度；%s", msg));
        } else if (e.getMessage().contains("Truncated incorrect")) {
            BusException.fail(60001, String.format("插入失败：数据类型不匹配或数值溢出，请检查数据格式；%s", msg));
        } else {
            // 捕获其他类型的 SQLException
            BusException.fail(60001, "数据库操作失败，请稍后重试！");
        }
    }


    /**
     * 处理违反数据库约束的异常
     * 如：数据重复、外键约束等
     */
    private static void handleIntegrityConstraintViolation(SQLIntegrityConstraintViolationException e, String msg) {
        String message = e.getMessage();
        if (message.contains("Duplicate entry")) {
            BusException.fail(60001, String.format("数据重复;%s", msg));
        }
        // 处理外键约束失败
        else if (message.contains("foreign key constraint fails")) {
            BusException.fail(60001, String.format("外键约束错误;%s", msg));
        }
        // 其他约束错误
        else {
            BusException.fail(60001, String.format("违反数据库约束;%s", msg));
        }
    }

    /**
     * 处理字段不能为空的错误
     * 如果数据库约束要求某些字段不为null
     */
    public static void handleNullValueViolation(SQLException e, String msg) {
        Pattern pattern = Pattern.compile("Column '(\\w+)' cannot be null");
        Matcher matcher = pattern.matcher(e.getMessage());

        if (matcher.find()) {
            String columnName = matcher.group(1);
            BusException.fail(60001, String.format("字段【%s】不能为空;%s", columnName, msg));
        } else {
            // 默认的错误提示
            BusException.fail(60001, String.format("字段不能为空;%s", msg));
        }
    }

}
