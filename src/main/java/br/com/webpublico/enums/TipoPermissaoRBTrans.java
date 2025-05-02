/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EnumComDescricao;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Felipe Marinzeck
 */
@GrupoDiagrama(nome = "RBTrans")
public enum TipoPermissaoRBTrans implements EnumComDescricao {

    TAXI("Táxi", TipoCredencialRBTrans.TRAFEGO),
    MOTO_TAXI("Moto-Táxi", TipoCredencialRBTrans.TRAFEGO),
    FRETE("Frete", TipoCredencialRBTrans.TRANSPORTE),
    NAO_ESPECIFICADO("Permissão Transporte não especificada", TipoCredencialRBTrans.TRAFEGO);
    private final String descricao;
    private final TipoCredencialRBTrans tipoCredencialRBTrans;

    private TipoPermissaoRBTrans(String descricao, TipoCredencialRBTrans tipoCredencialRBTrans) {
        this.descricao = descricao;
        this.tipoCredencialRBTrans = tipoCredencialRBTrans;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoCredencialRBTrans getTipoCredencialRBTrans() {
        return tipoCredencialRBTrans;
    }

    public static List<TipoPermissaoRBTrans> tiposDefinidos() {
        return Arrays.stream(TipoPermissaoRBTrans.values()).filter(tp -> !TipoPermissaoRBTrans.NAO_ESPECIFICADO.equals(tp)).collect(Collectors.toList());
    }
}

