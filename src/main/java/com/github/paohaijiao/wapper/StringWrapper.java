package com.github.paohaijiao.wapper;

import com.github.paohaijiao.spi.anno.Priority;
import com.github.paohaijiao.spi.constants.PriorityConstants;
import com.github.paohaijiao.xml.wrapper.Wrapper;
import com.github.paohaijiao.xml.wrapper.data.JQuickXmlWrapperData;

@Priority(PriorityConstants.APPLICATION_MEDIUM)
public class StringWrapper implements Wrapper {
    @Override
    public boolean support(JQuickXmlWrapperData value) {
        if(null==value||null==value.getData()){
            return false;
        }
        return value.getData() instanceof String;
    }

    @Override
    public Object wrap(JQuickXmlWrapperData value) {
        Object data=value.getData();
        return "\""+data.toString()+"\"";
    }
}
