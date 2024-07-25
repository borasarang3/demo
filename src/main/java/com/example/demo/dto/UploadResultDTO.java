package com.example.demo.dto;

import lombok.*;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadResultDTO implements Serializable {

    private String fileName; //파일이름
    private String uuid; //난수?
    private String folderPath; //저장경로

    //파일을 가져오는 메소드
    public String getImageURL() {
        try {
            return URLEncoder.encode(folderPath + "/" + uuid + "_" + fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "";
    }

    //썸네일
    public String getThumURL() {
        try {
            return URLEncoder.encode(folderPath + "/s_" + uuid + "_" + fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "";
    }

}
