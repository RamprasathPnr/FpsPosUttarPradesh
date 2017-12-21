/*
package com.omneagate.activity;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;

import com.finger.interfacelib.ZAZ_api;

import android_serialport_api.sample.IDCard;


public class ZAZAPIW {
	 
	public static ZAZ_api ZAZREAD = null;
	public static IDCard idcard = null; 
	public byte[] G_TemplateA = new byte[1024];
	public byte[] G_TemplateB = new byte[1024];
	public byte[] G_binImage = new byte[256*360];
	public int[]  G_nWidth = new int[1];
	public int[]  G_nHeight = new int[1];
	public int[]  G_fptype = new int[1];
	public byte[] G_gMBIndex = new byte[(17 + 1024 + 2 + 10)];
	public int[]  G_fpcount = new int[1];
	public int[]  G_erollcount = new int[1];
	public int[]  G_empty = new int[1];
	
	
	public ZAZAPIW(Activity ACss,int  fptype,int sfztype)
	{  	G_fptype[0]=fptype; 	
		ZAZREAD = new ZAZ_api(ACss, G_fptype, sfztype);
	}   
	//JAR ��汾
	public String LIB_Version()
	{
		return ZAZREAD.LIB_Version();
	}
	//ָ�ư汾
	public String ZAZ_Version()
	{ 
		return ZAZREAD.ZAZ_Version();
	}
	/*/
/****************电源管理�?********************//*
/
//	public int card_power_on() {
//		return ZAZREAD.card_power_on();
//	} 
//	public int card_power_off() {
//		return ZAZREAD.card_power_off();
//	} 
//	public int finger_power_on() {
//		return ZAZREAD.finger_power_on();
//	} 
//	public int finger_power_off() {
//		return ZAZREAD.finger_power_off();
//	}
	//////////////////////////////////////////////////////////// 
	
	/*/
/****************身份证操作类*********************//*
/
	public boolean InitIDCardDevice(Object args ) {
		return ZAZREAD.InitIDCardDevice(null); 
	}
	public boolean CloseIDCardDevice(Object args) {
		return ZAZREAD.CloseIDCardDevice(null);
	}
	public IDCard ReadCard(){	
		return ZAZREAD.ReadCard( );
	}
	public String getIDbaseyinan() //华视无此接口
	{	return ZAZREAD.getIDbaseyinan();
	}
	public Bitmap getPhotoBmp() 
	{	return ZAZREAD.getPhotoBmp( );
	} 
	public boolean FindIDCard()
	{
		return ZAZREAD.FindIDCard( );
		
	}
	public boolean SelectCard()
	{
		return ZAZREAD.SelectCard( );	
	}
	public IDCard readCard()
	{
		return ZAZREAD.readCard( );	
	}
	public int getIDbaseshenzhen(byte[] cardno)
	{	return ZAZREAD.getIDbaseshenzhen( cardno);	
		 
	}
//	public String Read_ICSFZ_PHYIDNumber()
//	{
//		return ZAZREAD.Read_ICSFZ_PHYIDNumber( );		
//	}
//	public String Read_SFZ_PHYIDNumber()
//	{
//		return ZAZREAD.Read_SFZ_PHYIDNumber( );		
//	}
//	
	//////////////////////////////////////////////////////////// 
	/*/
/***************×指纹操作�?********************//*
/
	public boolean Init_Fp()
   {    
		return ZAZREAD.Init_Fp( ); 
    }
	public boolean UnInit_Fp()
	{    
		return ZAZREAD.UnInit_Fp( ); 
	}
	public boolean IsFingerPress()
	{   
		return ZAZREAD.IsFingerPress( );
	}
	public int UpFpimage(byte[]  m_binImage,int[] w_nWidth,int[] w_nHeight)
	{   
		return ZAZREAD.UpFpimage(m_binImage,w_nWidth,w_nHeight);
	}
	public int ImageQuality(byte[]  m_binImage,int[] w_nWidth,int[] w_nHeight)
	{ 
		return ZAZREAD.ImageQuality(m_binImage,w_nWidth,w_nHeight); 
	}
	public void showimg(byte[]  m_binImage,int[] w_nWidth,int[] w_nHeight,Bitmap[] image )
	{   
		ZAZREAD.showimg(m_binImage,w_nWidth,w_nHeight,image);
	}
	public int  CreateTemplate(byte[] m_binImage,
		       int w_nWidth, int w_nHeight, byte[] p_pTemplate)
	{ 
		return ZAZREAD.CreateTemplate(m_binImage, w_nWidth, w_nHeight, p_pTemplate);
	}
	public int  Match(byte[] p_pTemplate1, byte[] p_pTemplate2, int[] resore)
	{  
		return ZAZREAD.Match(p_pTemplate1, p_pTemplate2, resore);
	}
	public int  ImgMatch(byte[] m_binImage,  byte[] p_pTemplate,int[] resore)
	{  
		return ZAZREAD.ImgMatch(m_binImage, p_pTemplate , resore);
	}
	public int Search(byte[] MatchTemplate, Object TemplateDB,
			Object p_nMatchCount, int[] p_pValidInfo  ,int[] p_pnMatchedID) {
		return ZAZREAD.Search(MatchTemplate, TemplateDB , p_nMatchCount,p_pValidInfo,p_pnMatchedID);
	}
	public int GetFBdata(byte[] gMBIndex,int[] fpcount,int[] erollcount,int[] empty )
	{   return ZAZREAD.GetFBdata(gMBIndex,fpcount,erollcount ,empty );
	}
	public int SaveFP(byte[] TemplateA,byte[] TemplateB, int[] TemplateDB,int[] p_nMatchCount, int[] p_pValidInfo)
	{  
		return ZAZREAD.SaveFP( TemplateA,TemplateB,TemplateDB,p_nMatchCount,p_pValidInfo);
	}
	public int DelFp(int Fpid)
	{   
		return ZAZREAD.DelFp(Fpid);
	}
	public int EmptyFp( )
	{ 
		return ZAZREAD.EmptyFp( );
	}
	public String  ReadParameters()
	{   
		return ZAZREAD.ReadParameters( );
	}
	////////////////////////////////////////////////////////////
		
		
	/*/
/***************×指昂数据�?********************//*
/
	public int DB_Add_user(String fpdata, String finger, String userlifeend,
			String userlifebegin, String grantdept, String idcardno,
			String address, String birthday, String nation, String sex,
			String name)
	{   
		return ZAZREAD.DB_Add_user(fpdata, finger, userlifeend, userlifebegin, grantdept, idcardno, address, birthday, nation, sex, name);
	} 
	public boolean DB_Find_User(String name) 
	{   
		return ZAZREAD.DB_Find_User(name); 
	}
	public boolean  DB_Dll_User(String name)
	{  
		return ZAZREAD.DB_Dll_User(name);
	}
	public boolean DB_DLLALL_User()
	{  
		return ZAZREAD.DB_DLLALL_User();
	}
	public void DB_Get_User(IDCard idcard)
	{   
		ZAZREAD.DB_Get_User(idcard);
	}
    ////////////////////////////////////////////////////////////
		 
	
	/*/
/****************摄像头类*********************//*
/
	public void Savephoto( String fileName,Intent data,Bitmap[] bmap )
	{   
		ZAZREAD.Savephoto(fileName,data, bmap);
	}
}

 
*/
