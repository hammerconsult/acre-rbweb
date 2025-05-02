package br.com.webpublico.controle;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.NaturezaJuridica;
import br.com.webpublico.entidades.TipoAutonomo;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoAlvara;
import br.com.webpublico.enums.TipoNaturezaJuridica;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultStreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

@ManagedBean(name = "processoRenovacaoAlvaraControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoProcessoAlvara", pattern = "/tributario/processo-renovacao-alvara/",
        viewId = "/faces/tributario/alvara/calculo/renovacao/edita.xhtml")
})
public class ProcessoRenovacaoAlvaraControlador implements Serializable {
    public static final Logger logger = LoggerFactory.getLogger(ProcessoRenovacaoAlvaraControlador.class);

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private TipoAutonomoFacade tipoAutonomoFacade;
    @EJB
    private NaturezaJuridicaFacade naturezaJuridicaFacade;
    @EJB
    private AlvaraFacade alvaraFacade;
    @EJB
    private ProcessoRenovacaoAlvaraFacade processoRenovacaoAlvaraFacade;
    private FiltroProcessoRenovacaoAlvara filtro;
    private List<CadastroEconomico> cadastrosParaGeracao;
    private Future<AssistenteRenovacaoAlvara> futureRenovacao;
    private AssistenteRenovacaoAlvara assistente;
    private List<NaturezaJuridica> listaGeralNaturezaJuridica;
    private List<TipoAutonomo> listaGeralTipoAutonomo;
    private List<VOCnae> listaGeralCnaes;
    private Map<CadastroEconomico, List<FacesMessage>> mapaInconsistencia;

    public ProcessoRenovacaoAlvaraControlador() {
    }

    public FiltroProcessoRenovacaoAlvara getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroProcessoRenovacaoAlvara filtro) {
        this.filtro = filtro;
    }

    public List<CadastroEconomico> getCadastrosParaGeracao() {
        return cadastrosParaGeracao;
    }

    public void setCadastrosParaGeracao(List<CadastroEconomico> cadastrosParaGeracao) {
        this.cadastrosParaGeracao = cadastrosParaGeracao;
    }

    public Future<AssistenteRenovacaoAlvara> getFutureRenovacao() {
        return futureRenovacao;
    }

    public void setFutureRenovacao(Future<AssistenteRenovacaoAlvara> futureRenovacao) {
        this.futureRenovacao = futureRenovacao;
    }

    public AssistenteRenovacaoAlvara getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteRenovacaoAlvara assistente) {
        this.assistente = assistente;
    }

    public List<VOCnae> getListaGeralCnaes() {
        return listaGeralCnaes;
    }

    public void setListaGeralCnaes(List<VOCnae> listaGeralCnaes) {
        this.listaGeralCnaes = listaGeralCnaes;
    }

    public Map<CadastroEconomico, List<FacesMessage>> getMapaInconsistencia() {
        return mapaInconsistencia;
    }

    public void setMapaInconsistencia(Map<CadastroEconomico, List<FacesMessage>> mapaInconsistencia) {
        this.mapaInconsistencia = mapaInconsistencia;
    }

    @URLAction(mappingId = "novoProcessoAlvara", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtro = new FiltroProcessoRenovacaoAlvara();
        filtro.setExercicio(sistemaFacade.getExercicioCorrente());
        filtro.setCmcInicial("1");
        filtro.setCmcFinal("9999999");
        filtro.setTipoAlvara(TipoAlvara.FUNCIONAMENTO);
        filtro.setGrauDeRiscoBaixoA(false);
        filtro.setGrauDeRiscoBaixoB(false);
        filtro.setMei(false);
        cadastrosParaGeracao = Lists.newArrayList();
        assistente = null;
        futureRenovacao = null;

        filtro.setNaturezasJuridicas(new ArrayList<NaturezaJuridica>());
        filtro.setTiposAutonomos(new ArrayList<TipoAutonomo>());

        listaGeralNaturezaJuridica = naturezaJuridicaFacade.buscarNaturezasJuridicasAtivas();
        listaGeralCnaes = processoRenovacaoAlvaraFacade.buscarCnaesAtivos();
        listaGeralTipoAutonomo = tipoAutonomoFacade.lista();
    }

    public List<SelectItem> getTiposAlvara() {
        List<SelectItem> retorno = Lists.newLinkedList();
        for (TipoAlvara tipo : TipoAlvara.values()) {
            if (TipoAlvara.LOCALIZACAO_FUNCIONAMENTO.equals(tipo)) {
                retorno.add(new SelectItem(tipo, tipo.getDescricao()));
            }
        }
        return retorno;
    }

    public List<NaturezaJuridica> getNaturezasJuridicas() {
        List<NaturezaJuridica> retorno = Lists.newLinkedList();
        for (NaturezaJuridica natureza : listaGeralNaturezaJuridica) {
            retorno.add(natureza);
        }
        return retorno;
    }

    public List<TipoAutonomo> getTiposAutonomos() {
        List<TipoAutonomo> retorno = Lists.newLinkedList();
        for (TipoAutonomo tipo : listaGeralTipoAutonomo) {
            retorno.add(tipo);
        }
        return retorno;
    }

    public boolean mostrarTipoAutonomo() {
        boolean mostrar = false;
        for (NaturezaJuridica natureza : filtro.getNaturezasJuridicas()) {
            if (TipoNaturezaJuridica.FISICA.equals(natureza.getTipoNaturezaJuridica())) {
                mostrar = true;
                break;
            }
        }
        return mostrar;
    }

    public void pesquisarAlvarasParaRenovacao() {
        cadastrosParaGeracao = alvaraFacade.buscarCadastroEconomicoParaGeracaoDeAlvara(filtro);
        if (cadastrosParaGeracao.isEmpty()) {
            FacesUtil.addError("Pesquisa!", "Não foi encontrado nenhum Cadastro Econômico com filtros informados, ou os cadastros já possuem Alvará gerado para o exercício, podem estar suspenso ou possui um CNAE de alto risco.");
        }
    }

    public void gerarAlvarasParaOsCadastrosListados() {
        if (!cadastrosParaGeracao.isEmpty()) {
            assistente = criarAssistente();
            futureRenovacao = processoRenovacaoAlvaraFacade.gerarAlvarasNovo(assistente);
        }
    }

    private AssistenteRenovacaoAlvara criarAssistente() {
        assistente = new AssistenteRenovacaoAlvara();
        assistente.setExecutando(true);
        assistente.setTotal(cadastrosParaGeracao.size());
        assistente.setCadastros(cadastrosParaGeracao);
        assistente.setFiltro(filtro);
        assistente.setExercicio(sistemaFacade.getExercicioCorrente());
        assistente.setDataAtual(sistemaFacade.getDataOperacao());
        assistente.setUsuarioSistema(Util.recuperarUsuarioCorrente());
        assistente.setConfiguracaoTributario(processoRenovacaoAlvaraFacade.getConfiguracaoTributario());
        return assistente;
    }

    public void posRenovacao() {
        try {
            if (futureRenovacao != null && futureRenovacao.isDone()) {
                mapaInconsistencia = futureRenovacao.get().getMapaInconsistencia();
                assistente.setExecutando(false);
                FacesUtil.executaJavaScript("terminaRenovacao();");
                if(!mapaInconsistencia.isEmpty()) {
                    FacesUtil.addAtencao("Foram encontradas inconsistências na Renovação do Alvará para alguns " +
                        "Cadastros. Pressione o botão Gerar Inconsistências para mais detalhes.");
                }
                futureRenovacao = null;
            }
        } catch (Exception e) {
            logger.error("Erro ao renovar alvarás. ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao renovar alvarás. Detalhes: " + e.getMessage());
        }
    }

    public DefaultStreamedContent gerarTxt() {
        try {
            StringBuilder detalhe = new StringBuilder();
            String quebraLinha = "</br>";
            detalhe.append(quebraLinha).append("Relatório de Insconsistências").append(quebraLinha).append(quebraLinha);

            detalhe.append("Inconsistência(s): ").append(quebraLinha).append(quebraLinha);
            for (Map.Entry<CadastroEconomico, List<FacesMessage>> entry : mapaInconsistencia.entrySet()) {
                detalhe.append("Cadastro Econômico: ").append(entry.getKey()).append(quebraLinha);

                for (FacesMessage message : entry.getValue()) {
                    detalhe.append("- ").append(message.getDetail()).append(quebraLinha);
                }
                detalhe.append(quebraLinha);
            }

            detalhe.append(quebraLinha).append(quebraLinha);
            detalhe.append("USUÁRIO RESPONSÁVEL: ").append(Util.recuperarUsuarioCorrente());
            detalhe.append(quebraLinha);
            detalhe.append("DATA: ").append(DataUtil.recuperarDataPorExtenso(sistemaFacade.getDataOperacao()));
            SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy");
            String nomeArquivo = "Relatório_De_Inconsistências_Renovação_Alvará_" + sf.format(sistemaFacade.getDataOperacao()) + ".txt";
            File arquivo = new File(nomeArquivo);
            FileOutputStream fos = new FileOutputStream(arquivo);
            fos.write(detalhe.toString().replace("</br>", System.getProperty("line.separator"))
                .replace("<b>", " ")
                .replace("</b>", " ")
                .replace("<font color='red'>", " ")
                .replace("<font color='blue'>", " ")
                .replace("</font>", " ").getBytes());
            InputStream stream = new FileInputStream(arquivo);
            return new DefaultStreamedContent(stream, "text/plain", nomeArquivo);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao gerar arquivo de inconsistencias. ", e);
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
        return null;
    }

    public boolean contemTodasNaturezasJuridicas() {
        boolean todos = true;
        for (NaturezaJuridica nt : listaGeralNaturezaJuridica) {
            if (!filtro.getNaturezasJuridicas().contains(nt)) {
                todos = false;
                break;
            }
        }
        return todos;
    }

    public void removerTodasNaturezasJuridicas() {
        filtro.getNaturezasJuridicas().clear();
        removerTodosTiposAutonomos();
    }

    public void adicionarTodasNaturezasJuridicas() {
        for (NaturezaJuridica nt : listaGeralNaturezaJuridica) {
            if (!filtro.getNaturezasJuridicas().contains(nt)) {
                filtro.getNaturezasJuridicas().add(nt);
            }
        }
    }

    public boolean contemNaturezaJuridica(NaturezaJuridica natureza) {
        return filtro.getNaturezasJuridicas().contains(natureza);
    }

    public void removerNaturezaJuridica(NaturezaJuridica natureza) {
        if (filtro.getNaturezasJuridicas().contains(natureza)) {
            filtro.getNaturezasJuridicas().remove(natureza);
        }
        if (natureza.getTipoNaturezaJuridica().equals(TipoNaturezaJuridica.FISICA)) {
            removerTodosTiposAutonomos();
        }
    }

    public void adicionarNaturezaJuridica(NaturezaJuridica natureza) {
        if (filtro.getNaturezasJuridicas() == null) {
            filtro.setNaturezasJuridicas(new ArrayList<NaturezaJuridica>());
        }
        if (!filtro.getNaturezasJuridicas().contains(natureza)) {
            filtro.getNaturezasJuridicas().add(natureza);
        }
    }

    public boolean contemTodosTiposAutonomos() {
        boolean todos = true;
        for (TipoAutonomo ta : listaGeralTipoAutonomo) {
            if (!filtro.getTiposAutonomos().contains(ta)) {
                todos = false;
                break;
            }
        }
        return todos;
    }

    public void removerTodosTiposAutonomos() {
        filtro.getTiposAutonomos().clear();
    }

    public void adicionarTodosTiposAutonomos() {
        for (TipoAutonomo ta : listaGeralTipoAutonomo) {
            if (!filtro.getTiposAutonomos().contains(ta)) {
                filtro.getTiposAutonomos().add(ta);
            }
        }
    }

    public boolean contemTipoAutonomo(TipoAutonomo tipo) {
        return filtro.getTiposAutonomos().contains(tipo);
    }

    public void removerTipoAutonomo(TipoAutonomo tipo) {
        if (filtro.getTiposAutonomos().contains(tipo)) {
            filtro.getTiposAutonomos().remove(tipo);
        }
    }

    public void adicionarTipoAutonomo(TipoAutonomo tipo) {
        if (filtro.getTiposAutonomos() == null) {
            filtro.setTiposAutonomos(new ArrayList<TipoAutonomo>());
        }
        if (!filtro.getTiposAutonomos().contains(tipo)) {
            filtro.getTiposAutonomos().add(tipo);
        }
    }

    public boolean hasTodosCnaes() {
        boolean todos = true;
        for (VOCnae cnae : listaGeralCnaes) {
            if (!filtro.getCnaes().contains(cnae)) {
                todos = false;
                break;
            }
        }
        return todos;
    }

    public void removerTodosCnaes() {
        filtro.getCnaes().clear();
    }

    public void adicionarTodosCnaes() {
        for (VOCnae cnae : listaGeralCnaes) {
            if (!filtro.getCnaes().contains(cnae)) {
                filtro.getCnaes().add(cnae);
            }
        }
    }

    public boolean hasCnae(VOCnae cnae) {
        return filtro.getCnaes().contains(cnae);
    }

    public void removerCnae(VOCnae cnae) {
        filtro.getCnaes().remove(cnae);
    }

    public void adicionarCnae(VOCnae cnae) {
        if (filtro.getCnaes() == null) {
            filtro.setCnaes(Lists.<VOCnae>newArrayList());
        }
        if (!filtro.getCnaes().contains(cnae)) {
            filtro.getCnaes().add(cnae);
        }
    }
}
