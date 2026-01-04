#ifndef UI_H
#define UI_H

#include "manager.h"

// Funções auxiliares de input
void clear_input_buffer();
int read_int(const char *prompt, int min, int max);
void read_string(const char *prompt, char *buffer, int max_len);

// Menus
int show_login_menu(StationManager *manager);
int show_main_menu();

// Handlers
void handle_assign_track(StationManager *manager);
void handle_set_track_nonoperational(StationManager *manager);
void handle_set_track_free(StationManager *manager);
void handle_departure_order(StationManager *manager);
void handle_view_sensors(StationManager *manager);
void handle_export_log(StationManager *manager);

// Main UI loop
void run_ui(StationManager *manager);

#endif // UI_H