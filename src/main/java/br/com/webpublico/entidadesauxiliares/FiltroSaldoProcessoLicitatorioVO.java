package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.TipoAvaliacaoLicitacao;
import br.com.webpublico.enums.TipoExecucaoProcesso;

public class FiltroSaldoProcessoLicitatorioVO {

    private TipoExecucaoProcesso tipoProcesso;
    private Long idProcesso;
    private Long idAta;
    private Boolean processoIrp;
    private Boolean recuperarOrigem;
    private Pessoa fornecedor;
    private TipoAvaliacaoLicitacao tipoAvaliacaoLicitacao;
    private UnidadeOrganizacional unidadeOrganizacional;

    public FiltroSaldoProcessoLicitatorioVO(TipoExecucaoProcesso tipoProcesso) {
        this.processoIrp = false;
        this.recuperarOrigem = false;
        this.tipoProcesso = tipoProcesso;
    }

    public TipoExecucaoProcesso getTipoProcesso() {
        return tipoProcesso;
    }

    public void setTipoProcesso(TipoExecucaoProcesso tipoProcesso) {
        this.tipoProcesso = tipoProcesso;
    }

    public Long getIdProcesso() {
        return idProcesso;
    }

    public void setIdProcesso(Long idProcesso) {
        this.idProcesso = idProcesso;
    }

    public Long getIdAta() {
        return idAta;
    }

    public void setIdAta(Long idAta) {
        this.idAta = idAta;
    }

    public Boolean getProcessoIrp() {
        return processoIrp;
    }

    public void setProcessoIrp(Boolean processoIrp) {
        this.processoIrp = processoIrp;
    }

    public Boolean getRecuperarOrigem() {
        return recuperarOrigem;
    }

    public void setRecuperarOrigem(Boolean recuperarOrigem) {
        this.recuperarOrigem = recuperarOrigem;
    }

    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public TipoAvaliacaoLicitacao getTipoAvaliacaoLicitacao() {
        return tipoAvaliacaoLicitacao;
    }

    public void setTipoAvaliacaoLicitacao(TipoAvaliacaoLicitacao tipoAvaliacaoLicitacao) {
        this.tipoAvaliacaoLicitacao = tipoAvaliacaoLicitacao;
    }
}
