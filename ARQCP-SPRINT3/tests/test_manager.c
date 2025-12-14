#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include "manager.h"

// Cores para output
#define GREEN "\033[32m"
#define RED "\033[31m"
#define RESET "\033[0m"

int tests_passed = 0;
int tests_failed = 0;

void test_init_manager() {
    printf("\n=== Test: init_manager ===\n");

    StationManager manager;
    int result = init_manager(&manager);

    assert(result == 1);
    assert(manager.users == NULL);
    assert(manager.num_users == 0);
    assert(manager.tracks == NULL);
    assert(manager.num_tracks == 0);
    assert(manager.logs != NULL);
    assert(manager.log_capacity == 100);

    free_manager(&manager);

    printf(GREEN "✓ PASSED" RESET "\n");
    tests_passed++;
}

void test_load_config_file() {
    printf("\n=== Test: load_config_file ===\n");

    StationManager manager;
    init_manager(&manager);

    // Criar ficheiro de config temporário
    FILE *f = fopen("test_config.txt", "w");
    fprintf(f, "[USERS]\n");
    fprintf(f, "Test User,testuser,PASSWORD,3\n");
    fprintf(f, "\n[SENSORS]\n");
    fprintf(f, "TEMP_BUFFER_LENGTH=5\n");
    fprintf(f, "TEMP_WINDOW_LENGTH=3\n");
    fprintf(f, "HUM_BUFFER_LENGTH=5\n");
    fprintf(f, "HUM_WINDOW_LENGTH=3\n");
    fprintf(f, "\n[TRACKS]\n");
    fprintf(f, "1\n");
    fprintf(f, "2\n");
    fprintf(f, "\n[TRAINS]\n");
    fprintf(f, "101\n");
    fclose(f);

    int result = load_config_file(&manager, "test_config.txt");

    assert(result == 1);
    assert(manager.num_users == 1);
    assert(manager.num_tracks == 2);
    assert(manager.num_trains == 1);
    assert(strcmp(manager.users[0].username, "testuser") == 0);
    assert(manager.tracks[0].id == 1);
    assert(manager.tracks[1].id == 2);
    assert(manager.trains[0].id == 101);

    free_manager(&manager);
    remove("test_config.txt");

    printf(GREEN "✓ PASSED" RESET "\n");
    tests_passed++;
}

void test_login_success() {
    printf("\n=== Test: login (success) ===\n");

    StationManager manager;
    init_manager(&manager);

    // Criar utilizador manualmente
    manager.users = (User*)malloc(sizeof(User));
    manager.num_users = 1;

    strcpy(manager.users[0].name, "Test User");
    strcpy(manager.users[0].username, "testuser");
    manager.users[0].caesar_key = 3;

    // Encriptar password "TEST" com chave 3
    extern int encrypt_data(char* in, int key, char *out);
    encrypt_data("TEST", 3, manager.users[0].encrypted_password);

    User *user = login(&manager, "testuser", "TEST");

    assert(user != NULL);
    assert(strcmp(user->username, "testuser") == 0);
    assert(manager.current_user == user);

    free_manager(&manager);

    printf(GREEN "✓ PASSED" RESET "\n");
    tests_passed++;
}

void test_login_failure() {
    printf("\n=== Test: login (failure) ===\n");

    StationManager manager;
    init_manager(&manager);

    manager.users = (User*)malloc(sizeof(User));
    manager.num_users = 1;

    strcpy(manager.users[0].username, "testuser");
    manager.users[0].caesar_key = 3;

    extern int encrypt_data(char* in, int key, char *out);
    encrypt_data("TEST", 3, manager.users[0].encrypted_password);

    User *user = login(&manager, "testuser", "WRONG");

    assert(user == NULL);
    assert(manager.current_user == NULL);

    free_manager(&manager);

    printf(GREEN "✓ PASSED" RESET "\n");
    tests_passed++;
}

void test_log_action() {
    printf("\n=== Test: log_action ===\n");

    StationManager manager;
    init_manager(&manager);

    // Criar utilizador e fazer login
    manager.users = (User*)malloc(sizeof(User));
    manager.num_users = 1;
    strcpy(manager.users[0].username, "testuser");
    manager.current_user = &manager.users[0];

    int result = log_action(&manager, "Test action");

    assert(result == 1);
    assert(manager.num_logs == 1);
    assert(strcmp(manager.logs[0].username, "testuser") == 0);
    assert(strcmp(manager.logs[0].action, "Test action") == 0);

    free_manager(&manager);

    printf(GREEN "✓ PASSED" RESET "\n");
    tests_passed++;
}

void test_assign_track_success() {
    printf("\n=== Test: assign_track_to_train (success) ===\n");

    StationManager manager;
    init_manager(&manager);

    // Setup
    manager.tracks = (Track*)malloc(sizeof(Track));
    manager.num_tracks = 1;
    manager.tracks[0].id = 1;
    manager.tracks[0].state = 0; // Free
    manager.tracks[0].train_id = -1;

    manager.users = (User*)malloc(sizeof(User));
    manager.num_users = 1;
    strcpy(manager.users[0].username, "testuser");
    manager.current_user = &manager.users[0];

    int result = assign_track_to_train(&manager, 1, 101);

    assert(result == 1);
    assert(manager.tracks[0].state == 2); // Assigned
    assert(manager.tracks[0].train_id == 101);

    free_manager(&manager);

    printf(GREEN "✓ PASSED" RESET "\n");
    tests_passed++;
}

void test_assign_track_emergency_stop() {
    printf("\n=== Test: assign_track_to_train (emergency stop) ===\n");

    StationManager manager;
    init_manager(&manager);

    // Setup - via já ocupada
    manager.tracks = (Track*)malloc(sizeof(Track));
    manager.num_tracks = 1;
    manager.tracks[0].id = 1;
    manager.tracks[0].state = 1; // Busy
    manager.tracks[0].train_id = 100;

    manager.users = (User*)malloc(sizeof(User));
    manager.num_users = 1;
    strcpy(manager.users[0].username, "testuser");
    manager.current_user = &manager.users[0];

    int result = assign_track_to_train(&manager, 1, 101);

    assert(result == 0); // Deve falhar
    assert(manager.tracks[0].state == 1); // Ainda Busy
    assert(manager.tracks[0].train_id == 100); // Comboio original

    free_manager(&manager);

    printf(GREEN "✓ PASSED" RESET "\n");
    tests_passed++;
}

void test_set_track_free() {
    printf("\n=== Test: set_track_free ===\n");

    StationManager manager;
    init_manager(&manager);

    manager.tracks = (Track*)malloc(sizeof(Track));
    manager.num_tracks = 1;
    manager.tracks[0].id = 1;
    manager.tracks[0].state = 1; // Busy
    manager.tracks[0].train_id = 101;

    manager.users = (User*)malloc(sizeof(User));
    manager.num_users = 1;
    strcpy(manager.users[0].username, "testuser");
    manager.current_user = &manager.users[0];

    int result = set_track_free(&manager, 1);

    assert(result == 1);
    assert(manager.tracks[0].state == 0); // Free
    assert(manager.tracks[0].train_id == -1); // No train

    free_manager(&manager);

    printf(GREEN "✓ PASSED" RESET "\n");
    tests_passed++;
}

int main() {
    printf("\n");
    printf("╔═══════════════════════════════════════════════════════════════════════════════╗\n");
    printf("║                    RAILWAY STATION MANAGER - UNIT TESTS                      ║\n");
    printf("╚═══════════════════════════════════════════════════════════════════════════════╝\n");

    test_init_manager();
    test_load_config_file();
    test_login_success();
    test_login_failure();
    test_log_action();
    test_assign_track_success();
    test_assign_track_emergency_stop();
    test_set_track_free();

    printf("\n");
    printf("╔═══════════════════════════════════════════════════════════════════════════════╗\n");
    printf("║                              TEST SUMMARY                                     ║\n");
    printf("╠═══════════════════════════════════════════════════════════════════════════════╣\n");
    printf("║  " GREEN "✓ Passed: %d" RESET "                                                               ║\n", tests_passed);
    printf("║  " RED "✗ Failed: %d" RESET "                                                               ║\n", tests_failed);
    printf("╚═══════════════════════════════════════════════════════════════════════════════╝\n");
    printf("\n");

    return tests_failed > 0 ? 1 : 0;
}