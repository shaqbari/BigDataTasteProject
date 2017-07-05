package com.sist.bigdata;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.mapreduce.JobRunner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sist.food.dao.*;
import com.sist.naver.NaverBlogManager;
import com.sist.r.RManager;

import java.util.*;

import javax.annotation.Resource;
@Controller
public class FoodMainController {
	
	@Autowired
	private FoodManager fmgr;
	
	@Autowired
	private Configuration conf;
	
	@Resource(name="fr")
	private JobRunner fjob;
	
	@Autowired
	private NaverBlogManager nbm;
	
	@Autowired
	private RManager rm;
	
	@RequestMapping("main/main.do")
	public String main_page(Model model){
		
		List<CategoryVO> list = fmgr.categoryAllData();
		model.addAttribute("list",list);
		model.addAttribute("main_jsp","default.jsp");
		return "main/main";
	}
	@RequestMapping("main/loc.do")
	public String main_loc(Model model)
	{
		model.addAttribute("main_jsp", "food/food_loc.jsp");
		return "main/main";
	}
	@RequestMapping("main/foodmain.do")
	public String main_foodpage(String title,String link,Model model){
		
		List<FoodVO> list=fmgr.categoryDetailData(link);
		model.addAttribute("title", title);
		model.addAttribute("list", list);
		model.addAttribute("main_jsp","food/foodmain.jsp");
		return "main/main";
	}
	
	//SNS 데이터 수집=> Flume, Sqoop(db에서 가져온다.), 파일럿, Strom(요즘뜬다), 척와
	//자바명령어 없이 설치해서 실행가능(바로 하둡안에 넣어준다.)
	
	//분석 => MapReduce, Hive(임팔라, 타조), Pig, Spark
	//시각화 =>제플린, SparkR, R, JS..
	//저장 => HBASE, MongoDB, 카산드라, 카프카..
	@RequestMapping("main/fooddetail.do")
	public String main_fooddetail(String link,String poster,Model model)
	{
		FoodVO vo=fmgr.foodDetailData(link);
		//plum은 같은데이터를 가려준다.				
		
		nbm.naverBlogData(vo.getName());
		nbm.naverXmlParse();//데이터 수집완료
		
		foodFileDelete();
		foodCopyFromLocal();
		try {
			fjob.call();//맵리듀스 실행
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		foodCopyToLocal();//이후 R로 보낸다.
		
		String json=rm.tasteData();
		model.addAttribute("json", json);
		model.addAttribute("vo", vo);
		model.addAttribute("poster", poster);
		model.addAttribute("main_jsp","food/fooddetail.jsp");
		return "main/main";
	}
	
	public void foodFileDelete(){
		try {
			FileSystem fs=FileSystem.get(conf);
			if (fs.exists(new Path("/food_input_ns1/naver.txt"))) {
				fs.delete(new Path("/food_input_ns1/naver.txt"), true);
			}
			if (fs.exists(new Path("/food_input_ns1/site.txt"))) {
				fs.delete(new Path("/food_input_ns1/site.txt"), true);
			}
			if (fs.exists(new Path("/food_output_ns1"))) {//파일이 두개생기기 때문에 폴더째로 지워야 한다.
				fs.delete(new Path("/food_output_ns1"), true);
			}
			fs.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
	}
	public void foodCopyFromLocal(){
		try {
			FileSystem fs=FileSystem.get(conf);
			fs.copyFromLocalFile(new Path("/home/sist/food_data/naver.txt"), new Path("/food_input_ns1/naver.txt"));
			fs.copyFromLocalFile(new Path("/home/sist/food_data/site.txt"), new Path("/food_input_ns1/site.txt"));
			fs.close();
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
	}
	public void foodCopyToLocal(){
		try {
			FileSystem fs=FileSystem.get(conf);
			fs.copyToLocalFile(new Path("/food_output_ns1/part-r-00000"), new Path("/home/sist/food_data/result"));			
			fs.close();
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
	}
}






