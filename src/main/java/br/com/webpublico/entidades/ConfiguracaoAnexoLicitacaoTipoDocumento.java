package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoDocumentoAnexoPNCP;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Table(name = "CONFIGURACAOANEXOLICDOC")
public class ConfiguracaoAnexoLicitacaoTipoDocumento extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Obrigatório")
    private Boolean obrigatorio;

    @ManyToOne
    @Etiqueta("Tipo Documento Anexo")
    private TipoDocumentoAnexo tipoDocumentoAnexo;

    @ManyToOne
    @Etiqueta("Configuração Anexo Licitação")
    private ConfiguracaoAnexoLicitacao configuracaoAnexoLicitacao;

    public ConfiguracaoAnexoLicitacaoTipoDocumento() {
        obrigatorio = false;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getObrigatorio() {
        return obrigatorio;
    }

    public void setObrigatorio(Boolean obrigatorio) {
        this.obrigatorio = obrigatorio;
    }

    public TipoDocumentoAnexo getTipoDocumentoAnexo() {
        return tipoDocumentoAnexo;
    }

    public void setTipoDocumentoAnexo(TipoDocumentoAnexo tipoDocumentoAnexo) {
        this.tipoDocumentoAnexo = tipoDocumentoAnexo;
    }

    public ConfiguracaoAnexoLicitacao getConfiguracaoAnexoLicitacao() {
        return configuracaoAnexoLicitacao;
    }

    public void setConfiguracaoAnexoLicitacao(ConfiguracaoAnexoLicitacao configuracaoAnexoLicitacao) {
        this.configuracaoAnexoLicitacao = configuracaoAnexoLicitacao;
    }
}
