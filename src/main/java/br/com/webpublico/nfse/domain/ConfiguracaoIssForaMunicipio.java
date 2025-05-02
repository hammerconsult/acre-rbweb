package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.Tributo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Table(name = "CONFIGURACAOISSFORAMUN")
public class ConfiguracaoIssForaMunicipio extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Dívida")
    @ManyToOne
    private Divida divida;
    @Obrigatorio
    @Etiqueta("Tributo")
    @ManyToOne
    private Tributo tributo;
    private Integer qtdeDiasEstorno;

    public ConfiguracaoIssForaMunicipio() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public Integer getQtdeDiasEstorno() {
        return qtdeDiasEstorno;
    }

    public void setQtdeDiasEstorno(Integer qtdeDiasEstorno) {
        this.qtdeDiasEstorno = qtdeDiasEstorno;
    }
}
