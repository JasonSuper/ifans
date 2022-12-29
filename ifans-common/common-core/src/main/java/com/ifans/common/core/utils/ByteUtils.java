package com.ifans.common.core.utils;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class ByteUtils {

    /**
     * 将int转为高端字节序排列的byte数组（Java内存存放顺序）
     *
     * @param n
     * @return
     */
    public static byte[] int2ByteArray(int n) {
        byte[] byteArray = null;
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            DataOutputStream dataOut = new DataOutputStream(byteOut);
            dataOut.writeInt(n);
            byteArray = byteOut.toByteArray();
            Arrays.toString(byteArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    /**
     * 将int转为高字节在前，低字节在后的byte数组
     *
     * @param n int
     * @return byte[]
     */
    public static byte[] int2Hbytes(int n) {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);

        return b;
    }

    public static byte[] inttobyte(int value) {
        byte[] bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            int py = (3 - i) << 3;
            bytes[i] = (byte) (value >> py);
        }
        return bytes;
    }

    /**
     * 将int转为高字节在前，低字节在后的byte数组,一字节
     *
     * @param n int
     * @return byte[]
     */
    public static byte[] int2Hbytes1byte(int n) {
        byte[] b = new byte[1];
        b[0] = (byte) (n & 0xff);
        return b;
    }

    /**
     * 将int转为高字节在前，低字节在后的byte数组,二字节
     *
     * @param n int
     * @return byte[]
     */
    public static byte[] int2Hbytes2byte(int n) {
        byte[] b = new byte[2];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);

        return b;
    }

    /**
     * 将short转为高字节在前，低字节在后的byte数组
     *
     * @param n short
     * @return byte[]
     */
    public static byte[] short2Hbytes(short n) {
        byte[] b = new byte[2];
        b[1] = (byte) (n & 0xff);
        b[0] = (byte) (n >> 8 & 0xff);
        return b;
    }

    /**
     * 以下 是整型数 和 网络字节序的 byte[] 数组之间的转换
     *
     * @param n
     * @return
     */
    public static byte[] long2Hbytes(long n) {
        byte[] b = new byte[8];
        b[7] = (byte) (n & 0xff);
        b[6] = (byte) (n >> 8 & 0xff);
        b[5] = (byte) (n >> 16 & 0xff);
        b[4] = (byte) (n >> 24 & 0xff);
        b[3] = (byte) (n >> 32 & 0xff);
        b[2] = (byte) (n >> 40 & 0xff);
        b[1] = (byte) (n >> 48 & 0xff);
        b[0] = (byte) (n >> 56 & 0xff);
        return b;
    }

    /**
     * 6字节
     * 高位在前，低位在后
     *
     * @param n
     * @return
     */
    public static byte[] long2H6bytes(long n) {
        byte[] b = new byte[6];
        b[5] = (byte) (n & 0xff);
        b[4] = (byte) (n >> 8 & 0xff);
        b[3] = (byte) (n >> 16 & 0xff);
        b[2] = (byte) (n >> 24 & 0xff);
        b[1] = (byte) (n >> 32 & 0xff);
        b[0] = (byte) (n >> 40 & 0xff);
        return b;
    }

    /**
     * 4字节
     * 高位在前，低位在后
     *
     * @param n
     * @return
     */
    public static byte[] long2H4bytes(long n) {
        byte[] b = new byte[4];
        b[3] = (byte) (n & 0xff);
        b[2] = (byte) (n >> 8 & 0xff);
        b[1] = (byte) (n >> 16 & 0xff);
        b[0] = (byte) (n >> 24 & 0xff);
        return b;
    }

    /**
     * 4字节
     * 低位在前，高位在后
     *
     * @param n
     * @return
     */
    public static byte[] unlong2H4bytes(long n) {
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return b;
    }


    /**
     * 合并数组
     *
     * @param first
     * @param rest
     * @return
     */
    public static byte[] concatBytes(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            if (null != array) {
                totalLength += array.length;
            }
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            if (null != array) {
                System.arraycopy(array, 0, result, offset, array.length);
                offset += array.length;
            }
        }
        return result;
    }

    /**
     * byte数组转为十六进制字符串
     *
     * @param bytes
     * @return
     */
    public static String byte2Hex(byte[] bytes) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xff & bytes[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * byte数组转为十六进制字符串 并反转
     *
     * @param bytes
     * @return
     */
    public static String byte2HexReverse(byte[] bytes) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xff & bytes[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.reverse().toString();
    }

    /**
     * Convert hex string to byte[]
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hex2Byte(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 输入流转为字节数组
     */
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    /**
     * bytes字符串转换为Byte值
     * src Byte字符串，每个Byte之间没有分隔符
     *
     * @return byte[]
     */
    public static byte[] hexStr2Bytes(String src) {
        int m = 0, n = 0;
        int l = src.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            m = i * 2 + 1;
            n = m + 1;
            ret[i] = Byte.decode("0x" + src.substring(i * 2, m) + src.substring(m, n));
        }
        return ret;
    }

    /**
     * 字符串转换成十六进制字符串
     * s 为待转换的ASCII字符串
     */
    public static String str2HexStr(String s) {

        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }

    /**
     * 16进制的字符串转byte数组
     *
     * @param src
     * @return
     */
    public static byte[] Str16toBytes(String src) {
        int w = 0;
        byte[] bytes_2 = new byte[src.length() / 2];
        for (int i = 0; i < src.length(); i++) {
            String zz = src.substring(i, i + 2);
            byte aaa = (byte) Integer.parseInt(zz, 16);
            bytes_2[w] = aaa;
            i++;

            w = w + 1;
        }

        return bytes_2;
    }

    /**
     * 16进制转10进制数字
     *
     * @param src
     * @return
     */
    public static int Str16toint10(String src) {
        int h = Integer.parseInt(src, 16);
        return h;
    }

    /**
     * 从16进制字符串中获取2进制为1的序列号
     *
     * @param src
     * @return
     */
    public static ArrayList Str16Get1(String src) {
        byte[] b_p = Str16toBytes(src);
        ArrayList list = new ArrayList<>();
        int w = 0;
        for (int i = 0; i < src.length() / 2; i++) {
            for (int j = 7; j >= 0; j--) {
                if ((b_p[i] & (1 << j)) != 0) {
                    int pp = i * 8 + 8 - j;
                    list.add(w, pp);
                    w = w + 1;
                } else {

                }
            }

        }
        return list;
    }

    /**
     * 异或校验
     *
     * @param bytes
     * @return
     */
    public static byte[] bytesXorCrc(byte[] bytes) {
        byte[] crc = new byte[1];// 异或校验
        crc[0] = bytes[0];
        for (int i = 1; i < bytes.length; i++) {
            crc[0] ^= bytes[i];
        }
        return crc;
    }

    /**
     * 字节码转换成十六进制字符串
     *
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v).toUpperCase();

            if (hv.length() < 2) {
                stringBuilder.append(0);
            }

            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 十六进制字符串转换成字节码
     *
     * @param hex
     * @return
     */
    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (charToByte(achar[pos]) << 4 | charToByte(achar[pos + 1]));
        }
        return result;
    }

    /**
     * 得到十进制数字的十六进制字符串
     *
     * @param number 十进制数字
     * @param size   十六进制字符串位数
     * @return
     */
    public static String getHexStringByNumber(long number, int size) {
        StringBuilder buf = new StringBuilder(size * 2);

        for (int i = 0; i < size; i++) {
            buf.append(String.format("%02X", new Long((number & (0xff << (i * 8))) >> (i * 8))));
        }
        return buf.toString();
    }

    /**
     * 计算列表aList相对于bList的增加的情况，兼容任何类型元素的列表数据结构
     *
     * @param aList 本列表
     * @param bList 对照列表
     * @return 返回增加的元素组成的列表
     */
    public static <E> List<E> getAddaListThanbList(List<E> aList, List<E> bList) {
        List<E> addList = new ArrayList<E>();
        for (int i = 0; i < aList.size(); i++) {
            if (!myListContains(bList, aList.get(i))) {
                addList.add(aList.get(i));
            }
        }
        return addList;
    }

    /**
     * 计算列表aList相对于bList的减少的情况，兼容任何类型元素的列表数据结构
     *
     * @param aList 本列表
     * @param bList 对照列表
     * @return 返回减少的元素组成的列表
     */
    public static <E> List<E> getReduceaListThanbList(List<E> aList, List<E> bList) {
        List<E> reduceaList = new ArrayList<E>();
        for (int i = 0; i < bList.size(); i++) {
            if (!myListContains(aList, bList.get(i))) {
                reduceaList.add(bList.get(i));
            }
        }
        return reduceaList;
    }

    /**
     * 判断元素element是否是sourceList列表中的一个子元素
     *
     * @param sourceList 源列表
     * @param element    待判断的包含元素
     * @return 包含返回 true，不包含返回 false
     */
    private static <E> boolean myListContains(List<E> sourceList, E element) {
        if (sourceList == null || element == null) {
            return false;
        }
        if (sourceList.isEmpty()) {
            return false;
        }
        for (E tip : sourceList) {
            if (element.equals(tip)) {
                return true;
            }
        }
        return false;
    }

//    public static String merge(String str1,String str2){
// String str = str1 + str2;//合并
// System.out.println(str);
// for (int i = 0; i < str.length(); i++) {
// char c = str.charAt(i);
// for (int j = i+1;j<str.length();j++) {
// char cc = str.charAt(j);
// if (c == cc) {
// str = str.substring(0, j) + str.substring(j + 1);
// j--;
// }
// }
// }
// return str;
// }

    public static String getSignTags(String tagsOld, String tagsNew) {
        Set<String> hashSet = null;
        String resultTags = tagsNew + "," + tagsOld;
        String result = "";
        if (resultTags.contains(",")) {
            String[] split = resultTags.split(",");
            hashSet = new LinkedHashSet<>(Arrays.asList(split));
        }
        if (null != hashSet) {
            result = hashSet.stream().collect(Collectors.joining(","));
        }
        return result;
    }

    public static String addComma(String purview) {

        purview = purview.replace(",", "");
        //String Url = "ssssss";
        StringBuffer temp = new StringBuffer();
        for (int i = 0; i < purview.length(); i++) {
            temp.append(purview.charAt(i) + ",");

        }
        purview = temp.substring(0, temp.length() - 1);
        return purview;
    }

    public static String decimalToBinary(int num, int size) {
        if (size < (Integer.SIZE - Integer.numberOfLeadingZeros(num))) {
            throw new RuntimeException("传入size小于num二进制位数");
        }
        StringBuilder binStr = new StringBuilder();
        for (int i = size - 1; i >= 0; i--) {
            binStr.append(num >>> i & 1);
        }
        return binStr.toString();
    }

    public static byte[] long2Hbytes1byte(long n) {
        byte[] b = new byte[1];
        b[0] = (byte) (n & 0xff);
        return b;
    }

    public static long binary2To10(String binary) {
        //先补足64位进行计算
        StringBuffer sb = new StringBuffer(binary);
        for (int i = 0; i < (64 - binary.length()); i++) {
            sb.insert(0, "0");
        }
        char[] in = sb.toString().toCharArray();
        char[] usea = new char[in.length];
        //首位等于1则为负数,需要取反
        if (in[0] == '1') {
            for (int i = 0; i < in.length; i++) {
                if (in[i] == '1') {
                    usea[i] = '0';
                } else {
                    usea[i] = '1';
                }
            }
        } else {
            usea = in;
        }
        long count = 0;
        for (int i = 0; i < usea.length; i++) {
            count += (long) ((int) ((int) usea[i] - (int) ('0')) * Math.pow(2, (usea.length - 1) - i));
        }
        //首位等于1则为负数, 需要加1再取负数
        if (in[0] == '1') {
            count = -(count + 1);
        }
        return count;
    }

    public static String int2HexStr(Integer n) {

        String str = Integer.toHexString(n);
        while (str.length() < 18) {
            str = "0" + str;
        }
        return str.toUpperCase();
    }

    public static String int2HexStr(String str) {
        while (str.length() < 18) {
            str = "0" + str;
        }
        return str.toUpperCase();
    }

    public static byte[] unlong2H2bytes(long n) {
        byte[] b = new byte[2];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        return b;
    }

    /**
     * 字符串转换成16进制
     *
     * @param str
     * @return
     */
    public static String convertStringToHex(String str) {
        char[] chars = str.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString();
    }

    public static byte[] hexDecode(String hexStr) {
        if (hexStr == null || "".equals(hexStr)) {
            return null;
        }
        try {
            char[] cs = hexStr.toCharArray();
            return Hex.decodeHex(cs);
        } catch (DecoderException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static final char[] CHAR16 = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * @param n
     * @Title: intTohex
     * @Description: int型转换成16进制
     * @return: String
     */
    public static String intToHex(int n) {
        StringBuffer s = new StringBuffer();
        String a;
        while (n != 0) {
            s = s.append(CHAR16[n % 16]);
            n = n / 16;
        }
        a = s.reverse().toString();
        if ("".equals(a)) {
            a = "00";
        }
        if (a.length() == 1) {
            a = "0" + a;
        }
        return a;
    }
}
