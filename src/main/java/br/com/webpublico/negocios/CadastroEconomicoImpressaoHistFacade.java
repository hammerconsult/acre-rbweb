package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.CadastroEconomicoImpressaoHist;
import br.com.webpublico.entidades.CalculoAlvara;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.GeradorChaveAutenticacao;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.ws.model.WSHistoricoBCM;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Stateless
public class CadastroEconomicoImpressaoHistFacade extends AbstractFacade<CadastroEconomicoImpressaoHist> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private CalculoAlvaraFacade calculoAlvaraFacade;

    public CadastroEconomicoImpressaoHistFacade() {
        super(CadastroEconomicoImpressaoHist.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }

    public String gerarChaveAutenticidade(CadastroEconomicoImpressaoHist cadastroEconomicoImpressaoHist){
        String assinatura = Util.dateToString(cadastroEconomicoImpressaoHist.getDataOperacao()) +  new Random().nextInt(10000) +
                cadastroEconomicoImpressaoHist.getUsuarioSistema() + cadastroEconomicoImpressaoHist.getCadastroEconomico().toString();
        String chaveDeAutenticacao = GeradorChaveAutenticacao.geraChaveDeAutenticacao(assinatura, GeradorChaveAutenticacao.TipoAutenticacao.CADASTRO_ECONOMICO);
        return chaveDeAutenticacao;
    }

    public WSHistoricoBCM buscarHistoricoBCMPorAutenticidade(String autenticidade) {

        String sql = "select * from cadastroeconomicohist " +
                "where replace(autenticidade, '.', '') = :autenticidade";

        Query q = em.createNativeQuery(sql, CadastroEconomicoImpressaoHist.class);
        q.setParameter("autenticidade", StringUtil.removeCaracteresEspeciaisSemEspaco(autenticidade));

        List<CadastroEconomicoImpressaoHist> cadastroEconomicoImpressaoHists = q.getResultList();
        WSHistoricoBCM wsHistoricoBCM = retornarHistoricoBCM(cadastroEconomicoImpressaoHists);
        return wsHistoricoBCM;
    }

    private WSHistoricoBCM retornarHistoricoBCM(List<CadastroEconomicoImpressaoHist> cadastroEconomicoImpressaoHists) {
        CadastroEconomicoImpressaoHist cadastroEconomicoImpressaoHist = null;
        if (cadastroEconomicoImpressaoHists != null && !cadastroEconomicoImpressaoHists.isEmpty()) {
            cadastroEconomicoImpressaoHist = cadastroEconomicoImpressaoHists.get(0);
        }
        WSHistoricoBCM wsHistoricoBCM = null;
        if (cadastroEconomicoImpressaoHist != null) {
            CadastroEconomicoImpressaoHist ultimoHistorico = buscarUltimoHistorico(cadastroEconomicoImpressaoHist.getCadastroEconomico().getId());

            wsHistoricoBCM = new WSHistoricoBCM();
            cadastroEconomicoImpressaoHist.setCadastroEconomico(cadastroEconomicoFacade.inicializarHistoricosImpressao(cadastroEconomicoImpressaoHist.getCadastroEconomico().getId()));
            wsHistoricoBCM.setId(cadastroEconomicoImpressaoHist.getId());
            wsHistoricoBCM.setAutenticidade(cadastroEconomicoImpressaoHist.getAutenticidade());
            wsHistoricoBCM.setNomeUsuario(cadastroEconomicoImpressaoHist.getUsuarioSistema() != null ? cadastroEconomicoImpressaoHist.getUsuarioSistema().getNome() : "Portal do Contribuinte");
            wsHistoricoBCM.setDataOperacao(cadastroEconomicoImpressaoHist.getDataOperacao());
            wsHistoricoBCM.setPessoa(cadastroEconomicoImpressaoHist.getCadastroEconomico().getPessoa().getNomeCpfCnpj());
            wsHistoricoBCM.setInscricaoCadastral(cadastroEconomicoImpressaoHist.getCadastroEconomico().getInscricaoCadastral());
            wsHistoricoBCM.setStatus(ultimoHistorico != null && !cadastroEconomicoImpressaoHist.getId().equals(ultimoHistorico.getId()) ? "Vencido" : "Válido");
        }
        return wsHistoricoBCM;
    }

    public WSHistoricoBCM buscarHistoricoBCMPorID(Long id) {

        String sql = "select * from CADASTROECONOMICOHIST " +
                "where ID = :id";

        Query q = em.createNativeQuery(sql, CadastroEconomicoImpressaoHist.class);

        q.setParameter("id", id);
        List<CadastroEconomicoImpressaoHist> cadastroEconomicoImpressaoHists = q.getResultList();
        WSHistoricoBCM wsHistoricoBCM = retornarHistoricoBCM(cadastroEconomicoImpressaoHists);
        return wsHistoricoBCM;
    }

    public CadastroEconomicoImpressaoHist gerarHistoricoImpressao(Long idCadastro) {
        CadastroEconomicoImpressaoHist historicoImpressao = buscarUltimoHistorico(idCadastro);
        if(historicoImpressao == null || canGerarHistoricoImpressao(idCadastro, historicoImpressao)) {
            historicoImpressao = gerarHistoricoCadastroEconomico(idCadastro);
        }
        return historicoImpressao;
    }

    private CadastroEconomicoImpressaoHist gerarHistoricoCadastroEconomico(Long idCadastro) {
        CadastroEconomico cadastroEconomico = em.find(CadastroEconomico.class, idCadastro);
        if (cadastroEconomico != null) {
            CadastroEconomicoImpressaoHist historicoImpressao = new CadastroEconomicoImpressaoHist(Util.recuperarUsuarioCorrente(),
                    new Date(), cadastroEconomico);
            historicoImpressao.setAutenticidade(GeradorChaveAutenticacao.formataChaveDeAutenticacao(gerarChaveAutenticidade(historicoImpressao)));
            return em.merge(historicoImpressao);
        }
        return null;
    }

    public boolean canGerarHistoricoImpressao(Long idCadastro, CadastroEconomicoImpressaoHist historico) {
        return hasAlteracaoCmc(idCadastro, historico.getDataOperacao());
    }

    public boolean hasAlteracaoCmc(Long idCadastro, Date dataComparacao) {
        if(buscarAlteracaoCmc(idCadastro, dataComparacao, "cadastroeconomico_aud", "id")) {
            return true;
        } else if (buscarAlteracaoCmc(idCadastro, dataComparacao, "cadastroeconomico_servico_aud", "cadastroeconomico_id")) {
            return true;
        } else if (buscarAlteracaoCmc(idCadastro, dataComparacao, "sociedadecadastroeconomico_aud", "cadastroeconomico_id")) {
            return true;
        } else if (buscarAlteracaoCmc(idCadastro, dataComparacao, "bcecaracfuncionamento_aud", "cadastroeconomico_id")) {
            return true;
        } else if (buscarAlteracaoEnderecoPessoaCmc(idCadastro, dataComparacao)) {
            return true;
        } else if (buscarAlteracaoCmc(idCadastro, dataComparacao, "enquadramentofiscal_aud", "cadastroeconomico_id")) {
            return true;
        }
        CalculoAlvara calculoAlvara = calculoAlvaraFacade.buscarUltimoCalculoLocalizacao(idCadastro);
        return (calculoAlvara != null && calculoAlvara.getDataCalculo() != null) && calculoAlvara.getDataCalculo().compareTo(dataComparacao) > 0;
    }

    private boolean buscarAlteracaoEnderecoPessoaCmc(Long idCmc, Date dataComparacao) {
        String sql = " select ec.* from vwenderecopessoa vw " +
            " inner join cadastroeconomico cmc on vw.pessoa_id = cmc.pessoa_id " +
            " inner join enderecocorreio_aud ec on vw.endereco_id = ec.id " +
            " inner join revisaoauditoria revisao on ec.rev = revisao.id " +
            " where revisao.id = (select ec2.rev from vwenderecopessoa vw2 " +
            "                     inner join enderecocorreio_aud ec2 on vw2.endereco_id = ec2.id " +
            "                     where vw2.pessoa_id = vw.pessoa_id " +
            "                     order by ec2.id desc fetch first 1 rows only) " +
            " and revisao.datahora > to_date(:dataComparacao, 'dd/MM/yyyy HH24:mi:ss') " +
            " and cmc.id = :idCmc ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("dataComparacao", DataUtil.getDataFormatadaDiaHora(dataComparacao));
        q.setParameter("idCmc", idCmc);

        return !q.getResultList().isEmpty();
    }

    private boolean buscarAlteracaoCmc(Long idCmc, Date dataComparacao, String tabela, String coluna) {
        String sql = " select aud.* from " + tabela + " aud " +
            " inner join revisaoauditoria revisao on revisao.id = aud.rev " +
            " where revisao.id = (select max(rev) from " + tabela + " where " + coluna + " = " + "aud" + "." + coluna + ") " +
            " and revisao.datahora > to_date(:dataComparacao, 'dd/MM/yyyy HH24:mi:ss') " +
            " and " + "aud" + "." + coluna + " = :idCmc ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("dataComparacao", DataUtil.getDataFormatadaDiaHora(dataComparacao));
        q.setParameter("idCmc", idCmc);

        return !q.getResultList().isEmpty();
    }

    public CadastroEconomicoImpressaoHist buscarUltimoHistorico(Long idCadastro) {
        String sql = " select hist.* from cadastroeconomicohist hist " +
                " where hist.cadastroeconomico_id = :idCadastro " +
                " and hist.dataoperacao = (select max(dataoperacao) from cadastroeconomicohist " +
                "                          where hist.cadastroeconomico_id = cadastroeconomico_id) " +
                " order by hist.dataoperacao desc ";

        Query q = em.createNativeQuery(sql, CadastroEconomicoImpressaoHist.class);
        q.setParameter("idCadastro", idCadastro);

        List<CadastroEconomicoImpressaoHist> historicos = q.getResultList();
        return (historicos != null && !historicos.isEmpty()) ? historicos.get(0) : null;
    }
}
