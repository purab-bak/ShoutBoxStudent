package org.kotakeducation.shoutboxstudent.Models;

public class FormModel {

    String heading, enquiryInfo;

    public FormModel(String heading, String enquiryInfo) {
        this.heading = heading;
        this.enquiryInfo = enquiryInfo;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getEnquiryInfo() {
        return enquiryInfo;
    }

    public void setEnquiryInfo(String enquiryInfo) {
        this.enquiryInfo = enquiryInfo;
    }
}
