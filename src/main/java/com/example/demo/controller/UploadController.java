package com.example.demo.controller;

import com.example.demo.dto.UploadResultDTO;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@Log4j2
//로그가 롬북만 있는 것은 아니다.
// 로그를 만들고 찍어내는 것도 공부 // 다른 방식으로 찍는 것도 공부
public class UploadController {

    @Value("${itemImgLocation}")
    private String itemImgLocation;

    //저장폴더 내에 날짜별로 필요 폴더 만들기
    //폴더가 있으면 만들고 없으면 안 만들고
    private String makeFolder(){
        String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String folderPath = str.replace("/", File.separator); // 2024/07/24 >> 20240724

        File uploadPathFolder = new File(itemImgLocation, folderPath);
        if (uploadPathFolder.exists() == false) {
            uploadPathFolder.mkdirs(); //폴더 생성함
        }

        return folderPath;

    }

    @PostMapping("/uploadAjax")
    public ResponseEntity<List<UploadResultDTO>> uploadFile(MultipartFile[] uploadFiles) {
        // 기존에 배운 건 1단계
        // 기존 MultipartFile 이번에는 MultipartFile[] < 배열 추가
        // 배운 지식으로는 배열이 추가되었다는 건 파일이 여러개

        List<UploadResultDTO> resultDTOList = new ArrayList<>();

        if (uploadFiles != null && uploadFiles.length != 0) {

            for(MultipartFile multipartFile : uploadFiles) {

                if (multipartFile != null && multipartFile.getContentType().startsWith("iamge") == false) {
                    log.warn("this file is not image type");
                }

                // 제목없음.jpg, 제목없음.txt, 제목없음.hwp
                // ie Edge 등 전체 경로가 들어와
                // 제목없음만 들어와야 하는데 경로도 같이 들어옴
                String originalName = multipartFile.getOriginalFilename();
                String fileName = originalName
                        .substring(originalName.lastIndexOf("\\") + 1);
                log.info("originalName : " + originalName);
                log.info("fileName : " + fileName);
                //log.info("byte : " + Arrays.toString(multipartFile.getBytes()));

                String uuid = UUID.randomUUID().toString();

                //폴더 생성
                String folderPath = makeFolder(); //반환은 folderPath 20240222


                //저장할 파일 이름 중간에 "_"를 이용해서 구분 // 썸네일은 s_
                String saveName = itemImgLocation + File.separator + folderPath + File.separator
                        + uuid + "_" + fileName; // C:/demo/item/20240222_제목없음.jpg

                log.info("저장경로 : " + saveName);

                Path savePath = Paths.get(saveName);

                try {
                    multipartFile.transferTo(savePath); //물리적 파일 저장 (원본)
                    //작은 사진 썸네일
                    String thumbnailSaveName = itemImgLocation + File.separator + folderPath + File.separator
                            + "s_" + uuid + "_" + fileName;
                    //c:asdf/asfd/s_1234_제목없음 (썸네일)
                    //c:asdf/asfd/1234_제목없음 (원본)

                    File thumbnailFile = new File(thumbnailSaveName);

                    Thumbnailator.createThumbnail(savePath.toFile(), thumbnailFile, 100, 100); //물리적인 파일저장

                    resultDTOList.add(new UploadResultDTO(fileName, uuid, folderPath));

                } catch (IOException e) {
                    log.info("사진저장이 안 될 걸?");
                    e.printStackTrace();
                }

            }


        }

        return new ResponseEntity<>(resultDTOList, HttpStatus.OK);

    }

    @GetMapping("/display")
    public ResponseEntity<byte[]> getFile (String fileName) {

        ResponseEntity<byte[]> result = null;

        try {
            String srcFileName = URLDecoder.decode(fileName, "UTF-8");

            log.info("fileName : " + srcFileName);

            File file = new File(itemImgLocation + File.separator + srcFileName);

            log.info("file : " + file);

            HttpHeaders headers = new HttpHeaders();

            //MIME 타입 처리
            headers.add("Content-Type", Files.probeContentType(file.toPath()));
            //파일 데이터 처리
            result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }




}
