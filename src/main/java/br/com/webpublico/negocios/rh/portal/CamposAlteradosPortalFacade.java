/*
 * Codigo gerado automaticamente em Mon Sep 05 09:56:38 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios.rh.portal;


import br.com.webpublico.entidades.Dependente;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.rh.portal.atualizacaocadastral.CamposAlteradosPortal;
import br.com.webpublico.entidadesauxiliares.rh.portal.CampoModificadoDependente;
import br.com.webpublico.entidadesauxiliares.rh.portal.CamposPortal;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.DependenteFacade;
import br.com.webpublico.negocios.PessoaFisicaFacade;
import br.com.webpublico.pessoa.enumeration.CamposPessoaDTO;
import br.com.webpublico.pessoa.enumeration.TipoPessoaPortal;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Stateless
public class CamposAlteradosPortalFacade extends AbstractFacade<CamposAlteradosPortal> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private DependenteFacade dependenteFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CamposAlteradosPortalFacade() {
        super(CamposAlteradosPortal.class);
    }

    @Override
    public CamposAlteradosPortal recuperar(Object id) {
        CamposAlteradosPortal a = em.find(CamposAlteradosPortal.class, id);
        return a;
    }

    public List<CamposAlteradosPortal> buscarPorIdPessoa(Long idPessoa, boolean dependente) {
        String hql = " select am from CamposAlteradosPortal am "
            + " where am.pessoaFisica.id = :idPessoa and am.atualizado = 0 " +
            " and am.tipoPessoaPortal = ";
        hql += dependente ? " :dependente order by am.dependente, am.id" : " :servidor ";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("idPessoa", idPessoa);
        if (dependente) {
            q.setParameter("dependente", TipoPessoaPortal.DEPENDENTE);
        } else {
            q.setParameter("servidor", TipoPessoaPortal.SERVIDOR);
        }
        return q.getResultList();
    }

    public List<CamposAlteradosPortal> buscarCamposAlteradosPortalPorDependente(Dependente dependente) {
        String hql = " select obj from CamposAlteradosPortal obj "
            + " where obj.dependente.id = :idDependente ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("idDependente", dependente.getId());
        return q.getResultList();
    }

    public void atualizarCamposAlteradosPortal(PessoaFisica pf) {
        List<CamposAlteradosPortal> campos = buscarPorIdPessoa(pf.getId(), false);
        campos.addAll(buscarPorIdPessoa(pf.getId(), true));
        for (CamposAlteradosPortal campo : campos) {
            campo.setAtualizado(true);
            em.merge(campo);
        }
    }

    public void salvarCamposAlterados(CamposPortal campos) {
        Dependente dependente = null;
        PessoaFisica pf = (PessoaFisica) pessoaFisicaFacade.recuperar(PessoaFisica.class, campos.getIdPessoa());
        if (campos.getIdDependente() != null) {
            dependente = dependenteFacade.recuperar(campos.getIdDependente());
        }
        List<CamposAlteradosPortal> lista = buscarPorIdPessoa(campos.getIdPessoa(), false);
        List<CamposAlteradosPortal> excluir = new ArrayList<>();

        if (campos.getIdDependente() == null && TipoPessoaPortal.SERVIDOR.equals(campos.getTipoPessoaPortal())) {
            for (CamposAlteradosPortal camposAlteradosPortal : lista) {
                for (Map.Entry<CamposPessoaDTO, String> camposVindosPortal : campos.getCampos().entrySet()) {
                    if (camposVindosPortal.getKey().equals(camposAlteradosPortal.getCampo())) {
                        excluir.add(camposAlteradosPortal);
                    }
                }
            }
        }
        List<CamposAlteradosPortal> aSalvar = Lists.newArrayList();
        converterMapEmLista(aSalvar, pf, campos.getCampos(), dependente, campos.getTipoPessoaPortal());

        for (CamposAlteradosPortal camposParaExclusao : excluir) {
            remover(camposParaExclusao);
        }

        for (CamposAlteradosPortal camposParaSalvar : aSalvar) {
            salvar(camposParaSalvar);
        }

    }

    private void converterMapEmLista(List<CamposAlteradosPortal> aSalvar, PessoaFisica pf, Map<CamposPessoaDTO, String> campos, Dependente dependente, TipoPessoaPortal tipoPessoa) {
        if (TipoPessoaPortal.DEPENDENTE.equals(tipoPessoa)) {
            if (dependente == null) {
                for (Map.Entry<CamposPessoaDTO, String> campo : campos.entrySet()) {
                    if (CamposPessoaDTO.nome.equals(campo.getKey())) {
                        aSalvar.add(new CamposAlteradosPortal(pf, campo.getKey(), campo.getValue(), false, dependente, tipoPessoa));
                    }
                }
            } else {
                aSalvar.add(new CamposAlteradosPortal(pf, CamposPessoaDTO.nome, dependente.getDependente().getNome(), false, dependente, tipoPessoa));
            }
            for (Map.Entry<CamposPessoaDTO, String> camposPessoaDTOStringEntry : campos.entrySet()) {
                if (!CamposPessoaDTO.nome.equals(camposPessoaDTOStringEntry.getKey()))
                    aSalvar.add(new CamposAlteradosPortal(pf, camposPessoaDTOStringEntry.getKey(), camposPessoaDTOStringEntry.getValue(), false, dependente, tipoPessoa));
            }
        } else {
            for (Map.Entry<CamposPessoaDTO, String> camposPessoaDTOStringEntry : campos.entrySet()) {
                aSalvar.add(new CamposAlteradosPortal(pf, camposPessoaDTOStringEntry.getKey(), camposPessoaDTOStringEntry.getValue(), false, dependente, tipoPessoa));
            }
        }
    }

    public CamposPortal buscarCamposSalvos(Long idPessoa) {
        CamposPortal campos = new CamposPortal();
        for (CamposAlteradosPortal camposAlteradosPortal : buscarPorIdPessoa(idPessoa, false)) {
            campos.getCampos().put(camposAlteradosPortal.getCampo(), camposAlteradosPortal.getConteudo());
        }
        return campos;
    }

    public List<CampoModificadoDependente> buscarCamposSalvosDependentes(Long idPessoa) {
        List<CampoModificadoDependente> camposModificados = Lists.newArrayList();
        for (CamposAlteradosPortal campos : buscarPorIdPessoa(idPessoa, true)) {
            camposModificados.add(new CampoModificadoDependente(campos.getCampo(), campos.getCampo().getDescricao(), campos.getConteudo(), campos.getPessoaFisica().getId(), campos.getDependente() != null ? campos.getDependente().getId() : null));
        }
        return camposModificados;
    }
}
