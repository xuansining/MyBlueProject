package com.example.myblueproject.bluetooth;
//信号处理
public class OpenBCIDataConversion {
	
	final static float fs_Hz = 250.0f;  //sample rate used by OpenBCI board...set by its Arduino code//抽样
	final static float ADS1299_Vref = 4.5f;  //reference voltage for ADC in ADS1299.  set by its hardware//参考电压
	final static float ADS1299_gain = 24;  //assumed gain setting for ADS1299.  set by its Arduino code
	final static float scale_fac_uVolts_per_count = (float) (ADS1299_Vref / (Math.pow(2,23)-1) / ADS1299_gain  * 1000000.f); 
	  
	// this function is passed a 3 byte array
	static int interpret24bitAsInt32(byte[] byteArray) {     //把三个小样字节合成一起
	    //little endian
	    int newInt = ( 
	      ((0xFF & byteArray[0]) << 16) |
	      ((0xFF & byteArray[1]) << 8) | 
	      (0xFF & byteArray[2])
	      );
	    if((newInt & 0x00800000) > 0){
	      newInt |= 0xFF000000;
	    }else{
	      newInt &= 0x00FFFFFF;
	    }
	    return newInt;
	  }
	
	static float convertByteToMicroVolts(byte[] byteArray){//把字节转化成微伏
		return scale_fac_uVolts_per_count * interpret24bitAsInt32(byteArray);
	}
}
