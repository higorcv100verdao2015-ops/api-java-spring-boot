package com.company.nomeprojeto.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroValidacaoDTO> handleValidation(MethodArgumentNotValidException exception) {
        List<String> erros = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return ResponseEntity.badRequest().body(new ErroValidacaoDTO("Dados invalidos.", erros));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroValidacaoDTO> handleIllegalArgument(IllegalArgumentException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErroValidacaoDTO(exception.getMessage(), List.of()));
    }

    public static class ErroValidacaoDTO {

        private String mensagem;
        private List<String> erros;

        public ErroValidacaoDTO(String mensagem, List<String> erros) {
            this.mensagem = mensagem;
            this.erros = erros;
        }

        public String getMensagem() {
            return mensagem;
        }

        public void setMensagem(String mensagem) {
            this.mensagem = mensagem;
        }

        public List<String> getErros() {
            return erros;
        }

        public void setErros(List<String> erros) {
            this.erros = erros;
        }
    }
}
