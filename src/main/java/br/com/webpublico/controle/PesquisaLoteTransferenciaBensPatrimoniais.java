package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.enums.SituacaoDaSolicitacao;
import br.com.webpublico.negocios.LoteTransferenciaFacade;
import br.com.webpublico.util.EntidadeMetaData;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 08/01/14
 * Time: 17:28
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class PesquisaLoteTransferenciaBensPatrimoniais extends ComponentePesquisaGenerico {


    @EJB
    private LoteTransferenciaFacade loteTransferenciaFacade;

    @Override
    public void getCampos() {
        itens = Lists.newArrayList();
        itensOrdenacao = Lists.newArrayList();

        itens.add(new ItemPesquisaGenerica("", "", null));
        itensOrdenacao.add(new ItemPesquisaGenerica("", "", null));

        itens.add(new ItemPesquisaGenerica("obj.codigo", "Código", Long.class,false, true));
        itens.add(new ItemPesquisaGenerica("obj.dataHoraCriacao", "Data de Criação", Date.class,false, true));
        itens.add(new ItemPesquisaGenerica("obj.descricao", "Descrição", String.class,false, true));
        itens.add(new ItemPesquisaGenerica("obj.situacaoTransferenciaBem", "Situação", SituacaoDaSolicitacao.class,true, true));
        itens.add(new ItemPesquisaGenerica("vworigem.codigo", "Unidade Administrativa de Origem - Código", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("vworigem.descricao", "Unidade Administrativa de Origem - Descrição", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("vwdestino.codigo", "Unidade Administrativa de Destino - Código", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("vwdestino.descricao", "Unidade Administrativa de Destino - Descrição", String.class, false, true));

        itensOrdenacao.addAll(itens);
    }


    @Override
    protected String ordenacaoPadrao() {
        return " order by coalesce(obj.CODIGO, obj.id) desc";
    }


    @Override
    public String getHqlConsulta() {
        return "select " +
            "  obj.id, " +
            "  obj.CODIGO, " +
            "  obj.DATAHORACRIACAO as dataHora, " +
            "  obj.DESCRICAO, " +
            "  pfOrigiem.NOME  as nomeResponsavelOrigem, " +
            "  pfOrigiem.CPF   as cpfResponsavelOrigem, " +
            "  pfDestino.NOME  as nomeResposavelDestino, " +
            "  pfDestino.CPF   as cpfResposavelDestino, " +
            "  vworigem.codigo || ' - ' || coalesce(vworigem.DESCRICAO, uniorigem.DESCRICAO) as unidadeOrigem, " +
            "  vwdestino.codigo || ' - ' || coalesce(vwdestino.DESCRICAO, unidestino.descricao) as unidadeDestino, " +
            "  obj.SITUACAOTRANSFERENCIABEM " + getSelect();

    }

    @Override
    public String getHqlContador() {
        return "select count(distinct obj.id) " +
            getSelect();
    }

    public String getSelect() {

        return " from LoteTransferenciaBem obj " +
            "  inner join UNIDADEORGANIZACIONAL uniorigem on uniorigem.id = obj.unidadeOrigem_id " +
            "  inner JOIN VWHIERARQUIAADMINISTRATIVA vworigem ON vworigem.SUBORDINADA_ID =  obj.unidadeOrigem_id " +
            "       AND obj.dataHoraCriacao BETWEEN vworigem.inicioVigencia AND coalesce(vworigem.fimVigencia, current_date) " +
            "  inner join UNIDADEORGANIZACIONAL unidestino on unidestino.id = obj.UNIDADEDESTINO_ID " +
            "  inner JOIN VWHIERARQUIAADMINISTRATIVA vwdestino ON vwdestino.SUBORDINADA_ID =  obj.UNIDADEDESTINO_ID " +
            "       AND obj.dataHoraCriacao BETWEEN vwdestino.inicioVigencia AND coalesce(vwdestino.fimVigencia, current_date) " +
            "  left join PESSOAFISICA pfOrigiem on pfOrigiem.id = obj.responsavelOrigem_id " +
            "  left join PESSOAFISICA pfDestino on pfDestino.id = obj.responsavelDestino_id  ";
    }


    @Override
    public String getComplementoQuery() {
        return "  where uniorigem.id in (select uni.UNIDADEORGANIZACIONAL_ID from USUARIOUNIDADEORGANIZACIO uni " +
            "    inner join USUARIOSISTEMA usu on usu.id = uni.USUARIOSISTEMA_ID " +
            "  where usu.id = " + sistemaControlador.getUsuarioCorrente().getId() + " and uni.GESTORPATRIMONIO = 1) and "
            + montaCondicao() + montaOrdenacao();

    }

    @Override
    public Integer getTotalDeRegistrosExistentes() {
        if (totalDeRegistrosExistentes == null) {
            String hql = getHqlParaTotalDeRegistros();
            atribuirHqlConsultaRelatorioTodosRegistros();
            totalDeRegistrosExistentes = loteTransferenciaFacade.count(hql).intValue();
        }
        return totalDeRegistrosExistentes;
    }


    @Override
    public void executarConsulta(String sql, String sqlCount) {
        try {
            Object objeto = classe.newInstance();
            Object[] retorno = loteTransferenciaFacade.filtarComContadorDeRegistros(sql, sqlCount, objeto, inicio, maximoRegistrosTabela);
            lista = (ArrayList) retorno[0];
            totalDeRegistrosExistentes = ((Long) retorno[1]).intValue();
            if (lista.size() > 0) {
                metadata = new EntidadeMetaData(lista.get(0).getClass());
            }
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar Pesquisar!", ex.getMessage()));
            logger.debug(ex.getMessage());
        }
    }

}
