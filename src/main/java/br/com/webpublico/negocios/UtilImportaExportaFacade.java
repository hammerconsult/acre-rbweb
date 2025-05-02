/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoFP;
import br.com.webpublico.entidadesauxiliares.rh.ResumoFichaFinanceira;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.joda.time.DateTime;
import com.google.common.collect.Lists;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author andre
 */
@Stateless
public class UtilImportaExportaFacade extends AbstractFacade<ExportaArquivoMargem> {

    private static boolean MOSTRAR_BOOLEAN = true;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private MiddleRHFacade middleRHFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContratoVinculoDeContratoFacade contratoVinculoDeContratoFacade;
    @EJB
    private CargoConfiancaFacade cargoConfiancaFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private FaltasFacade faltasFacade;

    public UtilImportaExportaFacade() {
        super(ExportaArquivoMargem.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public void setContratoFPFacade(ContratoFPFacade contratoFPFacade) {
        this.contratoFPFacade = contratoFPFacade;
    }

    public FolhaDePagamentoFacade getFolhaDePagamentoFacade() {
        return folhaDePagamentoFacade;
    }

    public void setFolhaDePagamentoFacade(FolhaDePagamentoFacade folhaDePagamentoFacade) {
        this.folhaDePagamentoFacade = folhaDePagamentoFacade;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @Asynchronous
    public void montarArquivoMargem(List<VinculoFP> listaContratoFP, Exercicio exercicio, Integer mesSelecionado, Integer anoSelecionado, ExportaArquivoMargem exportaArquivoMargem, ConfiguracaoFP configuracaoFP) {
        int contador = 0;

        try {

            HierarquiaOrganizacional ho = new HierarquiaOrganizacional();
            BigDecimal valorLiquido;
            SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yy");
            DecimalFormat df = new DecimalFormat("#,##0.00");
            BigDecimal valorMargem;
            DateTime dt = new DateTime();
            dt = dt.withMonthOfYear(mesSelecionado);
            dt = dt.withYear(anoSelecionado);
            dt = dt.withDayOfMonth(1);
            Map<Long, HierarquiaOrganizacional> hierarquiaOrganizacionalCash = new LinkedHashMap<>();
            carregarHierarquias(hierarquiaOrganizacionalCash, dt.toDate());

            String txtAux = "";
            StringBuilder texto = new StringBuilder("");
            StringBuilder linha = new StringBuilder("");

            //Data de Envio
            texto.append(sf.format(new Date()));
            //Ano
            texto.append(StringUtils.leftPad(anoSelecionado.toString(), 2, "0"));
            //mês
            texto.append(StringUtils.leftPad(mesSelecionado.toString(), 2, "0"));
            // fim do cabeçalho
            texto.append(System.getProperty("line.separator"));

            for (VinculoFP vinculo : listaContratoFP) {
                long incio = System.currentTimeMillis();
                linha = new StringBuilder("");
                if (exportaArquivoMargem.isCancelar()) {
                    return;
                }
                exportaArquivoMargem.somaContador();
                //matricula
                String modalidadeCodigo = "00";
//                ContratoFP c;
                String cargo = "";
                if (vinculo instanceof ContratoFP) {
                    ContratoFP c = (ContratoFP) vinculo;
                    ContratoVinculoDeContrato vinculoDeContrato = contratoVinculoDeContratoFacade.recuperaContratoVinculoDeContratoVigente(c);
                    if (vinculoDeContrato == null) {
                        logger.debug("não encontrado vinculo para " + c);
                        continue;
                    } else {
                        modalidadeCodigo = vinculoDeContrato.getVinculoDeContratoFP().getCodigo() + "";
                    }
//                    EnquadramentoCC funcional = cargoConfiancaFacade.recuperaEnquadramentoCCVigente(c, dt.toDate());
                    if (c.getModalidadeContratoFP() != null) {
                        cargo = c.getModalidadeContratoFP().getDescricaoModalideParaEConsig();
                        CargoConfianca cargoConfianca = cargoConfiancaFacade.recuperaCargoConfiancaVigente(c);
                        if (cargoConfianca != null && c.getModalidadeContratoFP().getCodigo() == 1) {
                            cargo += "/COMISSÃO";
                        }
                    }
                }
                if (vinculo instanceof Aposentadoria) {
                    if (((Aposentadoria) vinculo).getContratoFP().getModalidadeContratoFP() != null) {
                        ContratoVinculoDeContrato vinculoDeContrato = contratoVinculoDeContratoFacade.recuperaContratoVinculoDeContratoVigente(((Aposentadoria) vinculo).getContratoFP());
                        if (vinculoDeContrato != null)
                            modalidadeCodigo = vinculoDeContrato.getVinculoDeContratoFP().getCodigo() + "";

                        cargo = ((Aposentadoria) vinculo).getContratoFP().getModalidadeContratoFP().getDescricaoModalideParaEConsig();
                    }
                }
                if (vinculo instanceof Pensionista) {
                    Pensionista p = ((Pensionista) vinculo);
                    if (p.getPensao().getContratoFP().getModalidadeContratoFP() != null) {
                        ContratoVinculoDeContrato vinculoDeContrato = contratoVinculoDeContratoFacade.recuperaContratoVinculoDeContratoVigente(p.getPensao().getContratoFP());
                        if (vinculoDeContrato != null)
                            modalidadeCodigo = vinculoDeContrato.getVinculoDeContratoFP().getCodigo() + "";

                        cargo = p.getPensao().getContratoFP().getModalidadeContratoFP().getDescricaoModalideParaEConsig();
                    }
                }
                txtAux = StringUtils.leftPad(vinculo.getMatriculaFP().getMatricula(), 18, "0");
                linha.append(txtAux);
                //numero do vinculo
                txtAux = StringUtils.leftPad(vinculo.getNumero(), 2, "0");
                linha.append(txtAux);
                //Órgão
                //Passagem de 2 dígitos do código da hierarquia
                //TODO pegar o orgaão da lotação.
                incio = System.currentTimeMillis();
//                if (vinculo instanceof ContratoFP) {
                LotacaoFuncional lf = lotacaoFuncionalFacade.recuperaLotacaoFuncionalVigentePorContratoFPMes(vinculo, dt.toDate());
                //System.out.println((System.currentTimeMillis() - incio) + "ms para achar lotacao " + lf);
                if (lf != null) {
                    incio = System.currentTimeMillis();
//                        ho = hierarquiaOrganizacionalFacade.recuperaHierarquiaOrganizacionalPelaUnidade(lf.getUnidadeOrganizacional().getId());
                    ho = hierarquiaOrganizacionalCash.get(lf.getUnidadeOrganizacional().getId());
                    if (ho == null)
                        ho = hierarquiaOrganizacionalFacade.recuperaHierarquiaOrganizacionalPelaUnidadeUltima(lf.getUnidadeOrganizacional().getId());
                    //System.out.println((System.currentTimeMillis() - incio) + "ms para achar hierarquia " + ho);
                    if (ho != null) {
                        String codigo = ho.getCodigo();
                        codigo = codigo.substring(3, 5);
                        linha.append(codigo);
                    } else linha.append("00");
                } else {
                    linha.append("00");
                }
//                    //System.out.println((System.currentTimeMillis() - incio) + "ms para achar lotacao e hierarquia " + ho);
//                } else {
//                    ho = hierarquiaOrganizacionalFacade.recuperaHierarquiaOrganizacionalPelaUnidade(vinculo.getUnidadeOrganizacional().getId());
//                    if (ho != null) {
//                        txtAux = ho.getCodigo();
//                        txtAux = txtAux.substring(txtAux.indexOf(".") + 1, txtAux.length() - 1);
//                        txtAux = txtAux.substring(txtAux.indexOf(".") - 2, txtAux.indexOf("."));
//                        linha.append(txtAux);
//                    }
//                }
                ResumoFichaFinanceira resumoMes = fichaFinanceiraFPFacade.buscarValorPorMesAndAnoAndTipoFolha(vinculo.getId(), Mes.getMesToInt(exportaArquivoMargem.getMesFinanceiro()), exportaArquivoMargem.getAnoFinanceiro(), TipoFolhaDePagamento.NORMAL, 1071, false);
                valorLiquido = resumoMes != null ? resumoMes.getMargem() : BigDecimal.ZERO;

                if (valorLiquido.compareTo(BigDecimal.ZERO) <= 0) {

                    //Margem Disponível
                    txtAux = StringUtils.leftPad("", 13, "0");
                    linha.append(txtAux.substring(0, 13));
                    //Margem Empréstimo
                    txtAux = StringUtils.leftPad("", 13, "0");
                    linha.append(txtAux.substring(0, 13));
                    logger.debug("valor zerado para " + vinculo + " - " + vinculo.getId());
                    continue;

                } else {
                    valorMargem = valorLiquido.multiply((configuracaoFP.getPercentualMargemDisponivel()).divide(new BigDecimal(100)));
                    txtAux = df.format(valorMargem);
                    int i = txtAux.indexOf(",");
                    txtAux = txtAux.substring(0, i) + txtAux.substring(i + 1);
                    txtAux = txtAux.replaceAll("\\.", "");

                    //Margem Disponível
//                    txtAux = txtAux.replaceAll(".", "");
                    txtAux = StringUtils.leftPad(txtAux, 13, "0");
                    linha.append(txtAux.substring(0, 13));
                    //Margem Empréstimo
                    //valorMargem = valorLiquido.multiply((configuracaoFP.getPercentualMargemEmprestimo()).divide(new BigDecimal(100)));
                    valorMargem = calcularMargemEuConsigoMais(exportaArquivoMargem.getMesFinanceiro(), exportaArquivoMargem.getAnoFinanceiro(), configuracaoFP, vinculo, valorLiquido);
                    txtAux = df.format(valorMargem);
                    i = txtAux.indexOf(",");
                    txtAux = txtAux.substring(0, i) + txtAux.substring(i + 1);
                    txtAux = txtAux.replaceAll("\\.", "");
                    txtAux = StringUtils.leftPad(txtAux, 13, "0");
                    linha.append(txtAux.substring(0, 13));
                }

                //Nome  - 40 posições
                txtAux = vinculo.getMatriculaFP().getPessoa().getNome();
                txtAux = StringUtils.rightPad(txtAux, 40, " ");
                txtAux = txtAux.substring(0, 40);
                linha.append(txtAux);
                String cpf = "";
                if (vinculo.getMatriculaFP().getPessoa() != null && vinculo.getMatriculaFP().getPessoa().getCpf() != null) {
                    cpf = vinculo.getMatriculaFP().getPessoa().getCpf();
                } else cpf = "0";


                //CPF - 11 posições
                txtAux = cpf.replaceAll("[^0-9]*", "");
                linha.append(StringUtils.leftPad(txtAux, 11, "0"));
                //Vínculo - 2 posições


                txtAux = StringUtils.leftPad(modalidadeCodigo, 2, "0");
                linha.append(txtAux);

                //Categoria - 30 posições

//                if (vinculo.getCargo() != null) {
//                    cargo = vinculo.getCargo().getDescricao();
//                }
                txtAux = cargo;
                txtAux = StringUtils.rightPad(txtAux, 30, " ");
                txtAux = txtAux.substring(0, 30);
                linha.append(txtAux);

                sf = new SimpleDateFormat("dd/MM/yy");
                //Data de Admissão - 8 posições
                txtAux = sf.format(vinculo.getInicioVigencia());
                linha.append(txtAux);

                //Prazo - 8 posições
                if (vinculo.getFinalVigencia() != null) {
                    txtAux = sf.format(vinculo.getFinalVigencia());
                } else {
                    txtAux = StringUtils.rightPad("", 8, " ");//txtAux = "00/00/00";
                }
                linha.append(txtAux);

                //Data de Nascimento - 8 posições
                Date dataNascimento = vinculo.getMatriculaFP().getPessoa().getDataNascimento();
                txtAux = dataNascimento != null ? (sf.format(dataNascimento)) : StringUtil.cortaDireita(8, " ");
                linha.append(txtAux);
                //fim do registro
                linha.append(System.getProperty("line.separator"));
                texto.append(linha);
                contador++;

                //System.out.println((System.currentTimeMillis() - incio) + "ms para processa linha " + vinculo);
            }
            //rodapé
            //Registros - 11 posições
            txtAux = StringUtils.leftPad(contador + "", 11, "0");
            texto.append(txtAux);
            exportaArquivoMargem.setLiberado(true);
            exportaArquivoMargem.setConteudo(texto.toString());
            middleRHFacade.salvarNovoTransactional(exportaArquivoMargem);
        } catch (Exception e) {
            logger.error("Erro ao gerar o arquivo ", e);
            throw e;
        }
    }

    private BigDecimal calcularMargemEuConsigoMais(Integer mes, Integer ano, ConfiguracaoFP configuracaoFP, VinculoFP vinculo, BigDecimal valorBaseConsignacao) {
        BaseFP baseEuConsigoMais = configuracaoFP.getBaseMargemEuConsigoMais();
        if (baseEuConsigoMais == null) {
            throw new ExcecaoNegocioGenerica("Nenhum configuração encontrada para a Base de Cáclculo do 'Eu Consigo Mais', vá nas configurações do RH e defina um base no campo: Base Para Cálculo Eu Consigo+");
        }

        //validar dias trabalhados do servidor minimo 7 dias
        List<FichaFinanceiraFP> fichaFinanceiraFPS = fichaFinanceiraFPFacade.recuperaListaFichaFinanceiraPorVinculoFPMesAnoTipoFolha(vinculo, mes, ano, TipoFolhaDePagamento.NORMAL);
        if (fichaFinanceiraFPS != null && !fichaFinanceiraFPS.isEmpty()) {
            for (FichaFinanceiraFP fichaFinanceiraFP : fichaFinanceiraFPS) {
                if (fichaFinanceiraFP.getDiasTrabalhados() != null && fichaFinanceiraFP.getDiasTrabalhados().compareTo(BigDecimal.valueOf(configuracaoFP.getQtdeMinimaDiasEuConsigMais())) < 0) {
                    logger.debug("Servidor sem a quantidade de dias trabalhados necessário para a mergem eu consigo +: [{}], dias: [{}] ", vinculo, configuracaoFP.getQtdeMinimaDiasEuConsigMais());
                }
            }
        }

        //Calcular valor da base
        BigDecimal decimal = fichaFinanceiraFPFacade.buscarValorMensalDoServidorPorBaseFP(baseEuConsigoMais.getCodigo(), mes, ano, TipoFolhaDePagamento.NORMAL, vinculo);
        if (decimal != null && decimal.compareTo(BigDecimal.ZERO) >= 0) {
            logger.debug("Valor da Base: [{}]", decimal);
            //remover 30%(valores das margens do outros tipos)
            BigDecimal percentualConsigancao = configuracaoFP.getPercentualMargemDisponivel();
            logger.debug("Valor da base de Consignação: [{}]", valorBaseConsignacao);
            BigDecimal multiply = valorBaseConsignacao.multiply(percentualConsigancao.divide(new BigDecimal(100)));

            BigDecimal baseLiquida = decimal.subtract(multiply);
            logger.debug("Valor da Base Menos os [{}]%: [{}]", percentualConsigancao, baseLiquida);
            if (baseLiquida != null && baseLiquida.compareTo(BigDecimal.ZERO) >= 0) {
                logger.debug("Valor Final com os 20%: [{}]", baseLiquida.multiply((configuracaoFP.getPercentualMargemEmprestimo()).divide(new BigDecimal(100))));
                return baseLiquida.multiply((configuracaoFP.getPercentualMargemEmprestimo()).divide(new BigDecimal(100)));
            }
        }
        return BigDecimal.ZERO;
    }

    private void carregarHierarquias(Map<Long, HierarquiaOrganizacional> hierarquiaOrganizacionalCash, Date data) {
        List<HierarquiaOrganizacional> hos = hierarquiaOrganizacionalFacade.getListaDeHierarquiaAdministrativaOrdenadaPorCodigo(data);
        if (hos != null) {
            for (HierarquiaOrganizacional ho : hos) {
                hierarquiaOrganizacionalCash.put(ho.getSubordinada().getId(), ho);
            }
        }
    }

    private String getModalidadeTurmalina(ContratoFP c, Date data) {
        ModalidadeContratoFP m = c.getModalidadeContratoFP();
        if (m.getCodigo() == 1) {
            EnquadramentoCC funcional = cargoConfiancaFacade.recuperaEnquadramentoCCVigente(c, data);
            if (funcional != null) {
                return "CARREIRA/COMISSÃO";
            }
            return "CARREIRA";
        }
        if (m.getCodigo() == 2) {
            return "COMISSÃO";
        }
        if (m.getCodigo() == 4) {
            return "CONTRATO TEMPORÁRIO";
        }
        if (m.getCodigo() == 7) {
            return "ELETIVOS";
        }
        return "NÃO ENCONTRADO";
    }

    public boolean jaExisteLancamentoParaMesEAno(ExportaArquivoMargem margem) {
        Query q = em.createQuery("select margem from ExportaArquivoMargem  margem where margem.mes = :mes and margem.ano = :ano");
        q.setParameter("mes", margem.getMes());
        q.setParameter("ano", margem.getAno());
        return !q.getResultList().isEmpty();
    }

    @Override
    public ExportaArquivoMargem recuperar(Object id) {
        ExportaArquivoMargem exportaArquivoMargem = super.recuperar(id);
        exportaArquivoMargem.getItemArquivoMargem().size();
        return exportaArquivoMargem;
    }

    public List<Long> buscarIdsExportacaoArquivoMargemPorVinculoFP(VinculoFP vinculoFP) {
        Query q = em.createQuery(" select distinct margem.id from ArquivoMargemVinculo itemVinculo " +
            " inner join itemVinculo.exportaArquivoMargem margem  " +
            " where itemVinculo.vinculoFP = : vinculo");
        q.setParameter("vinculo", vinculoFP);
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }
}
