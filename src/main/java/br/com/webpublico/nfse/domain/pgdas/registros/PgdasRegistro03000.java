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
@Etiqueta("Estabelecimento Filial")
public class PgdasRegistro03000 extends SuperEntidade implements RegistroLinhaPgdas {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("03000")
    private String reg;
    @Etiqueta("Cnpj")
    private String cnpj;
    @Etiqueta("UF")
    private String uf;
    @Etiqueta("Código Município")
    private String codTom;
    @Etiqueta("Total Receita")
    private BigDecimal vlTotal;
    @Etiqueta("Índice Majoração")
    private String ime;
    @Etiqueta("Sublimite Estadual")
    private String limite;
    @Etiqueta("Qtde Limites")
    private String limUltrapassadoPa;
    @Etiqueta("Perc. Excedeu Limit. Faixa 1")
    private String prex1;
    @Etiqueta("Perc. Excedeu Limit. Faixa 2")
    private String prex2;
    @Etiqueta("Perc. Excedeu Limit. Merc. Inter. Faixa 1")
    private String prex1Int;
    @Etiqueta("Perc. Excedeu Limit. Merc. Inter. Faixa 1")
    private String prex2Int;
    @Etiqueta("Perc. Excedeu Limit. Merc. Ext. Faixa 1")
    private String prex1Ext;
    @Etiqueta("Perc. Excedeu Limit. Merc. Ext. Faixa 2")
    private String prex2Ext;
    @ManyToOne
    private PgdasRegistro00000 pgdasRegistro00000;
    @ManyToOne
    private ArquivoPgdas arquivoPgdas;


    public PgdasRegistro03000() {
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

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
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

    public BigDecimal getVlTotal() {
        return vlTotal;
    }

    public void setVlTotal(BigDecimal vlTotal) {
        this.vlTotal = vlTotal;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getLimite() {
        return limite;
    }

    public void setLimite(String limite) {
        this.limite = limite;
    }

    public String getLimUltrapassadoPa() {
        return limUltrapassadoPa;
    }

    public void setLimUltrapassadoPa(String limUltrapassadoPa) {
        this.limUltrapassadoPa = limUltrapassadoPa;
    }

    public String getPrex1() {
        return prex1;
    }

    public void setPrex1(String prex1) {
        this.prex1 = prex1;
    }

    public String getPrex2() {
        return prex2;
    }

    public void setPrex2(String prex2) {
        this.prex2 = prex2;
    }

    public String getPrex1Int() {
        return prex1Int;
    }

    public void setPrex1Int(String prex1Int) {
        this.prex1Int = prex1Int;
    }

    public String getPrex2Int() {
        return prex2Int;
    }

    public void setPrex2Int(String prex2Int) {
        this.prex2Int = prex2Int;
    }

    public String getPrex1Ext() {
        return prex1Ext;
    }

    public void setPrex1Ext(String prex1Ext) {
        this.prex1Ext = prex1Ext;
    }

    public String getPrex2Ext() {
        return prex2Ext;
    }

    public void setPrex2Ext(String prex2Ext) {
        this.prex2Ext = prex2Ext;
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
        pipes = UtilPgdas.getListComplementar(pipes, 13);
        setReg(pipes.get(0));
        setCnpj(pipes.get(1));
        setUf(pipes.get(2));
        setCodTom(pipes.get(3));
        setVlTotal(UtilPgdas.getAsValor(pipes.get(4)));
        setIme(pipes.get(5));
        setLimite(pipes.get(6));
        setLimUltrapassadoPa(pipes.get(7));
        setPrex1(pipes.get(8));
        setPrex2(pipes.get(9));
        setPrex1Int(pipes.get(10));
        setPrex2Int(pipes.get(11));
        setPrex1Ext(pipes.get(12));
        setPrex2Ext(pipes.get(13));
        setArquivoPgdas(assistente.getArquivoPgdas());
        setPgdasRegistro00000(assistente.getPgdasRegistro00000());
        assistente.getArquivoPgdas().getRegistros03000().add(this);
    }
}
