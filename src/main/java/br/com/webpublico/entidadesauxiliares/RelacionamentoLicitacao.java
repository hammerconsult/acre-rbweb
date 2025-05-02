package br.com.webpublico.entidadesauxiliares;

import java.util.Date;

public class RelacionamentoLicitacao {

    private Long id;
    private String movimento;
    private Date data;
    private String cor;
    private TipoRelacionamento tipo;

    public RelacionamentoLicitacao() {
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

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public TipoRelacionamento getTipo() {
        return tipo;
    }

    public void setTipo(TipoRelacionamento tipo) {
        this.tipo = tipo;
    }

    public boolean isPortal() {
        return TipoRelacionamento.PORTAL.equals(tipo);
    }

    public boolean isPropostaFornecedor() {
        return TipoRelacionamento.PROPOSTA_FORNECEDOR.equals(tipo);
    }

    public boolean isPropostaTecnicaFornecedor() {
        return TipoRelacionamento.PROPOSTA_TECNICA_FORNECEDOR.equals(tipo);
    }

    public boolean isPregaoPorItem() {
        return TipoRelacionamento.PREGAO_ITEM.equals(tipo);
    }

    public boolean isPregaoPorLote() {
        return TipoRelacionamento.PREGAO_LOTE.equals(tipo);
    }

    public boolean isAtaRegistroPrecoPortal() {
        return TipoRelacionamento.ATA_REGISTRO_PRECO_PORTAL.equals(tipo);
    }

    public boolean isAtaRegistroPreco() {
        return TipoRelacionamento.ATA_REGISTRO_PRECO.equals(tipo);
    }

    public boolean isMapaComparativo() {
        return TipoRelacionamento.MAPA_COMPARATIVO.equals(tipo);
    }

    public boolean isMapaComparativoTecnicaPreco() {
        return TipoRelacionamento.MAPA_COMPARATIVO_TECNICA_PRECO.equals(tipo);
    }

    public boolean isContrato() {
        return TipoRelacionamento.CONTRATO.equals(tipo);
    }

    public boolean isSolicitacaoAtaRegistroPreco() {
        return TipoRelacionamento.SOLICITACAO_ATA_REGISTRO_PRECO_EXTERNA.equals(tipo) || TipoRelacionamento.SOLICITACAO_ATA_REGISTRO_PRECO_INTERNA.equals(tipo);
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
        PORTAL("Portal", ""),
        ATA_REGISTRO_PRECO_PORTAL("Portal - Ata Registro de Preço", ""),
        PREGAO_ITEM("Pregão por Item", "/pregao/por-item/ver/"),
        PREGAO_LOTE("Pregão por Lote", "/pregao/por-lote/ver/"),
        PROPOSTA_FORNECEDOR("Proposta do Fornecedor", "/licitacao/proposta-fornecedor/ver/"),
        PROPOSTA_TECNICA_FORNECEDOR("Proposta Técnica do Fornecedor", "/licitacao/proposta-tecnica/ver/"),
        ATA_REGISTRO_PRECO("Ata Registro de Preço", "/ata-registro-preco/ver/"),
        SOLICITACAO_ATA_REGISTRO_PRECO_INTERNA("Solicitação a Ata Registro de Preço - Interna", "/solicitacao-adesao-ata-registro-preco-interna/ver/"),
        SOLICITACAO_ATA_REGISTRO_PRECO_EXTERNA("Solicitação a Ata Registro de Preço - Externa", "/solicitacao-adesao-ata-registro-preco-externa/ver/"),
        CONTRATO("Contrato", "/contrato-adm/ver/"),
        MAPA_COMPARATIVO("Mapa Comparativo - Preço", "/mapa-comparativo/ver/"),
        MAPA_COMPARATIVO_TECNICA_PRECO("Mapa Comparativo Técnica e Preço", "/mapa-comparativo-tecnica-preco/ver/");
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
