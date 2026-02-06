package org.example.model;

import org.example.manager.Replacement;

import java.util.HashMap;
import java.util.Map;
/// this is what we have in RAM
public class PhysicalMemory {
    Map<Integer, String> physicalMemory ;
    public int capacity;
    public int no_frames=0;
    public  PhysicalMemory(int capacity) {
        physicalMemory = new HashMap<Integer, String>();
        this.capacity = capacity;
        //initialize all the values
        for(int i=0;i<capacity;i++){
            physicalMemory.put(i,null);
        }
    }
    public Map<Integer, String> getPhysicalMemory() {
        return physicalMemory;
    }
    public void setPhysicalMemory(Map<Integer, String> physicalMemory) {
        this.physicalMemory = physicalMemory;
    }
    public boolean putData(Integer f, int d, String data)
    { //should we take acc that there is a d??
        if (f < 0 || f >= capacity) {
            return false;
        }
        no_frames++;
        physicalMemory.put(f, data);
        return true;
    }
    public void replaceData(Integer f, String data)
    {
        physicalMemory.replace(f, data);

    }
    public void removeData(Integer f)
    {
        no_frames--;
        physicalMemory.remove(f);
    }
    public String getData(Integer f, int d){
        String dataOnRow = physicalMemory.get(f);
        return dataOnRow+d;
    }
    public void bringFromDisk(Page p, Disk d, Replacement rM, PageTable pt)
    {
        //find a free frame where to put the page from disk
        //otherwise we need an algorithm to replace a page from here (ram)

        int frame=lookForAvailableFrame();
        System.out.println("frame form .bring from disk.:"+frame);

        if(frame!=-1){
            putData(frame, p.getD(), d.readPage(p.getP()));
            //update frame in PageTable
            p.setValid(true);
            pt.updateFrame(p, frame);

        }
        else{

            //!!!!!!!!!!!!!!
            //the page i replaced in the ram, and the page i brought from the disk
            //if the dirty bit was 1 -> put it back on the disk

            //replace
            rM.replaceInRAM(pt, this, p, d);

        }
    }
    public Integer lookForAvailableFrame()
    {
        if(no_frames==capacity)return -1;

        for(Map.Entry<Integer, String>entry : physicalMemory.entrySet()){
            if (entry.getValue()==null)
            {
                return entry.getKey();
            }
        }
        return -1;
    }

    public void setFrameData(Integer f, String data)
        {
        physicalMemory.put(f, data);
        }
    public Integer getFrame(int line) {
        int c=0;
        for (Map.Entry<Integer, String> entry : physicalMemory.entrySet()) {
            if(c==line){
                return entry.getKey();
            }
            c++;
        }
        return null;
    }
}
