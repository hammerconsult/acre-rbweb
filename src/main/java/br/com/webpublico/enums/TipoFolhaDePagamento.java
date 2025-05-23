/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.entidades.FolhaDePagamento;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.FolhaCalculavel;
import br.com.webpublico.webreportdto.dto.rh.TipoFolhaDePagamentoDTO;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * @author andre
 */
@GrupoDiagrama(nome = "RecursosHumanos")
public enum TipoFolhaDePagamento {
    NORMAL("Normal", 1, TipoFolhaDePagamentoDTO.NORMAL),
    COMPLEMENTAR("Complementar", 2, TipoFolhaDePagamentoDTO.COMPLEMENTAR),
    FERIAS("Férias", 3, TipoFolhaDePagamentoDTO.FERIAS),
    SALARIO_13("13° Salário", 4, TipoFolhaDePagamentoDTO.SALARIO_13),
    RESCISAO("Rescisão", 5, TipoFolhaDePagamentoDTO.RESCISAO),
    ADIANTAMENTO_NORMAL("Adiantamento normal", 6, TipoFolhaDePagamentoDTO.ADIANTAMENTO_NORMAL),
    ADIANTAMENTO_FERIAS("Adiantamento férias", 7, TipoFolhaDePagamentoDTO.ADIANTAMENTO_FERIAS),
    ADIANTAMENTO_13_SALARIO("Adiantamento 13° Salário", 8, TipoFolhaDePagamentoDTO.ADIANTAMENTO_13_SALARIO),
    RESCISAO_COMPLEMENTAR("Rescisão complementar", 9, TipoFolhaDePagamentoDTO.RESCISAO_COMPLEMENTAR),
    MANUAL("Manual", 10, TipoFolhaDePagamentoDTO.MANUAL);
    private String descricao;
    private Integer codigo;
    private TipoFolhaDePagamentoDTO toDto;

    public static TipoFolhaDePagamento getTipoFolhaPorCodigo(int mes) {
        switch (mes) {
            case 1:
                return TipoFolhaDePagamento.NORMAL;
            case 2:
                return TipoFolhaDePagamento.COMPLEMENTAR;
            case 3:
                return TipoFolhaDePagamento.FERIAS;
            case 4:
                return TipoFolhaDePagamento.SALARIO_13;
            case 5:
                return TipoFolhaDePagamento.RESCISAO;
            case 6:
                return TipoFolhaDePagamento.ADIANTAMENTO_NORMAL;
            case 7:
                return TipoFolhaDePagamento.ADIANTAMENTO_FERIAS;
            case 8:
                return TipoFolhaDePagamento.ADIANTAMENTO_13_SALARIO;
            case 9:
                return TipoFolhaDePagamento.RESCISAO_COMPLEMENTAR;
            case 10:
                return TipoFolhaDePagamento.MANUAL;
            default:
                return null;
        }
    }

    TipoFolhaDePagamento(String descricao, Integer codigo, TipoFolhaDePagamentoDTO toDto) {
        this.descricao = descricao;
        this.codigo = codigo;
        this.toDto = toDto;
    }

    public static List<TipoFolhaDePagamento> getFolhasPorPrioridadeDeUso() {
        List<TipoFolhaDePagamento> retorno = new ArrayList<>();
        retorno.add(TipoFolhaDePagamento.NORMAL);
        retorno.add(TipoFolhaDePagamento.RESCISAO);
        retorno.add(TipoFolhaDePagamento.COMPLEMENTAR);
        retorno.add(TipoFolhaDePagamento.FERIAS);
        retorno.add(TipoFolhaDePagamento.SALARIO_13);
        retorno.add(TipoFolhaDePagamento.ADIANTAMENTO_NORMAL);
        retorno.add(TipoFolhaDePagamento.ADIANTAMENTO_FERIAS);
        retorno.add(TipoFolhaDePagamento.ADIANTAMENTO_13_SALARIO);
        retorno.add(TipoFolhaDePagamento.RESCISAO_COMPLEMENTAR);
        retorno.add(TipoFolhaDePagamento.MANUAL);
        return retorno;
    }

    public static List<TipoFolhaDePagamento> getFolhasPorPrioridadeDeUsoSemDecimoTerceiro() {
        List<TipoFolhaDePagamento> retorno = new ArrayList<>();
        retorno.add(TipoFolhaDePagamento.NORMAL);
        retorno.add(TipoFolhaDePagamento.RESCISAO);
        retorno.add(TipoFolhaDePagamento.COMPLEMENTAR);
        retorno.add(TipoFolhaDePagamento.FERIAS);
        retorno.add(TipoFolhaDePagamento.ADIANTAMENTO_NORMAL);
        retorno.add(TipoFolhaDePagamento.ADIANTAMENTO_FERIAS);
        retorno.add(TipoFolhaDePagamento.RESCISAO_COMPLEMENTAR);
        retorno.add(TipoFolhaDePagamento.ADIANTAMENTO_13_SALARIO);
        retorno.add(TipoFolhaDePagamento.MANUAL);
        return retorno;
    }

    public static List<TipoFolhaDePagamento> getFolhasDePagamentoDecimoTerceiro() {
        List<TipoFolhaDePagamento> retorno = new ArrayList<>();
        retorno.add(TipoFolhaDePagamento.ADIANTAMENTO_13_SALARIO);
        retorno.add(TipoFolhaDePagamento.SALARIO_13);
        return retorno;
    }

    public static List<TipoFolhaDePagamento> getFolhasDePagamentoRescisaoDecimoTerceiro() {
        List<TipoFolhaDePagamento> retorno = new ArrayList<>();
        retorno.add(TipoFolhaDePagamento.ADIANTAMENTO_13_SALARIO);
        retorno.add(TipoFolhaDePagamento.SALARIO_13);
        retorno.add(TipoFolhaDePagamento.RESCISAO_COMPLEMENTAR);
        retorno.add(TipoFolhaDePagamento.RESCISAO);
        return retorno;
    }

    public static List<TipoFolhaDePagamento> getTiposFolhaDePagamentoParaRescisao() {
        List<TipoFolhaDePagamento> retorno = new ArrayList<>();
        retorno.add(TipoFolhaDePagamento.ADIANTAMENTO_13_SALARIO);
        retorno.add(TipoFolhaDePagamento.SALARIO_13);
        retorno.add(TipoFolhaDePagamento.NORMAL);
        retorno.add(TipoFolhaDePagamento.COMPLEMENTAR);
        return retorno;
    }

    public static List<TipoFolhaDePagamento> getTiposFolhaDePagamentoDeRescisao() {
        List<TipoFolhaDePagamento> retorno = new ArrayList<>();
        retorno.add(TipoFolhaDePagamento.RESCISAO);
        retorno.add(TipoFolhaDePagamento.RESCISAO_COMPLEMENTAR);
        return retorno;
    }

    public static boolean isFolhaRescisao(FolhaDePagamento folhaDePagamento) {
        return isFolhaRescisao(folhaDePagamento.getTipoFolhaDePagamento());
    }

    public static boolean isFolhaRescisao(FolhaCalculavel folhaDePagamento) {
        return isFolhaRescisao(folhaDePagamento.getTipoFolhaDePagamento());
    }

    public static boolean isFolhaRescisao(TipoFolhaDePagamento tipoFolha) {
        return tipoFolha.equals(TipoFolhaDePagamento.RESCISAO) || tipoFolha.equals(TipoFolhaDePagamento.RESCISAO_COMPLEMENTAR);
    }

    public static boolean isFolhaComplementar(TipoFolhaDePagamento tipoFolha) {
        return tipoFolha.equals(TipoFolhaDePagamento.COMPLEMENTAR);
    }

    public static List<TipoFolhaDePagamento> getFolhasDePagamentoSemDecimoTerceiro() {
        List<TipoFolhaDePagamento> retorno = new ArrayList<>();
        retorno.add(TipoFolhaDePagamento.NORMAL);
        retorno.add(TipoFolhaDePagamento.COMPLEMENTAR);
        retorno.add(TipoFolhaDePagamento.FERIAS);
        retorno.add(TipoFolhaDePagamento.ADIANTAMENTO_NORMAL);
        retorno.add(TipoFolhaDePagamento.ADIANTAMENTO_FERIAS);
        retorno.add(TipoFolhaDePagamento.MANUAL);
        retorno.add(TipoFolhaDePagamento.RESCISAO_COMPLEMENTAR);
        retorno.add(TipoFolhaDePagamento.RESCISAO);
        return retorno;
    }

    public static List<TipoFolhaDePagamento> getFolhasParaArquivoRAISRemuneracaoMes() {
        List<TipoFolhaDePagamento> retorno = new ArrayList<>();
        retorno.add(TipoFolhaDePagamento.NORMAL);
        retorno.add(TipoFolhaDePagamento.COMPLEMENTAR);
        retorno.add(TipoFolhaDePagamento.RESCISAO);
        retorno.add(TipoFolhaDePagamento.RESCISAO_COMPLEMENTAR);
        return retorno;
    }

    public static TipoFolhaDePagamento[] transformListToArray(List<TipoFolhaDePagamento> tipos) {
        TipoFolhaDePagamento[] array = new TipoFolhaDePagamento[tipos.size()];
        array = TipoFolhaDePagamento.getFolhasDePagamentoSemDecimoTerceiro().toArray(array);
        return array;
    }

    public static boolean isFolhaAdiantamento13Salario(FolhaDePagamento folhaDePagamento) {
        return isFolhaAdiantamento13Salario(folhaDePagamento.getTipoFolhaDePagamento());
    }

    public static boolean isFolhaAdiantamento13Salario(FolhaCalculavel folhaDePagamento) {
        return isFolhaAdiantamento13Salario(folhaDePagamento.getTipoFolhaDePagamento());
    }

    public static boolean isFolha13Salario(FolhaCalculavel folhaDePagamento) {
        return isFolha13Salario(folhaDePagamento.getTipoFolhaDePagamento());
    }

    public static boolean isFolhaAdiantamento13Salario(TipoFolhaDePagamento tipo) {
        return ADIANTAMENTO_13_SALARIO.equals(tipo);
    }

    public static boolean isFolha13Salario(TipoFolhaDePagamento tipo) {
        return SALARIO_13.equals(tipo);
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public TipoFolhaDePagamentoDTO getToDto() {
        return toDto;
    }

    public static List<TipoFolhaDePagamento> convertStringToTipoFolha(String[] tiposFolha) {
        if (tiposFolha == null) {
            return null;
        }
        List<TipoFolhaDePagamento> tipos = Lists.newArrayList();
        for (String s : tiposFolha) {
            TipoFolhaDePagamento tipoPorName = getTipoPorName(s);
            if (tipoPorName != null)
                tipos.add(tipoPorName);
        }
        return tipos;
    }


    @Override
    public String toString() {
        return descricao;
    }

    public static TipoFolhaDePagamento getTipoPorName(String descricao) {
        if (descricao == null) {
            return null;
        }
        for (TipoFolhaDePagamento tipoFolhaDePagamento : TipoFolhaDePagamento.values()) {
            if (descricao.equals(tipoFolhaDePagamento.name())) {
                return tipoFolhaDePagamento;
            }
        }
        return null;
    }

}
