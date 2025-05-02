package br.com.webpublico.relatoriofacade;

import br.com.webpublico.contabil.relatorioitemdemonstrativo.FormulaItemDemonstrativoDTO;
import br.com.webpublico.contabil.relatorioitemdemonstrativo.dto.ItemDemonstrativoFiltrosDTO;
import br.com.webpublico.contabil.relatorioitemdemonstrativo.enums.TipoCalculoItemDemonstrativo;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.negocios.ItemDemonstrativoFacade;
import br.com.webpublico.negocios.contabil.RelatorioItemDemonstrativoCalculador;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.math.BigDecimal;

/**
 * Created by mateus on 25/04/18.
 */
@Stateless
public class RelatorioRGFAnexo02Facade extends RelatorioItemDemonstrativoCalculador {
    @EJB
    private ItemDemonstrativoFacade itemDemonstrativoFacade;
    @EJB
    private RelatorioRREOAnexo03Calculator relatorioRREOAnexo03Calculator;
    @EJB
    private RelatorioRGFAnexo01Facade relatorioRGFAnexo01Facade;

    @Override
    protected BigDecimal buscarValor(FormulaItemDemonstrativoDTO formulaItemDemonstrativo, ItemDemonstrativoFiltrosDTO filtros, TipoCalculoItemDemonstrativo tipoCalculo) {
        switch (tipoCalculo) {
            case SALDO_CONTABIL_TRANSPORTE:
                return buscarSaldoContabilTransporte(formulaItemDemonstrativo, filtros);
            case SALDO_CONTABIL_ATUAL:
                return buscarSaldoContabilPorTipoBalancete(formulaItemDemonstrativo, filtros);
            default:
                return BigDecimal.ZERO;
        }
    }

    public BigDecimal recuperarValorTransferencias(Mes mes, Exercicio exercicio) {
        return relatorioRGFAnexo01Facade.recuperarValorTransferencias(mes, exercicio);
    }

    @Override
    public ItemDemonstrativoFacade getItemDemonstrativoFacade() {
        return itemDemonstrativoFacade;
    }

    public RelatorioRREOAnexo03Calculator getRelatorioRREOAnexo03Calculator() {
        return relatorioRREOAnexo03Calculator;
    }

    public Exercicio buscarExercicioAnterior() {
        return getExercicioFacade().getExercicioPorAno(getSistemaFacade().getExercicioCorrente().getAno() - 1);
    }
}
