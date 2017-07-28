package com.jlkj.kitchen.bean;

import java.io.Serializable;

public class Course implements Serializable{
	private int id;
	private String coursename;
	private int teacherid;
	private String introduction;
	private String courseimgurl;
	private double degree;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCoursename() {
		return coursename;
	}
	public void setCoursename(String coursename) {
		this.coursename = coursename;
	}
	public int getTeacherid() {
		return teacherid;
	}
	public void setTeacherid(int teacherid) {
		this.teacherid = teacherid;
	}
	public String getIntroduction() {
		return introduction;
	}
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	public String getCourseimgurl() {
		return courseimgurl;
	}
	public void setCourseimgurl(String courseimgurl) {
		this.courseimgurl = courseimgurl;
	}
	public double getDegree() {
		return degree;
	}
	public void setDegree(double degree) {
		this.degree = degree;
	}
	
}
