#include <stdio.h>
#include <string.h>

int encrypt_data(char* in, int key, char* out);

int main() {
    char input1[] = "HELLO";
    char output1[64];

    int r1 = encrypt_data(input1, 3, output1);
    printf("Input: %s\n", input1);
    printf("Key: 3\n");
    printf("Result: %s  (return = %d)\n\n", output1, r1);


    // Test 2: key inválida
    char output2[64];
    int r2 = encrypt_data("HELLO", 0, output2);
    printf("Test key inválida:\n");
    printf("Return = %d, Output = '%s'\n\n", r2, output2);


    // Test 3: string com caracter inválido
    char output3[64];
    int r3 = encrypt_data("HELLO1", 5, output3);
    printf("Test string inválida:\n");
    printf("Return = %d, Output = '%s'\n\n", r3, output3);


    // Test 4: string vazia
    char input4[] = "";
    char output4[64];
    int r4 = encrypt_data(input4, 4, output4);
    printf("Test string vazia:\n");
    printf("Return = %d, Output = '%s'\n\n", r4, output4);

    return 0;
}
