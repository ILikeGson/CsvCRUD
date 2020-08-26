package main.java.ZhenyaShvyrkov.javacore.chapter06.model;

public class AccountBuilder implements Builder{
    private Account account;

    @Override
    public AccountBuilder buildAccount() {
        account = new Account();
        return this;
    }

    @Override
    public AccountBuilder buildFirstName(String firstName) {
        account.setFirstName(firstName);
        return this;
    }

    @Override
    public AccountBuilder buildLastName(String lastName) {
        account.setLastName(lastName);
        return this;
    }

    @Override
    public AccountBuilder buildAge(int age) {
        account.setAge(age);
        return this;
    }

    @Override
    public AccountBuilder buildStatus(Account.AccountStatus status) {
        account.setStatus(status);
        return this;
    }

    public Account build() {
        return account;
    }
}
