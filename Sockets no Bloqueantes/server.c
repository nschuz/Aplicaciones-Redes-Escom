#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <pthread.h>
#include <sys/types.h>
#include "libreria_chat.h"
#include <fcntl.h>
#include <sys/time.h>
#include <sys/ioctl.h>
#include <sys/select.h>

#define MAX_CLIENTS 50
#define BUFFER_SZ 2048

int cli_count = 0;
static int uid = 10;


typedef struct
{
	struct sockaddr_in address;
	int sock;
	int uid;
	char nom_usuario[32];
} client_t;

client_t *clients[MAX_CLIENTS];

void ImprimeDireccion_Cliente(struct sockaddr_in addr);
void Inserta_Cola(client_t *cl);
void Borra_Cola(int uid);
void EnviaMensaje(char *s, int uid);
void *Control_Cliente(void *arg);


int main(int argc, char **argv)
{
	if (argc != 2)
	{
		printf("El puerto en uso es: %s \n", argv[0]);
		return EXIT_FAILURE;
	}

	char *ip = "127.0.0.1";
	int Puerto = atoi(argv[1]);
	int option = 1;
	int listenfd = 0, connfd = 0;
	int max_sd;
	struct sockaddr_in dir_server;
	struct sockaddr_in cli_addr;
	//struct fd_set master_set, working_set;
	fd_set rfds;
	struct timeval timeout;
	pthread_t tid;

	
	listenfd = socket(AF_INET, SOCK_STREAM, 0);
	dir_server.sin_family = AF_INET;
	dir_server.sin_addr.s_addr = inet_addr(ip);
	dir_server.sin_port = htons(Puerto);

	if (setsockopt(listenfd, SOL_SOCKET, (SO_REUSEPORT | SO_REUSEADDR), (char *)&option, sizeof(option)) < 0)
	{
		Termina_Error("ERROR: setsockopt falló");
	}

	/* EStablecemos el socket como no bloqueante */
	int r;
	r = fcntl(listenfd, F_GETFL,0);
	fcntl(listenfd, F_SETFL, r|O_NONBLOCK);
	int rc;
	rc = bind(listenfd, (struct sockaddr *)&dir_server, sizeof(dir_server));


	if ( rc < 0)
	{
		Termina_Error("ERROR: El enlace de socket falló");
	}


	if (listen(listenfd, 10) < 0)
	{
		Termina_Error("ERROR: Fallo al abrir el socket");
	}

	printf("=== Sala de chat iniciada ===\n");
	printf("=== Servidor en el puerto %d ===\n", Puerto);

	/*************************************************************/
	/* Initialize the master fd_set                              */
	/*************************************************************/
	FD_ZERO(&rfds);
	max_sd = listenfd;
	FD_SET(listenfd, &rfds);

	timeout.tv_sec  = 3 * 60;
	timeout.tv_usec = 0;

	while (1)
	{
		/**********************************************************/
      		/* Copy the master fd_set over to the working fd_set.     */
      		/**********************************************************/
      		//memcpy(&working_set, &rfds, sizeof(master_set));
		
		printf("Waiting on select()...\n");
		rc = select(max_sd + 1, &rfds, NULL, NULL, &timeout);

		if (rc < 0)
     		{
         		perror("  select() failed");
         		break;
      		}
		if (rc == 0)
      		{
        		printf("  select() timed out.  End program.\n");
         		break;
      		}


		socklen_t clilen = sizeof(cli_addr);
		connfd = accept(listenfd, (struct sockaddr *)&cli_addr, &clilen);

		if ((cli_count + 1) == MAX_CLIENTS)
		{
			printf("Max clients reached. Rejected: ");
			ImprimeDireccion_Cliente(cli_addr);
			printf(":%d\n", cli_addr.sin_port);
			close(connfd);
			continue;
		}

		client_t *cli = (client_t *)malloc(sizeof(client_t));
		cli->address = cli_addr;
		cli->sock = connfd;
		cli->uid = uid++;

		Inserta_Cola(cli);
		pthread_create(&tid, NULL, &Control_Cliente, (void *)cli);

		sleep(1);
	}

	return EXIT_SUCCESS;
}


void ImprimeDireccion_Cliente(struct sockaddr_in addr)
{
	printf("%d.%d.%d.%d",
		   addr.sin_addr.s_addr & 0xff,
		   (addr.sin_addr.s_addr & 0xff00) >> 8,
		   (addr.sin_addr.s_addr & 0xff0000) >> 16,
		   (addr.sin_addr.s_addr & 0xff000000) >> 24);
}

void Inserta_Cola(client_t *cl)
{
	for (int i = 0; i < MAX_CLIENTS; ++i)
	{
		if (!clients[i])
		{
			clients[i] = cl;
			break;
		}
	}
}

void Borra_Cola(int uid)
{
	for (int i = 0; i < MAX_CLIENTS; ++i)
	{
		if (clients[i])
		{
			if (clients[i]->uid == uid)
			{
				clients[i] = NULL;
				break;
			}
		}
	}
}

void EnviaMensaje(char *s, int uid)
{
	for (int i = 0; i < MAX_CLIENTS; ++i)
	{
		if (clients[i])
		{
			if (clients[i]->uid != uid)
			{
				if (send(clients[i]->sock, s, strlen(s), 0) < 0)
				{
					Termina_Error("ERROR: error al enviar el mensaje");
				}
			}
		}
	}
}

void *Control_Cliente(void *arg)
{
	char buff_out[BUFFER_SZ];
	char nom_usuario[32];
	int leave_flag = 0;

	cli_count++;
	client_t *cli = (client_t *)arg;

	memset(buff_out, 0, BUFFER_SZ);

	if (recv(cli->sock, nom_usuario, 32, 0) <= 0 || strlen(nom_usuario) < 2 || strlen(nom_usuario) >= 32 - 1)
	{
		printf("No ingreso un nombre.\n");
		leave_flag = 1;
	}
	else
	{
		strcpy(cli->nom_usuario, nom_usuario);
		nom_usuario[strcspn(nom_usuario, "\n")] = '\0';

		sprintf(buff_out, "%s se ha unido - %d conectados\n", nom_usuario, cli_count);
		printf("%s", buff_out);
		EnviaMensaje(buff_out, cli->uid);
	}


	while (1)
	{
		if (leave_flag)
		{
			break;
		}
		memset(buff_out, 0, BUFFER_SZ);
		int receive = recv(cli->sock, buff_out, BUFFER_SZ, 0);
		if (receive > 0)
		{
			if (strlen(buff_out) > 0)
			{
				EnviaMensaje(buff_out, cli->uid);
				printf("%s\n", buff_out);
			}
		}
		else if (receive == 0)
		{
			cli_count--;
			sprintf(buff_out, "%s se ha ido - %d conectados\n", cli->nom_usuario, cli_count);
			printf("%s", buff_out);
			EnviaMensaje(buff_out, cli->uid);
			leave_flag = 1;
		}
		else
		{
			printf("ERROR: -1\n");
			leave_flag = 1;
		}

	}

	close(cli->sock);
	Borra_Cola(cli->uid);
	free(cli);
	pthread_detach(pthread_self());

	return NULL;
}
