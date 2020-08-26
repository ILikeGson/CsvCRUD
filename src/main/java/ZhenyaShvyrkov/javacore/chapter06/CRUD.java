package main.java.ZhenyaShvyrkov.javacore.chapter06;

import main.java.ZhenyaShvyrkov.javacore.chapter06.view.AccountView;
import main.java.ZhenyaShvyrkov.javacore.chapter06.view.CustomerView;
import main.java.ZhenyaShvyrkov.javacore.chapter06.view.SpecialtyView;

public class CRUD {
    private static AccountView accountView = AccountView.getAccountView();
    private static CustomerView customerView = CustomerView.getCustomerView();
    private static SpecialtyView specialtyView = SpecialtyView.getSpecialtyView();

    public void workWithAccount(){
        accountView.getRequest();
    }
    public void workWithCustomer(){
        customerView.getRequest();
    }
    public void workWithSpecialty(){
        specialtyView.getRequest();
    }
}
