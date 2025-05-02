/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfigMovDividaPublica;
import br.com.webpublico.entidades.MovimentoDividaPublica;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.Date;

@Stateless
public class ConfigMovDividaPublicaFacade extends AbstractFacade<ConfigMovDividaPublica> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private UtilConfiguracaoEventoContabilFacade utilFacade;

    public ConfigMovDividaPublicaFacade() {
        super(ConfigMovDividaPublica.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public void setEventoContabilFacade(EventoContabilFacade eventoContabilFacade) {
        this.eventoContabilFacade = eventoContabilFacade;
    }

    @Override
    public ConfigMovDividaPublica recuperar(Object id) {
        ConfigMovDividaPublica cmdp = em.find(ConfigMovDividaPublica.class, id);
        return cmdp;
    }

    public boolean verificaConfiguracaoExistente(ConfigMovDividaPublica config, Date dataVigencia) {
        String sql = "select * from ConfigMovDividaPublica cmdp";
        sql += " inner join configuracaoevento ce on cmdp.id = ce.id";
        sql += " where ce.tipoLancamento = :tipolancamento ";
        sql += " and cmdp.operacaoMovimentoDividaPublica = :operacao ";
        sql += " and cmdp.naturezaDividaPublica = :natureza ";
        sql += " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";
        if (config.getId() != null) {
            sql += " AND CMDP.ID <> :config";
        }
        Query q = em.createNativeQuery(sql, ConfigMovDividaPublica.class);
        q.setParameter("tipolancamento", config.getTipoLancamento().name());
        q.setParameter("operacao", config.getOperacaoMovimentoDividaPublica().name());
        q.setParameter("natureza", config.getNaturezaDividaPublica().name());
        q.setParameter("data", DataUtil.getDataFormatada(dataVigencia));
        if (config.getId() != null) {
            q.setParameter("config", config.getId());
        }
        if (!q.getResultList().isEmpty() && q.getResultList() != null) {
            return true;
        } else {
            return false;
        }
    }

    public ConfigMovDividaPublica recuperaConfiguracaoExistente(MovimentoDividaPublica d) {

        String sql = "select csf.*,ce.* from ConfigMovDividaPublica csf";
        sql += " inner join configuracaoevento ce on csf.id = ce.id";
        sql += " where ce.tipoLancamento = :tipolancamento ";
        sql += " and csf.operacaoMovimentoDividaPublica = :operacao ";
        sql += " and csf.naturezaDividaPublica = :natureza ";
        sql += " AND to_date(:data,'dd/MM/yyyy') between trunc(CE.INICIOVIGENCIA) AND coalesce(trunc(CE.FIMVIGENCIA), to_date(:data,'dd/MM/yyyy')) ";

        Query q = em.createNativeQuery(sql, ConfigMovDividaPublica.class);
        q.setParameter("tipolancamento", d.getTipoLancamento().name());
        q.setParameter("operacao", d.getOperacaoMovimentoDividaPublica().name());
        q.setParameter("natureza", d.getDividaPublica().getCategoriaDividaPublica().getNaturezaDividaPublica().name());
        q.setParameter("data", new SimpleDateFormat("dd/MM/yyyy").format(d.getData()));
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (ConfigMovDividaPublica) q.getSingleResult();
        }
        throw new ExcecaoNegocioGenerica("Nenhum Evento Encontrado para o Tipo de Lançamento: " + d.getTipoLancamento().getDescricao() + "; Operação: " + d.getOperacaoMovimentoDividaPublica().getDescricao() + "; Natureza: " + d.getDividaPublica().getCategoriaDividaPublica().getNaturezaDividaPublica() + "; Data: " + DataUtil.getDataFormatada(d.getData()));
    }

    public void verificaAlteracoesEvento(ConfigMovDividaPublica configMovDividaPublicaNaoAlterado, ConfigMovDividaPublica cmdp) {
        if (cmdp.getId() == null) {
            return;
        }
        boolean alterou = false;
        if (!configMovDividaPublicaNaoAlterado.getTipoLancamento().equals(cmdp.getTipoLancamento())) {
            alterou = true;
        }
        if (!configMovDividaPublicaNaoAlterado.getOperacaoMovimentoDividaPublica().equals(cmdp.getOperacaoMovimentoDividaPublica())) {
            alterou = true;
        }
        if (!configMovDividaPublicaNaoAlterado.getNaturezaDividaPublica().equals(cmdp.getNaturezaDividaPublica())) {
            alterou = true;
        }
        if (!configMovDividaPublicaNaoAlterado.getInicioVigencia().equals(cmdp.getInicioVigencia())) {
            alterou = true;
        }
        if (!configMovDividaPublicaNaoAlterado.getEventoContabil().equals(cmdp.getEventoContabil())) {
            eventoContabilFacade.geraEventosReprocessar(cmdp.getEventoContabil(), cmdp.getId(), cmdp.getClass().getSimpleName());
            eventoContabilFacade.geraEventosReprocessar(configMovDividaPublicaNaoAlterado.getEventoContabil(), configMovDividaPublicaNaoAlterado.getId(), configMovDividaPublicaNaoAlterado.getClass().getSimpleName());
        }
        if (alterou) {
            eventoContabilFacade.geraEventosReprocessar(cmdp.getEventoContabil(), cmdp.getId(), cmdp.getClass().getSimpleName());
        }
    }

    public void meuSalvar(ConfigMovDividaPublica configMovDividaPublicaNaoAlterado, ConfigMovDividaPublica cmdp) {
        verificaAlteracoesEvento(configMovDividaPublicaNaoAlterado, cmdp);
        if (cmdp.getId() == null) {
            salvarNovo(cmdp);
        } else {
            salvar(cmdp);
        }
    }

    public void encerrarVigencia(ConfigMovDividaPublica entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }
}
