package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.Tributo;
import br.com.webpublico.nfse.enums.TipoDeclaracaoMensalServico;
import br.com.webpublico.nfse.enums.TipoMovimentoMensal;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class ConfiguracaoNfseDivida extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ConfiguracaoNfse configuracaoNfse;
    @Obrigatorio
    @Etiqueta("Tipo de Movimento Mensal")
    @Enumerated(EnumType.STRING)
    private TipoMovimentoMensal tipoMovimentoMensal;
    @Obrigatorio
    @Etiqueta("Tipo de Declaração Mensal de Serviço")
    @Enumerated(EnumType.STRING)
    private TipoDeclaracaoMensalServico tipoDeclaracaoMensalServico;
    @Obrigatorio
    @Etiqueta("Dívida")
    @ManyToOne
    private Divida dividaNfse;
    @Obrigatorio
    @Etiqueta("Tributo")
    @ManyToOne
    private Tributo tributo;

    public ConfiguracaoNfseDivida() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Divida getDividaNfse() {
        return dividaNfse;
    }

    public void setDividaNfse(Divida dividaNfse) {
        this.dividaNfse = dividaNfse;
    }

    public TipoMovimentoMensal getTipoMovimentoMensal() {
        return tipoMovimentoMensal;
    }

    public void setTipoMovimentoMensal(TipoMovimentoMensal tipoMovimentoMensal) {
        this.tipoMovimentoMensal = tipoMovimentoMensal;
    }

    public TipoDeclaracaoMensalServico getTipoDeclaracaoMensalServico() {
        return tipoDeclaracaoMensalServico;
    }

    public void setTipoDeclaracaoMensalServico(TipoDeclaracaoMensalServico tipoDeclaracaoMensalServico) {
        this.tipoDeclaracaoMensalServico = tipoDeclaracaoMensalServico;
    }

    public ConfiguracaoNfse getConfiguracaoNfse() {
        return configuracaoNfse;
    }

    public void setConfiguracaoNfse(ConfiguracaoNfse configuracaoNfse) {
        this.configuracaoNfse = configuracaoNfse;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    @Override
    public String toString() {
        return "ConfiguracaoNfseDivida{" +
            "id=" + id +
            ", dividaNfse=" + dividaNfse +
            ", tipoMovimentoMensal=" + tipoMovimentoMensal +
            ", tipoDeclaracaoMensalServico=" + tipoDeclaracaoMensalServico +
            ", configuracaoNfse=" + configuracaoNfse +
            '}';
    }
}
