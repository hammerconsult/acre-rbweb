package br.com.webpublico.nfse.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.Calculo;
import br.com.webpublico.entidades.DAM;
import br.com.webpublico.entidades.ParcelaValorDivida;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoRelatorioApresentacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.nfse.domain.DeclaracaoMensalServico;
import br.com.webpublico.nfse.domain.NotaFiscal;
import br.com.webpublico.nfse.domain.dtos.FiltroRelatorioDeclaracaoMensalDTO;
import br.com.webpublico.nfse.domain.dtos.NotaFiscalSearchDTO;
import br.com.webpublico.nfse.enums.TipoMovimentoMensal;
import br.com.webpublico.nfse.exceptions.NfseValidacaoException;
import br.com.webpublico.nfse.facades.DeclaracaoMensalServicoFacade;
import br.com.webpublico.nfse.facades.NotaFiscalFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by William on 01/02/2018.
 */


@ManagedBean(name = "declaracaoMensalControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "declaracaoMensalListar", pattern = "/nfse/declaracao-mensal/listar/",
        viewId = "/faces/tributario/nfse/declaracaomensal/lista.xhtml"),
    @URLMapping(id = "declaracaoMensalVer", pattern = "/nfse/declaracao-mensal/ver/#{declaracaoMensalControlador.id}/",
        viewId = "/faces/tributario/nfse/declaracaomensal/visualizar.xhtml")
})
public class DeclaracaoMensalControlador extends PrettyControlador<DeclaracaoMensalServico> implements CRUD {

    @EJB
    private NotaFiscalFacade notaFiscalFacade;
    @EJB
    private DeclaracaoMensalServicoFacade declaracaoMensalServicoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private DAMFacade damFacade;
    private List<NotaFiscalSearchDTO> notasDeclaradas;
    private List<ResultadoParcela> parcelasDaDMS;
    private Map<ResultadoParcela, String> mapaNumeroDam;
    private boolean fiscalTributario;
    private List<Object> notasFiscaisSelecionadas = Lists.newArrayList();
    private List<ResultadoParcela> parcelasPesquisa;

    public DeclaracaoMensalControlador() {
        super(DeclaracaoMensalServico.class);
    }

    public List<Object> getNotasFiscaisSelecionadas() {
        return notasFiscaisSelecionadas;
    }

    public void setNotasFiscaisSelecionadas(List<Object> notasFiscaisSelecionadas) {
        this.notasFiscaisSelecionadas = notasFiscaisSelecionadas;
    }

    public List<NotaFiscalSearchDTO> getNotasDeclaradas() {
        return notasDeclaradas;
    }

    public void setNotasDeclaradas(List<NotaFiscalSearchDTO> notasDeclaradas) {
        this.notasDeclaradas = notasDeclaradas;
    }

    @Override
    public AbstractFacade getFacede() {
        return declaracaoMensalServicoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/nfse/declaracao-mensal/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<ResultadoParcela> getParcelasPesquisa() {
        return parcelasPesquisa;
    }

    public void setParcelasPesquisa(List<ResultadoParcela> parcelasPesquisa) {
        this.parcelasPesquisa = parcelasPesquisa;
    }

    @Override
    @URLAction(mappingId = "declaracaoMensalVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        fiscalTributario = declaracaoMensalServicoFacade.getUsuarioSistemaFacade().isFiscalTributario();
        parcelasDaDMS = Lists.newArrayList();
        notasDeclaradas = notaFiscalFacade.buscarNotaPorDeclaracaoEmpresa(selecionado.getPrestador().getId(), selecionado.getId());
        if (selecionado.getProcessoCalculo() != null) {
            for (Calculo calculo : selecionado.getProcessoCalculo().getCalculos()) {
                parcelasDaDMS.addAll(new ConsultaParcela()
                    .addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, calculo.getId())
                    .executaConsulta().getResultados());
            }
            carregarNumeroDam(parcelasDaDMS);
        }
    }

    private void carregarNumeroDam(List<ResultadoParcela> parcelas) {
        mapaNumeroDam = Maps.newHashMap();
        for (ResultadoParcela resultadoParcela : parcelas) {
            DAM dam = damFacade.recuperaUltimoDamParcela(resultadoParcela.getIdParcela());
            mapaNumeroDam.put(resultadoParcela, dam != null ? dam.getNumeroDAM() : "");
        }
    }

    public void imprimirDAM() {
        try {
            List<ResultadoParcela> parcelas = declaracaoMensalServicoFacade.recuperarDebitosDaDecalaracao(selecionado.getId(), selecionado.getPrestador().getId());
            List<DAM> dams = Lists.newArrayList();
            for (ResultadoParcela parcela : parcelas) {
                ParcelaValorDivida p = new ParcelaValorDivida();
                p.setId(parcela.getIdParcela());
                DAM dam = damFacade.recuperaDAMPeloIdParcela(parcela.getIdParcela());
                if (dam == null || dam.getValorTotal().compareTo(parcela.getValorTotal()) != 0) {
                    Date vencimento = parcela.getVencimento() == null || parcela.isVencido(new Date()) ? DataUtil.ultimoDiaMes(new Date()).getTime() : parcela.getVencimento();
                    dam = damFacade.geraDAM(p, vencimento);
                }
                dams.add(dam);
            }
            ImprimeDAM imprimeDAM = new ImprimeDAM();
            imprimeDAM.setGeraNoDialog(true);
            imprimeDAM.imprimirDamUnicoViaApi(dams);
        } catch (ValidacaoException onpe) {
            FacesUtil.printAllFacesMessages(onpe.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um problema no servidor, tente denovo, se o problema persistir entre em contato com o suporte");
            logger.error("Erro ao emitir o DAM da declaraçção: {}", e);
        }
    }

    public void imprimirRelatorio() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setApi("tributario/nfse/declaracao-mensal-servico/");
            dto.setNomeParametroBrasao("BRASAO");
            dto.setNomeRelatorio("encerramento-mensal-servico");
            FiltroRelatorioDeclaracaoMensalDTO filtro = new FiltroRelatorioDeclaracaoMensalDTO();
            filtro.setTipoRelatorioApresentacao(TipoRelatorioApresentacao.DETALHADO);
            filtro.setDeclaracaoMensalServico(selecionado);
            filtro.montarParametros(dto);
            dto.adicionarParametro("MUNICIPIO", "PREFEITURA DE RIO BRANCO");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE FINANÇAS");
            dto.adicionarParametro("DEPARTAMENTO", "DEPARTAMENTO DE TRIBUTAÇÃO");
            dto.adicionarParametro("USUARIO", SistemaFacade.obtemLogin());
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (ValidacaoException op) {
            FacesUtil.printAllFacesMessages(op.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public List<SelectItem> getTiposMovimento() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(TipoMovimentoMensal.NORMAL, TipoMovimentoMensal.NORMAL.getDescricao()));
        toReturn.add(new SelectItem(TipoMovimentoMensal.RETENCAO, TipoMovimentoMensal.RETENCAO.getDescricao()));
        toReturn.add(new SelectItem(TipoMovimentoMensal.ISS_RETIDO, TipoMovimentoMensal.ISS_RETIDO.getDescricao()));
        return toReturn;
    }

    public List<SelectItem> getMeses() {
        return Util.getListSelectItem(Mes.values(), false);
    }

    public List<ResultadoParcela> getParcelasDaDMS() {
        return parcelasDaDMS;
    }

    public void setParcelasDaDMS(List<ResultadoParcela> parcelasDaDMS) {
        this.parcelasDaDMS = parcelasDaDMS;
    }

    public Map<ResultadoParcela, String> getMapaNumeroDam() {
        return mapaNumeroDam;
    }

    public void cancelarEncerramento() {
        try {
            validarCancelamento();
            declaracaoMensalServicoFacade.cancelarDeclaracaoMensalServico(selecionado);
            FacesUtil.addOperacaoRealizada("Encerramento cancelado com sucesso!");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        } catch (NfseValidacaoException ne) {
            FacesUtil.addOperacaoNaoPermitida(ne.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void validarCancelamento() {
        if (declaracaoMensalServicoFacade.hasDebitoDiferenteEmAberto(selecionado)) {
            throw new ValidacaoException("Este encerramento contém débitos que não estão mais em aberto. " +
                "Por esse motivo não poderá ser feito o cancelamento.");
        }
    }

    public BigDecimal getTotalServicos() {
        BigDecimal totalServicos = BigDecimal.ZERO;
        if (notasDeclaradas != null) {
            for (NotaFiscalSearchDTO notaDeclarada : notasDeclaradas) {
                totalServicos = totalServicos.add(notaDeclarada.getTotalServicos());
            }
        }
        return totalServicos;
    }

    public BigDecimal getBaseCalculo() {
        BigDecimal baseCalculo = BigDecimal.ZERO;
        if (notasDeclaradas != null) {
            for (NotaFiscalSearchDTO notaDeclarada : notasDeclaradas) {
                baseCalculo = baseCalculo.add(notaDeclarada.getBaseCalculo());
            }
        }
        return baseCalculo;
    }

    public BigDecimal getIss() {
        BigDecimal iss = BigDecimal.ZERO;
        if (notasDeclaradas != null) {
            for (NotaFiscalSearchDTO notaDeclarada : notasDeclaradas) {
                iss = iss.add(notaDeclarada.getIssCalculado());
            }
        }
        return iss;
    }

    public boolean isPermitidoAlterarDados() {
        return fiscalTributario && !selecionado.getTipoMovimento().equals(TipoMovimentoMensal.INSTITUICAO_FINANCEIRA);
    }

    public void removerNotaFiscalEncerramento(NotaFiscalSearchDTO notaFiscal) {
        declaracaoMensalServicoFacade.removerNotaFiscalEncerramento(selecionado, notaFiscal);
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
    }

    public void iniciarPesquisaNotasFiscais() {
        notasFiscaisSelecionadas = Lists.newArrayList();
    }

    public void adicionarNotasFiscais() {
        if (notasFiscaisSelecionadas.isEmpty()) {
            return;
        }
        List<NotaFiscal> notasRecuperadas = Lists.newArrayList();
        for (Object selecionada : notasFiscaisSelecionadas) {
            Long id = ((BigDecimal) selecionada).longValue();
            notasRecuperadas.add(declaracaoMensalServicoFacade.getNotaFiscalFacade().recuperar(id));
        }
        declaracaoMensalServicoFacade.adicionarNotasFiscais(selecionado, notasRecuperadas);
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
    }

    public void removerDebitoEncerramento() {
        declaracaoMensalServicoFacade.removerDebitoEncerramento(selecionado);
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
    }

    public void pesquisarDebitos() {
        parcelasPesquisa = declaracaoMensalServicoFacade.buscarDebitosReferenteEncerramento(selecionado);
        carregarNumeroDam(parcelasPesquisa);
        FacesUtil.executaJavaScript("dlgPesquisaDebito.show()");
    }

    public void adicionarDebito() {
        ResultadoParcela parcelaSelecionada = getParcelaSelecionada();
        if (parcelaSelecionada == null) {
            FacesUtil.addOperacaoNaoPermitida("Nenhum débito selecionado.");
        }
        declaracaoMensalServicoFacade.vincularDebitoEncerramento(selecionado, parcelaSelecionada);
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
    }

    public void selecionarParcela(ResultadoParcela resultadoParcela) {
        for (ResultadoParcela parcela : parcelasPesquisa) {
            desconsiderarParcela(parcela);
        }
        resultadoParcela.setSelecionada(true);
    }

    public void desconsiderarParcela(ResultadoParcela resultadoParcela) {
        resultadoParcela.setSelecionada(false);
    }

    public ResultadoParcela getParcelaSelecionada() {
        for (ResultadoParcela resultadoParcela : parcelasPesquisa) {
            if (resultadoParcela.getSelecionada()) {
                return resultadoParcela;
            }
        }
        return null;
    }

}
