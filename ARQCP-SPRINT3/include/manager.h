#ifndef MANAGER_H
#define MANAGER_H

#include <time.h>

#define MAX_USERNAME 50
#define MAX_PASSWORD 100
#define MAX_NAME 100
#define MAX_COMMAND 20

// Estrutura de Utilizador
typedef struct {
    char name[MAX_NAME];
    char username[MAX_USERNAME];
    char encrypted_password[MAX_PASSWORD];
    int caesar_key;
} User;

// Estrutura de Via (Track)
typedef struct {
    int id;
    int state; // 0-Free(Green), 1-Busy(Red), 2-Assigned(Yellow), 3-Inoperative(RedBlinking)
    int train_id; // -1 se não houver comboio
} Track;

// Estrutura de Comboio
typedef struct {
    int id;
} Train;

// Estrutura de Sensor Data
typedef struct {
    int *buffer;
    int length;
    int nelem;
    int tail;
    int head;
    int window_length; // para Moving Median
} SensorData;

// Estrutura de Sensores
typedef struct {
    SensorData temperature;
    SensorData humidity;
    int current_temp;
    int current_hum;
    char temp_unit[20];
    char hum_unit[20];
} Sensors;

// Estrutura de Log
typedef struct {
    int id;
    char username[MAX_USERNAME];
    char action[200];
    time_t timestamp;
} LogEntry;

// Estrutura Principal do Manager
typedef struct {
    User *users;
    int num_users;

    Track *tracks;
    int num_tracks;

    Train *trains;
    int num_trains;

    Sensors sensors;

    LogEntry *logs;
    int num_logs;
    int log_capacity;

    User *current_user;
} StationManager;

// Funções de inicialização
int init_manager(StationManager *manager);
void free_manager(StationManager *manager);

// USAC11 - Setup inicial
int load_config_file(StationManager *manager, const char *filename);

// USAC12 - Criar ficheiro de log
int create_user_log_file(StationManager *manager, const char *username, const char *output_file);

// USAC13 - Obter dados dos sensores
int get_sensor_data(StationManager *manager);

// USAC14 - Controlar luzes
int control_track_light(StationManager *manager, int track_id, int state);

// USAC15 - Enviar dados para o board
int send_to_board(StationManager *manager);

// USAC16 - Gerir vias
int assign_track_to_train(StationManager *manager, int track_id, int train_id);
int set_track_nonoperational(StationManager *manager, int track_id);
int set_track_free(StationManager *manager, int track_id);
int departure_order(StationManager *manager, int track_id);

// Função de logging
int log_action(StationManager *manager, const char *action);

// Login
User* login(StationManager *manager, const char *username, const char *password);

#endif // MANAGER_H