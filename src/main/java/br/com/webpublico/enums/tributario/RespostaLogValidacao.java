package br.com.webpublico.enums.tributario;

import br.com.webpublico.entidades.SuperEntidade;

import javax.persistence.*;

@Entity
public class RespostaLogValidacao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private LogRemessaProtesto log;

    private String codigoValidacao;
    private String respostaValidacao;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LogRemessaProtesto getLog() {
        return log;
    }

    public void setLog(LogRemessaProtesto log) {
        this.log = log;
    }

    public String getCodigoValidacao() {
        return codigoValidacao;
    }

    public void setCodigoValidacao(String codigoValidacao) {
        this.codigoValidacao = codigoValidacao;
    }

    public String getRespostaValidacao() {
        return respostaValidacao;
    }

    public void setRespostaValidacao(String respostaValidacao) {
        this.respostaValidacao = respostaValidacao;
    }
}
