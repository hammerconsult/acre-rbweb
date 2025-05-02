/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.PermissaoTransporte;
import br.com.webpublico.entidades.PontoTaxi;
import br.com.webpublico.enums.TipoPermissaoRBTrans;
import br.com.webpublico.util.MensagemValidacao;
import br.com.webpublico.util.ResultadoValidacao;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author cheles
 */
@Stateless
public class PontoTaxiFacade extends AbstractFacade<PontoTaxi> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PermissaoTransporteFacade permissaoTransporteFacade;

    public PontoTaxiFacade() {
        super(PontoTaxi.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PermissaoTransporteFacade getPermissaoTransporteFacade() {
        return permissaoTransporteFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return permissaoTransporteFacade.getSistemaFacade();
    }

    @Override
    public PontoTaxi recuperar(Object id) {
        PontoTaxi ponto = em.find(PontoTaxi.class, id);
        return ponto;
    }

    public void salvaNovo(PontoTaxi pontoTaxi) {
        ResultadoValidacao result = validaRegrasDeNegocio(pontoTaxi);
        if (result.isValidado()) {
            try {
                super.salvarNovo(pontoTaxi);
                result.limpaMensagens();
                result.addMensagem(MensagemValidacao.info(null, "Sucesso!", "Registro gravado com sucesso!"));
            } catch (Exception ex) {
                result.limpaMensagens();
                result.addErro(null, "Erro Gravando Registro.", ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    public PontoTaxi salva(PontoTaxi ponto) {
        ResultadoValidacao result = validaRegrasDeNegocio(ponto);
        if (result.isValidado()) {
            try {
                ponto = em.merge(ponto);
                result.limpaMensagens();
                result.addMensagem(MensagemValidacao.info(null, "Sucesso!", "Registro gravado com sucesso!"));
            } catch (Exception ex) {
                result.limpaMensagens();
                result.addErro(null, "Erro Gravando Registro.", ex.getMessage());
            }
        }
        return ponto;
    }

    private ResultadoValidacao validaRegrasDeNegocio(PontoTaxi pontoTaxi) {
        ResultadoValidacao resultado = new ResultadoValidacao();
        final String summary = "Não foi possível  Continuar!";
        if (pontoTaxi.getId() == null && numeroDePontoRepetido(pontoTaxi)) {
            resultado.addErro(null, summary, "O número de ponto de táxi " + pontoTaxi.getNumero() + " já está em utilização.");
        }
        return resultado;
    }

    private boolean numeroDePontoRepetido(PontoTaxi pontoTaxi) {
        try {
            PontoTaxi ponto = recuperarPontoTaxiPeloNumero(pontoTaxi.getNumero());
            return ponto != null;
        } catch (Exception ex) {
            return false;
        }
    }

    public PontoTaxi recuperarPontoTaxiPeloNumero(Integer numero) {
        String hql = " from PontoTaxi pt"
            + "   where pt.numero = :numero";

        Query q = em.createQuery(hql);
        q.setParameter("numero", numero);
        List<PontoTaxi> pontos = q.getResultList();
        if (pontos.isEmpty()) return null;
        return pontos.get(0);
    }

    public List<PontoTaxi> listaFiltrandoPontoTaxi(TipoPermissaoRBTrans tipoPermissao, String parte) {
        String sql = "select * from pontotaxi " +
            " where pontotaxi.ativo = 1 and tipopermissaorbtrans = :tipoPermissao and ((to_char(pontotaxi.numero)) like :parte or lower(pontotaxi.localizacao) like :parte) ";
        Query q = em.createNativeQuery(sql, PontoTaxi.class);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setParameter("tipoPermissao", tipoPermissao.name());
        return q.getResultList();
    }

    public Integer buscarNumeroDeVagasOcupadas(PontoTaxi ponto) {
        Query q = em.createNativeQuery("select count(permissao.id) from permissaotransporte permissao " +
            "  inner join pontotaxi ponto on permissao.pontotaxi_id = ponto.id " +
            "  where ponto.id = :ponto");
        q.setParameter("ponto", ponto.getId());
        return ((BigDecimal) q.getResultList().get(0)).intValue();
    }

    public Integer buscarNumeroDeVagasOcupadasExcetoPermissaoSelecionada(PontoTaxi ponto, PermissaoTransporte permissaoTransporte) {
        Query q = em.createNativeQuery("select coalesce(count(permissao.id), 0) from permissaotransporte permissao " +
            "  inner join pontotaxi ponto on permissao.pontotaxi_id = ponto.id " +
            "  where ponto.id = :ponto " +
            "  and permissao.id <> :idPermissao ");
        q.setParameter("ponto", ponto.getId());
        q.setParameter("idPermissao", permissaoTransporte.getId());
        return ((BigDecimal) q.getResultList().get(0)).intValue();
    }

    public List<PermissaoTransporte> buscarPermissoesDoPonto(PontoTaxi ponto) {
        List<PermissaoTransporte> retorno = Lists.newArrayList();
        if (ponto.getId() == null) return retorno;
        Query q = em.createNativeQuery("select perm.id from permissaotransporte perm " +
            " where perm.pontotaxi_id = :idPontoTaxi ");
        q.setParameter("idPontoTaxi", ponto.getId());
        List<BigDecimal> result = q.getResultList();
        for (BigDecimal idPermissao : result) {
            retorno.add(permissaoTransporteFacade.recuperar(idPermissao.longValue()));
        }
        return retorno;
    }

    public Integer numeroDeVagas(PontoTaxi ponto) {
        Query q = em.createNativeQuery("select totaldevagas from pontotaxi " +
            "  where pontotaxi.id = :ponto");
        q.setParameter("ponto", ponto.getId());
        List<BigDecimal> result = q.getResultList();
        if (result.isEmpty()) return null;
        return result.get(0).intValue();
    }

    public boolean existePontoDeTaxiPorNumero(Integer numero) {
        String hql = "from PontoTaxi where numero = :numero";
        Query q = em.createQuery(hql);
        q.setParameter("numero", numero);
        return !q.getResultList().isEmpty();
    }
}
