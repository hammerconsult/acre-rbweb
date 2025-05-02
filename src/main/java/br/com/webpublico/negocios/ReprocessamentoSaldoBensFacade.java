package br.com.webpublico.negocios;

import br.com.webpublico.controle.ReprocessamentoSaldoGrupoBemGrupoMaterialControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteReprocessamento;
import br.com.webpublico.entidadesauxiliares.ReprocessamentoSaldoGrupoBens;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoHistoricoFacade;
import br.com.webpublico.seguranca.service.QueryReprocessamentoService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 24/04/15
 * Time: 08:09
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ReprocessamentoSaldoBensFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SaldoGrupoBemMovelFacade saldoGrupoBemMovelFacade;
    @EJB
    private SaldoGrupoMaterialFacade saldoGrupoMaterialFacade;
    @EJB
    private SaldoGrupoBemImovelFacade saldoGrupoBemImovelFacade;
    @EJB
    private SaldoGrupoBemIntangivelFacade saldoGrupoBemIntangivelFacade;
    @EJB
    private TransfBensMoveisFacade transfBensMoveisFacade;
    @EJB
    private GrupoBemFacade grupoBemFacade;
    @EJB
    private GrupoMaterialFacade grupoMaterialFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private ConfigAlienacaoAtivosFacade configAlienacaoAtivosFacade;
    @EJB
    private ConfigBensMoveisFacade configBensMoveisFacade;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private ReprocessamentoHistoricoFacade reprocessamentoHistoricoFacade;

    public void excluirSaldosGrupoBensMoveis(Date dataInicial, Date dataFinal, GrupoBem grupoBem) {
        saldoGrupoBemMovelFacade.excluirMovimentoPorPeriodo(dataInicial, dataFinal, grupoBem);
        saldoGrupoBemMovelFacade.excluirSaldoPorPeriodo(dataInicial, dataFinal, grupoBem);
    }

    public void excluirSaldosGrupoBensImoveis(Date dataInicial, Date dataFinal, GrupoBem grupoBem) {
        saldoGrupoBemImovelFacade.excluirMovimentoPorPeriodo(dataInicial, dataFinal, grupoBem);
        saldoGrupoBemImovelFacade.excluirSaldoPorPeriodo(dataInicial, dataFinal, grupoBem);
    }

    public void excluirSaldosGrupoBensIntangiveis(Date dataInicial, Date dataFinal, GrupoBem grupoBem) {
        saldoGrupoBemIntangivelFacade.excluirMovimentoPorPeriodo(dataInicial, dataFinal, grupoBem);
        saldoGrupoBemIntangivelFacade.excluirSaldoPorPeriodo(dataInicial, dataFinal, grupoBem);
    }

    public void excluirSaldosGrupoMaterial(Date dataInicial, Date dataFinal, GrupoMaterial grupoMaterial) {
        saldoGrupoMaterialFacade.excluirMovimentoPorPeriodo(dataInicial, dataFinal, grupoMaterial);
        saldoGrupoMaterialFacade.excluirSaldoPorPeriodo(dataInicial, dataFinal, grupoMaterial);
    }

    public void gerarSaldoBensMoveis(ReprocessamentoSaldoGrupoBens bensMoveis, AssistenteReprocessamento assistente) {
        String objetosUtilizados = "";
        try {
            objetosUtilizados = "Valor: " + Util.formatarValor(bensMoveis.getValor()) +
                "; Tipo Grupo: " + (bensMoveis.getTipoGrupo() != null ? bensMoveis.getTipoGrupo().getDescricao() : "") +
                "; Data Movimento: " + DataUtil.getDataFormatada(bensMoveis.getDataMovimento()) +
                "; Operação: " + (bensMoveis.getTipoOperacaoBensMoveis() != null ? bensMoveis.getTipoOperacaoBensMoveis().getDescricao() : "") +
                "; Tipo Lançamento: " + (bensMoveis.getTipoLancamento() != null ? bensMoveis.getTipoLancamento().getDescricao() : "") +
                "; Unidade: " + bensMoveis.getUnidadeOrganizacional().toString();
            if (!TipoOperacaoBensMoveis.AQUISICAO_BENS_MOVEIS.equals(bensMoveis.getTipoOperacaoBensMoveis())) {
                if (TipoOperacaoBensMoveis.getOperacoesParaUnicoSaldoDebito().contains(bensMoveis.getTipoOperacaoBensMoveis())) {
                    gerarSaldoBensMoveisPorTipoOperacao(bensMoveis, TipoOperacao.DEBITO, assistente);
                    assistente.adicionarHistoricoLogSemErro(bensMoveis.getGrupoBem(), objetosUtilizados + "; Tipo: " + TipoOperacao.DEBITO.getDescricao());

                } else if (TipoOperacaoBensMoveis.getOperacoesParaUnicoSaldoCredito().contains(bensMoveis.getTipoOperacaoBensMoveis())) {
                    gerarSaldoBensMoveisPorTipoOperacao(bensMoveis, TipoOperacao.CREDITO, assistente);
                    assistente.adicionarHistoricoLogSemErro(bensMoveis.getGrupoBem(), objetosUtilizados + "; Tipo: " + TipoOperacao.CREDITO.getDescricao());
                } else {
                    gerarSaldoBensMoveisPorTipoOperacao(bensMoveis, TipoOperacao.CREDITO, assistente);
                    assistente.adicionarHistoricoLogSemErro(bensMoveis.getGrupoBem(), objetosUtilizados + "; Tipo: " + TipoOperacao.CREDITO.getDescricao());
                    if (TipoOperacaoBensMoveis.ALIENACAO_BENS_MOVEIS.equals(bensMoveis.getTipoOperacaoBensMoveis())) {
                        if (!bensMoveis.isReceitaRealizada()) {
                            gerarSaldoBensMoveisPorConfiguracaoAlienacao(bensMoveis, TipoOperacao.DEBITO, assistente);
                            assistente.adicionarHistoricoLogSemErro(bensMoveis.getGrupoBem(), objetosUtilizados + "; Tipo: " + TipoOperacao.DEBITO.getDescricao());
                        }
                    } else {
                        gerarSaldoBensMoveisPorTipoOperacao(bensMoveis, TipoOperacao.DEBITO, assistente);
                        assistente.adicionarHistoricoLogSemErro(bensMoveis.getGrupoBem(), objetosUtilizados + "; Tipo: " + TipoOperacao.DEBITO.getDescricao());
                    }
                }
            } else {
                gerarSaldoBensMoveisPorTipoOperacao(bensMoveis, TipoOperacao.DEBITO, assistente);
                assistente.adicionarHistoricoLogSemErro(bensMoveis.getGrupoBem(), objetosUtilizados + "; Tipo: " + TipoOperacao.DEBITO.getDescricao());
            }
            assistente.historicoContaSemErro();
        } catch (OptimisticLockException optex) {
            throw new ExcecaoNegocioGenerica("Saldo grupo patrimonial/materiais está sendo movimentado por outro usuário. Por favor, tente salvar o movimento em alguns instantes.");
        } catch (Exception ex) {
            assistente.adicionarReprocessandoErroLog(ex.getMessage(), bensMoveis.getGrupoBem(), objetosUtilizados);
            assistente.historicoContaComErro();
        } finally {
            assistente.historicoConta();
        }
    }

    private void gerarSaldoBensMoveisPorConfiguracaoAlienacao(ReprocessamentoSaldoGrupoBens bensMoveis, TipoOperacao tipoOperacao, AssistenteReprocessamento assistente) {
        ConfigAlienacaoAtivos configAlienacaoAtivos = buscarConfigAlienacaoAtivos(bensMoveis);
        assistente.getAssistenteBarraProgresso().setMensagem("GERANDO SALDO PARA O DIA " + DataUtil.getDataFormatada(bensMoveis.getDataMovimento()));
        saldoGrupoBemMovelFacade.geraSaldoGrupoBemMoveis(
            bensMoveis.getUnidadeOrganizacional(),
            configAlienacaoAtivos.getGrupo(),
            bensMoveis.getValor(),
            configAlienacaoAtivos.getTipoGrupo(),
            bensMoveis.getDataMovimento(),
            bensMoveis.getTipoOperacaoBensMoveis(),
            bensMoveis.getTipoLancamento(),
            tipoOperacao,
            false);
    }

    private void gerarSaldoBensMoveisPorTipoOperacao(ReprocessamentoSaldoGrupoBens bensMoveis, TipoOperacao tipoOperacao, AssistenteReprocessamento assistente) {
        assistente.getAssistenteBarraProgresso().setMensagem("GERANDO SALDO PARA O DIA " + DataUtil.getDataFormatada(bensMoveis.getDataMovimento()));
        saldoGrupoBemMovelFacade.geraSaldoGrupoBemMoveis(
            bensMoveis.getUnidadeOrganizacional(),
            bensMoveis.getGrupoBem(),
            bensMoveis.getValor(),
            bensMoveis.getTipoGrupo(),
            bensMoveis.getDataMovimento(),
            bensMoveis.getTipoOperacaoBensMoveis(),
            bensMoveis.getTipoLancamento(),
            tipoOperacao,
            false);
    }

    private ConfigAlienacaoAtivos buscarConfigAlienacaoAtivos(ReprocessamentoSaldoGrupoBens bensMoveis) {
        ConfigAlienacaoAtivos configAlienacaoAtivos = null;
        if (TipoOperacaoBensMoveis.ALIENACAO_BENS_MOVEIS.equals(bensMoveis.getTipoOperacaoBensMoveis())) {
            ConfigBensMoveis configuracao = configBensMoveisFacade.buscarEventoContabilPorOperacaoLancamentoAndDataMov(bensMoveis.getTipoOperacaoBensMoveis(), bensMoveis.getTipoLancamento(), bensMoveis.getDataMovimento());
            if (configuracao == null) {
                throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Bens Móveis. ");
            }
            configAlienacaoAtivos = configAlienacaoAtivosFacade.buscarConfigVigente(configuracao.getEventoContabil(), bensMoveis.getDataMovimento());
            if (configAlienacaoAtivos == null) {
                throw new ExcecaoNegocioGenerica("Não foi encontrado uma configuração de alienação de ativos para o evento: " + configuracao.getEventoContabil().toString());
            }
        }
        return configAlienacaoAtivos;
    }

    public void gerarSaldoBensImoveis(ReprocessamentoSaldoGrupoBens bensImoveis, AssistenteReprocessamento assistente) {
        String objetosUtilizados = "";
        try {
            assistente.getAssistenteBarraProgresso().setMensagem("GERANDO SALDO PARA O DIA " + DataUtil.getDataFormatada(bensImoveis.getDataMovimento()));
            objetosUtilizados = "Valor: " + Util.formatarValor(bensImoveis.getValor()) +
                "; Tipo Grupo: " + (bensImoveis.getTipoGrupo() != null ? bensImoveis.getTipoGrupo().getDescricao() : "") +
                "; Data Movimento: " + DataUtil.getDataFormatada(bensImoveis.getDataMovimento()) +
                "; Operação: " + (bensImoveis.getTipoOperacaoBensMoveis() != null ? bensImoveis.getTipoOperacaoBensMoveis().getDescricao() : "") +
                "; Tipo Lançamento: " + (bensImoveis.getTipoLancamento() != null ? bensImoveis.getTipoLancamento().getDescricao() : "") +
                "; Tipo: " + TipoOperacao.DEBITO.getDescricao() +
                "; Unidade: " + bensImoveis.getUnidadeOrganizacional().toString();
            saldoGrupoBemImovelFacade.geraSaldoGrupoBemImoveis(
                bensImoveis.getUnidadeOrganizacional(),
                bensImoveis.getGrupoBem(),
                bensImoveis.getValor(),
                bensImoveis.getTipoGrupo(),
                bensImoveis.getDataMovimento(),
                bensImoveis.getTipoOperacaoBensImoveis(),
                bensImoveis.getTipoLancamento(),
                TipoOperacao.DEBITO,
                false);
            assistente.adicionarHistoricoLogSemErro(bensImoveis.getGrupoBem(), objetosUtilizados);

            if (!bensImoveis.getTipoOperacaoBensImoveis().equals(TipoOperacaoBensImoveis.AQUISICAO_BENS_IMOVEIS)) {
                objetosUtilizados = "";
                objetosUtilizados = "Valor: " + Util.formatarValor(bensImoveis.getValor()) +
                    "; Tipo Grupo: " + (bensImoveis.getTipoGrupo() != null ? bensImoveis.getTipoGrupo().getDescricao() : "") +
                    "; Data Movimento: " + DataUtil.getDataFormatada(bensImoveis.getDataMovimento()) +
                    "; Operação: " + (bensImoveis.getTipoOperacaoBensMoveis() != null ? bensImoveis.getTipoOperacaoBensMoveis().getDescricao() : "") +
                    "; Tipo Lançamento: " + (bensImoveis.getTipoLancamento() != null ? bensImoveis.getTipoLancamento().getDescricao() : "") +
                    "; Tipo: " + TipoOperacao.CREDITO.getDescricao() +
                    "; Unidade: " + bensImoveis.getUnidadeOrganizacional().toString();
                saldoGrupoBemImovelFacade.geraSaldoGrupoBemImoveis(
                    bensImoveis.getUnidadeOrganizacional(),
                    bensImoveis.getGrupoBem(),
                    bensImoveis.getValor(),
                    bensImoveis.getTipoGrupo(),
                    bensImoveis.getDataMovimento(),
                    bensImoveis.getTipoOperacaoBensImoveis(),
                    bensImoveis.getTipoLancamento(),
                    TipoOperacao.CREDITO,
                    false);
                assistente.adicionarHistoricoLogSemErro(bensImoveis.getGrupoBem(), objetosUtilizados);
            }
            assistente.historicoContaSemErro();
        } catch (OptimisticLockException optex) {
            throw new ExcecaoNegocioGenerica("Saldo grupo patrimonial/materiais está sendo movimentado por outro usuário. Por favor, tente salvar o movimento em alguns instantes.");
        } catch (Exception ex) {
            assistente.adicionarReprocessandoErroLog(ex.getMessage(), bensImoveis.getGrupoBem(), objetosUtilizados);
            assistente.historicoContaComErro();
        } finally {
            assistente.historicoConta();
        }
    }

    public void gerarSaldoBensIntangiveis(ReprocessamentoSaldoGrupoBens bensIntangiveis, AssistenteReprocessamento assistente) {
        String objetosUtilizados = "";
        try {
            assistente.getAssistenteBarraProgresso().setMensagem("GERANDO SALDO PARA O DIA " + DataUtil.getDataFormatada(bensIntangiveis.getDataMovimento()));
            objetosUtilizados = "Valor: " + Util.formatarValor(bensIntangiveis.getValor()) +
                "; Tipo Grupo: " + (bensIntangiveis.getTipoGrupo() != null ? bensIntangiveis.getTipoGrupo().getDescricao() : "") +
                "; Data Movimento: " + DataUtil.getDataFormatada(bensIntangiveis.getDataMovimento()) +
                "; Operação: " + (bensIntangiveis.getTipoOperacaoBensMoveis() != null ? bensIntangiveis.getTipoOperacaoBensMoveis().getDescricao() : "") +
                "; Tipo Lançamento: " + (bensIntangiveis.getTipoLancamento() != null ? bensIntangiveis.getTipoLancamento().getDescricao() : "") +
                "; Tipo: " + TipoOperacao.DEBITO.getDescricao() +
                "; Unidade: " + bensIntangiveis.getUnidadeOrganizacional().toString();
            saldoGrupoBemIntangivelFacade.geraSaldoGrupoBemIntangiveis(
                bensIntangiveis.getUnidadeOrganizacional(),
                bensIntangiveis.getGrupoBem(),
                bensIntangiveis.getValor(),
                bensIntangiveis.getTipoGrupo(),
                bensIntangiveis.getDataMovimento(),
                bensIntangiveis.getTipoOperacaoBensIntangiveis(),
                bensIntangiveis.getTipoLancamento(),
                TipoOperacao.DEBITO,
                false);
            assistente.adicionarHistoricoLogSemErro(bensIntangiveis.getGrupoBem(), objetosUtilizados);

            if (!bensIntangiveis.getTipoOperacaoBensIntangiveis().equals(TipoOperacaoBensIntangiveis.AQUISICAO_BENS_INTANGIVEIS)) {
                objetosUtilizados = "Valor: " + Util.formatarValor(bensIntangiveis.getValor()) +
                    "; Tipo Grupo: " + (bensIntangiveis.getTipoGrupo() != null ? bensIntangiveis.getTipoGrupo().getDescricao() : "") +
                    "; Data Movimento: " + DataUtil.getDataFormatada(bensIntangiveis.getDataMovimento()) +
                    "; Operação: " + (bensIntangiveis.getTipoOperacaoBensMoveis() != null ? bensIntangiveis.getTipoOperacaoBensMoveis().getDescricao() : "") +
                    "; Tipo Lançamento: " + (bensIntangiveis.getTipoLancamento() != null ? bensIntangiveis.getTipoLancamento().getDescricao() : "") +
                    "; Tipo: " + TipoOperacao.CREDITO.getDescricao() +
                    "; Unidade: " + bensIntangiveis.getUnidadeOrganizacional().toString();
                saldoGrupoBemIntangivelFacade.geraSaldoGrupoBemIntangiveis(
                    bensIntangiveis.getUnidadeOrganizacional(),
                    bensIntangiveis.getGrupoBem(),
                    bensIntangiveis.getValor(),
                    bensIntangiveis.getTipoGrupo(),
                    bensIntangiveis.getDataMovimento(),
                    bensIntangiveis.getTipoOperacaoBensIntangiveis(),
                    bensIntangiveis.getTipoLancamento(),
                    TipoOperacao.CREDITO,
                    false);
                assistente.adicionarHistoricoLogSemErro(bensIntangiveis.getGrupoBem(), objetosUtilizados);
            }
            assistente.historicoContaSemErro();
        } catch (OptimisticLockException optex) {
            throw new ExcecaoNegocioGenerica("Saldo grupo patrimonial/materiais está sendo movimentado por outro usuário. Por favor, tente salvar o movimento em alguns instantes.");
        } catch (Exception ex) {
            assistente.adicionarReprocessandoErroLog(ex.getMessage(), bensIntangiveis.getGrupoBem(), objetosUtilizados);
            assistente.historicoContaComErro();
        } finally {
            assistente.historicoConta();
        }
    }

    public void gerarSaldoBensEstoque(ReprocessamentoSaldoGrupoBens bensEstoque, AssistenteReprocessamento assistente) {
        String objetosUtilizados = "";
        try {
            assistente.getAssistenteBarraProgresso().setMensagem("GERANDO SALDO PARA O DIA " + DataUtil.getDataFormatada(bensEstoque.getDataMovimento()));
            objetosUtilizados = "Valor: " + Util.formatarValor(bensEstoque.getValor()) +
                "; Tipo Estoque: " + (bensEstoque.getTipoEstoque() != null ? bensEstoque.getTipoEstoque().getDescricao() : "") +
                "; Data Movimento: " + DataUtil.getDataFormatada(bensEstoque.getDataMovimento()) +
                "; Operação: " + (bensEstoque.getTipoOperacaoBensEstoque() != null ? bensEstoque.getTipoOperacaoBensEstoque().getDescricao() : "") +
                "; Tipo Lançamento: " + (bensEstoque.getTipoLancamento() != null ? bensEstoque.getTipoLancamento().getDescricao() : "") +
                "; Tipo: " + TipoOperacao.DEBITO.getDescricao() +
                "; Unidade: " + bensEstoque.getUnidadeOrganizacional();
            saldoGrupoMaterialFacade.geraSaldoGrupoMaterial(
                bensEstoque.getUnidadeOrganizacional(),
                bensEstoque.getGrupoMaterial(),
                bensEstoque.getValor(),
                bensEstoque.getTipoEstoque(),
                bensEstoque.getDataMovimento(),
                bensEstoque.getTipoOperacaoBensEstoque(),
                bensEstoque.getTipoLancamento(),
                TipoOperacao.DEBITO,
                bensEstoque.getIdOrigem(),
                false);
            assistente.adicionarHistoricoLogSemErro(bensEstoque.getGrupoMaterial(), objetosUtilizados);

            if (!bensEstoque.getTipoOperacaoBensEstoque().equals(TipoOperacaoBensEstoque.AQUISICAO_BENS_ESTOQUE)) {

                objetosUtilizados = "Valor: " + Util.formatarValor(bensEstoque.getValor()) +
                    "; Tipo Estoque: " + (bensEstoque.getTipoEstoque() != null ? bensEstoque.getTipoEstoque().getDescricao() : "") +
                    "; Data Movimento: " + DataUtil.getDataFormatada(bensEstoque.getDataMovimento()) +
                    "; Operação: " + (bensEstoque.getTipoOperacaoBensEstoque() != null ? bensEstoque.getTipoOperacaoBensEstoque().getDescricao() : "") +
                    "; Tipo Lançamento: " + (bensEstoque.getTipoLancamento() != null ? bensEstoque.getTipoLancamento().getDescricao() : "") +
                    "; Tipo: " + TipoOperacao.CREDITO.getDescricao() +
                    "; Unidade: " + bensEstoque.getUnidadeOrganizacional();
                saldoGrupoMaterialFacade.geraSaldoGrupoMaterial(
                    bensEstoque.getUnidadeOrganizacional(),
                    bensEstoque.getGrupoMaterial(),
                    bensEstoque.getValor(),
                    bensEstoque.getTipoEstoque(),
                    bensEstoque.getDataMovimento(),
                    bensEstoque.getTipoOperacaoBensEstoque(),
                    bensEstoque.getTipoLancamento(),
                    TipoOperacao.CREDITO,
                    bensEstoque.getIdOrigem(),
                    false);
                assistente.adicionarHistoricoLogSemErro(bensEstoque.getGrupoMaterial(), objetosUtilizados);
            }
            assistente.historicoContaSemErro();
        } catch (OptimisticLockException optex) {
            throw new ExcecaoNegocioGenerica("Saldo grupo patrimonial/materiais está sendo movimentado por outro usuário. Por favor, tente salvar o movimento em alguns instantes.");
        } catch (Exception ex) {
            assistente.adicionarReprocessandoErroLog(ex.getMessage(), bensEstoque.getGrupoMaterial(), objetosUtilizados);
            assistente.historicoContaComErro();
        } finally {
            assistente.historicoConta();
        }
    }

    public List<ReprocessamentoSaldoGrupoBens> getBensIntangiveis(Date dataInicial, Date dataFinal, GrupoBem grupoBem) {
        String sql = QueryReprocessamentoService.getService().getReprocessamentoBensIntangiveis().replace("$Filtros", (grupoBem != null ? " and b.grupobem_id = :idGrupoBem " : ""));

        Query q = em.createNativeQuery(sql);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        if (grupoBem != null) {
            q.setParameter("idGrupoBem", grupoBem.getId());
        }
        if (!q.getResultList().isEmpty()) {
            List<ReprocessamentoSaldoGrupoBens> retorno = new ArrayList<>();
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                ReprocessamentoSaldoGrupoBens item = new ReprocessamentoSaldoGrupoBens();
                item.setDataMovimento((Date) obj[0]);
                item.setGrupoBem(grupoBemFacade.recuperar(((BigDecimal) obj[1]).longValue()));
                item.setUnidadeOrganizacional(unidadeOrganizacionalFacade.getUnidadePelaSubordinada(((BigDecimal) obj[2]).longValue(), item.getDataMovimento(), item.getDataMovimento()));
                item.setTipoLancamento(TipoLancamento.valueOf((String) obj[3]));
                item.setTipoGrupo(TipoGrupo.valueOf((String) obj[4]));
                item.setTipoOperacaoBensIntangiveis(TipoOperacaoBensIntangiveis.valueOf((String) obj[5]));
                item.setValor((BigDecimal) obj[6]);

                retorno.add(item);
            }
            return retorno;
        }
        return new ArrayList<>();
    }

    public List<ReprocessamentoSaldoGrupoBens> getBensMoveis(Date dataInicial, Date dataFinal, String filtro) {
        StringBuilder sql = new StringBuilder();
        sql.append(QueryReprocessamentoService.getService().getReprocessamentoBensMoveis().replace("$Filtros", filtro));

        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        if (!q.getResultList().isEmpty()) {
            List<ReprocessamentoSaldoGrupoBens> retorno = new ArrayList<>();
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                ReprocessamentoSaldoGrupoBens item = new ReprocessamentoSaldoGrupoBens();
                item.setDataMovimento((Date) obj[0]);
                item.setGrupoBem(grupoBemFacade.recuperar(((BigDecimal) obj[1]).longValue()));
                item.setUnidadeOrganizacional(unidadeOrganizacionalFacade.getUnidadePelaSubordinada(((BigDecimal) obj[2]).longValue(), item.getDataMovimento(), item.getDataMovimento()));
                item.setTipoLancamento(TipoLancamento.valueOf((String) obj[3]));
                item.setTipoGrupo(TipoGrupo.valueOf((String) obj[4]));
                item.setTipoOperacaoBensMoveis(TipoOperacaoBensMoveis.valueOf((String) obj[5]));
                item.setValor((BigDecimal) obj[6]);
                item.setEventoContabil(obj[7] != null ? eventoContabilFacade.recuperarSemDependencias(((BigDecimal) obj[7]).longValue()) : null);
                item.setReceitaRealizada(((BigDecimal) obj[8]).compareTo(BigDecimal.ONE) == 0);
                retorno.add(item);
            }
            return retorno;
        }
        return new ArrayList<>();
    }

    public List<ReprocessamentoSaldoGrupoBens> getBensImoveis(Date dataInicial, Date dataFinal, String filtro) {
        StringBuilder sql = new StringBuilder();
        sql.append(QueryReprocessamentoService.getService().getReprocessamentoBensImoveis().replace("$Filtros", filtro));

        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        if (!q.getResultList().isEmpty()) {
            List<ReprocessamentoSaldoGrupoBens> retorno = new ArrayList<>();
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                ReprocessamentoSaldoGrupoBens item = new ReprocessamentoSaldoGrupoBens();
                item.setDataMovimento((Date) obj[0]);
                item.setGrupoBem(grupoBemFacade.recuperar(((BigDecimal) obj[1]).longValue()));
                item.setUnidadeOrganizacional(unidadeOrganizacionalFacade.getUnidadePelaSubordinada(((BigDecimal) obj[2]).longValue(), item.getDataMovimento(), item.getDataMovimento()));
                item.setTipoLancamento(TipoLancamento.valueOf((String) obj[3]));
                item.setTipoGrupo(TipoGrupo.valueOf((String) obj[4]));
                item.setTipoOperacaoBensImoveis(TipoOperacaoBensImoveis.valueOf((String) obj[5]));
                item.setValor((BigDecimal) obj[6]);
                retorno.add(item);
            }
            return retorno;
        }
        return new ArrayList<>();
    }

    public List<ReprocessamentoSaldoGrupoBens> getBensEstoque(Date dataInicial, Date dataFinal, String filtro) {
        StringBuilder sql = new StringBuilder();
        sql.append(QueryReprocessamentoService.getService().getReprocessamentoBensEstoque().replace("$Filtros", filtro));

        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        List<Object[]> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            List<ReprocessamentoSaldoGrupoBens> retorno = new ArrayList<>();
            for (Object[] obj : resultado) {
                ReprocessamentoSaldoGrupoBens item = new ReprocessamentoSaldoGrupoBens();
                item.setDataMovimento((Date) obj[0]);
                item.setGrupoMaterial(grupoMaterialFacade.recuperar(((BigDecimal) obj[1]).longValue()));
                item.setUnidadeOrganizacional(unidadeOrganizacionalFacade.getUnidadePelaSubordinada(((BigDecimal) obj[2]).longValue(), item.getDataMovimento(), item.getDataMovimento()));
                item.setTipoLancamento(TipoLancamento.valueOf((String) obj[3]));
                item.setTipoEstoque(TipoEstoque.valueOf((String) obj[4]));
                item.setTipoOperacaoBensEstoque(TipoOperacaoBensEstoque.valueOf((String) obj[5]));
                item.setValor((BigDecimal) obj[6]);
                item.setIdOrigem(((BigDecimal) obj[7]).longValue());
                retorno.add(item);
            }
            return retorno;
        }
        return new ArrayList<>();
    }

    public EntityManager getEm() {
        return em;
    }

    public SaldoGrupoBemMovelFacade getSaldoGrupoBemFacade() {
        return saldoGrupoBemMovelFacade;
    }

    public GrupoBemFacade getGrupoBemFacade() {
        return grupoBemFacade;
    }

    public GrupoMaterialFacade getGrupoMaterialFacade() {
        return grupoMaterialFacade;
    }

    public TransfBensMoveisFacade getTransfBensMoveisFacade() {
        return transfBensMoveisFacade;
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<AssistenteReprocessamento> reprocessar(AssistenteReprocessamento assistente, ReprocessamentoSaldoGrupoBemGrupoMaterialControlador.TipoSaldo tipoSaldo) {
        AsyncResult<AssistenteReprocessamento> retorno = new AsyncResult<>(assistente);
        assistente.getAssistenteBarraProgresso().inicializa();
        gerarSaldosPorTipo(tipoSaldo, assistente);
        assistente.finalizar();
        salvarHistorico(assistente.getHistorico());
        return retorno;
    }

    public void gerarSaldosPorTipo(ReprocessamentoSaldoGrupoBemGrupoMaterialControlador.TipoSaldo tipoSaldo, AssistenteReprocessamento assistente) {
        switch (tipoSaldo) {
            case BENS_MOVEIS:
                if (!QueryReprocessamentoService.getService().getReprocessamentoBensMoveis().isEmpty()) {
                    if (assistente.getExcluirSaldos()) {
                        assistente.getAssistenteBarraProgresso().setMensagem("EXCLUINDO SALDOS DE BENS MÓVEIS");
                        excluirSaldosGrupoBensMoveis(assistente.getDataInicial(), assistente.getDataFinal(), assistente.getGrupoBem());
                    }
                    assistente.getAssistenteBarraProgresso().setMensagem("BUSCANDO BENS MÓVEIS");
                    List<ReprocessamentoSaldoGrupoBens> bens = getBensMoveis(assistente.getDataInicial(), assistente.getDataFinal(), assistente.getFiltro());
                    assistente.setTotal(bens.size());
                    bens.forEach(bem -> gerarSaldoBensMoveis(bem, assistente));
                }
                break;

            case BENS_IMOVEIS:
                if (!QueryReprocessamentoService.getService().getReprocessamentoBensImoveis().isEmpty()) {
                    if (assistente.getExcluirSaldos()) {
                        assistente.getAssistenteBarraProgresso().setMensagem("EXCLUINDO SALDOS DE BENS IMÓVEIS");
                        excluirSaldosGrupoBensImoveis(assistente.getDataInicial(), assistente.getDataFinal(), assistente.getGrupoBem());
                    }
                    assistente.getAssistenteBarraProgresso().setMensagem("BUSCANDO BENS IMÓVEIS");
                    List<ReprocessamentoSaldoGrupoBens> bens = getBensImoveis(assistente.getDataInicial(), assistente.getDataFinal(), assistente.getFiltro());
                    assistente.setTotal(bens.size());
                    bens.forEach(bem -> gerarSaldoBensImoveis(bem, assistente));
                }
                break;

            case BENS_INTANGIVEIS:
                if (!QueryReprocessamentoService.getService().getReprocessamentoBensIntangiveis().isEmpty()) {
                    if (assistente.getExcluirSaldos()) {
                        assistente.getAssistenteBarraProgresso().setMensagem("EXCLUINDO SALDOS DE BENS INTANGÍVEIS");
                        excluirSaldosGrupoBensIntangiveis(assistente.getDataInicial(), assistente.getDataFinal(), assistente.getGrupoBem());
                    }
                    assistente.getAssistenteBarraProgresso().setMensagem("BUSCANDO BENS INTANGÍVEIS");
                    List<ReprocessamentoSaldoGrupoBens> bens = getBensIntangiveis(assistente.getDataInicial(), assistente.getDataFinal(), assistente.getGrupoBem());
                    assistente.setTotal(bens.size());
                    bens.forEach(bem -> gerarSaldoBensIntangiveis(bem, assistente));
                }
                break;

            case BENS_ESTOQUE:
                if (!QueryReprocessamentoService.getService().getReprocessamentoBensEstoque().isEmpty()) {
                    if (assistente.getExcluirSaldos()) {
                        assistente.getAssistenteBarraProgresso().setMensagem("EXCLUINDO SALDOS DE BENS DE ESTOQUE");
                        excluirSaldosGrupoMaterial(assistente.getDataInicial(), assistente.getDataFinal(), assistente.getGrupoMaterial());
                    }
                    assistente.getAssistenteBarraProgresso().setMensagem("BUSCANDO BENS DE ESTOQUE");
                    List<ReprocessamentoSaldoGrupoBens> bens = getBensEstoque(assistente.getDataInicial(), assistente.getDataFinal(), assistente.getFiltro());
                    assistente.setTotal(bens.size());
                    bens.forEach(bem -> gerarSaldoBensEstoque(bem, assistente));
                }
                break;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void salvarHistorico(ReprocessamentoHistorico historico) {
        reprocessamentoHistoricoFacade.salvarNovo(historico);
    }
}
