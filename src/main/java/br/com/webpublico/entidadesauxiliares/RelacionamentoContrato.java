package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ExecucaoContrato;
import br.com.webpublico.entidades.FonteDespesaORC;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class RelacionamentoContrato {

    private Long id;
    private String movimento;
    private TipoRelacionamento tipo;
    private Date data;
    private String cor;

    private BigDecimal valorMovimento;
    private BigDecimal valorColunaReservado;
    private BigDecimal saldoColunaReservado;
    private FonteDespesaORC fonteDespesaORC;
    private Boolean gerarSaldoOrcamentario;
    private String idMovimento;
    private ExecucaoContrato execucaoContrato;

    public RelacionamentoContrato() {
        this.fontes = Lists.newArrayList();
        valorMovimento = BigDecimal.ZERO;
        valorColunaReservado = BigDecimal.ZERO;
        saldoColunaReservado = BigDecimal.ZERO;
        gerarSaldoOrcamentario = false;
    }

    private List<RelacionamentoContrato> fontes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMovimento() {
        return movimento;
    }

    public void setMovimento(String movimento) {
        this.movimento = movimento;
    }

    public TipoRelacionamento getTipo() {
        return tipo;
    }

    public void setTipo(TipoRelacionamento tipo) {
        this.tipo = tipo;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public BigDecimal getValorMovimento() {
        return valorMovimento;
    }

    public void setValorMovimento(BigDecimal valorMovimento) {
        this.valorMovimento = valorMovimento;
    }


    public BigDecimal getValorColunaReservado() {
        return valorColunaReservado;
    }

    public void setValorColunaReservado(BigDecimal valorColunaReservado) {
        this.valorColunaReservado = valorColunaReservado;
    }

    public BigDecimal getSaldoColunaReservado() {
        return saldoColunaReservado;
    }

    public void setSaldoColunaReservado(BigDecimal saldoColunaReservado) {
        this.saldoColunaReservado = saldoColunaReservado;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public List<RelacionamentoContrato> getFontes() {
        return fontes;
    }

    public void setFontes(List<RelacionamentoContrato> fontes) {
        this.fontes = fontes;
    }

    public ExecucaoContrato getExecucaoContrato() {
        return execucaoContrato;
    }

    public void setExecucaoContrato(ExecucaoContrato execucaoContrato) {
        this.execucaoContrato = execucaoContrato;
    }

    public Boolean getGerarSaldoOrcamentario() {
        return gerarSaldoOrcamentario;
    }

    public void setGerarSaldoOrcamentario(Boolean gerarSaldoOrcamentario) {
        this.gerarSaldoOrcamentario = gerarSaldoOrcamentario;
    }

    public boolean isPortal() {
        return TipoRelacionamento.PORTAL.equals(tipo);
    }

    public boolean isRequisicaoCompra() {
        return TipoRelacionamento.REQUISICAO_COMPRA.equals(tipo);
    }

    public boolean isPagamento() {
        return TipoRelacionamento.PAGAMENTO_NORMAL.equals(tipo) || TipoRelacionamento.PAGAMENTO_RESTO.equals(tipo);
    }

    public boolean isExecucao() {
        return TipoRelacionamento.EXECUCAO.equals(tipo);
    }

    public boolean isEstornoExecucao() {
        return TipoRelacionamento.ESTORNO_EXECUCAO.equals(tipo);
    }

    public boolean isEmpenho() {
        return TipoRelacionamento.EMPENHO_NORMAL.equals(tipo) || TipoRelacionamento.EMPENHO_RESTO.equals(tipo);
    }

    public boolean isSolicitacaoEmpenho() {
        return TipoRelacionamento.SOLICITACAO_EMPENHO.equals(tipo);
    }

    public boolean isAditivo() {
        return TipoRelacionamento.ADITIVO.equals(tipo);
    }

    public boolean isApostilamento() {
        return TipoRelacionamento.APOSTILAMENTO.equals(tipo);
    }

    public boolean hasFontesExecucao() {
        return fontes != null && !fontes.isEmpty();
    }

    public String getIdMovimento() {
        return idMovimento;
    }

    public void setIdMovimento(String idMovimento) {
        this.idMovimento = idMovimento;
    }

    public boolean hasValorReservadoPorLicitacaoNegativo() {
        for (RelacionamentoContrato fonte : getFontes()) {
            if (fonte.getSaldoColunaReservado().compareTo(BigDecimal.ZERO) < 0) {
                return true;
            }
            break;
        }
        return false;
    }

    public enum Legenda {
        REMOVER("Remover", "green"),
        ALTERAR("Alterar", "blue"),
        SEM_ALTERACAO("Sem Alteração", "red");
        private String descricao;
        private String cor;

        Legenda(String descricao, String cor) {
            this.descricao = descricao;
            this.cor = cor;
        }

        public String getDescricao() {
            return descricao;
        }

        public String getCor() {
            return cor;
        }
    }

    public enum TipoRelacionamento {
        EXECUCAO("Execução", "/execucao-contrato-adm/ver/"),
        ESTORNO_EXECUCAO("Estorno de Execução", "/execucao-contrato-estorno/ver/"),
        SOLICITACAO_EMPENHO("Solicitação de Empenho", "/solicitacao-empenho/ver/"),
        EMPENHO_NORMAL("Empenho", "/empenho/ver/"),
        EMPENHO_RESTO("Empenho", "/empenho/resto-a-pagar/ver/"),
        ADITIVO("Aditivo", "/aditivo-contrato/ver/"),
        APOSTILAMENTO("Apostilamento", "/apostilamento-contrato/ver/"),
        PUBLICACAO("Publicação", "/publicacao-contrato/ver/"),
        PORTAL("Portal", ""),
        REQUISICAO_COMPRA("Requisição de Compra", "/requisicao-compra/ver/"),
        SOLICITACAO_AQUISICAO("Solicitação de Aquisição", "/solicitacao-aquisicao-bem-movel/ver/"),
        AQUISICAO("Aquisição", "/efetivacao-aquisicao-bem-movel/ver/"),
        ENTRADA_COMPRA("Entrada por Compra", "/entrada-por-compra/ver/"),
        LIQUIDACAO_NORMAL("Liquidação", "/liquidacao/ver/"),
        LIQUIDACAO_RESTO("Liquidação", "/liquidacao/resto-a-pagar/ver/"),
        PAGAMENTO_NORMAL("Pagamento", "/pagamento/ver/"),
        PAGAMENTO_RESTO("Pagamento", "/pagamento/resto-a-pagar/ver/");
        private String descricao;
        private String link;

        TipoRelacionamento(String descricao, String link) {
            this.descricao = descricao;
            this.link = link;
        }

        public String getDescricao() {
            return descricao;
        }

        public String getLink() {
            return link;
        }
    }
}
