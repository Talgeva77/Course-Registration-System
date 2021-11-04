package bgu.spl.net;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.rci.Command;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class OperationEncoderDecoder implements MessageEncoderDecoder <Command> {

    private byte[] bytes = new byte[1 << 10];
    private int len = 0;
    private int numOfZeroes = 0;
    private short opCode = -1;
    private String userName;
    private String password;
    private String stringCourseNum;

    public Command decodeNextByte(byte nextByte) {
        short sendableOpCode = -1;
        Command output;
        pushByte(nextByte);
        if (nextByte == '\0') {
            numOfZeroes++;
        }
        if (opCode == -1) {
            if (len == 2) {
                opCode = bytesToShort(bytes);
                bytes = new byte[1 << 10];
                len = 0;
            }
        }
        else {
            if (opCode == 1) {
                if (numOfZeroes == 2 & userName == null) {
                    userName = popString();
                    bytes = new byte[1 << 10];
                    len = 0;
                }
                if (numOfZeroes == 3) {
                    password = popString();
                    bytes = new byte[1 << 10];
                    sendableOpCode = opCode;
                    output = new ADMINREG(userName, password, sendableOpCode);
                    clearAll();
                    return output;
                }
            }
            if (opCode == 2) {
                if (numOfZeroes == 2 & userName == null) {
                    userName = popString();
                    bytes = new byte[1 << 10];
                    len = 0;
                }
                if (numOfZeroes == 3) {
                    password = popString();
                    bytes = new byte[1 << 10];
                    sendableOpCode = opCode;
                    output = new STUDENTREG(userName, sendableOpCode, password);
                    clearAll();
                    return output;
                }
            }
            if (opCode == 3) {
                if (numOfZeroes == 2 & userName == null) {
                    userName = popString();
                    bytes = new byte[1 << 10];
                    len = 0;
                }
                if (numOfZeroes == 3) {
                    password = popString();
                    bytes = new byte[1 << 10];
                    sendableOpCode = opCode;
                    output = new LOGIN(userName, password, sendableOpCode);
                    clearAll();
                    return output;
                }
            }
            if (opCode == 5 & len == 2) {
                stringCourseNum = popString();
                int courseNum = Integer.parseInt(stringCourseNum);
                bytes = new byte[1 << 10];
                sendableOpCode = opCode;
                output = new COURSEREG(courseNum, sendableOpCode);
                clearAll();
                return output;
            }
            if (opCode == 6 && len == 2) {
                stringCourseNum = popString();
                int courseNum = Integer.parseInt(stringCourseNum);
                bytes = new byte[1 << 10];
                sendableOpCode = opCode;
                output = new KDAMCHECK(courseNum, sendableOpCode);
                clearAll();
                return output;
            }
            if (opCode == 7 && len == 2) {
                stringCourseNum = popString();
                int courseNum = Integer.parseInt(stringCourseNum);
                bytes = new byte[1 << 10];
                sendableOpCode = opCode;
                output = new COURSESTAT(courseNum, sendableOpCode);
                clearAll();
                return output;
            }
            if (opCode == 8 && numOfZeroes == 2) {
                userName = popString();
                bytes = new byte[1 << 10];
                sendableOpCode = opCode;
                output = new STUDENTSTAT(userName, sendableOpCode);
                clearAll();
                return output;
            }
            if (opCode == 9 && len == 2) {
                stringCourseNum = popString();
                int courseNum = Integer.parseInt(stringCourseNum);
                bytes = new byte[1 << 10];
                sendableOpCode = opCode;
                output = new ISREGISTERED(courseNum, sendableOpCode);
                clearAll();
                return output;
            }
            if (opCode == 10 && len == 2) {
                stringCourseNum = popString();
                int courseNum = Integer.parseInt(stringCourseNum);
                bytes = new byte[1 << 10];
                sendableOpCode = opCode;
                output = new UNREGISTER(courseNum, sendableOpCode);
                clearAll();
                return output;
            }
        }
        if (opCode == 4) {
            bytes = new byte[1 << 10];
            sendableOpCode = opCode;
            output = new LOGOUT(sendableOpCode);
            clearAll();
            return output;
        }
        if (opCode == 11) {
            bytes = new byte[1 << 10];
            sendableOpCode = opCode;
            output = new MYCOURSES(sendableOpCode);
            clearAll();
            return output;
        }
        return null;
    }


    public short bytesToShort(byte[] byteArr) {
            short result = (short)((byteArr[0] & 0xff) << 8);
            result += (short)(byteArr[1] & 0xff);
            return result;
    }

    public byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }




    public byte[] encode(Command message){
        byte [] opCode= shortToBytes(message.getOpCode());
        String optional = message.getOptional();
        if(message instanceof ACK){
            byte [] ack = shortToBytes((short) 12);
            if(optional != null){
                byte [] optionalAsByte = optional.getBytes(StandardCharsets.UTF_8);
                return mergeArraysACK(ack , opCode , optionalAsByte);
            }
            else
                return mergeArraysACK(ack , opCode , new byte[0]);

        }
        else
            return mergeArraysError(shortToBytes((short)13),opCode);
    }
    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
    }

    private String popString() {
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        return result;
    }
    public byte[] mergeArraysACK(byte[] a1, byte[] a2, byte[] a3) {

        byte[] answer = new byte[a1.length + a2.length + a3.length + 1];
        byte i,j,k;
        for (i = 0; i < a1.length; i++)
            answer[i] = a1[i];

        for (j = 0; j < a2.length; j++)
            answer[i++] = a2[j];

        for (k = 0; k < a3.length; k++)
            answer[i++] = a3[k];
        answer[i++] = '\0';

        return answer;
    }

    public byte[] mergeArraysError(byte[] a1, byte[] a2) {

        byte[] answer = new byte[a1.length + a2.length];
        byte i,j;
        for (i = 0; i < a1.length; i++)
            answer[i] = a1[i];
        for (j = 0; j < a2.length; j++)
            answer[i++] = a2[j];
        return answer;
    }

    private void clearAll(){
        len = 0;
        numOfZeroes = 0;
        opCode = -1;
        userName = null;
        password = null;
        stringCourseNum = null;
    }

}
