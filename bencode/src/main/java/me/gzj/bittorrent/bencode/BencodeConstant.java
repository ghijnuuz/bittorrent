package me.gzj.bittorrent.bencode;

import java.nio.charset.Charset;

public class BencodeConstant {
    final static Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    final static char PREFIX_NUMBER = 'i';
    final static char PREFIX_LIST = 'l';
    final static char PREFIX_DICTIONARY = 'd';
    final static char POSTFIX_END = 'e';
    final static char COLON = ':';
}
