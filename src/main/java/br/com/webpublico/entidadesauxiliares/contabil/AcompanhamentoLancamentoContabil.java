package br.com.webpublico.entidadesauxiliares.contabil;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by romanini on 16/10/17.
 */
public class AcompanhamentoLancamentoContabil {
    private Date dataInicial;
    private Date dataFinal;
    private TipoEventoContabil tipoEventoContabil;
    private EventoContabil eventoContabil;
    private Pessoa credor;
    private ClasseCredor classeCredor;
    private String historico;
    private String numero;
    private BigDecimal valor;
    private OperacaoReceita operacaoReceita;
    private ContaReceita contaReceita;
    private ContaDespesa contaDespesa;
    private ContaExtraorcamentaria contaExtraorcamentaria;
    private FonteDeRecursos fonteDeRecursos;
    private Exercicio exercicio;
    private ContaBancariaEntidade contaBancariaEntidade;
    private SubConta contaFinanceira;
    private TipoOperacaoBensMoveis tipoOperacaoBensMoveis;
    private TipoOperacaoBensImoveis tipoOperacaoBensImoveis;
    private TipoOperacaoBensIntangiveis tipoOperacaoBensIntangiveis;
    private OperacaoMovimentoDividaPublica operacaoMovimentoDividaPublica;
    private GrupoBem grupoBem;
    private TipoEstoque tipoEstoque;
    private TipoOperacaoBensEstoque tipoOperacaoBensEstoque;
    private GrupoMaterial grupoMaterial;
    private List<HierarquiaOrganizacional> hierarquias;
    private List<AcompanhamentoEventoContabil> eventos;
    private ConsultaMovimentoContabil.Ordem campoOrdenar;
    private Integer quantidadeDeRegistros;
    private Long idParametroEvento;
    private Boolean mostrarContabilizacao;
    private List<Conta> contasDebitos;
    private List<Conta> contasCreditos;
    private List<FonteDeRecursos> fontesDebitos;
    private List<FonteDeRecursos> fontesCreditos;

    public AcompanhamentoLancamentoContabil() {
        dataInicial = UtilRH.getDataOperacao();
        dataFinal = UtilRH.getDataOperacao();
        exercicio = UtilRH.getExercicio();
        eventos = Lists.newArrayList();
        hierarquias = Lists.newArrayList();
        campoOrdenar = new ConsultaMovimentoContabil.Ordem();
        quantidadeDeRegistros = ConsultaMovimentoContabil.MAXIMO_REGISTROS;
        mostrarContabilizacao = Boolean.FALSE;
        contasDebitos = Lists.newArrayList();
        contasCreditos = Lists.newArrayList();
        fontesDebitos = Lists.newArrayList();
        fontesCreditos = Lists.newArrayList();
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public TipoEventoContabil getTipoEventoContabil() {
        return tipoEventoContabil;
    }

    public void setTipoEventoContabil(TipoEventoContabil tipoEventoContabil) {
        this.tipoEventoContabil = tipoEventoContabil;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public Pessoa getCredor() {
        return credor;
    }

    public void setCredor(Pessoa credor) {
        this.credor = credor;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public List<AcompanhamentoEventoContabil> getEventos() {
        return eventos;
    }

    public void setEventos(List<AcompanhamentoEventoContabil> eventos) {
        this.eventos = eventos;
    }

    public List<HierarquiaOrganizacional> getHierarquias() {
        return hierarquias;
    }

    public void setHierarquias(List<HierarquiaOrganizacional> hierarquias) {
        this.hierarquias = hierarquias;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public ContaReceita getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(ContaReceita contaReceita) {
        this.contaReceita = contaReceita;
    }

    public ContaDespesa getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(ContaDespesa contaDespesa) {
        this.contaDespesa = contaDespesa;
    }

    public ContaExtraorcamentaria getContaExtraorcamentaria() {
        return contaExtraorcamentaria;
    }

    public void setContaExtraorcamentaria(ContaExtraorcamentaria contaExtraorcamentaria) {
        this.contaExtraorcamentaria = contaExtraorcamentaria;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public OperacaoReceita getOperacaoReceita() {
        return operacaoReceita;
    }

    public void setOperacaoReceita(OperacaoReceita operacaoReceita) {
        this.operacaoReceita = operacaoReceita;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public SubConta getContaFinanceira() {
        return contaFinanceira;
    }

    public void setContaFinanceira(SubConta contaFinanceira) {
        this.contaFinanceira = contaFinanceira;
    }

    public ContaBancariaEntidade getContaBancariaEntidade() {
        return contaBancariaEntidade;
    }

    public void setContaBancariaEntidade(ContaBancariaEntidade contaBancariaEntidade) {
        this.contaBancariaEntidade = contaBancariaEntidade;
    }

    public TipoOperacaoBensMoveis getTipoOperacaoBensMoveis() {
        return tipoOperacaoBensMoveis;
    }

    public void setTipoOperacaoBensMoveis(TipoOperacaoBensMoveis tipoOperacaoBensMoveis) {
        this.tipoOperacaoBensMoveis = tipoOperacaoBensMoveis;
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public TipoOperacaoBensImoveis getTipoOperacaoBensImoveis() {
        return tipoOperacaoBensImoveis;
    }

    public void setTipoOperacaoBensImoveis(TipoOperacaoBensImoveis tipoOperacaoBensImoveis) {
        this.tipoOperacaoBensImoveis = tipoOperacaoBensImoveis;
    }

    public TipoOperacaoBensIntangiveis getTipoOperacaoBensIntangiveis() {
        return tipoOperacaoBensIntangiveis;
    }

    public void setTipoOperacaoBensIntangiveis(TipoOperacaoBensIntangiveis tipoOperacaoBensIntangiveis) {
        this.tipoOperacaoBensIntangiveis = tipoOperacaoBensIntangiveis;
    }

    public OperacaoMovimentoDividaPublica getOperacaoMovimentoDividaPublica() {
        return operacaoMovimentoDividaPublica;
    }

    public void setOperacaoMovimentoDividaPublica(OperacaoMovimentoDividaPublica operacaoMovimentoDividaPublica) {
        this.operacaoMovimentoDividaPublica = operacaoMovimentoDividaPublica;
    }

    public TipoEstoque getTipoEstoque() {
        return tipoEstoque;
    }

    public void setTipoEstoque(TipoEstoque tipoEstoque) {
        this.tipoEstoque = tipoEstoque;
    }

    public TipoOperacaoBensEstoque getTipoOperacaoBensEstoque() {
        return tipoOperacaoBensEstoque;
    }

    public void setTipoOperacaoBensEstoque(TipoOperacaoBensEstoque tipoOperacaoBensEstoque) {
        this.tipoOperacaoBensEstoque = tipoOperacaoBensEstoque;
    }

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    public ConsultaMovimentoContabil.Ordem getCampoOrdenar() {
        return campoOrdenar;
    }

    public void setCampoOrdenar(ConsultaMovimentoContabil.Ordem campoOrdenar) {
        this.campoOrdenar = campoOrdenar;
    }

    public Integer getQuantidadeDeRegistros() {
        return quantidadeDeRegistros;
    }

    public void setQuantidadeDeRegistros(Integer quantidadeDeRegistros) {
        this.quantidadeDeRegistros = quantidadeDeRegistros;
    }

    public Long getIdParametroEvento() {
        return idParametroEvento;
    }

    public void setIdParametroEvento(Long idParametroEvento) {
        this.idParametroEvento = idParametroEvento;
    }

    public Boolean getMostrarContabilizacao() {
        return mostrarContabilizacao == null ? Boolean.FALSE : mostrarContabilizacao;
    }

    public void setMostrarContabilizacao(Boolean mostrarContabilizacao) {
        this.mostrarContabilizacao = mostrarContabilizacao;
    }

    public List<Conta> getContasDebitos() {
        return contasDebitos;
    }

    public void setContasDebitos(List<Conta> contasDebitos) {
        this.contasDebitos = contasDebitos;
    }

    public List<Conta> getContasCreditos() {
        return contasCreditos;
    }

    public void setContasCreditos(List<Conta> contasCreditos) {
        this.contasCreditos = contasCreditos;
    }

    public List<FonteDeRecursos> getFontesDebitos() {
        return fontesDebitos;
    }

    public void setFontesDebitos(List<FonteDeRecursos> fontesDebitos) {
        this.fontesDebitos = fontesDebitos;
    }

    public List<FonteDeRecursos> getFontesCreditos() {
        return fontesCreditos;
    }

    public void setFontesCreditos(List<FonteDeRecursos> fontesCreditos) {
        this.fontesCreditos = fontesCreditos;
    }

    public Boolean isTipoEventoContabil(List<TipoEventoContabil> tipos) {
        for (TipoEventoContabil tipo : tipos) {
            if (tipoEventoContabil.equals(tipo)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
