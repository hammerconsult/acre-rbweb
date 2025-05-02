/*
 * Codigo gerado automaticamente em Thu Aug 04 15:16:54 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.concursos.ClassificacaoInscricao;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.concursos.ClassificacaoConcursoFacade;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "matriculaFPControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaMatriculaFP", pattern = "/matriculafp/novo/", viewId = "/faces/rh/administracaodepagamento/matriculafp/edita.xhtml"),
    @URLMapping(id = "novaMatriculaFPPensionista", pattern = "/matriculafp-pensionista/novo/", viewId = "/faces/rh/administracaodepagamento/matriculafp/edita.xhtml"),
    @URLMapping(id = "editarMatriculaFP", pattern = "/matriculafp/editar/#{matriculaFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/matriculafp/edita.xhtml"),
    @URLMapping(id = "listarMatriculaFP", pattern = "/matriculafp/listar/", viewId = "/faces/rh/administracaodepagamento/matriculafp/lista.xhtml"),
    @URLMapping(id = "novoMatriculaCandidatoConcurso", pattern = "/matriculafp/novo/candidato-concurso/#{matriculaFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/matriculafp/edita.xhtml"),
    @URLMapping(id = "verMatriculaFP", pattern = "/matriculafp/ver/#{matriculaFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/matriculafp/visualizar.xhtml")
})
public class MatriculaFPControlador extends PrettyControlador<MatriculaFP> implements Serializable, CRUD {

    @EJB
    private MatriculaFPFacade matriculaFPFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    private ConverterGenerico converterPessoa;
    @EJB
    private UnidadeOrganizacionalFacade unidadeMatriculadoFacade;
    @EJB
    private SingletonMatriculaFP singletonMatriculaFP;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    private ConverterGenerico converterUnidadeMatriculado;
    private ConverterAutoComplete converterPessoaFisica;
    private boolean perfilRH;
    private boolean perfilPensionista;
    private Boolean pensionista;
    private String mensagemDialogPerfil;
    private String destino;
    private String origem;
    private Boolean podeSalvar;
    private ClassificacaoInscricao classificacaoInscricaoSelecionada;
    @EJB
    private ClassificacaoConcursoFacade classificacaoConcursoFacade;

    public MatriculaFPControlador() {
        super(MatriculaFP.class);
    }

    public boolean isPerfilRH() {
        return perfilRH;
    }

    public void setPerfilRH(boolean perfilRH) {
        this.perfilRH = perfilRH;
    }

    public MatriculaFPFacade getFacade() {
        return matriculaFPFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return matriculaFPFacade;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }

    @URLAction(mappingId = "novoMatriculaCandidatoConcurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void criarMatriculaDecorrenteDeCandidatoConcurso() {
        classificacaoInscricaoSelecionada = classificacaoConcursoFacade.buscarClassificacaoInscricao(getId());
        Long pessoaId = pessoaFisicaFacade.buscarIdDePessoaPorCpf(classificacaoInscricaoSelecionada.getInscricaoConcurso().getCpf());
        novo();
        selecionado.setPessoa(pessoaFisicaFacade.recuperar(pessoaId));
    }

    public List<SelectItem> getUnidadeMatriculado() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        if (pensionista != null && !pensionista) {
            for (UnidadeOrganizacional object : unidadeMatriculadoFacade.retornaEntidades()) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        } else {
            for (UnidadeOrganizacional object : unidadeMatriculadoFacade.retornaEntidadesPorTipoEntidade(TipoEntidade.FUNDO_PUBLICO)) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return toReturn;
    }

    public ConverterGenerico getConverterUnidadeMatriculado() {
        if (converterUnidadeMatriculado == null) {
            converterUnidadeMatriculado = new ConverterGenerico(UnidadeOrganizacional.class, unidadeMatriculadoFacade);
        }
        return converterUnidadeMatriculado;
    }

    public Converter getConverterPessoaFisica() {
        if (converterPessoaFisica == null) {
            converterPessoaFisica = new ConverterAutoComplete(PessoaFisica.class, pessoaFisicaFacade);
        }
        return converterPessoaFisica;
    }

    public List<PessoaFisica> completaPessoaFisica(String parte) {
        return pessoaFisicaFacade.buscarPessoasAtivasPorTiposPerfil(parte.trim(), PerfilEnum.PERFIL_RH, PerfilEnum.PERFIL_PENSIONISTA);
    }

    public String incrementaNumeroMatricula() {
        MatriculaFP m = ((MatriculaFP) selecionado);
        String numeroMatricula = matriculaFPFacade.incrementaNumero();
        if (numeroMatricula == null) {
            numeroMatricula = "0";
        }
        Integer numero = Integer.valueOf(numeroMatricula);
        return numero.toString();
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaOrganizacionalSelecionada == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Órgão é obrigatório.");
        }
        if (selecionado.getPessoa() == null || selecionado.getPessoa().getId() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Pessoa é obrigatório.");
        }
        if ((matriculaFPFacade.pessoaJaPossuiMatricula(selecionado)) && (operacao != Operacoes.EDITAR)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A pessoa selecionada já possui uma matricula..");
        }
        ve.lancarException();
    }

    public boolean isPensao() {
        String isPensao = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("pensao");
        pensionista = Boolean.valueOf(isPensao);
        return pensionista;
    }

    public Boolean getPensionista() {
        return pensionista;
    }

    public void setPensionista(Boolean pensionista) {
        this.pensionista = pensionista;
    }

    @Override
    public MatriculaFP getSelecionado() {
        return (MatriculaFP) selecionado;
    }

    @URLAction(mappingId = "novaMatriculaFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        ((MatriculaFP) selecionado).setMatricula(buscarProximaMatricula());
        getParametros();
        podeSalvar = true;
        pensionista = false;
        recuperarPessoaSessao();

    }

    private String buscarProximaMatricula() {
        Integer ultimaMatricula = singletonMatriculaFP.recuperaUltimaMatriculaDoBanco();
        ultimaMatricula++;
        return String.valueOf(ultimaMatricula);
    }

    private void recuperarPessoaSessao() {
        PessoaFisica pf = (PessoaFisica) Web.pegaDaSessao(PessoaFisica.class);
        if (pf != null)
            selecionado.setPessoa(pf);
    }

    @URLAction(mappingId = "novaMatriculaFPPensionista", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaMatriculaPensionista() {
        novo();
        pensionista = Boolean.TRUE;
        podeSalvar = true;
    }

    @URLAction(mappingId = "editarMatriculaFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        pensionista = false;
        if (selecionado.getUnidadeMatriculado() != null) {
            hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalFacade.recuperaHierarquiaOrganizacionalPelaUnidade(selecionado.getUnidadeMatriculado().getId());
        }
    }

    @URLAction(mappingId = "verMatriculaFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }


    private void atribuirUnidadeOrganizacional() {
        selecionado.setUnidadeMatriculado(hierarquiaOrganizacionalSelecionada.getSubordinada());
    }

    @Override
    public void salvar() {
        validaPerfil();
        if (podeSalvar) {
            try {
                validarCampos();
                atribuirUnidadeOrganizacional();
                String matriculaSalva = "";
                String matriculaProposta = selecionado.getMatricula();
                if (Util.validaCampos(selecionado)) {
                    try {
                        if (isOperacaoNovo()) {
                            MatriculaFP martricula = singletonMatriculaFP.gravarMatricula(selecionado);
                            matriculaSalva = martricula.getMatricula();
                            adicionarMensagemOperacaoMatriculasDiferentes(matriculaSalva, matriculaProposta);
                        }

                        if (isOperacaoEditar()) {
                            super.salvar();
                        }
                        Web.poeNaSessao(selecionado);
                    } catch (ValidacaoException ex) {
                        FacesUtil.printAllFacesMessages(ex.getMensagens());
                        return;
                    } catch (Exception e) {
                        descobrirETratarException(e);
                    }
                    redireciona();
                }
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            }

        }
    }

    private void adicionarMensagemOperacaoMatriculasDiferentes(String matriculaSalva, String matriculaProposta) {
        String msg = getMensagemSucessoAoSalvar();
        if (matriculaProposta != matriculaSalva) {
            msg = "A Matrícula " + matriculaProposta + " já está sendo usada e foi gerada uma nova matrícula " + matriculaSalva + " !";
        }
        FacesUtil.addOperacaoRealizada(msg);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/matriculafp/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public boolean desabilitarPessoa() {
        return operacao.equals(Operacoes.EDITAR);
    }

    public void ativouAba(ActionEvent ev) {
        RequestContext.getCurrentInstance().update("Formulario:pessoa");
    }

    public void validaPerfil() {
        podeSalvar = true;
        perfilRH = false;
        if (((MatriculaFP) selecionado).getPessoa() != null) {
            perfilRH = pessoaFisicaFacade.isPessoaNoPerfil(((MatriculaFP) selecionado).getPessoa(), PerfilEnum.PERFIL_RH);
            perfilPensionista = pessoaFisicaFacade.isPessoaNoPerfil(selecionado.getPessoa(), PerfilEnum.PERFIL_PENSIONISTA);

            if (!perfilRH && !perfilPensionista) {
                mensagemDialogPerfil = "A pessoa selecionada não possui todos os dados obrigatórios preenchidos para receber uma matrícula. (Sem Perfil RH)";
                destino = "pessoa";
                origem = "matriculafp";
                podeSalvar = false;
                RequestContext.getCurrentInstance().update("formDialog");
                RequestContext.getCurrentInstance().execute("dialogPerfil.show()");
            }

            if (pensionista != null) {
                if (pensionista && !perfilPensionista) {
                    mensagemDialogPerfil = "A pessoa selecionada não possui todos os dados obrigatórios preenchidos para receber uma matrícula. (Sem Perfil Pensionista)";
                    destino = "pessoa-pensionista";
                    origem = "matriculafp-pensionista";
                    podeSalvar = false;
                    RequestContext.getCurrentInstance().update("formDialog");
                    RequestContext.getCurrentInstance().execute("dialogPerfil.show()");
                }

            }

        }
    }

    public void limpaPessoa() {
        if (operacao == Operacoes.NOVO) {
            ((MatriculaFP) selecionado).setPessoa(null);
            podeSalvar = true;
        }
    }

    private void getParametros() {
        String pessoaId = PrettyContext.getCurrentInstance().getRequestQueryString().getParameter("responsavel");
        if (pessoaId != null && !pessoaId.trim().isEmpty()) {
            selecionado.setPessoa(pessoaFisicaFacade.recuperar(Long.parseLong(pessoaId)));
        }
    }

    public String getMensagemDialogPerfil() {
        return mensagemDialogPerfil;
    }

    public void setMensagemDialogPerfil(String mensagemDialogPerfil) {
        this.mensagemDialogPerfil = mensagemDialogPerfil;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public List<HierarquiaOrganizacional> completarHierarquias(String parte) {
        return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }
}
