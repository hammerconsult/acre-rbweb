package br.com.webpublico.controlerelatorio.contabil;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.contabil.reinf.NaturezaRendimentoReinf;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.contabil.reinf.TipoServicoReinf;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.negocios.ConfiguracaoContabilFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.TipoDocumentoFiscalFacade;
import br.com.webpublico.negocios.contabil.reinf.NaturezaRendimentoReinfFacade;
import br.com.webpublico.negocios.rh.esocial.ConfiguracaoEmpregadorESocialFacade;
import br.com.webpublico.reinf.eventos.TipoArquivoReinf;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-reinf-r2010", pattern = "/relatorio/reinf/r2010/", viewId = "/faces/financeiro/reinf/relatorio/r2010.xhtml"),
    @URLMapping(id = "relatorio-reinf-r4020", pattern = "/relatorio/reinf/r4020/", viewId = "/faces/financeiro/reinf/relatorio/r4020.xhtml")
})
public class RelatorioReinfControlador implements Serializable {
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private TipoDocumentoFiscalFacade tipoDocumentoFiscalFacade;
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;
    @EJB
    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;
    @EJB
    private NaturezaRendimentoReinfFacade naturezaRendimentoReinfFacade;
    private TipoArquivoReinf tipoArquivoReinf;
    private Exercicio exercicio;
    private Mes mes;
    private TipoDocumentoFiscal tipoDocumentoFiscal;
    private TipoServicoReinf tipoServicoReinf;
    private Entidade empregador;
    private EnviadoNaoEnviado enviadoNaoEnviado;
    private NaturezaRendimentoReinf naturezaRendimentoReinf;
    private String filtros;

    @URLAction(mappingId = "relatorio-reinf-r2010", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCamposR2010() {
        tipoArquivoReinf = TipoArquivoReinf.R2010;
        limparCampos();
    }

    @URLAction(mappingId = "relatorio-reinf-r4020", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCamposR4020() {
        tipoArquivoReinf = TipoArquivoReinf.R4020;
        limparCampos();
    }

    public void limparCampos() {
        exercicio = sistemaFacade.getExercicioCorrente();
        mes = null;
        tipoDocumentoFiscal = null;
        tipoServicoReinf = null;
        empregador = null;
        enviadoNaoEnviado = null;
        naturezaRendimentoReinf = null;
        filtros = "";
    }

    public void gerarRelatorio(String tipoExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoExtensao));
            dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("TIPO_ARQUIVO", tipoArquivoReinf.name());
            dto.adicionarParametro("CONDICAO", montarCondicao());
            dto.adicionarParametro("CNPJ_EMPREGADOR", Util.formatarCnpj(empregador.getPessoaJuridica().getCnpj()));
            dto.adicionarParametro("FILTROS", filtros);
            dto.setNomeRelatorio("Relatório REINF " + tipoArquivoReinf.getCodigo());
            dto.setApi("contabil/reinf/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (empregador == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Estabelecimento deve ser informado.");
        }
        if (exercicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício deve ser informado.");
        }
        ve.lancarException();
    }

    private String montarCondicao() {
        filtros = "Estabelecimento: " + empregador.toString() + "; Exercício: " + exercicio.getAno();
        String colunaDataReferencia = isEventoR2010() ? "dfl.datadocto" : "pag.datapagamento";
        String condicao = getCondicaoEmpregador() +
            " and extract(year from " + colunaDataReferencia + ") = " + exercicio.getAno() +
            " and (reg.id is not null or cd.tipocontadespesa in " + getTiposDespesa() + ")" +
            " and (reg.tipoarquivo in ('" +
            (isEventoR2010() ? TipoArquivoReinf.R2010.name() + "', '" + TipoArquivoReinf.R2010V2.name() : TipoArquivoReinf.R4020.name()) +
            "') or c.codigo in " + montarClausulaInComCodigosContasExtrasPorTipoArquivoReinf() + ")";

        if (mes != null) {
            condicao += " and extract(month from " + colunaDataReferencia + ") = " + mes.getNumeroMes();
            filtros += "; Mês: " + mes.getDescricao();
        }

        if (tipoDocumentoFiscal != null) {
            condicao += " and tdf.id = " + tipoDocumentoFiscal.getId();
            filtros += "; Tipo de Documento: " + tipoDocumentoFiscal.getCodigo();
        }

        if (tipoServicoReinf != null) {
            condicao += " and ldf.tipoServicoReinf = '" + tipoServicoReinf.name() + "'";
            filtros += "; Tipo de Serviço: " + tipoServicoReinf.toString();
        }

        if (enviadoNaoEnviado != null) {
            condicao += " and reg.id " + enviadoNaoEnviado.getCondicao();
            filtros += "; Somente: " + enviadoNaoEnviado.getDescricao();
        }

        if (naturezaRendimentoReinf != null) {
            condicao += " and ldf.naturezaRendimentoReinf_id = " + naturezaRendimentoReinf.getId();
            filtros += "; Natureza de Rendimentos: " + naturezaRendimentoReinf.toString();
        }
        return condicao;
    }

    private String montarClausulaInComCodigosContasExtrasPorTipoArquivoReinf() {
        List<ConfiguracaoContabilContaReinf> contasExtras = configuracaoContabilFacade.buscarContasReinfVigentesPorTipoArquivo(tipoArquivoReinf);
        if (contasExtras.isEmpty()) {
            throw new ExcecaoNegocioGenerica("Nenhum conta configurada para as retenções do REINF foi encontrada na configuração contábil para o Tipo de Arquivo REINF " + tipoArquivoReinf.getCodigo() + ".");
        }
        List<String> codigos = Lists.newArrayList();
        for (ConfiguracaoContabilContaReinf contaReinf : contasExtras) {
            codigos.add(contaReinf.getContaExtra().getCodigo());
        }
        return Util.montarClausulaIn(codigos);
    }

    private String getTiposDespesa() {
        String retorno = "('NAO_INFORMADO_NA_CONFIGURACAO')";
        ConfiguracaoContabil configuracaoContabil = configuracaoContabilFacade.configuracaoContabilVigente();
        if (configuracaoContabil != null && configuracaoContabil.getTiposContasDespesasReinf() != null && !configuracaoContabil.getTiposContasDespesasReinf().isEmpty()) {
            retorno = "(";
            for (ConfiguracaoContabilTipoContaDespesaReinf ccTipo : configuracaoContabil.getTiposContasDespesasReinf()) {
                retorno += "'" + ccTipo.getTipoContaDespesa().name() + "', ";
            }
            retorno = retorno.substring(0, retorno.length() - 2);
            retorno += ")";
        }
        return retorno;
    }

    private String getCondicaoEmpregador() {
        ConfiguracaoEmpregadorESocial config = configuracaoEmpregadorESocialFacade.recuperarPorEntidade(empregador);
        if (config == null)
            throw new ValidacaoException("Não foi encontrado configuração e-social para o empregador " + empregador.toString());
        return " where re.unidadeorganizacional_id in (select orc.subordinada_id " +
            " from itemempregadoresocial item " +
            "    inner join hierarquiaorganizacional orgaoadm on item.unidadeorganizacional_id = orgaoadm.subordinada_id " +
            "                                                 and trunc(re.datareceita) between orgaoadm.iniciovigencia and coalesce(orgaoadm.fimvigencia, trunc(re.datareceita)) " +
            "    inner join hierarquiaorgresp resp on resp.hierarquiaorgadm_id = orgaoadm.id " +
            "                                      and trunc(re.datareceita) between resp.datainicio and coalesce(resp.datafim, trunc(re.datareceita)) " +
            "    inner join hierarquiaorganizacional orc on resp.hierarquiaorgorc_id = orc.id " +
            "                                            and trunc(re.datareceita) between orc.iniciovigencia and coalesce(orc.fimvigencia, trunc(re.datareceita)) " +
            " where item.configempregadoresocial_id = " + config.getId() + ") ";
    }

    public List<SelectItem> getEmpregadores() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        for (ConfiguracaoEmpregadorESocial config : configuracaoEmpregadorESocialFacade.lista()) {
            if (config.getEntidade() != null)
                retorno.add(new SelectItem(config.getEntidade(), config.getEntidade().toString()));
        }
        return retorno;
    }

    public List<TipoDocumentoFiscal> completarTipoDoctos(String parte) {
        return tipoDocumentoFiscalFacade.buscarTiposDeDocumentosAtivos(parte.trim());

    }

    public List<SelectItem> getEnviadosNaoEnviados() {
        return Util.getListSelectItem(EnviadoNaoEnviado.values());
    }

    public List<SelectItem> getMeses() {
        return Util.getListSelectItem(Mes.values(), false);
    }

    public List<SelectItem> getTiposServicos() {
        return Util.getListSelectItem(TipoServicoReinf.values(), false);
    }

    public List<NaturezaRendimentoReinf> completarNaturezasDosRendimentosREINF(String parte) {
        return naturezaRendimentoReinfFacade.buscarNaturezasVigentes(parte, sistemaFacade.getDataOperacao());
    }

    public boolean isEventoR2010() {
        return TipoArquivoReinf.R2010.equals(tipoArquivoReinf);
    }

    public boolean isEventoR4020() {
        return TipoArquivoReinf.R4020.equals(tipoArquivoReinf);
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public TipoArquivoReinf getTipoArquivoReinf() {
        return tipoArquivoReinf;
    }

    public void setTipoArquivoReinf(TipoArquivoReinf tipoArquivoReinf) {
        this.tipoArquivoReinf = tipoArquivoReinf;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public TipoDocumentoFiscal getTipoDocumentoFiscal() {
        return tipoDocumentoFiscal;
    }

    public void setTipoDocumentoFiscal(TipoDocumentoFiscal tipoDocumentoFiscal) {
        this.tipoDocumentoFiscal = tipoDocumentoFiscal;
    }

    public TipoServicoReinf getTipoServicoReinf() {
        return tipoServicoReinf;
    }

    public void setTipoServicoReinf(TipoServicoReinf tipoServicoReinf) {
        this.tipoServicoReinf = tipoServicoReinf;
    }

    public Entidade getEmpregador() {
        return empregador;
    }

    public void setEmpregador(Entidade empregador) {
        this.empregador = empregador;
    }

    public EnviadoNaoEnviado getEnviadoNaoEnviado() {
        return enviadoNaoEnviado;
    }

    public void setEnviadoNaoEnviado(EnviadoNaoEnviado enviadoNaoEnviado) {
        this.enviadoNaoEnviado = enviadoNaoEnviado;
    }

    public NaturezaRendimentoReinf getNaturezaRendimentoReinf() {
        return naturezaRendimentoReinf;
    }

    public void setNaturezaRendimentoReinf(NaturezaRendimentoReinf naturezaRendimentoReinf) {
        this.naturezaRendimentoReinf = naturezaRendimentoReinf;
    }

    public enum EnviadoNaoEnviado implements EnumComDescricao {
        ENVIADO("Enviado", "is not null"),
        NAO_ENVIADO("Não Enviado", "is null");
        private String descricao;
        private String condicao;

        EnviadoNaoEnviado(String descricao, String condicao) {
            this.descricao = descricao;
            this.condicao = condicao;
        }

        @Override
        public String getDescricao() {
            return descricao;
        }

        public String getCondicao() {
            return condicao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
