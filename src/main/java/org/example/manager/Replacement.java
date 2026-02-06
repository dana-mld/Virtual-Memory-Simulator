package org.example.manager;

import org.example.model.*;

import java.util.Map;

/// LRU (Least Recently Used)
public class Replacement {
    public Replacement(){

    }
    //this is actually replace in ram, but page table holds the frames addresses for what is in ram

    public void replaceInRAM(PageTable pt, PhysicalMemory pm, Page newPage, Disk d)
    {
        Integer ff=-1;
        Page min = null;
        for (Map.Entry<Page, Integer> entry : pt.getPageToFrame().entrySet()) {
            if (entry.getKey().isValid()) {
                min = entry.getKey();
                ff = entry.getValue();
                break; // first valid one
            }
        }

            //go through the pt and find the frame that holds the page LRU
        for(Map.Entry<Page, Integer>entry : pt.getPageToFrame().entrySet()) {
            if (entry.getKey().isValid()) {
                Page p = entry.getKey();
                if (p.getLastTimeUsed().before(min.getLastTimeUsed())) {
                    min = p;
                    ff = entry.getValue();
                }

            }
        }
        min.setValid(false);
        if(min.getDirty()){
            min.setDirty(false);
            d.putPage(min.getP(), pm.getData(ff, 0));

        }

        //put the newPage in that frame
        String data = d.readPage(newPage.getP());
        pm.replaceData(ff, data);
        pt.updateFrame(newPage, ff);
        pt.makePageValid(newPage);
    }

    public void replaceInTLB(TLB tlb, PhysicalMemory pm, Page newPage, PageTable pt)
    {
        Integer ff=-1;
        Page min= pt.getPage(tlb.getTranslationLookasideBuffer().keySet().iterator().next());
        ff=tlb.getTranslationLookasideBuffer().values().iterator().next();
        //i need to get any elem, how??
        //go through the pt and find the frame that holds the page LRU
        for(Map.Entry<Integer, Integer>entry : tlb.getTranslationLookasideBuffer().entrySet()){
            Page p=pt.getPage(entry.getKey());

            if(p.getLastTimeUsed().before(min.getLastTimeUsed())){
                min=p;
                ff=entry.getKey();

            }

        }
        tlb.removePage(min.getP());
       tlb.addPageAfterReplacement(newPage.getP(), ff);
    }
}
