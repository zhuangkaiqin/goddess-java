package com.bjike.goddess.card.entity;


import com.bjike.goddess.common.api.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 实体对象
 */
@Entity
@Table(name = "demo_card")
public class Card extends BaseEntity {

    /**
     * 卡号
     */
    private String account;
    /**
     * 密码
     */
    private String password;
    /**
     * 余额
     */
    private Long money;


    public Card() {
    }

    public Card(String account, String password, Long money) {
        this.account = account;
        this.password = password;
        this.money = money;
    }


    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public Long getMoney() {
        return money;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMoney(Long money) {
        this.money = money;
    }

}
