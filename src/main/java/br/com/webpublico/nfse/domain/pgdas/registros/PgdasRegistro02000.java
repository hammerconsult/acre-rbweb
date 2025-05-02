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
@Etiqueta("Receitas Brutas de Períodos Anteriores, Valor Original e Tributos Fixos")
public class PgdasRegistro02000 extends SuperEntidade implements RegistroLinhaPgdas {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("02000")
    private String reg;
    @Etiqueta("RB 12 Meses")
    private BigDecimal rbt12;
    @Etiqueta("RB Ano-Cal Ant")
    private BigDecimal rbtaa;
    @Etiqueta("RB Ano-Cal")
    private BigDecimal rba;
    @Etiqueta("RB Ult 12 Meses")
    private BigDecimal rbt12o;
    @Etiqueta("RB Ano-Cal Orig")
    private BigDecimal rbtaao;
    @Etiqueta("Fixo ICMS")
    private BigDecimal icms;
    @Etiqueta("Fixo ISS")
    private BigDecimal iss;
    @Etiqueta("RB Ano-Cal Ant")
    private BigDecimal rbtaaInt;
    @Etiqueta("RB Ant Orig Merc Int")
    private BigDecimal rbtaaInto;
    @Etiqueta("RB Ant Orig Merc Ext")
    private BigDecimal rbtaaExt;
    @Etiqueta("RB Ano-Cal Ant Merc Ext")
    private BigDecimal rbtaaExto;
    @Etiqueta("RB Ult 12 Meses Merc Int")
    private BigDecimal rbt12Int;
    @Etiqueta("RB Ult 12 Meses Orig Merc Int")
    private BigDecimal rbt12Into;
    @Etiqueta("RB Ano-Cal Merc Int")
    private BigDecimal rbaInt;
    @Etiqueta("RB Ano-Cal Merc Ext")
    private BigDecimal rbaExt;
    @Etiqueta("RB Ult 12 Meses Merc Ext")
    private BigDecimal rbt12Ext;
    @Etiqueta("RB Ult 12 Meses Orig Merc Ext")
    private BigDecimal rbt12Exto;
    @ManyToOne
    private PgdasRegistro00000 pgdasRegistro00000;
    @ManyToOne
    private ArquivoPgdas arquivoPgdas;


    public PgdasRegistro02000() {
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

    public BigDecimal getRbt12() {
        return rbt12;
    }

    public void setRbt12(BigDecimal rbt12) {
        this.rbt12 = rbt12;
    }

    public BigDecimal getRbtaa() {
        return rbtaa;
    }

    public void setRbtaa(BigDecimal rbtaa) {
        this.rbtaa = rbtaa;
    }

    public BigDecimal getRba() {
        return rba;
    }

    public void setRba(BigDecimal rba) {
        this.rba = rba;
    }

    public BigDecimal getRbt12o() {
        return rbt12o;
    }

    public void setRbt12o(BigDecimal rbt12o) {
        this.rbt12o = rbt12o;
    }

    public BigDecimal getRbtaao() {
        return rbtaao;
    }

    public void setRbtaao(BigDecimal rbtaao) {
        this.rbtaao = rbtaao;
    }

    public BigDecimal getIcms() {
        return icms;
    }

    public void setIcms(BigDecimal icms) {
        this.icms = icms;
    }

    public BigDecimal getIss() {
        return iss;
    }

    public void setIss(BigDecimal iss) {
        this.iss = iss;
    }

    public BigDecimal getRbtaaInt() {
        return rbtaaInt;
    }

    public void setRbtaaInt(BigDecimal rbtaaInt) {
        this.rbtaaInt = rbtaaInt;
    }

    public BigDecimal getRbtaaInto() {
        return rbtaaInto;
    }

    public void setRbtaaInto(BigDecimal rbtaaInto) {
        this.rbtaaInto = rbtaaInto;
    }

    public BigDecimal getRbtaaExt() {
        return rbtaaExt;
    }

    public void setRbtaaExt(BigDecimal rbtaaExt) {
        this.rbtaaExt = rbtaaExt;
    }

    public BigDecimal getRbtaaExto() {
        return rbtaaExto;
    }

    public void setRbtaaExto(BigDecimal rbtaaExto) {
        this.rbtaaExto = rbtaaExto;
    }

    public BigDecimal getRbt12Int() {
        return rbt12Int;
    }

    public void setRbt12Int(BigDecimal rbt12Int) {
        this.rbt12Int = rbt12Int;
    }

    public BigDecimal getRbt12Into() {
        return rbt12Into;
    }

    public void setRbt12Into(BigDecimal rbt12Into) {
        this.rbt12Into = rbt12Into;
    }

    public BigDecimal getRbaInt() {
        return rbaInt;
    }

    public void setRbaInt(BigDecimal rbaInt) {
        this.rbaInt = rbaInt;
    }

    public BigDecimal getRbaExt() {
        return rbaExt;
    }

    public void setRbaExt(BigDecimal rbaExt) {
        this.rbaExt = rbaExt;
    }

    public BigDecimal getRbt12Ext() {
        return rbt12Ext;
    }

    public void setRbt12Ext(BigDecimal rbt12Ext) {
        this.rbt12Ext = rbt12Ext;
    }

    public BigDecimal getRbt12Exto() {
        return rbt12Exto;
    }

    public void setRbt12Exto(BigDecimal rbt12Exto) {
        this.rbt12Exto = rbt12Exto;
    }

    public ArquivoPgdas getArquivoPgdas() {
        return arquivoPgdas;
    }

    public void setArquivoPgdas(ArquivoPgdas arquivoPgdas) {
        this.arquivoPgdas = arquivoPgdas;
    }

    public PgdasRegistro00000 getPgdasRegistro00000() {
        return pgdasRegistro00000;
    }

    public void setPgdasRegistro00000(PgdasRegistro00000 pgdasRegistro00000) {
        this.pgdasRegistro00000 = pgdasRegistro00000;
    }

    @Override
    public void criarLinha(AssistenteArquivoPGDAS assistente, List<String> pipes) {
        pipes = UtilPgdas.getListComplementar(pipes, 18);
        setReg(pipes.get(0));
        setRbt12(UtilPgdas.getAsValor(pipes.get(1)));
        setRbtaa(UtilPgdas.getAsValor(pipes.get(2)));
        setRba(UtilPgdas.getAsValor(pipes.get(3)));
        setRbt12o(UtilPgdas.getAsValor(pipes.get(4)));
        setRbtaao(UtilPgdas.getAsValor(pipes.get(5)));
        setIcms(UtilPgdas.getAsValor(pipes.get(6)));
        setIss(UtilPgdas.getAsValor(pipes.get(7)));
        setRbtaaInt(UtilPgdas.getAsValor(pipes.get(8)));
        setRbtaaInto(UtilPgdas.getAsValor(pipes.get(9)));
        setRbtaaExt(UtilPgdas.getAsValor(pipes.get(10)));
        setRbtaaExto(UtilPgdas.getAsValor(pipes.get(11)));
        setRbt12Int(UtilPgdas.getAsValor(pipes.get(12)));
        setRbt12Into(UtilPgdas.getAsValor(pipes.get(13)));
        setRbaInt(UtilPgdas.getAsValor(pipes.get(14)));
        setRbaExt(UtilPgdas.getAsValor(pipes.get(15)));
        setRbt12Ext(UtilPgdas.getAsValor(pipes.get(16)));
        setRbt12Exto(UtilPgdas.getAsValor(pipes.get(17)));
        setArquivoPgdas(assistente.getArquivoPgdas());
        setPgdasRegistro00000(assistente.getPgdasRegistro00000());
        assistente.getArquivoPgdas().getRegistros02000().add(this);
    }
}
