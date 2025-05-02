package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoRelatorioApresentacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.nfse.domain.DeclaracaoMensalServico;
import br.com.webpublico.nfse.enums.TipoMovimentoMensal;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.webreportdto.dto.WebReportUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;

import java.util.List;
import java.util.Map;

public class FiltroRelatorioDeclaracaoMensalDTO {

    private CadastroEconomico cadastroEconomico;
    private String cnpjInicial;
    private String cnpjFinal;
    private Mes mes;
    private Exercicio exercicio;
    private TipoMovimentoMensal tipoMovimentoMensal;
    private DeclaracaoMensalServico declaracaoMensalServico;
    private TipoRelatorioApresentacao tipoRelatorioApresentacao;

    public FiltroRelatorioDeclaracaoMensalDTO() {
        this.tipoRelatorioApresentacao = TipoRelatorioApresentacao.RESUMIDO;
    }

    public TipoMovimentoMensal getTipoMovimentoMensal() {
        return tipoMovimentoMensal;
    }

    public void setTipoMovimentoMensal(TipoMovimentoMensal tipoMovimentoMensal) {
        this.tipoMovimentoMensal = tipoMovimentoMensal;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public String getCnpjInicial() {
        return cnpjInicial;
    }

    public void setCnpjInicial(String cnpjInicial) {
        this.cnpjInicial = cnpjInicial;
    }

    public String getCnpjFinal() {
        return cnpjFinal;
    }

    public void setCnpjFinal(String cnpjFinal) {
        this.cnpjFinal = cnpjFinal;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public DeclaracaoMensalServico getDeclaracaoMensalServico() {
        return declaracaoMensalServico;
    }

    public void setDeclaracaoMensalServico(DeclaracaoMensalServico declaracaoMensalServico) {
        this.declaracaoMensalServico = declaracaoMensalServico;
    }

    public TipoRelatorioApresentacao getTipoRelatorioApresentacao() {
        return tipoRelatorioApresentacao;
    }

    public void setTipoRelatorioApresentacao(TipoRelatorioApresentacao tipoRelatorioApresentacao) {
        this.tipoRelatorioApresentacao = tipoRelatorioApresentacao;
    }

    public void validarCamposObrigatorios() throws ValidacaoException {
        ValidacaoException operacaoNaoPermitidaException = new ValidacaoException();
        if (this.exercicio == null) {
            operacaoNaoPermitidaException.adicionarMensagemDeCampoObrigatorio("O Campo Exercício é obrigatório.");
        }
        operacaoNaoPermitidaException.lancarException();
    }

    public String getCriteriosUtilizados() {
        String criteriosUtilizados = "";
        String juncao = " ";
        if (cadastroEconomico != null) {
            criteriosUtilizados += juncao + "Cadastro Econômico: " + cadastroEconomico.toString();
            juncao = ", ";
        }
        if (StringUtils.isNotEmpty(cnpjInicial)) {
            criteriosUtilizados += juncao + "CNPJ Inicial: " + cnpjInicial;
            juncao = ", ";
        }
        if (StringUtils.isNotEmpty(cnpjFinal)) {
            criteriosUtilizados += juncao + "CNPJ Final: " + cnpjFinal;
            juncao = ", ";
        }
        if (exercicio != null) {
            criteriosUtilizados += juncao + "Exercício: " + exercicio.getAno();
            juncao = ", ";
        }
        if (mes != null) {
            criteriosUtilizados += juncao + "Mês: " + mes.getDescricao();
            juncao = ", ";
        }
        if (tipoMovimentoMensal != null) {
            criteriosUtilizados += juncao + "Tipo de Movimento: " + tipoMovimentoMensal.getDescricao();
            juncao = ", ";
        }
        if (declaracaoMensalServico != null) {
            criteriosUtilizados += juncao + "Encerramento: " + declaracaoMensalServico.toString();
            juncao = ", ";
        }
        return criteriosUtilizados + "; ";
    }

    public void montarParametros(RelatorioDTO dto) {
        dto.adicionarParametro("MUNICIPIO", "PREFEITURA DE RIO BRANCO");
        dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE FINANÇAS");
        dto.adicionarParametro("DEPARTAMENTO", "DEPARTAMENTO DE TRIBUTAÇÃO");
        dto.adicionarParametro("USUARIO", SistemaFacade.obtemLogin());
        dto.adicionarParametro("FILTROS", getCriteriosUtilizados());
        dto.adicionarParametro("CONSOLIDADO", TipoRelatorioApresentacao.RESUMIDO.equals(getTipoRelatorioApresentacao()));

        Map<String, Map<String, String>> filtrosExcel = Maps.newHashMap();

        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        if (getExercicio() != null) {
            parametros.add(new ParametrosRelatorios(" dms.exercicio_id ", ":exercicio", null,
                br.com.webpublico.enums.OperacaoRelatorio.IGUAL, getExercicio().getId(), null, 1, false));
            WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Exercício:", getExercicio().getAno().toString());
        }
        if (getMes() != null) {
            parametros.add(new ParametrosRelatorios(" dms.mes ", ":mes", null,
                br.com.webpublico.enums.OperacaoRelatorio.IGUAL, getMes().name(), null, 1, false));
            WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Mês:", getMes().getDescricao());
        }
        if (getCadastroEconomico() != null) {
            parametros.add(new ParametrosRelatorios(" ce.id ", ":cmc_id", null,
                br.com.webpublico.enums.OperacaoRelatorio.IGUAL, getCadastroEconomico().getId(), null, 1, false));
            WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Cadastro Econômico:", getCadastroEconomico().getCmcNomeCpfCnpj());
        }
        if (Strings.isNotEmpty(getCnpjInicial())) {
            parametros.add(new ParametrosRelatorios(" coalesce(ce_pf.cpf, ce_pj.cnpj) ", ":cpfcnpj_inicial", null,
                br.com.webpublico.enums.OperacaoRelatorio.MAIOR_IGUAL, br.com.webpublico.StringUtils.removerMascaraCpfCnpj(getCnpjInicial()), null, 1, false));
            WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "CNPJ Inicial:", getCnpjInicial());
        }
        if (Strings.isNotEmpty(getCnpjFinal())) {
            parametros.add(new ParametrosRelatorios(" coalesce(ce_pf.cpf, ce_pj.cnpj) ", ":cpfcnpj_final", null,
                br.com.webpublico.enums.OperacaoRelatorio.MENOR_IGUAL, br.com.webpublico.StringUtils.removerMascaraCpfCnpj(getCnpjFinal()), null, 1, false));
            WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "CNPJ Final:", getCnpjFinal());
        }
        if (getTipoMovimentoMensal() != null) {
            parametros.add(new ParametrosRelatorios(" dms.tipomovimento ", ":tipo_movimento", null,
                br.com.webpublico.enums.OperacaoRelatorio.IGUAL, getTipoMovimentoMensal().name(), null, 1, false));
            WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Tipo de Movimento:", getTipoMovimentoMensal().getDescricao());
        }
        if (getDeclaracaoMensalServico() != null) {
            parametros.add(new ParametrosRelatorios(" dms.id ", ":id_dms", null,
                OperacaoRelatorio.IGUAL, getDeclaracaoMensalServico().getId(), null, 1, false));
            WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Declaração:", getDeclaracaoMensalServico().getCodigo().toString());
        }
        dto.adicionarParametro("parametros", ParametrosRelatorios.parametrosToDto(parametros));
        dto.adicionarParametro("FILTROS_EXCEL", filtrosExcel);
    }
}
