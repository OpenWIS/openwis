/**
 * 
 */
package org.openwis.metadataportal.services.mock;

import java.util.Arrays;
import java.util.List;

import org.openwis.metadataportal.services.dissemination.dto.AllDiffusionDisseminationParameterDTO;
import org.openwis.metadataportal.services.dissemination.dto.AllDisseminationParametersDTO;
import org.openwis.metadataportal.services.dissemination.dto.AllMSSFSSDisseminationParameterDTO;
import org.openwis.securityservice.DisseminationTool;
import org.openwis.securityservice.OpenWISEmail;
import org.openwis.securityservice.OpenWISFTP;

/**
 * @author BAILLAGOU
 *
 */
public class MockGetDisseminationParameters {

    public static AllDisseminationParametersDTO getMock(boolean mssFssAuth, boolean rmdcnFtpAuth,
            boolean rmdcnEmailAuth, boolean publicFtpAuth, boolean publicMailAuth) {
        AllDisseminationParametersDTO dto = new AllDisseminationParametersDTO();

        //Channels.
        dto.setMssFss(new AllMSSFSSDisseminationParameterDTO());
        dto.getMssFss().setAuthorized(mssFssAuth);
        if (mssFssAuth) {
            dto.getMssFss().setMssFssChannels(getMSSFSSChannelsMock());
        }

        //RMDCN
        dto.setRmdcnDiffusion(new AllDiffusionDisseminationParameterDTO());
        dto.getRmdcnDiffusion().setAuthorizedFtp(rmdcnFtpAuth);
        dto.getRmdcnDiffusion().setAuthorizedMail(rmdcnEmailAuth);

        //	-> FTP
        if (rmdcnFtpAuth) {
            OpenWISFTP rmdcnFtp1 = new OpenWISFTP();
            rmdcnFtp1.setHost("ftp.akka.eu");
            rmdcnFtp1.setPath("repository");
            rmdcnFtp1.setUser("j.baillagou");
            rmdcnFtp1.setPassword("123456");
            rmdcnFtp1.setPort("21");
            rmdcnFtp1.setPassive(false);
            rmdcnFtp1.setDisseminationTool(DisseminationTool.RMDCN);
            rmdcnFtp1.setCheckFileSize(true);
            rmdcnFtp1.setFileName("extractAKKA.txt");
            dto.getRmdcnDiffusion().getFtp().add(rmdcnFtp1);

            OpenWISFTP rmdcnFtp2 = new OpenWISFTP();
            rmdcnFtp2.setHost("ftp.free.fr");
            rmdcnFtp2.setPath("private");
            rmdcnFtp2.setUser("julien.baillagou");
            rmdcnFtp2.setPassword("654321");
            rmdcnFtp2.setPort("22");
            rmdcnFtp2.setDisseminationTool(DisseminationTool.RMDCN);
            rmdcnFtp2.setPassive(true);
            rmdcnFtp2.setCheckFileSize(false);
            rmdcnFtp2.setFileName("extractFree.txt");
            dto.getRmdcnDiffusion().getFtp().add(rmdcnFtp2);
        }

        //	-> EMAIL
        if (rmdcnEmailAuth) {
            OpenWISEmail rmdcnMail1 = new OpenWISEmail();
            rmdcnMail1.setAddress("j.baillagou@akka.eu");
            rmdcnMail1.setHeaderLine("TOTO");
            rmdcnMail1.setMailDispatchMode("CC");
            rmdcnMail1.setSubject("Metadata is extracted");
            rmdcnMail1.setMailAttachmentMode("EMBEDDED_IN_BODY");
            rmdcnMail1.setFileName("mailResult.txt");
            rmdcnMail1.setDisseminationTool(DisseminationTool.RMDCN);
            dto.getRmdcnDiffusion().getMail().add(rmdcnMail1);

            OpenWISEmail rmdcnMail2 = new OpenWISEmail();
            rmdcnMail2.setAddress("n.guerrier@akka.eu");
            rmdcnMail2.setHeaderLine("TATA");
            rmdcnMail2.setMailDispatchMode("BCC");
            rmdcnMail2.setSubject("Metadata for Nicolas is extracted");
            rmdcnMail2.setMailAttachmentMode("AS_ATTACHMENT");
            rmdcnMail2.setFileName("mailResultNico.txt");
            rmdcnMail2.setDisseminationTool(DisseminationTool.RMDCN);
            dto.getRmdcnDiffusion().getMail().add(rmdcnMail2);
        }

        //PUBLIC
        dto.setPublicDiffusion(new AllDiffusionDisseminationParameterDTO());
        dto.getPublicDiffusion().setAuthorizedFtp(publicFtpAuth);
        dto.getPublicDiffusion().setAuthorizedMail(publicMailAuth);

        //  -> FTP
        if (publicFtpAuth) {
            OpenWISFTP publicFtp1 = new OpenWISFTP();
            publicFtp1.setHost("ftp.public_akka.eu");
            publicFtp1.setPath("repository");
            publicFtp1.setUser("j.baillagou");
            publicFtp1.setPassword("123456");
            publicFtp1.setPort("21");
            publicFtp1.setPassive(false);
            publicFtp1.setCheckFileSize(true);
            publicFtp1.setFileName("extractAKKA.txt");
            publicFtp1.setDisseminationTool(DisseminationTool.PUBLIC);
            dto.getPublicDiffusion().getFtp().add(publicFtp1);

            OpenWISFTP publicFtp2 = new OpenWISFTP();
            publicFtp2.setHost("ftp.public_free.fr");
            publicFtp2.setPath("public");
            publicFtp2.setUser("julien.baillagou");
            publicFtp2.setPassword("654321");
            publicFtp2.setPort("22");
            publicFtp2.setPassive(true);
            publicFtp2.setCheckFileSize(false);
            publicFtp2.setFileName("extractFree.txt");
            publicFtp2.setDisseminationTool(DisseminationTool.PUBLIC);
            dto.getPublicDiffusion().getFtp().add(publicFtp2);
        }

        //	-> EMAIL
        if (publicMailAuth) {
            OpenWISEmail publicMail1 = new OpenWISEmail();
            publicMail1.setAddress("j.baillagou_public@akka.eu");
            publicMail1.setHeaderLine("TOTO");
            publicMail1.setMailDispatchMode("CC");
            publicMail1.setSubject("Metadata is extracted");
            publicMail1.setMailAttachmentMode("EMBEDDED_IN_BODY");
            publicMail1.setFileName("mailResult.txt");
            publicMail1.setDisseminationTool(DisseminationTool.PUBLIC);
            dto.getPublicDiffusion().getMail().add(publicMail1);

            OpenWISEmail publicMail2 = new OpenWISEmail();
            publicMail2.setAddress("n.guerrier_public@akka.eu");
            publicMail2.setHeaderLine("TATA");
            publicMail2.setMailDispatchMode("BCC");
            publicMail2.setSubject("Metadata for Nicolas is extracted");
            publicMail2.setMailAttachmentMode("AS_ATTACHMENT");
            publicMail2.setFileName("mailResultNico.txt");
            publicMail2.setDisseminationTool(DisseminationTool.PUBLIC);
            dto.getPublicDiffusion().getMail().add(publicMail2);
        }
        return dto;
    }

    public static List<String> getMSSFSSChannelsMock() {
       return Arrays.asList("Channel_1", "Channel_2", "Channel_3");
    }
}
