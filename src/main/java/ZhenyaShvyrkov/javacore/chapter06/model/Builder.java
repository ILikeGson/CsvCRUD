package main.java.ZhenyaShvyrkov.javacore.chapter06.model;

public interface Builder {
    Builder buildAccount();
    Builder buildFirstName(String firstName);
    Builder buildLastName(String lastName);
    Builder buildAge(int age);
    Builder buildStatus(Account.AccountStatus status);
}
