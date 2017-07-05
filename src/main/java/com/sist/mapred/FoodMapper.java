package com.sist.mapred;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.*;

public class FoodMapper extends Mapper<LongWritable, Text, Text, IntWritable>{
	private static final IntWritable one=new IntWritable(1);
	private Text result=new Text();
	
	@Override
	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, IntWritable>.Context context)
			throws IOException, InterruptedException {
		
		FileReader fr=new FileReader("/home/sist/food_data/food_directory");
		int i=0;
		String data="";
		while ((i=fr.read())!=-1) {
			data+=String.valueOf((char)i);
		}
		fr.close();
		String[] feel=data.split("\n");
		
		Pattern[] p=new Pattern[feel.length];
		for(int a=0; a<p.length; a++){
			p[a]=Pattern.compile(feel[a]);
			
		}
		Matcher[] m=new Matcher[feel.length];
		String taste="";
		for (int a = 0; a < m.length; a++) {
			m[a]=p[a].matcher(value.toString());
			while (m[a].find()) {
				if (a>=0 && a<=3) {
					taste="매운맛";
				}else if (a>=4 && a<=6) {
					taste="싱거운";
				}else if (a>=7 && a<=8) {
					taste="짠맛";
				}else if (a>=9 && a<=12) {
					taste="단맛";
				}else if (a>=13 && a<=15) {
					taste="쓴맛";
				}else if (a>=16 && a<=17) {
					taste="신맛";
				}else{
					taste="기타맛";
					
				}
				result.set(taste);//String =>Text
				context.write(result, one);
			}
		}
		
	}
	
	
	
}
