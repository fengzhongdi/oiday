package oiday.impl;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public final class ShallowJSONParser {

    private static final int COMMA = ',';
    private static final int CLOSED_CURLY = '}';
    private static final int CLOSED_BRACKET = ']';
    private static final int MINUS = '-';
    private static final int ALPHA_0 = '0';
    private static final int ALPHA_1 = '1';
    private static final int ALPHA_2 = '2';
    private static final int ALPHA_3 = '3';
    private static final int ALPHA_4 = '4';
    private static final int ALPHA_5 = '5';
    private static final int ALPHA_6 = '6';
    private static final int ALPHA_7 = '7';
    private static final int ALPHA_8 = '8';
    private static final int ALPHA_9 = '9';
    private static final int DOUBLE_QUOTE = '"';
    private static final int ESCAPE = '\\';
    private static final int COLON = ':';
    private static final int OPEN_CURLY = '{';
    private static final int OPEN_BRACKET = '[';
    private static final int LETTER_N = 'n';
    private static final int LETTER_T = 't';
    private static final int LETTER_F = 'f';
    private static final String WORD_NULL = "null";

    private static final ArrayList<String> EMPTY_LIST =
            new ArrayList<String>();
    private static final HashMap<String, String> EMPTY_MAP =
            new HashMap<String, String>();
    private static final char[] controlMap = new char[255];
    private static final char[] inverseControlMap = new char[255];

    static {
        Arrays.fill(controlMap, (char) 0);
        controlMap[(int) 'n'] = '\n';
        controlMap[(int) 'b'] = '\b';
        controlMap[(int) '/'] = '/';
        controlMap[(int) 'f'] = '\f';
        controlMap[(int) 'r'] = '\r';
        controlMap[(int) 't'] = '\t';
        controlMap[(int) '\\'] = '\\';
        controlMap[(int) '"'] = '"';
    }

    static {
        Arrays.fill(inverseControlMap, (char) 0);
        inverseControlMap[(int) '\n'] = 'n';
        inverseControlMap[(int) '\b'] = 'b';
        inverseControlMap[(int) '\f'] = 'f';
        inverseControlMap[(int) '\r'] = 'r';
        inverseControlMap[(int) '\t'] = 't';
        inverseControlMap[(int) '\\'] = '\\';
        inverseControlMap[(int) '"'] = '"';
    }

    private final boolean supportXHex = true;
    private char[] charArray;
    private int index;
    private int len;
    private String name;
    private String value;
    private StringBuilder sBuilder = new StringBuilder();

    private static String str(Object o) {
        if (o == null) {
            return "[NULL]";
        }

        if (o instanceof char[]) {
            return new String((char[]) o);
        }
        return o.toString();
    }

    public final HashMap<String, String> parseJSONToMap(String json)
            throws IOException {

        try {
            if (!init(json)) {
                return null;
            }

            if (charArray[this.index++] != OPEN_CURLY) {
                return null;
            }
            skipWhiteSpace();
            if (charArray[this.index] == CLOSED_CURLY) {
                return EMPTY_MAP;
            }
            HashMap<String, String> res = null;

            while (getName() && getObjectValue()) {
                if (res == null) {
                    res = new HashMap<String, String>();
                }
                res.put(name, value);
                switch (charArray[this.index]) {
                    case CLOSED_CURLY:
                        return res;
                    case COMMA:
                        this.index++;
                        break;
                    default:
                        return null;
                }
            }

            return res;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Unexpected failure, see cause" + this.getDebugMessage(), e);
        }
    }

    public final ArrayList<String> parseJSONToArray(String json)
            throws IOException {
        try {
            if (!init(json)) {
                return null;
            }

            if (charArray[this.index++] != OPEN_BRACKET) {
                return null;
            }
            skipWhiteSpace();
            if (charArray[this.index] == CLOSED_BRACKET) {
                return EMPTY_LIST;
            }

            ArrayList<String> res = null;

            while (getArrayValue()) {
                if (res == null) {
                    res = new ArrayList<String>();
                }
                res.add(value);

                switch (charArray[this.index]) {
                    case CLOSED_BRACKET:
                        return res;
                    case COMMA:
                        this.index++;
                        break;
                    default:
                        return null;
                }
            }
            return null;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Unexpected failure, see cause" + this.getDebugMessage(), e);
        }
    }

    private boolean init(String json) {
        if (json == null || json.length() == 0) {
            return false;
        }

        this.charArray = json.toCharArray();
        this.index = 0;
        this.len = this.charArray.length;

        skipWhiteSpace();

        return this.index < this.len - 1;
    }

    private boolean getObjectValue() throws IOException {
        value = null;
        skipWhiteSpace();
        if (charArray[index] != COLON) {
            return false;
        }
        this.index++;
        return decodeValue();
    }

    private boolean decodeValue() throws IOException {
        skipWhiteSpace();
        char c = charArray[index];
        boolean validNull = false;

        switch (c) {
            case DOUBLE_QUOTE:
                value = decodeString();
                break;
            case ALPHA_1:
            case ALPHA_2:
            case ALPHA_3:
            case ALPHA_4:
            case ALPHA_5:
            case ALPHA_6:
            case ALPHA_7:
            case ALPHA_8:
            case ALPHA_9:
            case ALPHA_0:
            case MINUS:
            case LETTER_T:
            case LETTER_F:
                value = decodeRawString();
                break;
            case LETTER_N:
                value = decodeRawString();
                if (value.equals(WORD_NULL)) {
                    value = null;
                    validNull = true;
                }
                break;
            case OPEN_BRACKET:
                value = decodeJsonArray();
                break;
            case OPEN_CURLY:
                value = decodeJsonObject();
                break;
            default:
                throw new IOException("Could not determine type" + this.getDebugMessage());
        }
        skipWhiteSpace();
        return value != null || validNull;
    }

    private boolean getArrayValue() throws IOException {
        value = null;
        return decodeValue();
    }

    private String decodeJsonArray() {
        int startPos = this.index;
        int counter = 0;
        boolean inQuote = false;
        sBuilder.setLength(0);
        for (; this.index < len; this.index++) {
            switch (charArray[this.index]) {
                case ESCAPE:
                    startPos = processEscape(startPos);
                    break;
                case DOUBLE_QUOTE:
                    inQuote = !inQuote;
                    break;
                case OPEN_BRACKET:
                    if (inQuote) {
                        continue;
                    }
                    counter++;
                    break;
                case CLOSED_BRACKET:
                    if (inQuote) {
                        continue;
                    }
                    if (--counter > 0) {
                        continue;
                    }
                    sBuilder.append(new String(this.charArray, startPos,
                            ++this.index - startPos));
                    return sBuilder.toString();

            }
        }

        return null;
    }

    private String decodeJsonObject() {
        int startPos = this.index;
        sBuilder.setLength(0);
        int counter = 0;
        boolean inQuote = false;
        for (; this.index < len; this.index++) {
            char c = charArray[this.index];
            switch (c) {
                case ESCAPE:
                    startPos = processEscape(startPos);
                    break;
                case DOUBLE_QUOTE:
                    inQuote = !inQuote;
                    break;
                case OPEN_CURLY:
                    if (inQuote) {
                        continue;
                    }
                    counter++;
                    break;

                case CLOSED_CURLY:
                    if (inQuote) {
                        continue;
                    }
                    if (--counter > 0) {
                        continue;
                    }
                    sBuilder.append(new String(this.charArray, startPos,
                            ++this.index - startPos));
                    return sBuilder.toString();
            }

        }

        return null;
    }

    private int processEscape(int startPos) {
        char c = charArray[this.index + 1];
        // forward slash escaping isn't needed, artifact of javascript
        if (c == '/') {
            sBuilder.append(new String(this.charArray, startPos,
                    this.index++ - startPos));
            sBuilder.append(c);
            startPos = this.index + 1;
            // support for \x is not normal json but we do it as Jackson does it
        } else if (!(c == 'u' || (supportXHex && c == 'x'))) {
            this.index++;
            // to simulate jackson hex encoded characters are chosen if above or below 32 and 127
        } else if (this.index + 5 < len) {
            String hex = new String(charArray, this.index + 2, 4);
            int unicode = Integer.parseInt(hex, 16);
            sBuilder.append(new String(this.charArray, startPos,
                    this.index++ - startPos));
            // need to handle the unescaping of control characters
            if (unicode < 255 && inverseControlMap[unicode] != 0) {
                sBuilder.append((char) ESCAPE);
                sBuilder.append(inverseControlMap[unicode]);
            } else if (unicode > 32 && !(unicode >= 0xD800 && unicode <= 0xDFFF)) {
                sBuilder.append((char) unicode);
            } else {
                sBuilder.append((char) ESCAPE);
                sBuilder.append(c);
                // remove this after testing, this to reduce logs
                sBuilder.append(hex.toUpperCase());
            }
            this.index += 4;
            startPos = this.index + 1;

        }
        return startPos;
    }

    private boolean getName() {
        name = decodeString();
        return name != null;
    }

    private String decodeString() {
        skipWhiteSpace();
        int startPos = ++this.index;
        sBuilder.setLength(0);
        for (; this.index < len; this.index++) {
            switch (charArray[this.index]) {
                case ESCAPE:
                    sBuilder.append(new String(this.charArray, startPos,
                            this.index++ - startPos));
                    char c = charArray[this.index];

                    if (!(c == 'u' || (supportXHex && c == 'x'))) {
                        sBuilder.append(controlMap[(int) c]);
                        startPos = this.index + 1;
                    } else if (this.index + 4 < len) {
                        String hex = new String(charArray, this.index + 1, 4);
                        char unicode = (char) Integer.parseInt(hex, 16);
                        sBuilder.append(unicode);
                        this.index += 4;
                        startPos = this.index + 1;
                    }
                    break;
                case DOUBLE_QUOTE:
                    sBuilder.append(new String(this.charArray, startPos,
                            this.index++ - startPos));
                    return sBuilder.toString();
            }
        }
        return null;
    }

    private String decodeRawString() {
        skipWhiteSpace();
        int startPos = this.index;
        sBuilder.setLength(0);
        for (; this.index < len; this.index++) {
            switch (charArray[this.index]) {
                case ESCAPE:
                    startPos = processEscape(startPos);
                    break;
                case CLOSED_CURLY:
                case CLOSED_BRACKET:
                case COMMA:
                    sBuilder.append(new String(this.charArray, startPos,
                            this.index - startPos));
                    return sBuilder.toString();
            }
        }
        return null;
    }

    private void skipWhiteSpace() {
        for (; this.index < len; this.index++) {
            if (charArray[index] != 32) {
                return;
            }
        }
    }

    private String getDebugMessage() {
        return "Length:" + str(this.len)
                + ", Index:" + str(this.index)
                + ", Name:" + str(this.name)
                + ", Value:" + str(this.value)
                + ", SBuilder:" + str(this.sBuilder)
                + ", CharArray:" + str(this.charArray);
    }

}
