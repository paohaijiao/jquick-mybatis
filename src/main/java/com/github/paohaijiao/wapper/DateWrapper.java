package com.github.paohaijiao.wapper;

import com.github.paohaijiao.xml.wrapper.Wrapper;
import com.github.paohaijiao.xml.wrapper.data.JQuickXmlWrapperData;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateWrapper implements Wrapper {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    public boolean support(JQuickXmlWrapperData value) {
        if(null==value||null==value.getData()){
            return false;
        }
        return value.getData() instanceof Date;
    }

    @Override
    public Object wrap(JQuickXmlWrapperData value) {
        if(null==value){
          return null;
        }
        Date date = (Date)value.getData();
        return DATE_FORMAT.format(date);
    }
}
