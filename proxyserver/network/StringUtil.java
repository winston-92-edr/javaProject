package com.mynet.proxyserver.network;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.StringTokenizer;

public class StringUtil
{
    public static String[] splitTwo(String inputString, char tok)
    {
        String split1 = "", split2 = "";
        int index = 0;
        if ((index = inputString.indexOf(tok)) != -1)
        {
            split1 += inputString.substring(0, index);
            split2 += inputString.substring(index + 1);
        }
        else
            split1 += inputString;
        return new String[] { split1, split2 };
    }

    public static String[] splitTwoAfterN(int n, String inputString, char tok)
    {
        String split1 = "", split2 = "";
        String[] arr;
        split2 = inputString;
        for (int i = 0; i < n; i++)
        {
            if (split2.indexOf(tok) != -1)
            {
                arr = splitTwo(split2, tok);
                if (i != n - 1)
                    split1 += arr[0] + tok;
                else
                    split1 += arr[0];
                split2 = arr[1];
            }
            else
                break;
        }
        return new String[] { split1, split2 };
    }

    public static String[] processRawString(String rawString, String delim) throws Exception
    {
        StringTokenizer tokens = new StringTokenizer(rawString, delim);
        String[] tokArr = new String[tokens.countTokens()];
        for (int i = 0; i < tokArr.length; ++i)
            tokArr[i] = tokens.nextToken();
        return tokArr;
    }

    public static String correctTurkish(String str)
    {
        String c = str;

        c = c.replace("ç", "]c[");
        c = c.replace("ı", "]i[");
        c = c.replace("ğ", "]g[");
        c = c.replace("ş", "]s[");
        c = c.replace("ü", "]u[");
        c = c.replace("ö", "]o[");
        c = c.replace("Ç", "]C[");
        c = c.replace("İ", "]I[");
        c = c.replace("Ğ", "]G[");
        c = c.replace("Ş", "]S[");
        c = c.replace("Ü", "]U[");
        c = c.replace("Ö", "]O[");
        c = c.replace("ß", "]B[");
        return c;
    }

    public static String normalTurkish(String str)
    {
        String c = str;
        c = c.replace("]c[","ç");
        c = c.replace("]i[","ı");
        c = c.replace("]g[","ğ");
        c = c.replace("]s[","ş");
        c = c.replace("]u[","ü");
        c = c.replace("]o[","ö");
        c = c.replace("]C[","Ç");
        c = c.replace("]I[","İ");
        c = c.replace("]G[","Ğ");
        c = c.replace("]S[","Ş");
        c = c.replace("]U[","Ü");
        c = c.replace("]O[","Ö");
        c = c.replace("]B[","ß");
        return c;
    }

    public static String getHashCodeString(String input)
    {
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < input.length(); i++)
        {
            int hashCode = new Character(input.charAt(i)).hashCode();
            if (i == input.length() - 1)
                temp.append(hashCode);
            else
                temp.append(hashCode).append("&");
        }
        return temp.toString();
    }




//	private static String removeWildChar( String wordN )
//	{
//		String newWord = "";
//
//		StringTokenizer token =
//		return newWord;
//	}

    /**
     * from: http://propaso.com/blog/?cat=6
     * @param str
     * @return
     */
    public static byte[] hexToBytes(String str)
    {
        if (str == null)
        {
            return null;
        }
        else if (str.length() < 2)
        {
            return null;
        }
        else
        {
            int len = str.length() / 2;
            byte[] buffer = new byte[len];
            for (int i = 0; i < len; i++)
            {
                buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
            }
            return buffer;
        }
    }

    /**
     * from: http://propaso.com/blog/?cat=6
     * @param str
     * @return
     */
    public static String AESDecrypt(String decryptedText, String secKey, String iv) throws Exception
    {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(secKey.getBytes(), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] outText = cipher.doFinal(hexToBytes(decryptedText));
        return new String(outText).trim();
    }
}
