package org.example.model;

import java.sql.Time;

public class Page {
    private int p;
    private int d;
    private boolean valid;
    private boolean dirty;
    private Time lastTimeUsed;
    public Page() {
    }
    public Page(int p, int d) {
        this.p = p;
        this.d=d;
        this.dirty=false;
    }
    public Page(int p, int d, boolean valid) {
        this.p = p;
        this.d=d;
        this.valid = valid;
    }

    //the page is in the ram
    public void putPage() {
        this.valid = true;
    }
    public int getP() {
        return p;
    }
    public void setP(int p) {
        this.p = p;
    }
    public boolean isValid() {
        return valid;
    }
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    public Time getLastTimeUsed() {
        return lastTimeUsed;
    }
    public int getD() {
        return d;
    }
    public void setLastTimeUsed(Time lastTimeUsed) {
        this.lastTimeUsed = lastTimeUsed;
    }
    public boolean getDirty(){
        return dirty;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Page)) return false;
        Page other = (Page) o;
        return this.p == other.p;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(p);
    }

    public void setD(Integer d) {
        this.d = d;
    }
    public void setDirty(boolean value) {
        dirty = value;
    }


}
