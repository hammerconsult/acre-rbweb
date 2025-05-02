package br.com.webpublico.controle.rh.censo;


import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.censo.*;
import br.com.webpublico.enums.EstadoCivil;
import br.com.webpublico.enums.PerfilEnum;
import br.com.webpublico.enums.TipoTelefone;
import br.com.webpublico.enums.rh.censo.SituacaoCenso;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.censo.VinculoFPCensoFacade;
import br.com.webpublico.util.StringUtil;
import com.beust.jcommander.internal.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@ManagedBean(name = "vinculoFPCensoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "ver-vincufp-censo", pattern = "/rh/censo/ver/#{vinculoFPCensoControlador.id}/", viewId = "/faces/rh/censo/visualizar.xhtml"),
    @URLMapping(id = "lista-vinculofp-censo", pattern = "/rh/censo/listar/", viewId = "/faces/rh/censo/lista.xhtml"),

})
public class VinculoFPCensoControlador extends PrettyControlador<VinculoFPCenso> implements CRUD, Serializable {


    public VinculoFPCensoControlador() {
        super(VinculoFPCenso.class);
    }

    private VinculoFP original;
    private Aposentadoria aposentadoriaOriginal;

    @EJB
    private VinculoFPCensoFacade facade;

    private List<Dependente> itemDependentes;

    public VinculoFP getOriginal() {
        return original;
    }

    public void setOriginal(VinculoFP original) {
        this.original = original;
    }

    public Aposentadoria getAposentadoriaOriginal() {
        return aposentadoriaOriginal;
    }

    public void setAposentadoriaOriginal(Aposentadoria aposentadoriaOriginal) {
        this.aposentadoriaOriginal = aposentadoriaOriginal;
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rh/censo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<Dependente> getItemDependentes() {
        return itemDependentes;
    }

    public void setItemDependentes(List<Dependente> itemDependentes) {
        this.itemDependentes = itemDependentes;
    }

    @URLAction(mappingId = "ver-vincufp-censo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarVinculoOriginal();
        instaciarAposentadoria();
        instaciarAnaliseAtualizacaoCenso();
        buscarDependentes();
    }

    private void instaciarAnaliseAtualizacaoCenso() {
        if (selecionado.getAnaliseAtualizacaoCenso() == null) {
            AnaliseAtualizacaoCenso analise = new AnaliseAtualizacaoCenso();
            selecionado.setAnaliseAtualizacaoCenso(analise);
            analise.setUsuarioAvaliacao(this.facade.getSistemaFacade().getUsuarioCorrente());
            analise.setHoraAvaliacao(new Date());
        }
    }

    private void buscarDependentes() {
        itemDependentes = this.facade.getDependentes(original.getMatriculaFP().getPessoa());
        selecionado.getDependentes().forEach(dependente -> {
            dependente.setNovoDependente(itemDependentes.stream().noneMatch(itemDependente -> StringUtil.retornaApenasNumeros(itemDependente.getDependente().getCpf()).equals(StringUtil.retornaApenasNumeros(dependente.getCpf()))));
        });

    }

    private void recuperarVinculoOriginal() {
        original = this.facade.getVinculoFPFacade().buscarVinculoFPPorCPF(selecionado.getCpf()).orElseThrow(() -> new ValidacaoException("Vínculo não encontrado para o CPF: " + selecionado.getCpf()));
    }

    public void instaciarAposentadoria() {
        if (original instanceof Aposentadoria) {
            aposentadoriaOriginal = (Aposentadoria) original;
        }
    }

    @Override
    public void salvar() {
        atualizarAnaliseAtualizacaoCenso();
        EnderecoCorreio enderecoCorreio = atualizarInformacoesEndereco();
        Telefone telefone = atualizarInformacoeTelefone();
        EstadoCivil estadoCivil = atualizarEstadoCivil();
        List<Dependente> dependentes = criarDependente();

        if (enderecoCorreio != null) {
            this.facade.salvarDependencias(enderecoCorreio);
        }
        if (telefone != null) {
            this.facade.salvarDependencias(telefone);
        }
        if (estadoCivil != null) {
            PessoaFisica pessoa = original.getMatriculaFP().getPessoa();
            pessoa.setEstadoCivil(estadoCivil);
            this.facade.salvarDependencias(pessoa);
        }
        if (!dependentes.isEmpty()) {
            dependentes.forEach(dependente -> {
                this.facade.salvarDependencias(dependente);
            });
        }

        super.salvar();
    }

    private void atualizarAnaliseAtualizacaoCenso() {
        selecionado.getAnaliseAtualizacaoCenso().setHoraAvaliacao(new Date());
        selecionado.getAnaliseAtualizacaoCenso().setSituacaoCenso(SituacaoCenso.LIBERADO);
        selecionado.getAnaliseAtualizacaoCenso().setMotivoRejeicao(null);
    }

    private EnderecoCorreio atualizarInformacoesEndereco() {
        if (selecionado.getEndereco() != null) {
            EnderecoCenso enderecoCenso = selecionado.getEndereco();
            EnderecoCorreio enderecoOriginal = original.getMatriculaFP().getPessoa().getEnderecoPrincipal();
            boolean hasAlteracao = false;
            if (enderecoOriginal != null) {
                if (!enderecoCenso.getCep().trim().equals(enderecoOriginal.getCep())) {
                    enderecoOriginal.setCep(enderecoCenso.getCep());
                    hasAlteracao = true;
                }
                if (!enderecoCenso.getBairro().toUpperCase().trim().equals(enderecoOriginal.getBairro().toUpperCase().trim())) {
                    enderecoOriginal.setBairro(enderecoCenso.getBairro());
                    hasAlteracao = true;
                }
                if (!enderecoCenso.getCidade().toUpperCase().trim().equals(enderecoOriginal.getLocalidade().toUpperCase().trim())) {
                    enderecoOriginal.setLocalidade(enderecoCenso.getCidade());
                    hasAlteracao = true;
                }
                if (!enderecoCenso.getUf().toUpperCase().trim().equals(enderecoOriginal.getUf().toUpperCase().trim())) {
                    enderecoOriginal.setUf(enderecoCenso.getUf());
                    hasAlteracao = true;
                }
                if (!enderecoCenso.getLogradouro().toUpperCase().trim().equals(enderecoOriginal.getLogradouro().toUpperCase().trim())) {
                    enderecoOriginal.setLogradouro(enderecoCenso.getLogradouro());
                    hasAlteracao = true;
                }
                if (!enderecoCenso.getNumero().toUpperCase().trim().equals(enderecoOriginal.getNumero().toUpperCase().trim())) {
                    enderecoOriginal.setNumero(enderecoCenso.getNumero());
                    hasAlteracao = true;
                }
                if (hasAlteracao) {
                    enderecoOriginal.setPrincipal(true);
                    return enderecoOriginal;
                } else {
                    return null;
                }
            } else {
                EnderecoCorreio enderecoCorreio = new EnderecoCorreio();
                enderecoCorreio.setPessoaFisica(original.getMatriculaFP().getPessoa());
                original.getMatriculaFP().getPessoa().setEnderecoPrincipal(enderecoCorreio);
                enderecoCorreio.setPrincipal(true);
                enderecoCorreio.setCep(enderecoCenso.getCep());
                enderecoCorreio.setBairro(enderecoCenso.getBairro());
                enderecoCorreio.setLocalidade(enderecoCenso.getCidade());
                enderecoCorreio.setUf(enderecoCenso.getUf());
                enderecoCorreio.setLogradouro(enderecoCenso.getLogradouro());
                enderecoCorreio.setNumero(enderecoCenso.getNumero());
                return enderecoCorreio;
            }
        }
        return null;
    }

    private Telefone atualizarInformacoeTelefone() {
        if (selecionado.getTelefone() != null) {
            TelefoneCenso telefoneCenso = selecionado.getTelefone();
            Telefone telefoneOriginal = original.getMatriculaFP().getPessoa().getTelefonePrincipal();
            boolean hasAlteracao = false;
            if (telefoneOriginal != null) {
                if (!StringUtil.retornaApenasNumeros(telefoneCenso.getNumero().trim()).
                    equals(StringUtil.retornaApenasNumeros(telefoneOriginal.getTelefone()))) {
                    telefoneOriginal.setTelefone(telefoneCenso.getNumero());
                    hasAlteracao = true;
                }
                if (!telefoneCenso.getTipo().trim().equals(telefoneOriginal.getTipoFone().name())) {
                    telefoneOriginal.setTipoFone(TipoTelefone.valueOf(telefoneCenso.getTipo()));
                    hasAlteracao = true;
                }
                if (hasAlteracao) {
                    return telefoneOriginal;
                } else {
                    return null;
                }
            } else {
                Telefone telefone = new Telefone();
                telefone.setPessoa(original.getMatriculaFP().getPessoa());
                original.getMatriculaFP().getPessoa().setTelefonePrincipal(telefone);
                telefone.setPrincipal(true);
                telefone.setTelefone(telefoneCenso.getNumero());
                telefone.setTipoFone(TipoTelefone.valueOf(telefoneCenso.getTipo()));
                return telefone;
            }
        }
        return null;
    }

    private EstadoCivil atualizarEstadoCivil() {
        if (selecionado.getEstadoCivil() != null) {
            if (original.getMatriculaFP().getPessoa().getEstadoCivil() != null) {
                if (!selecionado.getEstadoCivil().equals(original.getMatriculaFP().getPessoa().getEstadoCivil())) {
                    return selecionado.getEstadoCivil();
                }
            }
        }
        return null;
    }

    private List<Dependente> criarDependente() {
        return selecionado.getDependentes().
            stream().
            filter(DependenteCenso::isNovoDependente).map(dependente -> {
                Dependente novoDependente = new Dependente();
                Long idPessoaJaCadastrada = buscarPessoaPorCPF(dependente.getCpf());
                PessoaFisica pf = new PessoaFisica();
                if (idPessoaJaCadastrada == null) {
                    if (pf.getPerfis() == null) {
                        pf.setPerfis(Lists.newArrayList());
                    }
                    pf.getPerfis().add(PerfilEnum.PERFIL_DEPENDENTE);

                    pf.setNome(dependente.getNome());
                    pf.setCpf(dependente.getCpf().replaceAll("[^0-9]", ""));
                    pf.setTipoDeficiencia(dependente.getTipoDeficiencia());
                    pf.setDataNascimento(dependente.getDataNascimento());
                } else {
                    pf = (PessoaFisica) this.facade.getPessoaFacade().recuperarComPerfil(idPessoaJaCadastrada);
                    if (pf.getPerfis() == null) {
                        pf.setPerfis(Lists.newArrayList());
                    }
                    if (!pf.getPerfis().isEmpty()) {
                        boolean hasPerfilDependente = pf.getPerfis().stream().anyMatch(PerfilEnum.PERFIL_DEPENDENTE::equals);
                        if (!hasPerfilDependente) {
                            pf.getPerfis().add(PerfilEnum.PERFIL_DEPENDENTE);
                        }
                    }
                }

                novoDependente.setDependente(pf);
                novoDependente.setResponsavel(original.getMatriculaFP().getPessoa());

                if (dependente.getGrauParentescoCenso() != null) {
                    novoDependente.setGrauDeParentesco(facade.getGrauDeParentescoFacade().buscarGrauDeParentescoAtivoPorCodigo(dependente.getGrauParentescoCenso().getCodigo()));
                }
                DependenteVinculoFP dependenteVinculoFP = new DependenteVinculoFP();
                dependenteVinculoFP.setDependente(novoDependente);
                dependenteVinculoFP.setInicioVigencia(new Date());
                dependenteVinculoFP.setTipoDependente(facade.getTipoDependenteFacade().recuperarTipoDependentePorCodigo("11"));
                novoDependente.getDependentesVinculosFPs().add(dependenteVinculoFP);

                return novoDependente;
            }).collect(Collectors.toList());
    }

    public void rejeitar() {
        selecionado.getAnaliseAtualizacaoCenso().setHoraAvaliacao(new Date());
        selecionado.getAnaliseAtualizacaoCenso().setSituacaoCenso(SituacaoCenso.REJEITADO);
        super.salvar();
    }

    private Long buscarPessoaPorCPF(String cpf) {
        return this.facade.getPessoaFacade().recuperarIdPessoaFisicaPorCPF(cpf);
    }
}
