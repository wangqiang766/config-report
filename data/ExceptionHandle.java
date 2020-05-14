package com.wq.multicommon.exceptions;


import com.wq.multicommon.common.Result;
import com.wq.multicommon.enums.SystemExceptionEnum;
import com.wq.multicommon.utils.ResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by 1 on 2017-08-16.
 */
@ControllerAdvice
public class ExceptionHandle {

    private final static Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);

    /**
     * 系统异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result handle(Exception e) {
/*    	 instanceof运算符用法
         运算符是双目运算符,左面的操作元是一个对象,右面是一个类.当
    	 左面的对象是右面的类创建的对象时,该运算符运算的结果是true,否则是false  */
        //添加参数不合法校验异常捕获
        if (e instanceof MethodArgumentNotValidException) {
            BindingResult bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
            logger.info(">>>>>>>>>>MethodArgumentNotValidException异常处理<<<<<<<<<<" + bindingResult.getFieldError().getDefaultMessage());
            return ResultUtils.error(SystemExceptionEnum.PARAMS_WRONG.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        if (e instanceof UserException) {
            UserException userException = (UserException) e;
            logger.info(">>>>>>>>>>userException异常处理<<<<<<<<<<" + userException.getMsg());
            return ResultUtils.result(userException.getCode(), userException.getMsg(), userException.getData());
        } else if (e instanceof MissingServletRequestParameterException) {
            MissingServletRequestParameterException missingServletRequestParameterException = (MissingServletRequestParameterException) e;
            logger.info(">>>>>>>>>>MissingServletRequestParameterException异常处理<<<<<<<<<<" + missingServletRequestParameterException.getMessage());
            return ResultUtils.error("-1", "请求参数缺失！");
        } else {
            logger.error("【系统异常】{}", e);
            return ResultUtils.error("-1", "未知错误");
        }
    }


}
