# USAC16 — Railway Track Management

## 1. User Story Description
As a station operator, I want to manage the operational state of railway tracks so that I can safely control train 
arrivals, departures, and track availability.
---

## 2. Requirements / Specifications
The system must provide a function or operation capable of executing one of several track-management actions.
The conceptual interface is:

```
manage_track(trackList, trackId, trainId, operation)

```

### Input
- `trackList` — collection of track objects in the station.
- `trackId` — identifier of the track to update (when applicable).
- `trainId` — identifier of the train involved (for arrival or departure).
- `operation` - code or label representing the requested action:
  - AssignTrain
  - SetNonOperational
  - SetFree
  - AuthorizeDeparture

### Operation Rules
- Assign a track to an arriving train
  - The system must search for the first FREE track.
  - If no such track exists, the operation fails.
  - Otherwise:

- Track becomes RESERVED
  - Track is associated with trainId
  - Return success.

- Set track as NONOPERATIONAL
  - Only allowed if the track is not BUSY.
  - The track’s:
    - `state` becomes NONOPERATIONAL
    - `trainId` is cleared
  - Return success.

- Set track as FREE
  - The associated train (if any) must be removed.
  - The track state becomes FREE.
  - Return success.

- Authorize train departure
  - Only allowed if the track is BUSY.
  - After departure:
    - `state` becomes FREE
    - `trainId` is cleared
  - Return success.


### Invalid Conditions

The operation must fail if any of the following occur:
- trackList is empty or missing.
- The selected trackId does not correspond to a valid track.
- The operation value is not recognised.
- For "AssignTrain":
  - No FREE track exists.
- For "SetNonOperational":
  - Track is BUSY.
- For "AuthorizeDeparture":
  - Track is not BUSY.

On failure:
- No changes to the track list should occur.

---

## 3. Acceptance Criteria

| ID      | Description                                                                                 |
|---------|---------------------------------------------------------------------------------------------|
| **AC1** | Assigning a train must select the first available FREE track.                               |
| **AC2** | If no FREE track exists, the operation must fail.                                           |
| **AC3** | Setting a track NONOPERATIONAL is only valid if the track is not BUSY.                      |
| **AC4** | Setting a track FREE must remove any associated train.                                      |
| **AC5** | Authorizing departure is only valid if the track is BUSY.                                   |
| **AC6** | All successful operations must update the track’s state and train association consistently. |
| **AC7** | Failed operations must not alter any track data.                                            |


---