package br.com.webpublico.entidadesauxiliares.contabil;

import br.com.webpublico.entidades.Aquisicao;
import br.com.webpublico.entidades.EntradaMaterial;
import br.com.webpublico.entidades.Liquidacao;
import br.com.webpublico.entidades.LiquidacaoEstorno;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.SituacaoEntradaMaterialOuAquisicao;

public class OrigemDoctoFiscalLiquidacaoVO {
    private Long idOrigem;
    private String origem;
    private SituacaoEntradaMaterialOuAquisicao situacao;
    private String descricao;
    private CategoriaOrcamentaria categoriaOrcamentaria;
    private String link;
    private String origemFormatada;

    public OrigemDoctoFiscalLiquidacaoVO() {
    }

    public Long getIdOrigem() {
        return idOrigem;
    }

    public void setIdOrigem(Long idOrigem) {
        this.idOrigem = idOrigem;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public SituacaoEntradaMaterialOuAquisicao getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoEntradaMaterialOuAquisicao situacao) {
        this.situacao = situacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public CategoriaOrcamentaria getCategoriaOrcamentaria() {
        return categoriaOrcamentaria;
    }

    public void setCategoriaOrcamentaria(CategoriaOrcamentaria categoriaOrcamentaria) {
        this.categoriaOrcamentaria = categoriaOrcamentaria;
    }

    public String getLink() {
        if (link == null) atualizarLink();
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getOrigemFormatada() {
        if (origemFormatada == null) atualizarOrigemFormatada();
        return origemFormatada;
    }

    public void setOrigemFormatada(String origemFormatada) {
        this.origemFormatada = origemFormatada;
    }

    public void atualizarOrigemFormatada() {
        if (LiquidacaoEstorno.class.getSimpleName().equals(this.origem)) {
            origemFormatada = "Estorno de Liquidação";
        }

        if (Liquidacao.class.getSimpleName().equals(this.origem)) {
            origemFormatada = "Liquidação";
        }

        if (EntradaMaterial.class.getSimpleName().equals(this.origem)) {
            origemFormatada = "Entrada de Material por Compra";
        }

        if (Aquisicao.class.getSimpleName().equals(this.origem)) {
            origemFormatada = "Efetivação de Aquisição de Bem Móvel";
        }
    }

    public void atualizarLink() {
        String caminhoPadrao = "";
        if (LiquidacaoEstorno.class.getSimpleName().equals(this.origem)) {
            if (categoriaOrcamentaria.isResto()) {
                caminhoPadrao = "/liquidacao-estorno/resto-a-pagar/";
            } else {
                caminhoPadrao = "/liquidacao-estorno/";
            }
        }

        if (Liquidacao.class.getSimpleName().equals(this.origem)) {
            if (categoriaOrcamentaria.isResto()) {
                caminhoPadrao = "/liquidacao/resto-a-pagar/";
            } else {
                caminhoPadrao = "/liquidacao/";
            }
        }

        if (EntradaMaterial.class.getSimpleName().equals(this.origem)) {
            caminhoPadrao = "/entrada-por-compra/";
        }

        if (Aquisicao.class.getSimpleName().equals(this.origem)) {
            caminhoPadrao = "/efetivacao-aquisicao-bem-movel/";
        }
        link = caminhoPadrao + "ver/" + idOrigem + "/";
    }
}
