package org.openwis.management.service;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.openwis.management.utils.JndiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the ConfigService.
 * 
 * @author lmika
 *
 */
@Stateless(name="ConfigService")
@Remote(ConfigService.class)
public class ConfigServiceImpl implements ConfigService {
   
   private static final Logger log = LoggerFactory.getLogger(ConfigServiceImpl.class);

   @Override
   public String getString(String key) {
      String value = JndiUtils.getString(key);
      return value;
   }
}
