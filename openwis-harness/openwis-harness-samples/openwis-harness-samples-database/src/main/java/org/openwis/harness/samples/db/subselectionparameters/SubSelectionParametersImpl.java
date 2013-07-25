package org.openwis.harness.samples.db.subselectionparameters;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.openwis.harness.samples.common.parameters.ParameterUtils;
import org.openwis.harness.subselectionparameters.DatePeriodSelection;
import org.openwis.harness.subselectionparameters.Parameters;
import org.openwis.harness.subselectionparameters.SubSelectionParameters;
import org.openwis.harness.subselectionparameters.TimePeriodSelection;

/**
 * The Class SubSelectionParametersImpl. <P>
 * Explanation goes here. <P>
 */
@WebService(targetNamespace = "http://subselectionparameters.harness.openwis.org/", name = "SubSelectionParametersService", portName = "SubSelectionParametersPort", serviceName = "SubSelectionParametersService")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public class SubSelectionParametersImpl implements SubSelectionParameters {

   /** The bundles cache. */
   private final Map<Locale, ResourceBundle> bundles;

   /**
    * Default constructor.
    * Builds a SubSelectionParametersImpl.
    */
   public SubSelectionParametersImpl() {
      super();
      bundles = new LinkedHashMap<Locale, ResourceBundle>();
   }

   /**
    * Gets the sub selection parameters for request.
    *
    * @param urn the urn
    * @param lang the lang
    * @return the sub selection parameters for request
    */
   @Override
   @WebMethod()
   public @WebResult(name = "subselectionParameters")
   Parameters getSubSelectionParametersForRequest(
         @WebParam(name = "productMetadataURN") String urn, @WebParam(name = "lang") String lang) {
      return getParameters(new Locale(lang));
   }

   /**
    * Gets the sub selection parameters for subscription.
    *
    * @param urn the URN
    * @param lang the language
    * @return the sub selection parameters for subscription
    */
   @Override
   @WebMethod()
   public @WebResult(name = "subselectionParameters")
   Parameters getSubSelectionParametersForSubscription(
         @WebParam(name = "productMetadataURN") String urn, @WebParam(name = "lang") String lang) {
      return getParameters(new Locale(lang));
   }

   /**
    * Gets the parameters.
    *
    * @param locale the locale
    * @return the parameters
    */
   private Parameters getParameters(Locale locale) {
      ResourceBundle bundle = getBundle(locale);

      Parameters result = new Parameters();
      // DatePeriode
      DatePeriodSelection dps = ParameterUtils.getDatePeriodSelection(bundle);
      // Time
      TimePeriodSelection tps = ParameterUtils.getTimePeriodSelection(bundle);

      // Fill result
      dps.setNextParameter(tps);
      result.getParameters().add(dps);
      result.getParameters().add(tps);
      return result;
   }

   /**
    * Gets the bundle.
    *
    * @param locale the locale
    * @return the bundle
    */
   private synchronized ResourceBundle getBundle(Locale locale) {
      ResourceBundle result = bundles.get(locale);
      if (result == null) {
         result = ResourceBundle.getBundle("SubSelectionParameters", locale);
         bundles.put(locale, result);
      }
      return result;
   }
}
