#include <stdio.h>
#include <string.h>

extern int move_n_to_array(int* buffer, int length, int* nelem, int* tail, int* head, int n, int* array);

void print_array(const char* msg, int* a, int n) {
    printf("%s", msg);
    for (int i = 0; i < n; i++) printf("%d ", a[i]);
    printf("\n");
}

int main() {
    printf("=== USAC07 Tests ===\n\n");

    // ----- Test 1: Normal circular case -----
    {
        int buffer[5] = {10, 20, 30, 40, 50};
        int length = 5;
        int nelem = 5;
        int tail = 3;  // oldest element is buffer[3] = 40
        int head = 3;  // full circular
        int out[3];

        printf("Test 1: Move 3 elements\n");
        int r = move_n_to_array(buffer, length, &nelem, &tail, &head, 3, out);

        printf("Return: %d\n", r);
        print_array("Output array: ", out, 3);
        printf("Updated tail=%d (expected 1), nelem=%d (expected 2)\n\n", tail, nelem);
    }

    // ----- Test 2: n larger than nelem (should fail) -----
    {
        int buffer[5] = {1,2,3,4,5};
        int length = 5;
        int nelem = 2;
        int tail = 0;
        int head = 2;
        int out[5];

        printf("Test 2: Move 3 elements but nelem=2 → must fail\n");
        int r = move_n_to_array(buffer, length, &nelem, &tail, &head, 3, out);

        printf("Return: %d (expected 0)\n", r);
        printf("nelem=%d (expected unchanged: 2)\n\n", nelem);
    }

    // ----- Test 3: Move all elements -----
    {
        int buffer[4] = {7,8,9,10};
        int length = 4;
        int nelem = 4;
        int tail = 1; // oldest = buffer[1] = 8
        int head = 1;
        int out[4];

        printf("Test 3: Move all 4 elements\n");
        int r = move_n_to_array(buffer, length, &nelem, &tail, &head, 4, out);

        printf("Return: %d\n", r);
        print_array("Output array: ", out, 4);
        printf("nelem=%d (expected 0), tail=%d (expected 1)\n\n", nelem, tail);
    }

    return 0;
}
