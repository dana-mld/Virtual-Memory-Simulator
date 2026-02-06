package org.example.model;

import java.util.HashMap;

public class TLB {
  private HashMap <Integer, Integer> TranslationLookasideBuffer;
  private int capacity;
  public int no_entries=0;
  public TLB(int capacity) {
      this.TranslationLookasideBuffer = new HashMap<>();
      this.capacity = capacity;
  }
  public int lookAside(Page pageToSearch) {
      int keyPage = pageToSearch.getP();
      for(HashMap.Entry<Integer, Integer> entry : TranslationLookasideBuffer.entrySet()) {
          if(keyPage == entry.getKey()) {
              return entry.getValue();
          }
      }
      return -1;
  }
  public boolean addPage(Integer page, Integer frame)
  {
      if(no_entries < capacity) {
          TranslationLookasideBuffer.put(page, frame);
          no_entries++;
          return true;
      }
      return false;
  }
  public void addPageAfterReplacement(Integer page, Integer frame){
      TranslationLookasideBuffer.put(page, frame);
  }
  public void removePage(Integer page){

      TranslationLookasideBuffer.remove(page);
  }

    public HashMap<Integer, Integer> getTranslationLookasideBuffer() {
      return TranslationLookasideBuffer;
    }
}
