#include <stdlib.h>
#include "./Task.cpp"


int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);
    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    mutex _mutex;
    Task t(connectionHandler, _mutex);
    std::thread th(&Task::run, &t);
    while (1) {
        std::string answer;
        if (!connectionHandler.getLine(answer)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
        }
        char opCode = answer [4]-48;
        if(opCode == 6 | opCode == 7 | opCode == 8 | opCode == 9) {
            std:: string ans1 = answer.substr (0, 5);
            std:: string ans2 = answer.substr(5, answer.size()-5);
            std::cout << ans1  << std::endl;
            std::cout << ans2 <<std::endl;
        }
        else if (opCode == 1 && answer.size() > 6){
            std:: string ans1 = answer.substr (0, 6);
            std:: string ans2 = answer.substr(6, answer.size()-6);
            std::cout << ans1  << std::endl;
            std::cout << ans2 <<std::endl;
        }
        else
            std::cout  << answer << std::endl;
        if (answer == "ACK 4") {
            th.detach();
        }
    }
    return 0;
}
