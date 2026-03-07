package com.grenade.main.service;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.grenade.main.dto.GrenadeRequest;
import com.grenade.main.dto.GrenadeResponse;
import com.grenade.main.dto.PageDTO;
import com.grenade.main.entity.Grenade;
import com.grenade.main.entity.User;
import com.grenade.main.entity.Media;
import com.grenade.main.entity.Stars;
import com.grenade.main.repo.GrenadeRepo;
import com.grenade.main.repo.MediaRepo;
import com.grenade.main.repo.StarsRepo;
import com.grenade.main.repo.UserRepo;

import jakarta.persistence.EntityNotFoundException;

@Service
public class GrenadeService extends ServiceBase<Grenade, GrenadeResponse, UUID, GrenadeRepo>{
    private final GrenadeRepo grenadeRepo;
    private final UserRepo userRepo;
    private final MediaRepo mediaRepo;
    private final VideoThumbnailExtractor videoThumbnailExtractor;
    private final StarsRepo starsRepo;
    private static final Logger logger = LoggerFactory.getLogger(GrenadeService.class);

    public GrenadeService(GrenadeRepo grenadeRepo, UserRepo userRepo, MediaRepo mediaRepo, VideoThumbnailExtractor videoThumbnailExtractor, StarsRepo starsRepo) {
        super(grenadeRepo);
        this.grenadeRepo = grenadeRepo;
        this.userRepo = userRepo;
        this.mediaRepo = mediaRepo;
        this.starsRepo = starsRepo;
        this.videoThumbnailExtractor = videoThumbnailExtractor;
    }
    
    public GrenadeResponse create(GrenadeRequest grenade){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User with username not found: "+ username));
        Media media = mediaRepo.findByUuid(UUID.fromString(grenade.media())).orElseThrow(() -> new EntityNotFoundException("Media with uuid not found: "+ UUID.fromString(grenade.media())));
        String thumbnail = videoThumbnailExtractor.extractThumbnail(media.getPath(), UUID.randomUUID()+".jpg");
        Grenade grnd = Grenade.builder()
                            .author(user)
                            .media(media.getPath())
                            .thumbnail(thumbnail)
                            .name(grenade.name())
                            .map(grenade.map())
                            .grenadeType(grenade.grenadeType())
                            .side(grenade.side())
                            .command(grenade.command())
                            .movement(grenade.movement())
                            .strength(grenade.strength())
                            .description(grenade.description())
                            .build();
        Grenade entity = Objects.requireNonNull(grnd, "Grenade builder returned null");
        grenadeRepo.save(entity);
        logger.info("Created grenade: {}, by user: {}", entity.getUuid(), entity.getAuthor().getUuid());
        return toDTO(entity);
    }

    public GrenadeResponse getByUuid(UUID uuid){
        Grenade entity = grenadeRepo.findByUuid(uuid).orElseThrow(() -> 
                        new EntityNotFoundException("Entity does not found with uuid:" + uuid));
        return toDTO(entity);
    }

    public GrenadeResponse adminUpdate(@NonNull UUID id, Grenade grnd){
        Grenade existing = toEntity(getByUuid(id));
        Grenade updated = existing.toBuilder()
            .id(grnd.getId())
            .name(grnd.getName())
            .map(grnd.getMap())
            .grenadeType(grnd.getGrenadeType())
            .side(grnd.getSide())
            .command(grnd.getCommand())
            .movement(grnd.getMovement())
            .strength(grnd.getStrength())
            .createdAt(grnd.getCreatedAt())
            .build();
        grenadeRepo.save(Objects.requireNonNull(updated, "Update grenade method returns null"));
        return toDTO(updated);
    }

    public PageDTO<GrenadeResponse> getByFilter(Pageable pageable, Grenade.MapType map,
                                    Grenade.GrenadeType grenade, String sortDirection,
                                    String userUuid, String name, String likedByUserId) {
        Long userId = null;
        if (likedByUserId != null){
            userId = userRepo.findByUuid(UUID.fromString(likedByUserId)).orElseThrow(() -> new EntityNotFoundException("Liked by user was not found with uuid:" + likedByUserId)).getId();
        }
        Long user = null;
        if (userUuid != null) {
            user = userRepo.findByUuid(UUID.fromString(userUuid)).orElseThrow(() -> new EntityNotFoundException("Entity not found with uuid:"+userUuid)).getId();
        }
        Page<Grenade> page = grenadeRepo.findByFilter(pageable, map, grenade, user, name, userId);
        PageDTO<GrenadeResponse> pageDTO = new PageDTO<GrenadeResponse>(page.getContent().stream().map(this::toDTO).toList() , page.getNumber() + 1, page.getTotalPages());
        return pageDTO;
    }

    public void approve(UUID uuid) {
        grenadeRepo.setApprovedTrueByUuid(uuid);
    }

    public PageDTO<GrenadeResponse> getApproved(Pageable pageable, boolean isApproved){
        Page<Grenade> page = grenadeRepo.findApproved(pageable, isApproved);
        return new PageDTO<GrenadeResponse>(page.getContent().stream().map(this::toDTO).toList(), page.getNumber() + 1, page.getTotalPages());
    }

    public List<GrenadeResponse> getByUser(String author) {
        User usr = userRepo.findByUsername(author).orElseThrow(() -> new EntityNotFoundException("User does not found: "+ author));
        return grenadeRepo.findByAuthor(usr)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public boolean isStaredByUser(UUID grUuid){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(username != "anonymousUser") {
            Grenade gr = grenadeRepo.findByUuid(grUuid)
                .orElseThrow(() -> new EntityNotFoundException("Entity with uuid "+grUuid+" was not found"));
            User user = userRepo.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User with name "+username+" was not found2"));
            return starsRepo.existsByUserIdAndGrenadeId(gr.getId(), user.getId());
        }
        return false;
    }

    public Stars getFavourite(UUID id){
        Grenade gr = grenadeRepo.findByUuid(id)
            .orElseThrow(() -> new EntityNotFoundException("Entity with uuid "+id+" was not found"));
        Stars page = starsRepo.findByUserId(gr.getId());
        return page;
    }

    @Override
    public GrenadeResponse toDTO(Grenade grnd) {
        GrenadeResponse dto = GrenadeResponse.builder()
                                .map(grnd.getMap())
                                .uuid(grnd.getUuid())
                                .name(grnd.getName())
                                .grenadeType(grnd.getGrenadeType())
                                .side(grnd.getSide())
                                .media(new File(grnd.getMedia()).getName())
                                .thumbnail(grnd.getThumbnail())
                                .command(grnd.getCommand())
                                .movement(grnd.getMovement())
                                .strength(grnd.getStrength())
                                .description(grnd.getDescription())
                                .authorName(grnd.getAuthor().getUsername())
                                .approved(grnd.isApproved())
                                .stars(grnd.getStars())
                                .likedByMe(isStaredByUser(grnd.getUuid()))
                                .createdAt(grnd.getCreatedAt())
                                .build();
        return dto;
    }

    public Grenade toEntity(GrenadeResponse grnd) {
        Grenade g = Grenade.builder()
                        .map(grnd.map())
                        .name(grnd.name())
                        .grenadeType(grnd.grenadeType())
                        .side(grnd.side())
                        .command(grnd.command())
                        .movement(grnd.movement())
                        .strength(grnd.strength())
                        .createdAt(grnd.createdAt())
                        .build();
        return g;
    }
}