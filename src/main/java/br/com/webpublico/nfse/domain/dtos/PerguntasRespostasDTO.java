package br.com.webpublico.nfse.domain.dtos;

/**
 * Created by wellington on 20/10/17.
 */
public class PerguntasRespostasDTO implements NfseDTO {

    private Long id;
    private AssuntoNfseDTO assuntoNfseDTO;
    private String pergunta;
    private String resposta;
    private Integer ordem;
    private Boolean habilitarExibicao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AssuntoNfseDTO getAssuntoNfseDTO() {
        return assuntoNfseDTO;
    }

    public void setAssuntoNfseDTO(AssuntoNfseDTO assuntoNfseDTO) {
        this.assuntoNfseDTO = assuntoNfseDTO;
    }

    public String getPergunta() {
        return pergunta;
    }

    public void setPergunta(String pergunta) {
        this.pergunta = pergunta;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public Boolean getHabilitarExibicao() {
        return habilitarExibicao;
    }

    public void setHabilitarExibicao(Boolean habilitarExibicao) {
        this.habilitarExibicao = habilitarExibicao;
    }
}
