package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.LivroDividaAtiva.TipoOrdenacao;
import br.com.webpublico.entidadesauxiliares.AssistenteCancelamentoDA;
import br.com.webpublico.entidadesauxiliares.ImprimeDemonstrativoDebitos;
import br.com.webpublico.enums.SituacaoInscricaoDividaAtiva;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.enums.TipoVencimentoParcela;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.InscricaoDividaAtivaFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.tributario.SingletonConcorrenciaParcela;
import br.com.webpublico.negocios.tributario.auxiliares.AssistenteDividaAtiva;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Renato
 */

@ManagedBean(name = "inscricaoDividaAtivaControlador")
@SessionScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarInscricaoDA", pattern = "/inscricao-em-divida-ativa/listar/", viewId = "/faces/tributario/dividaativa/inscricao/lista.xhtml"),
    @URLMapping(id = "editarInscricaoDA", pattern = "/inscricao-em-divida-ativa/editar/#{inscricaoDividaAtivaControlador.id}/", viewId = "/faces/tributario/dividaativa/inscricao/edita.xhtml"),
    @URLMapping(id = "inscreveDA", pattern = "/inscricao-em-divida-ativa/inscrevendo/", viewId = "/faces/tributario/dividaativa/inscricao/log.xhtml"),
    @URLMapping(id = "cancelaDA", pattern = "/inscricao-em-divida-ativa/cancelando/", viewId = "/faces/tributario/dividaativa/inscricao/logcancelamento.xhtml"),
    @URLMapping(id = "verInscricaoDA", pattern = "/inscricao-em-divida-ativa/ver/#{inscricaoDividaAtivaControlador.id}/", viewId = "/faces/tributario/dividaativa/inscricao/visualizar.xhtml"),
    @URLMapping(id = "novaIncricaoDA", pattern = "/inscricao-em-divida-ativa/novo/", viewId = "/faces/tributario/dividaativa/inscricao/edita.xhtml")
})
public class InscricaoDividaAtivaControlador extends PrettyControlador<InscricaoDividaAtiva> implements Serializable, CRUD {

    @EJB
    private InscricaoDividaAtivaFacade inscricaoDividaAtivaFacade;
    @EJB
    private SingletonConcorrenciaParcela concorrenciaParcela;
    private ConverterExercicio converterExercicio;
    private ConverterAutoComplete converterDivida;
    private List<ItemInscricaoDividaAtiva> itensExcluidos = new ArrayList<>();
    private boolean selecionouSim;
    private CancelamentoInscricaoDA cancelamentoInscricaoDA;
    private LivroDividaAtiva.TipoOrdenacao tipoOrdenacao;
    private Pessoa contribuinte;
    private boolean consultou;
    private AssistenteDividaAtiva assistente;
    private AssistenteCancelamentoDA assistenteCancelamento;
    private Future<AssistenteCancelamentoDA> future;
    private ItemInscricaoDividaAtiva itemInscricaoDividaAtivaVisualizar;
    private List<ResultadoParcela> parcelasOriginais;
    private List<ResultadoParcela> parcelasOriginadas;
    private List<Divida> dividas;
    private TipoCadastroTributario tipoCadastroTributario;

    public InscricaoDividaAtivaControlador() {
        super(InscricaoDividaAtiva.class);
    }

    public boolean isConsultou() {
        return consultou;
    }

    @Override
    public AbstractFacade getFacede() {
        return inscricaoDividaAtivaFacade;
    }

    public Pessoa getContribuinte() {
        return contribuinte;
    }

    public void setContribuinte(Pessoa contribuinte) {
        this.contribuinte = contribuinte;
    }

    public CancelamentoInscricaoDA getCancelamentoInscricaoDA() {
        return cancelamentoInscricaoDA;
    }

    public void setCancelamentoInscricaoDA(CancelamentoInscricaoDA cancelamentoInscricaoDA) {
        this.cancelamentoInscricaoDA = cancelamentoInscricaoDA;
    }

    public boolean isSelecionouSim() {
        return selecionouSim;
    }

    public void setSelecionouSim(boolean selecionouSim) {
        this.selecionouSim = selecionouSim;
    }

    public TipoOrdenacao getTipoOrdenacao() {
        return tipoOrdenacao;
    }

    public void setTipoOrdenacao(TipoOrdenacao tipoOrdenacao) {
        this.tipoOrdenacao = tipoOrdenacao;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/inscricao-em-divida-ativa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void redireciona() {
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }

    @URLAction(mappingId = "novaIncricaoDA", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        contribuinte = null;
        selecionado.setExercicio(null);
        selecionado.setCadastroFinal(null);
        selecionado.setCadastroInicial(null);
        selecionado.setDataInscricao(null);
        selecionado.setVencimentoFinal(DataUtil.getDataDiaAnterior(new Date()));
        selecionado.setVencimentoInicial(null);
        selecionado.setItensInscricaoDividaAtivas(null);
        selecionado.setDataInscricao(new Date());
        selecionado.setDividaAtivaDividas(new ArrayList<DividaAtivaDivida>());
        selecionado.setSituacaoInscricaoDividaAtiva(SituacaoInscricaoDividaAtiva.ABERTO);
        selecionado.setNumero(inscricaoDividaAtivaFacade.recuperaProximoNumeroInscricaoDividaAtiva());
        selecionado.setTipoVencimentoParcela(TipoVencimentoParcela.PRIMEIRO_VENCIMENTO);
        selecionado.setAgruparParcelas(true);
        this.tipoOrdenacao = null;
        itensExcluidos = new ArrayList<>();
        selecionado.setCadastroInicial("1");
        selecionado.setCadastroFinal("999999999999999");
        assistente = new AssistenteDividaAtiva();
        assistente.inicializaSingleton();
        consultou = false;
    }

    public List<Exercicio> completaExercicio(String parte) {
        return inscricaoDividaAtivaFacade.completaExercicio(parte.trim());
    }

    public ConverterExercicio getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterExercicio(inscricaoDividaAtivaFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public List<SelectItem> getTiposDeCadastroTributario() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (TipoCadastroTributario tipo : TipoCadastroTributario.values()) {
            lista.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return lista;
    }

    public List<SelectItem> getTipoOrdenacaoEnum() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (LivroDividaAtiva.TipoOrdenacao tipo : LivroDividaAtiva.TipoOrdenacao.values()) {
            lista.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return lista;
    }

    public ConverterAutoComplete getConverterDivida() {
        if (converterDivida == null) {
            converterDivida = new ConverterAutoComplete(Divida.class, inscricaoDividaAtivaFacade.getDividaFacade());
        }
        return converterDivida;
    }

    public List<Divida> completaDivida(String parte) {
        return inscricaoDividaAtivaFacade.getDividaFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public void limpaCadastro() {
        selecionado.setCadastroInicial("1");
        selecionado.setCadastroFinal("999999999999999999");
        selecionado.setItensInscricaoDividaAtivas(null);
    }

    public void pesquisarParcelas() {
        try {
            selecionado.setItensInscricaoDividaAtivas(new ArrayList<ItemInscricaoDividaAtiva>());
            assistente.setTipoCadastroTributario(selecionado.getTipoCadastroTributario());
            validaCamposPreenchidos();
            FacesUtil.executaJavaScript("iniciaPesquisa()");
            assistente.iniciaConsulta();
            adicionarParametro();
            adicionarJoin(selecionado);
            adicionarCamposAdicionais(selecionado);
            inscricaoDividaAtivaFacade.consultarParcelas(assistente, selecionado);
            FacesUtil.executaJavaScript("dialogConsultando.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void adicionarCamposAdicionais(InscricaoDividaAtiva selecionado) {
        if (TipoCadastroTributario.IMOBILIARIO.equals(selecionado.getTipoCadastroTributario())) {
            assistente.addCamposAdicionadis(" coalesce(ci.inscricaocadastral, pf.cpf, pj.cnpj) as cadastro ");
        } else if (TipoCadastroTributario.ECONOMICO.equals(selecionado.getTipoCadastroTributario())) {
            assistente.addCamposAdicionadis(" coalesce(ce.inscricaocadastral, pf.cpf, pj.cnpj) as cadastro ");
        } else if (TipoCadastroTributario.RURAL.equals(selecionado.getTipoCadastroTributario())) {
            assistente.addCamposAdicionadis(" coalesce(to_char(cr.codigo), pf.cpf, pj.cnpj) as cadastro ");
        } else {
            assistente.addCamposAdicionadis(" coalesce(pf.cpf, pj.cnpj) as cadastro ");
        }
    }

    private void adicionarJoin(InscricaoDividaAtiva selecionado) {
        if (TipoCadastroTributario.IMOBILIARIO.equals(selecionado.getTipoCadastroTributario())) {
            assistente.addJoin("inner join cadastro cad on cad.id = calc.cadastro_id ");
            assistente.addJoin("inner join cadastroimobiliario ci on ci.id = cad.id ");
        }
        if (TipoCadastroTributario.ECONOMICO.equals(selecionado.getTipoCadastroTributario())) {
            assistente.addJoin("inner join cadastro cad on cad.id = calc.cadastro_id ");
            assistente.addJoin("inner join cadastroeconomico ce on ce.id = cad.id ");
        }
        if (TipoCadastroTributario.RURAL.equals(selecionado.getTipoCadastroTributario())) {
            assistente.addJoin("inner join cadastro cad on cad.id = calc.cadastro_id ");
            assistente.addJoin("inner join cadastrorural cr on cr.id = cad.id ");
        }
    }

    private void adicionarParametro() {
        if (TipoCadastroTributario.PESSOA.equals(selecionado.getTipoCadastroTributario())
            && selecionado.getContribuinte() != null) {
            assistente.addWhere(" and pes.id = :idPessoa");
        }
        if (TipoCadastroTributario.IMOBILIARIO.equals(selecionado.getTipoCadastroTributario())) {
            assistente.addWhere(" and ci.inscricaoCadastral >= :inscricaoInicial");
            assistente.addWhere(" and ci.inscricaoCadastral <= :inscricaoFinal");
        }
        if (TipoCadastroTributario.ECONOMICO.equals(selecionado.getTipoCadastroTributario())) {
            assistente.addWhere(" and ce.inscricaoCadastral >= :inscricaoInicial");
            assistente.addWhere(" and ce.inscricaoCadastral <= :inscricaoFinal");
        }
        if (TipoCadastroTributario.RURAL.equals(selecionado.getTipoCadastroTributario())) {
            assistente.addWhere(" and cr.codigo >= :inscricaoInicial");
            assistente.addWhere(" and cr.codigo <= :inscricaoFinal");
        }
        if (selecionado.getExercicio() != null) {
            assistente.addWhere(" and ex.ano >= :exercicioInicial");
        }
        if (selecionado.getExercicioFinal() != null) {
            assistente.addWhere(" and ex.ano <= :exercicioFinal");
        }
        if (selecionado.getVencimentoInicial() != null) {
            assistente.addWhere(" and pvd.vencimento >= :vencimentoInicial");
        }
        if (selecionado.getVencimentoFinal() != null) {
            assistente.addWhere(" and pvd.vencimento <= :vencimentoFinal");
        }
        if (!selecionado.getDividaAtivaDividas().isEmpty()) {
            assistente.addWhere(" and div.id in (:listaIdDivida)");
        }
    }

    public void finalizaConsulta() {
        consultou = true;
    }

    public void emitirParcelas() {
        try {
            LinkedList<ResultadoParcela> demonstrativo = Lists.newLinkedList();
            for (ResultadoParcela resultadoParcela : assistente.getParcelas()) {
                resultadoParcela.setDebitoProtestado(inscricaoDividaAtivaFacade.buscarProcessoAtivoDaParcela(
                    resultadoParcela.getIdParcela()) == null ? Boolean.FALSE : Boolean.TRUE);
                demonstrativo.add(resultadoParcela);
            }

            AbstractReport report = new AbstractReport();
            ImprimeDemonstrativoDebitos imprime = new ImprimeDemonstrativoDebitos("RelatorioConsultaDebitos.jasper", demonstrativo);
            imprime.adicionarParametro("BRASAO", report.getCaminhoImagem());
            imprime.adicionarParametro("USUARIO", SistemaFacade.obtemLogin());
            imprime.adicionarParametro("FILTROS", selecionado.getFiltosUtilizados());
            imprime.adicionarParametro("SUBREPORT_DIR", report.getCaminho());
            imprime.adicionarParametro("TITULO_RELATORIO", "Débitos da Inscrição em Dívida Ativa");

            imprime.imprimeRelatorio();
        } catch (IOException | JRException e) {
            logger.error("Erro ao emitir as parcelas da inscrição em dívida ativa: {}", e);
        }
    }

    public int getTamanhoLista() {
        return assistente.getParcelas().size();
    }

    private void validaCamposPreenchidos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoCadastroTributario() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Tipo de Cadastro.");
        }
        if ((selecionado.getCadastroInicial() == null || selecionado.getCadastroInicial().trim().isEmpty()) && (selecionado.getCadastroFinal() == null || selecionado.getCadastroFinal().trim().isEmpty())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Cadastro Inicial ou Final.");
        }
        if (selecionado.getExercicio() == null || selecionado.getExercicio().getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o exercício inicial.");
        }
        if (selecionado.getExercicioFinal() == null || selecionado.getExercicioFinal().getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o exercício final.");
        }
        if (selecionado.getExercicio() != null && selecionado.getExercicioFinal() != null) {
            if (selecionado.getExercicio().getAno() > selecionado.getExercicioFinal().getAno()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Exercício Final deve ser menor que o exercício inicial!");
            }
        }
        if (selecionado.getVencimentoFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione o vencimento final.");
        } else if (DataUtil.dataSemHorario(selecionado.getVencimentoFinal()).compareTo(DataUtil.dataSemHorario(getSistemaControlador().getDataOperacao())) >= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Vencimento Final deve ser menor que a data atual!");
        }
        if (selecionado.getDividaAtivaDividas().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione pelo menos uma dívida para consulta.");
        }
        for (DividaAtivaDivida dividaAtivaDivida : selecionado.getDividaAtivaDividas()) {
            if (dividaAtivaDivida.getDivida().equals(selecionado.getDivida())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido que a dívida de destino seja igual a alguma das dívidas de consulta. Divida de Destino.: " + selecionado.getDivida().getDescricao());
                break;
            }
        }
        ve.lancarException();
    }

    public boolean desabilidaBotaoDeRemover() {
        if (selecionado.getTipoCadastroTributario() == null) {
            return false;
        }
        if ((selecionado.getCadastroInicial() == null || selecionado.getCadastroInicial().trim().isEmpty()) && (selecionado.getCadastroFinal() == null || selecionado.getCadastroFinal().trim().isEmpty())) {
            return false;
        }
        return true;
    }

    public String retonarInscricaoDoCadastro(Cadastro cadastro) {
        if (cadastro instanceof CadastroEconomico) {
            return ((CadastroEconomico) cadastro).getInscricaoCadastral();
        }
        if (cadastro instanceof CadastroImobiliario) {
            return ((CadastroImobiliario) cadastro).getInscricaoCadastral();
        }
        if (cadastro instanceof CadastroRural) {
            return ((CadastroRural) cadastro).getNumeroCadastro();
        }
        return "-";
    }

    public String retonarTipoCadastro(Cadastro cadastro) {
        if (cadastro instanceof CadastroEconomico) {
            return " (Cadastro Econômico)";
        }
        if (cadastro instanceof CadastroImobiliario) {
            return " (Cadastro Imobiliário)";
        }
        if (cadastro instanceof CadastroRural) {
            return " (Cadastro Rural)";
        }
        return "";
    }

//    public String retornarNomeRazaoDoCadastroImobiliario(CadastroImobiliario cadastro) {
//        String retorno = "Pessoa não encontrada";
//        cadastro = inscricaoDividaAtivaFacade.getCadastroImobiliarioFacade().recuperar(cadastro.getId());
//        if (cadastro.getPropriedade() != null && cadastro.getPropriedade().size() > 0) {
//            retorno = cadastro.getPropriedade().get(0).getPessoa().getNomeCpfCnpj();
//        }
//        return retorno;
//    }
//
//    public String retornarNomeRazaoDoCadastroEconomico(CadastroEconomico cadastro) {
//        String retorno = "Pessoa não encontrada";
//        if (cadastro.getPessoa() != null) {
//            retorno = cadastro.getPessoa().getNomeCpfCnpj();
//        }
//        return retorno;
//    }
//
//    public String retornarNomeRazaoDoCadastroRural(CadastroRural cadastro) {
//        String retorno = "Pessoa não encontrada";
//        cadastro = inscricaoDividaAtivaFacade.getCadastroRuralFacade().recuperar(cadastro.getId());
//        if (cadastro.getPropriedade() != null && cadastro.getPropriedade().size() > 0) {
//            retorno = cadastro.getPropriedade().get(0).getPessoa().getNomeCpfCnpj();
//        }
//        ;
//        return retorno;
//    }

    public BigDecimal retornaSaldoDaParcela(ParcelaValorDivida parcela) {
        return inscricaoDividaAtivaFacade.retornaSaldoDaParcela(parcela);
    }

//    public ItemInscricaoDividaAtiva retornarValorParaCadaItemCancelamento(ItemInscricaoDividaAtiva item) {
//        BigDecimal valor = BigDecimal.ZERO;
//        List<InscricaoDividaParcela> listaParcelas = inscricaoDividaAtivaFacade.listaInscricaoDividaParcela(item);
//        for (InscricaoDividaParcela inscricaoDividaParcela : listaParcelas) {
//            valor = valor.add(retornaSaldoDaParcela(inscricaoDividaParcela.getParcelaValorDivida()));
//        }
//        item.setValorReal(valor);
//        item.setValorEfetivo(valor);
//        return item;
//    }

    public ItemInscricaoDividaAtiva retornarValorParaCadaItem(ItemInscricaoDividaAtiva item) {
        return inscricaoDividaAtivaFacade.retornarValorParaCadaItem(item);
    }

    public BigDecimal retornarValorTotalGeral() {
        BigDecimal valor = BigDecimal.ZERO;
        if (selecionado == null || selecionado.getItensInscricaoDividaAtivas() == null || selecionado.getItensInscricaoDividaAtivas().isEmpty()) {
            return valor;
        }

        for (ItemInscricaoDividaAtiva item : selecionado.getItensInscricaoDividaAtivas()) {
            valor = valor.add(retornarValorParaCadaItem(item).getValorEfetivo());
        }
        return valor;
    }

    public String retornaUltimaSituacaoDaParcela(ParcelaValorDivida parcela) {
        SituacaoParcelaValorDivida ultimaSituacao = inscricaoDividaAtivaFacade.getConsultaDebitoFacade().getUltimaSituacao(parcela);
        return ultimaSituacao.getSituacaoParcela().getDescricao();
    }

    public List<Divida> buscarDividas() {
        if (selecionado != null && selecionado.getTipoCadastroTributario() != null) {
            dividas = inscricaoDividaAtivaFacade.buscarDividasPorTipoCadastroTributario(selecionado.getTipoCadastroTributario());
        }
        return new ArrayList<>();
    }

    public void adicionarDivida(Divida divida) {
        DividaAtivaDivida dividaAtivaDivida = new DividaAtivaDivida();
        dividaAtivaDivida.setDivida(divida);
        dividaAtivaDivida.setInscricaoDividaAtiva(selecionado);
        selecionado.getDividaAtivaDividas().add(dividaAtivaDivida);
    }

    public void adicionarTodasDividas() {
        if (dividas != null && !dividas.isEmpty()) {
            removerTodasDivida();
            for (Divida divida : dividas) {
                adicionarDivida(divida);
            }
        }
    }

    public void removerTodasDivida() {
        selecionado.getDividaAtivaDividas().clear();
    }

    public void removerDivida(Divida divida) {
        DividaAtivaDivida dividaParaRemover = null;
        for (DividaAtivaDivida dividaAtivaDivida : selecionado.getDividaAtivaDividas()) {
            if (dividaAtivaDivida.getDivida().equals(divida)) {
                dividaParaRemover = dividaAtivaDivida;
                break;
            }
        }
        if (dividaParaRemover != null) {
            selecionado.getDividaAtivaDividas().remove(dividaParaRemover);
        }
    }

    public boolean dividaEstaNoSelecionado(Divida divida) {
        for (DividaAtivaDivida dividaAtivaDivida : selecionado.getDividaAtivaDividas()) {
            if (dividaAtivaDivida.getDivida().equals(divida)) {
                return true;
            }
        }
        return false;
    }

    public boolean todasDividaEstaNoSelecionado() {
        if (selecionado.getDividaAtivaDividas().isEmpty()) {
            return false;
        }
        if (dividas != null && !dividas.isEmpty()) {
            for (Divida divida : dividas) {
                if (!hasDividaAdicionada(divida)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean hasDividaAdicionada(Divida divida) {
        for (DividaAtivaDivida dividaAtivaDivida : selecionado.getDividaAtivaDividas()) {
            if (dividaAtivaDivida.getDivida().equals(divida)) {
                return true;
            }
        }
        return false;
    }

    public void remover(ItemInscricaoDividaAtiva itemInscricaoDividaAtiva) {
        selecionado.getItensInscricaoDividaAtivas().remove(itemInscricaoDividaAtiva);
        itensExcluidos.add(itemInscricaoDividaAtiva);
    }

    public void removerTodos() {
        List<ItemInscricaoDividaAtiva> paraExlcuir = new ArrayList<>();
        for (ItemInscricaoDividaAtiva item : selecionado.getItensInscricaoDividaAtivas()) {
            paraExlcuir.add(item);
        }

        for (ItemInscricaoDividaAtiva itemInscricaoDividaAtiva : paraExlcuir) {
            remover(itemInscricaoDividaAtiva);
        }
    }

    public void reverterExlusao() {
        List<ItemInscricaoDividaAtiva> paraRemoverDosExlcuidos = new ArrayList<>();
        for (ItemInscricaoDividaAtiva itemInscricaoDividaAtiva : itensExcluidos) {
            paraRemoverDosExlcuidos.add(itemInscricaoDividaAtiva);
        }

        for (ItemInscricaoDividaAtiva itemInscricaoDividaAtiva : paraRemoverDosExlcuidos) {
            itensExcluidos.remove(itemInscricaoDividaAtiva);
            selecionado.getItensInscricaoDividaAtivas().add(itemInscricaoDividaAtiva);
        }
    }

    @Override
    public void salvar() {
        ResultadoValidacao resultado;
        resultado = inscricaoDividaAtivaFacade.validarProcesso(selecionado);
        if (resultado.isValidado()) {
            alteraSituacaoDosItens(selecionado);
            for (ItemInscricaoDividaAtiva item : selecionado.getItensInscricaoDividaAtivas()) {
                inscricaoDividaAtivaFacade.retornarValorParaCadaItem(item);
            }
            selecionado = inscricaoDividaAtivaFacade.salva(selecionado);
            FacesUtil.addInfo("Salvo com sucesso!", "");
            FacesUtil.printAllMessages(resultado.getMensagens());
        } else {
            FacesUtil.printAllMessages(resultado.getMensagens());
        }
    }

    public void processar(boolean inscreveLivro) {
        try {
            selecionado = inscricaoDividaAtivaFacade.processar(selecionado);
            inscricaoDividaAtivaFacade.getGeraDebito().geraValorDividaDaDividaAtiva(selecionado);

            if (inscreveLivro) {
                List<LivroDividaAtiva> livros = new ArrayList<>();
                //livros = inscricaoDividaAtivaFacade.getLivroDividaAtivaFacade().geraLivros(selecionado, selecionado.getExercicio(), selecionado.getTipoCadastroTributario(), null, livros);

                for (LivroDividaAtiva livro : livros) {
                    ResultadoValidacao reusltado = inscricaoDividaAtivaFacade.getLivroDividaAtivaFacade().processar(livro, tipoOrdenacao, null, null, null, null);
                    if (reusltado.isValidado()) {
                        FacesUtil.addInfo("Inscrição efetuada com sucesso no Livro " + livro.getNumero(), "");
                    } else {
                        FacesUtil.printAllMessages(reusltado.getMensagens());
                    }
                }
            }
        } catch (ExcecaoNegocioGenerica e) {
            FacesUtil.addError("Impossível continuar", e.getMessage());
        } catch (Exception ex) {
            FacesUtil.addError("Impossível continuar", "Ocorreu um erro ao processar a operação");
        }
    }

    public List<SelectItem> getTiposVencimentoDaParcela() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoVencimentoParcela tipo : TipoVencimentoParcela.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    @URLAction(mappingId = "verInscricaoDA", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        itemInscricaoDividaAtivaVisualizar = inscricaoDividaAtivaFacade.recuperarItemInscricaoDivida(getId());
        selecionado = inscricaoDividaAtivaFacade.recuperar(itemInscricaoDividaAtivaVisualizar.getInscricaoDividaAtiva().getId());
        itemInscricaoDividaAtivaVisualizar.setInscricaoDividaAtiva(selecionado);
        recuperarParcelasOriginais();
        recuperarParcelasOriginadas();
        selecionado = inscricaoDividaAtivaFacade.recuperar(itemInscricaoDividaAtivaVisualizar.getInscricaoDividaAtiva().getId());
    }

    private void recuperarParcelasOriginais() {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        parcelasOriginais = Lists.newArrayList();
        itemInscricaoDividaAtivaVisualizar.setItensInscricaoDividaParcelas(inscricaoDividaAtivaFacade.listaInscricaoDividaParcela(itemInscricaoDividaAtivaVisualizar));
        for (InscricaoDividaParcela inscricaoDividaParcela : itemInscricaoDividaAtivaVisualizar.getItensInscricaoDividaParcelas()) {
            consultaParcela.limpaConsulta();
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IGUAL, inscricaoDividaParcela.getParcelaValorDivida().getId());
            parcelasOriginais.addAll(consultaParcela.executaConsulta().getResultados());
        }
    }

    private void recuperarParcelasOriginadas() {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, itemInscricaoDividaAtivaVisualizar.getId());
        parcelasOriginadas = consultaParcela.executaConsulta().getResultados();
    }


    public boolean desabilitaBotaoAgruparParcelas() {
        if (selecionado.getTipoVencimentoParcela() == null) {
            return false;
        }
        if (selecionado.getTipoVencimentoParcela().equals(TipoVencimentoParcela.NAO_ALTERAR)) {
            selecionado.setAgruparParcelas(false);
            return true;
        }
        return false;
    }

    private void alteraSituacaoDosItens(InscricaoDividaAtiva esteSelecionado) {
        for (ItemInscricaoDividaAtiva itemInscricaoDividaAtiva : esteSelecionado.getItensInscricaoDividaAtivas()) {
            itemInscricaoDividaAtiva.setSituacao(ItemInscricaoDividaAtiva.Situacao.ATIVO);
        }
    }

    public BigDecimal retornaUFMParcela(ParcelaValorDivida parcelaValorDivida) {
        BigDecimal saldo = retornaSaldoDaParcela(parcelaValorDivida);
        BigDecimal valorUFMVigente = inscricaoDividaAtivaFacade.getMoedaFacade().recuperaValorUFMPorExercicio(selecionado.getExercicio().getAno());
        if (!valorUFMVigente.equals(BigDecimal.ZERO)) {
            BigDecimal retorno = saldo.divide(valorUFMVigente, 4, RoundingMode.HALF_EVEN);
            return retorno;
        } else {
            return BigDecimal.ZERO;
        }
    }

    public boolean podeEditar() {
        return selecionado.getSituacaoInscricaoDividaAtiva().equals(SituacaoInscricaoDividaAtiva.ABERTO);
    }

    public boolean podeExcluir() {
        return podeEditar();
    }

    @URLAction(mappingId = "editarInscricaoDA", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        if (selecionado.getTipoVencimentoParcela() == null) {
            selecionado.setTipoVencimentoParcela(TipoVencimentoParcela.NAO_ALTERAR);
        }
        if (!selecionado.getSituacaoInscricaoDividaAtiva().equals(SituacaoInscricaoDividaAtiva.ABERTO)) {
            FacesUtil.addInfo(selecionado.getSituacaoInscricaoDividaAtiva().getDescricao() + "!", "Não é permitido realizar alterações, pois o processo de inscrição está " + selecionado.getSituacaoInscricaoDividaAtiva() + ".");
        }
    }

    @URLAction(mappingId = "editarInscricaoDA", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void iniciaInscricao() {
        FacesUtil.executaJavaScript("iniciaPesquisa();");
    }

    public void inscreverDA() {
        //TODO
        try {
            verificarParcelasBloqueadas();
            assistente.inicializaIncricao();
            selecionado.setSituacaoInscricaoDividaAtiva(SituacaoInscricaoDividaAtiva.FINALIZADO);
            selecionado = inscricaoDividaAtivaFacade.salvaSemValidar(selecionado);
            assistente.setUsuario(getSistemaControlador().getUsuarioCorrente().getNome());
            assistente.setDescricao("Inscrevendo débitos em D.A");
            AsyncExecutor.getInstance().execute(assistente, () -> {
                inscricaoDividaAtivaFacade.inscreverDividaAtiva(selecionado, assistente);
                return null;
            });
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "inscrevendo/");
        } catch (ValidacaoException ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private void verificarParcelasBloqueadas() {
        for (ResultadoParcela item : assistente.getParcelas()) {
            if (concorrenciaParcela.isLocked(item.getId())) {
                throw new ValidacaoException("Existe(m) parcela(s) selecionada(s) que já está(ão) sendo processada(s).");
            }
        }
    }


    public AssistenteDividaAtiva getAssistente() {
        return assistente;
    }

    public void gerarLivroDA() {
        // gerar o livro
        assistente.inicializaGeracaoLivro(selecionado.getItensInscricaoDividaAtivas().size());
        inscricaoDividaAtivaFacade.gerarLivroDA(selecionado, assistente);
    }

    public void gerarCertidaoDividaAtiva() {
        FacesUtil.redirecionamentoInterno("/geracao-de-certidao-de-divida-ativa-inscricao/" + selecionado.getId() + "/");
    }

    public void navegaParaLivro() {
        FacesUtil.redirecionamentoInterno("/livro-de-divida-ativa/listar/");
    }

    public void voltar() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "listar/");
    }

    @Override
    public void cancelar() {
        if (validarCancelamentoInscricao()) {
            ArrayList<ItemInscricaoDividaAtiva> itens = Lists.newArrayList(itemInscricaoDividaAtivaVisualizar);
            assistenteCancelamento = new AssistenteCancelamentoDA(itens, getSistemaControlador().getUsuarioCorrente());
            future = inscricaoDividaAtivaFacade.cancelarInscricoesDividaAtiva(itens, assistenteCancelamento);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "cancelando/");
        }
    }

    private boolean validarCancelamentoInscricao() {
        boolean originais = true;
        boolean originadas = true;
        boolean podeCancelarAjuizadas = inscricaoDividaAtivaFacade.hasPermissaoParaCancelarAjuizada(getSistemaControlador().getUsuarioCorrente());
        for (ResultadoParcela parcela : parcelasOriginais) {
            if (!SituacaoParcela.INSCRITA_EM_DIVIDA_ATIVA.equals(parcela.getSituacaoEnumValue())) {
                FacesUtil.addError("Não é possível cancelar a inscrição!", "Existe(m) parcela(s) original(ais) com situação diferente de '" + SituacaoParcela.INSCRITA_EM_DIVIDA_ATIVA.getDescricao() + "'!");
                FacesUtil.executaJavaScript("aguarde.hide()");
                originais = false;
                break;
            }
        }
        for (ResultadoParcela parcela : parcelasOriginadas) {
            if (!SituacaoParcela.EM_ABERTO.equals(parcela.getSituacaoEnumValue())) {
                FacesUtil.addError("Não é possível cancelar a inscrição!", "Existe(m) parcela(s) originada(s) com situação diferente de '" + SituacaoParcela.INSCRITA_EM_DIVIDA_ATIVA.getDescricao() + "'!");
                FacesUtil.executaJavaScript("aguarde.hide()");
                originadas = false;
                break;
            }
            if (!podeCancelarAjuizadas) {
                if (parcela.getDividaAtivaAjuizada()) {
                    FacesUtil.addError("Não é possível cancelar a inscrição!", "Existe(m) parcela(s) originada(s) ajuizada(s)!");
                    FacesUtil.executaJavaScript("aguarde.hide()");
                    originadas = false;
                    break;
                }
            }
        }

        return originais && originadas;
    }

    public AssistenteCancelamentoDA getAssistenteCancelamento() {
        return assistenteCancelamento;
    }

    public void estaCancelando() {
        if (future != null && future.isDone()) {
            selecionado.setSituacaoInscricaoDividaAtiva(SituacaoInscricaoDividaAtiva.CANCELADO);
            selecionado = inscricaoDividaAtivaFacade.merge(selecionado);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + itemInscricaoDividaAtivaVisualizar.getId() + "/");
        }
    }

    public List<InscricaoDividaParcela> recuperarParcelasInscritas() {
        return inscricaoDividaAtivaFacade.recuperarParcelasInscritasPorInscricao(selecionado);
    }

    public ItemInscricaoDividaAtiva getItemInscricaoDividaAtivaVisualizar() {
        return itemInscricaoDividaAtivaVisualizar;
    }

    public void setItemInscricaoDividaAtivaVisualizar(ItemInscricaoDividaAtiva itemInscricaoDividaAtivaVisualizar) {
        this.itemInscricaoDividaAtivaVisualizar = itemInscricaoDividaAtivaVisualizar;
    }

    public List<ResultadoParcela> getParcelasOriginais() {
        return parcelasOriginais;
    }

    public List<ResultadoParcela> getParcelasOriginadas() {
        return parcelasOriginadas;
    }

    public List<Divida> getDividas() {
        return dividas;
    }

    public void setDividas(List<Divida> dividas) {
        this.dividas = dividas;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
        if (tipoCadastroTributario != null) {
            selecionado.setTipoCadastroTributario(tipoCadastroTributario);
            buscarDividas();
        }
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public void trocarTipoCadastro() {
        selecionado.setTipoCadastroTributario(null);
    }

    public List<SelectItem> getTiposCadastro() {
        return TipoCadastroTributario.asSelectItemList();
    }
}
