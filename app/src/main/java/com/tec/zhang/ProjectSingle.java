package com.tec.zhang;
import java.io.Serializable;


public class ProjectSingle implements Serializable{
	public static final long serialVersionUID = 2L;
	private String createTime = "";//检具创建时间
	private String stateNow = "";//现在的状态
	private String stateCode = "";//状态码
	private String projNum = "";//检具编号
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getStateNow() {
		return stateNow;
	}
	public void setStateNow(String stateNow) {
		this.stateNow = stateNow;
	}
	public String getStateCode() {
		return stateCode;
	}
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}
	public String getProjNum() {
		return projNum;
	}
	public void setProjNum(String projNum) {
		this.projNum = projNum;
	}
	public String getProjName() {
		return projName;
	}
	public void setProjName(String projName) {
		this.projName = projName;
	}
	public String getProjFather() {
		return projFather;
	}
	public void setProjFather(String projFather) {
		this.projFather = projFather;
	}
	public String getDivideTime() {
		return divideTime;
	}
	public void setDivideTime(String divideTime) {
		this.divideTime = divideTime;
	}
	public String getProjMasterName() {
		return projMasterName;
	}
	public void setProjMasterName(String projMasterName) {
		this.projMasterName = projMasterName;
	}
	public String getDesignStartTime() {
		return DesignStartTime;
	}
	public void setDesignStartTime(String designStartTime) {
		DesignStartTime = designStartTime;
	}
	public String getTotallyVersionNum() {
		return totallyVersionNum;
	}
	public void setTotallyVersionNum(String totallyVersionNum) {
		this.totallyVersionNum = totallyVersionNum;
	}
	public String getDesigneerName() {
		return designeerName;
	}
	public void setDesigneerName(String designeerName) {
		this.designeerName = designeerName;
	}
	public String getConfirmTime() {
		return confirmTime;
	}
	public void setConfirmTime(String confirmTime) {
		this.confirmTime = confirmTime;
	}
	public String getDesignDuration() {
		return DesignDuration;
	}
	public void setDesignDuration(String designDuration) {
		DesignDuration = designDuration;
	}
	public String getBookingMaterialStartTime() {
		return bookingMaterialStartTime;
	}
	public void setBookingMaterialStartTime(String bookingMaterialStartTime) {
		this.bookingMaterialStartTime = bookingMaterialStartTime;
	}
	public String getBookingEngineer() {
		return bookingEngineer;
	}
	public void setBookingEngineer(String bookingEngineer) {
		this.bookingEngineer = bookingEngineer;
	}
	public String getBookingMaterialFinishTime() {
		return bookingMaterialFinishTime;
	}
	public void setBookingMaterialFinishTime(String bookingMaterialFinishTime) {
		this.bookingMaterialFinishTime = bookingMaterialFinishTime;
	}
	public String getBookingDuration() {
		return bookingDuration;
	}
	public void setBookingDuration(String bookingDuration) {
		this.bookingDuration = bookingDuration;
	}
	public String getDrawingStartTime() {
		return drawingStartTime;
	}
	public void setDrawingStartTime(String drawingStartTime) {
		this.drawingStartTime = drawingStartTime;
	}
	public String getDrawingEndTime() {
		return drawingEndTime;
	}
	public void setDrawingEndTime(String drawingEndTime) {
		this.drawingEndTime = drawingEndTime;
	}
	public String getDrawingDuration() {
		return drawingDuration;
	}
	public void setDrawingDuration(String drawingDuration) {
		this.drawingDuration = drawingDuration;
	}
	public String getDrawingCheckingStart() {
		return drawingCheckingStart;
	}
	public void setDrawingCheckingStart(String drawingCheckingStart) {
		this.drawingCheckingStart = drawingCheckingStart;
	}
	public String getDrawingCheckingEnd() {
		return drawingCheckingEnd;
	}
	public void setDrawingCheckingEnd(String drawingCheckingEnd) {
		this.drawingCheckingEnd = drawingCheckingEnd;
	}
	public String getDrawingCheckingDuration() {
		return drawingCheckingDuration;
	}
	public void setDrawingCheckingDuration(String drawingCheckingDuration) {
		this.drawingCheckingDuration = drawingCheckingDuration;
	}
	public String getCheckingEngineerName() {
		return checkingEngineerName;
	}
	public void setCheckingEngineerName(String checkingEngineerName) {
		this.checkingEngineerName = checkingEngineerName;
	}
	public String getDrawingDeliverTime() {
		return drawingDeliverTime;
	}
	public void setDrawingDeliverTime(String drawingDeliverTime) {
		this.drawingDeliverTime = drawingDeliverTime;
	}
	public String getManufactoryTime() {
		return manufactoryTime;
	}
	public void setManufactoryTime(String manufactoryTime) {
		this.manufactoryTime = manufactoryTime;
	}
	public String getManufTeamLeader() {
		return ManufTeamLeader;
	}
	public void setManufTeamLeader(String manufTeamLeader) {
		ManufTeamLeader = manufTeamLeader;
	}
	public String getAssemblyFinishTime() {
		return assemblyFinishTime;
	}
	public void setAssemblyFinishTime(String assemblyFinishTime) {
		this.assemblyFinishTime = assemblyFinishTime;
	}
	public String getMeasureStartTime() {
		return measureStartTime;
	}
	public void setMeasureStartTime(String measureStartTime) {
		this.measureStartTime = measureStartTime;
	}
	public String getMeasureEndTime() {
		return measureEndTime;
	}
	public void setMeasureEndTime(String measureEndTime) {
		this.measureEndTime = measureEndTime;
	}
	public String getDocStartTime() {
		return docStartTime;
	}
	public void setDocStartTime(String docStartTime) {
		this.docStartTime = docStartTime;
	}
	public String getDocEndTime() {
		return docEndTime;
	}
	public void setDocEndTime(String docEndTime) {
		this.docEndTime = docEndTime;
	}
	public String getDocumentAuthor() {
		return documentAuthor;
	}
	public void setDocumentAuthor(String documentAuthor) {
		this.documentAuthor = documentAuthor;
	}
	public String getExportTime() {
		return exportTime;
	}
	public void setExportTime(String exportTime) {
		this.exportTime = exportTime;
	}
	private String projName = "";//检具全名
	private String projFather = "";//检具项目名称
	private String divideTime = "";//检具分配时间
	private String projMasterName = "";//项目主管名字
	private String DesignStartTime = "";//开始设计的时间
	private String totallyVersionNum = "";//总共经历的版本号
	private String designeerName = "";//设计工程师名字
	private String confirmTime = "";//确认时间
	private String DesignDuration = "";//设计总用时
	private String bookingMaterialStartTime = "";//开始备料时间
	private String bookingEngineer = "";//备料的工程师
	private String bookingMaterialFinishTime = "";//备料结束时间
	private String bookingDuration = "";//备料总用时
	private String drawingStartTime = "";//出图开始时间
	private String drawingEndTime = "";//出图结束时间
	private String drawingDuration = "";//出图总用时
	private String drawingEngineer = "";//出图工程师姓名
	public String getDrawingEngineer() {
		return drawingEngineer;
	}
	public void setDrawingEngineer(String drawingEngineer) {
		this.drawingEngineer = drawingEngineer;
	}
	private String drawingCheckingStart = "";//图纸检查开始时间
	private String drawingCheckingEnd = "";//图纸检查结束时间
	private String drawingCheckingDuration = "";//图纸检查总用时
	private String checkingEngineerName = "";//检查图纸工程师姓名
	private String drawingDeliverTime = "";//下发图纸时间
	private String manufactoryTime = "";//开始制造时间
	private String ManufTeamLeader = "";//装配小组负责人
	private String assemblyFinishTime = "";//装配结束时间
	private String assemblyDuration = "";//装配总用时
	public String getAssemblyDuration() {
		return assemblyDuration;
	}
	public void setAssemblyDuration(String assemblyDuration) {
		this.assemblyDuration = assemblyDuration;
	}
	private String measureStartTime = "";//测量开始时间
	private String measureEndTime = "";//测量结束时间
	private String measureDuation= "";//测量总用时
	private String docStartTime = "";//后续文件开始时间
	private String docEndTime = "";//后续文件结束时间
	private String documentAuthor = "";//后续文件负责人
	private String docDuration = "";//后续文件总用时
	private String attendNames = "于万红";//项目相关人员
	public String getAttendNames() {
		return attendNames;
	}
	public void setAttendNames(String attendNames) {
		this.attendNames = attendNames;
	}
	public String getMeasureDuation() {
		return measureDuation;
	}
	public void setMeasureDuation(String measureDuation) {
		this.measureDuation = measureDuation;
	}
	public String getDocDuration() {
		return docDuration;
	}
	public void setDocDuration(String docDuration) {
		this.docDuration = docDuration;
	}
	private String exportTime = "";//出货时间
}
