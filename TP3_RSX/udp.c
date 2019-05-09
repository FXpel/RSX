#include <assert.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>

#define NS_PACKETSZ 512
#define IPADRESS "127.0.0.53"
#define PORT     1200

int main(int argc, char **argv) {
  char *tosend;
  char buffer[NS_PACKETSZ];
  struct sockaddr_in name;
  int n;
  int sock;

  /* recuperation du buffer */
  assert (argc == 2);
  tosend = argv[1];

  memset(&name, 0, sizeof(struct sockaddr_in));

  /*create socket*/
  fprintf(stdout, " creation du socket en mode DGRAM (UDP) ... ");
  sock = socket(AF_INET, SOCK_DGRAM, 0);
  assert (sock != -1 );
  fprintf(stdout, "[ok]\n");

  /*param socket*/
  fprintf(stdout, " prépation de adresse à envoyer ... ");
  name.sin_family = AF_INET;
  name.sin_port = htons(PORT);
  name.sin_addr.s_addr = inet_addr(IPADRESS);
  fprintf(stdout, "[ok]\n");

  /*bind*/
  fprintf(stdout, " bind du socket ... ");
  n = bind(sock,(const struct sockaddr *) &name, sizeof(name));
  assert (n != -1 );
  fprintf(stdout, "[ok]\n");

  /*send*/
  fprintf(stdout, " envoie du message ... ");
  n = sendto(sock,tosend,sizeof(tosend),0
            ,(struct sockaddr *) &name
            ,sizeof(struct sockaddr_in));
  assert (n != -1 );
  fprintf(stdout, "[ok]\n");

  /*reception*/
  fprintf(stdout, " reception du message ... ");
  n = recv(sock,buffer,NS_PACKETSZ,0);
  assert (n != -1 );
  fprintf(stdout, "[ok]\n");
  if (n != NS_PACKETSZ) {
    buffer[n]= '\0';
  }

  /* fermeture de la liaison. */
  close(sock);

  /* affichage des données */
  printf ("\n***\nmessage :\n%s\n",buffer);
  fprintf(stdout, "***\n affichage du message ... ");
  fprintf(stdout, "[ok]\n");

  fprintf(stdout,"Fin de liaison\n");
  return 0;
}
