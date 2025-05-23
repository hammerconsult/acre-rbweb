package br.com.webpublico.negocios.contabil.reinf;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.contabil.reinf.*;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.SituacaoReceitaExtra;
import br.com.webpublico.esocial.enums.SituacaoESocial;
import br.com.webpublico.negocios.ConfiguracaoContabilFacade;
import br.com.webpublico.negocios.rh.esocial.ConfiguracaoEmpregadorESocialFacade;
import br.com.webpublico.reinf.eventos.TipoArquivoReinf;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.itextpdf.styledxmlparser.jsoup.helper.StringUtil;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Stateless
public class ReinfFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;
    private final static String CAMPO_DATA_DOCUMENTO_COMPROBATORIO_LIQUIDACAO = "doc.datadocto";
    private final static String CAMPO_DATA_PAGAMENTO = "pag.datapagamento";
    @EJB
    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<RegistroEventoRetencaoReinf> buscarReceitasExtrasR2020(ConfiguracaoEmpregadorESocial config, FiltroReinf filtroReinf, List<ConfiguracaoContabilContaReinf> contasExtra) {
        String sql = "select distinct pj.id,  pj.RAZAOSOCIAL as nome, pj.CNPJ " +
            " from Pessoa ob " +
            "         inner join empenho emp on ob.id = emp.FORNECEDOR_ID " +
            "         inner join VWHIERARQUIAORCAMENTARIA vw on emp.UNIDADEORGANIZACIONAL_ID = vw.SUBORDINADA_ID and trunc(emp.DATAEMPENHO) between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, trunc(emp.DATAEMPENHO) ) " +
            "         inner join entidade ent on vw.ENTIDADE_ID = ent.ID" +
            "         inner join PESSOAJURIDICA pj on ent.PESSOAJURIDICA_ID = pj.id " +
            "         inner join exercicio ex on emp.EXERCICIO_ID = ex.ID " +
            "         inner join DESDOBRAMENTOEMPENHO de on emp.ID = de.EMPENHO_ID " +
            "         inner join CONTADESPESA cd on de.CONTA_ID = cd.id " +
            "         inner join liquidacao liq on emp.id = liq.empenho_id" +
            "         inner join LIQUIDACAODOCTOFISCAL liqdoc on liq.ID = liqdoc.LIQUIDACAO_ID" +
            "         inner join DOCTOFISCALLIQUIDACAO doc on liqdoc.DOCTOFISCALLIQUIDACAO_ID = doc.ID " +
            "         inner join pagamento pag on liq.id = pag.liquidacao_id " +
            " where cd.TIPOCONTADESPESA in " + getTiposDespesa() +
            " and trunc(doc.DATADOCTO) between to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:DATAFINAL, 'dd/mm/yyyy') ";
        if (filtroReinf.getApenasNaoEnviados()) {
            sql += " and not exists(" +
                "    select * from registroreinf registro " +
                "    where registro.IDESOCIAL = concat(ob.ID, :mesAno) " +
                "    and registro.TIPOARQUIVO = :tipoArquivo " +
                (config != null ? " and registro.empregador_id = :empregador " : "") +
                "    and registro.SITUACAO = :situacao) ";
        }
        if (config != null) {
            sql += " and ob.id = :empregador ";
        }
        sql += " order by pj.RAZAOSOCIAL";
        Query q = em.createNativeQuery(sql);
        q.setParameter("DATAINICIAL", DataUtil.getDataFormatada(filtroReinf.getPrimeiroDiaDoMesAno()));
        q.setParameter("DATAFINAL", DataUtil.getDataFormatada(filtroReinf.getUltimoDiaDoMesAno()));
        if (filtroReinf.getApenasNaoEnviados()) {
            q.setParameter("mesAno", filtroReinf.getExercicio().getAno() + "" + filtroReinf.getMes().getNumeroMes());
            q.setParameter("tipoArquivo", TipoArquivoReinf.R2020.name());
            q.setParameter("situacao", SituacaoESocial.PROCESSADO_COM_SUCESSO.name());
        }
        if (config != null) {
            q.setParameter("empregador", config.getEntidade().getPessoaJuridica().getId());
        }
        List lista = q.getResultList();

        Map<Pessoa, List<RegistroEventoRetencaoReinf>> map = new HashMap<>();
        for (Object ob : lista) {
            Object[] objeto = (Object[]) ob;
            Number id = (Number) objeto[0];
            Pessoa p = em.find(Pessoa.class, id.longValue());
            RegistroEventoRetencaoReinf recuperado = buscarRegistroEvento(p, filtroReinf.getExercicio(), filtroReinf.getMes(), TipoArquivoReinf.R2020, config);
            if (recuperado == null) {
                RegistroEventoRetencaoReinf registro = new RegistroEventoRetencaoReinf();
                registro.setEmpregador(config);
                definirVariaveis(filtroReinf, registro, TipoArquivoReinf.R2020);
                registro.setPessoa(config.getEntidade().getPessoaJuridica());
                registro.setData(DataUtil.montaData(1, filtroReinf.getMes().getNumeroMesIniciandoEmZero(), filtroReinf.getExercicio().getAno()).getTime());

                if (map.containsKey(p)) {
                    map.get(p).add(registro);
                } else {
                    List<RegistroEventoRetencaoReinf> retorno = Lists.newArrayList();
                    retorno.add(registro);
                    map.put(p, retorno);
                }

            } else {
                recuperado.setMarcarPraEnviar(Boolean.FALSE);
                if (map.containsKey(p)) {
                    map.get(p).add(recuperado);
                } else {
                    List<RegistroEventoRetencaoReinf> retorno = Lists.newArrayList();
                    retorno.add(recuperado);
                    map.put(p, retorno);
                }
            }
        }
        List<RegistroEventoRetencaoReinf> listaRetorno = Lists.newArrayList();
        for (Pessoa pessoa : map.keySet()) {
            List<RegistroEventoRetencaoReinf> c = montarReceitas(config, filtroReinf, contasExtra, map.get(pessoa), TipoArquivoReinf.R2020);
            for (RegistroEventoRetencaoReinf registroEventoRetencaoReinf : c) {
                //atualiza o tomador
                registroEventoRetencaoReinf.setPessoa(pessoa);
            }
            listaRetorno.addAll(c);
        }
        return listaRetorno;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<RegistroEventoRetencaoReinf> buscarReceitasExtrasR2010(ConfiguracaoEmpregadorESocial config, FiltroReinf filtroReinf, List<ConfiguracaoContabilContaReinf> contasExtra) {
        String sql = "select distinct ob.id,  pj.RAZAOSOCIAL as nome, pj.CNPJ " +
            " from Pessoa ob " +
            "         inner join PESSOAJURIDICA pj on ob.id = pj.id " +
            "         inner join empenho emp on ob.id = emp.FORNECEDOR_ID " +
            "         inner join exercicio ex on emp.EXERCICIO_ID = ex.ID " +
            "         inner join DESDOBRAMENTOEMPENHO de on emp.ID = de.EMPENHO_ID " +
            "         inner join CONTADESPESA cd on de.CONTA_ID = cd.id " +
            "         inner join liquidacao liq on emp.id = liq.empenho_id" +
            "         inner join LIQUIDACAODOCTOFISCAL liqdoc on liq.ID = liqdoc.LIQUIDACAO_ID" +
            "         inner join DOCTOFISCALLIQUIDACAO doc on liqdoc.DOCTOFISCALLIQUIDACAO_ID = doc.ID" +
            "         inner join pagamento pag on liq.id = pag.liquidacao_id " +
            " where cd.TIPOCONTADESPESA in " + getTiposDespesa() +
            "  and trunc(doc.DATADOCTO) between to_date(:DATAINICIAL, 'dd/mm/yyyy') and to_date(:DATAFINAL, 'dd/mm/yyyy') ";
        if (filtroReinf.getApenasNaoEnviados()) {
            sql += " and not exists(" +
                "    select * from registroreinf registro " +
                "    where registro.IDESOCIAL = concat(ob.ID, :mesAno) " +
                "    and registro.TIPOARQUIVO = :tipoArquivo " +
                (config != null ? " and registro.empregador_id = :empregador " : "") +
                "    and registro.SITUACAO = :situacao) ";
        }
        if (config != null) {
            sql += " and emp.UNIDADEORGANIZACIONAL_ID in " + getComplementoQueryEmpregador();
        }
        sql += " order by pj.RAZAOSOCIAL";
        Query q = em.createNativeQuery(sql);
        q.setParameter("DATAINICIAL", DataUtil.getDataFormatada(filtroReinf.getPrimeiroDiaDoMesAno()));
        q.setParameter("DATAFINAL", DataUtil.getDataFormatada(filtroReinf.getUltimoDiaDoMesAno()));
        if (filtroReinf.getApenasNaoEnviados()) {
            q.setParameter("mesAno", filtroReinf.getExercicio().getAno() + "" + filtroReinf.getMes().getNumeroMes());
            q.setParameter("tipoArquivo", TipoArquivoReinf.R2010.name());
            q.setParameter("situacao", SituacaoESocial.PROCESSADO_COM_SUCESSO.name());
        }
        if (config != null) {
            q.setParameter("empregador", config.getId());
        }
        List lista = q.getResultList();

        List<RegistroEventoRetencaoReinf> retorno = Lists.newArrayList();
        for (Object ob : lista) {
            Object[] objeto = (Object[]) ob;
            Number id = (Number) objeto[0];
            Pessoa p = em.find(Pessoa.class, id.longValue());
            RegistroEventoRetencaoReinf recuperado = buscarRegistroEvento(p, filtroReinf.getExercicio(), filtroReinf.getMes(), TipoArquivoReinf.R2010, config);
            if (recuperado == null) {
                RegistroEventoRetencaoReinf registro = new RegistroEventoRetencaoReinf();
                registro.setEmpregador(config);
                definirVariaveis(filtroReinf, registro, TipoArquivoReinf.R2010);
                registro.setPessoa(p);
                registro.setData(DataUtil.montaData(1, filtroReinf.getMes().getNumeroMesIniciandoEmZero(), filtroReinf.getExercicio().getAno()).getTime());
                retorno.add(registro);
            } else {
                recuperado.setEventoAtual(configuracaoEmpregadorESocialFacade.getEventoPorEmpregadorTipoArquivoMesAnoAndCNPJFornecedor(
                    config.getEntidade(),
                    filtroReinf.getUtilizarVersao2_1() ? TipoArquivoReinf.R2010V2 : TipoArquivoReinf.R2010,
                    filtroReinf.getMes(),
                    filtroReinf.getExercicio().getAno(),
                    p.getCpf_Cnpj()
                ));
                recuperado.atualizarMensagemComEventoAtual();
                recuperado.setMarcarPraEnviar(Boolean.FALSE);
                retorno.add(recuperado);
            }
        }
        return montarReceitas(config, filtroReinf, contasExtra, retorno, TipoArquivoReinf.R2010);
    }

    private List<RegistroEventoRetencaoReinf> montarReceitas(ConfiguracaoEmpregadorESocial config, FiltroReinf filtroReinf, List<ConfiguracaoContabilContaReinf> contasExtra, List<RegistroEventoRetencaoReinf> lista, TipoArquivoReinf tipoArquivoReinf) {
        List<RegistroEventoRetencaoReinf> retorno = Lists.newArrayList();
        if (TipoArquivoReinf.R2010.equals(tipoArquivoReinf)) {
            List<ReceitaExtra> receitaExtras = buscarReceitas(config, filtroReinf, contasExtra, tipoArquivoReinf, CAMPO_DATA_DOCUMENTO_COMPROBATORIO_LIQUIDACAO);
            for (RegistroEventoRetencaoReinf registro : lista) {
                if (registro.getId() != null) {
                    retorno.add(registro);
                } else {
                    for (ReceitaExtra receitaExtra : receitaExtras) {
                        if (receitaExtra.getRetencaoPgto().getPagamento().getLiquidacao().getEmpenho().getFornecedor().getId().equals(registro.getPessoa().getId())) {
                            registro.getReceitas().add(new ReceitaExtraReinf(receitaExtra, registro));
                        }
                    }
                    buscarNotasPessoa(filtroReinf, registro, TipoArquivoReinf.R2010);
                    registro.calcularValores();
                    if (!registro.getReceitas().isEmpty()) {
                        retorno.add(registro);
                    }
                }
            }
        }
        if (TipoArquivoReinf.R2020.equals(tipoArquivoReinf)) {
            List<ReceitaExtra> receitaExtras = buscarReceitas(null, filtroReinf, contasExtra, tipoArquivoReinf, CAMPO_DATA_DOCUMENTO_COMPROBATORIO_LIQUIDACAO);
            for (RegistroEventoRetencaoReinf registro : lista) {
                if (registro.getId() != null) {
                    retorno.add(registro);
                } else {
                    for (ReceitaExtra receitaExtra : receitaExtras) {
                        if (receitaExtra.getRetencaoPgto().getPagamento().getLiquidacao().getEmpenho().getFornecedor().getCpf_Cnpj().equals(registro.getPessoa().getCpf_Cnpj())) {
                            registro.getReceitas().add(new ReceitaExtraReinf(receitaExtra, registro));
                        }
                    }
                    buscarNotasPessoa(filtroReinf, registro, TipoArquivoReinf.R2020);
                    registro.calcularValores();
                    if (!registro.getReceitas().isEmpty()) {
                        retorno.add(registro);
                    }
                }
            }
        }
        if (TipoArquivoReinf.R4020.equals(tipoArquivoReinf)) {
            List<ReceitaExtra> receitaExtras = buscarReceitas(config, filtroReinf, contasExtra, tipoArquivoReinf, CAMPO_DATA_PAGAMENTO);
            for (RegistroEventoRetencaoReinf registro : lista) {
                if (registro.getId() != null) {
                    retorno.add(registro);
                } else {
                    for (ReceitaExtra receitaExtra : receitaExtras) {
                        if (receitaExtra.getRetencaoPgto().getPagamento().getLiquidacao().getEmpenho().getFornecedor().getCpf_Cnpj().equals(registro.getPessoa().getCpf_Cnpj())) {
                            registro.getReceitas().add(new ReceitaExtraReinf(receitaExtra, registro));
                        }
                    }
                    buscarNotasPessoa(filtroReinf, registro, TipoArquivoReinf.R4020);
                    registro.calcularValores();
                    if (!registro.getReceitas().isEmpty()) {
                        retorno.add(registro);
                    }
                }
            }
        }
        return retorno;
    }

    private void definirVariaveis(FiltroReinf filtroReinf, RegistroEventoRetencaoReinf registro, TipoArquivoReinf tipoArquivoReinf) {
        registro.setUsuarioSistema(filtroReinf.getUsuarioSistema());
        registro.setExercicio(filtroReinf.getExercicio());
        registro.setMes(filtroReinf.getMes());
        registro.setTipoArquivo(tipoArquivoReinf);
    }


    private void buscarNotasPessoa(FiltroReinf filtroReinf, RegistroEventoRetencaoReinf registro, TipoArquivoReinf tipoArquivoReinf) {
        List<Long> documentosJaAdicionados = Lists.newArrayList();
        for (ReceitaExtraReinf receita : registro.getReceitas()) {
            for (LiquidacaoDoctoFiscal doctoFiscal : receita.getReceitaExtra().getRetencaoPgto().getPagamento().getLiquidacao().getDoctoFiscais()) {
                if (!documentosJaAdicionados.contains(doctoFiscal.getId())) {
                    Pagamento pagamento = null;
                    if (receita.getReceitaExtra() != null &&
                        receita.getReceitaExtra().getRetencaoPgto() != null &&
                        receita.getReceitaExtra().getRetencaoPgto().getPagamento() != null) {
                        pagamento = receita.getReceitaExtra().getRetencaoPgto().getPagamento();
                        if ((TipoArquivoReinf.R2010.equals(tipoArquivoReinf) || TipoArquivoReinf.R2010V2.equals(tipoArquivoReinf)) &&
                            (doctoFiscal.getValorBaseCalculo() == null || doctoFiscal.getValorBaseCalculo().compareTo(BigDecimal.ZERO) == 0)) {
                            doctoFiscal.setRetencaoPrevidenciaria(true);
                            doctoFiscal.setValorBaseCalculo(receita.getReceitaExtra().getRetencaoPgto().getPagamento().getValor());
                        }
                        if (TipoArquivoReinf.R4020.equals(tipoArquivoReinf) &&
                            (doctoFiscal.getValorBaseCalculoIrrf() == null || doctoFiscal.getValorBaseCalculoIrrf().compareTo(BigDecimal.ZERO) == 0)) {
                            doctoFiscal.setValorBaseCalculoIrrf(receita.getReceitaExtra().getRetencaoPgto().getPagamento().getValor());
                        }
                    }
                    registro.getNotas().add(new NotaReinf(doctoFiscal, registro, configuracaoContabilFacade.buscarRetencaoMaximaReinfPorContaExtraETipoArquivo(receita.getReceitaExtra().getContaExtraorcamentaria(), tipoArquivoReinf), pagamento));
                    documentosJaAdicionados.add(doctoFiscal.getId());
                }
            }
        }

        if (registro.getNotas() != null) {
            for (NotaReinf notaReinf : registro.getNotas()) {
                LiquidacaoDoctoFiscal nota = notaReinf.getNota();
                String path = "/liquidacao/";
                if (nota.getLiquidacao().isLiquidacaoRestoPagar()) {
                    path = "/liquidacao/resto-a-pagar/";
                }
                String url = "<b><a style='color: red;' href='" + FacesUtil.getRequestContextPath() + path + "editar/" + nota.getLiquidacao().getId() + "/' target='_blank'>Editar Liquidação</a></b>";
                if (StringUtil.isBlank(nota.getDoctoFiscalLiquidacao().getSerie())) {
                    registro.setMarcarPraEnviar(Boolean.FALSE);
                    registro.setMensagem(registro.getMensagem() + "<br> </br>" + url + " O Campo Série da nota deve ser preenchido na liquidação " + nota.getLiquidacao().toString() + ".");
                }
                if (StringUtil.isBlank(nota.getDoctoFiscalLiquidacao().getNumero())) {
                    registro.setMarcarPraEnviar(Boolean.FALSE);
                    registro.setMensagem(registro.getMensagem() + "<br> </br>" + url + " O Campo Número da nota deve ser preenchido na liquidação " + nota.getLiquidacao().toString() + ".");
                }

                if (TipoArquivoReinf.R2010.equals(tipoArquivoReinf) || TipoArquivoReinf.R2010V2.equals(tipoArquivoReinf)) {
                    Date dataDocto = nota.getDoctoFiscalLiquidacao().getDataDocto();
                    Integer mes = DataUtil.getMes(dataDocto);
                    Integer ano = DataUtil.getAno(dataDocto);

                    if (!filtroReinf.getMes().getNumeroMes().equals(mes)) {
                        registro.setMarcarPraEnviar(Boolean.FALSE);
                        registro.setMensagem(registro.getMensagem() + "<br></br> O Mês da data da nota é diferente de " + filtroReinf.getMes().getDescricao() + ".");
                    }
                    if (!filtroReinf.getExercicio().getAno().equals(ano)) {
                        registro.setMarcarPraEnviar(Boolean.FALSE);
                        registro.setMensagem(registro.getMensagem() + "<br></br> O Ano da data da nota é diferente de " + filtroReinf.getExercicio().getAno() + ".");
                    }
                    if (nota.getTipoServicoReinf() == null) {
                        registro.setMarcarPraEnviar(Boolean.FALSE);
                        registro.setMensagem(registro.getMensagem() + "<br> </br> " + url + " O Campo tipo de serviço da nota deve ser preenchido na liquidação " + nota.getLiquidacao().toString() + ".");
                    }
                }

                if (TipoArquivoReinf.R4020.equals(tipoArquivoReinf) && nota.getNaturezaRendimentoReinf() == null) {
                    registro.setMarcarPraEnviar(Boolean.FALSE);
                    registro.setMensagem(registro.getMensagem() + "<br> </br> " + url + " O Campo natureza de rendimentos da nota deve ser preenchido na liquidação " + nota.getLiquidacao().toString() + ".");
                }
            }
        }
    }

    private List<ReceitaExtra> buscarReceitas(ConfiguracaoEmpregadorESocial config, FiltroReinf filtroReinf, List<ConfiguracaoContabilContaReinf> contasExtra,
                                              TipoArquivoReinf tipoArquivoReinf, String campoDataReferencia) {
        String sql = "select distinct rec.* " +
            " from receitaextra rec " +
            "         inner join conta c on rec.contaextraorcamentaria_id = c.id " +
            "         inner join retencaopgto ret on rec.retencaopgto_id = ret.id " +
            "         inner join pagamento pag on ret.pagamento_id = pag.id " +
            "         inner join liquidacao liq on pag.liquidacao_id = liq.id " +
            "         inner join liquidacaodoctofiscal liqdoc on liq.id = liqdoc.liquidacao_id" +
            "         inner join doctofiscalliquidacao doc on liqdoc.doctofiscalliquidacao_id = doc.id" +
            "         inner join empenho emp on liq.empenho_id = emp.id " +
            "         inner join exercicio ex on emp.exercicio_id = ex.id " +
            "         inner join desdobramentoempenho de on emp.id = de.empenho_id " +
            "         inner join contadespesa cd on de.conta_id = cd.id " +
            "         inner join pessoa forn on emp.fornecedor_id = forn.id " +
            " where trunc(" + campoDataReferencia + ") between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') " +
            "  and cd.tipocontadespesa in " + getTiposDespesa() +
            (TipoArquivoReinf.R4020.equals(tipoArquivoReinf) ? " and (liqdoc.optantesimplesnacional is null or liqdoc.optantesimplesnacional = 0) " : "") +
            "  and rec.situacaoreceitaextra <> :situacao ";
        if (config != null) {
            sql += " and rec.unidadeorganizacional_id in " + getComplementoQueryEmpregador();
        }
        List<String> codigos = Lists.newArrayList();
        for (ConfiguracaoContabilContaReinf contaReinf : contasExtra) {
            codigos.add(contaReinf.getContaExtra().getCodigo());
        }
        sql += " and c.codigo in " + Util.montarClausulaIn(codigos);
        Query q = em.createNativeQuery(sql, ReceitaExtra.class);
        q.setParameter("DATAINICIAL", DataUtil.getDataFormatada(filtroReinf.getPrimeiroDiaDoMesAno()));
        q.setParameter("DATAFINAL", DataUtil.getDataFormatada(filtroReinf.getUltimoDiaDoMesAno()));
        q.setParameter("situacao", SituacaoReceitaExtra.ESTORNADA.name());
        if (config != null) {
            q.setParameter("empregador", config.getId());
        }
        List<ReceitaExtra> resultList = q.getResultList();
        return resultList;
    }

    private String getComplementoQueryEmpregador() {
        return "  (select orc.SUBORDINADA_ID " +
            "                                       from ITEMEMPREGADORESOCIAL item " +
            "                                                inner join HIERARQUIAORGANIZACIONAL orgaoadm " +
            "                                                           on item.UNIDADEORGANIZACIONAL_ID = orgaoadm.SUBORDINADA_ID " +
            "                                                               and " +
            "                                                              to_date(:DATAFINAL, 'dd/mm/yyyy') between orgaoadm.INICIOVIGENCIA and coalesce(orgaoadm.FIMVIGENCIA, to_date(:DATAFINAL, 'dd/mm/yyyy')) " +
            "                                                inner join HIERARQUIAORGRESP resp " +
            "                                                           on resp.HIERARQUIAORGADM_ID = orgaoadm.id " +
            "                                                               and " +
            "                                                              to_date(:DATAFINAL, 'dd/mm/yyyy') between resp.DATAINICIO and coalesce(resp.DATAFIM, to_date(:DATAFINAL, 'dd/mm/yyyy')) " +
            "                                                inner join HIERARQUIAORGANIZACIONAL orc " +
            "                                                           on resp.HIERARQUIAORGORC_ID = orc.id " +
            "                                                               and " +
            "                                                              to_date(:DATAFINAL, 'dd/mm/yyyy') between orc.INICIOVIGENCIA and coalesce(orc.FIMVIGENCIA, to_date(:DATAFINAL, 'dd/mm/yyyy')) " +
            "                                       where item.CONFIGEMPREGADORESOCIAL_ID = :empregador) ";
    }

    public void salvarRegistroEventoExclusao(RegistroEventoExclusaoReinf registro) {
        if (registro.getId() == null) {
            em.persist(registro);
        } else {
            registro = em.merge(registro);
        }
    }

    public void salvarRegistroEvento(RegistroEventoRetencaoReinf registro) {
        if (registro.getId() == null) {
            em.persist(registro);
        } else {
            registro = em.merge(registro);
        }
    }

    public void removerRegistroEvento(RegistroEventoRetencaoReinf registro) {
        RegistroEventoRetencaoReinf obj = em.find(RegistroEventoRetencaoReinf.class, registro.getId());
        if (obj != null) {
            if (obj.getEventos() != null) {
                for (EventoRegistroReinfRetencao evento : obj.getEventos()) {
                    em.remove(evento);
                }
            }
            em.remove(obj);
        }
    }

    public RegistroEventoRetencaoReinf recuperarRegistroEvento(Long id) {
        RegistroEventoRetencaoReinf registro = em.find(RegistroEventoRetencaoReinf.class, id);
        Hibernate.initialize(registro.getReceitas());
        Hibernate.initialize(registro.getNotas());
        Hibernate.initialize(registro.getPagamentos());
        Hibernate.initialize(registro.getEventos());
        return registro;
    }

    private RegistroEventoRetencaoReinf buscarRegistroEvento(Pessoa pessoa, Exercicio exercicio, Mes mes, TipoArquivoReinf tipoArquivoReinf, ConfiguracaoEmpregadorESocial config) {
        if (pessoa == null || pessoa.getId() == null) return null;
        String sql = "select distinct reg.id  " +
            "from RegistroEventoRetencaoReinf reg " +
            " where reg.pessoa_id = :idPessoa " +
            "  and reg.exercicio_id = :idExercicio" +
            "  and reg.mes = :mes" +
            "  and reg.tipoarquivo = :tipoArquivo " +
            " and reg.empregador_id = :idEmpregador";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idPessoa", pessoa.getId());
        q.setParameter("idExercicio", exercicio.getId());
        q.setParameter("idEmpregador", config.getId());
        q.setParameter("mes", mes.name());
        q.setParameter("tipoArquivo", tipoArquivoReinf.name());
        q.setMaxResults(1);
        try {
            Number id = (Number) q.getSingleResult();
            RegistroEventoRetencaoReinf singleResult = recuperarRegistroEvento(id.longValue());
            singleResult.setMensagem("Este evento já foi enviado para o EFD-REINF para essa pessoa no Mês de " + mes.getDescricao() + " de " + exercicio.getAno() + ".");
            return singleResult;
        } catch (Exception e) {
            return null;
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<RegistroEventoRetencaoReinf> buscarRegistrosEventoR4020(ConfiguracaoEmpregadorESocial config, FiltroReinf filtroReinf, List<ConfiguracaoContabilContaReinf> contasExtra) {
        String sql = "select distinct ob.id,  pj.razaosocial as nome, pj.cnpj " +
            " from pessoa ob " +
            "         inner join pessoajuridica pj on ob.id = pj.id " +
            "         inner join empenho emp on ob.id = emp.fornecedor_id " +
            "         inner join exercicio ex on emp.exercicio_id = ex.id " +
            "         inner join desdobramentoempenho de on emp.id = de.empenho_id " +
            "         inner join contadespesa cd on de.conta_id = cd.id " +
            "         inner join liquidacao liq on emp.id = liq.empenho_id " +
            "         inner join liquidacaodoctofiscal liqdoc on liq.id = liqdoc.liquidacao_id " +
            "         inner join doctofiscalliquidacao doc on liqdoc.doctofiscalliquidacao_id = doc.id " +
            "         inner join pagamento pag on liq.id = pag.liquidacao_id " +
            " where cd.tipocontadespesa in " + getTiposDespesa() +
            "  and trunc(pag.datapagamento) between to_date(:DATAINICIAL, 'dd/MM/yyyy') and to_date(:DATAFINAL, 'dd/MM/yyyy') ";
        if (filtroReinf.getApenasNaoEnviados()) {
            sql += " and not exists(" +
                "    select * from registroreinf registro " +
                "    where registro.idesocial = concat(ob.id, :mesAno) " +
                "    and registro.tipoarquivo = :tipoArquivo " +
                (config != null ? " and registro.empregador_id = :empregador " : "") +
                "    and registro.situacao = :situacao) ";
        }
        if (config != null) {
            sql += " and emp.unidadeorganizacional_id in " + getComplementoQueryEmpregador();
        }
        sql += " order by pj.razaosocial";
        Query q = em.createNativeQuery(sql);
        q.setParameter("DATAINICIAL", DataUtil.getDataFormatada(filtroReinf.getPrimeiroDiaDoMesAno()));
        q.setParameter("DATAFINAL", DataUtil.getDataFormatada(filtroReinf.getUltimoDiaDoMesAno()));
        q.setParameter("empregador", config.getId());
        if (filtroReinf.getApenasNaoEnviados()) {
            q.setParameter("mesAno", filtroReinf.getExercicio().getAno() + "" + filtroReinf.getMes().getNumeroMes());
            q.setParameter("tipoArquivo", TipoArquivoReinf.R4020.name());
            q.setParameter("situacao", SituacaoESocial.PROCESSADO_COM_SUCESSO.name());
        }
        List<Object[]> resultado = q.getResultList();

        List<RegistroEventoRetencaoReinf> retorno = Lists.newArrayList();
        for (Object[] objeto : resultado) {
            Number id = (Number) objeto[0];
            Pessoa p = em.find(Pessoa.class, id.longValue());
            RegistroEventoRetencaoReinf recuperado = buscarRegistroEvento(p, filtroReinf.getExercicio(), filtroReinf.getMes(), TipoArquivoReinf.R4020, config);
            if (recuperado == null) {
                RegistroEventoRetencaoReinf registro = new RegistroEventoRetencaoReinf();
                registro.setEmpregador(config);
                definirVariaveis(filtroReinf, registro, TipoArquivoReinf.R4020);
                registro.setPessoa(p);
                registro.setData(DataUtil.montaData(1, filtroReinf.getMes().getNumeroMesIniciandoEmZero(), filtroReinf.getExercicio().getAno()).getTime());
                retorno.add(registro);
            } else {
                recuperado.setEventoAtual(configuracaoEmpregadorESocialFacade.getEventoPorEmpregadorTipoArquivoMesAnoAndCNPJFornecedor(
                    config.getEntidade(),
                    TipoArquivoReinf.R4020,
                    filtroReinf.getMes(),
                    filtroReinf.getExercicio().getAno(),
                    p.getCpf_Cnpj()
                ));
                recuperado.atualizarMensagemComEventoAtual();
                recuperado.setMarcarPraEnviar(Boolean.FALSE);
                retorno.add(recuperado);
            }
        }
        return montarReceitas(config, filtroReinf, contasExtra, retorno, TipoArquivoReinf.R4020);
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
}
