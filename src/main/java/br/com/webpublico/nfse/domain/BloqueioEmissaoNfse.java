package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.nfse.enums.SituacaoBloqueioEmissaoNfse;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
public class BloqueioEmissaoNfse extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private UsuarioWeb usuarioWeb;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Temporal(TemporalType.DATE)
    private Date dataRegistro;
    @Etiqueta("Situação")
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private SituacaoBloqueioEmissaoNfse situacao;
    @Etiqueta("Cadastro Econômico")
    @Obrigatorio
    @ManyToOne
    private CadastroEconomico cadastroEconomico;
    @Etiqueta("Telefone")
    @Obrigatorio
    private String telefone;
    private String numeroProtocolo;
    private String anoProtocolo;
    @Etiqueta("Motivo")
    @Obrigatorio
    private String motivo;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public SituacaoBloqueioEmissaoNfse getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoBloqueioEmissaoNfse situacao) {
        this.situacao = situacao;
    }

    public UsuarioWeb getUsuarioWeb() {
        return usuarioWeb;
    }

    public void setUsuarioWeb(UsuarioWeb usuarioWeb) {
        this.usuarioWeb = usuarioWeb;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public String getAnoProtocolo() {
        return anoProtocolo;
    }

    public void setAnoProtocolo(String anoProtocolo) {
        this.anoProtocolo = anoProtocolo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
