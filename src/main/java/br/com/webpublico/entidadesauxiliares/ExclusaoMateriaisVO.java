package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;

import java.util.Date;

public class ExclusaoMateriaisVO {

    private Long id;
    private Date data;
    private String movimento;
    private TipoRelacionamento tipoRelacionamento;
    private Boolean selecionado;

    private SaidaMaterial saidaMaterial;
    private EntradaMaterial entradaMaterial;
    private RequisicaoMaterial requisicaoMaterial;
    private AprovacaoMaterial aprovacaoMaterial;
    private Liquidacao liquidacao;
    private SaidaMaterialContabil saidaMaterialContabil;
    private EntradaMaterialContabil entradaMaterialContabil;

    public ExclusaoMateriaisVO() {
        selecionado = false;
    }

    public EntradaMaterialContabil getEntradaMaterialContabil() {
        return entradaMaterialContabil;
    }

    public void setEntradaMaterialContabil(EntradaMaterialContabil entradaMaterialContabil) {
        this.entradaMaterialContabil = entradaMaterialContabil;
    }

    public SaidaMaterialContabil getSaidaMaterialContabil() {
        return saidaMaterialContabil;
    }

    public void setSaidaMaterialContabil(SaidaMaterialContabil saidaMaterialContabil) {
        this.saidaMaterialContabil = saidaMaterialContabil;
    }

    public Boolean getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    public TipoRelacionamento getTipoRelacionamento() {
        return tipoRelacionamento;
    }

    public void setTipoRelacionamento(TipoRelacionamento tipoRelacionamento) {
        this.tipoRelacionamento = tipoRelacionamento;
    }

    public SaidaMaterial getSaidaMaterial() {
        return saidaMaterial;
    }

    public void setSaidaMaterial(SaidaMaterial saidaMaterial) {
        this.saidaMaterial = saidaMaterial;
    }

    public RequisicaoMaterial getRequisicaoMaterial() {
        return requisicaoMaterial;
    }

    public void setRequisicaoMaterial(RequisicaoMaterial requisicaoMaterial) {
        this.requisicaoMaterial = requisicaoMaterial;
    }

    public AprovacaoMaterial getAprovacaoMaterial() {
        return aprovacaoMaterial;
    }

    public void setAprovacaoMaterial(AprovacaoMaterial aprovacaoMaterial) {
        this.aprovacaoMaterial = aprovacaoMaterial;
    }

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

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }


    public EntradaMaterial getEntradaMaterial() {
        return entradaMaterial;
    }

    public void setEntradaMaterial(EntradaMaterial entradaMaterial) {
        this.entradaMaterial = entradaMaterial;
    }

    public Liquidacao getLiquidacao() {
        return liquidacao;
    }

    public void setLiquidacao(Liquidacao liquidacao) {
        this.liquidacao = liquidacao;
    }

    public String getLinkLiquidacao() {
        if (liquidacao != null) {
            return liquidacao.getCategoriaOrcamentaria().isNormal() ? "/liquidacao/ver/" : "/liquidacao/resto-a-pagar/ver/";
        }
        return "";
    }

    public enum TipoRelacionamento {
        SAIDA_MATERIAL_CONSUMO("Saída de Material por Consumo", "/saida-por-consumo/ver/", 2),
        REQUISICAO_MATERIAL("Requisião de Material por Consumo", "/requisicao-de-material/ver/", 4),
        APROVACAO_MATERIAL("Aprovação de Material", "/avaliacao-de-requisicao-de-material/ver/", 3),
        ENTRADA_MATERIAL_COMPRA("Entrada de Material por Compra", "/entrada-por-compra/ver/", 0),
        BENS_ESTOQUE_SAIDA("Bens de Estoque", "/contabil/bens-estoque/ver/", 1),
        BENS_ESTOQUE_ENTRADA("Bens de Estoque", "/contabil/bens-estoque/ver/", 1),
        LIQUIDACAO("Liquidação", "/liquidacao/ver/", 0);
        private String descricao;
        private String link;
        private Integer orderBy;

        TipoRelacionamento(String descricao, String link, Integer orderBy) {
            this.descricao = descricao;
            this.link = link;
            this.orderBy = orderBy;
        }

        public String getDescricao() {
            return descricao;
        }

        public String getLink() {
            return link;
        }

        public Integer getOrderBy() {
            return orderBy;
        }

        public boolean isSaidaMaterial() {
            return SAIDA_MATERIAL_CONSUMO.equals(this);
        }

        public boolean isEntradaMaterial() {
            return ENTRADA_MATERIAL_COMPRA.equals(this);
        }

        public boolean isRequisicaoMaterial() {
            return REQUISICAO_MATERIAL.equals(this);
        }

        public boolean isAprovacaoMaterial() {
            return APROVACAO_MATERIAL.equals(this);
        }

        public boolean isLiquidacao() {
            return LIQUIDACAO.equals(this);
        }

        public boolean isBensEstoqueSaida() {
            return BENS_ESTOQUE_SAIDA.equals(this);
        }

        public boolean isBensEstoqueEntrada() {
            return BENS_ESTOQUE_ENTRADA.equals(this);
        }
    }
}
