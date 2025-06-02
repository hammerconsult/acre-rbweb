package br.com.webpublico.negocios;

import br.com.webpublico.controle.ParecerTransferenciaMovimentoPessoaControlador;
import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.tributario.procuradoria.PessoaProcesso;
import br.com.webpublico.entidades.tributario.procuradoria.TramiteProcessoJudicial;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.SituacaoCertidaoDA;
import br.com.webpublico.enums.SituacaoTransfMovimentoPessoa;
import br.com.webpublico.enums.TipoEndereco;
import br.com.webpublico.enums.TipoValorPensaoAlimenticia;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 05/02/15
 * Time: 08:33
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class TransferenciaMovPessoaFacade extends AbstractFacade<TransferenciaMovPessoa> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    @EJB
    private ParecerTransferenciaMovimentoPessoaFacade parecerTransferenciaMovimentoPessoaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private DependenteFacade dependenteFacade;

    public TransferenciaMovPessoaFacade() {
        super(TransferenciaMovPessoa.class);
    }

    public ParecerTransferenciaMovimentoPessoaFacade getParecerTransferenciaMovimentoPessoaFacade() {
        return parecerTransferenciaMovimentoPessoaFacade;
    }

    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }

    public CadastroRuralFacade getCadastroRuralFacade() {
        return cadastroRuralFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }


    @Override
    public TransferenciaMovPessoa recuperar(Object id) {
        TransferenciaMovPessoa transferenciaMovPessoa = super.recuperar(id);
        transferenciaMovPessoa.getBcis().size();
        transferenciaMovPessoa.getBces().size();
        transferenciaMovPessoa.getBcrs().size();
        transferenciaMovPessoa.getPareceresTransferecia().size();
        if (transferenciaMovPessoa.getDetentorArquivoComposicao() != null) {
            transferenciaMovPessoa.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        if (transferenciaMovPessoa.getPessoaOrigem() != null) {
            transferenciaMovPessoa.getPessoaOrigem().getHistoricoSituacoesPessoa().size();
        }
        if (transferenciaMovPessoa.getPessoaDestino() != null) {
            transferenciaMovPessoa.getPessoaDestino().getHistoricoSituacoesPessoa().size();
        }
        return transferenciaMovPessoa;
    }

    public List<TransferenciaMovPessoa> buscarSolicitacaoTransfMoviPessoaAberta(String parte) {
        String sql = " select transf.* from transferenciamovpessoa transf " +
            " inner join pessoa po on po.id = transf.pessoaorigem_id " +
            " left join pessoafisica pfo on pfo.id = po.id " +
            " left join pessoajuridica pjo on pjo.id = po.id " +
            " inner join pessoa pd on pd.id = transf.pessoadestino_id " +
            " left join pessoafisica pfd on pfd.id = pd.id " +
            " left join pessoajuridica pjd on pjd.id = pd.id " +
            "  where (lower(pfo.nome)         like :parteNome or pfo.cpf  like :parteCodigo or replace(pfo.cpf, '.', '') like :parteCodigo " +
            "     or  lower(pfd.nome)         like :parteNome or pfd.cpf  like :parteCodigo or replace(pfd.cpf, '.', '') like :parteCodigo " +
            "     or  lower(pjo.nomefantasia) like :parteNome or pjo.cnpj like :parteCodigo or replace(pjo.cnpj, '.','') like :parteCodigo " +
            "     or  lower(pjd.nomefantasia) like :parteNome or pjd.cnpj like :parteCodigo or replace(pjd.cnpj, '.','') like :parteCodigo " +
            "     or transf.numeroTransferencia = :numeroTransferencia) " +
            "  and transf.situacao = :situacaoAberta";
        Query q = em.createNativeQuery(sql, TransferenciaMovPessoa.class);
        q.setParameter("parteNome", "%" + parte.toLowerCase() + "%");
        q.setParameter("parteCodigo", "%" + parte.toLowerCase());
        q.setParameter("numeroTransferencia", parte.trim());
        q.setParameter("situacaoAberta", SituacaoTransfMovimentoPessoa.ABERTA.name());
        q.setMaxResults(MAX_RESULTADOS_NO_AUTOCOMPLATE);
        List<TransferenciaMovPessoa> lista = q.getResultList();
        if (lista.isEmpty()) {
            return new ArrayList<>();
        }
        for (TransferenciaMovPessoa transferenciaMovPessoa : lista) {
            Hibernate.initialize(transferenciaMovPessoa.getBcis());
            Hibernate.initialize(transferenciaMovPessoa.getBcrs());
            Hibernate.initialize(transferenciaMovPessoa.getBces());
        }

        return lista;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public String montarFiltrosUtilizadosRelatorio(TransferenciaMovPessoa t) {
        StringBuilder sb = new StringBuilder();

        if (t.getPessoaOrigem() != null) {
            sb.append("Pessoa de Origem: " + t.getPessoaOrigem().toString()).append("; ");
        }

        if (t.getPessoaDestino() != null) {
            sb.append("Pessoa de Destino: " + t.getPessoaDestino().toString()).append("; ");
        }

        if (t.getTransfereBcis()) {
            sb.append("Transferir Cadastros Imobiliários: " + (t.getTransfereBcis() ? "Sim" : "Não")).append("; ");
        }

        if (t.getTransfereBces()) {
            sb.append("Transferir Cadastros Econômicos: " + (t.getTransfereBces() ? "Sim" : "Não")).append("; ");
        }

        if (t.getTransfereBcrs()) {
            sb.append("Transferir Cadastros Rurais: " + (t.getTransfereBcrs() ? "Sim" : "Não")).append("; ");
        }

        if (t.getTransfereMovimentosTributario()) {
            sb.append("Transferir Movimentos do Tributário: " + (t.getTransfereMovimentosTributario() ? "Sim" : "Não")).append("; ");
        }

        if (t.getTransfereMovContabeis()) {
            sb.append("Transferir Movimentos do Contábil: " + (t.getTransfereMovContabeis() ? "Sim" : "Não")).append("; ");
        }

        return sb.toString();
    }

    public VOMovimentosTributario recuperarMovimentosDePessoaDoTributario(TransferenciaMovPessoa t) {
        VOMovimentosTributario movimentosTributario = new VOMovimentosTributario();

        if (t.getTransfereMovimentosTributario()) {
            movimentosTributario.setTransmitenteCalculosITBI(recuperarTransmitentesCalculoITBI(t.getPessoaOrigem()));
            movimentosTributario.setAdquirenteCalculosITBI(recuperarAdquirentesCalculoITBI(t.getPessoaOrigem()));
            movimentosTributario.setAssinanteCertidoesDividaAtiva(recuperarCertidoesDividaAtivaAssinante(t.getPessoaOrigem()));
            movimentosTributario.setEmissorCertidoesDividaAtiva(recuperarCertidoesDividaAtivaFuncionarioEmissor(t.getPessoaOrigem()));
            movimentosTributario.setDevedorCertidoesDividaAtiva(recuperarCertidoesDividaAtivaDevedor(t.getPessoaOrigem()));
            movimentosTributario.setContribuinteItensInscricaoDividaAtiva(recuperarItensInscricaoDividaAtivaContribuinte(t.getPessoaOrigem()));
            movimentosTributario.setSolicitanteTransferenciasPermissao(recuperarTransferenciasPermissaoTransporteSolicitante(t.getPessoaOrigem()));
            movimentosTributario.setEnvolvidoProcessosJudiciais(recuperarProcessosJudiciaisEnvolvido(t.getPessoaOrigem()));
            movimentosTributario.setSolicitanteFisicoSolicitacoesDocumentoOficial(recuperarSolicitacoesDeDocumentoOficialPessoaFisica(t.getPessoaOrigem()));
            movimentosTributario.setSolicitanteJuridicoSolicitacoesDocumentoOficial(recuperarSolicitacoesDeDocumentoOficialPessoaJuridica(t.getPessoaOrigem()));
            movimentosTributario.setContribuinteInscricoesDividaAtiva(recuperarInscricoesDividaAtivaContribuinte(t.getPessoaOrigem()));
            movimentosTributario.setFiscalizadoProcessosDeFiscalizacao(recuperarProcessosFiscalizacaoPessoa(t.getPessoaOrigem()));
            movimentosTributario.setLocatarioContratosRendasPatrimoniais(recuperarContratosDeRendasPatrimoniaisLocatario(t.getPessoaOrigem()));
            movimentosTributario.setDevedorProcessosDeParcelamento(recuperarProcessosDeParcelamentosDevedor(t.getPessoaOrigem()));
            movimentosTributario.setProcuradorProcessosParcelamento(recuperarProcessosDeParcelamentosProcurador(t.getPessoaOrigem()));
            movimentosTributario.setFiadorProcessosParcelamento(recuperarProcessosDeParcelamentosFiador(t.getPessoaOrigem()));
            movimentosTributario.setTomadorNotasFiscaisAvulsas(recuperarNotasFiscaisAvulsasTomador(t.getPessoaOrigem()));
            movimentosTributario.setPrestadorNotasFiscaisAvulsas(recuperarNotasFiscaisAvulsasPrestador(t.getPessoaOrigem()));
            movimentosTributario.setAutorRequerenteProcesso(recuperarProcessosSolicitante(t.getPessoaOrigem()));
            movimentosTributario.setRestituinteProcessoDeRestituicao(recuperarProcessosRestituicaoRestituinte(t.getPessoaOrigem()));
            movimentosTributario.setContribuinteCalculosDividaDiversa(recuperarCalculosDeDividaDiversaPessoa(t.getPessoaOrigem()));
            movimentosTributario.setContribuinteCalculosTaxasDiversas(recuperarCalculosDeTaxasDiversaPessoa(t.getPessoaOrigem()));
            movimentosTributario.setPessoaDocumentosOficiais(recuperarDocumentosOficiaisPessoa(t.getPessoaOrigem()));
            movimentosTributario.setPessoaProcessosDeIsencaoDeAcrescimos(recuperarProcessosDeIsencaoDeAcrescimosPessoa(t.getPessoaOrigem()));
            movimentosTributario.setPessoaPagamentosJudiciais(recuperarPagamentosJudiciaisPessoa(t.getPessoaOrigem()));
            movimentosTributario.setPessoaFisicaUsuariosSistema(recuperarUsuariosSistemaPessoa(t.getPessoaOrigem()));
            movimentosTributario.setJuizResponsavelProcessosJudiciais(recuperarTramitesDeProcessosJudiciaisDeJuizResponsavel(t.getPessoaOrigem()));
            movimentosTributario.setMotoristaInfratorAutuacoesFiscalizacaoRBTrans(recuperarAutuacoesFiscalizacaoRBTransMotorista(t.getPessoaOrigem()));
            movimentosTributario.setResultadosParcela(buscarDebitosParaTransferir(t));
        }


        return movimentosTributario;
    }

    private List<CalculoITBI> recuperarTransmitentesCalculoITBI(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder();

        sb.append(" select calculo from CalculoITBI calculo ");
        sb.append(" inner join calculo.transmitentesCalculo transmitenteCalculo ");
        sb.append(" where transmitenteCalculo.pessoa.id = :idPessoa ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idPessoa", pessoa.getId());


        return q.getResultList();
    }

    public List<TransmitentesCalculoITBI> recuperarTransmitentesPessoaCalculoITBI(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder();
        sb.append(" from TransmitentesCalculoITBI where pessoa.id = :idPessoa ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idPessoa", pessoa.getId());

        return q.getResultList();
    }

    private List<CalculoITBI> recuperarAdquirentesCalculoITBI(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder();

        sb.append(" select calculo from CalculoITBI calculo ");
        sb.append(" inner join calculo.adquirentesCalculo adquirenteCalculo ");
        sb.append(" where adquirenteCalculo.adquirente.id = :idPessoa ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idPessoa", pessoa.getId());


        return q.getResultList();
    }

    public List<AdquirentesCalculoITBI> recuperarAdquirentesPessoaCalculoITBI(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder();
        sb.append(" from AdquirentesCalculoITBI where adquirente.id = :idPessoa ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idPessoa", pessoa.getId());

        return q.getResultList();
    }

    private List<CertidaoDividaAtiva> recuperarCertidoesDividaAtivaAssinante(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder("");
        sb.append(" from CertidaoDividaAtiva where funcionarioAssinante.id = :idPessoa ");
        sb.append(" and situacaoCertidaoDA != :situacaoExcluida ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idPessoa", pessoa.getId());
        q.setParameter("situacaoExcluida", SituacaoCertidaoDA.JUNCAO_CDALEGADA);


        return q.getResultList();
    }

    private List<CertidaoDividaAtiva> recuperarCertidoesDividaAtivaFuncionarioEmissor(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder(" from CertidaoDividaAtiva where funcionarioEmissao.id = :idPessoa ");
        sb.append(" and situacaoCertidaoDA != :situacaoExcluida ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idPessoa", pessoa.getId());
        q.setParameter("situacaoExcluida", SituacaoCertidaoDA.JUNCAO_CDALEGADA);


        return q.getResultList();
    }

    private List<CertidaoDividaAtiva> recuperarCertidoesDividaAtivaDevedor(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder(" from CertidaoDividaAtiva where pessoa.id = :idPessoa ");
        sb.append(" and situacaoCertidaoDA != :situacaoExcluida ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idPessoa", pessoa.getId());
        q.setParameter("situacaoExcluida", SituacaoCertidaoDA.JUNCAO_CDALEGADA);


        return q.getResultList();
    }

    private List<ItemInscricaoDividaAtiva> recuperarItensInscricaoDividaAtivaContribuinte(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder(" from ItemInscricaoDividaAtiva where pessoa.id = :idPessoa ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idPessoa", pessoa.getId());

        return q.getResultList();
    }

    private List<TransferenciaPermissaoTransporte> recuperarTransferenciasPermissaoTransporteSolicitante(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder(" from TransferenciaPermissaoTransporte where solicitante.id = :idPessoa ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idPessoa", pessoa.getId());

        return q.getResultList();
    }

    private List<PessoaProcesso> recuperarProcessosJudiciaisEnvolvido(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select envolvido from ProcessoJudicial processo ");
        sb.append(" inner join processo.envolvidos envolvido ");
        sb.append(" where envolvido.pessoa.id = :idPessoa ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idPessoa", pessoa.getId());

        return q.getResultList();
    }

    private List<Propriedade> recuperarPropriedadesProprietario(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder(" from Propriedade where pessoa.id = :idPessoa ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idPessoa", pessoa.getId());

        return q.getResultList();
    }

    private List<SolicitacaoDoctoOficial> recuperarSolicitacoesDeDocumentoOficialPessoaFisica(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder(" from SolicitacaoDoctoOficial where pessoaFisica.id = :idPessoa ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idPessoa", pessoa.getId());

        return q.getResultList();
    }

    private List<SolicitacaoDoctoOficial> recuperarSolicitacoesDeDocumentoOficialPessoaJuridica(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder(" from SolicitacaoDoctoOficial where pessoaJuridica.id = :idPessoa ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idPessoa", pessoa.getId());

        return q.getResultList();
    }

    private List<InscricaoDividaAtiva> recuperarInscricoesDividaAtivaContribuinte(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder(" from InscricaoDividaAtiva where contribuinte.id = :idPessoa ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idPessoa", pessoa.getId());

        return q.getResultList();
    }

    private List<ProcessoFiscalizacao> recuperarProcessosFiscalizacaoPessoa(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder(" from ProcessoFiscalizacao where pessoa.id = :idPessoa ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idPessoa", pessoa.getId());

        return q.getResultList();
    }

    private List<ContratoRendasPatrimoniais> recuperarContratosDeRendasPatrimoniaisLocatario(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder(" from ContratoRendasPatrimoniais where locatario.id = :idPessoa ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idPessoa", pessoa.getId());

        return q.getResultList();
    }

    private List<PropriedadeCartorio> recuperarPropriedadesCartoriosPessoa(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder(" from PropriedadeCartorio where pessoa.id = :idPessoa ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idPessoa", pessoa.getId());

        return q.getResultList();
    }

    private List<ProcessoParcelamento> recuperarProcessosDeParcelamentosDevedor(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder(" from ProcessoParcelamento where pessoa.id = :idPessoa ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idPessoa", pessoa.getId());

        return q.getResultList();
    }

    private List<ProcessoParcelamento> recuperarProcessosDeParcelamentosProcurador(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder(" from ProcessoParcelamento where procurador.id = :idPessoa ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idPessoa", pessoa.getId());

        return q.getResultList();
    }

    private List<ProcessoParcelamento> recuperarProcessosDeParcelamentosFiador(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder(" from ProcessoParcelamento where fiador.id = :idPessoa ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idPessoa", pessoa.getId());

        return q.getResultList();
    }

    private List<NFSAvulsa> recuperarNotasFiscaisAvulsasTomador(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder(" from NFSAvulsa where tomador.id = :idPessoa ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idPessoa", pessoa.getId());

        return q.getResultList();
    }

    private List<NFSAvulsa> recuperarNotasFiscaisAvulsasPrestador(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder(" from NFSAvulsa where prestador.id = :idPessoa ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idPessoa", pessoa.getId());

        return q.getResultList();
    }

    private List<Processo> recuperarProcessosSolicitante(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder(" from Processo where pessoa.id = :idPessoa ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idPessoa", pessoa.getId());

        return q.getResultList();
    }

    private List<ProcessoRestituicao> recuperarProcessosRestituicaoRestituinte(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder(" from ProcessoRestituicao where restituinte.id = :idPessoa ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idPessoa", pessoa.getId());

        return q.getResultList();
    }

    private List<CalculoDividaDiversa> recuperarCalculosDeDividaDiversaPessoa(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder(" from CalculoDividaDiversa where pessoa.id = :idPessoa ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idPessoa", pessoa.getId());

        return q.getResultList();
    }

    private List<CalculoTaxasDiversas> recuperarCalculosDeTaxasDiversaPessoa(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder(" from CalculoTaxasDiversas where contribuinte.id = :idPessoa ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idPessoa", pessoa.getId());

        return q.getResultList();
    }

    private List<DocumentoOficial> recuperarDocumentosOficiaisPessoa(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder(" from DocumentoOficial where pessoa.id = :idPessoa ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idPessoa", pessoa.getId());

        return q.getResultList();
    }

    private List<ProcessoIsencaoAcrescimos> recuperarProcessosDeIsencaoDeAcrescimosPessoa(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder(" from ProcessoIsencaoAcrescimos where pessoa.id = :idPessoa ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idPessoa", pessoa.getId());

        return q.getResultList();
    }

    private List<PagamentoJudicial> recuperarPagamentosJudiciaisPessoa(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder(" from PagamentoJudicial where pessoa.id = :idPessoa ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idPessoa", pessoa.getId());

        return q.getResultList();
    }

    private List<UsuarioSistema> recuperarUsuariosSistemaPessoa(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder(" from UsuarioSistema where pessoaFisica.id = :idPessoa ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idPessoa", pessoa.getId());

        return q.getResultList();
    }

    private List<TramiteProcessoJudicial> recuperarTramitesDeProcessosJudiciaisDeJuizResponsavel(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder(" from TramiteProcessoJudicial where juizResponsavel.id = :idPessoa ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idPessoa", pessoa.getId());

        return q.getResultList();
    }

    private List<AutuacaoFiscalizacaoRBTrans> recuperarAutuacoesFiscalizacaoRBTransMotorista(Pessoa pessoa) {
        StringBuilder sb = new StringBuilder(" from AutuacaoFiscalizacaoRBTrans where motoristaInfrator.id = :idPessoa ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("idPessoa", pessoa.getId());

        return q.getResultList();
    }

    private List<InformacaoBancariaVO> buscarInformacoesBancarias(TransferenciaMovPessoa transferenciaMovPessoa) {
        List<InformacaoBancariaVO> informacaoBancariaVOs = new ArrayList<>();
        if (transferenciaMovPessoa.getTransferirInformacoesBancarias()) {
            StringBuilder sb = new StringBuilder("");
            sb.append(" SELECT  ")
                .append("   b.descricao as banco, ")
                .append("   ag.nomeagencia as agencia, ")
                .append("   ccb.numeroconta as numeroconta, ")
                .append("   ccb.digitoverificador as dv_conta, ")
                .append("   ag.numeroagencia as numeroagencia, ")
                .append("   ag.digitoverificador as dv_agencia ")
                .append(" FROM contacorrentebancaria ccb ")
                .append(" INNER JOIN contacorrentebancpessoa ccbp ON ccbp.contacorrentebancaria_id = ccb.id ")
                .append(" INNER JOIN agencia ag ON ag.id = ccb.agencia_id ")
                .append(" INNER JOIN banco b ON b.id = ag.banco_id ")
                .append(" INNER JOIN pessoa p ON p.id = ccbp.pessoa_id ")
                .append(" WHERE p.id = :pessoa_id ");
            Query q = em.createNativeQuery(sb.toString());
            q.setParameter("pessoa_id", transferenciaMovPessoa.getPessoaOrigem().getId());
            if (!q.getResultList().isEmpty()) {
                for (Object[] obj : (List<Object[]>) q.getResultList()) {
                    InformacaoBancariaVO informacaoBancariaVO = new InformacaoBancariaVO();
                    informacaoBancariaVO.setBanco((String) obj[0]);
                    informacaoBancariaVO.setAgencia((String) obj[1]);
                    informacaoBancariaVO.setNumeroConta((String) obj[2]);
                    informacaoBancariaVO.setDvConta((String) obj[3]);
                    informacaoBancariaVO.setNumeroAgencia((String) obj[4]);
                    informacaoBancariaVO.setDvAgencia((String) obj[5]);
                    informacaoBancariaVOs.add(informacaoBancariaVO);
                }
            }
        }
        return informacaoBancariaVOs;
    }

    private List<TelefoneVO> buscarTelefones(TransferenciaMovPessoa transferenciaMovPessoa) {
        List<TelefoneVO> telefones = new ArrayList<>();
        if (transferenciaMovPessoa.getTransferirTelefones()) {
            StringBuilder sb = new StringBuilder("");
            sb.append(" SELECT  ")
                .append("   t.telefone, ")
                .append("   t.tipofone, ")
                .append("   t.dataregistro, ")
                .append("   CASE t.principal ")
                .append("     WHEN 0    THEN 'Não' ")
                .append("     WHEN 1    THEN 'Sim' ")
                .append("     ELSE 'Não Informado' ")
                .append("   END AS principal ")
                .append(" FROM telefone t ")
                .append(" INNER JOIN pessoa p ON p.id = t.pessoa_id ")
                .append(" WHERE p.id = :pessoa_id ");
            Query q = em.createNativeQuery(sb.toString());
            q.setParameter("pessoa_id", transferenciaMovPessoa.getPessoaOrigem().getId());
            if (!q.getResultList().isEmpty()) {
                for (Object[] obj : (List<Object[]>) q.getResultList()) {
                    TelefoneVO telefoneVO = new TelefoneVO();
                    telefoneVO.setTelefone((String) obj[0]);
                    telefoneVO.setTipoTelefone((String) obj[1]);
                    telefoneVO.setDataRegistro((Date) obj[2]);
                    telefoneVO.setPrincipal((String) obj[3]);
                    telefones.add(telefoneVO);
                }
            }
        }
        return telefones;
    }

    private List<EnderecoVO> buscarEnderecos(TransferenciaMovPessoa transferenciaMovPessoa) {
        List<EnderecoVO> retorno = new ArrayList<>();
        if (transferenciaMovPessoa.getTransferirEnderecos()) {
            StringBuilder sb = new StringBuilder("");
            sb.append(" SELECT  ")
                .append("   ec.bairro, ")
                .append("   ec.cep, ")
                .append("   ec.complemento, ")
                .append("   ec.localidade, ")
                .append("   ec.logradouro, ")
                .append("   ec.numero, ")
                .append("   ec.tipoendereco, ")
                .append("   ec.uf, ")
                .append("   CASE ec.principal ")
                .append("     WHEN 0    THEN 'Não' ")
                .append("     WHEN 1    THEN 'Sim' ")
                .append("     ELSE 'Não Informado' ")
                .append("   END AS principal ")
                .append(" FROM enderecocorreio ec ")
                .append(" INNER JOIN pessoa_enderecocorreio pe on pe.enderecoscorreio_id = ec.id ")
                .append(" INNER JOIN pessoa p ON p.id = pe.pessoa_id ")
                .append(" WHERE p.id = :pessoa_id ");
            Query q = em.createNativeQuery(sb.toString());
            q.setParameter("pessoa_id", transferenciaMovPessoa.getPessoaOrigem().getId());
            if (!q.getResultList().isEmpty()) {
                for (Object[] obj : (List<Object[]>) q.getResultList()) {
                    EnderecoVO enderecoVO = new EnderecoVO();
                    enderecoVO.setBairro((String) obj[0]);
                    enderecoVO.setCep((String) obj[1]);
                    enderecoVO.setComplemento((String) obj[2]);
                    enderecoVO.setLocalidade((String) obj[3]);
                    enderecoVO.setLogradouro((String) obj[4]);
                    enderecoVO.setNumero((String) obj[5]);
                    enderecoVO.setTipoEndereco(obj[6] != null ? TipoEndereco.valueOf((String) obj[6]).getDescricao() : null);
                    enderecoVO.setUf((String) obj[7]);
                    enderecoVO.setPrincipal((String) obj[8]);
                    retorno.add(enderecoVO);
                }
            }
        }
        return retorno;
    }

    private List<DependenteVO> buscarDependentesPorDependente(TransferenciaMovPessoa transferenciaMovPessoa) {
        List<DependenteVO> dependentes = new ArrayList<>();
        if (transferenciaMovPessoa.getTransferirDependentes()) {
            StringBuilder sb = new StringBuilder("");
            sb.append(" SELECT  ")
                .append("   grau.descricao, ")
                .append("   formatacpfcnpj(pf.cpf) || ' -  ' || pf.nome as pessoaFisica ")
                .append(" FROM dependente dep ")
                .append(" INNER JOIN pessoafisica pf ON pf.id = dep.responsavel_id ")
                .append(" INNER JOIN GrauDeParentesco grau ON grau.id = dep.graudeparentesco_id ")
                .append(" WHERE dep.dependente_id = :pessoa_id ");
            Query q = em.createNativeQuery(sb.toString());
            q.setParameter("pessoa_id", transferenciaMovPessoa.getPessoaOrigem().getId());
            List<Object[]> resultList = q.getResultList();
            if (resultList != null && !resultList.isEmpty()) {
                for (Object[] obj : resultList) {
                    DependenteVO dependenteVO = new DependenteVO();
                    dependenteVO.setGrauParentesco((String) obj[0]);
                    dependenteVO.setResponsavel((String) obj[1]);
                    dependentes.add(dependenteVO);
                }
            }
        }
        return dependentes;
    }

    private List<DependenteVO> buscarDependentesPorResponsavel(TransferenciaMovPessoa transferenciaMovPessoa) {
        List<DependenteVO> dependentes =  Lists.newArrayList();
        if (transferenciaMovPessoa.getTransferirRespDependentes()) {
            StringBuilder sb = new StringBuilder("");
                sb.append(" select grau.descricao, ")
                .append("   formatacpfcnpj(pf.cpf) || ' -  ' || pf.nome as pessoaFisica ")
                .append(" FROM dependente dep ")
                .append(" INNER JOIN pessoafisica pf ON pf.id = dep.DEPENDENTE_ID ")
                .append(" INNER JOIN GrauDeParentesco grau ON grau.id = dep.graudeparentesco_id ")
                .append(" WHERE dep.RESPONSAVEL_ID = :pessoa_id ");
            Query q = em.createNativeQuery(sb.toString());
            q.setParameter("pessoa_id", transferenciaMovPessoa.getPessoaOrigem().getId());
            List<Object[]> resultList = q.getResultList();
            if (resultList != null && !resultList.isEmpty()) {
                for (Object[] obj : resultList) {
                    DependenteVO dependenteVO = new DependenteVO();
                    dependenteVO.setGrauParentesco((String) obj[0]);
                    dependenteVO.setPessoaDependente((String) obj[1]);
                    dependentes.add(dependenteVO);
                }
            }
        }
        return dependentes;
    }

    private List<PensaoAlimenticiaVO> buscarPensoesAlimenticiasPorBeneficiario(TransferenciaMovPessoa transferenciaMovPessoa) {
        List<PensaoAlimenticiaVO> pensoes = new ArrayList<>();
        if (transferenciaMovPessoa.getTransferirPensoesAlimenticias()) {
            StringBuilder sb = new StringBuilder("");
            sb.append(" select ben.tipovalorpensaoalimenticia, ")
                .append("       eve.codigo || ' - ' || eve.descricao as verba, ")
                .append("       formatacpfcnpj(pf.cpf) || ' - ' || pf.nome as instituidor, ")
                .append("       ben.iniciovigencia, ")
                .append("       ben.finalvigencia ")
                .append("  from PensaoAlimenticia pa ")
                .append(" inner join vinculofp vinc on pa.vinculofp_id = vinc.id ")
                .append(" inner join matriculaFp mat on vinc.matriculafp_id = mat.id ")
                .append(" inner join pessoafisica pf on mat.pessoa_id = pf.id ")
                .append(" inner join BeneficioPensaoAlimenticia ben on pa.id = ben.pensaoalimenticia_id ")
                .append(" inner join eventoFp eve on ben.eventofp_id = eve.id ")
                .append(" where ben.beneficiario_id = :pessoa_id ");
            Query q = em.createNativeQuery(sb.toString());
            q.setParameter("pessoa_id", transferenciaMovPessoa.getPessoaOrigem().getId());
            List<Object[]> resultList = q.getResultList();
            if (resultList != null && !resultList.isEmpty()) {
                for (Object[] obj : resultList) {
                    PensaoAlimenticiaVO pensaoAlimenticiaVO = new PensaoAlimenticiaVO();
                    pensaoAlimenticiaVO.setTipoValorPensaoAlimenticia(TipoValorPensaoAlimenticia.valueOf((String) obj[0]).getDescricao());
                    pensaoAlimenticiaVO.setVerba((String) obj[1]);
                    pensaoAlimenticiaVO.setInstituidor((String) obj[2]);
                    pensaoAlimenticiaVO.setInicioVigencia((Date) obj[3]);
                    pensaoAlimenticiaVO.setFimVigencia((Date) obj[4]);
                    pensoes.add(pensaoAlimenticiaVO);
                }
            }
        }
        return pensoes;
    }

    private List<PensaoAlimenticiaVO> buscarPensoesAlimenticiasPorResponsavel(TransferenciaMovPessoa transferenciaMovPessoa) {
        List<PensaoAlimenticiaVO> pensoes = new ArrayList<>();
        if (transferenciaMovPessoa.getTransferirRespPensoesAliment()) {
            String sql = " select ben.tipovalorpensaoalimenticia, " +
                "       eve.codigo || ' - ' || eve.descricao       as verba, " +
                "       formatacpfcnpj(pf.cpf) || ' - ' || pf.nome as instituidor, " +
                "       ben.iniciovigencia, " +
                "       ben.finalvigencia, " +
                "       formatacpfcnpj(pfBeneficiario.cpf) || ' - ' || pfBeneficiario.NOME beneficiario " +
                " from PensaoAlimenticia pa " +
                "         inner join vinculofp vinc on pa.vinculofp_id = vinc.id " +
                "         inner join matriculaFp mat on vinc.matriculafp_id = mat.id " +
                "         inner join pessoafisica pf on mat.pessoa_id = pf.id " +
                "         inner join BeneficioPensaoAlimenticia ben on pa.id = ben.pensaoalimenticia_id " +
                "         inner join PESSOAFISICA pfBeneficiario on ben.BENEFICIARIO_ID = pfBeneficiario.ID " +
                "         inner join eventoFp eve on ben.eventofp_id = eve.id " +
                " where ben.RESPONSAVEL_ID = :pessoa_id ";
            Query q = em.createNativeQuery(sql);
            q.setParameter("pessoa_id", transferenciaMovPessoa.getPessoaOrigem().getId());
            List<Object[]> resultList = q.getResultList();
            if (resultList != null && !resultList.isEmpty()) {
                for (Object[] obj : resultList) {
                    PensaoAlimenticiaVO pensaoAlimenticiaVO = new PensaoAlimenticiaVO();
                    pensaoAlimenticiaVO.setTipoValorPensaoAlimenticia(TipoValorPensaoAlimenticia.valueOf((String) obj[0]).getDescricao());
                    pensaoAlimenticiaVO.setVerba((String) obj[1]);
                    pensaoAlimenticiaVO.setInstituidor((String) obj[2]);
                    pensaoAlimenticiaVO.setInicioVigencia((Date) obj[3]);
                    pensaoAlimenticiaVO.setFimVigencia((Date) obj[4]);
                    pensaoAlimenticiaVO.setBeneficiario((String) obj[5]);
                    pensoes.add(pensaoAlimenticiaVO);
                }
            }
        }
        return pensoes;
    }

    private List<CadastroRHVO> buscarMatriculas(TransferenciaMovPessoa transferenciaMovPessoa) {
        List<CadastroRHVO> cadastros = Lists.newArrayList();
        if (transferenciaMovPessoa.getTransferirCadastrosRH()) {
            String sql =" select mat.MATRICULA," +
                " mat.PESSOA_ID " +
                " from MATRICULAFP mat " +
                " where mat.PESSOA_ID = :pessoa_id ";
            Query q = em.createNativeQuery(sql);
            q.setParameter("pessoa_id", transferenciaMovPessoa.getPessoaOrigem().getId());

            List<Object[]> resultList = q.getResultList();
            if (resultList != null && !resultList.isEmpty()) {
                for (Object[] obj : resultList) {
                    CadastroRHVO cadastroRH = new CadastroRHVO();
                    cadastroRH.setMatricula((String) obj[0]);
                    cadastros.add(cadastroRH);
                }
            }
        }
        return cadastros;
    }

    private List<CadastroRHVO> buscarPrestadoresDeServico(TransferenciaMovPessoa transferenciaMovPessoa) {
        List<CadastroRHVO> cadastros = Lists.newArrayList();
        if (transferenciaMovPessoa.getTransferirCadastrosRH()) {
            String sql =" select prestador.CODIGO codigo, " +
                "       cat.CODIGO || ' - ' || cat.DESCRICAO categoriaTrabalhador " +
                "       from PRESTADORSERVICOS prestador " +
                "       inner join CATEGORIATRABALHADOR cat on prestador.CATEGORIATRABALHADOR_ID = cat.ID " +
                " where prestador.PRESTADOR_ID = :pessoa_id ";
            Query q = em.createNativeQuery(sql);
            q.setParameter("pessoa_id", transferenciaMovPessoa.getPessoaOrigem().getId());
            List<Object[]> resultList = q.getResultList();
            if (resultList != null && !resultList.isEmpty()) {
                for (Object[] obj : resultList) {
                    CadastroRHVO cadastroRH = new CadastroRHVO();
                    cadastroRH.setCodigoPrestadorServico(((BigDecimal) obj[0]).toString());
                    cadastroRH.setCategoriaTrabalhador((String) obj[1]);
                    cadastros.add(cadastroRH);
                }
            }
        }
        return cadastros;
    }

    private List<DocumentosPessoaisVO> buscarDocumentos(TransferenciaMovPessoa transferenciaMovPessoa) {
        DocumentosPessoaisVO documento = new DocumentosPessoaisVO();
        List<DocumentosPessoaisVO> documentos = Lists.newArrayList();
        if (transferenciaMovPessoa.getTransferirDocumentosPessoais()) {
            documento.setCarteirasDeTrabalho(buscarCarteirasDeTrabalho(transferenciaMovPessoa));
            documento.setCarteirasDeVacinacao(buscarCarteirasDeVacinacao(transferenciaMovPessoa));
            documento.setCertidoesDeCasamento(buscarCertidoesDeCasamento(transferenciaMovPessoa));
            documento.setCertidoesDeNascimento(buscarCertidoesDeNascimento(transferenciaMovPessoa));
            documento.setHabilitacoes(buscarHabilitacoes(transferenciaMovPessoa));
            documento.setRgVOs(buscarRgs(transferenciaMovPessoa));
            documento.setSituacoesMilitares(buscarSituacoesMilitares(transferenciaMovPessoa));
            documento.setTitulosEleitoral(buscarTitulosEleitorais(transferenciaMovPessoa));
            if (documento.hasRegistros()) {
                documentos.add(documento);
            }
        }
        return documentos;
    }

    private List<TituloEleitorVO> buscarTitulosEleitorais(TransferenciaMovPessoa transferenciaMovPessoa) {
        List<TituloEleitorVO> tituloEleitorVOs = new ArrayList<>();
        StringBuilder sb = new StringBuilder("");
        sb.append(" SELECT  ")
            .append("   te.dataemissao, ")
            .append("   te.numero, ")
            .append("   te.sessao, ")
            .append("   te.zona, ")
            .append("   cidade.nome ")
            .append(" FROM documentopessoal doc ")
            .append(" INNER JOIN tituloeleitor te ")
            .append(" ON te.id = doc.id ")
            .append(" INNER JOIN cidade ")
            .append(" ON cidade.id              = te.cidade_id ")
            .append(" WHERE doc.pessoafisica_id = :pessoa_id ");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("pessoa_id", transferenciaMovPessoa.getPessoaOrigem().getId());
        if (!q.getResultList().isEmpty()) {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                TituloEleitorVO tituloEleitorVO = new TituloEleitorVO();
                tituloEleitorVO.setDataEmissao((Date) obj[0]);
                tituloEleitorVO.setNumero((String) obj[1]);
                tituloEleitorVO.setSessao((String) obj[2]);
                tituloEleitorVO.setZona((String) obj[3]);
                tituloEleitorVO.setCidade((String) obj[4]);
                tituloEleitorVOs.add(tituloEleitorVO);
            }
        }
        return tituloEleitorVOs;
    }

    private List<SituacaoMilitarVO> buscarSituacoesMilitares(TransferenciaMovPessoa transferenciaMovPessoa) {
        List<SituacaoMilitarVO> situacaoMilitarVOs = new ArrayList<>();
        StringBuilder sb = new StringBuilder("");
        sb.append(" SELECT  ")
            .append("   sm.categoriacertificadomilitar, ")
            .append("   sm.certificadomilitar, ")
            .append("   sm.seriecertificadomilitar, ")
            .append("   sm.tiposituacaomilitar, ")
            .append("   sm.dataemissao, ")
            .append("   sm.orgaoemissao ")
            .append(" FROM documentopessoal doc ")
            .append(" INNER JOIN situacaomilitar sm ON sm.id = doc.id ")
            .append(" WHERE doc.pessoafisica_id = :pessoa_id ");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("pessoa_id", transferenciaMovPessoa.getPessoaOrigem().getId());
        if (!q.getResultList().isEmpty()) {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                SituacaoMilitarVO situacaoMilitarVO = new SituacaoMilitarVO();
                situacaoMilitarVO.setCategoriaCertificadoMilitar((String) obj[0]);
                situacaoMilitarVO.setCertificadoMilitar((String) obj[1]);
                situacaoMilitarVO.setSerieCertificadoMilitar((String) obj[2]);
                situacaoMilitarVO.setTipoSituacaoMilitar((String) obj[3]);
                situacaoMilitarVO.setDataEmissao((Date) obj[4]);
                situacaoMilitarVO.setOrgaoEmissao((String) obj[5]);
                situacaoMilitarVOs.add(situacaoMilitarVO);
            }
        }
        return situacaoMilitarVOs;
    }

    private List<RgVO> buscarRgs(TransferenciaMovPessoa transferenciaMovPessoa) {
        List<RgVO> rgVOs = new ArrayList<>();
        StringBuilder sb = new StringBuilder("");
        sb.append(" SELECT ")
            .append("   rg.dataemissao, ")
            .append("   rg.numero, ")
            .append("   rg.orgaoemissao, ")
            .append("   uf.uf ")
            .append(" FROM documentopessoal doc ")
            .append(" INNER JOIN rg ON rg.id = doc.id ")
            .append(" INNER JOIN uf ON uf.id = rg.uf_id ")
            .append(" WHERE doc.pessoafisica_id = :pessoa_id ");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("pessoa_id", transferenciaMovPessoa.getPessoaOrigem().getId());
        if (!q.getResultList().isEmpty()) {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                RgVO rgVO = new RgVO();
                rgVO.setDataemissao((Date) obj[0]);
                rgVO.setNumero((String) obj[1]);
                rgVO.setOrgaoEmissao((String) obj[2]);
                rgVO.setUf((String) obj[3]);
                rgVOs.add(rgVO);
            }
        }
        return rgVOs;
    }

    private List<HabilitacaoVO> buscarHabilitacoes(TransferenciaMovPessoa transferenciaMovPessoa) {
        List<HabilitacaoVO> habilitacoes = new ArrayList<>();
        StringBuilder sb = new StringBuilder("");
        sb.append(" SELECT  ")
            .append("   habilitacao.categoria, ")
            .append("   habilitacao.numero, ")
            .append("   habilitacao.validade, ")
            .append("   habilitacao.dataemissao, ")
            .append("   habilitacao.orgaoexpedidor ")
            .append(" FROM documentopessoal doc ")
            .append(" INNER JOIN habilitacao ")
            .append(" ON habilitacao.id         = doc.id ")
            .append(" WHERE doc.pessoafisica_id = :pessoa_id ");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("pessoa_id", transferenciaMovPessoa.getPessoaOrigem().getId());
        if (!q.getResultList().isEmpty()) {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                HabilitacaoVO habilitacaoVO = new HabilitacaoVO();
                habilitacaoVO.setCategoria((String) obj[0]);
                habilitacaoVO.setNumero((String) obj[1]);
                habilitacaoVO.setValidade((Date) obj[2]);
                habilitacaoVO.setDataEmissao((Date) obj[3]);
                habilitacaoVO.setOrgaoExpedidor((String) obj[4]);
                habilitacoes.add(habilitacaoVO);
            }
        }
        return habilitacoes;
    }

    private List<CertidaoNascimentoVO> buscarCertidoesDeNascimento(TransferenciaMovPessoa transferenciaMovPessoa) {
        List<CertidaoNascimentoVO> certidoes = new ArrayList<>();
        StringBuilder sb = new StringBuilder("");
        sb.append(" SELECT ")
            .append("   certidao.nomecartorio, ")
            .append("   certidao.numerolivro, ")
            .append("   certidao.numeroregistro, ")
            .append("   certidao.numerofolha, ")
            .append("   cidade.nome ")
            .append(" FROM documentopessoal doc ")
            .append(" INNER JOIN certidaonascimento certidao ON certidao.id = doc.id ")
            .append(" INNER JOIN cidade ON cidade.id = certidao.cidade_id ")
            .append(" WHERE doc.pessoafisica_id = :pessoa_id");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("pessoa_id", transferenciaMovPessoa.getPessoaOrigem().getId());
        if (!q.getResultList().isEmpty()) {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                CertidaoNascimentoVO certidaoNascimentoVO = new CertidaoNascimentoVO();
                certidaoNascimentoVO.setNomeCartorio((String) obj[0]);
                certidaoNascimentoVO.setNumeroLivro((String) obj[1]);
                certidaoNascimentoVO.setNumeroRegistro((Number) obj[2]);
                certidaoNascimentoVO.setNumeroFolha((String) obj[3]);
                certidaoNascimentoVO.setCidade((String) obj[4]);
                certidoes.add(certidaoNascimentoVO);
            }
        }
        return certidoes;
    }

    private List<CertidaoCasamentoVO> buscarCertidoesDeCasamento(TransferenciaMovPessoa transferenciaMovPessoa) {
        List<CertidaoCasamentoVO> certidoes = new ArrayList<>();
        StringBuilder sb = new StringBuilder("");
        sb.append(" SELECT  ")
            .append("  certidao.nomeconjuge, ")
            .append("  certidao.nomecartorio, ")
            .append("  certidao.numerolivro, ")
            .append("  certidao.numerofolha, ")
            .append("  certidao.numeroregistro, ")
            .append("  nac.descricao, ")
            .append("  certidao.estado, ")
            .append("  certidao.localtrabalhoconjuge, ")
            .append("  certidao.datacasamento, ")
            .append("  certidao.datanascimentoconjuge, ")
            .append("  (SELECT cartorio.NOME ")
            .append("    FROM cidade cartorio ")
            .append("    INNER JOIN certidaocasamento cert ON cert.cidadecartorio_id = cartorio.id ")
            .append("    WHERE cert.id             = certidao.id ")
            .append("  ) AS CIDADECARTORIO, ")
            .append("  (SELECT naturalidade.NOME ")
            .append("    FROM cidade naturalidade ")
            .append("    INNER JOIN certidaocasamento c ON c.cidadecartorio_id = naturalidade.id ")
            .append("    WHERE c.id             = certidao.id ")
            .append("  ) AS NATURALIDADECONJUGE ")
            .append(" FROM documentopessoal doc ")
            .append(" INNER JOIN certidaocasamento certidao ON certidao.id = doc.id ")
            .append(" INNER JOIN nacionalidade nac ON nac.id = certidao.nacionalidade_id ")
            .append(" WHERE doc.pessoafisica_id = :pessoa_id");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("pessoa_id", transferenciaMovPessoa.getPessoaOrigem().getId());
        if (!q.getResultList().isEmpty()) {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                CertidaoCasamentoVO certidaoCasamentoVO = new CertidaoCasamentoVO();
                certidaoCasamentoVO.setNomeConjuge((String) obj[0]);
                certidaoCasamentoVO.setNomeCartorio((String) obj[1]);
                certidaoCasamentoVO.setNumeroLivro((String) obj[2]);
                certidaoCasamentoVO.setNumeroFolha((Number) obj[3]);
                certidaoCasamentoVO.setNumeroRegistro((Number) obj[4]);
                certidaoCasamentoVO.setNacionalidade((String) obj[5]);
                certidaoCasamentoVO.setEstado((String) obj[6]);
                certidaoCasamentoVO.setLocalTrabalhoConjuge((String) obj[7]);
                certidaoCasamentoVO.setDataCasamento((Date) obj[8]);
                certidaoCasamentoVO.setDataNascimentoConjuge((Date) obj[9]);
                certidaoCasamentoVO.setCidadeCartorio((String) obj[10]);
                certidaoCasamentoVO.setNaturalidadeConjuge((String) obj[11]);
                certidoes.add(certidaoCasamentoVO);
            }
        }
        return certidoes;
    }

    private List<CarteiraVacinacaoVO> buscarCarteirasDeVacinacao(TransferenciaMovPessoa transferenciaMovPessoa) {
        List<CarteiraVacinacaoVO> carteiras = new ArrayList<>();
        StringBuilder sb = new StringBuilder("");
        sb.append(" SELECT carteira.numero ")
            .append(" FROM documentopessoal doc ")
            .append(" INNER JOIN carteiravacinacao carteira ON carteira.id = doc.id ")
            .append(" WHERE doc.pessoafisica_id = :pessoa_id");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("pessoa_id", transferenciaMovPessoa.getPessoaOrigem().getId());
        if (!q.getResultList().isEmpty()) {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                CarteiraVacinacaoVO carteiraVacinacaoVO = new CarteiraVacinacaoVO();
                if (obj != null) {
                    carteiraVacinacaoVO.setNumero((String) obj[0]);
                    carteiras.add(carteiraVacinacaoVO);
                }
            }
        }
        return carteiras;
    }

    private List<CarteiraTrabalhoVO> buscarCarteirasDeTrabalho(TransferenciaMovPessoa transferenciaMovPessoa) {
        List<CarteiraTrabalhoVO> carteiras = new ArrayList<>();
        StringBuilder sb = new StringBuilder("");
        sb.append(" SELECT ")
            .append(" carteira.dataemissao, ")
            .append(" carteira.numero, ")
            .append(" carteira.pispasep, ")
            .append(" carteira.serie, ")
            .append(" uf.uf, ")
            .append(" carteira.orgaoexpedidor, ")
            .append(" coalesce(b.numerobanco, '') as numerobanco, ")
            .append(" coalesce(b.digitoverificador, '') as dvbanco, ")
            .append(" carteira.dataemissaopispasep, ")
            .append(" carteira.anoprimeiroemprego")
            .append(" FROM documentopessoal doc")
            .append(" INNER JOIN carteiratrabalho carteira ON carteira.id = doc.id")
            .append(" INNER JOIN uf ON uf.id = carteira.uf_id")
            .append(" LEFT JOIN banco b on b.id = carteira.bancopispasep_id")
            .append(" WHERE doc.pessoafisica_id = :pessoa_id");
        Query q = em.createNativeQuery(sb.toString());
        q.setParameter("pessoa_id", transferenciaMovPessoa.getPessoaOrigem().getId());
        if (!q.getResultList().isEmpty()) {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                CarteiraTrabalhoVO carteiraTrabalhoVO = new CarteiraTrabalhoVO();
                carteiraTrabalhoVO.setDataEmissao((Date) obj[0]);
                carteiraTrabalhoVO.setNumero((String) obj[1]);
                carteiraTrabalhoVO.setPisPasep((String) obj[2]);
                carteiraTrabalhoVO.setSerie((String) obj[3]);
                carteiraTrabalhoVO.setUf((String) obj[4]);
                carteiraTrabalhoVO.setOrgaoExpedidor((String) obj[5]);
                carteiraTrabalhoVO.setBancoPisPasep(obj[6] + (obj[7] != null ? "-" + obj[7] : ""));
                carteiraTrabalhoVO.setDataEmissaoPisPasep((Date) obj[8]);
                carteiraTrabalhoVO.setAnoPrimeiroEmprego((Number) obj[9]);
                carteiras.add(carteiraTrabalhoVO);
            }
        }
        return carteiras;
    }

    private List<TransferenciaMovPessoaContabil> recuperarMovimentosContabeis(TransferenciaMovPessoa transferenciaMovPessoa) {
        List<TransferenciaMovPessoaContabil> toReturn = new ArrayList<>();
        if (transferenciaMovPessoa.getTransfereMovContabeis()) {
            StringBuilder sb = new StringBuilder("");
            sb.append(" select tipoMovimento, ");
            sb.append("        dataMovimento, ");
            sb.append("        numeroMovimento, ");
            sb.append("        coalesce(historico, '') as historico, ");
            sb.append("        valor ");
            sb.append("  from ( ");
            sb.append(" select 'Empenho' as tipoMovimento, ");
            sb.append("        1 as ordem, ");
            sb.append("        cast(emp.dataempenho as Date) as datamovimento, ");
            sb.append("        cast(emp.numero as number) as numeromovimento, ");
            sb.append("        emp.complementoHistorico as historico, ");
            sb.append("        emp.valor as valor ");
            sb.append("   from empenho emp ");
            sb.append("  where emp.fornecedor_id = :idPessoa ");
            sb.append("  union all ");
            sb.append(" select 'Estorno de Empenho' as tipoMovimento, ");
            sb.append("        2 as ordem, ");
            sb.append("        cast(est.dataestorno as Date) as datamovimento, ");
            sb.append("        cast(est.numero as number) as numeromovimento, ");
            sb.append("        est.historicorazao as historico, ");
            sb.append("        est.valor as valor ");
            sb.append("   from empenhoestorno est ");
            sb.append("  inner join empenho emp on est.empenho_id = emp.id ");
            sb.append("  where emp.fornecedor_id = :idPessoa ");
            sb.append("  union all ");
            sb.append(" select 'Liquidação' as tipoMovimento, ");
            sb.append("        3 as ordem, ");
            sb.append("        cast(liq.dataliquidacao as Date) as datamovimento, ");
            sb.append("        cast(liq.numero as number) as numeromovimento, ");
            sb.append("        liq.complemento as historico, ");
            sb.append("        liq.valor as valor ");
            sb.append("   from liquidacao liq ");
            sb.append("  inner join empenho emp on liq.empenho_id = emp.id ");
            sb.append("  where emp.fornecedor_id = :idPessoa ");
            sb.append("  union all ");
            sb.append(" select 'Estorno de Liquidação' as tipoMovimento, ");
            sb.append("        4 as ordem, ");
            sb.append("        cast(est.dataestorno as Date) as datamovimento, ");
            sb.append("        cast(est.numero as number) as numeromovimento, ");
            sb.append("        est.historicorazao as historico, ");
            sb.append("        est.valor as valor ");
            sb.append("   from liquidacaoestorno est ");
            sb.append("  inner join liquidacao liq on est.liquidacao_id = liq.id ");
            sb.append("  inner join empenho emp on liq.empenho_id = emp.id ");
            sb.append("  where emp.fornecedor_id = :idPessoa ");
            sb.append("  union all ");
            sb.append(" select 'Pagamento' as tipoMovimento, ");
            sb.append("        5 as ordem, ");
            sb.append("        cast(pag.datapagamento as Date) as datamovimento, ");
            sb.append("        cast(pag.numeropagamento as number) as numeromovimento, ");
            sb.append("        pag.historicorazao as historico, ");
            sb.append("        pag.valor as valor ");
            sb.append("   from pagamento pag ");
            sb.append("  inner join liquidacao liq on pag.liquidacao_id = liq.id ");
            sb.append("  inner join empenho emp on liq.empenho_id = emp.id ");
            sb.append("  where emp.fornecedor_id = :idPessoa ");
            sb.append("  union all ");
            sb.append(" select 'Estorno de Pagamento' as tipoMovimento, ");
            sb.append("        6 as ordem, ");
            sb.append("        cast(est.dataestorno as Date) as datamovimento, ");
            sb.append("        cast(est.numero as number) as numeromovimento, ");
            sb.append("        est.historicorazao as historico, ");
            sb.append("        est.valor as valor ");
            sb.append("   from pagamentoestorno est ");
            sb.append("  inner join pagamento pag on est.pagamento_id = pag.id ");
            sb.append("  inner join liquidacao liq on pag.liquidacao_id = liq.id ");
            sb.append("  inner join empenho emp on liq.empenho_id = emp.id ");
            sb.append("  where emp.fornecedor_id = :idPessoa ");
            sb.append("  union all ");
            sb.append(" select 'Pagamento Extraorçamentário' as tipoMovimento, ");
            sb.append("        7 as ordem, ");
            sb.append("        cast(pagext.datapagto as Date) as datamovimento, ");
            sb.append("        cast(pagext.numero as number) as numeromovimento, ");
            sb.append("        pagext.historicorazao as historico, ");
            sb.append("        pagext.valor as valor ");
            sb.append("   from pagamentoextra pagext ");
            sb.append("  where pagext.fornecedor_id = :idPessoa ");
            sb.append("  union all ");
            sb.append(" select 'Estorno de Pagamento Extraorçamentário' as tipoMovimento, ");
            sb.append("        8 as ordem, ");
            sb.append("        cast(est.dataestorno as Date) as datamovimento, ");
            sb.append("        cast(est.numero as number) as numeromovimento, ");
            sb.append("        est.historicorazao as historico, ");
            sb.append("        est.valor as valor ");
            sb.append("   from pagamentoextraestorno est ");
            sb.append("  inner join pagamentoextra pagext on est.pagamentoextra_id = pagext.id ");
            sb.append("  where pagext.fornecedor_id = :idPessoa ");
            sb.append("  union all ");
            sb.append(" select 'Receita Extraorçamentária' as tipoMovimento, ");
            sb.append("        9 as ordem, ");
            sb.append("        cast(recext.datareceita as Date) as datamovimento, ");
            sb.append("        cast(recext.numero as number) as numeromovimento, ");
            sb.append("        recext.historicorazao as historico, ");
            sb.append("        recext.valor as valor ");
            sb.append("   from receitaextra recext ");
            sb.append("  where recext.pessoa_id = :idPessoa ");
            sb.append("  union all ");
            sb.append(" select 'Estorno de Receita Extraorçamentária' as tipoMovimento, ");
            sb.append("        10 as ordem, ");
            sb.append("        cast(est.dataestorno as Date) as datamovimento, ");
            sb.append("        cast(est.numero as number) as numeromovimento, ");
            sb.append("        est.historicorazao as historico, ");
            sb.append("        est.valor as valor ");
            sb.append("   from receitaextraestorno est ");
            sb.append("  inner join receitaextra recext on est.receitaextra_id = recext.id ");
            sb.append("  where recext.pessoa_id = :idPessoa ");
            sb.append("  union all ");
            sb.append(" select 'Movimento de Dívida Pública' as tipoMovimento, ");
            sb.append("        11 as ordem, ");
            sb.append("        cast(mov.data as Date) as datamovimento, ");
            sb.append("        cast(mov.numero as number) as numeromovimento, ");
            sb.append("        mov.historico as historico, ");
            sb.append("        mov.valor as valor ");
            sb.append("   from movimentodividapublica mov ");
            sb.append("  inner join dividapublica div on mov.dividapublica_id = div.id ");
            sb.append("  where div.pessoa_id = :idPessoa ");
            sb.append("  union all ");
            sb.append(" select 'Estorno de Movimento de Dívida Pública' as tipoMovimento, ");
            sb.append("        12 as ordem, ");
            sb.append("        cast(est.data as Date) as datamovimento, ");
            sb.append("        cast(est.numero as number) as numeromovimento, ");
            sb.append("        est.historico as historico, ");
            sb.append("        est.valor as valor ");
            sb.append("   from estornomovidividapublica est ");
            sb.append("  inner join movimentodividapublica mov on est.movimentodividapublica_id = mov.id ");
            sb.append("  inner join dividapublica div on mov.dividapublica_id = div.id ");
            sb.append("  where div.pessoa_id = :idPessoa ");
            sb.append("  union all ");
            sb.append(" select case lanc.tipooperacao ");
            sb.append("          when 'NORMAL' then 'Receita Orçamentária' else 'Estorno de Receita Orçamentária' ");
            sb.append("        end as tipoMovimento, ");
            sb.append("        case lanc.tipooperacao ");
            sb.append("          when 'NORMAL' then 13 else 14 ");
            sb.append("        end as ordem, ");
            sb.append("        cast(lanc.datalancamento as Date) as datamovimento, ");
            sb.append("        cast(lanc.numero as number) as numeromovimento, ");
            sb.append("        lanc.complemento as historico, ");
            sb.append("        lanc.valor as valor ");
            sb.append("   from lancamentoreceitaorc lanc ");
            sb.append("  where lanc.pessoa_id = :idPessoa ");
            sb.append("  union all ");
            sb.append(" select case ajd.tipolancamento ");
            sb.append("          when 'NORMAL' then 'Ajuste em Depósito' else 'Estorno de Ajuste em Depósito' ");
            sb.append("        end as tipoMovimento, ");
            sb.append("        case ajd.tipolancamento ");
            sb.append("          when 'NORMAL' then 15 else 16 ");
            sb.append("        end as ordem, ");
            sb.append("        cast(ajd.dataajuste as Date) as datamovimento, ");
            sb.append("        cast(ajd.numero as number) as numeromovimento, ");
            sb.append("        ajd.historicorazao as historico, ");
            sb.append("        ajd.valor as valor ");
            sb.append("   from ajustedeposito ajd ");
            sb.append("  where ajd.pessoa_id = :idPessoa ");
            sb.append("  union all ");
            sb.append(" select case ajad.tipolancamento ");
            sb.append("          when 'NORMAL' then 'Ajuste Ativo Disponível' else 'Estorno de Ajuste Ativo Disponível' ");
            sb.append("        end as tipoMovimento, ");
            sb.append("        case ajad.tipolancamento ");
            sb.append("          when 'NORMAL' then 17 else 18 ");
            sb.append("        end as ordem, ");
            sb.append("        cast(ajad.dataajuste as Date) as datamovimento, ");
            sb.append("        cast(ajad.numero as number) as numeromovimento, ");
            sb.append("        ajad.historicorazao as historico, ");
            sb.append("        ajad.valor as valor ");
            sb.append("   from ajusteativodisponivel ajad ");
            sb.append("  where ajad.pessoa_id = :idPessoa ");
            sb.append("  union all ");
            sb.append(" select 'Proposta de Concessão de Diária' as tipoMovimento, ");
            sb.append("        19 as ordem, ");
            sb.append("        cast(prop.datalancamento as Date) as datamovimento, ");
            sb.append("        cast(prop.codigo as number) as numeromovimento, ");
            sb.append("        prop.objetivo as historico, ");
            sb.append("        prop.valor as valor ");
            sb.append("   from propostaconcessaodiaria prop ");
            sb.append("  where prop.pessoafisica_id = :idPessoa ");
            sb.append("  union all ");
            sb.append(" select case div.tipolancamento ");
            sb.append("          when 'NORMAL' then 'Dívida Ativa Contábil' else 'Estorno de Dívida Ativa Contábil' ");
            sb.append("        end as tipoMovimento, ");
            sb.append("        case div.tipolancamento ");
            sb.append("          when 'NORMAL' then 20 else 21 ");
            sb.append("        end as ordem, ");
            sb.append("        cast(div.datadivida as Date) as datamovimento, ");
            sb.append("        cast(div.numero as number) as numeromovimento, ");
            sb.append("        div.historicorazao as historico, ");
            sb.append("        div.valor as valor ");
            sb.append("   from dividaativacontabil div ");
            sb.append("  where div.pessoa_id = :idPessoa ");
            sb.append("  union all ");
            sb.append(" select case cred.tipolancamento ");
            sb.append("          when 'NORMAL' then 'Crédito a Receber' else 'Estorno de Crédito a Receber' ");
            sb.append("        end as tipoMovimento, ");
            sb.append("        case cred.tipolancamento ");
            sb.append("          when 'NORMAL' then 22 else 23 ");
            sb.append("        end as ordem, ");
            sb.append("        cast(cred.datacredito as Date) as datamovimento, ");
            sb.append("        cast(cred.numero as number) as numeromovimento, ");
            sb.append("        cred.historicorazao as historico, ");
            sb.append("        cred.valor as valor ");
            sb.append("   from creditoreceber cred ");
            sb.append("  where cred.pessoa_id = :idPessoa ");
            sb.append(" ) order by ordem, dataMovimento, numeroMovimento ");
            Query q = em.createNativeQuery(sb.toString());
            q.setParameter("idPessoa", transferenciaMovPessoa.getPessoaOrigem().getId());
            if (!q.getResultList().isEmpty()) {
                for (Object[] obj : (List<Object[]>) q.getResultList()) {
                    TransferenciaMovPessoaContabil transferenciaMovPessoaContabil = new TransferenciaMovPessoaContabil();
                    transferenciaMovPessoaContabil.setTipoMovimento((String) obj[0]);
                    transferenciaMovPessoaContabil.setDataMovimento((Date) obj[1]);
                    transferenciaMovPessoaContabil.setNumero(((BigDecimal) obj[2]).toString());
                    transferenciaMovPessoaContabil.setHistorico((String) obj[3]);
                    transferenciaMovPessoaContabil.setValor((BigDecimal) obj[4]);
                    toReturn.add(transferenciaMovPessoaContabil);
                }
            }
        }
        return toReturn;
    }

    public List<Dependente> buscarDependentesPorPessoa(Pessoa pessoa) {
        return dependenteFacade.buscarDependentesPorDependente(pessoa);
    }

    public List<Dependente> buscarDependentesPorResponsavel(Pessoa pessoa) {
        return dependenteFacade.dependentesPorResponsavel(pessoa);
    }

    public List<BeneficioPensaoAlimenticia> buscarBeneficioPensaoAlimenticiaPorPessoa(PessoaFisica pf) {
        String hql = "select beneficio from PensaoAlimenticia pensao " +
            " inner join pensao.beneficiosPensaoAlimenticia beneficio " +
            " where beneficio.beneficiario = :pf ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("pf", pf);
        return q.getResultList();
    }

    public List<BeneficioPensaoAlimenticia> buscarBeneficioPensaoAlimenticiaPorResponsavel(PessoaFisica pf) {
        String hql = "select beneficio from PensaoAlimenticia pensao " +
            " inner join pensao.beneficiosPensaoAlimenticia beneficio " +
            " where beneficio.responsavel = :pf ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("pf", pf);
        return q.getResultList();
    }

    public List<MatriculaFP> buscarMatriculasPorPessoa(PessoaFisica pf) {
        String hql = "select mat from MatriculaFP mat " +
            " where mat.pessoa = :pf ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("pf", pf);
        return q.getResultList();
    }

    public List<PrestadorServicos> buscarPrestadorServicosPorPessoa(PessoaFisica pf) {
        String hql = "select p from PrestadorServicos p " +
            " where p.prestador = :pf ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("pf", pf);
        return q.getResultList();
    }

    public List<ResultadoParcela> buscarDebitosParaTransferir(TransferenciaMovPessoa transferenciaMovPessoa) {
        List<ResultadoParcela> parcelas = Lists.newArrayList();
        if (transferenciaMovPessoa.getTransfereMovimentosTributario()) {
            ConsultaParcela consultaParcela = criarConsultaParcelaTransferencia();
            consultaParcela.addParameter(ConsultaParcela.Campo.PESSOA_ID, ConsultaParcela.Operador.IGUAL, transferenciaMovPessoa.getPessoaOrigem().getId());
            consultaParcela.addComplementoDoWhere(" and vw.cadastro_id is null");

            parcelas.addAll(consultaParcela.executaConsulta().getResultados());

            List<Long> idsCadastros = Lists.newArrayList();

            if (transferenciaMovPessoa.getTransfereBcrs()) {
                List<PropriedadeRural> propriedadeRurals = getParecerTransferenciaMovimentoPessoaFacade().buscarPropriedadesCadastrosRuraisParaTransferir(transferenciaMovPessoa);
                for (PropriedadeRural propriedadeRural : propriedadeRurals) {
                    idsCadastros.add(propriedadeRural.getImovel().getId());
                }
            }

            if (transferenciaMovPessoa.getTransfereBcis()) {
                List<Propriedade> propriedades = getParecerTransferenciaMovimentoPessoaFacade().buscarPropriedadesCadastrosImobiliariosParaTransferir(transferenciaMovPessoa);
                for (Propriedade propriedade : propriedades) {
                    idsCadastros.add(propriedade.getImovel().getId());
                }
            }


            if (transferenciaMovPessoa.getTransfereBces()) {
                List<CadastroEconomico> cadastroEconomicos = getParecerTransferenciaMovimentoPessoaFacade().buscarCadastrosEconomicosParaTransferir(transferenciaMovPessoa);
                for (CadastroEconomico cadastroEconomico : cadastroEconomicos) {
                    idsCadastros.add(cadastroEconomico.getId());
                }
            }

            if (!idsCadastros.isEmpty()) {
                consultaParcela = criarConsultaParcelaTransferencia();
                consultaParcela.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IN, idsCadastros);
                consultaParcela.executaConsulta();
                for (ResultadoParcela parcelaDoCadastro : consultaParcela.getResultados()) {
                    if (!parcelas.contains(parcelaDoCadastro)) {
                        parcelas.add(parcelaDoCadastro);
                    }
                }
            }
            Collections.sort(parcelas, new OrdenaResultadoParcelaPadrao());
        }
        return parcelas;
    }

    private ConsultaParcela criarConsultaParcelaTransferencia() {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addOrdem(ConsultaParcela.Campo.DIVIDA_DESCRICAO, ConsultaParcela.Ordem.TipoOrdem.ASC);
        consultaParcela.addOrdem(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Ordem.TipoOrdem.ASC);
        consultaParcela.addOrdem(ConsultaParcela.Campo.REFERENCIA_PARCELA, ConsultaParcela.Ordem.TipoOrdem.ASC);
        consultaParcela.addOrdem(ConsultaParcela.Campo.PARCELA_DIVIDA_SEQUENCIA_PARCELA, ConsultaParcela.Ordem.TipoOrdem.ASC);
        consultaParcela.addOrdem(ConsultaParcela.Campo.PARCELA_VENCIMENTO, ConsultaParcela.Ordem.TipoOrdem.ASC);
        consultaParcela.addOrdem(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Ordem.TipoOrdem.ASC);
        return consultaParcela;
    }

    @Override
    public void salvarNovo(TransferenciaMovPessoa transferenciaMovPessoa) {
        transferenciaMovPessoa.setNumeroTransferencia(singletonGeradorCodigo.getProximoCodigo(TransferenciaMovPessoa.class, "numeroTransferencia"));
        transferenciaMovPessoa.setSituacao(SituacaoTransfMovimentoPessoa.ABERTA);
        super.salvarNovo(transferenciaMovPessoa);
    }

    public void salvarAndReabrir(TransferenciaMovPessoa transferenciaMovPessoa) {
        transferenciaMovPessoa.setSituacao(SituacaoTransfMovimentoPessoa.ABERTA);
        super.salvar(transferenciaMovPessoa);
    }

    public void deferirTransferencia(TransferenciaMovPessoa transferenciaMovPessoa, ParecerTransferenciaMovimentoPessoaControlador.ParametrosRelatorioTransferenciaPessoa parametroRelatorio, UsuarioSistema usuarioCorrente, UnidadeOrganizacional unidadeOrcamentariaCorrente) throws Exception {
        transferenciaMovPessoa.setSituacao(SituacaoTransfMovimentoPessoa.DEFERIDA);
        adicionarArquivoPdfDoRelatorioDeSimulacaoNaTransferencia(transferenciaMovPessoa, usuarioCorrente, unidadeOrcamentariaCorrente, parametroRelatorio);
        super.salvar(transferenciaMovPessoa);
    }

    public void indeferirTransferencia(TransferenciaMovPessoa transferenciaMovPessoa) {
        transferenciaMovPessoa.setSituacao(SituacaoTransfMovimentoPessoa.INDEFERIDA);
        super.salvar(transferenciaMovPessoa);
    }

    public ByteArrayOutputStream criarSimulacaoTransferenciaMovimento(TransferenciaMovPessoa transferenciaMovPessoa, UsuarioSistema usuarioCorrente, UnidadeOrganizacional unidadeOrcamentariaCorrente, ParecerTransferenciaMovimentoPessoaControlador.ParametrosRelatorioTransferenciaPessoa parametroRelatorio) throws IOException, JRException {
        AbstractReport ar = AbstractReport.getAbstractReport();
        String arquivoJasper = "SimulacaoDeTransferenciaDeMovimentosDePessoa.jasper";

        HashMap parameters = new HashMap();
        parameters.put("SUBREPORT_DIR", parametroRelatorio.getSubReport());
        parameters.put("BRASAO", parametroRelatorio.getCaminhoBrasao());
        parameters.put("ENTIDADE", "PREFEITURA MUNICIPAL DE RIO BRANCO");
        parameters.put("SECRETARIA", "SECRETARIA DE DESENVOLVIMENTO ECONÔMICO E FINANÇAS");
        parameters.put("FILTROS", montarFiltrosUtilizadosRelatorio(transferenciaMovPessoa));
        parameters.put("USUARIO", usuarioCorrente.toString());
        parameters.put("NOME_RELATORIO", "SIMULAÇÃO TRANSFERÊNCIA DE MOVIMENTOS DE PESSOA");
        List<VoSimulacaoTransferenciaMovimentoPessoa> dadosRelatorio = Lists.newArrayList();
        VoSimulacaoTransferenciaMovimentoPessoa voSimulacao = new VoSimulacaoTransferenciaMovimentoPessoa();
        voSimulacao.setPropriedadesCadastrosImobiliarios(getParecerTransferenciaMovimentoPessoaFacade().buscarPropriedadesCadastrosImobiliariosParaTransferir(transferenciaMovPessoa));
        voSimulacao.setCadastrosEconomicos(getParecerTransferenciaMovimentoPessoaFacade().buscarCadastrosEconomicosParaTransferir(transferenciaMovPessoa));
        voSimulacao.setPropriedadesCadastrosRurais(getParecerTransferenciaMovimentoPessoaFacade().buscarPropriedadesCadastrosRuraisParaTransferir(transferenciaMovPessoa));
        voSimulacao.setResultadoParcelas(buscarDebitosParaTransferir(transferenciaMovPessoa));
        voSimulacao.setMovimentosContabeis(recuperarMovimentosContabeis(transferenciaMovPessoa));
        voSimulacao.setMatriculasVOs(buscarMatriculas(transferenciaMovPessoa));
        voSimulacao.setPrestadoresVOs(buscarPrestadoresDeServico(transferenciaMovPessoa));
        voSimulacao.setResponsaveisDependenteVOs(buscarDependentesPorResponsavel(transferenciaMovPessoa));
        voSimulacao.setDependenteVOs(buscarDependentesPorDependente(transferenciaMovPessoa));
        voSimulacao.setPensaoAlimenticiaVOs(buscarPensoesAlimenticiasPorBeneficiario(transferenciaMovPessoa));
        voSimulacao.setResponsavelPensaoAlimenticiaVOs(buscarPensoesAlimenticiasPorResponsavel(transferenciaMovPessoa));
        voSimulacao.setDocumentosPessoaisVOs(buscarDocumentos(transferenciaMovPessoa));
        voSimulacao.setEnderecoVOs(buscarEnderecos(transferenciaMovPessoa));
        voSimulacao.setTelefoneVOs(buscarTelefones(transferenciaMovPessoa));
        voSimulacao.setInformacaoBancariaVOs(buscarInformacoesBancarias(transferenciaMovPessoa));
        dadosRelatorio.add(voSimulacao);
        return ar.gerarBytesEmPdfDeRelatorioComDadosEmCollectionViewAsync(parametroRelatorio.getCaminhoReport(), arquivoJasper, parameters, new JRBeanCollectionDataSource(dadosRelatorio), unidadeOrcamentariaCorrente);
    }

    public void adicionarArquivoPdfDoRelatorioDeSimulacaoNaTransferencia(TransferenciaMovPessoa transferenciaMovPessoa, UsuarioSistema usuarioCorrente, UnidadeOrganizacional unidadeOrcamentariaCorrente, ParecerTransferenciaMovimentoPessoaControlador.ParametrosRelatorioTransferenciaPessoa parametroRelatorio) throws Exception {
        try {
            if (transferenciaMovPessoa.getDetentorArquivoComposicao() == null) {
                transferenciaMovPessoa.setDetentorArquivoComposicao(new DetentorArquivoComposicao());
            }
            ArquivoComposicao arquivoComposicao = new ArquivoComposicao();
            arquivoComposicao.setDataUpload(new Date());
            arquivoComposicao.setDetentorArquivoComposicao(transferenciaMovPessoa.getDetentorArquivoComposicao());

            ByteArrayOutputStream bytes = criarSimulacaoTransferenciaMovimento(transferenciaMovPessoa, usuarioCorrente, unidadeOrcamentariaCorrente, parametroRelatorio);

            Arquivo arquivo = new Arquivo();
            arquivo.setDescricao("RELATÓRIO DE HISTÓRIO DE TRANSFERÊNCIA DE MOVIMENTO");
            arquivo.setNome("relatorioHistoricoTransferenciaMovimento.pdf");
            arquivo.setMimeType("application/pdf");
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes.toByteArray());
            arquivo.setTamanho(Long.valueOf(byteArrayInputStream.available()));
            arquivoComposicao.setArquivo(arquivoFacade.novoArquivoMemoria(arquivo, byteArrayInputStream));
            transferenciaMovPessoa.getDetentorArquivoComposicao().getArquivosComposicao().add(arquivoComposicao);
        } catch (Exception ex) {
            logger.error("Erro ao gerar o arquivo de simulação! {}", ex);
        }
    }

    public boolean hasPessoaComVinculoFP(Pessoa pessoa) {
        String sql = " select vinculo.* from VINCULOFP vinculo " +
            " inner join matriculafp mat on vinculo.MATRICULAFP_ID = mat.ID " +
            " inner join pessoafisica pf on mat.PESSOA_ID = pf.ID " +
            " where pf.id = :pf";
        Query q = em.createNativeQuery(sql);
        q.setParameter("pf", pessoa.getId());
        return !q.getResultList().isEmpty();
    }

    public boolean hasPessoaBeneficiariaPensaoAlimenticia(Pessoa pessoa) {
        String sql = " select b.* " +
            "from BeneficioPensaoAlimenticia b " +
            "where b.BENEFICIARIO_ID = :pf";
        Query q = em.createNativeQuery(sql);
        q.setParameter("pf", pessoa.getId());
        return !q.getResultList().isEmpty();
    }

    public List<TransferenciaMovPessoa> buscarTransferenciaMovPessoaPorPessoaAbertas(Pessoa pessoa) {
        String hql = "select distinct obj from TransferenciaMovPessoa obj " +
            " where (obj.pessoaOrigem.id = :idPessoa " +
            " or obj.pessoaDestino.id = :idPessoa) " +
            " and obj.situacao = :situacao ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("idPessoa", pessoa.getId());
        q.setParameter("situacao", SituacaoTransfMovimentoPessoa.ABERTA);
        return q.getResultList();
    }
}


