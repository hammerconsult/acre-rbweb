package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCobrancaTributario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.faces.model.SelectItem;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 01/06/15
 * Time: 11:44
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractFiltroRelacaoLancamentoDebito {

    private String juncao;
    private StringBuilder where;
    private StringBuilder filtro;
    private TipoRelatorio tipoRelatorio;
    private Boolean somenteTotalizador;
    private Boolean totalizadoresPorBairro;
    private Boolean totalizadoresPorSecretaria;

    //Filtros Genericos
    private Integer exercicioDividaInicial;
    private Integer exercicioDividaFinal;
    private Exercicio exercicioInicial;
    private Exercicio exercicioFinal;
    private Date dataLancamentoInicial;
    private Date dataLancamentoFinal;
    private Date dataVencimentoInicial;
    private Date dataVencimentoFinal;
    private Date dataPagamentoInicial;
    private Date dataPagamentoFinal;
    private Date dataMovimentoInicial;
    private Date dataMovimentoFinal;
    private Pessoa pessoa;
    private List<Pessoa> contribuintes;
    private SituacaoParcela situacaoParcela;
    private SituacaoParcela[] situacoes;
    private Divida divida;
    private List<Divida> dividas;
    private BigDecimal totalLancadoInicial;
    private BigDecimal totalLancadoFinal;
    private Tributo tributo;
    private UsuarioSistema fiscalDesignado;
    private List<UsuarioSistema> fiscaisDesignados;
    private TipoCobrancaTributario tipoCobrancaTributario;
    private boolean emitirHabitese;

    public void FiltroRelacaoLancamentoDebitos() {
        this.situacoes = new SituacaoParcela[]{SituacaoParcela.EM_ABERTO};
        this.tipoRelatorio = TipoRelatorio.RESUMIDO;
        this.somenteTotalizador = Boolean.FALSE;
        this.totalizadoresPorBairro = Boolean.FALSE;
        this.totalizadoresPorSecretaria = Boolean.FALSE;
        this.tipoCobrancaTributario = null;
        this.contribuintes = Lists.newArrayList();
    }

    public Integer getExercicioDividaInicial() {
        return exercicioDividaInicial;
    }

    public void setExercicioDividaInicial(Integer exercicioDividaInicial) {
        this.exercicioDividaInicial = exercicioDividaInicial;
    }

    public Integer getExercicioDividaFinal() {
        return exercicioDividaFinal;
    }

    public void setExercicioDividaFinal(Integer exercicioDividaFinal) {
        this.exercicioDividaFinal = exercicioDividaFinal;
    }

    public Date getDataLancamentoInicial() {
        return dataLancamentoInicial;
    }

    public void setDataLancamentoInicial(Date dataLancamentoInicial) {
        this.dataLancamentoInicial = dataLancamentoInicial;
    }

    public Date getDataLancamentoFinal() {
        return dataLancamentoFinal;
    }

    public void setDataLancamentoFinal(Date dataLancamentoFinal) {
        this.dataLancamentoFinal = dataLancamentoFinal;
    }

    public Date getDataVencimentoInicial() {
        return dataVencimentoInicial;
    }

    public void setDataVencimentoInicial(Date dataVencimentoInicial) {
        this.dataVencimentoInicial = dataVencimentoInicial;
    }

    public Date getDataVencimentoFinal() {
        return dataVencimentoFinal;
    }

    public void setDataVencimentoFinal(Date dataVencimentoFinal) {
        this.dataVencimentoFinal = dataVencimentoFinal;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<Pessoa> getContribuintes() {
        return contribuintes;
    }

    public void setContribuintes(List<Pessoa> contribuintes) {
        this.contribuintes = contribuintes;
    }

    public SituacaoParcela[] getSituacoes() {
        return situacoes;
    }

    public void setSituacoes(SituacaoParcela[] situacoes) {
        this.situacoes = situacoes;
    }

    public BigDecimal getTotalLancadoInicial() {
        return totalLancadoInicial;
    }

    public void setTotalLancadoInicial(BigDecimal totalLancadoInicial) {
        this.totalLancadoInicial = totalLancadoInicial;
    }

    public Exercicio getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Exercicio exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Exercicio getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Exercicio exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public BigDecimal getTotalLancadoFinal() {
        return totalLancadoFinal;
    }

    public void setTotalLancadoFinal(BigDecimal totalLancadoFinal) {
        this.totalLancadoFinal = totalLancadoFinal;
    }

    public String getJuncao() {
        return juncao;
    }

    public void setJuncao(String juncao) {
        this.juncao = juncao;
    }

    public StringBuilder getWhere() {
        return where;
    }

    public void setWhere(StringBuilder where) {
        this.where = where;
    }

    public StringBuilder getFiltro() {
        return filtro;
    }

    public void setFiltro(StringBuilder filtro) {
        this.filtro = filtro;
    }

    public TipoRelatorio getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(TipoRelatorio tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public Boolean getSomenteTotalizador() {
        return somenteTotalizador;
    }

    public void setSomenteTotalizador(Boolean somenteTotalizador) {
        this.somenteTotalizador = somenteTotalizador;
    }

    public Boolean getTotalizadoresPorBairro() {
        return totalizadoresPorBairro;
    }

    public void setTotalizadoresPorBairro(Boolean totalizadoresPorBairro) {
        this.totalizadoresPorBairro = totalizadoresPorBairro;
    }

    public Boolean getTotalizadoresPorSecretaria() {
        return totalizadoresPorSecretaria;
    }

    public void setTotalizadoresPorSecretaria(Boolean totalizadoresPorSecretaria) {
        this.totalizadoresPorSecretaria = totalizadoresPorSecretaria;
    }

    public Date getDataPagamentoInicial() {
        return dataPagamentoInicial;
    }

    public void setDataPagamentoInicial(Date dataPagamentoInicial) {
        this.dataPagamentoInicial = dataPagamentoInicial;
    }

    public Date getDataPagamentoFinal() {
        return dataPagamentoFinal;
    }

    public void setDataPagamentoFinal(Date dataPagamentoFinal) {
        this.dataPagamentoFinal = dataPagamentoFinal;
    }

    public Date getDataMovimentoInicial() {
        return dataMovimentoInicial;
    }

    public void setDataMovimentoInicial(Date dataMovimentoInicial) {
        this.dataMovimentoInicial = dataMovimentoInicial;
    }

    public Date getDataMovimentoFinal() {
        return dataMovimentoFinal;
    }

    public void setDataMovimentoFinal(Date dataMovimentoFinal) {
        this.dataMovimentoFinal = dataMovimentoFinal;
    }

    public SituacaoParcela getSituacaoParcela() {
        return situacaoParcela;
    }

    public void setSituacaoParcela(SituacaoParcela situacaoParcela) {
        this.situacaoParcela = situacaoParcela;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public List<Divida> getDividas() {
        return dividas;
    }

    public void setDividas(List<Divida> dividas) {
        this.dividas = dividas;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public UsuarioSistema getFiscalDesignado() {
        return fiscalDesignado;
    }

    public void setFiscalDesignado(UsuarioSistema fiscalDesignado) {
        this.fiscalDesignado = fiscalDesignado;
    }

    public List<UsuarioSistema> getFiscaisDesignados() {
        return fiscaisDesignados;
    }

    public void setFiscaisDesignados(List<UsuarioSistema> fiscaisDesignados) {
        this.fiscaisDesignados = fiscaisDesignados;
    }

    public TipoCobrancaTributario getTipoCobrancaTributario() {
        return tipoCobrancaTributario;
    }

    public void setTipoCobrancaTributario(TipoCobrancaTributario tipoCobrancaTributario) {
        this.tipoCobrancaTributario = tipoCobrancaTributario;
    }

    public boolean isEmitirHabitese() {
        return emitirHabitese;
    }

    public void setEmitirHabitese(boolean emitirHabitese) {
        this.emitirHabitese = emitirHabitese;
    }

    public void inicializarFiltro() {
        this.juncao = " where ";
        this.where = new StringBuilder();
        this.filtro = new StringBuilder();
    }

    public void addFiltroExists(String sqlExists) {
        where.append(juncao);
        where.append(" exists (").append(sqlExists).append(") ");
        juncao = " and ";
    }

    public void addFiltroNotExists(String sqlExists) {
        where.append(juncao);
        where.append(" not exists (").append(sqlExists).append(") ");
        juncao = " and ";
    }

    public void addFiltroLivre(String sqlFiltroLivre) {
        where.append(juncao);
        where.append(sqlFiltroLivre);
        juncao = " and ";
    }

    public void addFiltro(String campo, String operador, String descricaoFiltro, Integer valor) {
        if (valor != null && valor > 0) {
            where.append(juncao);
            where.append(" ").append(campo).append(" ").append(operador).append(" ").append(valor);

            filtro.append(" ").append(descricaoFiltro).append(": ");
            filtro.append(valor).append("; ");
            juncao = " and ";
        }
    }

    public void addFiltro(String campo, String operador, String descricaoFiltro, Long valor) {
        if (valor != null && valor > 0) {
            where.append(juncao);
            where.append(" ").append(campo).append(" ").append(operador).append(" ").append(valor);

            filtro.append(" ").append(descricaoFiltro).append(": ");
            filtro.append(valor).append("; ");
            juncao = " and ";
        }
    }

    public void addFiltroNotNull(String campo, String descricaoFiltro, String valorFiltro) {
        where.append(juncao);
        where.append(campo).append(" is not null ");

        filtro.append(" ").append(descricaoFiltro).append(": ");
        filtro.append(valorFiltro).append("; ");
        juncao = " and ";
    }

    public void addFiltroNull(String campo) {
        where.append(juncao);
        where.append(campo).append(" is null ");

        juncao = " and ";
    }

    public void addFiltro(String campo, String operador, String descricaoFiltro, BigDecimal valor) {
        if (valor != null && valor.compareTo(BigDecimal.ZERO) > 0) {
            where.append(juncao);
            where.append(" ").append(campo).append(" ").append(operador).append(" ").append(valor);

            filtro.append(" ").append(descricaoFiltro).append(": ");
            filtro.append(valor).append("; ");
            juncao = " and ";
        }
    }

    public void addFiltro(String campo, String operador, String descricaoFiltro, Long valor, String valorFiltro) {
        if (valor != null) {
            where.append(juncao);
            where.append(" ").append(campo).append(" ").append(operador).append(" ").append(valor);

            filtro.append(" ").append(descricaoFiltro).append(": ");
            filtro.append(valorFiltro).append("; ");
            juncao = " and ";
        }
    }

    public void addFiltro(String campo, String operador, String descricaoFiltro, Integer valor, String valorFiltro) {
        if (valor != null) {
            where.append(juncao);
            where.append(" ").append(campo).append(" ").append(operador).append(" ").append(valor);

            filtro.append(" ").append(descricaoFiltro).append(": ");
            filtro.append(valorFiltro).append("; ");
            juncao = " and ";
        }
    }

    public void addFiltro(String campo, String operador, String descricaoFiltro, Date valor) {
        if (valor != null) {
            where.append(juncao);
            where.append(" ").append(campo).append(" ").append(operador).append(" to_date('").append(Util.dateToString(valor)).append("', 'dd/MM/yyyy') ");

            filtro.append(" ").append(descricaoFiltro).append(": ");
            filtro.append(Util.dateToString(valor)).append("; ");
            juncao = " and ";
        }
    }

    public void addFiltro(String campo, String operador, String descricaoFiltro, Enum valor) {
        if (valor != null) {
            where.append(juncao);
            where.append(" ").append(campo).append(" ").append(operador).append(" '").append(valor.name()).append("' ");

            filtro.append(" ").append(descricaoFiltro).append(": ");
            filtro.append(valor.toString()).append("; ");
            juncao = " and ";
        }
    }

    public void addFiltro(String campo, String operador, String descricaoFiltro, String valor) {
        if (valor != null && !valor.trim().isEmpty()) {
            where.append(juncao);
            where.append(" ").append(campo).append(" ").append(operador).append(" '").append(valor.trim()).append("' ");

            filtro.append(" ").append(descricaoFiltro).append(": ");
            filtro.append(valor.trim()).append("; ");
            juncao = " and ";
        }
    }

    public void addFiltroIn(String campo, String descricaoFiltro, String valor, String valorFiltro) {
        if (valor != null && !valor.trim().isEmpty()) {
            where.append(juncao);
            where.append(" ").append("(");
            List<String> valores = Lists.newArrayList(valor.split(","));
            List<List<String>> inValores = Lists.partition(valores, 1000);
            for (List<String> inAtual : inValores) {
                String sql = "";
                for (String string : inAtual) {
                    sql += string + ",";
                }
                sql = sql.substring(0, sql.length() - 1);
                where.append(campo).append(" in(").append(sql).append(") or ");
            }
            where.deleteCharAt(where.length()-1);
            where.deleteCharAt(where.length()-1);
            where.deleteCharAt(where.length()-1);
            where.append(") ");

            filtro.append(" ").append(descricaoFiltro).append(": ");
            filtro.append(valorFiltro).append("; ");
            juncao = " and ";
        }
    }

    public void addFiltroIn(String campo, String valor) {
        if (valor != null && !valor.trim().isEmpty()) {
            where.append(juncao);
            where.append(" ").append(campo).append(" in (").append(valor).append(") ");

            juncao = " and ";
        }
    }

    public void addApenasFiltro(String descricaoFiltro, String valorFiltro) {
        filtro.append(" ").append(descricaoFiltro).append(": ");
        filtro.append(valorFiltro).append("; ");
    }

    public List<SelectItem> tiposRelatorio() {
        List<SelectItem> itens = Lists.newArrayList();
        for (TipoRelatorio tipo : TipoRelatorio.values()) {
            itens.add(new SelectItem(tipo, tipo.toString()));
        }
        return itens;
    }

    public void addContribuinte() {
        if (this.getPessoa() == null) {
            FacesUtil.addCampoObrigatorio("Por favor selecione um contribuinte!");
            return;
        }
        if (this.getContribuintes() != null && this.getContribuintes().contains(this.getPessoa())) {
            FacesUtil.addCampoObrigatorio("Contribuinte já foi adicionado a pesquisa!");
            return;
        }
        if (this.getContribuintes() == null) {
            this.setContribuintes(Lists.<Pessoa>newArrayList());
        }
        this.getContribuintes().add(pessoa);
        this.setPessoa(null);
    }

    public void delContribuinte(Pessoa pessoa) {
        contribuintes.remove(pessoa);
    }

    public void addDivida() {
        try {
            validarDividas();
            if (dividas == null) {
                dividas = Lists.newArrayList();
            }
            dividas.add(divida);
            divida = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void delDivida(Divida divida) {
        dividas.remove(divida);
    }

    private void validarDividas() {
        ValidacaoException ve = new ValidacaoException();
        if (dividas != null && divida != null && dividas.contains(divida)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Dívida já adicionada!");
        }
        ve.lancarException();
    }

    public String inDividas() {
        StringBuilder in = new StringBuilder();
        String juncao = " ";

        if (dividas != null && dividas.size() > 0) {
            for (Divida div : dividas) {
                in.append(juncao);
                in.append(div.getId());
                juncao = ", ";
            }
        }
        return in.toString();
    }

    public String filtroDividas() {
        StringBuilder selecionados = new StringBuilder();

        if (dividas != null && dividas.size() > 0) {
            for (Divida div : dividas) {
                selecionados.append(div.getDescricao());
                selecionados.append("; ");
            }
        }
        return selecionados.toString();
    }

    public void adicionarFiscalDesignado() {
        if (fiscalDesignado == null) {
            FacesUtil.addCampoObrigatorio("Por favor selecione um fiscal designado!");
            return;
        }
        if (fiscaisDesignados != null && fiscaisDesignados.contains(this.getFiscalDesignado())) {
            FacesUtil.addCampoObrigatorio("Fiscal Designado já foi adicionado a pesquisa!");
            return;
        }
        if (fiscaisDesignados == null) {
            fiscaisDesignados = Lists.newArrayList();
        }
        this.fiscaisDesignados.add(fiscalDesignado);
        this.fiscalDesignado = null;
    }

    public void removerFiscalDesignado(UsuarioSistema fiscalDesignado) {
        fiscaisDesignados.remove(fiscalDesignado);
    }

    @Override
    public String toString() {
        return filtro != null ? filtro.toString() : "";
    }

    public enum TipoRelatorio {
        RESUMIDO("Resumido"), DETALHADO("Detalhado");

        private String descricao;

        TipoRelatorio(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
