package com.grenade.main.service;

import org.bytedeco.javacv.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityNotFoundException;

import org.bytedeco.ffmpeg.global.avutil;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

@Component
public class VideoThumbnailExtractor {
    
    private final static Logger logger = LoggerFactory.getLogger(VideoThumbnailExtractor.class);

    static {
        avutil.av_log_set_level(avutil.AV_LOG_QUIET);
    }

    public String extractThumbnail(String videoPath, String outputImagePath){
        String uploadDirPath = System.getProperty("user.dir") +"/uploads" +"/thumbnails";
        if(videoPath == null || videoPath.isEmpty()) {
            logger.warn("File is empty");
            throw new EntityNotFoundException("File is empty");
        }
        try{
            File uploadDir = new File(uploadDirPath); 
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (created) {
                    System.out.println("Directory created!");
                    logger.info("Directory created!");
                } else {
                    logger.warn("Failed to create directory {}",uploadDirPath);
                    throw new RuntimeException("Failed to create upload directory: " + uploadDirPath);
                }
            }
        
            try(FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoPath);){
                grabber.start();
                Frame frame;
                int frameNumber = 0;
                while ((frame = grabber.grabImage()) == null && frameNumber < 300) {
                    frameNumber++;
                }
                if (frame == null) {
                    throw new RuntimeException("Frame is null");
                }
                @SuppressWarnings("resource")
                BufferedImage img = new Java2DFrameConverter().convert(frame);
                ImageIO.write(img, "jpg", new File(uploadDirPath, outputImagePath));
                logger.debug("Thumbnail created in {}", outputImagePath);
                return outputImagePath;
            } catch(Exception e) {
                e.printStackTrace();
                return "";
            }
        } catch(Exception e) {
            e.printStackTrace();
            return "";
        }    
    }
}
