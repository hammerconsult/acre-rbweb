package br.com.webpublico.controle.rh.concursos;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.CEPLogradouro;
import br.com.webpublico.entidades.EnderecoCorreio;
import br.com.webpublico.entidades.rh.concursos.CargoConcurso;
import br.com.webpublico.entidades.rh.concursos.Concurso;
import br.com.webpublico.entidades.rh.concursos.InscricaoConcurso;
import br.com.webpublico.enums.Sexo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.concursos.InscricaoConcursoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Buzatto on 26/08/2015.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaInscricaoConcurso", pattern = "/concursos/inscricao/novo/", viewId = "/faces/rh/concursos/inscricao/edita.xhtml"),
    @URLMapping(id = "editarInscricaoConcurso", pattern = "/concursos/inscricao/editar/#{inscricaoConcursoControlador.id}/", viewId = "/faces/rh/concursos/inscricao/edita.xhtml"),
    @URLMapping(id = "verInscricaoConcurso", pattern = "/concursos/inscricao/ver/#{inscricaoConcursoControlador.id}/", viewId = "/faces/rh/concursos/inscricao/edita.xhtml"),
    @URLMapping(id = "listarInscricaoConcurso", pattern = "/concursos/inscricao/listar/", viewId = "/faces/rh/concursos/inscricao/lista.xhtml")
})
public class InscricaoConcursoControlador extends PrettyControlador<InscricaoConcurso> implements Serializable, CRUD {

    @EJB
    private InscricaoConcursoFacade inscricaoConcursoFacade;
    private InscricaoConcurso inscricaoConcursoBackup;
    private Concurso concursoSelecionado;
    private CargoConcursoConverter converterCargoConcurso;

    public InscricaoConcursoControlador() {
        super(InscricaoConcurso.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/concursos/inscricao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return inscricaoConcursoFacade;
    }

    @Override
    @URLAction(mappingId = "novaInscricaoConcurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "verInscricaoConcurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editarInscricaoConcurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        concursoSelecionado = inscricaoConcursoFacade.getConcursoFacade().recuperarConcursoParaTelaDeInscricao(selecionado.getConcurso().getId());
        cancelarInscricaoSelecionada();
    }

    @Override
    public void salvar() {
        if (podeSalvar()) {
            try {
                inscricaoConcursoFacade.salvarConcurso(concursoSelecionado);
                FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            } catch (ValidacaoException ex) {
                FacesUtil.printAllFacesMessages(ex.getMensagens());
                return;
            } catch (Exception e) {
                descobrirETratarException(e);
            }
            redireciona();
        }
    }

    private boolean podeSalvar() {
        if (selecionado.getConcurso() == null) {
            FacesUtil.addCampoObrigatorio("O campo concurso é obrigatório!");
            return false;
        }
        return true;
    }

    @Override
    public void cancelar() {
        super.cancelar();
    }

    public List<SelectItem> getConcursos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (Concurso concurso : inscricaoConcursoFacade.getConcursoFacade().getUltimosConcursosComPublicacoes()) {
            toReturn.add(new SelectItem(concurso, concurso.toString()));
        }
        return toReturn;
    }

    public void carregarListarDoConcurso() {
        if (selecionado.temConcurso()) {
            selecionado.setConcurso(inscricaoConcursoFacade.getConcursoFacade().recuperarConcursoParaTelaDeInscricao(selecionado.getConcurso().getId()));
            concursoSelecionado = selecionado.getConcurso();
        }
    }

    public List<SelectItem> getSexo() {
        List<SelectItem> lista = new ArrayList<>();
        for (Sexo sexo : Sexo.values()) {
            lista.add(new SelectItem(sexo, sexo.getDescricao()));
        }
        return lista;
    }

    public void validaCpfRh() {

        if (!validaCpf(selecionado.getCpf())) {
            FacesUtil.addOperacaoNaoPermitida("O CPF digitado é inválido");
            selecionado.setCpf(null);
        }
    }

    public boolean validaCpf(String s_aux) {
        if (s_aux.isEmpty()) {
            return true;
        } else {
            s_aux = s_aux.replace(".", "");
            s_aux = s_aux.replace("-", "");
//------- Rotina para CPF
            if (s_aux.length() == 11) {
                int d1, d2;
                int digito1, digito2, resto;
                int digitoCPF;
                String nDigResult;
                d1 = d2 = 0;
                digito1 = digito2 = resto = 0;
                for (int n_Count = 1; n_Count < s_aux.length() - 1; n_Count++) {
                    digitoCPF = Integer.valueOf(s_aux.substring(n_Count - 1, n_Count)).intValue();
//--------- Multiplique a ultima casa por 2 a seguinte por 3 a seguinte por 4 e assim por diante.
                    d1 = d1 + (11 - n_Count) * digitoCPF;
//--------- Para o segundo digito repita o procedimento incluindo o primeiro digito calculado no passo anterior.
                    d2 = d2 + (12 - n_Count) * digitoCPF;
                }
                ;
//--------- Primeiro resto da divisão por 11.
                resto = (d1 % 11);
//--------- Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.
                if (resto < 2) {
                    digito1 = 0;
                } else {
                    digito1 = 11 - resto;
                }
                d2 += 2 * digito1;
//--------- Segundo resto da divisão por 11.
                resto = (d2 % 11);
//--------- Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11 menos o resultado anterior.
                if (resto < 2) {
                    digito2 = 0;
                } else {
                    digito2 = 11 - resto;
                }
//--------- Digito verificador do CPF que está sendo validado.
                String nDigVerific = s_aux.substring(s_aux.length() - 2, s_aux.length());
//--------- Concatenando o primeiro resto com o segundo.
                nDigResult = String.valueOf(digito1) + String.valueOf(digito2);
//--------- Comparar o digito verificador do cpf com o primeiro resto + o segundo resto.
                return nDigVerific.equals(nDigResult);
            } //-------- Rotina para CNPJ
            else {
                return false;
            }
        }
    }

    public List<SelectItem> getCargosDoConcurso() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, " "));
        for (CargoConcurso cargoConcurso : concursoSelecionado.getCargos()) {
            lista.add(new SelectItem(cargoConcurso, cargoConcurso.toString()));
        }
        return lista;
    }

    public CargoConcursoConverter getConverterCargo() {
        if (converterCargoConcurso == null) {
            converterCargoConcurso = new CargoConcursoConverter();
        }
        return converterCargoConcurso;
    }

    public void atualizaLogradouros() {
        inscricaoConcursoFacade.getConcursoFacade().getConsultaCepFacade().atualizarLogradouros(selecionado.getEnderecoCorreio());
    }

    public void confirmarInscricao() {
        if (podeConfirmarInscricao()) {
            try {
                setarNumero();
                inscricaoConcursoFacade.salvar(selecionado);
                concursoSelecionado = inscricaoConcursoFacade.getConcursoFacade().recuperarConcursoParaTelaDeInscricao(concursoSelecionado.getId());
                cancelarInscricao();
                fecharDialogInscricao();
            } catch (Exception ex) {
                FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
            }
        }
    }

    public void fecharDialogInscricao() {
        FacesUtil.executaJavaScript("dialogFormInscricao.hide()");
    }

    public void setarNumero() {
        if (selecionado.getNumero() == null) {
            selecionado.setNumero(concursoSelecionado.getInscricoes().size() + 1);
        }
    }

    public void cancelarInscricao() {
        cancelarInscricaoSelecionada();
        cancelarInscricaoBackup();
    }

    public void cancelarInscricaoSelecionada() {
        selecionado = new InscricaoConcurso();
        selecionado.setConcurso(concursoSelecionado);
    }

    public void cancelarInscricaoBackup() {
        if (inscricaoConcursoBackup != null) {
            concursoSelecionado.setInscricoes(Util.adicionarObjetoEmLista(concursoSelecionado.getInscricoes(), inscricaoConcursoBackup));
        }
        setaNullinscricaoConcursoBackupSemVerificacao();
    }

    public void setaNullinscricaoConcursoBackupSemVerificacao() {
        inscricaoConcursoBackup = null;
    }

    private boolean podeConfirmarInscricao() {
        if (!Util.validaCampos(selecionado)) {
            return false;
        }
        if (!rgValido()) {
            return false;
        }
        if (!enderecoValido()) {
            return false;
        }
        return true;
    }

    private boolean enderecoValido() {
        if (selecionado.getEnderecoCorreio() != null) {
            if (selecionado.getEnderecoCorreio().getCep() == null || selecionado.getEnderecoCorreio().getCep().isEmpty()) {
                FacesUtil.addCampoObrigatorio("O campo cep do endereço é obrigatório.");
                return false;
            }
            if (!Util.validaCampos(selecionado.getEnderecoCorreio())) {
                return false;
            }
        }
        return true;
    }

    private boolean rgValido() {
        if (selecionado.getRg() != null) {
            if (!Util.validaCampos(selecionado.getRg())) {
                return false;
            }
            if (selecionado.getRg().getDataemissao() == null) {
                FacesUtil.addCampoObrigatorio("O campo data de emissão do rg é obrigatório.");
                return false;
            }
            if (selecionado.getRg().getUf() == null) {
                FacesUtil.addCampoObrigatorio("O campo estado do rg é obrigatório.");
                return false;
            }
        }
        return true;
    }

    public void selecionarInscricao(InscricaoConcurso inscricao) {
        selecionado = inscricao;
        inscricaoConcursoBackup = inscricao;
        removerInscricao(inscricao);
    }

    public void removerInscricao(InscricaoConcurso inscricaoConcurso) {
        concursoSelecionado.getInscricoes().remove(inscricaoConcurso);
    }

    public InscricaoConcurso getInscricaoConcursoBackup() {
        return inscricaoConcursoBackup;
    }

    public void setInscricaoConcursoBackup(InscricaoConcurso inscricaoConcursoBackup) {
        this.inscricaoConcursoBackup = inscricaoConcursoBackup;
    }

    public Concurso getConcursoSelecionado() {
        return concursoSelecionado;
    }

    public void setConcursoSelecionado(Concurso concursoSelecionado) {
        this.concursoSelecionado = concursoSelecionado;
    }

    public class CargoConcursoConverter implements Converter, Serializable {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
            Long criadoEm = Long.parseLong(s);
            for (CargoConcurso cc : selecionado.getConcurso().getCargos()) {
                if (cc.getCriadoEm().equals(criadoEm)) {
                    return cc;
                }
            }
            return null;
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
            CargoConcurso c = (CargoConcurso) o;
            if (c != null) {
                return c.getCriadoEm().toString();
            } else {
                return "";
            }
        }
    }
}
