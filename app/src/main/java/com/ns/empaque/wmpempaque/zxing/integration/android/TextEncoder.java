package com.ns.empaque.wmpempaque.zxing.integration.android;


final class TextEncoder extends C40Encoder {

    @Override
    public int getEncodingMode() {
        return HighLevelEncoder.TEXT_ENCODATION;
    }

    @Override
    int encodeChar(char c, StringBuilder sb) {
        if (c == ' ') {
            sb.append('\3');
            return 1;
        }
        if (c >= '0' && c <= '9') {
            sb.append((char) (c - 48 + 4));
            return 1;
        }
        if (c >= 'a' && c <= 'z') {
            sb.append((char) (c - 97 + 14));
            return 1;
        }
        if (c >= '\0' && c <= '\u001f') {
            sb.append('\0'); //Shift 1 Set
            sb.append(c);
            return 2;
        }
        if (c >= '!' && c <= '/') {
            sb.append('\1'); //Shift 2 Set
            sb.append((char) (c - 33));
            return 2;
        }
        if (c >= ':' && c <= '@') {
            sb.append('\1'); //Shift 2 Set
            sb.append((char) (c - 58 + 15));
            return 2;
        }
        if (c >= '[' && c <= '_') {
            sb.append('\1'); //Shift 2 Set
            sb.append((char) (c - 91 + 22));
            return 2;
        }
        if (c == '\u0060') {
            sb.append('\2'); //Shift 3 Set
            sb.append((char) (c - 96));
            return 2;
        }
        if (c >= 'A' && c <= 'Z') {
            sb.append('\2'); //Shift 3 Set
            sb.append((char) (c - 65 + 1));
            return 2;
        }
        if (c >= '{' && c <= '\u007f') {
            sb.append('\2'); //Shift 3 Set
            sb.append((char) (c - 123 + 27));
            return 2;
        }
        if (c >= '\u0080') {
            sb.append("\1\u001e"); //Shift 2, Upper Shift
            int len = 2;
            len += encodeChar((char) (c - 128), sb);
            return len;
        }
        HighLevelEncoder.illegalCharacter(c);
        return -1;
    }

}
