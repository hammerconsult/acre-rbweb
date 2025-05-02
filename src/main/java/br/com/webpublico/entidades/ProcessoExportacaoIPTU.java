package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.PossuidorArquivo;

import javax.persistence.*;
import java.util.Date;

@Entity
public class ProcessoExportacaoIPTU extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private UsuarioSistema usuario;

    private Integer quantidadeLinhas;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataHora;
    @ManyToOne(cascade = CascadeType.ALL, optional = true)
    private DetentorArquivoComposicao detentorArquivoComposicao;
    private boolean novoSequencial;

    public ProcessoExportacaoIPTU() {
        this.dataHora = new Date();
        this.novoSequencial = Boolean.FALSE;
    }

    public Integer getQuantidadeLinhas() {
        return quantidadeLinhas;
    }

    public void setQuantidadeLinhas(Integer quantidadeLinhas) {
        this.quantidadeLinhas = quantidadeLinhas;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
    }

    public Date getDataHora() {
        return dataHora;
    }

    public void setDataHora(Date dataHoraLogin) {
        this.dataHora = dataHoraLogin;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public boolean isNovoSequencial() {
        return novoSequencial;
    }

    public void setNovoSequencial(boolean novoSequencial) {
        this.novoSequencial = novoSequencial;
    }

    @Override
    public Long getId() {
        return id;
    }
}
