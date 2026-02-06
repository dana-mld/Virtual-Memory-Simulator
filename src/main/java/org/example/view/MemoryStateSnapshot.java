package org.example.view;

import org.example.model.PageTable;
import org.example.model.PhysicalMemory;
import org.example.model.TLB;

import java.time.LocalTime;
import java.util.HashMap;

public class MemoryStateSnapshot {
    private final String message;
    private final HashMap<String, Object> tlbState;
    private final HashMap<String, Object> pageTableState;
    private final HashMap<String, Object> physicalMemoryState;
    private final String stepDescription;
    private final LocalTime timestamp;

    public MemoryStateSnapshot(String message, TLB tlb, PageTable pageTable,
                               PhysicalMemory physicalMemory, String stepDescription) {
        this.message = message;
        this.tlbState = captureTLBState(tlb);
        this.pageTableState = capturePageTableState(pageTable);
        this.physicalMemoryState = capturePhysicalMemoryState(physicalMemory);
        this.stepDescription = stepDescription;
        this.timestamp = LocalTime.now();
    }


    private HashMap<String, Object> captureTLBState(TLB tlb) {
        HashMap<String, Object> state = new HashMap<>();
        state.put("entries", new HashMap<>(tlb.getTranslationLookasideBuffer()));
        state.put("size", tlb.no_entries);
        return state;
    }

    private HashMap<String, Object> capturePageTableState(PageTable pageTable) {
        HashMap<String, Object> state = new HashMap<>();
        state.put("entries", new HashMap<>(pageTable.getPageToFrame()));
        return state;
    }

    private HashMap<String, Object> capturePhysicalMemoryState(PhysicalMemory physicalMemory) {
        HashMap<String, Object> state = new HashMap<>();
        state.put("frames", new HashMap<>(physicalMemory.getPhysicalMemory()));
        state.put("totalFrames", physicalMemory.no_frames);
        return state;
    }

    public String getMessage() { return message; }
    public HashMap<String, Object> getTlbState() { return tlbState; }
    public HashMap<String, Object> getPageTableState() { return pageTableState; }
    public HashMap<String, Object> getPhysicalMemoryState() { return physicalMemoryState; }
    public String getStepDescription() { return stepDescription; }
    public LocalTime getTimestamp() { return timestamp; }


}