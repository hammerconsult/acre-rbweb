package br.com.webpublico.entidades;

import br.com.webpublico.enums.ModalidadeLicitacao;
import br.com.webpublico.enums.TipoNaturezaDoProcedimentoLicitacao;
import br.com.webpublico.enums.TipoReservaDotacaoConfigLicitacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by Wellington Abdo on 29/11/2016.
 */
@Entity
@Audited
public class ConfiguracaoReservaDotacao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Configuração de Licitação")
    private ConfiguracaoLicitacao configuracaoLicitacao;

    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Modalidade")
    private ModalidadeLicitacao modalidadeLicitacao;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Natureza do Procedimento")
    private TipoNaturezaDoProcedimentoLicitacao naturezaProcedimento;

    @Obrigatorio
    @Etiqueta("Tipo Reserva Dotação")
    @Enumerated(EnumType.STRING)
    private TipoReservaDotacaoConfigLicitacao tipoReservaDotacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConfiguracaoLicitacao getConfiguracaoLicitacao() {
        return configuracaoLicitacao;
    }

    public void setConfiguracaoLicitacao(ConfiguracaoLicitacao configuracaoLicitacao) {
        this.configuracaoLicitacao = configuracaoLicitacao;
    }

    public ModalidadeLicitacao getModalidadeLicitacao() {
        return modalidadeLicitacao;
    }

    public void setModalidadeLicitacao(ModalidadeLicitacao modalidadeLicitacao) {
        this.modalidadeLicitacao = modalidadeLicitacao;
    }

    public TipoNaturezaDoProcedimentoLicitacao getNaturezaProcedimento() {
        return naturezaProcedimento;
    }

    public void setNaturezaProcedimento(TipoNaturezaDoProcedimentoLicitacao naturezaProcedimento) {
        this.naturezaProcedimento = naturezaProcedimento;
    }

    public TipoReservaDotacaoConfigLicitacao getTipoReservaDotacao() {
        return tipoReservaDotacao;
    }

    public void setTipoReservaDotacao(TipoReservaDotacaoConfigLicitacao tipoReservaDotacao) {
        this.tipoReservaDotacao = tipoReservaDotacao;
    }
}
