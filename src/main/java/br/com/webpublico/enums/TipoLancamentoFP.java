/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.rh.TipoLancamentoFPDTO;

/**
 * @author magneto
 */
public enum TipoLancamentoFP {

    VALOR("Valor em Reais", "V", TipoLancamentoFPDTO.VALOR),
    REFERENCIA("Percentual", "P", TipoLancamentoFPDTO.REFERENCIA),//p de percentual
    QUANTIDADE("Quantidade","Q", TipoLancamentoFPDTO.QUANTIDADE) ,
    HORAS("Horas","H", TipoLancamentoFPDTO.HORAS),
    DIAS("Dias","D", TipoLancamentoFPDTO.DIAS);

//    V VALOR
//    H HORAS
//    P PERCENTUAL
//    Q QUANTIDADE
//    D DIAS

    private String descricao;
    private String codigo;
    private TipoLancamentoFPDTO toDto;

    TipoLancamentoFP(String descricao, String codigo, TipoLancamentoFPDTO toDto) {
        this.descricao = descricao;
        this.codigo = codigo;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public TipoLancamentoFPDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
    public static boolean isReferencia(TipoLancamentoFP tipo){
       if(tipo.equals(REFERENCIA) || tipo.equals(HORAS) || tipo.equals(QUANTIDADE) || tipo.equals(DIAS)) return true; else return false;
    }

    public boolean isValorEmReais(){
        return this.equals(VALOR);
    }

    public boolean isValorEmPercentual(){
        return this.equals(REFERENCIA);
    }

    public static TipoLancamentoFP find(String codigo) {
        for (TipoLancamentoFP tipo : TipoLancamentoFP.values()) {
            if (tipo.codigo.equals(codigo)) {
                return tipo;
            }
        }
        throw new RuntimeException("Tipo não encontrado para o código:" + codigo);
    }
}
