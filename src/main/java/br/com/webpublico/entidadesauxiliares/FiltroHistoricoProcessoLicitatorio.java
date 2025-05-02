package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoMovimentoProcessoLicitatorio;

public class FiltroHistoricoProcessoLicitatorio {

   private Long idMovimento;
   private TipoMovimentoProcessoLicitatorio tipoMovimento;

   private Long identificador;

    public FiltroHistoricoProcessoLicitatorio(Long idMovimento, TipoMovimentoProcessoLicitatorio tipoMovimento) {
        this.identificador = idMovimento;
        this.idMovimento = idMovimento;
        this.tipoMovimento = tipoMovimento;
    }

    public FiltroHistoricoProcessoLicitatorio(Long idMovimento, TipoMovimentoProcessoLicitatorio tipoMovimento, Long identificador) {
        this.idMovimento = idMovimento;
        this.tipoMovimento = tipoMovimento;
        this.identificador = identificador;
    }

    public Long getIdMovimento() {
        return idMovimento;
    }

    public void setIdMovimento(Long idMovimento) {
        this.idMovimento = idMovimento;
    }

    public TipoMovimentoProcessoLicitatorio getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(TipoMovimentoProcessoLicitatorio tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public Long getIdentificador() {
        return identificador;
    }

    public void setIdentificador(Long identificador) {
        this.identificador = identificador;
    }

    public String getCondicaoSql() {
        String sql = " and ";
        if (tipoMovimento.isDispensa()){
            sql += " disp.id = " + idMovimento;
        }
        if (tipoMovimento.isLicitacao() || tipoMovimento.isCredenciamento()){
            sql += " lic.id = " + idMovimento;
        }
        if (tipoMovimento.isSolicitacaoCompra()){
            sql += " sol.id = " + idMovimento;
        }
        if (tipoMovimento.isCotacao() || tipoMovimento.isContratoVigente()) {
            sql += " cot.id = " + idMovimento;
        }
        if (tipoMovimento.isFormularioCotacao()) {
            sql += " fc.id = " + idMovimento;
        }
        if (tipoMovimento.isIrp()) {
            sql += " irp.id = " + idMovimento;
        }
        if (tipoMovimento.isReservaSoliciacaoCampora()) {
            sql += " dot.id = " + idMovimento;
        }
        if (tipoMovimento.isAvaliacaoSoliciacaoCampora()) {
            sql += " ava.id = " + idMovimento;
        }
        if (tipoMovimento.isProcessoCompra()) {
            sql += " pc.id = " + idMovimento;
        }
        if (tipoMovimento.isAdesaoExterna()) {
            sql += " reg.id = " + idMovimento;
        }
        if (tipoMovimento.isAdesaoInterna() || tipoMovimento.isSolAdesaoExterna()) {
            sql += " solad.id = " + idMovimento;
        }
        if (tipoMovimento.isParecerLicitacao()) {
            sql += " pl.id = " + idMovimento;
        }
        if (tipoMovimento.isAtaRegistroPreco()) {
            sql += " ata.id = " + idMovimento;
        }
        if (tipoMovimento.isContrato()) {
            sql += " cont.id = " + idMovimento;
        }
        if (tipoMovimento.isPregao()) {
            sql += " pg.id = " + idMovimento;
        }
        if (tipoMovimento.isRecurso()) {
            sql += " rec.id = " + idMovimento;
        }
        if (tipoMovimento.isMapaComparativo()) {
            sql += " cert.id = " + idMovimento;
        }
        if (tipoMovimento.isMapaComparativoTecnicaPreco()) {
            sql += " matec.id = " + idMovimento;
        }
        if (tipoMovimento.isRepactuacaoPreco()) {
            sql += " repac.id = " + idMovimento;
        }
        if (tipoMovimento.isProximoVencedor()) {
            sql += " pvl.id = " + idMovimento;
        }
        return sql;
    }
}
