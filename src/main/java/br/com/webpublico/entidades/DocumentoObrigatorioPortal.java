package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoPessoa;
import br.com.webpublico.enums.TipoSolicitacaoCadastroPessoa;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Length;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@GrupoDiagrama(nome = "Tributário")
@Entity
@Audited
@Etiqueta("Configurações do Portal do Contribuinte")
public class DocumentoObrigatorioPortal extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    @Invisivel
    private Long id;
    @Obrigatorio
    @Length(maximo = 255, minimo = 2)
    private String descricao;
    @ManyToOne
    private ConfiguracaoPortalContribuinte configuracao;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    private TipoPessoa tipoPessoa;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    private TipoSolicitacaoCadastroPessoa tipoSolicitacaoCadastroPessoa;

    public DocumentoObrigatorioPortal() {
        tipoSolicitacaoCadastroPessoa = TipoSolicitacaoCadastroPessoa.TRIBUTARIO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public ConfiguracaoPortalContribuinte getConfiguracao() {
        return configuracao;
    }

    public void setConfiguracao(ConfiguracaoPortalContribuinte configuracao) {
        this.configuracao = configuracao;
    }

    public TipoPessoa getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(TipoPessoa tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public TipoSolicitacaoCadastroPessoa getTipoSolicitacaoCadastroPessoa() {
        return tipoSolicitacaoCadastroPessoa;
    }

    public void setTipoSolicitacaoCadastroPessoa(TipoSolicitacaoCadastroPessoa tipoSolicitacaoCadastroPessoa) {
        this.tipoSolicitacaoCadastroPessoa = tipoSolicitacaoCadastroPessoa;
    }
}
