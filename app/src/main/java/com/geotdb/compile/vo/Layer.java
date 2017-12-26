package com.geotdb.compile.vo;

import java.io.Serializable;

import android.database.Cursor;

/**
 * 本地用户
 * 
 * @author XuFeng
 * 
 */

//井孔数据表
public class Layer implements Serializable {

	private static final long serialVersionUID = 1L;

	//"行号")
	// 土层号吧 // 设置为自增长的主键
	private String id; // id
	String code;
	//项目编号")
	String projectID = "";
	//项目名称")
	String projectName = "";
	//孔号")
	String holeID = "";
	//土样定名")
	String name = "";
	//层状")
	String shape = "";
	//层底深度")
	String depth = "";
	//试样起始位置")
	String sampleBegin = "";
	//试样终止位置")
	String sampleEnd = "";
	//试样类型")
	String sampleType = "";
	//初见水位")
	String initialWater = "";
	//稳定水位")
	String steadyWater = "";
	//水类型")
	String waterType = "";
	//贯探起始位置")
	String inertiaBegin = "";
	//贯探终止位置")
	String inertiaEnd = "";
	//贯探击数")
	String inertiaCount = "";
	//惯探类型")
	String inertiaType = "";
	//岩石风化程度")
	String weathering = "";
	//地质成因")
	String causes = "";
	//地质年代")
	String era = "";
	//底层描述")
	String describe = "";
	//经度")
	String longitude = "";
	//纬度")
	String latitude = "";
	//时间")
	String addTime = "";
	//文件1")
	String file1 = "";
	//文件2")
	String file2 = "";
	//文件3")
	String file3 = "";
	//文件4")
	String file4 = "";

	String ys = "";

	public Layer() {

	}

	public String getYs() {
		return ys;
	}

	public void setYs(String ys) {
		this.ys = ys;
	}

	public Layer(Cursor cursor) {
		//this.id = cursor.getString(cursor.getColumnIndex("行号"));
		this.code = cursor.getString(cursor.getColumnIndex("行号"));
//		try {
//			this.holeID = cursor.getString(cursor.getColumnIndex("孔号"));
//			this.projectID = Key.jm(cursor.getString(cursor.getColumnIndex("项目编号")),0);
//			this.projectName = Key.jm(cursor.getString(cursor.getColumnIndex("项目名称")),0);
//
//			this.name = Key.jm(cursor.getString(cursor.getColumnIndex("土样定名")),0);
//			this.shape = Key.jm(cursor.getString(cursor.getColumnIndex("层状")),0);
//			this.depth = Key.jm(cursor.getString(cursor.getColumnIndex("层底深度")),0);
//			this.sampleBegin = Key.jm(cursor.getString(cursor.getColumnIndex("试样起始位置")),0);
//			this.sampleEnd = Key.jm(cursor.getString(cursor.getColumnIndex("试样终止位置")),0);
//			this.sampleType = Key.jm(cursor.getString(cursor.getColumnIndex("试样类型")),0);
//			this.initialWater = Key.jm(cursor.getString(cursor.getColumnIndex("初见水位")),0);
//			this.steadyWater = Key.jm(cursor.getString(cursor.getColumnIndex("稳定水位")),0);
//			this.waterType = Key.jm(cursor.getString(cursor.getColumnIndex("水类型")),0);
//			this.inertiaBegin = Key.jm(cursor.getString(cursor.getColumnIndex("贯探起始位置")),0);
//			this.inertiaEnd = Key.jm(cursor.getString(cursor.getColumnIndex("贯探终止位置")),0);
//			this.inertiaCount = Key.jm(cursor.getString(cursor.getColumnIndex("贯探击数")),0);
//			this.inertiaType = Key.jm(cursor.getString(cursor.getColumnIndex("贯探类型")),0);
//			this.weathering = Key.jm(cursor.getString(cursor.getColumnIndex("岩石风化程度")),0);
//			this.causes = Key.jm(cursor.getString(cursor.getColumnIndex("地质成因")),0);
//			this.era = Key.jm(cursor.getString(cursor.getColumnIndex("地质年代")),0);
//			this.describe = Key.jm(cursor.getString(cursor.getColumnIndex("地层描述")),0);
//
//			this.longitude = Key.jm(cursor.getString(cursor.getColumnIndex("经度")),0);
//			this.latitude = Key.jm(cursor.getString(cursor.getColumnIndex("纬度")),0);
//			this.addTime = Key.jm(cursor.getString(cursor.getColumnIndex("时间")),0);
//			this.file1 = Key.jm(cursor.getString(cursor.getColumnIndex("文件1")),0);
//			this.file2 = Key.jm(cursor.getString(cursor.getColumnIndex("文件2")),0);
//			this.file3 = Key.jm(cursor.getString(cursor.getColumnIndex("文件3")),0);
//			this.file4 = Key.jm(cursor.getString(cursor.getColumnIndex("文件4")),0);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
		
	}

	public String getInsertSQL() {
		return "insert into 井孔数据表 (行号,项目编号,项目名称,孔号,土样定名,层状,层底深度,初见水位,稳定水位,水类型,试样起始位置,试样终止位置,试样类型,贯探起始位置,贯探终止位置,贯探击数,贯探类型,岩石风化程度,地质成因,地质年代,地层描述,经度,纬度,时间,文件1,文件2,文件3,文件4) 	values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	}

	public Object[] getObject() {
		return new Object[] { this.code, this.projectID, this.projectName, this.holeID, this.name, this.shape, this.depth,this.initialWater, this.steadyWater, this.waterType,this.sampleBegin, this.sampleEnd, this.sampleType, this.inertiaBegin, this.inertiaEnd,
				this.inertiaCount, this.inertiaType, this.weathering, this.causes, this.era, this.describe, this.longitude, this.latitude, this.addTime, this.file1, this.file2, this.file3, this.file4 };
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProjectID() {
		return projectID;
	}

	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}

	public String getProjectName() {
		return projectName;
	}
	
	

	public String getInitialWater() {
		return initialWater;
	}

	public void setInitialWater(String initialWater) {
		this.initialWater = initialWater;
	}

	public String getSteadyWater() {
		return steadyWater;
	}

	public void setSteadyWater(String steadyWater) {
		this.steadyWater = steadyWater;
	}

	public String getWaterType() {
		return waterType;
	}

	public void setWaterType(String waterType) {
		this.waterType = waterType;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getHoleID() {
		return holeID;
	}

	public void setHoleID(String holeID) {
		this.holeID = holeID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShape() {
		return shape;
	}

	public void setShape(String shape) {
		this.shape = shape;
	}

	public String getDepth() {
		return depth;
	}

	public void setDepth(String depth) {
		this.depth = depth;
	}

	public String getSampleBegin() {
		return sampleBegin;
	}

	public void setSampleBegin(String sampleBegin) {
		this.sampleBegin = sampleBegin;
	}

	public String getSampleEnd() {
		return sampleEnd;
	}

	public void setSampleEnd(String sampleEnd) {
		this.sampleEnd = sampleEnd;
	}

	public String getSampleType() {
		return sampleType;
	}

	public void setSampleType(String sampleType) {
		this.sampleType = sampleType;
	}

	public String getInertiaBegin() {
		return inertiaBegin;
	}

	public void setInertiaBegin(String inertiaBegin) {
		this.inertiaBegin = inertiaBegin;
	}

	public String getInertiaEnd() {
		return inertiaEnd;
	}

	public void setInertiaEnd(String inertiaEnd) {
		this.inertiaEnd = inertiaEnd;
	}

	public String getInertiaCount() {
		return inertiaCount;
	}

	public void setInertiaCount(String inertiaCount) {
		this.inertiaCount = inertiaCount;
	}

	public String getInertiaType() {
		return inertiaType;
	}

	public void setInertiaType(String inertiaType) {
		this.inertiaType = inertiaType;
	}

	public String getWeathering() {
		return weathering;
	}

	public void setWeathering(String weathering) {
		this.weathering = weathering;
	}

	public String getCauses() {
		return causes;
	}

	public void setCauses(String causes) {
		this.causes = causes;
	}

	public String getEra() {
		return era;
	}

	public void setEra(String era) {
		this.era = era;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getFile1() {
		return file1;
	}

	public void setFile1(String file1) {
		this.file1 = file1;
	}

	public String getFile2() {
		return file2;
	}

	public void setFile2(String file2) {
		this.file2 = file2;
	}

	public String getFile3() {
		return file3;
	}

	public void setFile3(String file3) {
		this.file3 = file3;
	}

	public String getFile4() {
		return file4;
	}

	public void setFile4(String file4) {
		this.file4 = file4;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	

}
