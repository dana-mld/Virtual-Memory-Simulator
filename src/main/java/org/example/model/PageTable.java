package org.example.model;

import org.example.manager.Replacement;

import java.util.HashMap;
import java.util.Map;

public class PageTable {
    //Here we dont necesarily talk ab capacity, here we have virtual addresses and THERE IS space
    private Map<Page, Integer> pageToFrame;

    public PageTable(int no_pages) {
        //   private int capacity;
        this.pageToFrame = new HashMap<>();
        //initialize
        // Create every page upfront
        for (int i = 0; i < no_pages; i++) {
            Page p = new Page(i, 0);
            p.setValid(false);
            pageToFrame.put(p, -1);
        }
       // this.capacity = capacity;
    }
    public Map<Page, Integer> getPageToFrame() {
        return pageToFrame;
    }
    public void setPageToFrame(Map<Page, Integer> pageToFrame) {
        this.pageToFrame = pageToFrame;
    }

    //In pageTable, there are all possible pages (those in RAM and on disk)
    public boolean putPage(Page p, Integer f)
    {

            this.pageToFrame.put(p, f);
            p.setValid(true);
            return true;
    }
    public void makePageValid(Page p)
    {
       for (Map.Entry<Page, Integer> entry : this.pageToFrame.entrySet()) {
           if(entry.getKey().equals(p))
           {
               entry.getKey().setValid(true);
           }
       }
    }
    public void addPageAnyAvailableFrame(Page p, PhysicalMemory physicalMemory, Disk d, Replacement rM)
    {
        for (Map.Entry<Page, Integer> entry : this.pageToFrame.entrySet()) {
            if(entry.getKey().getP()==p.getP())
            {
                physicalMemory.bringFromDisk(entry.getKey(), d, rM, this);
            }
        }

    }
    public Page getValidPage()
    {
        for(Map.Entry<Page, Integer> entry : this.pageToFrame.entrySet()){
            if (entry.getKey().isValid())return entry.getKey();
        }
        return null;
    }
    public Integer getValidFrameId()
    {
        return pageToFrame.get(this.getValidPage());
    }
    public boolean isPageInMemory(Page page) {
        if(pageToFrame.containsKey(page) && page.isValid()) {
            return true;
        }
        return false;
    }
    public void updateFrame(Page p, int f)
    {
        this.pageToFrame.put(p, f);
    }
    public void updatePageNewInfo(Page p)
    {
        for(Map.Entry<Page, Integer> e:this.pageToFrame.entrySet()) {
            if(e.getKey().getP()==p.getP()) {
                //copy values
                e.getKey().setValid(p.isValid());
                e.getKey().setLastTimeUsed(p.getLastTimeUsed());
            }
        }
    }
    public Page getPage(int p) {
        for(Map.Entry<Page, Integer> e:this.pageToFrame.entrySet()) {
            if(e.getKey().getP()==p) {
                return e.getKey();
            }
        }
        return null;
    }
    //  public int getCapacity() {
    //    return capacity;
    //}
    public Page getPageFromFrame(int f) {
        for(Map.Entry<Page, Integer> e:this.pageToFrame.entrySet()) {
            if(e.getKey().getP()==f) {
                return e.getKey();

            }
        }
        return null;
    }
    public Integer getFrameId(Page page)
    {
        return pageToFrame.get(page);
    }
    public void updatePage(int pageId, int frameId)
    {

    }




}
