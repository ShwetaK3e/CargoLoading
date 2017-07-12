package com.shwetak3e.loading.model;

/**
 * Created by shwetakumar on 7/5/17.
 */

public class Issues {

    int issueType=-1; // 0:damage  1:missing  2:weightLoss
    String Uri;
    String issueDescription;
    String issueDescriptionShort;
    int issueDescriptionType=-1;// 0:video 1:Image 2: text



    public int getIssueType() {
        return issueType;
    }

    public void setIssueType(int issueType) {
        this.issueType = issueType;
    }


    public String getIssueDescription() {
        return issueDescription;
    }

    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
    }

    public String getUri() {
        return Uri;
    }

    public void setUri(String uri) {
        Uri = uri;
    }

    public String getIssueDescriptionShort() {
        return issueDescriptionShort;
    }

    public void setIssueDescriptionShort(String issueDescriptionShort) {
        this.issueDescriptionShort = issueDescriptionShort;
    }

    public int getIssueDescriptionType() {
        return issueDescriptionType;
    }

    public void setIssueDescriptionType(int issueDescriptionType) {
        this.issueDescriptionType = issueDescriptionType;
    }
}
