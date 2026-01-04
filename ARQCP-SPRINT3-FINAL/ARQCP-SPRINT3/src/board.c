#include "board.h"
#include <stdio.h>
#include <string.h>

// Códigos de cores ANSI
#define RESET   "\033[0m"
#define RED     "\033[31m"
#define GREEN   "\033[32m"
#define YELLOW  "\033[33m"
#define BLUE    "\033[34m"
#define MAGENTA "\033[35m"
#define CYAN    "\033[36m"
#define BOLD    "\033[1m"

// Símbolos Unicode
#define TEMP_SYMBOL "🌡️"
#define HUM_SYMBOL "💧"
#define TRAIN_SYMBOL "🚂"
#define TRACK_SYMBOL "🛤️"

void clear_screen() {
    printf("\033[2J\033[H");
}

void draw_box(int width, const char *title) {
    printf("╔");
    for (int i = 0; i < width - 2; i++) printf("═");
    printf("╗\n");

    if (title && strlen(title) > 0) {
        int padding = (width - strlen(title) - 2) / 2;
        printf("║");
        for (int i = 0; i < padding; i++) printf(" ");
        printf(BOLD "%s" RESET, title);
        for (int i = 0; i < width - padding - strlen(title) - 2; i++) printf(" ");
        printf("║\n");

        printf("╠");
        for (int i = 0; i < width - 2; i++) printf("═");
        printf("╣\n");
    }
}

void draw_box_line(int width, const char *content) {
    printf("║ %-*s ║\n", width - 4, content);
}

void draw_box_bottom(int width) {
    printf("╚");
    for (int i = 0; i < width - 2; i++) printf("═");
    printf("╝\n");
}

void display_header(const char *station_name) {
    clear_screen();

    int width = 80;

    printf(CYAN BOLD);
    draw_box(width, NULL);

    char header[100];
    sprintf(header, "    RAILWAY STATION MANAGEMENT SYSTEM    ");
    draw_box_line(width, header);

    sprintf(header, "Station: %s", station_name);
    draw_box_line(width, header);

    draw_box_bottom(width);
    printf(RESET "\n");
}

void display_sensor_data(int temp, const char *temp_unit, int hum, const char *hum_unit) {
    int width = 80;

    printf(BLUE BOLD);
    draw_box(width, "ENVIRONMENTAL CONDITIONS");
    printf(RESET);

    char line[100];

    sprintf(line, "  %s Temperature: %s%d %s%s",
            TEMP_SYMBOL, CYAN, temp, temp_unit, RESET);
    draw_box_line(width, line);

    sprintf(line, "  %s Humidity:    %s%d %s%s",
            HUM_SYMBOL, CYAN, hum, hum_unit, RESET);
    draw_box_line(width, line);

    draw_box_bottom(width);
    printf("\n");
}

void display_track_status(Track *tracks, int num_tracks) {
    int width = 80;

    printf(MAGENTA BOLD);
    draw_box(width, "TRACK STATUS");
    printf(RESET);

    char line[100];

    for (int i = 0; i < num_tracks; i++) {
        const char *color;
        const char *status;
        const char *symbol;

        switch (tracks[i].state) {
            case 0: // Free (Green)
                color = GREEN;
                status = "FREE";
                symbol = "●";
                break;
            case 1: // Busy (Red)
                color = RED;
                status = "BUSY";
                symbol = "●";
                break;
            case 2: // Assigned (Yellow)
                color = YELLOW;
                status = "ASSIGNED";
                symbol = "●";
                break;
            case 3: // Inoperative (Red Blinking)
                color = RED;
                status = "INOPERATIVE";
                symbol = "✖";
                break;
            default:
                color = RESET;
                status = "UNKNOWN";
                symbol = "?";
        }

        if (tracks[i].train_id != -1) {
            sprintf(line, "  %s Track %02d: %s%s %-12s%s - %s Train %d",
                    TRACK_SYMBOL, tracks[i].id, color, symbol, status, RESET,
                    TRAIN_SYMBOL, tracks[i].train_id);
        } else {
            sprintf(line, "  %s Track %02d: %s%s %-12s%s",
                    TRACK_SYMBOL, tracks[i].id, color, symbol, status, RESET);
        }

        draw_box_line(width, line);
    }

    draw_box_bottom(width);
    printf("\n");
}

void display_board(const char *station_name, int temp, const char *temp_unit,
                   int hum, const char *hum_unit, Track *tracks, int num_tracks) {
    display_header(station_name);
    display_sensor_data(temp, temp_unit, hum, hum_unit);
    display_track_status(tracks, num_tracks);

    printf(CYAN "═══════════════════════════════════════════════════════════════════════════════\n" RESET);
}

void display_alert(const char *message, AlertType type) {
    const char *color;
    const char *symbol;
    const char *type_str;

    switch (type) {
        case ALERT_INFO:
            color = CYAN;
            symbol = "ℹ";
            type_str = "INFO";
            break;
        case ALERT_WARNING:
            color = YELLOW;
            symbol = "⚠";
            type_str = "WARNING";
            break;
        case ALERT_ERROR:
            color = RED;
            symbol = "✖";
            type_str = "ERROR";
            break;
        case ALERT_SUCCESS:
            color = GREEN;
            symbol = "✓";
            type_str = "SUCCESS";
            break;
        default:
            color = RESET;
            symbol = "•";
            type_str = "ALERT";
    }

    printf("\n");
    printf("%s╔═══════════════════════════════════════════════════════════════════════════════╗\n", color);
    printf("║ %s %s%-10s%s                                                                    ║\n",
           symbol, BOLD, type_str, RESET);
    printf("║                                                                               ║\n");
    printf("║  %-75s  ║\n", message);
    printf("║                                                                               ║\n");
    printf("╚═══════════════════════════════════════════════════════════════════════════════╝%s\n", RESET);
    printf("\n");
}