package org.openwis.metadataportal.kernel.search.index;

import java.util.Collection;
import java.util.Date;

import org.fao.geonet.kernel.search.IndexListener;

/**
 * The Interface IIndexManager. <P>
 * Explanation goes here. <P>
 */
public interface IIndexManager {

   /** The SCHEM a_ stylesheet s_ di r_ path. */
   String SCHEMA_STYLESHEETS_DIR_PATH = "xml/schemas";

   /**
    * Clear.
    *
    * @throws IndexException the index exception
    */
   void clear() throws IndexException;

   /**
    * Optimize.
    * @return
    *
    * @throws IndexException the index exception
    */
   boolean optimize() throws IndexException;

   /**
    * Commit.
    *
    * @throws IndexException the index exception
    */
   void commit() throws IndexException;

   /**
    * Adds the element to the index.
    * DOES NOT COMMIT.
    *
    * @param element the element
    * @throws IndexException the index exception
    */
   void add(IndexableElement element) throws IndexException;

   /**
    * Adds the.
    * DOES NOT COMMIT.
    *
    * @param elements the elements
    * @throws IndexException the index exception
    */
   void add(Collection<IndexableElement> elements) throws IndexException;

   /**
    * Adds the element to the index.
    *
    * @param element the element
    * @param commit if commit
    * @throws IndexException the index exception
    */
   void add(IndexableElement element, boolean commit) throws IndexException;

   /**
    * Adds elements.
    *
    * @param elements the elements
    * @param commit if commit
    * @throws IndexException the index exception
    */
   void add(Collection<IndexableElement> elements, boolean commit) throws IndexException;

   /**
    * Removes the element from the index.
    * DOES NOT COMMIT.
    *
    * @param element the element
    * @throws IndexException the index exception
    */
   void remove(IndexableElement element) throws IndexException;

   /**
    * Removes the elements.
    * DOES NOT COMMIT.
    *
    * @param elements the elements
    * @throws IndexException the index exception
    */
   void remove(Collection<IndexableElement> elements) throws IndexException;

   /**
    * Removes the element from the index.
    *
    * @param element the element
    * @param commit if commit
    * @throws IndexException the index exception
    */
   void remove(IndexableElement element, boolean commit) throws IndexException;

   /**
    * Removes the.
    *
    * @param elements the elements
    * @param commit if commit
    * @throws IndexException the index exception
    */
   void remove(Collection<IndexableElement> elements, boolean commit) throws IndexException;

   /**
    * Parses the date.
    *
    * @param sDate the s date
    * @return the date
    */
   Date parseDate(String sDate);

   /**
    * Adds the index listener.
    *
    * @param listener the listener
    */
   void addIndexListener(IndexListener listener);

   /**
    * Removes the index listener.
    *
    * @param listener the listener
    */
   void removeIndexListener(IndexListener listener);

   /**
    * Returns <code>true</code> if the index is available, <code>false</code> otherwise.
    * @return <code>true</code> if the index is available, <code>false</code> otherwise.
    */
   boolean isAvailable() throws IndexException;

}
