package com.github.paohaijiao.format;

import com.github.paohaijiao.model.JKeyValue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSqlFormatter {


    public static List<JKeyValue> parsePlaceholders(String sqlTemplate) {
        List<JKeyValue> list = new ArrayList<>();
        Pattern pattern = Pattern.compile("#\\{(\\w+)\\}");
        Matcher matcher = pattern.matcher(sqlTemplate);
        int num = 0;
        while (matcher.find()) {
            num = num + 1;
            String placeholder = matcher.group(1); //FieldName
            JKeyValue keyValue = new JKeyValue();
            keyValue.setKey(placeholder);
            keyValue.setValue(matcher.group());
            keyValue.setNum(num);
            list.add(keyValue);
        }
        return list;
    }

    public static List<JKeyValue> getFieldValues(Object entity, List<JKeyValue> list) {
        List<JKeyValue> result = new ArrayList<>();
        Class<?> clazz = entity.getClass();
        try {
            for (JKeyValue e : list) {
                String fieldName = e.getKey();
                Field field;
                try {
                    field = clazz.getDeclaredField(fieldName);//get Value By FieldId
                    field.setAccessible(true);
                    Object value = field.get(entity);
                    e.setValue(value);
                } catch (NoSuchFieldException ex1) {
                    try {
                        String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                        Object value = clazz.getMethod(getterName).invoke(entity);
                        e.setValue(value);
                        continue;
                    } catch (Exception ex) {
                        throw new RuntimeException("unable to get field " + fieldName + "'s value", ex);
                    }
                }
                result.add(e);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String replacePlaceholders(String sqlTemplate, List<JKeyValue> list) {
        String result = sqlTemplate;
        for (JKeyValue entry : list) {
            String placeholder = "#{" + entry.getKey() + "}";
            Object value = entry.getValue();
            String replacement;
            if (value == null) {
                replacement = "NULL";
            } else if (value instanceof String || value instanceof Character) {
                replacement = "'" + value.toString().replace("'", "''") + "'"; // 转义单引号
            } else if (value instanceof Number) {
                replacement = value.toString();
            } else if (value instanceof Boolean) {
                replacement = ((Boolean) value) ? "1" : "0";
            } else {
                replacement = "'" + value.toString().replace("'", "''") + "'";
            }
            result = result.replace(placeholder, replacement);
        }
        return result;
    }
}
