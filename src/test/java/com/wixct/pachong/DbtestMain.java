package com.wixct.pachong;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class DbtestMain {
    public static void main(String[] args) {
        DbUtils.initDb();
        Record r=Db.findFirst("select * from t_contents where cid=2");
        System.out.print(r);
    }
}
