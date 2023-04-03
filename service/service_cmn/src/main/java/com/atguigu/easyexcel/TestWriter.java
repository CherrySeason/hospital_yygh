package com.atguigu.easyexcel;

import com.alibaba.excel.EasyExcel;

import java.util.ArrayList;
import java.util.List;

public class TestWriter {
    public static void main(String[] args) {
        List<UserData> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            UserData data = new UserData();
            data.setUid(i);
            data.setUsername("name" + i);
            list.add(data);
        }

        String filename = "service/service_cmn/src/main/java/com/atguigu/easyexcel/test.xlsx";
        EasyExcel.write(filename, UserData.class).sheet("用户信息").doWrite(list);
    }
}
