package com.sist.r;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.Rserve.RConnection;
import org.springframework.stereotype.Component;
/*
 * 
 * 
 * */
@Component
public class RManager {
	public String tasteData() {
		String json="";
		
		try {
			RConnection rc=new RConnection();
			rc.voidEval("data<-read.table(\"/home/sist/food_data/result\")");
			REXP p=rc.eval("data$V1");
			String[] taste=p.asStrings();
			p=rc.eval("data$V2");
			int[] count=p.asIntegers();
			
			JSONArray arr=new JSONArray();
			for (int a = 0; a < taste.length; a++) {
				JSONObject obj=new JSONObject();
				obj.put("taste", taste[a]);
				obj.put("count", count[a]);
				arr.add(obj);
				
			}
			json=arr.toJSONString();
			
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		return json;
	}
}
