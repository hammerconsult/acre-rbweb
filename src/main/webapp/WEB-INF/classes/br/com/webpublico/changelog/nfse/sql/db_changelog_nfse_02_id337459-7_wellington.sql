create
or replace procedure proc_faleconosco_reclamado
as
    v_id numeric;
    v_cpfcnpj
varchar(70);
    v_nome
varchar(255);
begin
for registro in (select id,
                            substr(mensagem, instr(MENSAGEM, 'Dados do Prestador:<br>CPF/CNPJ: ', 1, 1) + length('Dados do Prestador:<br>CPF/CNPJ: '), 14) as cpfcnpj,

                            substr(mensagem, instr(MENSAGEM, 'Nome/Razão Social: ', 1, 1) + length('Nome/Razão Social: '),
                                   (instr(mensagem, '<br><br>', instr(MENSAGEM, 'Nome/Razão Social: ', 1, 1) + length('Nome/Razão Social: '), 1)) -
                                   (instr(MENSAGEM, 'Nome/Razão Social: ', 1, 1) + length('Nome/Razão Social: '))) as nome,

                            substr(mensagem, instr(MENSAGEM, 'Dados do Serviço:<br>Data: ', 1, 1) + length('Dados do Serviço:<br>Data: '),
                                   (instr(mensagem, '<br>', instr(MENSAGEM, 'Dados do Serviço:<br>Data: ', 1, 1) + length('Dados do Serviço:<br>Data: '), 1)) -
                                   (instr(MENSAGEM, 'Dados do Serviço:<br>Data: ', 1, 1) + length('Dados do Serviço:<br>Data: '))) as data_servico,

                            case ASSUNTO
                                when 'Nota Fiscal com Dados Incorretos' then
                                    substr(mensagem, instr(MENSAGEM, 'Descrição  da Correção: ', 1, 1) + length('Descrição  da Correção: '),
                                           (instr(mensagem, '<br>', instr(MENSAGEM, 'Descrição  da Correção: ', 1, 1) + length('Descrição  da Correção: '), 1)) -
                                           (instr(MENSAGEM, 'Descrição  da Correção: ', 1, 1) + length('Descrição  da Correção: ')))
                                else
                                    substr(mensagem, instr(MENSAGEM, 'Descrição do Serviço: ', 1, 1) + length('Descrição do Serviço: '),
                                           (instr(mensagem, '<br>', instr(MENSAGEM, 'Descrição do Serviço: ', 1, 1) + length('Descrição do Serviço: '), 1)) -
                                           (instr(MENSAGEM, 'Descrição do Serviço: ', 1, 1) + length('Descrição do Serviço: ')))
                                end as descricao_servico,

                            case ASSUNTO
                                when 'Nota Fiscal com Dados Incorretos' then
                                    substr(mensagem, instr(MENSAGEM, 'Número da Nota: ', 1, 1) + length('Número da Nota: '),
                                           (instr(mensagem, '<br>', instr(MENSAGEM, 'Número da Nota: ', 1, 1) + length('Número da Nota: '), 1)) -
                                           (instr(MENSAGEM, 'Número da Nota: ', 1, 1) + length('Número da Nota: ')))
                                end as numero_nota,

                            case ASSUNTO
                                when 'Nota Fiscal Não Emitida' then
                                    substr(mensagem, instr(MENSAGEM, 'Valor: ', 1, 1) + length('Valor: '),
                                           (instr(mensagem, '<br>', instr(MENSAGEM, 'Valor: ', 1, 1) + length('Valor: '), 1)) -
                                           (instr(MENSAGEM, 'Valor: ', 1, 1) + length('Valor: ')))
                                end as valor_servico,

                            substr(mensagem, instr(MENSAGEM, 'Reclamação/Denúncia: ', 1, 1) + length('Reclamação/Denúncia: '),
                                   (instr(mensagem, '<br>', instr(MENSAGEM, 'Reclamação/Denúncia: ', 1, 1) + length('Reclamação/Denúncia: '), 1)) -
                                   (instr(MENSAGEM, 'Reclamação/Denúncia: ', 1, 1) + length('Reclamação/Denúncia: '))) as mensagem

                     from FALECONOSCONFSE
                     where mensagem like '%Dados do Serviço:%')
        loop
select dp.CPFCNPJ, dp.NOMERAZAOSOCIAL
into v_cpfcnpj, v_nome
from dadospessoaisnfse dp
where dp.cpfcnpj = registro.cpfcnpj
    fetch first 1 rows only;

if
(v_cpfcnpj is not null) then
select HIBERNATE_SEQUENCE.nextval
into v_id
from dual;

insert into dadospessoaisnfse (id, CPFCNPJ, NOMERAZAOSOCIAL, EMAIL, TELEFONE)
values (v_id, v_cpfcnpj, v_nome, null, null);

update FALECONOSCONFSE
set dadosreclamado_id = v_id
where id = registro.ID;
end if;

update FALECONOSCONFSE
set dataservico       = to_date(registro.data_servico, 'dd/MM/yyyy'),
    descricaoservico  = registro.descricao_servico,
    numeronotaservico = to_number(registro.numero_nota),
    valorservico      = to_number(replace(replace(registro.valor_servico, ',', ''), '.', ',')),
    mensagem          = registro.mensagem
where id = registro.ID;
end loop;
end;
