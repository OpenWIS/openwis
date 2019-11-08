package org.fao.geonet.kernel.search;

import java.text.MessageFormat;
import java.util.Date;

/**
 * The Class IndexEvent. <P>
 * Explanation goes here. <P>
 */
public class IndexEvent {

   /**
    * The Type. <P>
    * Explanation goes here. <P>
    */
   public static enum Type {
      /** The AVAILABLE. */
      AVAILABLE,
      /** The UNAVAILABLE. */
      UNAVAILABLE,
      /** The OPTIMIZED. */
      OPTIMIZED,
      /** The COMMITTED. */
      COMMITTED;
   }

   /** The type. */
   private final Type type;

   /** The timestamp. */
   private final long timestamp;

   /**
    * Instantiates a new index event.
    *
    * @param type the type
    */
   private IndexEvent(Type type) {
      super();
      timestamp = System.currentTimeMillis();
      this.type = type;
   }

   @Override
   public String toString() {
      return MessageFormat.format("[INDEX EVENT] {0} at {1}", type, new Date(timestamp));
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
      result = prime * result + ((type == null) ? 0 : type.hashCode());
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
      IndexEvent other = (IndexEvent) obj;
      if (timestamp != other.timestamp)
         return false;
      if (type != other.type)
         return false;
      return true;
   }

   /**
    * Gets the type.
    *
    * @return the type
    */
   public Type getType() {
      return type;
   }

   /**
    * Gets the timestamp.
    *
    * @return the timestamp
    */
   public long getTimestamp() {
      return timestamp;
   }

   /**
    * The Class Factory. <P>
    * Explanation goes here. <P>
    */
   public static final class Factory {

      /**
       * Instantiates a new factory.
       */
      private Factory() {
         super();
      }

      /**
       * Creates the optimized event.
       *
       * @return the index event
       */
      public static IndexEvent createOptimizedEvent() {
         return new IndexEvent(Type.OPTIMIZED);
      }

      /**
       * Creates the committed event.
       *
       * @return the index event
       */
      public static IndexEvent createCommittedEvent() {
         return new IndexEvent(Type.COMMITTED);
      }

      /**
       * Creates the available event.
       *
       * @return the index event
       */
      public static IndexEvent createAvailableEvent() {
         return new IndexEvent(Type.AVAILABLE);
      }

      /**
       * Creates the available event.
       *
       * @return the index event
       */
      public static IndexEvent createUnavailableEvent() {
         return new IndexEvent(Type.UNAVAILABLE);
      }

   }

}
