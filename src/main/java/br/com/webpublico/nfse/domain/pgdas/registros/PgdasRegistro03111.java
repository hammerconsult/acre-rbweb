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
@Etiqueta("Receita com Isenção (faixa A)")
public class PgdasRegistro03111 extends SuperEntidade implements RegistroLinhaPgdas {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("03111")
    private String reg;
    @Etiqueta("Vl Receita")
    private BigDecimal valor;
    @Etiqueta("Vl Alíquota")
    private BigDecimal aliqApur;
    @Etiqueta("Vl Imposto")
    private BigDecimal vlImposto;
    @Etiqueta("Perc Alíquota COFINS")
    private BigDecimal aliqConfins;
    @Etiqueta("Vl Calculado COFINS")
    private BigDecimal vlApuradoConfins;
    @Etiqueta("Perc Alíquota CSLL")
    private BigDecimal aliqCsll;
    @Etiqueta("Vl Calculado CSLL")
    private BigDecimal vlApuradoCsll;
    @Etiqueta("Perc Alíquota ICMS")
    private BigDecimal aliqIcms;
    @Etiqueta("Vl Calculado ICMS")
    private BigDecimal vlApuradoIcms;
    @Etiqueta("Perc Alíquota INSS")
    private BigDecimal aliqInss;
    @Etiqueta("Vl Calculado INSS")
    private BigDecimal vlApuradoInss;
    @Etiqueta("Perc Alíquota IPI")
    private BigDecimal aliqIpi;
    @Etiqueta("Vl Calculado IPI")
    private BigDecimal vlApuradoIpi;
    @Etiqueta("Perc Alíquota IRPJ")
    private BigDecimal aliqIrpj;
    @Etiqueta("Vl Calculado IRPJ")
    private BigDecimal vlApuradoIrpj;
    @Etiqueta("Perc Alíquota ISS")
    private BigDecimal aliqIss;
    @Etiqueta("Vl Calculado ISS")
    private BigDecimal vlApuradoIss;
    @Etiqueta("Perc Alíquota PIS")
    private BigDecimal aliqPis;
    @Etiqueta("Vl Calculado PIS")
    private BigDecimal vlApuradoPis;
    @Etiqueta("Diferença")
    private BigDecimal diferenca;
    @Etiqueta("Maior Tributo")
    private BigDecimal maiorTributo;
    @ManyToOne
    private PgdasRegistro03130 pgdasRegistro03130;
    @ManyToOne
    private ArquivoPgdas arquivoPgdas;


    public PgdasRegistro03111() {
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

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
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

    public PgdasRegistro03130 getPgdasRegistro03130() {
        return pgdasRegistro03130;
    }

    public void setPgdasRegistro03130(PgdasRegistro03130 pgdasRegistro03130) {
        this.pgdasRegistro03130 = pgdasRegistro03130;
    }

    @Override
    public void criarLinha(AssistenteArquivoPGDAS assistente, List<String> pipes) {
        pipes = UtilPgdas.getListComplementar(pipes, 22);
        setReg(pipes.get(0));
        setValor(UtilPgdas.getAsValor(pipes.get(1)));
        setAliqApur(UtilPgdas.getAsValor(pipes.get(2)));
        setVlImposto(UtilPgdas.getAsValor(pipes.get(3)));
        setAliqConfins(UtilPgdas.getAsValor(pipes.get(4)));
        setVlApuradoConfins(UtilPgdas.getAsValor(pipes.get(5)));
        setAliqCsll(UtilPgdas.getAsValor(pipes.get(6)));
        setVlApuradoCsll(UtilPgdas.getAsValor(pipes.get(7)));
        setAliqIcms(UtilPgdas.getAsValor(pipes.get(8)));
        setVlApuradoIcms(UtilPgdas.getAsValor(pipes.get(9)));
        setAliqIss(UtilPgdas.getAsValor(pipes.get(10)));
        setVlApuradoIss(UtilPgdas.getAsValor(pipes.get(11)));
        setAliqIpi(UtilPgdas.getAsValor(pipes.get(12)));
        setVlApuradoIpi(UtilPgdas.getAsValor(pipes.get(13)));
        setAliqIrpj(UtilPgdas.getAsValor(pipes.get(14)));
        setVlApuradoIrpj(UtilPgdas.getAsValor(pipes.get(15)));
        setAliqIss(UtilPgdas.getAsValor(pipes.get(16)));
        setVlApuradoIss(UtilPgdas.getAsValor(pipes.get(17)));
        setAliqPis(UtilPgdas.getAsValor(pipes.get(18)));
        setVlApuradoPis(UtilPgdas.getAsValor(pipes.get(19)));
        setDiferenca(UtilPgdas.getAsValor(pipes.get(20)));
        setMaiorTributo(UtilPgdas.getAsValor(pipes.get(21)));
        setArquivoPgdas(assistente.getArquivoPgdas());
        setPgdasRegistro03130(assistente.getPgdasRegistro03130());
        assistente.getArquivoPgdas().getRegistros03111().add(this);
    }
}
