package org.openwis.metadataportal.services.login;

import jeeves.interfaces.Service;
import jeeves.interfaces.ServiceWithJsp;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import org.jdom.Element;

import java.util.HashMap;
import java.util.Map;

public class OpenWisLoginCaptchaGet implements Service, ServiceWithJsp {

    @Override
    public Map<String, Object> execWithJsp(Element params, ServiceContext context) throws Exception {

        Map<String, Object> attrMap = new HashMap<String, Object>();
        attrMap.put("context", context);
        return attrMap;
    }

    @Override
    public void init(String appPath, ServiceConfig params) throws Exception {

    }

    @Override
    public Element exec(Element params, ServiceContext context) throws Exception {
        return null;
    }
}
