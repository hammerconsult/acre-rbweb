package br.com.webpublico.entidadesauxiliares.rh.relatorio;

import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.webreportdto.dto.rh.AssistenteRelatorioFichaFinanceiraDTO;

import java.util.Date;

public class AssistenteRelatorioFichaFinanceira {

    private Long idFicha;
    private Long idVinculo;
    private String condicoes;
    private OperacaoFormula operacaoFormula;
    private Date dataOperacao;

    public AssistenteRelatorioFichaFinanceira() {
    }

    public AssistenteRelatorioFichaFinanceira(Long idFicha, Long idVinculo, String condicoes, OperacaoFormula operacaoFormula, Date dataOperacao) {
        this.idFicha = idFicha;
        this.idVinculo = idVinculo;
        this.condicoes = condicoes;
        this.operacaoFormula = operacaoFormula;
        this.dataOperacao = dataOperacao;
    }

    public static AssistenteRelatorioFichaFinanceiraDTO assistenteFichaToDto(AssistenteRelatorioFichaFinanceira assistenteRelatorioFichaFinanceira) {
        AssistenteRelatorioFichaFinanceiraDTO assistenteDto = new AssistenteRelatorioFichaFinanceiraDTO();
        assistenteDto.setIdFicha(assistenteRelatorioFichaFinanceira.getIdFicha());
        assistenteDto.setIdVinculo(assistenteRelatorioFichaFinanceira.getIdVinculo());
        assistenteDto.setCondicoes(assistenteRelatorioFichaFinanceira.getCondicoes());
        assistenteDto.setOperacaoFormulaDTO(assistenteRelatorioFichaFinanceira.getOperacaoFormula().getToDto());
        assistenteDto.setDataOperacao(assistenteRelatorioFichaFinanceira.getDataOperacao());
        return assistenteDto;
    }

    public Long getIdFicha() {
        return idFicha;
    }

    public void setIdFicha(Long idFicha) {
        this.idFicha = idFicha;
    }

    public Long getIdVinculo() {
        return idVinculo;
    }

    public void setIdVinculo(Long idVinculo) {
        this.idVinculo = idVinculo;
    }

    public String getCondicoes() {
        return condicoes;
    }

    public void setCondicoes(String condicoes) {
        this.condicoes = condicoes;
    }

    public OperacaoFormula getOperacaoFormula() {
        return operacaoFormula;
    }

    public void setOperacaoFormula(OperacaoFormula operacaoFormula) {
        this.operacaoFormula = operacaoFormula;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }
}
