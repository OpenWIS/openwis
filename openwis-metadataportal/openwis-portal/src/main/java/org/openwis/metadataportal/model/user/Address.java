/**
 * 
 */
package org.openwis.metadataportal.model.user;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public class Address {

   /**
    * Address
    * @member: address
    */
   private String address;

   /**
    * Address Zip Code
    * @member: zip
    */
   private String zip;

   /**
    * Address State
    * @member: state
    */
   private String state;

   /**
    * Address City
    * @member: city
    */
   private String city;

   /**
   * Address Country
   * @member: country
   */
   private String country;

   /**
    * Gets the address.
    * @return the address.
    */
   public String getAddress() {
      return address;
   }

   /**
    * Sets the address.
    * @param address the address to set.
    */
   public void setAddress(String address) {
      this.address = address;
   }

   /**
    * Gets the zip.
    * @return the zip.
    */
   public String getZip() {
      return zip;
   }

   /**
    * Sets the zip.
    * @param zip the zip to set.
    */
   public void setZip(String zip) {
      this.zip = zip;
   }

   /**
    * Gets the state.
    * @return the state.
    */
   public String getState() {
      return state;
   }

   /**
    * Sets the state.
    * @param state the state to set.
    */
   public void setState(String state) {
      this.state = state;
   }

   /**
    * Gets the city.
    * @return the city.
    */
   public String getCity() {
      return city;
   }

   /**
    * Sets the city.
    * @param city the city to set.
    */
   public void setCity(String city) {
      this.city = city;
   }

   /**
    * Gets the country.
    * @return the country.
    */
   public String getCountry() {
      return country;
   }

   /**
    * Sets the country.
    * @param country the country to set.
    */
   public void setCountry(String country) {
      this.country = country;
   }

}
