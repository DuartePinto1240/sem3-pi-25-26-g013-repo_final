#include <stdio.h>
#include <string.h>

// Declarar a função Assembly
extern int format_command(char* op, int n, char *cmd);

// Função auxiliar para facilitar os testes
void test_format(char* op, int n) {
    char cmd[20] = {0}; // Inicializar buffer
    int res = format_command(op, n, cmd);
    printf("Input: (\"%s\", %d) -> Resultado: %d | Output: \"%s\"\n", op, n, res, cmd);
}

int main() {
    printf("--- Testes USAC04 ---\n");

    // Teste 1: " R B ", 5  -> 1: "RB,05"
    test_format(" R B ", 5);

    // Teste 2: " Ye ", 25 -> 1: "YE,25"
    test_format(" Ye ", 25);

    // Teste 3: " Ye ", 125 -> 0: ""
    test_format(" Ye ", 125); // n > 99, deve falhar

    // Teste 4: " aaa ", 25 -> 0: ""
    test_format(" aaa ", 25); // op inválido

    // Teste 5: " gTh ", 25 -> 1: "GTH"
    test_format(" gTh ", 25); // 'n' é ignorado

    // Teste extra: "RBX" (comando inválido por trim)
    test_format("RBX", 10);

    printf("---------------------\n");
    return 0;
}
