package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Table(name = "MENSAGEMCONTRIBUSUARIO")
@Entity
@Audited
public class MensagemContribuinteUsuario extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private MensagemContribuinte mensagemContribuinte;

    @ManyToOne
    private CadastroEconomico cadastroEconomico;

    private Boolean lida;

    @ManyToOne
    private UsuarioWeb lidaPor;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataLeitura;

    private String resposta;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MensagemContribuinteUsuarioDocumento> documentos;

    public MensagemContribuinteUsuario() {
        super();
        this.lida = Boolean.FALSE;
        this.documentos = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MensagemContribuinte getMensagemContribuinte() {
        return mensagemContribuinte;
    }

    public void setMensagemContribuinte(MensagemContribuinte mensagemContribuinte) {
        this.mensagemContribuinte = mensagemContribuinte;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public Boolean getLida() {
        return lida;
    }

    public void setLida(Boolean lida) {
        this.lida = lida;
    }

    public UsuarioWeb getLidaPor() {
        return lidaPor;
    }

    public void setLidaPor(UsuarioWeb lidaPor) {
        this.lidaPor = lidaPor;
    }

    public Date getDataLeitura() {
        return dataLeitura;
    }

    public void setDataLeitura(Date dataLeitura) {
        this.dataLeitura = dataLeitura;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public List<MensagemContribuinteUsuarioDocumento> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<MensagemContribuinteUsuarioDocumento> documentos) {
        this.documentos = documentos;
    }
}

