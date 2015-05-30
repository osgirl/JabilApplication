/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Classes;

/**
 *
 * @author Sowaznebrowa
 */
import mcc_crypt.Crypt;

public class Cipher extends java.lang.Object
{
	protected static String mEncrypt(String clWord, StringBuffer msg)
	{
		StringBuffer tmp1 = new StringBuffer();
		StringBuffer tmp2 = new StringBuffer();
		Crypt mccCrypt = new Crypt();

		try
		{
			// apply the algorithm
			if (mccCrypt.MccEnCrypt(clWord, tmp1) == -1)
			{
				msg.append("Problem while encrypting word '" + clWord + "' in the first process.");
				return null;
			}

			// Transform to hexadecimal string
			if (mccCrypt.hexaString(0, tmp1.toString(), tmp2) == -1)
			{
				msg.append("Problem while encrypting word '" + clWord + "' in the second process.");
				return null;
			}

			// Remove all '0X'
			for (int i=0; i<18; i++)
			{
				tmp2.replace((2*i), (2*i)+2, "");
			}
		}
		catch (Exception e)
		{
			msg.append("Cipher - Problem while encrypting data.\n" + e.getMessage());
			return null;
		}

		return tmp2.toString(); // return crypted word
	}

	protected static String mDecrypt(String crWord, StringBuffer msg)
	{
		StringBuffer tmp = new StringBuffer(crWord);
		StringBuffer tmp1 = new StringBuffer();
		StringBuffer tmp2 = new StringBuffer();
		Crypt mccCrypt = new Crypt();

		try
		{
			// Add '0X'
			for (int i=0; i<18; i++)
			{
				tmp.insert((4*i), "0X");
			}

			// Process 1 : Transform to string		
			if (mccCrypt.hexaString(1, tmp.toString(), tmp1) == -1)
			{
				msg.append("Problem while decrypting word '" + crWord + "' in the first process.");
				return null;
			}

			// Process 2 : apply the algorithm
			if (mccCrypt.MccDeCrypt(tmp1.toString(), tmp2) == -1)
			{
				msg.append("Problem while decrypting word '" + crWord + "' in the second process.");
				return null;
			}
		}
		catch(Exception e)
		{
			msg.append("kAdpCommon.kAdpCipher - Problem while decrypting data.\n" + e.getMessage());
			return null;
		}

		return tmp2.toString(); // return cleared word
	}
}
