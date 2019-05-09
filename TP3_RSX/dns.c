/*
 * Original de Laurent NOE ~ 2009
 * Mise à jour du traitement des erreurs, des includes, et de l'affichage - Arthur ROLAND & Rudy SEYS - Mars 2017
 * Mise à jour des noms/adresses des serveurs DNS de la fac, affichage et commentaires - Laurent NOE - Janvier 2019
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <arpa/inet.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <string.h>
#include <netdb.h>
#include <unistd.h>
#include <assert.h>

#define PROGRAM_CALL_DNS

#define QUERYLENGTH 29
#define NS_PACKETSZ 512

/* NOM/ADRESSE-IP + PORT  du serveur DNS qui recevra notre requete */
#define HOSTDESTNAME "reserv1.univ-lille1.fr" /*  "reserv2.univ-lille1.fr" */
#define HOSTDESTADDR "127.0.0.53" /* "193.49.225.90" , "8.8.8.8", "8.8.4.4" */
#define PORT     53

int main(void)
{
    int sock = 0;
    struct sockaddr_in name;
    char query[QUERYLENGTH] = {
        (char) 0x08, (char) 0xbb, (char) 0x01, (char) 0x00,     /* a) 12 octets d'entete : identifiant de requete/parametres [RFC1035, 4.1.1]*/
        (char) 0x00, (char) 0x01, (char) 0x00, (char) 0x00,
        (char) 0x00, (char) 0x00, (char) 0x00, (char) 0x00,
        (char) 0x03, (char) 0x77, (char) 0x77, (char) 0x77,     /* b) QUESTION [RFC1035, 4.1.2] */
        (char) 0x04, (char) 0x6c, (char) 0x69, (char) 0x66,       /* b.1) QNAME : "3www4lifl2fr0" [RFC1035, 3.1]*/
        (char) 0x6c, (char) 0x02, (char) 0x66, (char) 0x72,
        (char) 0x00,
        (char) 0x00, (char) 0x01,                                 /* b.2) QTYPE : A (a host address) [RFC1035, 3.2.3]*/
        (char) 0x00, (char) 0x01                                  /* b.3) QCLASS : IN (the Internet) [RFC1035, 3.2.4]*/
    };

    char buffer[NS_PACKETSZ];

    /* recvfrom */
    ssize_t len;
    int i;
    struct sockaddr_in addrClient;
    socklen_t addrClientlen = 0;

#ifdef PROGRAM_CALL_DNS
    struct hostent *hp = NULL;
    char **p = NULL;
#endif

    memset(&addrClient, 0, sizeof(struct sockaddr_in));
    memset(&name, 0, sizeof(struct sockaddr_in));


    /* 1) obtenir l'adresse IP du serveur DNS ... en utilisant une requete DNS ??! */
#ifdef PROGRAM_CALL_DNS
    fprintf(stdout, " recherche de l'IP, pour le nom de l'hote de destination \"%s\" ... ",HOSTDESTNAME);
    hp = gethostbyname(HOSTDESTNAME);   /* convertir NOM SYMBOLIQUE en @IP */
    if (hp == NULL) {
        perror("[erreur] - gethostbyname ");
        return EXIT_FAILURE;
    }
    fprintf(stdout, "[ok]\n");

    /* affichage */
    fprintf(stdout, " - h_name : \"%s\"\n", hp->h_name);

    for (p = hp->h_aliases; *p != NULL; p++)
        fprintf(stdout, " - h_aliases : \"%s\"\n", *p);

    fprintf(stdout, " - h_addrtype : %s\n",
            (hp->h_addrtype ==
             AF_INET) ? "AF_INET" : ((hp->h_addrtype ==
                                      AF_INET6) ? "AF_INET6" : "unknown"));
    fprintf(stdout, " - h_length : %d\n", hp->h_length);

    for (p = hp->h_addr_list; *p != NULL; p++)
        fprintf(stdout, " - h_addr_list : \"%x.%x.%x.%x\"\n",
                *(*p) & 0xff, *(*p + 1) & 0xff, *(*p + 2) & 0xff,
                *(*p + 3) & 0xff);

    fprintf(stdout, " - h_addr : \"%x.%x.%x.%x\"\n",
            *(*hp->h_addr_list) & 0xff, *(*hp->h_addr_list + 1) & 0xff,
            *(*hp->h_addr_list + 2) & 0xff,
            *(*hp->h_addr_list + 3) & 0xff);

    /* 2) creation d'un socket udp */
    fprintf(stdout, " creation du socket en mode DGRAM (UDP) ... ");
    sock = socket(PF_INET, SOCK_DGRAM, 0);
    if (sock < 0) {
        perror("[erreur] - socket ");
        return EXIT_FAILURE;
    }

    fprintf(stdout, "[ok]\n");

    /* 3) preparation du socket d'envoi */
    name.sin_family = (short unsigned int) hp->h_addrtype;
    name.sin_port = htons(PORT);
    memcpy(&name.sin_addr.s_addr, hp->h_addr_list[0], (long unsigned int) hp->h_length);        /* <- memcpy + inversion!!! */

#else /* PROGRAM_CALL_DNS */

    /* 2) creation d'un socket udp */
    fprintf(stdout, " creation du socket en mode DGRAM (UDP) ... ");
    sock = socket(PF_INET, SOCK_DGRAM, 0);
    if (sock < 0) {
        perror("[erreur] - socket ");
        return EXIT_FAILURE;
    }

    /* 3) autre possibilite : on connait l'@IP du serveur DNS et c'est la solution la plus logique (le probleme de "poule et d'oeuf" disparait)  */
    fprintf(stdout, " conversion de l'IP de l'hote de destination \"%s\" en binaire ... ",HOSTDESTADDR);
    name.sin_family = AF_INET;
    name.sin_port = htons(PORT);
    if (inet_pton(AF_INET, HOSTDESTADDR, &name.sin_addr.s_addr) < 0) {
        perror("[erreur] - inet_pton ");
    }
    fprintf(stdout, "[ok]\n");

#endif /* PROGRAM_CALL_DNS */

    /* 4) envoi du message */
    fprintf(stdout, " envoie du message ... ");
    if ((len = sendto
        (sock, query, QUERYLENGTH, 0, (struct sockaddr *) &name,
         sizeof(struct sockaddr_in))) < 0) {
        perror("[erreur] - sendto ");
        return EXIT_FAILURE;
    }
    fprintf(stdout, "[ok]\n longueur du message envoye : %lu\n", len);

    /*
     * 5) reception du message
     */
    fprintf(stdout, " reception du message ... ");
    if ((len =
         recvfrom(sock, buffer, NS_PACKETSZ, 0,
                  (struct sockaddr *) &addrClient, &addrClientlen)) < 0) {
        perror("[erreur] - recvfrom ");
        return EXIT_FAILURE;
    }
    fprintf(stdout, "[ok]\n longueur du message recu : %lu\n", len);

    /* fermeture de la liaison. */
    close(sock);

    /* affichage de la reponse */

    /* affichage %c */
    for (i = 0; i < len; i++) {
      fprintf(stdout, " %c ", buffer[i] > 31 ? buffer[i] : '.');
        if (!((i + 1) % 16))
            fprintf(stdout, "\n");
    }
    fprintf(stdout, "\n");

    /* affichage %h */
    for (i = 0; i < len; i++) {
        fprintf(stdout, "%.2x ", (int) (buffer[i] & 0xff));
        if (!((i + 1) % 16))
            fprintf(stdout, "\n");
    }
    fprintf(stdout, "\n");

    return EXIT_SUCCESS;
}
