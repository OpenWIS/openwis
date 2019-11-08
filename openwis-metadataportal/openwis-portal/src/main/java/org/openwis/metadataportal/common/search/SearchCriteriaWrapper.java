/**
 * 
 */
package org.openwis.metadataportal.common.search;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class SearchCriteriaWrapper<C, S> {

    private C criteria;

    private Integer start;

    private Integer limit;

    private S sort;

    private SortDir dir;

    /**
     * Gets the criteria.
     * @return the criteria.
     */
    public C getCriteria() {
        return criteria;
    }

    /**
     * Sets the criteria.
     * @param criteria the criteria to set.
     */
    public void setCriteria(C criteria) {
        this.criteria = criteria;
    }

    /**
     * Gets the start.
     * @return the start.
     */
    public Integer getStart() {
        return start;
    }

    /**
     * Sets the start.
     * @param start the start to set.
     */
    public void setStart(Integer start) {
        this.start = start;
    }

    /**
     * Gets the limit.
     * @return the limit.
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * Sets the limit.
     * @param limit the limit to set.
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * Gets the sort.
     * @return the sort.
     */
    public S getSort() {
        return sort;
    }

    /**
     * Sets the sort.
     * @param sort the sort to set.
     */
    public void setSort(S sort) {
        this.sort = sort;
    }

    /**
     * Gets the dir.
     * @return the dir.
     */
    public SortDir getDir() {
        return dir;
    }

    /**
     * Sets the dir.
     * @param dir the dir to set.
     */
    public void setDir(SortDir dir) {
        this.dir = dir;
    }
}
