package org.openwis.datasource.server.jaxb.serializer;

import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

// TODO: Auto-generated Javadoc
/**
 * The Class Serializer. <P>
 * Explanation goes here. <P>
 */
public final class Serializer {

   /**
    * Default constructor.
    * Builds a Serializer.
    */
   private Serializer() {
      super();
   }

   /**
    * Serialize an object.
    *
    * @param o the o
    * @return the string
    * @throws JAXBException the jAXB exception
    */
   public static String serialize(Object o) throws JAXBException {
      // write it out as XML
      final JAXBContext jaxbContext = JAXBContext.newInstance(o.getClass());
      StringWriter writer = new StringWriter();

      // for cool output
      Marshaller marshaller = jaxbContext.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.marshal(o, writer);
      return writer.toString();
   }

   /**
    * Serialize an object.
    *
    * @param o the o
    * @param writer the writer
    * @throws JAXBException the jAXB exception
    */
   public static void serialize(Object o, Writer writer) throws JAXBException {
      // write it out as XML
      final JAXBContext jaxbContext = JAXBContext.newInstance(o.getClass());
      // for cool output
      Marshaller marshaller = jaxbContext.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.marshal(o, writer);
   }

   /**
    * Generic deserialization.
    *
    * @param <O> the generic type
    * @param clazz the clazz
    * @param reader the reader
    * @return the o
    * @throws JAXBException the jAXB exception
    */
   @SuppressWarnings("unchecked")
   public static <O extends Object> O deserialize(Class<O> clazz, Reader reader)
         throws JAXBException {
      final JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
      final O object = (O) jaxbContext.createUnmarshaller().unmarshal(reader);
      return object;
   }

}
