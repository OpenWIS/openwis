/**
 * 
 */
package org.openwis.metadataportal.services.harvest.oaipmh;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jeeves.exceptions.BadParameterEx;
import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.fao.geonet.constants.Geonet;
import org.fao.geonet.lib.Lib;
import org.fao.oaipmh.exceptions.NoSetHierarchyException;
import org.fao.oaipmh.exceptions.OaiPmhException;
import org.fao.oaipmh.requests.IdentifyRequest;
import org.fao.oaipmh.requests.ListMetadataFormatsRequest;
import org.fao.oaipmh.requests.ListSetsRequest;
import org.fao.oaipmh.requests.Transport;
import org.fao.oaipmh.responses.IdentifyResponse;
import org.fao.oaipmh.responses.IdentifyResponse.DeletedRecord;
import org.fao.oaipmh.responses.ListMetadataFormatsResponse;
import org.fao.oaipmh.responses.ListSetsResponse;
import org.fao.oaipmh.responses.MetadataFormat;
import org.fao.oaipmh.responses.SetInfo;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.category.CategoryManager;
import org.openwis.metadataportal.model.category.Category;
import org.openwis.metadataportal.services.common.json.AcknowledgementDTO;
import org.openwis.metadataportal.services.common.json.JeevesJsonWrapper;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class Info implements Service {

   private File oaiSchema;

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#exec(org.jdom.Element, jeeves.server.context.ServiceContext)
    */
   @Override
   public Element exec(Element params, ServiceContext context) throws Exception {
      OaipmhFetchRemoteInfoDTO readDto = JeevesJsonWrapper.read(params,
            OaipmhFetchRemoteInfoDTO.class);

      if (!Lib.net.isUrlValid(readDto.getUrl()))
         throw new BadParameterEx("url", readDto.getUrl());

      try {
         OaipmhInfosDTO dto = new OaipmhInfosDTO();
         dto.setFormats(getMdFormats(readDto.getUrl(), context));

         List<SetDTO> setDtos = getSets(readDto.getUrl(), context);

         if (readDto.isSynchronization()) {
            Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
            CategoryManager categoryManager = new CategoryManager(dbms);
            List<Category> localCategories = categoryManager.getAllCategories();
            final Collection<String> localCategoriesNames = Collections2.transform(localCategories,
                  new Function<Category, String>() {

                     @Override
                     public String apply(Category input) {
                        return input.getName().toLowerCase();
                     }
                  });

            Collection<SetDTO> filteredSetDtos = Collections2.filter(setDtos,
                  new Predicate<SetDTO>() {

                     @Override
                     public boolean apply(SetDTO input) {
                        return localCategoriesNames.contains(input.getName().toLowerCase());
                     }
                  });
            setDtos = Lists.newArrayList(filteredSetDtos);
         }
         dto.setSets(setDtos);

         IdentifyResponse res = getAdditionalInfo(readDto.getUrl(), context);
         dto.setDeletionSupport(!res.getDeletedRecord().equals(DeletedRecord.NO));
         //dto.setCompressions(res.getCompressions());

         return JeevesJsonWrapper.send(dto);
      } catch (OaiPmhException e) {
         return JeevesJsonWrapper.send(new AcknowledgementDTO(false, e.getMessage()));
      } catch (Exception e) {
         return JeevesJsonWrapper.send(new AcknowledgementDTO(false, e.getMessage()));
      }
   }

   /**
    * {@inheritDoc}
    * @see jeeves.interfaces.Service#init(java.lang.String, jeeves.server.ServiceConfig)
    */
   @Override
   public void init(String appPath, ServiceConfig config) throws Exception {
      oaiSchema = new File(appPath + "/xml/validation/oai/OAI-PMH.xsd");
   }

   //--------------------------------------------------------------------------

   private List<String> getMdFormats(String url, ServiceContext context) throws Exception {
      ListMetadataFormatsRequest req = new ListMetadataFormatsRequest();
      req.setSchemaPath(oaiSchema);
      Transport t = req.getTransport();
      t.setUrl(new URL(url));
      Lib.net.setupProxy(context, t);
      ListMetadataFormatsResponse res = req.execute();

      //--- build response
      List<String> formats = new ArrayList<String>();
      for (MetadataFormat mf : res.getFormats()) {
         formats.add(mf.prefix);
      }

      return formats;
   }

   //--------------------------------------------------------------------------

   private List<SetDTO> getSets(String url, ServiceContext context) throws Exception {
      try {
         ListSetsRequest req = new ListSetsRequest();
         req.setSchemaPath(oaiSchema);
         Transport t = req.getTransport();
         t.setUrl(new URL(url));
         Lib.net.setupProxy(context, t);
         ListSetsResponse res = req.execute();

         //--- build response
         List<SetDTO> sets = new ArrayList<SetDTO>();

         while (res.hasNextItem()) {
            SetInfo si = res.nextItem();
            sets.add(new SetDTO(si.getSpec(), si.getName()));
         }
         return sets;
      } catch (NoSetHierarchyException e) {
         //--- if the server does not support sets, simply returns an empty set
      }
      return null;
   }

   private IdentifyResponse getAdditionalInfo(String url, ServiceContext context) throws Exception {
      IdentifyRequest req = new IdentifyRequest();
      req.setSchemaPath(oaiSchema);
      Transport t = req.getTransport();
      t.setUrl(new URL(url));
      Lib.net.setupProxy(context, t);
      IdentifyResponse res = req.execute();
      return res;
   }

}
