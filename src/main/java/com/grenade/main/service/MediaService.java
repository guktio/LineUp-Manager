package com.grenade.main.service;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.grenade.main.dto.MediaDTO;
import com.grenade.main.entity.Media;
import com.grenade.main.repo.MediaRepo;
import com.grenade.main.repo.UserRepo;
import jakarta.persistence.EntityNotFoundException;

import org.bytedeco.javacv.*;
import org.bytedeco.ffmpeg.global.avutil;


@Service
public class MediaService extends ServiceBase<Media, MediaDTO, UUID, MediaRepo>{
    
    private final MediaRepo mediaRepo;
    private final UserRepo userRepo;
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Value("${files.UPLOAD_DIR}")
    private String uploadPath;

    static {
        avutil.av_log_set_level(avutil.AV_LOG_QUIET);
    }


    protected MediaService(MediaRepo mediaRepo, UserRepo userRepo) {
        super(mediaRepo);
        this.mediaRepo = mediaRepo;
        this.userRepo = userRepo;
    } 

    public String getFileExtention(String filename){
        if(filename == null || filename.isEmpty()){
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex <= 0 || dotIndex == filename.length() - 1) {
            return "";
        }
        String ext = filename.substring(dotIndex+1);
        return ext.toLowerCase();
    }

    public MediaDTO create(MultipartFile file){
    
        String uploadDirPath = System.getProperty("user.dir") + uploadPath + "/video";
        
        if(file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        
        try{
            File uploadDir = new File(uploadDirPath); 
            if (!uploadDir.exists() && !uploadDir.mkdirs()) {
                logger.info("Directory created!");
                    throw new RuntimeException("Failed to create upload directory: " + uploadDirPath);
            }
            String ext = getFileExtention(file.getOriginalFilename());
            if(!ext.equals("mp4")){
                throw new IllegalArgumentException("Unsupported file format");
            }
            
            UUID uuid = UUID.randomUUID();
            String uniqueFileName = uuid + "." + ext;
            
            File filePath = new File(uploadDir, uniqueFileName);
            
            file.transferTo(filePath);
            
            Media video = Media.builder()
                        .video(uniqueFileName)
                        .thumbnail(extractThumbnail(filePath.getPath(), uuid+".jpg"))
                        .uuid(uuid)
                        .user(userRepo.findByUsername(username).orElseThrow(
                                            () -> new EntityNotFoundException("User not found:" + username)))
                        .build();
            
            try {
                Media saved = mediaRepo.save(video);
                logger.debug("Saved media: {}", saved.toString());
                return(toDTO(saved));
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("File not saved in database", e);
            }
        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }

    private String extractThumbnail(String videoPath, String outputImagePath){
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
                try (Java2DFrameConverter converter = new Java2DFrameConverter();) {
                    BufferedImage img = converter.convert(frame);
                    ImageIO.write(img, "jpg", new File(uploadDirPath, outputImagePath));
                    logger.debug("Thumbnail created in {}", outputImagePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return outputImagePath;
            } catch(Exception e) {
                logger.error("Failed to extract thumbnail from {}", videoPath, e);
                throw new RuntimeException("Thumbnail extraction failed", e);
            }
        } catch(Exception e) {
            logger.error("Failed to extract thumbnail from {}", videoPath, e);
            throw new RuntimeException("Thumbnail extraction failed", e);
        }    
    }
    
    public MediaDTO toDTO(Media entity) {
        return MediaDTO.builder()
                        .UUID(entity.getUuid())
                        .videoPath(entity.getVideo())
                        .thumbnailPath(entity.getThumbnail())
                        .build();
    }    
}