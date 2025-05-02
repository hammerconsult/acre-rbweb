package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.comum.TipoMascaraUnidadeMedidaDTO;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

/**
 * Created by Desenvolvimento on 26/01/2017.
 */
public enum TipoMascaraUnidadeMedida implements EnumComDescricao {

    ZERO_CASA_DECIMAL("←#.###","#,##0", "(zero casas decimais após a virgula, com separador de milhar)",0, TipoMascaraUnidadeMedidaDTO.ZERO_CASA_DECIMAL),
    UMA_CASA_DECIMAL("←#.###,#","#,##0.0","(até 1 casa decimal após a virgula, com separador de milhar)", 1, TipoMascaraUnidadeMedidaDTO.UMA_CASA_DECIMAL),
    DUAS_CASAS_DECIMAL("←#.###,##","#,##0.00","(até 2 casas decimais após a virgula, com separador de milhar)",2, TipoMascaraUnidadeMedidaDTO.DUAS_CASAS_DECIMAL),
    TRES_CASAS_DECIMAL("←#.###,###","#,##0.000","(até 3 casas decimais após a virgula, com separador de milhar)",3, TipoMascaraUnidadeMedidaDTO.TRES_CASAS_DECIMAL),
    QUATRO_CASAS_DECIMAL("←#.###,####", "#,##0.0000","(até 4 casas decimais após a virgula, com separador de milhar)", 4, TipoMascaraUnidadeMedidaDTO.QUATRO_CASAS_DECIMAL);

    String exemplo;
    String mascara;
    String descricaoLonga;
    Integer quantidadeCasasDecimais;
    TipoMascaraUnidadeMedidaDTO toDto;

    TipoMascaraUnidadeMedida(String exemplo, String mascara, String descricaoLonga, Integer quantidadeCasasDecimais, TipoMascaraUnidadeMedidaDTO toDto) {
        this.exemplo = exemplo;
        this.mascara = mascara;
        this.descricaoLonga = descricaoLonga;
        this.quantidadeCasasDecimais = quantidadeCasasDecimais;
        this.toDto = toDto;
    }

    public String getExemplo() {
        return exemplo;
    }

    public String getMascara() {
        return mascara;
    }

    public String getDescricaoLonga() {
        return descricaoLonga;
    }

    public Integer getQuantidadeCasasDecimais() {
        return quantidadeCasasDecimais;
    }

    public static List<TipoMascaraUnidadeMedida> getMascarasValorUnitario() {
        List<TipoMascaraUnidadeMedida> list = Lists.newArrayList();
        list.add(TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL);
        list.add(TipoMascaraUnidadeMedida.TRES_CASAS_DECIMAL);
        list.add(TipoMascaraUnidadeMedida.QUATRO_CASAS_DECIMAL);
        return list;
    }

    public String getExemploDescricaoLonga() {
        return exemplo + " - " + descricaoLonga;
    }

    public String aplicarMascaraUnidadeMedida(BigDecimal valor) {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');

        DecimalFormat df = new DecimalFormat(this.mascara, symbols);
        return df.format(valor);
    }

    @Override
    public String toString() {
        return this.exemplo;
    }

    @Override
    public String getDescricao() {
        return exemplo;
    }
}
