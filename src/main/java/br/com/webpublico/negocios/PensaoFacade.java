/*
 * Codigo gerado automaticamente em Tue Feb 14 09:57:29 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;

@Stateless
public class PensaoFacade extends AbstractFacade<Pensao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private AposentadoriaFacade aposentadoriaFacade;
    @EJB
    private RegistroDeObitoFacade registroDeObitoFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private CategoriaPCSFacade categoriaPCSFacade;
    @EJB
    private ProgressaoPCSFacade progressaoPCSFacade;
    @EJB
    private EnquadramentoPCSFacade enquadramentoPCSFacade;
    @EJB
    private OpcaoSalarialCCFacade opcaoSalarialCCFacade;
    @EJB
    private CargoConfiancaFacade cargoConfiancaFacade;
    @EJB
    private EnquadramentoFuncionalFacade enquadramentoFuncionalFacade;

    public PensaoFacade() {
        super(Pensao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean existeInstituidor(Pensao pensao) {
        String hql = " from Pensao p where p.contratoFP = :instituidor ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("instituidor", pensao.getContratoFP());

        List<Pensao> lista = new ArrayList<>();
        lista = q.getResultList();

        if (lista.contains(pensao)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }

    @Override
    public Pensao recuperar(Object obj) {
        Pensao pensao = em.find(Pensao.class, obj);
        pensao.getListaDePensionistas().size();
        pensao.getPensaoAtoLegal().size();
        for (Pensionista pensionista : pensao.getListaDePensionistas()) {
            pensionista.getItensValorPensionista().size();
            pensionista.getListaInvalidez().size();
            pensionista.getLotacaoFuncionals().size();
            pensionista.getRecursosDoVinculoFP().size();
            for (InvalidezPensao invalidezPensao : pensionista.getListaInvalidez()) {
                invalidezPensao.getInvalidezMedicos().size();
            }
        }
        return pensao;
    }

    public void correcaoMigracaoPensionistaCC() {
        correcaoMigracaoPensionistaCC57754();
        correcaoMigracaoPensionistaCC6246();
        correcaoMigracaoPensionista57452();
        correcaoMigracaoPensionista225770();
    }


    public void correcaoMigracaoPensionistaCC57754() {
        //Amed - 57754/1(instituidor)
        try {
            VinculoFP v = vinculoFPFacade.recuperarVinculoContratoPorMatriculaENumero("57754", "1");
            CargoConfianca ccs = cargoConfiancaFacade.recuperaCargoConfiancaVigente((ContratoFP) v, new Date());
            if (ccs.getId() == null) {
                CategoriaPCS cat = categoriaPCSFacade.listaFiltrandoVigente("PENSIONISTA SM6").get(0);
                ProgressaoPCS progressaoPCS = progressaoPCSFacade.buscaProgressoesNoEnquadramentoPCS(cat, new Date()).get(0);
                EnquadramentoPCS epcs = enquadramentoPCSFacade.filtraEnquadramentoVigente(progressaoPCS, cat);
                OpcaoSalarialCC opcaoSalarialCC = opcaoSalarialCCFacade.recuperarPorCodigo("2");
                CargoConfianca cc = new CargoConfianca();
                cc.setContratoFP((ContratoFP) v);
                cc.setInicioVigencia(v.getInicioVigencia());
                cc.setOpcaoSalarialCC(opcaoSalarialCC);
                cc.setDataNomeacao(v.getInicioVigencia());
                EnquadramentoCC enquadramentoCC = new EnquadramentoCC();
                enquadramentoCC.setInicioVigencia(v.getInicioVigencia());
                enquadramentoCC.setCargoConfianca(cc);
                enquadramentoCC.setCategoriaPCS(cat);
                enquadramentoCC.setProgressaoPCS(progressaoPCS);
                cc.getEnquadramentoCCs().add(enquadramentoCC);
                logger.debug("valores... " + cc + enquadramentoCC + v + cat + progressaoPCS + epcs);
                em.persist(cc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void correcaoMigracaoPensionistaCC6246() {
        //6246/1(instituidor)
        try {
            VinculoFP v = vinculoFPFacade.recuperarVinculoContratoPorMatriculaENumero("6246", "1");
            CargoConfianca ccs = cargoConfiancaFacade.recuperaCargoConfiancaVigente((ContratoFP) v, new Date());
            if (ccs.getId() == null) {
                CategoriaPCS cat = categoriaPCSFacade.listaFiltrandoVigente("PENSIONISTA B5").get(0);
                ProgressaoPCS progressaoPCS = progressaoPCSFacade.buscaProgressoesNoEnquadramentoPCSSemVigencia(cat).get(0);
                EnquadramentoPCS epcs = enquadramentoPCSFacade.filtraEnquadramentoVigente(progressaoPCS, cat);
                OpcaoSalarialCC opcaoSalarialCC = opcaoSalarialCCFacade.recuperarPorCodigo("2");
                CargoConfianca cc = new CargoConfianca();
                cc.setContratoFP((ContratoFP) v);
                cc.setInicioVigencia(v.getInicioVigencia());
                cc.setOpcaoSalarialCC(opcaoSalarialCC);
                cc.setDataNomeacao(v.getInicioVigencia());
                EnquadramentoCC enquadramentoCC = new EnquadramentoCC();
                enquadramentoCC.setInicioVigencia(v.getInicioVigencia());
                enquadramentoCC.setCargoConfianca(cc);
                enquadramentoCC.setCategoriaPCS(cat);
                enquadramentoCC.setProgressaoPCS(progressaoPCS);
                cc.getEnquadramentoCCs().add(enquadramentoCC);
                logger.debug("valores... " + cc + enquadramentoCC + v + cat + progressaoPCS + epcs);
                em.persist(cc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        correcaoMigracaoPensionista57452();
    }

    public void correcaoMigracaoPensionista57452() {
        //Amed - 57754/1(instituidor)
        try {
            VinculoFP v = vinculoFPFacade.recuperarVinculoContratoPorMatriculaENumero("57452", "1");
            EnquadramentoFuncional efV = enquadramentoFuncionalFacade.recuperaEnquadramentoFuncionalVigente((ContratoFP) v);
            if (efV == null) {
                CategoriaPCS cat = categoriaPCSFacade.listaFiltrandoVigente("QUADRO EM EXTINCAO").get(0);
                ProgressaoPCS progressaoPCS = progressaoPCSFacade.buscaProgressoesNoEnquadramentoPCS("M", "QE.ADM006.12", cat, new Date()).get(0);
                EnquadramentoPCS epcs = enquadramentoPCSFacade.filtraEnquadramentoVigente(progressaoPCS, cat);

                EnquadramentoFuncional ef = new EnquadramentoFuncional();
                ef.setInicioVigencia(v.getInicioVigencia());
                ef.setContratoServidor((ContratoFP) v);
                ef.setCategoriaPCS(cat);
                ef.setProgressaoPCS(progressaoPCS);
                ef.setDataCadastro(v.getInicioVigencia());
                logger.debug("valores... " + ef);
                em.persist(ef);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void correcaoMigracaoPensionista225770() {
        //Amed - 57754/1(instituidor)
        try {


            VinculoFP v = vinculoFPFacade.recuperarVinculoContratoPorMatriculaENumero("225770", "1");
            EnquadramentoFuncional efV = enquadramentoFuncionalFacade.recuperaEnquadramentoFuncionalVigente((ContratoFP) v);
            if (efV == null) {
                CategoriaPCS cat = categoriaPCSFacade.listaFiltrandoVigente("QUADRO EM EXTINCAO").get(0);
                ProgressaoPCS progressaoPCS = progressaoPCSFacade.buscaProgressoesNoEnquadramentoPCS("M", "QE.ADM002.12", cat, new Date()).get(0);
                EnquadramentoPCS epcs = enquadramentoPCSFacade.filtraEnquadramentoVigente(progressaoPCS, cat);

                EnquadramentoFuncional ef = new EnquadramentoFuncional();
                ef.setInicioVigencia(v.getInicioVigencia());
                ef.setContratoServidor((ContratoFP) v);
                ef.setCategoriaPCS(cat);
                ef.setProgressaoPCS(progressaoPCS);
                ef.setDataCadastro(v.getInicioVigencia());
                logger.debug("valores... " + ef);
                em.persist(ef);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Pensionista> recuperarPensionistasParaInvalidez() {
        logger.debug("iniciando a recuperação dos pensionistas invalidos...");
        List<Pensionista> aposentadoriasInvalidas = new ArrayList<>();
        HashMap<String, String> valores = recuperarValores();
        for (Map.Entry<String, String> objeto : valores.entrySet()) {
            Query q = em.createQuery("select pen from Pensionista pen where pen.matriculaFP.matricula = :matricula and pen.numero = :numero ");
            q.setParameter("matricula", objeto.getKey());
            q.setParameter("numero", objeto.getValue());
            aposentadoriasInvalidas.addAll(q.getResultList());
        }
        return aposentadoriasInvalidas;
    }

    public HashMap<String, String> recuperarValores() {
        HashMap<String, String> valores = new LinkedHashMap<>();
        valores.put("705065", "1");
        valores.put("544604", "1");//JHEOSUA GILBERTO CASTELO BRANCO
        return valores;
    }

    public boolean buscarFichaPensionista(Pensionista pensionista) {
        String sql = " select ff.id from FICHAFINANCEIRAFP ff " +
            "inner join VINCULOFP V on v.id = ff.VINCULOFP_ID " +
            "where V.ID = :pensionista ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("pensionista", pensionista.getId());
        return !q.getResultList().isEmpty();
    }

    public List<Pensao> buscarPensoesPorInstituidor(ContratoFP contrato) {
        String sql = " select p.* from PENSAO p " +
            " where p.CONTRATOFP_ID = :contrato ";
        Query q = em.createNativeQuery(sql, Pensao.class);
        q.setParameter("contrato", contrato.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

}
