package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.entidades.Servico;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.ClassificacaoAtividade;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoPorte;
import br.com.webpublico.enums.TipoRelatorioApresentacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.nfse.enums.Exigibilidade;
import br.com.webpublico.nfse.enums.SituacaoNota;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class FiltroNotaFiscal extends AbstractFiltroNotaFiscal {
    private TipoAgrupamento tipoAgrupamento;

    public FiltroNotaFiscal() {
        super();
        setValorTotalInicial(BigDecimal.ZERO);
        setValorTotalFinal(new BigDecimal("9999999"));
        tipoAgrupamento = TipoAgrupamento.NATUREZA_OPERACAO;
        setTipoRelatorioApresentacao(TipoRelatorioApresentacao.RESUMIDO);
    }

    public TipoAgrupamento getTipoAgrupamento() {
        return tipoAgrupamento;
    }

    public void setTipoAgrupamento(TipoAgrupamento tipoAgrupamento) {
        this.tipoAgrupamento = tipoAgrupamento;
    }

    public void validarCampos(String tipoRelatorioExtensao) {
        ValidacaoException ve = new ValidacaoException();
        validarCamposPorEmissaoOrCompetencia(ve);
        if (getTipoRelatorioApresentacao() == null)
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Apresentação é obrigatório.");

        if (tipoAgrupamento == null)
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Agrupamento é obrigatório.");

        ve.lancarException();
    }


    public enum TipoAgrupamento {
        NATUREZA_OPERACAO("Por Natureza da Operação"),
        COMPETENCIA_NATUREZA_OPERACAO("Por Competência e Natureza de Operação"),
        SERVICO_NATUREZA_OPERACAO("Por Serviço e Natureza de Operação");

        private String descricao;

        TipoAgrupamento(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public void adicionarFiltros(RelatorioDTO dto) {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();

        if (getDataInicial() != null) {
            parametros.add(new ParametrosRelatorios(" trunc(nota.emissao) ", ":data_inicial", null,
                br.com.webpublico.enums.OperacaoRelatorio.MAIOR_IGUAL, DataUtil.getDataFormatada(getDataInicial()), null, 1, true));
        }
        if (getDataFinal() != null) {
            parametros.add(new ParametrosRelatorios(" trunc(nota.emissao) ", ":data_final", null,
                br.com.webpublico.enums.OperacaoRelatorio.MENOR_IGUAL, DataUtil.getDataFormatada(getDataFinal()), null, 1, true));
        }

        if (getExercicioInicial() != null && getMesInicial() != null) {
            Calendar competenciaInicial = DataUtil.montaData(1, getMesInicial().getNumeroMesIniciandoEmZero(), getExercicioInicial().getAno());
            parametros.add(new ParametrosRelatorios(" trunc(dec.competencia) ", ":competencia_inicial", null,
                OperacaoRelatorio.MAIOR_IGUAL, DataUtil.getDataFormatada(competenciaInicial.getTime()), null, 1, true));
        }

        if (getExercicioFinal() != null && getMesFinal() != null) {
            Calendar competenciaFinal = DataUtil.montaData(1, getMesFinal().getNumeroMesIniciandoEmZero(), getExercicioFinal().getAno());
            competenciaFinal = DataUtil.ultimoDiaMes(competenciaFinal.getTime());

            parametros.add(new ParametrosRelatorios(" trunc(dec.competencia) ", ":competencia_final", null,
                OperacaoRelatorio.MENOR_IGUAL, DataUtil.getDataFormatada(competenciaFinal.getTime()), null, 1, true));
        }

        if (getNumero() != null && !getNumero().isEmpty()) {
            parametros.add(new ParametrosRelatorios(" nota.numero ", ":numero", null,
                OperacaoRelatorio.IGUAL, new Long(getNumero()), null, 1, false));
        }

        if (!Strings.isNullOrEmpty(getCnpjInicial())) {
            parametros.add(new ParametrosRelatorios(" dpp.cpfcnpj ", ":cpfcnpj_inicial", null,
                OperacaoRelatorio.MAIOR_IGUAL, getCnpjInicial(), null, 1, false));
        }

        if (!Strings.isNullOrEmpty(getCnpjFinal())) {
            parametros.add(new ParametrosRelatorios(" dpp.cpfcnpj ", ":cpfcnpj_final", null,
                OperacaoRelatorio.MENOR_IGUAL, getCnpjFinal(), null, 1, false));
        }

        if (getTipoContribuinte() != null) {
            parametros.add(new ParametrosRelatorios(" case when length(dpt.cpfcnpj) = 14 then 'JURIDICA' else 'FISICA' end ",
                ":tipo_tomador", null,
                OperacaoRelatorio.IGUAL, getTipoContribuinte().name(), null, 1, false));
        }

        if (getContribuinte() != null) {
            parametros.add(new ParametrosRelatorios(" dpt.cpfcnpj ",
                ":tomador", null,
                OperacaoRelatorio.IGUAL, StringUtil.retornaApenasNumeros(getContribuinte().getCpf_Cnpj()), null, 1, false));
        }

        if (getCadastroEconomico() != null) {
            parametros.add(new ParametrosRelatorios(" ce.id ",
                ":prestador", null,
                OperacaoRelatorio.IGUAL, getCadastroEconomico().getId(), null, 1, false));
        }

        if (getExigibilidades() != null && !getExigibilidades().isEmpty()) {
            List<String> namesExigibilidades = Lists.newArrayList();
            for (Exigibilidade exigibilidade : getExigibilidades()) {
                namesExigibilidades.add(exigibilidade.name());
            }
            parametros.add(new ParametrosRelatorios(" dec.naturezaOperacao ",
                ":naturezas", null,
                OperacaoRelatorio.IN, namesExigibilidades, null, 1, false));
        }

        if (getSituacoes() != null && !getSituacoes().isEmpty()) {
            List<String> namesSituacoes = Lists.newArrayList();
            for (SituacaoNota situacaoNota : getSituacoes()) {
                namesSituacoes.add(situacaoNota.name());
            }
            parametros.add(new ParametrosRelatorios(" dec.situacao ",
                ":situacoes", null,
                OperacaoRelatorio.IN, namesSituacoes, null, 1, false));
        }

        if (!Strings.isNullOrEmpty(getCpfCnpjTomadorInicial())) {
            parametros.add(new ParametrosRelatorios(" dpt.cpfcnpj ",
                ":cpfcnpj_tomador_inicial", null,
                OperacaoRelatorio.MAIOR_IGUAL, StringUtil.retornaApenasNumeros(getCpfCnpjTomadorInicial()), null, 1, false));
        }

        if (!Strings.isNullOrEmpty(getCpfCnpjTomadorFinal())) {
            parametros.add(new ParametrosRelatorios(" dpt.cpfcnpj ",
                ":cpfcnpj_tomador_final", null,
                OperacaoRelatorio.MENOR_IGUAL, StringUtil.retornaApenasNumeros(getCpfCnpjTomadorFinal()), null, 1, false));
        }

        if (getTiposPorte() != null && !getTiposPorte().isEmpty()) {
            parametros.add(new ParametrosRelatorios(" enq.porte ",
                ":portes", null,
                OperacaoRelatorio.IN, getTiposPorte().stream().map(TipoPorte::name).collect(Collectors.toList()),
                null, 1, false));
        }

        if (getServicos() != null && !getServicos().isEmpty()) {
            List<Long> idsServicos = Lists.newArrayList();
            for (Servico servico : getServicos()) {
                idsServicos.add(servico.getId());
            }
            parametros.add(new ParametrosRelatorios(" ids.servico_id ",
                ":servicos", null,
                OperacaoRelatorio.IN, idsServicos, null, 1, false));
        }

        if (getClassificacoesAtividade() != null && !getClassificacoesAtividade().isEmpty()) {
            List<String> namesClassificacoesAtividades = Lists.newArrayList();
            for (ClassificacaoAtividade classificacaoAtividade : getClassificacoesAtividade()) {
                namesClassificacoesAtividades.add(classificacaoAtividade.name());
            }
            parametros.add(new ParametrosRelatorios(" ce.classificacaoatividade ",
                ":classificacoes_atividades", null,
                OperacaoRelatorio.IN, namesClassificacoesAtividades, null, 1, false));
        }

        if (getValorTotalInicial() != null && getValorTotalInicial().compareTo(BigDecimal.ZERO) >= 0) {
            parametros.add(new ParametrosRelatorios(" dec.totalservicos ",
                ":valor_total_inicial", null,
                OperacaoRelatorio.MAIOR_IGUAL, getValorTotalInicial(), null, 1, false));
        }

        if (getValorTotalFinal() != null && getValorTotalFinal().compareTo(BigDecimal.ZERO) >= 0) {
            parametros.add(new ParametrosRelatorios(" dec.totalservicos ",
                ":valor_total_final", null,
                OperacaoRelatorio.MENOR_IGUAL, getValorTotalFinal(), null, 1, false));
        }

        dto.adicionarParametro("PARAMETROS", ParametrosRelatorios.parametrosToDto(parametros));
        dto.adicionarParametro("FILTROS", montarDescricaoFiltros());
    }
}
