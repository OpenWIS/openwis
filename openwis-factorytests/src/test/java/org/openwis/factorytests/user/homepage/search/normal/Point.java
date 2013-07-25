package org.openwis.factorytests.user.homepage.search.normal;

import java.text.MessageFormat;

/**
 * The Class Point. <P>
 * Explanation goes here. <P>
 */
public class Point {

   /** The x. */
   double x;

   /** The y. */
   double y;

   /**
    * Instantiates a new point.
    *
    * @param x the x
    * @param y the y
    */
   public Point(double x, double y) {
      super();

      this.x = x;
      this.y = y;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return MessageFormat.format("({0}, {1})", x, y);
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      long temp;
      temp = Double.doubleToLongBits(x);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(y);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      return result;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Point other = (Point) obj;
      if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
         return false;
      if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
         return false;
      return true;
   }

}
