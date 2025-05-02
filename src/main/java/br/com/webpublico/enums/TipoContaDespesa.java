/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.TipoContaDespesaDTO;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Renato
 */
public enum TipoContaDespesa {

    NAO_APLICAVEL("Não Aplicável", "00", TipoContaDespesaDTO.NAO_APLICAVEL),
    VARIACAO_PATRIMONIAL_DIMINUTIVA("Variação Patrimonial Diminutiva", "11", TipoContaDespesaDTO.VARIACAO_PATRIMONIAL_DIMINUTIVA),
    PESSOAL_ENCARGOS("Pessoal e Encargos", "20", TipoContaDespesaDTO.PESSOAL_ENCARGOS),
    DIARIA_CIVIL("Diária Civil", "31", TipoContaDespesaDTO.DIARIA_CIVIL),
    DIARIA_CAMPO("Diária de Campo", "35", TipoContaDespesaDTO.DIARIA_CAMPO),
    COLABORADOR_EVENTUAL("Diária a Colaborador Eventual", "37", TipoContaDespesaDTO.COLABORADOR_EVENTUAL),
    SUPRIMENTO_FUNDO("Suprimento de Fundos", "39", TipoContaDespesaDTO.SUPRIMENTO_FUNDO),
    SERVICO_DE_TERCEIRO("Serviço de terceiro", "41", TipoContaDespesaDTO.SERVICO_DE_TERCEIRO),
    CONVENIO_DESPESA("Convênio de Despesa", "55", TipoContaDespesaDTO.CONVENIO_DESPESA),
    EMPRESTIMOS_FINANCIAMENTOS("Empréstimos e Financiamentos", "61", TipoContaDespesaDTO.EMPRESTIMOS_FINANCIAMENTOS),
    BEM_ESTOQUE("Bem de Estoque", "71", TipoContaDespesaDTO.BEM_ESTOQUE),
    BEM_MOVEL("Bem Móvel", "73", TipoContaDespesaDTO.BEM_MOVEL),
    BEM_IMOVEL("Bem Imóvel", "75", TipoContaDespesaDTO.BEM_IMOVEL),
    BEM_INTANGIVEL("Bem Intangível", "77", TipoContaDespesaDTO.BEM_INTANGIVEL),
    DIVIDA_PUBLICA("Dívida Pública", "80", TipoContaDespesaDTO.DIVIDA_PUBLICA),
    PRECATORIO("Precatórios", "85", TipoContaDespesaDTO.PRECATORIO);

    private String descricao;
    private String codigo;
    private TipoContaDespesaDTO toDto;

    TipoContaDespesa(String descricao) {
        this.descricao = descricao;
    }

    TipoContaDespesa(String descricao, String codigo, TipoContaDespesaDTO toDto) {
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

    public TipoContaDespesaDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }

    public static List<String> recuperarListaTipoContaName(List<TipoContaDespesa> tipos) {
        List<String> retorno = new ArrayList<>();
        for (TipoContaDespesa or : tipos) {
            retorno.add(or.name());
        }
        return retorno;
    }

    public static List<TipoContaDespesa> retornaTipoContaObrigacaoPagar() {
        List<TipoContaDespesa> toReturn = Lists.newArrayList();
        toReturn.add(TipoContaDespesa.VARIACAO_PATRIMONIAL_DIMINUTIVA);
        toReturn.add(TipoContaDespesa.PESSOAL_ENCARGOS);
        toReturn.add(TipoContaDespesa.SERVICO_DE_TERCEIRO);
        return toReturn;
    }

    public static List<TipoContaDespesa> retornaTipoContaGrupoBem() {
        List<TipoContaDespesa> toReturn = Lists.newArrayList();
        toReturn.add(TipoContaDespesa.BEM_ESTOQUE);
        toReturn.add(TipoContaDespesa.BEM_MOVEL);
        toReturn.add(TipoContaDespesa.BEM_INTANGIVEL);
        return toReturn;
    }

    public static List<TipoContaDespesa> retornaTipoContaConcessaoDiaria() {
        List<TipoContaDespesa> toReturn = Lists.newArrayList();
        toReturn.add(TipoContaDespesa.DIARIA_CAMPO);
        toReturn.add(TipoContaDespesa.DIARIA_CIVIL);
        toReturn.add(TipoContaDespesa.SUPRIMENTO_FUNDO);
        return toReturn;
    }

    public static List<TipoContaDespesa> retornaTipoContaDividaPublica() {
        List<TipoContaDespesa> toReturn = Lists.newArrayList();
        toReturn.add(TipoContaDespesa.PESSOAL_ENCARGOS);
        toReturn.add(TipoContaDespesa.DIVIDA_PUBLICA);
        toReturn.add(TipoContaDespesa.PRECATORIO);
        return toReturn;
    }

    public static TipoObjetoCompra getTipoObjetoCompra(TipoContaDespesa tipoContaDespesa) {
        switch (tipoContaDespesa) {
            case BEM_ESTOQUE:
                return TipoObjetoCompra.CONSUMO;
            case BEM_MOVEL:
                return TipoObjetoCompra.PERMANENTE_MOVEL;
            case BEM_IMOVEL:
                return TipoObjetoCompra.PERMANENTE_IMOVEL;
            default:
                return TipoObjetoCompra.SERVICO;
        }
    }

    public static TipoContaDespesa getContaDespesaPorTipoObjetoCompra(TipoObjetoCompra tipoObjetoCompra) {
        switch (tipoObjetoCompra) {
            case CONSUMO:
                return TipoContaDespesa.BEM_ESTOQUE;
            case PERMANENTE_MOVEL:
                return TipoContaDespesa.BEM_MOVEL;
            case PERMANENTE_IMOVEL:
                return TipoContaDespesa.BEM_IMOVEL;
            case SERVICO:
                return TipoContaDespesa.SERVICO_DE_TERCEIRO;
            default:
                return TipoContaDespesa.VARIACAO_PATRIMONIAL_DIMINUTIVA;
        }
    }

    public static List<TipoContaDespesa> getTiposContaDespesaPorTipoObjetoCompra(TipoObjetoCompra tipoObjetoCompra) {
        List<TipoContaDespesa> tipoContaDespesaList = Lists.newArrayList();
        switch (tipoObjetoCompra) {
            case CONSUMO:
                tipoContaDespesaList.add(TipoContaDespesa.BEM_ESTOQUE);
                tipoContaDespesaList.add(TipoContaDespesa.VARIACAO_PATRIMONIAL_DIMINUTIVA);
                break;
            case PERMANENTE_MOVEL:
                tipoContaDespesaList.add(TipoContaDespesa.BEM_MOVEL);
                break;
            case PERMANENTE_IMOVEL:
                tipoContaDespesaList.add(TipoContaDespesa.BEM_IMOVEL);
                break;
            case SERVICO:
                tipoContaDespesaList.add(TipoContaDespesa.SERVICO_DE_TERCEIRO);
                break;
        }
        return tipoContaDespesaList;
    }

    public boolean isDividaPublica() {
        return TipoContaDespesa.DIVIDA_PUBLICA.equals(this);
    }

    public boolean isBemImovel() {
        return TipoContaDespesa.BEM_IMOVEL.equals(this);
    }

    public boolean isEstoque() {
        return TipoContaDespesa.BEM_ESTOQUE.equals(this);
    }

    public boolean isBemMovel() {
        return TipoContaDespesa.BEM_MOVEL.equals(this);
    }

    public boolean isBemIntangivel() {
        return TipoContaDespesa.BEM_INTANGIVEL.equals(this);
    }

    public boolean isPrecatorio() {
        return TipoContaDespesa.PRECATORIO.equals(this);
    }

    public boolean isConvenioDespesa() {
        return TipoContaDespesa.CONVENIO_DESPESA.equals(this);
    }

    public boolean isSuprimentoFundo() {
        return TipoContaDespesa.SUPRIMENTO_FUNDO.equals(this);
    }

    public boolean isDiariaCampo() {
        return TipoContaDespesa.DIARIA_CAMPO.equals(this);
    }

    public boolean isDiariaCivil() {
        return TipoContaDespesa.DIARIA_CIVIL.equals(this);
    }

    public boolean isPessoaEncargos() {
        return TipoContaDespesa.PESSOAL_ENCARGOS.equals(this);
    }

    public boolean isNaoAplicavel() {
        return TipoContaDespesa.NAO_APLICAVEL.equals(this);
    }

    public boolean isServicoTerceiro() {
        return TipoContaDespesa.SERVICO_DE_TERCEIRO.equals(this);
    }

    public boolean isPropostaConcessaoDiaria() {
        return isDiariaCivil() || isDiariaCampo() || isSuprimentoFundo();
    }
}
