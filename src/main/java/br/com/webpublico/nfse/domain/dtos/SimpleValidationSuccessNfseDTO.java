package br.com.webpublico.nfse.domain.dtos;


public class SimpleValidationSuccessNfseDTO {

    private String title;
    private String message;

    public SimpleValidationSuccessNfseDTO() {
    }

    public String getMessage() {
        return message;
    }

    public SimpleValidationSuccessNfseDTO(String title, String message) {
        this.message = message;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
