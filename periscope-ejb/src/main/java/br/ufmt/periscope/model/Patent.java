package br.ufmt.periscope.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import com.github.jmkgreen.morphia.annotations.Embedded;
import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;
import com.github.jmkgreen.morphia.annotations.Reference;

@Entity
public class Patent {
	
	@Id
	private ObjectId id;
	
	private String titleSelect;	
	private String abstractSelect;
		
	private @Embedded Classification mainClassification;
	private @Embedded List<Classification> classifications = new ArrayList<Classification>();
	private @Embedded List<Priority> priorities = new ArrayList<Priority>();
	
	private String language;
	
	private String publicationNumber;
	private Date publicationDate;
	private @Embedded Country publicationCountry;
	
	private String applicationNumber;
	private Date applicationDate;
	private @Embedded Country applicationCountry;
		
	private @Embedded List<Applicant> applicants = new ArrayList<Applicant>();
	private @Embedded List<Inventor> inventors = new ArrayList<Inventor>();
		
	private Boolean blacklisted = false; 
	private Boolean completed = false;
	private Boolean shared = false;
	@Reference private Project project;
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getTitleSelect() {
		return titleSelect;
	}
	public void setTitleSelect(String titleSelect) {
		this.titleSelect = titleSelect;
	}
	public String getAbstractSelect() {
		return abstractSelect;
	}
	public void setAbstractSelect(String abstractSelect) {
		this.abstractSelect = abstractSelect;
	}
	public String getPublicationNumber() {
		return publicationNumber;
	}
	public void setPublicationNumber(String publicationNumber) {
		this.publicationNumber = publicationNumber;
	}
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	public Classification getMainClassification() {
		return mainClassification;
	}
	public void setMainClassification(Classification mainClassification) {
		this.mainClassification = mainClassification;
	}
	public List<Classification> getClassifications() {
		return classifications;
	}
	public void setClassifications(List<Classification> classifications) {
		this.classifications = classifications;
	}
	public Boolean getBlacklisted() {
		return blacklisted;
	}
	public void setBlacklisted(Boolean blacklisted) {
		this.blacklisted = blacklisted;
	}
	public Date getPublicationDate() {
		return publicationDate;
	}
	public void setPublicationDate(Date publicationDate) {
		this.publicationDate = publicationDate;
	}
	public Country getPublicationCountry() {
		return publicationCountry;
	}
	public void setPublicationCountry(Country publicationCountry) {
		this.publicationCountry = publicationCountry;
	}
	public String getApplicationNumber() {
		return applicationNumber;
	}
	public void setApplicationNumber(String applicationNumber) {
		this.applicationNumber = applicationNumber;
	}
	public Date getApplicationDate() {
		return applicationDate;
	}
	public void setApplicationDate(Date applicationDate) {
		this.applicationDate = applicationDate;
	}
	public Country getApplicationCountry() {
		return applicationCountry;
	}
	public void setApplicationCountry(Country applicationCountry) {
		this.applicationCountry = applicationCountry;
	}
	public Boolean getCompleted() {
		return completed;
	}
	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public List<Applicant> getApplicants() {
		return applicants;
	}
	public void setApplicants(List<Applicant> applicants) {
		this.applicants = applicants;
	}
	public List<Priority> getPriorities() {
		return priorities;
	}
	public void setPriorities(List<Priority> priorities) {
		this.priorities = priorities;
	}
	public List<Inventor> getInventors() {
		return inventors;
	}
	public void setInventors(List<Inventor> inventors) {
		this.inventors = inventors;
	}
	public Boolean getShared() {
		return shared;
	}
	public void setShared(Boolean shared) {
		this.shared = shared;
	}
	
	public String getApplicantsToString(){
		if(applicants == null){
			return "";
		}else{
			return Arrays.toString(applicants.toArray());
		}
	}
	
	public String getInventorsToString(){
		if(inventors == null){
			return "";
		}else{
			return Arrays.toString(inventors.toArray());
		}
	}
	
	public String getPrioritiesToString(){
		if(priorities == null){
			return "";
		}else{
			return Arrays.toString(priorities.toArray());
		}
	}
	

}
