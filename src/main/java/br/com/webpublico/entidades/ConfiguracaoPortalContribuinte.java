package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@GrupoDiagrama(nome = "Tributário")
@Entity
@Audited
@Etiqueta("Configurações do Portal do Contribuinte")
@Table(name = "CONFIGPORTALCONTRIBUINTE")
public class ConfiguracaoPortalContribuinte extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    @Invisivel
    private Long id;
    @OneToMany(mappedBy = "configuracao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentoObrigatorioPortal> documentosObrigatorios = Lists.newArrayList();
    @ManyToOne
    private Divida dividaCredor;
    @ManyToOne
    private Tributo tributoCredor;
    private BigDecimal porcentagemUfmCredor;
    @OneToMany(mappedBy = "configuracao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsuarioPermissaoAprovar> permissoesAprovacaoCredores;
    @OneToMany(mappedBy = "configuracao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HtmlEstaticoPortalContribuinte> htmls;

    public ConfiguracaoPortalContribuinte() {
        permissoesAprovacaoCredores = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<DocumentoObrigatorioPortal> getDocumentosObrigatorios() {
        return documentosObrigatorios;
    }

    public void setDocumentosObrigatorios(List<DocumentoObrigatorioPortal> documentosObrigatorios) {
        this.documentosObrigatorios = documentosObrigatorios;
    }

    public Divida getDividaCredor() {
        return dividaCredor;
    }

    public void setDividaCredor(Divida dividaCredor) {
        this.dividaCredor = dividaCredor;
    }

    public Tributo getTributoCredor() {
        return tributoCredor;
    }

    public void setTributoCredor(Tributo tributoCredor) {
        this.tributoCredor = tributoCredor;
    }

    public BigDecimal getPorcentagemUfmCredor() {
        return porcentagemUfmCredor;
    }

    public void setPorcentagemUfmCredor(BigDecimal porcentagemUfmCredor) {
        this.porcentagemUfmCredor = porcentagemUfmCredor;
    }

    public List<UsuarioPermissaoAprovar> getPermissoesAprovacaoCredores() {
        return permissoesAprovacaoCredores;
    }

    public void setPermissoesAprovacaoCredores(List<UsuarioPermissaoAprovar> permissoesAprovacaoCredores) {
        this.permissoesAprovacaoCredores = permissoesAprovacaoCredores;
    }

    public List<HtmlEstaticoPortalContribuinte> getHtmls() {
        if (htmls == null) htmls = Lists.newArrayList();
        return htmls;
    }

    public void setHtmls(List<HtmlEstaticoPortalContribuinte> htmls) {
        this.htmls = htmls;
    }
}
