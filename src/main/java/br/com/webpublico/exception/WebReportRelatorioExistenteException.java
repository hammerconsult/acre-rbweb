package br.com.webpublico.exception;

import br.com.webpublico.report.WebReportDTO;

/**
 * Renato Romanini
 */
public class WebReportRelatorioExistenteException extends RuntimeException {

    private  WebReportDTO webReportDTO;

    public WebReportRelatorioExistenteException(String message, WebReportDTO webReportDTO) {
        super(message);
        this.webReportDTO = webReportDTO;
    }

    public WebReportDTO getWebReportDTO() {
        return webReportDTO;
    }
}
