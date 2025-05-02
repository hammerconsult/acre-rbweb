update modelodoctooficial set conteudo = replace(conteudo, '$CPF_CNPJ', '$CPF_CNPJ_PROPRIETARIO')
where id in (select m.id from modelodoctooficial m
                                  inner join tipodoctooficial t on t.id = m.tipodoctooficial_id
             where m.conteudo like '%$CPF_CNPJ%'
               and t.tipocadastrodoctooficial = 'CADASTROIMOBILIARIO')
