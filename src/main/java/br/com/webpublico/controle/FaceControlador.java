/*
 * Codigo gerado automaticamente em Tue Mar 01 14:57:55 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.FaceFacade;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ManagedBean(name = "faceControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoFace", pattern = "/face/novo/", viewId = "/faces/tributario/cadastromunicipal/face/edita.xhtml"),
    @URLMapping(id = "editarFace", pattern = "/face/editar/#{faceControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/face/edita.xhtml"),
    @URLMapping(id = "listarFace", pattern = "/face/listar/", viewId = "/faces/tributario/cadastromunicipal/face/lista.xhtml"),
    @URLMapping(id = "verFace", pattern = "/face/ver/#{faceControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/face/visualizar.xhtml")
})
public class FaceControlador extends PrettyControlador<Face> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(FaceControlador.class);

    @EJB
    private FaceFacade faceFacade;
    private ConverterAutoComplete converterLogradouro;
    private ConverterExercicio converterExercicio;
    private ConverterGenerico converterServicoUrbano;
    private ConverterAutoComplete converterBairro;
    private Converter converterSetor;
    private ConverterAutoComplete converterQuadra;
    private MoneyConverter moneyConverter;
    private Operacoes operacao;
    private List<Number> bairrosSelecionados = Lists.newArrayList();

    private FormFace form;

    public FormFace getForm() {
        return form;
    }

    public void setForm(FormFace form) {
        this.form = form;
    }

    public Operacoes getOperacao() {
        return operacao;
    }

    public void setOperacao(Operacoes operacao) {
        this.operacao = operacao;
    }


    public FaceControlador() {
        super(Face.class);
    }

    public String getCaminhoPadrao() {
        return "/face/";
    }

    public String getUrlAtual() {
        return PrettyContext.getCurrentInstance().getRequestURL().toString();
    }


    public Object getUrlKeyValue() {
        return form.getFace().getId();
    }

    public void novaFacesServicos() {
        form.getFaceServico().setFace(form.getFace());
        if (validaFaceSevico()) {
            form.getFace().getFaceServicos().add(form.getFaceServico());
            form.setFaceServico(new FaceServico());
            Collections.sort(form.getFace().getFaceServicos());
        }
    }

    public List<Number> getBairrosSelecionados() {
        return bairrosSelecionados;
    }

    public void definirBairro() {
        if (!bairrosSelecionados.isEmpty()) {
            form.bairro = faceFacade.getBairroFacade().recuperar(bairrosSelecionados.get(0).longValue());
        }
    }

    public void novaFacesValores() {
        form.getFaceValor().setFace(form.getFace());
        if (validaFaceValor()) {
            form.getFace().getFaceValores().add(form.getFaceValor());
            Collections.sort(form.getFace().getFaceServicos());
            form.setFaceValor(new FaceValor());
            form.setSetor(new Setor());
        }
    }

    @URLAction(mappingId = "novoFace", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        form = (FormFace) Web.pegaDaSessao(FormFace.class);
        if (form == null) {
            form = new FormFace();
            form.setFaceServico(new FaceServico());
            form.setFaceServicoAux(new FaceServico());
            form.setFaceServicos(new ArrayList<FaceServico>());
            form.setFaceValor(new FaceValor());
            form.setLogradouro(new Logradouro());
            form.setSetor(new Setor());
            form.setCep("");
        }

        bairrosSelecionados = Lists.newArrayList();
    }

    @URLAction(mappingId = "verFace", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        editar();
    }

    @URLAction(mappingId = "editarFace", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        form = (FormFace) Web.pegaDaSessao(FormFace.class);
        if (form == null) {
            form = new FormFace();
            form.setFace(faceFacade.recuperar(getId()));
            if (form.getFace().getLogradouroBairro() != null) {
                form.setBairro(form.getFace().getLogradouroBairro().getBairro());
                form.setLogradouro(form.getFace().getLogradouroBairro().getLogradouro());
                form.setCep(form.getFace().getLogradouroBairro().getCep());
            } else {
                form.setBairro(new Bairro());
                form.setLogradouro(new Logradouro());
                form.setCep("");
            }
            form.setFaceServico(new FaceServico());
            form.setFaceServicoAux(new FaceServico());
            form.setFaceServicos(new ArrayList<FaceServico>());
            form.setFaceValor(new FaceValor());
            form.setSetor(new Setor());
        }
        Collections.sort(form.getFace().getFaceServicos());
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            try {
                if (operacao == Operacoes.NOVO) {
                    getFacede().salvarNovo(form.getFace());
                } else {
                    getFacede().salvar(form.getFace());
                }
                FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), "Registro salvo com sucesso.");
            } catch (ValidacaoException ex) {
                FacesUtil.printAllFacesMessages(ex.getMensagens());
                return;
            } catch (Exception e) {
                descobrirETratarException(e);
            }
            redireciona();
        }
    }

    public void redireciona() {
        CRUD c = (CRUD) this;
        FacesUtil.navegaEmbora(form, c.getCaminhoPadrao());
    }

    @Override
    public void cancelar() {
        Web.getEsperaRetorno();
        redireciona();
    }

    @Override
    public void excluir() {
        try {
            getFacede().remover(form.getFace());
            redireciona();
            FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), "Registro salvo com sucesso.");
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    protected void descobrirETratarException(Exception e) {
        try {
            Util.getRootCauseDataBaseException(e);
        } catch (SQLIntegrityConstraintViolationException sql) {
            try {
                FacesUtil.addWarn(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), getMensagemMotivoNaoExclusao(sql.getMessage()));
            } catch (Exception e1) {
                logger.error(e1.getMessage());
            }
        } catch (Exception ex) {
            logger.error("Erro:", ex);
            FacesUtil.addWarn("Não foi possível realizar a exclusão!", "");
        }
    }

    public String getMensagemMotivoNaoExclusao(String msg)  {
        try {
            String dependencia = getFacede().getNomeDependencia(msg);
            return "A Face possui dependência(s) com " + dependencia + ".";

        } catch (Exception e) {
            return "Este registro possui dependência(s).";
        }
    }

    private boolean validaCampos() {
        boolean retorno = true;

        if (form.getBairro() == null) {
            retorno = false;
            FacesUtil.addWarn("Atenção!", "O Bairro é um campo obrigatório.");
        }
        if (form.getFace().getLogradouroBairro() == null) {
            retorno = false;
            FacesUtil.addWarn("Atenção!", "O Logradouro é um campo obrigatório.");
        }
        if (form.getFace().getLargura() == null || form.getFace().getLargura() < 0) {
            retorno = false;
            FacesUtil.addWarn("Atenção!", "A Largura(m) é um campo obrigatório.");
        }
        if (form.getFace().getCodigoFace().trim().equals("")) {
            retorno = false;
            FacesUtil.addWarn("Atenção!", "A Face é um campo obrigatório.");
        }
        if (form.getFace().getLado() == null || form.getFace().getLado().trim().equals("")) {
            retorno = false;
            FacesUtil.addWarn("Atenção!", "O Lado é um campo obrigatório.");
        }
        if (form.getFace().getFaceServicos().isEmpty()) {
            retorno = false;
            FacesUtil.addWarn("Atenção!", "É necessário cadastrar ao menos um serviço.");
        }
        return retorno;
    }

    public void removeFaceServico(FaceServico faceServico) {
        form.getFace().getFaceServicos().remove(faceServico);
    }

    public void removeFaceValores(FaceValor faceValor) {
        form.getFace().getFaceValores().remove(faceValor);
    }

    public void alteraFaceServico(ActionEvent evento) {
        form.setFaceServicoAux((FaceServico) evento.getComponent().getAttributes().get("alteraFaceServico"));
    }

    public FaceFacade getFacade() {
        return faceFacade;
    }


    public AbstractFacade getFacede() {
        return faceFacade;
    }

    public List<SelectItem> getServicoUrbano() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (ServicoUrbano object : faceFacade.getServicoUrbanoFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public ConverterExercicio getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterExercicio(faceFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public Converter getConverterLogradouro() {
        if (converterLogradouro == null) {
            converterLogradouro = new ConverterAutoComplete(Logradouro.class, faceFacade.getLogradouroFacade());
        }
        return converterLogradouro;
    }

    public Converter getConverterServicoUrbano() {
        if (converterServicoUrbano == null) {
            converterServicoUrbano = new ConverterGenerico(ServicoUrbano.class, faceFacade.getServicoUrbanoFacade());
        }
        return converterServicoUrbano;
    }

    public Converter getConverterBairro() {
        if (converterBairro == null) {
            converterBairro = new ConverterAutoComplete(Bairro.class, faceFacade.getBairroFacade());
        }
        return converterBairro;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public Converter getConverterSetor() {
        if (converterSetor == null) {
            converterSetor = new ConverterGenerico(Setor.class, faceFacade.getSetorFacade());
        }
        return converterSetor;
    }

    public Converter getConverterQuadra() {
        if (converterQuadra == null) {
            converterQuadra = new ConverterAutoComplete(Quadra.class, faceFacade.getQuadraFacade());
        }
        return converterQuadra;
    }

    public List<Logradouro> completaLogradouro(String parte) {
        return faceFacade.getLogradouroFacade().listarPorBairro(form.getBairro(), parte.trim());

    }

    public List<Bairro> completaBairro(String parte) {
        return faceFacade.getBairroFacade().listaFiltrando(parte.trim(), "descricao");
    }

    private boolean validaFaceSevico() {
        boolean resultado = true;
        if (form.getFaceServico().getAno() == null) {
            resultado = false;
            FacesUtil.addWarn("Atenção!", "O Ano é um campo obrigatório.");
        }
        if (form.getFaceServico().getServicoUrbano() == null || form.getFaceServico().getServicoUrbano().getId() == null) {
            resultado = false;
            FacesUtil.addWarn("Atenção!", "O Serviço é um campo obrigatório");
        } else {
            for (FaceServico fs : form.getFace().getFaceServicos()) {
                if (fs.getServicoUrbano().getCodigo().equals(form.getFaceServico().getServicoUrbano().getCodigo())
                    && fs.getAno().equals(form.getFaceServico().getAno())) {
                    resultado = false;
                    FacesUtil.addWarn("Atenção!", "O Serviço de "
                        + form.getFaceServico().getServicoUrbano().getIdentificacao()
                        + " já foi adicionado para o ano de "
                        + form.getFaceServico().getAno());
                }
            }
        }
        return resultado;
    }

    private boolean validaFaceValor() {
        boolean resultado = true;
        if (form.getFaceValor().getExercicio() == null) {
            resultado = false;
            FacesUtil.addWarn("Atenção!", "O Exercício é um campo obrigatório.");
        } else if (form.getFaceValor().getQuadra() == null) {
            resultado = false;
            FacesUtil.addWarn("Atenção!", "A Quadra é um campo obrigatório.");
        } else {
            for (FaceValor fv : form.getFace().getFaceValores()) {
                if (fv.getExercicio().equals(form.getFaceValor().getExercicio()) && form.getFaceValor().getQuadra().equals(fv.getQuadra())) {
                    resultado = false;
                    FacesUtil.addWarn("Atenção!", "Já existe um valor para o exercício e quadra informado.");
                }
            }
        }
        if (form.getFaceValor().getValor() == null) {
            resultado = false;
            FacesUtil.addWarn("Atenção!", "Valor não informado");
        }
        return resultado;
    }

    public String caminhoAtual() {
        if (operacao.equals(Operacoes.NOVO)) {
            return getCaminhoPadrao() + "novo/";
        } else if (operacao.equals(Operacoes.EDITAR)) {
            return getCaminhoPadrao() + "editar/" + this.getId() + "/";
        }
        return "";
    }

    public void novoLogradouro() {
        Web.navegacao(caminhoAtual(),
            "/tributario/cadastromunicipal/logradouro/novo/",
            form.getFace());
    }

    public void novoBairro() {
        Web.navegacao(caminhoAtual(),
            "/tributario/cadastromunicipal/bairro/novo/",
            form.getFace());
    }

    public Integer retornaExercicioMaisUm() {
        return ((SistemaControlador) Util.getControladorPeloNome("sistemaControlador")).getExercicioCorrenteAsInteger() + 1;
    }

    public void limpaLogradouro() {
        form.setLogradouro(null);
        form.setCep("");
    }

    public void carregaCEP() {
        form.getFace().setLogradouroBairro(faceFacade.getLogradouroFacade().recuperaLogradourBairro(form.getBairro(),
            form.getLogradouro()));
        //form.setCep(form.getFace().getLogradouroBairro().getCep());
    }

    public List<Quadra> completarQuadra(String parte) {
        if (form.getSetor() != null) {
            return faceFacade.getQuadraFacade().buscarPorSetor(form.getSetor(), parte.trim());
        }
        return Lists.newArrayList();
    }

    public void atribuiQuadra() {
        if (form.getFaceValor() != null) {
            form.getFaceValor().setQuadra(null);
        }
    }

    public List<SelectItem> getSetores() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (Setor obj : faceFacade.getSetorFacade().listaSetoresOrdenadosPorCodigo()) {
            if (obj.getDistrito() != null) {
                toReturn.add(new SelectItem(obj, obj.toString() + " - " + obj.getDistrito().getNome()));
            } else {
                toReturn.add(new SelectItem(obj, obj.toString()));
            }
        }

        return toReturn;
    }

    public class FormFace implements Serializable {
        private FaceServico faceServico;
        private FaceServico faceServicoAux;
        private List<FaceServico> faceServicos;
        private Bairro bairro;
        private Logradouro logradouro;
        private String cep;
        private FaceValor faceValor;
        private Setor setor;
        private Face face;

        public FormFace() {
            face = new Face();
            faceServicos = new ArrayList<>();
        }


        public FaceServico getFaceServico() {
            return faceServico;
        }

        public void setFaceServico(FaceServico faceServico) {
            this.faceServico = faceServico;
        }

        public FaceServico getFaceServicoAux() {
            return faceServicoAux;
        }

        public void setFaceServicoAux(FaceServico faceServicoAux) {
            this.faceServicoAux = faceServicoAux;
        }

        public List<FaceServico> getFaceServicos() {
            return faceServicos;
        }

        public void setFaceServicos(List<FaceServico> faceServicos) {
            this.faceServicos = faceServicos;
        }

        public Bairro getBairro() {
            return bairro;
        }

        public void setBairro(Bairro bairro) {
            this.bairro = bairro;
        }

        public Logradouro getLogradouro() {
            return logradouro;
        }

        public void setLogradouro(Logradouro logradouro) {
            this.logradouro = logradouro;
        }

        public String getCep() {
            return cep;
        }

        public void setCep(String cep) {
            this.cep = cep;
        }

        public FaceValor getFaceValor() {
            return faceValor;
        }

        public void setFaceValor(FaceValor faceValor) {
            this.faceValor = faceValor;
        }

        public Face getFace() {
            return face;
        }

        public void setFace(Face face) {
            this.face = face;
        }

        public Setor getSetor() {
            return setor;
        }

        public void setSetor(Setor setor) {
            this.setor = setor;
        }
    }
}
