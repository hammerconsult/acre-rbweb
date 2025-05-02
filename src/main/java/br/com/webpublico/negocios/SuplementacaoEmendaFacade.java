package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoContaCorrenteTCE;
import br.com.webpublico.enums.TipoDespesaORC;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 16/06/15
 * Time: 10:48
 * To change this template use File | Settings | File Templates.
 */

@Stateless
public class SuplementacaoEmendaFacade extends AbstractFacade<SuplementacaoEmenda> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ProvisaoPPAFacade provisaoPPAFacade;
    @EJB
    private ProjetoAtividadeFacade projetoAtividadeFacade;
    @EJB
    private ExtensaoFonteRecursoFacade extensaoFonteRecursoFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SuplementacaoEmendaFacade() {
        super(SuplementacaoEmenda.class);
    }

    public EntityManager getEm() {
        return em;
    }

    public void movimentarProvisaoPPASuplementacao(SuplementacaoEmenda suplementacaoEmenda) {
        if (suplementacaoEmenda.getSubAcaoPPA() != null) {
            ProvisaoPPADespesa provisaoPPADespesa = buscarDespesaOrcamento(suplementacaoEmenda);
            if (provisaoPPADespesa != null) {
                List<ProvisaoPPAFonte> fontesCriadas = Lists.newArrayList();
                boolean hasFonte = false;
                for (ProvisaoPPAFonte provisaoPPAFonte : provisaoPPADespesa.getProvisaoPPAFontes()) {
                    if (provisaoPPAFonte.getDestinacaoDeRecursos().equals(suplementacaoEmenda.getDestinacaoDeRecursos())) {
                        provisaoPPAFonte.setValor(provisaoPPAFonte.getValor().add(suplementacaoEmenda.getValor()));
                        em.merge(provisaoPPAFonte);
                        hasFonte = true;
                    }
                }
                if (!hasFonte && suplementacaoEmenda.getEmenda().getModalidadeEmenda().isDireta()) {
                    fontesCriadas.add(criarProvisaoPPAFonte(suplementacaoEmenda, provisaoPPADespesa));
                }
                provisaoPPADespesa.getProvisaoPPAFontes().addAll(fontesCriadas);
                provisaoPPADespesa.setValor(provisaoPPADespesa.getValor().add(suplementacaoEmenda.getValor()));
                em.merge(provisaoPPADespesa);
            } else if (!suplementacaoEmenda.getContaPrevistaLoa()) {
                criarProvisaoPPADespesaAndFonte(suplementacaoEmenda, suplementacaoEmenda.getSubAcaoPPA());
            }
        } else {
            SubAcaoPPA subAcaoPPA = projetoAtividadeFacade.buscarSubacaoPorAcaoAndDescricao(suplementacaoEmenda.getPessoaJuridica().getRazaoSocial(), suplementacaoEmenda.getAcaoPPA());
            if (subAcaoPPA == null) {
                subAcaoPPA = criarSubAcaoPPA(suplementacaoEmenda);
            } else {
                subAcaoPPA.setTotalFinanceiro(subAcaoPPA.getTotalFinanceiro().add(suplementacaoEmenda.getValor()));
                subAcaoPPA.setTotalFisico(subAcaoPPA.getTotalFisico().add(BigDecimal.valueOf(suplementacaoEmenda.getQuantidade())));
                subAcaoPPA = em.merge(subAcaoPPA);
            }
            suplementacaoEmenda.setSubAcaoPPA(subAcaoPPA);
            suplementacaoEmenda = em.merge(suplementacaoEmenda);
            if (subAcaoPPA.getProvisaoPPADespesas().isEmpty()) {
                criarProvisaoPPADespesaAndFonte(suplementacaoEmenda, subAcaoPPA);
            } else {
                ProvisaoPPADespesa provisaoPPADespesa = null;
                for (ProvisaoPPADespesa provisaoDaSubacao : subAcaoPPA.getProvisaoPPADespesas()) {
                    if (provisaoDaSubacao.getContaDeDespesa().equals(suplementacaoEmenda.getConta())) {
                        provisaoPPADespesa = provisaoDaSubacao;
                        break;
                    }
                }
                if (provisaoPPADespesa != null) {
                    provisaoPPADespesa.setValor(provisaoPPADespesa.getValor().add(suplementacaoEmenda.getValor()));
                    ProvisaoPPAFonte provisaoPPAFonte = null;
                    for (ProvisaoPPAFonte provisaoFonte : provisaoPPADespesa.getProvisaoPPAFontes()) {
                        if (provisaoFonte.getDestinacaoDeRecursos().equals(suplementacaoEmenda.getDestinacaoDeRecursos())) {
                            provisaoPPAFonte = provisaoFonte;
                        }
                    }
                    if (provisaoPPAFonte != null) {
                        provisaoPPAFonte.setValor(provisaoPPAFonte.getValor().add(suplementacaoEmenda.getValor()));
                    } else {
                        provisaoPPAFonte = criarProvisaoPPAFonte(suplementacaoEmenda, provisaoPPADespesa);
                    }
                    Util.adicionarObjetoEmLista(provisaoPPADespesa.getProvisaoPPAFontes(), provisaoPPAFonte);
                    em.merge(provisaoPPADespesa);
                } else {
                    criarProvisaoPPADespesaAndFonte(suplementacaoEmenda, subAcaoPPA);
                }
            }
        }
    }

    private SubAcaoPPA criarSubAcaoPPA(SuplementacaoEmenda suplementacaoEmenda) {
        SubAcaoPPA subAcaoPPA = new SubAcaoPPA();
        subAcaoPPA.setDescricao(suplementacaoEmenda.getPessoaJuridica().getRazaoSocial());
        subAcaoPPA.setAcaoPPA(suplementacaoEmenda.getAcaoPPA());
        subAcaoPPA.setExercicio(suplementacaoEmenda.getEmenda().getExercicio());
        subAcaoPPA.setDataRegistro(new Date());
        subAcaoPPA.setTotalFinanceiro(suplementacaoEmenda.getValor());
        subAcaoPPA.setTotalFisico(BigDecimal.valueOf(suplementacaoEmenda.getQuantidade()));
        subAcaoPPA.setSomenteLeitura(false);
        projetoAtividadeFacade.retornaCodigoSubAcaoPPA(provisaoPPAFacade.getProjetoAtividadeFacade().recuperar(suplementacaoEmenda.getAcaoPPA().getId()), subAcaoPPA);
        subAcaoPPA = em.merge(subAcaoPPA);
        return subAcaoPPA;
    }

    private void criarProvisaoPPADespesaAndFonte(SuplementacaoEmenda suplementacaoEmenda, SubAcaoPPA subAcaoPPA) {
        ProvisaoPPADespesa provisaoPPADespesa = new ProvisaoPPADespesa();
        provisaoPPADespesa.setCodigo(provisaoPPAFacade.getProvisaoPPADespesaFacade().getCodigo(suplementacaoEmenda.getEmenda().getExercicio(), subAcaoPPA));
        provisaoPPADespesa.setUnidadeOrganizacional(suplementacaoEmenda.getUnidadeOrganizacional());
        provisaoPPADespesa.setSubAcaoPPA(subAcaoPPA);
        provisaoPPADespesa.setSomenteLeitura(false);
        provisaoPPADespesa.setContaDeDespesa(suplementacaoEmenda.getConta());
        provisaoPPADespesa.setTipoDespesaORC(TipoDespesaORC.ORCAMENTARIA);
        provisaoPPADespesa.setValor(suplementacaoEmenda.getValor());
        provisaoPPADespesa.setDataRegistro(new Date());

        Util.adicionarObjetoEmLista(provisaoPPADespesa.getProvisaoPPAFontes(), criarProvisaoPPAFonte(suplementacaoEmenda, provisaoPPADespesa));
        em.merge(provisaoPPADespesa);
    }

    private ProvisaoPPAFonte criarProvisaoPPAFonte(SuplementacaoEmenda suplementacaoEmenda, ProvisaoPPADespesa provisaoPPADespesa) {
        ProvisaoPPAFonte provisaoPPAFonte = new ProvisaoPPAFonte();
        provisaoPPAFonte.setValor(suplementacaoEmenda.getValor());
        provisaoPPAFonte.setSomenteLeitura(Boolean.TRUE);
        provisaoPPAFonte.setDestinacaoDeRecursos(suplementacaoEmenda.getDestinacaoDeRecursos());
        provisaoPPAFonte.setEsferaOrcamentaria(suplementacaoEmenda.getEmenda().getEsferaOrcamentaria());
        provisaoPPAFonte.setProvisaoPPADespesa(provisaoPPADespesa);
        ExtensaoFonteRecurso extensaoFonteRecurso = extensaoFonteRecursoFacade.buscarPorTipo(TipoContaCorrenteTCE.NAO_APLICAVEL);
        if (extensaoFonteRecurso != null) {
            provisaoPPAFonte.setExtensaoFonteRecurso(extensaoFonteRecurso);
        }
        return provisaoPPAFonte;
    }

    public ProvisaoPPADespesa buscarDespesaOrcamento(SuplementacaoEmenda suplementacaoEmenda) {
        List<ProvisaoPPADespesa> provisaoPPADespesas = provisaoPPAFacade.getProvisaoPPADespesaFacade().recuperarProvisaoPPADespesaPorSubAcao(suplementacaoEmenda.getSubAcaoPPA());
        for (ProvisaoPPADespesa ppaDespesa : provisaoPPADespesas) {
            if (suplementacaoEmenda.getConta().getId().equals(ppaDespesa.getContaDeDespesa().getId())) {
                return ppaDespesa;
            }
        }
        return null;
    }
}
