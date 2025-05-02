package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class SolicitacaoTransfMovBCIFacade extends AbstractFacade<SolicitacaoTransfMovBCI> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private ParecerTransferenciaMovBCIFacade parecerFacade;

    public SolicitacaoTransfMovBCIFacade() {
        super(SolicitacaoTransfMovBCI.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }

    public SolicitacaoTransfMovBCI salvarSolicitacao(Operacoes operacao, SolicitacaoTransfMovBCI solicitacao, List<ArquivoTransferenciaMovBCI> removidos) {
        removerArquivos(removidos);
        if (Operacoes.NOVO.equals(operacao)) {
            solicitacao = salvarNovoRetornando(solicitacao);
        } else {
            solicitacao = salvarRetornando(solicitacao);
        }
        return solicitacao;
    }

    private void removerArquivos(List<ArquivoTransferenciaMovBCI> removidos) {
        for (ArquivoTransferenciaMovBCI removido : removidos) {
            removerArquivos(removido.getId());
        }
    }

    public SolicitacaoTransfMovBCI salvarNovoRetornando(SolicitacaoTransfMovBCI solicitacao) {
        solicitacao.setNumeroSolicitacao(singletonGeradorCodigo.getProximoCodigo(SolicitacaoTransfMovBCI.class, "numeroSolicitacao"));
        return em.merge(solicitacao);
    }

    public ArquivoTransferenciaMovBCI salvarArquivo(ArquivoTransferenciaMovBCI arquivo) {
        return em.merge(arquivo);
    }

    @Override
    public SolicitacaoTransfMovBCI recuperar(Object id) {
        SolicitacaoTransfMovBCI solicitacao = em.find(SolicitacaoTransfMovBCI.class, id);
        Hibernate.initialize(solicitacao.getArquivos());
        for (ArquivoTransferenciaMovBCI arquivo : solicitacao.getArquivos()) {
            Hibernate.initialize(arquivo.getDetentorArquivoComposicao());
        }
        return solicitacao;
    }

    public String buscarEnderecoBCI(Long idCadastro) {
        String sql = " select v.logradouro || ', ' || v.numero from cadastroimobiliario bci " +
            " inner join vwenderecobci v on bci.id = v.cadastroimobiliario_id " +
            " where bci.id = :idCadastro ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCadastro", idCadastro);

        List<String> enderecos = q.getResultList();
        return (enderecos != null && !enderecos.isEmpty()) ? enderecos.get(0) : null;
    }

    public String buscarProprietarioBCI(Long idCadastro, Date dataOperacao) {
        String sql = " select coalesce(pf.cpf, pj.cnpj), coalesce(pf.nome, pj.razaosocial) " +
            " from cadastroimobiliario bci " +
            " inner join propriedade prop on bci.id = prop.imovel_id" +
            " inner join pessoa pes on prop.pessoa_id = pes.id " +
            " left join pessoafisica pf on pes.id = pf.id " +
            " left join pessoajuridica pj on pes.id = pj.id " +
            " where to_date(:dataOperacao, 'dd/MM/yyyy') between  prop.iniciovigencia " +
            " and coalesce(prop.finalvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            " and bci.id = :idCadastro ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCadastro", idCadastro);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));

        List<Object[]> resultado = q.getResultList();

        String proprietario = null;
        if (resultado != null && !resultado.isEmpty()) {
            Object[] obj = resultado.get(0);
            if (obj[0] != null) {
                proprietario = (String) obj[0];
                proprietario += obj[1] != null ? " - " : "";
            }
            if (obj[1] != null) {
                proprietario = proprietario != null ? proprietario + obj[1] : (String) obj[1];
            }
        }
        return proprietario;
    }

    public VOSimulacaoTransferenciaMovimentacoesBCI buscarDados(SolicitacaoTransfMovBCI selecionado) {
        VOSimulacaoTransferenciaMovimentacoesBCI simulacao = new VOSimulacaoTransferenciaMovimentacoesBCI();
        simulacao.setInscricaoOrigem(selecionado.getCadastroOrigem().getInscricaoCadastral());
        simulacao.setInscricaoDestino(selecionado.getCadastroDestino() != null ? selecionado.getCadastroDestino().getInscricaoCadastral() : "");

        Long idCadastro = selecionado.getCadastroOrigem().getId();
        List<VOCalculosSimulacaoTransferenciaBCI> processosDividaAtiva = buscarCalculosDividaAtiva(idCadastro, selecionado.getTransferirDividasAtivas());
        adicionarCalculos(simulacao.getCalculos(), processosDividaAtiva);
        List<VOCalculosSimulacaoTransferenciaBCI> calculosDividaAtiva = buscarItensInscricaoDividaAtiva(idCadastro, selecionado.getTransferirDividasAtivas());
        adicionarCalculos(simulacao.getCalculos(), calculosDividaAtiva);

        simulacao.getCertidoesDividaAtiva().addAll(buscarCdas(idCadastro, selecionado.getTransferirDividasAtivas()));

        List<VOCalculosSimulacaoTransferenciaBCI> processosIPTU = buscarCalculosCalculoIPTU(idCadastro, selecionado.getTransferirCalculosRevisaoIPTU());
        adicionarCalculos(simulacao.getCalculos(), processosIPTU);
        List<VOCalculosSimulacaoTransferenciaBCI> calculosIPTU = buscarCalculosIPTU(idCadastro, selecionado.getTransferirCalculosRevisaoIPTU());
        adicionarCalculos(simulacao.getCalculos(), calculosIPTU);

        simulacao.getRevisoesDeCalculoIPTU().addAll(buscarRevisoesCalculoIPTU(idCadastro, selecionado.getTransferirCalculosRevisaoIPTU()));
        simulacao.getIsencoesBci().addAll(buscarIsencoesBci(idCadastro, selecionado.getTransferirIsencoesIPTU()));

        List<VOCalculosSimulacaoTransferenciaBCI> processosParcelamento = buscarProcessosParcelamento(idCadastro, selecionado.getTransferirParcelamentos());
        adicionarCalculos(simulacao.getCalculos(), processosParcelamento);
        List<VOCalculosSimulacaoTransferenciaBCI> calculosParcelamentos = buscarCalculosProcessoParcelamento(idCadastro, selecionado.getTransferirParcelamentos());
        adicionarCalculos(simulacao.getCalculos(), calculosParcelamentos);

        List<VOCalculosSimulacaoTransferenciaBCI> processosITBI = buscarCalculosCalculoITBI(idCadastro, selecionado.getTransferirLancamentosITBI());
        adicionarCalculos(simulacao.getCalculos(), processosITBI);
        List<VOCalculosSimulacaoTransferenciaBCI> calculosITBI = buscarCalculosITBI(idCadastro, selecionado.getTransferirLancamentosITBI());
        adicionarCalculos(simulacao.getCalculos(), calculosITBI);

        if(selecionado.getTransferirDebitosDams()) {
            simulacao.getDans().addAll(buscarDans(idCadastro));
        }

        simulacao.getSolicitacoesDoctoOficial().addAll(buscarSolicitacoesDoctoOficial(idCadastro, selecionado.getTransferirCertidoes()));

        simulacao.getProcessosDebito().addAll(buscarProcessosDebito(idCadastro, selecionado.getTransferirProcessosDebitos()));

        return simulacao;
    }

    private void adicionarCalculos(List<VOCalculosSimulacaoTransferenciaBCI> calculos, List<VOCalculosSimulacaoTransferenciaBCI> calculosNovos) {
        for (VOCalculosSimulacaoTransferenciaBCI novo : calculosNovos) {
            if (!calculos.contains(novo)) {
                calculos.add(novo);
            }
        }
    }

    private List<VOCalculosSimulacaoTransferenciaBCI> buscarCalculosDividaAtiva(Long idCadastro, boolean canRealizarConsulta) {
        String condicao = " and exists(select item.id from iteminscricaodividaativa item where item.id = vw.calculo_id) ";
        String sql = montarSqlCalculos("", condicao);
        return preencherCalculos(idCadastro, sql, TipoSimulacaoMovBCI.DIVIDAS_ATIVAS, canRealizarConsulta);
    }

    private List<VOCalculosSimulacaoTransferenciaBCI> buscarItensInscricaoDividaAtiva(Long idCadastro, boolean canRealizarConsulta) {
        String join = " inner join iteminscricaodividaativa item on item.id = vw.calculo_id";
        String sql = montarSqlCalculos(join, "");
        return preencherCalculos(idCadastro, sql, TipoSimulacaoMovBCI.DIVIDAS_ATIVAS, canRealizarConsulta);
    }

    private List<VOCalculosSimulacaoTransferenciaBCI> buscarCalculosCalculoIPTU(Long idCadastro, boolean canRealizarConsulta) {
        String condicao = " and exists(select iptu.id from calculoiptu iptu where iptu.id = vw.calculo_id) ";
        String sql = montarSqlCalculos("", condicao);
        return preencherCalculos(idCadastro, sql, TipoSimulacaoMovBCI.CALCULOS_REVISOES_IPTU, canRealizarConsulta);
    }

    private List<VOCalculosSimulacaoTransferenciaBCI> buscarCalculosIPTU(Long idCadastro, boolean canRealizarConsulta) {
        String join = " inner join calculoiptu iptu on iptu.cadastroimobiliario_id = vw.cadastro_id and iptu.id = vw.calculo_id ";
        String sql = montarSqlCalculos(join, "");
        return preencherCalculos(idCadastro, sql, TipoSimulacaoMovBCI.CALCULOS_REVISOES_IPTU, canRealizarConsulta);
    }

    private List<VOCalculosSimulacaoTransferenciaBCI> buscarProcessosParcelamento(Long idCadastro, boolean canRealizarConsulta) {
        String join = " inner join processoparcelamento proc on proc.id = vw.calculo_id ";
        String sql = montarSqlCalculos(join, "");
        return preencherCalculos(idCadastro, sql, TipoSimulacaoMovBCI.PARCELAMENTOS, canRealizarConsulta);
    }

    private List<VOCalculosSimulacaoTransferenciaBCI> buscarCalculosProcessoParcelamento(Long idCadastro, boolean canRealizarConsulta) {
        String condicao = " and exists(select proc.id from processoparcelamento proc where proc.id = vw.calculo_id) ";
        String sql = montarSqlCalculos("", condicao);
        return preencherCalculos(idCadastro, sql, TipoSimulacaoMovBCI.PARCELAMENTOS, canRealizarConsulta);
    }

    private List<VOCalculosSimulacaoTransferenciaBCI> buscarCalculosITBI(Long idCadastro, boolean canRealizarConsulta) {
        String join = " inner join calculoitbi itbi on itbi.id = vw.calculo_id ";
        String sql = montarSqlCalculos(join, "", true);
        return preencherCalculos(idCadastro, sql, TipoSimulacaoMovBCI.LANCAMENTOS_ITBI, canRealizarConsulta);
    }

    private List<VOCalculosSimulacaoTransferenciaBCI> buscarCalculosCalculoITBI(Long idCadastro, boolean canRealizarConsulta) {
        String condicao = " and exists(select itbi.id from calculoitbi itbi where itbi.id = vw.calculo_id) ";
        String sql = montarSqlCalculos("", condicao);
        return preencherCalculos(idCadastro, sql, TipoSimulacaoMovBCI.LANCAMENTOS_ITBI, canRealizarConsulta);
    }

    private List<VOCdaSimulacaoTransferenciaBCI> buscarCdas(Long idCadastro, boolean canRealizarConsulta) {
        List<VOCdaSimulacaoTransferenciaBCI> cdas = Lists.newArrayList();
        if (canRealizarConsulta) {
            String sql = " select certidao.id, certidao.numero, ex.ano,                  " +
                "                 certidao.situacaocertidaoda, certidao.situacaojudicial " +
                " from certidaodividaativa certidao                        " +
                " inner join exercicio ex on certidao.exercicio_id = ex.id " +
                " where certidao.cadastro_id = :idCadastro " +
                " order by ex.ano, certidao.numero, certidao.situacaocertidaoda, " +
                " certidao.situacaojudicial ";

            Query q = em.createNativeQuery(sql);
            q.setParameter("idCadastro", idCadastro);

            List<Object[]> retorno = Lists.newArrayList();


            for (Object[] obj : retorno) {
                VOCdaSimulacaoTransferenciaBCI voCda = new VOCdaSimulacaoTransferenciaBCI();
                voCda.setId(((BigDecimal) obj[0]).longValue());
                voCda.setNumero(((BigDecimal) obj[1]).longValue());
                voCda.setExercicio((Integer) obj[2]);
                voCda.setSituacaoParcela(SituacaoCertidaoDA.valueOf((String) obj[3]).getDescricao());
                voCda.setSituacaoJudicial(SituacaoJudicial.valueOf((String) obj[4]).getDescricao());
                voCda.setAgrupador(TipoSimulacaoMovBCI.DIVIDAS_ATIVAS.getDescricao());

                cdas.add(voCda);
            }
        }
        return cdas;
    }

    private List<VORevisaoCalculoSimulacaoTransfMovBCI> buscarRevisoesCalculoIPTU(Long idCadastro, boolean canRealizarConsulta) {
        List<VORevisaoCalculoSimulacaoTransfMovBCI> revisoes = Lists.newArrayList();
        if (canRealizarConsulta) {
            String sql = " select revisao.id, revisao.datacriacao, " +
                "                 revisao.codigo, revisao.processo " +
                " from revisaocalculoiptu revisao " +
                " where revisao.cadastro_id = :idCadastro " +
                " order by revisao.datacriacao, revisao.codigo, " +
                " revisao.processo ";

            Query q = em.createNativeQuery(sql);
            q.setParameter("idCadastro", idCadastro);

            List<Object[]> retorno = q.getResultList();


            for (Object[] obj : retorno) {
                VORevisaoCalculoSimulacaoTransfMovBCI revisao = new VORevisaoCalculoSimulacaoTransfMovBCI();
                revisao.setId(((BigDecimal) obj[0]).longValue());
                revisao.setCodigo(((BigDecimal) obj[1]).longValue());
                revisao.setDataCriacao(DataUtil.getDataFormatada((Date) obj[2]));
                revisao.setProcesso((String) obj[3]);
                revisao.setAgrupador(TipoSimulacaoMovBCI.CALCULOS_REVISOES_IPTU.getDescricao());

                revisoes.add(revisao);
            }

        }
        return revisoes;
    }

    private List<VOIsencaoBci> buscarIsencoesBci(Long idCadastro, boolean canRealizarConsulta) {
        List<VOIsencaoBci> isencoes = Lists.newArrayList();
        if (canRealizarConsulta) {
            String sql = " select isencao.id as id, isencao.iniciovigencia as inicioVigencia, isencao.finalvigencia as fimVigencia, " +
                "                 isencao.tipolancamentoisencao as tipoLancamento, isencao.situacao as situacao" +
                " from isencaocadastroimobiliario isencao " +
                " where isencao.cadastroimobiliario_id = :idCadastro " +
                " order by isencao.iniciovigencia, isencao.situacao, " +
                " isencao.tipolancamentoisencao ";

            Query q = em.createNativeQuery(sql);
            q.setParameter("idCadastro", idCadastro);

            List<Object[]> retorno = q.getResultList();

            for (Object[] obj : retorno) {
                VOIsencaoBci isencao = new VOIsencaoBci();
                isencao.setId(((BigDecimal) obj[0]).longValue());
                isencao.setInicioVigencia((DataUtil.getDataFormatada((Date) obj[1])));
                isencao.setFimVigencia((DataUtil.getDataFormatada((Date) obj[2])));
                isencao.setTipoLancamento(TipoLancamentoIsencaoIPTU.valueOf((String) obj[3]).getDescricao());
                isencao.setSituacao(IsencaoCadastroImobiliario.Situacao.valueOf((String) obj[4]).getDescricao());
                isencao.setAgrupador(TipoSimulacaoMovBCI.ISENCOES_IPTU.getDescricao());

                isencoes.add(isencao);
            }
        }
        return isencoes;
    }

    public List<VOSolicitacaoDoctoOficial> buscarSolicitacoesDoctoOficial(Long idCadastro, boolean canRealizarConsulta) {
        List<VOSolicitacaoDoctoOficial> solicitacoes = Lists.newArrayList();
        if (canRealizarConsulta) {
            String sql = " select solicitacao.id, solicitacao.codigo as codigosolicitacao, solicitacao.datasolicitacao, " +
                "                 tipo.codigo as codigotipo, tipo.descricao " +
                " from solicitacaodoctooficial solicitacao    " +
                " inner join tipodoctooficial tipo on solicitacao.tipodoctooficial_id = tipo.id " +
                " where solicitacao.cadastroimobiliario_id = :idCadastro " +
                " order by solicitacao.datasolicitacao, codigosolicitacao, codigotipo ";

            Query q = em.createNativeQuery(sql);
            q.setParameter("idCadastro", idCadastro);

            List<Object[]> retorno = q.getResultList();


            for (Object[] obj : retorno) {
                VOSolicitacaoDoctoOficial voSolicitacao = new VOSolicitacaoDoctoOficial();
                voSolicitacao.setId(((BigDecimal) obj[0]).longValue());
                voSolicitacao.setCodigoSolicitacao(((BigDecimal) obj[1]).longValue());
                voSolicitacao.setDataSolicitacao((Date) obj[2]);
                voSolicitacao.setCodigoTipoDocto(((BigDecimal) obj[3]).longValue());
                voSolicitacao.setDescricaoTipoDocto((String) obj[4]);
                voSolicitacao.setAgrupador(TipoSimulacaoMovBCI.CERTIDOES.getDescricao());

                solicitacoes.add(voSolicitacao);
            }
        }
        return solicitacoes;
    }

    private List<Object[]> buscarDadosProcessoDebitosComCadastro(Long idCadastro) {
        String sql = " select proc.id, proc.codigo, proc.tipo, to_char(proc.motivo), proc.situacao, " +
            "                 ex.ano, proc.valorpago, proc.lancamento, proc.datapagamento " +
            " from processodebito proc " +
            " inner join exercicio ex on proc.exercicio_id = ex.id " +
            " where proc.cadastro_id = :idCadastro " +
            " order by proc.tipo, proc.lancamento, proc.codigo ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCadastro", idCadastro);
        return q.getResultList();
    }

    private List<Object[]> buscarDadosProcessoDebitosSemCadastro(Long idCadastro) {
        String sql = "select distinct proc.id, proc.codigo, proc.tipo, to_char(proc.motivo), proc.situacao, " +
            "       ex.ano, proc.valorpago, proc.lancamento, proc.datapagamento " +
            "from processodebito proc " +
            "inner join exercicio ex on proc.exercicio_id = ex.id " +
            "inner join itemprocessodebito ipd on ipd.processodebito_id = proc.id " +
            "inner join parcelavalordivida pvd on pvd.id = ipd.parcela_id " +
            "inner join valordivida vd on vd.id = pvd.valordivida_id " +
            "inner join calculo cal on cal.id = vd.calculo_id " +
            "where proc.CADASTRO_ID is null " +
            "  and cal.CADASTRO_ID = :idCadastro" +
            "  order by proc.tipo, proc.lancamento, proc.codigo ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCadastro", idCadastro);
        return q.getResultList();
    }

    private List<Object[]> buscarDadosAlteracaoVencimentoParcela(Long idCadastro) {
        String sql = "select distinct proc.id, proc.codigo, '" + TipoProcessoDebito.ALTERACAO_VENCIMENTO.name() + "', " +
            " to_char(proc.motivo), proc.situacao, ex.ano, 0 as valor, proc.realizadoEm, null " +
            "from PROCALTERACAOVENCPARCELA proc " +
            "inner join exercicio ex on proc.exercicio_id = ex.id " +
            "inner join ITEMPROCALTERACAOVENCPARC ipd on ipd.procAlteracaoVencimentoParc_id = proc.id " +
            "inner join parcelavalordivida pvd on pvd.id = ipd.parcelaValorDivida_id " +
            "inner join valordivida vd on vd.id = pvd.valordivida_id " +
            "inner join calculo cal on cal.id = vd.calculo_id " +
            "where cal.CADASTRO_ID = :idCadastro" +
            "  order by proc.realizadoEm, proc.codigo ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCadastro", idCadastro);
        return q.getResultList();
    }

    private List<Object[]> buscarDadosDeducaoAcrescimo(Long idCadastro) {
        String sql = "select distinct proc.id, proc.codigo, '" + TipoProcessoDebito.DEDUCAO_ACRESCIMO.name() + "' as tipo, " +
            " to_char(proc.motivo), proc.situacao, ex.ano, 0 as valor, proc.datalancamento, null " +
            "from ProcessoIsencaoAcrescimos proc " +
            "inner join exercicio ex on proc.exercicio_id = ex.id " +
            "where proc.CADASTRO_ID = :idCadastro" +
            "  order by proc.datalancamento, proc.codigo, proc.situacao ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCadastro", idCadastro);
        return q.getResultList();
    }

    private List<VOProcessoDebito> buscarProcessosDebito(Long idCadastro, boolean canRealizarConsulta) {
        List<VOProcessoDebito> processos = Lists.newArrayList();
        if (canRealizarConsulta) {
            List<Object[]> retorno = buscarDadosProcessoDebitosComCadastro(idCadastro);
            retorno.addAll(buscarDadosProcessoDebitosSemCadastro(idCadastro));
            retorno.addAll(buscarDadosDeducaoAcrescimo(idCadastro));
            retorno.addAll(buscarDadosAlteracaoVencimentoParcela(idCadastro));
            for (Object[] obj : retorno) {
                VOProcessoDebito voProcesso = new VOProcessoDebito();
                voProcesso.setId(((BigDecimal) obj[0]).longValue());
                voProcesso.setCodigo(((BigDecimal) obj[1]).longValue());
                voProcesso.setTipo(TipoProcessoDebito.valueOf((String) obj[2]).getDescricao());
                voProcesso.setMotivo((String) obj[3]);
                if (obj[4] != null && ProcessoIsencaoAcrescimos.Situacao.ATIVO.name().equals((String) obj[4])) {
                    voProcesso.setSituacao(SituacaoProcessoDebito.EM_ABERTO.getDescricao());
                } else if (obj[4] != null && ProcessoIsencaoAcrescimos.Situacao.EFETIVADO.name().equals((String) obj[4])) {
                    voProcesso.setSituacao(SituacaoProcessoDebito.FINALIZADO.getDescricao());
                } else {
                    voProcesso.setSituacao(SituacaoProcessoDebito.valueOf((String) obj[4]).getDescricao());
                }
                voProcesso.setAno(obj[5] != null ? ((BigDecimal) obj[5]).intValue() : null);
                voProcesso.setValorPago(obj[6] != null ? (BigDecimal) obj[6] : BigDecimal.ZERO);
                voProcesso.setLancamento(DataUtil.getDataFormatada(obj[7] != null ? (Date) obj[7] : null));
                voProcesso.setDataPagamento(DataUtil.getDataFormatada(obj[8] != null ? (Date) obj[8] : null));
                voProcesso.setAgrupador(TipoSimulacaoMovBCI.PROCESSOS_DEBITO.getDescricao());

                processos.add(voProcesso);
            }
        }
        return processos;
    }

    public List<VODamTransferenciaBCI> buscarDans(Long idCadastro) {
        String sql = " select dam.numerodam, ex.ano, dam.emissao, dam.vencimento, dam.tipo, dam.valororiginal, dam.desconto, dam.multa, dam.juros, " +
            "                 dam.correcaomonetaria, dam.honorarios, dam.situacao from dam " +
            " inner join exercicio ex on dam.exercicio_id = ex.id " +
            " inner join itemdam item on dam.id = item.dam_id " +
            " inner join parcelavalordivida pvd on item.parcela_id = pvd.id " +
            " inner join valordivida vd on pvd.valordivida_id = vd.id " +
            " inner join calculo calc on vd.calculo_id = calc.id " +
            " inner join cadastro c on calc.cadastro_id = c.id " +
            " inner join cadastroimobiliario ci on c.id = ci.id " +
            " where ci.id = :idCadastro " +
            " order by 2, dam.numero ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idCadastro", idCadastro);

        List<Object[]> resultado = q.getResultList();
        List<VODamTransferenciaBCI> dans = Lists.newArrayList();

        if(resultado != null && !resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                VODamTransferenciaBCI dam = new VODamTransferenciaBCI();
                dam.setNumeroDam((String) obj[0]);
                dam.setExercicio(((BigDecimal) obj[1]).intValue());
                dam.setDataEmissao((Date) obj[2]);
                dam.setDataVencimento((Date) obj[3]);
                dam.setTipoDam(obj[4] != null ? DAM.Tipo.valueOf((String) obj[4]).getDescricao() : "");
                dam.setValorOriginal(obj[5] != null ? (BigDecimal) obj[5] : BigDecimal.ZERO);
                dam.setDesconto(obj[6] != null ? (BigDecimal) obj[6] : BigDecimal.ZERO);
                dam.setMulta(obj[7] != null ? (BigDecimal) obj[7] : BigDecimal.ZERO);
                dam.setJuros(obj[8] != null ? (BigDecimal) obj[8] : BigDecimal.ZERO);
                dam.setCorrecao(obj[9] != null ? (BigDecimal) obj[9] : BigDecimal.ZERO);
                dam.setHonorarios(obj[10] != null ? (BigDecimal) obj[10] : BigDecimal.ZERO);
                dam.setSituacao(obj[11] != null ? DAM.Situacao.valueOf((String) obj[11]).getDescricao() : "");
                dam.setTotal(dam.getValorOriginal().subtract(dam.getDesconto()));
                dam.setAgrupador(TipoSimulacaoMovBCI.DEBITOS_DAMS.getDescricao());
                dans.add(dam);
            }
        }
        return  dans;
    }

    private String montarSqlCalculos(String join, String condicao) {
        return montarSqlCalculos(join, condicao, false);
    }

    private String montarSqlCalculos(String join, String condicao, boolean isItbi) {
        return " select vw.parcela_id, vw.divida, vw.situacaoparcela, vw.exercicio,    " +
            "           vw.valororiginal, vw.processocalculo, vw.tipocalculo " +
            " from vwconsultadedebitossemvalores vw                                    " +
            join +
            (isItbi ? "where itbi.cadastroimobiliario_id " : " where vw.cadastro_id ") + " = :idCadastro " +
            condicao +
            " order by vw.divida, vw.exercicio, vw.situacaoparcela, vw.tipocalculo ";
    }

    private List<VOCalculosSimulacaoTransferenciaBCI> preencherCalculos(Long idCadastro, String sql, TipoSimulacaoMovBCI tipo, boolean canRealizarConsulta) {
        List<VOCalculosSimulacaoTransferenciaBCI> calculos = Lists.newArrayList();
        if (canRealizarConsulta) {
            Query q = em.createNativeQuery(sql);
            q.setParameter("idCadastro", idCadastro);

            List<Object[]> retorno = q.getResultList();

            for (Object[] obj : retorno) {
                VOCalculosSimulacaoTransferenciaBCI voCalculo = new VOCalculosSimulacaoTransferenciaBCI();
                voCalculo.setId(((BigDecimal) obj[0]).longValue());
                voCalculo.setDivida((String) obj[1]);
                voCalculo.setSituacaoParcela(SituacaoParcela.valueOf((String) obj[2]).getDescricao());
                voCalculo.setExercicio(((BigDecimal) obj[3]).intValue());
                voCalculo.setValorOriginal((BigDecimal) obj[4]);
                voCalculo.setProcessoCalculo((String) obj[5]);
                voCalculo.setTipoCalculo(Calculo.TipoCalculo.valueOf((String) obj[6]).getDescricao());
                voCalculo.setAgrupador(tipo.getDescricao());

                calculos.add(voCalculo);
            }
        }
        return calculos;
    }

    public void salvarSimulacao(byte[] bytes, SolicitacaoTransfMovBCI solicitacao, List<ArquivoTransferenciaMovBCI> arquivos) throws Exception {
        solicitacao = salvarArquivos(solicitacao, arquivos);
        removerArquivoSolicitacao(solicitacao);

        ArquivoTransferenciaMovBCI arquivoTransferencia = criarArquivoTransferencia(solicitacao);

        Arquivo novoArquivo = criarArquivo(arquivoTransferencia.getTipoArquivo().getNomeArquivo() + ".pdf");
        ArquivoComposicao arquivoComposicao = criarArquivoComposicao(bytes, arquivoTransferencia.getDetentorArquivoComposicao(), novoArquivo);
        arquivoTransferencia.getDetentorArquivoComposicao().setArquivoComposicao(arquivoComposicao);

        arquivoTransferencia = salvarArquivo(arquivoTransferencia);

        solicitacao.getArquivos().add(arquivoTransferencia);

        solicitacao = em.merge(solicitacao);
        removerAndAdicionarArquivoAoParecer(solicitacao);
    }

    private SolicitacaoTransfMovBCI salvarArquivos(SolicitacaoTransfMovBCI solicitacao, List<ArquivoTransferenciaMovBCI> arquivos) {
        for (ArquivoTransferenciaMovBCI arquivo : arquivos) {
            arquivo.setSolicitacao(solicitacao);
            arquivo = salvarArquivo(arquivo);
            solicitacao.getArquivos().add(arquivo);
        }
        return em.merge(solicitacao);
    }

    public ArquivoTransferenciaMovBCI criarArquivoTransferencia(Object obj) {
        ArquivoTransferenciaMovBCI arquivoTransferencia = new ArquivoTransferenciaMovBCI();
        if (obj instanceof SolicitacaoTransfMovBCI) {
            arquivoTransferencia.setSolicitacao((SolicitacaoTransfMovBCI) obj);
        } else {
            arquivoTransferencia.setParecer((ParecerTransferenciaMovBCI) obj);
        }
        arquivoTransferencia.setTipoArquivo(TipoArquivoTransferenciaMovBCI.SIMULACAO);
        return arquivoTransferencia;
    }

    private void removerAndAdicionarArquivoAoParecer(SolicitacaoTransfMovBCI solicitacao) throws Exception {
        if (solicitacao.getId() != null) {
            ParecerTransferenciaMovBCI parecer = buscarParecerTransferencia(solicitacao.getId());
            if (parecer != null) {
                parecerFacade.salvaParecer(parecer);
            }
        }
    }

    private void removerArquivoSolicitacao(SolicitacaoTransfMovBCI solicitacao) {
        ArquivoTransferenciaMovBCI arq = null;
        for (ArquivoTransferenciaMovBCI arquivo : solicitacao.getArquivos()) {
            if(TipoArquivoTransferenciaMovBCI.SIMULACAO.equals(arquivo.getTipoArquivo())) {
                arq = arquivo;
            }
        }
        if(arq != null) {
            solicitacao.getArquivos().remove(arq);
            removerArquivos(arq.getId());
        }
    }

    public void removerArquivoParecer(ParecerTransferenciaMovBCI parecer) {
        ArquivoTransferenciaMovBCI arq = null;
        for (ArquivoTransferenciaMovBCI arquivo : parecer.getArquivos()) {
            if(TipoArquivoTransferenciaMovBCI.SIMULACAO.equals(arquivo.getTipoArquivo())) {
                arq = arquivo;
            }
        }
        if(arq != null) {
            parecer.getArquivos().remove(arq);
            removerArquivos(arq.getId());
        }
    }

    public void removerArquivos(Long idArquivo) {
        if(idArquivo != null) {
            String sql = " delete from arquivotransferenciamovbci arquivo " +
                " where arquivo.id = :idArquivo";

            Query q = em.createNativeQuery(sql);
            q.setParameter("idArquivo", idArquivo);
            q.executeUpdate();
        }
    }

    public ArquivoComposicao criarArquivoComposicao(byte[] bytes, DetentorArquivoComposicao detentor, Arquivo arquivo) throws Exception {
        ArquivoComposicao arquivoComposicao = new ArquivoComposicao();
        arquivoComposicao.setArquivo(arquivoFacade.novoArquivoMemoria(arquivo, new ByteArrayInputStream(bytes)));
        arquivoComposicao.setFile(Util.criarArquivoUpload(arquivo.getNome(), arquivo.getMimeType(), bytes));
        arquivoComposicao.setDataUpload(sistemaFacade.getDataOperacao());
        arquivoComposicao.setDetentorArquivoComposicao(detentor);
        return arquivoComposicao;
    }

    private Arquivo criarArquivo(String nomeArquivo) {
        Arquivo arquivo = new Arquivo();
        arquivo.setDescricao(nomeArquivo);
        arquivo.setMimeType("application/pdf");
        arquivo.setNome(nomeArquivo);
        return arquivo;
    }

    public ParecerTransferenciaMovBCI buscarParecerTransferencia(Long idSolicitacao) {
        String sql = " select parecer.* from solicitacaotransfmovbci solicitacao " +
            " inner join parecertransferenciamovbci parecer on solicitacao.id = parecer.solicitacao_id " +
            " where solicitacao.id = :idSolicitacao ";

        Query q = em.createNativeQuery(sql, ParecerTransferenciaMovBCI.class);
        q.setParameter("idSolicitacao", idSolicitacao);

        List<ParecerTransferenciaMovBCI> pareceres = q.getResultList();
        if (pareceres != null && !pareceres.isEmpty()) {
            ParecerTransferenciaMovBCI parecer = pareceres.get(0);
            return parecerFacade.recuperar(parecer.getId());
        }
        return null;
    }
}
