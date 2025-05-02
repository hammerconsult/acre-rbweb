/*
 * Codigo gerado automaticamente em Mon Jan 23 11:06:44 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.CotaOrcamentaria;
import br.com.webpublico.entidades.FonteDespesaORC;
import br.com.webpublico.entidades.GrupoCotaORC;
import br.com.webpublico.entidades.GrupoOrcamentario;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;

@Stateless
public class CotaOrcamentariaFacade extends AbstractFacade<CotaOrcamentaria> {

    @EJB
    private FonteDespesaORCFacade fonteDespesaORCFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CotaOrcamentariaFacade() {
        super(CotaOrcamentaria.class);
    }

    public void creditaValorNaCotaOrcamentaria(FonteDespesaORC fonte, Integer mes, BigDecimal valor, GrupoOrcamentario grupoOrcamentario) {
        CotaOrcamentaria co = listaPorFonteDespesaOrcEIndice(fonte, mes);
        if (co != null) {
            co.setValorProgramado(co.getValorProgramado().add(valor));
            recalcularPorcentagemGrupoOrc(grupoOrcamentario);
        } else {
            throw new ExcecaoNegocioGenerica("Cota Orçamentária não encontrada para a Fonte: " + fonte.getProvisaoPPAFonte().getDestinacaoDeRecursos() + ". Verifique se existe Grupo Orçmentário para essa fonte.");
        }
    }

    public void debitaValorNaCotaOrcamentaria(FonteDespesaORC fonte, Integer mes, BigDecimal valor, GrupoOrcamentario grupoOrcamentario) {
        CotaOrcamentaria co = listaPorFonteDespesaOrcEIndice(fonte, mes);
        if (co != null) {
            validarSaldoCotaOrcamentaria(fonte, valor, mes);
            debitaValorProgramadoCota(co, valor, co.getGrupoCotaORC().getGrupoOrcamentario());
            recalcularPorcentagemGrupoOrc(grupoOrcamentario);
        } else {
            throw new ExcecaoNegocioGenerica("Cota Orçamentária não encontrada para a Fonte: " + fonte.getProvisaoPPAFonte().getDestinacaoDeRecursos() + ". Verifique se existe Grupo Orçmentário para essa fonte.");
        }
    }

    public CotaOrcamentaria debitaValorProgramadoCota(CotaOrcamentaria cota, BigDecimal valor, GrupoOrcamentario grupoOrcamentario) {
        if (valor.compareTo(BigDecimal.ZERO) > 0) {
            for (int x = cota.getIndice(); x > 0; x--) {
                CotaOrcamentaria cotaDoMes = recuperarPorGrupoOrcEIndice(grupoOrcamentario, x);
                if (valor.compareTo(cotaDoMes.getValorProgramado()) <= 0) {
                    cotaDoMes.setValorProgramado(cotaDoMes.getValorProgramado().subtract(valor).setScale(2, RoundingMode.HALF_UP));
                    return cotaDoMes;
                }
            }
        }
        return cota;
    }


    public void validarSaldoCotaOrcamentaria(FonteDespesaORC fonteDespesaORC, BigDecimal valor, Integer mes) {
        if (!validarValorUtilizadoCotaOrcamentaria(fonteDespesaORC, mes, valor)) {
            CotaOrcamentaria cotaOrcamentaria = listaPorFonteDespesaOrcEIndice(fonteDespesaORC, mes);
            String msgCotaOrcamentaria = " Saldo indisponível para o Grupo Orçamentário ";
            if (cotaOrcamentaria != null) {
                msgCotaOrcamentaria += cotaOrcamentaria.getGrupoCotaORC().getGrupoOrcamentario() + " e Fonte: " + fonteDespesaORC.getDescricaoFonteDeRecurso();
            }
            throw new ExcecaoNegocioGenerica(msgCotaOrcamentaria);
        }
    }

    public void recalcularPorcentagemGrupoOrc(GrupoOrcamentario grupoOrcamentario) {
        BigDecimal saldoGrupoOrc = fonteDespesaORCFacade.recuperarSaldoAtualPorGrupoOrc(grupoOrcamentario);
        for (CotaOrcamentaria cota : recuperarCotaPorGrupoOrcamentario(grupoOrcamentario)) {
            BigDecimal valorProgramado = cota.getValorProgramado().multiply(new BigDecimal(100));
            if (saldoGrupoOrc.compareTo(BigDecimal.ZERO) > 0) {
                cota.setPercentual(valorProgramado.divide(saldoGrupoOrc, 2, RoundingMode.HALF_UP));
            }
        }
    }


    private void recalcularPorcentagemGrupoOrcPorSuplementacao(BigDecimal valor, GrupoOrcamentario grupoOrcamentario) {
        BigDecimal saldoGrupoOrc = fonteDespesaORCFacade.recuperarSaldoAtualPorGrupoOrc(grupoOrcamentario);
        for (CotaOrcamentaria cota : recuperarCotaPorGrupoOrcamentario(grupoOrcamentario)) {
            BigDecimal valorProgramado = cota.getValorProgramado().multiply(new BigDecimal(100));
            cota.setPercentual(valorProgramado.divide(saldoGrupoOrc, 2, RoundingMode.HALF_UP));
        }
    }

    public List<CotaOrcamentaria> getCotasDaFonte(FonteDespesaORC fdo) {
        List<CotaOrcamentaria> cotas = Lists.newArrayList();
        cotas = listaPorFonteDespesaOrc(fdo);
        return cotas;
    }


    public void diminuirValorUtilizadoCotaOrcamentaria(FonteDespesaORC fonteDespesaORC, int indiceMes, BigDecimal valor) throws ExcecaoNegocioGenerica {
        try {
            List<CotaOrcamentaria> cotas = listaPorFonteDespesaOrc(fonteDespesaORC);

            CotaOrcamentaria cotaOrcamentaria = null;
            for (CotaOrcamentaria co : cotas) {
                if (indiceMes == co.getIndice()) {
                    cotaOrcamentaria = co;
                    break;
                }
            }
            cotaOrcamentaria.setValorUtilizado(cotaOrcamentaria.getValorUtilizado().subtract(valor));
            em.merge(cotaOrcamentaria);
        } catch (Exception e) {
            String mes = "";
            for (Mes mesDaVez : Mes.values()) {
                if (indiceMes == mesDaVez.getNumeroMes()) {
                    mes = mesDaVez.getDescricao();
                }
            }
            throw new ExcecaoNegocioGenerica("Não foi encontrada Cota Orçamentária no mês de <b>" + mes + "</b> para a fonte de recurso: <b>"
                    + fonteDespesaORC.getDescricaoFonteDeRecurso() + "</b>. Verifique se existe Grupo Orçamentário para a cota de " + mes + ".");
        }
    }

    public void aumentarValorUtilizadoCotaOrcamentaria(FonteDespesaORC fonteDespesaORC, int indiceMes, BigDecimal valor) {
        try {
            List<CotaOrcamentaria> cotas = listaPorFonteDespesaOrc(fonteDespesaORC);

            CotaOrcamentaria cotaOrcamentaria = null;
            for (CotaOrcamentaria co : cotas) {
                if (indiceMes == co.getIndice()) {
                    cotaOrcamentaria = co;
                    break;
                }
            }
            cotaOrcamentaria.setValorUtilizado(cotaOrcamentaria.getValorUtilizado().add(valor));
            em.merge(cotaOrcamentaria);
        } catch (Exception e) {
            String mes = "";
            for (Mes mesDaVez : Mes.values()) {
                if (indiceMes == mesDaVez.getNumeroMes()) {
                    mes = mesDaVez.getDescricao();
                }
            }
            throw new ExcecaoNegocioGenerica("Não foi encontrada Cota Orçamentária no mês de <b>" + mes + "</b> para a fonte de recurso: <b>"
                    + fonteDespesaORC.getDescricaoFonteDeRecurso() + "</b>. Verifique se existe Grupo Orçamentário para a cota de " + mes + ".");
        }
    }

    public Boolean validarValorUtilizadoCotaOrcamentaria(FonteDespesaORC fonteDespesaORC, int indiceMes, BigDecimal valor) {
        List<CotaOrcamentaria> cotas = listaPorFonteDespesaOrc(fonteDespesaORC);
        BigDecimal saldos = new BigDecimal(BigInteger.ZERO);
        for (CotaOrcamentaria co : cotas) {
            if (co.getIndice() <= indiceMes) {
                saldos = saldos.add(co.getValorProgramado().subtract(co.getValorUtilizado()));
                if (indiceMes == co.getIndice()) {
                    break;
                }
            }
        }
        if (valor.compareTo(saldos) <= 0) {
            return true;
        } else {
            return false;
        }
    }

    public BigDecimal buscarValorDaCotaPorFonteDespesaORC(FonteDespesaORC fonteDespesaORC, int indiceMes) {
        List<CotaOrcamentaria> cotas = listaPorFonteDespesaOrc(fonteDespesaORC);
        BigDecimal saldo = new BigDecimal(BigInteger.ZERO);
        for (CotaOrcamentaria co : cotas) {
            if (co.getIndice() <= indiceMes) {
                saldo = saldo.add(co.getValorProgramado().subtract(co.getValorUtilizado()));
                if (indiceMes == co.getIndice()) {
                    break;
                }
            }
        }
       return saldo;
    }


    private SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public List<CotaOrcamentaria> recuperaPorGrupoCotaOrc(GrupoCotaORC gco) {
        String sql = "SELECT co.* FROM CotaOrcamentaria co WHERE co.grupoCotaORC_id = :param";
        Query q = em.createNativeQuery(sql, CotaOrcamentaria.class);
        q.setParameter("param", gco.getId());

        List<CotaOrcamentaria> lista = q.getResultList();
        if (lista.isEmpty()) {
            return new ArrayList<CotaOrcamentaria>();
        } else {
            return lista;
        }
    }

    public List<CotaOrcamentaria> recuperarCotaPorGrupoOrcamentario(GrupoOrcamentario grupoOrcamentario) {
        String sql = " select co.* from cotaorcamentaria co " +
                " inner join grupocotaorc gc on gc.id = co.grupocotaorc_id " +
                " inner join grupoorcamentario go on go.id = gc.grupoorcamentario_id " +
                " where go.id = :idGrupo";

        Query q = em.createNativeQuery(sql, CotaOrcamentaria.class);
        q.setParameter("idGrupo", grupoOrcamentario.getId());

        List<CotaOrcamentaria> lista = q.getResultList();
        if (lista.isEmpty()) {
            return new ArrayList<CotaOrcamentaria>();
        } else {
            return lista;
        }
    }

    public List<CotaOrcamentaria> listaPorFonteDespesaOrc(FonteDespesaORC fonte) {
        String sql = " SELECT co.* FROM cotaorcamentaria co "
                + " INNER JOIN grupocotaorc gco ON co.grupocotaorc_id = gco.id "
                + " INNER JOIN grupoorcamentario go ON gco.grupoorcamentario_id = go.id "
                + " INNER JOIN fontedespesaorc fd ON go.id = fd.grupoorcamentario_id "
                + " WHERE fd.id = :fonte ";
        Query q = em.createNativeQuery(sql, CotaOrcamentaria.class);
        q.setParameter("fonte", fonte.getId());

        List resultList = q.getResultList();
        if (resultList != null && resultList.isEmpty()) {
            throw new ExcecaoNegocioGenerica("Não foi encontrado Cota Orçamentária para a Fonte : " + fonte.getProvisaoPPAFonte().getDestinacaoDeRecursos() + ", verifique se existe Grupo Orçmentário para essa fonte.");
        } else {
            Collections.sort(resultList, new Comparator<CotaOrcamentaria>() {
                @Override
                public int compare(CotaOrcamentaria o1, CotaOrcamentaria o2) {
                    return o1.getMes().getNumeroMes().compareTo(o2.getMes().getNumeroMes());
                }
            });
            return resultList;
        }
    }

    public CotaOrcamentaria listaPorFonteDespesaOrcEIndice(FonteDespesaORC fonte, Integer indice) {
        String sql = "SELECT co.* FROM cotaorcamentaria co "
                + "INNER JOIN grupocotaorc gco ON co.grupocotaorc_id = gco.id "
                + "INNER JOIN grupoorcamentario go ON gco.grupoorcamentario_id = go.id "
                + "INNER JOIN fontedespesaorc fd ON go.id = fd.grupoorcamentario_id "
                + "WHERE fd.id = :fonte AND co.indice = :ind";
        Query q = em.createNativeQuery(sql, CotaOrcamentaria.class);
        q.setParameter("fonte", fonte.getId());
        q.setParameter("ind", indice);
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            return (CotaOrcamentaria) q.getSingleResult();
        }
    }

    public CotaOrcamentaria salvarRetornando(CotaOrcamentaria cotaOrcamentaria) {
        return em.merge(cotaOrcamentaria);
    }

    public CotaOrcamentaria recuperarPorGrupoOrcEIndice(GrupoOrcamentario grupo, Integer indice) {
        String sql = " SELECT co.* FROM cotaorcamentaria co "
                + " INNER JOIN grupocotaorc gco ON co.grupocotaorc_id = gco.id "
                + " INNER JOIN grupoorcamentario go ON gco.grupoorcamentario_id = go.id "
                + " WHERE go.id = :idGrupo "
                + " AND co.indice = :ind";
        Query q = em.createNativeQuery(sql, CotaOrcamentaria.class);
        q.setParameter("idGrupo", grupo.getId());
        q.setParameter("ind", indice);
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            return (CotaOrcamentaria) q.getSingleResult();
        }
    }

    public List<CotaOrcamentaria> listaTodasCotaOrcamentarias(String parte) {
        String sql = "SELECT co.* FROM cotaorcamentaria co " +
                "      WHERE lower(CO.MES) LIKE :param ";
        Query q = em.createNativeQuery(sql, CotaOrcamentaria.class);
        q.setParameter("param", "%" + parte.trim() + "%");
        q.setMaxResults(10);
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            return q.getResultList();
        }
    }

    public FonteDespesaORCFacade getFonteDespesaORCFacade() {
        return fonteDespesaORCFacade;
    }

    public void setFonteDespesaORCFacade(FonteDespesaORCFacade fonteDespesaORCFacade) {
        this.fonteDespesaORCFacade = fonteDespesaORCFacade;
    }
}
