package com.isaccanedo.examples.guice;

import com.isaccanedo.examples.common.AccountService;
import com.google.inject.Inject;

public class GuiceUserService {

	@Inject
	private AccountService accountService;

	public AccountService getAccountService() {
		return accountService;
	}

	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}

}
