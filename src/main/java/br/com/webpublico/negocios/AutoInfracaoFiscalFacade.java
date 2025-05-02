/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoAutoInfracao;
import br.com.webpublico.enums.SituacaoParcela;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * @author fabio
 */
@Stateless
public class AutoInfracaoFiscalFacade extends AbstractFacade<AutoInfracaoFiscal> {

    @EJB
    private ParametroFiscalizacaoFacade parametroFiscalizacaoFacade;
    @EJB
    private CalculoFiscalizacaoFacade calculoFiscalizacaoFacade;
    @EJB
    private AcaoFiscalFacade acaoFiscalFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private ProgramacaoFiscalFacade programacaoFiscalFacade;
    @EJB
    private DAMFacade damFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private MultaFiscalizacaoFacade multaFiscalizacaoFacade;
    @EJB
    private MoedaFacade moedaFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public AutoInfracaoFiscalFacade() {
        super(AutoInfracaoFiscal.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ParametroFiscalizacaoFacade getParametroFiscalizacaoFacade() {
        return parametroFiscalizacaoFacade;
    }

    public CalculoFiscalizacaoFacade getCalculoFiscalizacaoFacade() {
        return calculoFiscalizacaoFacade;
    }

    public AcaoFiscalFacade getAcaoFiscalFacade() {
        return acaoFiscalFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }

    public ProgramacaoFiscalFacade getProgramacaoFiscalFacade() {
        return programacaoFiscalFacade;
    }

    public DAMFacade getDamFacade() {
        return damFacade;
    }


    public RegistroLancamentoContabil recuperarRegistroLancamentoContabil(Object id) {
        RegistroLancamentoContabil reg = em.find(RegistroLancamentoContabil.class, id);
        reg.getLancamentosContabeis().size();
        reg.getMultas().size();
        reg.getAutosInfracao().size();
        Collections.sort(reg.getMultas());
        return reg;
    }

    public AutoInfracaoFiscal concluirAutoInfracao(AutoInfracaoFiscal autoInfracaoFiscal, SituacaoAutoInfracao situacaoAutoInfracao) throws Exception {
        ParametroFiscalizacao param = getParametroFiscalizacaoFacade().recuperarParametroFiscalizacao(sistemaFacade.getExercicioCorrente());
        Integer diasVencimento = 0;
        if (param != null) {
            diasVencimento = param.getVencimentoAutoInfracao();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(autoInfracaoFiscal.getDataCienciaRevelia());
        cal.add(Calendar.DAY_OF_MONTH, diasVencimento);
        autoInfracaoFiscal.setSituacaoAutoInfracao(situacaoAutoInfracao);
        autoInfracaoFiscal.setVencimento(cal.getTime());
        autoInfracaoFiscal.getRegistro().setSituacao(RegistroLancamentoContabil.Situacao.CIENCIA.equals(situacaoAutoInfracao) ?
            RegistroLancamentoContabil.Situacao.CIENCIA :
            RegistroLancamentoContabil.Situacao.REVELIA);
        if (autoInfracaoFiscal.getRegistro().getAcaoFiscal() != null) {
            getAcaoFiscalFacade().concluiAcao(autoInfracaoFiscal.getRegistro().getAcaoFiscal());
        }
        gerarDebitosDoAutoInfracaoFiscal(autoInfracaoFiscal);
        lancarSituacaoCanceladaParaParcelaMultaFiscalizacao(autoInfracaoFiscal);

        ProcessoCalculoMultaFiscal processoCalculoMultaFiscal = getCalculoFiscalizacaoFacade().lancaCalculoMultaFiscalizacao(autoInfracaoFiscal);
        processoCalculoMultaFiscal = recuperarProcessoMulta(processoCalculoMultaFiscal);
        getCalculoFiscalizacaoFacade().getGeraDebitoMulta().geraDebito(processoCalculoMultaFiscal);

        autoInfracaoFiscal = salvarAutoInfracaoFiscal(autoInfracaoFiscal);
        autoInfracaoFiscal = recuperar(autoInfracaoFiscal.getId());
        return autoInfracaoFiscal;
    }

    private void gerarDebitosDoAutoInfracaoFiscal(AutoInfracaoFiscal autoInfracaoFiscal) throws Exception {
        if (autoInfracaoFiscal.getValorTotal().compareTo(BigDecimal.ZERO) > 0) {
            lancarSituacaoCanceladaParaParcelaFiscalizacao(autoInfracaoFiscal);
            ProcessoCalculoFiscal processoCalculoFiscal = getCalculoFiscalizacaoFacade()
                .lancaCalculoFiscalizacao(autoInfracaoFiscal);
            processoCalculoFiscal = recuperarProcessoFiscal(processoCalculoFiscal);
            getCalculoFiscalizacaoFacade().getGeraDebitoFiscalizacao().geraDebito(processoCalculoFiscal);
        }
    }

    public AutoInfracaoFiscal salvarAutoInfracaoFiscal(AutoInfracaoFiscal entity) {
        return em.merge(entity);
    }

    public DoctoAcaoFiscal salvarDoctoAcaoFiscal(DoctoAcaoFiscal entity) {
        return em.merge(entity);
    }

    public ProcessoCalculoMultaFiscal recuperarProcessoMulta(ProcessoCalculoMultaFiscal processoCalculoMultaFiscal) {
        if (processoCalculoMultaFiscal != null && processoCalculoMultaFiscal.getId() != null) {
            ProcessoCalculoMultaFiscal p = em.find(ProcessoCalculoMultaFiscal.class, processoCalculoMultaFiscal.getId());
            p.getCalculoMultaFiscalizacao().size();
            for (CalculoMultaFiscalizacao calculoMultaFiscalizacao : p.getCalculoMultaFiscalizacao()) {
                calculoMultaFiscalizacao.getItemCalculoMultaFiscal().size();
            }
            return p;
        }
        return processoCalculoMultaFiscal;
    }

    public ProcessoCalculoFiscal recuperarProcessoFiscal(ProcessoCalculoFiscal processoCalculoFiscal) {
        if (processoCalculoFiscal != null && processoCalculoFiscal.getId() != null) {
            ProcessoCalculoFiscal p = em.find(ProcessoCalculoFiscal.class, processoCalculoFiscal.getId());
            p.getCalculosFiscalizacao().size();
            for (CalculoFiscalizacao calculoFiscalizacao : p.getCalculosFiscalizacao()) {
                calculoFiscalizacao.getItensCalculoFiscalizacao().size();
            }
            return p;
        }
        return processoCalculoFiscal;
    }

    public void lancarSituacaoCanceladaParaParcelaFiscalizacao(AutoInfracaoFiscal autoInfracaoFiscal) {
        List<ParcelaValorDivida> parcelaValorDividas = recuperarParcelasEmAbertoParaCalculoFiscalizacao(autoInfracaoFiscal);
        for (ParcelaValorDivida parcelaValorDivida : parcelaValorDividas) {
            SituacaoParcelaValorDivida situacaoParcela = new SituacaoParcelaValorDivida(SituacaoParcela.CANCELAMENTO, parcelaValorDivida, BigDecimal.ZERO);
            salvarSituacaoParcelaValorDivida(situacaoParcela);
        }
    }

    public void lancarSituacaoCanceladaParaParcelaMultaFiscalizacao(AutoInfracaoFiscal autoInfracaoFiscal) {
        List<ParcelaValorDivida> parcelaValorDividas = recuperarParcelasEmAbertoParaCalculoMultaFiscalizacao(autoInfracaoFiscal);
        for (ParcelaValorDivida parcelaValorDivida : parcelaValorDividas) {
            SituacaoParcelaValorDivida situacaoParcela = new SituacaoParcelaValorDivida(SituacaoParcela.CANCELAMENTO, parcelaValorDivida, BigDecimal.ZERO);
            salvarSituacaoParcelaValorDivida(situacaoParcela);
        }
    }

    public void salvarSituacaoParcelaValorDivida(SituacaoParcelaValorDivida situacao) {
        em.persist(situacao);
    }

    public List<ParcelaValorDivida> recuperarParcelasEmAbertoParaCalculoMultaFiscalizacao(AutoInfracaoFiscal autoInfracaoFiscal) {
        String sql = "SELECT pvd.*\n" +
            "  FROM parcelavalordivida pvd\n" +
            "INNER JOIN valordivida vd ON vd.id = pvd.valordivida_id\n" +
            "INNER JOIN CALCULOMULTAFISCALIZACAO calc ON calc.id = vd.calculo_id\n" +
            "INNER JOIN AUTOINFRACAOFISCAL auto ON auto.id = calc.AUTOINFRACAOFISCAL_ID\n" +
            "inner join SITUACAOPARCELAVALORDIVIDA sit on SIT.id = pvd.situacaoatual_id and SITUACAOPARCELA = :situacao" +
            "  WHERE auto.id = :auto";
        Query q = em.createNativeQuery(sql, ParcelaValorDivida.class);
        q.setParameter("auto", autoInfracaoFiscal.getId());
        q.setParameter("situacao", SituacaoParcela.EM_ABERTO.name());
        try {
            List<ParcelaValorDivida> parcelas = q.getResultList();
            for (ParcelaValorDivida parcela : parcelas) {
                initializeAndUnproxy(parcela.getValorDivida().getCalculo());
            }

            return parcelas;
        } catch (NoResultException e) {
            return new ArrayList<>();
        }
    }

    public List<ParcelaValorDivida> recuperarParcelasEmAbertoParaCalculoFiscalizacao(AutoInfracaoFiscal autoInfracaoFiscal) {
        String sql = "SELECT pvd.* " +
            "  FROM parcelavalordivida pvd " +
            "INNER JOIN valordivida vd ON vd.id = pvd.valordivida_id " +
            "INNER JOIN CALCULOFISCALIZACAO calc ON calc.id = vd.calculo_id " +
            "INNER JOIN AUTOINFRACAOFISCAL auto ON auto.id = calc.AUTOINFRACAOFISCAL_ID " +
            "inner join SITUACAOPARCELAVALORDIVIDA sit on SIT.id = pvd.situacaoatual_id and SITUACAOPARCELA = :situacao " +
            "  WHERE auto.id = :auto";
        Query q = em.createNativeQuery(sql, ParcelaValorDivida.class);
        q.setParameter("auto", autoInfracaoFiscal.getId());
        q.setParameter("situacao", SituacaoParcela.EM_ABERTO.name());
        try {
            List<ParcelaValorDivida> parcelas = q.getResultList();
            for (ParcelaValorDivida parcela : parcelas) {
                initializeAndUnproxy(parcela.getValorDivida().getCalculo());
            }

            return parcelas;
        } catch (NoResultException e) {
            return new ArrayList<>();
        }
    }

    public AutoInfracaoFiscal recuperarAutoInfracaoDaParcelaInscritaEmDA(Long idParcela) {
        String sql = "select auto.id " +
            "from autoinfracaofiscal auto " +
            "         left join calculofiscalizacao calcfisc on auto.id = calcfisc.autoinfracaofiscal_id " +
            "         left join calculomultafiscalizacao calcmultafisc on auto.id = calcmultafisc.autoinfracaofiscal_id " +
            "         inner join valordivida vd on vd.calculo_id = calcfisc.id or vd.calculo_id = calcmultafisc.id " +
            "         inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id " +
            " where pvd.id = (select inscparcela.parcelavalordivida_id " +
            "                from inscricaodividaparcela inscparcela " +
            "                         inner join valordivida vdinsc on inscparcela.iteminscricaodividaativa_id = vdinsc.calculo_id " +
            "                         inner join parcelavalordivida pvdinsc on pvdinsc.valordivida_id = vdinsc.id " +
            "                where pvdinsc.id = :idParcela) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idParcela", idParcela);
        List<BigDecimal> idAutos = q.getResultList();
        if (!idAutos.isEmpty()) return recuperar(idAutos.get(0).longValue());
        return null;
    }

    @Override
    public AutoInfracaoFiscal recuperar(Object id) {
        AutoInfracaoFiscal auto = super.recuperar(id);
        if (auto.getRegistro() != null) {
            auto.getRegistro().getAutosInfracao().size();
        }
        return auto;
    }

    public DocumentoOficialFacade getDocumentoOficialFacade() {
        return documentoOficialFacade;
    }

    public Integer numeroAutoInfracaoMaisUm(Integer ano) {
        return acaoFiscalFacade.buscarNumeroAutoInfracaoMaisUm(ano);
    }

    public List<MultaFiscalizacao> buscarMultasPorIncidenciaAndTipoMultaAndTipoCalculoAndFormaCalculo(String s) {
        return multaFiscalizacaoFacade.buscarMultasPorIncidenciaAndTipoMultaAndTipoCalculoAndFormaCalculo(s);
    }

    public BigDecimal buscarValorUFMParaAno(Integer ano) {
        return moedaFacade.buscarValorUFMParaAno(ano);
    }

    public RegistroLancamentoContabil salvarRegistro(RegistroLancamentoContabil registroLancamentoContabil) {
        return em.merge(registroLancamentoContabil);
    }
}
