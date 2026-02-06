package org.example.manager;

import org.example.view.MemoryStateSnapshot;
import org.example.model.*;
import org.example.view.MemorySimulatorGUI;

import java.sql.Time;
import java.time.LocalTime;
import java.util.*;

public class MemoryManager {
    public static List<MemoryStateSnapshot> snapshots = new ArrayList<>();
    MemorySimulatorGUI gui;
    public MemoryManager(MemorySimulatorGUI gui) {
        this.gui = gui;
    }
    public int getPhysicalAddress(Page pageToSearch, TLB tlb, PageTable pageTable, PhysicalMemory physicalMemory, Replacement rM, Disk d, HitFlag hit) throws InterruptedException {
        int physicalAdd = -1;
        //first look in TLB for that page
        physicalAdd= tlb.lookAside(pageToSearch);
        if(physicalAdd==-1)
        {
            //look in PageTable for this specific page, to have a frame corresponding
            if (pageTable.isPageInMemory(pageToSearch)) {
                saveSnapshot("Page was found in RAM. All the tables will be updated", "", tlb, pageTable, physicalMemory);

                //save the state of the tables and put them in a queue
                saveSnapshot("Updating TLB...", "", tlb, pageTable, physicalMemory);

                Integer frameFound = pageTable.getFrameId(pageToSearch);
                if(!tlb.addPage(pageToSearch.getP(), frameFound)) {
                    saveSnapshot("Not enough space in TLB...We will replace by using the algorithm: Least Recently Used", "", tlb, pageTable, physicalMemory);
                    rM.replaceInTLB(tlb, physicalMemory, pageToSearch, pageTable);
                }
                saveSnapshot("TLB was updated", "", tlb, pageTable, physicalMemory);

                //current state of the tlb

                //now, get the physical address
                //frame + d
                physicalAdd = frameFound + pageToSearch.getD();

            }
            else{
                //pick a free frame to add to make the page valid in the page table
                saveSnapshot("Page was not found. It will be brought from disk...All tables will be updated", "", tlb, pageTable, physicalMemory);
                hit.value=false;
                pageTable.addPageAnyAvailableFrame(pageToSearch, physicalMemory, d, rM);
                //find frame where it is now
                Integer frameFound = pageTable.getFrameId(pageToSearch);
                saveSnapshot("Updating TLB...", "", tlb, pageTable, physicalMemory);
                if(!tlb.addPage(pageToSearch.getP(), frameFound)) {
                    saveSnapshot("Not enough space in TLB...We will replace by using the algorithm: Least Recently Used", "", tlb, pageTable, physicalMemory);
                    rM.replaceInTLB(tlb, physicalMemory, pageToSearch, pageTable);
                }
                saveSnapshot("TLB was updated", "", tlb, pageTable, physicalMemory);

                //now, get the physical address
                //frame + d
                physicalAdd = frameFound + pageToSearch.getD();
            }
        }
        else saveSnapshot("Page was found in TLB.", "", tlb, pageTable, physicalMemory);
        // only update LRU
        //get current time
        pageToSearch.setLastTimeUsed(Time.valueOf(LocalTime.now()));
        pageTable.updatePageNewInfo(pageToSearch);
        //tlb.updatePage(pageToSearch);
        return physicalAdd;
    }
    public void getSpaceForPage(Page pageToSearch, TLB tlb, PageTable pageTable, PhysicalMemory pM, Replacement rM, Disk d) {

        //this method gets called if the valid in page from all the pt is -1 ->>> SO the page we look for is not in RAM. It's on disk
        //SO -> change in PT the valid bit, and put it in TLB.
        Integer frameFound = pM.lookForAvailableFrame();
        if (frameFound != -1) {
            //make update to the pageTable
            pageTable.makePageValid(pageToSearch);
            if(!tlb.addPage(pageToSearch.getP(), frameFound))
            {
                rM.replaceInTLB(tlb, pM, pageToSearch, pageTable);
            }
        } else {
            //there is no more space in ram, so we have to replace an existing page
            //choose which through an algorithm
            pM.bringFromDisk(pageToSearch, d, rM, pageTable);
            pageTable.makePageValid(pageToSearch);
            if(!tlb.addPage(pageToSearch.getP(), pageTable.getFrameId(pageToSearch))){
                rM.replaceInTLB(tlb, pM, pageToSearch, pageTable);
            }

        }


    }

    public boolean accessMemory(Page pageToSearch, TLB tlb, PageTable pageTable, PhysicalMemory pM, Replacement rM, Disk d) throws InterruptedException {
        HitFlag hit = new HitFlag();
        hit.value=true;
        int physicalAdd = getPhysicalAddress(pageToSearch, tlb, pageTable, pM, rM, d, hit);



        //there is no more space in ram
        if(physicalAdd == -1) {
            getSpaceForPage(pageToSearch, tlb, pageTable, pM, rM, d);
            physicalAdd = getPhysicalAddress(pageToSearch, tlb, pageTable, pM, rM, d, hit);
             d.readPage(physicalAdd);
        }
        gui.frameFound= physicalAdd;

        pM.getData(physicalAdd, pageToSearch.getD());
        return hit.value;
    }

    private void saveSnapshot(String message, String stepDescription,
                              TLB tlb, PageTable pageTable, PhysicalMemory pM) {


        MemoryStateSnapshot snap = new MemoryStateSnapshot(
                message, tlb, pageTable, pM, stepDescription
        );
        snapshots.add(snap);
    }
}
