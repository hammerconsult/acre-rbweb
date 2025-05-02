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
@Etiqueta("Receita com Isenção (faixa B)")
public class PgdasRegistro03121 extends SuperEntidade implements RegistroLinhaPgdas {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("03121")
    private String reg;
    @Etiqueta("Vl Alíquota")
    private BigDecimal aliqApur;
    @Etiqueta("Perc Alíquota COFINS")
    private BigDecimal aliqConfins;
    @Etiqueta("Vl Calculado COFINS")
    private BigDecimal vlApuradoConfins;
    @Etiqueta("Perc Alíquota CSLL")
    private BigDecimal aliqCsll;
    @Etiqueta("Perc Alíquota CSLL")
    private BigDecimal vlApuradoCsll;
    @Etiqueta("Perc Alíquota ICMS")
    private BigDecimal aliqIcms;
    @Etiqueta("Perc Alíquota ICMS")
    private BigDecimal vlApuradoIcms;
    @Etiqueta("Perc Alíquota INSS")
    private BigDecimal aliqInss;
    @Etiqueta("Perc Alíquota INSS")
    private BigDecimal vlApuradoInss;
    @Etiqueta("Perc Alíquota IPI")
    private BigDecimal aliqIpi;
    @Etiqueta("Perc Alíquota IPI")
    private BigDecimal vlApuradoIpi;
    @Etiqueta("Perc Alíquota IRPJ")
    private BigDecimal aliqIrpj;
    @Etiqueta("Perc Alíquota IRPJ")
    private BigDecimal vlApuradoIrpj;
    @Etiqueta("Perc Alíquota ISS")
    private BigDecimal aliqIss;
    @Etiqueta("Perc Alíquota ISS")
    private BigDecimal vlApuradoIss;
    @Etiqueta("Perc Alíquota PIS")
    private BigDecimal aliqPis;
    @Etiqueta("Perc Alíquota PIS")
    private BigDecimal vlApuradoPis;
    @ManyToOne
    private PgdasRegistro03130 pgdasRegistro03130;
    @ManyToOne
    private ArquivoPgdas arquivoPgdas;


    public PgdasRegistro03121() {
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

    public BigDecimal getAliqApur() {
        return aliqApur;
    }

    public void setAliqApur(BigDecimal aliqApur) {
        this.aliqApur = aliqApur;
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
        pipes = UtilPgdas.getListComplementar(pipes, 18);
        setReg(pipes.get(0));
        setAliqApur(UtilPgdas.getAsValor(pipes.get(1)));
        setAliqConfins(UtilPgdas.getAsValor(pipes.get(2)));
        setVlApuradoConfins(UtilPgdas.getAsValor(pipes.get(3)));
        setAliqCsll(UtilPgdas.getAsValor(pipes.get(4)));
        setVlApuradoCsll(UtilPgdas.getAsValor(pipes.get(5)));
        setAliqIcms(UtilPgdas.getAsValor(pipes.get(6)));
        setVlApuradoIcms(UtilPgdas.getAsValor(pipes.get(7)));
        setAliqIss(UtilPgdas.getAsValor(pipes.get(8)));
        setVlApuradoIss(UtilPgdas.getAsValor(pipes.get(9)));
        setAliqIpi(UtilPgdas.getAsValor(pipes.get(10)));
        setVlApuradoIpi(UtilPgdas.getAsValor(pipes.get(11)));
        setAliqIrpj(UtilPgdas.getAsValor(pipes.get(12)));
        setVlApuradoIrpj(UtilPgdas.getAsValor(pipes.get(13)));
        setAliqIss(UtilPgdas.getAsValor(pipes.get(14)));
        setVlApuradoIss(UtilPgdas.getAsValor(pipes.get(15)));
        setAliqPis(UtilPgdas.getAsValor(pipes.get(16)));
        setVlApuradoPis(UtilPgdas.getAsValor(pipes.get(17)));
        setArquivoPgdas(assistente.getArquivoPgdas());
        setPgdasRegistro03130(assistente.getPgdasRegistro03130());
        assistente.getArquivoPgdas().getRegistros03121().add(this);
    }
}
