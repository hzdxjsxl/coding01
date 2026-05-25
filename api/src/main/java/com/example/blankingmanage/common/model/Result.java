package com.example.blankingmanage.common.model;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
public  class Result<T> implements Serializable {
    /**
     * 错误码，0代表成功，其他代表失败 ，-1
     */
    @Schema(description = "状态码 (0:成功, 其他:失败)", example = "0")
    protected int code;

    /**
     * 错误信息，对应错误码
     */
    @Schema(description = "提示信息", example = "操作成功")
    protected String msg;

    /**
     * 业务数据
     */
    @Schema(description = "业务数据")
    protected T data;

    //高频对象，默认成功
    public static final Result SUCC = Result.success("成功");

    //高频对象，失败的默认对象
    public static final Result FAIL = Result.failed(0, "本次操作无信息"); //本次操作无信息或者系统错误

    //高频对象，参数失败
    public static final Result INVALID_PARAM = Result.failed("参数错误(失败)");

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public Result() {
    }

    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 调用接口成功，不代表业务返回成功的数据
     *
     * @return
     */
    public boolean success() {
        return code == 0;
    }

    /**
     * 调用接口失败
     *
     * @return
     */
    public boolean failed() {
        return code != 0;
    }

    /**
     * 调用接口执行成功，且返回的数据不为空，防止空指针的问题
     *
     * @return
     */
    public boolean ok() {
        return success() && data != null;
    }


    private static <T> Result<T> wrap(int code, String msg, T data) {
        return new Result<T>(code, msg, data);
    }

    public static <T> Result<T> success(T data) {
        return wrap(0, "", data);
    }

    public static <E> Result<E> failed(int code, String msg) {
        return wrap(code, msg, null);
    }

    public static <E> Result<E> failed(String msg) {
        return failed(1, msg);
    }

    public static <T> Result<T> failed(int code, String msg, T data) {
        return wrap(code, msg, data);
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
