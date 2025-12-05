# USAC10 — Implement and assembly the Sensors and the LightSigns devices

## 1. Requirements Engineering

### 1.1. User Story Description

**Context:**
The solution involves low-level interaction with hardware components. For implementing the Sensors and LightSigns components, the programming language must be C/C++ for Arduino.

**User Story:**
> Implement and assembly the Sensors and the LightSigns devices.

---

### 1.2. Customer Specifications and Clarifications

- **Sensors Component:**
    - The purpose of this component is to get temperature and humidity values from the sensor and provide them to the Manager component.
    - The default behavior of this component is to wait for commands from the Manager component.
    - In this particular case, it is waiting for the GTH command.
    - However, the sensor data is error-prone; therefore, to deal with such errors, the Moving Median filter technique should be used.
    - Such a technique collects the last n values, sorts them, and picks up the median.

- **LightSigns Component:**
    - The purpose of this component is to signal the state of the tracks.
    - It receives commands from the Manager component to control the LEDs.
    - In this particular case, it is waiting for a command in the following format: CMD, x.
    - **Commands:** RE to turn on the red LED; YE to turn on the yellow LED; GE to turn on the green LED; RB to blink the red LED.
    - **Constraints:** Given the exclusive state of a track, whenever it receives a command to turn on the LED of a track, it must first switch off all LEDs of that track.

- **Assembly:**
    - [cite_start]Note that this component shares hardware with the Sensors component. Fig. 7 shows the hardware of both components.

---

### 1.3. Acceptance Criteria

| ID | Requirement |
|----|-------------|
| **AC1** | **Physical Assembly:** Assemble the circuit on a breadboard including both LEDs for tracks and the Temperature/Humidity sensor, as depicted in Figure 7 . |
| **AC2** | **Sensors Output:** Upon receiving `GTH`, return a string in the format: `TOKEN&unit:xxxxxxx&value:xx#TOKEN&unit:xxxxxxxx&value:xx` (TEMP/HUM) . |
| **AC3** | **Error Handling:** Implement the Moving Median filter (sort window of *n* values and pick the middle one) to clean sensor readings . |
| **AC4** | **Light Logic:** Correctly parse commands `RE`, `YE`, `GE`, `RB` followed by track number `x` . |
| **AC5** | **Mutual Exclusion:** Ensure that turning on an LED for track `x` turns off any other active LED for the same track `x` . |

---

### 1.4. Found-Out Dependencies

- **Hardware:** Requires Raspberry Pi Pico, LEDs, and Sensors setup [cite: 9].
- **Software:** Depends on the assembly functions (e.g., `extract_data`, `format_command`) developed in Sprint 2 for string manipulation [cite: 10-11].

---

### 1.5. Input and Output Data

**Input:**
- Serial Strings: `GTH` or `CMD, x` (e.g., `GE, 1`).

**Output:**
- Serial Strings: Data formatted as `TEMP&unit:celsius&value:20...` [cite: 11].
- Physical: LEDs turning On/Off/Blinking.

---

## 2. Design & Algorithm

### 2.1. Algorithm Overview

**Sensors:**
1. Wait for serial input.
2. If `GTH`:
    - Read $N$ samples from DHT sensor.
    - Sort samples.
    - Select Median.
    - Format string.
    - Print to Serial.

**LightSigns:**
1. Wait for serial input.
2. If `CMD, x`:
    - Parse CMD (RE, YE, GE, RB) and Track ID.
    - **Critical:** Turn OFF all LEDs for Track ID.
    - Execute specific CMD (Turn ON specific Pin or Start Blink Timer).

