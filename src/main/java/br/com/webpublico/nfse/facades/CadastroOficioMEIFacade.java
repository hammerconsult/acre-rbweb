package br.com.webpublico.nfse.facades;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.nfse.domain.CadastroOficioMEI;
import br.com.webpublico.nfse.domain.LinhaCadastroOficioMEI;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

@Stateless
public class CadastroOficioMEIFacade extends AbstractFacade<CadastroOficioMEI> {


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private NaturezaJuridicaFacade naturezaJuridicaFacade;
    @EJB
    private CNAEFacade cnaeFacade;
    @EJB
    private PessoaJuridicaFacade pessoaJuridicaFacade;
    private LinhaCadastroOficioMEI linhaCadastroOficioMEI;

    private PessoaJuridica pessoaJuridica;
    private NaturezaJuridica naturezaJuridica;
    private PessoaCNAE pessoaCNAE;
    private boolean verificarUFECodigoMunicipio = false;
    private boolean verificarMEI = false;

    public CadastroOficioMEIFacade() {
        super(CadastroOficioMEI.class);
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Asynchronous
    public Future<List<LinhaCadastroOficioMEI>> lerArquivo(CadastroOficioMEI selecionado, AssistenteBarraProgresso assistente) {
        List<LinhaCadastroOficioMEI> toReturn = Lists.newArrayList();
        ArquivoComposicao arquivoComposicao = selecionado.getArquivoImportacao().getArquivoComposicao();
        arquivoComposicao.getArquivo().montarImputStream();
        InputStream inputStream = arquivoComposicao.getArquivo().getInputStream();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream));
            List<String> linhas = Lists.newArrayList();
            String linha = null;
            while ((linha = br.readLine()) != null) {
                linhas.add(linha);
            }
            assistente.setTotal(linhas.size());
            assistente.adicionarLogHtml("Início da Leitura do Arquivo");
            AtomicInteger numeroLinha = new AtomicInteger(0);
            String valorIdentificadorLinhaAnterior = "";
            int index = 0;
            for (String linhaLista : linhas) {
                linhaCadastroOficioMEI = new LinhaCadastroOficioMEI();
                linhaCadastroOficioMEI.setNumeroLinha(numeroLinha.addAndGet(1));
                try {
                    valorIdentificadorLinhaAnterior = index > 0 ? linhas.get(index-1).substring(0,2) : "";
                    preencherLinhaCadastroOficioMEI(linhaLista, valorIdentificadorLinhaAnterior);
                    index++;
                    toReturn.add(linhaCadastroOficioMEI);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    linhaCadastroOficioMEI.setErro(e.getMessage());
                    linhaCadastroOficioMEI.setErroComCNPJ(" - CNPJ Base " + linhaLista.substring(30, 44) + " Não foi possivel processar essa linha. Detalhes do erro: " + e.getMessage());
                    assistente.adicionarLogHtml("Linha " + linhaCadastroOficioMEI.getNumeroLinha() + " não verificada. detalhes do erro: " + e.getMessage());
                    toReturn.add(linhaCadastroOficioMEI);
                }
                assistente.conta();
            }
            assistente.adicionarLogHtml("Fim da Leitura do Arquivo");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        return new AsyncResult<>(toReturn);
    }

    public void realizarValidacoes(CadastroOficioMEI selecionado) throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDetentorArquivoComposicao() == null) {
           ve.adicionarMensagemDeOperacaoNaoPermitida("Por favor selecione o arquivo para importação.");
        }
        ve.lancarException();
    }

    @Async
    @Transactional
    public Future<CadastroOficioMEI> processarArquivo(CadastroOficioMEI selecionado,
                                                          List<LinhaCadastroOficioMEI> linhas,
                                                          AssistenteBarraProgresso assistente) {
        selecionado = em.merge(selecionado);
        assistente.setTotal(linhas.size());
        assistente.adicionarLogHtml("Início do Processamento do Arquivo");
        for (LinhaCadastroOficioMEI linha : linhas) {
            try {
                linha.setCadastroOficioMEI(selecionado);
                if("01".equals(linha.getCodigoRegistro())){
                    salvarPessoaJuridica(linha, assistente);
                } else {
                    salvarCadastroEconomico(linha, assistente);
                }
            } catch (Exception e) {
                logger.error("Erro ao processar arquivo MEI. ", e);
                assistente.adicionarLogHtml("Linha " + linha.getNumeroLinha() + " - Falha ao processar linha. Motivo: " + e.getMessage());

            }
            assistente.conta();
        }
        assistente.adicionarLogHtml("Fim do Processamento do Arquivo");
        return new AsyncResult<>(selecionado);
    }

    public EnderecoCorreio retornaEnderecoPJ(LinhaCadastroOficioMEI linhaCadastroOficioMEI){
        EnderecoCorreio enderecoCorreio = new EnderecoCorreio();
        enderecoCorreio.setLogradouro(linhaCadastroOficioMEI.getLogradouro());
        enderecoCorreio.setBairro(linhaCadastroOficioMEI.getBairro());
        enderecoCorreio.setNumero(String.valueOf(linhaCadastroOficioMEI.getNumero()));
        enderecoCorreio.setComplemento(linhaCadastroOficioMEI.getComplemento());
        enderecoCorreio.setUf(linhaCadastroOficioMEI.getUF());
        enderecoCorreio.setLocalidade("RIO BRANCO");
        enderecoCorreio.setCep(linhaCadastroOficioMEI.getCep());
        enderecoCorreio.setPrincipal(true);
        return enderecoCorreio;
    }

    public void salvarPessoaJuridica(LinhaCadastroOficioMEI linhaCadastroOficioMEI, AssistenteBarraProgresso assistenteBarraProgresso){
        if(!Strings.isNullOrEmpty(linhaCadastroOficioMEI.getCnpj())) {
            verificarMEI = linhaCadastroOficioMEI.isMEI();
            pessoaJuridica = new PessoaJuridica();
            Telefone telefone = new Telefone();
            HistoricoSituacaoPessoa historicoSituacaoPessoa = new HistoricoSituacaoPessoa();

            naturezaJuridica = naturezaJuridicaFacade.buscarNaturezaPorCodigo(Integer.parseInt(linhaCadastroOficioMEI.getNaturezaJuridica().replace(" ","")));
            if(!pessoaJuridicaFacade.hasCnpj(linhaCadastroOficioMEI.getCnpj())) {
                if (verificarMEI) {
                    pessoaJuridica.setRazaoSocial(linhaCadastroOficioMEI.getRazaoSocial());
                    pessoaJuridica.setNomeFantasia(linhaCadastroOficioMEI.getNomeFantasia());
                    pessoaJuridica.setCnpj(linhaCadastroOficioMEI.getCnpj());
                    if (!Strings.isNullOrEmpty(linhaCadastroOficioMEI.getNire().replace(" ", ""))) {
                        pessoaJuridica.setNire(linhaCadastroOficioMEI.getNire());
                    }
                    pessoaJuridica.setNaturezaJuridica(naturezaJuridica);
                    pessoaJuridica.setEmail(linhaCadastroOficioMEI.getEmail());
                    pessoaJuridica.setCapitalSocial(linhaCadastroOficioMEI.getCapitalSocial());
                    telefone.setTelefone(linhaCadastroOficioMEI.getTelefone());
                    telefone.setPrincipal(true);
                    pessoaJuridica.setTelefonePrincipal(telefone);
                    pessoaJuridica.getTelefones().add(telefone);
                    pessoaJuridica.setSituacaoCadastralPessoa(SituacaoCadastralPessoa.ATIVO);
                    historicoSituacaoPessoa.setSituacaoCadastralPessoa(SituacaoCadastralPessoa.ATIVO);
                    historicoSituacaoPessoa.setDataSituacao(SistemaFacade.getDataCorrente());
                    pessoaJuridica.getHistoricoSituacoesPessoa().add(historicoSituacaoPessoa);
                    pessoaJuridica.setEnderecoPrincipal(retornaEnderecoPJ(linhaCadastroOficioMEI));
                    pessoaJuridica.getEnderecos().add(retornaEnderecoPJ(linhaCadastroOficioMEI));
                    pessoaJuridica = pessoaJuridicaFacade.salvarRetornando(pessoaJuridica);
                    assistenteBarraProgresso.adicionarLogHtml("Linha " + linhaCadastroOficioMEI.getNumeroLinha() + " - CNPJ Base " + linhaCadastroOficioMEI.getCnpj() + " Nova Pessoa Juridica cadastrada com sucesso!");
                } else {
                    assistenteBarraProgresso.adicionarLogHtml("Linha " + linhaCadastroOficioMEI.getNumeroLinha() + " - CNPJ Base " + linhaCadastroOficioMEI.getCnpj() + " O Cadastro da pessoa juridica não será processado pois não é MEI");
                }
            } else {
                pessoaJuridica = pessoaJuridicaFacade.recuperaPessoaJuridicaPorCNPJ(linhaCadastroOficioMEI.getCnpj());
                assistenteBarraProgresso.adicionarLogHtml("Linha " + linhaCadastroOficioMEI.getNumeroLinha() + " - CNPJ Base " + linhaCadastroOficioMEI.getCnpj() + " Pessoa Juridica ja possuí cadastro!");
            }
        } else {
            assistenteBarraProgresso.adicionarLogHtml("Linha " + linhaCadastroOficioMEI.getNumeroLinha() + linhaCadastroOficioMEI.getErroComCNPJ());
        }

    }

    public void salvarCadastroEconomico(LinhaCadastroOficioMEI linhaCadastroOficioMEI, AssistenteBarraProgresso assistenteBarraProgresso ){
        if(!Strings.isNullOrEmpty(linhaCadastroOficioMEI.getCnpj())) {
            CadastroEconomico cadastroEconomico = new CadastroEconomico();
            if (linhaCadastroOficioMEI.getCadastroEconomico() == null) {
                if (verificarMEI) {
                    //cadastrar
                    cadastroEconomico.setPessoa(pessoaJuridica);
                    cadastroEconomico.setNaturezaJuridica(naturezaJuridica);
                    SituacaoCadastroEconomico situacaoCadastroEconomico = new SituacaoCadastroEconomico();
                    boolean isServico = false;
                    situacaoCadastroEconomico.setSituacaoCadastral(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR);
                    situacaoCadastroEconomico.setDataRegistro(SistemaFacade.getDataCorrente());
                    situacaoCadastroEconomico.setDataAlteracao(SistemaFacade.getDataCorrente());
                    cadastroEconomico.getSituacaoCadastroEconomico().add(situacaoCadastroEconomico);
                    cadastroEconomico.setDataCadastro(SistemaFacade.getDataCorrente());
                    cadastroEconomicoFacade.gerarInscricaoCadastral(cadastroEconomico);
                    if (linhaCadastroOficioMEI.getCNAEPrimaria() != null) {
                        pessoaCNAE = new PessoaCNAE();
                        pessoaCNAE.setPessoa(pessoaJuridica);
                        EconomicoCNAE economicoCNAE = new EconomicoCNAE();
                        pessoaCNAE.setCnae(linhaCadastroOficioMEI.getCNAEPrimaria());
                        economicoCNAE.setCnae(pessoaCNAE.getCnae());
                        economicoCNAE.setCadastroEconomico(cadastroEconomico);
                        economicoCNAE.setTipo(EconomicoCNAE.TipoCnae.Primaria);
                        cadastroEconomico.getEconomicoCNAE().add(economicoCNAE);
                        Util.adicionarObjetoEmLista(pessoaJuridica.getPessoaCNAE(), pessoaCNAE);
                        if (!cnaeFacade.recuperar(linhaCadastroOficioMEI.getCNAEPrimaria().getId()).getServicos().isEmpty()) {
                            isServico = true;
                        }
                    }
                    if (linhaCadastroOficioMEI.getCnaesSecundarias() != null) {
                        for (CNAE cnae : linhaCadastroOficioMEI.getCnaesSecundarias()) {
                            if (cnae != null) {
                                pessoaCNAE = new PessoaCNAE();
                                pessoaCNAE.setPessoa(pessoaJuridica);
                                EconomicoCNAE economicoCNAE = new EconomicoCNAE();
                                pessoaCNAE.setCnae(cnae);
                                economicoCNAE.setCnae(pessoaCNAE.getCnae());
                                economicoCNAE.setCadastroEconomico(cadastroEconomico);
                                economicoCNAE.setTipo(EconomicoCNAE.TipoCnae.Secundaria);
                                cadastroEconomico.getEconomicoCNAE().add(economicoCNAE);
                                Util.adicionarObjetoEmLista(pessoaJuridica.getPessoaCNAE(), pessoaCNAE);
                                if (!cnaeFacade.recuperar(cnae.getId()).getServicos().isEmpty()) {
                                    isServico = true;
                                }
                            }
                        }

                    }
                    if (isServico) {
                        cadastroEconomico.setClassificacaoAtividade(ClassificacaoAtividade.PRESTACAO_DE_SERVICO);
                    } else {
                        cadastroEconomico.setClassificacaoAtividade(ClassificacaoAtividade.COMERCIO);
                    }
                    cadastroEconomico.getEnquadramentos().add(getEnquadramentoFiscal(cadastroEconomico, isServico));
                    cadastroEconomicoFacade.salvarOficioMEI(cadastroEconomico);
                    assistenteBarraProgresso.adicionarLogHtml("Linha " + linhaCadastroOficioMEI.getNumeroLinha() + " - CNPJ Base " + linhaCadastroOficioMEI.getCnpj() + " Novo Cadastro econômico cadastrado com sucesso!");
                } else {
                    assistenteBarraProgresso.adicionarLogHtml("Linha " + linhaCadastroOficioMEI.getNumeroLinha() + " - CNPJ Base " + linhaCadastroOficioMEI.getCnpj() + " O Cadastro econômico não será processado pois não é MEI");
                }
            } else {
                //atualizar
                linhaCadastroOficioMEI.setCadastroEconomico(cadastroEconomicoFacade.recuperar(linhaCadastroOficioMEI.getCadastroEconomico().getId()));
                linhaCadastroOficioMEI.getCadastroEconomico().setPessoa(pessoaJuridica);
                linhaCadastroOficioMEI.getCadastroEconomico().setNaturezaJuridica(naturezaJuridica);
                if (verificarMEI && !TipoIssqn.MEI.equals(!linhaCadastroOficioMEI.getCadastroEconomico().getEnquadramentos().isEmpty() ? linhaCadastroOficioMEI.getCadastroEconomico().getEnquadramentos().get(0).getTipoIssqn() : null)) {
                    linhaCadastroOficioMEI.getCadastroEconomico().getEnquadramentos().get(0).setTipoIssqn(TipoIssqn.MEI);
                }
                cadastroEconomicoFacade.salvarOficioMEI(linhaCadastroOficioMEI.getCadastroEconomico());
                assistenteBarraProgresso.adicionarLogHtml("Linha " + linhaCadastroOficioMEI.getNumeroLinha() + " - CNPJ Base " + linhaCadastroOficioMEI.getCnpj() + " Cadastro econômico já possui cadastro!");
            }
        } else {
            assistenteBarraProgresso.adicionarLogHtml("Linha " + linhaCadastroOficioMEI.getNumeroLinha() + linhaCadastroOficioMEI.getErroComCNPJ());
        }
    }

    public EnquadramentoFiscal getEnquadramentoFiscal(CadastroEconomico cadastroEconomico, boolean isServico){
        EnquadramentoFiscal enquadramentoFiscal = new EnquadramentoFiscal();
        enquadramentoFiscal.setTipoIssqn(TipoIssqn.MEI);
        enquadramentoFiscal.setInicioVigencia(SistemaFacade.getDataCorrente());
        enquadramentoFiscal.setPorte(TipoPorte.MICRO);
        enquadramentoFiscal.setTipoContribuinte(TipoContribuinte.PERMANENTE);
        enquadramentoFiscal.setRegimeTributario(RegimeTributario.SIMPLES_NACIONAL);
        enquadramentoFiscal.setRegimeEspecialTributacao(RegimeEspecialTributacao.PADRAO);
        enquadramentoFiscal.setSubstitutoTributario(false);

        if(isServico) {
            enquadramentoFiscal.setTipoNotaFiscalServico(TipoNotaFiscalServico.ELETRONICA);
        } else {
            enquadramentoFiscal.setTipoNotaFiscalServico(null);
        }

        enquadramentoFiscal.setCadastroEconomico(cadastroEconomico);
        return enquadramentoFiscal;
    }

    private void preencheLinhaCadastroPJ(String linha) throws Exception {
        linhaCadastroOficioMEI.setUF(linha.substring(2423,2425));
        linhaCadastroOficioMEI.setCodigoMunicipio(linha.substring(2419,2423));
        linhaCadastroOficioMEI.setTelefone(linha.substring(3867,3879));
        linhaCadastroOficioMEI.setEmail(linha.substring(2677,2792));
        linhaCadastroOficioMEI.setLogradouro(linha.substring(2091,2157));
        linhaCadastroOficioMEI.setNumero(getInteger(linha.substring(2157,2163).replace(" ","")));
        linhaCadastroOficioMEI.setComplemento(linha.substring(2163,2319));
        linhaCadastroOficioMEI.setBairro(linha.substring(2319,2369));
        linhaCadastroOficioMEI.setCep(linha.substring(2425,2433));
        linhaCadastroOficioMEI.setRazaoSocial(linha.substring(1341,1491));
        linhaCadastroOficioMEI.setNomeFantasia(linha.substring(1794,1849));
        linhaCadastroOficioMEI.setCnpj(linha.substring(30, 44));
        linhaCadastroOficioMEI.setNire(linha.substring(1854,1865));
        linhaCadastroOficioMEI.setNaturezaJuridica(linha.substring(1849,1853));
        linhaCadastroOficioMEI.setCapitalSocial(new BigDecimal(linha.substring(1915,1929).replace(" ","").isEmpty() ?
            "0" : linha.substring(1915,1929).replace(" ","")));
        linhaCadastroOficioMEI.setMEI("S".equals(linha.substring(7094,7095)));
        verificarMEI = linhaCadastroOficioMEI.isMEI();
    }

    private void preencheLinhaOficioCadastroEconomico( String linha)  throws  Exception{
        linhaCadastroOficioMEI.setCNAES(linha.substring(1459,2153));
        linhaCadastroOficioMEI.setCNAEPrimaria(cnaeFacade.buscarCnaePorCodigo(linha.substring(1420,1427)));
        linhaCadastroOficioMEI.setCnaesSecundarias(Lists.<CNAE>newArrayList());
        for (int index = 0; index <= linhaCadastroOficioMEI.getCNAES().replace(" ","").length(); index=index+7) {
            linhaCadastroOficioMEI.getCnaesSecundarias().add(cnaeFacade.buscarCnaePorCodigo(linhaCadastroOficioMEI.getCNAES().substring(index, index + 7))
                != null ? cnaeFacade.buscarCnaePorCodigo(linhaCadastroOficioMEI.getCNAES().substring(index, index + 7)) : null);
        }
        linhaCadastroOficioMEI.setCnpj(linha.substring(30, 44));
        linhaCadastroOficioMEI.setNaturezaJuridica(linha.substring(1849,1853));
        linhaCadastroOficioMEI.setCadastroEconomico(cadastroEconomicoFacade.buscarCadastroEconomicoCNPJ(linhaCadastroOficioMEI.getCnpj()));

    }

    public LinhaCadastroOficioMEI preencherLinhaCadastroOficioMEI(String linha, String identificadorLinhaAnterior) throws Exception {
        linhaCadastroOficioMEI.setCodigoRegistro(linha.substring(0,2));
        linhaCadastroOficioMEI.setUF(linha.substring(2423,2425));
        linhaCadastroOficioMEI.setCodigoMunicipio(linha.substring(2419,2423));
        if ("01".equals(linhaCadastroOficioMEI.getCodigoRegistro()) && "AC".equals(linhaCadastroOficioMEI.getUF()) && "0139".equals(linhaCadastroOficioMEI.getCodigoMunicipio())) {
            preencheLinhaCadastroPJ(linha);
            verificarUFECodigoMunicipio = true;
        } else if ("01".equals(linhaCadastroOficioMEI.getCodigoRegistro()) && "01".equals(identificadorLinhaAnterior) && verificarUFECodigoMunicipio ) {
            linhaCadastroOficioMEI.setErroComCNPJ(" - CNPJ Base " + linha.substring(30, 44) + " Esse CNPJ já foi processado em uma linha anterior");
            verificarUFECodigoMunicipio = true;
        } else if ("04".equals(linhaCadastroOficioMEI.getCodigoRegistro()) && verificarUFECodigoMunicipio && !Strings.isNullOrEmpty(linha.substring(30, 44))) {
            preencheLinhaOficioCadastroEconomico(linha);
            verificarUFECodigoMunicipio = false;
        } else if (Strings.isNullOrEmpty(linha.substring(30, 44))) {
            linhaCadastroOficioMEI.setErroComCNPJ(" - CNPJ Base " + linha.substring(30, 44) + " O CNPJ está vazio ou nulo, impossivel efetuar cadastro!");
            verificarUFECodigoMunicipio = false;
        } else if ("01".equals(linhaCadastroOficioMEI.getCodigoRegistro()) && !verificarUFECodigoMunicipio && "    ".equals(linhaCadastroOficioMEI.getCodigoMunicipio())) {
            linhaCadastroOficioMEI.setErroComCNPJ(" - CNPJ Base " + linha.substring(30, 44) + " Essa linha não pode ser verificada pois está com o código de municipio em branco");
            verificarUFECodigoMunicipio = false;
        } else if ("01".equals(linhaCadastroOficioMEI.getCodigoRegistro()) && !"0139".equals(linhaCadastroOficioMEI.getCodigoMunicipio())){
            linhaCadastroOficioMEI.setErroComCNPJ(" - CNPJ Base " + linha.substring(30, 44) + " Esse CNPJ não pertence ao município de Rio Branco - AC. Código do municipio: "+linhaCadastroOficioMEI.getCodigoMunicipio()+" - " + linhaCadastroOficioMEI.getUF());
            verificarUFECodigoMunicipio = false;
        } else {
            linhaCadastroOficioMEI.setErroComCNPJ(" - CNPJ Base " + linha.substring(30, 44) + " Esse CNPJ já foi processado em uma linha anterior");
            verificarUFECodigoMunicipio = false;
        }
        return linhaCadastroOficioMEI;
    }

    public Integer getInteger(String value) {
        if (value != null && !value.trim().isEmpty()) {
            return new Integer(value);
        }
        return null;
    }

    public Date getDataFormadata(String data, String pattern) {
        if (data != null && !data.trim().isEmpty()) {
            return DataUtil.getDateParse(data, pattern);
        }
        return null;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
