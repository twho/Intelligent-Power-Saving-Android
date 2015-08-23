package com.ibplan.michaelho.com.ibplan.michaelho.constants;

/**
 * Created by Administrator on 2015/5/24.
 */
public interface SQLcommands {
    public static final String COMMAND_GET_EVENTS = "SELECT * FROM table_campus_events order by Id desc";
    public static final String COMMAND_GET_BULDING_LIST = "SELECT * FROM table_reg order by Id desc";
    public static final String COMMAND_GET_LAB_LIST = "SELECT * FROM table_lab_reg order by Id desc";
}
