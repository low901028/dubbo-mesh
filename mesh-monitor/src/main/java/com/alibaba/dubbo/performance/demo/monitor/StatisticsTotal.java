package com.alibaba.dubbo.performance.demo.monitor;

import java.util.ArrayList;
import java.util.List;

/**
 * 统计信息汇总
 * <p>
 *
 */
public class StatisticsTotal {
    /** 总计信息 */
    private StatisticsInfo total;

    /** 详情信息 */
    private List<StatisticsInfo> detail = new ArrayList<StatisticsInfo>();

    /**
     * getter method
     * 
     * @see StatisticsTotal#total
     * @return the total
     */
    public StatisticsInfo getTotal() {
        return total;
    }

    /**
     * setter method
     * 
     * @see StatisticsTotal#total
     * @param total
     *            the total to set
     */
    public void setTotal(StatisticsInfo total) {
        this.total = total;
    }

    /**
     * getter method
     * 
     * @see StatisticsTotal#detail
     * @return the detail
     */
    public List<StatisticsInfo> getDetail() {
        return detail;
    }

    /**
     * setter method
     * 
     * @see StatisticsTotal#detail
     * @param detail
     *            the detail to set
     */
    public void setDetail(List<StatisticsInfo> detail) {
        this.detail = detail;
    }

}
