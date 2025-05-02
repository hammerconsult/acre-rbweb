/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ConsultaTermoInscricaoDividaAtiva;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.TermoInscricaoDividaAtivaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Claudio
 */

@ManagedBean(name = "consultaTermoDividaAtivaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaConsultaDividaAtiva", pattern = "/tributario/dividaativa/termoinscricaoda/", viewId = "/faces/tributario/dividaativa/notificacao/termoinscricao.xhtml"),
})
public class ConsultaTermoDividaAtivaControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ConsultaTermoDividaAtivaControlador.class);

    @EJB
    private TermoInscricaoDividaAtivaFacade termoDividaAtivaFacade;
    private TipoCadastroTributario tipoCadastroTributario;
    private String exercicio;
    private Integer livroDividaAtiva;
    private String termoDividaAtivaInicial;
    private String termoDividaAtivaFinal;
    private String cadastroInicial;
    private String cadastroFinal;
    private Date dataInscricaoInicial;
    private Date dataInscricaoFinal;
    private ConverterAutoComplete converterExercicio;
    private ConverterAutoComplete converterTermoDividaAtiva;
    private ConverterAutoComplete converterDivida;
    private ConverterAutoComplete converterCadastroRural;
    private ConverterAutoComplete converterCadastroImobiliario;
    private ConverterAutoComplete converterCadastroEconomico;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterCadastroRendasPatrimoniais;
    //    private List<LinhaDoLivroDividaAtiva> listaDeLinha;
    private List<ConsultaTermoInscricaoDividaAtiva> consultaTermoInscricaoDividaAtiva;
    private Pessoa pessoa;
    private List<Divida> dividasSeleciondas;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private Divida divida;
    private Integer maxResult;
    private Integer inicio;

    public Integer getLivroDividaAtiva() {
        return livroDividaAtiva;
    }

    public void setLivroDividaAtiva(Integer livroDividaAtiva) {
        this.livroDividaAtiva = livroDividaAtiva;
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

    public String getExercicio() {
        return exercicio;
    }

    public void setExercicio(String exercicio) {
        this.exercicio = exercicio;
    }

    public Date getDataInscricaoFinal() {
        return dataInscricaoFinal;
    }

    public void setDataInscricaoFinal(Date dataInscricaoFinal) {
        this.dataInscricaoFinal = dataInscricaoFinal;
    }

    public Date getDataInscricaoInicial() {
        return dataInscricaoInicial;
    }

    public void setDataInscricaoInicial(Date dataInscricaoInicial) {
        this.dataInscricaoInicial = dataInscricaoInicial;
    }

    public String getTermoDividaAtivaFinal() {
        return termoDividaAtivaFinal;
    }

    public void setTermoDividaAtivaFinal(String termoDividaAtivaFinal) {
        this.termoDividaAtivaFinal = termoDividaAtivaFinal;
    }

    public String getTermoDividaAtivaInicial() {
        return termoDividaAtivaInicial;
    }

    public void setTermoDividaAtivaInicial(String termoDividaAtivaInicial) {
        this.termoDividaAtivaInicial = termoDividaAtivaInicial;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public List<Divida> getDividasSeleciondas() {
        return dividasSeleciondas;
    }

    public void setDividasSeleciondas(List<Divida> dividas) {
        this.dividasSeleciondas = dividas;
    }

    public List<SelectItem> tiposCadastros() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));
        for (TipoCadastroTributario tipoCadastro : TipoCadastroTributario.values()) {
            lista.add(new SelectItem(tipoCadastro, tipoCadastro.getDescricao()));
        }
        return lista;
    }

    public List<Exercicio> completaExercicio(String parte) {
        return termoDividaAtivaFacade.getExercicioFacade().listaFiltrandoEspecial(parte.trim());
    }

    public List<CadastroImobiliario> completaCadastroImobiliario(String parte) {
        return termoDividaAtivaFacade.completaCadastroImobiliario(parte.trim());
    }

    public List<CadastroEconomico> completaCadastroEconomico(String parte) {
        return termoDividaAtivaFacade.completaCadastroEconomico(parte.trim());
    }

    public List<CadastroRural> completaCadastroRural(String parte) {
        return termoDividaAtivaFacade.completaCadastroRural(parte.trim());
    }

    public List<ContratoRendasPatrimoniais> completaContratolRendasPatrimonial(String parte) {
        return termoDividaAtivaFacade.completaContratolRendasPatrimonial(parte.trim());
    }

    public ConverterAutoComplete getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterAutoComplete(Exercicio.class, termoDividaAtivaFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public List<Divida> completaDivida(String parte) {
        return termoDividaAtivaFacade.getDividaFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public ConverterAutoComplete getConverterDivida() {
        if (converterDivida != null) {
            converterDivida = new ConverterAutoComplete(Divida.class, termoDividaAtivaFacade.getDividaFacade());
        }
        return converterDivida;
    }

    public List<TermoInscricaoDividaAtiva> completaTermoDividaAtiva(String parte) {
        return termoDividaAtivaFacade.completaTermoDividaAtiva(parte.trim());
    }

    public ConverterAutoComplete getConverterTermoDividaAtiva() {
        if (converterTermoDividaAtiva == null) {
            converterTermoDividaAtiva = new ConverterAutoComplete(TermoInscricaoDividaAtiva.class, termoDividaAtivaFacade);
        }
        return converterTermoDividaAtiva;
    }

    public List<LivroDividaAtiva> completaLivroDividaAtiva(String parte) {
        return termoDividaAtivaFacade.getLivroDividaAtivaFacade().completaLivroDividaAtiva(parte.trim());
    }

    public ConverterAutoComplete getConverterCadastroImobiliario() {
        if (converterCadastroImobiliario == null) {
            converterCadastroImobiliario = new ConverterAutoComplete(CadastroImobiliario.class, termoDividaAtivaFacade.getCadastroImobiliarioFacade());
        }
        return converterCadastroImobiliario;
    }

    public ConverterAutoComplete getConverterCadastroEconomico() {
        if (converterCadastroEconomico == null) {
            converterCadastroEconomico = new ConverterAutoComplete(CadastroEconomico.class, termoDividaAtivaFacade.getCadastroEconomicoFacade());
        }
        return converterCadastroEconomico;
    }

    public ConverterAutoComplete getConverterContratoRendasPatrimoniais() {
        if (converterCadastroRendasPatrimoniais == null) {
            converterCadastroRendasPatrimoniais = new ConverterAutoComplete(ContratoRendasPatrimoniaisControlador.class, termoDividaAtivaFacade.getContratoRendasPatrimoniaisFacade());
        }
        return converterCadastroRendasPatrimoniais;
    }

    public ConverterAutoComplete getConverterCadastroRural() {
        if (converterCadastroRural == null) {
            converterCadastroRural = new ConverterAutoComplete(CadastroRural.class, termoDividaAtivaFacade.getCadastroRuralFacade());
        }
        return converterCadastroRural;
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, termoDividaAtivaFacade.getPessoa());
        }
        return converterPessoa;
    }

    public List<Pessoa> completaPessoa(String parte) {
        return termoDividaAtivaFacade.getPessoa().listaTodasPessoas(parte.trim());
    }

    public List<ConsultaTermoInscricaoDividaAtiva> getConsultaTermoInscricaoDividaAtiva() {
        return consultaTermoInscricaoDividaAtiva;
    }

    public void setConsultaTermoInscricaoDividaAtiva(List<ConsultaTermoInscricaoDividaAtiva> consultaTermoInscricaoDividaAtiva) {
        this.consultaTermoInscricaoDividaAtiva = consultaTermoInscricaoDividaAtiva;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public void filtrar() {
        consultaTermoInscricaoDividaAtiva = termoDividaAtivaFacade.filtrar(tipoCadastroTributario, cadastroInicial, cadastroFinal, exercicio, livroDividaAtiva, termoDividaAtivaInicial, termoDividaAtivaFinal, dataInscricaoInicial, dataInscricaoFinal, pessoa, dividasSeleciondas, maxResult, inicio);
        Collections.sort(consultaTermoInscricaoDividaAtiva);
    }

    public TipoDoctoOficial recuperaTipoDocumentoOficial(InscricaoDividaAtiva inscricao, Pessoa pessoa) {
        ParametrosDividaAtiva parametrosDividaAtivaPorExercicio = termoDividaAtivaFacade.getParametrosDividaAtivaFacade().parametrosDividaAtivaPorExercicio(inscricao.getExercicio());
        if (parametrosDividaAtivaPorExercicio == null) {
            throw new ExcecaoNegocioGenerica("Não foi possível gerar o livro de Dívida Ativa. Não existe parâmetro de Dívida Ativa para o exercicío de " + inscricao.getExercicio().getAno());
        }

        TipoDoctoOficial tipoDoctoOficial = new TipoDoctoOficial();
        if (inscricao.getTipoCadastroTributario().equals(TipoCadastroTributario.ECONOMICO)) {
            tipoDoctoOficial = parametrosDividaAtivaPorExercicio.getTipoDoctoOficialMobiliario();
        }
        if (inscricao.getTipoCadastroTributario().equals(TipoCadastroTributario.IMOBILIARIO)) {
            tipoDoctoOficial = parametrosDividaAtivaPorExercicio.getTipoDoctoOficialImobiliario();
        }
        if (inscricao.getTipoCadastroTributario().equals(TipoCadastroTributario.RURAL)) {
            tipoDoctoOficial = parametrosDividaAtivaPorExercicio.getTipoDoctoOficialRural();
        }
        if (inscricao.getTipoCadastroTributario().equals(TipoCadastroTributario.PESSOA)) {
            if (pessoa instanceof PessoaFisica) {
                tipoDoctoOficial = parametrosDividaAtivaPorExercicio.getTipoDoctoOficialContribPF();
            } else {
                tipoDoctoOficial = parametrosDividaAtivaPorExercicio.getTipoDoctoOficialContribPJ();
            }
        }
        return tipoDoctoOficial;
    }

    public DocumentoOficial geraDocumentoTermoInscricaoDividaAtiva(TipoDoctoOficial tipoDoctoOficial, LinhaDoLivroDividaAtiva linhaDoLivroDividaAtiva, TermoInscricaoDividaAtiva termo, Cadastro cadastro, Pessoa pessoa, SistemaControlador sistemaControlador) throws UFMException, AtributosNulosException {
        return termoDividaAtivaFacade.getDocumentoOficialFacade().geraDocumentoTermoInscricaoDividaAtiva(linhaDoLivroDividaAtiva, termo.getDocumentoOficial(), cadastro, pessoa, tipoDoctoOficial, sistemaControlador);
    }

    public void geraImprimirDocumentoOficial(ConsultaTermoInscricaoDividaAtiva consulta) {
        TermoInscricaoDividaAtiva termo = consulta.getTermo();
        try {
            termo.setDocumentoOficial(geraDocumentoTermoInscricaoDividaAtiva(recuperaTipoDocumentoOficial(consulta.getInscricao(), consulta.getPessoa()), consulta.getLinha(), termo, consulta.getCadastro(), consulta.getPessoa(), sistemaControlador));
            if (termo.getDocumentoOficial() != null) {
                termoDividaAtivaFacade.salvar(termo);
            }
        } catch (UFMException | AtributosNulosException ex) {
            logger.error("Erro: ", ex);
        }
    }

    @URLAction(mappingId = "novaConsultaDividaAtiva", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        limpaCadastro();
        exercicio = null;
        livroDividaAtiva = null;
        termoDividaAtivaInicial = null;
        termoDividaAtivaFinal = null;
        dataInscricaoInicial = null;
        dataInscricaoFinal = null;
        tipoCadastroTributario = null;
        consultaTermoInscricaoDividaAtiva = new ArrayList<>();
        pessoa = null;
        dividasSeleciondas = Lists.newArrayList();
        inicio = 0;
        maxResult = 10;
    }

    public void limpaCadastro() {
        cadastroInicial = "1";
        cadastroFinal = "999999999999999999";
    }

    public void copiarCadastroInicialParaCadastrofinal() {
        cadastroFinal = cadastroInicial;
    }

    public void copiarTermoInicialParaTermoFinal() {
        termoDividaAtivaFinal = termoDividaAtivaInicial;
    }

    public void copiarDataInscricaoInicialParaDataInscricaoFinal() {
        dataInscricaoFinal = dataInscricaoInicial;
    }

    public String recuperaNumeroCadastroDoItemInscricaoDividaAtiva(ItemInscricaoDividaAtiva item) {
        return termoDividaAtivaFacade.recuperaNumeroCadastroItemInscrica(item);
    }

    public List<SelectItem> getDividas() {
        List<SelectItem> toReturn = new ArrayList<>();
        if (tipoCadastroTributario != null) {
            List<Divida> dividas = tipoCadastroTributario.equals(TipoCadastroTributario.PESSOA) ? termoDividaAtivaFacade.getDividaFacade().lista() : termoDividaAtivaFacade.getDividaFacade().listaDividasPorTipoCadastro(tipoCadastroTributario);
            for (Divida divida : dividas) {
                toReturn.add(new SelectItem(divida, divida.getDescricao()));
            }
        }
        return toReturn;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public void removeDivida(Divida divida) {
        dividasSeleciondas.remove(divida);
    }

    public void addDivida() {
        if (divida != null && !dividasSeleciondas.contains(divida)) {
            dividasSeleciondas.add(divida);
        }
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public void proximaPagina() {
        inicio += maxResult;
        filtrar();
    }

    public void paginaAnterior() {
        inicio -= maxResult;
        filtrar();
    }

    public Integer getMaxResult() {
        return maxResult;
    }

    public Integer getInicio() {
        return inicio;
    }

    public void setMaxResult(Integer maxResult) {
        this.maxResult = maxResult;
    }

    public List<SelectItem> getTiposCadastro() {
        return TipoCadastroTributario.asSelectItemList();
    }
}
