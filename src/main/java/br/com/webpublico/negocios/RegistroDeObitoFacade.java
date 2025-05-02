/*
 * Codigo gerado automaticamente em Thu Mar 22 09:22:05 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.util.Util;
import org.hibernate.Hibernate;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class RegistroDeObitoFacade extends AbstractFacade<RegistroDeObito> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private PensionistaFacade pensionistaFacade;
    @EJB
    private AposentadoriaFacade aposentadoriaFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private ArquivoFacade arquivoFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RegistroDeObitoFacade() {
        super(RegistroDeObito.class);
    }

    public PessoaFisicaFacade getPessoaFisicaFacade() {
        return pessoaFisicaFacade;
    }

    public void salvarNovo(RegistroDeObito entity) {
        salvarArquivos(entity);
        super.salvarNovo(entity);

        for (ContratoFP contrato : contratoFPFacade.listaContratosVigentesPorPessoaFisica(entity.getPessoaFisica())) {
            contratoFPFacade.processarHistoricoAndEncerrarVigenciasPorContratoFP(contrato, entity.getDataFalecimento());
        }

        contratoFPFacade.fecharVigenciaContratoFP(entity.getPessoaFisica(), entity.getDataFalecimento());
        pensionistaFacade.fecharVigenciaPensionista(entity.getPessoaFisica(), entity.getDataFalecimento());
        aposentadoriaFacade.fecharVigenciaAposentadoria(entity.getPessoaFisica(), entity.getDataFalecimento());
    }

    public void salvar(RegistroDeObito selecionado, List<Arquivo> arquivosParaRemover) {
        for (Arquivo a : arquivosParaRemover) {
            if (arquivoFacade.verificaSeExisteArquivo(a)) {
                arquivoFacade.removerArquivo(a);
            }
        }

        salvarArquivos(selecionado);
        super.salvar(selecionado);
    }

    public void salvarArquivos(RegistroDeObito selecionado) {
        for (ArquivoRegistroDeObito a : selecionado.getArquivoRegistroDeObitos()) {
            a.setArquivo(em.merge(a.getArquivo()));
        }
    }

    public boolean existeRegristroDeObitoPorPessoaFisica(RegistroDeObito registroDeObito) {
        String hql = " from RegistroDeObito obito where obito.pessoaFisica = :parametroPessoaFisica ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametroPessoaFisica", registroDeObito.getPessoaFisica());

        List<RegistroDeObito> lista = q.getResultList();

        if (lista.contains(registroDeObito)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }

    public List<MatriculaFP> listaMatriculaFP(String s) {
        /*
         * String hql = " select pessoa from PessoaFisica pessoa " + " where
         * (pessoa in (select matricula.pessoa from ContratoFP contrato" + "
         * inner join contrato.matriculaFP matricula" + " where :dataVigencia >=
         * contrato.inicioVigencia " + " and :dataVigencia <=
         * coalesce(contrato.finalVigencia,:dataVigencia) )" + " or " + " pessoa
         * in (select matricula.pessoa from Aposentadoria aposentadoria " + "
         * inner join aposentadoria.contratoFP contrato " + " inner join
         * contrato.matriculaFP matricula " + " where :dataVigencia >=
         * aposentadoria.inicioVigencia " + " and :dataVigencia <=
         * coalesce(aposentadoria.finalVigencia,:dataVigencia) )" + " or " + "
         * pessoa in (select matricula.pessoa from Pensionista pensionista " + "
         * inner join pensionista.matriculaFP matricula " + " where
         * :dataVigencia >= pensionista.inicioVigencia " + " and :dataVigencia
         * <= coalesce(pensionista.finalVigencia,:dataVigencia))) " + " and ( ";
         */

        String hql = " select new MatriculaFP(m.id, m.matricula, m.pessoa.nome) from VinculoFP v "
            + " inner join v.matriculaFP m"
            + " where :dataVigencia >= v.inicioVigencia "
            + " and :dataVigencia <= coalesce(v.finalVigencia,:dataVigencia)"
            + " and (v.class = ContratoFP or v.class = Aposentadoria or v.class = Pensionista)"
            + " and (lower(m.pessoa.nome) like :filtro or lower(m.matricula) like :filtro) order by m.pessoa.nome";


//        String hql = " select matriculaFP from MatriculaFP matriculaFP "
//                + " where (matriculaFP in (select matricula from ContratoFP contrato"
//                + " inner join contrato.matriculaFP matricula"
//                + " where :dataVigencia >= contrato.inicioVigencia "
//                + " and :dataVigencia <= coalesce(contrato.finalVigencia,:dataVigencia) )"
//                + " or "
//                + " matriculaFP in (select matricula from Aposentadoria aposentadoria "
//                + " inner join aposentadoria.contratoFP contrato "
//                + " inner join contrato.matriculaFP matricula "
//                + " where :dataVigencia >= aposentadoria.inicioVigencia "
//                + " and :dataVigencia <= coalesce(aposentadoria.finalVigencia,:dataVigencia) )"
//                + " or "
//                + " matriculaFP in (select matricula from Pensionista pensionista "
//                + " inner join pensionista.matriculaFP matricula "
//                + " where :dataVigencia >= pensionista.inicioVigencia "
//                + " and :dataVigencia <= coalesce(pensionista.finalVigencia,:dataVigencia))) "
//                + " and matriculaFP.pessoa not in (select obito.pessoaFisica from RegistroDeObito obito) "
//                + " and (lower(matriculaFP.pessoa.nome) like :filtro or lower(matriculaFP.matricula) like :filtro)";

        Query q = em.createQuery(hql);
        q.setParameter("filtro", "%" + s.trim().toLowerCase() + "%");
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(new Date()));
        q.setMaxResults(10);
        return q.getResultList();
    }

    public boolean podeExcluir(RegistroDeObito registro) {
        Query q = em.createQuery("select obito from RegistroDeObito obito, Pensao pensao inner join pensao.contratoFP contrato "
            + " inner join contrato.matriculaFP matricula "
            + " where matricula.pessoa = obito.pessoaFisica and obito = :obito");
        q.setParameter("obito", registro);
        if (q.getResultList().isEmpty()) {
            return true;
        }
        return false;

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public RegistroDeObito buscarRegistroDeObitoPorPessoaFisica(PessoaFisica pessoaFisica) {
        try {
            Query q = em.createQuery(" from RegistroDeObito registro where registro.pessoaFisica = :parametroPessoa ");
            q.setMaxResults(1);
            q.setParameter("parametroPessoa", pessoaFisica);
            RegistroDeObito registro = (RegistroDeObito) q.getSingleResult();
            if (registro != null) {
                Hibernate.initialize(registro.getArquivoRegistroDeObitos());
            }
            return registro;
        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean existeRegistroDeObitoPorPessoaFisica(PessoaFisica pessoaFisica) {
        Query q = em.createQuery(" from RegistroDeObito registro where registro.pessoaFisica = :parametroPessoa ");
        q.setParameter("parametroPessoa", pessoaFisica);
        return !q.getResultList().isEmpty();
    }

    public List<PessoaFisica> listaMatriculaFPSemObito(String s) {
        String hql = "  select distinct new PessoaFisica(pf.id, (replace(replace(pf.cpf,'.',''),'-',''))||' - '|| pf.nome) "
            + "       from VinculoFP v "
            + " inner join v.matriculaFP m "
            + " inner join m.pessoa pf "
            + "      where (v.class = ContratoFP or v.class = Aposentadoria or v.class = Pensionista) "
            + "        and m.pessoa not in (select pf from RegistroDeObito reg inner join reg.pessoaFisica pf) "
            + "        and (lower(pf.nome) like :filtro or lower(pf.cpf) like :filtro or lower(m.matricula) like :filtro) ";

        Query q = em.createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase().trim() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public StreamedContent montarArquivoParaDownloadPorArquivo(Arquivo arq) {
        return arquivoFacade.montarArquivoParaDownloadPorArquivo(arq);
    }

    public Arquivo novoArquivoMemoria(Arquivo arquivo) throws Exception {
        return arquivoFacade.novoArquivoMemoria(arquivo);
    }

    @Override
    public RegistroDeObito recuperar(Object id) {
        RegistroDeObito obj = super.recuperar(id);
        Hibernate.initialize(obj.getArquivoRegistroDeObitos());
        for (ArquivoRegistroDeObito a : obj.getArquivoRegistroDeObitos()) {
            Hibernate.initialize(a.getArquivo().getPartes());
        }
        return obj;
    }
}
