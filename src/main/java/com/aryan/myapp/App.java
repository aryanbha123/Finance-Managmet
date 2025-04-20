package com.aryan.myapp;
import com.aryan.myapp.DB.init;
import com.aryan.myapp.Ui.Login;


public class App 
{
    public static void main( String[] args )
    {
        init.createTables();
        Login.main(args);
    }
}
