package com.study.mp.p6spy;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

/**
 * P6spy SQL 日志格式化
 *
 * @author liusheng
 * @since 2020-03-31
 */
public class P6spyLogFormat implements MessageFormattingStrategy {

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        return !"".equals(sql.trim()) ? "{><> (| took "
                + elapsed + "ms | " + category + " | connection " + connectionId + " |) <><} " + "\n "
                + "[><>> Execute SQL ><>>]: " + sql + ";" : "";
    }

}
    