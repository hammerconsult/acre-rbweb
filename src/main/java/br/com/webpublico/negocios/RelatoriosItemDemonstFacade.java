/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.enums.TipoDespesaORC;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.transaction.annotation.Transactional;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author juggernaut
 */
@Stateless
public class RelatoriosItemDemonstFacade extends AbstractFacade<RelatoriosItemDemonst> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ItemDemonstrativoFacade itemDemonstrativoFacade;
    @EJB
    private FormulaItemDemonstrativoFacade formulaItemDemonstrativoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private ProjetoAtividadeFacade projetoAtividadeFacade;
    @EJB
    private ProgramaPPAFacade programaPPAFacade;
    @EJB
    private SubProjetoAtividadeFacade subProjetoAtividadeFacade;
    @EJB
    private FuncaoFacade funcaoFacade;
    @EJB
    private SubFuncaoFacade subFuncaoFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public RelatoriosItemDemonst recuperar(Object id) {
        RelatoriosItemDemonst relatoriosItemDemonst = em.find(RelatoriosItemDemonst.class, id);
        relatoriosItemDemonst.getItensDemonstrativos().size();
        return relatoriosItemDemonst;
    }

    public RelatoriosItemDemonstFacade() {
        super(RelatoriosItemDemonst.class);
    }

    public List<RelatoriosItemDemonst> listaExercicio(Exercicio exercicioCorrente, String filtro) {
        String hql = "from RelatoriosItemDemonst obj where obj.exercicio = :exerc ";
        if (filtro != null) {
            if (!filtro.equals("")) {
                hql += " and (lower(obj.descricao) like \'%" + filtro.toLowerCase() + "%\' or lower(obj.tipoRelatorioItemDemonstrativo) like \'%" + filtro.toLowerCase() + "%\')";
            }
        }
        hql += " order by obj.id";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("exerc", exercicioCorrente);
        return q.getResultList();
    }

    public RelatoriosItemDemonst recuperaRelatorioPorTipoEDescricao(String descricao, Exercicio ex, TipoRelatorioItemDemonstrativo tipoRelatorioItemDemonstrativo) {
        String hql = "select obj from RelatoriosItemDemonst obj " +
            " where obj.exercicio = :exerc " +
            " and lower(obj.tipoRelatorioItemDemonstrativo) like lower('" + tipoRelatorioItemDemonstrativo.name() + "')" +
            " and lower(obj.descricao) like :descricao ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("exerc", ex);
        q.setParameter("descricao", descricao.toLowerCase());
        if (q.getResultList().isEmpty()) {
            return new RelatoriosItemDemonst();
        } else {
            return (RelatoriosItemDemonst) q.getSingleResult();
        }
    }

    public void removerComponentesFormulaComItemDemonstrativo(ItemDemonstrativo itemDemonstrativo) {
        String sql = " select co.*, coi.itemdemonstrativo_id from ComponenteFormula co " +
            " inner join componenteFormulaItem coi on co.id = coi.id " +
            " where coi.itemdemonstrativo_id = :itemId ";
        Query q = em.createNativeQuery(sql, ComponenteFormulaItem.class);
        q.setParameter("itemId", itemDemonstrativo.getId());
        List<ComponenteFormulaItem> componentes = q.getResultList();
        for (ComponenteFormulaItem componente : componentes) {
            removerComponenteFormulaItem(componente);
        }
    }

    public List<RelatoriosItemDemonst> buscarRelatoriosPorExercicioETipo(String parte, Exercicio ex, TipoRelatorioItemDemonstrativo tipoRelatorio) {
        if (parte == null || ex == null || tipoRelatorio == null) return Lists.newArrayList();
        String sql = " select rel.* " +
            " from RelatoriosItemDemonst rel " +
            " where rel.exercicio_id = :idExercicio " +
            "   and rel.tipoRelatorioItemDemonstrativo = :tipoRelatorio " +
            "   and trim(lower(rel.descricao)) like :parte ";
        Query q = em.createNativeQuery(sql, RelatoriosItemDemonst.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("idExercicio", ex.getId());
        q.setParameter("tipoRelatorio", tipoRelatorio.name());
        return q.getResultList();
    }

    public RelatoriosItemDemonst duplicarRelatorioNovoExercicio(RelatoriosItemDemonst relatorioOrigem, RelatoriosItemDemonst relatorioDestino, Exercicio exercicioCopia) {
        relatorioDestino.setDescricao(relatorioOrigem.getDescricao());
        relatorioDestino.setGrupos(relatorioOrigem.getGrupos());
        relatorioDestino.setNotaExplicativa(relatorioOrigem.getNotaExplicativa());
        relatorioDestino.setTipoRelatorioItemDemonstrativo(relatorioOrigem.getTipoRelatorioItemDemonstrativo());
        relatorioDestino.setUsaAcao(relatorioOrigem.getUsaAcao());
        relatorioDestino.setUsaTipoDespesa(relatorioOrigem.getUsaTipoDespesa());
        relatorioDestino.setUsaUnidadeOrganizacional(relatorioOrigem.getUsaUnidadeOrganizacional());
        relatorioDestino.setUsaSubFuncao(relatorioOrigem.getUsaSubFuncao());
        relatorioDestino.setUsaPrograma(relatorioOrigem.getUsaPrograma());
        relatorioDestino.setUsaConta(relatorioOrigem.getUsaConta());
        relatorioDestino.setUsaFonteRecurso(relatorioOrigem.getUsaFonteRecurso());
        relatorioDestino.setUsaFuncao(relatorioOrigem.getUsaFuncao());
        relatorioDestino.setUsaContaFinanceira(relatorioOrigem.getUsaContaFinanceira());
        relatorioDestino.setUsaSubAcao(relatorioOrigem.getUsaSubAcao());
        relatorioDestino.setExercicio(exercicioCopia);
        relatorioDestino = salvarRetornando(relatorioDestino);
        relatorioOrigem = recuperar(relatorioOrigem.getId());
        List<ItemDemonstrativo> itensDestinos = Lists.newArrayList();
        for (ItemDemonstrativo itemOrigem : relatorioOrigem.getItensDemonstrativos()) {
            itensDestinos.add(duplicarItemDemonstrativo(itemOrigem, relatorioDestino));
        }
        relatorioDestino.setItensDemonstrativos(itensDestinos);
        return relatorioDestino;
    }

    private ItemDemonstrativo duplicarItemDemonstrativo(ItemDemonstrativo itemOrigem, RelatoriosItemDemonst relatorioDestino) {
        ItemDemonstrativo itemDestino = new ItemDemonstrativo();
        itemDestino.setNome(itemOrigem.getNome());
        itemDestino.setDescricao(itemOrigem.getDescricao());
        itemDestino.setInverteSinal(itemOrigem.getInverteSinal());
        itemDestino.setItemExercicioAnterior(itemOrigem);
        itemDestino.setColuna(itemOrigem.getColuna());
        itemDestino.setEspaco(itemOrigem.getEspaco());
        itemDestino.setOrdem(itemOrigem.getOrdem());
        itemDestino.setGrupo(itemOrigem.getGrupo());
        itemDestino.setNumeroLinhaNoXLS(itemOrigem.getNumeroLinhaNoXLS());
        itemDestino.setRelatoriosItemDemonst(relatorioDestino);
        List<FormulaItemDemonstrativo> formulaDestino = Lists.newArrayList();
        itemDestino = getItemDemonstrativoFacade().salvarRetornando(itemDestino);
        itemOrigem = getItemDemonstrativoFacade().recuperar(itemOrigem.getId());
        for (FormulaItemDemonstrativo formulaOrigem : itemOrigem.getFormulas()) {
            formulaDestino.add(duplicarFormulaItemDemonstrativo(formulaOrigem, itemDestino));
        }
        itemDestino.setFormulas(formulaDestino);
        itemDestino = getItemDemonstrativoFacade().salvarRetornando(itemDestino);
        return itemDestino;
    }

    private FormulaItemDemonstrativo duplicarFormulaItemDemonstrativo(FormulaItemDemonstrativo formulaOrigem, ItemDemonstrativo itemDestino) {
        FormulaItemDemonstrativo formulaDestino = new FormulaItemDemonstrativo();
        formulaDestino.setItemDemonstrativo(itemDestino);
        formulaDestino.setOperacaoFormula(formulaOrigem.getOperacaoFormula());
        formulaDestino.setColuna(formulaOrigem.getColuna());
        List<ComponenteFormula> componenteDestino = Lists.newArrayList();
        formulaOrigem = getFormulaItemDemonstrativoFacade().recuperar(formulaOrigem.getId());
        for (ComponenteFormula componenteOrigem : formulaOrigem.getComponenteFormula()) {
            componenteDestino.add(duplicarComponenteFormula(formulaDestino, componenteOrigem.criarComponenteFormula()));
        }
        formulaDestino.setComponenteFormula(componenteDestino);
        formulaDestino = getFormulaItemDemonstrativoFacade().salvarRetornando(formulaDestino);
        return formulaDestino;
    }

    private ComponenteFormula duplicarComponenteFormula(FormulaItemDemonstrativo formulaItemDemonstrativo, ComponenteFormula componenteOrigem) {
        ComponenteFormula componenteDestino = componenteOrigem.criarComponenteFormula();
        componenteDestino.setFormulaItemDemonstrativo(formulaItemDemonstrativo);
        return componenteDestino;
    }

    private void removerComponenteFormulaItem(ComponenteFormulaItem componenteFormulaItem) {
        em.remove(componenteFormulaItem);
    }

    public RelatoriosItemDemonst salvarRetornando(RelatoriosItemDemonst relatoriosItemDemonst) {
        return em.merge(relatoriosItemDemonst);
    }

    public EntityManager getEm() {
        return em;
    }

    public ItemDemonstrativoFacade getItemDemonstrativoFacade() {
        return itemDemonstrativoFacade;
    }

    public FormulaItemDemonstrativoFacade getFormulaItemDemonstrativoFacade() {
        return formulaItemDemonstrativoFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public DocumentoOficialFacade getDocumentoOficialFacade() {
        return documentoOficialFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public SubContaFacade getSubContaFacade() {
        return subContaFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public StreamedContent gerarArquivoRelatoriosItemDemonst(RelatoriosItemDemonst relatoriosItemDemonst, Exercicio exercicio) throws IOException {
        List<RelatoriosItemDemonst> relatorios = Lists.newArrayList();
        String nomeArquivo = "arquivoRelatoriosItemDemonstAno" + exercicio.getAno();
        if (relatoriosItemDemonst != null) {
            relatorios.add(relatoriosItemDemonst);
            nomeArquivo += relatoriosItemDemonst.getDescricao().trim().replaceAll("\\s+", "") + relatoriosItemDemonst.getTipoRelatorioItemDemonstrativo().getDescricao();
        } else {
            relatorios.addAll(listaExercicio(exercicio, ""));
            nomeArquivo += "Qtde" + relatorios.size();
        }
        String conteudo = relatorios.isEmpty() ? "NENHUM RELATÓRIO ENCONTRADO PARA O EXERCICIO " + exercicio.getAno() : "";
        for (RelatoriosItemDemonst relatorio : relatorios) {
            conteudo += montarArquivoDoRelatório(relatorio);
        }

        File arquivo = new File(nomeArquivo);
        try (FileOutputStream fos = new FileOutputStream(arquivo)) {
            fos.write(conteudo.getBytes(StandardCharsets.UTF_8));
            InputStream stream = new FileInputStream(arquivo);
            return new DefaultStreamedContent(stream, "text/plain", nomeArquivo + ".txt");
        }
    }

    public String montarArquivoDoRelatório(RelatoriosItemDemonst relatoriosItemDemonst) {
        relatoriosItemDemonst = recuperar(relatoriosItemDemonst.getId());
        String retorno = montarRelatoriosItemDemonstExportar(relatoriosItemDemonst);
        retorno += "\r\n";
        for (ItemDemonstrativo item : relatoriosItemDemonst.getItensDemonstrativos()) {
            retorno += montarItemDemonstrativoExportar(item);
            retorno += "\r\n";

            for (FormulaItemDemonstrativo formula : item.getFormulas()) {
                retorno += montarItemDemonstrativoExportar(formula);
                retorno += "\r\n";

                for (ComponenteFormula componenteFormula : formula.getComponenteFormula()) {
                    if (componenteFormula instanceof ComponenteFormulaItem) {
                        retorno += ComponenteFormulaItem.class.getSimpleName() + ";" + ((ComponenteFormulaItem) componenteFormula).getItemDemonstrativo().getDescricao() + ";";
                    } else if (componenteFormula instanceof ComponenteFormulaConta) {
                        retorno += ComponenteFormulaConta.class.getSimpleName() + ";" + ((ComponenteFormulaConta) componenteFormula).getConta().getCodigo() + ";";
                    } else if (componenteFormula instanceof ComponenteFormulaPrograma) {
                        ProgramaPPA programaPPA = ((ComponenteFormulaPrograma) componenteFormula).getProgramaPPA();
                        retorno += ComponenteFormulaPrograma.class.getSimpleName() + ";" + programaPPA.getCodigo() + ";" + programaPPA.getDenominacao() + ";";
                    } else if (componenteFormula instanceof ComponenteFormulaAcao) {
                        AcaoPPA acaoPPA = ((ComponenteFormulaAcao) componenteFormula).getAcaoPPA();
                        retorno += ComponenteFormulaAcao.class.getSimpleName() + ";" + acaoPPA.getCodigo() + ";" + acaoPPA.getDescricao() + ";";
                    } else if (componenteFormula instanceof ComponenteFormulaSubacao) {
                        SubAcaoPPA subAcaoPPA = ((ComponenteFormulaSubacao) componenteFormula).getSubAcaoPPA();
                        retorno += ComponenteFormulaSubacao.class.getSimpleName() + ";" + subAcaoPPA.getCodigo() + ";" + subAcaoPPA.getDescricao() + ";";
                    } else if (componenteFormula instanceof ComponenteFormulaFuncao) {
                        retorno += ComponenteFormulaFuncao.class.getSimpleName() + ";" + ((ComponenteFormulaFuncao) componenteFormula).getFuncao().getCodigo() + ";";
                    } else if (componenteFormula instanceof ComponenteFormulaSubFuncao) {
                        retorno += ComponenteFormulaSubFuncao.class.getSimpleName() + ";" + ((ComponenteFormulaSubFuncao) componenteFormula).getSubFuncao().getCodigo() + ";";
                    } else if (componenteFormula instanceof ComponenteFormulaUnidadeOrganizacional) {
                        retorno += ComponenteFormulaUnidadeOrganizacional.class.getSimpleName() + ";" + ((ComponenteFormulaUnidadeOrganizacional) componenteFormula).getUnidadeOrganizacional().getDescricao() + ";";
                    } else if (componenteFormula instanceof ComponenteFormulaFonteRecurso) {
                        retorno += ComponenteFormulaFonteRecurso.class.getSimpleName() + ";" + ((ComponenteFormulaFonteRecurso) componenteFormula).getFonteDeRecursos().getCodigo().trim() + ";";
                    } else if (componenteFormula instanceof ComponenteFormulaTipoDesp) {
                        retorno += ComponenteFormulaTipoDesp.class.getSimpleName() + ";" + ((ComponenteFormulaTipoDesp) componenteFormula).getTipoDespesaORC().name() + ";";
                    } else if (componenteFormula instanceof ComponenteFormulaSubConta) {
                        retorno += ComponenteFormulaSubConta.class.getSimpleName() + ";" + ((ComponenteFormulaSubConta) componenteFormula).getSubConta().getCodigo() + ";";
                    }
                    retorno += "\r\n";
                }
            }
        }
        retorno += "RELATORIO FINALIZADO;";
        retorno += "\r\n";
        return retorno;
    }

    private String montarRelatoriosItemDemonstExportar(RelatoriosItemDemonst relatoriosItemDemonst) {
        return RelatoriosItemDemonst.class.getSimpleName() + ";" +
            relatoriosItemDemonst.getDescricao() + ";" +
            relatoriosItemDemonst.getTipoRelatorioItemDemonstrativo().name() + ";" +
            relatoriosItemDemonst.getUsaConta() + ";" +
            relatoriosItemDemonst.getUsaPrograma() + ";" +
            relatoriosItemDemonst.getUsaAcao() + ";" +
            relatoriosItemDemonst.getUsaSubAcao() + ";" +
            relatoriosItemDemonst.getUsaFuncao() + ";" +
            relatoriosItemDemonst.getUsaSubFuncao() + ";" +
            relatoriosItemDemonst.getUsaUnidadeOrganizacional() + ";" +
            relatoriosItemDemonst.getUsaFonteRecurso() + ";" +
            relatoriosItemDemonst.getUsaTipoDespesa() + ";" +
            relatoriosItemDemonst.getUsaContaFinanceira() + ";" +
            relatoriosItemDemonst.getGrupos() + ";" +
            relatoriosItemDemonst.getNotaExplicativa() + " ;";
    }

    private String montarItemDemonstrativoExportar(ItemDemonstrativo item) {
        return ItemDemonstrativo.class.getSimpleName() + ";" +
            item.getDescricao() + ";" +
            item.getNome() + ";" +
            item.getInverteSinal() + ";" +
            item.getOrdem() + ";" +
            item.getGrupo() + ";" +
            item.getColuna() + ";" +
            item.getEspaco() + ";" +
            item.getExibirNoRelatorio() + ";" +
            (item.getItemExercicioAnterior() != null ? item.getItemExercicioAnterior().getDescricao() : "null") + ";";
    }

    private String montarItemDemonstrativoExportar(FormulaItemDemonstrativo formula) {
        return FormulaItemDemonstrativo.class.getSimpleName() + ";" +
            formula.getOperacaoFormula().name() + ";" +
            formula.getColuna() + ";";
    }

    @Transactional(timeout = 20000)
    public String montarItemDemonstrativoComArquivoImportado(BufferedReader bufferedReader, Exercicio exercicio) {
        StringBuilder logNaoEncontrados = new StringBuilder();
        String line;
        try {
            RelatoriosItemDemonst novoRelatorio = new RelatoriosItemDemonst();
            novoRelatorio.setExercicio(exercicio);
            ItemDemonstrativo itemDaVez = null;
            FormulaItemDemonstrativo formulaDaVez = null;
            HashMap<String, List<String>> mapComponenteFormulaItem = new HashMap<>();
            while ((line = bufferedReader.readLine()) != null) {
                String[] partes = line.split(";");

                if (line.startsWith(RelatoriosItemDemonst.class.getSimpleName())) {
                    montarRelatoriosItemDemonstComLinhaRecuperada(partes, novoRelatorio);
                    logNaoEncontrados.append("Relatório: ").append(novoRelatorio.getDescricao()).append(" - ").append(novoRelatorio.getTipoRelatorioItemDemonstrativo().getDescricao())
                        .append(" Exercício Destino: ").append(exercicio.getAno()).append("\r\n");

                } else if (line.startsWith(ItemDemonstrativo.class.getSimpleName())) {
                    ItemDemonstrativo novoItem = new ItemDemonstrativo();
                    novoItem.setRelatoriosItemDemonst(novoRelatorio);
                    montarItemDemonstrativoComLinhaRecuperada(partes, novoItem, novoRelatorio);
                    if (itemDaVez != null) {
                        if (formulaDaVez != null) {
                            itemDaVez.getFormulas().add(formulaDaVez);
                        }
                        novoRelatorio.getItensDemonstrativos().add(itemDaVez);
                    }
                    itemDaVez = novoItem;

                } else if (line.startsWith(FormulaItemDemonstrativo.class.getSimpleName())) {
                    FormulaItemDemonstrativo novaFormula = new FormulaItemDemonstrativo();
                    novaFormula.setItemDemonstrativo(itemDaVez);
                    novaFormula.setOperacaoFormula(partes[1] != null ? OperacaoFormula.valueOf(partes[1]) : null);
                    novaFormula.setColuna(getValorLinhaAsInteger(partes[2]));
                    if (formulaDaVez != null && formulaDaVez.getOperacaoFormula() != null
                        && !formulaDaVez.getOperacaoFormula().equals(novaFormula.getOperacaoFormula())
                        && formulaDaVez.getItemDemonstrativo().getDescricao().equals(itemDaVez.getDescricao())) {
                        itemDaVez.getFormulas().add(formulaDaVez);
                    }
                    formulaDaVez = novaFormula;

                } else if (line.startsWith(ComponenteFormulaItem.class.getSimpleName())) {
                    atualizarMapComponenteFormulaItem(mapComponenteFormulaItem, formulaDaVez, partes);

                } else if (line.startsWith(ComponenteFormulaConta.class.getSimpleName())) {
                    montarComponenteFormulaConta(formulaDaVez, partes, exercicio, logNaoEncontrados);

                } else if (line.startsWith(ComponenteFormulaPrograma.class.getSimpleName())) {
                    montarComponenteFormulaPrograma(formulaDaVez, partes, exercicio, logNaoEncontrados);

                } else if (line.startsWith(ComponenteFormulaAcao.class.getSimpleName())) {
                    montarComponenteFormulaAcao(formulaDaVez, partes, exercicio, logNaoEncontrados);

                } else if (line.startsWith(ComponenteFormulaSubacao.class.getSimpleName())) {
                    montarComponenteFormulaSubacao(formulaDaVez, partes, exercicio, logNaoEncontrados);

                } else if (line.startsWith(ComponenteFormulaFuncao.class.getSimpleName())) {
                    montarComponenteFormulaFuncao(formulaDaVez, partes, logNaoEncontrados);

                } else if (line.startsWith(ComponenteFormulaSubFuncao.class.getSimpleName())) {
                    montarComponenteFormulaSubFuncao(formulaDaVez, partes, logNaoEncontrados);

                } else if (line.startsWith(ComponenteFormulaUnidadeOrganizacional.class.getSimpleName())) {
                    montarComponenteFormulaUnidadeOrganizacional(formulaDaVez, partes, logNaoEncontrados);

                } else if (line.startsWith(ComponenteFormulaFonteRecurso.class.getSimpleName())) {
                    montarComponenteFormulaFonteRecurso(formulaDaVez, partes, exercicio, logNaoEncontrados);

                } else if (line.startsWith(ComponenteFormulaTipoDesp.class.getSimpleName())) {
                    montarComponenteFormulaTipoDesp(formulaDaVez, partes, logNaoEncontrados);

                } else if (line.startsWith(ComponenteFormulaSubConta.class.getSimpleName())) {
                    montarComponenteFormulaSubConta(formulaDaVez, partes, exercicio, logNaoEncontrados);

                } else if (line.startsWith("RELATORIO FINALIZADO")) {
                    if (formulaDaVez != null) {
                        itemDaVez.getFormulas().add(formulaDaVez);
                    }
                    if (itemDaVez != null) {
                        novoRelatorio.getItensDemonstrativos().add(itemDaVez);
                    }
                    atualizarComponenteFormulaItem(novoRelatorio, mapComponenteFormulaItem);
                    salvar(novoRelatorio);
                    mapComponenteFormulaItem = new HashMap<>();
                    itemDaVez = null;
                    formulaDaVez = null;
                    novoRelatorio = new RelatoriosItemDemonst();
                    novoRelatorio.setExercicio(exercicio);
                    logNaoEncontrados.append("\r\n").append("\r\n");
                } else {
                    throw new ExcecaoNegocioGenerica("O tipo da linha não pertence a nenhum dos tipos previsto.");
                }
            }
        } catch (IOException ex) {
            throw new ExcecaoNegocioGenerica("A Leitura do Buffer apresentou um problema! " + ex.getMessage());
        }
        return logNaoEncontrados.toString();
    }

    private void montarRelatoriosItemDemonstComLinhaRecuperada(String[] partes, RelatoriosItemDemonst novoRelatorio) {
        novoRelatorio.setDescricao(getValorLinhaAsStringNotEmptyOrNull(partes[1]));
        novoRelatorio.setTipoRelatorioItemDemonstrativo(partes[2] != null ? TipoRelatorioItemDemonstrativo.valueOf(partes[2]) : null);
        novoRelatorio.setUsaConta(getValorLinhaAsBoolean(partes[3]));
        novoRelatorio.setUsaPrograma(getValorLinhaAsBoolean(partes[4]));
        novoRelatorio.setUsaAcao(getValorLinhaAsBoolean(partes[5]));
        novoRelatorio.setUsaSubAcao(getValorLinhaAsBoolean(partes[6]));
        novoRelatorio.setUsaFuncao(getValorLinhaAsBoolean(partes[7]));
        novoRelatorio.setUsaSubFuncao(getValorLinhaAsBoolean(partes[8]));
        novoRelatorio.setUsaUnidadeOrganizacional(getValorLinhaAsBoolean(partes[9]));
        novoRelatorio.setUsaFonteRecurso(getValorLinhaAsBoolean(partes[10]));
        novoRelatorio.setUsaTipoDespesa(getValorLinhaAsBoolean(partes[11]));
        novoRelatorio.setUsaContaFinanceira(getValorLinhaAsBoolean(partes[12]));
        novoRelatorio.setGrupos(getValorLinhaAsInteger(partes[13]));
        novoRelatorio.setNotaExplicativa(getValorLinhaAsStringNotEmptyOrNull(partes[14]));
    }

    private void montarItemDemonstrativoComLinhaRecuperada(String[] partes, ItemDemonstrativo novoItem, RelatoriosItemDemonst novoRelatorio) {
        novoItem.setDescricao(partes[1]);
        novoItem.setNome(partes[2]);
        novoItem.setInverteSinal(getValorLinhaAsBoolean(partes[3]));
        novoItem.setOrdem(getValorLinhaAsInteger(partes[4]));
        novoItem.setGrupo(getValorLinhaAsInteger(partes[5]));
        novoItem.setColuna(getValorLinhaAsInteger(partes[6]));
        novoItem.setEspaco(getValorLinhaAsInteger(partes[7]));
        novoItem.setExibirNoRelatorio(getValorLinhaAsBoolean(partes[8]));
        novoItem.setItemExercicioAnterior(itemDemonstrativoFacade.buscarItemPorDescricaoERelatorio(
            partes[9], novoRelatorio.getDescricao(), novoRelatorio.getTipoRelatorioItemDemonstrativo(), novoRelatorio.getExercicio().getAno() - 1)
        );
    }

    private void atualizarComponenteFormulaItem(RelatoriosItemDemonst novoRelatorio, HashMap<String, List<String>> mapComponenteFormulaItem) {
        for (ItemDemonstrativo item : novoRelatorio.getItensDemonstrativos()) {
            for (FormulaItemDemonstrativo formula : item.getFormulas()) {
                String key = getChaveDaFormula(formula);
                if (mapComponenteFormulaItem.containsKey(key)) {
                    for (String descricaoItem : mapComponenteFormulaItem.get(key)) {
                        formula.getComponenteFormula().add(montarComponenteFormulaItem(formula, descricaoItem));
                    }
                }
            }
        }
    }

    private ItemDemonstrativo buscarItemNoRelatorio(RelatoriosItemDemonst relatoriosItemDemonst, String descricaoItem) {
        for (ItemDemonstrativo itemDemonstrativo : relatoriosItemDemonst.getItensDemonstrativos()) {
            if (itemDemonstrativo.getDescricao().toUpperCase().trim().equals(descricaoItem.toUpperCase().trim())) {
                return itemDemonstrativo;
            }
        }
        return null;
    }

    private String getChaveDaFormula(FormulaItemDemonstrativo formulaDaVez) {
        return formulaDaVez.getItemDemonstrativo().getDescricao().toUpperCase().trim() + formulaDaVez.getOperacaoFormula().name() + (formulaDaVez.getColuna() != null ? formulaDaVez.getColuna().toString() : "");
    }

    private void atualizarMapComponenteFormulaItem(HashMap<String, List<String>> mapComponenteFormulaItem, FormulaItemDemonstrativo formulaDaVez, String[] partes) {
        if (formulaDaVez != null) {
            String key = getChaveDaFormula(formulaDaVez);
            if (!mapComponenteFormulaItem.containsKey(key)) {
                mapComponenteFormulaItem.put(key, Lists.newArrayList());
            }
            mapComponenteFormulaItem.get(key).add(partes[1]);
        }
    }

    private ComponenteFormulaItem montarComponenteFormulaItem(FormulaItemDemonstrativo formulaDaVez, String descricaoItem) {
        ComponenteFormulaItem novoCfItem = new ComponenteFormulaItem();
        novoCfItem.setFormulaItemDemonstrativo(formulaDaVez);
        novoCfItem.setDataRegistro(new Date());
        novoCfItem.setItemDemonstrativo(buscarItemNoRelatorio(formulaDaVez.getItemDemonstrativo().getRelatoriosItemDemonst(), descricaoItem));
        return novoCfItem;
    }

    private void montarComponenteFormulaConta(FormulaItemDemonstrativo formulaDaVez, String[] partes, Exercicio exercicio, StringBuilder logNaoEncontrados) {
        ComponenteFormulaConta novoCfConta = new ComponenteFormulaConta();
        novoCfConta.setFormulaItemDemonstrativo(formulaDaVez);
        novoCfConta.setDataRegistro(new Date());
        novoCfConta.setConta(contaFacade.buscarContaPorCodigoEExercicio(partes[1], exercicio));
        if (novoCfConta.getConta() == null) {
            logNaoEncontrados.append("\r\n");
            logNaoEncontrados.append("Item: ").append(formulaDaVez.getItemDemonstrativo().getDescricao()).append(" | Conta: ").append(partes[1]).append(" - ").append(exercicio.getAno());
        } else {
            formulaDaVez.getComponenteFormula().add(novoCfConta);
        }
    }

    private void montarComponenteFormulaPrograma(FormulaItemDemonstrativo formulaDaVez, String[] partes, Exercicio exercicio, StringBuilder logNaoEncontrados) {
        ComponenteFormulaPrograma novoCfPrograma = new ComponenteFormulaPrograma();
        novoCfPrograma.setFormulaItemDemonstrativo(formulaDaVez);
        novoCfPrograma.setDataRegistro(new Date());
        novoCfPrograma.setProgramaPPA(programaPPAFacade.buscarProgramasPorExercicio(partes[1], partes[2], exercicio));
        if (novoCfPrograma.getProgramaPPA() == null) {
            logNaoEncontrados.append("\r\n");
            logNaoEncontrados.append("Item: ").append(formulaDaVez.getItemDemonstrativo().getDescricao()).append(" | ProgramaPPA: ").append(partes[1]).append(" - ").append(partes[2]).append(" - ").append(exercicio.getAno());
        } else {
            formulaDaVez.getComponenteFormula().add(novoCfPrograma);
        }
    }

    private void montarComponenteFormulaAcao(FormulaItemDemonstrativo formulaDaVez, String[] partes, Exercicio exercicio, StringBuilder logNaoEncontrados) {
        ComponenteFormulaAcao novoCfAcao = new ComponenteFormulaAcao();
        novoCfAcao.setFormulaItemDemonstrativo(formulaDaVez);
        novoCfAcao.setDataRegistro(new Date());
        novoCfAcao.setAcaoPPA(projetoAtividadeFacade.buscarAcaoPPAPeloCodigoEDescricaoEExercicio(partes[1], partes[2], exercicio));
        if (novoCfAcao.getAcaoPPA() == null) {
            logNaoEncontrados.append("\r\n");
            logNaoEncontrados.append("Item: ").append(formulaDaVez.getItemDemonstrativo().getDescricao()).append(" | AcaoPPA: ").append(partes[1]).append(" - ").append(partes[2]).append(" - ").append(exercicio.getAno());
        } else {
            formulaDaVez.getComponenteFormula().add(novoCfAcao);
        }
    }

    private void montarComponenteFormulaSubacao(FormulaItemDemonstrativo formulaDaVez, String[] partes, Exercicio exercicio, StringBuilder logNaoEncontrados) {
        ComponenteFormulaSubacao novoCfSubacao = new ComponenteFormulaSubacao();
        novoCfSubacao.setFormulaItemDemonstrativo(formulaDaVez);
        novoCfSubacao.setDataRegistro(new Date());
        novoCfSubacao.setSubAcaoPPA(subProjetoAtividadeFacade.buscarSubAcaoPPAPeloCodigoEDescricaoEExercicio(partes[1], partes[2], exercicio));
        if (novoCfSubacao.getSubAcaoPPA() == null) {
            logNaoEncontrados.append("\r\n");
            logNaoEncontrados.append("Item: ").append(formulaDaVez.getItemDemonstrativo().getDescricao()).append(" | SubAcaoPPA: ").append(partes[1]).append(" - ").append(partes[2]).append(" - ").append(exercicio.getAno());
        } else {
            formulaDaVez.getComponenteFormula().add(novoCfSubacao);
        }
    }

    private void montarComponenteFormulaFuncao(FormulaItemDemonstrativo formulaDaVez, String[] partes, StringBuilder logNaoEncontrados) {
        ComponenteFormulaFuncao novoCfFuncao = new ComponenteFormulaFuncao();
        novoCfFuncao.setFormulaItemDemonstrativo(formulaDaVez);
        novoCfFuncao.setDataRegistro(new Date());
        novoCfFuncao.setFuncao(funcaoFacade.buscarFuncaoPorCodigo(partes[1]));
        if (novoCfFuncao.getFuncao() == null) {
            logNaoEncontrados.append("\r\n");
            logNaoEncontrados.append("Item: ").append(formulaDaVez.getItemDemonstrativo().getDescricao()).append(" | Funcao: ").append(partes[1]);
        } else {
            formulaDaVez.getComponenteFormula().add(novoCfFuncao);
        }
    }

    private void montarComponenteFormulaSubFuncao(FormulaItemDemonstrativo formulaDaVez, String[] partes, StringBuilder logNaoEncontrados) {
        ComponenteFormulaSubFuncao novoCfSubFuncao = new ComponenteFormulaSubFuncao();
        novoCfSubFuncao.setFormulaItemDemonstrativo(formulaDaVez);
        novoCfSubFuncao.setDataRegistro(new Date());
        novoCfSubFuncao.setSubFuncao(subFuncaoFacade.buscarSubFuncaoPorCodigo(partes[1]));
        if (novoCfSubFuncao.getSubFuncao() == null) {
            logNaoEncontrados.append("\r\n");
            logNaoEncontrados.append("Item: ").append(formulaDaVez.getItemDemonstrativo().getDescricao()).append(" | SubFuncao: ").append(partes[1]);
        } else {
            formulaDaVez.getComponenteFormula().add(novoCfSubFuncao);
        }
    }

    private void montarComponenteFormulaUnidadeOrganizacional(FormulaItemDemonstrativo formulaDaVez, String[] partes, StringBuilder logNaoEncontrados) {
        ComponenteFormulaUnidadeOrganizacional novoCfUnidadeOrg = new ComponenteFormulaUnidadeOrganizacional();
        novoCfUnidadeOrg.setFormulaItemDemonstrativo(formulaDaVez);
        novoCfUnidadeOrg.setDataRegistro(new Date());
        novoCfUnidadeOrg.setUnidadeOrganizacional(unidadeOrganizacionalFacade.buscarUnidadeOrganizacionalPorDescricao(partes[1]));
        if (novoCfUnidadeOrg.getUnidadeOrganizacional() == null) {
            logNaoEncontrados.append("\r\n");
            logNaoEncontrados.append("Item: ").append(formulaDaVez.getItemDemonstrativo().getDescricao()).append(" | UnidadeOrganizacional: ").append(partes[1]);
        } else {
            formulaDaVez.getComponenteFormula().add(novoCfUnidadeOrg);
        }
    }

    private void montarComponenteFormulaFonteRecurso(FormulaItemDemonstrativo formulaDaVez, String[] partes, Exercicio exercicio, StringBuilder logNaoEncontrados) {
        ComponenteFormulaFonteRecurso novoCfFonte = new ComponenteFormulaFonteRecurso();
        novoCfFonte.setFormulaItemDemonstrativo(formulaDaVez);
        novoCfFonte.setDataRegistro(new Date());
        novoCfFonte.setFonteDeRecursos(fonteDeRecursosFacade.buscarPorCodigoExercicio(partes[1], exercicio));
        if (novoCfFonte.getFonteDeRecursos() == null) {
            logNaoEncontrados.append("\r\n");
            logNaoEncontrados.append("Item: ").append(formulaDaVez.getItemDemonstrativo().getDescricao()).append(" | FonteDeRecursos: ").append(partes[1]).append(" - ").append(exercicio.getAno());
        } else {
            formulaDaVez.getComponenteFormula().add(novoCfFonte);
        }
    }

    private void montarComponenteFormulaTipoDesp(FormulaItemDemonstrativo formulaDaVez, String[] partes, StringBuilder logNaoEncontrados) {
        ComponenteFormulaTipoDesp novoCfTipoDespesa = new ComponenteFormulaTipoDesp();
        novoCfTipoDespesa.setFormulaItemDemonstrativo(formulaDaVez);
        novoCfTipoDespesa.setDataRegistro(new Date());
        novoCfTipoDespesa.setTipoDespesaORC(partes[1] != null ? TipoDespesaORC.valueOf(partes[1]) : null);
        if (novoCfTipoDespesa.getTipoDespesaORC() == null) {
            logNaoEncontrados.append("\r\n");
            logNaoEncontrados.append("Item: ").append(formulaDaVez.getItemDemonstrativo().getDescricao()).append(" | TipoDespesaORC: ").append(partes[1]);
        } else {
            formulaDaVez.getComponenteFormula().add(novoCfTipoDespesa);
        }
    }

    private void montarComponenteFormulaSubConta(FormulaItemDemonstrativo formulaDaVez, String[] partes, Exercicio exercicio, StringBuilder logNaoEncontrados) {
        ComponenteFormulaSubConta novoCfSubConta = new ComponenteFormulaSubConta();
        novoCfSubConta.setFormulaItemDemonstrativo(formulaDaVez);
        novoCfSubConta.setDataRegistro(new Date());
        novoCfSubConta.setSubConta(subContaFacade.buscarContaFinanceiraPorCodigoExericicio(partes[1], exercicio));
        if (novoCfSubConta.getSubConta() == null) {
            logNaoEncontrados.append("\r\n");
            logNaoEncontrados.append("Item: ").append(formulaDaVez.getItemDemonstrativo().getDescricao()).append(" | SubConta: ").append(partes[1]).append(" - ").append(exercicio.getAno());
        } else {
            formulaDaVez.getComponenteFormula().add(novoCfSubConta);
        }
    }

    private Boolean getValorLinhaAsBoolean(String parte) {
        return (parte != null && !parte.trim().isEmpty()) && !parte.trim().equals("null") ? Boolean.valueOf(parte) : Boolean.FALSE;
    }

    private Integer getValorLinhaAsInteger(String parte) {
        return (parte != null && !parte.trim().isEmpty()) && !parte.trim().equals("null") ? Integer.valueOf(parte) : null;
    }

    private Long getValorLinhaAsLong(String parte) {
        return (parte != null && !parte.trim().isEmpty()) && !parte.trim().equals("null") ? Long.valueOf(parte) : null;
    }

    private String getValorLinhaAsStringNotEmptyOrNull(String parte) {
        return (parte != null && !parte.trim().isEmpty()) && !parte.trim().equals("null") ? parte : null;
    }

    public void gerarPdfLog(String log) {
        String conteudo = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
            + " <!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">"
            + " <html>"
            + " <head>"
            + " <style type=\"text/css\">@page{size: A4 portrait;}</style>"
            + " <title>"
            + " < META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=iso-8859-1\">"
            + " </title>"
            + " </head>"
            + " <body>"
            + "<center>"
            + "<table>"
            + "<td><center>LOG IMPORTAR RELATÓRIO ITEM DEMONSTRATIVO</center></td>"
            + "</tr>"
            + "</table>"
            + "</center>"
            + "<div style=\"border : solid #92B8D3 1px; \""
            + "<p style='font-size : 8px!important;'>"
            + log.replace("\r\n", "<br/>")
            + "</p>"
            + "</div>"
            + " </body>"
            + " </html>";

        Util.downloadPDF("LogImportacaoRelatorioItemDemonstrativo", conteudo, FacesContext.getCurrentInstance());
    }
}
