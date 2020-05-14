package com.wq.multicommon.enums;

/**
 * Created by HX on 2018/12/10.
 * 系统异常枚举类
 */
public enum SystemExceptionEnum {
        SUCCESS("0","请求处理成功"),
        DOING("010","请求处理中"),
        PARAMS_WRONG("-102","参数为空或不合法"),
        SYSTEM_ERROR("-100","请求失败，系统异常");

        private String code;

        private String msg;

        /**
         * @return the code
         */
        public String getCode() {
            return code;
        }

        /**
         * @param code the code to set
         */
        public void setCode(String code) {
            this.code = code;
        }

        /**
         * @return the msg
         */
        public String getMsg() {
            return msg;
        }

        /**
         * @param msg the msg to set
         */
        public void setMsg(String msg) {
            this.msg = msg;
        }

        SystemExceptionEnum(String code, String msg) {
            this.code = code;
            this.msg = msg;
        }
}
