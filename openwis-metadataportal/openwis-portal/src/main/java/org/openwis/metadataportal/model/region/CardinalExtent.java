/**
 * 
 */
package org.openwis.metadataportal.model.region;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class CardinalExtent {

    private float top;

    private float bottom;

    private float left;

    private float right;

    /**
     * Default constructor.
     * Builds a CardinalExtent.
     * @param top
     * @param bottom
     * @param left
     * @param right
     */
    public CardinalExtent(float top, float bottom, float left, float right) {
        super();
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
    }

    /**
     * Gets the top.
     * @return the top.
     */
    public float getTop() {
        return top;
    }

    /**
     * Sets the top.
     * @param top the top to set.
     */
    public void setTop(float top) {
        this.top = top;
    }

    /**
     * Gets the bottom.
     * @return the bottom.
     */
    public float getBottom() {
        return bottom;
    }

    /**
     * Sets the bottom.
     * @param bottom the bottom to set.
     */
    public void setBottom(float bottom) {
        this.bottom = bottom;
    }

    /**
     * Gets the left.
     * @return the left.
     */
    public float getLeft() {
        return left;
    }

    /**
     * Sets the left.
     * @param left the left to set.
     */
    public void setLeft(float left) {
        this.left = left;
    }

    /**
     * Gets the right.
     * @return the right.
     */
    public float getRight() {
        return right;
    }

    /**
     * Sets the right.
     * @param right the right to set.
     */
    public void setRight(float right) {
        this.right = right;
    }
}
