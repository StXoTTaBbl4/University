package org.xoeqvdp.backend.dto;

import java.util.List;

public class AssessmentRequest {
    private List<AssessmentItem> hw;
    private List<AssessmentItem> sw;
    private List<AssessmentItem> pr;

    // Геттеры и сеттеры
    public List<AssessmentItem> getHw() { return hw; }
    public void setHw(List<AssessmentItem> hw) { this.hw = hw; }

    public List<AssessmentItem> getSw() { return sw; }
    public void setSw(List<AssessmentItem> sw) { this.sw = sw; }

    public List<AssessmentItem> getPr() { return pr; }
    public void setPr(List<AssessmentItem> pr) { this.pr = pr; }
}