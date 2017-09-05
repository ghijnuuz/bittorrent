package me.gzj.bittorrent.bencode;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

public class BencodeDecodeTest {
    @Test
    public void decodeObject() throws Exception {
        String input = "d4:dictd11:dict-item-14:test11:dict-item-25:thinge4:listl11:list-item-111:list-item-2e6:numberi123456e6:string5:valuee";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        BencodeDecode decode = new BencodeDecode(in);
        Map<String, Object> data = (Map) decode.decodeObject();

        Assert.assertEquals(4, data.size());

        Map<String, Object> map = (Map) data.get("dict");
        Assert.assertEquals(2, map.size());
        Assert.assertEquals("test", new String((byte[]) map.get("dict-item-1")));
        Assert.assertEquals("thing", new String((byte[]) map.get("dict-item-2")));

        List<Object> list = (List) data.get("list");
        Assert.assertEquals(2, list.size());
        Assert.assertEquals("list-item-1", new String((byte[]) list.get(0)));
        Assert.assertEquals("list-item-2", new String((byte[]) list.get(1)));

        long number = (Long) data.get("number");
        Assert.assertEquals(123456, number);

        String str = new String((byte[]) data.get("string"));
        Assert.assertEquals("value", str);
    }
}
