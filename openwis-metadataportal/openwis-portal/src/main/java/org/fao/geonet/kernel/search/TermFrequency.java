package org.fao.geonet.kernel.search;

import org.apache.commons.lang.builder.CompareToBuilder;

/**
 * The Class TermFrequency. <P>
 * Explanation goes here. <P>
 */
public class TermFrequency implements Comparable<TermFrequency> {

   /** The term. */
   private final String term;

   /** The frequency. */
   private final int frequency;

   /**
    * Instantiates a new term frequency.
    *
    * @param term the term
    * @param frequency the frequency
    */
   public TermFrequency(String term, int frequency) {
      this.term = term;
      this.frequency = frequency;
   }

   /**
    * Gets the term.
    *
    * @return the term
    */
   public String getTerm() {
      return term;
   }

   /**
    * Gets the frequency.
    *
    * @return the frequency
    */
   public int getFrequency() {
      return frequency;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   @Override
   public int compareTo(TermFrequency o) {
      return new CompareToBuilder().append(frequency, o.frequency).append(term, o.term)
            .toComparison();
   }
}
