package com.example.helloworld.resources;

import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.AmazonClientException;



/**
 * Created by prashanth.yv on 6/5/15.
 */
public class AWSResource {
    private static final String SUFFIX = "/";
    public static final String base_images_url= "https://s3-ap-southeast-1.amazonaws.com/buzkashi/images/";
    public static String uploadFile(String fileName, byte[] byteArray) {
        AWSCredentials credentials = new BasicAWSCredentials("AKIAJ32DHIOXHSN7ZHSA", "r+eFWMyEev9Qd51E8AYhn6qKV6fbocVbwwGjYYr0");
        AmazonS3 s3client = new AmazonS3Client(credentials);
        String bucketName = "buzkashi";
        String folderName = "images";
        fileName = folderName + "/" + fileName;
        try {
            Long contentLength = Long.valueOf(byteArray.length);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(contentLength);

            System.out.println("Uploading a new object to S3");
            //new File(filePath)).withCannedAcl(CannedAccessControlList.PublicRead)
            InputStream is = new ByteArrayInputStream(byteArray);

            s3client.putObject(new PutObjectRequest(bucketName, fileName, is, metadata));

            return "https://s3-ap-southeast-1.amazonaws.com/buzkashi/images/"+fileName;
        } catch (AmazonServiceException ase) {
            ase.printStackTrace();
//            System.out.println("Caught an AmazonServiceException, which " +
//                    "means your request made it " +
//                    "to Amazon S3, but was rejected with an error response" +
//                    " for some reason.");
//            System.out.println("Error Message:    " + ase.getMessage());
//            System.out.println("HTTP Status Code: " + ase.getStatusCode());
//            System.out.println("AWS Error Code:   " + ase.getErrorCode());
//            System.out.println("Error Type:       " + ase.getErrorType());
//            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            ace.printStackTrace();
//            System.out.println("Caught an AmazonClientException, which " +
//                    "means the client encountered " +
//                    "an internal error while trying to " +
//                    "communicate with S3, " +
//                    "such as not being able to access the network.");
//            System.out.println("Error Message: " + ace.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        Path path = Paths.get("/Users/vaidyanathan.s/Pictures/yd_profile.jpg");
        try {
            byte[] data = Files.readAllBytes(path);
            System.out.println(new String(data));
            System.out.println(AWSResource.uploadFile("test_image", data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}