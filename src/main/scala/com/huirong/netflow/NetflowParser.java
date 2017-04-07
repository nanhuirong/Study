package com.huirong.netflow;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * 
 * @author yaoxin   
 * 
 * 2015年11月27日
 */
public class NetflowParser implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4705134788260426128L;
	
	// public final static double ROUND_FACTOR_THRESHOLD = 0.1;
	public final static int HOUR_OFFSITE = -7;
	public final static int MINUTES_OFFSITE = -53;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public NetflowRecord parse(String text){
		String [] splitted = text.split(" ");
		
		if(isLegalInput(splitted) && !splitted[0].contains("Date") && !splitted[0].contains("Time") 
				&& !splitted[0].contains("Total") && !splitted[0].contains("Sys") && !splitted[0].contains("Summary")){
			 
			String [] fields = new String[14];
			int length = splitted.length;
			int counter = 0;
			for(int i = 0;i < length;i ++){
				
				String content = splitted[i].trim();
				if(content != null && content.length() > 0 && !content.contains("->")){
					if(content.equals("M"))
						fields[counter - 1] = String.valueOf((long)(1024L * Double.parseDouble(fields[counter - 1])));
					else if(content.equals("G")){
						fields[counter - 1] = String.valueOf((long)(1024L * 1024 * Double.parseDouble(fields[counter - 1])));
					}
					else{
						fields[counter] = splitted[i];
						counter ++;
					}
				}
			}
			
			try {
				
				String [] srcIp = fields[4].split(":");
				String [] dstIp = fields[5].split(":");
				
				String date = fields[0] + " " + fields[1];
				date = date.substring(0, 19);
				
				NetflowRecord rc = new NetflowRecord(date, Double.parseDouble(fields[2]),fields[3],srcIp[0],srcIp[1]
						,dstIp[0],dstIp[1],fields[6],fields[7],Integer.parseInt(fields[8]),Long.parseLong(fields[9]),
						Integer.parseInt(fields[10]),Long.parseLong(fields[11]),Long.parseLong(fields[12]),
						1);
				
				// 构造精确到分钟的时间窗口字段
				rc.setTimeFrame(date.substring(0, 16) + ":00");
				
				return rc;
				
			} catch (Exception e) {
				e.printStackTrace();
				try {
					// 输出错误日志, 后期考虑用logback等日志工具实现 : )
					BufferedWriter bw = new BufferedWriter(new FileWriter("/home/yaoxin/logs/storm/debug.txt", true));
					bw.write(e.getMessage() + "\n");
					bw.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
			
		}
		
		return null;
	}
	
	public static boolean isLegalInput(String[] ls){
		if(ls == null)
			return false;
		
		int counter = 0;
		String last = "";
		for(String s : ls){
			String content = s.trim();
			if(content != null && content.length() > 0){
				counter ++;
				last = content;
			}
		}
		
		return counter >= 15 && counter <= 17 && last.equals("1");
	}
	
	public void adjustTimeField(NetflowRecord rc){
		String[] tmp1 = rc.getDate().split(" ");
		
		String[] tmp2 = tmp1[0].split("-");
		String[] tmp3 = tmp1[1].split(":");
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.parseInt(tmp2[0]), Integer.parseInt(tmp2[1]) - 1, Integer.parseInt(tmp2[2]),
				Integer.parseInt(tmp3[0]), Integer.parseInt(tmp3[1]), Integer.parseInt(tmp3[2]));
		
		calendar.add(Calendar.MINUTE, MINUTES_OFFSITE);
		calendar.add(Calendar.HOUR, HOUR_OFFSITE);
		
		String time = sdf.format(calendar.getTime());
		
		rc.setDate(time);
		rc.setTimeFrame(time.substring(0, 16) + ":00");
		
	}
}
