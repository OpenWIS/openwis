package org.openwis.dataservice.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.openwis.dataservice.ConfigurationInfo;
import org.openwis.dataservice.common.util.JndiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WMOFTP {

   public static final String WMOFTP_SUFFIX = "suffix";

   private int bulletincounter = 0;

   private static final Logger LOG = LoggerFactory.getLogger(WMOFTP.class);

   private String outputDirectory;

   private Session session;

   private MessageProducer messageProducer;

   private int numberOfChecksumBytes;

   public WMOFTP(final File file, String outputDirectory, Session session,
         MessageProducer messageProducer) {
      this.outputDirectory = outputDirectory;
      this.session = session;
      this.messageProducer = messageProducer;

      String filename = file.getName().substring(0, file.getName().indexOf('.'));

      if (filename.length() != 12 && filename.length() != 8)
         return;

      try {
         createBulletinsFromFile(file);
      } catch (IOException e) {
         LOG.error("Error during parsing of packed file", e);
      } catch (RuntimeException e) {
         LOG.error("Error during parsing of packed file", e);
      }
   }

   /*
    * Manual on the GTS, A.II-15/30
    * The sending center should limit the number of messages in a file to a maximum of 100;
    * this limit should be set to a value depending upon the characteristics of the link.
    */
   private void createBulletinsFromFile(final File file) throws IOException {
      BufferedInputStream fis = null;
      byte[] buffer = null;

      int filePosition = 0;
      int fileLength = (int) file.length();

      final int MESSAGE_LENGTH_BYTESIZE = 8;
      final int FORMAT_IDENTIFIER_BYTESIZE = 2;

      try {
         fis = new BufferedInputStream(new FileInputStream(file), 65536);

         int messageLength;
         int formatIdentifier;

         while (filePosition < fileLength) {
            // read message length (to buffer)
            buffer = new byte[MESSAGE_LENGTH_BYTESIZE];
            fis.read(buffer);
            messageLength = byteArrayToInt(buffer);
            filePosition += MESSAGE_LENGTH_BYTESIZE;

            // handle optional empty message at the end of packed file
            if (messageLength == 0)
               break;

            // read format identifier (to buffer)
            buffer = new byte[FORMAT_IDENTIFIER_BYTESIZE];
            fis.read(buffer);
            formatIdentifier = buffer[1] - 48;
            filePosition += FORMAT_IDENTIFIER_BYTESIZE;

            // reading the message content (to ByteBuffer)
            buffer = new byte[messageLength];
            fis.read(buffer);
            filePosition += messageLength;

            // create bulletin from message
            Bulletin bulletin = null;
            try {
               bulletin = processPacket(buffer, file.getName(), formatIdentifier);
               if (bulletin == null)
                  throw new NullPointerException();
               LOG.debug("Extracted bulletin: " + bulletin.getAHL());
               saveBulletinAsWMOFNC(bulletin);
            } catch (Exception e) {
               LOG.error(e + " : Error while processing packed file.");
               return;
            } finally {
               bulletin = null;
            }
            bulletincounter++;
         }
      } catch (FileNotFoundException e) {
         throw new FileNotFoundException("+++ File " + file.getName() + " could not be read.");
      } catch (EOFException e) {
         throw new EOFException("+++ End of File " + file.getName()
               + " reached before finished processing.");
      } catch (IOException e) {
         throw new IOException("+++ Error while processing the file: " + file.getName());
      } finally {
         // clean up
         if (fis != null)
            fis.close();
         fis = null;
         buffer = null;
      }
   }

   private Bulletin processPacket(byte[] buffer, String filename, int formatIdentifier)
         throws IOException {
      byte[] headerArray = null;
      int headerLength = 0;
      String ahl = null;
      byte[] textArray = null;

      int bufferPosition = 0;
      int bufferLength = buffer.length;

      // handle starting line with format 00
      if (formatIdentifier == 0) {
         // skip SOH CR CR LF
         bufferPosition += 4;

         // skip nnn or nnnnn
         bufferPosition += 3;
         bufferPosition++; // skip 4th n or skip CR
         if (0x0D != buffer[bufferPosition]) { // check if there is a CR following
            // case nnnnn
            bufferPosition++; // skip 5th n
            // skip CR CR LF
            bufferPosition += 3;
         } else {
            // case nnn
            // skip remaining CR LF (as first CR has already been skipped before)
            bufferPosition += 2;
         }
      } else {
         // skip CR CR LF with format 01
         bufferPosition += 3;
      }

      // read header
      for (int i = 0; i < bufferLength - bufferPosition; i++) {
         if (buffer[i + bufferPosition] == 0x0D) {
            headerLength = i;
            break;
         }
      }
      if (headerLength == 0)
         return null;
      headerArray = new byte[headerLength];

      for (int i = 0; i < headerLength; i++) {
         headerArray[i] = buffer[bufferPosition + i];
      }
      //bufferPosition += headerLength + 3; // goto to start of text (skip header + CR CR LF [between header and text])

      // extract AHL from header
      ahl = new String(headerArray);
      ahl = ahl.replace(" ", "");
      Bulletin bulletin = FileNameParser.parseAHL(ahl, filename);

      // read text
      if (formatIdentifier == 0) {
         textArray = new byte[bufferLength - bufferPosition - 4]; // -4 ==> do not read the CR CR LF ETX at the end of the message
      } else {
         textArray = new byte[bufferLength - bufferPosition];
      }

      for (int i = 0; i < textArray.length; i++) {
         textArray[i] = buffer[bufferPosition + i];
      }

      // filling the bulletins remaining fields
      bulletin.setData(textArray);
      bulletin.setOriginator(bulletin.getLocationIndicator());
      bulletin.setType(getTypeFromMetadata(bulletin.getMetadataURN()));

      // clean up
      headerArray = null;
      textArray = null;
      ahl = null;

      return bulletin;
   }

   public final int byteArrayToInt(byte[] b) {
      int val = 0;
      for (int i = b.length - 1; i >= 0; i--) {
         val += (b[i] - 48) * (Math.pow(10, 7 - i));
      }
      return val;
   }

   /*
    * When unpacking a packed file the filetype of the resulting bulletin was originally derived from the metadata content.
    * To avoid db connections for each split files, the extension is now computed during the collection process.
    * The default filetype is always returned: "bin".
    */
   private String getTypeFromMetadata(final String metadataURN) {
      // default extension
      return "bin";
   }

   /*
    * Saves the extracted bulletins as WMOFNC files with pflag=A in the outputDirectory.
    * After bulletin extraction, a JMS message is sent to the collection queue so that it can be collected.
    */
   private void saveBulletinAsWMOFNC(Bulletin bulletin) {
      String filename;
      File outputFile;
      FileOutputStream fos;
      BufferedOutputStream bos;

      filename = bulletin.getWMOFNCFileName();
      outputFile = new File(outputDirectory, filename);

      // Always suffix filename with checksum
      String checksum = ChecksumCalculator.getChecksum(bulletin.getData(),
            getNumberOfChecksumBytes());
      String suffix = "-" + checksum;

      outputFile = new File(outputDirectory, filename + suffix);

      if (outputFile.exists()) {

         LOG.info("File {} already exists with the same checksum : {}. This one is ignored.",
               filename, checksum);
      } else {
         fos = null;
         try {
            // Write bulletin
            fos = new FileOutputStream(outputFile, false);
            bos = new BufferedOutputStream(fos);
            bos.write(bulletin.getData());
            bos.flush();
            bos.close();

            LOG.info("Saved bulletin as: " + outputFile);

            // Send message to the collection queue
            sendCollectionMessage(outputFile, suffix);
         } catch (IOException e) {
            LOG.error(e.getMessage(), e);
         } finally {
            // clean up
            filename = null;
            outputFile = null;
            fos = null;
            bos = null;
         }
      }
   }

   /**
    * Send new message to collection queue.
    * @param file the file to collect
    */
   private void sendCollectionMessage(File file, String suffix) {
      try {
         TextMessage messageToSend = session.createTextMessage(file.getAbsolutePath());
         if (suffix != null) {
            messageToSend.setStringProperty(WMOFTP_SUFFIX, suffix);
         }

         // Send message in the request queue
         messageProducer.send(messageToSend);
      } catch (JMSException e) {
         throw new RuntimeException(e);
      }
   }

   public int getNumberOfContainingBulletins() {
      return bulletincounter;
   }

   private int getNumberOfChecksumBytes() {
      if (numberOfChecksumBytes == 0) {
         numberOfChecksumBytes = JndiUtils.getInt(ConfigurationInfo.NUMBER_OF_CHECKSUM_BYTES_KEY);
      }
      return numberOfChecksumBytes;
   }

}