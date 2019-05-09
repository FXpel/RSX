# Les règles de compilation peuvent utiliser les variables suivantes
# Consultez la documentation de make pour en savoir plus (et en
# trouver d’autres)
#
# $@  Le nom de la cible
# $<  Le nom de la première dépendance
# $^  La liste des dépendances

CC      = gcc

CFLAGS  = -Wall -Wextra -ansi -pedantic
CFLAGS += -D_XOPEN_SOURCE=500
CFLAGS += -g
JAVAC=javac
sources = $(wildcard *.java)
classes = $(sources:.java=.class)



all: udp dns $(classes)

udp: udp.o
	$(CC) $(LDFLAGS) -o $@ $^

dns: dns.o
	$(CC) $(LDFLAGS) -o $@ $^

%.o: %.c
	$(CC) $(CFLAGS) -c $<
	
%.class : %.java
	$(JAVAC) $<
clean:
	rm *.o
	rm -f *.class
mrproper: clean
	rm -f udp
	rm -f dns
