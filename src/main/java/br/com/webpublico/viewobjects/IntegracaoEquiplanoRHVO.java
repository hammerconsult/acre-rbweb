package br.com.webpublico.viewobjects;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.enums.rh.integracao.TipoContabilizacao;
import br.com.webpublico.enums.rh.integracao.TipoContribuicao;

public class IntegracaoEquiplanoRHVO implements Comparable<IntegracaoEquiplanoRHVO> {
    private Long idFonteEvento;
    private RecursoFP recursoFP;
    private FontesRecursoFP fontesRecursoFP;
    private TipoContabilizacao tipoContabilizacao;
    private HierarquiaOrganizacional orcamentaria;
    private HierarquiaOrganizacional administrativa;
    private DespesaORC despesaORC;
    private FonteDespesaORC fonteDespesaORC;
    private FonteDeRecursos fonteDeRecursos;
    private Pessoa fornecedor;
    private Pessoa credor;
    private ClasseCredor classeCredor;
    private ContaExtraorcamentaria contaExtraorcamentaria;
    private SubConta subConta;
    private EventoFP eventoFP;
    private OperacaoFormula operacaoFormula;
    private Conta desdobramento;
    private TipoContribuicao tipoContribuicao;
    private ContaDeDestinacao contaDeDestinacao;
    private Boolean editTipoContabilizacao;
    private Boolean editDespesaOrc;
    private Boolean editContaDeDestinacao;
    private Boolean editSubConta;
    private Boolean editFornecedor;
    private Boolean editContaExtraorcamentaria;
    private Boolean editCredor;
    private Boolean editTipoContribuicao;
    private Boolean editOperacaoFormula;

    public IntegracaoEquiplanoRHVO() {
        this.editTipoContabilizacao = Boolean.FALSE;
        this.editDespesaOrc = Boolean.FALSE;
        this.editContaDeDestinacao = Boolean.FALSE;
        this.editSubConta = Boolean.FALSE;
        this.editFornecedor = Boolean.FALSE;
        this.editContaExtraorcamentaria = Boolean.FALSE;
        this.editCredor = Boolean.FALSE;
        this.editTipoContribuicao = Boolean.FALSE;
        this.editOperacaoFormula = Boolean.FALSE;
    }

    public Long getIdFonteEvento() {
        return idFonteEvento;
    }

    public void setIdFonteEvento(Long idFonteEvento) {
        this.idFonteEvento = idFonteEvento;
    }

    public RecursoFP getRecursoFP() {
        return recursoFP;
    }

    public void setRecursoFP(RecursoFP recursoFP) {
        this.recursoFP = recursoFP;
    }

    public FontesRecursoFP getFontesRecursoFP() {
        return fontesRecursoFP;
    }

    public void setFontesRecursoFP(FontesRecursoFP fontesRecursoFP) {
        this.fontesRecursoFP = fontesRecursoFP;
    }

    public TipoContabilizacao getTipoContabilizacao() {
        return tipoContabilizacao;
    }

    public void setTipoContabilizacao(TipoContabilizacao tipoContabilizacao) {
        this.tipoContabilizacao = tipoContabilizacao;
    }

    public DespesaORC getDespesaORC() {
        return despesaORC;
    }

    public void setDespesaORC(DespesaORC despesaORC) {
        this.despesaORC = despesaORC;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    public Pessoa getCredor() {
        return credor;
    }

    public void setCredor(Pessoa credor) {
        this.credor = credor;
    }


    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public ContaExtraorcamentaria getContaExtraorcamentaria() {
        return contaExtraorcamentaria;
    }

    public void setContaExtraorcamentaria(ContaExtraorcamentaria contaExtraorcamentaria) {
        this.contaExtraorcamentaria = contaExtraorcamentaria;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public EventoFP getEventoFP() {
        return eventoFP;
    }

    public void setEventoFP(EventoFP eventoFP) {
        this.eventoFP = eventoFP;
    }

    public OperacaoFormula getOperacaoFormula() {
        return operacaoFormula;
    }

    public void setOperacaoFormula(OperacaoFormula operacaoFormula) {
        this.operacaoFormula = operacaoFormula;
    }

    public Conta getDesdobramento() {
        return desdobramento;
    }

    public void setDesdobramento(Conta desdobramento) {
        this.desdobramento = desdobramento;
    }

    public TipoContribuicao getTipoContribuicao() {
        return tipoContribuicao;
    }

    public void setTipoContribuicao(TipoContribuicao tipoContribuicao) {
        this.tipoContribuicao = tipoContribuicao;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public Boolean getEditTipoContabilizacao() {
        return editTipoContabilizacao;
    }

    public void setEditTipoContabilizacao(Boolean editTipoContabilizacao) {
        this.editTipoContabilizacao = editTipoContabilizacao;
    }

    public Boolean getEditDespesaOrc() {
        return editDespesaOrc;
    }

    public void setEditDespesaOrc(Boolean editDespesaOrc) {
        this.editDespesaOrc = editDespesaOrc;
    }

    public Boolean getEditContaDeDestinacao() {
        return editContaDeDestinacao;
    }

    public void setEditContaDeDestinacao(Boolean editContaDeDestinacao) {
        this.editContaDeDestinacao = editContaDeDestinacao;
    }

    public Boolean getEditSubConta() {
        return editSubConta;
    }

    public void setEditSubConta(Boolean editSubConta) {
        this.editSubConta = editSubConta;
    }

    public Boolean getEditFornecedor() {
        return editFornecedor;
    }

    public void setEditFornecedor(Boolean editFornecedor) {
        this.editFornecedor = editFornecedor;
    }

    public Boolean getEditContaExtraorcamentaria() {
        return editContaExtraorcamentaria;
    }

    public void setEditContaExtraorcamentaria(Boolean editContaExtraorcamentaria) {
        this.editContaExtraorcamentaria = editContaExtraorcamentaria;
    }

    public Boolean getEditCredor() {
        return editCredor;
    }

    public void setEditCredor(Boolean editCredor) {
        this.editCredor = editCredor;
    }

    public Boolean getEditTipoContribuicao() {
        return editTipoContribuicao;
    }

    public void setEditTipoContribuicao(Boolean editTipoContribuicao) {
        this.editTipoContribuicao = editTipoContribuicao;
    }

    public Boolean getEditOperacaoFormula() {
        return editOperacaoFormula;
    }

    public void setEditOperacaoFormula(Boolean editOperacaoFormula) {
        this.editOperacaoFormula = editOperacaoFormula;
    }

    public HierarquiaOrganizacional getOrcamentaria() {
        return orcamentaria;
    }

    public void setOrcamentaria(HierarquiaOrganizacional orcamentaria) {
        this.orcamentaria = orcamentaria;
    }

    public HierarquiaOrganizacional getAdministrativa() {
        return administrativa;
    }

    public void setAdministrativa(HierarquiaOrganizacional administrativa) {
        this.administrativa = administrativa;
    }

    @Override
    public int compareTo(IntegracaoEquiplanoRHVO o) {
        try {
            int resultado = this.tipoContabilizacao == null && o.getTipoContabilizacao() != null ? -1 : 0;
            if (resultado == 0)
                resultado = this.tipoContabilizacao != null && o.getTipoContabilizacao() == null ? 1 : 0;
            if (resultado == 0)
                resultado = this.getRecursoFP().getCodigo().compareTo(o.getRecursoFP().getCodigo());
            if (resultado == 0)
                resultado = this.getRecursoFP().getDescricao().compareTo(o.getRecursoFP().getDescricao());
            if (resultado == 0)
                resultado = this.getTipoContabilizacao().compareTo(o.getTipoContabilizacao());
            if (resultado == 0)
                resultado = this.getEventoFP().getCodigo().compareTo(o.getEventoFP().getCodigo());
            if (resultado == 0)
                resultado = this.getEventoFP().getDescricao().compareTo(o.getEventoFP().getDescricao());
            return resultado;
        } catch (NullPointerException e) {
            return 0;
        }
    }

    @Override
    public boolean equals(Object obj) {
        try {
            return getRecursoFP().equals(((IntegracaoEquiplanoRHVO) obj).getRecursoFP()) && getEventoFP().equals(((IntegracaoEquiplanoRHVO) obj).getEventoFP());
        } catch (Exception e) {
            return false;
        }
    }
}
