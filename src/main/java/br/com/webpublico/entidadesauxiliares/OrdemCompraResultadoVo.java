package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.webreportdto.dto.administrativo.SituacaoEntradaMaterialDTO;

import java.math.BigDecimal;
import java.util.Date;

public class OrdemCompraResultadoVo {

    private Long id;
    private String numero;
    private Date dataLancamento;
    private String motivo;
    private BigDecimal valor;
    private String link;
    private SituacaoAtesto situacaoAtesto;

    public SituacaoAtesto getSituacaoAtesto() {
        return situacaoAtesto;
    }

    public void setSituacaoAtesto(SituacaoAtesto situacaoAtesto) {
        this.situacaoAtesto = situacaoAtesto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public enum SituacaoAtesto {
        ATESTO_PROVISORIO_AGUARDANDO_LIQUIDACAO("Atesto Provisório - Aguardando Liquidação"),
        ATESTO_PROVISORIO_COM_PENDENCIA("Atesto Provisório - Com Pendência"),
        ATESTO_DEFINITIVO_LIQUIDADO("Atesto Definitivo - Liquidado"),
        ATESTO_DEFINITIVO_ESTORNADO("Atesto Definitivo - Estornado"),
        EM_ELABORACAO("Em Elaboração"),
        AGUARDANDO_EFETIVACAO("Aguardando Efetivação"),
        FINALIZADO("Finalizado"),
        ESTORNADO("Estornado"),
        CONCLUIDA("Concluída"),
        RECUSADO("Recusado");
        private String descricao;

        SituacaoAtesto(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
