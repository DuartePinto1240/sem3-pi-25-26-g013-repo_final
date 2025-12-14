#include "manager.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Declaração das funções assembly (do Sprint 2)
extern int encrypt_data(char* in, int key, char *out);
extern int decrypt_data(char* in, int key, char *out);
extern int enqueue_value(int* buffer, int length, int *nelem, int* tail, int* head, int value);

int init_manager(StationManager *manager) {
    if (!manager) return 0;

    manager->users = NULL;
    manager->num_users = 0;

    manager->tracks = NULL;
    manager->num_tracks = 0;

    manager->trains = NULL;
    manager->num_trains = 0;

    manager->logs = NULL;
    manager->num_logs = 0;
    manager->log_capacity = 100;

    manager->current_user = NULL;

    // Alocar espaço inicial para logs
    manager->logs = (LogEntry*)malloc(manager->log_capacity * sizeof(LogEntry));
    if (!manager->logs) return 0;

    // Inicializar sensores
    manager->sensors.temperature.buffer = NULL;
    manager->sensors.humidity.buffer = NULL;
    manager->sensors.current_temp = 0;
    manager->sensors.current_hum = 0;
    strcpy(manager->sensors.temp_unit, "");
    strcpy(manager->sensors.hum_unit, "");

    return 1;
}

// USAC15 - Enviar dados para o Board
int send_to_board(StationManager *manager) {
    if (!manager) return 0;

    // Esta função será chamada pelo main loop
    // A função display_board em board.c faz o display real
    return 1;
}

void free_manager(StationManager *manager) {
    if (!manager) return;

    if (manager->users) free(manager->users);
    if (manager->tracks) free(manager->tracks);
    if (manager->trains) free(manager->trains);
    if (manager->logs) free(manager->logs);

    if (manager->sensors.temperature.buffer)
        free(manager->sensors.temperature.buffer);
    if (manager->sensors.humidity.buffer)
        free(manager->sensors.humidity.buffer);
}

// USAC11 - Carregar ficheiro de configuração
int load_config_file(StationManager *manager, const char *filename) {
    FILE *file = fopen(filename, "r");
    if (!file) {
        printf("Erro ao abrir ficheiro: %s\n", filename);
        return 0;
    }

    char line[256];
    char section[50] = "";

    while (fgets(line, sizeof(line), file)) {
        // Remover newline
        line[strcspn(line, "\n")] = 0;

        // Ignorar comentários e linhas vazias
        if (line[0] == '#' || strlen(line) == 0) continue;

        // Detetar secções
        if (line[0] == '[') {
            sscanf(line, "[%[^]]]", section);
            continue;
        }

        // Processar USERS
        if (strcmp(section, "USERS") == 0) {
            char name[MAX_NAME], username[MAX_USERNAME], password[MAX_PASSWORD];
            int key;

            char *token = strtok(line, ",");
            if (token) strcpy(name, token);

            token = strtok(NULL, ",");
            if (token) strcpy(username, token);

            token = strtok(NULL, ",");
            if (token) strcpy(password, token);

            token = strtok(NULL, ",");
            if (token) key = atoi(token);

            // Alocar/realocar users
            manager->users = (User*)realloc(manager->users,
                (manager->num_users + 1) * sizeof(User));

            User *user = &manager->users[manager->num_users];
            strcpy(user->name, name);
            strcpy(user->username, username);
            user->caesar_key = key;

            // Encriptar password usando função assembly
            encrypt_data(password, key, user->encrypted_password);

            manager->num_users++;
        }

        // Processar SENSORS
        else if (strcmp(section, "SENSORS") == 0) {
            if (strstr(line, "TEMP_BUFFER_LENGTH=")) {
                int len = atoi(strchr(line, '=') + 1);
                manager->sensors.temperature.length = len;
                manager->sensors.temperature.buffer =
                    (int*)calloc(len, sizeof(int));
                manager->sensors.temperature.nelem = 0;
                manager->sensors.temperature.tail = 0;
                manager->sensors.temperature.head = 0;
            }
            else if (strstr(line, "TEMP_WINDOW_LENGTH=")) {
                manager->sensors.temperature.window_length =
                    atoi(strchr(line, '=') + 1);
            }
            else if (strstr(line, "HUM_BUFFER_LENGTH=")) {
                int len = atoi(strchr(line, '=') + 1);
                manager->sensors.humidity.length = len;
                manager->sensors.humidity.buffer =
                    (int*)calloc(len, sizeof(int));
                manager->sensors.humidity.nelem = 0;
                manager->sensors.humidity.tail = 0;
                manager->sensors.humidity.head = 0;
            }
            else if (strstr(line, "HUM_WINDOW_LENGTH=")) {
                manager->sensors.humidity.window_length =
                    atoi(strchr(line, '=') + 1);
            }
        }

        // Processar TRACKS
        else if (strcmp(section, "TRACKS") == 0) {
            int track_id = atoi(line);

            manager->tracks = (Track*)realloc(manager->tracks,
                (manager->num_tracks + 1) * sizeof(Track));

            Track *track = &manager->tracks[manager->num_tracks];
            track->id = track_id;
            track->state = 0; // Free (Green)
            track->train_id = -1; // No train

            manager->num_tracks++;
        }

        // Processar TRAINS
        else if (strcmp(section, "TRAINS") == 0) {
            int train_id = atoi(line);

            manager->trains = (Train*)realloc(manager->trains,
                (manager->num_trains + 1) * sizeof(Train));

            Train *train = &manager->trains[manager->num_trains];
            train->id = train_id;

            manager->num_trains++;
        }
    }

    fclose(file);

    printf("Configuração carregada com sucesso!\n");
    printf("- %d utilizadores\n", manager->num_users);
    printf("- %d vias\n", manager->num_tracks);
    printf("- %d comboios\n", manager->num_trains);

    return 1;
}

// Login
User* login(StationManager *manager, const char *username, const char *password) {
    if (!manager || !username || !password) return NULL;

    for (int i = 0; i < manager->num_users; i++) {
        if (strcmp(manager->users[i].username, username) == 0) {
            // Desencriptar password armazenada
            char decrypted[MAX_PASSWORD];
            decrypt_data(manager->users[i].encrypted_password,
                        manager->users[i].caesar_key, decrypted);

            if (strcmp(decrypted, password) == 0) {
                manager->current_user = &manager->users[i];
                return &manager->users[i];
            }
        }
    }

    return NULL;
}

// Logging
int log_action(StationManager *manager, const char *action) {
    if (!manager || !manager->current_user || !action) return 0;

    // Realocar se necessário
    if (manager->num_logs >= manager->log_capacity) {
        manager->log_capacity *= 2;
        manager->logs = (LogEntry*)realloc(manager->logs,
            manager->log_capacity * sizeof(LogEntry));
        if (!manager->logs) return 0;
    }

    LogEntry *log = &manager->logs[manager->num_logs];
    log->id = manager->num_logs + 1;
    strcpy(log->username, manager->current_user->username);
    strncpy(log->action, action, 199);
    log->action[199] = '\0';
    log->timestamp = time(NULL);

    manager->num_logs++;
    return 1;
}

// USAC12 - Criar ficheiro de log para utilizador específico
int create_user_log_file(StationManager *manager, const char *username,
                         const char *output_file) {
    if (!manager || !username || !output_file) return 0;

    FILE *file = fopen(output_file, "w");
    if (!file) return 0;

    fprintf(file, "Log de ações do utilizador: %s\n", username);
    fprintf(file, "==========================================\n\n");

    int count = 0;
    for (int i = 0; i < manager->num_logs; i++) {
        if (strcmp(manager->logs[i].username, username) == 0) {
            char time_str[100];
            strftime(time_str, sizeof(time_str), "%Y-%m-%d %H:%M:%S",
                    localtime(&manager->logs[i].timestamp));

            fprintf(file, "[%d] %s - %s\n",
                   manager->logs[i].id, time_str, manager->logs[i].action);
            count++;
        }
    }

    fprintf(file, "\n==========================================\n");
    fprintf(file, "Total de ações: %d\n", count);

    fclose(file);
    return 1;
}

// Helper: Encontrar via por ID
Track* find_track(StationManager *manager, int track_id) {
    for (int i = 0; i < manager->num_tracks; i++) {
        if (manager->tracks[i].id == track_id) {
            return &manager->tracks[i];
        }
    }
    return NULL;
}

// USAC16 - Atribuir via a comboio
int assign_track_to_train(StationManager *manager, int track_id, int train_id) {
    if (!manager) return 0;

    Track *track = find_track(manager, track_id);
    if (!track) {
        printf("Via %d não encontrada!\n", track_id);
        return 0;
    }

    // Verificar se via está livre
    if (track->state != 0) {
        printf("EMERGENCY STOP! Via %d não está disponível!\n", track_id);

        char action[200];
        sprintf(action, "EMERGENCY STOP - Via %d indisponível para comboio %d",
                track_id, train_id);
        log_action(manager, action);

        return 0;
    }

    // Atribuir via
    track->state = 2; // Assigned (Yellow)
    track->train_id = train_id;

    // Controlar luz
    control_track_light(manager, track_id, 2);

    // Atualizar board
    send_to_board(manager);

    // Log
    char action[200];
    sprintf(action, "Via %d atribuída ao comboio %d", track_id, train_id);
    log_action(manager, action);

    printf("Via %d atribuída ao comboio %d com sucesso!\n", track_id, train_id);
    return 1;
}

// USAC16 - Marcar via como não operacional
int set_track_nonoperational(StationManager *manager, int track_id) {
    if (!manager) return 0;

    Track *track = find_track(manager, track_id);
    if (!track) {
        printf("Via %d não encontrada!\n", track_id);
        return 0;
    }

    track->state = 3; // Inoperative (Red Blinking)
    track->train_id = -1;

    // Controlar luz
    control_track_light(manager, track_id, 3);

    // Atualizar board
    send_to_board(manager);

    // Log
    char action[200];
    sprintf(action, "Via %d marcada como não operacional", track_id);
    log_action(manager, action);

    printf("Via %d marcada como não operacional!\n", track_id);
    return 1;
}

// USAC16 - Marcar via como livre
int set_track_free(StationManager *manager, int track_id) {
    if (!manager) return 0;

    Track *track = find_track(manager, track_id);
    if (!track) {
        printf("Via %d não encontrada!\n", track_id);
        return 0;
    }

    track->state = 0; // Free (Green)
    track->train_id = -1;

    // Controlar luz
    control_track_light(manager, track_id, 0);

    // Atualizar board
    send_to_board(manager);

    // Log
    char action[200];
    sprintf(action, "Via %d marcada como livre", track_id);
    log_action(manager, action);

    printf("Via %d está agora livre!\n", track_id);
    return 1;
}

// USAC16 - Ordem de partida
int departure_order(StationManager *manager, int track_id) {
    if (!manager) return 0;

    Track *track = find_track(manager, track_id);
    if (!track) {
        printf("Via %d não encontrada!\n", track_id);
        return 0;
    }

    if (track->state != 1 || track->train_id == -1) {
        printf("Não há comboio parado na via %d!\n", track_id);
        return 0;
    }

    int train_id = track->train_id;

    // Libertar via
    track->state = 0; // Free (Green)
    track->train_id = -1;

    // Controlar luz
    control_track_light(manager, track_id, 0);

    // Atualizar board
    send_to_board(manager);

    // Log
    char action[200];
    sprintf(action, "Ordem de partida dada ao comboio %d da via %d",
            train_id, track_id);
    log_action(manager, action);

    printf("Comboio %d pode partir da via %d!\n", train_id, track_id);
    return 1;
}

// Declarações de funções assembly adicionais
extern int extract_data(char* str, char* token, char* unit, int* value);
extern int format_command(char* op, int n, char *cmd);
extern int move_n_to_array(int* buffer, int length, int *nelem, int *tail,
                          int *head, int n, int* array);
extern int median(int* vec, int length, int *me);

// Função auxiliar para comunicação serial (simulada)
// Em produção, isto seria comunicação serial real com Arduino
int send_command_to_device(const char *device, const char *cmd, char *response, int max_len) {
    // Simulação de comunicação
    // Na implementação real, usarias funções como write/read em /dev/ttyUSB0

    if (strcmp(device, "SENSORS") == 0 && strcmp(cmd, "GTH") == 0) {
        // Simular resposta dos sensores
        snprintf(response, max_len,
                "TEMP&unit:celsius&value:20#HUM&unit:percentage&value:65");
        return 1;
    }

    if (strcmp(device, "LIGHTSIGNS") == 0) {
        // Simular envio para LEDs
        printf("[LightSigns] Comando enviado: %s\n", cmd);
        return 1;
    }

    return 0;
}

// USAC13 - Obter dados dos sensores
int get_sensor_data(StationManager *manager) {
    if (!manager) return 0;

    char cmd[] = "GTH";
    char response[256];

    // Enviar comando para sensores
    if (!send_command_to_device("SENSORS", cmd, response, sizeof(response))) {
        printf("Erro ao comunicar com sensores!\n");
        return 0;
    }

    // Extrair temperatura
    char temp_unit[20];
    int temp_value;
    if (extract_data(response, "TEMP", temp_unit, &temp_value)) {
        // Adicionar ao buffer circular
        enqueue_value(manager->sensors.temperature.buffer,
                     manager->sensors.temperature.length,
                     &manager->sensors.temperature.nelem,
                     &manager->sensors.temperature.tail,
                     &manager->sensors.temperature.head,
                     temp_value);

        // Calcular moving median
        int window_len = manager->sensors.temperature.window_length;
        if (manager->sensors.temperature.nelem >= window_len) {
            int *temp_array = (int*)malloc(window_len * sizeof(int));

            move_n_to_array(manager->sensors.temperature.buffer,
                           manager->sensors.temperature.length,
                           &manager->sensors.temperature.nelem,
                           &manager->sensors.temperature.tail,
                           &manager->sensors.temperature.head,
                           window_len,
                           temp_array);

            int med;
            if (median(temp_array, window_len, &med)) {
                manager->sensors.current_temp = med;
                strcpy(manager->sensors.temp_unit, temp_unit);
            }

            // Repor valores no buffer
            for (int i = 0; i < window_len; i++) {
                enqueue_value(manager->sensors.temperature.buffer,
                             manager->sensors.temperature.length,
                             &manager->sensors.temperature.nelem,
                             &manager->sensors.temperature.tail,
                             &manager->sensors.temperature.head,
                             temp_array[i]);
            }

            free(temp_array);
        }
    }

    // Extrair humidade
    char hum_unit[20];
    int hum_value;
    if (extract_data(response, "HUM", hum_unit, &hum_value)) {
        enqueue_value(manager->sensors.humidity.buffer,
                     manager->sensors.humidity.length,
                     &manager->sensors.humidity.nelem,
                     &manager->sensors.humidity.tail,
                     &manager->sensors.humidity.head,
                     hum_value);

        int window_len = manager->sensors.humidity.window_length;
        if (manager->sensors.humidity.nelem >= window_len) {
            int *hum_array = (int*)malloc(window_len * sizeof(int));

            move_n_to_array(manager->sensors.humidity.buffer,
                           manager->sensors.humidity.length,
                           &manager->sensors.humidity.nelem,
                           &manager->sensors.humidity.tail,
                           &manager->sensors.humidity.head,
                           window_len,
                           hum_array);

            int med;
            if (median(hum_array, window_len, &med)) {
                manager->sensors.current_hum = med;
                strcpy(manager->sensors.hum_unit, hum_unit);
            }

            for (int i = 0; i < window_len; i++) {
                enqueue_value(manager->sensors.humidity.buffer,
                             manager->sensors.humidity.length,
                             &manager->sensors.humidity.nelem,
                             &manager->sensors.humidity.tail,
                             &manager->sensors.humidity.head,
                             hum_array[i]);
            }

            free(hum_array);
        }
    }

    return 1;
}

// USAC14 - Controlar luzes das vias
int control_track_light(StationManager *manager, int track_id, int state) {
    if (!manager) return 0;

    char cmd_str[20];
    char formatted_cmd[20];

    // Determinar comando baseado no estado
    switch (state) {
        case 0: // Free (Green)
            strcpy(cmd_str, "GE");
            break;
        case 1: // Busy (Red)
            strcpy(cmd_str, "RE");
            break;
        case 2: // Assigned (Yellow)
            strcpy(cmd_str, "YE");
            break;
        case 3: // Inoperative (Red Blinking)
            strcpy(cmd_str, "RB");
            break;
        default:
            printf("Estado inválido: %d\n", state);
            return 0;
    }

    // Formatar comando usando função assembly
    if (!format_command(cmd_str, track_id, formatted_cmd)) {
        printf("Erro ao formatar comando!\n");
        return 0;
    }

    // Enviar para LightSigns
    char response[100];
    if (!send_command_to_device("LIGHTSIGNS", formatted_cmd, response, sizeof(response))) {
        printf("Erro ao enviar comando para LightSigns!\n");
        return 0;
    }

    return 1;
}