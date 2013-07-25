/**
 * 
 */
package org.openwis.metadataportal.common.search;

import java.util.List;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class SearchResultWrapper<T> {

    private int total;

    private List<T> rows;

    /**
     * Default constructor.
     * Builds a SearchResultWrapper.
     */
    public SearchResultWrapper() {
        super();
    }

    /**
     * Default constructor.
     * Builds a SearchResultWrapper.
     * @param count the count.
     * @param rows the rows.
     */
    public SearchResultWrapper(int total, List<T> rows) {
        super();
        this.total = total;
        this.rows = rows;
    }

    /**
     * Gets the total.
     * @return the total.
     */
    public int getTotal() {
        return total;
    }

    /**
     * Sets the total.
     * @param total the total to set.
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * Gets the rows.
     * @return the rows.
     */
    public List<T> getRows() {
        return rows;
    }

    /**
     * Sets the rows.
     * @param rows the rows to set.
     */
    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}
