/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ClassificacaoAtividade;
import br.com.webpublico.tributario.enumeration.GrauDeRiscoDTO;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Wellington
 */
@ManagedBean(name = "relatorioArrecadacoesControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaConsultaArrecadacoes", pattern = "/tributario/arrecadacao/relatorioarrecadacoes/",
        viewId = "/faces/tributario/arrecadacao/relatorios/relatorioarrecadacoes.xhtml")
})
public class RelatorioArrecadacoesControlador implements Serializable {

    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private LogradouroFacade logradouroFacade;
    @EJB
    private BairroFacade bairroFacade;
    @EJB
    private CNAEFacade cnaeFacade;
    @EJB
    private TipoAutonomoFacade tipoAutonomoFacade;
    @EJB
    private NaturezaJuridicaFacade naturezaJuridicaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    private String contribuinteInicial;
    private String contribuinteFinal;
    private String inscricaoCadastralImovelInicial;
    private String inscricaoCadastralImovelFinal;
    private String inscricaoCadastralCmcInicial;
    private String inscricaoCadastralCmcFinal;
    private String inscricaoCadastralRuralInicial;
    private String inscricaoCadastralRuralFinal;
    private Date dataPagamentoInicial;
    private Date dataPagamentoFinal;
    private TipoCadastroTributario tipoCadastroTributario;
    private List<Divida> dividasSelecionadas;
    private final SimpleDateFormat formatterData = new SimpleDateFormat("dd/MM/yyyy");
    private StringBuilder where;
    private StringBuilder filtro;
    private String setor;
    private String quadra;
    private String lote;
    private Bairro bairroCadastroImobiliario;
    private Bairro bairroCadastroEconomico;
    private Logradouro logradouroCadastroImobiliario;
    private Logradouro logradouroCadastroEconomico;
    // tipo imovel: PREDIAL OU TERRITORIAL, se tiver contruções é predial caso contrario é territorial
    private String tipoImovel;
    private ClassificacaoAtividade classificacao;
    private NaturezaJuridica naturezaJuridica;
    private GrauDeRiscoDTO grauDeRisco;
    private TipoAutonomo tipoAutonomo;
    private Date dataLancamentoInicial;
    private Date dataLancamentoFinal;
    private Date dataVencimentoInicial;
    private Date dataVencimentoFinal;
    private Date dataMovimentoInicial;
    private Date dataMovimentoFinal;
    private String numeroLoteArrecadacao;
    private BancoContaConfTributario[] contasSelecionadas;
    private Divida divida;
    private Date dataOperacao;
    private Exercicio exercicioInicial, exercicioFinal;
    private static final Logger logger = LoggerFactory.getLogger(RelatorioArrecadacoesControlador.class);

    @URLAction(mappingId = "novaConsultaArrecadacoes", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparFiltros() {
        contribuinteInicial = "";
        contribuinteFinal = "";
        inscricaoCadastralImovelInicial = "";
        inscricaoCadastralImovelFinal = "";
        inscricaoCadastralCmcInicial = "";
        inscricaoCadastralCmcFinal = "";
        inscricaoCadastralRuralInicial = "";
        inscricaoCadastralRuralFinal = "";
        dataPagamentoInicial = null;
        dataPagamentoFinal = null;
        dividasSelecionadas = null;
        tipoCadastroTributario = null;
        tipoImovel = null;
        bairroCadastroEconomico = null;
        logradouroCadastroEconomico = null;
        bairroCadastroImobiliario = null;
        logradouroCadastroImobiliario = null;
        setor = null;
        quadra = null;
        lote = null;
        dividasSelecionadas = new ArrayList<>();
        dataOperacao = sistemaFacade.getDataOperacao();
        dataLancamentoInicial = null;
        dataLancamentoFinal = null;
        dataVencimentoInicial = null;
        dataVencimentoFinal = null;
        dataMovimentoInicial = null;
        dataMovimentoFinal = null;
    }

    public void trocouTipoCadastro() {
        dividasSelecionadas = new ArrayList<>();
        if (tipoCadastroTributario == null) {
            return;
        }
        if (TipoCadastroTributario.PESSOA.equals(tipoCadastroTributario)) {
            contribuinteInicial = "";
            contribuinteFinal = "";
        }
        if (TipoCadastroTributario.IMOBILIARIO.equals(tipoCadastroTributario)) {
            inscricaoCadastralImovelInicial = "1";
            inscricaoCadastralImovelFinal = "999999999999999";
        }
        if (TipoCadastroTributario.ECONOMICO.equals(tipoCadastroTributario)) {
            inscricaoCadastralCmcInicial = "1";
            inscricaoCadastralCmcFinal = "999999999";
        }
        if (TipoCadastroTributario.RURAL.equals(tipoCadastroTributario)) {
            inscricaoCadastralRuralInicial = "1";
            inscricaoCadastralRuralFinal = "999999999";
        }
    }

    public void montarFiltros() {
        where = new StringBuilder();
        filtro = new StringBuilder();
        String juncao = " WHERE ";

        if (tipoCadastroTributario != null) {
            filtro.append("Tipo de Cadastro: ").append(tipoCadastroTributario.name()).append("; ");
            juncao = filtrarTipoCadastroPessoa(juncao);
            juncao = filtrarTipoCadastroImobiliario(juncao);
            juncao = filtrarTipoCadastroEconomico(juncao);
            juncao = filtrarTipoCadastroRural(juncao);
        }
        juncao = filtrarDataPagamento(juncao);
        juncao = filtrarTipoImovel(juncao);
        if (TipoCadastroTributario.IMOBILIARIO.equals(tipoCadastroTributario)) {
            juncao = filtrarEnderecoCadastroImobiliario(juncao);
        } else if (TipoCadastroTributario.ECONOMICO.equals(tipoCadastroTributario)) {
            juncao = filtrarEnderecoCadastroEconomico(juncao);
        }
        juncao = filtrarSetoOrQuadraOrLote(juncao);
        juncao = filtrarClassificaoAtividade(juncao);
        juncao = filtrarNaturezaJuridica(juncao);
        juncao = filtrarGrauDeRisco(juncao);
        juncao = filtrarTipoAutonomo(juncao);
        juncao = filtrarDataLancamentoOrVencimentoOrMovimento(juncao);
        juncao = filtrarCodigoLote(juncao);
        juncao = filtrarAgenteArrecadador(juncao);
        juncao = filtrarExercicio(juncao);
        filtrarDividas(juncao);
    }

    private String filtrarExercicio(String juncao) {
        if (exercicioInicial != null) {
            if (exercicioFinal == null) {
                exercicioFinal = exercicioFacade.getExercicioCorrente();
            }
            filtro.append("Exercicio Inicial: ").append(exercicioInicial.getAno()).append("; ");
            filtro.append("Exercicio Final: ").append(exercicioFinal.getAno()).append("; ");
            where.append(juncao);
            where.append("rel.exercicio between " + exercicioInicial.getAno() + " and " + exercicioFinal.getAno() + "");
            juncao = " AND ";
        }
        return juncao;
    }

    private String filtrarAgenteArrecadador(String juncao) {
        if (contasSelecionadas != null && contasSelecionadas.length > 0) {
            where.append(juncao);
            String idSubContas = "";
            String descritivosConta = "";
            for (BancoContaConfTributario contaConf : contasSelecionadas) {
                idSubContas = idSubContas + contaConf.getSubConta().getId() + ",";
                descritivosConta = descritivosConta + contaConf.getSubConta().getDescricao() + ", ";
            }
            idSubContas = idSubContas.substring(0, idSubContas.length() - 1);
            where.append("rel.subconta in ").append("(" + idSubContas + ")");
            juncao = " AND ";
        }
        return juncao;
    }

    private String filtrarCodigoLote(String juncao) {
        if (numeroLoteArrecadacao != null && !numeroLoteArrecadacao.trim().isEmpty()) {
            filtro.append("Código do Lote: ").append(numeroLoteArrecadacao).append("; ");
            where.append(juncao);
            where.append("rel.codigoLote = ").append(numeroLoteArrecadacao);
            juncao = " AND ";
        }
        return juncao;
    }

    private String filtrarDataLancamentoOrVencimentoOrMovimento(String juncao) {

        if (dataLancamentoInicial != null && dataLancamentoFinal != null) {
            filtro.append("Data de Lançamento: ").append(formatterData.format(dataLancamentoInicial)).append(" até ").append(formatterData.format(dataLancamentoFinal)).append("; ");
            where.append(juncao);
            where.append("rel.datalancamento between to_date('" + DataUtil.getDataFormatada(dataLancamentoInicial) + "','dd/MM/yyyy') and to_date('" + DataUtil.getDataFormatada(dataLancamentoFinal) + "','dd/MM/yyyy')");
            juncao = " AND ";
        }

        if (dataVencimentoInicial != null && dataVencimentoFinal != null) {
            filtro.append("Data de Vencimento: ").append(formatterData.format(dataVencimentoInicial)).append(" até ").append(formatterData.format(dataVencimentoFinal)).append("; ");
            where.append(juncao);
            where.append("rel.datavencimento between to_date('" + DataUtil.getDataFormatada(dataVencimentoInicial) + "','dd/MM/yyyy') and to_date('" + DataUtil.getDataFormatada(dataVencimentoFinal) + "','dd/MM/yyyy')");
            juncao = " AND ";
        }

        if (dataMovimentoInicial != null && dataMovimentoFinal != null) {
            filtro.append("Data de Movimento: ").append(formatterData.format(dataMovimentoInicial)).append(" até ").append(formatterData.format(dataMovimentoFinal)).append("; ");
            where.append(juncao);
            where.append("rel.dataMovimento between to_date('" + DataUtil.getDataFormatada(dataMovimentoInicial) + "','dd/MM/yyyy') and to_date('" + DataUtil.getDataFormatada(dataMovimentoFinal) + "','dd/MM/yyyy')");
            juncao = " AND ";
        }

        return juncao;
    }

    private String filtrarTipoAutonomo(String juncao) {
        if (tipoAutonomo != null) {
            filtro.append("Tipo de Autônomo: ").append(tipoAutonomo.getDescricao()).append("; ");
            where.append(juncao);
            where.append("rel.tipoAutonomo = " + tipoAutonomo.getId() + "");
            juncao = " AND ";
        }
        return juncao;
    }

    private String filtrarGrauDeRisco(String juncao) {
        if (grauDeRisco != null) {
            filtro.append("Grau de Risco: ").append(grauDeRisco.getDescricao()).append("; ");
            where.append(juncao);
            where.append(" exists(select ceCnae.cadastroEconomico_id " +
                "           from EconomicoCNAE ceCnae " +
                "           inner join cnae on ceCnae.cnae_id = cnae.id " +
                "           where rel.cadastro_id = ceCnae.cadastroEconomico_id " +
                "           and cnae.grauDeRisco = '" + grauDeRisco.name() + "' " +
                ") ");
            juncao = " AND ";
        }
        return juncao;
    }

    private String filtrarNaturezaJuridica(String juncao) {
        if (naturezaJuridica != null) {
            filtro.append("Natureza Jurídica: ").append(naturezaJuridica.getDescricao()).append("; ");
            where.append(juncao);
            where.append("rel.naturezaJuridica = " + naturezaJuridica.getId() + "");
            juncao = " AND ";
        }
        return juncao;
    }

    private String filtrarClassificaoAtividade(String juncao) {
        if (classificacao != null) {
            filtro.append("Classificação de Atividade: ").append(classificacao.getDescricao()).append("; ");
            where.append(juncao);
            where.append("rel.classificacaoAtividade = '" + classificacao.name() + "'");
            juncao = " AND ";
        }
        return juncao;
    }

    private String filtrarSetoOrQuadraOrLote(String juncao) {
        if (setor != null && !setor.trim().isEmpty()) {
            filtro.append("Setor: ").append(setor.trim()).append("; ");
            where.append(juncao);
            where.append("rel.setor like '%" + setor.trim() + "%'");
            juncao = " AND ";
        }

        if (quadra != null && !quadra.trim().isEmpty()) {
            filtro.append("Quadra: ").append(quadra.trim()).append("; ");
            where.append(juncao);
            where.append("rel.quadra like '%" + quadra.trim() + "%'");
            juncao = " AND ";
        }

        if (lote != null && !lote.trim().isEmpty()) {
            filtro.append("Lote: ").append(lote.trim()).append("; ");
            where.append(juncao);
            where.append("rel.lote like '%" + lote.trim() + "%'");
            juncao = " AND ";
        }
        return juncao;
    }

    private String filtrarTipoImovel(String juncao) {
        if (tipoImovel != null) {
            filtro.append("Tipo de Imovél: ").append(tipoImovel.equals("PREDIAL") ? "Predial" : "Territorial").append("; ");
            where.append(juncao);
            where.append(" rel.cadastro_id").append("PREDIAL".equals(tipoImovel) ? " in " : " not in ").append(" ( select c.imovel_id from construcao c where c.imovel_id = rel.cadastro_id) ");
            juncao = " AND ";
        }
        return juncao;
    }

    private String filtrarEnderecoCadastroImobiliario(String juncao) {
        if (bairroCadastroImobiliario != null) {
            filtro.append("Bairro: ").append(bairroCadastroImobiliario.getDescricao()).append("; ");
            where.append(juncao);
            where.append(" rel.bairro like '" + bairroCadastroImobiliario.getDescricao() + "'");
            juncao = " AND ";
        }

        if (logradouroCadastroImobiliario != null) {
            filtro.append("Logradouro: ").append(logradouroCadastroImobiliario.getNome()).append("; ");
            where.append(juncao);
            where.append(" rel.logradouro like '" + logradouroCadastroImobiliario.getNome() + "'");
            juncao = " AND ";
        }
        return juncao;
    }

    private String filtrarEnderecoCadastroEconomico(String juncao) {
        if (bairroCadastroEconomico != null) {
            filtro.append("Bairro: ").append(bairroCadastroEconomico.getDescricao()).append("; ");
            where.append(juncao);
            where.append(" rel.bairro like '" + bairroCadastroEconomico.getDescricao() + "'");
            juncao = " AND ";
        }

        if (logradouroCadastroEconomico != null) {
            filtro.append("Logradouro: ").append(logradouroCadastroEconomico.getNome()).append("; ");
            where.append(juncao);
            where.append(" rel.logradouro like '" + logradouroCadastroEconomico.getNome() + "'");
            juncao = " AND ";
        }
        return juncao;
    }

    private void filtrarDividas(String juncao) {
        if (dividasSelecionadas != null && dividasSelecionadas.size() > 0) {
            String divida_ids = " (";
            String divida_descricoes = " Dívidas: ";
            for (Divida d : dividasSelecionadas) {
                divida_ids += d.getId();
                divida_ids += ", ";

                divida_descricoes += d.getDescricao();
                divida_descricoes += ", ";
            }
            divida_ids = divida_ids.substring(1, divida_ids.length() - 2);
            divida_ids += ")";
            divida_descricoes = divida_descricoes.substring(1, divida_descricoes.length() - 2);
            divida_descricoes += "; ";

            where.append(juncao);
            where.append(" divida_id in ").append(divida_ids);

            filtro.append(divida_descricoes);
        }
    }

    private String filtrarDataPagamento(String juncao) {
        if (dataPagamentoInicial != null) {
            where.append(juncao);
            where.append(" rel.dataPagamento >= to_date('").append(formatterData.format(dataPagamentoInicial)).append("','dd/MM/yyyy') ");
            juncao = " AND ";

            filtro.append(" Data Pagamento Inicial: ").append(formatterData.format(dataPagamentoInicial)).append("; ");
        }

        if (dataPagamentoFinal != null) {
            where.append(juncao);
            where.append(" rel.dataPagamento <= to_date('").append(formatterData.format(dataPagamentoFinal)).append("','dd/MM/yyyy') ");
            juncao = " AND ";

            filtro.append(" Data Pagamento Final: ").append(formatterData.format(dataPagamentoFinal)).append("; ");
        }
        return juncao;
    }

    private String filtrarTipoCadastroRural(String juncao) {
        if (tipoCadastroTributario.equals(TipoCadastroTributario.RURAL) && inscricaoCadastralRuralInicial != null && !inscricaoCadastralRuralInicial.isEmpty()) {
            where.append(juncao);
            where.append(" to_number(rel.inscricao) >= ").append(inscricaoCadastralRuralInicial).append(" ");
            juncao = " AND ";

            filtro.append(" Inscrição Cadastral Rural Inicial: ").append(inscricaoCadastralRuralInicial).append("; ");
        }

        if (tipoCadastroTributario.equals(TipoCadastroTributario.RURAL) && inscricaoCadastralRuralFinal != null && !inscricaoCadastralRuralFinal.isEmpty()) {
            where.append(juncao);
            where.append(" to_number(rel.inscricao) <= ").append(inscricaoCadastralRuralFinal).append(" ");
            juncao = " AND ";

            filtro.append(" Inscrição Cadastral Rural Final: ").append(inscricaoCadastralRuralFinal).append("; ");
        }
        return juncao;
    }

    private String filtrarTipoCadastroEconomico(String juncao) {
        if (tipoCadastroTributario.equals(TipoCadastroTributario.ECONOMICO) && inscricaoCadastralCmcInicial != null && !inscricaoCadastralCmcInicial.isEmpty()) {
            where.append(juncao);
            where.append(" to_number(rel.inscricao) >= ").append(inscricaoCadastralCmcInicial).append(" ");
            juncao = " AND ";

            filtro.append(" Inscrição Cadastral C.M.C Inicial: ").append(inscricaoCadastralCmcInicial).append("; ");
        }

        if (tipoCadastroTributario.equals(TipoCadastroTributario.ECONOMICO) && inscricaoCadastralCmcInicial != null && !inscricaoCadastralCmcFinal.isEmpty()) {
            where.append(juncao);
            where.append(" to_number(rel.inscricao) <= ").append(inscricaoCadastralCmcFinal).append(" ");
            juncao = " AND ";

            filtro.append(" Inscrição Cadastral C.M.C: ").append(inscricaoCadastralCmcFinal).append("; ");
        }
        return juncao;
    }

    private String filtrarTipoCadastroImobiliario(String juncao) {
        if (tipoCadastroTributario.equals(TipoCadastroTributario.IMOBILIARIO) && inscricaoCadastralImovelInicial != null && !inscricaoCadastralImovelInicial.isEmpty()) {
            where.append(juncao);
            where.append(" to_number(replace(replace(replace(replace(REGEXP_REPLACE(rel.inscricao, '[A-Za-z]', '0'), '-', ''), '.', ''), '/', ''), ' ', '')) >= ").append(inscricaoCadastralImovelInicial).append(" ");
            juncao = " AND ";

            filtro.append(" Inscrição Cadastral Imovel Inicial: ").append(inscricaoCadastralImovelInicial).append("; ");
        }

        if (tipoCadastroTributario.equals(TipoCadastroTributario.IMOBILIARIO) && inscricaoCadastralImovelFinal != null && !inscricaoCadastralImovelFinal.isEmpty()) {
            where.append(juncao);
            where.append(" to_number(replace(replace(replace(replace(REGEXP_REPLACE(rel.inscricao, '[A-Za-z]', '0'), '-', ''), '.', ''), '/', ''), ' ', '')) <= ").append(inscricaoCadastralImovelFinal).append(" ");
            juncao = " AND ";

            filtro.append(" Inscrição Cadastral Imovel Final: ").append(inscricaoCadastralImovelFinal).append("; ");
        }
        return juncao;
    }

    private String filtrarTipoCadastroPessoa(String juncao) {
        if (tipoCadastroTributario.equals(TipoCadastroTributario.PESSOA) && contribuinteInicial != null && !contribuinteInicial.isEmpty()) {
            where.append(juncao);
            where.append(" upper(rel.nomerazao) >= '").append(contribuinteInicial.toUpperCase()).append("' ");
            juncao = " AND ";

            filtro.append(" Contribuinte Inicial: ").append(contribuinteInicial).append("; ");
        }

        if (tipoCadastroTributario.equals(TipoCadastroTributario.PESSOA) && contribuinteInicial != null && !contribuinteInicial.isEmpty()) {
            where.append(juncao);
            where.append(" upper(rel.nomerazao) <= '").append(contribuinteInicial.toUpperCase()).append("' ");
            juncao = " AND ";

            filtro.append(" Contribuinte Final: ").append(contribuinteFinal).append("; ");
        }

        if (!tipoCadastroTributario.equals(TipoCadastroTributario.PESSOA)) {
            where.append(juncao);
            where.append(" rel.tipocadastro = '").append(tipoCadastroTributario.name()).append("' ");
            juncao = " AND ";
        }
        return juncao;
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (tipoCadastroTributario == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Tipo de Cadastro deve ser preenchido!");
        }
        if (dataPagamentoInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A Data de Pagamento Inicial deve ser preenchida!");
        }
        if (dataPagamentoFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A Data de Pagamento Final deve ser preenchida!");
        }
        if ((dataPagamentoInicial != null && dataPagamentoFinal != null) && dataPagamentoFinal.compareTo(dataPagamentoInicial) < 0) {
            ve.adicionarMensagemDeCampoObrigatorio("A Data de Pagamento Final não pode ser menor que a Data de Pagamento Inicial!");
        }
        if (dataLancamentoInicial != null) {
            validarDatas(dataLancamentoInicial, dataLancamentoFinal, ve, "Lançamento");
        }
        if (dataVencimentoInicial != null) {
            validarDatas(dataVencimentoInicial, dataVencimentoFinal, ve, "Vencimento");
        }
        if (dataMovimentoInicial != null) {
            validarDatas(dataMovimentoInicial, dataMovimentoFinal, ve, "Movimento");
        }
        ve.lancarException();
    }


    public void validarDatas(Date inicio, Date fim, ValidacaoException ve, String campo) {
        if (inicio == null && fim != null) {
            ve.adicionarMensagemDeCampoObrigatorio("A Data de Inicio de " + campo + " deve ser preenchida!");
        }
        if (inicio != null && fim == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A Data de Final de " + campo + " deve ser preenchida!");
        }
        if (inicio != null && fim != null) {
            if (DataUtil.verificaDataFinalMaiorQueDataInicial(inicio, fim)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Inicio de " + campo + " deve ser anterior a Data de Fim do " + campo + "");
            }
        }
    }

    public List<SelectItem> getTiposDeCadastroTributario() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));
        for (TipoCadastroTributario tipo : TipoCadastroTributario.values()) {
            lista.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return lista;
    }

    public List<Divida> recuperaDividas(String parte) {
        if (tipoCadastroTributario != null) {
            return dividaFacade.listaDividasPorTipoCadastro(parte.trim(), tipoCadastroTributario);
        }
        FacesUtil.addAtencao("Para selecionar Dívidas é preciso informar o Tipo de Cadastro!");
        return null;
    }

    public void gerar() {
        try {
            validarCampos();
            adicionarDividaSelecionadoAoGerar();
            montarFiltros();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("condicao", where);
            dto.adicionarParametro("FILTROS", filtro);
            dto.adicionarParametro("TIPOCADASTRO", tipoCadastroTributario.name());
            dto.adicionarParametro("DATAOPERACAO", DataUtil.getDataFormatada(dataOperacao));
            dto.adicionarParametro("MODULO", "Tributário");
            dto.setNomeRelatorio("Relatório de Arrecadação Geral");
            dto.setApi("tributario/arrecadacao/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public StringBuilder getWhere() {
        return where;
    }

    public void setWhere(StringBuilder where) {
        this.where = where;
    }

    public StringBuilder getFiltro() {
        return filtro;
    }

    public void setFiltro(StringBuilder filtro) {
        this.filtro = filtro;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    public void setDividaFacade(DividaFacade dividaFacade) {
        this.dividaFacade = dividaFacade;
    }

    public String getContribuinteInicial() {
        return contribuinteInicial;
    }

    public void setContribuinteInicial(String contribuinteInicial) {
        this.contribuinteInicial = contribuinteInicial;
    }

    public String getContribuinteFinal() {
        return contribuinteFinal;
    }

    public void setContribuinteFinal(String contribuinteFinal) {
        this.contribuinteFinal = contribuinteFinal;
    }

    public String getInscricaoCadastralImovelInicial() {
        return inscricaoCadastralImovelInicial;
    }

    public void setInscricaoCadastralImovelInicial(String inscricaoCadastralImovelInicial) {
        this.inscricaoCadastralImovelInicial = inscricaoCadastralImovelInicial;
    }

    public String getInscricaoCadastralImovelFinal() {
        return inscricaoCadastralImovelFinal;
    }

    public void setInscricaoCadastralImovelFinal(String inscricaoCadastralImovelFinal) {
        this.inscricaoCadastralImovelFinal = inscricaoCadastralImovelFinal;
    }

    public String getInscricaoCadastralCmcInicial() {
        return inscricaoCadastralCmcInicial;
    }

    public void setInscricaoCadastralCmcInicial(String inscricaoCadastralCmcInicial) {
        this.inscricaoCadastralCmcInicial = inscricaoCadastralCmcInicial;
    }

    public String getInscricaoCadastralCmcFinal() {
        return inscricaoCadastralCmcFinal;
    }

    public void setInscricaoCadastralCmcFinal(String inscricaoCadastralCmcFinal) {
        this.inscricaoCadastralCmcFinal = inscricaoCadastralCmcFinal;
    }

    public String getInscricaoCadastralRuralInicial() {
        return inscricaoCadastralRuralInicial;
    }

    public void setInscricaoCadastralRuralInicial(String inscricaoCadastralRuralInicial) {
        this.inscricaoCadastralRuralInicial = inscricaoCadastralRuralInicial;
    }

    public String getInscricaoCadastralRuralFinal() {
        return inscricaoCadastralRuralFinal;
    }

    public void setInscricaoCadastralRuralFinal(String inscricaoCadastralRuralFinal) {
        this.inscricaoCadastralRuralFinal = inscricaoCadastralRuralFinal;
    }

    public Date getDataPagamentoInicial() {
        return dataPagamentoInicial;
    }

    public void setDataPagamentoInicial(Date dataPagamentoInicial) {
        this.dataPagamentoInicial = dataPagamentoInicial;
    }

    public Date getDataPagamentoFinal() {
        return dataPagamentoFinal;
    }

    public void setDataPagamentoFinal(Date dataPagamentoFinal) {
        this.dataPagamentoFinal = dataPagamentoFinal;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public List<Divida> getDividasSelecionadas() {
        return dividasSelecionadas;
    }

    public void setDividasSelecionadas(List<Divida> dividasSelecionadas) {
        this.dividasSelecionadas = dividasSelecionadas;
    }

    public List<SelectItem> getTiposImoveis() {
        List<SelectItem> tipos = new ArrayList<>();
        tipos.add(new SelectItem(null, ""));
        tipos.add(new SelectItem("PREDIAL", "Predial"));
        tipos.add(new SelectItem("TERRITORIAL", "Territorial"));
        return tipos;
    }

    public List<Bairro> buscarBairros(String partes) {
        return bairroFacade.completaBairro(partes);
    }

    public List<Logradouro> buscarLogradourosImobiliario(String partes) {
        if (bairroCadastroImobiliario != null) {
            return logradouroFacade.completaLogradourosPorBairro(partes.trim(), bairroCadastroImobiliario);
        } else {
            FacesUtil.addCampoObrigatorio("Para buscar o Logradouro é necessário selecionar o Bairro.");
        }
        return new ArrayList<>();
    }

    public List<Logradouro> buscarLogradourosEconomico(String partes) {
        if (bairroCadastroEconomico != null) {
            return logradouroFacade.completaLogradourosPorBairro(partes.trim(), bairroCadastroEconomico);
        } else {
            FacesUtil.addCampoObrigatorio("Para buscar o Logradouro é necessário selecionar o Bairro.");
        }
        return new ArrayList<>();
    }

    public List<SelectItem> getClassificoesAtividades() {
        List<SelectItem> classificacoes = new ArrayList<>();
        classificacoes.add(new SelectItem(null, ""));
        for (ClassificacaoAtividade classificacaoAtividade : ClassificacaoAtividade.values()) {
            classificacoes.add(new SelectItem(classificacaoAtividade, classificacaoAtividade.getDescricao()));
        }
        return classificacoes;
    }

    public List<SelectItem> getNaturezasJuridicas() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (NaturezaJuridica naturezaJuridica : naturezaJuridicaFacade.lista()) {
            toReturn.add(new SelectItem(naturezaJuridica, naturezaJuridica.getCodigo() + " - " + naturezaJuridica.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getGrausDeRisco() {
        List<SelectItem> niveis = new ArrayList<>();
        niveis.add(new SelectItem(null, ""));
        for (GrauDeRiscoDTO grauDeRisco : GrauDeRiscoDTO.values()) {
            niveis.add(new SelectItem(grauDeRisco, grauDeRisco.getDescricao()));
        }
        return niveis;
    }

    public List<SelectItem> getTiposAutonomos() {
        List<SelectItem> toReturn = new ArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAutonomo tipo : tipoAutonomoFacade.lista()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public void removerDivida(Divida divida) {
        if (dividasSelecionadas.contains(divida)) {
            dividasSelecionadas.remove(divida);
        }
    }

    public void adicionarDivida() {
        try {
            validarDivida();
            dividasSelecionadas.add(divida);
            divida = new Divida();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void adicionarDividaSelecionadoAoGerar() {
        if (divida != null) {
            dividasSelecionadas.add(divida);
            divida = new Divida();
        }
    }

    private void validarDivida() {
        ValidacaoException ve = new ValidacaoException();
        if (divida == null || divida.getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione uma dívida para adicionar");
        } else if (dividasSelecionadas.contains(divida)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Essa Dívida já foi selecionada.");
        }
        ve.lancarException();
    }

    public List<SelectItem> getDividas() {
        List<SelectItem> toReturn = new ArrayList<>();
        if (tipoCadastroTributario != null) {
            List<Divida> dividas = tipoCadastroTributario.equals(TipoCadastroTributario.PESSOA) ? dividaFacade.lista() : dividaFacade.listaDividasPorTipoCadastro(tipoCadastroTributario);
            for (Divida divida : dividas) {
                toReturn.add(new SelectItem(divida, divida.getDescricao()));
            }
        }
        return toReturn;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public String getQuadra() {
        return quadra;
    }

    public void setQuadra(String quadra) {
        this.quadra = quadra;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public Bairro getBairroCadastroImobiliario() {
        return bairroCadastroImobiliario;
    }

    public void setBairroCadastroImobiliario(Bairro bairroCadastroImobiliario) {
        this.bairroCadastroImobiliario = bairroCadastroImobiliario;
        this.logradouroCadastroImobiliario = null;
    }

    public Bairro getBairroCadastroEconomico() {
        return bairroCadastroEconomico;
    }

    public void setBairroCadastroEconomico(Bairro bairroCadastroEconomico) {
        this.bairroCadastroEconomico = bairroCadastroEconomico;
        this.logradouroCadastroEconomico = null;
    }

    public Logradouro getLogradouroCadastroImobiliario() {
        return logradouroCadastroImobiliario;
    }

    public void setLogradouroCadastroImobiliario(Logradouro logradouroCadastroImobiliario) {
        this.logradouroCadastroImobiliario = logradouroCadastroImobiliario;
    }

    public Logradouro getLogradouroCadastroEconomico() {
        return logradouroCadastroEconomico;
    }

    public void setLogradouroCadastroEconomico(Logradouro logradouroCadastroEconomico) {
        this.logradouroCadastroEconomico = logradouroCadastroEconomico;
    }

    public String getTipoImovel() {
        return tipoImovel;
    }

    public void setTipoImovel(String tipoImovel) {
        this.tipoImovel = tipoImovel;
    }

    public ClassificacaoAtividade getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(ClassificacaoAtividade classificacao) {
        this.classificacao = classificacao;
    }

    public NaturezaJuridica getNaturezaJuridica() {
        return naturezaJuridica;
    }

    public void setNaturezaJuridica(NaturezaJuridica naturezaJuridica) {
        this.naturezaJuridica = naturezaJuridica;
    }

    public GrauDeRiscoDTO getGrauDeRisco() {
        return grauDeRisco;
    }

    public void setGrauDeRisco(GrauDeRiscoDTO grauDeRisco) {
        this.grauDeRisco = grauDeRisco;
    }

    public TipoAutonomo getTipoAutonomo() {
        return tipoAutonomo;
    }

    public void setTipoAutonomo(TipoAutonomo tipoAutonomo) {
        this.tipoAutonomo = tipoAutonomo;
    }

    public Date getDataLancamentoInicial() {
        return dataLancamentoInicial;
    }

    public void setDataLancamentoInicial(Date dataLancamentoInicial) {
        this.dataLancamentoInicial = dataLancamentoInicial;
    }

    public Date getDataLancamentoFinal() {
        return dataLancamentoFinal;
    }

    public void setDataLancamentoFinal(Date dataLancamentoFinal) {
        this.dataLancamentoFinal = dataLancamentoFinal;
    }

    public Date getDataVencimentoInicial() {
        return dataVencimentoInicial;
    }

    public void setDataVencimentoInicial(Date dataVencimentoInicial) {
        this.dataVencimentoInicial = dataVencimentoInicial;
    }

    public Date getDataVencimentoFinal() {
        return dataVencimentoFinal;
    }

    public void setDataVencimentoFinal(Date dataVencimentoFinal) {
        this.dataVencimentoFinal = dataVencimentoFinal;
    }

    public Date getDataMovimentoInicial() {
        return dataMovimentoInicial;
    }

    public void setDataMovimentoInicial(Date dataMovimentoInicial) {
        this.dataMovimentoInicial = dataMovimentoInicial;
    }

    public Date getDataMovimentoFinal() {
        return dataMovimentoFinal;
    }

    public void setDataMovimentoFinal(Date dataMovimentoFinal) {
        this.dataMovimentoFinal = dataMovimentoFinal;
    }

    public String getNumeroLoteArrecadacao() {
        return numeroLoteArrecadacao;
    }

    public void setNumeroLoteArrecadacao(String numeroLoteArrecadacao) {
        this.numeroLoteArrecadacao = numeroLoteArrecadacao;
    }

    public BancoContaConfTributario[] getContasSelecionadas() {
        return contasSelecionadas;
    }

    public void setContasSelecionadas(BancoContaConfTributario[] contasSelecionadas) {
        this.contasSelecionadas = contasSelecionadas;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public Exercicio getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Exercicio exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Exercicio getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Exercicio exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }
}
