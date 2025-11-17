#include <stdio.h>

// Declarar a função Assembly para que o C a conheça
extern int extract_data(char* str, char* token, char* unit, int* value);

int main() {
    // Caso de Teste 1: Sucesso (Exemplo da US)
    // [cite: 225-230]
    char str[] = "TEMP&unit:celsius&value:20#HUM&unit:percentage&value:80";
    char token1[] = "TEMP";
    char unit1[20];
    int value1;

    int res1 = extract_data(str, token1, unit1, &value1);
    printf("--- Teste 1 (TEMP) ---\n");
    printf("Esperado: 1:celsius,20\n");
    printf("Recebido: %d:%s,%d\n", res1, unit1, value1);
    printf("--------------------------------\n");

    // Caso de Teste 2: Sucesso (Segundo token)
    char token2[] = "HUM";
    char unit2[20];
    int value2;

    int res2 = extract_data(str, token2, unit2, &value2);
    printf("--- Teste 2 (HUM) ---\n");
    printf("Esperado: 1:percentage,80\n");
    printf("Recebido: %d:%s,%d\n", res2, unit2, value2);
    printf("--------------------------------\n");


    // Caso de Teste 3: Falha (Token não existe)
    // [cite: 232-237]
    char token3[] = "AAA";
    char unit3[20];
    int value3;

    int res3 = extract_data(str, token3, unit3, &value3);
    printf("--- Teste 3 (AAA) ---\n");
    printf("Esperado: 0:,0\n");
    printf("Recebido: %d:%s,%d\n", res3, unit3, value3);
    printf("--------------------------------\n");

    return 0;
}
