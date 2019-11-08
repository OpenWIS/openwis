package org.openwis.usermanagement.model.user;

import java.io.Serializable;

/**
 * Defines an OpenWIS Address Model. <P>
 * 
 */
public class OpenWISAddress implements Serializable {

   /**
    * @member: address The address
    */
   private String address;

   /**
    * @member: city The address city
    */
   private String city;

   /**
    * @member: country The address country
    */
   private String country;

   /**
    * @member: state The address state
    */
   private String state;

   /**
    * @member: zip The address zip
    */
   private String zip;

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

}
