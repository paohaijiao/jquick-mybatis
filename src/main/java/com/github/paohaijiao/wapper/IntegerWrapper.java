package com.github.paohaijiao.wapper;

import com.github.paohaijiao.spi.anno.Priority;
import com.github.paohaijiao.spi.constants.PriorityConstants;
import com.github.paohaijiao.xml.wrapper.Wrapper;
import com.github.paohaijiao.xml.wrapper.data.JQuickXmlWrapperData;

@Priority(PriorityConstants.APPLICATION_HIGH)
public class IntegerWrapper implements Wrapper {
    @Override
    public boolean support(JQuickXmlWrapperData value) {
        if(null==value||null==value.getData()){
            return false;
        }
        return value.getData() instanceof Integer;
    }

    @Override
    public Object wrap(JQuickXmlWrapperData value) {
        return value.getData();
    }
}
