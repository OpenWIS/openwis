/**
 * 
 */
package eu.akka.openwis.dataservice.common.domain;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import junit.framework.Assert;

import org.junit.Test;
import org.openwis.dataservice.common.domain.entity.request.Parameter;
import org.openwis.dataservice.common.domain.entity.request.Value;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class ParameterTestCase extends AbstractTestCase {

   /**
    * Gets the relative data set.
    *
    * @return the relative data set
    * {@inheritDoc}
    * @see org.openwis.dataservice.common.domain.AbstractTestCase#getRelativeDataSet()
    */
   @Override
   public String getRelativeDataSet() {
      return "/dataset/parameter/parameters.xml";
   }

   /**
    * Test find parameter by id.
    */
   @Test
   public void testFindParameterById() {
      Parameter existingParameter = em.find(Parameter.class, Long.valueOf(11));
      Assert.assertNotNull("Paremeter shouldn't be null", existingParameter);
      Assert.assertTrue("Value should be 4", existingParameter.getValues().size() == 4);
      Value existingValue1 = existingParameter.getValues().iterator().next();
      Assert.assertNotNull("Value shouldn't be null", existingValue1);
   }

   /**
    * Test parameter create.
    *
    * @throws Exception the exception
    */
   @SuppressWarnings("unchecked")
   @Test
   public void testParameterCreate() throws Exception {
      EntityTransaction tx = em.getTransaction();
      tx.begin();

      Parameter parameter = new Parameter();
      Value value1 = new Value();
      value1.setValue("VALUE_1");
      Value value2 = new Value();
      value2.setValue("VALUE_2");
      parameter.getValues().add(value1);
      parameter.getValues().add(value2);
      em.persist(parameter);

      tx.commit();
      em.clear();

      //perform a find by id to ensure existence
      Query query = em.createQuery("SELECT param FROM Parameter param");
      List<Parameter> resultList = query.getResultList();
      Assert.assertNotNull(resultList);
      Assert.assertTrue(resultList.size() == 3);
      Query findQuery = em.createQuery("SELECT param FROM Parameter param WHERE param.id = 1");
      Parameter existingParameter = (Parameter) findQuery.getSingleResult();
      Assert.assertNotNull(existingParameter);
      Assert.assertTrue(existingParameter.getValues().size() == 2);

   }

   /**
    * Test update parameter.
    *
    * @throws Exception the exception
    */
   @Test
   public void testUpdateParameter() throws Exception {
      Parameter existingParameter = em.find(Parameter.class, Long.valueOf(11));
      Assert.assertNotNull("Parameter was null", existingParameter);
      Set<Value> existingValues = existingParameter.getValues();
      Assert.assertNotNull("Values was null", existingValues);
      Assert.assertTrue(existingValues.size() == 4);
      for (Value value : existingValues) {
         Assert.assertTrue(value.getValue().startsWith("VALUE_"));
      }

      int i = 0;
      for (Value value : existingValues) {
         value.setValue("UPDATE_VALUE_" + i++);
      }

      EntityTransaction tx = em.getTransaction();
      tx.begin();
      em.merge(existingParameter);
      tx.commit();
      em.clear();

      Parameter newParameter = em.find(Parameter.class, Long.valueOf(11));
      Assert.assertNotNull("Parameter was null", newParameter);
      Set<Value> newValues = existingParameter.getValues();
      Assert.assertNotNull("Values was null", newValues);
      Assert.assertTrue(newValues.size() == 4);
      for (Value value : newValues) {
         Assert.assertTrue(value.getValue().startsWith("UPDATE_VALUE_"));
      }
   }

   /**
    * Test delete employee.
    *
    * @throws Exception the exception
    */
   @Test
   public void testDeleteEmployee() throws Exception {
      Parameter existingParameter = em.find(Parameter.class, Long.valueOf(11));
      Assert.assertNotNull("Parameter was null", existingParameter);

      EntityTransaction tx = em.getTransaction();
      tx.begin();
      em.remove(existingParameter);
      tx.commit();
      em.clear();

      Parameter newParameter = em.find(Parameter.class, Long.valueOf(11));
      Assert.assertNull(newParameter);

      Value newValue = em.find(Value.class, Long.valueOf(11));
      Assert.assertNull(newValue);
   }
}
