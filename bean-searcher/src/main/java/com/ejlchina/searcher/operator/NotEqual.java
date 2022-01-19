package com.ejlchina.searcher.operator;

import com.ejlchina.searcher.FieldOp;

import java.util.List;

import static com.ejlchina.searcher.util.ObjectUtils.firstNotNull;
import static java.util.Collections.singletonList;

/**
 * 不等于运算符
 * @author Troy.Zhou @ 2022-01-19
 * @since v3.3.0
 */
public class NotEqual implements FieldOp {

    @Override
    public boolean isNamed(String name) {
        return "ne".equals(name) || "NotEqual".equals(name);
    }

    @Override
    public List<Object> operate(StringBuilder sqlBuilder, String dbField, Object[] values) {
        sqlBuilder.append(" != ?");
        return singletonList(firstNotNull(values));
    }

}