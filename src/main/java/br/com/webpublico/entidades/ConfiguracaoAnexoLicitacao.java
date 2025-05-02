package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoMovimentoProcessoLicitatorio;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Entity
@Audited
public class ConfiguracaoAnexoLicitacao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Recurso (Tela)")
    private TipoMovimentoProcessoLicitatorio recursoTela;

    @ManyToOne
    @Etiqueta("Configuração de Licitação")
    private ConfiguracaoLicitacao configuracaoLicitacao;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "configuracaoAnexoLicitacao", orphanRemoval = true)
    private List<ConfiguracaoAnexoLicitacaoTipoDocumento> tiposDocumentos;


    public ConfiguracaoAnexoLicitacao() {
        tiposDocumentos = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoMovimentoProcessoLicitatorio getRecursoTela() {
        return recursoTela;
    }

    public void setRecursoTela(TipoMovimentoProcessoLicitatorio recursoTela) {
        this.recursoTela = recursoTela;
    }

    public ConfiguracaoLicitacao getConfiguracaoLicitacao() {
        return configuracaoLicitacao;
    }

    public void setConfiguracaoLicitacao(ConfiguracaoLicitacao configuracaoLicitacao) {
        this.configuracaoLicitacao = configuracaoLicitacao;
    }

    public List<ConfiguracaoAnexoLicitacaoTipoDocumento> getTiposDocumentos() {
        return tiposDocumentos;
    }

    public void setTiposDocumentos(List<ConfiguracaoAnexoLicitacaoTipoDocumento> tiposDocumento) {
        this.tiposDocumentos = tiposDocumento;
    }
}
