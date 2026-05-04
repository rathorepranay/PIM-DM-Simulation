# PIM-DM Network Simulator

A visual simulation of **Protocol Independent Multicast - Dense Mode (PIM-DM)** network operations using Java Swing.

## Overview

This application provides an interactive GUI demonstration of PIM-DM multicast protocol operations, including:

- **Flood**: Broadcast packets to all reachable nodes
- **Prune**: Remove branches from the distribution tree that don't have active receivers
- **Graft**: Re-add pruned branches when new receivers appear

## Features

- **Visual Network Topology**: Interactive display of routers and their connections
- **Real-time Packet Animation**: Visualize packets moving through the network
- **Color-coded Nodes**:
  - 🟢 Green: Routers with active receivers
  - 🔴 Red: Pruned routers (removed from distribution tree)
  - ⚪ Light Gray: Regular routers
  - 🔵 Blue: Packets in transit

- **Detailed Logging**: Real-time log of all network operations
- **Interactive Controls**: Buttons to trigger Flood, Prune, and Graft operations

## How to Run

### Prerequisites

- Java Development Kit (JDK) 8 or higher

### Execution

```bash
javac PIMDMGUI.java
java PIMDMGUI
```

## Network Topology

The simulator includes a predefined network with 7 routers (A-G):

```
        A (Source)
       / \
      B   C
     / \ / \
    D  E F  G
```

**Configuration:**

- Routers D and F are configured as receivers
- Router A is the multicast source
- All routers are bidirectionally connected to their neighbors

## How It Works

### Flood Phase

- Source router broadcasts packets to all neighbors
- Flooding continues recursively through the network
- Each packet is animated moving along network links
- Operations are logged in real-time

### Prune Phase

- Evaluates each branch to determine if it has receivers
- Marks branches without receivers as pruned (red nodes)
- Pruned branches are removed from the distribution tree
- Prevents unnecessary traffic on branches without subscribers

### Graft Phase

- Simulates activation of a new receiver (Router G)
- Re-adds previously pruned branches that now have receivers
- Updates the distribution tree dynamically

## Class Structure

### `Router`

Represents a network node with:

- Name, x/y coordinates for visualization
- List of neighbor connections
- Receiver state and pruning status

### `NetworkPanel`

Custom JPanel that:

- Renders the network topology
- Displays animated packet movement
- Updates visual state of routers (color changes)

### `PIMDMGUI`

Main application class providing:

- GUI window and controls
- Flood, Prune, and Graft algorithm implementations
- Event handling for button actions

## Algorithm Details

### Flood Algorithm

- Recursive depth-first traversal from source
- Maintains visited set to prevent revisits
- Respects pruned node status
- Animates packet movement with 20-step interpolation

### Prune Algorithm

- Bottom-up tree traversal
- Marks nodes without receivers as pruned
- Logs each pruned node
- Ignores the parent connection to avoid infinite loops

### Graft Algorithm

- Traverses tree to find pruned nodes with receivers
- Removes pruning status to re-integrate branches
- Updates visualization after graft operations

## Controls

| Button    | Action                                                |
| --------- | ----------------------------------------------------- |
| **Flood** | Broadcast packets from source (Router A) to all nodes |
| **Prune** | Remove branches without receivers                     |
| **Graft** | Restore pruned branches that gain new receivers       |

## Technical Details

- **Language**: Java
- **GUI Framework**: Java Swing
- **Threading**: Uses threads for non-blocking animation
- **Animation Delay**: 40ms per animation step

## Educational Use

This simulator helps visualize and understand:

- Multicast routing concepts
- Tree-based distribution models
- Dynamic protocol adaptation
- Network bandwidth optimization techniques

## Output
**Flood:**
<img width="1103" height="582" alt="Screenshot 2026-05-04 155903" src="https://github.com/user-attachments/assets/461badbb-6a91-41ec-812a-4490ef556136" />
**Prune:**
<img width="1097" height="567" alt="Screenshot 2026-05-04 160012" src="https://github.com/user-attachments/assets/17d5b353-9ce2-447d-97cf-9337ed1dc650" />
**Graft:**
<img width="1101" height="572" alt="Screenshot 2026-05-04 160126" src="https://github.com/user-attachments/assets/9c2e62bd-0bab-4fea-9616-3abfbb3b592c" />


