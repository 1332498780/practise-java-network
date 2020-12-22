package cn.haohan.test;

import org.junit.Test;

import java.util.Scanner;

public class ScannerTest {

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        do{
            String str = scanner.next();
            System.out.println(str);
        }while(scanner.hasNext());
    }
}
