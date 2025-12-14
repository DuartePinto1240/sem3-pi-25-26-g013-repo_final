#include <stdio.h>
#include <stdlib.h>
#include "manager.h"
#include "ui.h"
#include "board.h"

int main(int argc, char *argv[]) {
    StationManager manager;

    // Inicializar manager
    if (!init_manager(&manager)) {
        fprintf(stderr, "Erro ao inicializar o sistema!\n");
        return 1;
    }

    // Carregar configuração
    const char *config_file = "data/config.txt";
    if (argc > 1) {
        config_file = argv[1];
    }

    printf("A carregar configuração de: %s\n", config_file);

    if (!load_config_file(&manager, config_file)) {
        fprintf(stderr, "Erro ao carregar configuração!\n");
        free_manager(&manager);
        return 1;
    }

    printf("\nPressione ENTER para iniciar...");
    getchar();

    // Executar UI
    run_ui(&manager);

    // Cleanup
    printf("\nA libertar recursos...\n");
    free_manager(&manager);

    printf("Sistema encerrado com sucesso!\n");

    return 0;
}