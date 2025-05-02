package br.com.webpublico.negocios.contabil;

import br.com.webpublico.contabil.relatorioitemdemonstrativo.calculadores.ItemDemonstrativoCalculador;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ItemDemonstrativo;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.dao.JdbcParcelaValorDividaDAO;
import br.com.webpublico.relatoriofacade.ItemDemonstrativoCalculator;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class RelatorioItemDemonstrativoCalculador extends ItemDemonstrativoCalculador {

    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private ItemDemonstrativoFacade itemDemonstrativoFacade;
    @EJB
    private ItemDemonstrativoCalculator itemDemonstrativoCalculator;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private RelatoriosItemDemonstFacade relatoriosItemDemonstFacade;
    @EJB
    private ConfiguracaoAnexosRREOFacade configuracaoAnexosRREOFacade;

    public RelatorioItemDemonstrativoCalculador() {
        super(JdbcParcelaValorDividaDAO.getInstance().getJdbcTemplate());
    }

    public ItemDemonstrativo recuperarItemDemonstrativoPeloNomeERelatorio(String nomeDoItem, Exercicio exercicio, RelatoriosItemDemonst relatoriosItemDemonst) {
        return itemDemonstrativoCalculator.recuperarItemDemonstrativoPeloNomeERelatorio(nomeDoItem, exercicio, relatoriosItemDemonst);
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public ItemDemonstrativoFacade getItemDemonstrativoFacade() {
        return itemDemonstrativoFacade;
    }

    public FonteDeRecursosFacade getFonteDeRecursosFacade() {
        return fonteDeRecursosFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public RelatoriosItemDemonstFacade getRelatoriosItemDemonstFacade() {
        return relatoriosItemDemonstFacade;
    }

    public ConfiguracaoAnexosRREOFacade getConfiguracaoAnexosRREOFacade() {
        return configuracaoAnexosRREOFacade;
    }
}
