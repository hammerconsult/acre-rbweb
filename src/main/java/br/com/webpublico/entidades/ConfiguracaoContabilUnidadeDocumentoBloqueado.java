package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@GrupoDiagrama(nome = "Contábil")
@Audited
@Table(name = "CONFIGCUNIDADEDOCBLOQ")
public class ConfiguracaoContabilUnidadeDocumentoBloqueado extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Configuração Contábil")
    private ConfiguracaoContabil configuracaoContabil;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Unidade Orçamentária")
    private UnidadeOrganizacional unidadeOrganizacional;
    @Obrigatorio
    @Etiqueta("Unidade Bloqueada para novo Documento Comprobatório na Liquidação? ")
    private Boolean bloqueado;
    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    public ConfiguracaoContabilUnidadeDocumentoBloqueado() {
        super();
        bloqueado = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public ConfiguracaoContabil getConfiguracaoContabil() {
        return configuracaoContabil;
    }

    public void setConfiguracaoContabil(ConfiguracaoContabil configuracaoContabil) {
        this.configuracaoContabil = configuracaoContabil;
    }

    public Boolean getBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(Boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }
}
