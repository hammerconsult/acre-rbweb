package br.com.webpublico.controle;


import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SituacaoContribuicaoMelhoria;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ContribuicaoMelhoriaLancamentoFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by William on 30/06/2016.
 */
@ManagedBean(name = "contribuicaoMelhoriaLancamentoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-contribuicao-lancamento", pattern = "/contribuicao-melhoria-lancamento/novo/", viewId = "/faces/tributario/contribuicaomelhoria/lancamento/edita.xhtml"),
    @URLMapping(id = "edita-contribuicao-lancamento", pattern = "/contribuicao-melhoria-lancamento/editar/#{contribuicaoMelhoriaLancamentoControlador.id}/", viewId = "/faces/tributario/contribuicaomelhoria/lancamento/edita.xhtml"),
    @URLMapping(id = "ver-contribuicao-lancamento", pattern = "/contribuicao-melhoria-lancamento/ver/#{contribuicaoMelhoriaLancamentoControlador.id}/", viewId = "/faces/tributario/contribuicaomelhoria/lancamento/visualizar.xhtml"),
    @URLMapping(id = "listar-contribuicao-lancamento", pattern = "/contribuicao-melhoria-lancamento/listar/", viewId = "/faces/tributario/contribuicaomelhoria/lancamento/lista.xhtml")
})

public class ContribuicaoMelhoriaLancamentoControlador extends PrettyControlador<ContribuicaoMelhoriaLancamento> implements CRUD {

    public List<Propriedade> propriedades;
    List<Future<AssistenteBarraProgresso>> futures;
    @EJB
    private ContribuicaoMelhoriaLancamentoFacade contribuicaoMelhoriaLancamentoFacade;
    private ContribuicaoMelhoriaItem item;
    private CadastroImobiliario cadastroImobiliario;
    private Quadra quadra;
    private Setor setor;
    private String cep;
    private List<Testada> listaTestadas;
    private String lote;
    private String distrito;
    private String unidade;
    private String setorQuery;
    private String quadraQuery;
    private BigDecimal areaAtingida;
    private Logradouro logradouro;
    private Bairro bairro;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private List<CadastroImobiliario> cadastroNaoAdicionadoNoProcesso;

    public ContribuicaoMelhoriaLancamentoControlador() {
        super(ContribuicaoMelhoriaLancamento.class);
        item = new ContribuicaoMelhoriaItem();
        propriedades = Lists.newArrayList();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/contribuicao-melhoria-lancamento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return contribuicaoMelhoriaLancamentoFacade;
    }

    public ContribuicaoMelhoriaItem getItem() {
        return item;
    }

    public void setItem(ContribuicaoMelhoriaItem item) {
        this.item = item;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public Logradouro getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(Logradouro logradouro) {
        this.logradouro = logradouro;
    }

    public Quadra getQuadra() {
        return quadra;
    }

    public void setQuadra(Quadra quadra) {
        this.quadra = quadra;
    }

    public BigDecimal getAreaAtingida() {
        return areaAtingida;
    }

    public void setAreaAtingida(BigDecimal areaAtingida) {
        this.areaAtingida = areaAtingida;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public Setor getSetor() {
        return setor;
    }

    public void setSetor(Setor setor) {
        this.setor = setor;
    }

    public List<Testada> getListaTestadas() {
        return listaTestadas;
    }

    public void setListaTestadas(List<Testada> listaTestadas) {
        this.listaTestadas = listaTestadas;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public void setContribuicaoMelhoriaLancamentoFacade(ContribuicaoMelhoriaLancamentoFacade contribuicaoMelhoriaLancamentoFacade) {
        this.contribuicaoMelhoriaLancamentoFacade = contribuicaoMelhoriaLancamentoFacade;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public List<Propriedade> getPropriedades() {
        return propriedades;
    }

    public void setPropriedades(List<Propriedade> propriedades) {
        this.propriedades = propriedades;
    }

    public String getSetorQuery() {
        return setorQuery;
    }

    public void setSetorQuery(String setorQuery) {
        this.setorQuery = setorQuery;
    }

    public String getQuadraQuery() {
        return quadraQuery;
    }

    public void setQuadraQuery(String quadraQuery) {
        this.quadraQuery = quadraQuery;
    }

    @URLAction(mappingId = "edita-contribuicao-lancamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        futures = Lists.newArrayList();
        cadastroNaoAdicionadoNoProcesso = Lists.newArrayList();
        if (SituacaoContribuicaoMelhoria.EFETIVADO.equals(selecionado.getSituacao())) {
            FacesUtil.addOperacaoNaoPermitida("Essa contribuição de melhoria está efetivada e não pode ser editada.");
            FacesUtil.redirecionamentoInterno("/contribuicao-melhoria-lancamento/ver/" + selecionado.getId() + "/");
        }
    }

    @URLAction(mappingId = "novo-contribuicao-lancamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setSituacao(SituacaoContribuicaoMelhoria.EM_ABERTO);
        futures = Lists.newArrayList();
        cadastroNaoAdicionadoNoProcesso = Lists.newArrayList();
    }

    @URLAction(mappingId = "ver-contribuicao-lancamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
    }

    public void selecionarObjetoPesquisaGenerico(ActionEvent e) {
        Object obj = e.getComponent().getAttributes().get("objeto");
        selecionado.setEdital((ContribuicaoMelhoriaEdital) obj);
    }

    public void adicionarCadastroImobiliario() {
        try {
            validarAdicionarCadastro();
            CadastroImobiliario cadastro = cadastroImobiliario;
            item.setCadastroImobiliario(cadastro);
            item.setContribuicaoMelhoriaLanc(selecionado);
            selecionado.getItens().add(item);
            this.cadastroImobiliario = new CadastroImobiliario();
            item = new ContribuicaoMelhoriaItem();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerCadastroImobiliario(ContribuicaoMelhoriaItem item) {
        selecionado.getItens().remove(item);
    }

    public List<Logradouro> completarLogradouro(String parte) {
        return contribuicaoMelhoriaLancamentoFacade.getLogradouroFacade().listaLogradourosAtivos(parte);
    }

    public List<CadastroImobiliario> getCadastroNaoAdicionadoNoProcesso() {
        return cadastroNaoAdicionadoNoProcesso;
    }

    public void setCadastroNaoAdicionadoNoProcesso(List<CadastroImobiliario> cadastroNaoAdicionadoNoProcesso) {
        this.cadastroNaoAdicionadoNoProcesso = cadastroNaoAdicionadoNoProcesso;
    }

    public List<Bairro> completarBairro(String parte) {
        return contribuicaoMelhoriaLancamentoFacade.getBairroFacade().completaBairro(parte);
    }

    public List<CadastroImobiliario> completarCadastroImobiliario(String parte) {
        return contribuicaoMelhoriaLancamentoFacade.getCadastroImobiliarioFacade().listaFiltrando(parte.trim(), "inscricaoCadastral");
    }


    private void validarAdicionarCadastro() {
        ValidacaoException ve = new ValidacaoException();
        if (cadastroImobiliario == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Cadastro Imobiliário.");

        }
        if (item.getAreaAtingida() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Área Atingida (m²).");
        }

        for (ContribuicaoMelhoriaItem contribuicaoMelhoriaLancamentoItem : selecionado.getItens()) {
            if (contribuicaoMelhoriaLancamentoItem.getCadastroImobiliario().equals(cadastroImobiliario)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Esse cadastro imobiliário já foi adicionado.");
            }
        }

        if (selecionado.getEdital() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Edital.");
        } else if (cadastroImobiliario != null && !contribuicaoMelhoriaLancamentoFacade.verificarSeCadastroJaFoiLancadoParaOEdital(selecionado.getEdital(), cadastroImobiliario)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já foi lançado contribuição de melhoria para esse imóvel no edital " + selecionado.getEdital().getDescricao());
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    @Override
    public boolean validaRegrasEspecificas() {
        if (selecionado.getItens().isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida("Adicione ao menos um Cadastro Imobiliário para continuar.");
            return false;
        }
        return true;
    }


    public void efetivar() {
        try {
            validarEfetivacao();
            FacesUtil.executaJavaScript("iniciarGeracaoCalculo()");
            selecionado.setSituacao(SituacaoContribuicaoMelhoria.EFETIVADO);
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            assistenteBarraProgresso.setExercicio(contribuicaoMelhoriaLancamentoFacade.getSistemaFacade().getExercicioCorrente());
            assistenteBarraProgresso.setTotal(selecionado.getItens().size());
            assistenteBarraProgresso.setDescricaoProcesso("Gerando os cálculos de Contribuição de Melhorias");
            assistenteBarraProgresso.setUsuarioSistema(contribuicaoMelhoriaLancamentoFacade.getSistemaFacade().getUsuarioCorrente());
            selecionado = contribuicaoMelhoriaLancamentoFacade.salvarContribuicaoMelhoria(selecionado);
            int partes = selecionado.getItens().size() > 5 ? (selecionado.getItens().size() / 5) : selecionado.getItens().size();
            List<List<ContribuicaoMelhoriaItem>> listaParte = Lists.partition(selecionado.getItens(), partes);
            for (List<ContribuicaoMelhoriaItem> itens : listaParte) {
                futures.add(contribuicaoMelhoriaLancamentoFacade.efetivar(itens, selecionado, assistenteBarraProgresso));
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void verificarTerminoGeracaoCalculo() {
        FacesUtil.atualizarComponente("meDaUpdate");
        boolean terminou = false;
        if (futures != null && !futures.isEmpty()) {
            terminou = true;
            for (Future<AssistenteBarraProgresso> future : futures) {
                if (!future.isDone()) {
                    terminou = false;
                    break;
                }
            }
        }

        if (terminou) {
            FacesUtil.executaJavaScript("terminouMesmo()");
        }
    }

    public void terminouMesmo() {
        efetivarDebitos();
    }

    private void efetivarDebitos() {
        try {

            List<CalculoContribuicaoMelhoria> calculos = contribuicaoMelhoriaLancamentoFacade.recuperarCalculoContribuicaoMelhoria(selecionado);
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            assistenteBarraProgresso.setExercicio(contribuicaoMelhoriaLancamentoFacade.getSistemaFacade().getExercicioCorrente());
            assistenteBarraProgresso.setTotal(calculos.size());
            assistenteBarraProgresso.setDescricaoProcesso("Efetivando os débitos de Contribuição de Melhorias");
            assistenteBarraProgresso.setUsuarioSistema(contribuicaoMelhoriaLancamentoFacade.getSistemaFacade().getUsuarioCorrente());
            int partes = calculos.size() > 5 ? (calculos.size() / 5) : calculos.size();
            List<List<CalculoContribuicaoMelhoria>> listaParte = Lists.partition(calculos, partes);
            for (List<CalculoContribuicaoMelhoria> itens : listaParte) {
                futures.add(contribuicaoMelhoriaLancamentoFacade.gerarDebitoValorDivida(itens, assistenteBarraProgresso));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void verificarTerminoEfetivacaoCalculo() {
        FacesUtil.atualizarComponente("meDaUpdate");
        boolean terminou = false;
        if (futures != null && !futures.isEmpty()) {
            terminou = true;
            for (Future<AssistenteBarraProgresso> future : futures) {
                if (!future.isDone()) {
                    terminou = false;
                    break;
                }
            }
        }
        if (terminou) {
            FacesUtil.executaJavaScript("terminarContribuicaoMelhoria()");
        }
    }

    public void validarEfetivacao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getItens().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Adicione ao menos um Cadastro Imobiliário para continuar.");
        }
        if (selecionado.getEdital() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Adicione ao menos um Edital para continuar.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }


    public void setarCadastroImobiliarioParaVisualizar(CadastroImobiliario cad) {
        cadastroImobiliario = cad;
        recuperarInformacoesCadastroImobiliario();
    }

    public void recuperarInformacoesCadastroImobiliario() {
        if (cadastroImobiliario.getLote() != null) {
            cadastroImobiliario.setLote(contribuicaoMelhoriaLancamentoFacade.getLoteFacade().recuperar(cadastroImobiliario.getLote().getId()));
            setQuadra(cadastroImobiliario.getLote().getQuadra());
            setSetor(cadastroImobiliario.getLote().getQuadra().getSetor());
            setListaTestadas(cadastroImobiliario.getLote().getTestadas());
            Testada testadaPrincipal = cadastroImobiliario.getLote().getTestadaPrincipal();
            if (testadaPrincipal != null) {
                Face faceTestadaPrincipal = testadaPrincipal.getFace();
                if (faceTestadaPrincipal != null) {
                    if (faceTestadaPrincipal.getLogradouroBairro() != null) {
                        cadastroImobiliario.setLogradouro(cadastroImobiliario.getLote().getTestadaPrincipal().getFace().getLogradouroBairro().getLogradouro());
                        cadastroImobiliario.setBairro(faceTestadaPrincipal.getLogradouroBairro().getBairro());
                        setCep(faceTestadaPrincipal.getCep());
                    }
                }
            }
            cadastroImobiliario = contribuicaoMelhoriaLancamentoFacade.getCadastroImobiliarioFacade().recuperarSemCalcular(cadastroImobiliario.getId());
            propriedades = cadastroImobiliario.getPropriedadeVigente();
        }
        FacesUtil.executaJavaScript("informacoescadastro.show()");
    }

    public void limpaCampos() {
        distrito = "";
        setorQuery = "";
        quadraQuery = "";
        lote = "";
        unidade = "";
        logradouro = null;
        bairro = null;
    }

    public void pesquisarCadastroImobiliario() {
        try {
            validarPesquisaCadastroImobiliario();
            List<CadastroImobiliario> cadastros = Lists.newArrayList();
            List<CadastroImobiliario> cadastrosRecuperados = contribuicaoMelhoriaLancamentoFacade.getCadastroImobiliarioFacade().buscarCadastroImobiliarioContribuicaoMelhoria(montaCondicao(), logradouro, bairro);
            if (cadastrosRecuperados != null) {
                for (CadastroImobiliario cadastrosRecuperado : cadastrosRecuperados) {
                    if (!contribuicaoMelhoriaLancamentoFacade.verificarSeCadastroJaFoiLancadoParaOEdital(selecionado.getEdital(), cadastrosRecuperado)) {
                        cadastroNaoAdicionadoNoProcesso.add(cadastrosRecuperado);
                    } else {
                        if (!selecionado.getItens().isEmpty()) {
                            for (ContribuicaoMelhoriaItem contribuicaoMelhoriaLancamentoItem : selecionado.getItens()) {
                                if (!contribuicaoMelhoriaLancamentoItem.getCadastroImobiliario().equals(cadastrosRecuperado)) {
                                    cadastros.add(cadastrosRecuperado);
                                }
                            }
                        } else {
                            cadastros.add(cadastrosRecuperado);
                        }
                    }
                }
            }
            adicionarCadastrosBuscaAvancada(cadastros);
            FacesUtil.addOperacaoRealizada("Foram adicionados " + cadastros.size() + " cadastros de acordo com os filtros informados.");
            FacesUtil.executaJavaScript("buscaAvancada.hide()");
        } catch (
            ValidacaoException ve
            )

        {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }

    }

    public void adicionarCadastrosBuscaAvancada(List<CadastroImobiliario> cadastros) {
        ContribuicaoMelhoriaItem itemContribuicaoMelhoria = new ContribuicaoMelhoriaItem();
        for (CadastroImobiliario cadastro : cadastros) {
            itemContribuicaoMelhoria.setCadastroImobiliario(cadastro);
            itemContribuicaoMelhoria.setAreaAtingida(areaAtingida);
            itemContribuicaoMelhoria.setContribuicaoMelhoriaLanc(selecionado);
            selecionado.getItens().add(itemContribuicaoMelhoria);
            itemContribuicaoMelhoria = new ContribuicaoMelhoriaItem();
        }

    }

    public void validarPesquisaCadastroImobiliario() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getEdital() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Edital.");
        }
        if (!distrito.trim().equals("") || !setorQuery.trim().equals("") || !quadraQuery.trim().equals("") || !lote.trim().equals("")) {
            if (distrito.trim().equals("")) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe o código do Distrito.");
            }
            if (setorQuery.trim().equals("")) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe o código do Setor.");
            }
            if (quadraQuery.trim().equals("")) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe o código do Quadra.");
            }
            if (lote.trim().equals("") && !unidade.trim().equals("")) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe o código do Lote.");
            }
        }

        if (areaAtingida == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Área Atingida (m²).");
        }

        if (ve.temMensagens()) {
            throw ve;
        }

    }

    public String montaCondicao() {
        StringBuilder where = new StringBuilder();
        if (!distrito.trim().equals("") && !setorQuery.trim().equals("") && !quadraQuery.trim().equals("") && !lote.trim().equals("") && !unidade.trim().equals("")) {
            where.append(distrito).append(setorQuery).append(quadraQuery).append(lote).append(unidade);
        } else if (!distrito.trim().equals("") && !setorQuery.trim().equals("") && !quadraQuery.trim().equals("") && !lote.trim().equals("")) {
            ajustaNumeros();
            where.append(distrito).append(setorQuery).append(quadraQuery).append(lote);
        } else if (!distrito.trim().equals("") && !setorQuery.trim().equals("") && !quadraQuery.trim().equals("")) {
            ajustaNumeros();
            where.append(distrito).append(setorQuery).append(quadraQuery);
        }

        return where.toString();
    }

    private void ajustaNumeros() {
        if (setorQuery.trim().length() < 3) {
            setorQuery = StringUtil.preencheString(setorQuery, 3, '0');
        }
        if (quadraQuery.trim().length() < 4) {
            quadraQuery = StringUtil.preencheString(quadraQuery, 4, '0');
        }
        if (!lote.trim().equals("") && lote.trim().length() < 4) {
            lote = StringUtil.preencheString(lote, 4, '0');
        }
        if (!unidade.trim().equals("") && unidade.trim().length() < 3) {
            unidade = StringUtil.preencheString(unidade, 3, '0');
        }
    }

}
