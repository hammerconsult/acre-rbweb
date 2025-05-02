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
@Etiqueta("Valor Apurado pelo Cálculo")
public class PgdasRegistro01000 extends SuperEntidade implements RegistroLinhaPgdas {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("01000")
    private String reg;
    @Etiqueta("Guia DAS")
    private String nrPagto;
    @Etiqueta("Principal")
    private BigDecimal princ;
    @Etiqueta("Multa")
    private BigDecimal multa;
    @Etiqueta("Juros")
    private BigDecimal juros;
    @Etiqueta("Total Devido")
    private BigDecimal tDevido;
    @Etiqueta("Vencimento")
    private String dtVenc;
    @Etiqueta("Validade Cálculo")
    private String dtValCalc;
    @Etiqueta("Valor DAS")
    private BigDecimal vDas;
    @ManyToOne
    private PgdasRegistro00000 pgdasRegistro00000;
    @ManyToOne
    private ArquivoPgdas arquivoPgdas;


    public PgdasRegistro01000() {
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

    public String getNrPagto() {
        return nrPagto;
    }

    public void setNrPagto(String nrPagto) {
        this.nrPagto = nrPagto;
    }

    public BigDecimal getPrinc() {
        return princ;
    }

    public void setPrinc(BigDecimal princ) {
        this.princ = princ;
    }

    public BigDecimal getMulta() {
        return multa;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public BigDecimal getJuros() {
        return juros;
    }

    public void setJuros(BigDecimal juros) {
        this.juros = juros;
    }

    public BigDecimal gettDevido() {
        return tDevido;
    }

    public void settDevido(BigDecimal tDevido) {
        this.tDevido = tDevido;
    }

    public String getDtVenc() {
        return dtVenc;
    }

    public void setDtVenc(String dtVenc) {
        this.dtVenc = dtVenc;
    }

    public String getDtValCalc() {
        return dtValCalc;
    }

    public void setDtValCalc(String dtValCalc) {
        this.dtValCalc = dtValCalc;
    }

    public BigDecimal getvDas() {
        return vDas;
    }

    public void setvDas(BigDecimal vDas) {
        this.vDas = vDas;
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
        pipes = UtilPgdas.getListComplementar(pipes, 6);
        setReg(pipes.get(0));
        setNrPagto(pipes.get(1));
        setPrinc(UtilPgdas.getAsValor(pipes.get(2)));
        setMulta(UtilPgdas.getAsValor(pipes.get(3)));
        setJuros(UtilPgdas.getAsValor(pipes.get(4)));
        settDevido(UtilPgdas.getAsValor(pipes.get(5)));
        setDtVenc(pipes.get(6));
        setDtValCalc(pipes.get(7));
        setvDas(UtilPgdas.getAsValor(pipes.get(8)));
        setArquivoPgdas(assistente.getArquivoPgdas());
        assistente.getArquivoPgdas().getRegistros01000().add(this);
    }
}
