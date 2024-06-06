package org.example.model.dto;

public class PrepaymentDto {
    private boolean success;
    private String cert_code;

    public PrepaymentDto(boolean success, String cert_code) {
        this.success = success;
        this.cert_code = cert_code;
    }

    public boolean isSuccess() {return this.success;}
    public String getCertCode() {return this.cert_code;}
}
