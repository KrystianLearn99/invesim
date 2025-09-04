package org.kris.invesim.userms;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileRepository repository;

    @PostMapping("/me")
    public ResponseEntity<UserProfile> createOrUpdateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody @Valid UserProfile incoming) {

        UUID externalId = UUID.fromString(jwt.getSubject());

        UserProfile profile = repository.findByExternalId(externalId)
                .orElseGet(() -> UserProfile.builder()
                        .externalId(externalId)
                        .build());

        profile.setPreferredCurrency(incoming.getPreferredCurrency());
        profile.setDefaultStrategy(incoming.getDefaultStrategy());

        return ResponseEntity.ok(repository.save(profile));
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfile> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        UUID externalId = UUID.fromString(jwt.getSubject());
        return repository.findByExternalId(externalId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}



