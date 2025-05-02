package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Código de Tributação (Municipal)")
public class CodigoTributacaoMunicipal extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @Obrigatorio
    @Etiqueta("Código")
    private String codigo;
    @Obrigatorio
    @Etiqueta("Código de Tributação")
    @ManyToOne
    private CodigoTributacao codigoTributacao;
    @Obrigatorio
    @Etiqueta("Alíquota")
    private BigDecimal aliquota;
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Início de Vigência")
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    private Date fimVigencia;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public CodigoTributacao getCodigoTributacao() {
        return codigoTributacao;
    }

    public void setCodigoTributacao(CodigoTributacao codigoTributacao) {
        this.codigoTributacao = codigoTributacao;
    }

    public BigDecimal getAliquota() {
        return aliquota;
    }

    public void setAliquota(BigDecimal aliquota) {
        this.aliquota = aliquota;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }
}
