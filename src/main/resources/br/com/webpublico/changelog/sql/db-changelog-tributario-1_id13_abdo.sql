update parametrosfuncionarios set funcao = case funcao 
                                              when 'SUPORTE TÉCNICO' then 'SUPORTE_TECNICO'
                                              when 'CHEFE DA DIVISÃO DE ITBI' then 'DIRETOR_CHEFE_DEPARTAMENTO_TRIBUTO'
                                           end