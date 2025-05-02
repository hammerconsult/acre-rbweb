/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UnidadeGestora;
import br.com.webpublico.entidades.UnidadeGestoraUnidadeOrganizacional;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.UnidadeGestoraFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author juggernaut
 */
@ViewScoped
@ManagedBean
public class ComponentePesquisaUnidadeOrganizacional implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private HierarquiaOrganizacional unidadeOrganizacionalInicial;
    private String unidadeInicial;
    private String unidadeFinal;
    private String orgaoInicial;
    private String orgaoFinal;
    private ConverterAutoComplete converterUnidadeOrganizacional;
    private ConverterAutoComplete converterUnidadeGestora;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private UnidadeGestoraFacade unidadeGestoraFacade;
    private ArrayList<HierarquiaOrganizacional> listaUnidades = new ArrayList<>();
    private String tipoHierarquia;
    private TipoComponentePesquisaUnidadeOrganizacional tipoComponentePesquisaUnidadeOrganizacional;
    private UnidadeGestora unidadeGestora;
    private Exercicio exercicioCorrente;
    private Date dataOperacao;
    private Boolean habilitaUnidadeGestora = Boolean.FALSE;

    public ComponentePesquisaUnidadeOrganizacional() {
    }

    public void adicionarUndDaUnidadeGestora() {
        if (unidadeGestora == null) {
            FacesUtil.addError("Não foi possível adicionar adicionar!", "Informe a Unidade Gestora.");
            return;
        }
        UnidadeGestora unidadeGestoraRecuperada = unidadeGestoraFacade.recuperar(unidadeGestora.getId());
        if (listaUnidades == null) {
            listaUnidades = new ArrayList<>();
        }

        if (unidadeGestoraRecuperada.getUnidadeGestoraUnidadesOrganizacionais() == null || unidadeGestoraRecuperada.getUnidadeGestoraUnidadesOrganizacionais().isEmpty()) {
            FacesUtil.addInfo("A Unidade Gestora selecionada não possui Unidades Organizacionas cadastradas!", "");
            return;
        }
        for (UnidadeGestoraUnidadeOrganizacional unidadeGestoraUnidadeOrganizacional : unidadeGestoraRecuperada.getUnidadeGestoraUnidadesOrganizacionais()) {
            HierarquiaOrganizacional hierarquiaOrganizacionalPorUnidade = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(dataOperacao, unidadeGestoraUnidadeOrganizacional.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
            listaUnidades = (ArrayList<HierarquiaOrganizacional>) Util.adicionarObjetoEmLista(listaUnidades, hierarquiaOrganizacionalPorUnidade);
        }
        limpaCampos();
    }

    public void adicionarUndDoUsuario() {
        if (listaUnidades == null) {
            listaUnidades = new ArrayList<>();
        }
        if (tipoComponentePesquisaUnidadeOrganizacional.equals(TipoComponentePesquisaUnidadeOrganizacional.UNITARIO)) {
            if (this.unidadeOrganizacionalInicial == null) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Unidade Organizacional deve ser informado para adicionar.");
            } else {
                Boolean podeSalvar = true;
                for (HierarquiaOrganizacional unidadeSalva : listaUnidades) {
                    if (unidadeOrganizacionalInicial.getId().compareTo(unidadeSalva.getId()) != 0) {
                        podeSalvar = true;
                    } else {
                        podeSalvar = false;
                        break;
                    }
                }
                if (podeSalvar) {
                    listaUnidades.add(unidadeOrganizacionalInicial);
                }
            }
        } else {
            if (this.unidadeInicial == null) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Unidade Inicial deve ser informado para adicionar.");
            } else if (this.unidadeFinal == null) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Unidade Final deve ser informado para adicionar.");
            } else {
                if (this.unidadeInicial.compareTo(this.unidadeFinal) > 0) {
                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "A Unidade Inicial não pode ser maior que a Unidade Final.");
                } else {
                    List<HierarquiaOrganizacional> lista = hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrenteIntervaloCodigo(unidadeInicial, unidadeFinal, orgaoInicial, orgaoFinal, sistemaControlador.getUsuarioCorrente(), exercicioCorrente, dataOperacao, tipoHierarquia);
                    if (lista != null) {
                        for (HierarquiaOrganizacional unidade : lista) {
                            Boolean podeSalvar = true;
                            for (HierarquiaOrganizacional unidadeSalva : listaUnidades) {
                                if (unidade.getId().compareTo(unidadeSalva.getId()) != 0) {
                                    podeSalvar = true;
                                } else {
                                    podeSalvar = false;
                                    break;
                                }
                            }
                            if (podeSalvar) {
                                listaUnidades.add(unidade);
                            }
                        }
                    }
                }
            }
        }
        limpaCampos();
    }

    public void adicionar() {
        if (listaUnidades == null) {
            listaUnidades = new ArrayList<>();
        }
        if (tipoComponentePesquisaUnidadeOrganizacional.equals(TipoComponentePesquisaUnidadeOrganizacional.UNITARIO)) {
            if (this.unidadeOrganizacionalInicial == null) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Unidade Organizacional deve ser informado para adicionar.");
            } else {
                Boolean podeSalvar = true;
                for (HierarquiaOrganizacional unidadeSalva : listaUnidades) {
                    if (unidadeOrganizacionalInicial.getId().compareTo(unidadeSalva.getId()) != 0) {
                        podeSalvar = true;
                    } else {
                        podeSalvar = false;
                        break;
                    }
                }
                if (podeSalvar) {
                    listaUnidades.add(unidadeOrganizacionalInicial);
                }
            }
        } else {
            if (this.unidadeInicial == null) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Unidade Inicial deve ser informado para adicionar.");
            } else if (this.unidadeFinal == null) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " O campo Unidade Final deve ser informado para adicionar.");
            } else {
                if (this.unidadeInicial.compareTo(this.unidadeFinal) > 0) {
                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), " A Unidade Inicial não pode ser maior que a Unidade Final.");
                } else {
                    List<HierarquiaOrganizacional> lista = hierarquiaOrganizacionalFacade.listaIntervaloCodigoUnidadeOrgaoPorTipo(unidadeInicial, unidadeFinal, orgaoInicial, orgaoFinal, "3", exercicioCorrente, TipoHierarquiaOrganizacional.valueOf(tipoHierarquia).name());
                    if (lista != null) {
                        for (HierarquiaOrganizacional unidade : lista) {
                            Boolean podeSalvar = true;
                            for (HierarquiaOrganizacional unidadeSalva : listaUnidades) {
                                if (unidade.getId().compareTo(unidadeSalva.getId()) != 0) {
                                    podeSalvar = true;
                                } else {
                                    podeSalvar = false;
                                    break;
                                }
                            }
                            if (podeSalvar) {
                                listaUnidades.add(unidade);
                            }
                        }
                    }
                }
            }
        }
        limpaCampos();
    }

    public void limpaCampos() {
        this.unidadeOrganizacionalInicial = null;
        this.unidadeInicial = "";
        this.unidadeFinal = "";
        this.orgaoInicial = "";
        this.orgaoFinal = "";
        this.unidadeGestora = null;
    }

    public void limpaCamposView() {
        limpaCampos();
        this.listaUnidades.clear();
    }

    public void setaUnidadeInicial(SelectEvent evento) {
        unidadeOrganizacionalInicial = (HierarquiaOrganizacional) evento.getObject();
    }

    public void removeUnidadeOrganizacional(HierarquiaOrganizacional uo) {
        listaUnidades.remove(uo);
    }

    public void removerTodasUnidadesOrganizacionais() {
        listaUnidades.clear();
    }

    public boolean renderizaBotaoRemoverTodas() {
        if (listaUnidades != null) {
            if (listaUnidades.size() > 1) {
                return true;
            }
        }
        return false;
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacionalUnidadeUsuario(String parte) {
        List<HierarquiaOrganizacional> lista = hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrentePorTipo(parte.trim(), sistemaControlador.getUsuarioCorrente(), exercicioCorrente, dataOperacao, TipoHierarquiaOrganizacional.valueOf(tipoHierarquia).name());
        return lista;
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacionalUnidade(String parte) {
        List<HierarquiaOrganizacional> lista = hierarquiaOrganizacionalFacade.filtraPorNivel(parte.trim(), "3", TipoHierarquiaOrganizacional.valueOf(tipoHierarquia).name(), dataOperacao);
        return lista;
    }

    public List<UnidadeGestora> completaUnidadeGestora(String parte) {
        return unidadeGestoraFacade.completaUnidadeGestoraDasUnidadeDoUsuarioLogadoVigentes(parte.trim(), sistemaControlador.getUsuarioCorrente(), dataOperacao, exercicioCorrente, null);
    }

    public ConverterAutoComplete getConverterUnidadeOrgao() {
        if (converterUnidadeOrganizacional == null) {
            converterUnidadeOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterUnidadeOrganizacional;
    }

    public ConverterAutoComplete getConverterUnidadeGestora() {
        if (converterUnidadeGestora == null) {
            converterUnidadeGestora = new ConverterAutoComplete(UnidadeGestora.class, unidadeGestoraFacade);
        }
        return converterUnidadeGestora;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public HierarquiaOrganizacional getUnidadeOrganizacionalInicial() {
        return unidadeOrganizacionalInicial;
    }

    public void setUnidadeOrganizacionalInicial(HierarquiaOrganizacional unidadeOrganizacionalInicial) {
        this.unidadeOrganizacionalInicial = unidadeOrganizacionalInicial;
    }

    public ArrayList<HierarquiaOrganizacional> getListaUnidades() {
        return listaUnidades;
    }

    public void setListaUnidades(ArrayList<HierarquiaOrganizacional> listaUnidades) {
        this.listaUnidades = listaUnidades;
    }

    public String getTipoHierarquia() {
        return tipoHierarquia;
    }

    public void setTipoHierarquia(String tipoHierarquia) {
        this.tipoHierarquia = tipoHierarquia;
    }

    public void novo(String tipo, ArrayList<HierarquiaOrganizacional> hierarquias, Boolean habilitaUnidadeGestora, UnidadeGestora unidadeGestora, Exercicio exercicioCorrente, Date dataOperacao) {
        this.tipoHierarquia = tipo;
        this.listaUnidades = hierarquias;
        this.tipoComponentePesquisaUnidadeOrganizacional = TipoComponentePesquisaUnidadeOrganizacional.UNITARIO;
        this.habilitaUnidadeGestora = habilitaUnidadeGestora;
        this.unidadeGestora = unidadeGestora;
        getExercicioCorrente(exercicioCorrente);
        getDataOperacao(dataOperacao);

    }

    public void getDataOperacao(Date dataOperacao) {
        if (dataOperacao == null) {
            this.dataOperacao = sistemaControlador.getDataOperacao();
        } else {
            this.dataOperacao = dataOperacao;
        }
    }

    public void getExercicioCorrente(Exercicio exercicioCorrente) {
        if (exercicioCorrente == null) {
            this.exercicioCorrente = sistemaControlador.getExercicioCorrente();
        } else {
            this.exercicioCorrente = exercicioCorrente;
        }
    }

    public String getUnidadeInicial() {
        return unidadeInicial;
    }

    public void setUnidadeInicial(String unidadeInicial) {
        this.unidadeInicial = unidadeInicial;
    }

    public String getUnidadeFinal() {
        return unidadeFinal;
    }

    public void setUnidadeFinal(String unidadeFinal) {
        this.unidadeFinal = unidadeFinal;
    }

    public String getOrgaoInicial() {
        return orgaoInicial;
    }

    public void setOrgaoInicial(String orgaoInicial) {
        this.orgaoInicial = orgaoInicial;
    }

    public String getOrgaoFinal() {
        return orgaoFinal;
    }

    public void setOrgaoFinal(String orgaoFinal) {
        this.orgaoFinal = orgaoFinal;
    }

    public TipoComponentePesquisaUnidadeOrganizacional getTipoComponentePesquisaUnidadeOrganizacional() {
        return tipoComponentePesquisaUnidadeOrganizacional;
    }

    public void setTipoComponentePesquisaUnidadeOrganizacional(TipoComponentePesquisaUnidadeOrganizacional tipoComponentePesquisaUnidadeOrganizacional) {
        this.tipoComponentePesquisaUnidadeOrganizacional = tipoComponentePesquisaUnidadeOrganizacional;
    }

    public UnidadeGestoraFacade getUnidadeGestoraFacade() {
        return unidadeGestoraFacade;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public List<SelectItem> getTiposComponentes() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        for (TipoComponentePesquisaUnidadeOrganizacional componentePesquisaUnidadeOrganizacional : TipoComponentePesquisaUnidadeOrganizacional.values()) {
            if (habilitaUnidadeGestora) {
                retorno.add(new SelectItem(componentePesquisaUnidadeOrganizacional, componentePesquisaUnidadeOrganizacional.getDescricao()));
            } else if (!componentePesquisaUnidadeOrganizacional.equals(TipoComponentePesquisaUnidadeOrganizacional.UNIDADE_GESTORA)) {
                retorno.add(new SelectItem(componentePesquisaUnidadeOrganizacional, componentePesquisaUnidadeOrganizacional.getDescricao()));
            }
        }
        return retorno;
    }

    public enum TipoComponentePesquisaUnidadeOrganizacional {
        UNITARIO("Unitário"),
        INTERVALO("Intervalo"),
        UNIDADE_GESTORA("Unidade Gestora");

        private String descricao;

        private TipoComponentePesquisaUnidadeOrganizacional(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }

    public Boolean getHabilitaUnidadeGestora() {
        return habilitaUnidadeGestora;
    }

    public void setHabilitaUnidadeGestora(Boolean habilitaUnidadeGestora) {
        this.habilitaUnidadeGestora = habilitaUnidadeGestora;
    }
}
