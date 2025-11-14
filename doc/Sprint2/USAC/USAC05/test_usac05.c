#include <stdio.h>
#include "asm.h"

static void dump(const char* tag, int buf[], int len, int nelem, int head, int tail) {
    printf("%s\n", tag);
    printf("buffer: [");
    for (int i = 0; i < len; ++i) {
        printf("%d%s", buf[i], (i+1<len)?", ":"");
    }
    printf("]\n");
    printf("nelem=%d head=%d tail=%d\n\n", nelem, head, tail);
}

int main() {
    int len = 4;
    int buf[4] = {0,0,0,0};
    int nelem = 0, head = 0, tail = 0;
    int ret;

    dump("Initial", buf, len, nelem, head, tail);

    ret = enqueue_value(buf, len, &nelem, &tail, &head, 10);
    printf("[enqueue 10] ret=%d  (expect 0)\n", ret);
    dump("After 1", buf, len, nelem, head, tail);

    ret = enqueue_value(buf, len, &nelem, &tail, &head, 20);
    printf("[enqueue 20] ret=%d  (expect 0)\n", ret);
    ret = enqueue_value(buf, len, &nelem, &tail, &head, 30);
    printf("[enqueue 30] ret=%d  (expect 0)\n", ret);
    ret = enqueue_value(buf, len, &nelem, &tail, &head, 40);
    printf("[enqueue 40] ret=%d  (expect 1 — became full)\n", ret);
    dump("After fill", buf, len, nelem, head, tail);

    // Overwrite oldest
    ret = enqueue_value(buf, len, &nelem, &tail, &head, 50);
    printf("[enqueue 50 overwrite] ret=%d  (expect 1 — stays full)\n", ret);
    dump("After overwrite", buf, len, nelem, head, tail);

    printf("Expected buffer like: [50,20,30,40] with head=1 tail=1 nelem=4 (order may differ if head index differs)\n");
    return 0;
}
