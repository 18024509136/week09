package com.geek.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author huangxiaodi
 * @since 2021-05-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class FrozenFundUsd implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 交易ID，主键
     */
    @TableId(value = TX_ID, type = IdType.INPUT)
    private Long txId;

    /**
     * 账户ID
     */
    private Long accountId;

    /**
     * 冻结资金
     */
    private BigDecimal frozenFund;

    /**
     * 类型：1.待转入 2.待转出
     */
    @TableField("TYPE")
    private Integer type;

    /**
     * 状态：0.解冻 1.冻结
     */
    @TableField("STATUS")
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;


    public static final String TX_ID = "tx_id";

    public static final String ACCOUNT_ID = "account_id";

    public static final String FROZEN_FUND = "frozen_fund";

    public static final String TYPE = "TYPE";

    public static final String STATUS = "STATUS";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

}
