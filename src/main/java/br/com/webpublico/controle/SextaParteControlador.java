package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.rh.VwFalta;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.PropositoAtoLegal;
import br.com.webpublico.enums.TipoFalta;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.relatoriofacade.rh.RelatorioTempoDeServicoFacade;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 15/10/13
 * Time: 17:29
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novosextaparte", pattern = "/sextaparte/novo/", viewId = "/faces/rh/administracaodepagamento/sextaparte/edita.xhtml"),
    @URLMapping(id = "sextapartecorrecao", pattern = "/sextaparte/correcao/", viewId = "/faces/rh/administracaodepagamento/sextaparte/correcao.xhtml"),
    @URLMapping(id = "editarsextaparte", pattern = "/sextaparte/editar/#{sextaParteControlador.id}/", viewId = "/faces/rh/administracaodepagamento/sextaparte/edita.xhtml"),
    @URLMapping(id = "listarsextaparte", pattern = "/sextaparte/listar/", viewId = "/faces/rh/administracaodepagamento/sextaparte/lista.xhtml"),
    @URLMapping(id = "versextaparte", pattern = "/sextaparte/ver/#{sextaParteControlador.id}/", viewId = "/faces/rh/administracaodepagamento/sextaparte/visualizar.xhtml")
})
public class SextaParteControlador extends PrettyControlador<SextaParte> implements Serializable, CRUD {

    @EJB
    private SextaParteFacade sextaParteFacade;
    private Mes mes;
    private boolean marcarTodos;
    private VinculoFP vinculoFP;
    private List<SextaParte> sextaPartes = new ArrayList<>();
    @EJB
    private AfastamentoFacade afastamentoFacade;
    @EJB
    private FaltasFacade faltasFacade;
    @EJB
    private AverbacaoTempoServicoFacade averbacaoTempoServicoFacade;
    private SextaParte sextaParte;
    private ConverterAutoComplete converterContratoFP;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private ComparadorDeFollhasFacade comparadorDeFollhasFacade;
    private List<String> resumoLog = new LinkedList<>();
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private ReintegracaoFacade reintegracaoFacade;
    private ConverterAutoComplete converterBaseFP;
    private ConverterAutoComplete converterAtoLegal;
    private Boolean baseExistente;
    private ItemBaseFP itemBaseFP;
    private BaseFP baseFP;
    @EJB
    private RelatorioTempoDeServicoFacade relatorioTempoDeServicoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private TipoSextaParteFacade tipoSextaParteFacade;
    private boolean exibirSomenteAfastamentoDescontaTempoServicoEfetivo = false;
    private boolean exibirSomenteAverbacoesSextaParte = false;

    public SextaParteControlador() {
        super(SextaParte.class);
        metadata = new EntidadeMetaData(SextaParte.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/sextaparte/";
    }

    @Override
    public Object getUrlKeyValue() {
        return getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return sextaParteFacade;
    }

    public List<SextaParte> getSextaPartes() {
        return sextaPartes;
    }

    public void setSextaPartes(List<SextaParte> sextaPartes) {
        this.sextaPartes = sextaPartes;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public boolean isMarcarTodos() {
        return marcarTodos;
    }

    public void setMarcarTodos(boolean marcarTodos) {
        this.marcarTodos = marcarTodos;
    }

    public Boolean getBaseExistente() {
        return baseExistente != null ? baseExistente : hasBaseAdicionada();
    }

    public void setBaseExistente(Boolean baseExistente) {
        this.baseExistente = baseExistente;
    }

    public ItemBaseFP getItemBaseFP() {
        return itemBaseFP;
    }

    public void setItemBaseFP(ItemBaseFP itemBaseFP) {
        this.itemBaseFP = itemBaseFP;
    }

    public BaseFP getBaseFP() {
        return baseFP;
    }

    public void setBaseFP(BaseFP baseFP) {
        this.baseFP = baseFP;
    }

    public List<Afastamento> getAfastamentoListExibidos() {
        if (this.exibirSomenteAfastamentoDescontaTempoServicoEfetivo) {
            return selecionado.getAfastamentoList().stream()
                .filter(this::isDescontarTempoServicoEfetivo)
                .collect(Collectors.toList());
        }
        return selecionado.getAfastamentoList();
    }

    public boolean getExibirSomenteAfastamentoDescontaTempoServicoEfetivo() {
        return exibirSomenteAfastamentoDescontaTempoServicoEfetivo;
    }

    public void setExibirSomenteAfastamentoDescontaTempoServicoEfetivo(boolean exibirSomenteAfastamentoDescontaTempoServicoEfetivo) {
        this.exibirSomenteAfastamentoDescontaTempoServicoEfetivo = exibirSomenteAfastamentoDescontaTempoServicoEfetivo;
    }

    public List<AverbacaoTempoServico> getAverbacoesTempoServicoExibidos() {
        if (this.exibirSomenteAverbacoesSextaParte) {
            return selecionado.getAverbacaoTempoServicoList().stream()
                .filter(this::isSextaParte)
                .collect(Collectors.toList());
        }
        return selecionado.getAverbacaoTempoServicoList();
    }

    public boolean getExibirSomenteAverbacoesSextaParte() {
        return exibirSomenteAverbacoesSextaParte;
    }

    public void setExibirSomenteAverbacoesSextaParte(boolean exibirSomenteAverbacoesSextaParte) {
        this.exibirSomenteAverbacoesSextaParte = exibirSomenteAverbacoesSextaParte;
    }

    public void marcaTodos() {
        for (SextaParte sp : sextaPartes) {
            sp.setTemDireito(marcarTodos);
        }

    }

    public void bucarServidoresComDireitoSextaParte() {
        if (permiteBuscar()) {
            DateTime data = null;
            try {
                data = getDataCorreta();
            } catch (Exception e) {
                FacesUtil.addError("Erro", "erro ao converter data. " + e.getMessage());
                return;
            }
            sextaPartes = sextaParteFacade.getServidoresDireitoSextaParte(data, getIntervaloMeses(), vinculoFP);
            if (sextaPartes.isEmpty() && vinculoFP != null) {
                FacesUtil.addWarn("Atenção", "O servidor não tem 25 anos completados de tempo de serviço efetivo.");
            }
        }
    }

    public String getIntervaloMeses() {
        String s = "";
        if (selecionado.getMes() != null && mes != null) {
            for (int i = mes.getNumeroMes(); i <= selecionado.getMes().getNumeroMes(); i++) {
                s += i + ",";
            }
        }
        return s.substring(0, s.length() - 1);
    }

    public List<TipoSextaParte> completarTiposSextaParte(String parte) {
        if (selecionado.getVinculoFP() == null) {
            return Lists.newArrayList();
        }
        return sextaParteFacade.getTipoSextaParteFacade().buscarTipoSextaParte(selecionado.getVinculoFP().getUnidadeOrganizacional(), parte);
    }

    public DateTime getDataCorreta() {
        DateTime novaData = new DateTime();
        try {
            Calendar cal = Calendar.getInstance();
            if (selecionado.getAno() != null) {
                novaData.withYear(selecionado.getAno());
            } else {
                novaData.withYear(cal.get(Calendar.YEAR));
            }
            if (selecionado.getMes() != null) {
                novaData = novaData.monthOfYear().setCopy(selecionado.getMes().getNumeroMes());
            } else {
                novaData = novaData.monthOfYear().setCopy(cal.get(Calendar.MONTH) + 1);
            }
            novaData = novaData.dayOfMonth().withMaximumValue();
        } catch (Exception ex) {
            try {
                throw new Exception("erro de conversão", ex.getCause());
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return novaData;
    }

    public boolean permiteBuscar() {
        if (selecionado != null) {
            if (selecionado.getAno() == null) {
                FacesUtil.addWarn("Atenção", "Campo ano de preenchimento obrigatório.");
                return false;
            }
            if (mes == null) {
                FacesUtil.addWarn("Atenção", "Campo Mês inicial de preenchimento obrigatório.");
                return false;
            }

            if (selecionado.getMes() == null) {
                FacesUtil.addWarn("Atenção", "Campo Mês final de preenchimento obrigatório.");
                return false;
            }
            if (mes != null && selecionado.getMes() != null) {
                if (mes.getNumeroMes() > selecionado.getMes().getNumeroMes()) {
                    FacesUtil.addWarn("Atenção", "Mês incial não pode ser maior que Mês final.");
                    return false;
                }
            }
        } else {
            FacesUtil.addError("erro", "selecionado nullo");
            return false;
        }
        return true;
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (Mes object : Mes.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    @Override
    public void salvar() {
        try {
            if (baseFP != null && !baseFP.getItensBasesFPs().isEmpty()) {
                selecionado.setBaseFP(baseFP);
            }
            validarCamposSelecionado();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void validarCamposSelecionado() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getVinculoFP() != null && selecionado.getInicioVigencia() != null && !DataUtil.isVigenciaValida(selecionado, sextaParteFacade.findSextaPartePorVinculoFP(selecionado.getVinculoFP()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O servidor selecionado já possui um registro vigente na tabela de sexta parte.");
        }
        if (selecionado.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Início da Concessão!");
        }
        if (selecionado.getAtoLegal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Ato Legal!");
        }
        if (selecionado.getVinculoFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Servidor!");
        }
        if (selecionado.getVinculoFP() != null && getServidorTemMenosDe25AnosTrabalhados() && isOperacaoNovo()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Servidor ainda não possuí 25 anos de trabalho. ");
        }
        ve.lancarException();
    }

    public ConverterAutoComplete getConverterBaseFP() {
        if (converterBaseFP == null) {
            converterBaseFP = new ConverterAutoComplete(BaseFP.class, sextaParteFacade.getBaseFPFacade());
        }
        return converterBaseFP;
    }

    public void carregaDosAfastamentosFaltasAverbacao(BigDecimal identificador) {
        this.sextaParte = sextaParteFacade.recuperar(identificador.longValue());
        List<Afastamento> afastamentoList = afastamentoFacade.buscarAfastamentosPorVinculoFPAndData(sextaParte.getVinculoFP(), sextaParte.getInicioVigencia(), Boolean.TRUE);
        List<VwFalta> faltasList = faltasFacade.buscarFaltasPorVinculoFPAndTipoFaltaAndData(sextaParte.getVinculoFP(), TipoFalta.FALTA_INJUSTIFICADA, sextaParte.getInicioVigencia());
        List<AverbacaoTempoServico> averbacaoTempoServicoList = averbacaoTempoServicoFacade.buscarAverbacaoPorContratoFPAndData(sextaParte.getVinculoFP().getContratoFP(), sextaParte.getInicioVigencia(), Boolean.TRUE);
        this.sextaParte.setAfastamentoList(afastamentoList);
        this.sextaParte.setFaltasList(faltasList);
        this.sextaParte.setAverbacaoTempoServicoList(averbacaoTempoServicoList);
        FacesUtil.atualizarComponente("formDialogAfastamentos");
    }

    @URLAction(mappingId = "novosextaparte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        vinculoFP = null;
        sextaParte = new SextaParte();
        super.novo();
        selecionado.setInicioVigencia(new Date());
        sextaParte.setTemDireito(true);
        selecionado.setTemDireito(true);
        baseFP = new BaseFP();
        itemBaseFP = new ItemBaseFP();
        baseExistente = true;
    }

    @URLAction(mappingId = "versextaparte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarsextaparte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        itemBaseFP = new ItemBaseFP();
        baseExistente = true;
        baseFP = new BaseFP();
        buscaInformacoesTempoTrabalho();
        buscarPeriodoExcludente();
    }

    public SextaParte getSextaParte() {
        return sextaParte;
    }

    public void setSextaParte(SextaParte sextaParte) {
        this.sextaParte = sextaParte;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public Converter getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(VinculoFP.class, vinculoFPFacade);
        }
        return converterContratoFP;
    }

    public List<String> getResumoLog() {
        return resumoLog;
    }

    public void setResumoLog(List<String> resumoLog) {
        this.resumoLog = resumoLog;
    }

    public List<VinculoFP> completaVinculos(String s) {
        return vinculoFPFacade.listaTodosVinculos1(s.trim());
    }

    public List<ContratoFP> completaContratosEstatutariosVigentes(String s) {
        return contratoFPFacade.buscarContratoPorTipoRegime(s.trim(), TipoRegime.CELETISTA, TipoRegime.ESTATUTARIO);
    }

    public void removerSextaPartes() {
        for (SextaParte s : sextaParteFacade.lista()) {
            sextaParteFacade.remover(s);
        }
    }

    public void corrigirSextaPartes() throws SQLException {
        resumoLog = new LinkedList<>();
        List<RegistroDB> sextasTurmalina = comparadorDeFollhasFacade.buscarServidoresParaSextaParte();
        List<VinculoFP> vinculoFPSextaParte = new LinkedList<>();
        //remover os que não tem no turmalina...
        for (RegistroDB registroDB : sextasTurmalina) {
            BigDecimal matricula = (BigDecimal) registroDB.getCampoByIndex(0).getValor();
            BigDecimal numero = (BigDecimal) registroDB.getCampoByIndex(1).getValor();
            Date inicioVigencia = (Date) registroDB.getCampoByIndex(2).getValor();
            Date finalVigencia = (Date) registroDB.getCampoByIndex(3).getValor();
            VinculoFP v = vinculoFPFacade.recuperarVinculoPorMatriculaENumero(matricula.intValue() + "", numero.intValue() + "");
            if (v != null) {
                vinculoFPSextaParte.add(v);
                SextaParte sexta = new SextaParte();
                sexta.setVinculoFP(v);
                DateTime dt = new DateTime(inicioVigencia);
                sexta.setInicioVigencia(dt.toDate());
                sexta.setFimVigencia(finalVigencia);
                sexta.setTemDireito(true);
                sexta.setAno(dt.getYear());
                sexta.setMes(Mes.getMesToInt(dt.getMonthOfYear()));
                sexta.setTotalAnos(25);
                logger.debug("criando nova sexta parte para " + v);
                resumoLog.add("criando nova sexta parte para " + v);
                sextaParteFacade.salvar(sexta);
            }

        }
    }

    public void buscaInformacoesTempoTrabalho() {
        verificarTipoRegime();
        List<Afastamento> afastamentoList = afastamentoFacade.buscarAfastamentosPorVinculoFPAndData(selecionado.getVinculoFP(), selecionado.getInicioVigencia(), null);
        List<VwFalta> faltasList = faltasFacade.buscarFaltasPorVinculoFPAndTipoFaltaAndData(selecionado.getVinculoFP(), TipoFalta.FALTA_INJUSTIFICADA, selecionado.getInicioVigencia());
        List<AverbacaoTempoServico> averbacaoTempoServicoList = averbacaoTempoServicoFacade.buscarAverbacaoPorContratoFPAndData(selecionado.getVinculoFP(), selecionado.getInicioVigencia(), null);
        selecionado.setAfastamentoList(afastamentoList);
        setExibirSomenteAfastamentoDescontaTempoServicoEfetivo(false);
        selecionado.setFaltasList(faltasList);
        setExibirSomenteAverbacoesSextaParte(false);
        selecionado.setAverbacaoTempoServicoList(averbacaoTempoServicoList);
    }

    public void buscarPeriodoExcludente() {
        List<PeriodoExcludenteDTO> periodoExcludenteList = tipoSextaParteFacade.buscarPeriodoExcludente(selecionado.getVinculoFP(), selecionado.getTipoSextaParte());
        selecionado.setPeriodoExcludenteList(periodoExcludenteList);
    }


    private void verificarTipoRegime() {
        if (selecionado.getVinculoFP() != null && selecionado.getVinculoFP().getTipoRegime() != null && TipoRegime.CELETISTA.equals(selecionado.getVinculoFP().getTipoRegime().getCodigo())) {
            FacesUtil.addInfo("Atenção", "Foi selecionado um servidor(a) do tipo " + selecionado.getVinculoFP().getTipoRegime().getDescricao());
        }
    }

    public String getTotalTempoAverbadoString() {
        return DataUtil.totalDeDiasEmAnosMesesDias(getTotalDiasTempoAverbado());
    }

    public String getTotalFaltasString() {
        return DataUtil.totalDeDiasEmAnosMesesDias(getTotalDiasFaltas());
    }

    public String getTotalAfastamentosString() {
        return DataUtil.totalDeDiasEmAnosMesesDias(getTotalDiasAfastamentos());
    }

    public String getTotalPeriodoExcludenteString() {
        return DataUtil.totalDeDiasEmAnosMesesDias(getTotalDiasPeriodoExcludente());
    }

    public String getTotalDiasExoneradoAntesDaReintegracaoString() {
        return DataUtil.totalDeDiasEmAnosMesesDias(getTotalDiasExoneradoAntesDaReintegracao());
    }

    public String getTotalTempoContratoString() {
        return DataUtil.totalDeDiasEmAnosMesesDias(getTotalDiasTempoContrato());
    }

    public int getTotalDiasTempoAverbado() {
        Integer dias = 0;
        for (AverbacaoTempoServico averbacao : selecionado.getAverbacaoTempoServicoList()) {
            if (isSextaParte(averbacao)) {
                dias = dias + DataUtil.getDias(averbacao.getInicioVigencia(), averbacao.getFinalVigencia());
            }
        }
        return dias;
    }

    private Boolean isSextaParte(AverbacaoTempoServico averbacao) {
        return Optional.ofNullable(averbacao.getSextaParte()).orElse(Boolean.FALSE);
    }

    public int getTotalDiasFaltas() {
        Integer dias = 0;
        for (VwFalta falta : selecionado.getFaltasList()) {
            dias = dias + falta.getTotalFaltas();
        }
        return dias;
    }

    public int getTotalDiasAfastamentos() {
        Integer dias = 0;
        for (Afastamento afastamento : selecionado.getAfastamentoList()) {
            if (isDescontarTempoServicoEfetivo(afastamento)) {
                dias = dias + DataUtil.getDias(afastamento.getInicio(), afastamento.getTermino());
            }
        }
        return dias;
    }

    private Boolean isDescontarTempoServicoEfetivo(Afastamento afastamento) {
        return Optional.ofNullable(afastamento.getTipoAfastamento()
            .getDescontarTempoServicoEfetivo()).orElse(Boolean.FALSE);
    }

    public int getTotalDiasPeriodoExcludente() {
        Integer dias = 0;
        for (PeriodoExcludenteDTO periodoExcludenteDTO : selecionado.getPeriodoExcludenteList()) {
            dias = dias + periodoExcludenteDTO.getDias();
        }
        return dias;
    }

    public int getTotalDiasExoneradoAntesDaReintegracao() {
        Integer dias = 0;
        if (selecionado.getVinculoFP() != null) {
            List<SituacaoContratoFP> situacoes = reintegracaoFacade.getSituacoesExoneradoAntesDaReintegracao(selecionado.getVinculoFP());
            if (situacoes != null) {
                for (SituacaoContratoFP situacao : situacoes) {
                    Integer diferencaDias = DataUtil.diferencaDiasInteira(situacao.getInicioVigencia(), situacao.getFinalVigencia());
                    dias = dias + diferencaDias == 0 ? 1 : -1;
                }
            }
        }
        return dias;
    }

    public int getTotalDiasTempoContrato() {
        if (selecionado.getVinculoFP() == null) {
            return 0;
        }
        Date inicioVIgencia = selecionado.getVinculoFP().getAlteracaoVinculo();
        Date finalVigencia = selecionado.getVinculoFP().getFinalVigencia();
        if (inicioVIgencia == null) {
            inicioVIgencia = selecionado.getVinculoFP().getInicioVigencia();
        }
        if (finalVigencia != null && finalVigencia.compareTo(sistemaFacade.getDataOperacao()) > 0) {
            finalVigencia = sistemaFacade.getDataOperacao();
        }
        if (selecionado.getVinculoFP() != null) {
            return DataUtil.getDias(inicioVIgencia, finalVigencia != null ? finalVigencia : UtilRH.getDataOperacao());
        }
        return 0;
    }

    public int getTotalDiasTempoServico() {
        return (getTotalDiasTempoContrato() + getTotalDiasTempoAverbado()) - (getTotalDiasFaltas() + getTotalDiasAfastamentos() + getTotalDiasExoneradoAntesDaReintegracao() + getTotalDiasPeriodoExcludente());
    }

    public String getTotalTempoServicoString() {
        return DataUtil.totalDeDiasEmAnosMesesDias(getTotalDiasTempoServico());
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return atoLegalFacade.listaFiltrandoParteEPropositoAtoLegal(parte, PropositoAtoLegal.ATO_DE_PESSOAL, "numero", "nome");
    }

    public Converter getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegal;
    }

    public boolean getServidorTemMenosDe25AnosTrabalhados() {
        try {
            Integer anos = getAnosTempoTotalServico();
            if (anos >= 25) return false;
            return true;
        } catch (Exception ex) {
            return true;
        }
    }

    public Integer getAnosTempoTotalServico() {
        String stringTempoTotalComAnosMesesDias = DataUtil.totalDeDiasEmAnosMesesDias(getTotalDiasTempoServico());
        String[] anosMesesDias = stringTempoTotalComAnosMesesDias.split("[a-zA-Z]+");
        return Integer.valueOf(anosMesesDias[0].trim());
    }

    public boolean desabilitarServidor() {
        return isOperacaoEditar();
    }

    public List<BaseFP> completarBasesFP(String filtro) {
        return sextaParteFacade.getBaseFPFacade().listaFiltrando(filtro.trim(), "codigo", "descricaoReduzida");
    }

    private void validarCriacaoBseFP() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getVinculoFP() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe o Servidor antes de continuar");
        }
        ve.lancarException();
    }

    public void associarBase() {
        try {
            validarCriacaoBseFP();
            baseFP.setDescricao("Sexta Parte - Base " + selecionado.getVinculoFP());
            procurarEDefinirNovoCodigoBaseFP();
            FacesUtil.executaJavaScript("dialogbasefp.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void procurarEDefinirNovoCodigoBaseFP() {
        String b = sextaParteFacade.getSingletonGeradorCodigoRH().getProximoCodigoBaseFPPensaoAlimenticia();
        if (b != null) {
            baseFP.setCodigo(b);
        }
    }

    public void adicionarItemBaseFP() {
        try {
            validarBaseFP();
            itemBaseFP.setBaseFP(baseFP);
            baseFP.getItensBasesFPs().add(itemBaseFP);
            Collections.sort(baseFP.getItensBasesFPs());
            itemBaseFP = new ItemBaseFP();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarBaseFP() {
        ValidacaoException ve = new ValidacaoException();
        if (baseFP != null) {
            if ("".equals(baseFP.getDescricao())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Descrição deve ser informado.");
            }
            for (ItemBaseFP item : baseFP.getItensBasesFPs()) {
                if ((item.getEventoFP().equals(itemBaseFP.getEventoFP()))
                    && (item.getOperacaoFormula().equals(itemBaseFP.getOperacaoFormula()))) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Essa verba já existe na base atual.");
                }
            }
        }
        ve.lancarException();
    }

    public void removerItemBaseFP(ItemBaseFP e) {
        if (selecionado != null && selecionado.getBaseFP() != null) {
            selecionado.getBaseFP().getItensBasesFPs().remove(e);
        }
    }

    public boolean hasBaseAdicionada() {
        return selecionado.getBaseFP() != null;
    }

    public void confirmarBaseFP() {
        try {
            validarItensBaseFP();
            selecionado.setBaseFP(baseFP);
            baseExistente = true;
            FacesUtil.executaJavaScript("dialogbasefp.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarItensBaseFP() {
        ValidacaoException ve = new ValidacaoException();
        if (baseFP != null) {
            if (baseFP.getItensBasesFPs().isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Informe os itens da Base.");
            }
        }
        ve.lancarException();
    }
}
