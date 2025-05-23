package br.com.webpublico.negocios.jdbc;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TagValor;
import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fernando on 30/01/15.
 */
public final class ContabilizarPagamentoJDBC extends ClassPatternJDBC {

    private static ContabilizarPagamentoJDBC instancia;
    private ParametroEventoJDBC parametroEventoJDBC = ParametroEventoJDBC.createInstance(conexao);
    private OrigemContaContabilJDBC origemContaContabilJDBC = OrigemContaContabilJDBC.createInstance(conexao);
    private PreparedStatement psSelectConfiguracaoPagamento;

    private ContabilizarPagamentoJDBC(Connection conn) {
        this.conexao = conn;
    }

    public static synchronized ContabilizarPagamentoJDBC createInstance(Connection conn) {
        if (instancia == null) {
            instancia = new ContabilizarPagamentoJDBC(conn);
        }
        showConnection(instancia.getClass());
        return instancia;
    }

    public void contabilizarPagamento(Pagamento pag, TipoLancamento tipo) {
        if (allFieldsNotNull(pag, pag.getLiquidacao().getEmpenho().getClasseCredor(), tipo)) {
            try {
                ConfigPagamento conf = selectConfiguracaoPagamento(pag, tipo);
                pag.setEventoContabil(conf.getEventoContabil());
                pag.gerarHistoricos();

                ParametroEvento parametroEvento = new ParametroEvento();
                parametroEvento.setComplementoHistorico(pag.getHistoricoRazao());
                parametroEvento.setDataEvento(pag.getDataPagamento());
                parametroEvento.setUnidadeOrganizacional(pag.getUnidadeOrganizacional());
                parametroEvento.setEventoContabil(conf.getEventoContabil());
                parametroEvento.setExercicio(pag.getExercicio());
                parametroEvento.setClasseOrigem(pag.getClass().getSimpleName());
                parametroEvento.setIdOrigem(pag.getId().toString());

                ItemParametroEvento item = new ItemParametroEvento();
                item.setValor(pag.getValorFinal());
                item.setParametroEvento(parametroEvento);
                item.setTagValor(TagValor.LANCAMENTO);

                List<ObjetoParametro> listaObj = new ArrayList<ObjetoParametro>();
                listaObj.add(new ObjetoParametro((pag.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa()).getId().toString(), ContaDespesa.class.getSimpleName(), item));
                listaObj.add(new ObjetoParametro(pag.getSubConta().getId().toString(), SubConta.class.getSimpleName(), item));
                listaObj.add(new ObjetoParametro(pag.getLiquidacao().getEmpenho().getClasseCredor().getId().toString(), ClasseCredor.class.getSimpleName(), item));
                listaObj.add(new ObjetoParametro(pag.getLiquidacao().getEmpenho().getDividaPublica().getCategoriaDividaPublica().getId().toString(), CategoriaDividaPublica.class.getSimpleName(), item));
                item.setObjetoParametros(listaObj);
                parametroEvento.getItensParametrosEvento().add(item);

                parametroEventoJDBC.contabilizarParametroEvento(parametroEvento);
            } catch (SQLException ex) {
                throw new ExcecaoNegocioGenerica("Erro ao contabilizar pagamento: " + ex.getMessage(), ex);
            }
        }
    }

    private ConfigPagamento selectConfiguracaoPagamento(Pagamento pag, TipoLancamento tipo) throws SQLException {
        List<ConfigPagamento> configs = new ArrayList<>();
        preparaSelectConfiguracaoPagamento();
        psSelectConfiguracaoPagamento.clearParameters();
        psSelectConfiguracaoPagamento.setDate(1, new java.sql.Date(pag.getDataPagamento().getTime()));
        psSelectConfiguracaoPagamento.setString(2, pag.getLiquidacao().getEmpenho().getTipoContaDespesa().toString());
        psSelectConfiguracaoPagamento.setString(3, tipo.toString());
        psSelectConfiguracaoPagamento.setLong(4, pag.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getId());
        try (ResultSet rs = psSelectConfiguracaoPagamento.executeQuery()) {
            while (rs.next()) {
                ConfigPagamento cp = new ConfigPagamento();
                cp.setId(rs.getLong("id"));
                cp.setTipoContaDespesa(TipoContaDespesa.valueOf(rs.getString("tipocontadespesa")));
                cp.setInicioVigencia(rs.getDate("iniciovigencia"));
                cp.setFimVigencia(rs.getDate("fimvigencia"));
                cp.setTipoLancamento(tipo);
                cp.setEventoContabil(origemContaContabilJDBC.selectEventoContabilById(rs.getLong("eventocontabil")));
                configs.add(cp);
            }
            if (configs.size() > 1) {
                throw new ExcecaoNegocioGenerica("Mais de uma configuração para o pagamento: " + pag.getNumeroPagamento());
            } else {
                return configs.get(0);
            }
        } catch (SQLException ex) {
            throw new ExcecaoNegocioGenerica("Erro ao recuperar configuração de pagamento: " + ex.getMessage(), ex);
        }
    }

    private void preparaSelectConfiguracaoPagamento() throws SQLException {
        if (psSelectConfiguracaoPagamento == null) {
            psSelectConfiguracaoPagamento = this.conexao.prepareStatement(Queries.selectConfigPagamento());
        }
    }
}
