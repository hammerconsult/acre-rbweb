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
@Etiqueta("Indicação do Contribuinte e Dados da Apuração")
public class PgdasRegistro00000 extends SuperEntidade implements RegistroLinhaPgdas {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("00000")
    private String reg;
    @Etiqueta("Declaração")
    private String idDeclaracao;
    @Etiqueta("Recibo de Entrega Apuração")
    private String numRecibo;
    @Etiqueta("Autenticação da Transmissão")
    private String numAutenticacao;
    @Etiqueta("Data Transmissão")
    private String dtTransmissao;
    @Etiqueta("Versão")
    private String versao;
    @Etiqueta("Cnpj")
    private String cnpjMatriz;
    @Etiqueta("Razão Social")
    private String nome;
    @Etiqueta("Código do Município")
    private String codTom;
    @Etiqueta("Optante")
    private String optante;
    @Etiqueta("Abertura")
    private String abertura;
    @Etiqueta("Apuração")
    private String pa;
    @Etiqueta("Receita Bruta - Regime Competência")
    private BigDecimal rpa;
    @Etiqueta("Razão Folha Salarial")
    private String r;
    @Etiqueta("Índice Majoração")
    private String im;
    @Etiqueta("Apuração (A)/Retificação(R)")
    private String operacao;
    @Etiqueta("Regime de Competência")
    private String regime;
    @Etiqueta("Receita Bruta - Regime da Caixa")
    private BigDecimal rpac;
    @Etiqueta("Receita Bruta - Regime Compet. Interna")
    private BigDecimal rpaInt;
    @Etiqueta("Receita Bruta - Regime Compet. Externa")
    private BigDecimal rpaExt;
    @Etiqueta("Receita Bruta - Regime Caixa Interna")
    private BigDecimal rpaCInt;
    @Etiqueta("Receita Bruta - Regime Caixa Externa")
    private BigDecimal rpaCExt;
    @Etiqueta("Índice Majoração - Receita Interna")
    private BigDecimal imfInt;
    @Etiqueta("Índice Majoração - Receita Externa")
    private BigDecimal imfExt;
    @ManyToOne
    private PgdasRegistroAAAAA pgdasRegistroAAAAA;
    @ManyToOne
    private ArquivoPgdas arquivoPgdas;


    public PgdasRegistro00000() {
    }

    public String getReg() {
        return reg;
    }

    public void setReg(String reg) {
        this.reg = reg;
    }

    public String getIdDeclaracao() {
        return idDeclaracao;
    }

    public void setIdDeclaracao(String idDeclaracao) {
        this.idDeclaracao = idDeclaracao;
    }

    public String getNumRecibo() {
        return numRecibo;
    }

    public void setNumRecibo(String numRecibo) {
        this.numRecibo = numRecibo;
    }

    public String getNumAutenticacao() {
        return numAutenticacao;
    }

    public void setNumAutenticacao(String numAutenticacao) {
        this.numAutenticacao = numAutenticacao;
    }

    public String getDtTransmissao() {
        return dtTransmissao;
    }

    public void setDtTransmissao(String dtTransmissao) {
        this.dtTransmissao = dtTransmissao;
    }

    public String getVersao() {
        return versao;
    }

    public void setVersao(String versao) {
        this.versao = versao;
    }

    public String getCnpjMatriz() {
        return cnpjMatriz;
    }

    public void setCnpjMatriz(String cnpjMatriz) {
        this.cnpjMatriz = cnpjMatriz;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodTom() {
        return codTom;
    }

    public void setCodTom(String codTom) {
        this.codTom = codTom;
    }

    public String getOptante() {
        return optante;
    }

    public void setOptante(String optante) {
        this.optante = optante;
    }

    public String getAbertura() {
        return abertura;
    }

    public void setAbertura(String abertura) {
        this.abertura = abertura;
    }

    public String getPa() {
        return pa;
    }

    public void setPa(String pa) {
        this.pa = pa;
    }

    public BigDecimal getRpa() {
        return rpa;
    }

    public void setRpa(BigDecimal rpa) {
        this.rpa = rpa;
    }

    public String getR() {
        return r;
    }

    public void setR(String r) {
        this.r = r;
    }

    public String getIm() {
        return im;
    }

    public void setIm(String im) {
        this.im = im;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public String getRegime() {
        return regime;
    }

    public void setRegime(String regime) {
        this.regime = regime;
    }

    public BigDecimal getRpac() {
        return rpac;
    }

    public void setRpac(BigDecimal rpac) {
        this.rpac = rpac;
    }

    public BigDecimal getRpaInt() {
        return rpaInt;
    }

    public void setRpaInt(BigDecimal rpaInt) {
        this.rpaInt = rpaInt;
    }

    public BigDecimal getRpaExt() {
        return rpaExt;
    }

    public void setRpaExt(BigDecimal rpaExt) {
        this.rpaExt = rpaExt;
    }

    public BigDecimal getRpaCInt() {
        return rpaCInt;
    }

    public void setRpaCInt(BigDecimal rpaCInt) {
        this.rpaCInt = rpaCInt;
    }

    public BigDecimal getRpaCExt() {
        return rpaCExt;
    }

    public void setRpaCExt(BigDecimal rpaCExt) {
        this.rpaCExt = rpaCExt;
    }

    public BigDecimal getImfInt() {
        return imfInt;
    }

    public void setImfInt(BigDecimal imfInt) {
        this.imfInt = imfInt;
    }

    public BigDecimal getImfExt() {
        return imfExt;
    }

    public void setImfExt(BigDecimal imfExt) {
        this.imfExt = imfExt;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ArquivoPgdas getArquivoPgdas() {
        return arquivoPgdas;
    }

    public void setArquivoPgdas(ArquivoPgdas arquivoPgdas) {
        this.arquivoPgdas = arquivoPgdas;
    }

    public PgdasRegistroAAAAA getPgdasRegistroAAAAA() {
        return pgdasRegistroAAAAA;
    }

    public void setPgdasRegistroAAAAA(PgdasRegistroAAAAA pgdasRegistroAAAAA) {
        this.pgdasRegistroAAAAA = pgdasRegistroAAAAA;
    }

    @Override
    public void criarLinha(AssistenteArquivoPGDAS assistente, List<String> pipes) {
        pipes = UtilPgdas.getListComplementar(pipes, 22);
        setReg(pipes.get(0));
        setIdDeclaracao(pipes.get(1));
        setNumRecibo(pipes.get(2));
        setNumAutenticacao(pipes.get(3));
        setDtTransmissao(pipes.get(4));
        setVersao(pipes.get(5));
        setCnpjMatriz(pipes.get(6));
        setNome(pipes.get(7));
        setCodTom(pipes.get(8));
        setOptante(pipes.get(9));
        setAbertura(pipes.get(10));
        setPa(pipes.get(11));
        setRpa(UtilPgdas.getAsValor(pipes.get(12)));
        setR(pipes.get(13));
        setIm(pipes.get(14));
        setOperacao(pipes.get(15));
        setRegime(pipes.get(16));
        setRpac(UtilPgdas.getAsValor(pipes.get(17)));
        setRpaInt(UtilPgdas.getAsValor(pipes.get(18)));
        setRpaCInt(UtilPgdas.getAsValor(pipes.get(19)));
        setRpaCExt(UtilPgdas.getAsValor(pipes.get(20)));
        setImfInt(UtilPgdas.getAsValor(pipes.get(21)));
        setImfExt(UtilPgdas.getAsValor(pipes.get(22)));
        setArquivoPgdas(assistente.getArquivoPgdas());
        setPgdasRegistroAAAAA(assistente.getPgdasRegistroAAAAA());
        assistente.getArquivoPgdas().getRegistros00000().add(this);
    }
}
