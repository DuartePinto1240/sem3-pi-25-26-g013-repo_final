#include "ui.h"
#include "board.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#define RESET   "\033[0m"
#define CYAN    "\033[36m"
#define YELLOW  "\033[33m"
#define BOLD    "\033[1m"

void clear_input_buffer() {
    int c;
    while ((c = getchar()) != '\n' && c != EOF);
}

int read_int(const char *prompt, int min, int max) {
    int value;
    char input[100];
    int valid = 0;

    while (!valid) {
        printf(CYAN "%s" RESET " (%d-%d): ", prompt, min, max);

        if (fgets(input, sizeof(input), stdin) == NULL) {
            continue;
        }

        // Remover newline
        input[strcspn(input, "\n")] = 0;

        // Verificar se é um número
        int is_number = 1;
        for (int i = 0; input[i] != '\0'; i++) {
            if (!isdigit(input[i]) && input[i] != '-') {
                is_number = 0;
                break;
            }
        }

        if (!is_number) {
            display_alert("Input inválido! Por favor introduza um número.", ALERT_ERROR);
            continue;
        }

        value = atoi(input);

        if (value >= min && value <= max) {
            valid = 1;
        } else {
            display_alert("Valor fora do intervalo permitido!", ALERT_ERROR);
        }
    }

    return value;
}

void read_string(const char *prompt, char *buffer, int max_len) {
    printf(CYAN "%s" RESET ": ", prompt);

    if (fgets(buffer, max_len, stdin) != NULL) {
        buffer[strcspn(buffer, "\n")] = 0;
    }
}

int show_login_menu(StationManager *manager) {
    clear_screen();

    printf(BOLD CYAN "\n");
    printf("╔═══════════════════════════════════════════════════════════════════════════════╗\n");
    printf("║                     RAILWAY STATION MANAGEMENT SYSTEM                         ║\n");
    printf("║                                   LOGIN                                       ║\n");
    printf("╚═══════════════════════════════════════════════════════════════════════════════╝\n");
    printf(RESET "\n");

    char username[MAX_USERNAME];
    char password[MAX_PASSWORD];

    read_string("Username", username, sizeof(username));
    read_string("Password", password, sizeof(password));

    User *user = login(manager, username, password);

    if (user) {
        display_alert("Login efetuado com sucesso!", ALERT_SUCCESS);
        printf("Bem-vindo, %s!\n", user->name);

        char action[200];
        sprintf(action, "Login efetuado por %s", username);
        log_action(manager, action);

        getchar(); // Pausa
        return 1;
    } else {
        display_alert("Credenciais inválidas!", ALERT_ERROR);
        printf("Pressione ENTER para tentar novamente...");
        getchar();
        return 0;
    }
}

int show_main_menu() {
    printf(BOLD YELLOW "\n");
    printf("╔═══════════════════════════════════════════════════════════════════════════════╗\n");
    printf("║                              MENU PRINCIPAL                                   ║\n");
    printf("╠═══════════════════════════════════════════════════════════════════════════════╣\n");
    printf("║  1. Atribuir via a comboio                                                    ║\n");
    printf("║  2. Marcar via como não operacional                                           ║\n");
    printf("║  3. Marcar via como livre                                                     ║\n");
    printf("║  4. Dar ordem de partida                                                      ║\n");
    printf("║  5. Ver dados dos sensores                                                    ║\n");
    printf("║  6. Atualizar display                                                         ║\n");
    printf("║  7. Exportar log de utilizador                                                ║\n");
    printf("║  0. Sair                                                                      ║\n");
    printf("╚═══════════════════════════════════════════════════════════════════════════════╝\n");
    printf(RESET "\n");

    return read_int("Escolha uma opção", 0, 7);
}

void handle_assign_track(StationManager *manager) {
    printf(BOLD "\n=== ATRIBUIR VIA A COMBOIO ===" RESET "\n\n");

    int track_id = read_int("ID da via", 1, 99);
    int train_id = read_int("ID do comboio", 1, 9999);

    if (assign_track_to_train(manager, track_id, train_id)) {
        display_alert("Via atribuída com sucesso!", ALERT_SUCCESS);
    } else {
        display_alert("Falha ao atribuir via!", ALERT_ERROR);
    }

    printf("\nPressione ENTER para continuar...");
    getchar();
}

void handle_set_track_nonoperational(StationManager *manager) {
    printf(BOLD "\n=== MARCAR VIA COMO NÃO OPERACIONAL ===" RESET "\n\n");

    int track_id = read_int("ID da via", 1, 99);

    if (set_track_nonoperational(manager, track_id)) {
        display_alert("Via marcada como não operacional!", ALERT_SUCCESS);
    } else {
        display_alert("Falha ao marcar via!", ALERT_ERROR);
    }

    printf("\nPressione ENTER para continuar...");
    getchar();
}

void handle_set_track_free(StationManager *manager) {
    printf(BOLD "\n=== MARCAR VIA COMO LIVRE ===" RESET "\n\n");

    int track_id = read_int("ID da via", 1, 99);

    if (set_track_free(manager, track_id)) {
        display_alert("Via marcada como livre!", ALERT_SUCCESS);
    } else {
        display_alert("Falha ao marcar via!", ALERT_ERROR);
    }

    printf("\nPressione ENTER para continuar...");
    getchar();
}

void handle_departure_order(StationManager *manager) {
    printf(BOLD "\n=== DAR ORDEM DE PARTIDA ===" RESET "\n\n");

    int track_id = read_int("ID da via", 1, 99);

    if (departure_order(manager, track_id)) {
        display_alert("Ordem de partida dada com sucesso!", ALERT_SUCCESS);
    } else {
        display_alert("Falha ao dar ordem de partida!", ALERT_ERROR);
    }

    printf("\nPressione ENTER para continuar...");
    getchar();
}

void handle_view_sensors(StationManager *manager) {
    printf(BOLD "\n=== DADOS DOS SENSORES ===" RESET "\n\n");

    printf("Temperatura: %d %s\n", manager->sensors.current_temp,
           manager->sensors.temp_unit);
    printf("Humidade: %d %s\n", manager->sensors.current_hum,
           manager->sensors.hum_unit);

    printf("\nPressione ENTER para continuar...");
    getchar();
}

void handle_export_log(StationManager *manager) {
    printf(BOLD "\n=== EXPORTAR LOG DE UTILIZADOR ===" RESET "\n\n");

    char username[MAX_USERNAME];
    char filename[100];

    read_string("Username", username, sizeof(username));
    read_string("Nome do ficheiro", filename, sizeof(filename));

    if (create_user_log_file(manager, username, filename)) {
        display_alert("Log exportado com sucesso!", ALERT_SUCCESS);
    } else {
        display_alert("Falha ao exportar log!", ALERT_ERROR);
    }

    printf("\nPressione ENTER para continuar...");
    getchar();
}

void run_ui(StationManager *manager) {
    if (!manager) return;

    // Login
    while (!show_login_menu(manager));

    int running = 1;

    while (running) {
        // Atualizar sensores
        get_sensor_data(manager);

        // Mostrar board
        display_board("Porto Station",
                     manager->sensors.current_temp,
                     manager->sensors.temp_unit,
                     manager->sensors.current_hum,
                     manager->sensors.hum_unit,
                     manager->tracks,
                     manager->num_tracks);

        // Menu
        int option = show_main_menu();

        switch (option) {
            case 1:
                handle_assign_track(manager);
                break;
            case 2:
                handle_set_track_nonoperational(manager);
                break;
            case 3:
                handle_set_track_free(manager);
                break;
            case 4:
                handle_departure_order(manager);
                break;
            case 5:
                handle_view_sensors(manager);
                break;
            case 6:
                // Refresh automático no próximo loop
                break;
            case 7:
                handle_export_log(manager);
                break;
            case 0:
                running = 0;
                display_alert("A encerrar sistema...", ALERT_INFO);
                log_action(manager, "Logout");
                break;
            default:
                display_alert("Opção inválida!", ALERT_ERROR);
        }
    }
}