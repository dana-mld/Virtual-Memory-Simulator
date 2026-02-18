# Virtual-Memory-Simulator
Written in Java, it is meant to be a helper in the process of learning how OS manages the memory of the computer.


---

## 1. Project Overview

This Java-based tool simulates the translation of virtual addresses into physical addresses, demonstrating how modern hardware and software coordinate memory access.

**Key Objectives:**

- Visualize the translation flow from Virtual Addresses to Physical Addresses.  
- Demonstrate the role of the TLB (Translation Lookaside Buffer) and Page Table.  
- Simulate Page Fault handling and data retrieval from Secondary Storage (Disk).  
- Implement and visualize the LRU (Least Recently Used) replacement algorithm for both RAM and TLB.  

---

## 2. Key Features

- **Interactive Address Decomposition:** Decomposes user-input virtual addresses into Page Numbers and Offsets.  
- **Step-by-Step Execution:** A "Next Step" mechanism allows users to follow the simulation at their own pace through a snapshot system.  
- **Real-time Visualization:** Graphical displays show the current state of TLB, Page Tables, RAM, and Disk.  
- **Automated Testing:** Features a "Demo Mode" for random or sequential page access sequences.  
- **Performance Metrics:** Real-time monitoring of Hit and Miss rates.  

---

## 3. System Architecture

The simulator is built with a modular structure where each class represents a hardware or software entity:

- **MemoryManager:** Acts as the system controller, orchestrating the translation logic and page fault management.  
- **TLB:** A fast associative cache mapping Page IDs to Frame IDs.  
- **PageTable:** Maps virtual pages to physical frames and tracks state bits like Valid and Dirty.  
- **PhysicalMemory (RAM):** Represents the main memory where active pages are stored in frames.  
- **Disk:** Serves as secondary storage for pages not currently in RAM.  
- **Replacement:** Implements the LRU algorithm to identify victim pages when memory is full.  

---

## 4. Technical Implementation

- **Language:** Java  
- **UI Framework:** Swing (`javax.swing`)  
- **Data Structures:** Uses `Map<Page, Integer>` for page tables and `Map<Integer, String>` for memory storage  

**Address Translation Logic:**

- `Page Number = Virtual Address / Page Size`  
- `Offset = Virtual Address % Page Size`  

---

## 5. Simulation Logic

1. **Search TLB:** Checks for an immediate mapping. A TLB Hit completes the translation.  
2. **Search Page Table:** On a TLB Miss, the system checks the Page Table for a RAM Hit.  
3. **Handle Page Fault:** If the page is not in RAM, it is fetched from Disk. If RAM is full, the LRU Algorithm selects a page to replace.  
4. **Update Structures:** The TLB and Page Table are updated with the new mapping.  
5. **LRU Update:** The `lastTimeUsed` timestamp is updated for every access to ensure correct replacement priority.  

---

## 6. Installation & Usage

1. Ensure you have the **JDK** installed.  
2. Clone the repository:  

```bash
git clone https://github.com/yourusername/Virtual-Memory-Simulator.git
