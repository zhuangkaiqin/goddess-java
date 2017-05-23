package com.bjike.goddess.materialsummary.dao;

import com.bjike.goddess.common.jpa.dao.JpaRep;
import com.bjike.goddess.materialsummary.dto.AreaBuyStatusMonthSumDTO;
import com.bjike.goddess.materialsummary.entity.AreaBuyStatusMonthSum;

/**
* 地区购买情况月汇总持久化接口, 继承基类可使用ｊｐａ命名查询
* @Author:			[ sunfengtao ]
* @Date:			[  2017-05-22 10:54 ]
* @Description:	[ 地区购买情况月汇总持久化接口, 继承基类可使用ｊｐａ命名查询 ]
* @Version:		[ v1.0.0 ]
* @Copy:   		[ com.bjike ]
*/
public interface AreaBuyStatusMonthSumRep extends JpaRep<AreaBuyStatusMonthSum ,AreaBuyStatusMonthSumDTO> { 

 }