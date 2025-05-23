/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import br.com.webpublico.entidades.EnderecoCorreio;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.PessoaJuridica;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.Date;

/**
 * @author andre (créu)
 */
public class ValidaPessoa {

    private static final Logger logger = LoggerFactory.getLogger(ValidaPessoa.class);

    public static void validar(Pessoa pessoa, PerfilEnum p, ValidacaoException ve) {
        if (PerfilEnum.PERFIL_RH.equals(p)) {
            validarPerfilRH(pessoa, ve);
        } else if (PerfilEnum.PERFIL_DEPENDENTE.equals(p)) {
            validarPerfilDependente(pessoa, ve);
        } else if (PerfilEnum.PERFIL_PENSIONISTA.equals(p)) {
            validarPerfilPensionista(pessoa, ve);
        } else if (PerfilEnum.PERFIL_CREDOR.equals(p)) {
            validarPerfilCredor(pessoa, ve);
        } else if (PerfilEnum.PERFIL_PRESTADOR.equals(p)) {
            validarPerfilPrestador(pessoa, ve);
        }
    }

    @Deprecated
    public static boolean valida(Pessoa pessoa, PerfilEnum p) {
        boolean valida = true;

        if (PerfilEnum.PERFIL_ADMINISTRATIVO.equals(p)) {
            valida = valida && validaPerfilAdministrativo(pessoa);
        } else if (PerfilEnum.PERFIL_CREDOR.equals(p)) {
            valida = valida && validaPerfilCredor(pessoa);
        } else if (PerfilEnum.PERFIL_ESPECIAL.equals(p)) {
            valida = valida && validaPerfilEspecial(pessoa);
        } else if (PerfilEnum.PERFIL_TRIBUTARIO.equals(p)) {
            valida = valida && validaPerfilTributario(pessoa);
        }

        return valida;
    }

    public static void validarPerfilRH(Pessoa pessoa, ValidacaoException ve) {
        if (pessoa instanceof PessoaFisica) {
            PessoaFisica pf = (PessoaFisica) pessoa;

            if (pf.getNome().trim().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Nome deve ser informado!");
            }
            if (pf.getDataNascimento() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Nascimento deve ser informado!");
            }
            if (pf.getCpf().trim().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo CPF deve ser informado!");
            }
            if (pf.getSexo() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Sexo deve ser informado!");
            }
            if (pf.getMae().trim().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Mãe deve ser informado!");
            }
            if (pf.getPai().trim().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Pai deve ser informado!");
            }
            if (pf.getRacaCor() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Selecione uma opção de Raça/Cor.");
            }
            if (pf.getEstadoCivil() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Selecione uma opção de Estado Civil.");
            }
            if (pf.getTipoDeficiencia() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Deficiência deve ser informado!");
            } else if (!TipoDeficiencia.NAO_PORTADOR.equals(pf.getTipoDeficiencia()) &&
                (pf.getPessoaFisicaCid() == null || pf.getPessoaFisicaCid().getCid() == null)) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo CID deve ser informado!");
            }
            if (pf.getRg() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O RG deve ser informado!");
            }
            if (pf.getCertidaoNascimento() == null && EstadoCivil.SOLTEIRO.equals(pf.getEstadoCivil())) {
                ve.adicionarMensagemDeCampoObrigatorio("A Certidão de Nascimento deve ser informada!");
            }
            if (pf.getCarteiraDeTrabalho() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("A Carteira de trabalho deve ser informada!");
            }
            if (pf.getTelefones().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("É Obrigatório adicionar pelo menos um telefone!");
            }
            if (pf.getContaCorrenteBancPessoas().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("É Obrigatório adicionar pelo menos uma conta bancária!");
            }
        }
    }

    public static void validarPerfilPrestador(Pessoa pessoa, ValidacaoException ve) {
        if (pessoa instanceof PessoaFisica) {
            PessoaFisica pf = (PessoaFisica) pessoa;

            if (pf.getCpf().trim().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo CPF deve ser informado!");
            }
            if (pf.getNome().trim().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Nome deve ser informado!");
            }
            if (pf.getSexo() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Sexo deve ser informado!");
            }
            if (pf.getRacaCor() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Selecione uma opção de Raça/Cor.");
            }
            if (pf.getEstadoCivil() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Selecione uma opção de Estado Civil.");
            }
            if (pf.getNivelEscolaridade() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Selecione uma opção de Nível de Escolaridade.");
            }
            if (pf.getDataNascimento() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Nascimento deve ser informado!");
            }
            if (pf.getNaturalidade() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Naturalidade deve ser informado!");
            }
            if (pf.getNacionalidade() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Nacionalidade deve ser informado!");
            }
            if (pf.getEnderecos().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("O endereço deve ser informado.");
            }
            if (pf.getTipoDeficiencia() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Deficiência deve ser informado!");
            } else if (!TipoDeficiencia.NAO_PORTADOR.equals(pf.getTipoDeficiencia()) &&
                (pf.getPessoaFisicaCid() == null || pf.getPessoaFisicaCid().getCid() == null)) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo CID deve ser informado!");
            }
            if (pf.getDataNascimento() != null && isMenorDe18Anos(pf) && pf.getEmancipacao() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A pessoa que você está cadastrando ainda não tem 18 anos e não foi informado o documento de emancipação. Se você realmente quer cadastrar uma pessoa com menos de 18 anos sem documento de emancipação por favor vá até o cadastro de Pessoa (Perfil Dependente). Caso esta pessoa tenha 18 anos ou mais, verifique o campo Data de Nascimento.");
            }
        }
    }

    public static boolean isMenorDe18Anos(PessoaFisica p) {
        if (p.getDataNascimento() != null) {
            DateTime dtNascimento = new DateTime(p.getDataNascimento());
            DateTime dataAtual = new DateTime(new Date());
            Period period = new Period(dtNascimento, dataAtual);
            period.getYears();
            return period.getYears() < 18;
        } else {
            return false;
        }
    }

    public static boolean isMaiorDe8Anos(PessoaFisica p) {
        if (p.getDataNascimento() != null) {
            DateTime dtNascimento = new DateTime(p.getDataNascimento());
            DateTime dataAtual = new DateTime(new Date());
            Period period = new Period(dtNascimento, dataAtual);
            period.getYears();
            return period.getYears() >= 8;
        } else {
            return false;
        }
    }

    public static void validarPerfilDependente(Pessoa pessoa, ValidacaoException ve) {
        PessoaFisica pf = (PessoaFisica) pessoa;
        if (pf.getNome().equals("")) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Nome deve ser informado !");
        }
        if (pf.getSexo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Sexo deve ser informado !");
        }
        if (pf.getDataNascimento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Nascimento deve ser informado!");
        }
        if (pf.getTipoDeficiencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Deficiência deve ser informado!");
        } else if (!TipoDeficiencia.NAO_PORTADOR.equals(pf.getTipoDeficiencia()) &&
            (pf.getPessoaFisicaCid() == null || pf.getPessoaFisicaCid().getCid() == null)) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo CID deve ser informado!");
        }
    }

    public static void validarPerfilPensionista(Pessoa pessoa, ValidacaoException ve) {
        if (pessoa instanceof PessoaFisica) {
            PessoaFisica pf = (PessoaFisica) pessoa;

            if (pf.getNome().equals("")) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Nome deve ser informado !");
            }
            if (pf.getCpf().equals("")) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo CPF deve ser informado !");
            }
            if (pf.getTipoDeficiencia() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Deficiência deve ser informado!");
            } else if (!TipoDeficiencia.NAO_PORTADOR.equals(pf.getTipoDeficiencia()) &&
                (pf.getPessoaFisicaCid() == null || pf.getPessoaFisicaCid().getCid() == null)) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo CID deve ser informado!");
            }
        }
    }

    private static void validarPerfilCredor(Pessoa pessoa, ValidacaoException ve) {
        if (pessoa instanceof PessoaFisica) {
            PessoaFisica pf = (PessoaFisica) pessoa;
            if (pf.getNome() == null || "".equals(pf.getNome().trim())) {
                ve.adicionarMensagemDeCampoObrigatorio(" O campo Nome deve ser informado!");
            }
            if (pf.getDataNascimento() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Nascimento deve ser informado!");
            }
            if ("".equals(pf.getCpf())) {
                ve.adicionarMensagemDeCampoObrigatorio(" O campo CPF deve ser informado!");
            }
            if (pf.getClasseCredorPessoas().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe uma Classe de Pessoa!");
            }
        } else if (pessoa instanceof PessoaJuridica) {
            PessoaJuridica pj = (PessoaJuridica) pessoa;
            if ("".equals(pj.getRazaoSocial())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Razão Social deve ser informado!");
            }
            if ("".equals(pj.getNomeFantasia())) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Nome Fantasia deve ser informado!");
            }
            if (pj.getTipoInscricao().equals(TipoInscricao.CNPJ)) {
                if ("".equals(pj.getCnpj())) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo CNPJ deve ser informado!");
                }
            } else {
                if ("".equals(pj.getCei())) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo CEI deve ser informado!");
                }
            }
            if (pj.getClasseCredorPessoas().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe uma Classe de Pessoa!");
            }
        }
    }

    public static boolean validaPerfilAdministrativo(Pessoa pessoa) {
        boolean valida = true;

        return valida;
    }

    public static boolean validaPerfilCredor(Pessoa pessoa) {
        boolean valida = true;

        if (pessoa instanceof PessoaFisica) {
            PessoaFisica pf = (PessoaFisica) pessoa;
            if (pf.getNome() == null || pf.getNome().trim().equals("")) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo Nome deve ser informado!");
                valida = false;
            }

            if (pf.getDataNascimento() == null) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "O campo Data de Nascimento deve ser informado!");
                valida = false;
            }

            if (pf.getCpf().equals("")) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), " O campo CPF deve ser informado!");
                valida = false;
            }

//            if (pf.getUnidadeOrganizacional() == null) {
//                FacesUtil.addMessageError("iSecretariaRequerente", "", "O campo Secretaria / Requerente deve ser informado !");
//                valida = false;
//            }

//            if (pf.getBloqueado().equals(true)) {
//                FacesUtil.addError("", "O campo Motivo do Bloqueio deve ser informado !");
//                valida = false;
//            }

            if (pf.getClasseCredorPessoas().isEmpty()) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Informe uma Classe de Pessoa!");
                valida = false;
            }

//            if (pf.getRg().getNumero() == null || pf.getRg().getNumero().trim().isEmpty()) {
//                FacesUtil.addError("", "O campo Número do RG deve ser informado !");
//                valida = false;
//            }

//            if (pf.getRg().getDataemissao() == null || pf.getRg().getDataemissao().equals("")) {
//                FacesUtil.addError("", "O campo Data Emissão do RG deve ser informado !");
//                valida = false;
//            }
//
//            if (pf.getRg().getOrgaoEmissao() == null || pf.getRg().getOrgaoEmissao().trim().isEmpty()) {
//                FacesUtil.addError("", "O campo Órgão Emissor do RG deve ser informado !");
//                valida = false;
//            }

//            if (pf.getCertidaoNascimento().getNomeCartorio().isEmpty()) {
//                FacesUtil.addMessageError("iNomeCartorioNascimento", "", "O campo Nome do Cartório deve ser informado !");
//                valida = false;
//            }

//            if (pf.getCertidaoNascimento().getNumeroLivro().isEmpty()) {
//                FacesUtil.addMessageError("iLivroNascimento", "", "O campo Número do Livro deve ser informado !");
//                valida = false;
//            }
//
//            if (pf.getCertidaoNascimento().getNumeroFolha().isEmpty()) {
//                FacesUtil.addMessageError("iFolhaNascimento", "", "O campo Número da Folha deve ser informado !");
//                valida = false;
//            }
//
//            if (pf.getCertidaoNascimento().getNumeroRegistro() == null) {
//                FacesUtil.addMessageError("iRegistroNascimento", "", "O campo Número do Registro deve ser informado !");
//                valida = false;
//            }

        } else if (pessoa instanceof PessoaJuridica) {
            PessoaJuridica pj = (PessoaJuridica) pessoa;
            if (pj.getRazaoSocial().equals("")) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "O campo Razão Social deve ser informado!");
                valida = false;
            }

            if (pj.getNomeFantasia().equals("")) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "O campo Nome Fantasia deve ser informado!");
                valida = false;
            }
            if (pj.getTipoInscricao().equals(TipoInscricao.CNPJ)) {
                if (pj.getCnpj().equals("")) {
                    FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "O campo CNPJ deve ser informado!");
                    valida = false;
                }
            } else {
                if (pj.getCei().equals("")) {
                    FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "O campo CEI deve ser informado!");
                    valida = false;
                }
            }

            if (pj.getClasseCredorPessoas().isEmpty()) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Informe uma Classe de Pessoa!");
                valida = false;
            }
        }
        return valida;
    }

    public static boolean validaPerfilEspecial(Pessoa pessoa) {
        boolean valida = true;

        if (pessoa instanceof PessoaFisica) {
            PessoaFisica pf = (PessoaFisica) pessoa;
            if (pf.getNome().equals("")) {
                FacesUtil.addCampoObrigatorio("O campo Nome deve ser informado !");
                valida = false;
            }
            if (pf.getCpf().equals("")) {
                FacesUtil.addCampoObrigatorio("O campo CPF deve ser informado !");
                valida = false;
            }
            if (pf.getUnidadeExterna() == null) {
                FacesUtil.addCampoObrigatorio("O campo Unidade Externa deve ser informado !");
                valida = false;
            }
        }
        return valida;
    }

    @Deprecated
    public static boolean validaPerfilTributario(Pessoa pessoa) {
        boolean valida = true;

        if (pessoa instanceof PessoaFisica) {
            PessoaFisica pf = (PessoaFisica) pessoa;
            if (pf.getNome().length() < 3) {
                valida = false;
                FacesUtil.addCampoObrigatorio("Nome menor que 3 caracteres. ");
            }
            if (pf.getDataNascimento() == null || pf.getDataNascimento().toString().isEmpty()) {
                FacesUtil.addCampoObrigatorio("Data de nascimento deve ser informado.");
                valida = false;

            }
            if (pf.getCpf() == null || pf.getCpf().isEmpty()) {
                FacesUtil.addCampoObrigatorio("CPF deve ser informado.");
                valida = false;
            }
            if (pf.getMae() == null || pf.getMae().isEmpty()) {
                FacesUtil.addCampoObrigatorio("Nome da mãe deve ser informado.");
                valida = false;
            }
            if (pf.getRg().getNumero() == null || pf.getRg().getNumero().trim().isEmpty()) {
                valida = false;
                FacesUtil.addCampoObrigatorio("Número do RG deve ser informado.");
            }
            if (pf.getRg().getOrgaoEmissao() == null || pf.getRg().getOrgaoEmissao().trim().isEmpty()) {
                valida = false;
                FacesUtil.addCampoObrigatorio("Órgão Emissor deve ser informado.");
            }
            if (pf.getEnderecos().isEmpty()) {
                valida = false;
                FacesUtil.addCampoObrigatorio("Endereço deve ser informado.");
            }


            if (pf.getEmail() == null || (!pf.getEmail().contains("@") && !pf.getEmail().trim().isEmpty())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "e-mail inválido", "e-mail inválido"));
                valida = false;
            }
            if (!pf.getHomePage().trim().isEmpty() && !pf.getHomePage().contains("www.")) {
                valida = false;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Home Page inválida", "Home Page inválida"));
            }


            if (pf.getRg() != null) {
                if (pf.getRg().getDataemissao() != null) {
                    if (pf.getRg().getDataemissao().after(new Date())) {
                        valida = false;
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção", "A Data Emissão do RG deve ser menor que a data atual!"));
                    }
                }
            }

        } else {
            PessoaJuridica pj = (PessoaJuridica) pessoa;
            if (pj.getRazaoSocial().trim().isEmpty()) {
                valida = false;
                FacesContext.getCurrentInstance().addMessage("iRazaoSocial", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção! ", "A Razão Social deve ser informada!"));
            }
            if (pj.getCnpj() == null || pj.getCnpj().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage("iCnpj", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção! ", "O CNPJ deve ser informado."));
                valida = false;
            }
        }
        return valida;
    }

    public static void validarPerfilTributario(Pessoa pessoa) {
        ValidacaoException ve = new ValidacaoException();
        if (!Util.valida_CpfCnpj(String.valueOf(pessoa.getCpf_Cnpj()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("CPF/CNPJ inválido!");
        } else {
            if (pessoa instanceof PessoaFisica) {
                PessoaFisica pf = (PessoaFisica) pessoa;
                if (pf.getNome().length() < 3) {
                    ve.adicionarMensagemDeCampoObrigatorio("Nome menor que 3 caracteres. ");
                }
                if (pf.getDataNascimento() == null || pf.getDataNascimento().toString().isEmpty()) {
                    ve.adicionarMensagemDeCampoObrigatorio("Data de nascimento deve ser informado.");
                }
                if (pf.getCpf() == null || pf.getCpf().isEmpty()) {
                    ve.adicionarMensagemDeCampoObrigatorio("CPF deve ser informado.");
                }
                if (pf.getMae() == null || pf.getMae().isEmpty()) {
                    ve.adicionarMensagemDeCampoObrigatorio("Nome da mãe deve ser informado.");
                }
                if (pf.getEnderecos().isEmpty()) {
                    ve.adicionarMensagemDeCampoObrigatorio("Endereço deve ser informado.");
                } else {
                    if (pessoa.getEnderecos().stream().noneMatch(e -> TipoEndereco.DOMICILIO_FISCAL.equals(e.getTipoEndereco()))) {
                        ve.adicionarMensagemDeCampoObrigatorio("É obrigatório ter pelo menos um endereço do tipo Domicílio Fiscal.");
                    }
                    for (EnderecoCorreio endereco : pf.getEnderecos()) {
                        validarEnderecosJaAdicionados(ve, endereco);
                    }
                }
                if (pf.getEmail() == null || pf.getEmail().isEmpty()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Informe o email.");
                } else {
                    if (!pf.getEmail().contains("@")) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("O e-mail informado é inválido.");
                    }
                }
                if (pf.getHomePage() != null && !pf.getHomePage().trim().isEmpty() && !pf.getHomePage().toLowerCase().contains("www.")) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A Home Page informada é inválida.");
                }
                if (pf.getRg() != null
                    && pf.getRg().getDataemissao() != null
                    && pf.getRg().getDataemissao().after(new Date())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A Data Emissão do RG deve ser menor que a data atual.");
                }
                if (pessoa.getTelefones() == null || pessoa.getTelefones().isEmpty()) {
                    ve.adicionarMensagemDeCampoObrigatorio("Informe pelo menos um telefone.");
                }
            } else {
                PessoaJuridica pj = (PessoaJuridica) pessoa;
                if (pj.getRazaoSocial().trim().isEmpty()) {
                    ve.adicionarMensagemDeCampoObrigatorio("A Razão Social deve ser informada.");
                }
                if (pj.getCnpj() == null || pj.getCnpj().isEmpty()) {
                    ve.adicionarMensagemDeCampoObrigatorio("O CNPJ deve ser informado.");
                }
            }
        }
        ve.lancarException();
    }

    private static void validarEnderecosJaAdicionados(ValidacaoException ve, EnderecoCorreio endereco) {
        TipoEndereco tipoEndereco = endereco.getTipoEndereco();
        if (endereco.getCep() == null || endereco.getCep().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio(montarMensagemCampoEnderecoObrigatorio(tipoEndereco, "CEP"));
        }
        if (endereco.getLocalidade() == null || endereco.getLocalidade().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio(montarMensagemCampoEnderecoObrigatorio(tipoEndereco, "cidade"));
        }
        if (endereco.getBairro() == null || endereco.getBairro().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio(montarMensagemCampoEnderecoObrigatorio(tipoEndereco, "bairro"));
        }
        if (endereco.getLogradouro() == null || endereco.getLogradouro().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio(montarMensagemCampoEnderecoObrigatorio(tipoEndereco, "logradouro"));
        }
        if (endereco.getNumero() == null || endereco.getNumero().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio(montarMensagemCampoEnderecoObrigatorio(tipoEndereco, "número"));
        }
    }

    private static String montarMensagemCampoEnderecoObrigatorio(TipoEndereco tipoEndereco, String campo) {
        return "O campo " + campo + " do tipo de endereço " + tipoEndereco + " é obrigatório, edite o endereço e adicione o campo.";
    }
}
