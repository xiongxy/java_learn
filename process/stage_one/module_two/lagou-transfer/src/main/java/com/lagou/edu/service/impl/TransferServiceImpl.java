package com.lagou.edu.service.impl;

import com.lagou.edu.annotation.Autowired;
import com.lagou.edu.annotation.Service;
import com.lagou.edu.annotation.Transactional;
import com.lagou.edu.dao.AccountDao;
import com.lagou.edu.pojo.Account;
import com.lagou.edu.service.TransferService;
import com.lagou.edu.tranInterface.TransactionalCg;
import com.lagou.edu.tranInterface.TransactionalJdk;
import com.lagou.edu.utils.ConnectionUtils;
import com.lagou.edu.utils.TransactionManager;

/**
 * @author 应癫
 */
@Service("TransferService")
@Transactional
public class TransferServiceImpl implements TransferService, TransactionalJdk {

    @Autowired
    private AccountDao accountDao;


    @Override
    public void transfer(String fromCardNo, String toCardNo, int money) throws Exception {

        Account from = accountDao.queryAccountByCardNo(fromCardNo);
        Account to = accountDao.queryAccountByCardNo(toCardNo);

        from.setMoney(from.getMoney() - money);
        to.setMoney(to.getMoney() + money);

        accountDao.updateAccountByCardNo(to);
        int c = 1 / 0;
        accountDao.updateAccountByCardNo(from);
    }
}
