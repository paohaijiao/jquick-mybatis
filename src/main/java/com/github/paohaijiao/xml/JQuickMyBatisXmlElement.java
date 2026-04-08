package com.github.paohaijiao.xml;

import com.github.paohaijiao.xml.element.JQuickXmlElement;

import java.util.ArrayList;
import java.util.List;

public class JQuickMyBatisXmlElement implements JQuickXmlElement {
    @Override
    public String getNameSpaceName() {
        return "namespace";
    }

    @Override
    public String getRootElementTagName() {
        return "mapper";
    }

    @Override
    public List<String> getChildElementTagName() {
        List<String> tagList=new ArrayList<>();
        tagList.add("insert");
        tagList.add("delete");
        tagList.add("update");
        tagList.add("select");
        return tagList;
    }

    @Override
    public String getMethodName() {
        return "name";
    }

    @Override
    public String getMethodReturnClass() {
        return "returnClass";
    }

    @Override
    public String getMethodParamClass() {
        return "paramClass";
    }

    @Override
    public String getValue() {
        return "value";
    }
}
