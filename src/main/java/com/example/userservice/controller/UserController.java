package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Tag(name = "User Controller", description = "Контроллер User-Service")
@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @Operation(
            summary = "Создать пользователя",
            description = "Добавляет нового пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
            @ApiResponse(responseCode = "500", description = "Ошибка на сервере")
    })
    @PostMapping
    public ResponseEntity<EntityModel<UserDto>> create(
            @Valid @RequestBody UserDto dto
    ) {
        UserDto created = service.create(dto);
        log.info("log method create completed");

        EntityModel<UserDto> model = EntityModel.of(created,
                linkTo(methodOn(UserController.class).get(created.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).list()).withRel("all-users"),
                linkTo(methodOn(UserController.class).update(created.getId(), dto)).withRel("update"),
                linkTo(methodOn(UserController.class).delete(created.getId())).withRel("delete"));

        return ResponseEntity
                .created(linkTo(methodOn(UserController.class).get(created.getId())).toUri())
                .body(model);
    }

    @Operation(
            summary = "Получить пользователя по ID",
            description = "Возвращает пользователя по ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserDto>> get(
            @Parameter(description = "ID", example = "1")
            @PathVariable Long id
    ) {
        UserDto result = service.getById(id);
        log.info("log method get completed");

        EntityModel<UserDto> model = EntityModel.of(result,
                linkTo(methodOn(UserController.class).get(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).list()).withRel("all-users"),
                linkTo(methodOn(UserController.class).update(id, result)).withRel("update"),
                linkTo(methodOn(UserController.class).delete(id)).withRel("delete"));

        return ResponseEntity.ok(model);
    }

    @Operation(
            summary = "Получить всех пользователей",
            description = "Возвращает список всех пользователей"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список успешно получен")
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UserDto>>> list() {
        List<EntityModel<UserDto>> users = service.getAll().stream()
                .map(u -> EntityModel.of(u,
                        linkTo(methodOn(UserController.class).get(u.getId())).withSelfRel(),
                        linkTo(methodOn(UserController.class).update(u.getId(), u)).withRel("update"),
                        linkTo(methodOn(UserController.class).delete(u.getId())).withRel("delete")))
                .toList();

        CollectionModel<EntityModel<UserDto>> collection = CollectionModel.of(users,
                linkTo(methodOn(UserController.class).list()).withSelfRel());

        log.info("log method list completed");
        return ResponseEntity.ok(collection);
    }

    @Operation(
            summary = "Обновить данные пользователя",
            description = "Обновляет существующего пользователя по ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлён"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UserDto>> update(
            @Parameter(description = "ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody UserDto dto
    ) {
        UserDto updated = service.update(id, dto);
        log.info("log method update completed");

        EntityModel<UserDto> model = EntityModel.of(updated,
                linkTo(methodOn(UserController.class).get(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).list()).withRel("all-users"),
                linkTo(methodOn(UserController.class).delete(id)).withRel("delete"));

        return ResponseEntity.ok(model);
    }

    @Operation(
            summary = "Удалить пользователя",
            description = "Удаляет пользователя по ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь успешно удалён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID", example = "1") @PathVariable Long id
    ) {
        service.delete(id);
        log.info("log method delete completed");
        return ResponseEntity.noContent().build();
    }
}
