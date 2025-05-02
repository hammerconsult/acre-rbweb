package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Table(name = "UNIDADEGRUPOOBJCOMP")
@GrupoDiagrama(nome = "Configuração Licitação")
@Etiqueta("Unidade Grupo Objeto de Compra")
public class UnidadeGrupoObjetoCompra extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Etiqueta("Configuração de Licitação")
    @ManyToOne
    private ConfiguracaoLicitacao configuracaoLicitacao;

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;

    @Etiqueta("Unidade Organizacional")
    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    public UnidadeGrupoObjetoCompra() {
    }

    @Override
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

    public ConfiguracaoLicitacao getConfiguracaoLicitacao() {
        return configuracaoLicitacao;
    }

    public void setConfiguracaoLicitacao(ConfiguracaoLicitacao configuracaoLicitacao) {
        this.configuracaoLicitacao = configuracaoLicitacao;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }
}
