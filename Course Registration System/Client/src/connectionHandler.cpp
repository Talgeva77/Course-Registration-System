#include "../include/connectionHandler.h"
#include <boost/type_traits/is_base_of.hpp>

using boost::asio::ip::tcp;

using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;
using namespace std;

 
ConnectionHandler::ConnectionHandler(string host, short port): host_(host), port_(port), io_service_(), socket_(io_service_), decoderAnswerOpCodeChar(), decoderOpCodeChar(),decoderOptionalChar(), encoderOpCodeChar(){}
    
ConnectionHandler::~ConnectionHandler() {
    close();
}
 
bool ConnectionHandler::connect() {
    std::cout << "Starting connect to " 
        << host_ << ":" << port_ << std::endl;
    try {
		tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // the server endpoint
		boost::system::error_code error;
		socket_.connect(endpoint, error);
		if (error)
			throw boost::system::system_error(error);
    }
    catch (std::exception& e) {
        std::cerr << "Connection failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}
 
bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
    size_t tmp = 0;
	boost::system::error_code error;
    try {
        while (!error && bytesToRead > tmp ) {
			tmp += socket_.read_some(boost::asio::buffer(bytes+tmp, bytesToRead-tmp), error);			
        }
		if(error)
			throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool ConnectionHandler::sendBytes(const char bytes[], int bytesToWrite) {
    int tmp = 0;
	boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp ) {
			tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
		if(error)
			throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}
 
bool ConnectionHandler::getLine(std::string& line) {
    return Decoder(line);
}

bool ConnectionHandler::sendLine(std::string& line) {
    return Encoder(line);
}
 

bool ConnectionHandler::getFrameAscii(std::string& frame, char delimiter) {
    char ch;
    // Stop when we encounter the null character.
    // Notice that the null character is not appended to the frame string.
    try {
	do{
		if(!getBytes(&ch, 1))
		{
			return false;
		}
		if(ch!='\0')  
			frame.append(1, ch);
	}while (delimiter != ch);
    } catch (std::exception& e) {
	std::cerr << "recv failed2 (Error: " << e.what() << ')' << std::endl;
	return false;
    }
    return true;
}
 
 
bool ConnectionHandler::sendFrameAscii(const std::string& frame, char delimiter) {
	bool result=sendBytes(frame.c_str(),frame.length());
	if(!result) return false;
	return sendBytes(&delimiter,1);
}
 
// Close down the connection properly.
void ConnectionHandler::close() {
    try{
        socket_.close();
    } catch (...) {
        std::cout << "closing failed: connection already closed" << std::endl;
    }
}

bool ConnectionHandler:: Decoder(string& decode_Result) {
    char ch;
    while (decoderOpCodeChar.size() < 2) {
        if (!getBytes(&ch, 1)) {
            clearDecoder();
            return false;
        }
        decoderOpCodeChar.push_back(ch);
    }
    short opCode = bytesToShort(decoderOpCodeChar);
    if (opCode == 12)
        decode_Result = "ACK ";
    else
        decode_Result = "ERROR ";
    while (decoderAnswerOpCodeChar.size()<2) {
        if (!getBytes(&ch, 1)) {
            clearDecoder();
            return false;
        }
        if (ch != '\0')
            decode_Result = decode_Result + to_string(ch);
        decoderAnswerOpCodeChar.push_back(ch);
    }
    short answerOpCodeShort = bytesToShort(decoderAnswerOpCodeChar);
    if (opCode == 12) {
        if (answerOpCodeShort == 6 | answerOpCodeShort == 7 | answerOpCodeShort == 8 | answerOpCodeShort == 9 |answerOpCodeShort == 11) {
            if (answerOpCodeShort == 9){
                std:: string s;
                getFrameAscii(s,'D');
                decode_Result = decode_Result + s;
                getBytes(&ch, 1);
            }
            else{
                 std:: string s;
                 getFrameAscii(s,']');
                 decode_Result = decode_Result + s;
                 getBytes(&ch, 1);
            }
        }
        else{
            getBytes(&ch , 1);
        }
    }
    clearDecoder();
    return true;
}

bool ConnectionHandler:: Encoder(std:: string& frame){
    std:: string temp = frame;
    short opCodeShort;
    size_t pos1 = frame.find(" ");
    std:: string first = frame.substr(0,pos1);
    if (first == "ADMINREG")
        opCodeShort=1;
    if (first == "STUDENTREG")
        opCodeShort=2;
    if (first == "LOGIN")
        opCodeShort=3;
    if (first == "LOGOUT")
        opCodeShort=4;
    if (first == "COURSEREG")
        opCodeShort=5;
    if (first == "KDAMCHECK")
        opCodeShort=6;
    if (first == "COURSESTAT")
        opCodeShort=7;
    if (first == "STUDENTSTAT")
        opCodeShort=8;
    if (first == "ISREGISTERED")
        opCodeShort=9;
    if (first == "UNREGISTER")
        opCodeShort=10;
    if (first == "MYCOURSES")
        opCodeShort=11;
    shortToBytes(opCodeShort,encoderOpCodeChar);
    sendBytes(&encoderOpCodeChar[0], 2);
    if (opCodeShort == 4 | opCodeShort == 11){
        encoderOpCodeChar.clear();
        return true;
    }
    if (opCodeShort == 5 | opCodeShort == 6 | opCodeShort == 7 | opCodeShort == 9 | opCodeShort == 10){
        temp.erase(0,pos1+1);
        encoderOpCodeChar.clear();
        return sendBytes(temp.c_str(),2);
    }
    if (opCodeShort == 1 | opCodeShort == 2 |opCodeShort == 3) {
        temp.erase(0,pos1+1);
        size_t pos2 = temp.find(" ");
        std:: string second = temp.substr(0,pos2);
        temp.erase(0,pos2+1);
        size_t pos3 = temp.find(" ");
        std:: string third = temp.substr(0,pos3);
        bool result1 = sendBytes(second.c_str(), second.size());
        if (!result1) {
            encoderOpCodeChar.clear();
            return false;
        }
        sendBytes("\000", 1);
        bool result2 = sendBytes(third.c_str(), third.size());
        if (!result2) {
            encoderOpCodeChar.clear();
            return false;
        }
        encoderOpCodeChar.clear();
        return sendBytes("\000", 1);
    }
    if (opCodeShort == 8){
        temp.erase(0,pos1+1);
        bool result1 = sendBytes(temp.c_str(), temp.size());
        if (!result1) {
            encoderOpCodeChar.clear();
            return false;
        }
        return sendBytes("\000", 1);
    }
}



short ConnectionHandler:: bytesToShort(vector<char>& bytesArr)
{
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}

void ConnectionHandler:: shortToBytes(short num, vector<char>& bytesArr)
{
    bytesArr.push_back((num >> 8) & 0xFF);
    bytesArr.push_back(num & 0xFF);
}

void ConnectionHandler:: clearDecoder() {
    decoderOpCodeChar.clear();
    decoderAnswerOpCodeChar.clear();
    decoderOptionalChar.clear();
}

