package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;

public class RequisicaoCompraEmpenhoVO implements Comparable<RequisicaoCompraEmpenhoVO> {

    private Empenho empenho;
    private ExecucaoContratoEmpenho execucaoContratoEmpenho;
    private ExecucaoProcessoEmpenho execucaoProcessoEmpenho;
    private SolicitacaoEmpenhoReconhecimentoDivida execucaoReconhecimentoDivida;
    private DesdobramentoEmpenho desdobramentoEmpenho;
    private Long idGrupo;
    private String codigoGrupo;
    private Boolean selecionado;

    public RequisicaoCompraEmpenhoVO() {
        selecionado = false;
    }

    public String getCodigoGrupo() {
        return codigoGrupo;
    }

    public void setCodigoGrupo(String codigoGrupo) {
        this.codigoGrupo = codigoGrupo;
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public ExecucaoContratoEmpenho getExecucaoContratoEmpenho() {
        return execucaoContratoEmpenho;
    }

    public void setExecucaoContratoEmpenho(ExecucaoContratoEmpenho execucaoContratoEmpenho) {
        this.execucaoContratoEmpenho = execucaoContratoEmpenho;
    }

    public ExecucaoProcessoEmpenho getExecucaoProcessoEmpenho() {
        return execucaoProcessoEmpenho;
    }

    public void setExecucaoProcessoEmpenho(ExecucaoProcessoEmpenho execucaoProcessoEmpenho) {
        this.execucaoProcessoEmpenho = execucaoProcessoEmpenho;
    }

    public DesdobramentoEmpenho getDesdobramentoEmpenho() {
        return desdobramentoEmpenho;
    }

    public void setDesdobramentoEmpenho(DesdobramentoEmpenho desdobramentoEmpenho) {
        this.desdobramentoEmpenho = desdobramentoEmpenho;
    }

    public SolicitacaoEmpenhoReconhecimentoDivida getExecucaoReconhecimentoDivida() {
        return execucaoReconhecimentoDivida;
    }

    public void setExecucaoReconhecimentoDivida(SolicitacaoEmpenhoReconhecimentoDivida execucaoReconhecimentoDivida) {
        this.execucaoReconhecimentoDivida = execucaoReconhecimentoDivida;
    }

    public Long getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(Long idGrupo) {
        this.idGrupo = idGrupo;
    }

    public Boolean getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    public Long getIdExecucao() {
        if (execucaoContratoEmpenho != null) {
            return execucaoContratoEmpenho.getExecucaoContrato().getId();
        } else if (execucaoProcessoEmpenho != null) {
            return execucaoProcessoEmpenho.getExecucaoProcesso().getId();
        }
        return execucaoReconhecimentoDivida.getReconhecimentoDivida().getId();
    }

    public String getToStringEmpenho() {
        try {
            String s = empenho.getNumero()
                + "/" + empenho.getExercicio().getAno()
                + " - " + empenho.getCategoriaOrcamentaria().getDescricao()
                + " (" + Util.formataValor(empenho.getValor()) + ")";
            s += desdobramentoEmpenho != null ? ", <b>Desdobramento:</b> " + desdobramentoEmpenho.getConta().getCodigo() : "";
            s += ", <b>Fonte:</b> " + empenho.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos().getCodigo();
            s += !Strings.isNullOrEmpty(codigoGrupo) ? ", <b>Grupo:</b> " + codigoGrupo : "";
            return s;
        } catch (NullPointerException e) {
            return empenho.getNumero()
                + "/" + empenho.getExercicio().getAno()
                + " - " + empenho.getCategoriaOrcamentaria().getDescricao()
                + " (" + Util.formataValor(empenho.getValor()) + ")";
        }
    }

    @Override
    public int compareTo(RequisicaoCompraEmpenhoVO reqEmpVO) {
        if (reqEmpVO.getEmpenho() != null && getEmpenho() != null) {
            return ComparisonChain.start().compare(getEmpenho().getNumero(), reqEmpVO.getEmpenho().getNumero()).result();
        }
        return 0;
    }
}
