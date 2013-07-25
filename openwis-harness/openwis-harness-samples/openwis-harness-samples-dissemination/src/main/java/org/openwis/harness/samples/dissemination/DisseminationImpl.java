package org.openwis.harness.samples.dissemination;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.openwis.harness.dissemination.Dissemination;
import org.openwis.harness.dissemination.DisseminationInfo;
import org.openwis.harness.dissemination.DisseminationStatus;
import org.openwis.harness.dissemination.FTPDiffusion;
import org.openwis.harness.dissemination.MailDiffusion;
import org.openwis.harness.dissemination.RequestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebService(targetNamespace = "http://dissemination.harness.openwis.org/", name = "DisseminationImplService", portName = "DisseminationImplPort", serviceName = "DisseminationImplService")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public class DisseminationImpl implements Dissemination {

	/** The logger. */
	private static Logger LOG = LoggerFactory.getLogger(DisseminationImpl.class);

	public List<DisseminationStatus> monitorDissemination(List<String> requestIDs) {

		List<DisseminationStatus> disseminationStatusList = null;
		
		if (requestIDs != null && requestIDs.size() > 0)
		{
			StringBuffer strBuf = new StringBuffer("Dissemination harness: monitorDissemination called for following requestIDs: ");
			String sep = " ";

			disseminationStatusList = new ArrayList<DisseminationStatus>();

			for (String requestId : requestIDs)
			{
				DisseminationStatus disseminationStatus = new DisseminationStatus();

				if (requestId.equals("99")) {
					// Set to failure (for test purposes)
					disseminationStatus.setRequestStatus(RequestStatus.FAILED);
					disseminationStatus.setRequestId(requestId);
					disseminationStatus.setMessage("Dissemination failed for requestID: " + requestId);
				} 
//				else {
//               disseminationStatus.setRequestStatus(RequestStatus.ONGOING_DISSEMINATION);
//               disseminationStatus.setRequestId(requestId);
//				} 
				else {
					disseminationStatus.setRequestStatus(RequestStatus.DISSEMINATED);
					disseminationStatus.setRequestId(requestId);
					disseminationStatus.setMessage("Dissemination was successful for requestID: " + requestId);
				}
				
				disseminationStatusList.add(disseminationStatus);
				
				strBuf.append(requestId);
				strBuf.append(sep);				
			}
			
			LOG.info(strBuf.toString());
		}
		else
		{
			LOG.error("monitorDissemination called with empty requestIDs list");
		}
		
		return disseminationStatusList;
	}

	public DisseminationStatus disseminate(String requestId,
			String fileURI,
			DisseminationInfo disseminationInfo) {

		logDisseminateParameter(requestId, fileURI, disseminationInfo);

		DisseminationStatus disseminationStatus = new DisseminationStatus();
		
		disseminationStatus.setRequestStatus(RequestStatus.ONGOING_DISSEMINATION);
		disseminationStatus.setRequestId(requestId);
		disseminationStatus.setMessage("Dissemination is ongoing for requestID: " + requestId);
		
		return disseminationStatus;
	}
	
	private void logDisseminateParameter(String requestId,
			String fileURI,
			DisseminationInfo disseminationInfo)
	{
		StringBuffer strBuf = new StringBuffer("Dissemination harness: disseminate called with following parameters: ");
		strBuf.append("requestID: ");
		strBuf.append(requestId);
		strBuf.append(" | fileURI: ");
		strBuf.append(fileURI);
		strBuf.append(" | priority: ");
		strBuf.append(disseminationInfo.getPriority());
		strBuf.append(" | sla: ");
		strBuf.append(disseminationInfo.getSLA());
		strBuf.append(" | dataPolicy: ");
		strBuf.append(disseminationInfo.getDataPolicy());

		strBuf.append(" | diffusion: ");
		if (disseminationInfo.getDiffusion() instanceof MailDiffusion)
		{
			strBuf.append(getMailDiffusionContent((MailDiffusion)disseminationInfo.getDiffusion()));
		}
		else if (disseminationInfo.getDiffusion() instanceof FTPDiffusion)
		{
			strBuf.append(getFTPDiffusionContent((FTPDiffusion)disseminationInfo.getDiffusion()));
		}
		
		if (disseminationInfo.getAlternativeDiffusion() != null)
		{
			strBuf.append(" | alternativeDiffusion: ");

			if (disseminationInfo.getAlternativeDiffusion() instanceof MailDiffusion)
			{
				strBuf.append(getMailDiffusionContent((MailDiffusion)disseminationInfo.getAlternativeDiffusion()));
			}
			else if (disseminationInfo.getAlternativeDiffusion() instanceof FTPDiffusion)
			{
				strBuf.append(getFTPDiffusionContent((FTPDiffusion)disseminationInfo.getAlternativeDiffusion()));
			}
		}

		LOG.info(strBuf.toString());
	}
	
	private String getMailDiffusionContent(MailDiffusion mailDiffusion)
	{
		StringBuffer strBuf = new StringBuffer(" MailDiffusion: ");
		strBuf.append(" address: ");
		strBuf.append(mailDiffusion.getAddress());
		strBuf.append(" | subject: ");
		strBuf.append(mailDiffusion.getSubject());
		strBuf.append(" | headerLine: ");
		strBuf.append(mailDiffusion.getHeaderLine());
      strBuf.append(" | dispatchMode: ");
      strBuf.append(mailDiffusion.getDispatchMode());
      strBuf.append(" | attachmentMode: ");
      strBuf.append(mailDiffusion.getAttachmentMode());
      strBuf.append(" | filename: ");
      strBuf.append(mailDiffusion.getFileName());
	
		return strBuf.toString();
	}

	private String getFTPDiffusionContent(FTPDiffusion ftpDiffusion)
	{
		StringBuffer strBuf = new StringBuffer(" FTPDiffusion: ");
		strBuf.append(" host: ");
		strBuf.append(ftpDiffusion.getHost());
		strBuf.append(" | port: ");
		strBuf.append(ftpDiffusion.getPort());
		strBuf.append(" | user: ");
		strBuf.append(ftpDiffusion.getUser());
		strBuf.append(" | password: ");
		strBuf.append(ftpDiffusion.getPassword());
		strBuf.append(" | remotePath: ");
		strBuf.append(ftpDiffusion.getRemotePath());
      strBuf.append(" | fileName: ");
      strBuf.append(ftpDiffusion.getFileName());
      strBuf.append(" | checkFileSize: ");
      strBuf.append(ftpDiffusion.isCheckFileSize());
      strBuf.append(" | encrypted: ");
      strBuf.append(ftpDiffusion.isEncrypted());
	
		return strBuf.toString();
	}
}