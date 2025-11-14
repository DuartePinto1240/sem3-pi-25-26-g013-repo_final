#include <stdio.h>

extern int sort_array(int* vec, int length, char order);

void print_vec(int* v, int n) {
    for (int i = 0; i < n; i++) printf("%d ", v[i]);
    printf("\n");
}

int main() {
    printf("=== USAC08 Tests ===\n\n");

    // ----- Test 1: Ascending -----
    {
        int v[6] = {9, 4, 1, 7, 3, 8};
        printf("Test 1: Ascending sort\n");
        int r = sort_array(v, 6, 1);
        printf("Return: %d (expected 1)\n", r);
        print_vec(v, 6);
        printf("\n");
    }

    // ----- Test 2: Descending -----
    {
        int v[5] = {10, 0, -1, 40, 2};
        printf("Test 2: Descending sort\n");
        int r = sort_array(v, 5, 0);
        printf("Return: %d (expected 1)\n", r);
        print_vec(v, 5);
        printf("\n");
    }

    // ----- Test 3: Invalid length -----
    {
        int v[3] = {5, 2, 9};
        printf("Test 3: Invalid length (0) → must fail\n");
        int r = sort_array(v, 0, 1);
        printf("Return: %d (expected 0)\n\n", r);
    }

    return 0;
}
