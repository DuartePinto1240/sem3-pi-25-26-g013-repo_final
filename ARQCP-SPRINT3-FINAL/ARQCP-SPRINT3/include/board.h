#ifndef BOARD_H
#define BOARD_H

#include "manager.h"

typedef enum {
    ALERT_INFO,
    ALERT_WARNING,
    ALERT_ERROR,
    ALERT_SUCCESS
} AlertType;

// Funções de display
void clear_screen();
void display_header(const char *station_name);
void display_sensor_data(int temp, const char *temp_unit, int hum, const char *hum_unit);
void display_track_status(Track *tracks, int num_tracks);
void display_board(const char *station_name, int temp, const char *temp_unit,
                   int hum, const char *hum_unit, Track *tracks, int num_tracks);
void display_alert(const char *message, AlertType type);

#endif // BOARD_H