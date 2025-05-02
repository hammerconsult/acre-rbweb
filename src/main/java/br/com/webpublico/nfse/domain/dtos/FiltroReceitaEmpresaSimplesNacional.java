package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.DateUtils;
import br.com.webpublico.StringUtils;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.enums.TipoIssqn;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.nfse.enums.SituacaoNota;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.Calendar;
import java.util.List;

public class FiltroReceitaEmpresaSimplesNacional extends AbstractFiltroNotaFiscal {

    public FiltroReceitaEmpresaSimplesNacional() {
        super();
        setSituacoes(Lists.newArrayList(SituacaoNota.EMITIDA, SituacaoNota.PAGA));
        setTiposIssqn(Lists.newArrayList(TipoIssqn.SIMPLES_NACIONAL, TipoIssqn.SUBLIMITE_ULTRAPASSADO));
        setSituacoesCadastral(Lists.newArrayList(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR));
    }

    public void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();
        validarCamposPorEmissaoOrCompetencia(ve);
        ve.lancarException();
    }

    public void adicionarFiltros(RelatorioDTO dto) {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();

        if (getDataInicial() != null) {
            parametros.add(new ParametrosRelatorios(" nf.emissao ", ":data_inicial", null,
                br.com.webpublico.enums.OperacaoRelatorio.MAIOR_IGUAL, DataUtil.getDataFormatada(getDataInicial()), null, 1, true));
        }
        if (getDataFinal() != null) {
            parametros.add(new ParametrosRelatorios(" nf.emissao ", ":data_final", null,
                br.com.webpublico.enums.OperacaoRelatorio.MENOR_IGUAL, DataUtil.getDataFormatada(getDataFinal()), null, 1, true));
        }

        if (getExercicioInicial() != null && getMesInicial() != null) {
            Calendar competenciaInicial = DataUtil.montaData(1, getMesInicial().getNumeroMesIniciandoEmZero(), getExercicioInicial().getAno());
            parametros.add(new ParametrosRelatorios(" dec.competencia ", ":competencia_inicial", null,
                br.com.webpublico.enums.OperacaoRelatorio.MAIOR_IGUAL, DataUtil.getDataFormatada(competenciaInicial.getTime()), null, 1, true));
        }

        if (getExercicioFinal() != null && getMesFinal() != null) {
            Calendar competenciaFinal = DataUtil.montaData(1, getMesFinal().getNumeroMesIniciandoEmZero(), getExercicioFinal().getAno());
            competenciaFinal = DataUtil.ultimoDiaMes(competenciaFinal.getTime());

            parametros.add(new ParametrosRelatorios(" dec.competencia ", ":competencia_final", null,
                br.com.webpublico.enums.OperacaoRelatorio.MENOR_IGUAL, DataUtil.getDataFormatada(competenciaFinal.getTime()), null, 1, true));
        }

        if (getSituacoes() != null && !getSituacoes().isEmpty()) {
            List<String> namesSituacoes = Lists.newArrayList();
            for (SituacaoNota situacaoNota : getSituacoes()) {
                namesSituacoes.add(situacaoNota.name());
            }
            parametros.add(new ParametrosRelatorios(" dec.situacao ",
                ":situacoes", null,
                br.com.webpublico.enums.OperacaoRelatorio.IN, namesSituacoes, null, 1, false));
        }

        if (!Strings.isNullOrEmpty(getCnpjInicial())) {
            parametros.add(new ParametrosRelatorios(" coalesce(pf.cpf, pj.cnpj) ", ":cpfcnpj_inicial", null,
                br.com.webpublico.enums.OperacaoRelatorio.MAIOR_IGUAL, StringUtils.removerMascaraCpfCnpj(getCnpjInicial()), null, 2, false));
        }

        if (!Strings.isNullOrEmpty(getCnpjFinal())) {
            parametros.add(new ParametrosRelatorios(" coalesce(pf.cpf, pj.cnpj) ", ":cpfcnpj_final", null,
                br.com.webpublico.enums.OperacaoRelatorio.MENOR_IGUAL, StringUtils.removerMascaraCpfCnpj(getCnpjFinal()), null, 2, false));
        }

        List<TipoIssqn> tiposIssqn = getTiposIssqn();
        if (tiposIssqn == null || tiposIssqn.isEmpty()) {
            tiposIssqn = Lists.newArrayList();
            tiposIssqn.add(TipoIssqn.SIMPLES_NACIONAL);
            tiposIssqn.add(TipoIssqn.SUBLIMITE_ULTRAPASSADO);
        }
        parametros.add(new ParametrosRelatorios(" ef.tipoissqn ",
            ":tiposIssqn", null,
            br.com.webpublico.enums.OperacaoRelatorio.IN, tiposIssqn.stream().map(Enum::name), null, 2, false));

        List<SituacaoCadastralCadastroEconomico> situacoesCadastral = getSituacoesCadastral();
        if (situacoesCadastral == null || situacoesCadastral.isEmpty()) {
            situacoesCadastral = Lists.newArrayList();
            situacoesCadastral.add(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR);
            situacoesCadastral.add(SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR);
            situacoesCadastral.add(SituacaoCadastralCadastroEconomico.INATIVO);
        }
        parametros.add(new ParametrosRelatorios(" sce.situacaocadastral ",
            ":situacoesCadastral", null,
            br.com.webpublico.enums.OperacaoRelatorio.IN, situacoesCadastral.stream().map(Enum::name), null, 2, false));

        dto.adicionarParametro("PERIODO_INICIAL", getPeriodoInicial().getTime());
        dto.adicionarParametro("PERIODO_FINAL", getPeriodoFinal().getTime());
        dto.adicionarParametro("PARAMETROS", ParametrosRelatorios.parametrosToDto(parametros));
        dto.adicionarParametro("FILTROS", montarDescricaoFiltros());
        dto.adicionarParametro("TIPO_APRESENTACAO", getTipoRelatorioApresentacao().name());
    }
}
