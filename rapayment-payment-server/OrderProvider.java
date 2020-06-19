package com.hxyl.bbp.order.feign.provider;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hxyl.bbp.common.common.ResultObj;
import com.hxyl.bbp.common.common.exceptions.BusinessException;
import com.hxyl.bbp.common.dto.LevelDataCache;
import com.hxyl.bbp.common.enums.OrderPoolStatusEnum;
import com.hxyl.bbp.common.feign.order.client.OrderClient;
import com.hxyl.bbp.common.feign.order.dto.req.CommitOrderReq;
import com.hxyl.bbp.common.feign.order.dto.req.CreateEqhOrderReq;
import com.hxyl.bbp.common.utils.BbpObjectUtil;
import com.hxyl.bbp.order.common.constant.OrderPoolConstant;
import com.hxyl.bbp.order.common.constant.RedisKeyConstant;
import com.hxyl.bbp.order.common.enums.EqhPdfFileTypeEnum;
import com.hxyl.bbp.order.common.enums.ErrorCodes;
import com.hxyl.bbp.order.common.util.RedisUtil;
import com.hxyl.bbp.order.domain.entity.AssetOrderPool;
import com.hxyl.bbp.order.domain.entity.FjAuthInfo;
import com.hxyl.bbp.order.domain.entity.UserCardInfo;
import com.hxyl.bbp.order.domain.entity.UserInfo;
import com.hxyl.bbp.order.domain.eqd.entity.OrderPool;
import com.hxyl.bbp.order.mapper.eqd.LevelDataCacheDictMapper;
import com.hxyl.bbp.order.service.AssetOrderPoolService;
import com.hxyl.bbp.order.service.UserCardInfoService;
import com.hxyl.bbp.order.service.UserInfoService;
import com.hxyl.bbp.order.service.eqd.OrderPoolService;
import com.hxyl.bbp.order.service.eqd.PmsService;
import com.hxyl.bbp.order.service.eqd.file.EqhPdfDataHolderService;
import com.hxyl.bbp.order.service.fengjin.FjAuthInfoService;
import com.miaofq.pms.loancenter.req.ProductInfoReq;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class OrderProvider implements OrderClient {

	@Autowired
	private AssetOrderPoolService assetOrderPoolService;

	@Autowired
	private FjAuthInfoService fjAuthInfoService;

	@Autowired
	private UserCardInfoService userCardInfoService;

	@Autowired
	private UserInfoService userInfoService;

	@Autowired
	private OrderPoolService orderPoolService;

	@Autowired
	private LevelDataCacheDictMapper levelDataCacheDictMapper;
	@Autowired
	private PmsService pmsService;
	
	@Autowired
	private EqhPdfDataHolderService eqhPdfDataHolderService;

	@Autowired
	private RedisUtil redisUtil;


	@Override
	public Integer commitOrder(CommitOrderReq commitOrderReq) {
		log.info("OrderProvider commitOrder req:{}", commitOrderReq);

		// 检查用户是否有订单在库
		assetOrderPoolService.checkUserOrder(commitOrderReq.getUserId());

		// 借款金额检查
		if (commitOrderReq.getBorrowAmount() % 100 != 0) {
			throw new BusinessException("您的借款金额必须是100的倍数");
		}

		// 系统最大额度
		if (commitOrderReq.getBorrowAmount() > 20000000) {
			throw new BusinessException("您目前最高可借款额度不能超过" + (20000000 / 100) + "元。");
		}

		/* ============   2.生成借款总订单与分期订单   ============ */
		// 查询有效的用户银行卡信息
		UserCardInfo parmCard = new UserCardInfo();
		parmCard.setType(2); // 借记卡
		parmCard.setUserId(commitOrderReq.getUserId());
		parmCard.setDefaultStatus(1); // 默认卡
		QueryWrapper<UserCardInfo> queryCard = new QueryWrapper<UserCardInfo>(parmCard);
		UserCardInfo userCardInfo = userCardInfoService.selectOne(queryCard);

		if (null == userCardInfo) {
			throw new BusinessException("请先设置收款银行");
		}
		AssetOrderPool assetOrderPool = assetOrderPoolService.createOrder(commitOrderReq, userCardInfo);

		return assetOrderPool.getId();
	}

	@Override
	public Integer getOrderStatus(String userPhone) {
		UserInfo userInfo = userInfoService.selectByPhone(userPhone);
		AssetOrderPool orderPool = assetOrderPoolService.selectByUserId(userInfo.getId());
		Integer status = null;
		if (null == orderPool) {
			FjAuthInfo fjAuthInfo = fjAuthInfoService.selectbyUserId(userInfo.getId());

			status = null == fjAuthInfo ? null : fjAuthInfo.getStatus();
		} else {
			status = orderPool.getStatus();
		}
		return status;
	}

	@Override
	public ResultObj createEqhOrder(CreateEqhOrderReq createEqhOrderReq) {
		log.info("createEqhOrder req:{}", createEqhOrderReq);
		try {
			// 确认是否有订单正在创建 简单锁防止重复提交
			boolean redisFlag = redisUtil.setNx(RedisKeyConstant.ORDER_CREATE_LOCK + createEqhOrderReq.getUserGuid(),
					RedisKeyConstant.ORDER_CREATE_LOCK, 60);
			if (redisFlag) {
				log.info("===createEqhOrder加redis锁成功===");
			} else {
				log.error("===用户:{}已有createEqhOrder任务在运行，redis锁没有失效,本次任务结束", createEqhOrderReq.getUserGuid());
				return ResultObj.buildExceptionMsg("正在处理请不要重复提交");
			}
			// 检查是否有订单在库
			OrderPool orderPool = orderPoolService.selectByUserGuid(createEqhOrderReq.getUserGuid());
			if (BbpObjectUtil.isNotNull(orderPool)) {
				Integer status = orderPool.getStatus();
				OrderPoolStatusEnum statusEnum = OrderPoolStatusEnum.getByCode(status);
				if (OrderPoolConstant.ORDERING_YES == statusEnum.getType()) {
					log.error("===用户:{}已有已有订单在库(ordering),本次任务结束", createEqhOrderReq.getUserGuid());
					return ResultObj.buildExceptionMsg("该用户已有订单在库");
				}
			}
			// 检查是否有默认卡
			// UserBankCardInfo cardInfo =
			// userBankCardInfoService.selectByDefaultBank(createEqhOrderReq.getUserGuid());
			/*if (BbpObjectUtil.isNull(cardInfo)) {
				return ResultObj.buildExceptionMsg("该用户已未设置默认银行卡");
			}*/
			// 写入订单
			orderPoolService.createOrder(createEqhOrderReq);
			return ResultObj.buildSuccessMsg();
		} catch (Exception e) {
			log.info("createEqhOrder exception:{}", createEqhOrderReq);
			return ResultObj.buildExceptionMsg(e.getMessage());
		} finally {
			redisUtil.del(RedisKeyConstant.ORDER_CREATE_LOCK + createEqhOrderReq.getUserGuid());
		}
	}

	@Override
	public List getStratifiedata(String type) {
		log.info("获取层级基础数据入参type：{}", type);
		if (BbpObjectUtil.isNull(type)) {
			return null;
		}
		// 从redis缓存中取数据，有返回，没有从数据库撸数据(redis数据有刷新机制)
		Object obj = redisUtil.get(type);
		if (obj != null) {
			return (List<LevelDataCache>) obj;
		}
		// 查询type类型有效状态的基础数据
		LevelDataCache levelDataCache = new LevelDataCache();
		levelDataCache.setState(1);
		levelDataCache.setType(type);
		List<LevelDataCache> allLevelDataCaches = levelDataCacheDictMapper
				.selectList(new QueryWrapper<>(levelDataCache));
		Integer minLevel = allLevelDataCaches.stream().sorted((a, b) -> a.getLevel() - b.getLevel()).findFirst().get()
				.getLevel();
		Integer maxLevel = allLevelDataCaches.stream().sorted((a, b) -> b.getLevel() - a.getLevel()).findFirst().get()
				.getLevel();
		List<LevelDataCache> newList = divileData(allLevelDataCaches, maxLevel, minLevel);
		// rides存入最新的基础数据
		redisUtil.set(type, newList);
		return newList;
	}

	/**
	 * 生成分层数据
	 * @updateby wangqiang/hx0015699
	 * @param list
	 * @param maxLevel 当前type类型数据最大level 比如省市区 最大level=3(值最大)
	 * @param minLevel 当前type类型数据最小level 比如1(值最小)
	 * @return
	 */
	private List divileData(List<LevelDataCache> list, Integer maxLevel, Integer minLevel) {
		List<LevelDataCache> currentLevelDatas = null;
		Integer nextLevel = minLevel + 1;
		if (nextLevel > maxLevel) {
			return currentLevelDatas;
		}
		currentLevelDatas = list.stream().filter(a -> a.getLevel() == minLevel).collect(Collectors.toList());
		List<LevelDataCache> nextLevelDatas = list.stream().filter(a -> a.getLevel() == minLevel + 1)
				.collect(Collectors.toList());
		currentLevelDatas.stream().forEach(parent -> {
			if (parent.getChild() == null) {
				parent.setChild(new ArrayList<>());
			}
			nextLevelDatas.stream().forEach(child -> {
				if (null != parent.getCode() && parent.getCode().equals(child.getParentCode())) {
					parent.getChild().add(child);
				}
			});
		});
		divileData(list, maxLevel, nextLevel);
		return currentLevelDatas;
	}

	@Override
	public String getRateInfo(String productCode, Integer period, BigDecimal creditMoney) {
		ProductInfoReq productInfoReq = new ProductInfoReq();
		productInfoReq.setProductCode(productCode);
		productInfoReq.setLoanPeriods(period);
		productInfoReq.setCreditAmount(creditMoney);
		return pmsService.getPmsProductInfo(productInfoReq);
	}

	@Override
	public Map<String, Object> contractData(String userGuid, String appName, Integer loanPeriods, Integer type,Integer creditMoney,String loanUsed) {
		log.info("查询合同数据接口，userId:{}，appName:{}，loanPeriods:{}，type:{},creditMoney:{},loanUsed：{}", userGuid, appName, loanPeriods, type,creditMoney,loanUsed);
		Map<String, Object> returnMap = new HashMap<String, Object>();
		if (userGuid == null || type == null) {
			returnMap.put("ResultType", ErrorCodes.FAILURE.getCode());
			returnMap.put("Message", "参数不能为空");
			return returnMap;
		}
		Map<String, Object> dataMap = null;
		try {
			if (EqhPdfFileTypeEnum.XSRZFWXY.getCode().equals(type)) {
				dataMap = eqhPdfDataHolderService.pdfByRzfwxi(userGuid, appName, loanPeriods);
			}else if(EqhPdfFileTypeEnum.HKJHB.getCode().equals(type)) {
				dataMap = eqhPdfDataHolderService.pdfByHkjhb(userGuid, appName, loanPeriods);
			}else if(EqhPdfFileTypeEnum.WTHKXY.getCode().equals(type)) {
				dataMap = eqhPdfDataHolderService.pdfWthkxy(userGuid, appName, loanPeriods);
			}else if(EqhPdfFileTypeEnum.JKFWSQB.getCode().equals(type)) {
				dataMap = eqhPdfDataHolderService.pdf07Beafor(userGuid, loanPeriods, creditMoney, appName,loanUsed);
			}
		} catch (Exception e) {
			log.error("查询合同数据接口: Exception{}", e);
			returnMap.put("ResultType", ErrorCodes.FAILURE.getCode());
			returnMap.put("Message", "查询合同数据接口异常");
			return returnMap;
		}
		returnMap.put("ResultType", ErrorCodes.SUCCSS.getCode());
		returnMap.put("Message", ErrorCodes.SUCCSS.getValue());
		returnMap.put("Model", dataMap);
		return returnMap;
	}


}
