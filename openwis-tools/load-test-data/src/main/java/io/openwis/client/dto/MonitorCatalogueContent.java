package io.openwis.client.dto;

import java.util.List;

public class MonitorCatalogueContent {
   private int count;
   private List<Metadata> metadatas;
   
   public int getCount() {
      return count;
   }
   
   public void setCount(int count) {
      this.count = count;
   }
   
   public List<Metadata> getMetadatas() {
      return metadatas;
   }
   
   public void setMetadatas(List<Metadata> metadatas) {
      this.metadatas = metadatas;
   }
}
