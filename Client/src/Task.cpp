#include "../include/connectionHandler.h"
#include <thread>
#include <mutex>
using namespace std;

class Task{
private:
    ConnectionHandler & connectionHandler;
    mutex & _mutex;
public:
    Task (ConnectionHandler & c, mutex & m):connectionHandler(c), _mutex(m){}
    void run(){
        while (true) {
            const short bufsize = 1024;
            char buf[bufsize];
            std::cin.getline(buf, bufsize);
            std::string line(buf);
            if (!connectionHandler.sendLine(line)) {
                std::cout << "Disconnected. Exiting...\n" << std::endl;
            }
        }
    }
};
