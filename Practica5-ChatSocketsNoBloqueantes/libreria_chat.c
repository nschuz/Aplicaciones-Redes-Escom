#include "libreria_chat.h"

void Termina_Error(char *errorMessage)
{
    perror(errorMessage);
    exit(EXIT_FAILURE);
}


void Vaciado_Buffer()
{
	printf("%s", "> ");
	fflush(stdout);
}

