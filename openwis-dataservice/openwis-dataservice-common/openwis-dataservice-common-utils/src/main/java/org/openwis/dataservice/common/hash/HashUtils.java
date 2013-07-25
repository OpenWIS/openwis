/**
 * 
 */
package org.openwis.dataservice.common.hash;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Short Description goes here. <P>
 * Explanation goes here. <P>
 * 
 */
public final class HashUtils {

   /** The Constant MD5. */
   private final static String MD5 = "MD5";

   /**
    * Default constructor.
    * Builds a HashUtils.
    */
   private HashUtils() {
      //does nothing
   }

   /**
    * Return the MD5 hash of the given encoded string.
    *
    * @param toHash string to hash
    * @return hashed string
    * @throws NoSuchAlgorithmException the no such algorithm exception
    */
   public static String getMD5Digest(String toHash) throws NoSuchAlgorithmException {
      MessageDigest m = MessageDigest.getInstance(MD5);
      byte[] data = toHash.getBytes();
      m.update(data, 0, data.length);
      BigInteger i = new BigInteger(1, m.digest());
      return String.format("%1$032x", i);
   }

   /**
    * Return the MD5 hash of the given encoded string.
    *
    * @param toHash string to hash
    * @return hashed string
    * @throws NoSuchAlgorithmException the no such algorithm exception
    */
   public static String getTextMD5Digest(String toHash) throws NoSuchAlgorithmException {
      byte[] defaultBytes = toHash.getBytes();

      MessageDigest md5 = MessageDigest.getInstance(MD5);
      md5.reset();
      md5.update(defaultBytes);
      byte[] messageDigest = md5.digest();

      StringBuffer hexString = new StringBuffer();
      for (int i = 0; i < messageDigest.length; i++) {
         String hex = Integer.toHexString(0xFF & messageDigest[i]);
         if (hex.length() == 1) {
            hexString.append('0');
         }
         hexString.append(hex);
      }

      return hexString.toString();
   }

}
