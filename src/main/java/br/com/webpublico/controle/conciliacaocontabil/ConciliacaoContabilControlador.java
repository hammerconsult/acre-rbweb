package br.com.webpublico.controle.conciliacaocontabil;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.entidadesauxiliares.contabil.conciliacaocontabil.ConciliacaoContabilVO;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.conciliacaocontabil.TipoConfigConciliacaoContabil;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.contabil.conciliacaocontabil.ConciliacaoContabilFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "conciliacao-contabil", pattern = "/conciliacao-contabil/", viewId = "/faces/financeiro/conciliacao-contabil/lista.xhtml")
})
public class ConciliacaoContabilControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ConciliacaoContabilControlador.class);
    @EJB
    private ConciliacaoContabilFacade facade;
    private Date dataInicial;
    private Date dataFinal;
    private List<ConciliacaoContabilVO> dados;
    private List<HierarquiaOrganizacional> hierarquias;

    @URLAction(mappingId = "conciliacao-contabil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        dataInicial = new Date();
        dataFinal = new Date();
        dados = Lists.newArrayList();
        limparUnidades();
    }

    public void limparUnidades() {
        hierarquias = Lists.newArrayList();
    }

    public void buscarValores() {
        try {
            validarCampos();
            dados = facade.montarDados(dataInicial, dataFinal, montarParametros());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro durante a busca dos valores. Detalhes: " + ex.getMessage());
            logger.error("Erro ao buscar os valores: {}", ex);
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado.");
        }
        ve.lancarException();
        if (dataInicial.after(dataFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Data Inicial deve ser igual ou anterior à Data Final.");
        }
        ve.lancarException();
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        List<Long> idsUnidades = Lists.newArrayList();
        if (!hierarquias.isEmpty()) {
            for (HierarquiaOrganizacional hierarquia : hierarquias) {
                idsUnidades.add(hierarquia.getSubordinada().getId());
            }
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
        } else {
            List<HierarquiaOrganizacional> hierarquiasDoUsuario = facade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", facade.getSistemaFacade().getUsuarioCorrente(), getExercicioDaDataFinal(), dataFinal, TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional hierarquia : hierarquiasDoUsuario) {
                idsUnidades.add(hierarquia.getSubordinada().getId());
            }
            if (!idsUnidades.isEmpty()) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            }
        }
        return parametros;
    }

    public Exercicio getExercicioDaDataFinal() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        return facade.getExercicioFacade().getExercicioPorAno(Integer.valueOf(format.format(dataFinal)));
    }

    public BigDecimal buscarTotalizadorConciliacaoPorQuadro(Integer quadro) {
        BigDecimal retorno = BigDecimal.ZERO;
        for (ConciliacaoContabilVO vo : dados) {
            if (vo.getQuadro().equals(quadro)) {
                retorno = retorno.add(vo.getValorConciliacao());
            }
        }
        return retorno;
    }

    public BigDecimal buscarTotalizadorIntercorrentePorQuadro(Integer quadro) {
        BigDecimal retorno = BigDecimal.ZERO;
        for (ConciliacaoContabilVO vo : dados) {
            if (vo.getQuadro().equals(quadro)) {
                retorno = retorno.add(vo.getValorIntercorrente());
            }
        }
        return retorno;
    }

    public BigDecimal buscarTotalizadorContabilPorQuadro(Integer quadro) {
        BigDecimal retorno = BigDecimal.ZERO;
        for (ConciliacaoContabilVO vo : dados) {
            if (vo.getQuadro().equals(quadro)) {
                retorno = retorno.add(vo.getValorContabil());
            }
        }
        return retorno;
    }

    public BigDecimal buscarTotalizadorConciliacaoPorQuadroETotalizador(Integer quadro, TipoConfigConciliacaoContabil totalizador) {
        BigDecimal retorno = BigDecimal.ZERO;
        for (ConciliacaoContabilVO vo : dados) {
            if (quadro.equals(vo.getQuadro()) && vo.getTotalizador().equals(totalizador)) {
                retorno = retorno.add(vo.getValorConciliacao());
            }
        }
        return retorno;
    }

    public BigDecimal buscarTotalizadorIntercorrentePorQuadroETotalizador(Integer quadro, TipoConfigConciliacaoContabil totalizador) {
        BigDecimal retorno = BigDecimal.ZERO;
        for (ConciliacaoContabilVO vo : dados) {
            if (quadro.equals(vo.getQuadro()) && vo.getTotalizador().equals(totalizador)) {
                retorno = retorno.add(vo.getValorIntercorrente());
            }
        }
        return retorno;
    }

    public BigDecimal buscarTotalizadorContabilPorQuadroETotalizador(Integer quadro, TipoConfigConciliacaoContabil totalizador) {
        BigDecimal retorno = BigDecimal.ZERO;
        for (ConciliacaoContabilVO vo : dados) {
            if (quadro.equals(vo.getQuadro()) && vo.getTotalizador().equals(totalizador)) {
                retorno = retorno.add(vo.getValorContabil());
            }
        }
        return retorno;
    }

    public List<Integer> buscarQuadros() {
        List<Integer> quadros = Lists.newArrayList();
        for (ConciliacaoContabilVO vo : dados) {
            Util.adicionarObjetoEmLista(quadros, vo.getQuadro());
        }
        return quadros;
    }

    public List<ConciliacaoContabilVO> buscarTotalizadores(Integer quadro) {
        Set<ConciliacaoContabilVO> totalizadores = Sets.newHashSet();
        for (ConciliacaoContabilVO vo : dados) {
            if (quadro.equals(vo.getQuadro())) {
                boolean canAdicionar = true;
                for (ConciliacaoContabilVO totalizador : totalizadores) {
                    if (vo.getTotalizador().equals(totalizador.getTotalizador()) && vo.getQuadro().equals(totalizador.getQuadro())) {
                        canAdicionar = false;
                        break;
                    }
                }
                if (canAdicionar) {
                    totalizadores.add(vo);
                }
            }
        }
        List<ConciliacaoContabilVO> retorno = Lists.newArrayList(totalizadores.iterator());
        ordernarDados(retorno);
        return retorno;
    }

    private void ordernarDados(List<ConciliacaoContabilVO> retorno) {
        Collections.sort(retorno, new Comparator() {
            public int compare(Object o1, Object o2) {
                ConciliacaoContabilVO s1 = (ConciliacaoContabilVO) o1;
                ConciliacaoContabilVO s2 = (ConciliacaoContabilVO) o2;

                if (s1.getQuadro() != null && s2.getQuadro() != null) {
                    int i = s1.getQuadro().compareTo(s2.getQuadro());
                    return i == 0 ? s1.getOrdem().compareTo(s2.getOrdem()) : i;
                } else {
                    return 0;
                }
            }
        });
    }

    public List<ConciliacaoContabilVO> buscarDadosPorTotalizador(Integer quadro, TipoConfigConciliacaoContabil totalizador) {
        List<ConciliacaoContabilVO> totalizadores = Lists.newArrayList();
        for (ConciliacaoContabilVO vo : dados) {
            if (quadro.equals(vo.getQuadro()) && vo.getTotalizador().equals(totalizador)) {
                totalizadores.add(vo);
            }
        }
        ordernarDados(totalizadores);
        return totalizadores;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public List<ConciliacaoContabilVO> getDados() {
        return dados;
    }

    public void setDados(List<ConciliacaoContabilVO> dados) {
        this.dados = dados;
    }

    public List<HierarquiaOrganizacional> getHierarquias() {
        return hierarquias;
    }

    public void setHierarquias(List<HierarquiaOrganizacional> hierarquias) {
        this.hierarquias = hierarquias;
    }
}
