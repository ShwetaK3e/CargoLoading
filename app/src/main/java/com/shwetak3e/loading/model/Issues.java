package com.shwetak3e.loading.model;

/**
 * Created by shwetakumar on 7/5/17.
 */

public class Issues {

    int issueType; // 0:damage  1:missing
    String Uri;
    String issueDescription;
    int issueDefn; // 0:video 1:Image 2: text



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

    public int getIssueDefn() {
        return issueDefn;
    }

    public void setIssueDefn(int issueDefn) {
        this.issueDefn = issueDefn;
    }
}
