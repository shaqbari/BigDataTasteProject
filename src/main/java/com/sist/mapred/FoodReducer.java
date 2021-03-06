package com.sist.mapred;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

//여기서 가중치를 줄수도 있다.
public class FoodReducer extends Reducer<Text, IntWritable, Text, IntWritable>{
	private IntWritable res=new IntWritable();
	
	protected void reduce(Text key, Iterable<IntWritable> values,
			Reducer<Text, IntWritable, Text, IntWritable>.Context context) throws IOException, InterruptedException {
		int sum=0;
		for (IntWritable i : values) {
			sum+=i.get();//IntWritable을 정수형으로 바꾸는 메소드 IntWritable ==> int
			
		}
		
		res.set(sum); // int => IntWritable
		context.write(key, res);
	
	}
}
