package dev.mv.cstblib;

import dev.mv.cstblib.memory.Memory;
import dev.mv.cstblib.memory.Pointer;

import static dev.mv.cstblib.memory.Memory.*;
import static dev.mv.cstblib.memory.Sizes.*;
import static dev.mv.cstblib.memory.Pointer.*;

public class Main {
    public static void printf(String str, Object... arr) {
        System.out.printf(str, arr);
    }

    public static void main(String[] args) {
        Pointer<Character> x = malloc(5, sizeof(char.class));
        x.at(0, 'h');
        x.at(1, 'e');
        x.at(2, 'l');
        x.at(3, 'l');
        x.at(4, 'o');

        printf("%s", x.as_str(5));
        printf("\n");

        System.out.println(x.incr(3).deref());
        System.out.println(x.address());
        free(x);
    }
}

/*
#include <stdio.h>
#include <stdlib.h>

int main(int argc, int* args) {
    char* x=malloc(6*sizeof(char));
    x[0]='k';
    x[1]='u';
    x[2]='r';
    x[3]='w';
    x[4]='a';
    x[5]='\0';
    printf("%s",x);
    free(x);
    return 0;
}

 */