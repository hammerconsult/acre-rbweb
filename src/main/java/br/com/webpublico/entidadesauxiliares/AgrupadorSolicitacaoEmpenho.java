package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoObjetoCompra;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class AgrupadorSolicitacaoEmpenho {

    private FonteDespesaORC fonteDespesaORC;
    private Long idOrigemSaldoOrcamentario;
    private String classeOrigemSaldoOrcamentario;
    private GrupoMaterial grupoMaterial;
    private GrupoBem grupoBem;
    private BigDecimal valor;
    private Boolean gerarReserva;
    private Conta contaDesdobrada;
    private ClasseCredor classeCredor;
    private TipoObjetoCompra tipoObjetoCompra;
    private List<Conta> contasDesdobradas;
    private SolicitacaoEmpenho solicitacaoEmpenho;
    private List<ExecucaoContratoItemDotacao> itensDotacaoExecucao;
    private List<ExecucaoProcessoFonteItem> itensDotacaoExecucaoProcesso;
    private List<ItemReconhecimentoDividaDotacao> itensDotacaoReconhecimento;

    public AgrupadorSolicitacaoEmpenho(FonteDespesaORC fonteDespesaORC, List<Conta> contasDespesa) {
        this.fonteDespesaORC = fonteDespesaORC;
        this.contasDesdobradas = contasDespesa;
        this.itensDotacaoExecucao = Lists.newArrayList();
        this.itensDotacaoExecucaoProcesso = Lists.newArrayList();
        this.itensDotacaoReconhecimento = Lists.newArrayList();
        this.valor = BigDecimal.ZERO;
        this.gerarReserva = false;
    }

    public List<ExecucaoProcessoFonteItem> getItensDotacaoExecucaoProcesso() {
        return itensDotacaoExecucaoProcesso;
    }

    public void setItensDotacaoExecucaoProcesso(List<ExecucaoProcessoFonteItem> itensDotacaoExecucaoProcesso) {
        this.itensDotacaoExecucaoProcesso = itensDotacaoExecucaoProcesso;
    }

    public TipoObjetoCompra getTipoObjetoCompra() {
        return tipoObjetoCompra;
    }

    public void setTipoObjetoCompra(TipoObjetoCompra tipoObjetoCompra) {
        this.tipoObjetoCompra = tipoObjetoCompra;
    }

    public Conta getContaDesdobrada() {
        return contaDesdobrada;
    }

    public void setContaDesdobrada(Conta contaDesdobrada) {
        this.contaDesdobrada = contaDesdobrada;
    }

    public List<Conta> getContasDesdobradas() {
        return contasDesdobradas;
    }

    public void setContasDesdobradas(List<Conta> contasDesdobradas) {
        this.contasDesdobradas = contasDesdobradas;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public boolean hasGrupoComMaisUmaConta() {
        return getContasDesdobradas() != null && getContasDesdobradas().size() > 1;
    }

    public List<ExecucaoContratoItemDotacao> getItensDotacaoExecucao() {
        return itensDotacaoExecucao;
    }

    public void setItensDotacaoExecucao(List<ExecucaoContratoItemDotacao> itensDotacaoExecucao) {
        this.itensDotacaoExecucao = itensDotacaoExecucao;
    }

    public Boolean getGerarReserva() {
        return gerarReserva;
    }

    public void setGerarReserva(Boolean gerarReserva) {
        this.gerarReserva = gerarReserva;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public Long getIdOrigemSaldoOrcamentario() {
        return idOrigemSaldoOrcamentario;
    }

    public void setIdOrigemSaldoOrcamentario(Long idOrigemSaldoOrcamentario) {
        this.idOrigemSaldoOrcamentario = idOrigemSaldoOrcamentario;
    }

    public String getClasseOrigemSaldoOrcamentario() {
        return classeOrigemSaldoOrcamentario;
    }

    public void setClasseOrigemSaldoOrcamentario(String classeOrigemSaldoOrcamentario) {
        this.classeOrigemSaldoOrcamentario = classeOrigemSaldoOrcamentario;
    }

    public SolicitacaoEmpenho getSolicitacaoEmpenho() {
        return solicitacaoEmpenho;
    }

    public void setSolicitacaoEmpenho(SolicitacaoEmpenho solicitacaoEmpenho) {
        this.solicitacaoEmpenho = solicitacaoEmpenho;
    }

    public List<ItemReconhecimentoDividaDotacao> getItensDotacaoReconhecimento() {
        return itensDotacaoReconhecimento;
    }

    public void setItensDotacaoReconhecimento(List<ItemReconhecimentoDividaDotacao> itensDotacaoReconhecimento) {
        this.itensDotacaoReconhecimento = itensDotacaoReconhecimento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgrupadorSolicitacaoEmpenho that = (AgrupadorSolicitacaoEmpenho) o;
        return Objects.equals(fonteDespesaORC, that.fonteDespesaORC) &&
            Objects.equals(contasDesdobradas, that.contasDesdobradas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fonteDespesaORC, contasDesdobradas);
    }
}
