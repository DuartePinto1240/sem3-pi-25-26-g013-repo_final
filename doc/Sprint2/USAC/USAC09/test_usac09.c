#include <stdio.h>
#include "median.h"

static void dump_array(const char *tag, int vec[], int len) {
    printf("%s [", tag);
    for (int i = 0; i < len; i++) {
        printf("%d%s", vec[i], (i+1 < len ? ", " : ""));
    }
    printf("]\n");
}

static void print_header(const char *title) {
    printf("\n================ %s ================\n", title);
}

static void run_test(const char *title, int vec[], int len, int expected_ret, int expected_me) {
    print_header(title);

    int me = -999;
    int ret;

    dump_array("Before =", vec, len);

    ret = median(vec, len, &me);

    dump_array("After  =", vec, len);

    printf("Return: %d (expected %d)\n", ret, expected_ret);

    if (expected_ret == 1)
        printf("Median: %d (expected %d)\n", me, expected_me);
    else
        printf("Median ignored (expected return = 0)\n");

    printf("------------------------------------------\n");
}

int main() {

    // length <= 0
    int v0[] = {1,2,3};
    run_test("Test 1: length = 0", v0, 0, 0, 0);
    run_test("Test 2: length = -5", v0, -5, 0, 0);

    // ímpar (med = elemento central)
    int v1[] = {7, 2, 9, 1, 4};	// med = 4
    run_test("Test 3: odd length", v1, 5, 1, 4);

    // par (med = média dos centrais)
    int v2[] = {10, 0, 20, 30};	// med = (10+20)/2 = 15
    run_test("Test 4: even length", v2, 4, 1, 15);

    // já ordenado (par)
    int v3[] = {1,2,3,4};
    run_test("Test 5: sorted even", v3, 4, 1, 2);

    // já ordenado (ímpar)
    int v4[] = {5,10,15};
    run_test("Test 6: sorted odd", v4, 3, 1, 10);

    // repetidos
    int v5[] = {5,5,5,5,5};
    run_test("Test 7: repeated values", v5, 5, 1, 5);

    // negativos
    int v6[] = {-3, -10, 7, 0, 4}; // med = 0
    run_test("Test 8: negative values", v6, 5, 1, 0);

    printf("\n=========== ALL TESTS COMPLETED ===========\n");

    return 0;
}