/**
 * 
 */
package org.openwis.metadataportal.kernel.harvest.filesystem;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;

import org.apache.commons.lang.StringUtils;
import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.DataManager;
import org.fao.geonet.kernel.mef.MEFLib;
import org.fao.geonet.kernel.search.ISearchManager;
import org.jdom.Element;
import org.openwis.metadataportal.kernel.harvest.AbstractHarvester;
import org.openwis.metadataportal.kernel.harvest.HarvestingTaskManager;
import org.openwis.metadataportal.kernel.metadata.IMetadataAligner;
import org.openwis.metadataportal.kernel.metadata.MetadataAligner;
import org.openwis.metadataportal.kernel.metadata.collector.ChangeDateCollector;
import org.openwis.metadataportal.kernel.metadata.extractor.IMetadataAlignerExtractor;
import org.openwis.metadataportal.kernel.metadata.extractor.MetadataAlignerExtractorFactory;
import org.openwis.metadataportal.model.category.Category;
import org.openwis.metadataportal.model.harvest.HarvestingTask;
import org.openwis.metadataportal.model.metadata.Metadata;
import org.openwis.metadataportal.model.metadata.MetadataAlignerError;
import org.openwis.metadataportal.model.metadata.MetadataAlignerResult;
import org.openwis.metadataportal.model.metadata.PredicatedStylesheet;
import org.openwis.metadataportal.model.metadata.source.AbstractSource;
import org.openwis.metadataportal.model.metadata.source.HarvestingSource;
import org.openwis.metadataportal.model.metadata.source.SiteSource;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;

/**
 * Short Description goes here.
 * <P>
 * Explanation goes here.
 * <P>
 * 
 */
public class LocalFileSystemHarvester extends AbstractHarvester {

	private static final int BATCH_SIZE = 10;

	/**
	 * The task harvested.
	 */
	private HarvestingTask task;

	/**
	 * The metadata aligner.
	 */
	private IMetadataAligner metadataAligner;

	/**
	 * The number of processed elements.
	 */
	private int processed = 0;

	/**
	 * The number of elements to be processed.
	 */
	private int total = 0;

	/**
	 * Default constructor. Builds a FileSystemHarvester.
	 * 
	 * @param context
	 * @param dbms
	 */
	public LocalFileSystemHarvester(ServiceContext context, Dbms dbms) {
		super(context, dbms);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.openwis.metadataportal.kernel.harvest.AbstractHarvester#harvest(org.openwis.metadataportal.model.harvest.HarvestingTask)
	 */
	@Override
	public MetadataAlignerResult harvest(HarvestingTask task) throws Exception {
		// The task.
		this.task = task;

		// Get the geonetwork context.
		GeonetContext gc = ((GeonetContext) getContext().getHandlerContext(
				Geonet.CONTEXT_NAME));

		// Creating the managers.
		DataManager dataManager = gc.getDataManager();
		ISearchManager searchManager = gc.getSearchmanager();
		String dataDir = gc.getHandlerConfig().getMandatoryValue(
				Geonet.Config.DATA_DIR);
		String preferredSchema = (gc.getHandlerConfig().getMandatoryValue(
				"preferredSchema") != null ? gc.getHandlerConfig()
				.getMandatoryValue("preferredSchema") : "iso19139");

		metadataAligner = new MetadataAligner(getDbms(), dataManager,
				searchManager, dataDir);

		// Predicated style sheets.
		List<PredicatedStylesheet> predicatedStylesheets = new ArrayList<PredicatedStylesheet>();
		String styleSheet = task.getConfiguration().get("styleSheet");
		if (StringUtils.isNotBlank(styleSheet)) {
			PredicatedStylesheet ps = new PredicatedStylesheet(styleSheet,
					Predicates.<Element> alwaysTrue());
			predicatedStylesheets.add(ps);
		}

		// Source.
		AbstractSource source = null;
		if (Boolean.valueOf(task.getConfiguration().get("localImport")).equals(
				Boolean.TRUE)) {
			source = new SiteSource(task.getConfiguration().get(
					"authorUserName"), gc.getSiteId(), gc.getSiteName());
		} else {
			source = new HarvestingSource(this.task);
		}

		// Gets the metadatas.
		File dir = new File(this.task.getConfiguration().get("dir"));
		boolean recursive = Boolean.valueOf(task.getConfiguration().get(
				"recursive"));
		List<File> files = getFiles(dir, recursive);

		this.total = files.size();

		if (this.total > 0) {
			// Iterating on all metadatas.
			List<Metadata> mds = new ArrayList<Metadata>();
			for (int i = 0; i < files.size(); i++) {
				File file = files.get(i);
				if (!file.isDirectory()) {
					List<Metadata> metadatas = importFile(this.task
							.getConfiguration().get("fileType"),
							this.task.getCategory(), source, file, getDbms(),
							task.getConfiguration().get("authorUserName"),
							preferredSchema, dataManager);
					mds.addAll(metadatas);
					if (i % BATCH_SIZE == 0 || i == files.size() - 1) {
						// Batch of import.
						this.metadataAligner.importMetadatas(mds,
								this.task.getValidationMode(),
								predicatedStylesheets,
								new ChangeDateCollector());
						// Index all.
						this.metadataAligner.indexImportedMetadatas();

						this.processed = this.metadataAligner.getResult()
								.getTotal();
						mds.clear();
					}
				}
			}
		} else {
			// initialize the result with a default date rather than let a null
			// value
			this.metadataAligner.getResult().setDate(new Date());
		}

		// Test if metadata should be kept locally if not present on the file
		// system.
		boolean keepLocalIfDeleted = Boolean.valueOf(this.task
				.getConfiguration().get("keepLocalIfDeleted"));
		if (!keepLocalIfDeleted) {
			HarvestingTaskManager htm = new HarvestingTaskManager(getDbms());
			Set<Metadata> localMds = Sets.newHashSet(htm
					.getAllMetadataByHarvestingTask(task.getId()));
			final Set<String> processedUrns = Sets
					.newHashSet(this.metadataAligner.getProcessedMetadatas());

			Set<Metadata> filteredMds = Sets.filter(localMds,
					new Predicate<Metadata>() {

						@Override
						public boolean apply(Metadata input) {
							return !processedUrns.contains(input.getUrn());
						}
					});

			this.total += filteredMds.size();

			this.metadataAligner.deleteMetadatas(filteredMds);

			this.processed = this.metadataAligner.getResult().getTotal();
		}

		// Returns the import result.
		return this.metadataAligner.getResult();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.openwis.metadataportal.kernel.common.IMonitorable#getProcessed()
	 */
	@Override
	public int getProcessed() {
		return processed;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.openwis.metadataportal.kernel.common.IMonitorable#getTotal()
	 */
	@Override
	public int getTotal() {
		return total;
	}

	/**
	 * Gets the type of the harvester.
	 * 
	 * @return the type of the harvester.
	 */
	public static String getType() {
		return "localfilesystem";
	}

	// ----------------------------------------------------------------------
	// Private methods.

	/**
	 * Import file.
	 * 
	 * @param dto
	 *            the dto
	 * @param source
	 *            the source
	 * @param mds
	 *            the mds
	 * @param f
	 *            the f
	 * @return the metadata
	 * @throws Exception
	 *             the exception
	 */
	private List<Metadata> importFile(String fileType, Category category,
			AbstractSource source, File f, Dbms dbms, String userName,
			String preferredSchema, DataManager dataManager) throws Exception {

		// discard wrong files
		if ((fileType.startsWith("mef") && !f.getName().endsWith(".zip"))
				|| (!fileType.startsWith("mef") && !f.getName()
						.endsWith(".xml"))) {
			Log.info(Geonet.HARVESTER, "FileSystemHarvester: ignoring file "
					+ f.getName());
			return new ArrayList<Metadata>();
		}

		try {
			if (fileType.equals("mef")) {
				MEFLib.Version version = MEFLib.getMEFVersion(f);
				if (version.equals(MEFLib.Version.V2))
					fileType = "mef2";
			}

			IMetadataAlignerExtractor extractor = MetadataAlignerExtractorFactory
					.getMetadataAlignerExtractor(fileType, dbms, userName,
							preferredSchema, dataManager);
			List<Metadata> recs = extractor.extract(f);
			for (Metadata rec : recs) {
				rec.setSource(Objects.firstNonNull(rec.getSource(), source));
				rec.setCategory(Objects.firstNonNull(rec.getCategory(),
						category));
			}
			return recs;
		} catch (Exception e) {
			// Error during file parsing
			String msg = "Error while importing file: " + f.getName() + " ("
					+ e.getMessage() + ")";
			Log.error(Geonet.HARVESTER, msg);
			this.metadataAligner.getResult().getErrors()
					.add(new MetadataAlignerError("", msg));
			this.metadataAligner.getResult().incUnexpected();
			this.metadataAligner.getResult().incTotal();
			return new ArrayList<Metadata>();
		}
	}

	/**
	 * Gets a list of files of the specified directory.
	 * 
	 * @param dir
	 *            the root directory.
	 * @param recursive
	 *            <code>true</code> if files shall be searched recursively,
	 *            <code>false</code> otherwise.
	 * @return a list of files to be imported.
	 */
	private List<File> getFiles(File dir, boolean recursive) {
		List<File> metadataFiles = new ArrayList<File>();
		// test whether the directory exists or not
		if (dir.exists()) {
			for (File f : Arrays.asList(dir.listFiles(filenameFilter))) {
				if (f.isDirectory() && recursive) {
					metadataFiles.addAll(getFiles(f, recursive));
				} else if (!f.isDirectory()) {
					metadataFiles.add(f);
				}
			}
		}
		return metadataFiles;
	}

	/** The filename filter. */
	private final FilenameFilter filenameFilter = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			return !("CVS".equals(name) || name.startsWith("."));
		}
	};

}
