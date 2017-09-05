package me.gzj.bittorrent.bencode;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class BencodeEncode {
    private final OutputStream out;
    private final Charset charset;

    public BencodeEncode(OutputStream out, Charset charset) throws NullPointerException {
        if (out == null) {
            throw new NullPointerException("out cannot be null.");
        }
        if (charset == null) {
            throw new NullPointerException("charset cannot be null.");
        }

        this.out = out;
        this.charset = charset;
    }

    public BencodeEncode(OutputStream out) throws NullPointerException {
        this(out, BencodeConstant.DEFAULT_CHARSET);
    }

    private byte[] encode2Bytes(final byte[] b) throws NullPointerException {
        if (b == null) {
            throw new NullPointerException();
        }
        byte[] pBytes = String.format("%d%s", b.length, BencodeConstant.COLON).getBytes(charset);
        return ArrayUtils.addAll(pBytes, b);
    }

    private byte[] encode2Bytes(final String s) throws NullPointerException {
        if (s == null) {
            throw new NullPointerException();
        }
        byte[] sBytes = s.getBytes(charset);
        byte[] pBytes = String.format("%d%s", sBytes.length, BencodeConstant.COLON).getBytes(charset);
        return ArrayUtils.addAll(pBytes, sBytes);
    }

    private byte[] encode2Bytes(final Number n) throws NullPointerException {
        if (n == null) {
            throw new NullPointerException();
        }
        return String.format("%s%d%s", BencodeConstant.PREFIX_NUMBER, n.longValue(), BencodeConstant.POSTFIX_END).getBytes(charset);
    }

    private byte[] encode2Bytes(final Iterable<?> l) throws NullPointerException, IOException {
        if (l == null) {
            throw new NullPointerException();
        }
        byte[] bytes = String.valueOf(BencodeConstant.PREFIX_LIST).getBytes(charset);
        for (Object o : l) {
            bytes = ArrayUtils.addAll(bytes, encodeObject2Bytes(o));
        }
        bytes = ArrayUtils.addAll(bytes, String.valueOf(BencodeConstant.POSTFIX_END).getBytes(charset));
        return bytes;
    }

    private byte[] encode2Bytes(final Map<?, ?> m) throws NullPointerException, IOException {
        if (m == null) {
            throw new NullPointerException();
        }

        Map<?, ?> map;
        if (m instanceof SortedMap<?, ?>) {
            map = m;
        } else {
            map = new TreeMap<Object, Object>(m);
        }

        byte[] bytes = String.valueOf(BencodeConstant.PREFIX_DICTIONARY).getBytes(charset);
        for (Map.Entry<?, ?> e : map.entrySet()) {
            if (!(e.getKey() instanceof String)) {
                throw new IOException();
            }
            bytes = ArrayUtils.addAll(bytes, encode2Bytes((String) e.getKey()));
            bytes = ArrayUtils.addAll(bytes, encodeObject2Bytes(e.getValue()));
        }
        bytes = ArrayUtils.addAll(bytes, String.valueOf(BencodeConstant.POSTFIX_END).getBytes(charset));

        return bytes;
    }

    private byte[] encodeObject2Bytes(final Object o) throws NullPointerException, IOException {
        if (o == null) {
            throw new NullPointerException();
        }

        if (o instanceof byte[]) {
            return encode2Bytes((byte[]) o);
        } else if (o instanceof String) {
            return encode2Bytes((String) o);
        } else if (o instanceof Number) {
            return encode2Bytes((Number) o);
        } else if (o instanceof Iterable<?>) {
            return encode2Bytes((Iterable<?>) o);
        } else if (o instanceof Map<?, ?>) {
            return encode2Bytes((Map<?, ?>) o);
        } else {
            throw new IOException();
        }
    }

    public void encodeObject(final Object o) throws NullPointerException, IOException {
        out.write(encodeObject2Bytes(o));
    }
}
