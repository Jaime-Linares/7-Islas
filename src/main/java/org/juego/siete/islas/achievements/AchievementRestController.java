package org.juego.siete.islas.achievements;

import java.util.List;

import org.juego.siete.islas.auth.payload.response.MessageResponse;
import org.juego.siete.islas.util.RestPreconditions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;


@RestController
@RequestMapping("api/v1/achievements")
@Tag(name = "Achievements", description = "API for the management of achievements")
@SecurityRequirement(name = "bearerAuth")
public class AchievementRestController {

    private final AchievementService achievementService;

    @Autowired
    public AchievementRestController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }
    

    @GetMapping
    public ResponseEntity<List<Achievement>> findAll() {
        return new ResponseEntity<>((List<Achievement>) achievementService.findAll(), HttpStatus.OK);
    }

    @GetMapping(value = "{achievementId}")
    public ResponseEntity<Achievement> findAchievementById(@PathVariable("achievementId") int achievementId) {
        return new ResponseEntity<>(achievementService.findAchievementById(achievementId), HttpStatus.OK);
    }

    @GetMapping(value = "user/{userId}")
    public ResponseEntity<List<Achievement>> findAchievementsByUserId(@PathVariable("userId") int userId) {
        return new ResponseEntity<>(achievementService.findAchievementByUserId(userId), HttpStatus.OK);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MessageResponse> saveAchievement(@RequestBody @Valid Achievement achievement)
            throws Exception {
        if (achievementService.existsAchievementByName(achievement.getName()).equals(true)) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Este nombre de logro ya ha sido escogido, prueba con otro."));
        }
        Achievement newAchievement = new Achievement();
        BeanUtils.copyProperties(achievement, newAchievement, "id");
        achievementService.saveAchievement(newAchievement);
        return new ResponseEntity<>(new MessageResponse("Achievement created!!"), HttpStatus.CREATED);
    }

    @PutMapping(value = "{achievementId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MessageResponse> updateAchievementById(@RequestBody @Valid Achievement achievement, @PathVariable("achievementId") int achievementId) throws Exception {
        Achievement repAchievement = achievementService.findAchievementByName(achievement.getName()).orElse(null);
        if (repAchievement != null) {
            if (achievementService.existsAchievementByName(achievement.getName()).equals(true) && repAchievement.getId() != achievement.getId()) {
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("Este nombre de logro ya ha sido escogido, prueba con otro."));
            }
        }
        RestPreconditions.checkNotNull(achievementService.findAchievementById(achievementId),
                "Achievement", "ID", achievementId);
        achievementService.updateAchievement(achievement, achievementId);
        return ResponseEntity.ok(new MessageResponse("¡Logro actualizado correctamente!"));
    }

    @DeleteMapping(value = "{achievementId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MessageResponse> deleteAchievement(@PathVariable("achievementId") int achievementId) {
        RestPreconditions.checkNotNull(achievementService.findAchievementById(achievementId),
                "Achievement", "ID", achievementId);
        achievementService.deleteAchievement(achievementId);
        return new ResponseEntity<>(new MessageResponse("Achievement deleted!"), HttpStatus.OK);
    }

}
