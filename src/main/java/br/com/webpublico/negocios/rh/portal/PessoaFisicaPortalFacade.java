package br.com.webpublico.negocios.rh.portal;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.cadastrofuncional.TempoContratoFP;
import br.com.webpublico.entidades.rh.cadastrofuncional.TempoContratoFPPessoa;
import br.com.webpublico.entidades.rh.portal.atualizacaocadastral.*;
import br.com.webpublico.enums.PerfilEnum;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.enums.rh.SituacaoPessoaPortal;
import br.com.webpublico.negocios.*;
import br.com.webpublico.pessoa.enumeration.TipoAlteracaoPortal;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * Created by peixe on 01/09/17.
 */
@Stateless
public class PessoaFisicaPortalFacade extends AbstractFacade<PessoaFisicaPortal> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private DocumentoPessoalFacade documentoPessoalFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private TipoDependenteFacade tipoDependenteFacade;
    @EJB
    private CIDFacade cidFacade;
    @EJB
    private DependenteFacade dependenteFacade;

    public PessoaFisicaPortalFacade() {
        super(PessoaFisicaPortal.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public PessoaFisicaPortal recuperar(Object id) {
        PessoaFisicaPortal p = em.find(PessoaFisicaPortal.class, id);
        Hibernate.initialize(p.getConselhos());
        Hibernate.initialize(p.getTelefones());
        Hibernate.initialize(p.getEnderecos());
        Hibernate.initialize(p.getHabilidades());
        Hibernate.initialize(p.getFormacoesPessoa());
        Hibernate.initialize(p.getConselhos());
        Hibernate.initialize(p.getHabilitacao());
        Hibernate.initialize(p.getItemTempoContratoFPPessoaPortal());
        Hibernate.initialize(p.getDependentes());
        for (DependentePortal dependente : p.getDependentes()) {
            if (dependente.getEnderecos() != null) {
                Hibernate.initialize(dependente.getEnderecos());
            }
            if (dependente.getTelefones() != null) {
                Hibernate.initialize(dependente.getTelefones());
            }
            if (dependente.getDependentesVinculos() != null) {
                Hibernate.initialize(dependente.getDependentesVinculos());
            }
        }

        return p;
    }

    private void salvarDependentes(PessoaFisicaPortal pessoaFisicaPortal, PessoaFisica pessoaFisica) {
        if (pessoaFisicaPortal != null) {
            if (pessoaFisicaPortal.getDependentes() != null && !pessoaFisicaPortal.getDependentes().isEmpty()) {
                for (DependentePortal dependentePortal : pessoaFisicaPortal.getDependentes()) {
                    if (!Strings.isNullOrEmpty(dependentePortal.getCpf())) {
                        Dependente dependente = dependentePortal.getIdDependente() != null ? dependenteFacade.recuperar(dependentePortal.getIdDependente()) : null;
                        PessoaFisica pessoaF;
                        if (dependente != null && dependentePortal.getAlterado()) {
                            if (dependente.getDependente() != null) {
                                pessoaF = pessoaFisicaFacade.recuperar(dependente.getDependente().getId());
                                Dependente dependenteAlterado = atribuirDadosDependente(dependente, dependentePortal);
                                atribuirDependenteVinculoFP(dependentePortal, dependente);
                                dependenteAlterado.setDependente(atribuirDadosPessoaFisicaDependente(pessoaF, dependentePortal));
                                dependentePortal.setAlterado(Boolean.FALSE);
                                Util.adicionarObjetoEmLista(pessoaFisica.getDependentes(), dependenteAlterado);
                            }
                        }
                        if (dependente == null) {
                            pessoaF = buscarPessoaFisicaPeloCpf(dependentePortal.getCpf());
                            if (pessoaF == null) {
                                pessoaF = new PessoaFisica();
                            }
                            dependente = new Dependente();
                            atribuirDadosDependente(dependente, dependentePortal);
                            PessoaFisica novaPessoa = em.merge(pessoaF);
                            atribuirDependenteVinculoFP(dependentePortal, dependente);
                            dependente.setDependente(atribuirDadosPessoaFisicaDependente(novaPessoa, dependentePortal));
                            Util.adicionarObjetoEmLista(pessoaFisica.getDependentes(), dependente);
                        }
                    }
                }
                em.merge(pessoaFisica);
            }
        }
    }

    private void atribuirDependenteVinculoFP(DependentePortal dependentePortal, Dependente dependente) {
        List<DependenteVinculoFP> dependenteVinculoFPSPortal = DependenteVinculoFP.toDependenteVinculoFPs(dependente, dependentePortal.getDependentesVinculos());
        for (DependenteVinculoPortal vinculoPortal : dependentePortal.getDependentesVinculos()) {
            if (TipoAlteracaoPortal.NOVO.equals(vinculoPortal.getTipoAlteracaoPortal())) {
                DependenteVinculoFP novoDependenteVinculo = DependenteVinculoFP.toDependenteVinculoFP(dependente, vinculoPortal);
                novoDependenteVinculo.setInicioVigencia(new Date());
                dependente.getDependentesVinculosFPs().add(novoDependenteVinculo);
            }
        }
    }

    private PessoaFisica atribuirDadosPessoaFisicaDependente(PessoaFisica dependente, DependentePortal dependentePortal) {
        if (dependentePortal != null) {
            dependente.setNome(dependentePortal.getNome());
            dependente.setMae(dependentePortal.getNomeMae());
            dependente.setPai(dependentePortal.getNomePai());
            dependente.setCpf(dependentePortal.getCpf());
            dependente.setDataNascimento(dependentePortal.getDataNascimento());
            dependente.setSexo(dependentePortal.getSexo());
            dependente.setDeficienteFisico(dependentePortal.getPortadorNecessidadeEspecial());
            dependente.setDataInvalidez(dependentePortal.getDeficienteDesde());
            dependente.setEstadoCivil(dependentePortal.getEstadoCivil());
            dependente.setPerfis(Lists.newArrayList(PerfilEnum.PERFIL_DEPENDENTE));
            dependente.setSituacaoCadastralPessoa(SituacaoCadastralPessoa.ATIVO);
            dependente.setNaturalidade(dependentePortal.getNaturalidade());
            RG rg = verificarRGDependente(dependente, dependentePortal.getRg());
            CarteiraTrabalho carteira = verificarCarteiraTrabalhoDependente(dependente, dependentePortal.getCarteiraTrabalho());
            TituloEleitor titulo = verificarTituloDependente(dependente, dependentePortal.getTituloEleitor());
            CertidaoNascimento certidaoNascimento = verificarCertidaoNascimentoDependente(dependente, dependentePortal.getCertidaoNascimento());
            CertidaoCasamento certidaoCasamento = verificarCertidaoCasamentoDependente(dependente, dependentePortal.getCertidaoCasamento());
            SituacaoMilitar situacaoMilitar = verificarSituacaoMilitarDependente(dependente, dependentePortal.getSituacaoMilitar());

            if (dependentePortal.getTelefones() != null && !dependentePortal.getTelefones().isEmpty()) {
                List<Telefone> telefones = verificarTelefonesDependente(dependente, dependentePortal.getTelefones());
                if (telefones != null) {
                    dependente.setTelefonePrincipal(null);
                    for (Telefone telefone : telefones) {
                        if (telefone.getPrincipal()) {
                            dependente.setTelefonePrincipal(telefone);
                        }
                    }
                    dependente.getTelefones().addAll(telefones);
                }
            }

            if (dependentePortal.getEnderecos() != null && !dependentePortal.getEnderecos().isEmpty()) {
                List<EnderecoCorreio> enderecos = verificarEnderecosDependente(dependente, dependentePortal.getEnderecos());
                if (enderecos != null) {
                    dependente.setEnderecoPrincipal(null);
                    for (EnderecoCorreio endereco : enderecos) {
                        if (endereco.getPrincipal()) {
                            dependente.setEnderecoPrincipal(endereco);
                        }
                    }
                    dependente.getEnderecos().addAll(enderecos);
                }
            }

            if (rg != null) {
                Util.adicionarObjetoEmLista(dependente.getDocumentosPessoais(), rg);
            }
            if (carteira != null) {
                Util.adicionarObjetoEmLista(dependente.getDocumentosPessoais(), carteira);
            }
            if (titulo != null) {
                Util.adicionarObjetoEmLista(dependente.getDocumentosPessoais(), titulo);
            }
            if (certidaoNascimento != null) {
                Util.adicionarObjetoEmLista(dependente.getDocumentosPessoais(), certidaoNascimento);
            }
            if (certidaoCasamento != null) {
                Util.adicionarObjetoEmLista(dependente.getDocumentosPessoais(), certidaoCasamento);
            }
            if (situacaoMilitar != null) {
                Util.adicionarObjetoEmLista(dependente.getDocumentosPessoais(), situacaoMilitar);
            }
        }
        return em.merge(dependente);
    }

    private Dependente atribuirDadosDependente(Dependente dependente, DependentePortal dependentePortal) {
        dependente.setGrauDeParentesco(dependentePortal.getGrauDeParentesco());
        dependente.setResponsavel(dependentePortal.getPessoaFisicaPortal().getPessoaFisica());
        dependente.setAtivo(Boolean.TRUE);

        return dependente;
    }

    private void inativarDependente(PessoaFisicaPortal pessoaFisicaPortal) {
        if (pessoaFisicaPortal.getDependentes() != null) {
            for (DependentePortal dependentePortal : pessoaFisicaPortal.getDependentes()) {
                if (dependentePortal.getInativarDependente() && dependentePortal.getIdDependente() != null) {
                    Dependente dep = dependenteFacade.recuperar(dependentePortal.getIdDependente());
                    dep.setAtivo(Boolean.FALSE);
                    for (DependenteVinculoFP vinculoWeb : dep.getDependentesVinculosFPs()) {
                        if (vinculoWeb.getFinalVigencia() == null || vinculoWeb.getFinalVigencia().after(sistemaFacade.getDataOperacao())) {
                            vinculoWeb.setFinalVigencia(sistemaFacade.getDataOperacao());
                            em.merge(vinculoWeb);
                        }
                    }
                    em.merge(dep);
                }
            }
        }
    }

    public PessoaFisica copiarPessoaPortalParaPessoaFisicaRH(PessoaFisicaPortal pessoaPortal) {
        pessoaPortal = recuperar(pessoaPortal.getId());
        PessoaFisica pessoa = pessoaFisicaFacade.recuperar(pessoaPortal.getPessoaFisica().getId());
        pessoaPortal.setPessoaFisica(pessoa);
        PessoaFisica pf = PessoaFisica.pessoaPortalToPessoaFisica(pessoa, pessoaPortal);
        verificarDocumentosPessoais(pf, pessoaPortal);
        verificarTempoAnterior(pessoaPortal.getItemTempoContratoFPPessoaPortal(), pf);
        return em.merge(pf);
    }

    public void copiarDependentePortal(PessoaFisicaPortal pessoaPortal, PessoaFisica pf){
        salvarDependentes(pessoaPortal, pf);
        inativarDependente(pessoaPortal);
        pessoaPortal.setStatus(SituacaoPessoaPortal.LIBERADO);
        pessoaPortal.setUsuario(sistemaFacade.getUsuarioCorrente());
        pessoaPortal.setLiberadoEm(new Date());
        em.merge(pessoaPortal);
    }

    private void verificarTempoAnterior(List<TempoContratoFP> tempoContratoFPS, PessoaFisica pessoaFisica) {
        pessoaFisica.getItemTempoContratoFPPessoa().clear();
        for (TempoContratoFP tempoContratoFP : tempoContratoFPS) {
            TempoContratoFPPessoa tempoContratoFPPessoa = new TempoContratoFPPessoa();
            tempoContratoFPPessoa.setPessoaFisica(pessoaFisica);
            tempoContratoFPPessoa.setLocalTrabalho(tempoContratoFP.getLocalTrabalho());
            tempoContratoFPPessoa.setInicio(tempoContratoFP.getInicio());
            tempoContratoFPPessoa.setFim(tempoContratoFP.getFim());
            pessoaFisica.getItemTempoContratoFPPessoa().add(tempoContratoFPPessoa);
        }
    }

    private void verificarDocumentosPessoais(PessoaFisica pf, PessoaFisicaPortal pessoaPortal) {
        RG rg = verificarRG(pessoaPortal);
        TituloEleitor tituloEleitor = verificarTitulo(pessoaPortal);
        List<Habilitacao> habilitacao = verificarHabilitacao(pessoaPortal);
        CarteiraTrabalho carteiraTrabalho = verificarCarteiraTrabalho(pessoaPortal);
        SituacaoMilitar situacaoMilitar = verificarSituacaoMilitar(pessoaPortal);
        CertidaoNascimento certidaoNascimento = verificarCertidaoNascimento(pessoaPortal);
        CertidaoCasamento certidaoCasamento = verificarCertidaoCasamento(pessoaPortal);
        List<MatriculaFormacao> matriculaFormacao = verificarFormacao(pessoaPortal);
        List<Telefone> telefones = verificarTelefones(pessoaPortal);
        List<EnderecoCorreio> enderecos = verificarEnderecos(pessoaPortal);
        List<ConselhoClasseContratoFP> conselhos = verificarConselhos(pessoaPortal);
        List<PessoaHabilidade> habilidades = verificarHabilidades(pessoaPortal);

        if (rg != null) {
            pf.getDocumentosPessoais().add(rg);
        }
        if (tituloEleitor != null) {
            pf.getDocumentosPessoais().add(tituloEleitor);
        }
        if (habilitacao != null) {
            pf.getDocumentosPessoais().addAll(habilitacao);
        }
        if (carteiraTrabalho != null) {
            pf.getDocumentosPessoais().add(carteiraTrabalho);
        }
        if (situacaoMilitar != null) {
            pf.getDocumentosPessoais().add(situacaoMilitar);
        }
        if (certidaoNascimento != null) {
            pf.getDocumentosPessoais().add(certidaoNascimento);
        }
        if (certidaoCasamento != null) {
            pf.getDocumentosPessoais().add(certidaoCasamento);
        }
        if (matriculaFormacao != null) {
            pf.getFormacoes().addAll(matriculaFormacao);
        }
        if (habilidades != null) {
            pf.setHabilidades(habilidades);
        }
        if (telefones != null) {
            pf.setTelefonePrincipal(null);
            for (Telefone telefone : telefones) {
                if (telefone.getPrincipal()) {
                    pf.setTelefonePrincipal(telefone);
                }
            }
            pf.getTelefones().addAll(telefones);
        }

        if (enderecos != null) {
            pf.setEnderecoPrincipal(null);
            for (EnderecoCorreio endereco : enderecos) {
                if (endereco.getPrincipal()) {
                    pf.setEnderecoPrincipal(endereco);
                }
            }
            pf.getEnderecos().addAll(enderecos);
        }
        if (conselhos != null) {
            pf.getConselhoClasseContratos().addAll(conselhos);
        }


    }

    private List<PessoaHabilidade> verificarHabilidades(PessoaFisicaPortal pessoaPortal) {
        if (pessoaPortal.getHabilidades() != null && !pessoaPortal.getHabilidades().isEmpty()) {
            List<PessoaHabilidade> habilidades = Habilidade.toHabilidades(pessoaPortal.getPessoaFisica(), pessoaPortal.getHabilidades());
            pessoaPortal.getPessoaFisica().getHabilidades().clear();
            return habilidades;
        }
        return null;
    }

    private List<ConselhoClasseContratoFP> verificarConselhos(PessoaFisicaPortal pessoaPortal) {
        if (pessoaPortal.getConselhos() != null && !pessoaPortal.getConselhos().isEmpty()) {
            List<ConselhoClasseContratoFP> conselhos = ConselhoClasseContratoFP.toConselhos(pessoaPortal.getPessoaFisica(), pessoaPortal.getConselhos());
            pessoaPortal.getPessoaFisica().getConselhoClasseContratos().clear();
            return conselhos;
        }
        return null;
    }

    private List<EnderecoCorreio> verificarEnderecos(PessoaFisicaPortal pessoaPortal) {
        if (pessoaPortal.getEnderecos() != null && !pessoaPortal.getEnderecos().isEmpty()) {
            List<EnderecoCorreio> enderecos = EnderecoCorreio.toEnderecos(pessoaPortal.getPessoaFisica(), pessoaPortal.getEnderecos());
            pessoaPortal.getPessoaFisica().getEnderecos().clear();
            return enderecos;
        }
        return null;
    }

    private List<EnderecoCorreio> verificarEnderecosDependente(PessoaFisica pf, List<EnderecoCorreioPortal> enderecos) {
        if (enderecos != null && !enderecos.isEmpty()) {
            List<EnderecoCorreio> retorno = EnderecoCorreio.toEnderecos(pf, enderecos);
            pf.getEnderecos().clear();
            return retorno;
        }
        return null;
    }

    private List<Telefone> verificarTelefones(PessoaFisicaPortal pessoaPortal) {
        if (pessoaPortal.getTelefones() != null && !pessoaPortal.getTelefones().isEmpty()) {
            List<Telefone> telefones = Telefone.toTelefones(pessoaPortal.getPessoaFisica(), pessoaPortal.getTelefones());
            pessoaPortal.getPessoaFisica().getTelefones().clear();
            return telefones;
        }
        return null;
    }

    private List<Telefone> verificarTelefonesDependente(PessoaFisica pf, List<TelefonePortal> telefones) {
        if (telefones != null && !telefones.isEmpty()) {
            List<Telefone> retorno = Telefone.toTelefones(pf, telefones);
            pf.getTelefones().clear();
            return retorno;
        }
        return null;
    }

    private List<MatriculaFormacao> verificarFormacao(PessoaFisicaPortal pessoaPortal) {
        if (pessoaPortal.getFormacoesPessoa() != null && !pessoaPortal.getFormacoesPessoa().isEmpty()) {
            List<MatriculaFormacao> formacoes = MatriculaFormacao.toMatriculasFormacao(pessoaPortal.getPessoaFisica(), pessoaPortal.getFormacoesPessoa());
            limparFormacoes(pessoaPortal.getPessoaFisica());
            return formacoes;
        }
        return null;
    }

    private CertidaoCasamento verificarCertidaoCasamento(PessoaFisicaPortal pessoaPortal) {
        if (pessoaPortal.getCertidaoCasamento() != null && pessoaPortal.getCertidaoCasamento().isCamposPreenchidos()) {
            CertidaoCasamento certidao = CertidaoCasamento.toCertidaoCasamentoPortal(pessoaPortal.getPessoaFisica(), pessoaPortal.getCertidaoCasamento());
            limparCertidaoCasamento(pessoaPortal.getPessoaFisica());
            return certidao;
        }
        return null;
    }

    private CertidaoCasamento verificarCertidaoCasamentoDependente(PessoaFisica pessoa, CertidaoCasamentoPortal certidaoCasamento) {
        if (certidaoCasamento != null && certidaoCasamento.isCamposPreenchidos()) {
            CertidaoCasamento certidao = CertidaoCasamento.toCertidaoCasamentoPortal(pessoa, certidaoCasamento);
            limparCertidaoCasamento(pessoa);
            return certidao;
        }
        return null;
    }


    private CertidaoNascimento verificarCertidaoNascimento(PessoaFisicaPortal pessoaPortal) {
        if (pessoaPortal.getCertidaoNascimento() != null) {
            CertidaoNascimento certidao = CertidaoNascimento.toCertidaoNascimento(pessoaPortal.getPessoaFisica(), pessoaPortal.getCertidaoNascimento());
            limparCertidaoNascimento(pessoaPortal.getPessoaFisica());
            return certidao;
        }
        return null;
    }

    private CertidaoNascimento verificarCertidaoNascimentoDependente(PessoaFisica pessoa, CertidaoNascimentoPortal certidaoNascimento) {
        if (certidaoNascimento != null) {
            CertidaoNascimento certidao = CertidaoNascimento.toCertidaoNascimento(pessoa, certidaoNascimento);
            limparCertidaoNascimento(pessoa);
            return certidao;
        }
        return null;
    }

    private SituacaoMilitar verificarSituacaoMilitar(PessoaFisicaPortal pessoaPortal) {
        if (pessoaPortal.getSituacaoMilitar() != null && pessoaPortal.getSituacaoMilitar().isCamposPreenchidos()) {
            SituacaoMilitar situacao = SituacaoMilitar.toSituacaoMilitar(pessoaPortal.getPessoaFisica(), pessoaPortal.getSituacaoMilitar());
            limparSituacaoMilitar(pessoaPortal.getPessoaFisica());
            return situacao;
        }
        return null;
    }

    private SituacaoMilitar verificarSituacaoMilitarDependente(PessoaFisica pessoa, SituacaoMilitarPortal situacaoMilitar) {
        if (situacaoMilitar != null && situacaoMilitar.isCamposPreenchidos()) {
            SituacaoMilitar situacao = SituacaoMilitar.toSituacaoMilitar(pessoa, situacaoMilitar);
            limparSituacaoMilitar(pessoa);
            return situacao;
        }
        return null;
    }


    private CarteiraTrabalho verificarCarteiraTrabalho(PessoaFisicaPortal pessoaPortal) {
        if (pessoaPortal.getCarteiraTrabalho() != null) {
            CarteiraTrabalho titulo = CarteiraTrabalho.toCarteiraTrabalho(pessoaPortal.getPessoaFisica(), pessoaPortal.getCarteiraTrabalho());
            limparCarteiraTrabalho(pessoaPortal.getPessoaFisica());
            return titulo;
        }
        return null;
    }

    private CarteiraTrabalho verificarCarteiraTrabalhoDependente(PessoaFisica pessoa, CarteiraTrabalhoPortal carteiraTrabalho) {
        if (carteiraTrabalho != null) {
            CarteiraTrabalho carteira = CarteiraTrabalho.toCarteiraTrabalho(pessoa, carteiraTrabalho);
            limparCarteiraTrabalho(pessoa);
            return carteira;
        }
        return null;
    }


    private List<Habilitacao> verificarHabilitacao(PessoaFisicaPortal pessoaPortal) {
        if (pessoaPortal.getHabilitacao() != null) {
            List<Habilitacao> titulo = Habilitacao.toHabilitacoes(pessoaPortal.getPessoaFisica(), pessoaPortal.getHabilitacao());
            limparHabilitacao(pessoaPortal.getPessoaFisica());
            return titulo;
        }
        return null;
    }


    private TituloEleitor verificarTitulo(PessoaFisicaPortal pessoaPortal) {
        if (pessoaPortal.getTituloEleitor() != null) {
            TituloEleitor titulo = TituloEleitor.toTituloEleitor(pessoaPortal.getPessoaFisica(), pessoaPortal.getTituloEleitor());
            limparTituloEleitor(pessoaPortal.getPessoaFisica());
            return titulo;
        }
        return null;
    }

    private TituloEleitor verificarTituloDependente(PessoaFisica pessoa, TituloEleitorPortal tituloEleitor) {
        if (tituloEleitor != null) {
            TituloEleitor titulo = TituloEleitor.toTituloEleitor(pessoa, tituloEleitor);
            limparTituloEleitor(pessoa);
            return titulo;
        }
        return null;
    }

    private RG verificarRG(PessoaFisicaPortal pessoaPortal) {
        if (pessoaPortal.getRg() != null) {
            RG rg = RG.rgPortalToRG(pessoaPortal.getPessoaFisica(), pessoaPortal.getRg());
            limparRG(pessoaPortal.getPessoaFisica());
            return rg;
        }
        return null;
    }

    private RG verificarRGDependente(PessoaFisica pessoa, RGPortal rgPortal) {
        limparRG(pessoa);
        if (rgPortal != null) {
            RG rg = RG.rgPortalToRG(pessoa, rgPortal);
            return rg;
        }
        return null;
    }

    private void limparRG(PessoaFisica pf) {
        List<DocumentoPessoal> rgs = Lists.newArrayList();
        for (DocumentoPessoal documentoPessoal : pf.getDocumentosPessoais()) {
            if (documentoPessoal instanceof RG) {
                rgs.add(documentoPessoal);
            }
        }
        if (!rgs.isEmpty()) {
            pf.getDocumentosPessoais().removeAll(rgs);
        }
        documentoPessoalFacade.removerRGPorPessoa(pf);
    }

    private void limparTituloEleitor(PessoaFisica pf) {
        List<DocumentoPessoal> titulos = Lists.newArrayList();
        for (DocumentoPessoal documentoPessoal : pf.getDocumentosPessoais()) {
            if (documentoPessoal instanceof TituloEleitor) {
                titulos.add(documentoPessoal);
            }
        }
        if (!titulos.isEmpty()) {
            pf.getDocumentosPessoais().removeAll(titulos);
        }
        documentoPessoalFacade.removerTituloEletiorPorPessoa(pf);
    }

    private void limparHabilitacao(PessoaFisica pessoaFisica) {
        List<DocumentoPessoal> titulos = Lists.newArrayList();
        for (DocumentoPessoal documentoPessoal : pessoaFisica.getDocumentosPessoais()) {
            if (documentoPessoal instanceof Habilitacao) {
                titulos.add(documentoPessoal);
            }
        }
        if (!titulos.isEmpty()) {
            pessoaFisica.getDocumentosPessoais().removeAll(titulos);
        }
        documentoPessoalFacade.removerHabilitcaoPorPessoa(pessoaFisica);
    }

    private void limparCarteiraTrabalho(PessoaFisica pessoaFisica) {
        List<DocumentoPessoal> titulos = Lists.newArrayList();
        for (DocumentoPessoal documentoPessoal : pessoaFisica.getDocumentosPessoais()) {
            if (documentoPessoal instanceof CarteiraTrabalho) {
                titulos.add(documentoPessoal);
            }
        }
        if (!titulos.isEmpty()) {
            pessoaFisica.getDocumentosPessoais().removeAll(titulos);
        }
        documentoPessoalFacade.removerCarteiraTrabalhoPorPessoa(pessoaFisica);
    }

    private void limparSituacaoMilitar(PessoaFisica pessoaFisica) {
        List<DocumentoPessoal> situacoes = Lists.newArrayList();
        for (DocumentoPessoal documentoPessoal : pessoaFisica.getDocumentosPessoais()) {
            if (documentoPessoal instanceof SituacaoMilitar) {
                situacoes.add(documentoPessoal);
            }
        }
        if (!situacoes.isEmpty()) {
            pessoaFisica.getDocumentosPessoais().removeAll(situacoes);
        }
        documentoPessoalFacade.removerSituacaoMilitarPorPessoa(pessoaFisica);
    }

    private void limparCertidaoNascimento(PessoaFisica pessoaFisica) {
        List<DocumentoPessoal> titulos = Lists.newArrayList();
        for (DocumentoPessoal documentoPessoal : pessoaFisica.getDocumentosPessoais()) {
            if (documentoPessoal instanceof CertidaoNascimento) {
                titulos.add(documentoPessoal);
            }
        }
        if (!titulos.isEmpty()) {
            pessoaFisica.getDocumentosPessoais().removeAll(titulos);
        }
        documentoPessoalFacade.removerCertidaoNascimentoPorPessoa(pessoaFisica);
    }

    private void limparCertidaoCasamento(PessoaFisica pessoaFisica) {
        List<DocumentoPessoal> titulos = Lists.newArrayList();
        for (DocumentoPessoal documentoPessoal : pessoaFisica.getDocumentosPessoais()) {
            if (documentoPessoal instanceof CertidaoCasamento) {
                titulos.add(documentoPessoal);
            }
        }
        if (!titulos.isEmpty()) {
            pessoaFisica.getDocumentosPessoais().removeAll(titulos);
        }
        documentoPessoalFacade.removerCertidaoCasamentoPorPessoa(pessoaFisica);
    }

    private void limparFormacoes(PessoaFisica pessoaFisica) {
        pessoaFisica.getFormacoes().clear();
    }


    public PessoaFisicaPortal buscarPessoaPortalPorCpf(String cpf) {
        String hql = " from PessoaFisicaPortal pf " +
            " where REGEXP_REPLACE(pf.cpf, '[^0-9]') = REGEXP_REPLACE(:cpf, '[^0-9]') order by liberadoEm desc";
        Query q = em.createQuery(hql);
        q.setParameter("cpf", cpf);
        if (!q.getResultList().isEmpty()) {
            return (PessoaFisicaPortal) q.getResultList().get(0);
        }
        return null;

    }

    public PessoaFisica buscarPessoaFisicaPeloCpf(String cpf) {
        String hql = " from PessoaFisica pf " +
            " where REGEXP_REPLACE(pf.cpf, '[^0-9]') = REGEXP_REPLACE(:cpf, '[^0-9]') ";

        Query q = em.createQuery(hql);
        q.setParameter("cpf", StringUtil.retornaApenasNumeros(cpf));

        if (!q.getResultList().isEmpty()) {
            return (PessoaFisica) q.getResultList().get(0);
        }
        return null;
    }

    public Dependente buscarDependentePelaPessoaFisica(String cpf) {
        String hql = " from Dependente dependente " +
            " where REGEXP_REPLACE(dependente.dependente.cpf, '[^0-9]') = REGEXP_REPLACE(:cpf, '[^0-9]') ";

        Query q = em.createQuery(hql);
        q.setParameter("cpf", StringUtil.retornaApenasNumeros(cpf));

        if (!q.getResultList().isEmpty()) {
            return (Dependente) q.getResultList().get(0);
        }
        return null;
    }

    public PessoaFisicaPortal buscarPessoaPortalPorPessoaId(Long idPessoa) {
        String hql = " from PessoaFisicaPortal pf " +
            " where pf.pessoaFisica.id = :id order by liberadoEm desc ";
        Query q = em.createQuery(hql);
        q.setParameter("id", idPessoa);
        if (!q.getResultList().isEmpty()) {
            return (PessoaFisicaPortal) q.getResultList().get(0);
        }
        return null;

    }

    public Dependente buscarDependentePorDependentePortal(DependentePortal dependente) {
        String hql = " from Dependente dependente " +
            " where dependente.id = :idDep ";

        Query q = em.createQuery(hql);
        q.setParameter("idDep", dependente.getIdDependente());
        if (!q.getResultList().isEmpty()) {
            return (Dependente) q.getResultList().get(0);
        }
        return null;
    }

    public Dependente recuperarDependenteSelecionadoAutorizacaoPortal(Object id) {
        Dependente dependente = em.find(Dependente.class, id);
        dependente.getDependentesVinculosFPs().size();
        return dependente;
    }

    public void salvarNovoDependentePortal(DependentePortal dependentePortal) {
        em.merge(dependentePortal);
    }
}


