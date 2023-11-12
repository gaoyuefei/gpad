package com.gpad.gpadtool.constant;

import org.omg.CORBA.UNKNOWN;

public enum CommCode {

    SUCCESS(10000000, "comm.success", "成功"),
    ERROR(10000001, "comm.error", "系统错误"),
    UNKNOW(10000002, "comm.unknow", "未知"),
    INTFR_INNER_INVOKE_ERROR(10000003, "comm.intfr.inner.invoke.error", "内部系统接口调用异常"),
    INTFR_OUTTER_INVOKE_ERROR(10000004, "comm.intfr.outter.invoke.error", "外部系统接口调用异常"),
    INTFR_FORBID_VISIT(10000005, "comm.intfr.forbid.visit", "该接口禁止访问"),
    INTFR_ADDRESS_INVALID(10000006, "comm.intfr.address.invalid", "接口地址无效"),
    INTFR_REQUEST_TIMEOUT(10000007, "comm.intfr.request.timeout", "接口请求超时"),
    INTFR_EXCEED_LOAD(10000008, "comm.intfr.exceed.load", "接口负载过高"),
    JVM_OUT_OF_MEMORY(10000051, "comm.jvm.out.of.memory", "内存溢出"),
    JVM_STACK_OVER_FLOW(10000052, "comm.jvm.stack.over.flow", "栈溢出"),
    NETWORK_CONNECT_TIMED_OUT(10000101, "comm.network.connect.timed.out", "连接超时"),
    NETWORK_READ_TIMED_OUT(10000102, "comm.network.read.timed.out", "读取超时"),
    NETWORK_CONNECTION_REFUSED(10000103, "comm.network.connection.refused", "连接拒绝"),
    NETWORK_NO_ROUTE_TO_HOST(10000104, "comm.network.no.route.to.host", "网络不可达"),
    NETWORK_CLIENT_ABORT(10000105, "comm.network.client.abort", "客户端主动断开"),
    NETWORK_CONNECTION_RESET(10000106, "comm.network.connection.reset", "连接重置"),
    NETWORK_CONNECTION_RESET_BY_PEER(10000107, "comm.network.connection.reset.by.peer", "连接重置"),
    NETWORK_SOCKET_IS_CLOSED(10000108, "comm.network.socket.is.closed", "连接已关闭"),
    NETWORK_BROKEN_PIPE(10000109, "comm.network.broken.pipe", "通信管道已损坏"),
    NETWORK_TOO_MANY_OPEN_FILES(10000110, "comm.network.too.many.open.files", "文件句柄数过多"),
    NETWORK_RECV_FAILED(10000111, "comm.network.recv.failed", "socket读取异常"),
    NETWORK_DOES_NOT_HAVE_AVAILABLE_SERVER(10000112, "comm.network.does.not.have.available.server", "没有可用的服务"),
    NETWORK_UNKNOWN(10000149, "comm.network.unknown", "其他未知异常"),
    SQL_ACCESS_DENIED(10000151, "comm.sql.access.denied", "SQL拒绝访问"),
    SQL_COULD_NOT_ESTABLISH(10000152, "comm.sql.could.not.establish", "DB无法连接"),
    SQL_UNKNOWN_DATABASE(10000153, "comm.sql.unknown.database", "未知DB"),
    SQL_TABLE_DOES_NOT_EXIST(10000154, "comm.sql.table.does.not.exist", "数据表不存在"),
    SQL_UNKNOWN_COLUMN(10000155, "comm.sql.unknown.column", "列不存在"),
    SQL_ERROR_IN_SQL_SYNTAX(10000156, "comm.sql.error.in.sql.syntax", "SQL语法错误"),
    SQL_INTEGRITY_CONSTRAINT_VIOLATION(10000157, "comm.sql.integrity.constraint.violation", "约束冲突"),
    SQL_QUERY_TIMEOUT(10000158, "comm.sql.query.timeout", "SQL执行超时"),
    SQL_LOGIN_TIMEOUT(10000159, "comm.sql.login.timeout", "SQL登录超时"),
    SQL_DATA_TRUNCATION(10000160, "comm.sql.data.truncation", "SQL内容截断"),
    SQL_TRANSACTION_ROLLBACK(10000161, "comm.sql.transaction.rollback", "SQL事务回滚"),
    SQL_FULL_TABLE_SCAN(10000162, "comm.sql.full.table.scan", "SQL全表扫描"),
    SQL_GET_CONNECTION_FAIL(10000163, "comm.sql.get.connection.timeout", "获取连接失败"),
    SQL_STATEMENT_CANCELLED(10000164, "comm.sql.statement.cancelled", "SQL指令被取消"),
    SQL_TOO_MANY_RESULT(10000165, "comm.sql.too.many.result", "结果数过多"),
    SQL_DATA_CONVERSION(10000166, "comm.sql.data.conversion", "数据格式异常"),
    SQL_CONNECTION_IS_CLOSED(10000167, "comm.sql.connection.is.closed", "连接已被断开"),
    SQL_PARAM_IS_INVALID(10000166, "comm.sql.data.conversion", "参数异常"),
    SQL_UNKNOWN(10000199, "comm.sql.unknown", "其他未知异常"),
    REDIS_CONNECT_TIMED_OUT(10000201, "comm.redis.connect.timed.out", "redis连接超时"),
    REDIS_COMMAND_TIMED_OUT(10000202, "comm.redis.command.timed.out", "redis指令超时"),
    REDIS_COMMAND_WRITE_INTO_CONNECTION_FAIL(10000203, "comm.redis.command.write.into.connection.fail", "指令无法写入连接"),
    REDIS_ATTEMPT_TO_UNLOCK_LOCK(10000204, "comm.redis.attempt.to.unlock.lock", "释放锁失败"),
    REDIS_BUSY(10000205, "comm.redis.busy", "Redis繁忙"),
    REDIS_CLUSTER_DOWN(10000206, "comm.redis.cluster.down", "Redis集群失效"),
    REDIS_UNABLE_TO_CONNECT(10000207, "comm.redis.unable.to.connect", "Redis无法连接"),
    PARAM_IS_INVALID(10000501, "comm.param.is.invalid", "参数无效"),
    PARAM_IS_BLANK(10000502, "comm.param.is.blank", "参数为空"),
    PARAM_LENGTH_OVER(10000503, "comm.param.length.over", "参数长度过长"),
    INVALID_DATE_STRING(10000504, "comm.param.date.format.error", "输入日期格式不对"),
    METHOD_IS_NOT_SUPPORT(10000505, "comm.para.method.is.not.support", "请求方法不支持"),
    USER_NOT_LOGIN(10001001, "comm.user.not_login", "用户未登录"),
    USER_LOGIN_ERROR(10001002, "comm.user.login.error", "账号不存在或密码错误"),
    USER_ACCOUNT_FORBIDDEN(10001003, "comm.user.account.forbidden", "账号已被禁用"),
    USER_NOT_EXIST(10001004, "comm.user.login.credential.existed", "用户不存在"),
    USER_LOGIN_CREDENTIAL_EXISTED(10001005, "comm.user.not.exist", "凭证已存在"),
    USER_LOGIN_TOKEN_EMPTY(10001006, "comm.user.login.token.empty", "登陆token为空"),
    USER_LOGIN_TOKEN_TIMEOUT(10001007, "comm.user.login.token.timeout", "token已过期"),
    TOKEN_ERROR(10001008, "comm.user.token.error", "该Token无效"),
    USER_NO_PERMISSION(10001009, "comm.user.no.permission", "没有访问该资源的权限"),
    USER_NO_PERMISSION_FORMAT(10001010, "comm.user.no.permission.format", "没有访问{0}资源的权限"),
    DATA_NOT_FOUND(10002001, "comm.data.not.found", "数据不存在"),
    DATA_IS_WRONG(10002002, "comm.data.is.wrong", "数据有误"),
    DATA_UPDATE_WRONG(10002003, "comm.data.update.wrong", "数据更新出错"),
    DATA_ALREADY_EXISTED(10002004, "comm.data.already.existed", "数据已存在"),
    DATA_SERIALIZE_ERROR(10002005, "comm.data.already.existed", "序列化失败"),
    DATA_UNSERIALIZE_ERROR(10002006, "comm.data.already.existed", "反序列化失败"),
    UNKNOWN_REAL_NAME(10002007, "comm.data.not.found", "反序列化失败"),
    IDENTITY_CARD_SIGN_WRONG(10002009, "comm.data.not.found", "君子签校验身份信息失败"),
    IDENTITY_WRITEPNG_SIGN_WRONG(100020012, "comm.data.not.found", "君子签签名序列化失败"),
    IDENTITY_WRITEPNG_SIGN_INFO_WRONG(100020013, "comm.data.not.found", "君子签签名二要素认证失败，请联系管理人员"),
    IDENTITY_WRITEPNG_SIGN_INFO_FAIL(100020014, "comm.data.not.found", "君子签发起失败"),
    UPLOAD_SIGN_PNG_WRONG(10002010, "comm.data.not.found", "上传个人签名失败");



    private Integer code;
    private String name;
    private String message;

    private CommCode(Integer code, String name, String msg) {
        this.code = code;
        this.name = name;
        this.message = msg;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public String getMessage() {
        return this.message;
    }

}
