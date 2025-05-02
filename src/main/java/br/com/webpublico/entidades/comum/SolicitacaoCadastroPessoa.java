package br.com.webpublico.entidades.comum;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.TipoSolicitacaoCadastroPessoa;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

@Entity
public class SolicitacaoCadastroPessoa extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    private String cpfCnpj;
    private String email;
    private String key;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataExpiracao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataConclusao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRejeicao;
    @ManyToOne
    @JsonIgnore
    private UsuarioSistema usuarioRejeicao;
    private String motivoRejeicao;
    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private TipoSolicitacaoCadastroPessoa tipo;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpf) {
        this.cpfCnpj = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Date getDataExpiracao() {
        return dataExpiracao;
    }

    public void setDataExpiracao(Date dataExpiracao) {
        this.dataExpiracao = dataExpiracao;
    }

    public Date getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(Date dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public Date getDataRejeicao() {
        return dataRejeicao;
    }

    public void setDataRejeicao(Date dataRejeicao) {
        this.dataRejeicao = dataRejeicao;
    }

    @JsonIgnore
    public UsuarioSistema getUsuarioRejeicao() {
        return usuarioRejeicao;
    }

    public void setUsuarioRejeicao(UsuarioSistema usuarioRejeicao) {
        this.usuarioRejeicao = usuarioRejeicao;
    }

    public String getMotivoRejeicao() {
        return motivoRejeicao;
    }

    public void setMotivoRejeicao(String motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }

    @JsonIgnore
    public TipoSolicitacaoCadastroPessoa getTipo() {
        return tipo;
    }

    public void setTipo(TipoSolicitacaoCadastroPessoa tipo) {
        this.tipo = tipo;
    }
}
