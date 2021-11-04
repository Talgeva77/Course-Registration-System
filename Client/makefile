CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -lboost_thread -pthread

all: BGRSclient
	g++ -o bin/BGRSclient bin/BGRSclient.o bin/connectionHandler.o bin/Task.o $(LDFLAGS)
BGRSclient: bin/connectionHandler.o bin/BGRSclient.o bin/Task.o

	
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp


bin/BGRSclient.o: src/BGRSclient.cpp
	g++ $(CFLAGS) -o bin/BGRSclient.o src/BGRSclient.cpp


bin/Task.o: src/Task.cpp
	g++ $(CFLAGS) -o bin/Task.o src/Task.cpp




.PHONY: clean
clean:
	rm -f bin/*

