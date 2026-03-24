package com.grenade.main.service;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.grenade.main.dto.GrenadeRequest;
import com.grenade.main.dto.GrenadeResponse;
import com.grenade.main.dto.PageDTO;
import com.grenade.main.entity.Grenade;
import com.grenade.main.entity.User;
import com.grenade.main.entity.Media;
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

    public String generateName(){
        List<String> words1 = List.of("iron", "shadow", "frost", "storm", "dark", "blood", "silver", "golden");
        List<String> words2 = List.of("blade", "edge", "fang", "claw", "strike", "born", "forge", "bane");

        Random random = new Random();

        String name = words1.get(random.nextInt(words1.size()))+"_"+words2.get(random.nextInt(words2.size()));
        return name;
    }
    
    public GrenadeResponse create(GrenadeRequest grenade){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByUsername(username).orElseThrow(() -> 
            new EntityNotFoundException("User with username not found: "+ username));
        String name = grenade.name;
        if(grenade.name == null || grenade.name.isBlank()){
            name = generateName();
        } 
        Grenade.GrenadeBuilder grnd = Grenade.builder()
                            .author(user)
                            .name(name)
                            .map(grenade.map)
                            .grenadeType(grenade.grenadeType)
                            .side(grenade.side)
                            .speed(grenade.speed)
                            .command(grenade.command)
                            .buttons(grenade.buttons)
                            .description(grenade.description);
        if (grenade.media != null && !grenade.media.isBlank()) {
            Media media = mediaRepo.findByUuid(UUID.fromString(grenade.media)).orElseThrow(() -> 
                new EntityNotFoundException("Media with uuid not found: "+ UUID.fromString(grenade.media)));
            String thumbnail = videoThumbnailExtractor.extractThumbnail(media.getPath(), UUID.randomUUID()+".jpg");
            grnd.media(media.getPath()).thumbnail(thumbnail);
        }
        Grenade entity = Objects.requireNonNull(grnd.build(), "Grenade builder returned null");
        grenadeRepo.save(entity);
        logger.info("Created grenade: {}, by user: {}", entity.getUuid(), entity.getAuthor().getUuid());
        return toDTO(entity);
    }

    public GrenadeResponse getByUuid(UUID uuid){
        Grenade entity = grenadeRepo.findByUuid(uuid).orElseThrow(() -> 
                        new EntityNotFoundException("Entity does not found with uuid:" + uuid));
        return toDTO(entity);
    }

    @SuppressWarnings("null")
    public GrenadeResponse update(UUID uuid, GrenadeRequest grnd){
    Authentication user = SecurityContextHolder.getContext().getAuthentication();
    Grenade existing = grenadeRepo.findByUuid(uuid)
        .orElseThrow(() -> new EntityNotFoundException("Entity not found: " + uuid));
    boolean isOwner = existing.getAuthor().getUsername().equals(user.getName());
    boolean isAdmin = user.getAuthorities().stream()
        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    
    if (!isOwner && !isAdmin){
        throw new AccessDeniedException("You can only update your own grenades");
    }
    
    Grenade.GrenadeBuilder builder = existing.toBuilder();
    
    if(grnd.name != null) builder.name(grnd.name);
    if(grnd.command != null) builder.command(grnd.command);
    if(grnd.map != null) builder.map(grnd.map);
    if(grnd.grenadeType != null) builder.grenadeType(grnd.grenadeType);
    if(grnd.side != null) builder.side(grnd.side);
    if(grnd.speed != null) builder.speed(grnd.speed);
    if(grnd.buttons != null) builder.buttons(grnd.buttons);
    if(grnd.description != null) builder.description(grnd.description);
    
    if(grnd.media != null && !grnd.media.isBlank()){
        Media media = mediaRepo.findByUuid(UUID.fromString(grnd.media))
            .orElseThrow(() -> new EntityNotFoundException("Media not found: " + grnd.media));
        String thumbnail = videoThumbnailExtractor.extractThumbnail(
            media.getPath(), UUID.randomUUID()+".jpg");
        builder.media(media.getPath()).thumbnail(thumbnail);
    }
    UUID userUuid = userRepo.findByUsername(user.getName()).orElseThrow(() -> 
            new EntityNotFoundException("User was not exists with username " + user.getName())).getUuid();
    Grenade updated = builder.build();
    grenadeRepo.save(updated);
    logger.info("Updated grenade: {}, by user: {}", uuid, userUuid);
    return toDTO(updated);
}

    public PageDTO<GrenadeResponse> getByFilter(Pageable pageable, Grenade.MapType map,
                                    Grenade.GrenadeType grenade, String sortDirection,
                                    String authorUuid, String name, String likedByUserId) {
        Long userId = null;
        if (likedByUserId != null){
            userId = userRepo.findByUuid(UUID.fromString(likedByUserId)).orElseThrow(() -> new EntityNotFoundException("Liked by user was not found with uuid:" + likedByUserId)).getId();
        }
        Long user = null;
        if (authorUuid != null) {
            user = userRepo.findByUuid(UUID.fromString(authorUuid)).orElseThrow(() -> new EntityNotFoundException("Entity not found with uuid:"+authorUuid)).getId();
        }
        Page<Grenade> page = grenadeRepo.findByFilter(pageable, map, grenade, user, name, userId);
        logger.debug("");
        PageDTO<GrenadeResponse> pageDTO = new PageDTO<GrenadeResponse>(page.getContent().stream().map(this::toDTO).toList() , page.getNumber() + 1, page.getTotalPages());
        return pageDTO;
    }

    public PageDTO<GrenadeResponse> getUreadyGrenade(Pageable pageable){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long userId = userRepo.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("Entity not found with username:" + username)).getId();
        Page<Grenade> page = grenadeRepo.findUnreadyByAuthor(pageable, userId);
        PageDTO<GrenadeResponse> pageDTO = new PageDTO<GrenadeResponse>(page.getContent().stream().map(this::toDTO).toList(), page.getNumber() + 1, page.getTotalPages());
        return pageDTO;
    }

    public void setReadyToGrenade(UUID uuid){
        grenadeRepo.setReadyTrueByUuid(uuid);
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
        if(username.equals("anonymousUser")) {
            return false;
        }
        Grenade gr = grenadeRepo.findByUuid(grUuid)
                .orElseThrow(() -> new EntityNotFoundException("Entity with uuid "+grUuid+" was not found"));
        User user = userRepo.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User with name "+username+" was not found2"));
        return starsRepo.existsByUserIdAndGrenadeId(user.getId(), gr.getId());
    }

    @Override
    public GrenadeResponse toDTO(Grenade grnd) {
        GrenadeResponse.GrenadeResponseBuilder dto = GrenadeResponse.builder()
                                .map(grnd.getMap())
                                .uuid(grnd.getUuid())
                                .name(grnd.getName())
                                .isReady(grnd.isReady())
                                .grenadeType(grnd.getGrenadeType())
                                .side(grnd.getSide())
                                .speed(grnd.getSpeed())
                                .command(grnd.getCommand())
                                .buttons(grnd.getButtons())
                                .description(grnd.getDescription())
                                .authorName(grnd.getAuthor().getUsername())
                                .approved(grnd.isApproved())
                                .stars(grnd.getStars())
                                .likedByMe(isStaredByUser(grnd.getUuid()))
                                .createdAt(grnd.getCreatedAt());
        if (grnd.getMedia() != null && !grnd.getMedia().isBlank()) {
            dto.media(new File(grnd.getMedia()).getName()).thumbnail(grnd.getThumbnail());
        }
        return dto.build();
    }
}