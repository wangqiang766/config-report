package com.wq.multicommon.exceptions;

/**
 * 自定义异常 类型为非检查异常,避开throws
 * 
 */
public class BusinessException extends RuntimeException {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -2878090452790426501L;
	/**
	 * 错误编码
	 */
	private String errorCode;

	/**
	 * 构造一个基本异常.
	 * 
	 * @param message
	 *            信息描述
	 */
	public BusinessException(String message) {
		super(message);
	}

	/**
	 * 构造一个基本异常.
	 * 
	 * @param errorCode
	 *            错误编码
	 * @param message
	 *            信息描述
	 */
	public BusinessException(String errorCode, String message) {
		super(message);
		this.setErrorCode(errorCode);
	}

	/**
	 * 构造一个基本异常.
	 * 
	 * @param errorCode
	 *            错误编码
	 * @param message
	 *            信息描述
	 * @param cause
	 *            根异常类（可以存入任何异常）
	 */
	public BusinessException(String errorCode, String message, Throwable cause) {
		super(message, cause);
		this.setErrorCode(errorCode);
	}

	/**
	 * 构造一个基本异常.
	 * 
	 * @param message
	 *            信息描述
	 * @param cause
	 *            根异常类（可以存入任何异常）
	 */
	public BusinessException(String message, Throwable cause) {
		super(message, cause);
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
}
