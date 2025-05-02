package br.com.webpublico.entidades.rh.dirf;

import br.com.webpublico.entidades.Dirf;
import br.com.webpublico.entidades.PessoaJuridica;
import br.com.webpublico.entidades.VinculoFP;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by William on 21/02/2019.
 */
public interface DirfVinculoFPPessoa {

    Long getId();

    Dirf getDirf();

    PessoaJuridica getFontePagadora();

    VinculoFP getVinculoFP();

    String getNaturezaDoRendimento();

    BigDecimal getTotalRendimentos();

    //------------- 3.
    BigDecimal getContribuicaoPrevidenciaria();

    BigDecimal getPensaoProventos();

    BigDecimal getPensaoAlimenticia();

    BigDecimal getImpostoDeRendaRetido();

    //------------- 4.
    BigDecimal getParcelaIsenta();

    BigDecimal getDiariasAjudasDeCusto();

    BigDecimal getPensaoProventosPorAcidente();

    BigDecimal getLucroEDividendoApurado();

    BigDecimal getValoresPagosAoTitular();

    BigDecimal getIndenizacoesPorRescisao();

    BigDecimal getOutrosRendimentosIsentos();

    //------------- 5.
    BigDecimal getDecimoTerceiroSalario();

    BigDecimal getDecimoTerceiroSalarioPensao();

    BigDecimal getImpostoSobreARendaRetidoNo13();

    BigDecimal getOutrosSujeitosATributacao();

    BigDecimal getImpostoSobreARendaRetido();

    //------------- 6.
    String getNumeroProcesso();

    BigDecimal getQuantidadeDeMeses();

    String getNaturezaDoRendimentoRecebido();

    BigDecimal getTotalRendimentosTributaveis();

    BigDecimal getExclusaoDespesasAcaoJudicial();

    BigDecimal getDeducaoContribuicaoPrev();

    BigDecimal getDeducaoPensaoAlimenticia();

    BigDecimal ImpostoSobreARendaRetido();

    BigDecimal getRendimentosIsentosDePensao();

    Boolean isDirfVinculo();

    List<DirfInfoComplementares> getInformacoesComplementares();

    String getNome();

    String getCpf();

}


