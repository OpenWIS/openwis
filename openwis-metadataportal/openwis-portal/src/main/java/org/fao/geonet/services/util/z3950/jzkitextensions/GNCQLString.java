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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jzkit.search.util.QueryModel.InvalidQueryException;
import org.jzkit.search.util.QueryModel.QueryModel;
import org.jzkit.search.util.QueryModel.CQLString.CQLString;
import org.jzkit.search.util.QueryModel.Internal.AttrPlusTermNode;
import org.jzkit.search.util.QueryModel.Internal.AttrValue;
import org.jzkit.search.util.QueryModel.Internal.ComplexNode;
import org.jzkit.search.util.QueryModel.Internal.InternalModelNamespaceNode;
import org.jzkit.search.util.QueryModel.Internal.InternalModelRootNode;
import org.jzkit.search.util.QueryModel.Internal.QueryNode;
import org.springframework.context.ApplicationContext;
import org.z3950.zing.cql.CQLAndNode;
import org.z3950.zing.cql.CQLBooleanNode;
import org.z3950.zing.cql.CQLNode;
import org.z3950.zing.cql.CQLNotNode;
import org.z3950.zing.cql.CQLOrNode;
import org.z3950.zing.cql.CQLParser;
import org.z3950.zing.cql.CQLPrefixNode;
import org.z3950.zing.cql.CQLProxNode;
import org.z3950.zing.cql.CQLRelation;
import org.z3950.zing.cql.CQLTermNode;

/**
 * code copied and pasted from JZKit sourcecode to fix a bug in the original class.
 *
 * @author 'Ian Ibbotson <ianibbo@googlemail.com>'
 * @author 'Timo Proescholdt <tproescholdt@wmo.int>'
 * @see CQLString
 */
public class GNCQLString implements QueryModel, java.io.Serializable {

   /** The log. */
   private final Log log = LogFactory.getLog(GNCQLString.class);

   /** The default_qualifier. */
   private static String default_qualifier = "cql.serverChoice";

   /** The internal_model. */
   private InternalModelRootNode internal_model = null;

   /** The cql_root. */
   private CQLNode cql_root;

   /**
    * Instantiates a new gNCQL string.
    *
    * @param cql the the_cql_string
    */
   public GNCQLString(String cql) {
      try {
         CQLParser parser = new CQLParser();
         cql_root = parser.parse(cql);
         log.debug("Parsed CQL");
      } catch (org.z3950.zing.cql.CQLParseException cqle) {
         log.warn("Problem parsing CQL", cqle);
         // cqle.printStackTrace();
      } catch (java.io.IOException ioe) {
         log.warn("Problem parsing CQL", ioe);
         // ioe.printStackTrace();
      }
   }

   /**
    * Instantiates a new gNCQL string.
    *
    * @param cql_root the cql_root
    */
   public GNCQLString(CQLNode cql_root) {
      this.cql_root = cql_root;
   }

   /**
    * {@inheritDoc}
    * @see org.jzkit.search.util.QueryModel.QueryModel#toInternalQueryModel(org.springframework.context.ApplicationContext)
    */
   @Override
   public InternalModelRootNode toInternalQueryModel(ApplicationContext ctx)
         throws InvalidQueryException {
      if (internal_model == null) {
         internal_model = new InternalModelRootNode(translate(cql_root));
      }
      return internal_model;
   }

   /**
    * Translate.
    *
    * @param cql_node the cql_node
    * @return the query node
    */
   private QueryNode translate(CQLNode cql_node) {
      QueryNode result = null;

      if (cql_node instanceof CQLBooleanNode) {
         CQLBooleanNode cbn = (CQLBooleanNode) cql_node;
         if (cbn instanceof CQLAndNode) {
            result = new ComplexNode(translate(cbn.left), translate(cbn.right),
                  ComplexNode.COMPLEX_AND);
         } else if (cbn instanceof CQLOrNode) {
            result = new ComplexNode(translate(cbn.left), translate(cbn.right),
                  ComplexNode.COMPLEX_OR);
         } else if (cbn instanceof CQLNotNode) {
            result = new ComplexNode(translate(cbn.left), translate(cbn.right),
                  ComplexNode.COMPLEX_ANDNOT);
         } else if (cbn instanceof CQLProxNode) {
            result = new ComplexNode(translate(cbn.left), translate(cbn.right),
                  ComplexNode.COMPLEX_PROX);
         }
      } else if (cql_node instanceof CQLTermNode) {
         log.debug("Warning: We should properly translate the CQLTermNode");
         CQLTermNode cql_term_node = (CQLTermNode) cql_node;
         AttrPlusTermNode aptn = new AttrPlusTermNode();

         processCQLTermNode(aptn, cql_term_node);

         result = aptn;
      } else if (cql_node instanceof CQLPrefixNode) {
         CQLPrefixNode pn = (CQLPrefixNode) cql_node;
         result = new InternalModelNamespaceNode(pn.prefix.name,
               translate(((CQLPrefixNode) cql_node).subtree));
      }

      return result;
   }

   /**
    * Process cql term node.
    *
    * @param aptn the aptn
    * @param cql_term_node the cql_term_node
    */
   protected void processCQLTermNode(AttrPlusTermNode aptn, CQLTermNode cql_term_node) {

      aptn.setTerm(cql_term_node.getTerm());

      String qualifier = cql_term_node.getQualifier();

      if ((qualifier != null) && (qualifier.length() > 0)) {
         log.debug("Using supplied qualifier : " + qualifier);
         aptn.setAttr(AttrPlusTermNode.ACCESS_POINT_ATTR, process(qualifier));
      } else {
         log.debug("Using default qualifier");

         aptn.setAttr(AttrPlusTermNode.ACCESS_POINT_ATTR, process(default_qualifier));
      }

      // CQL Relation object:
      CQLRelation relation = cql_term_node.getRelation();
      relation.getBase();

      if (relation.getBase() != null) {
         if (relation.getBase().equalsIgnoreCase("scr")) {
            aptn.setAttr(AttrPlusTermNode.RELATION_ATTR, new AttrValue("="));
         } else if (relation.getBase().equalsIgnoreCase("exact")) {
            aptn.setAttr(AttrPlusTermNode.RELATION_ATTR, new AttrValue("="));
         } else if (relation.getBase().equalsIgnoreCase("all")) {
            aptn.setAttr(AttrPlusTermNode.RELATION_ATTR, new AttrValue("="));
         } else if (relation.getBase().equalsIgnoreCase("any")) {
            aptn.setAttr(AttrPlusTermNode.RELATION_ATTR, new AttrValue("="));
         } else {
            aptn.setAttr(AttrPlusTermNode.RELATION_ATTR, new AttrValue(relation.getBase()));
         }
      }

   }

   /**
    * Process.
    *
    * @param s the s
    * @return the attr value
    */
   private AttrValue process(String s) {
      AttrValue result = null;
      if ((s != null) && (s.length() > 0)) {
         String[] components = s.split("\\.");
         if (components.length == 1) {
            result = new AttrValue(components[0]);
         } else if (components.length == 2) {
            result = new AttrValue(components[0], components[1]);
         } else {
            result = new AttrValue(s);
         }
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      if (cql_root != null) {
         return cql_root.toCQL();
      }

      return null;
   }
}
