package org.openwis.factorytests.performance;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

public class HomePageTest {

   public static final String HOME_PAGE_SERVICE = "/srv/en/main.home";

   private String getBaseUrl() {
      String baseUrl = System.getProperty("baseUrl");
      if (baseUrl == null || baseUrl.length() == 0) {
         baseUrl = "http://localhost:8080/openwis-portal";
      }
      return baseUrl;
   }

   private String getHomePageUri() {
      return getBaseUrl() + HOME_PAGE_SERVICE;
   }

   private void openHomePage(int i) throws HttpException, IOException {
      HttpClient client = new HttpClient();
      GetMethod homePageGet = new GetMethod(getHomePageUri());
      long start = System.currentTimeMillis();
      client.executeMethod(homePageGet);
      byte[] resp = homePageGet.getResponseBody();
      long ellapsed = System.currentTimeMillis() - start;
      System.out.println(i + " - Response received in " + ellapsed);
      if (resp.length != 13550) {
         System.out.println("!! received length: " + resp.length);
      }
   }

   private void runPerfTest() {
      int nb = 500;
      ThreadPoolExecutor serv = (ThreadPoolExecutor) Executors.newFixedThreadPool(nb);
      System.out.println("Starting threads: " + nb);
      serv.prestartAllCoreThreads();
      for (int i = 0; i < nb; i++) {
         final int index = i;
         serv.submit(new Runnable() {
            @Override
            public void run() {
               try {
                  openHomePage(index);
               } catch (Exception e) {
                  e.printStackTrace();
               }
            }
         });
      }
      long lastCount = 0;
      while (serv.getCompletedTaskCount() < nb) {
         try {
            Thread.sleep(1000);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
         long taskCount = serv.getCompletedTaskCount();
         if (lastCount != taskCount && taskCount % 2 == 0) {
//            System.out.print(".");
            lastCount = taskCount;
         }
      }
      System.out.println("End of tasks");
      serv.shutdownNow();
   }

   public static void main(String[] args) {
      HomePageTest test = new HomePageTest();
      test.runPerfTest();
   }

}
