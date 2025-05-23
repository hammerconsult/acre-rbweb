package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.enums.rh.esocial.TipoGrupoCategoriaTrabalhador;
import br.com.webpublico.esocial.comunicacao.enums.TipoOperacaoESocial;
import br.com.webpublico.esocial.comunicacao.eventos.naoperiodicos.EventoS2300;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.CargoFacade;
import br.com.webpublico.negocios.EnderecoCorreioFacade;
import br.com.webpublico.negocios.FichaFinanceiraFPFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.math.BigDecimal;
import java.util.*;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

/**
 * Created by William on 08/11/2018.
 */
@Service(value = "s2300Service")
public class S2300Service {
    public static final int COD_IBGE_MUNIC_RIO_BRANCO = 1200401;
    public static final int DIRIGENTE_SINDICAL = 401;
    protected static final Logger logger = LoggerFactory.getLogger(S2300Service.class);
    @Autowired
    private ESocialService eSocialService;

    private EnderecoCorreioFacade enderecoCorreioFacade;

    private CargoFacade cargoFacade;

    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;


    @PostConstruct
    public void init() {
        try {
            enderecoCorreioFacade = (EnderecoCorreioFacade) new InitialContext().lookup("java:module/EnderecoCorreioFacade");
            fichaFinanceiraFPFacade = (FichaFinanceiraFPFacade) new InitialContext().lookup("java:module/FichaFinanceiraFPFacade");
            cargoFacade = (CargoFacade) new InitialContext().lookup("java:module/CargoFacade");
        } catch (NamingException e) {
            logger.error("Erro ao injetar Facades : ", e);
        }
    }

    public void enviarS2300(ConfiguracaoEmpregadorESocial config, PrestadorServicos prestadorServicos) {
        ValidacaoException val = new ValidacaoException();
        EventoS2300 s2300 = criarEventoS2300(config, prestadorServicos, val);
        logger.error("Antes de Enviar: " + s2300.getXml());
        if (!val.temMensagens()) {
            eSocialService.enviarEventoS2300(s2300);
        }
        val.lancarException();
    }

    private EventoS2300 criarEventoS2300(ConfiguracaoEmpregadorESocial config, PrestadorServicos prestador, ValidacaoException val) {
        if (prestador.getPrestador() instanceof PessoaJuridica) {
            val.adicionarMensagemDeOperacaoNaoRealizada("As informações referentes do prestador de serviço devem ser para Pessoa Física");
            val.lancarException();
        }
        EmpregadorESocial empregador = eSocialService.getEmpregadorPorCnpj(retornaApenasNumeros(config.getEntidade().getPessoaJuridica().getCnpj()));
        EventosESocialDTO.S2300 eventoS2300 = (EventosESocialDTO.S2300) eSocialService.getEventoS2300(empregador);
        eventoS2300.setIdentificadorWP(prestador.getId().toString());
        eventoS2300.setClasseWP(ClasseWP.PRESTADORSERVICOS);
        eventoS2300.setDescricao(prestador.getPrestador().toString());

        eventoS2300.setIdESocial(prestador.getId().toString());
        adicionarInformacoesTrabalhadorSemVinculo(eventoS2300, prestador, config, val);
        adicionarInformacoesNascimento(eventoS2300, prestador, val);
        adicionarInformacoesEndereco(eventoS2300, prestador, val);
        adicionarInformacoesBasicas(eventoS2300, prestador, val);
//        adicionarInformacoesTrabalhadorOptanteFGTS(eventoS2300, vinculoFP, config, val);
        adicionarInformacoesCargoFuncao(eventoS2300, prestador, config, val);

        return eventoS2300;
    }

    private void adicionarMatricula(EventosESocialDTO.S2300 eventoS2300, PrestadorServicos prestadorServicos) {
        if (prestadorServicos != null) {
            eventoS2300.setMatricula(prestadorServicos.getMatriculaESocial());
        }
    }

    private void adicionarInformacoesBasicas(EventosESocialDTO.S2300 eventoS2300, PrestadorServicos prestador, ValidacaoException val) {
        PessoaFisica pf = (PessoaFisica) prestador.getPrestador();
        String urlPessoa = "/pessoa/editar/" + pf.getId();
        if (pf.getCpf() == null) {
            val.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + pf + " não tem o CPF informado. " + urlPessoa);
        } else {
            eventoS2300.setCpfTrab(retornaApenasNumeros(prestador.getPrestador().getCpf_Cnpj()));
            adicionarMatricula(eventoS2300, prestador);
        }
        if (pf.getCarteiraDeTrabalho() == null) {
            val.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + pf + " não possui carteira de trabalho. " + urlPessoa);
        } else {
            eventoS2300.setNisTrab(pf.getCarteiraDeTrabalho().getPisPasep());
        }
        if (Strings.isNullOrEmpty(pf.getNome())) {
            val.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + pf + " não tem o nome informado. " + urlPessoa);
        } else {
            eventoS2300.setNmTrab(pf.getNome());
        }
        if (pf.getSexo() == null) {
            val.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + pf + " não tem informações cadastradas sobre o Sexo. " + urlPessoa);
        } else {
            eventoS2300.setSexo(pf.getSexo().getSigla());
        }
        if (pf.getRacaCor() == null) {
            val.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + pf + " não tem informações cadastradas sobre o Raça/Cor. " + urlPessoa);
        } else {
            Date dataLimite = DataUtil.getDateParse("21/04/2024");
            Set<Integer> racaCorPermitidas = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
            Integer racaCor = pf.getRacaCor().getCodigoEsocial();

            if (prestador.getInicioVigenciaContrato() != null) {
                if (!racaCorPermitidas.contains(racaCor) && prestador.getInicioVigenciaContrato().after(dataLimite)) {
                    val.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + pf + " possui um código de Raça/Cor inválido. Se data de Início de Vigência do contrato for igual ou posterior a 22/04/2024, " +
                        "não pode ser informado o valor [6]." +
                        "De acordo com as regras do eSocial, a partir de 22/04/2024, os valores permitidos são: " +
                        "1 - Branca, 2 - Preta, 3 - Parda, 4 - Amarela ou 5 - Indígena. " + urlPessoa);
                } else {
                    eventoS2300.setRacaCor(pf.getRacaCor().getCodigoEsocial());
                }
            }
        }
        if (pf.getEstadoCivil() != null) {
            eventoS2300.setEstCiv(pf.getEstadoCivil().getCodigoESocial());
        }
        if (pf.getNivelEscolaridade() == null) {
            val.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + pf + " não tem informações cadastradas sobre o Nível de Escolaridade. " + urlPessoa);
        } else {
            eventoS2300.setGrauInstr(pf.getNivelEscolaridade().getGrauEscolaridadeESocial().getCodigo());
        }

        if (prestador.getTipoOperacaoESocial() != null) {
            eventoS2300.setOperacao(TipoOperacaoESocial.valueOf(prestador.getTipoOperacaoESocial().name()));
        }
    }

    private void adicionarInformacoesNascimento(EventosESocialDTO.S2300 eventoS2300, PrestadorServicos prestador, ValidacaoException val) {
        PessoaFisica pf = (PessoaFisica) prestador.getPrestador();
        String urlPessoa = "/pessoa/editar/" + pf.getId();
        if (pf.getDataNascimento() == null) {
            val.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + pf + " não tem a data de nascimento  informada. " + urlPessoa);
        } else {
            eventoS2300.setDtNascto(pf.getDataNascimento());
        }
        if (pf.getNacionalidade() == null) {
            val.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + pf + " não tem a Nacionalidade informada. " + urlPessoa);
        } else {
            eventoS2300.setPaisNascto(Util.cortarString(pf.getNacionalidade().getPais().getCodigoBacen().toString(), 3));
            eventoS2300.setPaisNac(Util.cortarString(pf.getNacionalidade().getPais().getCodigoBacen().toString(), 3));
        }
    }


    private void adicionarInformacoesEndereco(EventosESocialDTO.S2300 eventoS2300, PrestadorServicos prestador, ValidacaoException ve) {
        PessoaFisica pf = (PessoaFisica) prestador.getPrestador();
        EnderecoCorreio enderecoPrincipal = pf.getEnderecoPrincipal();
        String urlPessoa = "/pessoa/editar/" + pf.getId();
        if (enderecoPrincipal == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + pf + " não possúi endereço. " + urlPessoa);
            return;
        }
        if (Strings.isNullOrEmpty(enderecoPrincipal.getCep()) || enderecoPrincipal.getCep().length() < 8) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("O cep da pessoa " + pf + " não é valido. " + urlPessoa);
            return;
        }
        eventoS2300.setTpLogradBr(enderecoPrincipal.getTipoLogradouro() != null ? enderecoPrincipal.getTipoLogradouro().name() : "R");
        if (Strings.isNullOrEmpty(enderecoPrincipal.getLogradouro())) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("O endereço da pessoa " + pf + " não possui descrição do logradouro. " + urlPessoa);
        } else {
            eventoS2300.setDscLogradBr(enderecoPrincipal.getLogradouro().trim());
        }
        eventoS2300.setNrLogradBr(enderecoPrincipal.getNumero() != null ? enderecoPrincipal.getNumero() : "S/N");
        String complemento = !Strings.isNullOrEmpty(enderecoPrincipal.getComplemento()) ? enderecoPrincipal.getComplemento() : null;
        if (!Util.isStringNulaOuVazia(complemento) && complemento.length() > 30) {
            complemento = complemento.substring(0, 30);
        }

        if (enderecoPrincipal.getNumero() != null && enderecoPrincipal.getNumero().length() > 10) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("O Numero do logradouro deve ser menor que 10 caracteres " + pf + " não é valido " + urlPessoa);
            return;
        }

        eventoS2300.setComplementoBr(complemento);
        eventoS2300.setBairroBr(!Strings.isNullOrEmpty(enderecoPrincipal.getBairro()) ? enderecoPrincipal.getBairro() : null);
        eventoS2300.setCepBr(retornaApenasNumeros(enderecoPrincipal.getCep()));

        if (enderecoPrincipal.getLocalidade() != null && enderecoPrincipal.getUf() != null) {
            BigDecimal codigoIBGECidade = enderecoCorreioFacade.getCodigoIBGECidade(enderecoPrincipal.getLocalidade(), enderecoPrincipal.getUf());
            if (codigoIBGECidade != null) {
                eventoS2300.setCodMunicBr(Integer.parseInt(codigoIBGECidade.toString()));
            } else {
                eventoS2300.setCodMunicBr(COD_IBGE_MUNIC_RIO_BRANCO);
            }
            eventoS2300.setUfBr(enderecoPrincipal.getUf().trim());
        } else {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Verifique a Localidade e UF do endereço da pessoa " + pf + " " + urlPessoa);
        }
    }

    private void adicionarInformacoesTrabalhadorSemVinculo(EventosESocialDTO.S2300 eventoS2300, PrestadorServicos prestador, ConfiguracaoEmpregadorESocial config, ValidacaoException ve) {
        if (config.getDataInicioObrigatoriedadeFase2() == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Não foi informado a data de início da obrigatoriedade da 2º fase do e-social'");
        }
        if (prestador.getInicioVigenciaContrato() == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Não foi informado o Início de Vigência do Prestador de Serviço. " + prestador.getPrestador());
        } else {
            eventoS2300.setDtInicio(prestador.getInicioVigenciaContrato());

            eventoS2300.setCadIni(definirCadIni(prestador, config));
        }
        if (prestador.getCategoriaTrabalhador() == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Não foi informado a categoria do prestador " + prestador.getPrestador());
        } else {
            eventoS2300.setCodCateg(prestador.getCategoriaTrabalhador().getCodigo());
        }

        if (prestador.getCategoriaTrabalhador() != null && (TipoGrupoCategoriaTrabalhador.AVULSO.equals(prestador.getCategoriaTrabalhador().getTipo()) ||
            isCooperado(prestador) || prestador.getCategoriaTrabalhador().getCodigo().equals(DIRIGENTE_SINDICAL))) {
            eventoS2300.setNatAtividade(1); //TODO empregado de trabalho urbano
        }
    }

    private boolean definirCadIni(PrestadorServicos prestador, ConfiguracaoEmpregadorESocial configuracao) {
        if (prestador.getCadastroInicialEsocial() != null) {
            return VinculoFP.TipoCadastroInicialVinculoFP.SIM.equals(prestador.getCadastroInicialEsocial());
        } else {
            return prestador.getInicioVigenciaContrato().before(configuracao.getDataInicioObrigatoriedadeFase2());
        }
    }

    private void adicionarInformacoesCargoFuncao(EventosESocialDTO.S2300 eventoS2300, PrestadorServicos
        prestador, ConfiguracaoEmpregadorESocial config, ValidacaoException ve) {
        if (prestador.getCargo() != null) {
            eventoS2300.setNmCargo(prestador.getCargo().getCodigoDoCargo());
            Cargo cargo = cargoFacade.recuperar(prestador.getCargo().getId());
            if (!cargo.getCbos().isEmpty()) {
                eventoS2300.setCboCargo(cargo.getCbos().get(0).getCodigo().toString());
            }
        }

        if (prestador.getValorParcelaFixa() == null || prestador.getValorParcelaFixa().compareTo(BigDecimal.ZERO) == 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para a Unidade de Pagamento '" + prestador.getUnidadePagamento().getDescricao() + "' deve ser informado o Valor da Parcela Fixa da Remuneração e deve ser maior que Zero");
        }
        eventoS2300.setVrSalFx(prestador.getValorParcelaFixa());
        eventoS2300.setUndSalFixo(prestador.getUnidadePagamento().getCodigo());
    }

    private boolean isCooperado(PrestadorServicos prestador) {
        for (Integer integer : getCategoriaCooperado()) {
            if (Objects.equals(prestador.getCategoriaTrabalhador().getCodigo(), integer)) {
                return true;
            }
        }
        return false;
    }

    private List<Integer> getCategoriaCooperado() {
        List<Integer> cooperados = Lists.newArrayList();
        cooperados.add(761);
        cooperados.add(701);
        cooperados.add(738);
        cooperados.add(731);
        return cooperados;
    }
}
