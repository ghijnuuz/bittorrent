package me.gzj.bittorrent.bencode;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class BencodeEncodeTest {
    @Test
    public void encodeObject() throws Exception {
        Map data = new TreeMap<String, Object>() {{
            put("string", "value");
            put("number", 123456);
            put("list", new ArrayList<Object>() {{
                add("list-item-1");
                add("list-item-2");
            }});
            put("dict", new TreeMap<String, Object>() {{
                put("dict-item-1", "test");
                put("dict-item-2", "thing");
            }});
        }};
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        BencodeEncode encode = new BencodeEncode(output);
        encode.encodeObject(data);

        String encodeStr = "d4:dictd11:dict-item-14:test11:dict-item-25:thinge4:listl11:list-item-111:list-item-2e6:numberi123456e6:string5:valuee";
        Assert.assertEquals(encodeStr, output.toString());
    }
}
