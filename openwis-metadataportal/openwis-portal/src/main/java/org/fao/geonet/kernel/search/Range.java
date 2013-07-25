package org.fao.geonet.kernel.search;

import java.text.MessageFormat;

/**
 * The Class MinMax. <P>
 * Explanation goes here. <P>
 */
public class Range<T extends Comparable<T>> {

   /** The lower bound. */
   private final T lowerBound;

   /** The upper bound. */
   private final T upperBound;

   /**
    * Builds the range.
    *
    * @param <T> the generic type
    * @param lower the lower
    * @param upper the upper
    * @return the range
    */
   public static <T extends Comparable<T>> Range<T> buildRange(T lower, T upper) {
      if (lower == null) {
         throw new IllegalArgumentException("lower bound should not being null !");
      }
      if (upper == null) {
         throw new IllegalArgumentException("upper bound should not being null !");
      }
      if (lower.compareTo(upper) > 0) {
         throw new IllegalArgumentException(MessageFormat.format(
               "upper bound [{0}] should being greater than lower bound [{1}] !", upper, lower));
      }
      return new Range<T>(lower, upper);
   }

   /**
    * Instantiates a new range.
    *
    * @param lower the lower
    * @param upper the upper
    */
   private Range(T lower, T upper) {
      super();
      this.lowerBound = lower;
      this.upperBound = upper;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return MessageFormat.format("[{0}, {1}]", this.lowerBound, this.upperBound);
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      int result = 31;
      result = 31 * result + lowerBound.hashCode();
      result = 31 * result + upperBound.hashCode();
      return result;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      boolean result = false;
      if (obj == this) {
         result = true;
      } else if (obj == null || obj.getClass() != getClass()) {
         result = false;
         @SuppressWarnings("unchecked")
         Range<T> range = (Range<T>) obj;
         result = lowerBound.equals(range.lowerBound) && upperBound.equals(range.upperBound);
      }
      return result;
   }

   /**
    * Contains.
    *
    * @param t the t
    * @return true, if successful
    */
   public boolean contains(T t) {
      return t != null && t.compareTo(lowerBound) >= 0 && t.compareTo(upperBound) <= 0;
   }

   /**
    * Intersection.
    *
    * @param t the t
    * @return the range
    */
   public Range<T> intersection(Range<T> t) {
      Range<T> result = null;
      // check intersection
      if (t != null && upperBound.compareTo(t.lowerBound) > 0
            || t.upperBound.compareTo(lowerBound) > 0) {
         // min
         T min;
         if (lowerBound.compareTo(t.lowerBound) > 0) {
            min = t.lowerBound;
         } else {
            min = lowerBound;
         }

         // max
         T max;
         if (upperBound.compareTo(t.upperBound) > 0) {
            max = upperBound;
         } else {
            max = t.upperBound;
         }
         result = new Range<T>(min, max);
      }
      return result;
   }

   /**
    * Overlaps.
    *
    * @param t the t
    * @return true, if successful
    */
   public boolean overlaps(Range<T> t) {
      return intersection(t) != null;
   }

   /**
    * Include.
    *
    * @param t the t
    * @return true, if successful
    */
   public boolean include(Range<T> t) {
      return t != null && t.equals(intersection(t));

   }

   /**
    * Gets the lower bound.
    *
    * @return the lower bound
    */
   public T getLowerBound() {
      return lowerBound;
   }

   /**
    * Gets the upper bound.
    *
    * @return the upper bound
    */
   public T getUpperBound() {
      return upperBound;
   }
}
