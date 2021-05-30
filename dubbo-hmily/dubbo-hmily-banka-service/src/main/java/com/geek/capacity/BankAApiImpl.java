package com.geek.capacity;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.geek.BankAApi;
import com.geek.BankATransferDto;
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
public class BankAApiImpl implements BankAApi {

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
     * @param bankATransferDto
     * @return
     */
    @HmilyTCC(confirmMethod = "tradeCommit", cancelMethod = "tradeRollback")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean tradeInit(BankATransferDto bankATransferDto) {
        log.info("=================执行try=================");
        // 查询美元账户
        AccountUsd accountUsd = accountUsdService.getById(bankATransferDto.getUsdAccountId());

        BigDecimal fund = accountUsd.getFund();
        // 检查美元账户余额是否充足
        BigDecimal accountUsdBalance = fund.subtract(bankATransferDto.getOutputFund());
        if (accountUsdBalance.compareTo(new BigDecimal(0)) < 0) {
            return false;
        }

        Date now = new Date();

        // 预扣除美元账户金额，防止事务期间余额不足时发生多次出账操作
        accountUsd.setFund(accountUsdBalance);
        accountUsd.setUpdateTime(now);
        accountUsdService.updateById(accountUsd);

        // 冻结美元账户出账金额
        FrozenFundUsd frozenFundUsd = new FrozenFundUsd();
        frozenFundUsd.setTxId(bankATransferDto.getTxId());
        frozenFundUsd.setAccountId(bankATransferDto.getUsdAccountId());
        frozenFundUsd.setFrozenFund(bankATransferDto.getOutputFund());
        frozenFundUsd.setType(FrozenType.OUT);
        frozenFundUsd.setStatus(FrozenStatus.FREEZE);
        frozenFundUsd.setCreateTime(now);
        frozenFundUsdService.save(frozenFundUsd);

        // 人民币账户不预先入账，防止事务期间发生需要转出这笔入账金额的交易
        // 冻结人民币账户入账金额
        FrozenFundCny frozenFundCny = new FrozenFundCny();
        frozenFundCny.setTxId(bankATransferDto.getTxId());
        frozenFundCny.setAccountId(bankATransferDto.getCnyAccountId());
        frozenFundCny.setFrozenFund(bankATransferDto.getInputFund());
        frozenFundCny.setType(FrozenType.IN);
        frozenFundCny.setStatus(FrozenStatus.FREEZE);
        frozenFundCny.setCreateTime(now);
        frozenFundCnyService.save(frozenFundCny);

        return true;
    }

    /**
     * 提交交易
     *
     * @param bankATransferDto
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean tradeCommit(BankATransferDto bankATransferDto) {
        log.info("=================执行confirm=================");
        FrozenFundUsd frozenFundUsd = frozenFundUsdService.getById(bankATransferDto.getTxId());
        Date now = new Date();

        // 先判断状态，维持幂等性
        if (frozenFundUsd != null && frozenFundUsd.getStatus().equals(FrozenStatus.FREEZE)) {
            // 解冻美元账户冻结资金
            frozenFundUsd.setStatus(FrozenStatus.UNFREEZE);
            frozenFundUsd.setUpdateTime(now);
            frozenFundUsdService.updateById(frozenFundUsd);
        }

        FrozenFundCny frozenFundCny = frozenFundCnyService.getById(bankATransferDto.getTxId());
        // 幂等判断
        if (frozenFundCny != null && frozenFundCny.getStatus().equals(FrozenStatus.FREEZE)) {
            // 将人民币账户冻结资金汇入人民币账户
            AccountCny accountCny = accountCnyService.getById(bankATransferDto.getCnyAccountId());
            accountCny.setFund(accountCny.getFund().add(frozenFundCny.getFrozenFund()));
            accountCny.setUpdateTime(now);
            Integer oldVersion = accountCny.getVersion();
            accountCny.setVersion(oldVersion + 1);

            // 更新人民币账户金额，加入版本号的控制，来达到幂等性
            UpdateWrapper<AccountCny> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(AccountCny.VERSION, oldVersion);
            accountCnyService.update(accountCny, updateWrapper);

            // 解冻人民币账户冻结资金
            frozenFundCny.setStatus(FrozenStatus.UNFREEZE);
            frozenFundCny.setUpdateTime(now);
            frozenFundCnyService.updateById(frozenFundCny);
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
    public boolean tradeRollback(BankATransferDto bankATransferDto) {
        log.info("=================执行cancel=================");
        FrozenFundUsd frozenFundUsd = frozenFundUsdService.getById(bankATransferDto.getTxId());
        Date now = new Date();

        // 先判断状态，维持幂等性
        if (frozenFundUsd != null && frozenFundUsd.getStatus().equals(FrozenStatus.FREEZE)) {
            // 返还美元账户冻结资金
            AccountUsd accountUsd = accountUsdService.getById(bankATransferDto.getUsdAccountId());
            accountUsd.setFund(accountUsd.getFund().add(frozenFundUsd.getFrozenFund()));
            Integer oldVersion = accountUsd.getVersion();
            accountUsd.setVersion(oldVersion + 1);

            UpdateWrapper<AccountUsd> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(AccountUsd.VERSION, oldVersion);
            accountUsdService.update(accountUsd, updateWrapper);

            // 解冻美元账户冻结资金
            frozenFundUsd.setStatus(FrozenStatus.UNFREEZE);
            frozenFundUsd.setUpdateTime(now);
            frozenFundUsdService.updateById(frozenFundUsd);
        }

        FrozenFundCny frozenFundCny = frozenFundCnyService.getById(bankATransferDto.getTxId());
        // 幂等判断
        if (frozenFundCny != null && frozenFundCny.getStatus().equals(FrozenStatus.FREEZE)) {
            // 解冻人民币账户冻结资金
            frozenFundCny.setStatus(FrozenStatus.UNFREEZE);
            frozenFundCny.setUpdateTime(now);
            frozenFundCnyService.updateById(frozenFundCny);
        }

        return false;
    }
}
