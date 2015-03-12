/*
 * 文件名称 : ByteConvert.java
 * <p>
 * 作者信息 : liuzongyao
 * <p>
 * 创建时间 : 2013-9-10, 下午7:34:24
 * <p>
 * 版权声明 : Copyright (c) 2009-2012 Hydb Ltd. All rights reserved
 * <p>
 * 评审记录 :
 * <p>
 */

package com.xunlei.crystalandroid.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Paint;

/**
 * 请在这里增加文件描述
 * <p>
 */
public class ConvertUtil
{
    
    // 产品要求单位都使用一个字节
    private static final long BASE_B = 1L; // 转换为字节基数
    
    private static final long BASE_KB = 1024; // 转换为KB
    
    private static final long BASE_MB = 1024 * 1024; // 转换为M的基数
    
    private static final long BASE_GB = 1024 * 1024 * 1024;
    
    private static final long BASE_TB = 1024L * 1024L * 1024L * 1024L;
    
    public static final String UNIT_BIT = "B";
    
    public static final String UNIT_KB = "KB";
    
    public static final String UNIT_MB = "MB";
    
    public static final String UNIT_GB = "GB";
    
    public static final String UNIT_TB = "TB";
    
    public static String byteConvert(long byteNum)
    {
        double tmp = byteNum / (1000 * 1024 * 1024 * 1.0);
        double f;
        String res;
        if (tmp >= 1.0)
        {
            // 大于等于1000M 用G表示
            f = byteNum / (1024 * 1024 * 1024 * 1.0);
            BigDecimal b = new BigDecimal(f);
            double doubleRes = b.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
            res = Double.toString(doubleRes) + "GB";
        }
        else
        {
            // 小于1000M
            tmp = byteNum / (1000 * 1024 * 1.0);
            if (tmp >= 1.0)
            {
                // 大于等于1000K
                f = byteNum / (1024 * 1024 * 1.0);
                BigDecimal b = new BigDecimal(f);
                double doubleRes = b.setScale(1, BigDecimal.ROUND_DOWN).doubleValue();
                res = Double.toString(doubleRes) + "MB";
            }
            else
            {
                // 小于1M
                tmp = byteNum / (1000 * 1.0);
                if (tmp >= 1.0)
                {
                    // 大于等于1000B
                    f = byteNum / (1024 * 1.0);
                    BigDecimal b = new BigDecimal(f);
                    double doubleRes = b.setScale(1, BigDecimal.ROUND_DOWN).doubleValue();
                    res = Double.toString(doubleRes) + "KB";
                }
                else
                {
                    // 小于1000B
                    res = byteNum + "B";
                }
            }
        }
        return res;
    }
    
    public static String byteConvert(long byteNum, boolean trim)
    {
        String res = byteConvert(byteNum);
        if (trim)
        {
            if (res.length() >= 7)
            {
                String original = res;
                res = original.substring(0, 4);
                if (res.endsWith("."))
                {
                    res = res.substring(0, 3);
                }
                res = res + original.substring(original.length() - 2, original.length());
            }
        }
        return res;
    }
    
    /**
     * 通过等级，算升级所需的经验值
     * 
     * @param level
     * @return
     */
    public static int levelToScore(int level)
    {
        return 50 * (level + 1) * (level + 4);
    }
    
    /**
     * 通过当前经验值算等级
     * 
     * @param score
     * @return
     */
    public static int scoreToLevel(int score)
    {
        int rank = 0;
        
        while (true)
        {
            if (score < 50 * rank * (rank + 3))
                break;
            rank += 1;
        }
        
        return rank > 1 ? rank - 1 : 0;
    }
    
    public static long stringToLong(String string)
    {
        long value = 0;
        try
        {
            value = Long.valueOf(string);
        }
        catch (Exception e)
        {
            
            value = 0;
        }
        return value;
    }
    
    public static int stringToInt(String string)
    {
        int value = 0;
        try
        {
            value = Integer.valueOf(string);
        }
        catch (Exception e)
        {
            
            value = 0;
        }
        return value;
    }
    
    /**
     * 将String类型的IP地址转换成int32类型。将192.168.11.101转换为1812703424 注：int32 高位在前，地位在后
     * 
     * @param IPAddress
     *            String类型的IP地址，如：192.168.11.101
     * @return int32类型的IP地址，如：1812703424, 如果IPAddress为null或者不合法，则返回0
     */
    public static int ipAddrToInt(String IPAddress)
    {
        if (null == IPAddress)
        {
            // throw new NullPointerException("IPAddress is null.");
            // NullPointerException 如果IPAddress为null
            return 0;
        }
        if (!IPAddress.matches("^([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}$"))
        {
            // throw new
            // RuntimeException("IPAddress is not valid...");RuntimeException
            // 如果String类型的IP地址格式不合法。
            return 0;
        }
        String[] array = IPAddress.split("\\.");
        int a = Integer.parseInt(array[0]);
        int b = Integer.parseInt(array[1]) << 8;
        int c = Integer.parseInt(array[2]) << 16;
        int d = Integer.parseInt(array[3]) << 24;
        return a | b | c | d;
    }
    
    /**
     * 速度转为字符串
     * 
     * @param speed
     *            速度，以B为单位
     * @param precision
     *            保留小数点位数
     * @return String[2] 其中ret[0]是数字 ret[1]是单位
     */
    public static String[] convertSpeeds(long speed, int precision)
    {
        String[] ret = new String[2];
        String str = convertFileSize(speed, 0);
        
        System.out.println(str);
        
        // 单位是一个字节
        String unit = str.substring(str.length() - 2);
        ret[0] = str.substring(0, str.lastIndexOf(unit));
        // ret[1] = unit + "/s";
        // 调整单位格式统一设置为B/s、KB/s、MB/s、GB/s
        if (unit.equals(UNIT_KB) || unit.equals(UNIT_MB) || unit.equals(UNIT_GB) || unit.equals(UNIT_TB))
        {
            
        }
        else
        {
            unit = str.substring(str.length() - 1);
            ret[0] = str.substring(0, str.lastIndexOf(unit));
        }
        ret[1] = unit + "/S";
        
        return ret;
    }
    
    /**
     * 小数转换为百分值
     * 
     * @param value
     *            待转换
     * @param scale
     *            小数位数 10.1% 10.01%
     * @param zeroStr
     *            0的返回值
     * @return e.g. 10.5
     */
    public static String convertPercent(float value, int scale, String zeroStr)
    {
        BigDecimal b = new BigDecimal(value * 100);
        value = b.divide(new BigDecimal(1), scale, BigDecimal.ROUND_HALF_UP).floatValue();
        if (Float.compare(value, (float) Math.pow(10, -scale)) < 0)
        {
            return zeroStr;
        }
        else
        {
            return String.valueOf(value);
        }
    }
    
    public static String convertFileSize(long file_size, int precision)
    {
        long int_part = 0;
        double fileSize = file_size;
        double floatSize = 0L;
        long temp = file_size;
        int i = 0;
        long base = 1L;
        String baseUnit = "M";
        String fileSizeStr = null;
        int indexMid = 0;
        
        while (temp / 1024 > 0)
        {
            int_part = temp / 1024;
            temp = int_part;
            i++;
            if (i == 4)
            {
                break;
            }
        }
        
        switch (i)
        {
            case 0: // B
                base = BASE_B;
                baseUnit = UNIT_BIT;
                break;
            
            case 1: // KB
                base = BASE_KB;
                baseUnit = UNIT_KB;
                break;
            
            case 2: // MB
                base = BASE_MB;
                baseUnit = UNIT_MB;
                break;
            
            case 3: // GB
                base = BASE_GB;
                baseUnit = UNIT_GB;
                break;
            
            case 4: // TB
                base = BASE_TB;
                baseUnit = UNIT_TB;
                break;
            
            default:
                break;
        }
        
        BigDecimal filesizeDecimal = new BigDecimal(fileSize);
        BigDecimal baseDecimal = new BigDecimal(base);
        floatSize = filesizeDecimal.divide(baseDecimal, precision, BigDecimal.ROUND_HALF_UP).doubleValue();
        fileSizeStr = Double.toString(floatSize);
        if (precision == 0)
        {
            indexMid = fileSizeStr.indexOf('.');
            if (-1 == indexMid)
            { // 字符串中没有这样的字符
                return fileSizeStr + baseUnit;
            }
            return fileSizeStr.substring(0, indexMid) + baseUnit;
        }
        
        // baseUnit = UNIT_BIT;
        if (baseUnit.equals(UNIT_BIT))
        {
            int pos = fileSizeStr.indexOf('.');
            fileSizeStr = fileSizeStr.substring(0, pos);
        }
        
        if (baseUnit.equals(UNIT_KB))
        {
            int pos = fileSizeStr.indexOf('.');
            if (pos != -1)
            {
                fileSizeStr = fileSizeStr.substring(0, pos + 2);
            }
            else
            {
                fileSizeStr = fileSizeStr + ".0";
            }
        }
        
        return fileSizeStr + baseUnit;
    }
    
    /**
     * 将int32类型的IP地址转换成四个8字节的String类型。
     * 
     * @param addr
     * @return
     */
    public static String ipAddressToString(int addr)
    {
        StringBuffer buf = new StringBuffer(16);
        buf.append(addr & 0xff).append('.').append((addr >>>= 8) & 0xff).append('.').append((addr >>>= 8) & 0xff).append('.').append((addr >>>= 8) & 0xff);
        return buf.toString();
    }
    
    static public int parseInt(String mEpisodeIdStr2)
    {
        int i, result = 0;
        if (null == mEpisodeIdStr2)
            return 0;
        for (i = 0; i < mEpisodeIdStr2.length(); i++)
            if ((mEpisodeIdStr2.charAt(i) >= '0') && mEpisodeIdStr2.charAt(i) <= '9')
                result = result * 10 + (mEpisodeIdStr2.charAt(i) - '0');
        return result;
    }
    
    public static int Str2Int(String strInt, int defaultValue)
    {
        if (null == strInt)
            return defaultValue;
        String newStrInt = strInt.trim();
        int result = 0;
        try
        {
            result = Integer.parseInt(newStrInt);
        }
        catch (NumberFormatException e)
        {
            result = defaultValue;
        }
        return result;
    }
    
    public static String byteConvertToSpeed(long byteNum, boolean trim)
    {
        double tmp = byteNum / (1000 * 1024 * 1024 * 1.0);
        double f;
        String res;
        if (tmp >= 1.0)
        {
            // 大于等于1000M 用G表示
            f = byteNum / (1024 * 1024 * 1024 * 1.0);
            BigDecimal b = new BigDecimal(f);
            double doubleRes = b.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
            res = Double.toString(doubleRes) + "GB";
        }
        else
        {
            // 小于1000M
            tmp = byteNum / (1000 * 1024 * 1.0);
            if (tmp >= 1.0)
            {
                // 大于等于1000K
                f = byteNum / (1024 * 1024 * 1.0);
                BigDecimal b = new BigDecimal(f);
                double doubleRes = b.setScale(1, BigDecimal.ROUND_DOWN).doubleValue();
                res = Double.toString(doubleRes) + "MB";
            }
            else
            {
                // 小于1M
                tmp = byteNum / (1000 * 1.0);
                if (tmp >= 1.0)
                {
                    // 大于等于1000B
                    f = byteNum / (1024 * 1.0);
                    BigDecimal b = new BigDecimal(f);
                    double doubleRes = b.setScale(1, BigDecimal.ROUND_DOWN).doubleValue();
                    res = Double.toString(doubleRes) + "KB";
                }
                else
                {
                    // 小于1000B
                    res = byteNum + "B";
                    trim = false;
                }
            }
        }
        if (trim)
        {
            if (res.length() >= 7)
            {
                String original = res;
                res = original.substring(0, 4);
                if (res.endsWith("."))
                {
                    res = res.substring(0, 3);
                }
                res = res + original.substring(original.length() - 2, original.length());
            }
        }
        
        return res;
    }
    
    /**
     * 将秒转化成合适的时间显示
     * 
     * @param seconds
     * @return
     */
    public static String convertFromSecToTime(long seconds)
    {
        
        StringBuilder time = new StringBuilder();
        long hour = seconds / (60 * 60);
        long min = (seconds % (60 * 60)) / (60);
        long second = (seconds % (60));
        
        String minString = (min >= 10) ? min + "" : ("0" + min);
        String secondString = (second >= 10) ? second + "" : ("0" + second);
        
        if (hour > 0)
        {
            String hourString = (hour >= 10) ? hour + "" : ("0" + hour);
            time.append(hourString).append(":").append(minString).append(":").append(secondString);
        }
        else
        {
            time.append(minString).append(":").append(secondString);
        }
        
        return time.toString();
        
    }
    
    /**
     * 转化产出
     * 
     * @param num
     * @return
     */
    public static String convertFormatNum(long num)
    {
        String formatString = "";
        
        DecimalFormat df = new DecimalFormat(",###");
        
        DecimalFormat dfl = new DecimalFormat(",###.0");
        
        // 10w以内
        if (num < 100000)
        {
            formatString = df.format(num);
        }
        // 超过10w
        else
        {
            float left = num / 10000f;
            formatString = dfl.format(left) + "万";
            
        }
        return formatString;
        
    }
    
    public static String convertFromProductTime(long seconds)
    {
        
        StringBuilder time = new StringBuilder();
        
        long hour = seconds / (60 * 60);
        long min = seconds / 60;
        
        if (hour > 0)
        {
            
            time.append(hour).append("小时");
        }
        else if (min > 0)
        {
            time.append(min).append("分钟");
        }
        
        return time.toString();
        
    }
    
    // 将十进制整数形式转换成127.0.0.1形式的ip地址
    public static String longToIP(long longIp)
    {
        StringBuffer sb = new StringBuffer("");
        
        // 将高24位置0
        sb.append(String.valueOf((longIp & 0x000000FF)));
        sb.append(".");
        
        // 将高16位置0，然后右移8位
        sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));
        sb.append(".");
        
        // 将高8位置0，然后右移16位
        sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));
        sb.append(".");
        
        // 直接右移24位
        sb.append(String.valueOf((longIp >>> 24)));
        
        return sb.toString();
    }
    
    /**
     * 四舍五入
     * 
     * @param number
     *            数字
     * @param newScale
     *            小数点后保留的位数，精度
     * @return 四舍五入后的数字
     */
    public static double round(double number, int newScale)
    {
        BigDecimal d = new BigDecimal(number);
        BigDecimal d2 = d.setScale(newScale, BigDecimal.ROUND_HALF_UP);
        return d2.doubleValue();
    }
    
    public static BigDecimal roundDown(String number, int newScale)
    {
        BigDecimal res = null;
        
        int indexOf = number.indexOf(".");
        if (indexOf != -1)
        {
            
            if (number.length() - indexOf > newScale)
            {
                String r = number.substring(0, indexOf + newScale + 1);
                res = new BigDecimal(r);
            }
            else
            {
                for (int i = number.length() - indexOf - 1; i < newScale; i++)
                {
                    number += "0";
                }
                res = new BigDecimal(number);
            }
            
        }
        else
        {
            number += ".";
            for (int i = 0; i < newScale; i++)
            {
                number += "0";
            }
            res = new BigDecimal(number);
        }
        
        return res;
    }
    
    public static String round2String(double number, int newScale)
    {
        return String.format("%." + newScale + "f", number);
    }
    
    public static String date2String(Date date)
    {
        if (null == date)
        {
            return "year-month-day hour:minite";
        }
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        return f.format(date);
    }
    
    public static float getTextWidth(Paint paint, String text)
    {
        float width = 0;
        
        if (null != text)
        {
            float[] widths = new float[text.length()];
            paint.getTextWidths(text, widths);
            for (float f : widths)
            {
                width += f;
            }
        }
        
        return width;
    }
    
    /**
     * 
     * @param d1
     * @param d2
     * @return 1 if d1 > d2, -1 if d1 < d2, 0 if d1 == d2.
     */
    public static int compare(BigDecimal d1, String d2)
    {
        
        return d1.compareTo(new BigDecimal(d2));
    }
    
    public static String timeFormat(long milliseconds)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        return sdf.format(new Date(milliseconds));
    }
    
}
