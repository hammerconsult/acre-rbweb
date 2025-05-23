package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.PrestacaoDeContasItens;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.relatoriofacade.RelatorioBalanceteContabilPorTipoFacade;
import br.com.webpublico.util.*;
import com.beust.jcommander.internal.Lists;
import com.google.common.base.Preconditions;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: RenatoRomanini
 * Date: 31/01/14
 * Time: 12:05
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class PrestacaoDeContasFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private UnidadeGestoraFacade unidadeGestoraFacade;
    @EJB
    private PPAFacade ppaFacade;
    @EJB
    private ProjetoAtividadeFacade projetoAtividadeFacade;
    @EJB
    private ProgramaPPAFacade programaPPAFacade;
    @EJB
    private LOAFacade loaFacade;
    @EJB
    private ProvisaoPPAFonteFacade provisaoPPAFonteFacade;
    @EJB
    private CLPFacade clpFacade;
    @EJB
    private RelatorioBalanceteContabilPorTipoFacade relatorioBalanceteContabilPorTipoFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private LancamentoContabilManualFacade lancamentoContabilManualFacade;
    private Date dataOperacao;

    public List<LancamentoContabil> buscarLancamentoContabil(Mes mes, Exercicio exercicio, List<TipoEventoContabil> tipoEventoContabils, UnidadeGestora unidadeGestora) {
        String tiposEventos = getEventosAsString(tipoEventoContabils);
        String unidades = getUnidadesAsString(unidadeGestora);

        String hql = " select l.* from lancamentocontabil l " +
            " inner join lcp on l.lcp_id = lcp.id" +
            " inner join itemparametroevento item on l.itemparametroevento_id = item.id " +
            " inner join parametroevento p on item.parametroevento_id = p.id " +
            " inner join eventocontabil evento on p.eventocontabil_id = evento.id " +
            " where extract (year from l.dataLancamento) = :ano and extract (month from l.dataLancamento) = :mes " +
            " and (lcp.usoInterno = 0 or lcp.usoInterno is null) " +
            " and l.valor <> 0 " +
            " and evento.tipoEventoContabil in " + tiposEventos;
        if (unidadeGestora != null) {
            hql += " and l.unidadeOrganizacional_id in " + unidades;
        }
        hql += " order by p.idorigem ||''|| " +
            "case p.complementoid " +
            "when '" + ParametroEvento.ComplementoId.CONCEDIDO + "' then '1' " +
            "else '2' end";

        Query consulta = em.createNativeQuery(hql, LancamentoContabil.class);
        consulta.setParameter("ano", exercicio.getAno());
        consulta.setParameter("mes", mes.getNumeroMes());
        return consulta.getResultList();
    }

    public List<LancamentoContabilManual> buscarLancamentosContabeisManuais(Mes mes, Exercicio exercicio, UnidadeGestora unidadeGestora) {
        return lancamentoContabilManualFacade.buscarLancamentosContabeisManuais(
            mes, exercicio, Lists.newArrayList(TipoPrestacaoDeContas.PCM), (unidadeGestora != null ? getUnidadesAsString(unidadeGestora) : null)
        );
    }

    public List<LiquidacaoDoctoFiscal> buscarDocumentosFiscais(Mes mes, Exercicio exercicio, UnidadeGestora unidadeGestora) {
        String unidades = getUnidadesAsString(unidadeGestora);
        String sql = " select liqdoc.*  " +
            "  from doctofiscalliquidacao doc " +
            " inner join tipodocumentofiscal tipo on DOC.TIPODOCUMENTOFISCAL_ID = tipo.id " +
            " inner join LiquidacaoDoctoFiscal liqdoc on doc.id = LIQDOC.DOCTOFISCALLIQUIDACAO_ID " +
            " inner join liquidacao liq on liqDOC.LIQUIDACAO_ID = liq.id " +
            " where TIPO.OBRIGARCHAVEDEACESSO = 1 " +
            " and doc.chavedeacesso is not null " +
            " and extract(year from liq.dataliquidacao) = :ano and extract(month from liq.dataliquidacao) = :mes ";
        if (unidadeGestora != null) {
            sql += " and liq.unidadeOrganizacional_id in " + unidades;
        }
        sql += " order by LIQ.NUMERO, doc.numero ";
        Query consulta = em.createNativeQuery(sql, LiquidacaoDoctoFiscal.class);
        consulta.setParameter("ano", exercicio.getAno());
        consulta.setParameter("mes", mes.getNumeroMes());
        return consulta.getResultList();
    }

    public String getEventosAsString(List<TipoEventoContabil> tipoEventoContabils) {
        String tiposEventos = "(";

        for (TipoEventoContabil tipoEventoContabil : tipoEventoContabils) {
            tiposEventos += "'" + tipoEventoContabil.name() + "',";
        }
        tiposEventos = tiposEventos.substring(0, tiposEventos.length() - 1) + ")";
        return tiposEventos;
    }

    public String getUnidadesAsString(UnidadeGestora unidadeGestora) {
        String unidades = "(";
        if (unidadeGestora != null) {
            unidadeGestora = unidadeGestoraFacade.recuperarSemAtualizarHierarquias(unidadeGestora.getId());
            for (UnidadeGestoraUnidadeOrganizacional unidadeGestoraUnidadeOrganizacional : unidadeGestora.getUnidadeGestoraUnidadesOrganizacionais()) {
                unidades += "'" + unidadeGestoraUnidadeOrganizacional.getUnidadeOrganizacional().getId() + "',";
            }
            unidades = unidades.substring(0, unidades.length() - 1) + ")";
        }
        return unidades;
    }

    public String dataInicialAsString(Mes mes, Exercicio exercicio) {
        return "01/" + mes.getNumeroMesString() + "/" + exercicio.getAno();
    }

    public Date dataInicialAsDate(Mes mes, Exercicio exercicio) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 01);
        calendar.set(Calendar.MONTH, mes.getNumeroMesIniciandoEmZero());
        calendar.set(Calendar.YEAR, exercicio.getAno());
        return calendar.getTime();
    }

    public Date dataFimAsDate(Mes mes, Exercicio exercicio) {
        Calendar calendar = Calendar.getInstance();
        Integer mesInteger = mes.getNumeroMesIniciandoEmZero();
        calendar.set(Calendar.DAY_OF_MONTH, DataUtil.ultimoDiaDoMes(mesInteger + 1));
        calendar.set(Calendar.MONTH, mesInteger);
        calendar.set(Calendar.YEAR, exercicio.getAno());
        return calendar.getTime();
    }

    public String dataFimAsString(Mes mes, Exercicio exercicio) {
        int ultimoDia = DataUtil.ultimoDiaDoMes(mes.getNumeroMes());
        return ultimoDia + "/" + mes.getNumeroMesString() + "/" + exercicio.getAno();
    }

    public Class getClasse(String classe) {
        try {
            return Class.forName(classe);
        } catch (Exception e) {
            return null;
        }
    }

    public Object buscar(Long id, String classe) {
        try {
            classe = "br.com.webpublico.entidades." + classe;
            Query consulta = em.createQuery("select obj from " + classe + " obj where obj.id = :id", getClasse(classe));
            consulta.setParameter("id", id);
            consulta.setMaxResults(1);
            return consulta.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public UnidadeOrganizacional buscarUnidadeDaProvisaoPPA(ProgramaPPA programaPPA) {
        Query consulta = em.createQuery("select prov.unidadeOrganizacional from ProvisaoPPADespesa prov" +
            " inner join prov.subAcaoPPA.acaoPPA.programa programa" +
            " where programa.id = :prog " +
            " order by prov.unidadeOrganizacional.id ", UnidadeOrganizacional.class);
        consulta.setMaxResults(1);
        consulta.setParameter("prog", programaPPA.getId());
        try {
            return (UnidadeOrganizacional) consulta.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<HierarquiaOrganizacional> buscarCodigoNoHierarquiaUnidadeGestora(UnidadeGestora unidadeGestora, Date data) {
        if (unidadeGestora == null) {
            return new ArrayList<HierarquiaOrganizacional>();
        }
        Query consulta = em.createNativeQuery(" select distinct ho.* from unidadegestora ug" +
            " inner join UGESTORAUORGANIZACIONAL uog on ug.id = uog.unidadegestora_id" +
            " inner join unidadeorganizacional uo on uog.unidadeorganizacional_id = uo.id" +
            " inner join hierarquiaorganizacional ho on uo.id = ho.subordinada_id" +
            " where ug.id = :idUnidadeGestora" +
            " and ho.tipohierarquiaorganizacional = 'ORCAMENTARIA' " +
            " and :data BETWEEN ho.INICIOVIGENCIA AND coalesce(ho.FIMVIGENCIA, :data)", HierarquiaOrganizacional.class);
        consulta.setParameter("idUnidadeGestora", unidadeGestora.getId());
        consulta.setParameter("data", data, TemporalType.DATE);
        return consulta.getResultList();
    }

    public void removerArquivos(List<PartidaArquivoTCE> partidas, List<LancamentoArquivoTCE> lancamentos) {
        for (LancamentoArquivoTCE lancamentoArquivoTCE : lancamentos) {
            em.remove(em.find(LancamentoArquivoTCE.class, lancamentoArquivoTCE.getId()));
        }
        lancamentos.clear();

        for (PartidaArquivoTCE partidaArquivoTCE : partidas) {
            em.remove(em.find(PartidaArquivoTCE.class, partidaArquivoTCE.getId()));
        }
        partidas.clear();
    }

    public void salvar(Object entity) {
        Object o = Persistencia.getId(entity);
        if (o == null) {
            em.persist(entity);
        } else {
            entity = em.merge(entity);
        }
    }

    public List<PartidaArquivoTCE> buscarPartidas(Exercicio e) {
        Query consulta = em.createNativeQuery("select e.* from partidaarquivotce e where e.exercicio_id = :id", PartidaArquivoTCE.class);
        consulta.setParameter("id", e.getId());
        return consulta.getResultList();
    }

    public List<PartidaArquivoTCE> buscarPartidasPorNumeroLancamento(Exercicio e, String numeroLancamento) {
        try {
            String sql = "select e.* from partidaarquivotce e " +
                " where e.exercicio_id = :id" +
                " and e.numeroLancamento in " + numeroLancamento;
            Query consulta = em.createNativeQuery(sql, PartidaArquivoTCE.class);
            consulta.setParameter("id", e.getId());
            return consulta.getResultList();
        } catch (Exception ex) {
            return new ArrayList<PartidaArquivoTCE>();
        }
    }

    public List<LancamentoArquivoTCE> buscarLancamento(Exercicio e) {
        Query consulta = em.createNativeQuery("select e.* from lancamentoarquivotce e where e.exercicio_id = :id", LancamentoArquivoTCE.class);
        consulta.setParameter("id", e.getId());
        return consulta.getResultList();
    }

    public List<LancamentoArquivoTCE> buscarLancamentoPorOrgaoUnidade(Exercicio e, String orgaos, String unidades) {
        String sql = "select e from LancamentoArquivoTCE e " +
            " where e.exercicio.id  = :id" +
            " and e.orgao in " + orgaos +
            " and e.unidade in " + unidades;
        Query consulta = em.createQuery(sql, LancamentoArquivoTCE.class);
        consulta.setParameter("id", e.getId());
        return consulta.getResultList();
    }

    public List<LancamentoContabil> prepararLancamentoContabeisPorIdMovimentoOrigem(List<LancamentoContabil> lancamentoContabils) {
        Map<String, LancamentoContabil> map = new HashMap<>();

        for (LancamentoContabil lancamentoContabil : lancamentoContabils) {
            String idOrigem = lancamentoContabil.getItemParametroEvento().getParametroEvento().getIdOrigem();
            idOrigem += lancamentoContabil.getItemParametroEvento().getParametroEvento().getComplementoId().getCodigo();
            if (!map.containsKey(idOrigem)) {
                map.put(idOrigem, lancamentoContabil);
            }
        }

        List<LancamentoContabil> lancamentos = new ArrayList<>();
        for (String key : map.keySet()) {
            lancamentos.add(map.get(key));
        }
        lancamentoContabils.clear();
        lancamentoContabils.addAll(lancamentos);
        return lancamentoContabils;
    }

    public void inicializa(PrestacaoDeContasItens selecionado) {
        selecionado.setProcessados(0);
        selecionado.setTotal(0);
        selecionado.setCalculando(true);
        selecionado.setTempo(System.currentTimeMillis());

    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public void gerarArquivo(PrestacaoDeContasItens selecionado) throws ParserConfigurationException, IOException, TransformerException {
        dataOperacao = selecionado.getDataOperacao();

        addMensagem("<b> <font color='blue'>...Inciando...</font> </b>", selecionado);
        inicializa(selecionado);
        if (selecionado.getGerarArquivoOrcamento()) {
            gerarArquivoPlanejamento(selecionado);
        } else {
            gerarArquivoExecucaoOrcamentaria(getTiposEventoContabeis(selecionado), selecionado);
        }
        addMensagem("<b> <font color='black'>...Compactando Arquivo...</font> </b>", selecionado);
        addToZip(selecionado);
        addMensagem("<b> <font color='blue'>...Arquivo Pronto para Donwload...</font> </b>", selecionado);
        finaliza(selecionado);
    }

    public void addToZip(PrestacaoDeContasItens selecionado) {
        try {

            String nomeArquivo = getNomeArquivo(selecionado) + "-TCE";
            if (selecionado.getGerarArquivoOrcamento()) {
                nomeArquivo += "-ORC";
            }
            selecionado.setZipFile(File.createTempFile(nomeArquivo, "zip"));
            byte[] buffer = new byte[1024];
            FileOutputStream fout = new FileOutputStream(selecionado.getZipFile());
            ZipOutputStream zout = new ZipOutputStream(fout);

            for (Map.Entry<String, File> m : selecionado.getFiles().entrySet()) {
                FileInputStream fin = new FileInputStream(m.getValue());
                zout.putNextEntry(new ZipEntry(m.getKey()));
                int length;
                while ((length = fin.read(buffer)) > 0) {
                    zout.write(buffer, 0, length);
                }
                fin.close();
            }
            zout.close();

            FileInputStream fis = new FileInputStream(selecionado.getZipFile());
            selecionado.setStreamedContent(new DefaultStreamedContent(fis, "application/zip", nomeArquivo + ".zip"));
        } catch (IOException ioe) {
            FacesUtil.addError("Erro grave!", "Ocorreu um erro para gerar o arquivo, comunique o administrador.");
        }
    }

    public void criarESalvarArquivoPrestacaoDeContas(PrestacaoDeContasItens selecionado) {
        try {
            String nomeArquivo = getNomeArquivo(selecionado) + "-TCE";
            if (selecionado.getGerarArquivoOrcamento()) {
                nomeArquivo += "-ORC";
            }
            FileInputStream fis = new FileInputStream(selecionado.getZipFile());
            Arquivo arquivo = new Arquivo();
            arquivo.setInputStream(new DefaultStreamedContent(fis, "application/zip", nomeArquivo + ".zip").getStream());
            arquivo.setNome(nomeArquivo + ".zip");
            arquivo.setDescricao("Prestação de Contas");
            arquivo.setMimeType("application/zip");
            arquivo = arquivoFacade.novoArquivoMemoria(arquivo);

            ArquivoPrestacaoDeContas arquivoPrestacaoDeContas = new ArquivoPrestacaoDeContas();
            arquivoPrestacaoDeContas.setArquivo(arquivo);
            arquivoPrestacaoDeContas.setDataGeracao(selecionado.getDataOperacao());
            arquivoPrestacaoDeContas.setUsuarioSistema(selecionado.getUsuarioSistema());
            arquivoPrestacaoDeContas.setExercicio(selecionado.getExercicio());
            arquivoPrestacaoDeContas.setMes(selecionado.getMes());
            salvar(arquivoPrestacaoDeContas);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public String getNomeArquivo(PrestacaoDeContasItens selecionado) {
        //            PCM-2014-02-102
        String nomeArquivo = "PCM-" + selecionado.getExercicio().getAno() + "-" + selecionado.getMes().getNumeroMesString();
        if (selecionado.getUnidadeGestora() != null) {
            nomeArquivo += "-" + selecionado.getUnidadeGestora().getCodigo();
        }
        return nomeArquivo;
    }


    public String addMensagem(String msg, PrestacaoDeContasItens selecionado) {
        selecionado.setMensagens(msg);
        return selecionado.getMensagens();
    }

    public void finaliza(PrestacaoDeContasItens selecionado) {
        selecionado.setCalculando(false);
    }

    private void gerarArquivoPlanejamento(PrestacaoDeContasItens selecionado) throws ParserConfigurationException, IOException, TransformerException {
        addMensagem("<b> <font color='black'>...Recuperando Lançamentos...</font> </b>", selecionado);
        gerarArquivoPlanejamentoOrgao(selecionado);
        gerarArquivoPlanejamentoUnidade(selecionado);
        gerarArquivoPlanejamentoProgama(selecionado);
        gerarArquivoPlanejamentoAcao(selecionado);
    }

    private void gerarArquivoPlanejamentoAcao(PrestacaoDeContasItens selecionado) throws ParserConfigurationException, IOException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();
        Element lista = doc.createElement("lista");
        doc.appendChild(lista);

        PPA ppa = ppaFacade.ultimoPpaDoExercicio(selecionado.getExercicio());
        if (ppa == null) {
            throw new ExcecaoNegocioGenerica("Não foi encontrado nenhum PPA para " + selecionado.getExercicio().getAno() + " APROVADO.");
        }

        LOA loa = loaFacade.listaUltimaLoaPorExercicio(selecionado.getExercicio());
        addMensagem("<b> <font color='black'>...Recuperando Projeto/Atividade...</font> </b>", selecionado);
        Date dataInicial = montarDataInicial(selecionado);
        Date dataFinal = montarDataFinal(selecionado);
        List<AcaoPPA> acaoPPAs = Lists.newArrayList();
        List<AcaoPPA> todasAcao = projetoAtividadeFacade.buscarAcoesPPAPorPPAComLancamento(ppa, dataInicial, dataFinal);

        addMensagem("<b> <font color='black'>...Gerando Arquivo para Projeto/Atividade...</font> </b>", selecionado);
        inicializa(selecionado);
        selecionado.setQuantidadeDeLancamentoPorMovimento(acaoPPAs.size());
        selecionado.setTotal(selecionado.getQuantidadeDeLancamentoPorMovimento());

        for (AcaoPPA acaoPPA : todasAcao) {
            String codigo = acaoPPA.getTipoAcaoPPA().getCodigo() + acaoPPA.getCodigo();
            String titulo = acaoPPA.getDescricao();

            String programa = "";
            if (acaoPPA.getAcaoPrincipal() != null) {
                programa = acaoPPA.getAcaoPrincipal().getPrograma().getCodigo();
            } else {
                programa = acaoPPA.getPrograma().getCodigo();
            }

            Boolean achouAlguem = false;

            for (AcaoPPA acao : acaoPPAs) {
                String codigoAcao = acao.getTipoAcaoPPA().getCodigo() + acao.getCodigo();
                String tituloAcao = acao.getDescricao();

                String programaAcao = "";
                if (acaoPPA.getAcaoPrincipal() != null) {
                    programaAcao = acao.getAcaoPrincipal().getPrograma().getCodigo();
                } else {
                    programaAcao = acao.getPrograma().getCodigo();
                }

                if (codigo.equals(codigoAcao) && titulo.equals(tituloAcao) && programa.equals(programaAcao)) {
                    achouAlguem = true;
                }
            }

            if (!achouAlguem) {
                acaoPPAs.add(acaoPPA);
            }

        }


        for (AcaoPPA acaoPPA : acaoPPAs) {
            selecionado.setProcessados(selecionado.getProcessados() + 1);
            criarTagOrcamentoAcaoPPA(acaoPPA, lista, doc, ppa, loa);
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");

        DOMSource source = new DOMSource(doc);
        File arquivo = File.createTempFile("acao", "xml");
        StreamResult result = new StreamResult(arquivo);
        transformer.transform(source, result);

        selecionado.getFiles().put("acao.xml", arquivo);

    }

    private void criarTagOrcamentoAcaoPPA(AcaoPPA acaoPPA, Element lista, Document doc, PPA ppa, LOA loaExercicio) {
        Element acao = doc.createElement("acao");
        lista.appendChild(acao);

        criarTag("codigo", new Integer(acaoPPA.getTipoAcaoPPA().getCodigo() + acaoPPA.getCodigo()).toString(), doc, acao);

        Element programa = doc.createElement("programa");
        acao.appendChild(programa);

        if (acaoPPA.getAcaoPrincipal() != null) {
            criarTag("codigo", new Integer(acaoPPA.getAcaoPrincipal().getPrograma().getCodigo()).toString(), doc, programa);
        } else {
            criarTag("codigo", new Integer(acaoPPA.getPrograma().getCodigo()).toString(), doc, programa);
        }

        Element elmentPpa = doc.createElement("ppa");
        programa.appendChild(elmentPpa);

        criarTag("numero", ppa.getAtoLegal().getNumero(), doc, elmentPpa);
        criarTag("ano", Util.getAnoDaData(ppa.getAtoLegal().getDataEmissao()), doc, elmentPpa);

        criarTag("titulo", acaoPPA.getDescricao(), doc, acao);
        criarTag("tipoAcao", acaoPPA.getTipoAcaoPPA().getNomenclaturaTCE().toUpperCase(), doc, acao);
        criarTag("valor", projetoAtividadeFacade.getValorTotalProjetoAtividade(acaoPPA).toString(), doc, acao);
        if (acaoPPA.getAcaoPrincipal() != null) {
            criarTag("anoInicialVigencia", getAnoDaData(acaoPPA.getAcaoPrincipal().getPrograma().getInicio()).toString(), doc, acao);
            criarTag("anoFinalVigencia", getAnoDaData(acaoPPA.getAcaoPrincipal().getPrograma().getFim()).toString(), doc, acao);
        } else {
            criarTag("anoInicialVigencia", getAnoDaData(acaoPPA.getPrograma().getInicio()).toString(), doc, acao);
            criarTag("anoFinalVigencia", getAnoDaData(acaoPPA.getPrograma().getFim()).toString(), doc, acao);
        }

        UnidadeOrganizacional responsavel = acaoPPA.getResponsavel();
        if (responsavel == null) {
            responsavel = buscarUnidadeDaProvisaoPPA(acaoPPA.getPrograma());
        }


        Element funcao = doc.createElement("funcao");
        acao.appendChild(funcao);

        criarTag("numero", new Integer(acaoPPA.getFuncao().getCodigo()).toString(), doc, funcao);

        Element subFuncao = doc.createElement("subFuncao");
        acao.appendChild(subFuncao);
        criarTag("numero", new Integer(acaoPPA.getSubFuncao().getCodigo()).toString(), doc, subFuncao);

        Element loa = doc.createElement("loa");
        acao.appendChild(loa);

        criarTag("numero", loaExercicio.getAtoLegal().getNumero(), doc, loa);
        criarTag("ano", Util.getAnoDaData(loaExercicio.getAtoLegal().getDataEmissao()), doc, loa);
    }

    private void gerarArquivoPlanejamentoProgama(PrestacaoDeContasItens selecionado) throws ParserConfigurationException, IOException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();
        Element lista = doc.createElement("lista");
        doc.appendChild(lista);

        PPA ppa = ppaFacade.ultimoPpaDoExercicio(selecionado.getExercicio());
        if (ppa == null) {
            throw new ExcecaoNegocioGenerica("Não foi encontrado nenhum PPA para " + selecionado.getExercicio().getAno() + " APROVADO.");
        }
        LOA loa = loaFacade.listaUltimaLoaPorExercicio(selecionado.getExercicio());
        if (loa.getAtoLegal() == null) {
            throw new ExcecaoNegocioGenerica("A LOA de " + selecionado.getExercicio().getAno() + " não possui um Ato Legal.");
        }
        addMensagem("<b> <font color='black'>...Recuperando Programa PPA...</font> </b>", selecionado);
        Date dataInicial = montarDataInicial(selecionado);
        Date dataFinal = montarDataFinal(selecionado);
        List<ProgramaPPA> programaPPAs = programaPPAFacade.buscarProgramasPPAOrdenandoPorCodigoQuePossuiLancamento(ppa, dataInicial, dataFinal);

        addMensagem("<b> <font color='black'>...Gerando Arquivo para Programa PPA...</font> </b>", selecionado);
        inicializa(selecionado);
        selecionado.setQuantidadeDeLancamentoPorMovimento(programaPPAs.size());
        selecionado.setTotal(selecionado.getQuantidadeDeLancamentoPorMovimento());

        for (ProgramaPPA programaPPA : programaPPAs) {
            selecionado.setProcessados(selecionado.getProcessados() + 1);
            criarTagOrcamentoProgramaPPA(programaPPA, lista, doc, ppa, loa);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");

        DOMSource source = new DOMSource(doc);
        File arquivo = File.createTempFile("programa", "xml");
        StreamResult result = new StreamResult(arquivo);
        transformer.transform(source, result);

        selecionado.getFiles().put("programa.xml", arquivo);
    }

    private void gerarArquivoPlanejamentoOrgao(PrestacaoDeContasItens selecionado) throws ParserConfigurationException, IOException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();
        Element lista = doc.createElement("lista");
        doc.appendChild(lista);

        PPA ppa = ppaFacade.ultimoPpaDoExercicio(selecionado.getExercicio());
        if (ppa == null) {
            throw new ExcecaoNegocioGenerica("Não foi encontrado nenhum PPA para " + selecionado.getExercicio().getAno() + " APROVADO.");
        }

        LOA loa = loaFacade.listaUltimaLoaPorExercicio(selecionado.getExercicio());
        addMensagem("<b> <font color='black'>...Recuperando Órgão...</font> </b>", selecionado);
        Date dataInicial = montarDataInicial(selecionado);
        Date dataFinal = montarDataFinal(selecionado);
        List<HierarquiaOrganizacional> orcamentaria = hierarquiaOrganizacionalFacade.buscarHierarquiasOrcamentariasCriadosNoPeriodoPorIndice(2, dataInicial, dataFinal);

        addMensagem("<b> <font color='black'>...Gerando Arquivo para Órgãos...</font> </b>", selecionado);
        inicializa(selecionado);
        selecionado.setQuantidadeDeLancamentoPorMovimento(orcamentaria.size());
        selecionado.setTotal(selecionado.getQuantidadeDeLancamentoPorMovimento());

        for (HierarquiaOrganizacional hierarquiaOrganizaciona : orcamentaria) {
            selecionado.setProcessados(selecionado.getProcessados() + 1);
            criaTagOrcamentoOrgao(hierarquiaOrganizaciona, lista, doc, ppa, loa);
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");

        DOMSource source = new DOMSource(doc);
        File arquivo = File.createTempFile("orgao", "xml");
        StreamResult result = new StreamResult(arquivo);
        transformer.transform(source, result);

        selecionado.getFiles().put("orgao.xml", arquivo);
    }

    private Date montarDataInicial(PrestacaoDeContasItens selecionado) {
        return DataUtil.montaData(1, selecionado.getMes().getNumeroMesIniciandoEmZero(), selecionado.getExercicio().getAno()).getTime();
    }

    private void criarTagOrcamentoProgramaPPA(ProgramaPPA programaPPA, Element lista, Document doc, PPA ppa, LOA loaExercicio) {
        Element programa = criarTagProgramaPPA(programaPPA, lista, doc, ppa, loaExercicio);

        UnidadeOrganizacional responsavel = programaPPA.getResponsavel();
        if (responsavel == null) {
            responsavel = buscarUnidadeDaProvisaoPPA(programaPPA);
        }
        if (responsavel != null) {
            HierarquiaOrganizacional orcamentaria = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade("ORCAMENTARIA", responsavel, dataOperacao);
            Element elementOrgao = criarTagOrgao(orcamentaria, doc, programa);
            criarTagPPA(doc, ppa, elementOrgao, loaExercicio);
        } else {
            Element orgao = doc.createElement("orgao");
            programa.appendChild(orgao);
            criarTag("codigo", "", doc, orgao);
            criarTagPPA(doc, ppa, orgao, loaExercicio);
        }
        criarTag("nome", programaPPA.getDenominacao(), doc, programa);
        criarTag("objetivo", programaPPA.getObjetivo(), doc, programa);
        criarTag("valor", programaPPAFacade.getValorTotalProgramaPPA(programaPPA).toString(), doc, programa);
        criarTag("anoInicial", getAnoDaData(programaPPA.getInicio()) + "", doc, programa);
        criarTag("anoFinal", getAnoDaData(programaPPA.getFim()) + "", doc, programa);
    }


    private Element criarTagProgramaPPA(ProgramaPPA programaPPA, Element lista, Document doc, PPA ppa, LOA loaExercicio) {
        Element programa = doc.createElement("programa");
        lista.appendChild(programa);

        criarTag("codigo", new Integer(programaPPA.getCodigo()).toString(), doc, programa);
        criarTagPPA(doc, ppa, programa, loaExercicio);
        return programa;
    }

    private Integer getAnoDaData(Date data) {
        Calendar cInicio = Calendar.getInstance();
        cInicio.setTime(data);
        return cInicio.get(Calendar.YEAR);
    }

    private void criarTag(String label, String valor, Document doc, Element elementPai) {
        Element elementFilho = doc.createElement(label);
        elementFilho.setTextContent(valor);
        elementPai.appendChild(elementFilho);
    }

    private void criaTagOrcamentoOrgao(HierarquiaOrganizacional hierarquiaOrganizaciona, Element lista, Document doc, PPA ppaExercicio, LOA loaExercicio) {
        Element orgao = doc.createElement("orgao");
        lista.appendChild(orgao);

        criarTag("codigo", new Integer(hierarquiaOrganizaciona.getCodigoNo()).toString(), doc, orgao);
        criarTag("nome", hierarquiaOrganizaciona.getDescricao(), doc, orgao);
        criarTagPPA(doc, ppaExercicio, orgao, loaExercicio);
    }

    private void criarTagPPA(Document doc, PPA ppaExercicio, Element orgao, LOA loaExercicio) {
        Element elmentPpa = doc.createElement("ppa");
        orgao.appendChild(elmentPpa);
        Preconditions.checkNotNull(ppaExercicio.getAtoLegal(), "Não existe Lei no PPA " + ppaExercicio.getDescricao() + ".");
        criarTag("numero", ppaExercicio.getAtoLegal().getNumero(), doc, elmentPpa);
        criarTag("ano", Util.getAnoDaData(ppaExercicio.getAtoLegal().getDataEmissao()), doc, elmentPpa);
    }

    private void gerarArquivoPlanejamentoUnidade(PrestacaoDeContasItens selecionado) throws ParserConfigurationException, IOException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();
        Element lista = doc.createElement("lista");
        doc.appendChild(lista);

        PPA ppa = ppaFacade.ultimoPpaDoExercicio(selecionado.getExercicio());
        if (ppa == null) {
            throw new ExcecaoNegocioGenerica("Não foi encontrado nenhum PPA para " + selecionado.getExercicio().getAno() + " APROVADO.");
        }

        LOA loa = loaFacade.listaUltimaLoaPorExercicio(selecionado.getExercicio());

        addMensagem("<b> <font color='black'>...Recuperando Unidade Orçamentária...</font> </b>", selecionado);
        Date dataInicial = montarDataInicial(selecionado);
        Date dataFinal = montarDataFinal(selecionado);
        List<HierarquiaOrganizacional> orcamentaria = hierarquiaOrganizacionalFacade.buscarHierarquiasOrcamentariasCriadosNoPeriodoPorIndice(3, dataInicial, dataFinal);

        addMensagem("<b> <font color='black'>...Gerando Arquivo para Unidades...</font> </b>", selecionado);
        inicializa(selecionado);
        selecionado.setQuantidadeDeLancamentoPorMovimento(orcamentaria.size());
        selecionado.setTotal(selecionado.getQuantidadeDeLancamentoPorMovimento());

        for (HierarquiaOrganizacional hierarquiaOrganizaciona : orcamentaria) {
            selecionado.setProcessados(selecionado.getProcessados() + 1);
            criaTagOrcamentoUnidade(hierarquiaOrganizaciona, lista, doc, ppa, loa);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");

        DOMSource source = new DOMSource(doc);
        File arquivo = File.createTempFile("unidade", "xml");
        StreamResult result = new StreamResult(arquivo);
        transformer.transform(source, result);

        selecionado.getFiles().put("unidade.xml", arquivo);
    }

    private Date montarDataFinal(PrestacaoDeContasItens selecionado) {
        return DataUtil.criarDataUltimoDiaMes(selecionado.getMes().getNumeroMes(), selecionado.getExercicio().getAno()).toDate();
    }

    private void criaTagOrcamentoUnidade(HierarquiaOrganizacional hierarquiaOrganizaciona, Element lista, Document doc, PPA ppaDoExercicio, LOA loaExercicio) {
        Element unidade = criarTagUnidadeOrcamentaria(hierarquiaOrganizaciona, lista, doc);

        Element orgao = criarTagOrgao(hierarquiaOrganizaciona, doc, unidade);
        criarTagPPA(doc, ppaDoExercicio, orgao, loaExercicio);
    }

    private Element criarTagUnidadeOrcamentaria(HierarquiaOrganizacional hierarquiaOrganizaciona, Element lista, Document doc) {
        Element unidade = doc.createElement("unidadeOrcamentaria");
        lista.appendChild(unidade);
        if (hierarquiaOrganizaciona == null) {
            criarTag("codigo", "", doc, unidade);
            criarTag("nome", "", doc, unidade);
        } else {
            criarTag("codigo", new Integer(hierarquiaOrganizaciona.getCodigoNo()).toString(), doc, unidade);
            criarTag("nome", hierarquiaOrganizaciona.getDescricao(), doc, unidade);
        }
        return unidade;
    }

    private Element criarTagOrgao(HierarquiaOrganizacional hierarquiaOrganizaciona, Document doc, Element unidade) {
        Element orgao = doc.createElement("orgao");
        unidade.appendChild(orgao);

        try {
            criarTag("codigo", new Integer(getCodigoOrgaoDaHierarquia(hierarquiaOrganizaciona)).toString(), doc, orgao);
        } catch (NumberFormatException e) {
            criarTag("codigo", getCodigoOrgaoDaHierarquia(hierarquiaOrganizaciona), doc, orgao);
        }


        return orgao;
    }

    private void gerarArquivoPartida(List<LancamentoContabil> lancamentoContabils, List<LancamentoContabilManual> lancamentosManuais, PrestacaoDeContasItens selecionado) throws ParserConfigurationException, IOException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();
        Element lista = doc.createElement("lista");
        doc.appendChild(lista);

        if (selecionado.getPartidasSaldoInicial() != null) {
            for (PartidaArquivoTCE partidaArquivo : selecionado.getPartidasSaldoInicial()) {
                criarTagPartidaSaldoInicial(doc, lista, partidaArquivo);
            }
        }

        gerarTagsPartidasLancamentosContabeis(lancamentoContabils, lista, doc, selecionado);
        gerarTagsPartidasLancamentosManuais(lancamentosManuais, lista, doc, selecionado);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");

        DOMSource source = new DOMSource(doc);
        File arquivo = File.createTempFile("Partida", "xml");
        StreamResult result = new StreamResult(arquivo);
        transformer.transform(source, result);
        selecionado.getFiles().put("Partida.xml", arquivo);
    }

    private void gerarTagsPartidasLancamentosContabeis(List<LancamentoContabil> lacamentos, Element lista, Document doc, PrestacaoDeContasItens selecionado) {
        for (LancamentoContabil lancamentoContabil : lacamentos) {
            try {
                criarTagPartida(doc, lista, lancamentoContabil);
                selecionado.setProcessados(selecionado.getProcessados() + 1);
            } catch (ClassCastException e) {
                e.printStackTrace();
                selecionado.setErrosGeracaoDoArquivo(Util.adicionarObjetoEmLista(selecionado.getErrosGeracaoDoArquivo(), "ClassCastException -> " + e.getMessage()));
                continue;
            } catch (Exception e) {
                e.printStackTrace();
                selecionado.setErrosGeracaoDoArquivo(Util.adicionarObjetoEmLista(selecionado.getErrosGeracaoDoArquivo(), e.getMessage()));
                continue;
            }
        }
    }

    private void gerarTagsPartidasLancamentosManuais(List<LancamentoContabilManual> lancamentosManuais, Element lista, Document doc, PrestacaoDeContasItens selecionado) {
        for (LancamentoContabilManual lancamentoManual : lancamentosManuais) {
            for (ContaLancamentoManual contaLancamentoManual : lancamentoManual.getContas()) {
                try {
                    gerarTagPartidaConta(doc, contaLancamentoManual, lista, contaLancamentoManual.getContaContabil());
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    selecionado.setErrosGeracaoDoArquivo(Util.adicionarObjetoEmLista(selecionado.getErrosGeracaoDoArquivo(), "ClassCastException -> " + e.getMessage()));
                    continue;
                } catch (Exception e) {
                    e.printStackTrace();
                    selecionado.setErrosGeracaoDoArquivo(Util.adicionarObjetoEmLista(selecionado.getErrosGeracaoDoArquivo(), e.getMessage()));
                    continue;
                }
            }
            selecionado.setProcessados(selecionado.getProcessados() + 1);
        }
    }

    private void criarTagPartida(Document doc, Element lista, LancamentoContabil lancamentoContabil) throws Exception {
        Object o = buscarObjetoDoLancamento(lancamentoContabil);
        gerarTagPartidaConta(doc, lancamentoContabil, lista, lancamentoContabil.getContaCredito(), "C", o);
        gerarTagPartidaConta(doc, lancamentoContabil, lista, lancamentoContabil.getContaDebito(), "D", o);
    }

    private Object buscarObjetoDoLancamento(LancamentoContabil lancamentoContabil) {
        if (lancamentoContabil.getItemParametroEvento().getParametroEvento() != null) {
            if (lancamentoContabil.getItemParametroEvento().getParametroEvento().getIdOrigem() != null) {
                String idOrigem = lancamentoContabil.getItemParametroEvento().getParametroEvento().getIdOrigem();
                String classeOrigem = lancamentoContabil.getItemParametroEvento().getParametroEvento().getClasseOrigem();

                return buscar(Long.parseLong(idOrigem), classeOrigem);
            }
        }
        return null;
    }

    private void gerarTagPartidaConta(Document doc, LancamentoContabil lancamentoContabil, Element lista, Conta conta, String naturezaConta, Object objeto) throws Exception {
        Element partida = doc.createElement("partida");
        lista.appendChild(partida);

        Element contaContabilCredito = doc.createElement("contaContabil");
        partida.appendChild(contaContabilCredito);


        String codigoConta = conta.getCodigo();
        if (conta.getCodigoTCE() != null) {
            if (!conta.getCodigoTCE().trim().isEmpty()) {
                String codigo = StringUtil.cortaOuCompletaEsquerda(conta.getCodigoTCE(), 9, "0");
                codigoConta = Util.formatarValorComMascara(codigo, "#.#.#.#.#.##.##", ".");
            }
        }

        criarTag("codigo", codigoConta, doc, contaContabilCredito);

        criarTag("conteudoContaCorrente", montarConteudoContaCorrente(lancamentoContabil, naturezaConta, objeto), doc, partida);
        TipoMovimentoTCE tipoMovimentoTCE = buscarTipoMovimentoTCE(lancamentoContabil.getLcp(), naturezaConta);
        if (tipoMovimentoTCE != null) {
            if (!tipoMovimentoTCE.equals(TipoMovimentoTCE.NAO_APLICAVEL)) {
                criartTagTipoMovimentoTCE(doc, lancamentoContabil.getLcp(), naturezaConta, partida);
            }
        }
        criarTag("natureza", naturezaConta, doc, partida);
        String valor = buscarValorIndicadorSuperavitFinanceiro((ContaContabil) conta);
        if (!valor.isEmpty()) {
            criarTag("indicadorSuperavitFinanceiro", valor, doc, partida);
        }

        Element lancamento = doc.createElement("lancamento");
        partida.appendChild(lancamento);
        criarTag("numero", lancamentoContabil.getItemParametroEvento().getParametroEvento().getIdOrigem() + lancamentoContabil.getItemParametroEvento().getParametroEvento().getComplementoId().getCodigo(), doc, lancamento);
        eventosContabil(doc, lancamentoContabil, lancamento);
        criarTag("valor", lancamentoContabil.getValor().setScale(2, RoundingMode.HALF_UP).toString(), doc, partida);
    }

    private void gerarTagPartidaConta(Document doc, ContaLancamentoManual contaLancamentoManual, Element lista, Conta conta) {
        String naturezaConta = TipoLancamentoContabil.CREDITO.equals(contaLancamentoManual.getTipo()) ? "C" : "D";
        Element partida = doc.createElement("partida");
        lista.appendChild(partida);

        Element contaContabilCredito = doc.createElement("contaContabil");
        partida.appendChild(contaContabilCredito);

        String codigoConta = conta.getCodigo();
        if (conta.getCodigoTCE() != null) {
            if (!conta.getCodigoTCE().trim().isEmpty()) {
                String codigo = StringUtil.cortaOuCompletaEsquerda(conta.getCodigoTCE(), 9, "0");
                codigoConta = Util.formatarValorComMascara(codigo, "#.#.#.#.#.##.##", ".");
            }
        }
        criarTag("codigo", codigoConta, doc, contaContabilCredito);
        criarTag("conteudoContaCorrente", montarConteudoContaCorrente(contaLancamentoManual, naturezaConta), doc, partida);
        TipoMovimentoTCE tipoMovimentoTCE = buscarTipoMovimentoTCE(contaLancamentoManual.getLcp(), naturezaConta);
        if (tipoMovimentoTCE != null) {
            if (!tipoMovimentoTCE.equals(TipoMovimentoTCE.NAO_APLICAVEL)) {
                criartTagTipoMovimentoTCE(doc, contaLancamentoManual.getLcp(), naturezaConta, partida);
            }
        }
        criarTag("natureza", naturezaConta, doc, partida);
        String valor = buscarValorIndicadorSuperavitFinanceiro((ContaContabil) conta);
        if (!valor.isEmpty()) {
            criarTag("indicadorSuperavitFinanceiro", valor, doc, partida);
        }

        Element lancamento = doc.createElement("lancamento");
        partida.appendChild(lancamento);
        criarTag("numero", contaLancamentoManual.getLancamento().getId() + "0", doc, lancamento);
        criarTag("tipoDeLancamento", contaLancamentoManual.getLancamento().getEventoContabil().getTipoLancamento().equals(TipoLancamento.NORMAL) ? "ORDINARIO" : "ESTORNO", doc, lancamento);
        criarTag("valor", contaLancamentoManual.getLancamento().getValor().setScale(2, RoundingMode.HALF_UP).toString(), doc, partida);
    }

    private void eventosContabil(Document doc, LancamentoContabil lancamentoContabil, Element lancamento) {
        EventoContabil eventoContabilDoLancamentoContabil = buscarTipoEventoContabil(lancamentoContabil);
        TipoLancamento tipoLancamentoValor = eventoContabilDoLancamentoContabil.getTipoLancamento();
        criarTag("tipoDeLancamento", tipoLancamentoValor.equals(TipoLancamento.NORMAL) ? "ORDINARIO" : "ESTORNO", doc, lancamento);
    }

    private void criartTagTipoMovimentoTCE(Document doc, LCP lcp, String naturezaConta, Element partida) {
        Element tipoDeContaCorrente = doc.createElement("tipoDeContaCorrente");
        partida.appendChild(tipoDeContaCorrente);
        TipoMovimentoTCE tipoMovimentoTCE = buscarTipoMovimentoTCE(lcp, naturezaConta);
        if (tipoMovimentoTCE != null) {
            criarTag("numero", String.valueOf(tipoMovimentoTCE.getCodigo()), doc, tipoDeContaCorrente);
        }
    }

    private String buscarStringTipoMovimentoTCETipoContaAuxiliar(LancamentoContabil lancamentoContabil, String naturezaConta) {
        TipoMovimentoTCE tipoMovimentoTCE = buscarTipoMovimentoTCE(lancamentoContabil.getLcp(), naturezaConta);
        if (tipoMovimentoTCE != null) {
            return tipoMovimentoTCE.getDescricao();
        }
        return "";
    }

    private String buscarValorIndicadorSuperavitFinanceiro(ContaContabil conta) {
        if (conta.getSubSistema() != null) {
            if (conta.getSubSistema().equals(SubSistema.FINANCEIRO)) {
                return "F";
            } else if (conta.getSubSistema().equals(SubSistema.PERMANENTE)) {
                return "P";
            } else if (conta.getSubSistema().equals(SubSistema.COMPENSADO)) {
                return "";
            } else if (conta.getSubSistema().equals(SubSistema.ORCAMENTARIO)) {
                return "";
            }
        }
        return "";
    }

    private String montarConteudoContaCorrente(ContaLancamentoManual contaLancamentoManual, String naturezaConta) {
        TipoMovimentoTCE tipoMovimentoTCE = buscarTipoMovimentoTCE(contaLancamentoManual.getLcp(), naturezaConta);
        if (tipoMovimentoTCE != null) {
            switch (tipoMovimentoTCE) {
                case RECEITA_ORCAMENTARIA:
                    return montarConteudoContaCorrenteReceitaOrcamentariaContaReceitaEFonteDeRecursos(contaLancamentoManual.getLancamento().getContaReceita(), contaLancamentoManual.getContaDeDestinacao());

                case DOTACAO:
                    return montarConteudoContaCorrenteDotacaoDespesa(contaLancamentoManual);

                case FONTE_DE_RECURSO:
                    return montarConteudoContaCorrenteContaDeDestinacao(contaLancamentoManual.getContaDeDestinacao());

                case CREDOR:
                    return montarConteudoContaCorrenteCredor(contaLancamentoManual.getLancamento().getPessoa());

                case MOVIMENTACAO_FINANCEIRA:
                    return montarConteudoContaMovimentacaoFinanceira(contaLancamentoManual.getLancamento().getSubConta(),
                        contaLancamentoManual.getLancamento().getNumeroDocumentoFinanceiro(),
                        contaLancamentoManual.getLancamento().getTipoDocPagto(),
                        contaLancamentoManual.getContaDeDestinacao());

                default:
                    return "";
            }
        }
        return "";
    }

    private String montarConteudoContaCorrente(LancamentoContabil lancamentoContabil, String naturezaConta, Object o) throws Exception {
        String retorno = "";
        if (o != null) {
            TipoMovimentoTCE tipoMovimentoTCE = buscarTipoMovimentoTCE(lancamentoContabil.getLcp(), naturezaConta);
            if (tipoMovimentoTCE != null) {
                if (tipoMovimentoTCE.equals(TipoMovimentoTCE.RECEITA_ORCAMENTARIA)) {
                    retorno = montarConteudoContaCorrenteReceitaOrcamentaria(lancamentoContabil, o);
                } else if (tipoMovimentoTCE.equals(TipoMovimentoTCE.DOTACAO)) {
                    retorno = montarConteudoContaCorrenteDotacao(lancamentoContabil, o);
                } else if (tipoMovimentoTCE.equals(TipoMovimentoTCE.DESPESA_ORCAMENTARIA)) {
                    retorno = montarConteudoContaCorrenteDespesaOrcamentaria(lancamentoContabil, o);
                } else if (tipoMovimentoTCE.equals(TipoMovimentoTCE.FONTE_DE_RECURSO)) {
                    retorno = montarConteudoContaCorrenteContaDeDestinacao(lancamentoContabil, o);
                } else if (tipoMovimentoTCE.equals(TipoMovimentoTCE.CREDOR)) {
                    retorno = montarConteudoContaCorrenteCredor(lancamentoContabil, o);
                } else if (tipoMovimentoTCE.equals(TipoMovimentoTCE.MOVIMENTACAO_FINANCEIRA)) {
                    retorno = montarConteudoContaMovimentacaoFinanceira(lancamentoContabil, o);
                } else if (tipoMovimentoTCE.equals(TipoMovimentoTCE.RESTO_A_PAGAR)) {
                    retorno = montarConteudoContaRestoAPagar(lancamentoContabil, o);
                } else if (tipoMovimentoTCE.equals(TipoMovimentoTCE.CONTRATOS_PASSIVOS)) {
                    retorno = montarConteudoContaContratosPassivos(lancamentoContabil, o);
                } else if (tipoMovimentoTCE.equals(TipoMovimentoTCE.CONTRATOS)) {
                    retorno = montarConteudoContaContratos(lancamentoContabil, o);
                }
            }

        }
        return retorno;
    }

    private void montarConteudoContaCorrenteTipoContaCorrenteTCE(StringBuilder retorno, ProvisaoPPAFonte provisaoPPAFonte) {
        if (provisaoPPAFonte.getExtensaoFonteRecurso() != null) {
            retorno.append(provisaoPPAFonte.getExtensaoFonteRecurso().getTipoContaCorrenteTCE().getCodigo());
        }
    }

    private void montarConteudoContaCorrenteTipoContaCorrenteTCE(StringBuilder retorno, Empenho empenho) {
        if (empenho.getExtensaoFonteRecurso() != null) {
            retorno.append(empenho.getExtensaoFonteRecurso().getTipoContaCorrenteTCE().getCodigo());
        }
    }

    private String montarConteudoContaContratos(LancamentoContabil lancamentoContabil, Object o) {
        if (o instanceof Contrato) {
            return montarConteudoContaContratos((Contrato) o);
        }
        return "";
    }

    private String montarConteudoContaContratosPassivos(LancamentoContabil lancamentoContabil, Object o) {
        if (o instanceof Contrato) {
            return montarConteudoContaContratosPassivos((Contrato) o);
        }
        return "";
    }

    private String montarConteudoContaCorrenteReceitaOrcamentaria(LancamentoContabil lancamentoContabil, Object objeto) throws Exception {
        if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.PREVISAO_INICIAL_RECEITA)) {
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                return montarConteudoContaCorrenteReceitaOrcamentariaPrevisaoInicialReceita((ReceitaLOAFonte) objeto);
            }
        }
        if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.PREVISAO_ADICIONAL_RECEITA)) {
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                return montarConteudoContaCorrenteReceitaOrcamentariaPrevisaoAdicionalReceita((ReceitaAlteracaoORC) objeto);
            } else {
                return montarConteudoContaCorrenteReceitaOrcamentariaPrevisaoAdicionalReceita(((EstornoReceitaAlteracaoOrc) objeto).getReceitaAlteracaoORC());
            }
        }
        if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.RECEITA_REALIZADA)) {
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                return montarConteudoContaCorrenteReceitaOrcamentariaReceitaRealizada(((LancReceitaFonte) objeto).getReceitaLoaFonte());
            } else {
                return montarConteudoContaCorrenteReceitaOrcamentariaReceitaRealizada(((ReceitaORCFonteEstorno) objeto).getReceitaLoaFonte());
            }
        }
        if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.EXECUCAO_RECEITA)) {
            if (objeto instanceof ReceitaFechamentoExercicio) {
                ReceitaFechamentoExercicio receita = (ReceitaFechamentoExercicio) objeto;
                return montarConteudoContaCorrenteReceitaOrcamentariaContaReceitaEFonteDeRecursos(receita.getConta(), receita.getContaDeDestinacao());
            }
        }
        throw new Exception("IMPLEMENTAR PARA A CLASSE " + objeto.getClass().getSimpleName() + " E TIPOMOVIMENTO TCE = RECEITA ORCAMENTARIA e Evento.: " + buscarTipoEventoContabil(lancamentoContabil).getTipoEventoContabil());
    }

    private String montarConteudoContaCorrenteDotacao(LancamentoContabil lancamentoContabil, Object objeto) throws Exception {

        if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.CREDITO_INICIAL)) {
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                return montarConteudoContaCorrenteDotacaoDespesa((ProvisaoPPAFonte) objeto);
            }
        }
        if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.CREDITO_ADICIONAL)) {
            return montarConteudoContaCorrenteDotacaoCreditoAdicional(lancamentoContabil, objeto);
        }
        if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.EMPENHO_DESPESA)) {
            if (objeto instanceof Empenho) {
                return montarConteudoContaCorrenteCreditoAdicionalEmpenho((Empenho) objeto, ((Empenho) objeto).getCodigoContaTCE());
            } else if (objeto instanceof EmpenhoEstorno) {
                return montarConteudoContaCorrenteCreditoAdicionalEmpenhoEstorno((EmpenhoEstorno) objeto, ((EmpenhoEstorno) objeto).getEmpenho().getCodigoContaTCE());
            } else if (objeto instanceof DesdobramentoEmpenho) {
                return montarConteudoContaCorrenteCreditoAdicionalEmpenho(((DesdobramentoEmpenho) objeto).getEmpenho(), ((DesdobramentoEmpenho) objeto).getConta().getCodigoTCEOrCodigoSemPonto());
            } else if (objeto instanceof DesdobramentoEmpenhoEstorno) {
                return montarConteudoContaCorrenteCreditoAdicionalEmpenhoEstorno(((DesdobramentoEmpenhoEstorno) objeto).getEmpenhoEstorno(), ((DesdobramentoEmpenhoEstorno) objeto).getConta().getCodigoTCEOrCodigoSemPonto());
            }
        }
        if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.EXECUCAO_DESPESA)) {
            if (objeto instanceof DotacaoFechamentoExercicio) {
                DotacaoFechamentoExercicio dotacao = (DotacaoFechamentoExercicio) objeto;
                StringBuilder retorno = new StringBuilder();
                montarConteudoContaCorrenteFuncaoSubFuncao(retorno, dotacao.getFuncao(), dotacao.getSubFuncao());
                montarConteudoContaCorrenteProgramaPPA(retorno, dotacao.getProgramaPPA());
                montarConteudoContaCorrenteAcaoPPA(retorno, dotacao.getProjetoAtividade());
                montarConteudoContaCorrenteContaDespesa(retorno, dotacao.getConta().getCodigoTCEOrCodigoSemPonto(), 6);
                montarConteudoContaCorrenteVariaveisContaDeDestinacao(retorno, dotacao.getContaDeDestinacao());
                montarConteudoContaCorrenteVariaveisUnidadeOgao(retorno, dotacao.getUnidadeOrganizacional());

                ProvisaoPPAFonte provisaoPPAFonte = provisaoPPAFonteFacade.recuperarProvisaoPPAFontePorDotacaoFechamentoExercicio(dotacao);
                montarConteudoContaCorrenteTipoContaCorrenteTCE(retorno, provisaoPPAFonte);
                return retorno.toString();
            } else if (objeto instanceof DespesaFechamentoExercicio) {
                DespesaFechamentoExercicio despesa = (DespesaFechamentoExercicio) objeto;
                StringBuilder retorno = new StringBuilder();
                montarConteudoContaCorrenteFuncaoSubFuncao(retorno, despesa.getFuncao(), despesa.getSubFuncao());
                montarConteudoContaCorrenteProgramaPPA(retorno, despesa.getProgramaPPA());
                montarConteudoContaCorrenteAcaoPPA(retorno, despesa.getProjetoAtividade());
                montarConteudoContaCorrenteContaDespesa(retorno, despesa.getConta().getCodigoTCEOrCodigoSemPonto(), 6);
                montarConteudoContaCorrenteVariaveisContaDeDestinacao(retorno, despesa.getContaDeDestinacao());
                montarConteudoContaCorrenteVariaveisUnidadeOgao(retorno, despesa.getUnidadeOrganizacional());

                ProvisaoPPAFonte provisaoPPAFonte = provisaoPPAFonteFacade.recuperarProvisaoPPAFontePorDespesaFechamentoExercicio(despesa);
                montarConteudoContaCorrenteTipoContaCorrenteTCE(retorno, provisaoPPAFonte);
                return retorno.toString();
            }
        }
        if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.OBRIGACAO_APAGAR)) {
            if (objeto instanceof DesdobramentoObrigacaoPagar) {
                return montarConteudoContaCorrenteCreditoAdicionalEmpenho(((DesdobramentoObrigacaoPagar) objeto).getObrigacaoAPagar().getEmpenho(), ((DesdobramentoObrigacaoPagar) objeto).getConta().getCodigoTCEOrCodigoSemPonto());
            } else if (objeto instanceof DesdobramentoObrigacaoAPagarEstorno) {
                return montarConteudoContaCorrenteCreditoAdicionalEmpenho(((DesdobramentoObrigacaoAPagarEstorno) objeto).getObrigacaoAPagarEstorno().getObrigacaoAPagar().getEmpenho(), ((DesdobramentoObrigacaoAPagarEstorno) objeto).getConta().getCodigoTCEOrCodigoSemPonto());
            }
        }
        throw new Exception("IMPLEMENTAR PARA A CLASSE " + objeto.getClass().getSimpleName() + " E TIPOMOVIMENTO TCE = DOTACAO  e Evento.: " + buscarTipoEventoContabil(lancamentoContabil).getTipoEventoContabil());
    }

    private String montarConteudoContaCorrenteDotacaoCreditoAdicional(LancamentoContabil lancamentoContabil, Object objeto) {
        if (objeto instanceof SuplementacaoORC) {
            return montarConteudoContaCorrenteCreditoAdicionalSuplementacao((SuplementacaoORC) objeto);
        } else if (objeto instanceof AnulacaoORC) {
            return montarConteudoContaCorrenteCreditoAdicionalAnulacao((AnulacaoORC) objeto);
        } else if (objeto instanceof EstornoSuplementacaoOrc) {
            return montarConteudoContaCorrenteCreditoAdicionalSuplementacao(((EstornoSuplementacaoOrc) objeto).getSuplementacaoORC());
        } else if (objeto instanceof EstornoAnulacaoOrc) {
            return montarConteudoContaCorrenteCreditoAdicionalAnulacao(((EstornoAnulacaoOrc) objeto).getAnulacaoORC());
        }
        return null;
    }

    private String montarConteudoContaCorrenteDespesaOrcamentaria(LancamentoContabil lancamentoContabil, Object o) throws Exception {

        if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.EMPENHO_DESPESA)
            || verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.RESTO_PAGAR)) {
            if (o instanceof Empenho) {
                return montarConteudoContaCorrenteEmpenhoNormal((Empenho) o, ((Empenho) o).getCodigoContaTCE());
            } else if (o instanceof EmpenhoEstorno) {
                return montarConteudoContaCorrenteEmpenhoEstorno((EmpenhoEstorno) o, ((EmpenhoEstorno) o).getEmpenho().getCodigoContaTCE());
            } else if (o instanceof DesdobramentoEmpenho) {
                return montarConteudoContaCorrenteEmpenhoNormal(((DesdobramentoEmpenho) o).getEmpenho(), ((DesdobramentoEmpenho) o).getConta().getCodigoTCEOrCodigoSemPonto());
            } else if (o instanceof DesdobramentoEmpenhoEstorno) {
                return montarConteudoContaCorrenteEmpenhoEstorno(((DesdobramentoEmpenhoEstorno) o).getEmpenhoEstorno(), ((DesdobramentoEmpenhoEstorno) o).getConta().getCodigoTCEOrCodigoSemPonto());
            } else if (o instanceof InscricaoEmpenho) {
                return montarConteudoContaCorrenteEmpenhoNormal(((InscricaoEmpenho) o).getEmpenhoInscrito(), ((InscricaoEmpenho) o).getEmpenho().getCodigoContaTCE());
            }
        }
        //        Adiconar TCE
        if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.LIQUIDACAO_DESPESA)) {
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                return montarConteudoContaCorrenteEmpenhoNormal(((Desdobramento) o).getLiquidacao().getEmpenho(), ((Desdobramento) o).getLiquidacao().getEmpenho().getCodigoContaTCE());
            } else {
                return montarConteudoContaCorrenteEmpenhoNormal(((DesdobramentoLiquidacaoEstorno) o).getLiquidacaoEstorno().getLiquidacao().getEmpenho(), ((DesdobramentoLiquidacaoEstorno) o).getLiquidacaoEstorno().getLiquidacao().getEmpenho().getCodigoContaTCE());
            }
        }
        //        Adiconar TCE
        if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.PAGAMENTO_DESPESA)) {
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                return montarConteudoContaCorrenteEmpenhoNormal(((Pagamento) o).getLiquidacao().getEmpenho(), ((Pagamento) o).getLiquidacao().getEmpenho().getCodigoContaTCE());
            } else {
                return montarConteudoContaCorrenteEmpenhoNormal(((PagamentoEstorno) o).getPagamento().getLiquidacao().getEmpenho(), ((PagamentoEstorno) o).getPagamento().getLiquidacao().getEmpenho().getCodigoContaTCE());
            }
        }
        //        Adiconar TCE
        if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.RECEITA_EXTRA_ORCAMENTARIA)) {
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                return montarConteudoContaCorrenteEmpenhoNormal(((ReceitaExtra) o).getRetencaoPgto().getPagamento().getLiquidacao().getEmpenho(), ((ReceitaExtra) o).getRetencaoPgto().getPagamento().getLiquidacao().getEmpenho().getCodigoContaTCE());
            } else {
                return montarConteudoContaCorrenteEmpenhoNormal(((ReceitaExtraEstorno) o).getReceitaExtra().getRetencaoPgto().getPagamento().getLiquidacao().getEmpenho(), ((ReceitaExtraEstorno) o).getReceitaExtra().getRetencaoPgto().getPagamento().getLiquidacao().getEmpenho().getCodigoContaTCE());
            }
        }
        if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.EXECUCAO_DESPESA)
            || verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.APURACAO)) {

            if (o instanceof DespesaFechamentoExercicio) {
                return montarConteudoContaCorrenteEmpenhoNormal(((DespesaFechamentoExercicio) o).getEmpenho(), ((DespesaFechamentoExercicio) o).getEmpenho().getCodigoContaTCE());
            } else if (o instanceof Empenho) {
                return montarConteudoContaCorrenteEmpenhoNormal(((Empenho) o).getEmpenho(), ((Empenho) o).getEmpenho().getCodigoContaTCE());
            } else {
                return montarConteudoContaCorrenteEmpenhoNormal(((EmpenhoEstorno) o).getEmpenho(), ((EmpenhoEstorno) o).getEmpenho().getCodigoContaTCE());
            }
        }

        if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.OBRIGACAO_APAGAR)) {
            if (o instanceof DesdobramentoObrigacaoPagar) {
                return montarConteudoContaCorrenteEmpenhoNormal(((DesdobramentoObrigacaoPagar) o).getObrigacaoAPagar().getEmpenho(), ((DesdobramentoObrigacaoPagar) o).getConta().getCodigoTCEOrCodigoSemPonto());
            } else if (o instanceof DesdobramentoObrigacaoAPagarEstorno) {
                return montarConteudoContaCorrenteEmpenhoNormal(((DesdobramentoObrigacaoAPagarEstorno) o).getObrigacaoAPagarEstorno().getObrigacaoAPagar().getEmpenho(), ((DesdobramentoObrigacaoAPagarEstorno) o).getConta().getCodigoTCEOrCodigoSemPonto());
            }
        }
        throw new Exception("IMPLEMENTAR PARA A CLASSE " + o.getClass().getSimpleName() + " E TIPOMOVIMENTO TCE = DESPESA ORCAMENTARIA e Evento.: " + buscarTipoEventoContabil(lancamentoContabil).getTipoEventoContabil());
    }

    private String montarConteudoContaCorrenteContaDeDestinacao(LancamentoContabil lancamentoContabil, Object o) throws Exception {
        if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.AJUSTE_ATIVO_DISPONIVEL)) {
            ContaDeDestinacao contaDeDestinacao = ((AjusteAtivoDisponivel) o).getContaDeDestinacao();
            return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.AJUSTE_DEPOSITO)) {
            ContaDeDestinacao contaDeDestinacao = ((AjusteDeposito) o).getContaDeDestinacao();
            return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.CREDITO_ADICIONAL)) {
            return montarConteudoContaCorrenteFonteDeRecursosCreditoAdicional(o);
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.DESPESA_EXTRA_ORCAMENTARIA)) {
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                ContaDeDestinacao contaDeDestinacao = ((PagamentoExtra) o).getContaDeDestinacao();
                return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
            } else {
                ContaDeDestinacao contaDeDestinacao = ((PagamentoExtraEstorno) o).getPagamentoExtra().getContaDeDestinacao();
                return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
            }
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.EMPENHO_DESPESA)
            || verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.RESTO_PAGAR)) {
            if (o instanceof Empenho) {
                ContaDeDestinacao contaDeDestinacao = (ContaDeDestinacao) ((Empenho) o).getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos();
                return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
            } else if (o instanceof EmpenhoEstorno) {
                ContaDeDestinacao contaDeDestinacao = (ContaDeDestinacao) ((EmpenhoEstorno) o).getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos();
                return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
            } else if (o instanceof DesdobramentoEmpenho) {
                ContaDeDestinacao contaDeDestinacao = (ContaDeDestinacao) ((DesdobramentoEmpenho) o).getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos();
                return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
            } else if (o instanceof DesdobramentoEmpenhoEstorno) {
                ContaDeDestinacao contaDeDestinacao = (ContaDeDestinacao) ((DesdobramentoEmpenhoEstorno) o).getEmpenhoEstorno().getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos();
                return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
            }
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.APURACAO)) {
            if (o instanceof Empenho) {
                ContaDeDestinacao contaDeDestinacao = (ContaDeDestinacao) ((Empenho) o).getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos();
                return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
            } else {
                ContaDeDestinacao contaDeDestinacao = (ContaDeDestinacao) ((EmpenhoEstorno) o).getEmpenho().getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos();
                return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
            }
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.LIBERACAO_FINANCEIRA)) {
            ParametroEvento.ComplementoId complementoId = buscarComplementoId(lancamentoContabil);
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                if (complementoId.equals(ParametroEvento.ComplementoId.CONCEDIDO)) {
                    ContaDeDestinacao contaDeDestinacao = ((LiberacaoCotaFinanceira) o).getContaDeDestinacaoRetirada();
                    return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
                } else if (complementoId.equals(ParametroEvento.ComplementoId.RECEBIDO)) {
                    ContaDeDestinacao contaDeDestinacao = ((LiberacaoCotaFinanceira) o).getContaDeDestinacaoRetirada();
                    return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
                }
            } else {
                if (complementoId.equals(ParametroEvento.ComplementoId.CONCEDIDO)) {
                    ContaDeDestinacao contaDeDestinacao = ((EstornoLibCotaFinanceira) o).getLiberacao().getContaDeDestinacaoRetirada();
                    return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
                } else if (complementoId.equals(ParametroEvento.ComplementoId.RECEBIDO)) {
                    ContaDeDestinacao contaDeDestinacao = ((EstornoLibCotaFinanceira) o).getLiberacao().getContaDeDestinacaoRecebida();
                    return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
                }
            }
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.LIQUIDACAO_DESPESA)
            || verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.LIQUIDACAO_RESTO_PAGAR)) {
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                ContaDeDestinacao contaDeDestinacao = (ContaDeDestinacao) ((Desdobramento) o).getLiquidacao().getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos();
                return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
            } else {
                ContaDeDestinacao contaDeDestinacao = (ContaDeDestinacao) ((DesdobramentoLiquidacaoEstorno) o).getLiquidacaoEstorno().getLiquidacao().getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos();
                return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
            }
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.PAGAMENTO_DESPESA)
            || verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.PAGAMENTO_RESTO_PAGAR)) {
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                ContaDeDestinacao contaDeDestinacao = (ContaDeDestinacao) ((Pagamento) o).getLiquidacao().getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos();
                return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
            } else {
                ContaDeDestinacao contaDeDestinacao = (ContaDeDestinacao) ((PagamentoEstorno) o).getPagamento().getLiquidacao().getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos();
                return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
            }
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.RESTO_PAGAR)) {
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                ContaDeDestinacao contaDeDestinacao = (ContaDeDestinacao) ((Empenho) o).getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos();
                return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
            }
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.CANCELAMENTO_RESTO_PAGAR)) {
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                ContaDeDestinacao contaDeDestinacao = (ContaDeDestinacao) ((EmpenhoEstorno) o).getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos();
                return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
            }
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.RECEITA_EXTRA_ORCAMENTARIA)) {
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                ContaDeDestinacao contaDeDestinacao = ((ReceitaExtra) o).getContaDeDestinacao();
                return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
            } else {
                ContaDeDestinacao contaDeDestinacao = ((ReceitaExtraEstorno) o).getReceitaExtra().getContaDeDestinacao();
                return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
            }
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.RECEITA_REALIZADA)) {
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                ContaDeDestinacao contaDeDestinacao = (ContaDeDestinacao) ((LancReceitaFonte) o).getReceitaLoaFonte().getDestinacaoDeRecursos();
                return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
            } else {
                ContaDeDestinacao contaDeDestinacao = (ContaDeDestinacao) ((ReceitaORCFonteEstorno) o).getReceitaLoaFonte().getDestinacaoDeRecursos();
                return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
            }
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.TRANSFERENCIA_FINANCEIRA)) {
            ParametroEvento.ComplementoId complementoId = buscarComplementoId(lancamentoContabil);
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                if (complementoId.equals(ParametroEvento.ComplementoId.CONCEDIDO)) {
                    ContaDeDestinacao contaDeDestinacao = ((TransferenciaContaFinanceira) o).getContaDeDestinacaoRetirada();
                    return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
                } else if (complementoId.equals(ParametroEvento.ComplementoId.RECEBIDO)) {
                    ContaDeDestinacao contaDeDestinacao = ((TransferenciaContaFinanceira) o).getContaDeDestinacaoDeposito();
                    return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
                }
            } else {
                if (complementoId.equals(ParametroEvento.ComplementoId.CONCEDIDO)) {
                    ContaDeDestinacao contaDeDestinacao = ((EstornoTransferencia) o).getTransferencia().getContaDeDestinacaoRetirada();
                    return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
                } else if (complementoId.equals(ParametroEvento.ComplementoId.RECEBIDO)) {
                    ContaDeDestinacao contaDeDestinacao = ((EstornoTransferencia) o).getTransferencia().getContaDeDestinacaoDeposito();
                    return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
                }
            }
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.TRANSFERENCIA_MESMA_UNIDADE)) {
            ParametroEvento.ComplementoId complementoId = buscarComplementoId(lancamentoContabil);
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                if (complementoId.equals(ParametroEvento.ComplementoId.CONCEDIDO)) {
                    ContaDeDestinacao contaDeDestinacao = ((TransferenciaMesmaUnidade) o).getContaDeDestinacaoRetirada();
                    return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
                } else if (complementoId.equals(ParametroEvento.ComplementoId.RECEBIDO)) {
                    ContaDeDestinacao contaDeDestinacao = ((TransferenciaMesmaUnidade) o).getContaDeDestinacaoDeposito();
                    return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
                }
            } else {
                if (complementoId.equals(ParametroEvento.ComplementoId.CONCEDIDO)) {
                    ContaDeDestinacao contaDeDestinacao = ((EstornoTransfMesmaUnidade) o).getTransferenciaMesmaUnidade().getContaDeDestinacaoRetirada();
                    return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
                } else if (complementoId.equals(ParametroEvento.ComplementoId.RECEBIDO)) {
                    ContaDeDestinacao contaDeDestinacao = ((EstornoTransfMesmaUnidade) o).getTransferenciaMesmaUnidade().getContaDeDestinacaoDeposito();
                    return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
                }
            }
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.EXECUCAO_DESPESA)) {
            if (o instanceof DotacaoFechamentoExercicio) {
                ContaDeDestinacao contaDeDestinacao = ((DotacaoFechamentoExercicio) o).getContaDeDestinacao();
                return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
            }
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.EXECUCAO_RECURSO)) {
            if (o instanceof FonteDeRecursoFechamentoExercicio) {
                ContaDeDestinacao contaDeDestinacao = ((FonteDeRecursoFechamentoExercicio) o).getContaDeDestinacao();
                return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
            }
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.OBRIGACAO_APAGAR)) {
            if (o instanceof DesdobramentoObrigacaoPagar) {
                ContaDeDestinacao contaDeDestinacao = ((DesdobramentoObrigacaoPagar) o).getObrigacaoAPagar().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao();
                return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
            } else if (o instanceof DesdobramentoObrigacaoAPagarEstorno) {
                ContaDeDestinacao contaDeDestinacao = ((DesdobramentoObrigacaoAPagarEstorno) o).getObrigacaoAPagarEstorno().getObrigacaoAPagar().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao();
                return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
            }
        }
        throw new Exception("IMPLEMENTAR PARA A CLASSE " + o.getClass().getSimpleName() + " E TIPOMOVIMENTO TCE = FONTE DE RECURSOS e Evento.: " + buscarTipoEventoContabil(lancamentoContabil).getTipoEventoContabil());
    }

    private String montarConteudoContaCorrenteFonteDeRecursosCreditoAdicional(Object o) {
        if (o instanceof SuplementacaoORC) {
            ContaDeDestinacao contaDeDestinacao = (ContaDeDestinacao) ((SuplementacaoORC) o).getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos();
            return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
        }
        if (o instanceof AnulacaoORC) {
            ContaDeDestinacao contaDeDestinacao = (ContaDeDestinacao) ((AnulacaoORC) o).getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos();
            return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
        }
        if (o instanceof EstornoSuplementacaoOrc) {
            ContaDeDestinacao contaDeDestinacao = (ContaDeDestinacao) ((EstornoSuplementacaoOrc) o).getSuplementacaoORC().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos();
            return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
        }
        if (o instanceof EstornoAnulacaoOrc) {
            ContaDeDestinacao contaDeDestinacao = (ContaDeDestinacao) ((EstornoAnulacaoOrc) o).getAnulacaoORC().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos();
            return montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao);
        }
        return null;
    }

    private ParametroEvento.ComplementoId buscarComplementoId(LancamentoContabil lancamentoContabil) {
        return lancamentoContabil.getItemParametroEvento().getParametroEvento().getComplementoId();
    }

    private String montarConteudoContaMovimentacaoFinanceira(LancamentoContabil lancamentoContabil, Object o) throws Exception {
        if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.AJUSTE_ATIVO_DISPONIVEL)) {
            return montarConteudoContaMovimentacaoFinanceira(((AjusteAtivoDisponivel) o).getContaFinanceira(), "", null, ((AjusteAtivoDisponivel) o).getContaDeDestinacao());

        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.RECEITA_EXTRA_ORCAMENTARIA)) {
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                return montarConteudoContaMovimentacaoFinanceira(((ReceitaExtra) o).getSubConta(), "", null, ((ReceitaExtra) o).getContaDeDestinacao());
            } else {
                return montarConteudoContaMovimentacaoFinanceira(((ReceitaExtraEstorno) o).getReceitaExtra().getSubConta(), "", null, ((ReceitaExtraEstorno) o).getReceitaExtra().getContaDeDestinacao());
            }

        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.DESPESA_EXTRA_ORCAMENTARIA)) {
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                return montarConteudoContaMovimentacaoFinanceira(((PagamentoExtra) o).getSubConta(), "", ((PagamentoExtra) o).getTipoDocumento(), ((PagamentoExtra) o).getContaDeDestinacao());
            } else {
                return montarConteudoContaMovimentacaoFinanceira(((PagamentoExtraEstorno) o).getPagamentoExtra().getSubConta(), "", ((PagamentoExtraEstorno) o).getPagamentoExtra().getTipoDocumento(), ((PagamentoExtraEstorno) o).getPagamentoExtra().getContaDeDestinacao());
            }

        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.LIBERACAO_FINANCEIRA)) {
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                ParametroEvento.ComplementoId complementoId = buscarComplementoId(lancamentoContabil);
                LiberacaoCotaFinanceira liberacao = ((LiberacaoCotaFinanceira) o);
                if (complementoId.equals(ParametroEvento.ComplementoId.RECEBIDO)) {
                    return montarConteudoContaMovimentacaoFinanceira(liberacao.getContaFinanceiraRecebida(), "", liberacao.getTipoDocumento(), liberacao.getContaDeDestinacaoRecebida());
                } else if (complementoId.equals(ParametroEvento.ComplementoId.CONCEDIDO)) {
                    return montarConteudoContaMovimentacaoFinanceira(liberacao.getContaFinanceiraRetirada(), "", liberacao.getTipoDocumento(), liberacao.getContaDeDestinacaoRetirada());
                }
            } else {
                LiberacaoCotaFinanceira liberacaoEstorno = ((EstornoLibCotaFinanceira) o).getLiberacao();
                ParametroEvento.ComplementoId complementoId = buscarComplementoId(lancamentoContabil);
                if (complementoId.equals(ParametroEvento.ComplementoId.RECEBIDO)) {
                    return montarConteudoContaMovimentacaoFinanceira(liberacaoEstorno.getContaFinanceiraRecebida(), "", liberacaoEstorno.getTipoDocumento(), liberacaoEstorno.getContaDeDestinacaoRecebida());
                } else if (complementoId.equals(ParametroEvento.ComplementoId.CONCEDIDO)) {
                    return montarConteudoContaMovimentacaoFinanceira(liberacaoEstorno.getContaFinanceiraRetirada(), "", liberacaoEstorno.getTipoDocumento(), liberacaoEstorno.getContaDeDestinacaoRetirada());
                }
            }

        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.PAGAMENTO_DESPESA)) {
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                Pagamento pagamento = ((Pagamento) o);
                ContaDeDestinacao contaDeDestinacao = (ContaDeDestinacao) pagamento.getLiquidacao().getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos();
                return montarConteudoContaMovimentacaoFinanceira(pagamento.getSubConta(), pagamento.getNumDocumento(), pagamento.getTipoDocPagto(), contaDeDestinacao);
            } else {
                PagamentoEstorno pagamentoEstorno = ((PagamentoEstorno) o);
                ContaDeDestinacao contaDeDestinacao = (ContaDeDestinacao) pagamentoEstorno.getPagamento().getLiquidacao().getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos();
                return montarConteudoContaMovimentacaoFinanceira(pagamentoEstorno.getPagamento().getSubConta(), pagamentoEstorno.getPagamento().getNumDocumento(), pagamentoEstorno.getPagamento().getTipoDocPagto(), contaDeDestinacao);
            }

        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.PAGAMENTO_RESTO_PAGAR)) {
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                Pagamento pagamento = ((Pagamento) o);
                ContaDeDestinacao contaDeDestinacao = (ContaDeDestinacao) pagamento.getLiquidacao().getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos();
                return montarConteudoContaMovimentacaoFinanceira(pagamento.getSubConta(), pagamento.getNumDocumento(), pagamento.getTipoDocPagto(), contaDeDestinacao);
            } else {
                PagamentoEstorno pagamentoEstorno = ((PagamentoEstorno) o);
                ContaDeDestinacao contaDeDestinacao = (ContaDeDestinacao) pagamentoEstorno.getPagamento().getLiquidacao().getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos();
                return montarConteudoContaMovimentacaoFinanceira(pagamentoEstorno.getPagamento().getSubConta(), pagamentoEstorno.getPagamento().getNumDocumento(), pagamentoEstorno.getPagamento().getTipoDocPagto(), contaDeDestinacao);
            }

        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.RECEITA_REALIZADA)) {
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                return montarConteudoContaMovimentacaoFinanceira(((LancReceitaFonte) o).getLancReceitaOrc().getSubConta(), "", null, ((LancReceitaFonte) o).getReceitaLoaFonte().getDestinacaoDeRecursos());
            } else {
                return montarConteudoContaMovimentacaoFinanceira(((ReceitaORCFonteEstorno) o).getReceitaORCEstorno().getContaFinanceira(), "", null, ((ReceitaORCFonteEstorno) o).getReceitaLoaFonte().getDestinacaoDeRecursos());
            }

        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.TRANSFERENCIA_FINANCEIRA)) {
            ParametroEvento.ComplementoId complementoId = buscarComplementoId(lancamentoContabil);
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                TransferenciaContaFinanceira transf = ((TransferenciaContaFinanceira) o);
                if (complementoId.equals(ParametroEvento.ComplementoId.CONCEDIDO)) {
                    return montarConteudoContaMovimentacaoFinanceira(transf.getSubContaRetirada(), "", transf.getTipoDocumento(), transf.getContaDeDestinacaoRetirada());
                } else if (complementoId.equals(ParametroEvento.ComplementoId.RECEBIDO)) {
                    return montarConteudoContaMovimentacaoFinanceira(transf.getSubContaDeposito(), "", transf.getTipoDocumento(), transf.getContaDeDestinacaoDeposito());
                }
            } else {
                TransferenciaContaFinanceira transfEstorno = ((EstornoTransferencia) o).getTransferencia();
                if (complementoId.equals(ParametroEvento.ComplementoId.CONCEDIDO)) {
                    return montarConteudoContaMovimentacaoFinanceira(transfEstorno.getSubContaRetirada(), "", transfEstorno.getTipoDocumento(), transfEstorno.getContaDeDestinacaoRetirada());
                } else if (complementoId.equals(ParametroEvento.ComplementoId.RECEBIDO)) {
                    return montarConteudoContaMovimentacaoFinanceira(transfEstorno.getSubContaDeposito(), "", transfEstorno.getTipoDocumento(), transfEstorno.getContaDeDestinacaoDeposito());
                }
            }

        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.TRANSFERENCIA_MESMA_UNIDADE)) {
            ParametroEvento.ComplementoId complementoId = buscarComplementoId(lancamentoContabil);
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                TransferenciaMesmaUnidade transf = ((TransferenciaMesmaUnidade) o);
                if (complementoId.equals(ParametroEvento.ComplementoId.CONCEDIDO)) {
                    return montarConteudoContaMovimentacaoFinanceira(transf.getSubContaRetirada(), "", transf.getTipoDocPagto(), transf.getContaDeDestinacaoRetirada());
                } else if (complementoId.equals(ParametroEvento.ComplementoId.RECEBIDO)) {
                    return montarConteudoContaMovimentacaoFinanceira(transf.getSubContaDeposito(), "", transf.getTipoDocPagto(), transf.getContaDeDestinacaoDeposito());
                }
            } else {
                TransferenciaMesmaUnidade transfEstorno = ((EstornoTransfMesmaUnidade) o).getTransferenciaMesmaUnidade();
                if (complementoId.equals(ParametroEvento.ComplementoId.CONCEDIDO)) {
                    return montarConteudoContaMovimentacaoFinanceira(transfEstorno.getSubContaRetirada(), "", transfEstorno.getTipoDocPagto(), transfEstorno.getContaDeDestinacaoRetirada());
                } else if (complementoId.equals(ParametroEvento.ComplementoId.RECEBIDO)) {
                    return montarConteudoContaMovimentacaoFinanceira(transfEstorno.getSubContaDeposito(), "", transfEstorno.getTipoDocPagto(), transfEstorno.getContaDeDestinacaoDeposito());
                }
            }
        }
        throw new Exception("IMPLEMENTAR PARA A CLASSE " + o.getClass().getSimpleName() + " E TIPOMOVIMENTO TCE = MOVIMENTACAO FINANCEIRA");
    }

    private String montarConteudoContaCorrenteCredor(LancamentoContabil lancamentoContabil, Object o) throws Exception {
        if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.RESPONSABILIDADE_VTB)) {
            Pessoa pessoa = ((ResponsabilidadeVTB) o).getPessoa();
            return montarConteudoContaCorrenteCredor(pessoa);
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.DIARIA_CAMPO)
            || verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.DIARIAS_CIVIL)) {
            Pessoa pessoa = ((DiariaContabilizacao) o).getPropostaConcessaoDiaria().getPessoaFisica();
            return montarConteudoContaCorrenteCredor(pessoa);
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.SUPRIMENTO_FUNDO)) {
            if (o instanceof DiariaContabilizacao) {
                Pessoa pessoa = ((DiariaContabilizacao) o).getPropostaConcessaoDiaria().getPessoaFisica();
                return montarConteudoContaCorrenteCredor(pessoa);
            }
            if (o instanceof DesdobramentoDiaria) {
                Pessoa pessoa = ((DesdobramentoDiaria) o).getDiariaContabilizacao().getPropostaConcessaoDiaria().getPessoaFisica();
                return montarConteudoContaCorrenteCredor(pessoa);
            }
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.DESPESA_EXTRA_ORCAMENTARIA)) {
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                Pessoa pessoa = ((PagamentoExtra) o).getFornecedor();
                return montarConteudoContaCorrenteCredor(pessoa);
            } else {
                Pessoa pessoa = ((PagamentoExtraEstorno) o).getPagamentoExtra().getFornecedor();
                return montarConteudoContaCorrenteCredor(pessoa);
            }
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.RECEITA_EXTRA_ORCAMENTARIA)) {
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                Pessoa pessoa = ((ReceitaExtra) o).getPessoa();
                return montarConteudoContaCorrenteCredor(pessoa);
            } else {
                Pessoa pessoa = ((ReceitaExtraEstorno) o).getReceitaExtra().getPessoa();
                return montarConteudoContaCorrenteCredor(pessoa);
            }
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.EMPENHO_DESPESA)
            || verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.RESTO_PAGAR)
            || verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.APURACAO)
            || verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.CANCELAMENTO_RESTO_PAGAR)
        ) {
            if (o instanceof Empenho) {
                Pessoa pessoa = ((Empenho) o).getFornecedor();
                return montarConteudoContaCorrenteCredor(pessoa);
            } else if (o instanceof EmpenhoEstorno) {
                Pessoa pessoa = ((EmpenhoEstorno) o).getEmpenho().getFornecedor();
                return montarConteudoContaCorrenteCredor(pessoa);
            } else if (o instanceof DesdobramentoEmpenho) {
                Pessoa pessoa = ((DesdobramentoEmpenho) o).getEmpenho().getFornecedor();
                return montarConteudoContaCorrenteCredor(pessoa);
            } else if (o instanceof DesdobramentoEmpenhoEstorno) {
                Pessoa pessoa = ((DesdobramentoEmpenhoEstorno) o).getEmpenhoEstorno().getEmpenho().getFornecedor();
                return montarConteudoContaCorrenteCredor(pessoa);
            }
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.CANCELAMENTO_RESTO_PAGAR)) {
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                Pessoa pessoa = ((EmpenhoEstorno) o).getEmpenho().getFornecedor();
                return montarConteudoContaCorrenteCredor(pessoa);
            }
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.LIQUIDACAO_DESPESA)
            || verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.LIQUIDACAO_RESTO_PAGAR)) {
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                Pessoa pessoa = ((Desdobramento) o).getLiquidacao().getEmpenho().getFornecedor();
                return montarConteudoContaCorrenteCredor(pessoa);
            } else {
                Pessoa pessoa = ((DesdobramentoLiquidacaoEstorno) o).getLiquidacaoEstorno().getLiquidacao().getEmpenho().getFornecedor();
                return montarConteudoContaCorrenteCredor(pessoa);
            }
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.PAGAMENTO_DESPESA)
            || verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.PAGAMENTO_RESTO_PAGAR)) {
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                Pessoa pessoa = ((Pagamento) o).getLiquidacao().getEmpenho().getFornecedor();
                return montarConteudoContaCorrenteCredor(pessoa);
            } else {
                Pessoa pessoa = ((PagamentoEstorno) o).getPagamento().getLiquidacao().getEmpenho().getFornecedor();
                return montarConteudoContaCorrenteCredor(pessoa);
            }
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.DIVIDA_PUBLICA)) {
            Pessoa pessoa = ((MovimentoDividaPublica) o).getDividaPublica().getPessoa();
            return montarConteudoContaCorrenteCredor(pessoa);
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.RECEITA_REALIZADA)) {
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                Pessoa pessoa = ((LancReceitaFonte) o).getLancReceitaOrc().getPessoa();
                return montarConteudoContaCorrenteCredor(pessoa);
            } else {
                Pessoa pessoa = ((ReceitaORCFonteEstorno) o).getReceitaORCEstorno().getPessoa();
                return montarConteudoContaCorrenteCredor(pessoa);
            }

        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.PROVISAO_MATEMATICA_PREVIDENCIARIA)) {
            Pessoa pessoa = ((ProvAtuarialMatematica) o).getDividaPublica().getPessoa();
            return montarConteudoContaCorrenteCredor(pessoa);
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.AJUSTE_DEPOSITO)) {
            Pessoa pessoa = ((AjusteDeposito) o).getPessoa();
            return montarConteudoContaCorrenteCredor(pessoa);
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.AJUSTE_ATIVO_DISPONIVEL)) {
            Pessoa pessoa = ((AjusteAtivoDisponivel) o).getPessoa();
            return montarConteudoContaCorrenteCredor(pessoa);
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.OBRIGACAO_APAGAR)) {
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                Pessoa pessoa = ((DesdobramentoObrigacaoPagar) o).getObrigacaoAPagar().getPessoa();
                return montarConteudoContaCorrenteCredor(pessoa);
            } else {
                Pessoa pessoa = ((DesdobramentoObrigacaoAPagarEstorno) o).getObrigacaoAPagarEstorno().getObrigacaoAPagar().getPessoa();
                return montarConteudoContaCorrenteCredor(pessoa);
            }
        }
        throw new Exception("IMPLEMENTAR PARA A CLASSE " + o.getClass().getSimpleName() + " E TIPOMOVIMENTO TCE = CREDOR");
    }

    private String montarConteudoContaRestoAPagar(LancamentoContabil lancamentoContabil, Object o) {
        StringBuilder retorno = new StringBuilder();
        if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.RESTO_PAGAR)) {
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                if (o instanceof Empenho) {
                    return montarConteudoContaRestoAPagar(((Empenho) o).getEmpenho() != null ? ((Empenho) o).getEmpenho() : (Empenho) o);
                } else if (o instanceof DespesaFechamentoExercicio) {
                    return montarConteudoContaRestoAPagar(((DespesaFechamentoExercicio) o).getEmpenho());
                } else if (o instanceof InscricaoEmpenho) {
                    return montarConteudoContaRestoAPagar(((InscricaoEmpenho) o).getEmpenhoInscrito());
                }
            }
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.CANCELAMENTO_RESTO_PAGAR)) {
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                return montarConteudoContaRestoAPagar(((EmpenhoEstorno) o).getEmpenho().getEmpenho());
            }
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.APURACAO)) {
            if (o instanceof Empenho) {
                return montarConteudoContaRestoAPagar(((Empenho) o).getEmpenho());
            } else {
                return montarConteudoContaRestoAPagar(((EmpenhoEstorno) o).getEmpenho().getEmpenho());
            }
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.LIQUIDACAO_RESTO_PAGAR)) {
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                return montarConteudoContaRestoAPagar(((Desdobramento) o).getLiquidacao().getEmpenho().getEmpenho());
            } else {
                return montarConteudoContaRestoAPagar(((DesdobramentoLiquidacaoEstorno) o).getLiquidacaoEstorno().getLiquidacao().getEmpenho().getEmpenho());
            }
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.PAGAMENTO_RESTO_PAGAR)) {
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                return montarConteudoContaRestoAPagar(((Pagamento) o).getLiquidacao().getEmpenho().getEmpenho());
            } else {
                return montarConteudoContaRestoAPagar(((PagamentoEstorno) o).getPagamento().getLiquidacao().getEmpenho().getEmpenho());
            }
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.RECEITA_EXTRA_ORCAMENTARIA)) {
            if (verificarLancamentoNormal(buscarTipoEventoContabil(lancamentoContabil))) {
                return montarConteudoContaRestoAPagar(((ReceitaExtra) o).getRetencaoPgto().getPagamento().getLiquidacao().getEmpenho().getEmpenho());
            } else {
                return montarConteudoContaRestoAPagar(((ReceitaExtraEstorno) o).getReceitaExtra().getRetencaoPgto().getPagamento().getLiquidacao().getEmpenho().getEmpenho());
            }
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.ABERTURA)) {
            return montarConteudoContaRestoAPagar(((AberturaInscricaoResto) o).getEmpenho());
        } else if (verificarTipoEventoContabil(lancamentoContabil, TipoEventoContabil.OBRIGACAO_APAGAR)) {
            if (o instanceof DesdobramentoObrigacaoPagar) {
                return montarConteudoContaRestoAPagar(((DesdobramentoObrigacaoPagar) o).getObrigacaoAPagar().getEmpenho().getEmpenho() != null ?
                    ((DesdobramentoObrigacaoPagar) o).getObrigacaoAPagar().getEmpenho().getEmpenho() : ((DesdobramentoObrigacaoPagar) o).getObrigacaoAPagar().getEmpenho());
            } else if (o instanceof DesdobramentoObrigacaoAPagarEstorno) {
                return montarConteudoContaRestoAPagar(((DesdobramentoObrigacaoAPagarEstorno) o).getObrigacaoAPagarEstorno().getObrigacaoAPagar().getEmpenho().getEmpenho() != null ?
                    ((DesdobramentoObrigacaoAPagarEstorno) o).getObrigacaoAPagarEstorno().getObrigacaoAPagar().getEmpenho().getEmpenho() : ((DesdobramentoObrigacaoAPagarEstorno) o).getObrigacaoAPagarEstorno().getObrigacaoAPagar().getEmpenho());
            }
        }
        return retorno.toString();
    }

    private String montarConteudoContaRestoAPagar(Empenho empenho) {
        StringBuilder retorno = new StringBuilder();

        AcaoPPA acaoPPA = empenho.getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
        ContaDeDestinacao contaDeDestinacao = empenho.getContaDeDestinacao();

        montarConteudoContaCorrenteFuncaoSubFuncao(retorno, acaoPPA.getFuncao(), acaoPPA.getSubFuncao());
        montarConteudoContaCorrenteContaDespesa(retorno, empenho.getCodigoContaTCE(), 6);

        retorno.append(montarConteudoContaCorrenteContaDeDestinacao(contaDeDestinacao));
        //ANO EMPENHO
        retorno.append(StringUtil.cortarOuCompletarEsquerda((empenho.getEmpenho() != null ? empenho.getEmpenho() : empenho).getExercicio().getAno().toString(), 4, "0"));
        //NUMERO EMPENHO
        retorno.append(StringUtil.cortarOuCompletarEsquerda((empenho.getEmpenho() != null ? empenho.getEmpenho() : empenho).getNumero(), 12, "0"));

        return retorno.toString();
    }

    private String montarConteudoContaMovimentacaoFinanceira(SubConta contaFinanceira, String numeroDocumento, TipoDocPagto tipoDocPagto, ContaDeDestinacao contaDeDestinacao) {
        StringBuilder retorno = new StringBuilder();

        ContaBancariaEntidade contaBancariaEntidade = contaFinanceira.getContaBancariaEntidade();
        Agencia agencia = contaBancariaEntidade.getAgencia();
        Banco banco = agencia.getBanco();

        //NÚMERO BANCO
        retorno.append(StringUtil.cortaOuCompletaEsquerda(banco.getNumeroBanco(), 6, " "));
        //NÚMERO AGENCIA
        retorno.append(StringUtil.cortaOuCompletaEsquerda(agencia.getNumeroAgencia() + agencia.getDigitoVerificador(), 6, " "));
        //NúMERO CONTA BANCARIA
        retorno.append(StringUtil.cortaOuCompletaEsquerda(contaBancariaEntidade.getNumeroConta() + contaBancariaEntidade.getDigitoVerificador(), 12, " "));
        //TIPO CONTA BANCARIA
        String tipoContaBancaria = "";
        if (contaBancariaEntidade.getTipoContaBancaria().equals(TipoContaBancaria.CORRENTE)) {
            tipoContaBancaria = "1";
        } else if (contaBancariaEntidade.getTipoContaBancaria().equals(TipoContaBancaria.APLICACAO_CORRENTE)) {
            tipoContaBancaria = "2";
        } else if (contaBancariaEntidade.getTipoContaBancaria().equals(TipoContaBancaria.POUPANCA)) {
            tipoContaBancaria = "3";
        } else if (contaBancariaEntidade.getTipoContaBancaria().equals(TipoContaBancaria.SALARIO)) {
            tipoContaBancaria = "4";
        } else if (contaBancariaEntidade.getTipoContaBancaria().equals(TipoContaBancaria.VINCULADA)) {
            tipoContaBancaria = "5";
        } else if (contaBancariaEntidade.getTipoContaBancaria().equals(TipoContaBancaria.APLICACAO_VINCULADA)) {
            tipoContaBancaria = "6";
        }
        retorno.append(StringUtil.cortaOuCompletaEsquerda(tipoContaBancaria, 1, "0"));
        //NUMERO DO DOCUMENTO
        retorno.append(StringUtil.cortaOuCompletaEsquerda(numeroDocumento, 15, "0"));
        //TIPO DE DOCUMENTO
        String tipoDocumento = "3";
        if (tipoDocPagto != null) {
            if (tipoDocPagto.equals(TipoDocPagto.ORDEMPAGAMENTO)) {
                tipoDocumento = "1";
            } else if (tipoDocPagto.equals(TipoDocPagto.CHEQUE)) {
                tipoDocumento = "2";
            }
        }
        retorno.append(StringUtil.cortaOuCompletaEsquerda(tipoDocumento, 1, "0"));
        retorno.append(StringUtil.cortaOuCompletaEsquerda(contaDeDestinacao.getCodigoTCE(), 3, "1"));
        return retorno.toString();
    }

    private String montarConteudoContaCorrenteCredor(Pessoa pessoa) {
        StringBuilder retorno = new StringBuilder();
        //CPF OU CNPJ CREDOR
        if (pessoa.getCpf_Cnpj() == null || pessoa.getCpf_Cnpj().trim().isEmpty()) {
            throw new ExcecaoNegocioGenerica("A pessoa " + pessoa.toString() + " não possui CPF/CPNJ informado.");
        } else {
            retorno.append(StringUtil.cortaOuCompletaEsquerda(pessoa.getCpf_Cnpj().replace(".", "").replace("-", "").replace("/", ""), 14, pessoa instanceof PessoaFisica ? " " : "0"));
        }
        //TIPO CREDOR
        retorno.append(StringUtil.cortaOuCompletaEsquerda((pessoa instanceof PessoaJuridica ? "2" : "1"), 1, "0"));
        return retorno.toString();
    }

    private String montarConteudoContaContratos(Contrato contrato) {
        StringBuilder retorno = new StringBuilder();
        //CONTRATO
        retorno.append(StringUtil.cortaOuCompletaEsquerda(contrato.getNumeroProcesso() + "", 12, "0"));
        retorno.append(StringUtil.cortaOuCompletaEsquerda(contrato.getLicitacao().getExercicio().getAno().toString(), 12, "0"));
        return retorno.toString();
    }

    private String montarConteudoContaContratosPassivos(Contrato contrato) {
        StringBuilder retorno = new StringBuilder();
        //DIVIDA PUBLICA
        retorno.append(StringUtil.cortaOuCompletaEsquerda(contrato.getNumeroProcesso() + "", 12, "0"));
        retorno.append(StringUtil.cortaOuCompletaEsquerda(contrato.getLicitacao().getExercicio().getAno().toString(), 4, "0"));

        retorno.append(StringUtil.cortaOuCompletaEsquerda(contrato.getLicitacao().getExercicio().getAno().toString(), 4, "0"));
        retorno.append(StringUtil.cortaOuCompletaEsquerda(contrato.getLicitacao().getNumero() + "", 12, "0"));
        return retorno.toString();
    }

    private String montarConteudoContaCorrenteContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        StringBuilder retorno = new StringBuilder();
        montarConteudoContaCorrenteVariaveisContaDeDestinacao(retorno, contaDeDestinacao);
        return retorno.toString();
    }

    private String montarConteudoContaCorrenteReceitaOrcamentariaReceitaRealizada(ReceitaLOAFonte receitaLOAFonte) {
        Conta contaDeReceita = receitaLOAFonte.getReceitaLOA().getContaDeReceita();
        return montarConteudoContaCorrenteReceitaOrcamentariaContaReceitaEFonteDeRecursos(contaDeReceita, receitaLOAFonte.getDestinacaoDeRecursos());
    }

    private String montarConteudoContaCorrenteReceitaOrcamentariaPrevisaoInicialReceita(ReceitaLOAFonte receitaLOAFonte) {
        Conta contaDeReceita = receitaLOAFonte.getReceitaLOA().getContaDeReceita();
        return montarConteudoContaCorrenteReceitaOrcamentariaContaReceitaEFonteDeRecursos(contaDeReceita, receitaLOAFonte.getDestinacaoDeRecursos());
    }

    private String montarConteudoContaCorrenteReceitaOrcamentariaPrevisaoAdicionalReceita(ReceitaAlteracaoORC receitaAlteracaoORC) {
        Conta contaDeReceita = receitaAlteracaoORC.getReceitaLOA().getContaDeReceita();
        return montarConteudoContaCorrenteReceitaOrcamentariaContaReceitaEFonteDeRecursos(contaDeReceita, receitaAlteracaoORC.getContaDeDestinacao());
    }

    private String montarConteudoContaCorrenteReceitaOrcamentariaContaReceitaEFonteDeRecursos(Conta contaDeReceita, ContaDeDestinacao contaDeDestinacao) {
        StringBuilder retorno = new StringBuilder();
        //NATUREZA DA RECEITA
        String codigoConta = contaDeReceita.getCodigo();
        if (contaDeReceita.getCodigoTCE() != null) {
            if (!contaDeReceita.getCodigoTCE().trim().isEmpty()) {
                String codigo = StringUtil.cortaOuCompletaDireita(contaDeReceita.getCodigoTCE(), 10, "0");
                codigoConta = Util.formatarValorComMascara(codigo, "#.#.#.#.##.##.##.##", ".");
            }
        }
        retorno.append(StringUtil.cortaOuCompletaDireita(codigoConta.replace(".", ""), 10, "0"));
        montarConteudoContaCorrenteVariaveisContaDeDestinacao(retorno, contaDeDestinacao);
        return retorno.toString();
    }

    private String montarConteudoContaCorrenteCreditoAdicionalEmpenhoEstorno(EmpenhoEstorno empenhoEstorno, String codigoContaTCE) {
        StringBuilder retorno = new StringBuilder();
        montarConteudoContaCorrenteVariaveisOrcamento(retorno, empenhoEstorno.getEmpenho().getDespesaORC().getProvisaoPPADespesa(), codigoContaTCE, 6);
        montarConteudoContaCorrenteVariaveisContaDeDestinacao(retorno, empenhoEstorno.getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte());
        montarConteudoContaCorrenteVariaveisUnidadeOgao(retorno, empenhoEstorno.getUnidadeOrganizacional());
        montarConteudoContaCorrenteTipoContaCorrenteTCE(retorno, empenhoEstorno.getEmpenho());
        return retorno.toString();
    }

    private String montarConteudoContaCorrenteCreditoAdicionalEmpenho(Empenho empenho, String codigoContaTCE) {
        StringBuilder retorno = new StringBuilder();
        montarConteudoContaCorrenteVariaveisOrcamento(retorno, empenho.getDespesaORC().getProvisaoPPADespesa(), codigoContaTCE, 6);
        montarConteudoContaCorrenteVariaveisContaDeDestinacao(retorno, empenho.getFonteDespesaORC().getProvisaoPPAFonte());
        montarConteudoContaCorrenteVariaveisUnidadeOgao(retorno, empenho.getUnidadeOrganizacional());
        montarConteudoContaCorrenteTipoContaCorrenteTCE(retorno, empenho);
        return retorno.toString();
    }

    private String montarConteudoContaCorrenteCreditoAdicionalSuplementacao(SuplementacaoORC suplementacaoORC) {
        StringBuilder retorno = new StringBuilder();
        montarConteudoContaCorrenteVariaveisOrcamento(retorno, suplementacaoORC.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa(), suplementacaoORC.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getCodigoTCEOrCodigoSemPonto(), 6);
        montarConteudoContaCorrenteVariaveisContaDeDestinacao(retorno, suplementacaoORC.getFonteDespesaORC().getProvisaoPPAFonte());
        montarConteudoContaCorrenteVariaveisUnidadeOgao(retorno, suplementacaoORC.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getUnidadeOrganizacional());
        montarConteudoContaCorrenteTipoContaCorrenteTCE(retorno, suplementacaoORC.getFonteDespesaORC().getProvisaoPPAFonte());
        return retorno.toString();
    }

    private String montarConteudoContaCorrenteCreditoAdicionalAnulacao(AnulacaoORC anulacaoORC) {
        StringBuilder retorno = new StringBuilder();
        montarConteudoContaCorrenteVariaveisOrcamento(retorno, anulacaoORC.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa(), anulacaoORC.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getCodigoTCEOrCodigoSemPonto(), 6);
        montarConteudoContaCorrenteVariaveisContaDeDestinacao(retorno, anulacaoORC.getFonteDespesaORC().getProvisaoPPAFonte());
        montarConteudoContaCorrenteVariaveisUnidadeOgao(retorno, anulacaoORC.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getUnidadeOrganizacional());
        montarConteudoContaCorrenteTipoContaCorrenteTCE(retorno, anulacaoORC.getFonteDespesaORC().getProvisaoPPAFonte());
        return retorno.toString();
    }

    private String montarConteudoContaCorrenteDotacaoDespesa(ProvisaoPPAFonte provisaoPPAFonte) {
        StringBuilder retorno = new StringBuilder();
        montarConteudoContaCorrenteVariaveisOrcamento(retorno, provisaoPPAFonte.getProvisaoPPADespesa(), provisaoPPAFonte.getProvisaoPPADespesa().getContaDeDespesa().getCodigoTCEOrCodigoSemPonto(), 6);
        montarConteudoContaCorrenteVariaveisContaDeDestinacao(retorno, provisaoPPAFonte);
        montarConteudoContaCorrenteVariaveisUnidadeOgao(retorno, provisaoPPAFonte.getProvisaoPPADespesa().getUnidadeOrganizacional());
        montarConteudoContaCorrenteTipoContaCorrenteTCE(retorno, provisaoPPAFonte);
        return retorno.toString();
    }

    private String montarConteudoContaCorrenteDotacaoDespesa(ContaLancamentoManual contaLancamentoManual) {
        StringBuilder retorno = new StringBuilder();
        montarConteudoContaCorrenteVariaveisOrcamento(retorno, contaLancamentoManual);
        montarConteudoContaCorrenteVariaveisContaDeDestinacao(retorno, contaLancamentoManual.getContaDeDestinacao());
        montarConteudoContaCorrenteVariaveisUnidadeOgao(retorno, contaLancamentoManual.getLancamento().getUnidadeOrganizacional());
        if (contaLancamentoManual.getLancamento().getExtensaoFonteRecurso() != null) {
            retorno.append(contaLancamentoManual.getLancamento().getExtensaoFonteRecurso().getTipoContaCorrenteTCE().getCodigo());
        }
        return retorno.toString();
    }

    private String montarConteudoContaCorrenteEmpenhoNormal(Empenho empenho, String codigoContaTCE) {
        StringBuilder retorno = new StringBuilder();
        montarConteudoContaCorrenteVariaveisOrcamento(retorno, empenho.getDespesaORC().getProvisaoPPADespesa(), codigoContaTCE, 8);
        montarConteudoContaCorrenteVariaveisContaDeDestinacao(retorno, empenho.getFonteDespesaORC().getProvisaoPPAFonte());
        montarConteudoContaCorrenteVariaveisUnidadeOgao(retorno, empenho.getUnidadeOrganizacional());
        String tipoEmpenho = "";
        if (CategoriaOrcamentaria.NORMAL.equals(empenho.getCategoriaOrcamentaria())) {
            if (TipoEmpenho.ORDINARIO.equals(empenho.getTipoEmpenho())) {
                tipoEmpenho = "1";
            } else if (TipoEmpenho.ESTIMATIVO.equals(empenho.getTipoEmpenho())) {
                tipoEmpenho = "2";
            } else if (TipoEmpenho.GLOBAL.equals(empenho.getTipoEmpenho())) {
                tipoEmpenho = "3";
            }
        } else if (CategoriaOrcamentaria.RESTO.equals(empenho.getCategoriaOrcamentaria())) {
            tipoEmpenho = "5";
        }
        Exercicio exercicio = empenho.getExercicio();
        String numero = empenho.getNumero();
        if (CategoriaOrcamentaria.RESTO.equals(empenho.getCategoriaOrcamentaria())) {
            exercicio = empenho.getEmpenho().getExercicio();
            numero = empenho.getEmpenho().getNumero();
        }
        montarConteudoContaCorrenteVariaveiAnoNumeroTIpoPessoa(retorno, exercicio.getAno().toString(), numero, tipoEmpenho, empenho.getFornecedor(), "000000000000");
        montarConteudoContaCorrenteTipoContaCorrenteTCE(retorno, empenho);
        return retorno.toString();
    }

    private String montarConteudoContaCorrenteEmpenhoEstorno(EmpenhoEstorno o, String codigoContaTCE) {
        StringBuilder retorno = new StringBuilder();
        montarConteudoContaCorrenteVariaveisOrcamento(retorno, o.getEmpenho().getDespesaORC().getProvisaoPPADespesa(), codigoContaTCE, 8);
        montarConteudoContaCorrenteVariaveisContaDeDestinacao(retorno, o.getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte());
        montarConteudoContaCorrenteVariaveisUnidadeOgao(retorno, o.getEmpenho().getUnidadeOrganizacional());
        String tipoEmpenho = "";
        if (o.getCategoriaOrcamentaria().equals(CategoriaOrcamentaria.NORMAL)) {
            tipoEmpenho = "4";
        } else if (o.getCategoriaOrcamentaria().equals(CategoriaOrcamentaria.RESTO)) {
            tipoEmpenho = "5";
        }
        montarConteudoContaCorrenteVariaveiAnoNumeroTIpoPessoa(retorno, o.getEmpenho().getExercicio().getAno().toString(), o.getNumero(), tipoEmpenho, o.getEmpenho().getFornecedor(), o.getEmpenho().getNumero());
        montarConteudoContaCorrenteTipoContaCorrenteTCE(retorno, o.getEmpenho());
        return retorno.toString();
    }

    private Boolean verificarTipoEventoContabil(LancamentoContabil lancamentoContabil, TipoEventoContabil tipoEventoContabil) {
        EventoContabil eventoContabil = buscarTipoEventoContabil(lancamentoContabil);
        if (eventoContabil.getTipoEventoContabil().equals(tipoEventoContabil)) {
            return true;
        }
        return false;
    }

    private EventoContabil buscarTipoEventoContabil(LancamentoContabil lancamentoContabil) {
        return lancamentoContabil.getItemParametroEvento().getParametroEvento().getEventoContabil();
    }

    private Boolean verificarLancamentoNormal(EventoContabil eventoContabil) {
        TipoLancamento tipoLancamento = eventoContabil.getTipoLancamento();
        if (tipoLancamento.equals(TipoLancamento.NORMAL)) {
            return true;
        }
        return false;
    }

    private TipoMovimentoTCE buscarTipoMovimentoTCE(LCP lcp, String naturezaConta) {
        if (naturezaConta != null) {
            if (naturezaConta.equals("C")) {
                return lcp.getTipoMovimentoTCECredito();
            } else if (naturezaConta.equals("D")) {
                return lcp.getTipoMovimentoTCEDebito();
            } else {
                throw new ExcecaoNegocioGenerica("Erro a montar a Tag conteudoContaCorrente, a Natureza da conta está vazia.");
            }
        }
        throw new ExcecaoNegocioGenerica("Não foi encontrado o Tipo de Movimento do TCE para a Natureza '" + naturezaConta + "'.");
    }

    private void montarConteudoContaCorrenteVariaveiAnoNumeroTIpoPessoa(StringBuilder retorno, String ano, String numero, String tipoEmpenho, Pessoa pessoa, String numeroOriginal) {
        //ANO EMPENHO
        retorno.append(StringUtil.cortaOuCompletaEsquerda(ano, 4, "0"));
        //NUMERO EMPENHO
        retorno.append(StringUtil.cortaOuCompletaEsquerda(numero, 12, "0"));
        // TIPO EMPENHO
        retorno.append(StringUtil.cortaOuCompletaEsquerda(tipoEmpenho, 1, "0"));
        //CPF OU CNPJ CREDOR
        if (pessoa.getCpf_Cnpj() == null || pessoa.getCpf_Cnpj().trim().isEmpty()) {
            throw new ExcecaoNegocioGenerica("A pessoa " + pessoa.toString() + " não possui CPF/CPNJ informado.");
        } else {
            retorno.append(StringUtil.cortaOuCompletaEsquerda(pessoa.getCpf_Cnpj().replace(".", "").replace("-", "").replace("/", ""), 14, pessoa instanceof PessoaFisica ? " " : "0"));
        }
        //TIPO CREDOR
        retorno.append(StringUtil.cortaOuCompletaEsquerda((pessoa instanceof PessoaJuridica ? "2" : "1"), 1, "0"));
        //NUMERO DO EMPENHO DE REFERENCIA
        retorno.append(StringUtil.cortaOuCompletaEsquerda(numeroOriginal, 12, "0"));
    }

    private void montarConteudoContaCorrenteVariaveisUnidadeOgao(StringBuilder retorno, UnidadeOrganizacional unidadeOrganizacional) {
        HierarquiaOrganizacional unidade = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(dataOperacao, unidadeOrganizacional, TipoHierarquiaOrganizacional.ORCAMENTARIA);
        HierarquiaOrganizacional orgao = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(dataOperacao, unidade.getSuperior(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        //ORGAO
        retorno.append(StringUtil.cortaOuCompletaEsquerda(orgao.getCodigoNo(), 6, "0"));
        //UNIDADE
        retorno.append(StringUtil.cortaOuCompletaEsquerda(unidade.getCodigoNo(), 6, "0"));
    }

    private void montarConteudoContaCorrenteVariaveisContaDeDestinacao(StringBuilder retorno, ProvisaoPPAFonte provisaoPPAFonte) {
        montarConteudoContaCorrenteVariaveisContaDeDestinacao(retorno, (ContaDeDestinacao) provisaoPPAFonte.getDestinacaoDeRecursos());
    }

    private void montarConteudoContaCorrenteVariaveisContaDeDestinacao(StringBuilder retorno, ContaDeDestinacao contaDeDestinacao) {
        retorno.append(StringUtil.cortaOuCompletaEsquerda(contaDeDestinacao.getCodigoTCE(), 3, "1"));
    }

    private void montarConteudoContaCorrenteVariaveisOrcamento(StringBuilder retorno, ProvisaoPPADespesa provisaoPPADespesa, String codigoConta, Integer tamanho) {
        Funcao funcao = provisaoPPADespesa.getSubAcaoPPA().getAcaoPPA().getFuncao();
        SubFuncao subFuncao = provisaoPPADespesa.getSubAcaoPPA().getAcaoPPA().getSubFuncao();
        ProgramaPPA programa = provisaoPPADespesa.getSubAcaoPPA().getAcaoPPA().getPrograma();
        AcaoPPA acaoPPA = provisaoPPADespesa.getSubAcaoPPA().getAcaoPPA();

        montarConteudoContaCorrenteFuncaoSubFuncao(retorno, funcao, subFuncao);
        //PROGRAMA PPA
        montarConteudoContaCorrenteProgramaPPA(retorno, programa);
        //ACAOPPA
        montarConteudoContaCorrenteAcaoPPA(retorno, acaoPPA);
        montarConteudoContaCorrenteContaDespesa(retorno, codigoConta, tamanho);
    }

    private void montarConteudoContaCorrenteVariaveisOrcamento(StringBuilder retorno, ContaLancamentoManual contaLancamentoManual) {
        montarConteudoContaCorrenteFuncaoSubFuncao(retorno, contaLancamentoManual.getLancamento().getFuncao(), contaLancamentoManual.getLancamento().getSubFuncao());
        montarConteudoContaCorrenteProgramaPPA(retorno, contaLancamentoManual.getLancamento().getProgramaPPA());
        montarConteudoContaCorrenteAcaoPPA(retorno, contaLancamentoManual.getLancamento().getAcaoPPA());
        montarConteudoContaCorrenteContaDespesa(retorno, contaLancamentoManual.getLancamento().getContaDespesa().getCodigoTCEOrCodigoSemPonto(), 6);
    }

    private void montarConteudoContaCorrenteAcaoPPA(StringBuilder retorno, AcaoPPA acaoPPA) {
        retorno.append(StringUtil.cortaOuCompletaEsquerda(acaoPPA.getTipoAcaoPPA().getCodigo() + acaoPPA.getCodigo(), 6, "0"));
    }

    private void montarConteudoContaCorrenteProgramaPPA(StringBuilder retorno, ProgramaPPA programa) {
        retorno.append(StringUtil.cortaOuCompletaEsquerda(programa.getCodigo(), 4, "0"));
    }

    private void montarConteudoContaCorrenteContaDespesa(StringBuilder retorno, String codigoContaTCE, Integer tamanho) {
        retorno.append(StringUtil.cortaOuCompletaDireita(codigoContaTCE, tamanho, "0"));
    }

    private void montarConteudoContaCorrenteFuncaoSubFuncao(StringBuilder retorno, Funcao funcao, SubFuncao subFuncao) {
        //FUNCAO
        retorno.append(StringUtil.cortaOuCompletaEsquerda(funcao.getCodigo(), 2, "0"));
        //SUBFUNCAO
        retorno.append(StringUtil.cortaOuCompletaEsquerda(subFuncao.getCodigo(), 3, "0"));
    }

    private void gerarArquivoLancamento(List<LancamentoContabil> lancamentoContabils, List<LancamentoContabilManual> lancamentosManuais, PrestacaoDeContasItens selecionado) throws ParserConfigurationException, IOException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        Element lista = doc.createElement("lista");
        doc.appendChild(lista);

        if (selecionado.getLancamentosSaldoInicial() != null) {
            for (LancamentoArquivoTCE lancamentoArquivo : selecionado.getLancamentosSaldoInicial()) {
                criarTagLancamentoSaldoInicial(doc, lista, lancamentoArquivo);
            }
        }

        for (LancamentoContabil lancamentoContabil : lancamentoContabils) {
            criarTagLancamentoContabil(doc, lista, lancamentoContabil);
            selecionado.setProcessados(selecionado.getProcessados() + 1);
        }

        for (LancamentoContabilManual lancamentoManual : lancamentosManuais) {
            criarTagLancamentoContabilManual(doc, lista, lancamentoManual);
            selecionado.setProcessados(selecionado.getProcessados() + 1);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        DOMSource source = new DOMSource(doc);
        File arquivo = File.createTempFile("Lancamento", "xml");
        StreamResult result = new StreamResult(arquivo);
        transformer.transform(source, result);
        selecionado.getFiles().put("Lancamento.xml", arquivo);
    }

    private void gerarArquivoNotasFiscais(List<LiquidacaoDoctoFiscal> documentosFiscais, PrestacaoDeContasItens selecionado) throws ParserConfigurationException, IOException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();
        Element lista = doc.createElement("lista");
        doc.appendChild(lista);

        for (LiquidacaoDoctoFiscal documentoFiscal : documentosFiscais) {
            criarTagNotaFiscal(doc, lista, documentoFiscal);
            selecionado.setProcessados(selecionado.getProcessados() + 1);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");

        DOMSource source = new DOMSource(doc);
        File arquivo = File.createTempFile("Notafiscal", "xml");

        StreamResult result = new StreamResult(arquivo);
        transformer.transform(source, result);

        selecionado.getFiles().put("Notafiscal.xml", arquivo);
    }

    private void criarTagNotaFiscal(Document doc, Element lista, LiquidacaoDoctoFiscal liquidacaoDoctoFiscal) {
        Element notaFiscal = doc.createElement("notaFiscal");
        lista.appendChild(notaFiscal);

        criarTag("numeroEmpenho", (liquidacaoDoctoFiscal.getLiquidacao().getEmpenho().getEmpenho() != null ? liquidacaoDoctoFiscal.getLiquidacao().getEmpenho().getEmpenho() : liquidacaoDoctoFiscal.getLiquidacao().getEmpenho()).getNumero(), doc, notaFiscal);
        criarTag("anoEmpenho", (liquidacaoDoctoFiscal.getLiquidacao().getEmpenho().getEmpenho() != null ? liquidacaoDoctoFiscal.getLiquidacao().getEmpenho().getEmpenho() : liquidacaoDoctoFiscal.getLiquidacao().getEmpenho()).getExercicio().getAno().toString(), doc, notaFiscal);
        criarTag("numeroOrdemBancaria", liquidacaoDoctoFiscal.getLiquidacao().getExercicio().getAno() + liquidacaoDoctoFiscal.getLiquidacao().getNumero(), doc, notaFiscal);
        criarTag("chaveNFE", liquidacaoDoctoFiscal.getDoctoFiscalLiquidacao().getChaveDeAcesso(), doc, notaFiscal);
    }

    private void criarTagLancamentoContabil(Document doc, Element lista, LancamentoContabil lancamentoContabil) {
        Element lancamento = doc.createElement("lancamento");
        lista.appendChild(lancamento);

        criarTag("numero", lancamentoContabil.getItemParametroEvento().getParametroEvento().getIdOrigem() + lancamentoContabil.getItemParametroEvento().getParametroEvento().getComplementoId().getCodigo(), doc, lancamento);
        criarTag("data", Util.dateToString(lancamentoContabil.getDataLancamento()), doc, lancamento);

        EventoContabil eventoContabilDoLancamentoContabil = buscarTipoEventoContabil(lancamentoContabil);

        eventosContabil(doc, lancamentoContabil, lancamento);

        try {
            Object o = buscarObjetoDoLancamento(lancamentoContabil);
            criarTag("historico", eventoContabilDoLancamentoContabil.getCodigo() + " - " + eventoContabilDoLancamentoContabil.getDescricao() + ". " + montarHistoricoLancamento(o, eventoContabilDoLancamentoContabil.getTipoEventoContabil(), lancamentoContabil), doc, lancamento);
        } catch (Exception e) {
            criarTag("historico", eventoContabilDoLancamentoContabil.getCodigo() + " - " + eventoContabilDoLancamentoContabil.getDescricao() + ". " + lancamentoContabil.getComplementoHistorico(), doc, lancamento);
        }


        Element unidade = doc.createElement("unidadeOrcamentaria");
        lancamento.appendChild(unidade);

        criarTag("codigo", new Integer(getCodigoUnidadeLancamentoContabil(lancamentoContabil.getDataLancamento(), lancamentoContabil.getUnidadeOrganizacional())).toString(), doc, unidade);

        Element orgao = doc.createElement("orgao");
        unidade.appendChild(orgao);

        criarTag("codigo", new Integer(getCodigoOrgaoLancamentoContabil(lancamentoContabil.getDataLancamento(), lancamentoContabil.getUnidadeOrganizacional())).toString(), doc, orgao);

        Element clp = doc.createElement("clp");
        lancamento.appendChild(clp);

        try {
            criarTag("codigo", new Integer(eventoContabilDoLancamentoContabil.getEventoTce()).toString(), doc, clp);
        } catch (Exception e) {
            criarTag("codigo", "", doc, clp);
        }

    }

    private void criarTagLancamentoContabilManual(Document doc, Element lista, LancamentoContabilManual lancamentoContabilManual) {
        Element lancamento = doc.createElement("lancamento");
        lista.appendChild(lancamento);
        criarTag("numero", lancamentoContabilManual.getId() + "0", doc, lancamento);
        criarTag("data", Util.dateToString(lancamentoContabilManual.getData()), doc, lancamento);
        criarTag("tipoDeLancamento", TipoLancamento.ESTORNO.equals(lancamentoContabilManual.getEventoContabil().getTipoLancamento()) ? "ESTORNO" : "ORDINARIO", doc, lancamento);
        criarTag("historico", lancamentoContabilManual.getEventoContabil().getCodigo() + " - " + lancamentoContabilManual.getEventoContabil().getDescricao() + ". " + lancamentoContabilManual.getComplementoHistorico(), doc, lancamento);

        Element unidade = doc.createElement("unidadeOrcamentaria");
        lancamento.appendChild(unidade);
        criarTag("codigo", new Integer(getCodigoUnidadeLancamentoContabil(lancamentoContabilManual.getData(), lancamentoContabilManual.getUnidadeOrganizacional())).toString(), doc, unidade);

        Element orgao = doc.createElement("orgao");
        unidade.appendChild(orgao);
        criarTag("codigo", new Integer(getCodigoOrgaoLancamentoContabil(lancamentoContabilManual.getData(), lancamentoContabilManual.getUnidadeOrganizacional())).toString(), doc, orgao);

        Element clp = doc.createElement("clp");
        lancamento.appendChild(clp);
        try {
            criarTag("codigo", new Integer(lancamentoContabilManual.getEventoContabil().getEventoTce()).toString(), doc, clp);
        } catch (Exception e) {
            criarTag("codigo", "", doc, clp);
        }
    }

    private String montarHistoricoLancamento(Object obj, TipoEventoContabil tipoEventoContabil, LancamentoContabil lancamentoContabil) {
        if (obj instanceof EntidadeContabil) {
            String retorno = ((EntidadeContabil) obj).getReferenciaArquivoPrestacaoDeContas();
            if (!TipoEventoContabil.RECEITA_REALIZADA.equals(tipoEventoContabil)
                && !TipoEventoContabil.CREDITO_INICIAL.equals(tipoEventoContabil)
                && !TipoEventoContabil.PREVISAO_INICIAL_RECEITA.equals(tipoEventoContabil)
                && !TipoEventoContabil.RESTO_PAGAR.equals(tipoEventoContabil)
                && !TipoEventoContabil.LIQUIDACAO_RESTO_PAGAR.equals(tipoEventoContabil)
                && !TipoEventoContabil.CANCELAMENTO_RESTO_PAGAR.equals(tipoEventoContabil)
                && !TipoEventoContabil.PAGAMENTO_RESTO_PAGAR.equals(tipoEventoContabil)
                && !TipoEventoContabil.ABERTURA.equals(tipoEventoContabil)) {
                retorno += " - " + lancamentoContabil.getComplementoHistorico();
            } else if (TipoEventoContabil.RESTO_PAGAR.equals(tipoEventoContabil)
                || TipoEventoContabil.LIQUIDACAO_RESTO_PAGAR.equals(tipoEventoContabil)
                || TipoEventoContabil.PAGAMENTO_RESTO_PAGAR.equals(tipoEventoContabil)
                || TipoEventoContabil.CANCELAMENTO_RESTO_PAGAR.equals(tipoEventoContabil)
                || TipoEventoContabil.ABERTURA.equals(tipoEventoContabil)) {
                retorno = ((EntidadeContabil) obj).getComplementoHistoricoPrestacaoDeContas();
            }
            return retorno.replaceAll("\\s+", " ");
        }
        return obj.toString();
    }

    private String getCodigoUnidadeLancamentoContabil(Date dataReferencia, UnidadeOrganizacional unidadeOrganizacional) {
        try {
            HierarquiaOrganizacional hierarquiaOrganizacionalPorUnidade = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(dataReferencia, unidadeOrganizacional, TipoHierarquiaOrganizacional.ORCAMENTARIA);
            return hierarquiaOrganizacionalPorUnidade.getCodigoNo();
        } catch (Exception e) {
            return "";
        }
    }

    private String getCodigoOrgaoLancamentoContabil(Date dataReferencia, UnidadeOrganizacional unidadeOrganizacional) {
        try {
            HierarquiaOrganizacional hierarquiaOrganizacionalPorUnidade = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(dataReferencia, unidadeOrganizacional, TipoHierarquiaOrganizacional.ORCAMENTARIA);
            HierarquiaOrganizacional orgao = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(dataReferencia, hierarquiaOrganizacionalPorUnidade.getSuperior(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
            return orgao.getCodigoNo();
        } catch (Exception e) {
            return "";
        }
    }

    private String getCodigoOrgaoDaHierarquia(HierarquiaOrganizacional hierarquiaOrganizacional) {
        try {
            HierarquiaOrganizacional orgao = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(dataOperacao, hierarquiaOrganizacional.getSuperior(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
            return orgao.getCodigoNo();
        } catch (Exception e) {
            return "";
        }
    }

    private void gerarArquivoExecucaoOrcamentaria(List<TipoEventoContabil> tipoEventoContabils, PrestacaoDeContasItens selecionado) throws ParserConfigurationException, IOException, TransformerException {
        List<LancamentoContabil> lancamentoContabils = Lists.newArrayList();
        tipoEventoContabils.remove(TipoEventoContabil.AJUSTE_CONTABIL_MANUAL);

        if (!tipoEventoContabils.isEmpty()) {
            addMensagem("<b> <font color='black'>...Recuperando Lançamentos Contábeis...</font> </b>", selecionado);
            lancamentoContabils = buscarLancamentoContabil(selecionado.getMes(), selecionado.getExercicio(), tipoEventoContabils, selecionado.getUnidadeGestora());
        }

        addMensagem("<b> <font color='black'>...Recuperando Lançamentos Contábeis Manuais...</font> </b>", selecionado);
        List<LancamentoContabilManual> lancamentosManuais = buscarLancamentosContabeisManuais(selecionado.getMes(), selecionado.getExercicio(), selecionado.getUnidadeGestora());
        selecionado.setQuantidadeDeLancamento(lancamentoContabils.size() + lancamentosManuais.size());
        selecionado.setTotal(selecionado.getQuantidadeDeLancamento());

        if (selecionado.getBuscarSaldoInicial()) {
            buscarTodosArquivosInicial(selecionado);
        }

        addMensagem("<b> <font color='black'>...Gerando Arquivo das Partidas...</font> </b>", selecionado);
        gerarArquivoPartida(lancamentoContabils, lancamentosManuais, selecionado);

        lancamentoContabils = prepararLancamentoContabeisPorIdMovimentoOrigem(lancamentoContabils);
        inicializa(selecionado);
        selecionado.setQuantidadeDeLancamentoPorMovimento(lancamentoContabils.size() + lancamentosManuais.size());
        selecionado.setTotal(selecionado.getQuantidadeDeLancamentoPorMovimento());

        addMensagem("<b> <font color='black'>...Gerando Arquivo dos Lançamentos...</font> </b>", selecionado);
        gerarArquivoLancamento(lancamentoContabils, lancamentosManuais, selecionado);

        addMensagem("<b> <font color='black'>...Gerando Arquivo das Notas Fiscais...</font> </b>", selecionado);
        List<LiquidacaoDoctoFiscal> documentos = buscarDocumentosFiscais(selecionado.getMes(), selecionado.getExercicio(), selecionado.getUnidadeGestora());
        selecionado.setQuantidadeDeLancamentoPorMovimento(selecionado.getQuantidadeDeLancamentoPorMovimento() + documentos.size());
        selecionado.setTotal(selecionado.getQuantidadeDeLancamentoPorMovimento());
        gerarArquivoNotasFiscais(documentos, selecionado);
    }

    private void criarArquivoPartida(HSSFWorkbook wb, PrestacaoDeContasItens selecionado) {
        HSSFSheet sheet = wb.getSheetAt(1);
        if (selecionado.getPartidasSaldoInicial() == null) {
            selecionado.setPartidasSaldoInicial(new ArrayList<PartidaArquivoTCE>());
        }


        Iterator rows = sheet.rowIterator();
        rows.next();
        while (rows.hasNext()) {
            HSSFRow row = (HSSFRow) rows.next();
            PartidaArquivoTCE partidaArquivo = new PartidaArquivoTCE(selecionado.getExercicio());

            Iterator cells = row.cellIterator();
            while (cells.hasNext()) {
                HSSFCell cell = (HSSFCell) cells.next();
                if (cell != null) {
                    String valor = getValorCell(cell);
                    if (valor != null) {
                        //ARQUIVO PARTIDA
                        if (cell.getColumnIndex() == 0) {
                            partidaArquivo.setContaContabil(valor);
                        } else if (cell.getColumnIndex() == 1) {
                            partidaArquivo.setConteudoContaCorrente(valor);
                        } else if (cell.getColumnIndex() == 2) {
                            partidaArquivo.setTipoContaCorrente(valor);
                        } else if (cell.getColumnIndex() == 3) {
                            partidaArquivo.setNatureza(valor);
                        } else if (cell.getColumnIndex() == 4) {
                            partidaArquivo.setIndicadorSuperavitFinanceiro(valor);
                        } else if (cell.getColumnIndex() == 5) {
                            partidaArquivo.setNumeroLancamento(valor);
                        } else if (cell.getColumnIndex() == 6) {
                            partidaArquivo.setTipoLancamento(valor);
                        } else if (cell.getColumnIndex() == 7) {
                            partidaArquivo.setValor(valor);
                        } else if (cell.getColumnIndex() == 8) {
                            partidaArquivo.setTipoArquivoTCE(TipoArquivoTCE.valueOf(valor));
                        }
                    }
                }
            }
            if (selecionado.getUnidadeGestora() != null) {
                for (LancamentoArquivoTCE lancamentoArquivo : selecionado.getLancamentosSaldoInicial()) {
                    if (lancamentoArquivo.getNumero().equals(partidaArquivo.getNumeroLancamento())) {
                        selecionado.getPartidasSaldoInicial().add(partidaArquivo);
                    }
                }
            } else {
                selecionado.getPartidasSaldoInicial().add(partidaArquivo);
            }
        }
    }

    private void criarArquivoLancamento(HSSFWorkbook wb, PrestacaoDeContasItens selecionado) {
        HSSFSheet sheet = wb.getSheetAt(0);
        if (selecionado.getLancamentosSaldoInicial() == null) {
            selecionado.setLancamentosSaldoInicial(new ArrayList<LancamentoArquivoTCE>());
        }
        List<HierarquiaOrganizacional> hierarquias = buscarCodigoNoHierarquiaUnidadeGestora(selecionado.getUnidadeGestora(), dataOperacao);

        Iterator rows = sheet.rowIterator();
        rows.next();
        while (rows.hasNext()) {
            HSSFRow row = (HSSFRow) rows.next();
            LancamentoArquivoTCE lancamentoArquivo = new LancamentoArquivoTCE(selecionado.getExercicio());

            Iterator cells = row.cellIterator();
            while (cells.hasNext()) {
                HSSFCell cell = (HSSFCell) cells.next();
                if (cell != null) {
                    String valor = getValorCell(cell);
                    if (valor != null) {
                        //ARQUIVO LANCAMENTO
                        if (cell.getColumnIndex() == 0) {
                            lancamentoArquivo.setNumero(valor);
                        } else if (cell.getColumnIndex() == 1) {
                            lancamentoArquivo.setData(valor);
                        } else if (cell.getColumnIndex() == 2) {
                            lancamentoArquivo.setTipoLancamento(valor);
                        } else if (cell.getColumnIndex() == 3) {
                            lancamentoArquivo.setHistorico(valor.length() > 255 ? valor.substring(0, 255) : valor);
                        } else if (cell.getColumnIndex() == 4) {
                            lancamentoArquivo.setUnidade(valor);
                        } else if (cell.getColumnIndex() == 5) {
                            lancamentoArquivo.setOrgao(valor);
                        } else if (cell.getColumnIndex() == 6) {
                            lancamentoArquivo.setClp(valor);
                        } else if (cell.getColumnIndex() == 7) {
                            lancamentoArquivo.setTipoArquivoTCE(TipoArquivoTCE.valueOf(valor));
                        }
                    }
                }
            }
            if (selecionado.getUnidadeGestora() != null) {

                for (HierarquiaOrganizacional hierarquiaOrganizacional : hierarquias) {
                    if ((Integer.parseInt(hierarquiaOrganizacional.getCodigoNo()) == (Integer.parseInt(lancamentoArquivo.getUnidade()))) &&
                        (Integer.parseInt(hierarquiaOrganizacional.getCodigo().substring(3, 6)) == (Integer.parseInt(lancamentoArquivo.getOrgao())))) {
                        selecionado.getLancamentosSaldoInicial().add(lancamentoArquivo);
                    }
                }


            } else {
                selecionado.getLancamentosSaldoInicial().add(lancamentoArquivo);
            }
        }
    }

    private String getValorCell(HSSFCell cell) {
        if (HSSFCell.CELL_TYPE_NUMERIC == cell.getCellType()) {
            Double valor = cell.getNumericCellValue();
            return valor.intValue() + "";
        } else if (HSSFCell.CELL_TYPE_STRING == cell.getCellType()) {
            return cell.getStringCellValue();
        } else if (HSSFCell.CELL_TYPE_BOOLEAN == cell.getCellType()) {
            return cell.getBooleanCellValue() ? "Sim " : "Não";
        } else if (HSSFCell.CELL_TYPE_FORMULA == cell.getCellType()) {
            if (cell.getCachedFormulaResultType() == HSSFCell.CELL_TYPE_STRING) {
                return cell.getStringCellValue();
            } else if (cell.getCachedFormulaResultType() == HSSFCell.CELL_TYPE_NUMERIC) {
                return String.valueOf(cell.getNumericCellValue());
            } else {
                return "";
            }
        } else if (HSSFCell.CELL_TYPE_ERROR == cell.getCellType()) {
            return "error";
        } else if (HSSFCell.CELL_TYPE_BLANK == cell.getCellType()) {
            return "";
        } else {
            return "";

        }
    }

    private void criarTagLancamentoSaldoInicial(Document doc, Element lista, LancamentoArquivoTCE lancamentoArquivo) {
        Element lancamento = doc.createElement("lancamento");
        lista.appendChild(lancamento);

        criarTag("numero", lancamentoArquivo.getNumero(), doc, lancamento);
        criarTag("data", lancamentoArquivo.getData(), doc, lancamento);
        criarTag("tipoDeLancamento", lancamentoArquivo.getTipoLancamento(), doc, lancamento);
        criarTag("historico", lancamentoArquivo.getHistorico(), doc, lancamento);

        Element unidade = doc.createElement("unidadeOrcamentaria");
        lancamento.appendChild(unidade);

        criarTag("codigo", lancamentoArquivo.getUnidade(), doc, unidade);

        Element orgao = doc.createElement("orgao");
        unidade.appendChild(orgao);

        criarTag("codigo", lancamentoArquivo.getOrgao(), doc, orgao);

        Element clp = doc.createElement("clp");
        lancamento.appendChild(clp);

        try {
            criarTag("codigo", lancamentoArquivo.getClp(), doc, clp);
        } catch (Exception e) {
            criarTag("codigo", "", doc, clp);
        }
    }

    private void criarTagPartidaSaldoInicial(Document doc, Element lista, PartidaArquivoTCE partidaArquivo) {
        Element partida = doc.createElement("partida");
        lista.appendChild(partida);

        Element contaContabilCredito = doc.createElement("contaContabil");
        partida.appendChild(contaContabilCredito);


        criarTag("codigo", partidaArquivo.getContaContabil(), doc, contaContabilCredito);
        criarTag("conteudoContaCorrente", partidaArquivo.getConteudoContaCorrente(), doc, partida);


        if (partidaArquivo.getTipoContaCorrente() != null) {
            if (!partidaArquivo.getTipoContaCorrente().trim().isEmpty()) {
                Element tipoDeContaCorrente = doc.createElement("tipoDeContaCorrente");
                partida.appendChild(tipoDeContaCorrente);
                criarTag("numero", String.valueOf(partidaArquivo.getTipoContaCorrente()), doc, tipoDeContaCorrente);
            }
        }


        criarTag("natureza", partidaArquivo.getNatureza(), doc, partida);
        String valor = partidaArquivo.getIndicadorSuperavitFinanceiro();
        if (valor != null) {
            if (!valor.isEmpty()) {
                criarTag("indicadorSuperavitFinanceiro", valor, doc, partida);
            }
        }


        Element lancamento = doc.createElement("lancamento");
        partida.appendChild(lancamento);

        criarTag("numero", partidaArquivo.getNumeroLancamento(), doc, lancamento);
        criarTag("tipoDeLancamento", partidaArquivo.getTipoLancamento(), doc, lancamento);
        criarTag("valor", partidaArquivo.getValor(), doc, partida);
    }

    public void buscarTodosArquivosInicial(PrestacaoDeContasItens selecionado) {
        if (selecionado.getUnidadeGestora() != null) {
            List<HierarquiaOrganizacional> hierarquias = buscarCodigoNoHierarquiaUnidadeGestora(selecionado.getUnidadeGestora(), dataOperacao);
            List<String> codigoOrgaos = new ArrayList<>();
            List<String> codigoUnidades = new ArrayList<>();
            for (HierarquiaOrganizacional hierarquiaOrganizacional : hierarquias) {
                Integer codigoUnidade = Integer.parseInt(hierarquiaOrganizacional.getCodigoNo());
                Integer codigoOrgao = Integer.parseInt(hierarquiaOrganizacional.getCodigo().substring(3, 6));

                codigoOrgaos.add(String.valueOf(codigoOrgao));
                codigoUnidades.add(String.valueOf(codigoUnidade));
            }

            String unidades = "(";
            for (String codigo : codigoUnidades) {
                unidades += "'" + codigo + "',";
            }
            unidades = unidades.substring(0, unidades.length() - 1) + ")";


            String orgaos = "(";
            for (String codigo : codigoOrgaos) {
                orgaos += "'" + codigo + "',";
            }
            orgaos = orgaos.substring(0, orgaos.length() - 1) + ")";

            selecionado.setLancamentosSaldoInicial(buscarLancamentoPorOrgaoUnidade(selecionado.getExercicio(), orgaos, unidades));

            if (!selecionado.getLancamentosSaldoInicial().isEmpty()) {
                String numeroLancamento = "(";
                for (LancamentoArquivoTCE lancamentoArquivoTCE : selecionado.getLancamentosSaldoInicial()) {
                    numeroLancamento += "'" + lancamentoArquivoTCE.getNumero() + "',";
                }
                numeroLancamento = numeroLancamento.substring(0, numeroLancamento.length() - 1) + ")";

                selecionado.setPartidasSaldoInicial(buscarPartidasPorNumeroLancamento(selecionado.getExercicio(), numeroLancamento));
            } else {
                selecionado.setPartidasSaldoInicial(new ArrayList<PartidaArquivoTCE>());
            }

        } else {
            selecionado.setLancamentosSaldoInicial(buscarLancamento(selecionado.getExercicio()));
            selecionado.setPartidasSaldoInicial(buscarPartidas(selecionado.getExercicio()));
        }
    }

    public void gerarExcelToXml(FileUploadEvent event, PrestacaoDeContasItens selecionado) {
        try {
            InputStream input = event.getFile().getInputstream();
            POIFSFileSystem fs = new POIFSFileSystem(input);
            HSSFWorkbook wb = new HSSFWorkbook(fs);

            criarArquivoLancamento(wb, selecionado);
            criarArquivoPartida(wb, selecionado);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<TipoEventoContabil> getTiposEventoContabeis(PrestacaoDeContasItens selecionado) {
        List<TipoEventoContabil> tipoEventoContabils = new ArrayList<>();
        for (String s : selecionado.getTiposEventosContabeis().getTarget()) {
            List<TipoEventoContabil> tipoEventoContabils1 = Arrays.asList(TipoEventoContabil.values());
            for (TipoEventoContabil tipoEventoContabil : tipoEventoContabils1) {
                if (tipoEventoContabil.getDescricao().equals(s)) {
                    tipoEventoContabils.add(tipoEventoContabil);
                }
            }
        }
        return tipoEventoContabils;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public UnidadeGestoraFacade getUnidadeGestoraFacade() {
        return unidadeGestoraFacade;
    }

    public PPAFacade getPpaFacade() {
        return ppaFacade;
    }

    public ProjetoAtividadeFacade getProjetoAtividadeFacade() {
        return projetoAtividadeFacade;
    }

    public ProgramaPPAFacade getProgramaPPAFacade() {
        return programaPPAFacade;
    }

    public LOAFacade getLoaFacade() {
        return loaFacade;
    }

    public CLPFacade getClpFacade() {
        return clpFacade;
    }

    public RelatorioBalanceteContabilPorTipoFacade getRelatorioBalanceteContabilPorTipoFacade() {
        return relatorioBalanceteContabilPorTipoFacade;
    }

    public ArquivoPrestacaoDeContas recuperarArquivoPrestacaoDeContas(Long id) {
        ArquivoPrestacaoDeContas arquivoPDC = em.find(ArquivoPrestacaoDeContas.class, id);
        Hibernate.initialize(arquivoPDC.getArquivo().getPartes());
        return arquivoPDC;
    }
}
