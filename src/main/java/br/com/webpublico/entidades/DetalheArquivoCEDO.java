package br.com.webpublico.entidades;

import br.com.webpublico.tributario.enumeration.MotivoDevolucaoArquivoCEDO;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class DetalheArquivoCEDO extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private ArquivoCEDO arquivoCEDO;
    private String identificacaoObjeto;
    @Enumerated(EnumType.STRING)
    private MotivoDevolucaoArquivoCEDO motivoDevolucao;
    private String sequencial;
    private String inscricao;
    @ManyToOne
    private CadastroImobiliario cadastroImobiliario;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public ArquivoCEDO getArquivoCEDO() {
        return arquivoCEDO;
    }

    public void setArquivoCEDO(ArquivoCEDO arquivoCEDO) {
        this.arquivoCEDO = arquivoCEDO;
    }

    public String getIdentificacaoObjeto() {
        return identificacaoObjeto;
    }

    public void setIdentificacaoObjeto(String identificacaoObjeto) {
        this.identificacaoObjeto = identificacaoObjeto;
    }

    public MotivoDevolucaoArquivoCEDO getMotivoDevolucao() {
        return motivoDevolucao;
    }

    public void setMotivoDevolucao(MotivoDevolucaoArquivoCEDO motivoDevolucao) {
        this.motivoDevolucao = motivoDevolucao;
    }

    public String getSequencial() {
        return sequencial;
    }

    public void setSequencial(String sequencial) {
        this.sequencial = sequencial;
    }

    public String getInscricao() {
        return inscricao;
    }

    public void setInscricao(String inscricao) {
        this.inscricao = inscricao;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }
}
