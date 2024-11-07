package hhplus.concertreservationservice.presentation.queue.controller;

import hhplus.concertreservationservice.global.exception.ErrorResponse;
import hhplus.concertreservationservice.presentation.queue.dto.QueueRequest;
import hhplus.concertreservationservice.presentation.queue.dto.QueueResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

public interface IQueueController {

    @Operation(summary = "대기열 생성", description = "사용자를 대기열에 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "대기열 생성 성공 또는 WAITING된 대기열과 순번 반환 또는 PASS된 대기열 반환",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = QueueResponse.Enqueue.class))),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음 (USER_NOT_FOUND)",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/enqueue")
    ResponseEntity<Void> enqueue(
        @RequestBody(description = "대기열 생성 요청 정보", required = true,
            content = @Content(mediaType = "application/json",
                schema = @Schema(example = "{\n"
                    + "  \"userId\": 1\n"
                    + "}")))
        @Valid QueueRequest.Enqueue request
    );
}
