//===	Copyright (C) 2001-2005 Food and Agriculture Organization of the
//===	United Nations (FAO-UN), United Nations World Food Programme (WFP)
//===	and United Nations Environment Programme (UNEP)
//===
//===	This program is free software; you can redistribute it and/or modify
//===	it under the terms of the GNU General Public License as published by
//===	the Free Software Foundation; either version 2 of the License, or (at
//===	your option) any later version.
//===
//===	This program is distributed in the hope that it will be useful, but
//===	WITHOUT ANY WARRANTY; without even the implied warranty of
//===	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//===	General Public License for more details.
//===
//===	You should have received a copy of the GNU General Public License
//===	along with this program; if not, write to the Free Software
//===	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//===
//===	Contact: Jeroen Ticheler - FAO - Viale delle Terme di Caracalla 2,
//===	Rome - Italy. email: GeoNetwork@fao.org
//==============================================================================

package org.fao.geonet.kernel;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Hashtable;
import java.util.Iterator;

import jeeves.utils.Log;

import org.fao.geonet.constants.Geonet;
import org.openrdf.model.Value;
import org.openrdf.sesame.Sesame;
import org.openrdf.sesame.config.ConfigurationException;
import org.openrdf.sesame.config.RepositoryConfig;
import org.openrdf.sesame.config.SailConfig;
import org.openrdf.sesame.constants.RDFFormat;
import org.openrdf.sesame.query.QueryResultsTable;
import org.openrdf.sesame.repository.local.LocalRepository;
import org.openrdf.sesame.repository.local.LocalService;

//=============================================================================

public class ThesaurusManager {

   private Hashtable<String, Thesaurus> thesauriTable = null;

   private LocalService service = null;

   private String thesauriDirectory = null;
   
   /** keep last modified date for thesauri dir */
   private long lastModifiedForDir;
   
   /** Whether the thesauri dir should be checked for reload */
   private boolean reloadDir;
   
   /**
    * 
    * @param appPath
    * @param thesauriRepository
    * @param reloadDir Whether the thesauri dir should be checked for reload
    * @throws Exception
    */
   public ThesaurusManager(String appPath, String thesauriRepository, boolean reloadDir) throws Exception {
      // Get Sesame interface
      service = Sesame.getService();

      File thesauriDir = new File(thesauriRepository);

      if (!thesauriDir.isAbsolute())
         thesauriDir = new File(appPath + thesauriDir);

      thesauriDirectory = thesauriDir.getAbsolutePath();

      initThesauriTable(thesauriDir);
      
      // keep last modified date for thesauri dir
      lastModifiedForDir = findLastModifiedDateForDir(thesauriDir);
      
      this.reloadDir = reloadDir;
   }

   /**
    * @param fname
    * @param type
    * @param dname
    * @return
    */
   public String buildThesaurusFilePath(String fname, String type, String dname) {
      return thesauriDirectory + File.separator + type + File.separator + Geonet.CodeList.THESAURUS
            + File.separator + dname + File.separator + fname;
   }

   /**
    * 
    * @param thesauriDirectory
    */
   private void initThesauriTable(File thesauriDirectory) {

      //repositoryTable = new Hashtable<String, LocalRepository>();
      thesauriTable = new Hashtable<String, Thesaurus>();

      if (thesauriDirectory.isDirectory()) {
         // init of external repositories
         File externalThesauriDirectory = new File(thesauriDirectory, Geonet.CodeList.EXTERNAL
               + File.separator + Geonet.CodeList.THESAURUS);
         if (externalThesauriDirectory.isDirectory()) {
            File[] rdfDataDirectory = externalThesauriDirectory.listFiles();
            for (File aRdfDataDirectory : rdfDataDirectory) {
               if (aRdfDataDirectory.isDirectory()) {
                  loadRepositories(aRdfDataDirectory, Geonet.CodeList.EXTERNAL);
               }
            }
         }

         // init of local repositories
         File localThesauriDirectory = new File(thesauriDirectory, Geonet.CodeList.LOCAL
               + File.separator + Geonet.CodeList.THESAURUS);
         if (localThesauriDirectory.isDirectory()) {
            File[] rdfDataDirectory = localThesauriDirectory.listFiles();
            for (File aRdfDataDirectory : rdfDataDirectory) {
               if (aRdfDataDirectory.isDirectory()) {
                  loadRepositories(aRdfDataDirectory, Geonet.CodeList.LOCAL);
               }
            }
         }
      }
   }

   /**
    * 
    * @param thesauriDirectory
    */
   private void loadRepositories(File thesauriDirectory, String root) {

      FilenameFilter filter = new FilenameFilter() {
         public boolean accept(File dir, String name) {
            return name.endsWith(".rdf");
         }
      };

      String[] rdfDataFile = thesauriDirectory.list(filter);

      for (String aRdfDataFile : rdfDataFile) {

         Thesaurus gst = new Thesaurus(aRdfDataFile, root, thesauriDirectory.getName(), new File(
               thesauriDirectory, aRdfDataFile));
         try {
            addThesaurus(gst);
         } catch (Exception e) {
            e.printStackTrace();
            // continue loading
         }
      }
   }

   /**
    * 
    * @param gst
    */
   public void addThesaurus(Thesaurus gst) throws Exception {

      String thesaurusName = gst.getKey();

      Log.debug(Geonet.THESAURUS_MAN, "Adding thesaurus : " + thesaurusName);

      if (existsThesaurus(thesaurusName)) {
         throw new Exception("A thesaurus exists with code " + thesaurusName);
      }

      LocalRepository thesaurusRepository;
      try {
         RepositoryConfig repConfig = new RepositoryConfig(gst.getKey());

         SailConfig syncSail = new SailConfig(
               "org.openrdf.sesame.sailimpl.sync.SyncRdfSchemaRepository");
         SailConfig memSail = new org.openrdf.sesame.sailimpl.memory.RdfSchemaRepositoryConfig(gst
               .getFile().getAbsolutePath(), RDFFormat.RDFXML);
         repConfig.addSail(syncSail);
         repConfig.addSail(memSail);
         repConfig.setWorldReadable(true);
         repConfig.setWorldWriteable(true);

         thesaurusRepository = service.createRepository(repConfig);

         gst.setRepository(thesaurusRepository);

         thesauriTable.put(thesaurusName, gst);

      } catch (ConfigurationException e) {
         e.printStackTrace();
         throw e;
      }
   }

   /**
    * 
    * @param name
    */
   public void remove(String name) {
      service.removeRepository(name);
      thesauriTable.remove(name);
   }

   // =============================================================================
   // PUBLIC SERVICES

   public String getThesauriDirectory() {
      return thesauriDirectory;
   }

   public Hashtable<String, Thesaurus> getThesauriTable() {
      if (reloadDir) {
         reloadThesauri();
      }
      return thesauriTable;
   }
   
   /**
    * Find the most recent last modified date recursively in the given folder.
    * @param dir the folder to check
    * @return the most recent last modified date
    */
   private static long findLastModifiedDateForDir(File dir) {
      File[] files = dir.listFiles();
      long lastModified = 0;
      for (File file : files) {
         long m;
         if (file.isDirectory()) {
            m = findLastModifiedDateForDir(file);
         } else {
            m = file.lastModified();
         }
         if (m > lastModified) {
            lastModified = m;
         }
      }
      return lastModified;
   }
   
   /**
    * Reload thesauri if one element in the thesauri directory has changed since last check.
    */
   private void reloadThesauri() {
      File thesauriDir = new File(getThesauriDirectory());
      long lastModified = findLastModifiedDateForDir(thesauriDir);
      if (lastModified > lastModifiedForDir) {
         Log.warning(Geonet.THESAURUS_MAN, "Thesauri directory has changed, reloading Thesauri");
         
         Iterator<String> repoNameIter = thesauriTable.keySet().iterator();
         while (repoNameIter.hasNext()) {
            String repoName = (String) repoNameIter.next();
            repoNameIter.remove();
            service.removeRepository(repoName);
         }
         
         initThesauriTable(new File(getThesauriDirectory()));
         lastModifiedForDir = lastModified;
      }
   }

   public Thesaurus getThesaurusByName(String thesaurusName) {
      return thesauriTable.get(thesaurusName);
   }

   /**
    * @param name
    * @return
    */
   public boolean existsThesaurus(String name) {
      return (thesauriTable.get(name) != null);
   }

   // =============================================================================

   public static void main(String[] args) throws Exception {
      ThesaurusManager tm = new ThesaurusManager(
            "",
            "D:\\OPENWIS\\workspace\\OpenWIS\\openwis-metadataportal\\openwis-portal\\target\\openwis-user-portal\\xml\\codelist\\", true);
      //tm.addElement("local.place.regions", "monPays", "le pays de toto", "le pays de toto");
      //tm.updateElement("local.place.regions", "Zimbabwe", "le pays",
      //    "voila un pays");
      // tm.getAllPrefLabel("local.place.regions");
      File rdf = new File(
            "D:\\OPENWIS\\workspace\\OpenWIS\\openwis-metadataportal\\openwis-portal\\target\\openwis-user-portal\\xml\\codelist\\external\\thesauri\\place\\regions.rdf");
      Thesaurus thesaurus = tm.getThesaurusByName("external.place.regions2");
         //new Thesaurus("regions.rdf", "external", "place", rdf);
      tm.addThesaurus(thesaurus);

      String query = "SELECT prefLab, id " + " from {id} rdf:type {skos:Concept}; "
      + " skos:prefLabel {prefLab} " + " where lang(prefLab) like \"en\""
      + " USING NAMESPACE skos=<http://www.w3.org/2004/02/skos/core#>";

      QueryResultsTable resultsTable = thesaurus.performRequest(query);

      tm.printResultsTable(resultsTable);

      //tm.addElement("external.place.toto", "monPays", "le pays de toto");
      //tm.getAllPrefLabel("external.place.toto");      
      System.out.println("fin!!");
   }

   private void printResultsTable(QueryResultsTable resultsTable) {
      int rowCount = resultsTable.getRowCount();
      int columnCount = resultsTable.getColumnCount();

      for (int row = 0; row < rowCount; row++) {
         for (int column = 0; column < columnCount; column++) {
            Value value = resultsTable.getValue(row, column);

            if (value != null) {
               System.out.print(value.toString());
            } else {
               System.out.print("null");
            }

            System.out.print("\t");
         }

         System.out.println();
      }
   }

}
