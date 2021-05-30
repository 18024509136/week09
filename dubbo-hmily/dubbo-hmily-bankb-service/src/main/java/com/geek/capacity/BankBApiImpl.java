package com.geek.capacity;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.geek.BankBApi;
import com.geek.BankBTransferDto;
import com.geek.constant.FrozenStatus;
import com.geek.constant.FrozenType;
import com.geek.entity.AccountCny;
import com.geek.entity.AccountUsd;
import com.geek.entity.FrozenFundCny;
import com.geek.entity.FrozenFundUsd;
import com.geek.service.AccountCnyService;
import com.geek.service.AccountUsdService;
import com.geek.service.FrozenFundCnyService;
import com.geek.service.FrozenFundUsdService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.hmily.annotation.HmilyTCC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@DubboService
@Component
public class BankBApiImpl implements BankBApi {

    @Autowired
    private AccountCnyService accountCnyService;

    @Autowired
    private AccountUsdService accountUsdService;

    @Autowired
    private FrozenFundCnyService frozenFundCnyService;

    @Autowired
    private FrozenFundUsdService frozenFundUsdService;

    /**
     * 交易预处理
     *
     * @param bankBTransferDto
     * @return
     */
    @HmilyTCC(confirmMethod = "tradeCommit", cancelMethod = "tradeRollback")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean tradeInit(BankBTransferDto bankBTransferDto) {
        log.info("=================执行try=================");

        int a = 1 / 0;

        // 查询人民币账户
        AccountCny accountCny = accountCnyService.getById(bankBTransferDto.getCnyAccountId());

        BigDecimal fund = accountCny.getFund();
        // 检查人民币账户余额是否充足
        BigDecimal accountCnyBalance = fund.subtract(bankBTransferDto.getOutputFund());
        if (accountCnyBalance.compareTo(new BigDecimal(0)) < 0) {
            return false;
        }

        Date now = new Date();

        // 预扣除人民币账户金额，防止事务期间余额不足时发生多次转出操作
        accountCny.setFund(accountCnyBalance);
        accountCny.setUpdateTime(now);
        accountCnyService.updateById(accountCny);

        // 冻结人民币账户转出金额
        FrozenFundCny frozenFundCny = new FrozenFundCny();
        frozenFundCny.setTxId(bankBTransferDto.getTxId());
        frozenFundCny.setAccountId(bankBTransferDto.getCnyAccountId());
        frozenFundCny.setFrozenFund(bankBTransferDto.getOutputFund());
        frozenFundCny.setType(FrozenType.OUT);
        frozenFundCny.setStatus(FrozenStatus.FREEZE);
        frozenFundCny.setCreateTime(now);
        frozenFundCnyService.save(frozenFundCny);

        // 美元账户不预先入账，防止事务期间发生需要转出这笔入账金额的交易
        // 冻结美元账户转入金额
        FrozenFundUsd frozenFundUsd = new FrozenFundUsd();
        frozenFundUsd.setTxId(bankBTransferDto.getTxId());
        frozenFundUsd.setAccountId(bankBTransferDto.getUsdAccountId());
        frozenFundUsd.setFrozenFund(bankBTransferDto.getInputFund());
        frozenFundUsd.setType(FrozenType.IN);
        frozenFundUsd.setStatus(FrozenStatus.FREEZE);
        frozenFundUsd.setCreateTime(now);
        frozenFundUsdService.save(frozenFundUsd);

        return true;
    }

    /**
     * 提交交易
     *
     * @param bankBTransferDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean tradeCommit(BankBTransferDto bankBTransferDto) {
        log.info("=================执行confirm=================");
        FrozenFundCny frozenFundCny = frozenFundCnyService.getById(bankBTransferDto.getTxId());
        Date now = new Date();

        // 先判断状态，维持幂等性
        if (frozenFundCny != null && frozenFundCny.getStatus().equals(FrozenStatus.FREEZE)) {
            // 解冻人民币账户冻结资金
            frozenFundCny.setStatus(FrozenStatus.UNFREEZE);
            frozenFundCny.setUpdateTime(now);
            frozenFundCnyService.updateById(frozenFundCny);
        }

        FrozenFundUsd frozenFundUsd = frozenFundUsdService.getById(bankBTransferDto.getTxId());
        // 幂等判断
        if (frozenFundUsd != null && frozenFundUsd.getStatus().equals(FrozenStatus.FREEZE)) {
            // 将美元账户冻结资金汇入美元账户
            AccountUsd accountUsd = accountUsdService.getById(bankBTransferDto.getUsdAccountId());
            accountUsd.setFund(accountUsd.getFund().add(frozenFundUsd.getFrozenFund()));
            accountUsd.setUpdateTime(now);
            Integer oldVersion = accountUsd.getVersion();
            accountUsd.setVersion(oldVersion + 1);

            // 更新美元账户金额，加入版本号的控制，来达到幂等性
            UpdateWrapper<AccountUsd> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(AccountUsd.VERSION, oldVersion);
            accountUsdService.update(accountUsd, updateWrapper);

            // 解冻美元账户冻结资金
            frozenFundUsd.setStatus(FrozenStatus.UNFREEZE);
            frozenFundUsd.setUpdateTime(now);
            frozenFundUsdService.updateById(frozenFundUsd);
        }

        return true;
    }

    /**
     * 交易回滚
     *
     * @param bankATransferDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean tradeRollback(BankBTransferDto bankATransferDto) {
        log.info("=================执行cancel=================");
        FrozenFundCny frozenFundCny = frozenFundCnyService.getById(bankATransferDto.getTxId());
        Date now = new Date();

        // 先判断状态，维持幂等性
        if (frozenFundCny != null && frozenFundCny.getStatus().equals(FrozenStatus.FREEZE)) {
            // 返还人民币账户冻结资金
            AccountCny accountCny = accountCnyService.getById(bankATransferDto.getCnyAccountId());
            accountCny.setFund(accountCny.getFund().add(frozenFundCny.getFrozenFund()));
            Integer oldVersion = accountCny.getVersion();
            accountCny.setVersion(oldVersion + 1);

            UpdateWrapper<AccountCny> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(AccountCny.VERSION, oldVersion);
            accountCnyService.update(accountCny, updateWrapper);

            // 解冻人民币账户冻结资金
            frozenFundCny.setStatus(FrozenStatus.UNFREEZE);
            frozenFundCny.setUpdateTime(now);
            frozenFundCnyService.updateById(frozenFundCny);
        }

        FrozenFundUsd frozenFundUsd = frozenFundUsdService.getById(bankATransferDto.getTxId());
        // 幂等判断
        if (frozenFundUsd != null && frozenFundUsd.getStatus().equals(FrozenStatus.FREEZE)) {
            // 解冻人民币账户冻结资金
            frozenFundUsd.setStatus(FrozenStatus.UNFREEZE);
            frozenFundUsd.setUpdateTime(now);
            frozenFundUsdService.updateById(frozenFundUsd);
        }

        return false;
    }
}
