/**
 * 
 */
package org.openwis.metadataportal.model.deployment;

/**
 * The definition of a deployment. <P>
 * A deployment is composed of a logical name and a URL. <P>
 * 
 */
public class Deployment implements Comparable<Deployment> {
   /**
    * The logical name of the deployment.
    */
   private String name;

   /**
    * The URL of the deployment.
    */
   private String url;

   /**
    * Admin Mail
    */
   private String adminMail;

   //------------------------------------------------------ Constructors.

   /**
     * Default constructor.
     * Builds a Deployment.
     */
   public Deployment() {
      super();
   }

   /**
    * Default constructor.
    * Builds a Deployment.
    * @param name
    */
   public Deployment(String name) {
      super();
      this.name = name;
   }

   /**
    * Default constructor.
    * Builds a Deployment.
    * @param name the logical name of the deployment.
    * @param url the URL of the deployment.
    */
   public Deployment(String name, String url) {
      super();
      this.name = name;
      this.url = url;
   }

   /**
    * Default constructor.
    * Builds a Deployment.
    * @param name the logical name of the deployment.
    * @param url the URL of the deployment.
    * @param adminMail the mail of the administrator
    */
   public Deployment(String name, String url, String adminMail) {
      super();
      this.name = name;
      this.url = url;
      this.adminMail = adminMail;
   }

   //------------------------------------------------------ Getters & Setters.

   /**
    * Gets the name.
    * @return the name.
    */
   public String getName() {
      return name;
   }

   /**
    * Sets the name.
    * @param name the name to set.
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * Gets the url.
    * @return the url.
    */
   public String getUrl() {
      return url;
   }

   /**
    * Sets the url.
    * @param url the url to set.
    */
   public void setUrl(String url) {
      this.url = url;
   }

   /**
   * Gets the adminMail.
   * @return the adminMail.
   */
   public String getAdminMail() {
      return adminMail;
   }

   /**
    * Sets the adminMail.
    * @param adminMail the adminMail to set.
    */
   public void setAdminMail(String adminMail) {
      this.adminMail = adminMail;
   }

   //------------------------------------------------------ Overriden methods.

   /**
    * {@inheritDoc}
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      return result;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (!(obj instanceof Deployment)) {
         return false;
      }
      Deployment other = (Deployment) obj;
      if (name == null) {
         if (other.name != null) {
            return false;
         }
      } else if (!name.equals(other.name)) {
         return false;
      }
      return true;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   @Override
   public int compareTo(Deployment o) {
      return name.compareToIgnoreCase(o.getName());
   }
}
