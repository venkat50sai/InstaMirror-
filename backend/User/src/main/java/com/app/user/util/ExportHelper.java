package com.app.user.util;

import com.app.user.dto.*;
import com.app.user.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class ExportHelper {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostService postService;

    public byte[] createZip(UserDto user) throws IOException {
        ByteArrayOutputStream zipOut = new ByteArrayOutputStream();

        try (ZipOutputStream zip = new ZipOutputStream(zipOut)) {

            // 1️⃣ Add images first
            List<PostDto> posts = postService.getPosts(user.getId());
            int i = 1;
            for (PostDto post : posts) {
                if (post.getImage() != null) {
                    String imageFileName = "images/post_" + i + ".png";
                    zip.putNextEntry(new ZipEntry(imageFileName));
                    byte[] imageBytes = post.getImage();
                    zip.write(imageBytes);
                    zip.closeEntry();
                    i++;
                }
            }

            // 2️⃣ Build HTML referencing images
            StringBuilder html = new StringBuilder();
            html.append("<html><head><style>")
                    .append("body {font-family: Arial;} img {margin: 5px;} .post {display:inline-block; margin:5px;}")
                    .append("</style></head><body>");

            html.append("<h1>").append(user.getFullname()).append("</h1>");
            html.append("<p>Username: ").append(user.getUsername()).append("</p>");
            html.append("<p>Email: ").append(user.getEmail()).append("</p>");
            html.append("<p>DOB: ").append(user.getDob()).append("</p>");
            html.append("<p>Bio: ").append(user.getBio()).append("</p>");
            html.append("<p>Followers: ").append(user.getFollowers())
                    .append(", Following: ").append(user.getFollowings()).append("</p>");

            html.append("<h2>Posts</h2><div>");
            i = 1;
            for (PostDto post : posts) {
                html.append("<div class='post'>");
                html.append("<p>").append(post.getCaption() != null ? post.getCaption() : "").append("</p>");
                html.append("<img src='images/post_").append(i).append(".png' width='150'/>");
                html.append("<p>Likes: ").append(post.getLikes() != null ? post.getLikes() : 0)
                        .append(", Comments: ").append(post.getComments() != null ? post.getComments() : 0).append("</p>");
                html.append("</div>");
                i++;
            }
            html.append("</div></body></html>");

            // 3️⃣ Add HTML to ZIP
            zip.putNextEntry(new ZipEntry("index.html"));
            zip.write(html.toString().getBytes());
            zip.closeEntry();
        }

        return zipOut.toByteArray();
    }

    public  byte[] createExcel(UserDto user) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("User Profile");

            int rowIdx = 0;
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("ID");
            row.createCell(1).setCellValue(user.getId());

            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("Username");
            row.createCell(1).setCellValue(user.getUsername());

            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("Full Name");
            row.createCell(1).setCellValue(user.getFullname());

            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("Email");
            row.createCell(1).setCellValue(user.getEmail());

            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("Phone");
            row.createCell(1).setCellValue(user.getPhone());

            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("DOB");
            row.createCell(1).setCellValue(user.getDob().toString());

            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("Bio");
            row.createCell(1).setCellValue(user.getBio());

            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("Followers");
            row.createCell(1).setCellValue(user.getFollowers());

            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("Following");
            row.createCell(1).setCellValue(user.getFollowings());

            // Posts header
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("Post Caption");
            row.createCell(1).setCellValue("Likes");
            row.createCell(2).setCellValue("Comments");

            for (PostDto post : postService.getPosts(user.getId())) {
                row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(post.getCaption() != null ? post.getCaption() : "");
                row.createCell(1).setCellValue(post.getLikes()!= null ? post.getLikes() : 0);
                row.createCell(2).setCellValue(post.getComments()!= null ? post.getComments() : 0);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }


    public byte[] createJson(UserDto user) throws IOException {
        return objectMapper.writeValueAsBytes(user);
    }
}
