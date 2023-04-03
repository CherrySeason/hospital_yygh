package com.atguigu.easyexcel;

import com.alibaba.excel.EasyExcel;

public class TestReader {
    public static void main(String[] args) {
        String filename = "service/service_cmn/src/main/java/com/atguigu/easyexcel/test.xlsx";
        EasyExcel.read(filename, UserData.class, new ExcelListener()).sheet().doRead();
    }
}
