package me.gzj.bittorrent.bencode;

import java.io.*;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BencodeDecode {
    private final PushbackInputStream in;
    private final Charset charset;

    private final static int EOF = -1;

    public BencodeDecode(final InputStream in, final Charset charset) throws NullPointerException {
        if (in == null) {
            throw new NullPointerException("in cannot be null.");
        }
        if (charset == null) {
            throw new NullPointerException("charset cannot be null.");
        }

        this.in = new PushbackInputStream(in);
        this.charset = charset;
    }

    public BencodeDecode(final InputStream in) throws NullPointerException {
        this(in, BencodeConstant.DEFAULT_CHARSET);
    }

    private int peek() throws IOException {
        int b = this.in.read();
        in.unread(b);
        return b;
    }

    private void checkEOF(final int b) throws EOFException {
        if (b == EOF) {
            throw new EOFException();
        }
    }

    private byte[] decodeBytes() throws IOException, EOFException, NumberFormatException {
        int b = in.read();
        checkEOF(b);
        if (!Character.isDigit(b)) {
            throw new IOException();
        }

        StringBuilder builder = new StringBuilder();
        builder.append((char) b);
        while (true) {
            b = in.read();
            checkEOF(b);
            if (b == BencodeConstant.COLON) {
                break;
            }
            builder.append((char) b);
        }
        int length = Integer.parseInt(builder.toString());

        byte[] bytes = new byte[length];
        int readLength = in.read(bytes);
        if (length != readLength) {
            throw new EOFException();
        }

        return bytes;
    }

    private String decodeString() throws IOException, EOFException, NumberFormatException {
        return new String(decodeBytes(), charset);
    }

    private Long decodeNumber() throws IOException, EOFException, NumberFormatException {
        int b = in.read();
        checkEOF(b);
        if (b != BencodeConstant.PREFIX_NUMBER) {
            throw new IOException();
        }

        StringBuilder builder = new StringBuilder();
        while (true) {
            b = in.read();
            checkEOF(b);
            if (b == BencodeConstant.POSTFIX_END) {
                break;
            }
            builder.append((char) b);
        }
        return Long.parseLong(builder.toString());
    }

    private List<Object> decodeList() throws IOException, EOFException, NumberFormatException {
        int b = in.read();
        checkEOF(b);
        if (b != BencodeConstant.PREFIX_LIST) {
            throw new IOException();
        }

        List<Object> result = new LinkedList<>();
        while (true) {
            b = peek();
            checkEOF(b);
            if (Character.isDigit(b)) {
                byte[] bytes = decodeBytes();
                result.add(bytes);
            } else if (b == BencodeConstant.PREFIX_NUMBER) {
                long number = decodeNumber();
                result.add(number);
            } else if (b == BencodeConstant.PREFIX_LIST) {
                List<Object> list = decodeList();
                result.add(list);
            } else if (b == BencodeConstant.PREFIX_DICTIONARY) {
                Map<String, Object> dictionary = decodeDictionary();
                result.add(dictionary);
            } else if (b == BencodeConstant.POSTFIX_END) {
                b = in.read();
                break;
            } else {
                throw new IOException();
            }
        }
        return result;
    }

    private Map<String, Object> decodeDictionary() throws IOException, EOFException, NumberFormatException {
        int b = in.read();
        checkEOF(b);
        if (b != BencodeConstant.PREFIX_DICTIONARY) {
            throw new IOException();
        }

        Map<String, Object> result = new TreeMap<>();
        while (true) {
            b = peek();
            checkEOF(b);
            if (b == BencodeConstant.POSTFIX_END) {
                b = in.read();
                break;
            }
            result.put(decodeString(), decodeObject());
        }
        return result;
    }

    public Object decodeObject() throws IOException, EOFException, NumberFormatException {
        int b = peek();
        checkEOF(b);
        if (Character.isDigit(b)) {
            return decodeBytes();
        } else if (b == BencodeConstant.PREFIX_NUMBER) {
            return decodeNumber();
        } else if (b == BencodeConstant.PREFIX_LIST) {
            return decodeList();
        } else if (b == BencodeConstant.PREFIX_DICTIONARY) {
            return decodeDictionary();
        } else {
            throw new IOException();
        }
    }
}
