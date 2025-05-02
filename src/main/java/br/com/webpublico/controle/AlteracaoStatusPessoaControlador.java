package br.com.webpublico.controle;

import br.com.webpublico.entidades.AlteracaoStatusPessoa;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AlteracaoStatusPessoaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "alteracaoStatusPessoaControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novaAlteracao",
                viewId = "/faces/tributario/cadastromunicipal/alteracaostatuspessoa/edita.xhtml",
                pattern = "/tributario/processo-ativacao-inativacao-de-pessoa/novo/"
        ),
        @URLMapping(id = "editarAlteracao",
                viewId = "/faces/tributario/cadastromunicipal/alteracaostatuspessoa/edita.xhtml",
                pattern = "/tributario/processo-ativacao-inativacao-de-pessoa/editar/#{alteracaoStatusPessoaControlador.id}/"
        ),
        @URLMapping(id = "verAlteracao",
                viewId = "/faces/tributario/cadastromunicipal/alteracaostatuspessoa/visualiza.xhtml",
                pattern = "/tributario/processo-ativacao-inativacao-de-pessoa/ver/#{alteracaoStatusPessoaControlador.id}/"
        ),
        @URLMapping(id = "listarAlteracao",
                viewId = "/faces/tributario/cadastromunicipal/alteracaostatuspessoa/lista.xhtml",
                pattern = "/tributario/processo-ativacao-inativacao-de-pessoa/listar/"
        )
})
public class AlteracaoStatusPessoaControlador extends PrettyControlador<AlteracaoStatusPessoa> implements Serializable, CRUD {

    @EJB
    private AlteracaoStatusPessoaFacade alteracaoStatusPessoaFacade;
    private Converter converterPessoa;

    public AlteracaoStatusPessoaControlador() {
        super(AlteracaoStatusPessoa.class);
    }

    @URLAction(mappingId = "novaAlteracao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setData(new Date());
        selecionado.setAno(Integer.parseInt(DataUtil.getDataFormatada(selecionado.getData(), "yyyy")));
        selecionado.setNumero(Integer.parseInt(alteracaoStatusPessoaFacade.retornaNovoNumero(selecionado.getAno())));
        selecionado.setNumeroCompleto(alteracaoStatusPessoaFacade.retornaNovoNumeroCompleto(selecionado.getData()));
        selecionado.setUsuarioSistema(((SistemaControlador) Util.getControladorPeloNome("sistemaControlador")).getUsuarioCorrente());
    }

    @URLAction(mappingId = "verAlteracao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarAlteracao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public AbstractFacade getFacede() {
        return alteracaoStatusPessoaFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/processo-ativacao-inativacao-de-pessoa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return getId();
    }

    @Override
    public void salvar() {
        if (validaTudo()) {
            if (!pessoaTemUsuarioSistema()) {
                salva();
            } else {
                RequestContext.getCurrentInstance().execute("confirmacaoInativacaoComUsuarioSistema.show()");
            }
        }
    }

    public void salva() {
        try {
            if (pessoaTemUsuarioSistema()) {
                alteracaoStatusPessoaFacade.inativaUsuarioDaPessoa(selecionado.getPessoa());
            }
            validaNumeroCadastro();
            alteracaoStatusPessoaFacade.salvarNovo(selecionado);
            FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
            return;
        } catch (Exception e) {
            descobrirETratarException(e);

        }
        redireciona();
    }

    public String getDataFormatadaVisualiza() {
        return DataUtil.getDataFormatada(selecionado.getData(), "dd/MM/yyyy HH:mm");
    }

    public void validaNumeroCadastro() {
        if (alteracaoStatusPessoaFacade.existeNumeroProcesso(selecionado.getNumeroCompleto())) {
            selecionado.setNumero(Integer.parseInt(alteracaoStatusPessoaFacade.retornaNovoNumero(selecionado.getAno())));
            selecionado.setNumeroCompleto(alteracaoStatusPessoaFacade.retornaNovoNumeroCompleto(selecionado.getData()));

            FacesUtil.addWarn("Atenção!", "O número do processo já está sendo usando, o sistema atualizou o número para " + selecionado.getNumeroCompleto());
        }
    }

    public boolean podeAlterarStatusCadastro(AlteracaoStatusPessoa.TipoProcessoAlteracaoCadastroPessoa tipoProcesso, Pessoa pessoa) {
        if (AlteracaoStatusPessoa.TipoProcessoAlteracaoCadastroPessoa.ATIVACAO.equals(tipoProcesso)
                && SituacaoCadastralPessoa.ATIVO.equals(pessoa.getSituacaoCadastralPessoa())) {
            return false;
        }

        if (AlteracaoStatusPessoa.TipoProcessoAlteracaoCadastroPessoa.INATIVACAO.equals(tipoProcesso)) {
            if (SituacaoCadastralPessoa.INATIVO.equals(pessoa.getSituacaoCadastralPessoa())) {
                return false;
            }
        }

        if (AlteracaoStatusPessoa.TipoProcessoAlteracaoCadastroPessoa.BAIXA.equals(tipoProcesso)
                && SituacaoCadastralPessoa.BAIXADO.equals(pessoa.getSituacaoCadastralPessoa())) {
            return false;
        }

        if (AlteracaoStatusPessoa.TipoProcessoAlteracaoCadastroPessoa.SUSPENSAO.equals(tipoProcesso)
                && SituacaoCadastralPessoa.SUSPENSO.equals(pessoa.getSituacaoCadastralPessoa())) {
            return false;
        }

        return true;
    }

    public boolean podeInativarPessoa(AlteracaoStatusPessoa.TipoProcessoAlteracaoCadastroPessoa tipoProcesso, Pessoa pessoa) {
        if (AlteracaoStatusPessoa.TipoProcessoAlteracaoCadastroPessoa.INATIVACAO.equals(tipoProcesso)) {
            if (alteracaoStatusPessoaFacade.pessoaPossuiVinculoComCadastroImobiliarioAtivo(pessoa)) {
                FacesUtil.addError("Atenção!", "A pessoa selecionada possui vínculo com algum Cadastro Imobiliário 'ATIVO'.");
                return false;
            }
            if (alteracaoStatusPessoaFacade.pessoaPossuiVinculoComCadastroEconomicoAtivo(pessoa)) {
                FacesUtil.addError("Atenção!", "A pessoa selecionada possui vínculo com algum Cadastro Econômico 'ATIVO' ou 'REATIVO'.");
                return false;
            }
            if (alteracaoStatusPessoaFacade.pessoaSociaCadastroEconomicoAtivo(pessoa)) {
                FacesUtil.addError("Atenção!", "A pessoa selecionada é sócia de um Cadastro Econômico 'ATIVO' ou 'REATIVO'.");
                return false;
            }
            if (alteracaoStatusPessoaFacade.pessoaPossuiDebitoEmAberto(pessoa)) {
                FacesUtil.addError("Atenção!", "A pessoa selecionada possui vínculo com qualquer débito 'EM ABERTO'.");
                return false;
            }
            if (alteracaoStatusPessoaFacade.pessoaProprietariaCadastroImobiliarioComDebito(pessoa)) {
                FacesUtil.addError("Atenção!", "A pessoa selecionada é proprietária de um Cadastro Imobiliário que possui débito 'EM ABERTO'.");
                return false;
            }
            if (alteracaoStatusPessoaFacade.pessoaCompromissariaCadastroImobiliarioComDebito(pessoa)) {
                FacesUtil.addError("Atenção!", "A pessoa selecionada é compromissária de um Cadastro Imobiliário que possui débito 'EM ABERTO'.");
                return false;
            }
            if (alteracaoStatusPessoaFacade.pessoaSociaCadastroEconomicoComDebito(pessoa)) {
                FacesUtil.addError("Atenção!", "A pessoa selecionada é sócia um Cadastro Econômico que possui débito 'EM ABERTO'.");
                return false;
            }
        }
        return true;
    }

    public boolean validaTudo() {
        boolean valida = true;

        if (selecionado.getTipoProcesso() == null) {
            valida = false;
            FacesUtil.addCampoObrigatorio("Informe o Tipo de Processo.");
        } else if (selecionado.getPessoa() != null) {
            if (!podeAlterarStatusCadastro(selecionado.getTipoProcesso(), selecionado.getPessoa())) {
                valida = false;
                FacesUtil.addError("Atenção!", "O cadastro de " + selecionado.getPessoa() + " já se enconta " + selecionado.getPessoa().getSituacaoCadastralPessoa().getDescricao().toUpperCase() + ".");
            } else {
                valida = podeInativarPessoa(selecionado.getTipoProcesso(), selecionado.getPessoa());
            }
        }

        if (selecionado.getMotivo() == null
                || selecionado.getMotivo().trim().equals("")) {
            valida = false;
            FacesUtil.addCampoObrigatorio("Informe o Motivo.");
        }
        return valida;
    }

    private boolean pessoaTemUsuarioSistema() {
        if (AlteracaoStatusPessoa.TipoProcessoAlteracaoCadastroPessoa.INATIVACAO.equals(selecionado.getTipoProcesso())) {
            return alteracaoStatusPessoaFacade.pessoaTemUsuarioSistemaAtivo(selecionado.getPessoa());
        }
        return false;
    }

    public Converter getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, alteracaoStatusPessoaFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public List<SelectItem> getTiposProcesso() {
        List<SelectItem> toRetun = new ArrayList<>();
        toRetun.add(new SelectItem(null, ""));
        toRetun.add(new SelectItem(AlteracaoStatusPessoa.TipoProcessoAlteracaoCadastroPessoa.ATIVACAO, AlteracaoStatusPessoa.TipoProcessoAlteracaoCadastroPessoa.ATIVACAO.getDescricao()));
        toRetun.add(new SelectItem(AlteracaoStatusPessoa.TipoProcessoAlteracaoCadastroPessoa.INATIVACAO, AlteracaoStatusPessoa.TipoProcessoAlteracaoCadastroPessoa.INATIVACAO.getDescricao()));
        return toRetun;
    }

    public List<Pessoa> completaPessoa(String parte) {
        if (selecionado.getTipoProcesso() != null) {
            if (selecionado.getTipoProcesso().equals(AlteracaoStatusPessoa.TipoProcessoAlteracaoCadastroPessoa.INATIVACAO)) {
                return alteracaoStatusPessoaFacade.getPessoaFacade().listaTodasPessoasAtivas(parte.trim());
            }
            return alteracaoStatusPessoaFacade.getPessoaFacade().listaTodasPessoasInativasESuspensas(parte.trim());
        } else {
            return null;
        }
    }
}
