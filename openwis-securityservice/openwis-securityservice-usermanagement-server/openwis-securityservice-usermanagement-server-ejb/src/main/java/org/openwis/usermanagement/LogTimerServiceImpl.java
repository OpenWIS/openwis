package org.openwis.usermanagement;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;

import org.openwis.management.alert.AlertService;
import org.openwis.management.utils.SecurityServiceAlerts;
import org.openwis.usermanagement.model.user.OpenWISUser;
import org.openwis.usermanagement.util.JNDIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Local(LogTimerService.class)
@Stateless(name = "LogTimerService")
public class LogTimerServiceImpl implements LogTimerService {

   /** The logger. */
   private static Logger logger = LoggerFactory.getLogger(LogTimerServiceImpl.class);

   /** The timer service. */
   @Resource
   private TimerService timerService;

   /** The authentication log timer name. */
   public static final String NAME = "AUTH_LOG_TIMER";

   /** Date/time format used in OpenAM logs */
   public static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

   private UserManagementServiceImpl userManagementService = new UserManagementServiceImpl();

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.LogTimerService#destroy()
    */
   @SuppressWarnings("unchecked")
   @Override
   public void destroy() {
      Collection<Timer> timersCollection = timerService.getTimers();
      // Generics way
      for (Timer timer : timersCollection) {
         if (timer.getInfo().equals(NAME)) {
            timer.cancel();
            logger.info("Timer: {} has been removed.", NAME);
         }
      }
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.LogTimerService#start(long)
    */
   @Override
   public void start(long interval) {
      destroy();
      timerService.createTimer(interval, interval, NAME);
      logger.info("Timer: {} created with period = {}", NAME, Long.valueOf(interval));
   }

   /**
    * {@inheritDoc}
    * @see org.openwis.usermanagement.LogTimerService#timeout(javax.ejb.Timer)
    */
   @Override
   @Timeout
   public void timeout(Timer timer) {
      // Check OpenAM Logs
      logger.debug("Check OpenAM logs");

      // Get the amAuthentication.error file
      File amAuthenticationErrorFile = new File(JNDIUtils.getInstance().getLogTimerFile());

      File lastDateFile = new File(amAuthenticationErrorFile.getAbsoluteFile().getParent(),
            "openWisLogDate.txt");

      try {
         Calendar newLastDate = null;
         Calendar lastDate = null;
         if (lastDateFile.exists()) {
            lastDate = getLastDate(lastDateFile);
         }
         if (lastDate != null && logger.isDebugEnabled()) {
            logger.debug("Checking logs from date {}", DATE_TIME_FORMAT.format(lastDate.getTime()));
         }

         FileInputStream fstream = new FileInputStream(amAuthenticationErrorFile);
         DataInputStream in = new DataInputStream(fstream);
         BufferedReader br = new BufferedReader(new InputStreamReader(in));
         String strLine;
         //Read File Line By Line
         while ((strLine = br.readLine()) != null) {
            if (!strLine.contains("AUTHENTICATION-201")) {
               continue;
            }
            String[] error = strLine.split("\"");
            if (error.length < 5) {
               continue;
            }
            try {
               newLastDate = Calendar.getInstance();
               newLastDate.setTime(DATE_TIME_FORMAT.parse(error[1]));
            } catch (ParseException e) {
               // can't parse date, skip line
               continue;
            }

            if (lastDate == null || !newLastDate.after(lastDate)) {
               continue;
            }

            checkUser(error[4].trim());
         }
         //Close the input stream
         in.close();

         setActualDate(lastDateFile, newLastDate);
      } catch (Exception e) {//Catch exception if any
         logger.error("Error during LogTimerService: ", e);
      }
   }

   /**
    * Check user; errors are catched, so that this user is skipped for next timer.
    * 
    * @param user the user login
    */
   private void checkUser(String user) {
      try {
         OpenWISUser u = userManagementService.getUserInfo(user);
         if (u != null) {
            String userProfile = u.getProfile();
            if ("Administrator".equals(userProfile) || "Operator".equals(userProfile)) {
               logger.warn(MessageFormat.format("Authentication failed for {0} ({1})", user,
                     userProfile));
               sendAlarm(user);
            }
         }
      } catch (Exception e) {
         logger.error("Error in LogTimerService during user checking: " + user, e);
      }
   }

   private Calendar getLastDate(File lastDateFile) throws IOException, ParseException {
      FileInputStream fstream = new FileInputStream(lastDateFile);
      DataInputStream in = new DataInputStream(fstream);
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      Calendar c;
      try {
         String result = br.readLine();
         c = Calendar.getInstance();
         c.setTime(DATE_TIME_FORMAT.parse(result));
      } catch (ParseException e) {
         // can't parse date, skip line
         c = null;
      }
      in.close();
      return c;
   }

   private void setActualDate(File lastDateFile, Calendar date) throws IOException {
      if (date != null) {
         FileWriter fw = new FileWriter(lastDateFile);
         fw.write(DATE_TIME_FORMAT.format(date.getTime()));
         fw.close();
      }
   }

   /**
    * Send authentication failed for operator/administrator alarm.
    * 
    * @param user user login
    */
   private void sendAlarm(String user) {
      AlertService alertService = ManagementServiceProvider.getAlertService();
      if (alertService == null) {
         logger.error("Could not get hold of the AlertService. No alert was passed!");
      } else {
         String source = "Security Service";
         String location = "Log Timer Service";

         String eventId = SecurityServiceAlerts.AUTHENTICATION_FAILED.getKey();

         List<Object> arguments = new ArrayList<Object>();
         arguments.add(user);

         alertService.raiseEvent(source, location, null, eventId, arguments);
      }
   }
}
