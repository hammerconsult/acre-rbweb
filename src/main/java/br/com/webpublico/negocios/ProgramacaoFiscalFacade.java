package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigoTributario;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * @author fabio
 */
@Stateless
public class ProgramacaoFiscalFacade extends AbstractFacade<ProgramacaoFiscal> {

    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private AcaoFiscalFacade acaoFiscalFacade;
    @EJB
    private CNAEFacade cnaefacade;
    @EJB
    private EnderecoCorreioFacade enderecoCorreioFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private ParametroFiscalizacaoFacade parametroFiscalizacaoFacade;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private ServicoFacade servicoFacade;
    @EJB
    private SingletonGeradorCodigoTributario singletonGeradorCodigoTributario;

    public ProgramacaoFiscalFacade() {
        super(ProgramacaoFiscal.class);
    }

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }

    public AcaoFiscalFacade getAcaoFiscalFacade() {
        return acaoFiscalFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public CNAEFacade getCnaefacade() {
        return cnaefacade;
    }

    public ParametroFiscalizacaoFacade getParametroFiscalizacaoFacade() {
        return parametroFiscalizacaoFacade;
    }

    public MoedaFacade getMoedaFacade() {
        return moedaFacade;
    }

    public ServicoFacade getServicoFacade() {
        return servicoFacade;
    }

    public DocumentoOficialFacade getDocumentoOficialFacade() {
        return documentoOficialFacade;
    }

    public EnderecoCorreioFacade getEnderecoCorreioFacade() {
        return enderecoCorreioFacade;
    }

    @Override
    public ProgramacaoFiscal recuperar(Object id) {
        ProgramacaoFiscal programacaoFiscal = em.find(ProgramacaoFiscal.class, id);
        programacaoFiscal.getAcoesFiscais().size();
        for (AcaoFiscal acao : programacaoFiscal.getAcoesFiscais()) {
            acao.getLancamentosContabeis().size();
        }
        if (programacaoFiscal.getDetentorArquivoComposicao() != null) {
            programacaoFiscal.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        return programacaoFiscal;
    }

    public boolean existeCodigo(Long numero) {
        String hql = "from ProgramacaoFiscal where numero = :numero";
        Query q = em.createQuery(hql);
        q.setParameter("numero", numero);
        return !q.getResultList().isEmpty();
    }

    public List<CadastroEconomico> recuperarPorFiltroCmc(List<Servico> servicos,
                                                         ClassificacaoAtividade classificacaoAtividade,
                                                         String bairro, Date dataInicial,
                                                         Date dataFinal,
                                                         BigDecimal valorInicial,
                                                         BigDecimal valorFinal,
                                                         SituacaoCadastralCadastroEconomico situacao,
                                                         String endereco) {

        String hql = "select ce from CadastroEconomico ce "
            + " join ce.pessoa p "
            + " join p.enderecoscorreio endereco "
            + " join fetch ce.situacaoCadastroEconomico situacao"
            + " left join ce.economicoCNAE ecnae "
            + " left join ce.servico  servico "
            + " left join ce.situacaoCadastroEconomico situacoesCMC"
            + " left join ce.enquadramentos enquadramento with enquadramento.fimVigencia is null "
            + " where situacoesCMC = (select max(situacao.id) from CadastroEconomico cad "
            + " inner join cad.situacaoCadastroEconomico situacao where cad = ce "
            + " and situacoesCMC.situacaoCadastral in (:situacoes))";

        if (servicos != null && !servicos.isEmpty()) {
            hql += " and servico in (:filtroServicos)";
        }
        if (classificacaoAtividade != null) {
            hql += " and ce.classificacaoAtividade = :filtroAtividade ";
        }
        if (endereco != null) {
            hql += " and lower(endereco.logradouro) like :filtroLogradouro ";
        }
        if (bairro != null) {
            hql += " and lower(endereco.bairro) like :filtroBairro ";
        }
        if (situacao != null) {
            hql += " and situacao.dataRegistro = (select max(situacao2.dataRegistro) from CadastroEconomico ce2 "
                + " join ce2.situacaoCadastroEconomico situacao2 where ce2 = ce) and situacao.situacaoCadastral = :situacao  ";
        }
        if (valorInicial != null && valorFinal != null && dataInicial != null || dataFinal != null) {
            hql += " and (select sum(iss.baseCalculo) from CalculoISS iss where iss.cadastroEconomico = ce and iss in (select vd.calculo from ValorDivida vd where vd.calculo.cadastro = ce and vd.emissao >= :filtroDataInicial and vd.emissao <= :filtroDataFinal)) between :filtroValorInicial and :filtroValorFinal ";
        }

        if (endereco != null && !endereco.trim().isEmpty()) {
            hql += " and lower(endereco.logradouro) like  :filtroLogradouro ";
        }

        Query q = em.createQuery(hql);
        q.setParameter("situacoes", Lists.newArrayList(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR.name(), SituacaoCadastralCadastroEconomico.ATIVA_NAO_REGULAR.name(), SituacaoCadastralCadastroEconomico.SUSPENSA.name()));
        if (servicos != null && !servicos.isEmpty()) {
            q.setParameter("filtroServicos", servicos);
        }
        if (classificacaoAtividade != null) {
            q.setParameter("filtroAtividade", classificacaoAtividade);
        }
        if (situacao != null) {
            q.setParameter("situacao", situacao);
        }
        if (bairro != null) {
            q.setParameter("filtroBairro", "%" + bairro.trim().toLowerCase() + "%");
        }
        if (endereco != null && !endereco.trim().isEmpty()) {
            q.setParameter("filtroLogradouro", "%" + endereco.trim().toLowerCase() + "%");
        }
        if (valorInicial != null && valorFinal != null && dataInicial != null || dataFinal != null) {
            q.setParameter("filtroDataInicial", dataInicial);
            q.setParameter("filtroDataFinal", dataFinal);
            q.setParameter("filtroValorInicial", valorInicial);
            q.setParameter("filtroValorFinal", valorFinal);
        }

        return new ArrayList<>(new HashSet<>((ArrayList<CadastroEconomico>) q.getResultList()));
    }

    public Long ultimoNumeroMaisUm() {
        return singletonGeradorCodigoTributario.getProximoNumero(sistemaFacade.getExercicioCorrente(), ProgramacaoFiscal.class, "numero", 5);
    }

    public Long getNumeroOrdemServicoMaisUm() {
        Query q = em.createNativeQuery("select coalesce(max(ordemservico), 0) + 1 as ordemservico from AcaoFiscal where ano = :ano");
        q.setParameter("ano", sistemaFacade.getExercicioCorrente().getAno());
        return Long.valueOf(q.getSingleResult().toString());
    }

    public DoctoAcaoFiscal buscarDocumentoFiscalPorTipo(AcaoFiscal acaoFiscal, TipoDoctoAcaoFiscal tipoDctoAcaoFiscal) {
        String hql = "select doc from DoctoAcaoFiscal doc where doc.acaoFiscal = :acao and doc.tipoDoctoAcaoFiscal = :tipo ";
        if (!SituacaoAcaoFiscal.REABERTO.equals(acaoFiscal.getSituacaoAcaoFiscal())) {
            hql += " and doc.ativo = true";
        }
        if (TipoDoctoAcaoFiscal.AUTOINFRACAO.equals(tipoDctoAcaoFiscal)) {
            hql += " and doc.id = (select max(d.id) from DoctoAcaoFiscal d where d.acaoFiscal = :acao and doc.tipoDoctoAcaoFiscal = :tipo)";
        }
        Query q = em.createQuery(hql);
        q.setParameter("acao", acaoFiscal);
        q.setParameter("tipo", tipoDctoAcaoFiscal);
        q.setMaxResults(1);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (DoctoAcaoFiscal) resultList.get(0);
        } else {
            return null;
        }
    }

    public DoctoAcaoFiscal salvaDocto(DoctoAcaoFiscal docto) {
        if (docto.getDocumentoOficial() != null) {
            DocumentoOficial doc = em.find(DocumentoOficial.class, docto.getDocumentoOficial().getId());
            docto.setDocumentoOficial(doc);
        }
        return em.merge(docto);
    }

    @Override
    public ProgramacaoFiscal recarregar(ProgramacaoFiscal pf) {
        pf = super.recarregar(pf);
        pf.getAcoesFiscais().size();
        return pf;
    }

    public ProgramacaoFiscal salva(ProgramacaoFiscal programacaoFiscal) {
        return em.merge(programacaoFiscal);
    }

    public void concluiProgramacao(ProgramacaoFiscal programacaoFiscal) {
        if (naoTemAcaoAberta(programacaoFiscal)) {
            programacaoFiscal.setSituacao(TipoSituacaoProgramacaoFiscal.CONCLUIDO);
            em.merge(programacaoFiscal);
        }
    }

    private boolean naoTemAcaoAberta(ProgramacaoFiscal programacaoFiscal) {
        return em.createQuery("select a from AcaoFiscal a where a.programacaoFiscal = :p and a.situacaoAcaoFiscal != 'CANCELADO' and a.situacaoAcaoFiscal != 'CONCLUIDO'").setParameter("p", programacaoFiscal).getResultList().isEmpty();
    }
}
