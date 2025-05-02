/*
 * Codigo gerado automaticamente em Fri Feb 11 09:06:37 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AuxiliarAndamentoBBAtuarial;
import br.com.webpublico.enums.TipoEventoFP;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.joda.time.DateTime;
import org.joda.time.Days;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class BBAtuarialFacade extends AbstractFacade<BBAtuarial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ArquivoFacade arquivoFacade;
    @Resource
    private SessionContext sctx;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private AposentadoriaFacade aposentadoriaFacade;
    @EJB
    private DependenteVinculoFPFacade dependenteVinculoFPFacade;
    @EJB
    private PensionistaFacade pensionistaFacade;
    @EJB
    private DocumentoPessoalFacade documentoPessoalFacade;
    @EJB
    private DependenteFacade dependenteFacade;
    @EJB
    private EnquadramentoFuncionalFacade enquadramentoFuncionalFacade;
    @EJB
    private AverbacaoTempoServicoFacade averbacaoTempoServicoFacade;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ContratoVinculoDeContratoFacade contratoVinculoDeContratoFacade;
    @EJB
    private CategoriaPCSFacade categoriaPCSFacade;
    @EJB
    private ProgressaoPCSFacade progressaoPCSFacade;
    @EJB
    private EnquadramentoPCSFacade enquadramentoPCSFacade;
    @EJB
    private EventoFPFacade eventoFPFacade;
    private DecimalFormat moneyFormat = new DecimalFormat("#,###,##0.00");
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;

    public BBAtuarialFacade() {
        super(BBAtuarial.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Long ultimoNumeroSequenciaMaisUm() {
        String hql = "select coalesce(max(sequencia),0)+1 from BBAtuarial ";
        Query q = em.createQuery(hql);
        return (Long) q.getSingleResult();
    }

    public BBAtuarial salvarRetornando(BBAtuarial entity, InputStream inputStream) {
        try {
            arquivoFacade.novoArquivo(entity.getArquivo(), inputStream);

            return em.merge(entity);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public BigDecimal getSomaDosEventos(Long vinculoId, Integer ano) {
        String sql = " select (select coalesce(sum(iff.valor),0) from itemfichafinanceirafp iff                    "
                + "inner join fichafinanceirafp ff on ff.id = iff.fichafinanceirafp_id                             "
                + "inner join folhadepagamento  fp on fp.id = ff.folhadepagamento_id                               "
                + "     where fp.ano = :ano                                                                        "
                + "       and ff.vinculofp_id = :vinculo_id                                                        "
                + "       and iff.tipoeventofp = :tipoVantagem)                                                    "
                + " -                                                                                              "
                + "    (select coalesce(sum(iff.valor),0) from itemfichafinanceirafp iff                           "
                + " inner join fichafinanceirafp ff on ff.id = iff.fichafinanceirafp_id                            "
                + " inner join folhadepagamento fp on fp.id = ff.folhadepagamento_id                               "
                + "      where fp.ano = :ano                                                                       "
                + "        and ff.vinculofp_id = :vinculo_id                                                       "
                + "        and iff.tipoeventofp = :tipoDesconto) as resultado from dual                            ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", ano);
        q.setParameter("vinculo_id", vinculoId);
        q.setParameter("tipoVantagem", TipoEventoFP.VANTAGEM.name());
        q.setParameter("tipoDesconto", TipoEventoFP.DESCONTO.name());

        try {
            BigDecimal resultado = (BigDecimal) q.getSingleResult();
            if (resultado == null) {
                return BigDecimal.ZERO;
            }
            return resultado;
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }

    public List<List> particionarEmDez(List objetos) {
        List<List> retorno = new ArrayList<>();
        int parte = objetos.size() / 10;
        retorno.add(objetos.subList(0, parte));
        retorno.add(objetos.subList(parte, parte * 2));
        retorno.add(objetos.subList(parte * 2, parte * 3));
        retorno.add(objetos.subList(parte * 3, parte * 4));
        retorno.add(objetos.subList(parte * 4, parte * 5));
        retorno.add(objetos.subList(parte * 5, parte * 6));
        retorno.add(objetos.subList(parte * 6, parte * 7));
        retorno.add(objetos.subList(parte * 7, parte * 8));
        retorno.add(objetos.subList(parte * 8, parte * 9));
        retorno.add(objetos.subList(parte * 9, objetos.size()));
        return retorno;
    }

    public synchronized void addLog(AuxiliarAndamentoBBAtuarial auxBB, String valor, String abre, String fecha) {
        auxBB.contar();
        valor = abre + valor + fecha;
        try {
            String agora = Util.dateHourToString(new Date());
            auxBB.getLog().add(agora + " - " + auxBB.getCalculados() + valor.concat("<br/>"));
        } catch (NullPointerException npe) {
            if (auxBB == null) {
                auxBB = new AuxiliarAndamentoBBAtuarial();
            }
            auxBB.setLog(new ArrayList<String>());
            auxBB.getLog().add(valor);
        }
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public Future<AuxiliarAndamentoBBAtuarial> getConteudoArquivoServidoresAtivos(AuxiliarAndamentoBBAtuarial auxBB, List<Long> idsServidoresAtivos) {
        AsyncResult<AuxiliarAndamentoBBAtuarial> result = new AsyncResult<>(auxBB);
        for (Long id : idsServidoresAtivos) {
            if (sctx != null && sctx.wasCancelCalled()) {
                break;
            }
            ContratoFP contrato = em.find(ContratoFP.class, id);
            HSSFRow row = auxBB.criarRowServidoresAtivos();
            gerarInformacoesServidorAtivo(contrato, auxBB, row);
            addLog(auxBB, " - <font style='color : green;'><i>GERADO COM SUCESSO - SERVIDOR ATIVO</i></font> &nbsp;&bull; REGISTRO DE : " + contrato.getMatriculaFP().getPessoa().getNome(), "<b>", "</b>");
        }
        return result;
    }

    private void gerarInformacoesServidorAtivo(ContratoFP c, AuxiliarAndamentoBBAtuarial auxBB, HSSFRow row) {
        int coluna = 0;
        auxBB.getWorkBookServidoresAtivos().escreverEm(row, coluna++, Double.parseDouble(c.getMatriculaFP().getMatricula().trim()));
        auxBB.getWorkBookServidoresAtivos().escreverEm(row, coluna++, StringUtil.retornaApenasNumeros(c.getMatriculaFP().getPessoa().getCpf()));
        auxBB.getWorkBookServidoresAtivos().escreverEm(row, coluna++, c.getMatriculaFP().getPessoa().getDataNascimento());
        auxBB.getWorkBookServidoresAtivos().escreverEm(row, coluna++, c.getMatriculaFP().getPessoa().getSexo() != null ? Double.parseDouble(c.getMatriculaFP().getPessoa().getSexo().getCodigo()) : "");
        auxBB.getWorkBookServidoresAtivos().escreverEm(row, coluna++, c.getMatriculaFP().getPessoa().getEstadoCivil() != null ? Double.parseDouble(c.getMatriculaFP().getPessoa().getEstadoCivil().getCodigoRPPS()) : "");

        // Cônjuge
        CertidaoCasamento cc = documentoPessoalFacade.recuperaCertidaoCasamento(c.getMatriculaFP().getPessoa());
        if (cc != null) {
            auxBB.getWorkBookServidoresAtivos().escreverEm(row, coluna++, cc.getDataNascimentoConjuge());
        } else {
            auxBB.getWorkBookServidoresAtivos().escreverEm(row, coluna++, "");
        }

        // Dependentes
        List<Date> dependentes = dependenteFacade.getDependentesDePessoaParaBBAtuarial(c.getMatriculaFP().getPessoa(), auxBB.getBbAtuarial().getDataReferencia());
        auxBB.getWorkBookServidoresAtivos().escreverEm(row, coluna++, !CollectionUtils.isEmpty(dependentes) ? dependentes.size() : Integer.parseInt("0"));
        auxBB.getWorkBookServidoresAtivos().escreverEm(row, coluna++, !CollectionUtils.isEmpty(dependentes) ? dependentes.get(0) : "");

        auxBB.getWorkBookServidoresAtivos().escreverEm(row, coluna++, getTempoContribuicaoRGPS(c));
        auxBB.getWorkBookServidoresAtivos().escreverEm(row, coluna++, getTempoContribuicaoRPPS(c));
        auxBB.getWorkBookServidoresAtivos().escreverEm(row, coluna++, c.getDataAdmissao());
        auxBB.getWorkBookServidoresAtivos().escreverEm(row, coluna++, c.getDataAdmissao());

        VinculoDeContratoFP tipoVinculo = getTipoVinculo(c, auxBB.getBbAtuarial().getDataReferencia());
        auxBB.getWorkBookServidoresAtivos().escreverEm(row, coluna++, tipoVinculo != null ? tipoVinculo.getCodigo() : "");
        auxBB.getWorkBookServidoresAtivos().escreverEm(row, coluna++, getDescricaoOrgaoEntidade(c).toUpperCase());
        auxBB.getWorkBookServidoresAtivos().escreverEm(row, coluna++, c.getCargo() != null && c.getCargo().getCodigoCarreira() != null ? Double.parseDouble(c.getCargo().getCodigoCarreira()) : "");
        auxBB.getWorkBookServidoresAtivos().escreverEm(row, coluna++, c.getCargo() != null ? Double.parseDouble(c.getCargo().getCodigoDoCargo()) : "");

        auxBB.getWorkBookServidoresAtivos().escreverEm(row, coluna++, Integer.parseInt("0")); // Segundo conversa com a Márcia Bastos, 16/04/2015, na prefeitura não há aposentadoria especial
        auxBB.getWorkBookServidoresAtivos().escreverEm(row, coluna++, Integer.parseInt("1")); // Segundo conversa com a Márcia Bastos, 16/04/2015, na prefeitura não há previdência complementar

        for (int i = DataUtil.getAno(auxBB.getBbAtuarial().getDataReferencia()) - 4; i <= DataUtil.getAno(auxBB.getBbAtuarial().getDataReferencia()) - 1; i++){
            auxBB.getWorkBookServidoresAtivos().escreverEmMonetario(row, coluna++, getValorVerbasFixasDezembro(c, i).doubleValue());
        }
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public Future<AuxiliarAndamentoBBAtuarial> getConteudoArquivoAposentados(AuxiliarAndamentoBBAtuarial auxBB, List<Long> idsAposentados) {
        AsyncResult<AuxiliarAndamentoBBAtuarial> result = new AsyncResult<>(auxBB);

        for (Long id : idsAposentados) {
            if (sctx != null && sctx.wasCancelCalled()) {
                break;
            }
            Aposentadoria a = em.find(Aposentadoria.class, id);
            HSSFRow row = auxBB.criarRowAposentados();
            gerarInformacoesAposentado(a, auxBB, row);
            addLog(auxBB, " - <font style='color : green;'><i>GERADO COM SUCESSO - APOSENTADO</i></font> &nbsp;&bull; REGISTRO DE : " + a.getMatriculaFP().getPessoa().getNome(), "<b>", "</b>");
        }
        return result;
    }

    private void gerarInformacoesAposentado(Aposentadoria a, AuxiliarAndamentoBBAtuarial auxBB, HSSFRow row) {
        int coluna = 0;
        auxBB.getWorkBookAposentados().escreverEm(row, coluna++, Double.parseDouble(a.getMatriculaFP().getMatricula()));
        auxBB.getWorkBookAposentados().escreverEm(row, coluna++, a.getMatriculaFP().getPessoa().getDataNascimento());
        auxBB.getWorkBookAposentados().escreverEm(row, coluna++, a.getMatriculaFP().getPessoa().getSexo() != null ? Double.parseDouble(a.getMatriculaFP().getPessoa().getSexo().getCodigo()) : "");
        auxBB.getWorkBookAposentados().escreverEm(row, coluna++, a.getMatriculaFP().getPessoa().getEstadoCivil() != null ? Double.parseDouble(a.getMatriculaFP().getPessoa().getEstadoCivil().getCodigoRPPS()) : "");

        // Conjuge
        CertidaoCasamento cc = documentoPessoalFacade.recuperaCertidaoCasamento(a.getMatriculaFP().getPessoa());
        auxBB.getWorkBookAposentados().escreverEm(row, coluna++, cc != null ? cc.getDataNascimentoConjuge() : "");

        // Dependentes
        List<Date> dependentes = dependenteFacade.getDependentesDePessoaParaBBAtuarial(a.getMatriculaFP().getPessoa(), auxBB.getBbAtuarial().getDataReferencia());
        auxBB.getWorkBookAposentados().escreverEm(row, coluna++, !CollectionUtils.isEmpty(dependentes) ? dependentes.size() : 0);
        auxBB.getWorkBookAposentados().escreverEm(row, coluna++, !CollectionUtils.isEmpty(dependentes) ? dependentes.get(0) : "");

        auxBB.getWorkBookAposentados().escreverEm(row, coluna++, a.getContratoFP().getDataAdmissao());

        auxBB.getWorkBookAposentados().escreverEm(row, coluna++, getTempoContribuicaoRPPS(a.getContratoFP()));
        auxBB.getWorkBookAposentados().escreverEm(row, coluna++, getTempoContribuicaoRGPS(a.getContratoFP()));

        auxBB.getWorkBookAposentados().escreverEm(row, coluna++, a.getContratoFP().getCargo() != null && a.getContratoFP().getCargo().getCodigoCarreira() != null ? Double.parseDouble(a.getContratoFP().getCargo().getCodigoCarreira()) : "");
        auxBB.getWorkBookAposentados().escreverEm(row, coluna++, a.getContratoFP().getCargo() != null ? Double.parseDouble(a.getContratoFP().getCargo().getCodigoDoCargo()) : "");

        auxBB.getWorkBookAposentados().escreverEm(row, coluna++, a.getTipoAposentadoria().getDescricao());
        auxBB.getWorkBookAposentados().escreverEm(row, coluna++, a.getInicioVigencia());

        auxBB.getWorkBookAposentados().escreverEm(row, coluna++, Double.parseDouble("1"));
        // Para este evento deve ser passado o contrato do cidadão pois ele(evento) só é lançado para servidores ativos e segundo a Márcia seu resultado deve ser a soma dos valores no ano
        auxBB.getWorkBookAposentados().escreverEmMonetario(row, coluna++, getValorDosEventosNoAno(a.getContratoFP(), DataUtil.getAno(auxBB.getBbAtuarial().getDataReferencia()), "902").doubleValue());

        for (int i = DataUtil.getAno(auxBB.getBbAtuarial().getDataReferencia()) - 4; i <= DataUtil.getAno(auxBB.getBbAtuarial().getDataReferencia()); i++){
            auxBB.getWorkBookAposentados().escreverEmMonetario(row, coluna++, getValorVerbasFixasDezembroComEventos(a, i, "363", "369", "600").doubleValue());
        }
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public Future<AuxiliarAndamentoBBAtuarial> getConteudoArquivoDependentes(AuxiliarAndamentoBBAtuarial auxBB, List<Long> idsDependentes) {
        AsyncResult<AuxiliarAndamentoBBAtuarial> result = new AsyncResult<>(auxBB);

        for (Long id : idsDependentes) {
            if (sctx != null && sctx.wasCancelCalled()) {
                break;
            }
            DependenteVinculoFP dv = dependenteVinculoFPFacade.recuperar(id);
            HSSFRow row = auxBB.criarRowDependentes();
            gerarInformacoesDependentes(dv, auxBB, row);
            addLog(auxBB, " - <font style='color : green;'><i>GERADO COM SUCESSO - DEPENDENTE</i></font> &nbsp;&bull; REGISTRO DE : " + dv.getDependente().getDependente().getNome(), "<b>", "</b>");
        }
        return result;
    }

    private void gerarInformacoesDependentes(DependenteVinculoFP dv, AuxiliarAndamentoBBAtuarial auxBB, HSSFRow row) {
        int coluna = 0;

        List<String> matriculas = contratoFPFacade.listaMatriculasDoContratoVigentesPorPessoaFisicaNaData(dv.getDependente().getResponsavel(), auxBB.getBbAtuarial().getDataReferencia());
        auxBB.getWorkBookDependentes().escreverEm(row, coluna++, (matriculas + "").replace("[", "").replace("]", ""));
        auxBB.getWorkBookDependentes().escreverEm(row, coluna++, Double.parseDouble("0"));
        auxBB.getWorkBookDependentes().escreverEm(row, coluna++, dv.getDependente().getDependente().getNome());
        auxBB.getWorkBookDependentes().escreverEm(row, coluna++, dv.getDependente().getDependente().getCpf() != null ? dv.getDependente().getDependente().getCpf().replaceAll("\\.", "").replaceAll("-", "") : "");
        auxBB.getWorkBookDependentes().escreverEm(row, coluna++, Double.parseDouble(dv.getDependente().getDependente().getSexo().getCodigo()));
        auxBB.getWorkBookDependentes().escreverEm(row, coluna++, dv.getDependente().getDependente().getDeficienteFisico() ? Double.parseDouble("1") : Double.parseDouble("0"));
        auxBB.getWorkBookDependentes().escreverEm(row, coluna++, dv.getDependente().getGrauDeParentesco() != null ? Double.parseDouble(dv.getDependente().getGrauDeParentesco().getCodigoEsocial()) : "");
        auxBB.getWorkBookDependentes().escreverEm(row, coluna++, dv.getDependente().getDependente().getDataNascimento());
        auxBB.getWorkBookDependentes().escreverEm(row, coluna++, dv.getDependente().getDependente().getNivelEscolaridade() != null && dv.getDependente().getDependente().getNivelEscolaridade().getGrauEscolaridadeDependente() != null ? Double.parseDouble(dv.getDependente().getDependente().getNivelEscolaridade().getGrauEscolaridadeDependente().getCodigo()) : "");
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public Future<AuxiliarAndamentoBBAtuarial> getConteudoArquivoPensionistas(AuxiliarAndamentoBBAtuarial auxBB, List<Long> idsPensionistas) {
        AsyncResult<AuxiliarAndamentoBBAtuarial> result = new AsyncResult<>(auxBB);

        for (Long id : idsPensionistas) {
            if (sctx != null && sctx.wasCancelCalled()) {
                break;
            }
            Pensionista p = pensionistaFacade.recuperar(id);
            HSSFRow row = auxBB.criarRowPensionistas();
            gerarInformacoesPensionistas(p, auxBB, row);
            addLog(auxBB, " - <font style='color : green;'><i>GERADO COM SUCESSO - PENSIONISTA</i></font> &nbsp;&bull; REGISTRO DE : " + p.getMatriculaFP().getPessoa().getNome(), "<b>", "</b>");
        }
        return result;
    }

    private void gerarInformacoesPensionistas(Pensionista p, AuxiliarAndamentoBBAtuarial auxBB, HSSFRow row) {
        int coluna = 0;
        auxBB.getWorkBookPensionistas().escreverEm(row, coluna++, Double.parseDouble(p.getMatriculaFP().getMatricula()));
        auxBB.getWorkBookPensionistas().escreverEm(row, coluna++, Double.parseDouble(p.getPensao().getContratoFP().getMatriculaFP().getMatricula()));
        auxBB.getWorkBookPensionistas().escreverEm(row, coluna++, p.getMatriculaFP().getPessoa().getDataNascimento());
        auxBB.getWorkBookPensionistas().escreverEm(row, coluna++, p.getMatriculaFP().getPessoa().getSexo() != null ? Double.parseDouble(p.getMatriculaFP().getPessoa().getSexo().getCodigo()) : "");
        auxBB.getWorkBookPensionistas().escreverEm(row, coluna++, p.getGrauParentescoPensionista().getCodigo());
        auxBB.getWorkBookPensionistas().escreverEm(row, coluna++, Double.parseDouble("1"));
        auxBB.getWorkBookPensionistas().escreverEm(row, coluna++, p.getInicioVigencia());
        auxBB.getWorkBookPensionistas().escreverEm(row, coluna++, p.getTipoPensao() != null ? p.getTipoPensao().getDescricao() : "");
        auxBB.getWorkBookPensionistas().escreverEm(row, coluna++, Double.parseDouble("1"));

        for (int i = DataUtil.getAno(auxBB.getBbAtuarial().getDataReferencia()) - 4; i <= DataUtil.getAno(auxBB.getBbAtuarial().getDataReferencia()); i++){
            auxBB.getWorkBookPensionistas().escreverEmMonetario(row, coluna++, getValorVerbasFixasDezembroComEventos(p, i, "353", "354", "355", "600").doubleValue());
        }
    }

    public int getTempoServico(ContratoFP contrato) {
        int quantidadeDias = 0;
        if (contrato != null && contrato.getId() != null) {
            List<AverbacaoTempoServico> lista = averbacaoTempoServicoFacade.averbacaoDeAposentado(contrato);

            if (lista != null) {
                for (AverbacaoTempoServico obj : lista) {
                    DateTime inicio = new DateTime(obj.getInicioVigencia() != null ? obj.getInicioVigencia() : new Date());
                    DateTime fim = new DateTime(obj.getFinalVigencia() != null ? obj.getFinalVigencia() : new Date());

                    quantidadeDias += Days.daysBetween(inicio, fim).getDays();
                }
            }
        }
        return quantidadeDias;
    }

    public String getDescricaoOrgaoEntidade(ContratoFP contratoFP) {
        LotacaoFuncional lot = null;
        HierarquiaOrganizacional ho = null;

        lot = lotacaoFuncionalFacade.buscarUltimaLotacaoVigentePorVinculoFP(contratoFP);

        if (lot != null && lot.getUnidadeOrganizacional() != null) {
            ho = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(lot.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        }

        if (ho != null && ho.getId() != null) {
            return ho.getCodigo() + " - " + lot.getUnidadeOrganizacional().getDescricao();
        }

        return "Não foi encontrada a Lotação Funcional";
    }

    private VinculoDeContratoFP getTipoVinculo(ContratoFP contratoFP, Date dataReferencia) {
        ContratoVinculoDeContrato contratoVinculoDeContrato = contratoVinculoDeContratoFacade.recuperaContratoVinculoDeContratoVigente(contratoFP, dataReferencia);
        if (contratoVinculoDeContrato != null) {
            return contratoVinculoDeContrato.getVinculoDeContratoFP();
        }
        return null;
    }

    //RGPS (INSS)
    private Date getDataFimContribuicaoRGPS(VinculoFP v) {
        String sql = "select * from (select fp.efetivadaem from folhadepagamento fp"
                + " inner join fichafinanceirafp ff on ff.folhadepagamento_id = fp.id"
                + " inner join itemfichafinanceirafp iff on ff.id = iff.fichafinanceirafp_id"
                + " inner join eventofp efp on efp.id = iff.eventofp_id"
                + "      where fp.efetivadaem <= :dataPadrao"
                + "        and efp.codigo = :codEvento"
                + "        and vinculofp_id = :vinculoId"
                + "   order by fp.efetivadaem desc) where rownum = 1";
        Query q = em.createNativeQuery(sql);
        q.setParameter("codEvento", 900); //INSS
        q.setParameter("dataPadrao", DataUtil.montaData(31, 02, 2010).getTime()); // 31/03/2010, sim 31 de março, no calendar, o mês começa com 0(zero)
        q.setParameter("vinculoId", v.getId());
        try {
            return (Date) q.getSingleResult();
        } catch (NoResultException nre) {
        }
        return null;
    }

    private Date getDataInicioContribuicaoRGPS(VinculoFP v) {
        String sql = "select * from (select fp.efetivadaem from folhadepagamento fp"
                + " inner join fichafinanceirafp ff on ff.folhadepagamento_id = fp.id"
                + " inner join itemfichafinanceirafp iff on ff.id = iff.fichafinanceirafp_id"
                + " inner join eventofp efp on efp.id = iff.eventofp_id"
                + "      where fp.efetivadaem <= :dataPadrao"
                + "        and efp.codigo = :codEvento"
                + "        and vinculofp_id = :vinculoId"
                + "   order by fp.efetivadaem asc) where rownum = 1";
        Query q = em.createNativeQuery(sql);
        q.setParameter("codEvento", 900); //INSS
        q.setParameter("dataPadrao", DataUtil.montaData(31, 02, 2010).getTime()); // 31/03/2010, sim 31 de março, no calendar, o mês começa com 0(zero)
        q.setParameter("vinculoId", v.getId());
        try {
            return (Date) q.getSingleResult();
        } catch (NoResultException nre) {
        }
        return null;
    }

    private Integer getTempoContribuicaoRGPS(VinculoFP v) {
        Date inicio = getDataInicioContribuicaoRGPS(v);
        Date fim = getDataFimContribuicaoRGPS(v);

        Integer tempoEmDias = 0;
        if (inicio != null && fim != null) {
            tempoEmDias = DataUtil.diferencaDiasInteira(inicio, fim);
        }
        return tempoEmDias;
    }

    // RPPS
    private Date getDataFimContribuicaoRPPS(ContratoFP c) {
        String sql = "select * from (select fp.efetivadaem from folhadepagamento fp"
                + " inner join fichafinanceirafp ff on ff.folhadepagamento_id = fp.id"
                + " inner join itemfichafinanceirafp iff on ff.id = iff.fichafinanceirafp_id"
                + " inner join eventofp efp on efp.id = iff.eventofp_id"
                + "      where fp.efetivadaem >= :dataPadrao"
                + "        and efp.codigo = :codEvento"
                + "        and vinculofp_id = :vinculoId"
                + "   order by fp.efetivadaem desc) where rownum = 1";
        Query q = em.createNativeQuery(sql);
        q.setParameter("codEvento", 898); // RPPS
        q.setParameter("dataPadrao", DataUtil.montaData(01, 03, 2010).getTime()); // 01/04/2010, sim 01 de abril, no calendar, o mês começa com 0(zero)
        q.setParameter("vinculoId", c.getId());
        try {
            return (Date) q.getSingleResult();
        } catch (NoResultException nre) {
        }
        return null;
    }

    private Date getDataInicioContribuicaoRPPS(ContratoFP c) {
        String sql = "select * from (select fp.efetivadaem from folhadepagamento fp"
                + " inner join fichafinanceirafp ff on ff.folhadepagamento_id = fp.id"
                + " inner join itemfichafinanceirafp iff on ff.id = iff.fichafinanceirafp_id"
                + " inner join eventofp efp on efp.id = iff.eventofp_id"
                + "      where fp.efetivadaem >= :dataPadrao"
                + "        and efp.codigo = :codEvento"
                + "        and vinculofp_id = :vinculoId"
                + "   order by fp.efetivadaem asc) where rownum = 1";
        Query q = em.createNativeQuery(sql);
        q.setParameter("codEvento", 898);  // RPPS
        q.setParameter("dataPadrao", DataUtil.montaData(01, 03, 2010).getTime()); // 01/04/2010, sim 01 de abril, no calendar, o mês começa com 0(zero)
        q.setParameter("vinculoId", c.getId());
        try {
            return (Date) q.getSingleResult();
        } catch (NoResultException nre) {
        }
        return null;
    }

    private Integer getTempoContribuicaoRPPS(ContratoFP c) {
        Date inicio = getDataInicioContribuicaoRPPS(c);
        Date fim = getDataFimContribuicaoRPPS(c);

        Integer tempoEmDias = 0;
        if (inicio != null && fim != null) {
            tempoEmDias = DataUtil.diferencaDiasInteira(inicio, fim);
        }
        return tempoEmDias;
    }

    public BigDecimal getBaseDoEventoNoMes(ContratoFP c, String evento, Integer mes, Integer ano) {
        String sql = "  SELECT coalesce(sum(iff.valorbasedecalculo),0)   FROM itemfichafinanceirafp iff                          "
                + " INNER JOIN fichafinanceirafp                 ff ON ff.id = iff.fichafinanceirafp_id                          "
                + " INNER JOIN folhadepagamento                  fp ON fp.id = ff.folhadepagamento_id                            "
                + " INNER JOIN vinculofp                          v ON v.id = ff.vinculofp_id                                    "
                + " INNER JOIN eventofp                         efp ON efp.id = iff.eventofp_id                                  "
                + "      WHERE v.id          = :contratofp_id                                                                    "
                + "        AND fp.mes = :mes                   "
                + "        AND fp.ano = :ano                   "
                + "        AND efp.codigo    = :cod_evento";

        Query q = em.createNativeQuery(sql);

        q.setParameter("contratofp_id", c.getId());
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        q.setParameter("cod_evento", evento);

        try {
            BigDecimal resultado = (BigDecimal) q.getSingleResult();
            if (resultado == null) {
                return BigDecimal.ZERO;
            }
            return resultado;
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorVerbasFixasDezembro(VinculoFP v, Integer ano) {
        String sql = "  SELECT coalesce(sum(iff.valor),0)   FROM itemfichafinanceirafp iff                                       "
                + " INNER JOIN fichafinanceirafp                 ff ON ff.id = iff.fichafinanceirafp_id                          "
                + " INNER JOIN folhadepagamento                  fp ON fp.id = ff.folhadepagamento_id                            "
                + " INNER JOIN vinculofp                          v ON v.id = ff.vinculofp_id                                    "
                + " INNER JOIN eventofp                         efp ON efp.id = iff.eventofp_id                                  "
                + "      WHERE v.id   = :vinculofp_id                                                                            "
                + "        AND fp.ano = :ano                                                                                     "
                + "        AND fp.mes = :dezembro                                                                                "
                + "        AND fp.tipoFolhaDePagamento = :tipoFolhaDePagamento                                                   "
                + "        AND efp.verbafixa = :verbaFixa                                                                        ";

        Query q = em.createNativeQuery(sql);

        q.setParameter("vinculofp_id", v.getId());
        q.setParameter("ano", ano);
        q.setParameter("dezembro", 11);
        q.setParameter("tipoFolhaDePagamento", TipoFolhaDePagamento.NORMAL.name());
        q.setParameter("verbaFixa", "1");

        try {
            BigDecimal resultado = (BigDecimal) q.getSingleResult();
            if (resultado == null) {
                return BigDecimal.ZERO;
            }
            return resultado;
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorVerbasFixasDezembroComEventos(VinculoFP v, Integer ano, String... eventos) {
        String sql = "  SELECT coalesce(sum(iff.valor),0)   FROM itemfichafinanceirafp iff                                       "
                + " INNER JOIN fichafinanceirafp                 ff ON ff.id = iff.fichafinanceirafp_id                          "
                + " INNER JOIN folhadepagamento                  fp ON fp.id = ff.folhadepagamento_id                            "
                + " INNER JOIN vinculofp                          v ON v.id = ff.vinculofp_id                                    "
                + " INNER JOIN eventofp                         efp ON efp.id = iff.eventofp_id                                  "
                + "      WHERE v.id   = :vinculofp_id                                                                            "
                + "        AND fp.ano = :ano                                                                                     "
                + "        AND fp.mes = :dezembro                                                                                "
                + "        AND fp.tipoFolhaDePagamento = :tipoFolhaDePagamento                                                   "
                + "        AND (efp.codigo    in :codigos_eventos or efp.verbafixa = :verbaFixa)";

        Query q = em.createNativeQuery(sql);

        q.setParameter("vinculofp_id", v.getId());
        q.setParameter("ano", ano);
        q.setParameter("dezembro", 11);
        q.setParameter("codigos_eventos", Arrays.asList(eventos));
        q.setParameter("tipoFolhaDePagamento", TipoFolhaDePagamento.NORMAL.name());
        q.setParameter("verbaFixa", "1");

        try {
            BigDecimal resultado = (BigDecimal) q.getSingleResult();
            if (resultado == null) {
                return BigDecimal.ZERO;
            }
            return resultado;
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorDosEventosNoMes(VinculoFP v, Integer mes, Integer ano, String... eventos) {
        String sql = "  SELECT coalesce(sum(iff.valor),0)   FROM itemfichafinanceirafp iff                          "
                + " INNER JOIN fichafinanceirafp                 ff ON ff.id = iff.fichafinanceirafp_id             "
                + " INNER JOIN folhadepagamento                  fp ON fp.id = ff.folhadepagamento_id               "
                + " INNER JOIN vinculofp                          v ON v.id = ff.vinculofp_id                       "
                + " INNER JOIN eventofp                         efp ON efp.id = iff.eventofp_id                     "
                + "      WHERE v.id          = :vinculofp_id                                                        "
                + "        AND fp.mes        = :mes                   "
                + "        AND fp.ano        = :ano                   "
                + "        AND efp.codigo    in :codigos_eventos";

        Query q = em.createNativeQuery(sql);

        q.setParameter("vinculofp_id", v.getId());
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        q.setParameter("codigos_eventos", Arrays.asList(eventos));

        try {
            BigDecimal resultado = (BigDecimal) q.getSingleResult();
            if (resultado == null) {
                return BigDecimal.ZERO;
            }
            return resultado;
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorDosEventosNoAno(VinculoFP v, Integer ano, String... eventos) {
        String sql = "  SELECT coalesce(sum(iff.valor),0)   FROM itemfichafinanceirafp iff                          "
                + " INNER JOIN fichafinanceirafp                 ff ON ff.id = iff.fichafinanceirafp_id             "
                + " INNER JOIN folhadepagamento                  fp ON fp.id = ff.folhadepagamento_id               "
                + " INNER JOIN vinculofp                          v ON v.id = ff.vinculofp_id                       "
                + " INNER JOIN eventofp                         efp ON efp.id = iff.eventofp_id                     "
                + "      WHERE v.id          = :vinculofp_id                                                        "
                + "        AND fp.ano        = :ano                   "
                + "        AND efp.codigo    in :codigos_eventos";

        Query q = em.createNativeQuery(sql);

        q.setParameter("vinculofp_id", v.getId());
        q.setParameter("ano", ano);
        q.setParameter("codigos_eventos", Arrays.asList(eventos));

        try {
            BigDecimal resultado = (BigDecimal) q.getSingleResult();
            if (resultado == null) {
                return BigDecimal.ZERO;
            }
            return resultado;
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }
}
