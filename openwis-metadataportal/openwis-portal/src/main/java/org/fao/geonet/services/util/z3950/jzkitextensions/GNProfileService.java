//=============================================================================
//===  Copyright (C) 2009 World Meteorological Organization
//===  This program is free software; you can redistribute it and/or modify
//===  it under the terms of the GNU General Public License as published by
//===  the Free Software Foundation; either version 2 of the License, or (at
//===  your option) any later version.
//===
//===  This program is distributed in the hope that it will be useful, but
//===  WITHOUT ANY WARRANTY; without even the implied warranty of
//===  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//===  General Public License for more details.
//===
//===  You should have received a copy of the GNU General Public License
//===  along with this program; if not, write to the Free Software
//===  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301, USA
//===
//===  Contact: Timo Proescholdt
//===  email: tproescholdt_at_wmo.int
//==============================================================================

package org.fao.geonet.services.util.z3950.jzkitextensions;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jzkit.ServiceDirectory.AttributeSetDBO;
import org.jzkit.configuration.api.Configuration;
import org.jzkit.configuration.api.ConfigurationException;
import org.jzkit.search.util.Profile.CrosswalkDBO;
import org.jzkit.search.util.Profile.ProfileDBO;
import org.jzkit.search.util.Profile.ProfileService;
import org.jzkit.search.util.Profile.ProfileServiceException;
import org.jzkit.search.util.Profile.ProfileServiceImpl;
import org.jzkit.search.util.Profile.QueryVerifyResult;
import org.jzkit.search.util.QueryModel.InvalidQueryException;
import org.jzkit.search.util.QueryModel.QueryModel;
import org.jzkit.search.util.QueryModel.Internal.AttrPlusTermNode;
import org.jzkit.search.util.QueryModel.Internal.AttrValue;
import org.jzkit.search.util.QueryModel.Internal.ComplexNode;
import org.jzkit.search.util.QueryModel.Internal.InternalModelNamespaceNode;
import org.jzkit.search.util.QueryModel.Internal.InternalModelRootNode;
import org.jzkit.search.util.QueryModel.Internal.QueryNode;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * code copied and pasted from JZKit sourcecode to fix a bug in the original class.
 *
 * @author 'Ian Ibbotson <ianibbo@googlemail.com>'
 * @author 'Timo Proescholdt <tproescholdt@wmo.int>'
 * @see ProfileServiceImpl
 */
public class GNProfileService implements ProfileService, ApplicationContextAware {

   // private Map m = new HashMap();
   /** The log. */
   private static Log log = LogFactory.getLog(GNProfileService.class);

   /** The ctx. */
   private ApplicationContext ctx = null;

   /** The configuration. */
   private Configuration configuration = null;

   /** The Constant ERROR_QUERY. */
   public static final int ERROR_QUERY = 1;

   /** The Constant ERROR_CONFIG. */
   public static final int ERROR_CONFIG = 2;

   /**
    * Instantiates a new gN profile service.
    */
   public GNProfileService() {
   }

   /**
    * {@inheritDoc}
    * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
    */
   @Override
   public void setApplicationContext(ApplicationContext ctx) {
      this.ctx = ctx;
   }

   /**
    * Sets the configuration.
    *
    * @param configuration the new configuration
    */
   public void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }

   /**
    * Gets the configuration.
    *
    * @return the configuration
    */
   public Configuration getConfiguration() {
      return configuration;
   }

   /* For backwards compatibility */
   // public InternalModelRootNode makeConformant(QueryModel qm, String profile_code) throws ProfileServiceException {
   //   return makeConformant(qm,profile_code,SEMANTIC_ACTION_STRICT);
   // }

   /**
    * {@inheritDoc}
    * @see org.jzkit.search.util.Profile.ProfileService#makeConformant(org.jzkit.search.util.QueryModel.QueryModel, java.util.Map, java.util.Map, java.lang.String)
    */
   @Override
   public InternalModelRootNode makeConformant(QueryModel qm,
         Map<String, AttributeSetDBO> valid_attributes,
         Map<String, AttrValue> service_specific_rewrite_rules, String profile_code)
         throws ProfileServiceException {

      InternalModelRootNode result = null;

      // Walk the query tree.. validate each node.
      log.debug("makeConformant profile:" + profile_code + " query:" + qm.toString());

      try {
         ProfileDBO p = configuration.lookupProfile(profile_code);

         if ((p == null) && (valid_attributes == null)) {
            log.debug("No profile defined and no valid attributes list, unable to rewrite");
            result = qm.toInternalQueryModel(ctx);
         } else {
            log.debug("Rewriting");
            result = (InternalModelRootNode) visit(qm.toInternalQueryModel(ctx), "bib-1",
                  valid_attributes, service_specific_rewrite_rules, p);
         }
      } catch (org.jzkit.search.util.QueryModel.InvalidQueryException iqe) {
         throw new ProfileServiceException(iqe.toString(), ERROR_QUERY);
      } catch (org.jzkit.configuration.api.ConfigurationException ce) {
         throw new ProfileServiceException(ce.toString(), ERROR_CONFIG);
      }

      // log.debug("makeConformant result="+result);
      return result;
   }

   /**
    * Visit.
    *
    * @param qn the qn
    * @param default_namespace the default_namespace
    * @param valid_attributes the valid_attributes
    * @param service_specific_rewrite_rules the service_specific_rewrite_rules
    * @param p the p
    * @return the query node
    * @throws InvalidQueryException the invalid query exception
    * @throws ProfileServiceException the profile service exception
    */
   private QueryNode visit(QueryNode qn, String default_namespace,
         Map<String, AttributeSetDBO> valid_attributes,
         Map<String, AttrValue> service_specific_rewrite_rules, ProfileDBO p)
         throws org.jzkit.search.util.QueryModel.InvalidQueryException, ProfileServiceException {

      if (qn == null) {
         throw new org.jzkit.search.util.QueryModel.InvalidQueryException(
               "Query node was null, unable to rewrite");
      }

      log.debug("Rewrite: visit instance of " + qn.getClass().getName());

      if (qn instanceof InternalModelRootNode) {
         InternalModelRootNode imrn = (InternalModelRootNode) qn;
         return new InternalModelRootNode(visit(imrn.getChild(), default_namespace,
               valid_attributes, service_specific_rewrite_rules, p));
      } else if (qn instanceof InternalModelNamespaceNode) {
         InternalModelNamespaceNode imns = (InternalModelNamespaceNode) qn;
         log.debug("child default attrset will be " + imns.getAttrset());
         return new InternalModelNamespaceNode(imns.getAttrset(), visit(imns.getChild(),
               imns.getAttrset(), valid_attributes, service_specific_rewrite_rules, p));
      } else if (qn instanceof ComplexNode) {
         ComplexNode cn = (ComplexNode) qn;

         QueryNode lhs = null;
         QueryNode rhs = null;

         if ((cn.getLHS() != null) && (cn.getLHS().countChildrenWithTerms() > 0)) {
            lhs = visit(cn.getLHS(), default_namespace, valid_attributes,
                  service_specific_rewrite_rules, p);
         }

         if ((cn.getRHS() != null) && (cn.getRHS().countChildrenWithTerms() > 0)) {
            rhs = visit(cn.getRHS(), default_namespace, valid_attributes,
                  service_specific_rewrite_rules, p);
         }

         if ((lhs != null) && (rhs != null)) {
            return new ComplexNode(lhs, rhs, cn.getOp());
         } else if (lhs != null) {
            return lhs;
         } else {
            return rhs;
         }
      } else if (qn instanceof AttrPlusTermNode) {
         AttrPlusTermNode aptn = null;

         if ((valid_attributes != null) && (service_specific_rewrite_rules != null)
               && (valid_attributes.size() > 0)) {
            // Use explain mode - valid queries taken from service itself
            aptn = rewriteUntilValid((AttrPlusTermNode) qn, valid_attributes,
                  service_specific_rewrite_rules, default_namespace);
         } else {
            // Use profile mode - valid queries determined from a pre-arranged profile
            aptn = rewriteUntilValid((AttrPlusTermNode) qn, p, default_namespace);
         }

         // If we are in strict mode, throw an exception
         if (aptn == null) {
            throw new ProfileServiceException(
                  "Unable to rewrite node. Semantic action was set to strict, and there appears to be no valid alternatives for node "
                        + qn, ERROR_QUERY);
         }

         return aptn;
      } else {
         throw new ProfileServiceException("Should never be here");
      }
   }

   /**
    * Rewrite until valid.
    *
    * @param q the q
    * @param valid_attributes the valid_attributes
    * @param service_specific_rewrite_rules the service_specific_rewrite_rules
    * @param default_namespace the default_namespace
    * @return the attr plus term node
    * @throws InvalidQueryException the invalid query exception
    * @throws ProfileServiceException the profile service exception
    */
   @SuppressWarnings("unchecked")
   private AttrPlusTermNode rewriteUntilValid(AttrPlusTermNode q,
         Map<String, AttributeSetDBO> valid_attributes,
         Map<String, AttrValue> service_specific_rewrite_rules, String default_namespace)
         throws org.jzkit.search.util.QueryModel.InvalidQueryException, ProfileServiceException {

      AttrPlusTermNode result = q;

      for (Iterator<String> i = q.getAttrIterator(); i.hasNext();) {
         // 1. extract and rewrite use attribute
         String attr_type = i.next();
         AttrValue av = (AttrValue) q.getAttr(attr_type);
         log.debug("Rewriting " + attr_type + "=" + av);
         AttributeSetDBO as = valid_attributes.get(attr_type);

         if (as == null) {
            throw new ProfileServiceException("No " + attr_type
                  + " attr types allowed for target repository", 4);
         }

         AttrValue new_av = rewriteUntilValid(av, as.getAttrs(), service_specific_rewrite_rules,
               default_namespace);
         log.debug("Setting attr " + attr_type + " to " + new_av);
         q.setAttr(attr_type, new_av);

      }

      log.debug(q.getAttrs());

      return result;
   }

   /**
    * Rewrite until valid.
    *
    * @param av the av
    * @param explainUseIndexes the explain_use_indexes
    * @param serviceSpecificRewriteRules the service_specific_rewrite_rules
    * @param defaultNamespace the default_namespace
    * @return the attr value
    * @throws ProfileServiceException the profile service exception
    */
   private AttrValue rewriteUntilValid(AttrValue av, Set<AttrValue> explainUseIndexes,
         Map<String, AttrValue> serviceSpecificRewriteRules, String defaultNamespace)
         throws ProfileServiceException {
      AttrValue result = av;

      if (av != null) {
         String avStrVal = av.getWithDefaultNamespace(defaultNamespace);
         if (explainUseIndexes.contains(av)) {
            log.debug("No need to rewrite, source index " + av + " is already allowed by target");
         } else {
            log.debug(MessageFormat
                  .format(
                        "Rewrite, source index {0} is disallowed, scanning server alternatives allowed={1}",
                        av, explainUseIndexes));
            boolean found = false;
            for (Iterator<Entry<String, AttrValue>> i = serviceSpecificRewriteRules.entrySet()
                  .iterator(); ((i.hasNext()) && (!found));) {
               Entry<String, AttrValue> e = i.next();
               if (e.getKey().equals(avStrVal)) {
                  AttrValue newAv = e.getValue();
                  log.debug("Possible rewrite: " + newAv);
                  if (explainUseIndexes.contains(newAv)) {
                     log.debug("Matched, replacing");
                     result = newAv;
                     found = true;
                  }
               }
            }
            if (!found) {
               log.debug("Unable to rewrite query, exception");
               throw new ProfileServiceException("Unable to rewrite access point '" + avStrVal
                     + "' to comply with service explain record", ERROR_QUERY);
            }
         }
      }

      return result;
   }

   /**
    * Continue to rewrite the source query until one which validates agains the profile is found.
    * Returns null if there are no valid expansions.
    *
    * @param q the q
    * @param p the p
    * @param defaultNamespace the default_namespace
    * @return the attr plus term node
    * @throws ProfileServiceException the profile service exception
    */
   private AttrPlusTermNode rewriteUntilValid(AttrPlusTermNode q, ProfileDBO p,
         String defaultNamespace) throws ProfileServiceException {

      log.debug("rewriteUntilValid.... def ns = " + defaultNamespace);

      QueryVerifyResult qvr = p.validate(q, defaultNamespace);
      // if ( p.isValid(q, default_namespace) )
      AttrPlusTermNode result = null;

      if (qvr.queryIsValid()) {
         log.debug("Node is conformant to profile.... return it");
         result = q;
      } else {
         log.debug(MessageFormat.format(
               "Node does not conform to profile ({0} not allowed by profile {1})",
               q.getAccessPoint(), p.getCode()));
         // Get failing attr from QVR, generate expansions, rewriteUntilValid each expansion.
         // What if failing attr was an AND..?.. Still had to be a component that failed. The Rule that returned false.
         String failingAttrType = qvr.getFailingAttr();
         AttrValue av = (AttrValue) q.getAttr(failingAttrType);

         if (av != null) {
            Set<AttrValue> possibleAlternatives = lookupKnownAlternatives(av, defaultNamespace);
            if (possibleAlternatives != null) {
               log.debug(MessageFormat.format("Check out alternatives for {0}:{1}",
                     failingAttrType, possibleAlternatives));
               for (Iterator<AttrValue> i = possibleAlternatives.iterator(); ((i.hasNext()) && (result == null));) {
                  AttrValue targetAv = i.next();
                  AttrPlusTermNode newVariant = q.cloneForAttrs();
                  newVariant.setAttr(failingAttrType, targetAv);

                  result = rewriteUntilValid(newVariant, p, defaultNamespace);
               }
            } else {
               log.debug("No expansions available. Return null");
            }
         } else {
            log.debug("Hmm.. It appears that we failed because a rule required an attr type which is not present in the query tree("
                  + failingAttrType + "). Perhaps we should add missing attrs ;)");
         }
      }

      return result;
   }

   /**
    * Lookup known alternatives.
    *
    * @param av the av
    * @param default_namespace the default_namespace
    * @return the sets the
    */
   private Set<AttrValue> lookupKnownAlternatives(AttrValue av, String default_namespace) {
      Set<AttrValue> result = null;
      try {
         String namespace = av.getNamespaceIdentifier();
         if (namespace == null) {
            namespace = default_namespace;
         }

         log.debug("Lookup mappings from namespace " + namespace + " attr value = " + av.getValue());

         CrosswalkDBO cw = configuration.lookupCrosswalk(namespace);

         if (cw != null) {
            org.jzkit.search.util.Profile.AttrMappingDBO am = cw.lookupMapping(av.getValue()
                  .toString());
            if (am != null) {
               result = am.getTargetAttrs();
            }
         } else {
            log.warn("No crosswalk available for source namespace " + namespace);
         }
      } catch (ConfigurationException ce) {
         log.warn("Problem looking up alternatives for " + av.getValue().toString(), ce);
      }

      return result;
   }

}
