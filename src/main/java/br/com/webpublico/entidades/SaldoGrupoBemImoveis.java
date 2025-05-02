package br.com.webpublico.entidades;

import br.com.webpublico.enums.NaturezaTipoGrupoBem;
import br.com.webpublico.enums.TipoGrupo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 05/02/14
 * Time: 12:01
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Etiqueta("Saldo Bens Imóveis")
public class SaldoGrupoBemImoveis extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Data")
    private Date dataSaldo;

    @Monetario
    @Etiqueta("Crédito")
    private BigDecimal credito;

    @Monetario
    @Etiqueta("Débito")
    private BigDecimal debito;

    @ManyToOne
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo Grupo")
    private TipoGrupo tipoGrupo;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Natureza")
    private NaturezaTipoGrupoBem naturezaTipoGrupoBem;

    @ManyToOne
    @Etiqueta("Grupo Patrimonial")
    private GrupoBem grupoBem;

    public SaldoGrupoBemImoveis() {
        super();
        this.credito = new BigDecimal(BigInteger.ZERO);
        this.debito = new BigDecimal(BigInteger.ZERO);
    }

    public SaldoGrupoBemImoveis(Date dataSaldo, BigDecimal credito, BigDecimal debito, UnidadeOrganizacional unidadeOrganizacional, TipoGrupo tipoGrupo, NaturezaTipoGrupoBem naturezaTipoGrupoBem, GrupoBem grupoBem) {
        this.dataSaldo = dataSaldo;
        this.credito = credito;
        this.debito = debito;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.tipoGrupo = tipoGrupo;
        this.naturezaTipoGrupoBem = naturezaTipoGrupoBem;
        this.grupoBem = grupoBem;
    }

    public Date getDataSaldo() {
        return dataSaldo;
    }

    public void setDataSaldo(Date dataSaldo) {
        this.dataSaldo = dataSaldo;
    }

    public BigDecimal getCredito() {
        return credito;
    }

    public void setCredito(BigDecimal credito) {
        this.credito = credito;
    }

    public BigDecimal getDebito() {
        return debito;
    }

    public void setDebito(BigDecimal debito) {
        this.debito = debito;
    }

    public NaturezaTipoGrupoBem getNaturezaTipoGrupoBem() {
        return naturezaTipoGrupoBem;
    }

    public void setNaturezaTipoGrupoBem(NaturezaTipoGrupoBem naturezaTipoGrupoBem) {
        this.naturezaTipoGrupoBem = naturezaTipoGrupoBem;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public TipoGrupo getTipoGrupo() {
        return tipoGrupo;
    }

    public void setTipoGrupo(TipoGrupo tipoGrupo) {
        this.tipoGrupo = tipoGrupo;
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        try {
            return "Data: " + DataUtil.getDataFormatada(dataSaldo) + " - Saldo: " + Util.formataValor(credito.subtract(debito));
        } catch (NullPointerException ex) {
            return "";
        }
    }

    public BigDecimal getSaldoAtual() {
        return getCredito().subtract(getDebito());
    }
}
