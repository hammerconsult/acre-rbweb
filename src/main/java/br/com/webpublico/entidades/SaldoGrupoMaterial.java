package br.com.webpublico.entidades;

import br.com.webpublico.enums.NaturezaTipoGrupoMaterial;
import br.com.webpublico.enums.TipoEstoque;
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
 * User: Desenvolvimento
 * Date: 09/03/14
 * Time: 17:31
 * To change this template use File | Settings | File Templates.
 */

@Entity


@Etiqueta("Saldo Grupo Material")
public class SaldoGrupoMaterial extends SuperEntidade {

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
    @Etiqueta("Tipo de Estoque")
    private TipoEstoque tipoEstoque;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Natureza Grupo Material")
    private NaturezaTipoGrupoMaterial naturezaTipoGrupoMaterial;

    @ManyToOne
    @Etiqueta("Grupo Material")
    private GrupoMaterial grupoMaterial;

    public SaldoGrupoMaterial() {
        super();
        this.credito = new BigDecimal(BigInteger.ZERO);
        this.debito = new BigDecimal(BigInteger.ZERO);
    }

    public NaturezaTipoGrupoMaterial getNaturezaTipoGrupoMaterial() {
        return naturezaTipoGrupoMaterial;
    }

    public void setNaturezaTipoGrupoMaterial(NaturezaTipoGrupoMaterial naturezaTipoGrupoMaterial) {
        this.naturezaTipoGrupoMaterial = naturezaTipoGrupoMaterial;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public TipoEstoque getTipoEstoque() {
        return tipoEstoque;
    }

    public void setTipoEstoque(TipoEstoque tipoEstoque) {
        this.tipoEstoque = tipoEstoque;
    }

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    public BigDecimal getValorSaldo() {
        return debito.subtract(credito);
    }

    @Override
    public String toString() {
        try {
            return "Data: " + DataUtil.getDataFormatada(dataSaldo) + "; Crédito: " + Util.formataValor(credito) + "; Débito: " + Util.formataValor(debito);
        } catch (Exception ex) {
            return "";
        }
    }
}
