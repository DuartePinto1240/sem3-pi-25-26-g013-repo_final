# USAC15 — Send Data to Board

## 1. Requirements Engineering

### 1.1. User Story Description

**Context:**  
The railway control system includes a **Manager** component responsible for coordinating system behavior and a **Board** component responsible for presenting system information to the operator.  
The Board acts as a visual dashboard, sharing the same console with the UI, and must receive structured data from the Manager to display the current system state.

**User Story:**  
> As a system operator, I want the Manager component to send operational data to the Board component, so that the Board can display the current system status in a clear and readable format.

---

### 1.2. Customer Specifications and Clarifications

- **Manager Component:**
  - The Manager is responsible for collecting and aggregating relevant system data.
  - During its execution loop, the Manager must send updated data to the Board component.
  - The data transmission must occur through a dedicated operation, conceptually named:
    ```
    send_data_to_board(data)
    ```

- **Board Component:**
  - The Board receives data sent by the Manager.
  - Its responsibility is limited to **displaying** the received data.
  - The Board must not modify the data contents.
  - The display must be human-readable and may include visual formatting such as boxes, separators, or icons.

- **Console Sharing:**
  - The Board shares the same console output with the UI component.
  - Therefore, output formatting must be concise and clearly delimited to avoid confusion with UI messages.

- **Data Content:**
  - The transmitted data must represent the current operational state of the system.
  - At minimum, the data should include:
    - Track states
    - Sensor readings (if available)
    - Current system or operational status

---

### 1.3. Acceptance Criteria

| ID | Requirement |
|----|-------------|
| **AC1** | The Manager must invoke `send_data_to_board(data)` during its execution loop. |
| **AC2** | The data sent to the Board must represent the current system state at the moment of transmission. |
| **AC3** | The Board must correctly receive and interpret the transmitted data. |
| **AC4** | The Board must display the received data in a clear, human-readable format on the console. |
| **AC5** | Board output must be visually distinguishable from UI messages (e.g., separators or headers). |
| **AC6** | If no data is available, the Board must display a valid empty or default state instead of failing. |

---

### 1.4. Found-Out Dependencies

- **Software Components:**
  - Depends on the **Manager** component for data production.
  - Depends on the **Board** component for data visualization.
- **Architecture:**
  - Must integrate with the Manager execution loop defined in previous US.
- **Implementation Constraints:**
  - Communication mechanism between Manager and Board must be consistent with the project’s IPC or function-call architecture.

---

### 1.5. Input and Output Data

**Input (to Manager):**
- Internal system data structures representing:
  - Track states
  - Sensor values
  - System operational flags

**Input (to Board):**
- A structured data payload sent by the Manager via `send_data_to_board(data)`.

**Output (from Board):**
- Console output displaying the received data, formatted as a status panel or dashboard.

---

## 2. Design & Algorithm

### 2.1. Algorithm Overview

**Manager Component:**
1. Collect current system state data.
2. Aggregate the data into a single data structure.
3. Call `send_data_to_board(data)`.

**Board Component:**
1. Receive data from the Manager.
2. Parse the received structure or message.
3. Format the data for display.
4. Print the formatted output to the shared console.

---

## 3. Error Handling

- If the data payload is NULL or invalid:
  - The Board must not crash.
  - A default or error message must be displayed.
- Failures in data transmission must not interrupt the Manager execution loop.
