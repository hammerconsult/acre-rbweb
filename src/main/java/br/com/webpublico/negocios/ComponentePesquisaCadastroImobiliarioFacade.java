/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CadastroImobiliario;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * @author Renato
 */
@Stateless
public class ComponentePesquisaCadastroImobiliarioFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;

    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }

    public List<CadastroImobiliario> listaPorFiltro(String filtroCodigo,
                                                    String filtroContribuinte,
                                                    String filtroInscricao,
                                                    String filtroLote,
                                                    String filtroQuadra,
                                                    String filtroSetor,
                                                    String filtroCompromissario,
                                                    int filtroMunicipioLoteamento,
                                                    int filtroTipoProprietario,
                                                    String filtroCpfCnpj,
                                                    String filtroLogradouro,
                                                    String filtroBairro,
                                                    String filtroNumeroLote,
                                                    boolean filtroSituacaoCadastral) {
        String juncao = " where ";
        StringBuilder hql = new StringBuilder("select distinct ci from CadastroImobiliario ci ")
                .append("left join ci.propriedade p ")
                .append("left join ci.listaCompromissarios cp ")
                .append("left join ci.construcoes construcao ")
                .append("left join ci.lote.testadas testada ");
        Date dataAtual = new Date();

        if (filtroCpfCnpj != null && !filtroCpfCnpj.isEmpty()) {
            hql.append(juncao).append(" (p.pessoa in (select pj from PessoaJuridica pj where replace(replace(replace(pj.cnpj,'-',''), '.', ''), '/', '') like :cpfCnpj)) ")
                    .append("or (p.pessoa in (select pf from PessoaFisica pf where replace(replace(pf.cpf,'-',''), '.', '') like :cpfCnpj))");
            juncao = " and ";
        }

        if (filtroLogradouro != null && !filtroLogradouro.isEmpty()) {
            hql.append(juncao).append(" lower(trim(testada.face.logradouroBairro.logradouro.tipoLogradouro.descricao) ||' '|| lower(trim(testada.face.logradouroBairro.logradouro.nome))) like :logradouro");
            juncao = " and ";
        }

        if (filtroBairro != null && !filtroBairro.isEmpty()) {
            hql.append(juncao).append(" lower(testada.face.logradouroBairro.bairro.descricao) like :bairro");
            juncao = " and ";
        }
        if (filtroSituacaoCadastral) {
            hql.append(juncao).append("coalesce(ci.ativo, 1) = 1");
            juncao = " and ";
        } else {
            hql.append(juncao).append("ci.ativo = 0");
            juncao = " and ";
        }

        if (filtroNumeroLote != null && !filtroNumeroLote.isEmpty()) {
            hql.append(juncao).append(" lower(ci.lote.numeroCorreio) like :numeroLote");
            juncao = " and ";
        }

        if (filtroCompromissario != null && !filtroCompromissario.isEmpty()) {
            hql.append(juncao).append(" (cp.compromissario in (select pf from PessoaFisica pf where lower(pf.nome) like :compromissario)) ")
                    .append("  or (cp.compromissario in (select pj from PessoaJuridica pj where lower(pj.razaoSocial) like :compromissario))");
            juncao = " and ";
        }

        if (filtroTipoProprietario == 1) {
            hql.append(juncao).append(" (p.finalVigencia >= :dataAtual or p.finalVigencia is null)");
            juncao = " and ";
        } else if (filtroTipoProprietario == 2) {
            hql.append(juncao).append(" (p.finalVigencia <= :dataAtual)");
            juncao = " and ";
        }
        if (filtroContribuinte != null && !filtroContribuinte.isEmpty()) {
            hql.append(juncao).append(" (p.pessoa in (select pf from PessoaFisica pf where lower(pf.nome) like :pessoa) ")
                    .append(" or p.pessoa in (select pj from PessoaJuridica pj where lower(pj.razaoSocial) like :pessoa))");
            juncao = " and ";
        }

        if (filtroCodigo.length() > 0 && filtroCodigo != null) {
            hql.append(juncao).append(" ci.codigo like :filtroCodigo");
            juncao = " and ";
        }
        if (filtroInscricao.length() > 0 && filtroInscricao != null) {
            hql.append(juncao).append(" ci.inscricaoCadastral like :filtroInscricao");
            juncao = " and ";
        }
        if (filtroMunicipioLoteamento > 0) {
            if (filtroMunicipioLoteamento == 1) {
                if (filtroLote.length() > 0 && filtroLote != null) {
                    hql.append(juncao).append(" ci.lote.codigoLote like :filtroLote");
                    juncao = " and ";
                }
                if (filtroQuadra.length() > 0 && filtroQuadra != null) {
                    hql.append(juncao).append(" lower(ci.lote.quadra.descricao) like :filtroQuadra");
                    juncao = " and ";
                }
            } else if (filtroMunicipioLoteamento == 2) {

                if (filtroLote.length() > 0 && filtroLote != null) {
                    hql.append(juncao).append(" ci.lote.descricaoLoteamento like :filtroLote");
                    juncao = " and ";
                }
                if (filtroQuadra.length() > 0 && filtroQuadra != null) {
                    hql.append(juncao).append(" lower(ci.lote.quadra.descricaoLoteamento) like :filtroQuadra");
                    juncao = " and ";
                }
            } else {
                if (filtroLote.length() > 0 && filtroLote != null) {
                    hql.append(juncao).append(" ci.lote.codigoLote like :filtroLote or ci.lote.descricaoLoteamento like :filtroLote");
                    juncao = " and ";
                }
                if (filtroQuadra.length() > 0 && filtroQuadra != null) {
                    hql.append(juncao).append(" lower(ci.lote.quadra.descricao) like :filtroQuadra or lower(ci.lote.quadra.descricaoLoteamento) like :filtroQuadra");
                    juncao = " and ";
                }
            }
        }

        if (filtroSetor.length() > 0 && filtroSetor != null) {
            hql.append(juncao).append(" (lower(ci.lote.quadra.setor.nome) like :filtroSetor or ci.lote.quadra.setor.codigo like :filtroSetor)");
            juncao = " and ";
        }

        Query q = em.createQuery(hql.toString());
        q.setMaxResults(100);


        if (filtroCpfCnpj.length() > 0 && filtroCpfCnpj != null) {
            q.setParameter("cpfCnpj", "%" + filtroCpfCnpj.trim().replaceAll("-","").replaceAll("\\.","").replaceAll("/","")+ "%");
        }
        if (filtroCodigo.length() > 0 && filtroCodigo != null) {
            q.setParameter("filtroCodigo", "%" + filtroCodigo.trim() + "%");
        }
        if (filtroBairro.length() > 0 && filtroBairro != null) {
            q.setParameter("bairro", "%" + filtroBairro.trim().toLowerCase() + "%");
        }
        if (filtroNumeroLote.length() > 0 && filtroNumeroLote != null) {
            q.setParameter("numeroLote", "%" + filtroNumeroLote.trim() + "%");
        }
        if (filtroMunicipioLoteamento > 0) {
            if (filtroQuadra.length() > 0 && filtroQuadra != null) {
                q.setParameter("filtroQuadra", "%" + filtroQuadra.trim().toLowerCase() + "%");
            }
        }
        if (filtroSetor.length() > 0 && filtroSetor != null) {
            q.setParameter("filtroSetor", "%" + filtroSetor.trim().toLowerCase() + "%");
        }
        if (filtroInscricao.length() > 0 && filtroInscricao != null) {
            q.setParameter("filtroInscricao", "%" + filtroInscricao.trim() + "%");
        }
        if (filtroMunicipioLoteamento > 0) {
            if (filtroLote.length() > 0 && filtroLote != null) {
                q.setParameter("filtroLote", "%" + filtroLote.trim() + "%");
            }
        }
        if (filtroCompromissario != null && !filtroCompromissario.isEmpty()) {
            q.setParameter("compromissario", "%" + filtroCompromissario.trim().toLowerCase() + "%");
        }
        if (filtroLogradouro != null && !filtroLogradouro.isEmpty()) {

            q.setParameter("logradouro", "%" + filtroLogradouro.toLowerCase().trim() + "%");
        }
        if (filtroContribuinte != null && !filtroContribuinte.isEmpty()) {
            q.setParameter("pessoa", "%" + filtroContribuinte.trim().toLowerCase() + "%");
        }

        if (filtroTipoProprietario != 3) {
            q.setParameter("dataAtual", dataAtual);
        }

        return q.getResultList();
    }
}
