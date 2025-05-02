package br.com.webpublico.negocios.rh.rotinasanuaismensais;

import br.com.webpublico.controle.rh.rotinasanuaisemensais.ImportacaoExpotacaoPlanilhaContaCorrenteCaixaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.rh.ExportacaoPlanilhaContaCorrenteCaixa;
import br.com.webpublico.entidadesauxiliares.rh.ImportacaoPlanilhaContaCorrenteCaixa;
import br.com.webpublico.enums.ModalidadeConta;
import br.com.webpublico.enums.PerfilEnum;
import br.com.webpublico.enums.SituacaoConta;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ExcelUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author peixe on 31/03/2017  14:15.
 */
@Stateless
public class ImportacaoExportacaoPlanilhaContaCorrenteCaixaFacade implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(ImportacaoExportacaoPlanilhaContaCorrenteCaixaFacade.class);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ContaCorrenteBancariaFacade contaCorrenteBancariaFacade;
    @EJB
    private ContaCorrenteBancPessoaFacade contaCorrenteBancPessoaFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private AgenciaFacade agenciaFacade;
    @EJB
    private BancoFacade bancoFacade;


    public void importarPessoas(List<ImportacaoPlanilhaContaCorrenteCaixa> pessoasImportadas, InputStream inputstream) {
        try {
            Workbook workbook = WorkbookFactory.create(inputstream);
            Sheet sheet = workbook.getSheetAt(0);
            int rowsCount = sheet.getLastRowNum();
            for (int i = 0; i <= rowsCount; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                percorrerCelulas(row, pessoasImportadas);
            }
        } catch (IOException e) {
            logger.error("Erro", e);
        } catch (InvalidFormatException e) {
            logger.error("Erro", e);
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public void importarPessoasPessoas(List<ImportacaoPlanilhaContaCorrenteCaixa> pessoasImportadas, InputStream inputstream) {
        Banco bancoBB = bancoFacade.retornaBancoPorNumero("001");
        try {
            Workbook workbook = WorkbookFactory.create(inputstream);
            Sheet sheet = workbook.getSheetAt(0);
            int rowsCount = sheet.getLastRowNum();
            for (int i = 0; i <= rowsCount; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                percorrerCelulasBuscandoPessoas(row, pessoasImportadas, bancoBB);
            }
        } catch (IOException e) {
            logger.error("Erro", e);
        } catch (InvalidFormatException e) {
            logger.error("Erro", e);
        }
    }

    private void percorrerCelulasBuscandoPessoas(Row row, List<ImportacaoPlanilhaContaCorrenteCaixa> pessoasImportadas, Banco bancoBB) {
        String ordem = getCell(row, 0);
        String nome = getCell(row, 1);
        String cpf = getCell(row, 2);
        cpf = getValorCellParaCpf(cpf);
        String ag = getCell(row, 3);
        String conta = getCell(row, 4);
        String modalidade = getCell(row, 5);
        ModalidadeConta modalidadeConta = traduzirModalidade(modalidade);

        ImportacaoPlanilhaContaCorrenteCaixa item = new ImportacaoPlanilhaContaCorrenteCaixa();

        item.setCpf(Util.formatarCpf(cpf));
        item.setNome(nome);
        item.setAgencia(ag);
        String[] numeroConta = conta.split("-");
        if (numeroConta.length > 1) {
            String digito = StringUtil.removeCaracteresEspeciais(numeroConta[1]).trim();
            item.setConta(numeroConta[0] + "-" + (digito.length() > 0 ? digito.substring(digito.length() - 1) : digito));
        } else {
            item.setConta(conta);
        }
        item.setModalidadeConta(modalidadeConta);

        Long idPessoaFisica = pessoaFisicaFacade.buscarIdDePessoaPorCpf(cpf);
        if (idPessoaFisica != null) {
            PessoaFisica pf = (PessoaFisica) pessoaFisicaFacade.recuperar(PessoaFisica.class, idPessoaFisica);
            Hibernate.initialize(pf.getContaCorrenteBancPessoas());
            item.setPessoaFisica(pf);
            List<VinculoFP> vinculoFPS = vinculoFPFacade.buscarVinculosVigentesPorPessoa(pf, sistemaFacade.getDataOperacao());
            item.setVinculos(vinculoFPS);
            if (ag != null) {
                String[] numeroAgencia = ag.split("-");
                String numeroAg = numeroAgencia[0];

                if (numeroAg != null) {
                    String numeroAgenciaConvertido = "";
                    try {
                        numeroAgenciaConvertido = Integer.valueOf(numeroAg).toString();
                    } catch (Exception e) {
                        logger.error("não foi possivel converter numero da agência");
                    }
                    Agencia agencia = agenciaFacade.buscarAgenciaPeloNumeroDaAgenciaAndBanco(numeroAgenciaConvertido, bancoBB.getNumeroBanco());
                    if (agencia != null) {
                        item.setAgenciaObjeto(agencia);
                        item.setBanco(bancoBB);
                    }
                }
            }
        }
        pessoasImportadas.add(item);
    }

    private String getCell(Row row, int i) {
        Cell cell = row.getCell(i);
        if (cell != null) {
            return ExcelUtil.getValorCellParaCpf(cell);
        }
        return "";
    }


    private ModalidadeConta traduzirModalidade(String modalidade) {
        switch (modalidade) {
            case "Conta Corrente Comum":
                return ModalidadeConta.CONTA_CORRENTE;
            case "Conta Registro Salários":
                return ModalidadeConta.CONTA_SALARIO;
            case "Conta Poupança":
                return ModalidadeConta.CONTA_POUPANCA;
            case "Conta Corrente Especial":
                return ModalidadeConta.CONTA_CORRENTE;
            case "Conta Corrente Estilo":
                return ModalidadeConta.CONTA_CORRENTE;
            case "Conta Corrente Universitária":
                return ModalidadeConta.CONTA_CORRENTE;
            default:
                return ModalidadeConta.CONTA_CORRENTE;
        }
    }

    public static String getValorCellParaCpf(String cell) {
        if (cell == null) {
            return "";
        }
        cell = cell.length() < 11 ? Util.zerosAEsquerda(cell, 11) : cell;
        return cell;
    }

    private void percorrerCelulas(Row row, List<ImportacaoPlanilhaContaCorrenteCaixa> pessoasImportadas) {
        ImportacaoPlanilhaContaCorrenteCaixa item = new ImportacaoPlanilhaContaCorrenteCaixa();
        for (ImportacaoExpotacaoPlanilhaContaCorrenteCaixaControlador.CampoImportacao campoImportacao : ImportacaoExpotacaoPlanilhaContaCorrenteCaixaControlador.CampoImportacao.values()) {
            Cell cell = row.getCell(campoImportacao.ordinal());
            String valorCelula = ExcelUtil.getValorCellParaCpf(cell);
            switch (campoImportacao) {
                case CPF:
                    item.setCpf(Util.formatarCpf(valorCelula));
                    Long idPessoaFisica = pessoaFisicaFacade.buscarIdDePessoaPorCpf(valorCelula);
                    if (idPessoaFisica != null) {
                        PessoaFisica pf = pessoaFisicaFacade.recuperar(idPessoaFisica);
                        item.setPessoaFisica(pf);
                    } else {
                        continue;
                    }
                    break;
                case NOME:
                    item.setNome(valorCelula);
                    break;
                default:
                    break;
            }
        }
        pessoasImportadas.add(item);
    }

    public List<ExportacaoPlanilhaContaCorrenteCaixa> preencherDadosExportacao(List<ImportacaoPlanilhaContaCorrenteCaixa> pessoasImportadas) {
        List<ExportacaoPlanilhaContaCorrenteCaixa> retorno = Lists.newLinkedList();
        for (ImportacaoPlanilhaContaCorrenteCaixa pessoaImportada : pessoasImportadas) {
            if (pessoaImportada.getPessoaFisica() != null) {
                ExportacaoPlanilhaContaCorrenteCaixa exportacao = new ExportacaoPlanilhaContaCorrenteCaixa();
                preencherDadosBasicosPessoa(pessoaImportada.getPessoaFisica(), exportacao);
                preencherDadosNascimento(pessoaImportada.getPessoaFisica(), exportacao);
                preencherDadosCarteiraTrabalho(pessoaImportada.getPessoaFisica(), exportacao);
                preencherDadosRg(pessoaImportada.getPessoaFisica(), exportacao);
                preencherDadosEndereco(pessoaImportada.getPessoaFisica(), exportacao);
                preencherDadosTelefone(pessoaImportada.getPessoaFisica(), exportacao);

                preencherDadosAdmissao(pessoaImportada.getPessoaFisica(), exportacao);
                retorno.add(exportacao);
            }
        }
        adicionarPerfilRH(pessoasImportadas);
        return retorno;
    }

    public void adicionarPerfilRH(List<ImportacaoPlanilhaContaCorrenteCaixa> pessoasImportadas) {
        for (ImportacaoPlanilhaContaCorrenteCaixa pessoaImportada : pessoasImportadas) {
            if (pessoaImportada.getPessoaFisica() != null) {
                if (!pessoaFisicaFacade.isPessoaNoPerfil(pessoaImportada.getPessoaFisica(), PerfilEnum.PERFIL_RH)) {
                    pessoaImportada.getPessoaFisica().getPerfis().add(PerfilEnum.PERFIL_RH);
                    pessoaFisicaFacade.salvar(pessoaImportada.getPessoaFisica());
                }
            }
        }
    }

    private ExportacaoPlanilhaContaCorrenteCaixa preencherDadosAdmissao(PessoaFisica pessoaFisica, ExportacaoPlanilhaContaCorrenteCaixa exportacao) {
        Date inicioVigencia = vinculoFPFacade.buscarDataInicioVigenciaUltimoVinculoPorPessoa(pessoaFisica, sistemaFacade.getDataOperacao());
        if (inicioVigencia != null) {
            exportacao.setDataAdmissao(inicioVigencia);
        }
        return exportacao;
    }


    private ExportacaoPlanilhaContaCorrenteCaixa preencherDadosCarteiraTrabalho(PessoaFisica pessoaFisica, ExportacaoPlanilhaContaCorrenteCaixa exportacao) {
        CarteiraTrabalho carteira = pessoaFisica.getCarteiraDeTrabalho();
        if (carteira != null) {
            exportacao.setPis(carteira.getPisPasep());
        }
        return exportacao;
    }

    private ExportacaoPlanilhaContaCorrenteCaixa preencherDadosConjuge(PessoaFisica pessoaFisica, ExportacaoPlanilhaContaCorrenteCaixa exportacao) {
        CertidaoCasamento certidao = pessoaFisica.getCertidaoCasamento();
        if (certidao != null) {
            exportacao.setNomeConjuge(certidao.getNomeConjuge());
        }
        return exportacao;
    }

    private ExportacaoPlanilhaContaCorrenteCaixa preencherDadosEndereco(PessoaFisica pessoaFisica, ExportacaoPlanilhaContaCorrenteCaixa exportacao) {
        EnderecoCorreio endereco = getEnderecoPessoa(pessoaFisica);
        if (endereco != null) {
            exportacao.setEnderecoCorreio(endereco);
        }
        return exportacao;
    }

    private ExportacaoPlanilhaContaCorrenteCaixa preencherDadosTelefone(PessoaFisica pessoaFisica, ExportacaoPlanilhaContaCorrenteCaixa exportacao) {
        Telefone telefone = pessoaFisica.getTelefoneValido();
        if (telefone != null) {
            exportacao.setTelefone(telefone);
        }
        return exportacao;
    }

    private EnderecoCorreio getEnderecoPessoa(PessoaFisica pessoaFisica) {
        if (pessoaFisica.getEnderecoPrincipal() != null) {
            return pessoaFisica.getEnderecoPrincipal();
        }
        if (pessoaFisica.getEnderecoCorrespondencia() != null) {
            return pessoaFisica.getEnderecoCorrespondencia();
        }
        if (pessoaFisica.getEnderecos() != null && !pessoaFisica.getEnderecos().isEmpty()) {
            return pessoaFisica.getEnderecos().get(0);
        }
        return null;
    }

    private ExportacaoPlanilhaContaCorrenteCaixa preencherDadosRg(PessoaFisica pessoaFisica, ExportacaoPlanilhaContaCorrenteCaixa exportacao) {
        RG rg = pessoaFisica.getRg();
        if (rg != null) {
            exportacao.setRg(rg);
        }
        return exportacao;
    }

    private ExportacaoPlanilhaContaCorrenteCaixa preencherDadosNascimento(PessoaFisica pessoaFisica, ExportacaoPlanilhaContaCorrenteCaixa exportacao) {
        CertidaoNascimento nascimento = pessoaFisica.getCertidaoNascimento();
        exportacao.setLocalNascimento(buscarLocalNascimento(nascimento, pessoaFisica));
        exportacao.setUfNascimento(buscarUfNascimento(nascimento, pessoaFisica));
        return exportacao;
    }

    private String buscarUfNascimento(CertidaoNascimento nascimento, PessoaFisica pessoaFisica) {
        if (pessoaFisica.getNaturalidade() != null && pessoaFisica.getNaturalidade().getUf() != null) {
            return pessoaFisica.getNaturalidade().getUf().getUf();
        }
        if (nascimento != null && nascimento.getCidade() != null && nascimento.getCidade().getUf() != null) {
            return nascimento.getCidade().getUf().getUf();
        }
        return "";
    }

    private String buscarLocalNascimento(CertidaoNascimento nascimento, PessoaFisica pessoaFisica) {
        if (pessoaFisica.getNaturalidade() != null) {
            return pessoaFisica.getNaturalidade().getNome();
        }
        if (nascimento != null && nascimento.getCidade() != null) {
            return nascimento.getCidade().getNome();
        }
        return "";
    }

    private ExportacaoPlanilhaContaCorrenteCaixa preencherDadosBasicosPessoa(PessoaFisica pessoaImportada, ExportacaoPlanilhaContaCorrenteCaixa exportacao) {

        exportacao.setNomeCompleto(pessoaImportada.getNome());
        exportacao.setNomeReduzido(pessoaImportada.getNomeAbreviado());
        exportacao.setNascimento(pessoaImportada.getDataNascimento());
        exportacao.setCpf(pessoaImportada.getCpf());
        exportacao.setNomeMae(pessoaImportada.getMae());
        exportacao.setNomePai("");
        exportacao.setSexo(pessoaImportada.getSexo());
        exportacao.setEstadoCivil(pessoaImportada.getEstadoCivil());
        exportacao.setGrauInstrucao(pessoaImportada.getNivelEscolaridade() != null ? pessoaImportada.getNivelEscolaridade().getGrauInstrucaoCAGED() : null);

        return exportacao;
    }

    public void criarContas(List<ImportacaoPlanilhaContaCorrenteCaixa> pessoasImportadas) {
        for (ImportacaoPlanilhaContaCorrenteCaixa pessoasImportada : pessoasImportadas) {
            if (!isRegistroInvalido(pessoasImportada)) {
                ContaCorrenteBancaria contaCorrenteBancaria = criarContaBancaria(pessoasImportada);
                ContaCorrenteBancPessoa cc = new ContaCorrenteBancPessoa();
                cc.setContaCorrenteBancaria(contaCorrenteBancaria);
                cc.setPessoa(pessoasImportada.getPessoaFisica());
                cc.setObservacao("Conta importada via rotina do sistema");
                cc.setPrincipal(false);
                cc.setJustificativa("IMPORTADO AUTOMATICAMENTE");

                if (!contaCorrenteBancariaFacade.jaPossuiContaCorrente(contaCorrenteBancaria.getNumeroConta(), contaCorrenteBancaria.getAgencia().getNumeroAgencia(), cc.getPessoa().getId())) {
                    ContaCorrenteBancaria contaSalva = contaCorrenteBancariaFacade.salvarContaCorrente(contaCorrenteBancaria);
                    cc.setContaCorrenteBancaria(contaSalva);
                    contaCorrenteBancPessoaFacade.salvarNovo(cc);
                    pessoasImportada.setContaCorrenteBancaria(contaSalva);
                }
            }
        }

    }

    private ContaCorrenteBancaria criarContaBancaria(ImportacaoPlanilhaContaCorrenteCaixa pessoasImportada) {
        ContaCorrenteBancaria contaBancaria = new ContaCorrenteBancaria();

        contaBancaria.setModalidadeConta(pessoasImportada.getModalidadeConta());
        contaBancaria.setAgencia(pessoasImportada.getAgenciaObjeto());
        contaBancaria.setBanco(pessoasImportada.getBanco());
        contaBancaria.setSituacao(SituacaoConta.ATIVO);

        String[] numeroConta = pessoasImportada.getConta().split("-");
        String numeroContaConvertido = "";
        String s = numeroConta[0];
        try {
            numeroContaConvertido = Integer.valueOf(s).toString();
        } catch (Exception e) {
        }
        contaBancaria.setNumeroConta(numeroContaConvertido);
        contaBancaria.setDigitoVerificador(numeroConta[1]);
        return contaBancaria;
    }

    public boolean isRegistroInvalido(ImportacaoPlanilhaContaCorrenteCaixa pessoasImportada) {
        if (pessoasImportada.getPessoaFisica() == null) {
            return true;
        }
        if (pessoasImportada.getAgenciaObjeto() == null) {
            return true;
        }
        if (pessoasImportada.getConta() == null) {
            return true;
        }
        if (pessoasImportada.getVinculos().isEmpty()) {
            return true;
        }

        String[] numeroConta = pessoasImportada.getConta().split("-");
        String s = numeroConta[0];
        try {
            if (Integer.parseInt(s) == 0) {
                return true;
            }
            if (numeroConta.length == 1) {
                return true;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return false;
    }

}
