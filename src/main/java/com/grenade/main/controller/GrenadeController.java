package com.grenade.main.controller;

import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.grenade.main.dto.GrenadeRequest;
import com.grenade.main.dto.GrenadeResponse;
import com.grenade.main.dto.MediaDTO;
import com.grenade.main.dto.PageDTO;
import com.grenade.main.dto.ReadyDTO;
import com.grenade.main.entity.Grenade;
import com.grenade.main.service.GrenadeService;
import com.grenade.main.service.MediaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/grenades")
@SecurityRequirement(name = "token")
@AllArgsConstructor
public class GrenadeController {

    private static final String api = "/api/grenades";

    private final GrenadeService grenadeService;
    private final MediaService mediaService;
    private static final Logger logger = LoggerFactory.getLogger(GrenadeController.class);

    @Tag(name = "user")
    @Operation(summary = "Create grenade")
    @PostMapping()
    public ResponseEntity<GrenadeResponse> create(@RequestBody GrenadeRequest grenade) {
        logger.info("POST {}" ,api);
        return new ResponseEntity<>(grenadeService.create(grenade), HttpStatus.CREATED);
    }

    @Tag(name = "user")
    @Operation(summary = "Update grenade")
    @PutMapping("/{id}")
    public ResponseEntity<GrenadeResponse> update(@PathVariable UUID id, @RequestBody GrenadeRequest grenade) {
        logger.info("PUT {} Grenade info:",api,grenade);
        return new ResponseEntity<>(grenadeService.update(id, grenade), HttpStatus.OK);
    }

    @Tag(name = "user")
    @Operation(summary = "Upload video for grenade")
    @PostMapping(value = "/video",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MediaDTO> saveVideo(MultipartFile file){
        logger.info("POST {}/video format={}, name={}", api, file.getContentType(), file.getOriginalFilename());
        return new ResponseEntity<>(mediaService.create(file), HttpStatus.CREATED);
    }

    @Tag(name = "public")
    @Operation(summary = "Get grenade by uuid")
    @GetMapping("/{uuid}")
    public ResponseEntity<GrenadeResponse> getByUuid(@PathVariable UUID uuid){
        logger.info("GET {}/{}",api,uuid);
        return new ResponseEntity<>(grenadeService.getByUuid(uuid), HttpStatus.OK);
    }

    @Tag(name = "public")
    @Operation(summary = "Get filtered Grenades")
    @GetMapping
    public ResponseEntity<PageDTO<GrenadeResponse>> getByFilter(@RequestParam(defaultValue = "1") int p,
                                                                @RequestParam(defaultValue = "5") int s,
                                                                @RequestParam(required = false) Grenade.MapType map,
                                                                @RequestParam(required = false) Grenade.GrenadeType grenade,
                                                                @RequestParam(required = false) String sortdirection,
                                                                @RequestParam(required = false) String userUuid,
                                                                @RequestParam(required = false) String name,
                                                                @RequestParam(required = false) String likedByUserId) {
         Sort sort = Sort.by(Sort.Direction.DESC, "stars");
        if (sortdirection != null && !sortdirection.isBlank()) {
            sort = Sort.by(sortdirection.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, "stars");
        }
        logger.info("GET {} Params: p={}, s={}, map={}, grenade={}, sortDirection={}, userUuid={}, name={}, likedByUserId={}", 
                                api, p , s, map, grenade, sortdirection, userUuid, name, likedByUserId);
        return new ResponseEntity<>(grenadeService.getByFilter(PageRequest.of(p-1, s, sort), map, grenade, sortdirection, userUuid, name, likedByUserId), HttpStatus.OK);
    }

    @Tag(name = "admin")
    @Operation(summary = "Get user unredy Grenades")
    @GetMapping("/unready")
    public ResponseEntity<PageDTO<GrenadeResponse>> getUnready(@RequestParam(defaultValue = "1") int p,
                                                                @RequestParam(defaultValue = "5") int s){
        logger.info("PATCH {}/unready",api);
        return new ResponseEntity<>(grenadeService.getUreadyGrenade(PageRequest.of(p-1, s)), HttpStatus.OK);
    }

    @Tag(name = "admin")
    @Operation(summary = "Set user grenade ready")
    @PatchMapping("/{id}/ready")
    public ResponseEntity<GrenadeResponse> setReady(@PathVariable UUID id, @RequestBody ReadyDTO readyDTO){
        logger.info("PATCH {}/{}/ready",api , id);
        grenadeService.setReadyToGrenade(id);
        return new ResponseEntity<>(grenadeService.getByUuid(id) ,HttpStatus.OK);
    }
    
    @Tag(name = "user")
    @Operation(summary = "Delete grenade by uuid")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        logger.info("DELETE {}/{}",api , id);
        grenadeService.delete(Objects.requireNonNull(id));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Tag(name = "admin")
    @Operation(summary = "Get unaproved grenades")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/unapproved")
    public ResponseEntity<PageDTO<GrenadeResponse>> getNotApprovedGrenades(@RequestParam(defaultValue = "1") int p,
                                                                            @RequestParam(defaultValue = "5") int s,
                                                                            @RequestParam(defaultValue = "false") boolean isApproved) {
        logger.info("GET {}/admin Params: p={}, s={}, isApproved={}",api, p, s, isApproved);
        return new ResponseEntity<>(grenadeService.getApproved(PageRequest.of(p,s), isApproved) ,HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Tag(name = "admin")
    @Operation(summary = "Approve grenade")
    @GetMapping("/unapproved/{uuid}")
    public ResponseEntity<Void> approveGrenade(@PathVariable UUID uuid){
        logger.info("GET {}/admin/{}",api, uuid);
        grenadeService.approve(uuid);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
