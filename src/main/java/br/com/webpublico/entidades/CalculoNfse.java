package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Numerico;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import br.com.webpublico.ws.model.WSDadosArrecadacaoEntrada;
import br.com.webpublico.ws.model.WSDadosIssSemMovimentoEntrada;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 20/01/14
 * Time: 17:31
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Etiqueta("Cálculos de NFS-e")
@Audited
public class CalculoNfse extends Calculo {

    @Transient
    @Tabelavel
    @Etiqueta("Cadastro Econômico")
    private CadastroEconomico cadastroParaListaGenerica;
    @ManyToOne
    private ProcessoCalculoNfse processoCalculo;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Guia")
    private Integer identificacaoDaGuia;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Mês")
    private Integer mesDeReferencia;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Ano")
    private Integer anoDeReferencia;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Vencimento")
    private Date dataVencimentoDebito;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Numerico
    @Etiqueta("Valor")
    private BigDecimal valorTotalDoDebito;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Numerico
    @Etiqueta("Multa")
    private BigDecimal valorDaMulta;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Numerico
    @Etiqueta("Juros")
    private BigDecimal valorDosJuros;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Numerico
    @Etiqueta("Correção")
    private BigDecimal valorDaCorrecao;
    private Integer tipoDoMovimento;
    private Integer tipoTributo;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data do Movimento")
    private Date dataDoMovimento;
    private BigDecimal valorTotalPago;
    @OneToMany(mappedBy = "calculoNfse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCalculoNfse> itensCalculo;
    private String nossoNumero;
    @OneToMany(mappedBy = "calculoNfse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LogNFSE> logs;
    private Date vencimentoDam;

    public CalculoNfse() {
        this.itensCalculo = new ArrayList<>();
        this.setPessoas(new ArrayList<CalculoPessoa>());
        this.setTipoCalculo(TipoCalculo.NFSE);
    }

    public CalculoNfse(Long id, Cadastro cadastroEconomico, Integer identificacaoDaGuia, Integer mesDeReferencia, Integer anoDeReferencia, Date dataVencimentoDebito, BigDecimal valorTotalDoDebito, BigDecimal valorDaMulta, BigDecimal valorDosJuros, BigDecimal valorDaCorrecao, Date dataDoMovimento) {
        setId(id);
        setCadastro(cadastroEconomico);
        this.cadastroParaListaGenerica = (CadastroEconomico)cadastroEconomico;
        this.identificacaoDaGuia = identificacaoDaGuia;
        this.mesDeReferencia = mesDeReferencia;
        this.anoDeReferencia = anoDeReferencia;
        this.dataVencimentoDebito = dataVencimentoDebito;
        this.valorTotalDoDebito = valorTotalDoDebito;
        this.valorDaMulta = valorDaMulta;
        this.valorDosJuros = valorDosJuros;
        this.valorDaCorrecao = valorDaCorrecao;
        this.dataDoMovimento = dataDoMovimento;
    }

    @Override
    public ProcessoCalculo getProcessoCalculo() {
        return processoCalculo;
    }

    public void setProcessoCalculo(ProcessoCalculoNfse processoCalculo) {
        super.setProcessoCalculo(processoCalculo);
        this.processoCalculo = processoCalculo;
    }

    public Integer getIdentificacaoDaGuia() {
        return identificacaoDaGuia;
    }

    public void setIdentificacaoDaGuia(Integer identificacaoDaGuia) {
        this.identificacaoDaGuia = identificacaoDaGuia;
    }

    public Integer getMesDeReferencia() {
        return mesDeReferencia;
    }

    public void setMesDeReferencia(Integer mesDeReferencia) {
        this.mesDeReferencia = mesDeReferencia;
    }

    public Integer getAnoDeReferencia() {
        return anoDeReferencia;
    }

    public void setAnoDeReferencia(Integer anoDeReferencia) {
        this.anoDeReferencia = anoDeReferencia;
    }

    public Date getDataVencimentoDebito() {
        return dataVencimentoDebito;
    }

    public void setDataVencimentoDebito(Date dataVencimentoDebito) {
        this.dataVencimentoDebito = dataVencimentoDebito;
    }

    public BigDecimal getValorTotalDoDebito() {
        return valorTotalDoDebito;
    }

    public void setValorTotalDoDebito(BigDecimal valorTotalDoDebito) {
        this.valorTotalDoDebito = valorTotalDoDebito;
    }

    public BigDecimal getValorDaMulta() {
        return valorDaMulta;
    }

    public void setValorDaMulta(BigDecimal valorDaMulta) {
        this.valorDaMulta = valorDaMulta;
    }

    public BigDecimal getValorDosJuros() {
        return valorDosJuros;
    }

    public void setValorDosJuros(BigDecimal valorDosJuros) {
        this.valorDosJuros = valorDosJuros;
    }

    public BigDecimal getValorDaCorrecao() {
        return valorDaCorrecao;
    }

    public void setValorDaCorrecao(BigDecimal valorDaCorrecao) {
        this.valorDaCorrecao = valorDaCorrecao;
    }

    public Integer getTipoDoMovimento() {
        return tipoDoMovimento;
    }

    public void setTipoDoMovimento(Integer tipoDoMovimento) {
        this.tipoDoMovimento = tipoDoMovimento;
    }

    public Date getDataDoMovimento() {
        return dataDoMovimento;
    }

    public void setDataDoMovimento(Date dataDoMovimento) {
        this.dataDoMovimento = dataDoMovimento;
    }

    public BigDecimal getValorTotalPago() {
        return valorTotalPago;
    }

    public void setValorTotalPago(BigDecimal valorTotalPago) {
        this.valorTotalPago = valorTotalPago;
    }

    public List<ItemCalculoNfse> getItensCalculo() {
        return itensCalculo;
    }

    public void setItensCalculo(List<ItemCalculoNfse> itensCalculo) {
        this.itensCalculo = itensCalculo;
    }

    public String getNossoNumero() {
        return nossoNumero;
    }

    public void setNossoNumero(String nossoNumero) {
        this.nossoNumero = nossoNumero;
    }

    public CadastroEconomico getCadastroParaListaGenerica() {
        return cadastroParaListaGenerica;
    }

    public void setCadastroParaListaGenerica(CadastroEconomico cadastroParaListaGenerica) {
        this.cadastroParaListaGenerica = cadastroParaListaGenerica;
    }

    public Date getVencimentoDam() {
        return vencimentoDam;
    }

    public void setVencimentoDam(Date vencimentoDam) {
        this.vencimentoDam = vencimentoDam;
    }

    public Integer getTipoTributo() {
        return tipoTributo;
    }

    public void setTipoTributo(Integer tipoTributo) {
        this.tipoTributo = tipoTributo;
    }

    @Override
    public CadastroEconomico getCadastro() {
        return (CadastroEconomico) super.getCadastro();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public List<LogNFSE> getLogs() {
        if (logs == null) {
            logs = Lists.newArrayList();
        }
        Collections.sort(logs);
        return logs;
    }

    public void setLogs(List<LogNFSE> logs) {
        this.logs = logs;
    }

    public void recuperarDados(WSDadosArrecadacaoEntrada dadosEntrada) {
        this.identificacaoDaGuia = dadosEntrada.getGRCID();
        this.mesDeReferencia = dadosEntrada.getGRCMESREF();
        this.anoDeReferencia = dadosEntrada.getGRCANOREF();
        this.dataVencimentoDebito = dadosEntrada.getGRCDATAVNC();
        this.valorDaMulta = dadosEntrada.getGRCVLRMULTA();
        this.valorDosJuros = dadosEntrada.getGRCVLRJUROS();
        this.valorDaCorrecao = dadosEntrada.getGRCVLRCORRECAO();
        this.tipoDoMovimento = dadosEntrada.getGRCTIPOMOVIMENTO();
        this.tipoTributo = dadosEntrada.getGRCTRIBUTO();
        this.dataDoMovimento = dadosEntrada.getGRCDTMOVIMENTO();
        this.valorTotalPago = dadosEntrada.getGRCVLRPAGO();
        this.nossoNumero = dadosEntrada.getGRCNOSSONUMERO();
        this.vencimentoDam = dadosEntrada.getGRCDATAVNCDAM();
        this.valorTotalDoDebito = dadosEntrada.getGRCVLRDEB();
        this.setValorReal(this.getValorTotalDoDebito());
        this.setValorEfetivo(this.getValorReal());
    }

    public void recuperarDados(WSDadosIssSemMovimentoEntrada dadosEntrada) {
        this.identificacaoDaGuia = 0;
        this.mesDeReferencia = dadosEntrada.getDMSMESREF();
        this.anoDeReferencia = dadosEntrada.getDMSANOREF();
        this.dataVencimentoDebito = dadosEntrada.getDMSDTMOV();
        this.valorTotalDoDebito = dadosEntrada.getDMSVLRDEC();
        this.valorDaMulta = BigDecimal.ZERO;
        this.valorDosJuros = BigDecimal.ZERO;
        this.valorDaCorrecao = BigDecimal.ZERO;
        this.tipoDoMovimento = dadosEntrada.getDMSTIPOMOV();
        this.dataDoMovimento = dadosEntrada.getDMSDTMOV();
        this.valorTotalPago = BigDecimal.ZERO;
        this.nossoNumero = "";
        this.vencimentoDam = dadosEntrada.getDMSDTMOV();
        this.setValorReal(this.getValorTotalDoDebito());
        this.setValorEfetivo(this.getValorReal());
    }

    public WSDadosArrecadacaoEntrada toDadosArrecadacaoEntrada() {
        WSDadosArrecadacaoEntrada dadosEntrada = new WSDadosArrecadacaoEntrada();
        dadosEntrada.setGRCID(this.identificacaoDaGuia);
        if(getCadastro() != null){
            dadosEntrada.setGRCCTCID(Integer.valueOf(this.getCadastro().getInscricaoCadastral()));
        }
        dadosEntrada.setGRCMESREF(this.mesDeReferencia);
        dadosEntrada.setGRCANOREF(this.anoDeReferencia);
        dadosEntrada.setGRCDATAVNC(this.dataVencimentoDebito);
        dadosEntrada.setGRCVLRDEB(this.valorTotalDoDebito);
        dadosEntrada.setGRCVLRMULTA(this.valorDaMulta);
        dadosEntrada.setGRCVLRJUROS(this.valorDosJuros);
        dadosEntrada.setGRCVLRCORRECAO(this.valorDaCorrecao);
        dadosEntrada.setGRCTIPOMOVIMENTO(this.tipoDoMovimento);
        dadosEntrada.setGRCDTMOVIMENTO(this.dataDoMovimento);
        dadosEntrada.setGRCVLRPAGO(this.valorTotalPago);
        dadosEntrada.setGRCNOSSONUMERO(this.nossoNumero);
        dadosEntrada.setGRCDATAVNCDAM(this.vencimentoDam);
        return dadosEntrada;

    }

}
