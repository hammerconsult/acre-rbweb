update solicitacaoitbionlinedocumento set descricao = (select descricao from parametrositbidocumento where id = solicitacaoitbionlinedocumento.parametrositbidocumento_id)
