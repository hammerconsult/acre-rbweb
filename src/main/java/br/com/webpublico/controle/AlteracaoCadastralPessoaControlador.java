package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoAlteracaoCadastralPessoa;
import br.com.webpublico.enums.TipoTelefone;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AlteracaoCadastralPessoaFacade;
import br.com.webpublico.negocios.ArquivoFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "alteracaoCadastralPessoaControlador")
@ViewScoped
@URLMappings(
    mappings = {
        @URLMapping(id = "verCadastroPessoa", pattern = "/alterar-cadastro-pessoa/ver/#{alteracaoCadastralPessoaControlador.id}/",
            viewId = "/faces/tributario/alteracaocadastralpessoa/visualizar.xhtml"),
        @URLMapping(id = "listarCadastroPessoa", pattern = "/alterar-cadastro-pessoa/listar/", viewId = "/faces/tributario/alteracaocadastralpessoa/lista.xhtml")
    }
)
public class AlteracaoCadastralPessoaControlador extends PrettyControlador<AlteracaoCadastralPessoa> implements Serializable, CRUD {

    @EJB
    private AlteracaoCadastralPessoaFacade alteracaoCadastralPessoaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    private EnderecoCorreio enderecoPessoa;
    private Telefone telefonePessoa;

    public AlteracaoCadastralPessoaControlador() {
        super(AlteracaoCadastralPessoa.class);
    }

    @Override
    @URLAction(mappingId = "verCadastroPessoa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        selecionado.setUsuarioResponsavel(sistemaFacade.getUsuarioCorrente());

        enderecoPessoa = (selecionado.getPessoa().getEnderecoPrincipal() != null ? selecionado.getPessoa().getEnderecoPrincipal() : new EnderecoCorreio());
        telefonePessoa = (selecionado.getPessoa().getTelefonePrincipal() != null ? selecionado.getPessoa().getTelefonePrincipal() : new Telefone());
    }

    @Override
    public String getCaminhoPadrao() {
        return "/alterar-cadastro-pessoa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return alteracaoCadastralPessoaFacade;
    }

    public EnderecoCorreio getEnderecoPessoa() {
        return enderecoPessoa;
    }

    public void setEnderecoPessoa(EnderecoCorreio enderecoPessoa) {
        this.enderecoPessoa = enderecoPessoa;
    }

    public Telefone getTelefonePessoa() {
        return telefonePessoa;
    }

    public void setTelefonePessoa(Telefone telefonePessoa) {
        this.telefonePessoa = telefonePessoa;
    }

    public void aprovarAlteracoes() {
        if (isPessoaFisicaOuPessoaJuridica()) {
            selecionado.getPessoa().getAsPessoaFisica().setNome(selecionado.getNomeRazaoSocial());
            selecionado.getPessoa().getAsPessoaFisica().setDataNascimento(selecionado.getDataNascimentoAbertura());
        } else {
            selecionado.getPessoa().getAsPessoaJuridica().setRazaoSocial(selecionado.getNomeRazaoSocial());
            selecionado.getPessoa().getAsPessoaJuridica().setInicioAtividade(selecionado.getDataNascimentoAbertura());
        }

        EnderecoCorreio enderecoCorreio = atribuirNovoEndereco(new EnderecoCorreio());
        Telefone telefone = atribuirNovoTelefone(new Telefone());

        if (selecionado.getPessoa().getEnderecoPrincipal() != null) {
            atribuirNovoEndereco(selecionado.getPessoa().getEnderecoPrincipal());
        } else {
            selecionado.getPessoa().getEnderecos().add(enderecoCorreio);
        }

        if (selecionado.getPessoa().getTelefonePrincipal() != null) {
            atribuirNovoTelefone(selecionado.getPessoa().getTelefonePrincipal());
        } else {
            selecionado.getPessoa().getTelefones().add(telefone);
        }
        selecionado.getPessoa().setEmail(selecionado.getEmail());
        selecionado.setSituacao(SituacaoAlteracaoCadastralPessoa.APROVADO);
        selecionado.setUsuarioConclusao(sistemaFacade.getUsuarioCorrente());
        selecionado.setDataConclusao(new Date());
        alteracaoCadastralPessoaFacade.salvarSelecionado(selecionado);

        if (selecionado.getDetentorArquivoComposicao() != null) {
            if (selecionado.getDetentorArquivoComposicao().getArquivosComposicao() != null
                && !selecionado.getDetentorArquivoComposicao().getArquivosComposicao().isEmpty()) {
                if (selecionado.getPessoa().getDetentorArquivoComposicao() != null) {
                    selecionado.getDetentorArquivoComposicao().setId(selecionado.getPessoa().getDetentorArquivoComposicao().getId());

                    List<ArquivoComposicao> arquivosComposicao = selecionado.getDetentorArquivoComposicao().getArquivosComposicao();
                    for (ArquivoComposicao arquivoComposicao : arquivosComposicao) {
                        ArquivoComposicao arqPessoa = new ArquivoComposicao();
                        arqPessoa.setArquivo(arquivoComposicao.getArquivo());
                        arqPessoa.setDataUpload(new Date());
                        arqPessoa.setDetentorArquivoComposicao(selecionado.getPessoa().getDetentorArquivoComposicao());
                        selecionado.getPessoa().getDetentorArquivoComposicao().getArquivosComposicao().add(arqPessoa);
                    }
                } else {
                    selecionado.getPessoa().setDetentorArquivoComposicao(selecionado.getDetentorArquivoComposicao());
                }
            }
        }
        alteracaoCadastralPessoaFacade.salvarPessoa(selecionado.getPessoa());
        AssistenteBarraProgresso assistente = criarAssistente("Enviando E-mail de Aprovação de Alteração Cadastral de Pessoa");
        FacesUtil.addInfo("Enviado!", alteracaoCadastralPessoaFacade.enviarEmailDeferimentoAlteracaoCadastral(assistente));
        FacesUtil.addInfo("Sucesso", "Alteração deferida com sucesso!");
        redireciona();
    }

    private AssistenteBarraProgresso criarAssistente(String descricaoProcesso) {
        AssistenteBarraProgresso assistente = new AssistenteBarraProgresso();
        assistente.setExecutando(true);
        assistente.setDescricaoProcesso(descricaoProcesso);
        assistente.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        assistente.setSelecionado(selecionado);
        return assistente;
    }

    private EnderecoCorreio atribuirNovoEndereco(EnderecoCorreio novoEndereco) {
        novoEndereco.setCep(selecionado.getCep());
        novoEndereco.setLogradouro(selecionado.getLogradouro());
        novoEndereco.setBairro(selecionado.getBairro());
        novoEndereco.setLocalidade(selecionado.getCidade());
        novoEndereco.setUf(selecionado.getUf());
        novoEndereco.setComplemento(selecionado.getComplemento());
        novoEndereco.setNumero(selecionado.getNumero());
        novoEndereco.setPrincipal(Boolean.TRUE);

        return novoEndereco;
    }

    private Telefone atribuirNovoTelefone(Telefone novoTelefone) {
        novoTelefone.setTelefone(selecionado.getTelefone());
        novoTelefone.setPrincipal(Boolean.TRUE);
        novoTelefone.setTipoFone(TipoTelefone.OUTROS);
        novoTelefone.setPessoa(selecionado.getPessoa());
        return novoTelefone;
    }

    private void validarIndeferimento() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getMotivoConclusao() == null || "".equals(selecionado.getMotivoConclusao())) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor Informar o motivo do indeferimento!");
        }
        ve.lancarException();
    }

    public void indeferirAlteracoes() {
        try {
            validarIndeferimento();

            selecionado.setSituacao(SituacaoAlteracaoCadastralPessoa.INDEFERIDO);
            selecionado.setUsuarioConclusao(sistemaFacade.getUsuarioCorrente());
            selecionado.setDataConclusao(new Date());
            alteracaoCadastralPessoaFacade.salvarSelecionado(selecionado);
            FacesUtil.executaJavaScript("dialogIndeferir.hide()");
            AssistenteBarraProgresso assistente = criarAssistente("Enviando E-mail de Indeferimento de Alteração Cadastral de Pessoa");
            FacesUtil.addInfo("Enviado!", alteracaoCadastralPessoaFacade.enviarEmailIndeferimentoAlteracaoCadastral(assistente));
            FacesUtil.addInfo("Sucesso", "Alteração indeferida com sucesso!");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public boolean isPessoaFisicaOuPessoaJuridica() {
        return selecionado.getPessoa() instanceof PessoaFisica;
    }

    public boolean canAprovar() {
        return !SituacaoAlteracaoCadastralPessoa.EM_ABERTO.equals(selecionado.getSituacao());
    }
}
