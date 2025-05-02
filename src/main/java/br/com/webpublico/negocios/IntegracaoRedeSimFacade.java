/*
 * Codigo gerado automaticamente em Tue Feb 15 14:19:22 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.tributario.TipoWebService;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.comum.UsuarioWebFacade;
import br.com.webpublico.negocios.tributario.LeitorWsConfig;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.tributario.dto.*;
import br.com.webpublico.tributario.enumeration.GrauDeRiscoDTO;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.net.ssl.SSLContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Stateless
public class IntegracaoRedeSimFacade extends AbstractFacade<PessoaJuridica> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private NaturezaJuridicaFacade naturezaJuridicaFacade;
    @EJB
    private CNAEFacade cnaeFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    private ObjectMapper mapper = new ObjectMapper();
    @EJB
    private LeitorWsConfig leitorWsConfig;
    @EJB
    private HorarioFuncionamentoFacade horarioFuncionamentoFacade;
    @EJB
    private AlteracaoStatusPessoaFacade alteracaoStatusPessoaFacade;
    @EJB
    private ConfiguracaoAgendamentoTarefaFacade configuracaoAgendamentoTarefaFacade;
    @EJB
    private UsuarioWebFacade usuarioWebFacade;

    public IntegracaoRedeSimFacade() {
        super(PessoaJuridica.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfiguracaoAgendamentoTarefaFacade getConfiguracaoAgendamentoTarefaFacade() {
        return configuracaoAgendamentoTarefaFacade;
    }

    @Override
    public PessoaJuridica recuperar(Object id) {
        PessoaJuridica pf = em.find(PessoaJuridica.class, id);
        pf.getTelefones().size();
        pf.getEnderecos().size();
        pf.getContaCorrenteBancPessoas().size();
        pf.getPessoaCNAE().size();
        pf.getSociedadePessoaJuridica().size();
        return pf;
    }

    public PessoaJuridica buscarPessoaJuridicaPorCNPJ(String cnpj) {
        return pessoaFacade.buscarPessoaJuridicaPorCNPJ(cnpj, false);
    }

    public Boolean hasPessoaJuridicaPorCNPJ(String cnpj) {
        String hql = " from PessoaJuridica pj " +
            "  where (replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','')) = :cnpj";
        Query q = em.createQuery(hql);
        q.setParameter("cnpj", cnpj.trim());
        q.setMaxResults(1);
        return !pessoaFacade.buscarPessoasPorCPFCNPJ(cnpj, false).isEmpty();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    public PessoaJuridica atualizarDadosPessoaJuridica(EventoRedeSimDTO eventoRedeSimDTO,
                                                       String identificadorHistorico,
                                                       UsuarioSistema usuarioSistema,
                                                       String usuarioBancoDados) {
        return criarOrAtualizarPessoaJuridica(eventoRedeSimDTO, identificadorHistorico, usuarioSistema, usuarioBancoDados);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public PessoaJuridica criarOrAtualizarPessoaJuridica(EventoRedeSimDTO eventoRedeSimDTO, String identificadorHistorico,
                                                         UsuarioSistema usuarioSistema, String usuarioBancoDados) {
        PessoaJuridicaDTO pessoaJuridicaDTO = eventoRedeSimDTO.getPessoaDTO();
        PessoaJuridica pessoaJuridica = criarOuAtualizarPessoaJuridica(pessoaJuridicaDTO, usuarioSistema);
        criarDadosJuntaComercial(eventoRedeSimDTO, pessoaJuridicaDTO, pessoaJuridica);
        criarDadosFiliais(pessoaJuridicaDTO, pessoaJuridica, usuarioBancoDados, usuarioSistema);
        pessoaJuridica = em.merge(pessoaJuridica);
        if (pessoaJuridica.isModificado()) {
            criarHistoricoAlteracaoRedeSim(eventoRedeSimDTO, pessoaJuridica, identificadorHistorico, usuarioSistema);
        }
        return pessoaJuridica;
    }

    private void criarHistoricoAlteracaoRedeSim(EventoRedeSimDTO eventoRedeSimDTO, PessoaJuridica pessoaJuridica, String identificador, UsuarioSistema usuarioSistema) {
        String conteudo;
        try {
            conteudo = (new ObjectMapper().writeValueAsString(eventoRedeSimDTO.getPessoaDTO()));
        } catch (JsonProcessingException e) {
            conteudo = "";
        }
        String descricao = hasPessoaJuridicaPorCNPJ(eventoRedeSimDTO.getPessoaDTO().getCnpj()) ? "Alteração" : "Inserção";
        identificador += " - Tipo de Evento: " + descricao;
        HistoricoAlteracaoRedeSim historico = new HistoricoAlteracaoRedeSim(
            pessoaJuridica,
            usuarioSistema,
            identificador,
            conteudo);
        pessoaJuridica.getHistoricosIntegracaoRedeSim().add(historico);
    }

    public PessoaJuridica atualizarDadosPessoaJuridicaSemSalvar(PessoaJuridica pessoaJuridica,
                                                                EventoRedeSimDTO eventoRedeSimDTO,
                                                                UsuarioSistema usuarioSistema) {
        PessoaJuridicaDTO pessoaJuridicaDTO = eventoRedeSimDTO.getPessoaDTO();
        pessoaJuridica = atualizarPessoaJuridica(pessoaJuridica, pessoaJuridicaDTO, usuarioSistema);
        criarDadosJuntaComercial(eventoRedeSimDTO, pessoaJuridicaDTO, pessoaJuridica);
        return pessoaJuridica;
    }

    public SituacaoCadastralPessoa getSituacaoCadastroPorSituacaoCadastralRFB(SituacaoCadastralRFB situacaoCadastralRFB) {
        if (SituacaoCadastralRFB.SUSPENSA.equals(situacaoCadastralRFB)) {
            return SituacaoCadastralPessoa.SUSPENSO;
        } else if (SituacaoCadastralRFB.INAPTA.equals(situacaoCadastralRFB)) {
            return SituacaoCadastralPessoa.INATIVO;
        } else if (SituacaoCadastralRFB.BAIXADA.equals(situacaoCadastralRFB)) {
            return SituacaoCadastralPessoa.BAIXADO;
        }
        return SituacaoCadastralPessoa.ATIVO;
    }

    private PessoaJuridica criarOuAtualizarPessoaJuridica(PessoaJuridicaDTO pessoaJuridicaDTO, UsuarioSistema usuarioSistema) {
        PessoaJuridica pessoaJuridica = buscarPessoaJuridicaPorCNPJ(pessoaJuridicaDTO.getCnpj());
        return atualizarPessoaJuridica(pessoaJuridica, pessoaJuridicaDTO, usuarioSistema);
    }

    private PessoaJuridica atualizarPessoaJuridica(PessoaJuridica pessoaJuridica, PessoaJuridicaDTO pessoaJuridicaDTO, UsuarioSistema usuarioSistema) {
        SituacaoCadastralPessoa situacaoCadastral = getSituacaoCadastroPorSituacaoCadastralRFB(SituacaoCadastralRFB.getEnumByCodigo(pessoaJuridicaDTO.getCodigoSituacaoRFB()));
        if (pessoaJuridica == null) {
            pessoaJuridica = new PessoaJuridica();
            pessoaJuridica.setCnpj(pessoaJuridicaDTO.getCnpj());
            pessoaJuridica.setSituacaoCadastralPessoa(situacaoCadastral);
            pessoaJuridica.adicionarHistoricoSituacaoCadastral();
        } else {
            if ((!situacaoCadastral.equals(pessoaJuridica.getSituacaoCadastralPessoa())
                || pessoaJuridica.getHistoricoSituacoesPessoa().isEmpty())
                && pessoaJuridica.getId() != null) {
                pessoaJuridica.setSituacaoCadastralPessoa(situacaoCadastral);
                pessoaJuridica.adicionarHistoricoSituacaoCadastral();
                if (SituacaoCadastralPessoa.BAIXADO.equals(situacaoCadastral)) {
                    criarProcessoInativacaoPessoaQuandoBaixado(pessoaJuridica, usuarioSistema);
                }
            }
        }
        if (pessoaJuridicaDTO.getNome() != null) {
            pessoaJuridica.setRazaoSocial(pessoaJuridicaDTO.getNome());
        }
        if (pessoaJuridicaDTO.getNomeFantasia() != null) {
            pessoaJuridica.setNomeFantasia(pessoaJuridicaDTO.getNomeFantasia());
        }
        if (pessoaJuridicaDTO.getAbertura() != null) {
            pessoaJuridica.setInicioAtividade(pessoaJuridicaDTO.getAbertura());
        }
        if (pessoaJuridicaDTO.getPorte() != null) {
            pessoaJuridica.setPorte(TipoPorte.getTipoPortePorPorteDTO(pessoaJuridicaDTO.getPorte()));
        }
        if (pessoaJuridicaDTO.getNumeroInscricaoEstadual() != null) {
            pessoaJuridica.setInscricaoEstadual(pessoaJuridicaDTO.getNumeroInscricaoEstadual());
        }
        if (pessoaJuridicaDTO.getCapitalSocial() != null) {
            pessoaJuridica.setCapitalSocial(pessoaJuridicaDTO.getCapitalSocial());
        }
        if (pessoaJuridicaDTO.getCapitalIntegralizado() != null) {
            pessoaJuridica.setCapitalIntegralizado(pessoaJuridicaDTO.getCapitalIntegralizado());
        }
        if (pessoaJuridicaDTO.getValorQuota() != null) {
            pessoaJuridica.setValorCota(pessoaJuridicaDTO.getValorQuota());
        }
        pessoaJuridica.setUltimaIntegracaoRedeSim(new Date());
        if (!Strings.isNullOrEmpty(pessoaJuridicaDTO.getEmail())) {
            pessoaJuridica.setEmail(pessoaJuridicaDTO.getEmail());
        }
        if (pessoaJuridicaDTO.getNaturezaJuridica() != null) {
            pessoaJuridica.setNaturezaJuridica(naturezaJuridicaFacade.buscarNaturezaPorCodigo(pessoaJuridicaDTO.getNaturezaJuridica().getCodigo()));
        }
        criarDadosHorarioFuncionamento(pessoaJuridica, pessoaJuridicaDTO);
        criarDadosEndereco(pessoaJuridicaDTO, pessoaJuridica);
        criarDadosCnae(pessoaJuridicaDTO, pessoaJuridica);
        criarDadosSocios(pessoaJuridicaDTO, pessoaJuridica);
        criarDadosRepresentantes(pessoaJuridicaDTO, pessoaJuridica);
        return pessoaJuridica;
    }

    private void criarProcessoInativacaoPessoaQuandoBaixado(PessoaJuridica pessoaJuridica, UsuarioSistema usuarioSistema) {
        AlteracaoStatusPessoa processo = new AlteracaoStatusPessoa();
        processo.setPessoa(pessoaJuridica);
        processo.setTipoProcesso(AlteracaoStatusPessoa.TipoProcessoAlteracaoCadastroPessoa.BAIXA);
        processo.setData(new Date());
        processo.setUsuarioSistema(usuarioSistema);
        processo.setAno(DataUtil.getAno(processo.getData()));
        processo.setNumero(Integer.parseInt(alteracaoStatusPessoaFacade.retornaNovoNumero(processo.getAno())));
        processo.setNumeroCompleto(alteracaoStatusPessoaFacade.retornaNovoNumeroCompleto(processo.getData()));
        processo.setMotivo("Baixa automática a partir da sincronização com a RedeSim.");
        em.merge(processo);
    }

    private void criarDadosHorarioFuncionamento(PessoaJuridica pessoaJuridica, PessoaJuridicaDTO pessoaJuridicaDTO) {
        if (pessoaJuridicaDTO.getHorariosFuncionamento() != null && !pessoaJuridicaDTO.getHorariosFuncionamento().isEmpty()) {
            boolean hasHorario = false;
            HorarioFuncionamento horarioFuncionamento = buscarOuCriarHorarioFuncionamento(pessoaJuridicaDTO.getHorariosFuncionamento());
            for (PessoaHorarioFuncionamento pessoaHorarioFuncionamento : pessoaJuridica.getHorariosFuncionamentoAtivos()) {
                if (horarioFuncionamento.equals(pessoaHorarioFuncionamento.getHorarioFuncionamento())) {
                    hasHorario = true;
                    break;
                }
            }
            if (horarioFuncionamento != null) {
                for (PessoaHorarioFuncionamento pessoaHorarioFuncionamento : pessoaJuridica.getHorariosFuncionamentoAtivos()) {
                    if (!horarioFuncionamento.equals(pessoaHorarioFuncionamento.getHorarioFuncionamento())) {
                        pessoaHorarioFuncionamento.setAtivo(false);
                    }
                }
            }

            if (!hasHorario) {
                pessoaJuridica.getHorariosFuncionamento().add(criarPessoaHorarioFuncionamento(horarioFuncionamento, pessoaJuridica));
            }

            for (PessoaHorarioFuncionamento horariosAtivos : pessoaJuridica.getHorariosFuncionamentoAtivos()) {
                Hibernate.initialize(horariosAtivos.getHorarioFuncionamento().getItens());
            }
        }
    }

    private void criarDadosFiliais(PessoaJuridicaDTO pessoaJuridicaDTO, PessoaJuridica pessoaJuridica, String usuarioBancoDados,
                                   UsuarioSistema usuarioSistema) {
        if (!pessoaJuridicaDTO.getFiliais().isEmpty()) {
            List<PessoaJuridicaDTO> filiaisNaoExistentes = Lists.newArrayList();
            for (PessoaJuridicaDTO dto : pessoaJuridicaDTO.getFiliais()) {
                boolean jaTem = false;
                for (FilialPessoaJuridica filial : pessoaJuridica.getFiliais()) {
                    if (Util.removeMascaras(filial.getFilial().getCpf_Cnpj()).equals(Util.removeMascaras(dto.getCnpj()))) {
                        jaTem = true;
                    }
                }
                if (!jaTem) {
                    filiaisNaoExistentes.add(dto);
                }
            }
            for (PessoaJuridicaDTO filialNaoExistente : filiaisNaoExistentes) {
                FilialPessoaJuridica filialPessoaJuridica = criarFilial(pessoaJuridica, filialNaoExistente, usuarioBancoDados,
                    usuarioSistema);
                if (filialPessoaJuridica != null) {
                    pessoaJuridica.getFiliais().add(filialPessoaJuridica);
                }
            }
        }
    }

    private void criarDadosRepresentantes(PessoaJuridicaDTO pessoaJuridicaDTO, PessoaJuridica pessoaJuridica) {
        if (!pessoaJuridicaDTO.getRepresentantesLegais().isEmpty()) {
            for (RepresentanteLegalPessoa representanteLegalPessoa : new ArrayList<>(pessoaJuridica.getRepresentantesLegal())) {
                boolean found = false;
                for (PessoaFisicaDTO pessoaDTO : pessoaJuridicaDTO.getRepresentantesLegais()) {
                    if (pessoaDTO.getCpf().equals(representanteLegalPessoa.getRepresentante().getCpf_Cnpj().replace(".", "").replace("-", ""))) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    pessoaJuridica.getRepresentantesLegal().remove(representanteLegalPessoa);
                }
            }
            for (PessoaFisicaDTO pessoaDTO : pessoaJuridicaDTO.getRepresentantesLegais()) {
                boolean flagCriar;
                if (pessoaDTO.getNome() == null) pessoaDTO.setNome("Não informado");
                Pessoa pessoa = pessoaFacade.recuperaPessoaFisicaPorCPF(pessoaDTO.getCpf());
                if (pessoa == null) {
                    flagCriar = true;
                } else {
                    flagCriar = true;
                    for (RepresentanteLegalPessoa representanteLegalPessoa : pessoaJuridica.getRepresentantesLegal()) {
                        if (representanteLegalPessoa.getRepresentante().getCpf_Cnpj().replace(".", "").replace("-", "").equals(pessoaDTO.getCpf())) {
                            flagCriar = false;
                            PessoaFisica pf = (PessoaFisica) representanteLegalPessoa.getRepresentante();
                            if (!pessoaDTO.getNome().equals(pf.getNome())) {
                                pf.setNome(pessoaDTO.getNome());
                            }
                            if (!pessoaDTO.getCpf().equals(pf.getCpf().replace(".", "").replace("-", ""))) {
                                pf.setCpf(pessoaDTO.getCpf());
                            }
                            if (!SituacaoCadastralPessoa.ATIVO.equals(pf.getSituacaoCadastralPessoa())) {
                                pf.setSituacaoCadastralPessoa(SituacaoCadastralPessoa.ATIVO);
                                pf.adicionarHistoricoSituacaoCadastral();
                            }
                            if (pessoaDTO.getEnderecos() != null && !pessoaDTO.getEnderecos().isEmpty()) {
                                EnderecoCorreio enderecoCorreio = copiarEnderecoCorreioDTOParaEnderecoCorreio(pessoaDTO.getEnderecos().get(0), new EnderecoCorreio());
                                if (pessoa.getEnderecos().isEmpty()) {
                                    pessoa.getEnderecos().add(enderecoCorreio);
                                } else {
                                    boolean found = false;
                                    for (EnderecoCorreio end : pessoa.getEnderecos()) {
                                        if (end.getEnderecoCompleto().equals(enderecoCorreio.getEnderecoCompleto())) {
                                            found = true;
                                            break;
                                        }
                                    }
                                    if (!found) {
                                        pessoa.getEnderecos().add(enderecoCorreio);
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
                if (flagCriar) {
                    adicionarNovoRepresentanteLegal(pessoaJuridica, pessoaDTO);
                }
            }
        }
    }

    private void criarDadosSocios(PessoaJuridicaDTO pessoaJuridicaDTO, PessoaJuridica pessoaJuridica) {
        if (!pessoaJuridicaDTO.getSocios().isEmpty()) {
            List<SociedadePessoaJuridicaDTO> sociosAdicionar = Lists.newArrayList();
            List<SociedadePessoaJuridica> sociosRemover = Lists.newArrayList();
            for (SociedadePessoaJuridicaDTO socioDTO : pessoaJuridicaDTO.getSocios()) {
                boolean jaTem = false;
                for (SociedadePessoaJuridica socio : pessoaJuridica.getSociedadePessoaJuridica()) {
                    String cpf = socioDTO.getPessoa().isPessoaFisica() ? socioDTO.getPessoa().getPessoaFisica().getCpf() : socioDTO.getPessoa().getPessoaJuridica().getCnpj();
                    if (Util.removeMascaras(socio.getSocio().getCpf_Cnpj()).equals(Util.removeMascaras(cpf))) {
                        jaTem = true;
                    }
                }
                if (!jaTem) {
                    sociosAdicionar.add(socioDTO);
                }
            }
            for (SociedadePessoaJuridica socio : pessoaJuridica.getSociedadePessoaJuridica()) {
                boolean jaTem = false;
                for (SociedadePessoaJuridicaDTO socioDTO : pessoaJuridicaDTO.getSocios()) {
                    String cpf = socioDTO.getPessoa().isPessoaFisica() ? socioDTO.getPessoa().getPessoaFisica().getCpf() : socioDTO.getPessoa().getPessoaJuridica().getCnpj();
                    if ((socio.getSocio().getCpf_Cnpj() != null && !"".equals(socio.getSocio().getCpf_Cnpj())) &&
                        Util.removeMascaras(socio.getSocio().getCpf_Cnpj()).equals(Util.removeMascaras(cpf))) {
                        jaTem = true;
                        Double proporcaoDoSocio = calcularProporcaoDoSocio(pessoaJuridicaDTO, socioDTO);
                        if (proporcaoDoSocio != null) {
                            socio.setProporcao(proporcaoDoSocio);
                        }
                    }
                }
                if (!jaTem) {
                    sociosRemover.add(socio);
                }
            }
            for (SociedadePessoaJuridicaDTO socioDTO : sociosAdicionar) {
                String cpf = socioDTO.getPessoa().isPessoaFisica() ? socioDTO.getPessoa().getPessoaFisica().getCpf() : socioDTO.getPessoa().getPessoaJuridica().getCnpj();
                if (!hasSocioRepetido(cpf, pessoaJuridica.getSociedadePessoaJuridica())) {
                    adicionarNovoSocio(pessoaJuridicaDTO, pessoaJuridica, socioDTO);
                }
            }

            for (SociedadePessoaJuridica socio : sociosRemover) {
                pessoaJuridica.getSociedadePessoaJuridica().remove(socio);
            }

            correcaoDasProporcoesDosSocios(pessoaJuridica.getSociedadePessoaJuridica());
        }
    }

    private boolean hasSocioRepetido(String cpf, List<SociedadePessoaJuridica> sociedadePessoaJuridica) {
        int qtde = 0;
        for (SociedadePessoaJuridica sociedade : sociedadePessoaJuridica) {
            if (Util.removeMascaras(sociedade.getSocio().getCpf_Cnpj()).equals(Util.removeMascaras(cpf))) {
                qtde++;
            }
        }
        return qtde > 1;
    }

    private void correcaoDasProporcoesDosSocios(List<SociedadePessoaJuridica> sociedadePessoaJuridica) {
        BigDecimal totalProporcao = somarTotalProporcaoDosSocios(sociedadePessoaJuridica);
        int quantidade = sociedadePessoaJuridica.size();
        if (totalProporcao.compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal proporcao = BigDecimal.valueOf(100).divide(BigDecimal.valueOf(quantidade), 2, RoundingMode.HALF_UP);
            for (SociedadePessoaJuridica socio : sociedadePessoaJuridica) {
                socio.setProporcao(proporcao.doubleValue());
            }
            totalProporcao = somarTotalProporcaoDosSocios(sociedadePessoaJuridica);
        }

        BigDecimal diferenca = BigDecimal.valueOf(100).subtract(totalProporcao);
        if (diferenca.compareTo(BigDecimal.ZERO) != 0) {
            Collections.sort(sociedadePessoaJuridica, new Comparator<SociedadePessoaJuridica>() {
                @Override
                public int compare(SociedadePessoaJuridica o1, SociedadePessoaJuridica o2) {
                    return o2.getProporcao().compareTo(o1.getProporcao());
                }
            });
            sociedadePessoaJuridica.get(0).setProporcao(BigDecimal.valueOf(sociedadePessoaJuridica.get(0).getProporcao()).add(diferenca).doubleValue());
        }
    }

    private BigDecimal somarTotalProporcaoDosSocios(List<SociedadePessoaJuridica> sociedadePessoaJuridica) {
        BigDecimal totalProporcao = BigDecimal.ZERO;
        for (SociedadePessoaJuridica socio : sociedadePessoaJuridica) {
            totalProporcao = totalProporcao.add(BigDecimal.valueOf(socio.getProporcao()).setScale(2, RoundingMode.HALF_UP));
        }
        return totalProporcao;
    }

    private Double calcularProporcaoDoSocio(PessoaJuridicaDTO pessoaJuridicaDTO, SociedadePessoaJuridicaDTO socioDTO) {
        BigDecimal capitalSocial = pessoaJuridicaDTO.getCapitalSocial();
        BigDecimal capitalSocio = socioDTO.getCapitalSocial() != null ? socioDTO.getCapitalSocial() : BigDecimal.ZERO;
        BigDecimal capitalSocialMatriz = pessoaJuridicaDTO.getCapitalSocialMatriz() != null ? pessoaJuridicaDTO.getCapitalSocialMatriz() : BigDecimal.ZERO;
        if (capitalSocial != null && capitalSocio.compareTo(capitalSocial) > 0
            && capitalSocialMatriz.compareTo(BigDecimal.ZERO) > 0) {
            capitalSocial = capitalSocialMatriz;
        }
        if (capitalSocial != null) {
            BigDecimal proporcao = capitalSocio.divide(capitalSocial, 8, RoundingMode.HALF_UP);
            if (proporcao.compareTo(BigDecimal.valueOf(100)) > 0) {
                proporcao = BigDecimal.valueOf(100);
            }
            return proporcao.doubleValue();
        }
        return null;
    }

    public PessoaJuridica criarDadosCnae(PessoaJuridicaDTO pessoaJuridicaDTO, PessoaJuridica pessoaJuridica) {
        if (!pessoaJuridicaDTO.getCnaes().isEmpty()) {
            removerCNAEsNaoExistentesNaRedeSim(pessoaJuridicaDTO, pessoaJuridica);
            criarNovosCNAEsNaoExistemNoSistema(pessoaJuridicaDTO, pessoaJuridica);
        }
        return pessoaJuridica;
    }

    private void criarNovosCNAEsNaoExistemNoSistema(PessoaJuridicaDTO pessoaJuridicaDTO, PessoaJuridica pessoaJuridica) {
        List<PessoaCnaeDTO> cnaesNaoExistentes = Lists.newArrayList();
        for (PessoaCnaeDTO pessoaCnaeDTO : pessoaJuridicaDTO.getCnaes()) {
            boolean jaTem = false;
            for (PessoaCNAE pessoaCNAE : pessoaJuridica.getPessoaCNAE()) {
                if (pessoaCnaeDTO.getCnae() != null && pessoaCNAE.getCnae() != null &&
                    pessoaCnaeDTO.getCnae().getCodigo().equals(pessoaCNAE.getCnae().getCodigoCnae()) &&
                    pessoaCnaeDTO.getCnae().getGrauDeRisco().equals(pessoaCNAE.getCnae().getGrauDeRisco()) &&
                    CNAE.Situacao.ATIVO.equals(pessoaCNAE.getCnae().getSituacao())) {

                    pessoaCNAE.setTipo(EconomicoCNAE.TipoCnae.getTipoCnaePorTipoCnaeDTO(pessoaCnaeDTO.getTipoCnae()));
                    pessoaCNAE.setExercidaNoLocal(pessoaCnaeDTO.getExercidaNoLocal());
                    pessoaCNAE.setFim(null);
                    jaTem = true;
                }
            }
            if (!jaTem) {
                cnaesNaoExistentes.add(pessoaCnaeDTO);
            }
        }
        for (PessoaCnaeDTO cnaesNaoExistente : cnaesNaoExistentes) {
            pessoaJuridica.getPessoaCNAE().add(criarPessoaCnae(pessoaJuridica, cnaesNaoExistente));
        }
    }

    private void removerCNAEsNaoExistentesNaRedeSim(PessoaJuridicaDTO pessoaJuridicaDTO, PessoaJuridica pessoaJuridica) {
        removerCnaesRepetidos(pessoaJuridica);
        List<PessoaCNAE> cnaesNaoExistentesNoDto = Lists.newArrayList();
        for (PessoaCNAE pessoaCnae : pessoaJuridica.getPessoaCNAEAtivos()) {
            boolean jaTem = false;
            for (PessoaCnaeDTO pessoaCnaeDTO : pessoaJuridicaDTO.getCnaes()) {
                if (pessoaCnae.getCnae().getCodigoCnae().equals(pessoaCnaeDTO.getCnae().getCodigo())
                    && pessoaCnae.getCnae().getGrauDeRisco().equals(pessoaCnaeDTO.getCnae().getGrauDeRisco())
                    && CNAE.Situacao.ATIVO.equals(pessoaCnae.getCnae().getSituacao())) {
                    jaTem = true;
                }
            }
            if (!jaTem) {
                cnaesNaoExistentesNoDto.add(pessoaCnae);
            }
        }
        for (PessoaCNAE cnaesNaoExistente : cnaesNaoExistentesNoDto) {
            cnaesNaoExistente.setFim(new Date());
        }
    }

    private void removerCnaesRepetidos(PessoaJuridica pessoaJuridica) {
        List<PessoaCNAE> cnaesParaRemover = Lists.newArrayList();
        for (PessoaCNAE pessoaCnae : pessoaJuridica.getPessoaCNAEAtivos()) {
            if (hasCnaeRepetido(pessoaCnae.getCnae().getCodigoCnae(), pessoaCnae.getCnae().getGrauDeRisco(), pessoaJuridica.getPessoaCNAEAtivos())
                && !cnaesParaRemover.contains(pessoaCnae)) {
                cnaesParaRemover.add(pessoaCnae);
            }
        }

        for (PessoaCNAE remover : cnaesParaRemover) {
            pessoaJuridica.getPessoaCNAE().remove(remover);
        }
    }

    private boolean hasCnaeRepetido(String codigoCnae, GrauDeRiscoDTO grauDeRisco, List<PessoaCNAE> pessoaCNAEs) {
        int qtde = 0;
        for (PessoaCNAE pessoaCNAE : pessoaCNAEs) {
            if (pessoaCNAE.getCnae().getCodigoCnae().equals(codigoCnae)
                && grauDeRisco.equals(pessoaCNAE.getCnae().getGrauDeRisco())) {
                qtde++;
            }
        }
        return qtde > 1;
    }

    private void criarDadosEndereco(PessoaJuridicaDTO pessoaJuridicaDTO, PessoaJuridica pessoaJuridica) {
        if (pessoaJuridicaDTO.getEnderecoCorreio() != null) {
            EnderecoCorreio endereco = pessoaJuridica.getEnderecoMarcadoPrincipal();
            if (endereco == null) {
                endereco = pessoaJuridica.getEnderecoscorreio().stream()
                    .filter(ec -> compareEndereco(ec, pessoaJuridicaDTO.getEnderecoCorreio()))
                    .findFirst().orElse(null);
                if (endereco == null) {
                    endereco = new EnderecoCorreio();
                    pessoaJuridica.getEnderecoscorreio().add(endereco);
                }
                copiarEnderecoCorreioDTOParaEnderecoCorreio(pessoaJuridicaDTO.getEnderecoCorreio(), endereco);
                endereco.setPrincipal(Boolean.TRUE);
            } else {
                endereco = pessoaJuridica.getEnderecoscorreio().stream().findFirst().orElse(new EnderecoCorreio());
                copiarEnderecoCorreioDTOParaEnderecoCorreio(pessoaJuridicaDTO.getEnderecoCorreio(), endereco);
            }
        }
    }

    private boolean compareEndereco(EnderecoCorreio ec, EnderecoCorreioDTO enderecoCorreio) {
        return Util.getStringVaziaSeNull(ec.getBairro()).equals(Util.getStringVaziaSeNull(enderecoCorreio.getBairro())) &&
            Util.getStringVaziaSeNull(ec.getLogradouro()).equals(Util.getStringVaziaSeNull(enderecoCorreio.getLogradouro())) &&
            Util.getStringVaziaSeNull(ec.getNumero()).equals(Util.getStringVaziaSeNull(enderecoCorreio.getNumero())) &&
            Util.getStringVaziaSeNull(ec.getComplemento()).equals(Util.getStringVaziaSeNull(enderecoCorreio.getComplemento())) &&
            Util.getStringVaziaSeNull(ec.getLocalidade()).equals(Util.getStringVaziaSeNull(enderecoCorreio.getLocalidade()));
    }

    private void criarDadosJuntaComercial(EventoRedeSimDTO eventoRedeSimDTO, PessoaJuridicaDTO pessoaJuridicaDTO, PessoaJuridica pessoaJuridica) {
        JuntaComercialPessoaJuridica junta = null;
        JuntaComercialPessoaJuridica juntaSemCodigo = null;
        for (JuntaComercialPessoaJuridica juntaComercialPessoaJuridica : pessoaJuridica.getJuntaComercial()) {
            if (juntaComercialPessoaJuridica.getNumeroProcesso() != null && juntaComercialPessoaJuridica.getNumeroProcesso().equals(pessoaJuridicaDTO.getNumeroProcessoJuntaComercial())) {
                junta = juntaComercialPessoaJuridica;
                break;
            }
            if (Strings.isNullOrEmpty(juntaComercialPessoaJuridica.getNumeroProcesso())) {
                juntaSemCodigo = juntaComercialPessoaJuridica;
            }
        }
        if (junta == null) {
            if (juntaSemCodigo != null) {
                junta = juntaSemCodigo;
            } else {
                junta = new JuntaComercialPessoaJuridica();
            }
            junta.setPessoaJuridica(pessoaJuridica);
            if (pessoaJuridicaDTO.getNumeroProcessoJuntaComercial() != null) {
                junta.setNumeroProcesso(pessoaJuridicaDTO.getNumeroProcessoJuntaComercial());
            }
            if (pessoaJuridicaDTO.getNaturezaJuridica() != null) {
                junta.setNaturezaJuridica(naturezaJuridicaFacade.buscarNaturezaPorCodigo(pessoaJuridicaDTO.getNaturezaJuridica().getCodigo()));
            }
            if (pessoaJuridicaDTO.getNumeroProcessoJuntaComercial() != null) {
                junta.setNumeroProtocoloJuntaComercial(pessoaJuridicaDTO.getNumeroProcessoJuntaComercial());
            }
            if (pessoaJuridicaDTO.getUltimaViabilidadeVinculada() != null) {
                junta.setNumeroProtocoloViabilidade(pessoaJuridicaDTO.getUltimaViabilidadeVinculada());
            }
            pessoaJuridica.getJuntaComercial().add(junta);
        } else {
            if (pessoaJuridicaDTO.getNaturezaJuridica() != null) {
                junta.setNaturezaJuridica(naturezaJuridicaFacade.buscarNaturezaPorCodigo(pessoaJuridicaDTO.getNaturezaJuridica().getCodigo()));
            }
            if (pessoaJuridicaDTO.getNumeroProcessoJuntaComercial() != null) {
                junta.setNumeroProtocoloJuntaComercial(pessoaJuridicaDTO.getNumeroProcessoJuntaComercial());
            }
            if (pessoaJuridicaDTO.getUltimaViabilidadeVinculada() != null) {
                junta.setNumeroProtocoloViabilidade(pessoaJuridicaDTO.getUltimaViabilidadeVinculada());
            }
        }

        junta.getEventosRedeSim().add(new EventoRedeSimPessoaJuridica(junta, eventoRedeSimDTO));
    }

    private FilialPessoaJuridica criarFilial(PessoaJuridica pessoaJuridica,
                                             PessoaJuridicaDTO filialNaoExistente,
                                             String usuarioBancoDados,
                                             UsuarioSistema usuarioSistema) {
        PessoaJuridica pessoaJuridicaFilial = pessoaFacade.recuperaPessoaJuridicaPorCNPJ(filialNaoExistente.getCnpj());
        if (pessoaJuridicaFilial == null) {
            EventoRedeSimDTO eventoRedeSimDTO = buscarRedeSimPorCnpj(filialNaoExistente.getCnpj(), usuarioBancoDados, true);
            if (eventoRedeSimDTO != null) {
                filialNaoExistente = eventoRedeSimDTO.getPessoaDTO();
                pessoaJuridicaFilial = criarOuAtualizarPessoaJuridica(filialNaoExistente, usuarioSistema);
            }
        }
        if (pessoaJuridicaFilial != null) {
            FilialPessoaJuridica filialPessoaJuridica = new FilialPessoaJuridica();
            filialPessoaJuridica.setPessoaJuridica(pessoaJuridica);
            filialPessoaJuridica.setFilial(em.merge(pessoaJuridicaFilial));
            return filialPessoaJuridica;
        }
        return null;
    }

    private void adicionarNovoSocio(PessoaJuridicaDTO pessoaJuridicaDTO, PessoaJuridica pessoaJuridica, SociedadePessoaJuridicaDTO socioDTO) {
        SociedadePessoaJuridica novoSocio = new SociedadePessoaJuridica();
        Pessoa pessoa = null;
        if (socioDTO.getPessoa().isPessoaFisica()) {
            PessoaFisicaDTO pfDto = socioDTO.getPessoa().getPessoaFisica();
            pessoa = pessoaFacade.buscarPessoaPorCpfOrCnpj(pfDto.getCpf());
            pessoa = criarPessoaFisicaCasoNaoExista(pessoa, pfDto);
        } else {
            PessoaJuridicaDTO pjDto = socioDTO.getPessoa().getPessoaJuridica();
            pessoa = pessoaFacade.buscarPessoaPorCpfOrCnpj(pjDto.getCnpj());
            SituacaoCadastralPessoa situacaoCadastral = getSituacaoCadastroPorSituacaoCadastralRFB(SituacaoCadastralRFB.getEnumByCodigo(pjDto.getCodigoSituacaoRFB()));
            if (pessoa == null) {
                PessoaJuridica pj = new PessoaJuridica();
                pj.setCnpj(pjDto.getCnpj());
                pj.setRazaoSocial(pjDto.getNome());
                pj.setSituacaoCadastralPessoa(situacaoCadastral);
                pj.adicionarHistoricoSituacaoCadastral();
                pessoa = em.merge(pj);
            } else {
                if (!situacaoCadastral.equals(pessoa.getSituacaoCadastralPessoa())) {
                    pessoa.setSituacaoCadastralPessoa(situacaoCadastral);
                    pessoa.adicionarHistoricoSituacaoCadastral();
                }
                if (pjDto.getEnderecoCorreio() != null) {
                    pessoa.getEnderecos().add(copiarEnderecoCorreioDTOParaEnderecoCorreio(pjDto.getEnderecoCorreio(), new EnderecoCorreio()));
                }
                pessoa = em.merge(pessoa);
            }
        }
        novoSocio.setDataRegistro(new Date());
        novoSocio.setDataInicio(socioDTO.getInicioVigencia());
        novoSocio.setSocio(pessoa);
        novoSocio.setPessoaJuridica(pessoaJuridica);
        novoSocio.setParticipacao(socioDTO.getCapitalSocial());
        if (socioDTO.getCapitalSocial() != null && pessoaJuridicaDTO.getCapitalSocial() != null) {
            novoSocio.setProporcao(calcularProporcaoDoSocio(pessoaJuridicaDTO, socioDTO));
        }
        if (novoSocio.getProporcao() == null) {
            novoSocio.setProporcao(0D);
        }
        pessoaJuridica.getSociedadePessoaJuridica().add(novoSocio);
    }

    private void adicionarNovoRepresentanteLegal(PessoaJuridica pessoaJuridica, PessoaFisicaDTO pessoaDTO) {
        Pessoa pessoa = pessoaFacade.buscarPessoaPorCpfOrCnpj(pessoaDTO.getCpf());
        pessoa = criarPessoaFisicaCasoNaoExista(pessoa, pessoaDTO);
        RepresentanteLegalPessoa representanteLegalPessoa = new RepresentanteLegalPessoa();
        representanteLegalPessoa.setPessoa(pessoaJuridica);
        representanteLegalPessoa.setDataInicio(new Date());
        representanteLegalPessoa.setRepresentante(pessoa);
        pessoaJuridica.getRepresentantesLegal().add(representanteLegalPessoa);
    }

    private Pessoa criarPessoaFisicaCasoNaoExista(Pessoa pessoa, PessoaFisicaDTO pfDto) {
        if (pessoa == null) {
            PessoaFisica pf = new PessoaFisica();
            pf.setCpf(pfDto.getCpf());
            pf.setNome(pfDto.getNome());
            pf.setSituacaoCadastralPessoa(SituacaoCadastralPessoa.ATIVO);
            pf.adicionarHistoricoSituacaoCadastral();
            pessoa = em.merge(pf);
        } else {
            if (!SituacaoCadastralPessoa.ATIVO.equals(pessoa.getSituacaoCadastralPessoa())) {
                pessoa.setSituacaoCadastralPessoa(SituacaoCadastralPessoa.ATIVO);
                pessoa.adicionarHistoricoSituacaoCadastral();
            }
            if (pfDto.getEnderecos() != null && !pfDto.getEnderecos().isEmpty()) {
                pessoa.getEnderecos().add(copiarEnderecoCorreioDTOParaEnderecoCorreio(pfDto.getEnderecos().get(0), new EnderecoCorreio()));
            }
            pessoa = em.merge(pessoa);
        }
        return pessoa;
    }

    public CadastroEconomico criarDadosDoCadastro(EventoRedeSimDTO eventoRedeSimDTO, PessoaJuridica pessoaJuridica, UsuarioSistema usuarioSistema) {
        CadastroEconomico cadastroEconomico = cadastroEconomicoFacade.buscarCadastroEconomicoPorCnpjRedeSim(eventoRedeSimDTO.getPessoaDTO().getCnpj());
        if (pessoaJuridica == null) {
            pessoaJuridica = pessoaFacade.recuperaPessoaJuridicaPorCNPJ(eventoRedeSimDTO.getPessoaDTO().getCnpj());
        } else {
            pessoaJuridica = (PessoaJuridica) pessoaFacade.recuperarPJ(pessoaJuridica.getId());
        }

        if (cadastroEconomico == null) {
            cadastroEconomico = new CadastroEconomico();
            cadastroEconomico.setPessoa(pessoaJuridica);
            cadastroEconomicoFacade.gerarInscricaoCadastral(cadastroEconomico);
            cadastroEconomico.setDataCadastro(new Date());

            criarEnquadramentoFiscal(eventoRedeSimDTO, cadastroEconomico);

            cadastroEconomicoFacade.adicionarServicos(cadastroEconomico, pessoaJuridica);
        }

        cadastroEconomico = adicionarSituacaoCadastralCadastro(cadastroEconomico, eventoRedeSimDTO, usuarioSistema, true);
        cadastroEconomico = atualizarDadosDoCadastro(eventoRedeSimDTO, cadastroEconomico, false, usuarioSistema);
        usuarioWebFacade.criarUsuarioWebParaCadastroEconomico(cadastroEconomico);
        return cadastroEconomico;
    }

    private void criarEnquadramentoFiscal(EventoRedeSimDTO eventoRedeSimDTO, CadastroEconomico cadastroEconomico) {
        EnquadramentoFiscal enquadramentoFiscal = new EnquadramentoFiscal();
        enquadramentoFiscal.setCadastroEconomico(cadastroEconomico);
        enquadramentoFiscal.setInicioVigencia(new Date());
        enquadramentoFiscal.setPorte(TipoPorte.getTipoPortePorPorteDTO(eventoRedeSimDTO.getPessoaDTO().getPorte()));
        enquadramentoFiscal.setTipoContribuinte(TipoContribuinte.PERMANENTE);
        enquadramentoFiscal.setRegimeTributario(eventoRedeSimDTO.getPessoaDTO().isOpcaoSimplesNacional() ?
            RegimeTributario.SIMPLES_NACIONAL : RegimeTributario.LUCRO_PRESUMIDO);
        enquadramentoFiscal.setRegimeEspecialTributacao(RegimeEspecialTributacao.PADRAO);
        enquadramentoFiscal.setTipoIssqn(eventoRedeSimDTO.getPessoaDTO().isOpcaoSimplesNacional() ? TipoIssqn.SIMPLES_NACIONAL
            : TipoIssqn.MENSAL);
        enquadramentoFiscal.setSubstitutoTributario(Boolean.FALSE);
        enquadramentoFiscal.setTipoNotaFiscalServico(TipoNotaFiscalServico.ELETRONICA);
        cadastroEconomico.getEnquadramentos().add(enquadramentoFiscal);
    }

    public CadastroEconomico atualizarDadosDoCadastro(EventoRedeSimDTO eventoRedeSimDTO,
                                                      CadastroEconomico cadastroEconomico,
                                                      boolean atualizarEnquadramentoFiscal,
                                                      UsuarioSistema usuarioSistema) {
        if (cadastroEconomico.getId() != null) {
            cadastroEconomico = em.find(CadastroEconomico.class, cadastroEconomico.getId());
        }

        PessoaJuridicaDTO pessoaJuridicaDTO = eventoRedeSimDTO.getPessoaDTO();
        if (pessoaJuridicaDTO.getDataUltimaAnaliseEndereco() != null) {
            cadastroEconomico.setUltimaAnaliseEndereco(pessoaJuridicaDTO.getDataUltimaAnaliseEndereco());
        }
        if (pessoaJuridicaDTO.getUltimaViabilidadeAnaliseEndereco() != null) {
            cadastroEconomico.setUltimaViabilidadeEndereco(pessoaJuridicaDTO.getUltimaViabilidadeAnaliseEndereco());
        }
        if (pessoaJuridicaDTO.getAreaTotalUtilizada() != null) {
            cadastroEconomico.getLocalizacao().setAreaUtilizacao(pessoaJuridicaDTO.getAreaTotalUtilizada().doubleValue());
        }
        if (pessoaJuridicaDTO.getUltimaViabilidadeVinculada() != null) {
            cadastroEconomico.setUltimaViabilidadeVinculada(pessoaJuridicaDTO.getUltimaViabilidadeVinculada());
        }
        if (pessoaJuridicaDTO.getAbertura() != null) {
            cadastroEconomico.setAbertura(pessoaJuridicaDTO.getAbertura());
        }
        if (pessoaJuridicaDTO.getNaturezaJuridica() != null) {
            cadastroEconomico.setNaturezaJuridica(naturezaJuridicaFacade.buscarNaturezaPorCodigo(pessoaJuridicaDTO.getNaturezaJuridica().getCodigo()));
        }
        if (atualizarEnquadramentoFiscal) {
            EnquadramentoFiscal enquadramentoVigente = cadastroEconomico.getEnquadramentoVigente();
            if (enquadramentoVigente == null) {
                enquadramentoVigente = new EnquadramentoFiscal();
            }
            if (enquadramentoVigente.getTipoContribuinte() == null) {
                enquadramentoVigente.setTipoContribuinte(TipoContribuinte.PERMANENTE);
            }
            if (enquadramentoVigente.getRegimeEspecialTributacao() == null) {
                enquadramentoVigente.setRegimeEspecialTributacao(RegimeEspecialTributacao.PADRAO);
            }
            if (pessoaJuridicaDTO.isOpcaoSimplesNacional()) {
                enquadramentoVigente.setRegimeTributario(RegimeTributario.SIMPLES_NACIONAL);
            } else {
                enquadramentoVigente.setRegimeTributario(RegimeTributario.LUCRO_PRESUMIDO);
            }
            if (enquadramentoVigente.getTipoIssqn() == null ||
                (TipoIssqn.SIMPLES_NACIONAL.equals(enquadramentoVigente.getTipoIssqn()) ||
                    TipoIssqn.MENSAL.equals(enquadramentoVigente.getTipoIssqn()))) {
                if (pessoaJuridicaDTO.isOpcaoSimplesNacional()) {
                    enquadramentoVigente.setTipoIssqn(TipoIssqn.SIMPLES_NACIONAL);
                } else {
                    enquadramentoVigente.setTipoIssqn(TipoIssqn.MENSAL);
                }
            }
            if (enquadramentoVigente.getTipoNotaFiscalServico() == null) {
                enquadramentoVigente.setTipoNotaFiscalServico(TipoNotaFiscalServico.ELETRONICA);
            }
            if (pessoaJuridicaDTO.getPorte() != null) {
                enquadramentoVigente.setPorte(TipoPorte.getTipoPortePorPorteDTO(pessoaJuridicaDTO.getPorte()));
            }
        }
        cadastroEconomico.setUltimaIntegracaoRedeSim(new Date());

        cadastroEconomicoFacade.atualizarLocalizacaoDoCadastroEconomicoComEnderecoPrincipalPessoa(cadastroEconomico,
            cadastroEconomico.getPessoa());
        cadastroEconomicoFacade.atualizarCnaesDoCadastroEconomicoComOsCnaesDaPessoaJuridica(cadastroEconomico,
            (PessoaJuridica) cadastroEconomico.getPessoa());
        cadastroEconomicoFacade.atualizarSociedadeDoCadastroEconomicoComSociedadeDaPessoaJuridica(cadastroEconomico,
            (PessoaJuridica) cadastroEconomico.getPessoa());

        cadastroEconomico = adicionarSituacaoCadastralCadastro(cadastroEconomico, eventoRedeSimDTO, usuarioSistema, false);
        return em.merge(cadastroEconomico);
    }

    private CadastroEconomico adicionarSituacaoCadastralCadastro(CadastroEconomico cadastroEconomico, EventoRedeSimDTO eventoRedeSimDTO, UsuarioSistema usuarioSistema, boolean salvarCadastro) {
        SituacaoCadastroEconomico situacao = new SituacaoCadastroEconomico();
        SituacaoCadastralRFB sitRFB = SituacaoCadastralRFB.getEnumByCodigo(eventoRedeSimDTO.getPessoaDTO().getCodigoSituacaoRFB());
        situacao.setSituacaoCadastral(SituacaoCadastralCadastroEconomico.getSituacaoCadastralBySituacaoCadastralRFB(sitRFB));

        if ((cadastroEconomico.getSituacaoAtual() == null && situacao.getSituacaoCadastral() != null) ||
            (situacao.getSituacaoCadastral() != null &&
                !situacao.getSituacaoCadastral().equals(cadastroEconomico.getSituacaoAtual().getSituacaoCadastral()))) {
            situacao.setDataAlteracao(new Date());
            situacao.setDataRegistro(situacao.getDataAlteracao());
            cadastroEconomico.getSituacaoCadastroEconomico().add(situacao);
            if (salvarCadastro) {
                cadastroEconomico = em.merge(cadastroEconomico);
            }
            criarCertidaoBaixaAtividade(cadastroEconomico, eventoRedeSimDTO.getPessoaDTO().getSituacaoCadastralOrgaoRegistro(), usuarioSistema);
        }
        return cadastroEconomico;
    }

    private void criarCertidaoBaixaAtividade(CadastroEconomico cadastroEconomico, String motivo, UsuarioSistema usuarioSistema) {
        CertidaoAtividadeBCE baixa = new CertidaoAtividadeBCE();
        baixa.setCadastroEconomico(cadastroEconomico);
        baixa.setDataCadastro(new Date());
        baixa.setOperador(usuarioSistema);
        baixa.setMotivo(!Strings.isNullOrEmpty(motivo) ? motivo : "INTEGRAÇÃO COM A REDESIM.");
        baixa.setSituacao(TipoCertidaoAtividadeBCE.getTipoPorSituacaoCadastral(cadastroEconomico.getSituacaoAtual().getSituacaoCadastral()));
        em.persist(baixa);
    }

    private PessoaHorarioFuncionamento criarPessoaHorarioFuncionamento(HorarioFuncionamento horarioFuncionamento, PessoaJuridica pessoa) {
        PessoaHorarioFuncionamento pessoaHorarioFuncionamento = new PessoaHorarioFuncionamento();
        pessoaHorarioFuncionamento.setPessoa(pessoa);
        pessoaHorarioFuncionamento.setHorarioFuncionamento(horarioFuncionamento);
        pessoaHorarioFuncionamento.setAtivo(true);
        return pessoaHorarioFuncionamento;
    }

    public static Comparator<HorarioFuncionamentoDTO> getComparatorHorarioFuncionamentoDTO() {
        return new Comparator<HorarioFuncionamentoDTO>() {
            @Override
            public int compare(HorarioFuncionamentoDTO um, HorarioFuncionamentoDTO dois) {
                Integer diaSemanaUm = um.getDiaSemana() != null ? um.getDiaSemana().getDiaDaSemana() : 0;
                Integer diaSemanaDois = dois.getDiaSemana() != null ? dois.getDiaSemana().getDiaDaSemana() : 0;
                return diaSemanaUm.compareTo(diaSemanaDois);
            }
        };
    }

    private String montarDescricaoHorarioFuncionamento(List<HorarioFuncionamentoDTO> horariosFuncionamentoDTO) {
        Collections.sort(horariosFuncionamentoDTO, getComparatorHorarioFuncionamentoDTO());

        String diaInicial = horariosFuncionamentoDTO.get(0).getDiaSemana().getDescricao();
        String horaInicialEntrada = horariosFuncionamentoDTO.get(0).getHorarioInicio();
        String horaInicialSaida = horariosFuncionamentoDTO.get(0).getHorarioFim();

        String diaFinal = horariosFuncionamentoDTO.get(horariosFuncionamentoDTO.size() - 1).getDiaSemana().getDescricao();
        String horaFinalEntrada = horariosFuncionamentoDTO.get(horariosFuncionamentoDTO.size() - 1).getHorarioInicio();
        String horaFinalSaida = horariosFuncionamentoDTO.get(horariosFuncionamentoDTO.size() - 1).getHorarioFim();

        return "De " + diaInicial + " das " + horaInicialEntrada + " às " + horaInicialSaida + " até " + diaFinal + " das " + horaFinalEntrada + " às " + horaFinalSaida;
    }

    private PessoaCNAE criarPessoaCnae(PessoaJuridica pessoaJuridica, PessoaCnaeDTO pessoaCnaeDTO) {
        PessoaCNAE pessoaCNAE = new PessoaCNAE();
        pessoaCNAE.setPessoa(pessoaJuridica);
        pessoaCNAE.setCnae(cnaeFacade.buscarCnaeAtivoPorCodigoAndGrauDeRisco(pessoaCnaeDTO.getCnae().getCodigo(),
            pessoaCnaeDTO.getCnae().getGrauDeRisco() != null ? pessoaCnaeDTO.getCnae().getGrauDeRisco().name() : null));
        pessoaCNAE.setTipo(EconomicoCNAE.TipoCnae.getTipoCnaePorTipoCnaeDTO(pessoaCnaeDTO.getTipoCnae()));
        pessoaCNAE.setExercidaNoLocal(pessoaCnaeDTO.getExercidaNoLocal());
        pessoaCNAE.setDataregistro(new Date());
        pessoaCNAE.setInicio(new Date());
        return pessoaCNAE;
    }

    private HorarioFuncionamento buscarOuCriarHorarioFuncionamento(List<HorarioFuncionamentoDTO> horariosFuncionamentoDTO) {
        if (horariosFuncionamentoDTO != null && !horariosFuncionamentoDTO.isEmpty()) {
            String descricao = montarDescricaoHorarioFuncionamento(horariosFuncionamentoDTO);
            HorarioFuncionamento horarioFuncionamento = horarioFuncionamentoFacade.buscarHorarioFuncionamentoPorItensAndDescricao(horariosFuncionamentoDTO,
                descricao);

            if (horarioFuncionamento == null) {
                horarioFuncionamento = new HorarioFuncionamento();
                horarioFuncionamento.setCodigo(horarioFuncionamentoFacade.retornaUltimoCodigoLong());
                horarioFuncionamento.setDescricao(descricao);

                for (HorarioFuncionamentoDTO horarioFuncionamentoDTO : horariosFuncionamentoDTO) {
                    HorarioFuncionamentoItem item = new HorarioFuncionamentoItem();
                    item.setHorarioFuncionamento(horarioFuncionamento);
                    item.setDiaEntrada(DiaSemana.getEnumByDTO(horarioFuncionamentoDTO.getDiaSemana()));
                    item.setDiaSaida(DiaSemana.getEnumByDTO(horarioFuncionamentoDTO.getDiaSemana()));
                    item.setHorarioEntrada(DataUtil.getDateParse(horarioFuncionamentoDTO.getHorarioInicio(), "HH:mm"));
                    item.setHorarioSaida(DataUtil.getDateParse(horarioFuncionamentoDTO.getHorarioFim(), "HH:mm"));

                    horarioFuncionamento.getItens().add(item);
                }
                horarioFuncionamento = em.merge(horarioFuncionamento);
            }
            return horarioFuncionamento;
        }
        return null;
    }

    private EconomicoCNAE criarEconomicoCnae(CadastroEconomico cadastroEconomico, PessoaCnaeDTO pessoaCnaeDTO, CNAE cnae) {
        EconomicoCNAE economicoCnae = new EconomicoCNAE();
        economicoCnae.setCadastroEconomico(cadastroEconomico);
        economicoCnae.setCnae(cnae);
        economicoCnae.setTipo(EconomicoCNAE.TipoCnae.getTipoCnaePorTipoCnaeDTO(pessoaCnaeDTO.getTipoCnae()));
        economicoCnae.setExercidaNoLocal(pessoaCnaeDTO.getExercidaNoLocal());
        economicoCnae.setDataregistro(new Date());
        economicoCnae.setInicio(new Date());
        return economicoCnae;
    }

    private EnderecoCorreio copiarEnderecoCorreioDTOParaEnderecoCorreio(EnderecoCorreioDTO enderecoCorreio, EnderecoCorreio endereco) {
        endereco.setCep(enderecoCorreio.getCep());
        endereco.setTipoLogradouro(TipoLogradouroEnderecoCorreio.buscarTipoLogradouroPelaDescricao(enderecoCorreio.getTipoLogradouro()));
        endereco.setLogradouro(enderecoCorreio.getLogradouro());
        endereco.setComplemento(enderecoCorreio.getComplemento());
        endereco.setBairro(enderecoCorreio.getBairro());
        endereco.setLocalidade(enderecoCorreio.getLocalidade());
        endereco.setUf(enderecoCorreio.getUf());
        endereco.setNumero(enderecoCorreio.getNumero());
        endereco.setTipoEndereco(TipoEndereco.getTipoEnderecoPorTipoEnderecoDTO(enderecoCorreio.getTipoEndereco()));
        return endereco;
    }

    public EventoRedeSim salvarEventoRedeSim(EventoRedeSim evento) {
        return em.merge(evento);
    }

    public void criarEventoRedeSim(EventoRedeSimDTO dto) throws JsonProcessingException {
        if (buscarEventoPorCodigoIdentificador(dto.getCodigoEvento(), dto.getIdentificador()) == null) {
            EventoRedeSim evento = new EventoRedeSim();
            evento.setVersao(dto.getVersao());
            evento.setCodigo(dto.getCodigoEvento());
            evento.setIdentificador(dto.getIdentificador());
            evento.setCnpj(Util.formatarCnpj(dto.getPessoaDTO().getCnpj()));
            PessoaJuridicaDTO pessoa = dto.getPessoaDTO();
            if (hasPessoaJuridicaPorCNPJ(pessoa.getCnpj())) {
                evento.setTipoEvento(EventoRedeSim.TipoEvento.ATUALIZACAO);
                if (cadastroEconomicoFacade.buscarCadastroEconomicoPorCnpjRedeSim(pessoa.getCnpj()) == null) {
                    Notificacao notificacao = new Notificacao();
                    notificacao.setDescricao("Nova Pessoa disponível para cadastro - " + pessoa.getNome() + "(" + Util.formatarCnpj(pessoa.getCnpj()) + ")");
                    notificacao.setGravidade(Notificacao.Gravidade.INFORMACAO);
                    notificacao.setLink("/tributario/cadastroeconomico/eventos-rede-sim/");
                    notificacao.setTipoNotificacao(TipoNotificacao.INTEGRACAO_REDESIM);
                    NotificacaoService.getService().notificar(notificacao);
                }
            } else {
                evento.setTipoEvento(EventoRedeSim.TipoEvento.INSERCAO);
            }
            if (pessoa.getNaturezaJuridica() != null) {
                NaturezaJuridica naturezaJuridica = naturezaJuridicaFacade.buscarNaturezaPorCodigo(pessoa.getNaturezaJuridica().getCodigo());
                if (naturezaJuridica != null) {
                    pessoa.getNaturezaJuridica().setDescricao(naturezaJuridica.getDescricao());
                }
            }
            evento.setDescricao(pessoa.getNome() + "(" + pessoa.getCnpj() + ")");
            evento.setSituacaoEmpresa(pessoa.getDescricaoSituacaoRFB());
            evento.setConteudo(mapper.writeValueAsString(pessoa));
            em.merge(evento);
        }
    }

    public List<EventoRedeSim> buscarEventosNaoProcessados(int first, int pageSize, Date dataInicial, Date dataFinal, EventoRedeSim.TipoEvento tipoEvento) {
        String sql = "select e.* from EventoRedeSim e where e.situacao = :situacao";
        if (dataInicial != null) {
            sql += " and trunc(e.dataOperacao) >= to_date(:dataInicial, 'dd/MM/yyyy') ";
        }
        if (dataFinal != null) {
            sql += " and trunc(e.dataOperacao) <= to_date(:dataFinal, 'dd/MM/yyyy') ";
        }
        if (tipoEvento != null) {
            sql += " and e.tipoEvento = :tipoEvento ";
        }
        Query q = em.createNativeQuery(sql, EventoRedeSim.class);
        q.setFirstResult(first);
        q.setMaxResults(pageSize);
        q.setParameter("situacao", EventoRedeSim.Situacao.AGUARDANDO.name());
        if (dataInicial != null) {
            q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial, "dd/MM/yyyy"));
        }
        if (dataFinal != null) {
            q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal, "dd/MM/yyyy"));
        }
        if (tipoEvento != null) {
            q.setParameter("tipoEvento", tipoEvento.name());
        }
        return q.getResultList();
    }

    public Integer contarEventosNaoProcessados(Date dataInicial, Date dataFinal, EventoRedeSim.TipoEvento tipoEvento) {
        String sql = "select count(e.id) from EventoRedeSim e where e.situacao = :situacao";
        if (dataInicial != null) {
            sql += " and trunc(e.dataOperacao) >= to_date(:dataInicial, 'dd/MM/yyyy') ";
        }
        if (dataFinal != null) {
            sql += " and trunc(e.dataOperacao) <= to_date(:dataFinal, 'dd/MM/yyyy') ";
        }
        if (tipoEvento != null) {
            sql += " and e.tipoEvento = :tipoEvento ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("situacao", EventoRedeSim.Situacao.AGUARDANDO.name());
        if (dataInicial != null) {
            q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial, "dd/MM/yyyy"));
        }
        if (dataFinal != null) {
            q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal, "dd/MM/yyyy"));
        }
        if (tipoEvento != null) {
            q.setParameter("tipoEvento", tipoEvento.name());
        }
        return ((Number) q.getSingleResult()).intValue();
    }

    public EventoRedeSim buscarEventoPorCodigoIdentificador(String codigo, String identificador) {
        Query query = em.createQuery("select e from EventoRedeSim e where e.identificador = :identificador and e.codigo = :codigo");
        query.setParameter("identificador", identificador);
        query.setParameter("codigo", codigo);
        query.setMaxResults(1);
        if (!query.getResultList().isEmpty()) {
            return (EventoRedeSim) query.getResultList().get(0);
        }
        return null;
    }

    public RestTemplate getRestTemplate() {
        try {
            TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            };

            SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();

            SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

            CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(csf)
                .build();

            HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();

            requestFactory.setHttpClient(httpClient);

            return new RestTemplate(requestFactory);

        } catch (Exception e) {
            e.printStackTrace();
            return new RestTemplate();
        }
    }

    private void validarCnpjSincronizacaoComRedeSim(String cnpj) {
        if (cadastroEconomicoFacade.getPessoaFacade().hasMaisDeUmaPessoaComCnpj(cnpj)) {
            ValidacaoException ve = new ValidacaoException();
            ve.adicionarMensagemDeOperacaoNaoPermitida("Existe mais de uma pessoa juridica cadastrada com esse mesmo CNPJ: " + cnpj + ", Realize a unificação dos cadastros antes de sincronizar");
            ve.lancarException();
        }
    }

    private boolean validarSincronizacaoComRedeSim(ConfiguracaoWebService configuracaoWs, boolean lancarException) {
        ValidacaoException ve = new ValidacaoException();
        if (configuracaoWs == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A sincronização precisa de uma configuração de conexão, confira nas configurações do tributário, aba WebServices se existe essa configuração");
        } else {
            if (Strings.isNullOrEmpty(configuracaoWs.getUrl())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Para conexão com a REDESIM é necessário informar a URL de conexão nas configuração do tributário, aba WebServices");
            }
            if (Strings.isNullOrEmpty(configuracaoWs.getUsuario())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Para conexão com a REDESIM é necessário informar o usuário de conexão nas configuração do tributário, aba WebServices");
            }
            if (Strings.isNullOrEmpty(configuracaoWs.getSenha())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Para conexão com a REDESIM é necessário informar a senha de conexão nas configuração do tributário, aba WebServices");
            }
            if (Strings.isNullOrEmpty(configuracaoWs.getDetalhe())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Para conexão com a REDESIM é necessário informar a URL do integrador nas configuração do tributário, aba WebServices, campo detalhes");
            }
        }
        if (lancarException) {
            ve.lancarException();
        }
        return !ve.temMensagens();
    }

    public void buscarEventosRedeSim() {
        ConfiguracaoWebService configuracaoWs = leitorWsConfig.getConfiguracaoPorTipoDaKeyCorrente(TipoWebService.REDESIM, cadastroEconomicoFacade.getSistemaFacade().getUsuarioBancoDeDados());

        validarSincronizacaoComRedeSim(configuracaoWs, true);

        String url = configuracaoWs.getDetalhe() + "/consultar-empresas?" +
            "usuario=" + configuracaoWs.getUsuario() +
            "&senha=" + configuracaoWs.getSenha();

        RestTemplate restTemplate = getRestTemplate();
        ResponseEntity<EventoRedeSimDTO[]> exchange = restTemplate.getForEntity(url, EventoRedeSimDTO[].class);
        List<EventoRedeSimDTO> eventos = Lists.newArrayList(exchange.getBody());

        for (EventoRedeSimDTO evento : eventos) {
            try {
                criarEventoRedeSim(evento);
                url = configuracaoWs.getDetalhe() + "/confirmar-recebimento?" +
                    "identificador=" + evento.getIdentificador() +
                    "&usuario=" + configuracaoWs.getUsuario() +
                    "&senha=" + configuracaoWs.getSenha();
                restTemplate.getForEntity(url, Void.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    public void confirmarResposta(EventoRedeSimDTO evento, PessoaJuridica pessoaJuridica, CadastroEconomico cadastroEconomico) {
        try {
            ConfiguracaoWebService configuracaoWs = leitorWsConfig
                .getConfiguracaoPorTipoDaKeyCorrente(TipoWebService.REDESIM, cadastroEconomicoFacade.getSistemaFacade().getUsuarioBancoDeDados());

            validarSincronizacaoComRedeSim(configuracaoWs, true);

            String url = configuracaoWs.getDetalhe() + "/confirmar-resposta";

            evento.setCnpj(StringUtil.retornaApenasNumeros(pessoaJuridica.getCnpj()));
            evento.setInscricaoCadastral(cadastroEconomico.getInscricaoCadastral());
            evento.setUsuario(configuracaoWs.getUsuario());
            evento.setSenha(configuracaoWs.getSenha());

            evento.setUrl(configuracaoWs.getUrl() + "/WSE013");

            RestTemplate restTemplate = getRestTemplate();
            restTemplate.postForEntity(url, evento, EventoRedeSimDTO.class);
        } catch (Exception ex) {
            logger.error("Não foi possível comunicar a resposta à RedeSim: {}", ex);
        }
    }

    public EventoRedeSimDTO buscarRedeSimPorCnpj(String cnpj, String usuarioBancoDados, boolean lancarException) {
        ConfiguracaoWebService configuracaoWs = leitorWsConfig
            .getConfiguracaoPorTipoDaKeyCorrente(TipoWebService.REDESIM, usuarioBancoDados);

        boolean validou = validarSincronizacaoComRedeSim(configuracaoWs, lancarException);
        if (validou) {
            validarCnpjSincronizacaoComRedeSim(cnpj);

            String url = getUrlConsultaRedeSim(cnpj, configuracaoWs);

            ResponseEntity<EventoRedeSimDTO> exchange = getRestTemplate().getForEntity(url, EventoRedeSimDTO.class);
            EventoRedeSimDTO eventoRedeSimDTO = exchange.getBody();
            if (eventoRedeSimDTO != null) {
                if (eventoRedeSimDTO.getPessoaDTO().getCnaes() != null) {
                    adicionarCnaesPessoaDTO(eventoRedeSimDTO);
                }
                if (eventoRedeSimDTO.getPessoaDTO().getHorariosFuncionamento() != null) {
                    HorarioFuncionamento horarioFuncionamento = buscarOuCriarHorarioFuncionamento(eventoRedeSimDTO.getPessoaDTO().getHorariosFuncionamento());
                    for (HorarioFuncionamentoDTO horarioFuncionamentoDTO : eventoRedeSimDTO.getPessoaDTO().getHorariosFuncionamento()) {
                        horarioFuncionamentoDTO.setCodigo(horarioFuncionamento.getCodigo());
                        horarioFuncionamentoDTO.setDescricao(horarioFuncionamento.getDescricao());
                    }

                }
                if (eventoRedeSimDTO.getPessoaDTO().getNaturezaJuridica() != null) {
                    NaturezaJuridica naturezaJuridica = naturezaJuridicaFacade.buscarNaturezaPorCodigo(eventoRedeSimDTO.getPessoaDTO().getNaturezaJuridica().getCodigo());
                    if (naturezaJuridica != null) {
                        eventoRedeSimDTO.getPessoaDTO().getNaturezaJuridica().setDescricao(naturezaJuridica.getDescricao());
                    }
                }
                return eventoRedeSimDTO;
            }
        }
        return null;
    }

    private void adicionarCnaesPessoaDTO(EventoRedeSimDTO eventoRedeSimDTO) {
        List<PessoaCnaeDTO> cnaesPessoaDto = Lists.newArrayList();
        for (PessoaCnaeDTO pcnae : eventoRedeSimDTO.getPessoaDTO().getCnaes()) {
            if (pcnae.getCnae().getId() != null) {
                cnaesPessoaDto.add(pcnae);
            } else {
                List<CNAE> cnaes = cadastroEconomicoFacade.getCnaeFacade().buscarCnaesAtivosPorCodigo(pcnae.getCnae().getCodigo());
                for (CNAE cnae : cnaes) {
                    PessoaCnaeDTO pessoaCnaeDTO = new PessoaCnaeDTO();
                    pessoaCnaeDTO.setTipoCnae(pcnae.getTipoCnae());
                    pessoaCnaeDTO.setExercidaNoLocal(pcnae.getExercidaNoLocal());

                    CnaeDTO cnaeDTO = new CnaeDTO();
                    cnaeDTO.setNome(cnae != null ? cnae.getDescricaoDetalhada() : pcnae.getCnae().getNome());
                    cnaeDTO.setCodigo(cnae != null ? cnae.getCodigoCnae() : "");
                    cnaeDTO.setGrauDeRisco((cnae != null && cnae.getGrauDeRisco() != null) ? GrauDeRiscoDTO.toGrauDeRisco(cnae.getGrauDeRisco()) : null);
                    cnaeDTO.setId(cnae != null ? cnae.getId() : null);
                    pessoaCnaeDTO.setCnae(cnaeDTO);
                    cnaesPessoaDto.add(pessoaCnaeDTO);
                }
            }
        }
        eventoRedeSimDTO.getPessoaDTO().setCnaes(cnaesPessoaDto);
    }

    private String getUrlConsultaRedeSim(String cnpj, ConfiguracaoWebService configuracaoWs) {
        return configuracaoWs.getDetalhe() + "/consultar-empresa-por-cnpj?cnpj=" + StringUtil.retornaApenasNumeros(cnpj) +
            "&url=" + configuracaoWs.getUrl() + "/WSE031" +
            "&usuario=" + configuracaoWs.getUsuario() +
            "&senha=" + configuracaoWs.getSenha();
    }

    private EventoRedeSimDTO criarEventoRedeSimDTO(EventoRedeSim evento) throws IOException {
        EventoRedeSimDTO dto = new EventoRedeSimDTO();
        dto.setCodigoEvento(evento.getCodigo());
        dto.setTipo(evento.getTipoEvento().getDescricao());
        dto.setIdentificador(evento.getIdentificador());
        dto.setPessoaDTO(mapper.readValue(evento.getConteudo(), PessoaJuridicaDTO.class));
        return dto;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private CadastroEconomico criarOrAtualizarCadastroEconomico(EventoRedeSimDTO dto,
                                                                PessoaJuridica pessoaJuridica,
                                                                UsuarioSistema usuarioSistema,
                                                                boolean criarCadastro,
                                                                boolean atualizarEnquadramentoFiscal) {
        CadastroEconomico cadastroEconomico = cadastroEconomicoFacade.buscarCadastroEconomicoPorCnpjRedeSim(pessoaJuridica.getCnpj());
        if (cadastroEconomico != null) {
            cadastroEconomico = cadastroEconomicoFacade.recuperar(cadastroEconomico.getId());
            return atualizarDadosDoCadastro(dto, cadastroEconomico, atualizarEnquadramentoFiscal, usuarioSistema);
        } else if (criarCadastro) {
            CadastroEconomico novoCadastro = criarDadosDoCadastro(dto, pessoaJuridica, usuarioSistema);
            confirmarResposta(dto, pessoaJuridica, novoCadastro);
            return novoCadastro;
        } else {
            return null;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public void processarEventoRedeSim(EventoRedeSim evento,
                                       UsuarioSistema usuarioSistema,
                                       String usuarioBancoDados,
                                       boolean criarNovoCadastro,
                                       boolean atualizarEnquadramentoFiscal) {
        try {
            EventoRedeSimDTO dto = criarEventoRedeSimDTO(evento);
            if (dto.getPessoaDTO().getCnaes() != null) {
                adicionarCnaesPessoaDTO(dto);
            }
            PessoaJuridica pessoaJuridica = criarOrAtualizarPessoaJuridica(dto,
                HistoricoAlteracaoRedeSim.SINCRONIZADOR_REDESIM,
                usuarioSistema, usuarioBancoDados);
            CadastroEconomico cadastroEconomico = criarOrAtualizarCadastroEconomico(dto, pessoaJuridica,
                usuarioSistema, criarNovoCadastro, atualizarEnquadramentoFiscal);
            if (cadastroEconomico != null) {
                evento.setInscricaoCadastral(cadastroEconomico.getInscricaoCadastral());
            }
            em.merge(evento);
        } catch (Exception e) {
            logger.error("Erro ao processar o evento da RedeSim {}", e);
        }
    }

    public void gerarBcmAndConfirmarResposta(EventoRedeSim eventoRedeSim) throws IOException {
        CadastroEconomico cadastroEconomico = cadastroEconomicoFacade.buscarCadastroEconomicoPorCnpjRedeSim(eventoRedeSim.getCnpj());
        if (cadastroEconomico != null && cadastroEconomico.getPessoa().isPessoaJuridica()) {
            EnquadramentoFiscal enquadramentoVigente = cadastroEconomico.getEnquadramentoVigente();
            if (enquadramentoVigente != null && cadastroEconomico.getEnquadramentoVigente().isMei()) {
                EventoRedeSimDTO dto = criarEventoRedeSimDTO(eventoRedeSim);
                dto.setPdfResultado(cadastroEconomicoFacade.gerarBcm(cadastroEconomico));
                confirmarResposta(dto, (PessoaJuridica) cadastroEconomico.getPessoa(), cadastroEconomico);
            }
        }
    }

    public void alterarParaProcessado(EventoRedeSim eventoRedeSim) {
        eventoRedeSim.setSituacao(EventoRedeSim.Situacao.PROCESSADO);
        em.merge(eventoRedeSim);
    }

    public EventoRedeSim registrarEventoRedeSim(PessoaJuridica pessoaJuridica,
                                                EventoRedeSimDTO eventoRedeSimDTO) {
        EventoRedeSim evento = new EventoRedeSim();
        evento.setCodigo(pessoaJuridica.getId().toString());
        evento.setVersao(eventoRedeSimDTO.getVersao());
        evento.setIdentificador("Sincronização em lote");
        evento.setCnpj(Util.formatarCnpj(eventoRedeSimDTO.getPessoaDTO().getCnpj()));
        evento.setTipoEvento(EventoRedeSim.TipoEvento.ATUALIZACAO);
        evento.setDescricao(pessoaJuridica.getNomeCpfCnpj());
        evento.setSituacao(EventoRedeSim.Situacao.AGUARDANDO);
        evento.setSituacaoEmpresa(eventoRedeSimDTO.getPessoaDTO().getDescricaoSituacaoRFB());
        try {
            evento.setConteudo(new ObjectMapper().writeValueAsString(eventoRedeSimDTO.getPessoaDTO()));
        } catch (JsonProcessingException e) {
            evento.setConteudo(null);
        }
        return salvarEventoRedeSim(evento);
    }
}
