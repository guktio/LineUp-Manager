package com.grenade.main.service;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.grenade.main.dto.MediaDTO;
import com.grenade.main.entity.Media;
import com.grenade.main.repo.GrenadeRepo;
import com.grenade.main.repo.MediaRepo;
import com.grenade.main.repo.UserRepo;

import jakarta.persistence.EntityNotFoundException;

@Service
public class MediaService extends ServiceBase<Media, MediaDTO, UUID, MediaRepo>{

    private MediaRepo mediaRepo;
    private UserRepo userRepo;

    private static final Logger logger = LoggerFactory.getLogger(MediaService.class);

    @Value("${files.UPLOAD_DIR}")
    private String uploadDirPath;

    protected MediaService(MediaRepo mediaRepo, UserRepo userRepo, GrenadeRepo grenadeRepo) {
        super(mediaRepo);
        this.mediaRepo = mediaRepo;
        this.userRepo = userRepo;
    } 

    public String getFileExtention(MultipartFile file){
        String filename = file.getOriginalFilename();
        if(filename == null || filename.isEmpty()){
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex <= 0 || dotIndex == filename.length() - 1) {
            return "";
        }
        String ext = filename.substring(dotIndex+1);
        return ext;
    }

    public MediaDTO create(MultipartFile file){
        String uploadDirPath = System.getProperty("user.dir") + "/uploads" + "/video";
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(file == null || file.isEmpty()) {
            throw new EntityNotFoundException("File is empty");
        }
        try{
            File uploadDir = new File(uploadDirPath); 
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (created) {
                    System.out.println("Directory created!");
                } else {
                    throw new RuntimeException("Failed to create upload directory: " + uploadDirPath);
                }
            }
            if(!getFileExtention(file).equals("mp4")){
                throw new IllegalArgumentException("Unsupported file format");
            }
            UUID uuid = UUID.randomUUID();
            String uniqueFileName = uuid + "." + getFileExtention(file);
            File filePath = new File(uploadDir, uniqueFileName);
            file.transferTo(filePath);
            Media video = Media.builder()
                        .path(filePath.getPath())
                        .uuid(uuid)
                        .user(userRepo.findByUsername(username).orElseThrow(
                                            () -> new EntityNotFoundException("User does not found:" + username)))
                        .build();
            Media saved = mediaRepo.save(Objects.requireNonNull(video));
            logger.debug("Saved media: {}", saved.toString());
            return(toDTO(video));
        } catch (IOException e) {
            throw new RuntimeException("File upload failed", e);
        }
    }
    
    public MediaDTO toDTO(Media entity) {
        MediaDTO dto = MediaDTO.builder()
                        .UUID(entity.getUuid())
                        .build();
        return dto;
    }    
}
