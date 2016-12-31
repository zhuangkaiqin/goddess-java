package com.bjike.goddess.user.common.service;


import com.bjike.goddess.dbs.jpa.service.ServiceImpl;
import com.bjike.goddess.user.common.dto.GroupDto;
import com.bjike.goddess.user.common.entity.Group;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;


/**
 * @Author: [liguiqin]
 * @Date: [2016-12-28 15:47]
 * @Description: [部门业务实现]
 * @Version: [1.0.0]
 * @Copy: [org.ndshop]
 */
@CacheConfig(cacheNames = "userSerCache")
@Service
public class GroupSerImpl extends ServiceImpl<Group, GroupDto> implements IGroupSer {

}
