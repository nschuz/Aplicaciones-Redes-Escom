#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <signal.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <pthread.h>
#include "libreria_chat.h" 

#define LENGTH 2048 

int sockfd = 0;
char nom_usuario[32];

void Vaciado_Buffer(); 
void Emisor_Mensajes();  
void Receptor_Mensajes(); 

int main(int argc, char **argv)
{
	printf("\033[1;35m");

	printf("PRACTICA NUMERO 5 CHAT-SOCKET NO BLOQUEANTE\n");

	 printf("\033[0m");
	if (argc != 2)
	{
		printf("El puerto en uso es: %s \n", argv[0]);
		return EXIT_FAILURE;
	}

	char *ip = "127.0.0.1";
	int Puerto = atoi(argv[1]);

	printf("Por favor ingresa tu nombre (Máximo 32 caracteres): ");
	fgets(nom_usuario, 32, stdin); 
	nom_usuario[strcspn(nom_usuario, "\n")] = '\0';


	if (strlen(nom_usuario) > 32 || strlen(nom_usuario) < 2)
	{
		printf("El nombre debe tener minimo 2 caracteres y maximo 32\n");
		return EXIT_FAILURE;
	}

	struct sockaddr_in server_addr;

	sockfd = socket(AF_INET, SOCK_STREAM, 0);
	server_addr.sin_family = AF_INET;
	server_addr.sin_addr.s_addr = inet_addr(ip);
	server_addr.sin_port = htons(Puerto);

	int err = connect(sockfd, (struct sockaddr *)&server_addr, sizeof(server_addr));
	if (err == -1)
	{
		printf("Error: No se pudo crear la conexión\n");
		return EXIT_FAILURE;
	}

	send(sockfd, nom_usuario, 32, 0);

	printf("=== Bienvenido a la sala ===\n");


	pthread_t hilo_emisor;
	if (pthread_create(&hilo_emisor, NULL, (void *)Emisor_Mensajes, NULL) != 0)
	{
		printf("Error: No se pudo crear el hilo del emisor\n");
		return EXIT_FAILURE;
	}

	pthread_t hilo_receptor;
	if (pthread_create(&hilo_receptor, NULL, (void *)Receptor_Mensajes, NULL) != 0)
	{
		printf("ERROR: No se pudo crear el hilo del receptor\n");
		return EXIT_FAILURE;
	}
	while (1)
	{
		
	}
	
	close(sockfd);
	return EXIT_SUCCESS;
}


void Emisor_Mensajes()
{
	char mensaje[LENGTH] = {};
	char buffer[LENGTH + 32] = {};


	while (1)
	{
		memset(mensaje, 0, LENGTH); 
		memset(buffer, 0, LENGTH+32); 
		Vaciado_Buffer();

		fgets(mensaje, LENGTH, stdin); 
		
		mensaje[strcspn(mensaje, "\n")] = '\0';
		
		sprintf(buffer, "°/||%s||/° → %s\n", nom_usuario, mensaje);
		
		send(sockfd, buffer, strlen(buffer), 0);
	}
}


void Receptor_Mensajes()
{
	char mensaje[LENGTH] = {};
	while (1)
	{
		int receive = recv(sockfd, mensaje, LENGTH, 0); 
		if (receive > 0)
		{
			printf("%s", mensaje); 
			Vaciado_Buffer();
		}
		else if (receive == 0)
		{
			break;
		}
		else
		{
		
		}
		memset(mensaje, 0, sizeof(mensaje)); 
	}
}
