package br.com.webpublico.controle.rh.portal;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.controle.Web;
import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.cadastrofuncional.TempoContratoFP;
import br.com.webpublico.entidades.rh.cadastrofuncional.TempoContratoFPPessoa;
import br.com.webpublico.entidades.rh.portal.atualizacaocadastral.*;
import br.com.webpublico.enums.rh.SituacaoPessoaPortal;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.DependenteFacade;
import br.com.webpublico.negocios.PessoaFisicaFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.rh.portal.CamposAlteradosPortalFacade;
import br.com.webpublico.negocios.rh.portal.PessoaFisicaPortalFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * Created by peixe on 01/09/17.
 */
@ManagedBean(name = "pessoaFisicaPortalControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "editar-pessoa-fisica-portal", pattern = "/pessoa-portal/editar/#{pessoaFisicaPortalControlador.id}/", viewId = "/faces/rh/portal/pessoafisicaportal/edita.xhtml"),
    @URLMapping(id = "listar-pessoa-fisica-portal", pattern = "/pessoa-portal/listar/", viewId = "/faces/rh/portal/pessoafisicaportal/lista.xhtml"),
    @URLMapping(id = "ver-pessoa-fisica-portal", pattern = "/pessoa-portal/ver/#{pessoaFisicaPortalControlador.id}/", viewId = "/faces/rh/portal/pessoafisicaportal/visualizar.xhtml")
})
public class PessoaFisicaPortalControlador extends PrettyControlador<PessoaFisicaPortal> implements Serializable, CRUD {

    @EJB
    private PessoaFisicaPortalFacade pessoaFisicaPortalFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private PessoaFisica pessoaFisica;
    @EJB
    private DependenteFacade dependenteFacade;
    @EJB
    private CamposAlteradosPortalFacade camposAlteradosPortalFacade;
    private Dependente dependenteSelecionado;
    private DependentePortal dependenteAlterado;
    private PessoaFisica pessoaFisicaDependente;

    public PessoaFisicaPortalControlador() {
        super(PessoaFisicaPortal.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/pessoa-portal/";
    }

    @Override
    public Object getUrlKeyValue() {
        return getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return pessoaFisicaPortalFacade;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public Dependente getDependenteSelecionado() {
        return dependenteSelecionado;
    }

    public void setDependenteSelecionado(Dependente dependenteSelecionado) {
        this.dependenteSelecionado = dependenteSelecionado;
    }

    public DependentePortal getDependenteAlterado() {
        return dependenteAlterado;
    }

    public void setDependenteAlterado(DependentePortal dependenteAlterado) {
        this.dependenteAlterado = dependenteAlterado;
    }

    public PessoaFisica getPessoaFisicaDependente() {
        return pessoaFisicaDependente;
    }

    @URLAction(mappingId = "ver-pessoa-fisica-portal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @URLAction(mappingId = "editar-pessoa-fisica-portal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();    //To change body of overridden methods use File | Settings | File Templates.
        pessoaFisica = pessoaFisicaFacade.recuperar(selecionado.getPessoaFisica().getId());
    }

    public List<Dependente> dependentesPorResponsavel() {
        return dependenteFacade.dependentesPorResponsavel(selecionado.getPessoaFisica());
    }

    public void copiarDadosPessoaFisica() {
        try {
            PessoaFisica pf = pessoaFisicaPortalFacade.copiarPessoaPortalParaPessoaFisicaRH(selecionado);
            pessoaFisicaPortalFacade.copiarDependentePortal(selecionado, pf);
            atribuirIdDependente();
            camposAlteradosPortalFacade.atualizarCamposAlteradosPortal(selecionado.getPessoaFisica());
            FacesUtil.addOperacaoRealizada("Operação realizada com sucesso. Os dados foram atualizados.");
            redireciona();
        } catch (ValidacaoException val) {
            FacesUtil.printAllFacesMessages(val.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addError("Erro:", e.getMessage());
        }
    }

    private void atribuirIdDependente() {
        for (DependentePortal dp : selecionado.getDependentes()) {
            if (dp.getIdDependente() == null) {
                Dependente dependente = pessoaFisicaPortalFacade.buscarDependentePelaPessoaFisica(dp.getCpf());
                dp.setIdDependente(dependente.getId());
                pessoaFisicaPortalFacade.salvarNovoDependentePortal(dp);
            }
        }
    }

    public String getMensagem() {
        String mensagem = "";
        if (selecionado != null && selecionado.getStatus() != null) {
            mensagem += selecionado.getStatus();
            if (SituacaoPessoaPortal.LIBERADO.equals(selecionado.getStatus())) {
                mensagem += " Por " + selecionado.getUsuario();
                mensagem += " Em " + DataUtil.getDataFormatadaDiaHora(selecionado.getLiberadoEm());
            }
        }
        return mensagem;
    }


    public void imprimirComprovanteRecadastramento() {
        AbstractReport abstractReport = new AbstractReport().getAbstractReport();
        abstractReport.setGeraNoDialog(true);
        String arquivoJasper = "ComprovanteRecadastramento.jasper";
        HashMap parameters = new HashMap();
        parameters.put("MUNICIPIO", "MUNICIPIO DE RIO BRANCO");
        parameters.put("SECRETARIA", "SECRETARIA MINICIPAL DE ADMINISTRAÇÃO");
        parameters.put("NOMERELATORIO", "DEPARTAMENTO DE RECURSOS HUMANOS");
        parameters.put("BRASAO", abstractReport.getCaminhoImagem());
        parameters.put("USUARIO", sistemaFacade.getUsuarioCorrente().getPessoaFisica().getNome());
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(Lists.newArrayList(montarComprovanteComparamentroPortal()));
        try {
            abstractReport.gerarRelatorioComDadosEmCollection(arquivoJasper, parameters, ds);
        } catch (JRException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private ComprovanteComparecimentoPortal montarComprovanteComparamentroPortal() {
        ComprovanteComparecimentoPortal comprovante = new ComprovanteComparecimentoPortal();
        comprovante.setNomeServidor(selecionado.getPessoaFisica().getNome());
        comprovante.setDataCadastro(DataUtil.getDataFormatada(selecionado.getLiberadoEm()));
        comprovante.setAnoCadastro(DataUtil.getAno(selecionado.getLiberadoEm()) + "");
        comprovante.setAtendente(selecionado.getUsuario().getPessoaFisica().getNome());
        return comprovante;
    }

    public void atribuirDependentePortal(DependentePortal dependentePortal) {
        dependenteSelecionado = new Dependente();
        dependenteAlterado = new DependentePortal();

        if (dependentePortal.getIdDependente() != null) {
            dependenteSelecionado = dependenteFacade.recuperar(dependentePortal.getIdDependente());
            dependenteSelecionado = pessoaFisicaPortalFacade.recuperarDependenteSelecionadoAutorizacaoPortal(dependenteSelecionado.getId());
            PessoaFisica pessoaRecuperada = pessoaFisicaFacade.recuperar(this.dependenteSelecionado.getDependente().getId());
            this.dependenteSelecionado.setDependente((PessoaFisica) Util.clonarObjeto(pessoaRecuperada));
        }
        dependenteAlterado = (DependentePortal) Util.clonarObjeto(dependentePortal);
        pessoaFisicaDependente = pessoaFisicaPortalFacade.buscarPessoaFisicaPeloCpf(dependentePortal.getCpf());
    }

    public void fecharDialogDependentes() {
        dependenteSelecionado = null;
        dependenteAlterado = null;
        pessoaFisicaDependente = null;
    }

    public void verPessoaFisicaDependente() {
        if (pessoaFisicaDependente != null) {
            Web.navegacao(getUrlAtual(), "/pessoa/ver/" + pessoaFisicaDependente.getId() + "/");
        }
    }

    public boolean isRGIguais(RGPortal rgPortal, RG rgPessoa) {
        RGPortal rg = new RGPortal();
        if (rgPessoa != null) {
            rg.setNumero(rgPessoa.getNumero());
            rg.setDataemissao(rgPessoa.getDataemissao());
            rg.setOrgaoEmissao(rgPessoa.getOrgaoEmissao());
            rg.setUf(rgPessoa.getUf());
        }
        if (rgPortal == null) {
            rgPortal = new RGPortal();
        }
        return rgPortal.equals(rg);
    }

    public boolean isTitulosEleitorIguais(TituloEleitorPortal tituloEleitorPortal, TituloEleitor tituloEleitorPessoa) {
        TituloEleitorPortal tituloEleitor = new TituloEleitorPortal();
        if (tituloEleitorPessoa != null) {
            tituloEleitor.setNumero(tituloEleitorPessoa.getNumero());
            tituloEleitor.setZona(tituloEleitorPessoa.getZona());
            tituloEleitor.setSessao(tituloEleitorPessoa.getSessao());
            tituloEleitor.setDataEmissao(tituloEleitorPessoa.getDataEmissao());
            tituloEleitor.setCidade(tituloEleitorPessoa.getCidade());
        }
        if (tituloEleitorPortal == null) {
            tituloEleitorPortal = new TituloEleitorPortal();
        }
        return tituloEleitorPortal.equals(tituloEleitor);
    }

    public boolean isHabilitacoesIguais(List<CarteiraHabilitacaoPortal> habilitacoesPortal, Habilitacao habilitacaoPessoa) {
        CarteiraHabilitacaoPortal habilitacao1 = getHabilitacao(habilitacoesPortal);
        CarteiraHabilitacaoPortal habilitacao = new CarteiraHabilitacaoPortal();
        if (habilitacaoPessoa != null) {
            habilitacao.setNumero(habilitacaoPessoa.getNumero());
            habilitacao.setCategoria(habilitacaoPessoa.getCategoria());
            habilitacao.setValidade(habilitacaoPessoa.getValidade());
            habilitacao.setDataEmissao(habilitacaoPessoa.getDataEmissao());
            habilitacao.setOrgaoExpedidor(habilitacaoPessoa.getOrgaoExpedidor());
        }
        return habilitacao1.equals(habilitacao);
    }

    public boolean isCarteirasDeTrabalhoIguais(CarteiraTrabalhoPortal carteiraTrabalhoPortal, CarteiraTrabalho carteiraTrabalhoPessoa) {
        CarteiraTrabalhoPortal carteiraTrabalho = new CarteiraTrabalhoPortal();
        if (carteiraTrabalhoPessoa != null) {
            carteiraTrabalho.setNumero(carteiraTrabalhoPessoa.getNumero());
            carteiraTrabalho.setSerie(carteiraTrabalhoPessoa.getSerie());
            carteiraTrabalho.setDataEmissao(carteiraTrabalhoPessoa.getDataEmissao());
            carteiraTrabalho.setUf(carteiraTrabalhoPessoa.getUf());
            carteiraTrabalho.setPisPasep(carteiraTrabalhoPessoa.getPisPasep());
            carteiraTrabalho.setBancoPisPasep(carteiraTrabalhoPessoa.getBancoPisPasep());
            carteiraTrabalho.setDataEmissaoPisPasep(carteiraTrabalhoPessoa.getDataEmissaoPisPasep());
            carteiraTrabalho.setAnoPrimeiroEmprego(carteiraTrabalhoPessoa.getAnoPrimeiroEmprego());
        }
        if (carteiraTrabalhoPortal == null) {
            carteiraTrabalhoPortal = new CarteiraTrabalhoPortal();
        }
        return carteiraTrabalhoPortal.equals(carteiraTrabalho);
    }

    public boolean isSituacoesMilitarIguais(SituacaoMilitarPortal situacaoMilitarPortal, SituacaoMilitar situacaoMilitarPessoa) {
        SituacaoMilitarPortal situacaoMilitar = new SituacaoMilitarPortal();
        if (situacaoMilitarPessoa != null) {
            situacaoMilitar.setTipoSituacaoMilitar(situacaoMilitarPessoa.getTipoSituacaoMilitar());
            situacaoMilitar.setCertificadoMilitar(situacaoMilitarPessoa.getCertificadoMilitar());
            situacaoMilitar.setSerieCertificadoMilitar(situacaoMilitarPessoa.getSerieCertificadoMilitar());
            situacaoMilitar.setCategoriaCertificadoMilitar(situacaoMilitarPessoa.getCategoriaCertificadoMilitar());
            situacaoMilitar.setDataEmissao(situacaoMilitarPessoa.getDataEmissao());
            situacaoMilitar.setOrgaoEmissao(situacaoMilitarPessoa.getOrgaoEmissao());
        }
        if (situacaoMilitarPortal == null) {
            situacaoMilitarPortal = new SituacaoMilitarPortal();
        }
        return situacaoMilitarPortal.equals(situacaoMilitar);
    }

    public boolean isCertidoesNascimentoIguais(CertidaoNascimentoPortal certidaoNascimentoPortal, CertidaoNascimento certidaoNascimentoPessoa) {
        CertidaoNascimentoPortal certidaoNascimento = new CertidaoNascimentoPortal();
        if (certidaoNascimentoPessoa != null) {
            certidaoNascimento.setNomeCartorio(certidaoNascimentoPessoa.getNomeCartorio());
            certidaoNascimento.setNumeroLivro(certidaoNascimentoPessoa.getNumeroLivro());
            certidaoNascimento.setNumeroFolha(certidaoNascimentoPessoa.getNumeroFolha());
            certidaoNascimento.setNumeroRegistro(certidaoNascimentoPessoa.getNumeroRegistro());
            certidaoNascimento.setCidade(certidaoNascimentoPessoa.getCidade());
        }
        if (certidaoNascimentoPortal == null) {
            certidaoNascimentoPortal = new CertidaoNascimentoPortal();
        }
        return certidaoNascimentoPortal.equals(certidaoNascimento);
    }

    public boolean isCertidoesCasamentoIguais(CertidaoCasamentoPortal certidaoCasamentoPortal, CertidaoCasamento certidaoCasamentoPessoa) {
        CertidaoCasamentoPortal certidaoCasamento = new CertidaoCasamentoPortal();
        if (certidaoCasamentoPessoa != null) {
            certidaoCasamento.setNomeCartorio(certidaoCasamentoPessoa.getNomeCartorio());
            certidaoCasamento.setCidadeCartorio(certidaoCasamentoPessoa.getCidadeCartorio());
            certidaoCasamento.setNumeroLivro(certidaoCasamentoPessoa.getNumeroLivro());
            certidaoCasamento.setNumeroFolha(certidaoCasamentoPessoa.getNumeroFolha());
            certidaoCasamento.setNumeroRegistro(certidaoCasamentoPessoa.getNumeroRegistro());
            certidaoCasamento.setLocalTrabalhoConjuge(certidaoCasamentoPessoa.getLocalTrabalhoConjuge());
            certidaoCasamento.setNomeConjuge(certidaoCasamentoPessoa.getNomeConjuge());
            certidaoCasamento.setNacionalidade(certidaoCasamentoPessoa.getNacionalidade());
            certidaoCasamento.setNaturalidadeConjuge(certidaoCasamentoPessoa.getNaturalidadeConjuge());
            certidaoCasamento.setEstado(certidaoCasamentoPessoa.getEstado());
            certidaoCasamento.setDataCasamento(certidaoCasamentoPessoa.getDataCasamento());
            certidaoCasamento.setDataNascimentoConjuge(certidaoCasamentoPessoa.getDataNascimentoConjuge());
            certidaoCasamento.setDataAverbacao(certidaoCasamentoPessoa.getDataAverbacao());
        }
        if (certidaoCasamentoPortal == null) {
            certidaoCasamentoPortal = new CertidaoCasamentoPortal();
        }
        return certidaoCasamentoPortal.equals(certidaoCasamento);
    }

    public boolean isFormacoesHabilidadesIguais(List<FormacaoPessoaPortal> formacoesPortal, List<HabilidadePortal> habilidadesPortal, List<MatriculaFormacao> formacoesPessoa, List<PessoaHabilidade> habilidadesPessoa) {
        List<FormacaoPessoaPortal> formacoes = Lists.newArrayList();
        List<HabilidadePortal> habilidades = Lists.newArrayList();
        if (formacoesPessoa != null) {
            for (MatriculaFormacao matriculaFormacao : formacoesPessoa) {
                FormacaoPessoaPortal formacao = new FormacaoPessoaPortal();
                formacao.setNomeCurso(matriculaFormacao.getFormacao() == null ? null : matriculaFormacao.getFormacao().getNome());
                formacao.setAreaFormacao(matriculaFormacao.getFormacao() == null ? null : matriculaFormacao.getFormacao().getAreaFormacao());
                formacao.setInstituicao(matriculaFormacao.getFormacao() != null && matriculaFormacao.getFormacao().getPromotorEvento() != null ? matriculaFormacao.getFormacao().getPromotorEvento().toString() : matriculaFormacao.getInstituicao());
                formacao.setDataInicio(matriculaFormacao.getDataInicio());
                formacao.setDataFim(matriculaFormacao.getDataFim());
                formacao.setConcluido(matriculaFormacao.getConcluido());
                formacoes.add(formacao);
            }
        }
        if (habilidadesPessoa != null) {
            for (PessoaHabilidade habilidade : habilidadesPessoa) {
                HabilidadePortal habilidadePortal = new HabilidadePortal();
                habilidadePortal.setHabilidade(habilidade.getHabilidade());
                habilidades.add(habilidadePortal);
            }
        }
        return isListasIguais(formacoesPortal, formacoes) && isListasIguais(habilidadesPortal, habilidades);
    }

    public boolean isTelefonesIguais(List<TelefonePortal> telefonesPortal, List<Telefone> telefonesPessoa) {
        List<TelefonePortal> telefones = Lists.newArrayList();
        if (telefonesPessoa != null) {
            for (Telefone telefone : telefonesPessoa) {
                TelefonePortal telefonePortal = new TelefonePortal();
                telefonePortal.setTelefone(telefone.getTelefone());
                telefonePortal.setTipoFone(telefone.getTipoFone());
                telefonePortal.setPessoaContato(telefone.getPessoaContato());
                telefonePortal.setPrincipal(telefone.getPrincipal());
                telefones.add(telefonePortal);
            }
        }
        return isListasIguais(telefonesPortal, telefones);
    }

    public boolean isEnderecosIguais(List<EnderecoCorreioPortal> enderecosPortal, List<EnderecoCorreio> enderecosPessoa) {
        List<EnderecoCorreioPortal> enderecos = Lists.newArrayList();
        if (enderecosPessoa != null) {
            for (EnderecoCorreio enderecoCorreio : enderecosPessoa) {
                EnderecoCorreioPortal enderecoPortal = new EnderecoCorreioPortal();
                enderecoPortal.setCep(enderecoCorreio.getCep());
                enderecoPortal.setLogradouro(enderecoCorreio.getLogradouro());
                enderecoPortal.setComplemento(enderecoCorreio.getComplemento());
                enderecoPortal.setBairro(enderecoCorreio.getBairro());
                enderecoPortal.setLocalidade(enderecoCorreio.getLocalidade());
                enderecoPortal.setUf(enderecoCorreio.getUf());
                enderecoPortal.setNumero(enderecoCorreio.getNumero());
                enderecoPortal.setTipoEndereco(enderecoCorreio.getTipoEndereco());
                enderecoPortal.setPrincipal(enderecoCorreio.getPrincipal());
                enderecos.add(enderecoPortal);
            }
        }
        return isListasIguais(enderecosPortal, enderecos);
    }

    public boolean isConselhosClassesIguais(List<ConselhoClasseOrdemPortal> conselhosPortal, List<ConselhoClasseContratoFP> conselhosPessoa) {
        List<ConselhoClasseOrdemPortal> conselhos = Lists.newArrayList();
        if (conselhosPessoa != null) {
            for (ConselhoClasseContratoFP conselhoClasseContratoFP : conselhosPessoa) {
                ConselhoClasseOrdemPortal conselho = new ConselhoClasseOrdemPortal();
                conselho.setDataEmissao(conselhoClasseContratoFP.getDataEmissao());
                conselho.setUf(conselhoClasseContratoFP.getUf());
                conselho.setConselhoClasseOrdem(conselhoClasseContratoFP.getConselhoClasseOrdem());
                conselho.setNumeroDocumento(conselhoClasseContratoFP.getNumeroDocumento());
                conselhos.add(conselho);
            }
        }
        return isListasIguais(conselhosPortal, conselhos);
    }

    public boolean isEstrangeirosIguais() {
        return ((selecionado.getTipoCondicaoIngresso() == null && pessoaFisica.getTipoCondicaoIngresso() == null) || (selecionado.getTipoCondicaoIngresso() != null && pessoaFisica.getTipoCondicaoIngresso() != null && selecionado.getTipoCondicaoIngresso().equals(pessoaFisica.getTipoCondicaoIngresso())))
            && ((selecionado.getCasadoBrasileiro() != null ? selecionado.getCasadoBrasileiro() : false) == (pessoaFisica.getCasadoBrasileiro() != null ? pessoaFisica.getCasadoBrasileiro() : false))
            && ((selecionado.getFilhoBrasileiro() != null ? selecionado.getFilhoBrasileiro() : false) == (pessoaFisica.getFilhoBrasileiro() != null ? pessoaFisica.getFilhoBrasileiro() : false));
    }


    public boolean isTemposAnterioresIguais(List<TempoContratoFP> temposPortal, List<TempoContratoFPPessoa> temposPessoa) {
        List<TempoContratoFP> tempos = Lists.newArrayList();
        if (temposPessoa != null) {
            for (TempoContratoFPPessoa tempo : temposPessoa) {
                TempoContratoFP tempoContratoFP = new TempoContratoFP();
                tempoContratoFP.setLocalTrabalho(tempo.getLocalTrabalho());
                tempoContratoFP.setInicio(tempo.getInicio());
                tempoContratoFP.setFim(tempo.getFim());
                tempos.add(tempoContratoFP);
            }
        }
        return isListasIguais(temposPortal, tempos);
    }

    public boolean isDependentesAlterados(List<DependentePortal> dependentes) {
        if (dependentes == null || dependentes.isEmpty()) {
            return false;
        }
        for (DependentePortal dependente : dependentes) {
            if (dependente.getAlterado() || dependente.getIdDependente() == null) {
                return true;
            }
        }
        return false;
    }

    public boolean isListasIguais(List<?> l1, List<?> l2) {
        if ((l1 == null && l2 != null) || (l2 == null && l1 != null)) {
            return false;
        }
        return Util.listasIguais(l1, l2);
    }

    public CarteiraHabilitacaoPortal getHabilitacao(List<CarteiraHabilitacaoPortal> habilitacoes) {
        if (habilitacoes.size() > 0) {
            Collections.sort(habilitacoes, new Comparator<CarteiraHabilitacaoPortal>() {
                @Override
                public int compare(CarteiraHabilitacaoPortal o1, CarteiraHabilitacaoPortal o2) {
                    if (o1 == null || o2 == null) {
                        return 0;
                    }
                    if (o1.getValidade() == null || o2.getValidade() == null) {
                        return 0;
                    }
                    return o2.getValidade().compareTo(o1.getValidade());
                }
            });
            return habilitacoes.get(0);
        }
        return null;
    }

    public boolean isLiberarAutorizacao() {
        return selecionado.getStatus().equals(SituacaoPessoaPortal.LIBERADO);
    }

    public class ComprovanteComparecimentoPortal {
        private String nomeServidor;
        private String dataCadastro;
        private String anoCadastro;
        private String atendente;

        public String getNomeServidor() {
            return nomeServidor;
        }

        public void setNomeServidor(String nomeServidor) {
            this.nomeServidor = nomeServidor;
        }

        public String getDataCadastro() {
            return dataCadastro;
        }

        public void setDataCadastro(String dataCadastro) {
            this.dataCadastro = dataCadastro;
        }

        public String getAnoCadastro() {
            return anoCadastro;
        }

        public void setAnoCadastro(String anoCadastro) {
            this.anoCadastro = anoCadastro;
        }

        public String getAtendente() {
            return atendente;
        }

        public void setAtendente(String atendente) {
            this.atendente = atendente;
        }
    }

}


