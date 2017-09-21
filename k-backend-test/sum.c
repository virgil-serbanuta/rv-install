#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>

int main(int argc, char* argv[]) {
unsigned long long n = atoi(argv[1]);
unsigned long long s = 0;

while (!(n <= 0)) {
s = s + n ;
n = n + -1 ;
}
printf("%llu\n", s);
return 0;
}
