UPDATE CONFIGURACAOTRIBUTARIO
SET
  USUARIOSISTEMA_ID = (
    SELECT US.ID FROM USUARIOSISTEMA US WHERE LOWER(US.LOGIN) LIKE LOWER('%mga%')
  ),
  UNIDADEORGANIZACIONAL_ID = (
    SELECT UO.ID FROM HIERARQUIAORGANIZACIONAL HO
      INNER JOIN UNIDADEORGANIZACIONAL UO ON UO.ID = HO.SUBORDINADA_ID
      INNER JOIN EXERCICIO EX ON EX.ID = HO.EXERCICIO_ID
    WHERE HO.INDICEDONO = 1
          AND HO.FIMVIGENCIA IS NULL
          AND HO.TIPOHIERARQUIAORGANIZACIONAL = 'ADMINISTRATIVA'
  ),
  ASSUNTO = 'Solicitação de Cadastro Municipal',
  CONTEUDO = 'Olá, seja bem vindo ao cadastro único do Sistema de Gestão Municipal de Rio Branco, com esse cadastro você terá acesso a todos as facilidades do município, através de uma senha única, seus dados serão analisados para a ativação de seu cadastro, para agilizar o processo você pode comparecer a um centro de atendimento com os seus documentos em mãos (CPF, RG, CNH).'
WHERE
  VIGENCIA = (
    SELECT MAX(CONF.VIGENCIA) FROM CONFIGURACAOTRIBUTARIO CONF
  )
