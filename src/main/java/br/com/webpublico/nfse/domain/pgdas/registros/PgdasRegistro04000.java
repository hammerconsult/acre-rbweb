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
@Etiqueta("Perfil")
public class PgdasRegistro04000 extends SuperEntidade implements RegistroLinhaPgdas {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("04000")
    private String reg;
    @Etiqueta("Receita Principal")
    private String codrecp;
    @Etiqueta("Valor Principal")
    private BigDecimal valorPrinc;
    @Etiqueta("Receita Multa")
    private String codrecm;
    @Etiqueta("Valor Multa")
    private BigDecimal valorm;
    @Etiqueta("Receita Juros")
    private String codrecj;
    @Etiqueta("Valor Juros")
    private BigDecimal valorj;
    @Etiqueta("UF")
    private String uf;
    @Etiqueta("Cód. Município")
    private String codmunic;
    @ManyToOne
    private PgdasRegistro00000 pgdasRegistro00000;
    @ManyToOne
    private ArquivoPgdas arquivoPgdas;


    public PgdasRegistro04000() {
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

    public String getCodrecp() {
        return codrecp;
    }

    public void setCodrecp(String codrecp) {
        this.codrecp = codrecp;
    }

    public BigDecimal getValorPrinc() {
        return valorPrinc;
    }

    public void setValorPrinc(BigDecimal valorPrinc) {
        this.valorPrinc = valorPrinc;
    }

    public String getCodrecm() {
        return codrecm;
    }

    public void setCodrecm(String codrecm) {
        this.codrecm = codrecm;
    }

    public BigDecimal getValorm() {
        return valorm;
    }

    public void setValorm(BigDecimal valorm) {
        this.valorm = valorm;
    }

    public String getCodrecj() {
        return codrecj;
    }

    public void setCodrecj(String codrecj) {
        this.codrecj = codrecj;
    }

    public BigDecimal getValorj() {
        return valorj;
    }

    public void setValorj(BigDecimal valorj) {
        this.valorj = valorj;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCodmunic() {
        return codmunic;
    }

    public void setCodmunic(String codmunic) {
        this.codmunic = codmunic;
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
        setCodrecp(pipes.get(1));
        setValorPrinc(UtilPgdas.getAsValor(pipes.get(2)));
        setCodrecm(pipes.get(3));
        setValorm(UtilPgdas.getAsValor(pipes.get(4)));
        setCodrecj(pipes.get(5));
        setValorj(UtilPgdas.getAsValor(pipes.get(6)));
        setUf(pipes.get(7));
        setCodmunic(pipes.get(8));
        setArquivoPgdas(assistente.getArquivoPgdas());
        setPgdasRegistro00000(assistente.getPgdasRegistro00000());
        assistente.getArquivoPgdas().getRegistros04000().add(this);
    }
}
