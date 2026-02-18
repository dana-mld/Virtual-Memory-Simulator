package org.example.manager;

import org.example.manager.MemoryManager;
import org.example.manager.Replacement;
import org.example.model.*;
import org.example.view.MemorySimulatorGUI;
import org.example.view.SimulationConfigurator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemoryManagerTest {

    MemoryManager manager;
    MemorySimulatorGUI gui;
    TLB tlb;
    PageTable pageTable;
    PhysicalMemory pM;
    Replacement rM;
    Disk d;
    SimulationConfigurator sg;
 SimulationConfigurator.MemoryConfiguration config;
    @BeforeEach
    void setup() {
        sg = new SimulationConfigurator();
        config = new SimulationConfigurator.MemoryConfiguration(16, 2, 4, 16, 4);
        sg.setDefaultValues();
        gui = new MemorySimulatorGUI(config);
        tlb = new TLB(4);
        pageTable = new PageTable(8);
        pM = new PhysicalMemory(8);
        rM = new Replacement();
        d = new Disk(8);

        manager = new MemoryManager(gui);
    }

    @Test
    void testTLBHit() throws InterruptedException {
        Page page = new Page(0, 0);

        pageTable.addPageAnyAvailableFrame(page, pM, d, rM);
        tlb.addPage(page.getP(), pageTable.getFrameId(page));

        HitFlag hit = new HitFlag();
        int address = manager.getPhysicalAddress(page, tlb, pageTable, pM, rM, d, hit);

        assertEquals(pageTable.getFrameId(page), address);
//        assertTrue(hit.value);
    }

    @Test
    void testPageNotInTLBButInRAM() throws InterruptedException {
        Page page = new Page(4, 0);

        // in RAM but not to TLB
        pageTable.addPageAnyAvailableFrame(page, pM, d, rM);

        HitFlag hit = new HitFlag();
        int address = manager.getPhysicalAddress(page, tlb, pageTable, pM, rM, d, hit);

        assertEquals(pageTable.getFrameId(page), address);
    }

    @Test
    void testPageFault() throws InterruptedException {
        Page page = new Page(3, 0);

        // in RAM or TLB
        HitFlag hit = new HitFlag();
        int address = manager.getPhysicalAddress(page, tlb, pageTable, pM, rM, d, hit);

        assertEquals(pageTable.getFrameId(page), address); // page should now be in RAM
        //assertFalse(hit.value);
    }
/* MORE TESTS TO BE ADDED
    @Test
    void testAccessMemory() throws InterruptedException {
        Page page = new Page(8, 0);

        HitFlag hit=new HitFlag();
        System.out.println(manager.getPhysicalAddress(page, tlb, pageTable, pM, rM, d, hit));
      //  assertFalse(hit.value);
      //  assertEquals(pageTable.getFrameId(page), gui.frameFound);
    }

 */
}
