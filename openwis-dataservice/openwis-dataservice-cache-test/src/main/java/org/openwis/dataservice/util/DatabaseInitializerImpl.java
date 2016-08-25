package org.openwis.dataservice.util;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.openwis.dataservice.ConfigurationInfo;
import org.openwis.dataservice.common.domain.entity.enumeration.ClassOfService;
import org.openwis.dataservice.common.domain.entity.enumeration.ExtractMode;
import org.openwis.dataservice.common.domain.entity.enumeration.MailAttachmentMode;
import org.openwis.dataservice.common.domain.entity.enumeration.MailDispatchMode;
import org.openwis.dataservice.common.domain.entity.enumeration.RequestResultStatus;
import org.openwis.dataservice.common.domain.entity.request.Parameter;
import org.openwis.dataservice.common.domain.entity.request.ParameterCode;
import org.openwis.dataservice.common.domain.entity.request.ProcessedRequest;
import org.openwis.dataservice.common.domain.entity.request.ProductMetadata;
import org.openwis.dataservice.common.domain.entity.request.Temporal;
import org.openwis.dataservice.common.domain.entity.request.Value;
import org.openwis.dataservice.common.domain.entity.request.dissemination.DisseminationZipMode;
import org.openwis.dataservice.common.domain.entity.request.dissemination.MSSFSSChannel;
import org.openwis.dataservice.common.domain.entity.request.dissemination.MailDiffusion;
import org.openwis.dataservice.common.domain.entity.request.dissemination.RMDCNDissemination;
import org.openwis.dataservice.common.domain.entity.subscription.EventBasedFrequency;
import org.openwis.dataservice.common.domain.entity.subscription.Subscription;
import org.openwis.dataservice.common.service.ProductMetadataService;
import org.openwis.dataservice.common.util.JndiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless(name = "DatabaseInitializer")
public class DatabaseInitializerImpl implements DatabaseInitializer, ConfigurationInfo {
	
	private final Logger LOG = LoggerFactory.getLogger(DatabaseInitializerImpl.class);
	
	private ProductMetadataService pmds;

	@PersistenceContext
	protected EntityManager entityManager;
	
	private ProcessedRequest processedRequest;
		private Long processedRequestId;
		
	private MailDiffusion diffusion;
		private Long diffusionId;
		
	private RMDCNDissemination dissemination;
		private Long disseminationId;
		
	private Subscription subscription;
		private Long subscriptionId;
		
	private EventBasedFrequency frequency;
		private Long frequencyId;
		
	private ProductMetadata pm;
		private Long pmId;
		
	private MSSFSSChannel channel;
		private Long channelId;
		
	private Temporal updateFrequency;
		private Long updateFrequencyId;
	

	public Long initForExtraction() {
		emptyAllDBTables();
		createProductMetadataTestEntries();
		createDiffusionTestEntry();
		createDisseminationTestEntry();		
		createFrequencyTestEntry();
		createSubscriptionTestEntry();
		createProcessedRequestTestEntry();
		createMSSFSSChannelTestEntry();
		createUpdateFrequencyTestEntry();
		return processedRequestId;
	}
	
	public void initForCollection(){
		emptyAllDBTables();
		// product 1
		ProductMetadata pm1 = new ProductMetadata();		
		pm1.setDataPolicy("B");
		pm1.setFed(false);
		pm1.setFileExtension("met");
		pm1.setFncPattern(".*X.*");
		pm1.setGtsCategory("WMO NonEssential");
		pm1.setIngested(false);
		pm1.setLocalDataSource("B");
		pm1.setOriginator("EFGH");
		pm1.setPriority(1);
		pm1.setProcess("Y");
		pm1.setTitle("Product 2");
		pm1.setUrn("urn:x-wmo:md:int.wmo.wis::FCSN32ESAI");
		pm1.setStopGap(false);
		getProductMetadataService().createProductMetadata(pm1);
		
		// product 2
		ProductMetadata pm2 = new ProductMetadata();		
		pm2.setDataPolicy("C");
		pm2.setFed(false);
		pm2.setFileExtension("ps");
		pm2.setFncPattern(".*P.*");
		pm2.setGtsCategory("WMO Additional");
		pm2.setIngested(false);
		pm2.setLocalDataSource("C");
		pm2.setOriginator("IJKL");
		pm2.setPriority(2);
		pm2.setProcess("X");
		pm2.setTitle("Product 3");
		pm2.setUrn("urn:x-wmo:md:int.wmo.wis::FCSN33ESWI");
		pm2.setStopGap(false);
		getProductMetadataService().createProductMetadata(pm2);
		
		// product 3
		ProductMetadata pm3 = new ProductMetadata();		
		pm3.setDataPolicy("D");
		pm3.setFed(false);
		pm3.setFileExtension("ps");
		pm3.setFncPattern(".*Q.*");
		pm3.setGtsCategory("WMO Essential");
		pm3.setIngested(false);
		pm3.setLocalDataSource("D");
		pm3.setOriginator("MNOP");
		pm3.setPriority(2);
		pm3.setProcess("X");
		pm3.setTitle("Product 4");
		pm3.setUrn("urn:x-wmo:md:int.wmo.wis::HPSQ89EGRR");
		pm3.setStopGap(false);
		getProductMetadataService().createProductMetadata(pm3);
		
		// product 4
		ProductMetadata pm4 = new ProductMetadata();		
		pm4.setDataPolicy("E");
		pm4.setFed(false);
		pm4.setFileExtension("tif");
		pm4.setGtsCategory("WMO Essential");
		pm4.setIngested(false);
		pm4.setLocalDataSource("E");
		pm4.setOriginator("QRST");
		pm4.setPriority(1);
		pm4.setProcess("X");
		pm4.setTitle("Product 5");
		pm4.setUrn("urn:x-wmo:md:int.wmo.wis::DDDD11EGRR");
		pm4.setStopGap(false);
		getProductMetadataService().createProductMetadata(pm4);
		
//		// product 5
//		ProductMetadata pm5 = new ProductMetadata();		
//		pm5.setDataPolicy("F");
//		pm5.setFed(false);
//		pm5.setFileExtension("tif");
//		pm5.setFncPattern(".*L.*");
//		pm5.setGtsCategory("WMO Essential");
//		pm5.setIngested(false);
//		pm5.setLocalDataSource("F");
//		pm5.setOriginator("UVWX");
//		pm5.setPriority(1);
//		pm5.setProcess("X");
//		pm5.setTitle("Product 6");
//		pm5.setUrn("urn:x-wmo:md:int.wmo.wis::DDDD11EGRX");
//		getProductMetadataService().createProductMetadata(pm5);
		
		// product 6
		ProductMetadata pm6 = new ProductMetadata();		
		pm6.setDataPolicy("A");
		pm6.setFed(false);
		pm6.setFileExtension("ps");
		pm6.setFncPattern(".*L.*");
		pm6.setGtsCategory("WMO Essential");
		pm6.setIngested(false);
		pm6.setLocalDataSource("A");
		pm6.setOriginator("ABCD");
		pm6.setPriority(2);
		pm6.setProcess("X");
		pm6.setTitle("Product 1");
		pm6.setUrn("urn:x-wmo:md:int.wmo.wis::FCSN32ESWI");
		pm6.setStopGap(false);
		getProductMetadataService().createProductMetadata(pm6);
		
		// product 7
		ProductMetadata pm7 = new ProductMetadata();		
		pm7.setDataPolicy("A");
		pm7.setFed(false);
		pm7.setFileExtension("ps");
		pm7.setFncPattern("^B_SMVF11.*");
		pm7.setGtsCategory("WMO Essential");
		pm7.setIngested(false);
		pm7.setLocalDataSource("A");
		pm7.setOriginator("ABCD");
		pm7.setPriority(2);
		pm7.setProcess("X");
		pm7.setTitle("Product 1");
		pm7.setUrn("urn:x-wmo:md:int.wmo.wis::SMVF11BIRK");
		pm7.setStopGap(false);
//		getProductMetadataService().createProductMetadata(pm7);
		
//		IngestionFilter ingestionFilter = new IngestionFilter();
//		ingestionFilter.setDescription("Description 1");
//		ingestionFilter.setRegex(".*FC.*");
//		entityManager.persist(ingestionFilter);
//		
//		IngestionFilter ingestionFilter2 = new IngestionFilter();
//		ingestionFilter2.setDescription("Description 2");
//		ingestionFilter2.setRegex(".*");
//		entityManager.persist(ingestionFilter2);
//		
//		FeedingFilter feedingFilter = new FeedingFilter();
//		feedingFilter.setDescription("Feeding Filter");
//		feedingFilter.setRegex(".*");
//		entityManager.persist(feedingFilter);
	}
	
	public void emptyDatabaseTables(){
		emptyAllDBTables();
	}
	
	private void emptyAllDBTables(){
//		Query feedingFilterTableDeleteQuery = entityManager.createQuery("DELETE FROM FeedingFilter ff");
//		feedingFilterTableDeleteQuery.executeUpdate();
//		entityManager.flush();
		
		Query requestsParameterTableDeleteQuery = entityManager.createNativeQuery("DELETE FROM OPENWIS_REQUESTS_PARAMETERS");
		requestsParameterTableDeleteQuery.executeUpdate();
		entityManager.flush();
		
		Query processedRequestTableDeleteQuery = entityManager.createQuery("DELETE FROM ProcessedRequest pr");
		processedRequestTableDeleteQuery.executeUpdate();
		entityManager.flush();
		
		Query subscriptionTableDeleteQuery = entityManager.createQuery("DELETE FROM Subscription sub");
		subscriptionTableDeleteQuery.executeUpdate();
		entityManager.flush();
		
		Query patternMetadataMappingTableDeleteQuery = entityManager.createQuery("DELETE FROM PatternMetadataMapping pmm");
		patternMetadataMappingTableDeleteQuery.executeUpdate();
		entityManager.flush();				
		
		Query disseminationJobTableDeleteQuery = entityManager.createQuery("DELETE FROM DisseminationJob dj");
		disseminationJobTableDeleteQuery.executeUpdate();
		entityManager.flush();
		
		Query cachedFileTableDeleteQuery = entityManager.createQuery("DELETE FROM CachedFile cf");
		cachedFileTableDeleteQuery.executeUpdate();
		entityManager.flush();
		
		Query mappedMetadataTableDeleteQuery = entityManager.createQuery("DELETE FROM MappedMetadata mm");
		mappedMetadataTableDeleteQuery.executeUpdate();
		entityManager.flush();
		
//		Query ingestionFilterTableDeleteQuery = entityManager.createQuery("DELETE FROM IngestionFilter if");
//		ingestionFilterTableDeleteQuery.executeUpdate();
//		entityManager.flush();
		
		// #######
		
		Query productMetadataTableDeleteQuery = entityManager.createQuery("DELETE FROM ProductMetadata pm");
		productMetadataTableDeleteQuery.executeUpdate();
		entityManager.flush();
		
		Query disseminationTableDeleteQuery = entityManager.createQuery("DELETE FROM Dissemination diss");
		disseminationTableDeleteQuery.executeUpdate();
		entityManager.flush();
		
		Query diffusionTableDeleteQuery = entityManager.createQuery("Delete FROM Diffusion dif");
		diffusionTableDeleteQuery.executeUpdate();
		entityManager.flush();
		
		Query frequencyTableDeleteQuery = entityManager.createQuery("DELETE FROM Frequency f");
		frequencyTableDeleteQuery.executeUpdate();
		entityManager.flush();
		
		Query channelTableDeleteQuery = entityManager.createQuery("DELETE FROM MSSFSSChannel ch");
		channelTableDeleteQuery.executeUpdate();
		entityManager.flush();
		
		Query updateFrequencyTableDeleteQuery = entityManager.createQuery("DELETE FROM UpdateFrequency uf");
		updateFrequencyTableDeleteQuery.executeUpdate();
		entityManager.flush();
		
		Query productAdvertisementDeleteQuery = entityManager.createQuery("DELETE FROM ProductAdvertisementEntity pae");
		productAdvertisementDeleteQuery.executeUpdate();
		entityManager.flush();
		
		Query obsoleteAdvertisementDeleteQuery = entityManager.createQuery("DELETE FROM ObsoleteAdvertisementEntity oae");
		obsoleteAdvertisementDeleteQuery.executeUpdate();
		entityManager.flush();
	}

	private void createProductMetadataTestEntries(){
	//		// product 0
	//		pm = new ProductMetadata();		
	//		pm.setDataPolicy("A");
	//		pm.setFed(false);
	//		pm.setFileExtension("ps");		
	//		pm.setGtsCategory("WMO Essential");
	//		pm.setIngested(false);
	//		pm.setLocalDataSource("A");
	//		pm.setOriginator("ABCD");
	//		pm.setPriority(2);
	//		pm.setProcess("X");
	//		pm.setTitle("Product 1");
	//		pm.setUrn("urn:x-wmo:md:int.wmo.wis::UAPF01NTAA");
	//		pmId = getProductMetadataService().createProductMetadata(pm);
			
			// product 1
			pm = new ProductMetadata();		
			pm.setDataPolicy("A");
			pm.setFed(false);
			pm.setFileExtension("ps");
			pm.setFncPattern(".*L.*");
			pm.setGtsCategory("WMO Essential");
			pm.setIngested(false);
			pm.setLocalDataSource("A");
			pm.setOriginator("ABCD");
			pm.setPriority(3);
			pm.setProcess("X");
			pm.setTitle("Product 1");
			pm.setUrn("urn:x-wmo:md:int.wmo.wis::FCSN32ESWI");
			pm.setStopGap(false);
			pmId = getProductMetadataService().createProductMetadata(pm);
		}

	private void createDiffusionTestEntry(){
		diffusion = new MailDiffusion();
		diffusion.setAddress("joscha.kulbatzki@vcs.de");
		diffusion.setFileName("file.txt");
		diffusion.setHeaderLine("headerline");
		diffusion.setMailAttachmentMode(MailAttachmentMode.AS_ATTACHMENT);
		diffusion.setMailDispatchMode(MailDispatchMode.TO);
		diffusion.setSubject("Dissemination");
		entityManager.persist(diffusion);
		diffusionId = diffusion.getId();
	}

	private void createDisseminationTestEntry(){
		diffusion = (MailDiffusion) entityManager.createQuery("select diff from Diffusion diff where id = '" + diffusionId + "'").getSingleResult();		
		dissemination = new RMDCNDissemination();
		dissemination.setDiffusion(diffusion);	
		dissemination.setZipMode(DisseminationZipMode.ZIPPED);
		entityManager.persist(dissemination);
		disseminationId = dissemination.getId();
	}

	private void createFrequencyTestEntry(){
		frequency = new EventBasedFrequency();
		frequency.setZipped(false);
		entityManager.persist(frequency);
		frequencyId = frequency.getId();
	}

	private void createSubscriptionTestEntry(){
		subscription = new Subscription();
		Set<Parameter> parameterSet = new LinkedHashSet<Parameter>();
		Parameter parameter = new Parameter();
		parameter.setCode(ParameterCode.TIME_INTERVAL);
		Set<Value> valueSet = new LinkedHashSet<Value>();
		Value value = new Value();
		value.setValue("14:00Z/16:00Z");
		valueSet.add(value);
		parameter.setValues(valueSet);
		parameterSet.add(parameter);
		subscription.setParameters(parameterSet);
		subscription.setValid(true);
		subscription.setExtractMode(ExtractMode.GLOBAL);
		frequency = (EventBasedFrequency) entityManager.createQuery("select f from Frequency f where id = '" + frequencyId + "'").getSingleResult();
		subscription.setFrequency(frequency);
		subscription.setLastEventDate(null);
		dissemination = (RMDCNDissemination) entityManager.createQuery("select diss from Dissemination diss where id = '" + disseminationId + "'").getSingleResult();
		subscription.setPrimaryDissemination(dissemination);
		pm = (ProductMetadata) entityManager.createQuery("select pm from ProductMetadata pm where id = '" + pmId + "'").getSingleResult();
		subscription.setProductMetadata(pm);
		subscription.setRequestType("SUBSCRIPTION");
		Date now = new Date(System.currentTimeMillis());
		subscription.setStartingDate(now);
		subscription.setBackup(false);
		subscription.setEmail("joscha.kulbatzki@vcs.de");
		subscription.setUser("user");
		subscription.setClassOfService(ClassOfService.GOLD);
		entityManager.persist(subscription);
		subscriptionId = subscription.getId();
	}
	
	private void createProcessedRequestTestEntry(){
		Date now = Calendar.getInstance().getTime();
	
		processedRequest = new ProcessedRequest();
		processedRequest.setCreationDate(now);
		processedRequest.setRequestResultStatus(RequestResultStatus.CREATED);
		processedRequest.setUri("TEST_" + now);
		subscription = (Subscription) entityManager.createQuery("select sub from Subscription sub where id = '" + subscriptionId + "'").getSingleResult();
		processedRequest.setRequest(subscription);
		entityManager.persist(processedRequest);
		processedRequestId = processedRequest.getId();
	}

	private void createMSSFSSChannelTestEntry(){
		channel = new MSSFSSChannel();
		channel.setChannel("Testchannel");
		entityManager.persist(channel);
		channelId = channel.getId();
	}
	
	private void createUpdateFrequencyTestEntry(){
		updateFrequency = new Temporal();
		Date from = new Date(System.currentTimeMillis() - 31536000000l);
		Date to = new Date(System.currentTimeMillis() + 3153600000000l);
		updateFrequency.setFrom(from);
		updateFrequency.setTo(to);
		entityManager.persist(updateFrequency);
		updateFrequencyId = updateFrequency.getId();
	}
	
	private ProductMetadataService getProductMetadataService(){
		if (pmds == null){
			try {
				InitialContext context = new InitialContext();
//				Properties properties = new Properties();
//		        properties.put("java.naming.factory.initial","org.jnp.interfaces.NamingContextFactory");
//		        properties.put("java.naming.factory.url.pkgs","org.jboss.naming rg.jnp.interfaces");
//		        properties.put("java.naming.provider.url", JndiUtils.getString(PRODUCT_METADATA_SERVICE_PROVIDER_URL_KEY));
//		        context = new InitialContext(properties);
				pmds = (ProductMetadataService) context.lookup(JndiUtils.getString(PRODUCT_METADATA_SERVICE_URL_KEY));
			}
			catch (NamingException e) {
				e.printStackTrace();
			}
		}
		return pmds;		
	}
}