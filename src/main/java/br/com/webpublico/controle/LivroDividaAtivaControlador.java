/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.LivroDividaAtivaRelatorio;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.LivroDividaAtiva.TipoOrdenacao;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.LivroDividaAtivaFacade;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Renato
 */
@ManagedBean(name = "livroDividaAtivaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "gerarLivroDA",
        pattern = "/livro-de-divida-ativa/gerar-por-cda/#{livroDividaAtivaControlador.idItemInscricaoDividaAtiva}/",
        viewId = "/faces/tributario/dividaativa/livrodividaativa/gerar.xhtml"),
    @URLMapping(id = "novoLivroDA",
        pattern = "/livro-de-divida-ativa/gerar/",
        viewId = "/faces/tributario/dividaativa/livrodividaativa/gerar.xhtml"),
    @URLMapping(id = "listaLivroDA",
        pattern = "/livro-de-divida-ativa/listar/",
        viewId = "/faces/tributario/dividaativa/livrodividaativa/lista.xhtml"),
    @URLMapping(id = "verLivroDA",
        pattern = "/livro-de-divida-ativa/ver/#{livroDividaAtivaControlador.id}/",
        viewId = "/faces/tributario/dividaativa/livrodividaativa/visualizar.xhtml")

})
public class LivroDividaAtivaControlador extends PrettyControlador<LivroDividaAtiva> implements Serializable, CRUD {

    @EJB
    private LivroDividaAtivaFacade livroDividaAtivaFacade;
    private ConverterExercicio converterExercicio;
    private ConverterAutoComplete converterInscricaoDividaAtiva;
    private ConverterAutoComplete dividaConverter;
    private InscricaoDividaAtiva inscricaoDividaAtiva;
    private boolean bloqueiaCampos;
    private Exercicio exercicio;
    private TipoCadastroTributario tipoCadastro;
    private List<ItemLivroDividaAtiva> inscricoes;
    private List<LivroDividaAtiva> livros;
    private LivroDividaAtiva.TipoOrdenacao tipoOrdenacao;
    private LivroDividaAtivaRelatorio livroDividaAtivaRelatorio;
    private List<Divida> listaDividas;
    private Divida divida;
    private String cadastroInicial, cadastroFinal;
    private Pessoa pessoa;
    private Long idItemInscricaoDividaAtiva;

    public LivroDividaAtivaControlador() {
        metadata = new EntidadeMetaData(LivroDividaAtiva.class);
    }

    public Long getIdItemInscricaoDividaAtiva() {
        return idItemInscricaoDividaAtiva;
    }

    public void setIdItemInscricaoDividaAtiva(Long idItemInscricaoDividaAtiva) {
        this.idItemInscricaoDividaAtiva = idItemInscricaoDividaAtiva;
    }

    @Override
    public AbstractFacade getFacede() {
        return livroDividaAtivaFacade;
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public ConverterExercicio getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterExercicio(livroDividaAtivaFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public Converter getDividaConverter() {
        if (dividaConverter == null) {
            dividaConverter = new ConverterAutoComplete(Divida.class, livroDividaAtivaFacade.getDividaFacade());
        }
        return dividaConverter;
    }

    public ConverterAutoComplete converteInscricaoDividaAtiva() {
        if (converterInscricaoDividaAtiva == null) {
            converterInscricaoDividaAtiva = new ConverterAutoComplete(InscricaoDividaAtiva.class, livroDividaAtivaFacade.getInscricaoDividaAtivaFacade());
        }
        return converterInscricaoDividaAtiva;
    }

    public List<Exercicio> completaExercicio(String parte) {
        return livroDividaAtivaFacade.completaExercicio(parte.trim());
    }

    public List<InscricaoDividaAtiva> completaInscricaoDividaAtiva(String parte) {
        return livroDividaAtivaFacade.completaInscricaoDividaAtiva(parte.trim());
    }

    public InscricaoDividaAtiva getInscricaoDividaAtiva() {
        return inscricaoDividaAtiva;
    }

    public void setInscricaoDividaAtiva(InscricaoDividaAtiva inscricaoDividaAtiva) {
        this.inscricaoDividaAtiva = inscricaoDividaAtiva;
    }

    public boolean isBloqueiaCampos() {
        return bloqueiaCampos;
    }

    public void setBloqueiaCampos(boolean bloqueiaCampos) {
        this.bloqueiaCampos = bloqueiaCampos;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public String getCadastroFinal() {
        return cadastroFinal;
    }

    public void setCadastroFinal(String cadastroFinal) {
        this.cadastroFinal = cadastroFinal;
    }

    public String getCadastroInicial() {
        return cadastroInicial;
    }

    public void setCadastroInicial(String cadastroInicial) {
        this.cadastroInicial = cadastroInicial;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<SelectItem> getTiposDeDividas() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));
        for (Divida tipo : listaDividas) {
            lista.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return lista;
    }

    public List<Divida> recuperaDividas() {
        if (tipoCadastro != null) {
            try {
                listaDividas = livroDividaAtivaFacade.getInscricaoDividaAtivaFacade().buscarDividasPorTipoCadastroTributario(tipoCadastro);
                return listaDividas;
            } catch (Exception ex) {
                return new ArrayList<Divida>();
            }
        }
        return new ArrayList<Divida>();
    }

    public void carregaProcessos() {
        if (exercicio == null) {
            FacesUtil.addError("Erro ao tentar recupera os livros!", "Informe o exercício.");
            return;
        }
        if (tipoCadastro == null) {
            FacesUtil.addError("Erro ao tentar recupera os livros!", "Informe o tipo de cadastro.");
            return;
        }
        if (tipoCadastro.PESSOA.equals(tipoCadastro)) {
            if (pessoa == null) {
                FacesUtil.addError("Erro ao tentar recuperar os Livros!", "Informe o Contribuinte.");
                return;
            }
        }
        bloqueiaCampos = true;
        livros = new ArrayList<>();
        List<InscricaoDividaAtiva> inscricoes = livroDividaAtivaFacade.completaInscricaoDividaAtiva(tipoCadastro, exercicio, divida, cadastroInicial, cadastroFinal, pessoa);
        livros = geraLivros(inscricoes, exercicio, tipoCadastro, divida, livros);
    }

    public List<LivroDividaAtiva> geraLivros(List<InscricaoDividaAtiva> inscricoes, Exercicio exercicio, TipoCadastroTributario tipoCadastro, Divida divida, List<LivroDividaAtiva> livros) {
        for (InscricaoDividaAtiva inscricao : inscricoes) {
            livros = geraLivros(inscricao, exercicio, tipoCadastro, divida, livros);
        }
        return livros;
    }

    public List<LivroDividaAtiva> geraLivros(InscricaoDividaAtiva inscricao, Exercicio exercicio, TipoCadastroTributario tipoCadastro, Divida divida, List<LivroDividaAtiva> livros) {
        List<Divida> dividas = Lists.newArrayList();
        if (divida == null) {
            dividas = livroDividaAtivaFacade.getInscricaoDividaAtivaFacade().recuperarDividasDosItensDeIncricao(inscricao);
        } else {
            dividas.add(divida);
        }

        for (Divida div : dividas) {
            if (div.getNrLivroDividaAtiva() != null) {
                LivroDividaAtiva livro = new LivroDividaAtiva();
                livro.setExercicio(exercicio);
                livro.setTipoCadastroTributario(tipoCadastro);

                livro.setNumero(Long.parseLong(div.getNrLivroDividaAtiva()));
                livro = recuperaLivroComNumero(livro);

                ItemLivroDividaAtiva itemLivroDividaAtiva = new ItemLivroDividaAtiva();
                itemLivroDividaAtiva.setInscricaoDividaAtiva(inscricao);
                itemLivroDividaAtiva.setLivroDividaAtiva(livro);

                livro.getItensLivros().add(itemLivroDividaAtiva);

                boolean inserido = false;
                for (LivroDividaAtiva adicionado : livros) {
                    if (adicionado.getNumero().equals(livro.getNumero())) {
                        inserido = true;
                        itemLivroDividaAtiva.setLivroDividaAtiva(adicionado);
                        adicionado.getItensLivros().add(itemLivroDividaAtiva);

                    }
                }
                if (!inserido) {
                    livros.add(livro);
                }
            }
        }
        return livros;
    }

    public List<ItemLivroDividaAtiva> getInscricoes() {
        return inscricoes;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/livro-de-divida-ativa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "verLivroDA", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        livroDividaAtivaRelatorio = new LivroDividaAtivaRelatorio(selecionado.getId());
    }

    @Override
    @URLAction(mappingId = "novoLivroDA", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        inscricaoDividaAtiva = null;
        bloqueiaCampos = false;
        exercicio = null;
        inscricoes = new ArrayList<>();
        livros = new ArrayList<>();
        tipoCadastro = null;
        listaDividas = new ArrayList<>();
        divida = null;
        tipoOrdenacao = null;
        cadastroInicial = "1";
        cadastroFinal = "999999999999999999";
    }

    @URLAction(mappingId = "gerarLivroDA", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void gerarPorIdItemInscricaoDividaAtiva() {
        novo();
        ItemInscricaoDividaAtiva item = livroDividaAtivaFacade.getInscricaoDividaAtivaFacade().recuperarItemInscricaoDivida(idItemInscricaoDividaAtiva);
        bloqueiaCampos = false;
        exercicio = item.getInscricaoDividaAtiva().getExercicio();
        tipoCadastro = item.getInscricaoDividaAtiva().getTipoCadastroTributario();
        recuperaDividas();
        divida = item.getDivida();
        tipoOrdenacao = null;
        cadastroInicial = item.getCadastro().getNumeroCadastro();
        cadastroFinal = item.getCadastro().getNumeroCadastro();
    }

    public void adicionarItem() {
        if (validaProcessoJaAdicionado()) {
            ItemLivroDividaAtiva itemLivroDividaAtiva = new ItemLivroDividaAtiva();
            itemLivroDividaAtiva.setInscricaoDividaAtiva(inscricaoDividaAtiva);
            inscricaoDividaAtiva = null;
        }
    }

    public void remover(LivroDividaAtiva livro) {
        livroDividaAtivaFacade.remover(livro);
    }

    public TipoOrdenacao getTipoOrdenacao() {
        return tipoOrdenacao;
    }

    public void setTipoOrdenacao(TipoOrdenacao tipoOrdenacao) {
        this.tipoOrdenacao = tipoOrdenacao;
    }

    private boolean validaExclusao() {
        return true;
    }

    private boolean validaProcessoJaAdicionado() {
        return true;
    }

    public List getLista() {
        return livroDividaAtivaFacade.lista();
    }

    public void processar() {
        salvarTodos();
        for (LivroDividaAtiva livro : livros) {
            processar(livro);
        }
    }

    public void salvarTodos() {
        livros = livroDividaAtivaFacade.salvaLivro(livros);
    }

    public void removeLivroDaInscricao(LivroDividaAtiva livro) {
        livros.remove(livro);
    }

    public void removeItemDoLivroDaInscricao(LivroDividaAtiva livro, ItemLivroDividaAtiva item) {
        livro.getItensLivros().remove(item);
    }

    public void processar(LivroDividaAtiva livro) {
        ResultadoValidacao resultado = null;
        if (this.tipoOrdenacao != null) {
            try {
                resultado = livroDividaAtivaFacade.processar(livro, tipoOrdenacao, divida, cadastroInicial, cadastroFinal, pessoa);
                livro = livroDividaAtivaFacade.recuperar(resultado.getIdObjetoSalvo());
            } catch (ExcecaoNegocioGenerica ex) {
                FacesUtil.addError("Impossível continuar!", ex.getMessage());
            } catch (AtributosNulosException ex) {
                FacesUtil.addError("Impossível continuar!", ex.getMessage());
            }
            if (resultado != null) {
                if (resultado.isValidado()) {
                    FacesUtil.addInfo("Livro gerado com sucesso!", "Foram inscritas " + livro.getTotalPaginas() + " páginas no Livro  " + livro.getNumero());
                    FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + livro.getId() + "/");

                } else {
                    FacesUtil.printAllMessages(resultado.getMensagens());
                }
            }
        } else {
            FacesUtil.addError("Impossível continuar!", "Selecione o tipo de ordenação do livro de Dívida Ativa.");
        }

    }

    public void emitir() {
        livroDividaAtivaRelatorio.setGerando(true);
        livroDividaAtivaRelatorio.setGerado(false);
        livroDividaAtivaFacade.geraRelatorio(livroDividaAtivaRelatorio);
        FacesUtil.atualizarComponente("enviandoDialog");
        FacesUtil.executaJavaScript("enviando.show()");
    }

    public List<SelectItem> getTiposDeCadastroTributario() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));
        for (TipoCadastroTributario tipo : TipoCadastroTributario.values()) {
            lista.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return lista;
    }

    public LivroDividaAtiva recuperaLivroComNumero(LivroDividaAtiva livro) {
        LivroDividaAtiva livroAntigo = livroDividaAtivaFacade.recuperaLivroComNumero(livro);
        if (livroAntigo != null && livroAntigo.getItensLivros() != null) {
            for (ItemLivroDividaAtiva itemLivroDividaAtiva : livro.getItensLivros()) {
                livroAntigo.getItensLivros().add(itemLivroDividaAtiva);
            }
            livro = livroAntigo;
            bloqueiaCampos = true;
        }
        return livro;
    }

    public boolean getVisibilidadeDoProcessar() {
        return false;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public TipoCadastroTributario getTipoCadastro() {
        return tipoCadastro;
    }

    public void setTipoCadastro(TipoCadastroTributario tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public List<LivroDividaAtiva> getLivros() {
        return livros;
    }

    private boolean validaCamposPreenchidosDoTermo() {
        return true;
    }

    public List<SelectItem> getTipoOrdenacaoEnum() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));
        for (LivroDividaAtiva.TipoOrdenacao tipo : LivroDividaAtiva.TipoOrdenacao.values()) {
            lista.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return lista;
    }

    public void imprime() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.responseComplete();
        byte[] bytes = livroDividaAtivaRelatorio.getDadosByte().toByteArray();
        if (bytes != null && bytes.length > 0) {
            try {
                HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
                response.setContentType("application/pdf");
                response.setHeader("Content-disposition", "inline; filename=\"Livro de Dívida Ativa.pdf\"");
                response.setContentLength(bytes.length);
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.write(bytes, 0, bytes.length);
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                FacesUtil.addError("Operação não realizada", "Ocorreu um problema de comunicação com o servidor");
                logger.error(e.getMessage());
            }
        }
    }

    public LivroDividaAtivaRelatorio getLivroDividaAtivaRelatorio() {
        return livroDividaAtivaRelatorio;
    }

    public void setLivroDividaAtivaRelatorio(LivroDividaAtivaRelatorio livroDividaAtivaRelatorio) {
        this.livroDividaAtivaRelatorio = livroDividaAtivaRelatorio;
    }
}
