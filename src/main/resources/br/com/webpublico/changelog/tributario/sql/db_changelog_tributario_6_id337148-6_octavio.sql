merge into enderecoalvara endereco_antigo
using (select ea.id as id_endereco, vw.logradouro, vw.bairro, vw.cep, vw.complemento
       from alvara
       inner join enderecoalvara ea on alvara.enderecoalvara_id = ea.id
       inner join cadastroeconomico cad on alvara.cadastroeconomico_id = cad.id
       inner join pessoa on cad.pessoa_id = pessoa.id
       inner join vwenderecopessoa vw on pessoa.id = vw.pessoa_id
       inner join ce_situacaocadastral cmcsit on cad.id = cmcsit.cadastroeconomico_id
       inner join situacaocadastroeconomico sit on cmcsit.situacaocadastroeconomico_id = sit.id
       left join processocalculoalvara pca on alvara.id = pca.alvara_id
       left join calculoalvaralocalizacao cal on alvara.id = cal.alvara_id
       left join calculoalvarafunc caf on alvara.id = caf.alvara_id
       left join calculoalvarasanitario cas on alvara.id = cas.alvara_id
       left join processocalculo proc on proc.id = coalesce(cal.processocalculoalvaraloc_id, caf.processocalculo_id,
                                                            cas.processocalculoalvarasan_id)
       where alvara.id = (select al.id from alvara al
                          where al.cadastroeconomico_id = alvara.cadastroeconomico_id
                          order by al.id desc fetch first 1 rows only)
       and coalesce(pca.situacaocalculoalvara, cal.situacaocalculoalvara,
                    caf.situacaocalculoalvara, cas.situacaocalculoalvara) <> 'ESTORNADO'
       and sit.id = (select sit2.id from ce_situacaocadastral cmcsit2
                     inner join situacaocadastroeconomico sit2 on cmcsit2.situacaocadastroeconomico_id = sit2.id
                     where cmcsit2.cadastroeconomico_id = cad.id
                     order by sit2.id desc fetch first 1 rows only)
       and sit.situacaocadastral = 'ATIVO'
       and pessoa.situacaocadastralpessoa = 'ATIVO'
       and exists(select lb.id from logradourobairro lb
                  inner join enderecoalvara ea2 on lb.id = ea2.logradourobairro_id
                  inner join logradouro l on lb.logradouro_id = l.id
                  inner join bairro b on lb.bairro_id = b.id
                  where ea2.id = ea.id and lb.id = ea.logradourobairro_id
                  and coalesce(lower(trim(l.nome)), '') = coalesce(lower(trim(vw.logradouro)), '')
                  and coalesce(lower(trim(b.descricao)), '') = coalesce(lower(trim(vw.bairro)), '')
                  and coalesce(lower(trim(ea.numero)), '') = coalesce(lower(trim(vw.numero)), '')
                  and coalesce(lower(trim(lb.cep)), '') = coalesce(lower(trim(vw.cep)), ''))) endereco_novo
on (endereco_antigo.id = endereco_novo.id_endereco)
when matched then update set
endereco_antigo.logradouro = endereco_novo.logradouro,
endereco_antigo.bairro = endereco_novo.bairro,
endereco_antigo.cep = endereco_novo.cep,
endereco_antigo.complemento = endereco_novo.complemento,
endereco_antigo.logradourobairro_id = null

