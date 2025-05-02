/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums.rh;

import br.com.webpublico.geradores.GrupoDiagrama;

/**
 * @author andre
 */
@GrupoDiagrama(nome = "RecursosHumanos")
public enum TipoAvisoPrevio {
    AVISO_PREVIO_REDUCAO_DUAS_HORAS_DIARIAS("Aviso prévio trabalhado dado pelo empregador ao empregado, que optou pela redução de duas horas diárias [caput do art. 488 da CLT]", 1),
    AVISO_PREVIO_REDUCAO_DIAS_CORRIDOS("Aviso prévio trabalhado dado pelo empregador ao empregado, que optou pela redução de dias corridos [parágrafo único do art. 488 da CLT]", 2),
    AVISO_PREVIO_PEDIDO_DEMISSAO_SOB_PENA_DESCONTO("Aviso prévio dado pelo empregado (pedido de demissão), não dispensado de seu cumprimento, sob pena de desconto, pelo empregador, dos salários correspondentes ao prazo respectivo (§2º do art. 487 da CLT)", 4),
    AVISO_PREVIO_EMPREGADOR_RURAL_REDUCAO_UM_DIA_SEMANA("Aviso prévio trabalhado dado pelo empregador rural ao empregado, com redução de um dia por semana ( art. 15 da Lei nº 5889/73)", 5),
    AVISO_PREVIO_ACORDO_EMPREGADO_E_EMPREGADOR("Aviso prévio trabalhado decorrente de acordo entre empregado e empregador (art. 484-A, caput, da CLT)", 6);

    private String descricao;
    private Integer codigoESocial;

    TipoAvisoPrevio(String descricao, Integer codigoESocial) {
        this.descricao = descricao;
        this.codigoESocial = codigoESocial;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getCodigoESocial() {
        return codigoESocial;
    }

    public void setCodigoESocial(Integer codigoESocial) {
        this.codigoESocial = codigoESocial;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
