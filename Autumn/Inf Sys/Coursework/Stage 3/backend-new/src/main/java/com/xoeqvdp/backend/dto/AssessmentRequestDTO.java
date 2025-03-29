package com.xoeqvdp.backend.dto;

import java.util.List;

public class AssessmentRequestDTO {
    private List<AssessmentItemDTO> hw;
    private List<AssessmentItemDTO> sw;
    private List<AssessmentItemDTO> pr;

    public List<AssessmentItemDTO> getHw() { return hw; }
    public void setHw(List<AssessmentItemDTO> hw) { this.hw = hw; }

    public List<AssessmentItemDTO> getSw() { return sw; }
    public void setSw(List<AssessmentItemDTO> sw) { this.sw = sw; }

    public List<AssessmentItemDTO> getPr() { return pr; }
    public void setPr(List<AssessmentItemDTO> pr) { this.pr = pr; }
}