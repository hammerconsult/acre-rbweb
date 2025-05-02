package br.com.webpublico.entidades.contabil.conciliacaocontabil;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.conciliacaocontabil.TipoMovimentoHashContabil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.anotacoes.Monetario;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
public class MovimentoHashContabil extends SuperEntidade implements Serializable {

    public static final String SEPARADOR = ";";
    public static final String SIGLA_DESPESA = "DESP:";
    public static final String SIGLA_CONTADESPESA = "CD:";
    public static final String SIGLA_FONTERECURSO = "FR:";
    public static final String SIGLA_CONTAFINANCEIRA = "CF:";
    public static final String SIGLA_HIERARQUIA = "UO:";
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String hash;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date data;
    @Monetario
    private BigDecimal valor;
    @Enumerated(EnumType.STRING)
    private TipoMovimentoHashContabil tipo;

    public MovimentoHashContabil() {
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public TipoMovimentoHashContabil getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentoHashContabil tipo) {
        this.tipo = tipo;
    }


    //builder
    public MovimentoHashContabil toOrcamentario(FonteDespesaORC fonte, Date data, BigDecimal valor) {
        this.tipo = TipoMovimentoHashContabil.ORCAMENTARIO;
        this.data = data;
        this.valor = valor;
        this.hash = StringUtil.concatenar(this.hash, SIGLA_DESPESA + fonte.getDespesaORC().getCodigo(), SEPARADOR);
        this.hash = StringUtil.concatenar(this.hash, SIGLA_CONTADESPESA + fonte.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getCodigo(), SEPARADOR);
        this.hash = StringUtil.concatenar(this.hash, SIGLA_FONTERECURSO + fonte.getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos().getCodigo(), SEPARADOR);
        return this;
    }

    public MovimentoHashContabil toFinanceiro(HierarquiaOrganizacional hierarquiaOrganizacional, SubConta subConta, FonteDeRecursos fonteDeRecursos, Date data, BigDecimal valor) {
        this.tipo = TipoMovimentoHashContabil.FINANCEIRO;
        this.data = data;
        this.valor = valor;
        if (hierarquiaOrganizacional != null) {
            this.hash = StringUtil.concatenar(this.hash, SIGLA_HIERARQUIA + hierarquiaOrganizacional.getCodigo(), SEPARADOR);
        }
        this.hash = StringUtil.concatenar(this.hash, SIGLA_CONTAFINANCEIRA + subConta.getCodigo(), SEPARADOR);
        this.hash = StringUtil.concatenar(this.hash, SIGLA_FONTERECURSO + fonteDeRecursos.getCodigo(), SEPARADOR);
        return this;
    }
}
