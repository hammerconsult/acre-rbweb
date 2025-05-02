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
@Etiqueta("Receita por Atividade com Percentual (faixa B)")
public class PgdasRegistro03120 extends SuperEntidade implements RegistroLinhaPgdas {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("03120")
    private String reg;
    @Etiqueta("Alíquota Apurada")
    private BigDecimal aliqApur;
    @Etiqueta("Alíquota Apurada COFINS")
    private BigDecimal aliqConfins;
    @Etiqueta("Vl Calculado COFINS")
    private BigDecimal vlApuradoConfins;
    @Etiqueta("Alíquota Apurada CSLL")
    private BigDecimal aliqCsll;
    @Etiqueta("Vl Calculado CSLL")
    private BigDecimal vlApuradoCsll;
    @Etiqueta("Alíquota Apurada ICMS")
    private BigDecimal aliqIcms;
    @Etiqueta("Vl Calculado ICMS")
    private BigDecimal vlApuradoIcms;
    @Etiqueta("Alíquota Apurada INSS")
    private BigDecimal aliqInss;
    @Etiqueta("Vl Calculado INSS")
    private BigDecimal vlApuradoInss;
    @Etiqueta("Alíquota Apurada IPI")
    private BigDecimal aliqIpi;
    @Etiqueta("Vl Calculado IPI")
    private BigDecimal vlApuradoIpi;
    @Etiqueta("Alíquota Apurada IRPJ")
    private BigDecimal aliqIrpj;
    @Etiqueta("Vl Calculado IRPJ")
    private BigDecimal vlApuradoIrpj;
    @Etiqueta("Alíquota Apurada ISS")
    private BigDecimal aliqIss;
    @Etiqueta("Vl Calculado ISS")
    private BigDecimal vlApuradoIss;
    @Etiqueta("Alíquota Apurada PIS")
    private BigDecimal aliqPis;
    @Etiqueta("Vl Calculado PIS")
    private BigDecimal vlApuradoPis;
    @ManyToOne
    private PgdasRegistro03100 pgdasRegistro03100;
    @ManyToOne
    private ArquivoPgdas arquivoPgdas;


    public PgdasRegistro03120() {
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

    public PgdasRegistro03100 getPgdasRegistro03100() {
        return pgdasRegistro03100;
    }

    public void setPgdasRegistro03100(PgdasRegistro03100 pgdasRegistro03100) {
        this.pgdasRegistro03100 = pgdasRegistro03100;
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
        setPgdasRegistro03100(assistente.getPgdasRegistro03100());
        assistente.getArquivoPgdas().getRegistros03120().add(this);
    }
}
