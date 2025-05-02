package br.com.webpublico.negocios;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.controlerelatorio.LivroDividaAtivaRelatorio;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoInscricaoDividaAtiva;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.util.ResultadoValidacao;
import br.com.webpublico.util.UltimoLinhaDaPaginaDoLivroDividaAtiva;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRSwapFile;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Stateless
public class LivroDividaAtivaFacade extends AbstractFacade<LivroDividaAtiva> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private InscricaoDividaAtivaFacade inscricaoDividaAtivaFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private ParametrosDividaAtivaFacade parametrosDividaAtivaFacade;
    @EJB
    private TermoInscricaoDividaAtivaFacade termoInscricaoDividaAtivaFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private PessoaFacade pessoaFacade;

    public LivroDividaAtivaFacade() {
        super(LivroDividaAtiva.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public InscricaoDividaAtivaFacade getInscricaoDividaAtivaFacade() {
        return inscricaoDividaAtivaFacade;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    public List<Exercicio> completaExercicio(String parte) {
        return exercicioFacade.listaFiltrandoEspecial(parte.trim());
    }

    public List<InscricaoDividaAtiva> completaInscricaoDividaAtiva(String parte) {
        Query consulta = em.createQuery("select inscricao from InscricaoDividaAtiva inscricao"
                + " inner join inscricao.exercicio exer"
                + " where to_char(inscricao.numero) like :parte"
                + " or to_char(exer.ano) like :parte");
        consulta.setParameter("parte", "%" + parte.toLowerCase() + "%");
        return (List<InscricaoDividaAtiva>) consulta.getResultList();
    }

    public ResultadoValidacao salva(LivroDividaAtiva esteSelecionado) {
        ResultadoValidacao result = validaProcesso(esteSelecionado);
        if (result.isValidado()) {
            try {
                if (esteSelecionado.getId() == null) {
                    esteSelecionado = em.merge(esteSelecionado);
                } else {
                    List<ItemLivroDividaAtiva> itensNaoSalvos = esteSelecionado.getItensLivros();
                    LivroDividaAtiva livro = em.find(LivroDividaAtiva.class, esteSelecionado.getId());
                    livro.setItensLivros(itensNaoSalvos);
                    livro.setTotalPaginas(esteSelecionado.getTotalPaginas());
                    esteSelecionado = em.merge(livro);
                }
                result.setIdObjetoSalvo(esteSelecionado.getId());
                result.limpaMensagens();
                result.addInfo(null, "Salvo com sucesso!", "");
            } catch (Exception ex) {
                result.limpaMensagens();
                result.addErro(null, "Erro ao tentar salvar o Livro de Dívida Ativa!", ex.getMessage());
            }
        }
        return result;
    }

    private ResultadoValidacao validaProcesso(LivroDividaAtiva selecionado) {
        ResultadoValidacao resultado = new ResultadoValidacao();
        final String summary = "Não foi possível continuar!";
        if (selecionado.getExercicio() == null || selecionado.getExercicio().getId() == null) {
            resultado.addErro(null, summary, "O Exercício deve ser informado.");
        }
        if (selecionado.getNumero() == null) {
            resultado.addErro(null, summary, "O Número deve ser informado.");
        }
        if (selecionado.getTipoCadastroTributario() == null) {
            resultado.addErro(null, summary, "O Tipo de Cadastro deve ser informado.");
        }
        if (selecionado.getItensLivros() == null || selecionado.getItensLivros().isEmpty()) {
            resultado.addErro(null, summary, "É necessário que informe pelo menos um cadastro para salvar o Livro.");
        }
        return resultado;
    }

    @Override
    public LivroDividaAtiva recuperar(Object id) {
        LivroDividaAtiva livro = em.find(LivroDividaAtiva.class, id);
        return livro;
    }

    public ItemInscricaoDividaAtiva recuperaParcelas(ItemInscricaoDividaAtiva itemInscricaoDividaAtiva) {
        itemInscricaoDividaAtiva = em.find(ItemInscricaoDividaAtiva.class, itemInscricaoDividaAtiva.getId());
        itemInscricaoDividaAtiva.getItensInscricaoDividaParcelas().size();
        return itemInscricaoDividaAtiva;
    }

    public List<ItemInscricaoDividaAtiva> recuperaItens(InscricaoDividaAtiva inscricao, Divida divida, String cadastroInicial, String cadastroFinal, Pessoa pessoa) {
        String sql = "select item.id, item.inscricaodividaativa_id, item.divida_id, divida.nrLivroDividaAtiva, item.situacao " +
                " from ItemInscricaoDividaAtiva item " +
                " inner join Calculo cal on cal.id = item.id " +
                " inner join Divida divida on divida.id = item.divida_id ";

        if (TipoCadastroTributario.IMOBILIARIO.equals(inscricao.getTipoCadastroTributario())) {
            sql += " inner join cadastroImobiliario ci on ci.id = cal.cadastro_id ";
        } else if (TipoCadastroTributario.ECONOMICO.equals(inscricao.getTipoCadastroTributario())) {
            sql += " inner join cadastroEconomico ce on ce.id = cal.cadastro_id ";
        } else if (TipoCadastroTributario.RURAL.equals(inscricao.getTipoCadastroTributario())) {
            sql += " inner join cadastroRural cr on cr.id = cal.cadastro_id ";
        }
        sql += " where item.inscricaoDividaAtiva_id = :inscricao ";
        if (divida != null) {
            sql += " and item.divida_id = :idDivida ";
        }
        if (TipoCadastroTributario.IMOBILIARIO.equals(inscricao.getTipoCadastroTributario())) {
            sql += " and (ci.inscricaoCadastral >= :cadastroInicial and ci.inscricaoCadastral <= :cadastroFinal) ";
        } else if (TipoCadastroTributario.ECONOMICO.equals(inscricao.getTipoCadastroTributario())) {
            sql += " and (ce.inscricaoCadastral >= :cadastroInicial and ce.inscricaoCadastral <= :cadastroFinal) ";
        } else if (TipoCadastroTributario.RURAL.equals(inscricao.getTipoCadastroTributario())) {
            sql += " and (cr.codigo >= :cadastroInicial and cr.codigo <= :cadastroFinal) ";
        } else if (TipoCadastroTributario.PESSOA.equals(inscricao.getTipoCadastroTributario())) {
            sql += " and item.pessoa_id = :idPessoa ";
        }
        sql += " and item.id not in (select linha.itemInscricaoDividaAtiva_id from LinhaDoLivroDividaAtiva linha) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("inscricao", inscricao.getId());
        if (divida != null) {
            q.setParameter("idDivida", divida.getId());
        }
        if (!TipoCadastroTributario.PESSOA.equals(inscricao.getTipoCadastroTributario())) {
            q.setParameter("cadastroInicial", cadastroInicial);
            q.setParameter("cadastroFinal", cadastroFinal);
        } else {
            q.setParameter("idPessoa", pessoa.getId());
        }
        List<Object[]> lista = q.getResultList();
        List<ItemInscricaoDividaAtiva> retorno = Lists.newArrayList();
        for (Object[] obj : lista) {
            ItemInscricaoDividaAtiva item = new ItemInscricaoDividaAtiva();
            item.setId(((BigDecimal) obj[0]).longValue());
            InscricaoDividaAtiva ins = new InscricaoDividaAtiva();
            ins.setId(((BigDecimal) obj[1]).longValue());
            item.setInscricaoDividaAtiva(ins);
            Divida div = new Divida();
            div.setId(((BigDecimal) obj[2]).longValue());
            div.setNrLivroDividaAtiva(((String) obj[3]));
            item.setDivida(div);
            item.setSituacao(ItemInscricaoDividaAtiva.Situacao.valueOf(((String) obj[4])));
            retorno.add(item);
        }
        return retorno;
    }

    public void salvar(LinhaDoLivroDividaAtiva linha) {
        em.persist(linha);
    }

    public ItemLivroDividaAtiva salvar(ItemLivroDividaAtiva item) {
        return em.merge(item);
    }

    public LivroDividaAtiva recuperaLivroComNumero(LivroDividaAtiva selecionado) {
        Query consulta = em.createQuery("select livro from LivroDividaAtiva livro "
                + " inner join livro.itensLivros item"
                + " where livro.numero = :numero and livro.exercicio = :exercicio"
                + " and livro.tipoCadastroTributario = :tipoCadastro ");
        consulta.setParameter("numero", selecionado.getNumero());
        consulta.setParameter("exercicio", selecionado.getExercicio());
        consulta.setParameter("tipoCadastro", selecionado.getTipoCadastroTributario());
        consulta.setMaxResults(1);

        List<LivroDividaAtiva> livros = consulta.getResultList();
        if (!livros.isEmpty()) {
            LivroDividaAtiva livro = livros.get(0);
            for (ItemLivroDividaAtiva item : livro.getItensLivros()) {
                item.getLinhasDoLivro().size();
            }
            return livro;
        } else {
            return selecionado;
        }
    }

    public ItemLivroDividaAtiva recuperaItens(ItemLivroDividaAtiva item) {
        item = em.find(ItemLivroDividaAtiva.class, item.getId());
        item.getLinhasDoLivro().size();
        return item;
    }

    public UltimoLinhaDaPaginaDoLivroDividaAtiva getUltimoLinhaDaPaginaDoLivroDividaAtiva(LivroDividaAtiva livro) {
        String hql = "select new br.com.webpublico.util.UltimoLinhaDaPaginaDoLivroDividaAtiva( "
                + " max(linha.numeroDaLinha), "
                + " max(linha.sequencia), "
                + " linha.pagina) from LinhaDoLivroDividaAtiva linha"
                + " where linha.pagina = (select max(l.pagina) from LinhaDoLivroDividaAtiva l where l.itemLivroDividaAtiva.livroDividaAtiva = :livro) "
                + " and linha.itemLivroDividaAtiva.livroDividaAtiva = :livro"
                + " group by linha.pagina";
        Query q = em.createQuery(hql).setParameter("livro", livro);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (UltimoLinhaDaPaginaDoLivroDividaAtiva) q.getSingleResult();
        }
        return new UltimoLinhaDaPaginaDoLivroDividaAtiva(1l, 1l, 1l);
    }

    public List<LivroDividaAtiva> completaLivroDividaAtiva(String parte) {
        String hql = "select livro from LivroDividaAtiva livro where to_char(livro.numero) like :parte";
        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte + "%");
        return q.getResultList();
    }

    public List<InscricaoDividaAtiva> completaInscricaoDividaAtiva(TipoCadastroTributario tipoCadastro, Exercicio exercicio, Divida divida, String cadastroInicial, String cadastroFinal, Pessoa pessoa) {
        String sql = "select distinct pro.*, inscricao.* from InscricaoDividaAtiva inscricao "
                + " inner join ProcessoCalculo pro on pro.id = inscricao.id "
                + " inner join ItemInscricaoDividaAtiva item on item.InscricaoDividaAtiva_id = inscricao.id "
                + " inner join Calculo cal on cal.id = item.id ";
        if (TipoCadastroTributario.IMOBILIARIO.equals(tipoCadastro)) {
            sql += " inner join cadastroImobiliario ci on ci.id = cal.cadastro_id ";
        } else if (TipoCadastroTributario.ECONOMICO.equals(tipoCadastro)) {
            sql += " inner join cadastroEconomico ce on ce.id = cal.cadastro_id ";
        } else if (TipoCadastroTributario.RURAL.equals(tipoCadastro)) {
            sql += " inner join cadastroRural cr on cr.id = cal.cadastro_id ";
        }
        sql += " where inscricao.exercicio_id = :idExercicio "
                + "   and inscricao.tipoCadastroTributario = :tipoCadastro "
                + "   and item.id not in (select linha.itemInscricaoDividaAtiva_id from LinhaDoLivroDividaAtiva linha) "
                + "   and inscricao.situacaoInscricaoDividaAtiva = :situacaoInscricao ";
        if (divida != null) {
            sql += " and item.divida_id = :idDivida ";
        }
        if (TipoCadastroTributario.IMOBILIARIO.equals(tipoCadastro)) {
            sql += " and (ci.inscricaoCadastral >= :cadastroInicial and ci.inscricaoCadastral <= :cadastroFinal) ";
        } else if (TipoCadastroTributario.ECONOMICO.equals(tipoCadastro)) {
            sql += " and (ce.inscricaoCadastral >= :cadastroInicial and ce.inscricaoCadastral <= :cadastroFinal) ";
        } else if (TipoCadastroTributario.RURAL.equals(tipoCadastro)) {
            sql += " and (cr.codigo >= :cadastroInicial and cr.codigo <= :cadastroFinal) ";
        } else if (TipoCadastroTributario.PESSOA.equals(tipoCadastro)) {
            sql += " and item.pessoa_id = :idPessoa ";
        }

        Query consulta = em.createNativeQuery(sql, InscricaoDividaAtiva.class);
        consulta.setParameter("idExercicio", exercicio.getId());
        consulta.setParameter("tipoCadastro", tipoCadastro.name());
        consulta.setParameter("situacaoInscricao", SituacaoInscricaoDividaAtiva.FINALIZADO.name());
        if (divida != null) {
            consulta.setParameter("idDivida", divida.getId());
        }
        if (TipoCadastroTributario.PESSOA.equals(tipoCadastro)) {
            consulta.setParameter("idPessoa", pessoa.getId());
        } else {
            consulta.setParameter("cadastroInicial", cadastroInicial);
            consulta.setParameter("cadastroFinal", cadastroFinal);
        }
        //new Util().imprimeSQL(sql, consulta);

        List<InscricaoDividaAtiva> lista = consulta.getResultList();
        return lista;
    }

    public List<LivroDividaAtiva> salvaLivro(List<LivroDividaAtiva> entity) {
        List<LivroDividaAtiva> retorno = Lists.newArrayList();
        for (LivroDividaAtiva livro : entity) {
            retorno.add(em.merge(livro));
        }
        return retorno;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public ResultadoValidacao processar(LivroDividaAtiva livro, final LivroDividaAtiva.TipoOrdenacao tipoOrdenacao, Divida divida, String cadastroInicial, String cadastroFinal, Pessoa pessoa) throws ExcecaoNegocioGenerica, AtributosNulosException {
        Long sequencia = 1l;
        Long pagina = 1l;
        Long numeroLinha = 1l;

        if (livro.getId() != null) {
            livro = recuperar(livro.getId());
            UltimoLinhaDaPaginaDoLivroDividaAtiva ultimo = getUltimoLinhaDaPaginaDoLivroDividaAtiva(livro);
            if (ultimo != null) {
                sequencia = ultimo.getSequencia();
                sequencia++;
                pagina = ultimo.getPagina();
                numeroLinha = ultimo.getLinha();
                numeroLinha++;
            }
        }

        Long numeroDoTermoInscricao = termoInscricaoDividaAtivaFacade.recuperaProximoCodigoTermoInscricaoDividaAtiva();
        for (ItemLivroDividaAtiva itemLivroDividaAtiva : livro.getItensLivros()) {
            if (!itemLivroDividaAtiva.isProcessado()) {
                List<ItemInscricaoDividaAtiva> recuperaItens = recuperaItens(itemLivroDividaAtiva.getInscricaoDividaAtiva(), divida, cadastroInicial, cadastroFinal, pessoa);
//                Collections.sort(recuperaItens, new Comparator() {
//                    @Override
//                    public int compare(Object o1, Object o2) {
//                        if (tipoOrdenacao.equals(LivroDividaAtiva.TipoOrdenacao.NUMERICA)) {
//                            ItemInscricaoDividaAtiva item1 = (ItemInscricaoDividaAtiva) o1;
//                            ItemInscricaoDividaAtiva item2 = (ItemInscricaoDividaAtiva) o2;
//                            Long numeroCadastro1 = 0L;
//                            if (item1.getCadastro() != null) {
//                                numeroCadastro1 = Long.parseLong(item1.getCadastro().getNumeroCadastro());
//                            }
//                            Long numeroCadastro2 = 0L;
//                            if (item2.getCadastro() != null) {
//                                numeroCadastro2 = Long.parseLong(item2.getCadastro().getNumeroCadastro());
//                            }
//                            return numeroCadastro1.compareTo(numeroCadastro2);
//                        } else {
//                            ItemInscricaoDividaAtiva item1 = (ItemInscricaoDividaAtiva) o1;
//                            ItemInscricaoDividaAtiva item2 = (ItemInscricaoDividaAtiva) o2;
//                            try {
//                                Pessoa pessoa1 = pessoaFacade.recuperaPessoasDoCadastro(item1.getCadastro()).get(0);
//                                Pessoa pessoa2 = pessoaFacade.recuperaPessoasDoCadastro(item2.getCadastro()).get(0);
//                                return pessoa1.getNome().compareTo(pessoa2.getNome());
//                            } catch (Exception e) {
//                                return 0;
//                            }
//                        }
//                    }
//                });

                for (ItemInscricaoDividaAtiva itemInscricaoDividaAtiva : recuperaItens) {
                    if (itemInscricaoDividaAtiva.getSituacao().equals(ItemInscricaoDividaAtiva.Situacao.ATIVO)) {
                        if (itemInscricaoDividaAtiva.getDivida().getNrLivroDividaAtiva() != null) {
                            if (livro.getNumero().equals(Long.parseLong(itemInscricaoDividaAtiva.getDivida().getNrLivroDividaAtiva()))) {
                                LinhaDoLivroDividaAtiva linha = new LinhaDoLivroDividaAtiva();
                                linha.setItemInscricaoDividaAtiva(itemInscricaoDividaAtiva);
                                linha.setItemLivroDividaAtiva(itemLivroDividaAtiva);
                                linha.setNumeroDaLinha(numeroLinha);
                                linha.setPagina(pagina);
                                linha.setSequencia(sequencia);

                                TermoInscricaoDividaAtiva termo = new TermoInscricaoDividaAtiva();
                                termo.setNumero(numeroDoTermoInscricao.toString());
                                linha.setTermoInscricaoDividaAtiva(termo);
                                numeroLinha++;
                                sequencia++;
                                numeroDoTermoInscricao++;
                                if (numeroLinha > livro.QUANTIDADE_LINHA_POR_PAGINA) {
                                    numeroLinha = 1l;
                                    pagina++;
                                }
                                itemLivroDividaAtiva.getLinhasDoLivro().add(linha);
                            }
                        }
                    }
                }

                itemLivroDividaAtiva.setProcessado(true);
            }
        }
        livro.setTotalPaginas(pagina);
        livro.setTipoOrdenacao(tipoOrdenacao);
        ResultadoValidacao resultado = salva(livro);
        if (resultado.isValidado()) {
            resultado.addInfo(null, "Foram gerados " + numeroDoTermoInscricao + " Termo de Inscrição de Dívida Ativa.", "");
        }
        return resultado;
    }

    public LinhaDoLivroDividaAtiva recuperaLinhaPorItemInscricao(ItemInscricaoDividaAtiva item) {
        String hql = "select linha from LinhaDoLivroDividaAtiva linha where linha.itemInscricaoDividaAtiva = :item";
        Query q = em.createQuery(hql);
        q.setParameter("item", item);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (LinhaDoLivroDividaAtiva) q.getResultList().get(0);
    }

    @Override
    public List<LivroDividaAtiva> lista() {
        String hql = "select l from LivroDividaAtiva l order by l.id desc";
        Query q = em.createQuery(hql);
        return q.getResultList();
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void geraRelatorio(LivroDividaAtivaRelatorio livroDividaAtivaRelatorio) {
        Connection con = livroDividaAtivaRelatorio.recuperaConexaoJDBC();
        try {
            livroDividaAtivaRelatorio.getParametros().put(JRParameter.REPORT_LOCALE, new Locale("pt", "BR"));
            String nomeArquivoSwap = this.getClass().getSimpleName() + System.nanoTime();
            File swapFile = File.createTempFile(nomeArquivoSwap, ".swp");
            nomeArquivoSwap = swapFile.getParent();
            swapFile.delete();
            JRSwapFileVirtualizer virtualizer = new JRSwapFileVirtualizer(3, new JRSwapFile(nomeArquivoSwap, 2048, 1024), false);
            livroDividaAtivaRelatorio.getParametros().put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
            JasperPrint jasperPrint = JasperFillManager.fillReport(livroDividaAtivaRelatorio.getReport(), livroDividaAtivaRelatorio.getParametros(), con);
            livroDividaAtivaRelatorio.setDadosByte(new ByteArrayOutputStream());
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, livroDividaAtivaRelatorio.getDadosByte());
            exporter.exportReport();
            livroDividaAtivaRelatorio.setGerando(false);
            livroDividaAtivaRelatorio.setGerado(true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            AbstractReport.fecharConnection(con);
        }

    }


}
