package br.com.webpublico.entidadesauxiliares.contabil;

import br.com.webpublico.enums.OperacaoORC;
import br.com.webpublico.enums.TipoOperacaoORC;
import br.com.webpublico.webreportdto.dto.contabil.MovimentoDespesaORCDTO;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class MovimentoDespesaORCVO {

    private BigDecimal valor;
    private OperacaoORC operacaoORC;
    private String tipoOperacaoORC;
    private String idOrigem;
    private String classeOrigem;
    private String numeroMovimento;
    private String historico;
    private String href;
    private Date dataMovimento;
    private String despesa;
    private String fonte;

    public MovimentoDespesaORCVO() {
    }

    public static List<MovimentoDespesaORCDTO> movimentosToDto(List<MovimentoDespesaORCVO> movimentos) {
        List<MovimentoDespesaORCDTO> retorno = Lists.newArrayList();
        if (movimentos != null && !movimentos.isEmpty()) {
            for (MovimentoDespesaORCVO movimento : movimentos) {
                retorno.add(movimentoToDto(movimento));
            }
        }
        return retorno;
    }

    public static MovimentoDespesaORCDTO movimentoToDto(MovimentoDespesaORCVO movimento) {
        MovimentoDespesaORCDTO retorno = new MovimentoDespesaORCDTO();
        retorno.setOperacao(movimento.getOperacaoORC().getDescricao());
        retorno.setTipoLancamento(movimento.getTipoOperacaoORC());
        retorno.setClasseDeOrigem(MovimentoDespesaORCVO.getDescricaoClasseOrigem(movimento.getClasseOrigem()));
        retorno.setNumeroOrigem(movimento.getNumeroMovimento());
        retorno.setHistorico(movimento.getHistorico());
        retorno.setValor(movimento.getValor());
        return retorno;
    }

    public OperacaoORC getOperacaoORC() {
        return operacaoORC;
    }

    public void setOperacaoORC(OperacaoORC operacaoORC) {
        this.operacaoORC = operacaoORC;
    }

    public String getTipoOperacaoORC() {
        return tipoOperacaoORC;
    }

    public void setTipoOperacaoORC(String tipoOperacaoORC) {
        this.tipoOperacaoORC = tipoOperacaoORC;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getIdOrigem() {
        return idOrigem;
    }

    public void setIdOrigem(String idOrigem) {
        this.idOrigem = idOrigem;
    }

    public String getClasseOrigem() {
        return classeOrigem;
    }

    public void setClasseOrigem(String classeOrigem) {
        this.classeOrigem = classeOrigem;
    }

    public String getNumeroMovimento() {
        return numeroMovimento;
    }

    public void setNumeroMovimento(String numeroMovimento) {
        this.numeroMovimento = numeroMovimento;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public Date getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(Date dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public String getDespesa() {
        return despesa;
    }

    public void setDespesa(String despesa) {
        this.despesa = despesa;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    public BigDecimal getValorEmpenhado() {
        if (isEmpenhado()) {
            return valor;
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getValorEmpenhadoEstorno() {
        if (isEmpenhadoEstorno()) {
            return valor;
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getValorReservadoPorLicitacao() {
        if (isReservadoPorLicitacao()) {
            return valor;
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getValorReservadoPorLicitacaoEstorno() {
        if (isReservadoPorLicitacaoEstorno()) {
            return valor;
        }
        return BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return operacaoORC + " - " + valor;
    }

    public Boolean isTipoOperacaoNormal() {
        return TipoOperacaoORC.NORMAL.getDescricao().equals(this.getTipoOperacaoORC());
    }

    public Boolean isTipoOperacaoEstorno() {
        return TipoOperacaoORC.ESTORNO.getDescricao().equals(this.getTipoOperacaoORC());
    }

    public Boolean isEmpenhado() {
        return isTipoOperacaoNormal() && operacaoORC.isEmpenhado();
    }

    public Boolean isEmpenhadoEstorno() {
        return isTipoOperacaoEstorno() && operacaoORC.isEmpenhado();
    }

    public Boolean isReservadoPorLicitacao() {
        return isTipoOperacaoNormal() && operacaoORC.isReservadoPorLicitacao();
    }

    public Boolean isReservadoPorLicitacaoEstorno() {
        return isTipoOperacaoEstorno() && operacaoORC.isReservadoPorLicitacao();
    }

    public String getDescricaoClasseOrigem() {
        return getDescricaoClasseOrigem(classeOrigem);
    }

    public static final String getDescricaoClasseOrigem(String classeOrigem) {
        switch (classeOrigem) {
            case "EmpenhoEstorno":
                return "Estorno de Empenho";
            case "Liquidacao":
                return "Liquidação";
            case "LiquidacaoEstorno":
                return "Estorno de Liquidação";
            case "PagamentoEstorno":
                return "Estorno de Pagamento";
            case "SolicitacaoEmpenho":
                return "Solicitação de Empenho";
            case "PropostaConcessaoDiaria":
                return "Diária/Suprimento de Fundo";
            case "CancelamentoReservaDotacao":
                return "Cancelamento de Reserva de Dotação";
            case "DotacaoSolicitacaoMaterialItemFonte":
                return "Dotação Solicitação de Compra";
            case "ExecucaoContratoTipoFonte":
                return "Dotação Execução Contrato";
            case "ExecucaoProcessoFonte":
                return "Dotação Execução Processo";
            case "ReconhecimentoDivida":
                return "Reconhecimento de Dívida";
            case "ExecucaoContratoEmpenhoEstorno":
                return "Dotação Execução Contrato Estorno";
            case "ExecucaoProcessoEmpenhoEstorno":
                return "Dotação Execução Processo Estorno";
            case "ProvisaoPPAFonte":
                return "Provisão PPA Fonte";
            case "AnulacaoORC":
                return "Anulação Orçamentária";
            case "SuplementacaoORC":
                return "Suplementação Orçamentária";
            case "ReservaFonteDespesaOrc":
                return "Reserva Fonte de Despesa Orçamentária";
            case "MovimentoAlteracaoContratual":
                return "Aditivo/Apostilamento";
            default:
                return classeOrigem;
        }
    }
}
