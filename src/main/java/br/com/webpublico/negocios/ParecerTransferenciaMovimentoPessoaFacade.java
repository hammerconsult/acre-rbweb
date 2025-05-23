package br.com.webpublico.negocios;

import br.com.webpublico.controle.ParecerTransferenciaMovimentoPessoaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.tributario.procuradoria.PessoaProcesso;
import br.com.webpublico.entidades.tributario.procuradoria.TramiteProcessoJudicial;
import br.com.webpublico.entidadesauxiliares.VOMovimentosTributario;
import br.com.webpublico.negocios.tributario.dao.JdbcCalculoDAO;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 10/10/15
 * Time: 08:33
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ParecerTransferenciaMovimentoPessoaFacade extends AbstractFacade<ParecerTransferenciaMovimentoPessoa> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private EmpenhoFacade empenhoFacade;
    @EJB
    private PropostaConcessaoDiariaFacade propostaConcessaoDiariaFacade;
    @EJB
    private DividaPublicaFacade dividaPublicaFacade;
    @EJB
    private AjusteDepositoFacade ajusteDepositoFacade;
    @EJB
    private AjusteAtivoDisponivelFacade ajusteAtivoDisponivelFacade;
    @EJB
    private ReceitaExtraFacade receitaExtraFacade;
    @EJB
    private ReceitaExtraEstornoFacade receitaExtraEstornoFacade;
    @EJB
    private PagamentoExtraFacade pagamentoExtraFacade;
    @EJB
    private PagamentoExtraEstornoFacade pagamentoExtraEstornoFacade;
    @EJB
    private CreditoReceberFacade creditoReceberFacade;
    @EJB
    private DividaAtivaContabilFacade dividaAtivaContabilFacade;
    @EJB
    private LancamentoReceitaOrcFacade lancamentoReceitaOrcFacade;
    @EJB
    private ReceitaORCEstornoFacade receitaORCEstornoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private TransferenciaMovPessoaFacade transferenciaMovPessoaFacade;
    private JdbcCalculoDAO dao;

    public ParecerTransferenciaMovimentoPessoaFacade() {
        super(ParecerTransferenciaMovimentoPessoa.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ParecerTransferenciaMovimentoPessoa recuperar(Object id) {
        ParecerTransferenciaMovimentoPessoa entity = em.find(ParecerTransferenciaMovimentoPessoa.class, id);
        if (entity.getTransferenciaMovPessoa() != null) {
            Hibernate.initialize(entity.getTransferenciaMovPessoa().getBcis());
            Hibernate.initialize(entity.getTransferenciaMovPessoa().getBces());
            Hibernate.initialize(entity.getTransferenciaMovPessoa().getBcrs());
        }
        return entity;
    }

    private void transferirMovimentosTributario(TransferenciaMovPessoa transferencia) {
        if (transferencia.getTransfereMovimentosTributario()) {
            VOMovimentosTributario vo = transferenciaMovPessoaFacade.recuperarMovimentosDePessoaDoTributario(transferencia);

            transferirTransmitentesCalculoITBI(transferencia, vo);
            transferirAdquirentesCalculoITBI(transferencia, vo);
            transferirAssinantesCertidoesDividaAtiva(transferencia, vo);
            transferirEmissoresCertidoesDividaAtiva(transferencia, vo);
            transferirDevedorCertidoesDividaAtiva(transferencia, vo);
            transferirItensInscricaoDividaAtiva(transferencia, vo);
            transferirTransferenciasPermissaoDeTransporte(transferencia, vo);
            transferirEnvolvidoProcessosJudiciais(transferencia, vo);
            transferirSolicitanteFisicoDocumentoOficial(transferencia, vo);
            transferirContribuintesInscricoesDividaAtiva(transferencia, vo);
            transferirPessoaProcessoFiscalizacao(transferencia, vo);
            transferirLocatariosContratoRendasPatrimoniais(transferencia, vo);
            transferirProprietariosCartorios(transferencia, vo);
            transferirDevedoresProcessoDeParcelamento(transferencia, vo);
            transferirProcuradorProcessosDeParcelamento(transferencia, vo);
            transferirFiadorProcessoDeParcelamento(transferencia, vo);
            transferirTomadorNotaFiscalAvulsa(transferencia, vo);
            transferirPrestadorNotaFiscalAvulsa(transferencia, vo);
            transferirSolicitanteProcessos(transferencia, vo);
            transferirRestituintesProcessoRestituicao(transferencia, vo);
            transferirPessoaCalculoDividaDiversa(transferencia, vo);
            transferirContribuintesCalculoTaxaDiversa(transferencia, vo);
            transferirPessoasDocumentoOficial(transferencia, vo);
            transferirPessoasProcessoDeIsencaoDeAcrescimos(transferencia, vo);
            transferirPessoasPagamentoJudicial(transferencia, vo);
            transferirPessoasUsuarioSistema(transferencia, vo);
            transferirJuizesResponsaveisProcessoJudicial(transferencia, vo);
            transferirMotoristaInfratorAutuacaoRBTrans(transferencia, vo);
            transferirCalculoPessoa(transferencia, vo);
        }
    }

    private void transferirMotoristaInfratorAutuacaoRBTrans(TransferenciaMovPessoa transferencia, VOMovimentosTributario vo) {
        if (vo.getMotoristaInfratorAutuacoesFiscalizacaoRBTrans() != null) {
            for (AutuacaoFiscalizacaoRBTrans a : vo.getMotoristaInfratorAutuacoesFiscalizacaoRBTrans()) {
                try {
                    a.setMotoristaInfrator((PessoaFisica) transferencia.getPessoaDestino());
                    em.merge(a);
                } catch (Exception ex) {
                    logger.error(ex.getMessage());
                }
            }
        }
    }

    private void transferirJuizesResponsaveisProcessoJudicial(TransferenciaMovPessoa transferencia, VOMovimentosTributario vo) {
        for (TramiteProcessoJudicial t : vo.getJuizResponsavelProcessosJudiciais()) {
            t.setJuizResponsavel((PessoaFisica) transferencia.getPessoaDestino());
            em.merge(t);
        }
    }

    private void transferirPessoasUsuarioSistema(TransferenciaMovPessoa transferencia, VOMovimentosTributario vo) {
        for (UsuarioSistema u : vo.getPessoaFisicaUsuariosSistema()) {
            u.setPessoaFisica((PessoaFisica) transferencia.getPessoaDestino());
            em.merge(u);
        }
    }

    private void transferirPessoasPagamentoJudicial(TransferenciaMovPessoa transferencia, VOMovimentosTributario vo) {
        for (PagamentoJudicial p : vo.getPessoaPagamentosJudiciais()) {
            p.setPessoa(transferencia.getPessoaDestino());
            em.merge(p);
        }
    }

    private void transferirPessoasProcessoDeIsencaoDeAcrescimos(TransferenciaMovPessoa transferencia, VOMovimentosTributario vo) {
        for (ProcessoIsencaoAcrescimos p : vo.getPessoaProcessosDeIsencaoDeAcrescimos()) {
            p.setPessoa(transferencia.getPessoaDestino());
            em.merge(p);

        }
    }

    private void transferirPessoasDocumentoOficial(TransferenciaMovPessoa transferencia, VOMovimentosTributario vo) {
        for (DocumentoOficial d : vo.getPessoaDocumentosOficiais()) {
            d.setPessoa(transferencia.getPessoaDestino());
            em.merge(d);
        }
    }

    private void transferirContribuintesCalculoTaxaDiversa(TransferenciaMovPessoa transferencia, VOMovimentosTributario vo) {
        for (CalculoTaxasDiversas c : vo.getContribuinteCalculosTaxasDiversas()) {
            c.setContribuinte(transferencia.getPessoaDestino());
            em.merge(c);
        }
    }

    private void transferirPessoaCalculoDividaDiversa(TransferenciaMovPessoa transferencia, VOMovimentosTributario vo) {
        for (CalculoDividaDiversa c : vo.getContribuinteCalculosDividaDiversa()) {
            c.setPessoa(transferencia.getPessoaDestino());
            em.merge(c);
        }
    }

    private void transferirRestituintesProcessoRestituicao(TransferenciaMovPessoa transferencia, VOMovimentosTributario vo) {
        for (ProcessoRestituicao p : vo.getRestituinteProcessoDeRestituicao()) {
            p.setRestituinte(transferencia.getPessoaDestino());
            em.merge(p);
        }
    }

    private void transferirSolicitanteProcessos(TransferenciaMovPessoa transferencia, VOMovimentosTributario vo) {
        for (Processo p : vo.getAutorRequerenteProcesso()) {
            p.setPessoa(transferencia.getPessoaDestino());
            em.merge(p);
        }
    }

    private void transferirPrestadorNotaFiscalAvulsa(TransferenciaMovPessoa transferencia, VOMovimentosTributario vo) {
        for (NFSAvulsa n : vo.getPrestadorNotasFiscaisAvulsas()) {
            n.setPrestador(transferencia.getPessoaDestino());
            em.merge(n);
        }
    }

    private void transferirTomadorNotaFiscalAvulsa(TransferenciaMovPessoa transferencia, VOMovimentosTributario vo) {
        for (NFSAvulsa n : vo.getTomadorNotasFiscaisAvulsas()) {
            n.setTomador(transferencia.getPessoaDestino());
            em.merge(n);
        }
    }

    private void transferirFiadorProcessoDeParcelamento(TransferenciaMovPessoa transferencia, VOMovimentosTributario vo) {
        for (ProcessoParcelamento p : vo.getFiadorProcessosParcelamento()) {
            p.setFiador(transferencia.getPessoaDestino());
            em.merge(p);
        }
    }

    private void transferirProcuradorProcessosDeParcelamento(TransferenciaMovPessoa transferencia, VOMovimentosTributario vo) {
        for (ProcessoParcelamento p : vo.getProcuradorProcessosParcelamento()) {
            p.setProcurador(transferencia.getPessoaDestino());
            em.merge(p);
        }
    }

    private void transferirDevedoresProcessoDeParcelamento(TransferenciaMovPessoa transferencia, VOMovimentosTributario vo) {
        for (ProcessoParcelamento p : vo.getDevedorProcessosDeParcelamento()) {
            p.setPessoa(transferencia.getPessoaDestino());
            em.merge(p);
        }
    }

    private void transferirProprietariosCartorios(TransferenciaMovPessoa transferencia, VOMovimentosTributario vo) {
        for (PropriedadeCartorio p : vo.getProprietarioCartorios()) {
            p.setPessoa(transferencia.getPessoaDestino());
            em.merge(p);
        }
    }

    private void transferirLocatariosContratoRendasPatrimoniais(TransferenciaMovPessoa transferencia, VOMovimentosTributario vo) {
        for (ContratoRendasPatrimoniais c : vo.getLocatarioContratosRendasPatrimoniais()) {
            c.setLocatario(transferencia.getPessoaDestino());
            em.merge(c);
        }
    }

    private void transferirPessoaProcessoFiscalizacao(TransferenciaMovPessoa transferencia, VOMovimentosTributario vo) {
        for (ProcessoFiscalizacao p : vo.getFiscalizadoProcessosDeFiscalizacao()) {
            p.setPessoa(transferencia.getPessoaDestino());
            em.merge(p);
        }
    }

    private void transferirContribuintesInscricoesDividaAtiva(TransferenciaMovPessoa transferencia, VOMovimentosTributario vo) {
        for (InscricaoDividaAtiva i : vo.getContribuinteInscricoesDividaAtiva()) {
            i.setContribuinte(transferencia.getPessoaDestino());
            em.merge(i);
        }
    }

    private void transferirSolicitanteJuridicoDocumentoOficial(TransferenciaMovPessoa transferencia, VOMovimentosTributario vo) {
        for (SolicitacaoDoctoOficial s : vo.getSolicitanteFisicoSolicitacoesDocumentoOficial()) {
            s.setPessoaJuridica((PessoaJuridica) transferencia.getPessoaDestino());
            em.merge(s);
        }
    }

    private void transferirSolicitanteFisicoDocumentoOficial(TransferenciaMovPessoa transferencia, VOMovimentosTributario vo) {
        for (SolicitacaoDoctoOficial s : vo.getSolicitanteFisicoSolicitacoesDocumentoOficial()) {
            s.setPessoaFisica((PessoaFisica) transferencia.getPessoaDestino());
            em.merge(s);
        }
    }

    private void transferirEnvolvidoProcessosJudiciais(TransferenciaMovPessoa transferencia, VOMovimentosTributario vo) {
        for (PessoaProcesso p : vo.getEnvolvidoProcessosJudiciais()) {
            p.setPessoa(transferencia.getPessoaDestino());
            em.merge(p);
        }
    }

    private void transferirTransferenciasPermissaoDeTransporte(TransferenciaMovPessoa transferencia, VOMovimentosTributario vo) {
        for (TransferenciaPermissaoTransporte t : vo.getSolicitanteTransferenciasPermissao()) {
            t.setSolicitante(transferencia.getPessoaDestino());
            em.merge(t);
        }
    }

    private void transferirItensInscricaoDividaAtiva(TransferenciaMovPessoa transferencia, VOMovimentosTributario vo) {
        for (ItemInscricaoDividaAtiva i : vo.getContribuinteItensInscricaoDividaAtiva()) {
            i.setPessoa(transferencia.getPessoaDestino());
            em.merge(i);
        }
    }

    private void transferirDevedorCertidoesDividaAtiva(TransferenciaMovPessoa transferencia, VOMovimentosTributario vo) {
        for (CertidaoDividaAtiva c : vo.getEmissorCertidoesDividaAtiva()) {
            c.setPessoa(transferencia.getPessoaDestino());
            em.merge(c);
        }
    }

    private void transferirEmissoresCertidoesDividaAtiva(TransferenciaMovPessoa transferencia, VOMovimentosTributario vo) {
        for (CertidaoDividaAtiva c : vo.getEmissorCertidoesDividaAtiva()) {
            c.setFuncionarioEmissao((PessoaFisica) transferencia.getPessoaDestino());
            em.merge(c);
        }
    }

    private void transferirAssinantesCertidoesDividaAtiva(TransferenciaMovPessoa transferencia, VOMovimentosTributario vo) {
        for (CertidaoDividaAtiva c : vo.getAssinanteCertidoesDividaAtiva()) {
            c.setFuncionarioAssinante((PessoaFisica) transferencia.getPessoaDestino());
            em.merge(c);
        }
    }

    private void transferirAdquirentesCalculoITBI(TransferenciaMovPessoa transferencia, VOMovimentosTributario vo) {
        for (CalculoITBI obj : vo.getTransmitenteCalculosITBI()) {
            List<AdquirentesCalculoITBI> transmitentes = transferenciaMovPessoaFacade.recuperarAdquirentesPessoaCalculoITBI(transferencia.getPessoaOrigem());
            for (AdquirentesCalculoITBI a : transmitentes) {
                a.setAdquirente(transferencia.getPessoaDestino());
                em.merge(a);
            }
        }
    }

    private void transferirTransmitentesCalculoITBI(TransferenciaMovPessoa transferencia, VOMovimentosTributario vo) {
        for (CalculoITBI obj : vo.getTransmitenteCalculosITBI()) {
            List<TransmitentesCalculoITBI> transmitentes = transferenciaMovPessoaFacade.recuperarTransmitentesPessoaCalculoITBI(transferencia.getPessoaOrigem());
            for (TransmitentesCalculoITBI t : transmitentes) {
                t.setPessoa(transferencia.getPessoaDestino());
                em.merge(t);
            }
        }
    }

    public List<Propriedade> buscarPropriedadesCadastrosImobiliariosParaTransferir(TransferenciaMovPessoa transferenciaMovPessoa) {
        List<Propriedade> propriedades = Lists.newArrayList();
        if (transferenciaMovPessoa.getTransfereBcis()) {
            if (transferenciaMovPessoa.getBcis().size() > 0) {
                for (TransferenciaMovPessoaBci transferenciaMovPessoaBci : transferenciaMovPessoa.getBcis()) {
                    CadastroImobiliario cadastroImobiliario =  cadastroImobiliarioFacade.recuperarSemCalcular(transferenciaMovPessoaBci.getCadastroImobiliario().getId());
                    propriedades.add(cadastroImobiliario.getPropriedade(transferenciaMovPessoa.getPessoaOrigem()));
                }
            } else {
                propriedades = cadastroImobiliarioFacade.recuperarPropriedadesVigentes(transferenciaMovPessoa.getPessoaOrigem());
//                for (Propriedade propriedade : propriedades) {
//                    if (!verificarJaExisteImovelAdicionado(propriedade.getImovel(), transferenciaMovPessoa)) {
//                        TransferenciaMovPessoaBci bci = new TransferenciaMovPessoaBci();
//                        bci.setTransferenciaMovPessoa(transferenciaMovPessoa);
//                        bci.setCadastroImobiliario(propriedade.getImovel());
//                        transferenciaMovPessoa.getBcis().add(bci);
//                    }
//                }
            }
        }
        return propriedades;
    }

//    private boolean verificarJaExisteImovelAdicionado(CadastroImobiliario cadastroImobiliario, TransferenciaMovPessoa transferenciaMovPessoa) {
//        if (transferenciaMovPessoa.getBcis() != null) {
//            for (TransferenciaMovPessoaBci bci : transferenciaMovPessoa.getBcis()) {
//                if (bci.getId() != null && bci.getId().equals(cadastroImobiliario.getId())) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

    private void transferirCadastrosImobiliarios(TransferenciaMovPessoa transferenciaMovPessoa) {
        for (Propriedade propriedade : buscarPropriedadesCadastrosImobiliariosParaTransferir(transferenciaMovPessoa)) {
            getDAO().transferirPropriedade(propriedade.getId(), transferenciaMovPessoa.getPessoaDestino().getId());
        }
    }

    public List<CadastroEconomico> buscarCadastrosEconomicosParaTransferir(TransferenciaMovPessoa transferenciaMovPessoa) {
        List<CadastroEconomico> cadastros = Lists.newArrayList();
        if (transferenciaMovPessoa.getTransfereBces()) {
            if (!transferenciaMovPessoa.getBces().isEmpty()) {
                for (TransferenciaMovPessoaBce transferenciaMovPessoaBce : transferenciaMovPessoa.getBces()) {
                    cadastros.add(transferenciaMovPessoaBce.getCadastroEconomico());
                }
            } else {
                cadastros = cadastroEconomicoFacade.recuperarCadastrosSomenteDaPessoa(transferenciaMovPessoa.getPessoaOrigem());
            }
        }
        return cadastros;
    }

    private void transferirCadastroEconomico(TransferenciaMovPessoa transferenciaMovPessoa) {
        for (CadastroEconomico cmc : buscarCadastrosEconomicosParaTransferir(transferenciaMovPessoa)) {
            if (cmc.getPessoa().equals(transferenciaMovPessoa.getPessoaOrigem())) {
                getDAO().transferirPessoaCMC(cmc.getId(), transferenciaMovPessoa.getPessoaDestino().getId());
            }
        }
    }

    public List<PropriedadeRural> buscarPropriedadesCadastrosRuraisParaTransferir(TransferenciaMovPessoa transferenciaMovPessoa) {
        List<PropriedadeRural> propriedadeRurais = Lists.newArrayList();
        if (transferenciaMovPessoa.getTransfereBcrs()) {
            if (transferenciaMovPessoa.getBcrs().size() > 0) {
                for (TransferenciaMovPessoaBcr transferenciaMovPessoaBcr : transferenciaMovPessoa.getBcrs()) {
                    transferenciaMovPessoaBcr.getCadastroRural().setPropriedade(cadastroRuralFacade.recuperarPropriedadesVigentes(transferenciaMovPessoaBcr.getCadastroRural()));
                    propriedadeRurais.add(transferenciaMovPessoaBcr.getCadastroRural().getPropriedade(transferenciaMovPessoa.getPessoaOrigem()));
                }
            } else {
                propriedadeRurais.addAll(cadastroRuralFacade.recuperarPropriedadesVigentes(transferenciaMovPessoa.getPessoaOrigem()));
//                for (PropriedadeRural propriedadeRural : propriedadeRurais) {
//                    TransferenciaMovPessoaBcr bcr = new TransferenciaMovPessoaBcr();
//                    bcr.setTransferenciaMovPessoa(transferenciaMovPessoa);
//                    bcr.setCadastroRural(propriedadeRural.getImovel());
//                    transferenciaMovPessoa.getBcrs().add(bcr);
//                }
            }
        }
        return propriedadeRurais;
    }

    private void transferirCadastroRural(TransferenciaMovPessoa transferenciaMovPessoa) {
        for (PropriedadeRural propriedadeRural : buscarPropriedadesCadastrosRuraisParaTransferir(transferenciaMovPessoa)) {
            getDAO().transferirPropriedadeRural(propriedadeRural.getId(), transferenciaMovPessoa.getPessoaDestino().getId());
        }
    }

    private void transferirCalculoPessoa(TransferenciaMovPessoa transferenciaMovPessoa, VOMovimentosTributario vo) {
        for (ResultadoParcela parcela : vo.getResultadosParcela()) {
            getDAO().transferirCalculoPessoa(transferenciaMovPessoa.getPessoaOrigem().getId(), transferenciaMovPessoa.getPessoaDestino().getId(), parcela.getIdCalculo());
        }
    }

    private void transferirMovimentosContabeis(TransferenciaMovPessoa transferenciaMovPessoa) {
        if (!transferenciaMovPessoa.getTransfereMovContabeis()) {
            return;
        }
        Pessoa pessoaOrigem = pessoaFacade.recuperar(transferenciaMovPessoa.getPessoaOrigem().getId());
        Pessoa pessoaDestino = pessoaFacade.recuperar(transferenciaMovPessoa.getPessoaDestino().getId());

        adicionarClasseCredorPessoaOrigemNaPessoaDestino(pessoaOrigem, pessoaDestino);
        transferirPessoaNoEmpenho(pessoaOrigem, pessoaDestino);
        transferirPessoaNaDiaria(pessoaOrigem, pessoaDestino);
        transferirPessoaNaDividaPublica(pessoaOrigem, pessoaDestino);
        transferirPessoaNaReceitaExtra(pessoaOrigem, pessoaDestino);
        transferirPessoaNaDespesaExtra(pessoaOrigem, pessoaDestino);
        transferirPessoaNoAjusteAtivoDisponivel(pessoaOrigem, pessoaDestino);
        transferirPessoaNoAjusteDeposito(pessoaOrigem, pessoaDestino);
        transferirPessoaNoCreditoReceber(pessoaOrigem, pessoaDestino);
        transferirPessoaNaDividaAtiva(pessoaOrigem, pessoaDestino);
        transferirPessoaNaReceitaRealizada(pessoaOrigem, pessoaDestino);
        transferirPessoaNoEstornoReceitaRealizada(pessoaOrigem, pessoaDestino);
    }


    private void transferirPessoaNoEmpenho(Pessoa pessoaOrigem, Pessoa pessoaDestino) {
        List<Empenho> empenhos = empenhoFacade.recuperarEmpenhosDaPessoa(pessoaOrigem);
        for (Empenho empenho : empenhos) {
            empenho.setFornecedor(pessoaDestino);
            em.merge(empenho);
        }
    }

    private void transferirPessoaNaDiaria(Pessoa pessoaOrigem, Pessoa pessoaDestino) {
        List<PropostaConcessaoDiaria> diarias = propostaConcessaoDiariaFacade.recuperarDiariasDaPessoa(pessoaOrigem);
        for (PropostaConcessaoDiaria diaria : diarias) {
            diaria.setPessoaFisica(pessoaDestino);
            em.merge(diaria);
        }
    }

    private void transferirPessoaNaDividaPublica(Pessoa pessoaOrigem, Pessoa pessoaDestino) {
        List<DividaPublica> dividaPublicas = dividaPublicaFacade.recuperarDividaPublicasDaPessoa(pessoaOrigem);
        for (DividaPublica dividaPublica : dividaPublicas) {
            dividaPublica.setPessoa(pessoaDestino);
            em.merge(dividaPublica);
        }
    }

    private void transferirPessoaNaReceitaExtra(Pessoa pessoaOrigem, Pessoa pessoaDestino) {
        List<ReceitaExtra> receitaExtras = receitaExtraFacade.recuperarReceitaExtraDaPessoa(pessoaOrigem);
        for (ReceitaExtra receitaExtra : receitaExtras) {
            receitaExtra.setPessoa(pessoaDestino);
            em.merge(receitaExtra);
        }
    }

    private void transferirPessoaNaDespesaExtra(Pessoa pessoaOrigem, Pessoa pessoaDestino) {
        List<PagamentoExtra> lista = pagamentoExtraFacade.recuperarPagamentoExtraDaPessoa(pessoaOrigem);
        for (PagamentoExtra o : lista) {
            o.setFornecedor(pessoaDestino);
            em.merge(o);
        }
    }

    private void transferirPessoaNoAjusteDeposito(Pessoa pessoaOrigem, Pessoa pessoaDestino) {
        List<AjusteDeposito> lista = ajusteDepositoFacade.recuperarAjusteDepositoDaPessoa(pessoaOrigem);
        for (AjusteDeposito o : lista) {
            o.setPessoa(pessoaDestino);
            em.merge(o);
        }
    }

    private void transferirPessoaNoAjusteAtivoDisponivel(Pessoa pessoaOrigem, Pessoa pessoaDestino) {
        List<AjusteAtivoDisponivel> lista = ajusteAtivoDisponivelFacade.recuperarAjusteAtivoDisponivelDaPessoa(pessoaOrigem);
        for (AjusteAtivoDisponivel o : lista) {
            o.setPessoa(pessoaDestino);
            em.merge(o);
        }
    }

    private void transferirPessoaNoCreditoReceber(Pessoa pessoaOrigem, Pessoa pessoaDestino) {
        List<CreditoReceber> lista = creditoReceberFacade.recuperarCreditoReceberDaPessoa(pessoaOrigem);
        for (CreditoReceber o : lista) {
            o.setPessoa(pessoaDestino);
            em.merge(o);
        }
    }

    private void transferirPessoaNaDividaAtiva(Pessoa pessoaOrigem, Pessoa pessoaDestino) {
        List<DividaAtivaContabil> lista = dividaAtivaContabilFacade.recuperarDividaAtivaDaPessoa(pessoaOrigem);
        for (DividaAtivaContabil o : lista) {
            o.setPessoa(pessoaDestino);
            em.merge(o);
        }
    }

    private void transferirPessoaNaReceitaRealizada(Pessoa pessoaOrigem, Pessoa pessoaDestino) {
        List<LancamentoReceitaOrc> lista = lancamentoReceitaOrcFacade.recuperarReceitaRealizadaDaPessoa(pessoaOrigem);
        for (LancamentoReceitaOrc o : lista) {
            o.setPessoa(pessoaDestino);
            em.merge(o);
        }
    }

    private void transferirPessoaNoEstornoReceitaRealizada(Pessoa pessoaOrigem, Pessoa pessoaDestino) {
        List<ReceitaORCEstorno> lista = receitaORCEstornoFacade.recuperarEstornoReceitaRealizadaDaPessoa(pessoaOrigem);
        for (ReceitaORCEstorno o : lista) {
            o.setPessoa(pessoaDestino);
            em.merge(o);
        }
    }

    private void adicionarClasseCredorPessoaOrigemNaPessoaDestino(Pessoa pessoaOrigem, Pessoa pessoaDestino) {

        List<ClasseCredorPessoa> listaClasseCredorPessoaAux = new ArrayList<>();
        List<ClasseCredor> classesCredorDestino = recuperarClasseCredor(pessoaDestino);

        for (ClasseCredorPessoa classeCredorPessoaOrigem : pessoaOrigem.getClasseCredorPessoas()) {
            if (!classesCredorDestino.contains(classeCredorPessoaOrigem.getClasseCredor())) {
                listaClasseCredorPessoaAux.add(classeCredorPessoaOrigem);
            }
        }
        for (ClasseCredorPessoa classeCredorPessoaAux : listaClasseCredorPessoaAux) {
            ClasseCredorPessoa classeCredorPessoa = new ClasseCredorPessoa();
            classeCredorPessoa.setClasseCredor(classeCredorPessoaAux.getClasseCredor());
            classeCredorPessoa.setDataInicioVigencia(classeCredorPessoaAux.getDataInicioVigencia());
            classeCredorPessoa.setCriadoEm(classeCredorPessoaAux.getCriadoEm());
            classeCredorPessoa.setDataFimVigencia(null);
            classeCredorPessoa.setId(null);
            classeCredorPessoa.setOperacaoClasseCredor(classeCredorPessoaAux.getOperacaoClasseCredor());
            classeCredorPessoa.setPessoa(pessoaDestino);
            pessoaDestino.getClasseCredorPessoas().add(classeCredorPessoa);
        }
        pessoaFacade.salvar(pessoaDestino);
    }

    public List<ClasseCredor> recuperarClasseCredor(Pessoa pessoa) {
        List<ClasseCredor> listaClasse = new ArrayList<>();
        for (ClasseCredorPessoa classe : pessoa.getClasseCredorPessoas()) {
            listaClasse.add(classe.getClasseCredor());
        }
        return listaClasse;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 10)
    public Future<TransferenciaMovPessoa> deferirParecerSolicitacao(ParecerTransferenciaMovimentoPessoa entity, ParecerTransferenciaMovimentoPessoaControlador.ParametrosRelatorioTransferenciaPessoa parametro, UsuarioSistema usuarioCorrente, UnidadeOrganizacional unidadeOrcamentariaCorrente) throws Exception {
        TransferenciaMovPessoa transferenciaMovPessoa = entity.getTransferenciaMovPessoa();
        if (transferenciaMovPessoa != null) {
            transferirCadastrosImobiliarios(transferenciaMovPessoa);
            transferirCadastroEconomico(transferenciaMovPessoa);
            transferirCadastroRural(transferenciaMovPessoa);
            transferirMovimentosTributario(transferenciaMovPessoa);
            transferirMovimentosContabeis(transferenciaMovPessoa);
            inativarPessoaTransferida(transferenciaMovPessoa);
            transferenciaMovPessoaFacade.deferirTransferencia(transferenciaMovPessoa, parametro, usuarioCorrente, unidadeOrcamentariaCorrente);
        } else {
            throw new ExcecaoNegocioGenerica("A solicitação de transferência de movimento da pessoa não encontrada realizar a operação.");
        }
        salvarNovo(entity);
        return new AsyncResult<>(transferenciaMovPessoa);
    }

    private void inativarPessoaTransferida(TransferenciaMovPessoa transferenciaMovPessoa) {
        if (transferenciaMovPessoa.getInativaPessoaTransferida()) {
            removerCpfCpnjPessoaOrigem(transferenciaMovPessoa);
            pessoaFacade.inativarPessoa(transferenciaMovPessoa.getPessoaOrigem());
        }
    }

    private void removerCpfCpnjPessoaOrigem(TransferenciaMovPessoa transferenciaMovPessoa){
        if (transferenciaMovPessoa.getPessoaOrigem() instanceof PessoaFisica)
            ((PessoaFisica) transferenciaMovPessoa.getPessoaOrigem()).setCpf(null);
         else
            ((PessoaJuridica) transferenciaMovPessoa.getPessoaOrigem()).setCnpj(null);
    }

    public void indeferirParecerSolicitacao(ParecerTransferenciaMovimentoPessoa entity) {
        TransferenciaMovPessoa transferenciaMovPessoa = entity.getTransferenciaMovPessoa();
        if (transferenciaMovPessoa != null) {
            transferenciaMovPessoaFacade.indeferirTransferencia(transferenciaMovPessoa);
        } else {
            throw new ExcecaoNegocioGenerica("A solicitação de transferência de movimento da pessoa não encontrada realizar a operação.");
        }
        salvarNovo(entity);
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public TransferenciaMovPessoaFacade getTransferenciaMovPessoaFacade() {
        return transferenciaMovPessoaFacade;
    }

    private JdbcCalculoDAO getDAO() {
        if (dao == null) {
            ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
            dao = (JdbcCalculoDAO) ap.getBean("calculoDAO");
        }
        return dao;
    }
}
