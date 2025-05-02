package br.com.webpublico.nfse.domain.pgdas.registros;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidadesauxiliares.AssistenteArquivoPGDAS;
import br.com.webpublico.nfse.domain.pgdas.ArquivoPgdas;
import br.com.webpublico.nfse.domain.pgdas.util.RegistroLinhaPgdas;
import br.com.webpublico.nfse.domain.pgdas.util.UtilPgdas;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Audited
@Etiqueta("Receita por Atividade com Percentual (faixa A)")
public class PgdasRegistro03110 extends SuperEntidade implements RegistroLinhaPgdas {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("03110")
    private String reg;
    @Etiqueta("UF")
    private String uf;
    @Etiqueta("Código Município")
    private String codTom;
    @Etiqueta("Receita")
    private BigDecimal valor;
    @Enumerated(EnumType.STRING)
    @Etiqueta("CONFINS")
    private PgdasRegistro03110Confins confins;
    @Enumerated(EnumType.STRING)
    @Etiqueta("CSLL")
    private PgdasRegistro03110Csll csll;
    @Etiqueta("ICMS")
    @Enumerated(EnumType.STRING)
    private PgdasRegistro03110Icms icms;
    @Etiqueta("INSS")
    @Enumerated(EnumType.STRING)
    private PgdasRegistro03110Inss inss;
    @Etiqueta("IPI")
    @Enumerated(EnumType.STRING)
    private PgdasRegistro03110Ipi ipi;
    @Etiqueta("IRPJ")
    @Enumerated(EnumType.STRING)
    private PgdasRegistro03110Irpj irpj;
    @Etiqueta("ISS")
    @Enumerated(EnumType.STRING)
    private PgdasRegistro03110Iss iss;
    @Etiqueta("PIS")
    @Enumerated(EnumType.STRING)
    private PgdasRegistro03110Pis pis;
    @Etiqueta("Alíquota Apurada")
    private BigDecimal aliqApur;
    @Etiqueta("Imposto")
    private BigDecimal vlImposto;
    @Etiqueta("Alíquota CONFINS")
    private BigDecimal aliqConfins;
    @Etiqueta("Vl Calculado CONFINS")
    private BigDecimal vlApuradoConfins;
    @Etiqueta("Alíquota CSLL")
    private BigDecimal aliqCsll;
    @Etiqueta("Vl Calculado CSLL")
    private BigDecimal vlApuradoCsll;
    @Etiqueta("Alíquota ICMS")
    private BigDecimal aliqIcms;
    @Etiqueta("Vl Calculado ICMS")
    private BigDecimal vlApuradoIcms;
    @Etiqueta("Alíquota INSS")
    private BigDecimal aliqInss;
    @Etiqueta("Vl Calculado INSS")
    private BigDecimal vlApuradoInss;
    @Etiqueta("Alíquota IPI")
    private BigDecimal aliqIpi;
    @Etiqueta("Vl Calculado IPI")
    private BigDecimal vlApuradoIpi;
    @Etiqueta("Alíquota IRPJ")
    private BigDecimal aliqIrpj;
    @Etiqueta("Vl Calculado IRPJ")
    private BigDecimal vlApuradoIrpj;
    @Etiqueta("Alíquota ISS")
    private BigDecimal aliqIss;
    @Etiqueta("Vl Calculado ISS")
    private BigDecimal vlApuradoIss;
    @Etiqueta("Alíquota PIS")
    private BigDecimal aliqPis;
    @Etiqueta("Vl Calculado PIS")
    private BigDecimal vlApuradoPis;
    @Etiqueta("Diferença")
    private BigDecimal diferenca;
    @Etiqueta("Maior Tributo")
    private BigDecimal maiorTributo;
    @ManyToOne
    private PgdasRegistro03100 pgdasRegistro03100;
    @ManyToOne
    private ArquivoPgdas arquivoPgdas;


    public PgdasRegistro03110() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReg() {
        return reg;
    }

    public void setReg(String reg) {
        this.reg = reg;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCodTom() {
        return codTom;
    }

    public void setCodTom(String codTom) {
        this.codTom = codTom;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public PgdasRegistro03110Confins getConfins() {
        return confins;
    }

    public void setConfins(PgdasRegistro03110Confins confins) {
        this.confins = confins;
    }

    public PgdasRegistro03110Csll getCsll() {
        return csll;
    }

    public void setCsll(PgdasRegistro03110Csll csll) {
        this.csll = csll;
    }

    public PgdasRegistro03110Icms getIcms() {
        return icms;
    }

    public void setIcms(PgdasRegistro03110Icms icms) {
        this.icms = icms;
    }

    public PgdasRegistro03110Inss getInss() {
        return inss;
    }

    public void setInss(PgdasRegistro03110Inss inss) {
        this.inss = inss;
    }

    public PgdasRegistro03110Ipi getIpi() {
        return ipi;
    }

    public void setIpi(PgdasRegistro03110Ipi ipi) {
        this.ipi = ipi;
    }

    public PgdasRegistro03110Irpj getIrpj() {
        return irpj;
    }

    public void setIrpj(PgdasRegistro03110Irpj irpj) {
        this.irpj = irpj;
    }

    public PgdasRegistro03110Iss getIss() {
        return iss;
    }

    public void setIss(PgdasRegistro03110Iss iss) {
        this.iss = iss;
    }

    public PgdasRegistro03110Pis getPis() {
        return pis;
    }

    public void setPis(PgdasRegistro03110Pis pis) {
        this.pis = pis;
    }

    public BigDecimal getAliqApur() {
        return aliqApur;
    }

    public void setAliqApur(BigDecimal aliqApur) {
        this.aliqApur = aliqApur;
    }

    public BigDecimal getVlImposto() {
        return vlImposto;
    }

    public void setVlImposto(BigDecimal vlImposto) {
        this.vlImposto = vlImposto;
    }

    public BigDecimal getAliqConfins() {
        return aliqConfins;
    }

    public void setAliqConfins(BigDecimal aliqConfins) {
        this.aliqConfins = aliqConfins;
    }

    public BigDecimal getVlApuradoConfins() {
        return vlApuradoConfins;
    }

    public void setVlApuradoConfins(BigDecimal vlApuradoConfins) {
        this.vlApuradoConfins = vlApuradoConfins;
    }

    public BigDecimal getAliqCsll() {
        return aliqCsll;
    }

    public void setAliqCsll(BigDecimal aliqCsll) {
        this.aliqCsll = aliqCsll;
    }

    public BigDecimal getVlApuradoCsll() {
        return vlApuradoCsll;
    }

    public void setVlApuradoCsll(BigDecimal vlApuradoCsll) {
        this.vlApuradoCsll = vlApuradoCsll;
    }

    public BigDecimal getAliqIcms() {
        return aliqIcms;
    }

    public void setAliqIcms(BigDecimal aliqIcms) {
        this.aliqIcms = aliqIcms;
    }

    public BigDecimal getVlApuradoIcms() {
        return vlApuradoIcms;
    }

    public void setVlApuradoIcms(BigDecimal vlApuradoIcms) {
        this.vlApuradoIcms = vlApuradoIcms;
    }

    public BigDecimal getAliqInss() {
        return aliqInss;
    }

    public void setAliqInss(BigDecimal aliqInss) {
        this.aliqInss = aliqInss;
    }

    public BigDecimal getVlApuradoInss() {
        return vlApuradoInss;
    }

    public void setVlApuradoInss(BigDecimal vlApuradoInss) {
        this.vlApuradoInss = vlApuradoInss;
    }

    public BigDecimal getAliqIpi() {
        return aliqIpi;
    }

    public void setAliqIpi(BigDecimal aliqIpi) {
        this.aliqIpi = aliqIpi;
    }

    public BigDecimal getVlApuradoIpi() {
        return vlApuradoIpi;
    }

    public void setVlApuradoIpi(BigDecimal vlApuradoIpi) {
        this.vlApuradoIpi = vlApuradoIpi;
    }

    public BigDecimal getAliqIrpj() {
        return aliqIrpj;
    }

    public void setAliqIrpj(BigDecimal aliqIrpj) {
        this.aliqIrpj = aliqIrpj;
    }

    public BigDecimal getVlApuradoIrpj() {
        return vlApuradoIrpj;
    }

    public void setVlApuradoIrpj(BigDecimal vlApuradoIrpj) {
        this.vlApuradoIrpj = vlApuradoIrpj;
    }

    public BigDecimal getAliqIss() {
        return aliqIss;
    }

    public void setAliqIss(BigDecimal aliqIss) {
        this.aliqIss = aliqIss;
    }

    public BigDecimal getVlApuradoIss() {
        return vlApuradoIss;
    }

    public void setVlApuradoIss(BigDecimal vlApuradoIss) {
        this.vlApuradoIss = vlApuradoIss;
    }

    public BigDecimal getAliqPis() {
        return aliqPis;
    }

    public void setAliqPis(BigDecimal aliqPis) {
        this.aliqPis = aliqPis;
    }

    public BigDecimal getVlApuradoPis() {
        return vlApuradoPis;
    }

    public void setVlApuradoPis(BigDecimal vlApuradoPis) {
        this.vlApuradoPis = vlApuradoPis;
    }

    public BigDecimal getDiferenca() {
        return diferenca;
    }

    public void setDiferenca(BigDecimal diferenca) {
        this.diferenca = diferenca;
    }

    public BigDecimal getMaiorTributo() {
        return maiorTributo;
    }

    public void setMaiorTributo(BigDecimal maiorTributo) {
        this.maiorTributo = maiorTributo;
    }

    public ArquivoPgdas getArquivoPgdas() {
        return arquivoPgdas;
    }

    public void setArquivoPgdas(ArquivoPgdas arquivoPgdas) {
        this.arquivoPgdas = arquivoPgdas;
    }

    public PgdasRegistro03100 getPgdasRegistro03100() {
        return pgdasRegistro03100;
    }

    public void setPgdasRegistro03100(PgdasRegistro03100 pgdasRegistro03100) {
        this.pgdasRegistro03100 = pgdasRegistro03100;
    }

    @Override
    public void criarLinha(AssistenteArquivoPGDAS assistente, List<String> pipes) {
        pipes = UtilPgdas.getListComplementar(pipes, 32);
        setReg(pipes.get(0));
        setUf(pipes.get(1));
        setCodTom(pipes.get(2));
        setValor(UtilPgdas.getAsValor(pipes.get(3)));
        setConfins(PgdasRegistro03110Confins.getEnumByCodigo(pipes.get(4)));
        setCsll(PgdasRegistro03110Csll.getEnumByCodigo(pipes.get(5)));
        setIcms(PgdasRegistro03110Icms.getEnumByCodigo(pipes.get(6)));
        setInss(PgdasRegistro03110Inss.getEnumByCodigo(pipes.get(7)));
        setIpi(PgdasRegistro03110Ipi.getEnumByCodigo(pipes.get(8)));
        setIrpj(PgdasRegistro03110Irpj.getEnumByCodigo(pipes.get(9)));
        setIss(PgdasRegistro03110Iss.getEnumByCodigo(pipes.get(10)));
        setPis(PgdasRegistro03110Pis.getEnumByCodigo(pipes.get(11)));
        setAliqApur(UtilPgdas.getAsValor(pipes.get(12)));
        setVlImposto(UtilPgdas.getAsValor(pipes.get(13)));
        setAliqConfins(UtilPgdas.getAsValor(pipes.get(14)));
        setVlApuradoConfins(UtilPgdas.getAsValor(pipes.get(15)));
        setAliqCsll(UtilPgdas.getAsValor(pipes.get(16)));
        setVlApuradoCsll(UtilPgdas.getAsValor(pipes.get(17)));
        setAliqIcms(UtilPgdas.getAsValor(pipes.get(18)));
        setVlApuradoIcms(UtilPgdas.getAsValor(pipes.get(19)));
        setAliqInss(UtilPgdas.getAsValor(pipes.get(20)));
        setVlApuradoInss(UtilPgdas.getAsValor(pipes.get(21)));
        setAliqIpi(UtilPgdas.getAsValor(pipes.get(22)));
        setVlApuradoIpi(UtilPgdas.getAsValor(pipes.get(23)));
        setAliqIrpj(UtilPgdas.getAsValor(pipes.get(24)));
        setVlApuradoIrpj(UtilPgdas.getAsValor(pipes.get(25)));
        setAliqIss(UtilPgdas.getAsValor(pipes.get(26)));
        setVlApuradoIss(UtilPgdas.getAsValor(pipes.get(27)));
        setAliqPis(UtilPgdas.getAsValor(pipes.get(28)));
        setVlApuradoPis(UtilPgdas.getAsValor(pipes.get(29)));
        setDiferenca(UtilPgdas.getAsValor(pipes.get(30)));
        setMaiorTributo(UtilPgdas.getAsValor(pipes.get(31)));
        setArquivoPgdas(assistente.getArquivoPgdas());
        setPgdasRegistro03100(assistente.getPgdasRegistro03100());
        assistente.getArquivoPgdas().getRegistros03110().add(this);
    }
}
