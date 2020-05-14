package com.wq.multiuser.aop;



import com.wq.multicommon.annotation.SystemControllerLog;
import com.wq.multicommon.annotation.SystemServiceLog;
import com.wq.multicommon.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Enumeration;

/**
* @Description:    日志切面打印
* @Author:         wangqiang01
* @CreateDate:     2020/5/5
* @Version:        1.0
*/
@Aspect
@Component
@Slf4j
public class SystemLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(SystemLogAspect.class);

    /**
     * Controller层切点
     */
    @Pointcut("@annotation(com.wq.multicommon.annotation.SystemControllerLog)")
    public void controllerAspect() {
    }

    /**
     * Service层切点
     */
    @Pointcut("@annotation(com.wq.multicommon.annotation.SystemControllerLog)")
    public void serviceAspect() {
    }

    /**
     * 前置通知 用于拦截Controller层记录用户的操作
     *
     * @param joinPoint 切点
     */
    @Before("controllerAspect()")
    public void doBefore(JoinPoint joinPoint) {
        logger.info("=========================请求开始=========================");
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            // 收到请求记录内容
            logger.info("URL:{}, METHOD:{}, IP:{}", request.getRequestURL(), request.getMethod(), request.getRemoteAddr());
            Enumeration<String> enums = request.getParameterNames();
            while (enums.hasMoreElements()) {
                String name = enums.nextElement();
                logger.info("name:{},value:{}", name, request.getParameter(name));
            }
        } catch (Exception e) {
            //记录本地异常日志    
            log.error("==前置通知异常==");
            log.error("异常信息:{}", e.getMessage());
        }
    }

    /**
     * 后续通知 用于拦截Controller层记录用户的操作
     *
     *
     */
    @AfterReturning(returning = "result", pointcut = "controllerAspect()")
    public void doAfter(Object result) {
        try {
            logger.info("RESPONSE : ", result);
        } catch (Exception e) {
            //记录本地异常日志
            log.error("==后续通知异常==");
            log.error("异常信息:{}", e.getMessage());
        }
        logger.info("=========================请求结束=========================");
    }

    /**
     * 异常通知 用于拦截service层记录异常日志
     *
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(pointcut = "serviceAspect()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable e) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //获取请求ip    
        String ip = request.getRemoteAddr();
        //获取用户请求方法的参数并序列化为JSON格式字符串
        String params = "";
        if (joinPoint.getArgs() != null && joinPoint.getArgs().length > 0) {
            for (int i = 0; i < joinPoint.getArgs().length; i++) {
                params += JsonUtil.toJson(joinPoint.getArgs()[i]) + ";";
            }
        }
        try {
            log.error("异常代码:{}", e.getClass().getName());
            log.error("异常信息:{}", e.getMessage());
            log.error("异常方法:{}", (joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()"));
            log.error("方法描述:{}", getServiceMethodDescription(joinPoint));
            log.error("请求IP:{}", ip);
            log.error("请求参数:{}", params);
        } catch (Exception ex) {
            //记录本地异常日志    
            log.error("==异常通知异常==");
            log.error("异常信息:{}", ex.getMessage());
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于service层注解
     *
     * @param joinPoint 切点
     * @return 方法描述
     * @throws Exception
     */
    public static String getServiceMethodDescription(JoinPoint joinPoint)
            throws Exception {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        String description = "";
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    description = method.getAnnotation(SystemServiceLog.class).description();
                    break;
                }
            }
        }
        return description;
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param joinPoint 切点
     * @return 方法描述
     * @throws Exception
     */
    public static String getControllerMethodDescription(JoinPoint joinPoint) throws Exception {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        String description = "";
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    description = method.getAnnotation(SystemControllerLog.class).description();
                    break;
                }
            }
        }
        return description;
    }

}
