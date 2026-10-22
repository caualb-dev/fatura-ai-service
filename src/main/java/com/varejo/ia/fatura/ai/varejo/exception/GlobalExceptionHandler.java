package com.varejo.ia.fatura.ai.varejo.exception;

import com.varejo.ia.fatura.ai.varejo.dto.ErroRespostaDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroRespostaDTO> tratarArgumentoInvalido(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        ErroRespostaDTO erro = new ErroRespostaDTO(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Requisição Inválida",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErroRespostaDTO> tratarArquivoAusente(
            MissingServletRequestPartException ex,
            HttpServletRequest request
    ) {
        ErroRespostaDTO erro = new ErroRespostaDTO(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Arquivo Ausente",
                "O parâmetro obrigatório '" + ex.getRequestPartName() + "' não foi enviado na requisição.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErroRespostaDTO> tratarTamanhoMaximoExcedido(
            MaxUploadSizeExceededException ex,
            HttpServletRequest request
    ) {
        ErroRespostaDTO erro = new ErroRespostaDTO(
                Instant.now(),
                HttpStatus.CONTENT_TOO_LARGE.value(),
                "Arquivo Muito Grande",
                "O arquivo enviado excede o limite máximo permitido de 20MB.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE).body(erro);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroRespostaDTO> tratarErroGenerico(
            Exception ex,
            HttpServletRequest request
    ) {
        ErroRespostaDTO erro = new ErroRespostaDTO(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro no Processamento da Fatura",
                ex.getMessage() != null ? ex.getMessage() : "Ocorreu um erro inesperado ao processar o documento fiscal.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}
