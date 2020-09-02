package jeeves.resources.dbms;

import org.jdom.Element;

public class AwsDBSecret {

    String username;

    String password;

    String engine;

    String host;

    String port;

    String dbname;

    String dbInstanceIdentifier;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public String getDbInstanceIdentifier() {
        return dbInstanceIdentifier;
    }

    public void setDbInstanceIdentifier(String dbInstanceIdentifier) {
        this.dbInstanceIdentifier = dbInstanceIdentifier;
    }

    public Element toElement() {
        Element config = new Element("config");
        config.addContent(new Element("user").setText(this.getUsername()));
        config.addContent(new Element("password").setText(this.getPassword()));
        config.addContent(new Element("url").setText(String.format("jdbc:postgresql://%s:%s/%s",this.getHost(), this.getPort(), this.getDbname())));
        return config;
    }
}
