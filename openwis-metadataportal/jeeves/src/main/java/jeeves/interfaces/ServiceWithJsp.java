package jeeves.interfaces;

import java.util.Map;

import jeeves.server.context.ServiceContext;

import org.jdom.Element;

/**
 * GUI (=sub-) Service that can be used in JSP forward.
 */
public interface ServiceWithJsp {

   /**
    * Execute the service and returns an attribute map that will be passed to the JSP as request attribute.
    */
   public Map<String, Object> execWithJsp(Element params, ServiceContext context) throws Exception;
}
